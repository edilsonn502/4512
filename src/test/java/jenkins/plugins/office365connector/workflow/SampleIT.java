package jenkins.plugins.office365connector.workflow;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.scm.ChangeLogSet;
import jenkins.plugins.office365connector.FileUtils;
import jenkins.plugins.office365connector.Office365ConnectorWebhookNotifier;
import jenkins.plugins.office365connector.WebhookJobProperty;
import jenkins.plugins.office365connector.helpers.AffectedFileBuilder;
import jenkins.plugins.office365connector.helpers.WebhookBuilder;
import jenkins.plugins.office365connector.utils.TimeUtils;
import jenkins.plugins.office365connector.utils.TimeUtilsTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Office365ConnectorWebhookNotifier.class, Run.class, TimeUtils.class})
public class SampleIT extends AbstractTest {

    private static final String JOB_NAME = "myFirstJob";
    private static final String CAUSE_DESCRIPTION = "Started by John";
    private static final int BUILD_NUMBER = 167;
    private static final long START_TIME = 1508617305000L;
    private static final long DURATION = 1000 * 60 * 60;

    private static final String FORMATTED_START_TIME;
    private static final String FORMATTED_COMPLETED_TIME;

    static {
        TimeUtilsTest.setupTimeZoneAndLocale();
        FORMATTED_START_TIME = TimeUtils.dateToString(START_TIME);
        FORMATTED_COMPLETED_TIME = TimeUtils.dateToString(START_TIME + DURATION);
    }

    @Before
    public void setUp() {
        mockListener();

        run = mockRun();

        mockDisplayURLProvider(JOB_NAME, BUILD_NUMBER);
        mockEnvironment();
        mockHttpWorker();
        mockGetChangeSets();
        mockTimeUtils();
    }

    private AbstractBuild mockRun() {
        AbstractBuild run = mock(AbstractBuild.class);

        when(run.getNumber()).thenReturn(BUILD_NUMBER);
        when(run.getStartTimeInMillis()).thenReturn(START_TIME);
        when(run.getDuration()).thenReturn(DURATION);

        Job job = mockJob(JOB_NAME);
        when(run.getParent()).thenReturn(job);

        // getProperty
        WebhookJobProperty property = new WebhookJobProperty(WebhookBuilder.sampleWebhookWithAllStatuses());
        when(job.getProperty(WebhookJobProperty.class)).thenReturn(property);

        // remarks
        Cause cause = mock(Cause.class);
        when(cause.getShortDescription()).thenReturn(CAUSE_DESCRIPTION);
        when(run.getCauses()).thenReturn(Arrays.asList(cause));

        return run;
    }

    private void mockGetChangeSets() {
        List<ChangeLogSet> files = new AffectedFileBuilder().singleChangeLog(run);
        when(run.getChangeSets()).thenReturn(files);
    }

    private void mockTimeUtils() {
        mockStatic(TimeUtils.class);
        when(TimeUtils.dateToString(START_TIME)).thenReturn(FORMATTED_START_TIME);
        when(TimeUtils.dateToString(START_TIME + DURATION)).thenReturn(FORMATTED_COMPLETED_TIME);
    }


    @Test
    public void validateStartedRequest() {

        // given
        Office365ConnectorWebhookNotifier notifier = new Office365ConnectorWebhookNotifier(run, mockListener());

        // when
        notifier.sendBuildStartedNotification(true);

        // then
        assertHasSameContent(workerAnswer.getData(), FileUtils.getContentFile("started.json"));
    }

    @Test
    public void validateCompletedRequest_OnSuccess() {

        // given
        when(run.getResult()).thenReturn(Result.SUCCESS);
        Office365ConnectorWebhookNotifier notifier = new Office365ConnectorWebhookNotifier(run, mockListener());

        // when
        notifier.sendBuildCompletedNotification();

        // then
        assertHasSameContent(workerAnswer.getData(), FileUtils.getContentFile("completed-success.json"));
    }

    @Test
    public void validateCompletedRequest_OnFailed() {

        // given
        when(run.getResult()).thenReturn(Result.FAILURE);
        Office365ConnectorWebhookNotifier notifier = new Office365ConnectorWebhookNotifier(run, mockListener());

        // when
        notifier.sendBuildCompletedNotification();

        // then
        assertHasSameContent(workerAnswer.getData(), FileUtils.getContentFile("completed-failed.json"));
    }

    @Test
    public void validateCompletedRequest_OnRepeatedFailure() {

        // given
        mockFailedResult();
        Office365ConnectorWebhookNotifier notifier = new Office365ConnectorWebhookNotifier(run, mockListener());

        // when
        notifier.sendBuildCompletedNotification();

        // then
        assertHasSameContent(workerAnswer.getData(), FileUtils.getContentFile("completed-repeated_failure.json"));
    }
}
