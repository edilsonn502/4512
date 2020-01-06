/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jenkins.plugins.office365connector;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;

/**
 * Makes http post requests in a separate thread.
 */
public class HttpWorker implements Runnable {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final PrintStream logger;

    private final String url;
    private final String data;
    private final int timeout;

    private static final int RETRIES = 3;

    public HttpWorker(String url, String data, int timeout, PrintStream logger) {
        this.url = url;
        this.data = data;
        this.timeout = timeout;
        this.logger = logger;
    }

    /**
     * Sends the notification to the hook.
     */
    public void submit() {
        executorService.submit(this);
    }

    @Override
    public void run() {
        int tried = 0;
        boolean success = false;
        HttpClient client = getHttpClient();
        do {
            tried++;
            RequestEntity requestEntity;
            try {
                // uncomment to log what message has been sent
                // log("Posted JSON: %s", data);
                requestEntity = new StringRequestEntity(data, "application/json", StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace(logger);
                break;
            }

            PostMethod post = new PostMethod(url);
            try {
                post.setRequestEntity(requestEntity);
                int responseCode = client.executeMethod(post);
                if (responseCode != HttpStatus.SC_OK) {
                    String response = post.getResponseBodyAsString();
                    log("Posting data to %s may have failed. Webhook responded with status code - %s", url, responseCode);
                    log("Message from webhook - %s", response);

                } else {
                    success = true;
                }
            } catch (IOException e) {
                log("Failed to post data to webhook - %s", url);
                e.printStackTrace(logger);
            } finally {
                post.releaseConnection();
            }
        } while (tried < RETRIES && !success);

    }

    private HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null) {
            ProxyConfiguration proxy = jenkins.proxy;
            if (proxy != null) {
                client.getHostConfiguration().setProxy(proxy.name, proxy.port);
                String username = proxy.getUserName();
                String password = proxy.getPassword();
                // Consider it to be passed if username specified. Sufficient?
                if (StringUtils.isNotBlank(username)) {
                    client.getState().setProxyCredentials(AuthScope.ANY,
                            new UsernamePasswordCredentials(username, password));
                }
            }
        }
        client.getParams().setConnectionManagerTimeout(timeout);
        client.getHttpConnectionManager().getParams().setSoTimeout(timeout);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        return client;
    }

    /**
     * Helper method for logging.
     */
    private void log(String format, Object... args) {
        this.logger.println("[Office365connector] " + String.format(format, args));
    }
}
