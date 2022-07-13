package jenkins.plugins.office365connector.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Collections;
import java.util.List;

import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;
import jenkins.plugins.office365connector.Office365ConnectorWebhookNotifier;
import jenkins.plugins.office365connector.Webhook;
import jenkins.plugins.office365connector.WebhookJobProperty;
import jenkins.plugins.office365connector.helpers.WebhookBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
@PowerMockIgnore("jdk.internal.reflect.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({Office365ConnectorWebhookNotifier.class, WebhookJobProperty.class, Jenkins.class})
public class WebhookJobPropertyTest extends AbstractTest {

    @Before
    public void setUp() {
        Webhook.DescriptorImpl mockDescriptor = mock(Webhook.DescriptorImpl.class);
        when(mockDescriptor.getName()).thenReturn("testName");

        mockStatic(Jenkins.class);
        Jenkins jenkins = mock(Jenkins.class);
        mockListener();

        when(jenkins.getDescriptorOrDie(Webhook.class)).thenReturn(mockDescriptor);
        when(Jenkins.get()).thenReturn(jenkins);
    }
    @Test
    public void getWebhooks_ReturnsHook() {

        // when
        List<Webhook> webhooks = WebhookBuilder.sampleWebhookWithAllStatuses();
        WebhookJobProperty property = new WebhookJobProperty(webhooks);

        // when
        List<Webhook> extractedWebhooks = property.getWebhooks();

        // then
        assertThat(extractedWebhooks)
                .hasSameSizeAs(webhooks)
                .containsAll(webhooks);

    }

    @Test
    public void prebuild_SendNotification() {

        // given
        run = mock(AbstractBuild.class);
        mockOffice365ConnectorWebhookNotifier();

        WebhookJobProperty property = new WebhookJobProperty(Collections.emptyList());

        // when
        property.prebuild(run, mockListener());

        assertThat(notifierAnswer.getTimes()).isOne();
    }
}
