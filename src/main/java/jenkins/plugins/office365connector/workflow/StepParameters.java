/*
 * Copyright 2016 srhebbar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jenkins.plugins.office365connector.workflow;

/**
 * @author srhebbar
 */
public class StepParameters {

    private final String message;
    private final String webhookUrl;
    private final String status;
    private final String color;

    StepParameters(String message, String webhookUrl, String status, String color) {
        this.message = message;
        this.webhookUrl = webhookUrl;
        this.status = status;
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getColor() {
        return color;
    }

}
