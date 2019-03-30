package jenkins.plugins.office365connector;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import javax.annotation.Nonnull;

/**
 * Office365ConnectorBuildListener {@link RunListener}.
 * <p>
 * <p>When a build starts, the {@link #onStarted(Run, TaskListener)} method will be invoked. And
 * when a build finishes, the {@link #onCompleted(Run, TaskListener)} method will be invoked.
 *
 * @author Srivardhan Hebbar
 */

@Extension
public class Office365ConnectorBuildListener extends RunListener<Run> {

    /**
     * Called when a build is first started.
     *
     * @param run      - A Run object representing a particular execution of Job.
     * @param listener - A TaskListener object which receives events that happen during some
     *                 operation.
     */
    @Override
    public void onStarted(Run run, final TaskListener listener) {
        Office365ConnectorWebhookNotifier notifier = new Office365ConnectorWebhookNotifier(run, listener);
        notifier.sendBuildStartedNotification(false);
    }

    /**
     * Called when a build is completed.
     *
     * @param run      - A Run object representing a particular execution of Job.
     * @param listener - A TaskListener object which receives events that happen during some
     *                 operation.
     */
    @Override
    public void onCompleted(final Run run, @Nonnull final TaskListener listener) {
        Office365ConnectorWebhookNotifier notifier = new Office365ConnectorWebhookNotifier(run, listener);
        notifier.sendBuildCompletedNotification();
    }
}
