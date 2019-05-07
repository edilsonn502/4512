package jenkins.plugins.office365connector.helpers;

import static org.powermock.api.mockito.PowerMockito.mock;

import jenkins.plugins.office365connector.HttpWorker;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class HttpWorkerAnswer implements Answer<HttpWorker> {

    private String url;
    private String data;

    private int times;

    @Override
    public HttpWorker answer(InvocationOnMock invocation) {
        url = (String) (invocation.getArguments())[0];
        data = (String) (invocation.getArguments())[1];

        times++;
        return mock(HttpWorker.class);
    }

    public String getUrl() {
        return url;
    }

    public String getData() {
        return data;
    }

    public int getTimes() {
        return times;
    }
}
