package jenkins.plugins.office365connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hudson.model.Run;
import hudson.model.User;
import jenkins.plugins.office365connector.model.Fact;
import jenkins.plugins.office365connector.utils.TimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TimeUtils.class})
public class FactsBuilderTest {

    private Run run;

    @Before
    public void setUp() {
        run = mock(Run.class);
    }

    @Test
    public void addBackToNormalTime_AddsFact() {

        // given
        long backToNormalDuration = 1000L;
        String durationString = "16 minutes, 40 seconds";

        PowerMockito.mockStatic(TimeUtils.class);
        BDDMockito.given(TimeUtils.durationToString(backToNormalDuration)).willReturn(durationString);

        FactsBuilder factBuilder = new FactsBuilder(run);

        // when
        factBuilder.addBackToNormalTime(backToNormalDuration);

        // then
        assertThat(factBuilder.collect())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("name", FactsBuilder.NAME_BACK_TO_NORMAL_TIME)
                .hasFieldOrPropertyWithValue("value", durationString);
    }

    @Test
    public void addCulprits_AddsFact() {

        // given
        FactsBuilder factBuilder = new FactsBuilder(run);
        User one = createUser("damian");
        User two = createUser("365");
        Set<User> users = new HashSet<>();
        users.add(one);
        users.add(two);

        // when
        factBuilder.addCulprits(users);

        // then
        List<Fact> facts = factBuilder.collect();
        assertThat(facts).hasSize(1);

        Fact fact = facts.get(0);
        assertThat(fact.getName()).isEqualTo(FactsBuilder.NAME_CULPRITS);
        assertThat(fact.getValue())
                .hasSize(one.getFullName().length() + two.getFullName().length() + 2)
                // depends on JVM implementation 'one' could be listed on the first or last position
                .contains(one.getFullName())
                .contains(two.getFullName());
    }

    @Test
    public void addCulprits_OnNoUser_AddsNoFact() {

        // given
        FactsBuilder factBuilder = new FactsBuilder(run);

        // when
        factBuilder.addCulprits(Collections.emptySet());

        // then
        assertThat(factBuilder.collect()).isEmpty();
    }

    private static User createUser(String fullName) {
        User user = mock(User.class);
        when(user.getFullName()).thenReturn(fullName);
        return user;
    }
}