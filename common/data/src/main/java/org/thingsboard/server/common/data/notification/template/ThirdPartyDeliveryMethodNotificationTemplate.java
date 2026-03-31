/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.notification.template;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ThirdPartyDeliveryMethodNotificationTemplate extends DeliveryMethodNotificationTemplate {

    private final List<TemplatableValue> templatableValues = List.of(
            TemplatableValue.of(this::getBody, this::setBody)
    );

    public ThirdPartyDeliveryMethodNotificationTemplate(ThirdPartyDeliveryMethodNotificationTemplate other) {
        super(other);
    }

    @NoXss(fieldName = "Third-party message")
    @Length(fieldName = "Third-party message", max = 4000, message = "cannot be longer than 4000 chars")
    @Override
    public String getBody() {
        return super.getBody();
    }

    @Override
    public NotificationDeliveryMethod getMethod() {
        return NotificationDeliveryMethod.THIRD_PARTY;
    }

    @Override
    public DeliveryMethodNotificationTemplate copy() {
        return new ThirdPartyDeliveryMethodNotificationTemplate(this);
    }

}
