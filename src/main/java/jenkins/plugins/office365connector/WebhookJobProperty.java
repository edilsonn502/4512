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

import java.util.ArrayList;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.JobProperty;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Job Property.
 */
public class WebhookJobProperty extends JobProperty<AbstractProject<?, ?>> {

    private final List<Webhook> webhooks;

    @DataBoundConstructor
    public WebhookJobProperty(List<Webhook> webhooks) {
        this.webhooks = new ArrayList<>(webhooks);
    }

    public List<Webhook> getWebhooks() {
        return webhooks;
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> run, BuildListener listener) {
        Office365ConnectorWebhookNotifier notifier = new Office365ConnectorWebhookNotifier(run, listener);
        notifier.sendBuildStartedNotification(true);

        return super.prebuild(run, listener);
    }
}
