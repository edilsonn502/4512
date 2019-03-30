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

package jenkins.plugins.office365connector.model;

import java.util.List;

/**
 * @author srhebbar
 */
public class Section {

    private boolean markdown;

    private List<Fact> facts;

    private String activityTitle;

    private String activitySubtitle;

    public Section(String activityTitle, String activitySubtitle, List<Fact> factsList) {
        this.activityTitle = activityTitle;
        this.activitySubtitle = activitySubtitle;
        this.facts = factsList;
        this.markdown = true;
    }

    public boolean getMarkdown() {
        return markdown;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public List<Fact> getFacts() {
        return facts;
    }

    public String getActivitySubtitle() {
        return activitySubtitle;
    }
}
