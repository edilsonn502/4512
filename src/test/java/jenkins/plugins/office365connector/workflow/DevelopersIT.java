package jenkins.plugins.office365connector.workflow;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Job;
import hudson.scm.ChangeLogSet;
import jenkins.plugins.office365connector.FileUtils;
import jenkins.plugins.office365connector.Office365ConnectorWebhookNotifier;
import jenkins.plugins.office365connector.helpers.AffectedFileBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Office365ConnectorWebhookNotifier.class)
public class DevelopersIT extends AbstractTest {

    private static final String JOB_NAME = "simple job";
    private static final int BUILD_NUMBER = 1;

    @Before
    public void setUp() {
        mockListener();

        run = mockRun();

        mockDisplayURLProvider(JOB_NAME, BUILD_NUMBER);
        mockEnvironment();
        mockHttpWorker();
        mockGetChangeSets();
    }

    private AbstractBuild mockRun() {
        AbstractBuild run = mock(AbstractBuild.class);

        when(run.getNumber()).thenReturn(BUILD_NUMBER);

        Job job = mockJob(JOB_NAME);
        when(run.getParent()).thenReturn(job);

        mockProperty(job);

        return run;
    }

    private void mockGetChangeSets() {
        List<ChangeLogSet> files = new AffectedFileBuilder().sampleChangeLogs(run);
        when(run.getChangeSets()).thenReturn(files);
    }


    @Test
    public void validateStartedRequest_WithManyDevelopers() {

        // given
        Office365ConnectorWebhookNotifier notifier = new Office365ConnectorWebhookNotifier(run, mockListener());

        // when
        notifier.sendBuildStartedNotification(true);

        // then
        assertHasSameContent(workerAnswer.getData(), FileUtils.getContentFile("started-developers.json"));
    }
}
