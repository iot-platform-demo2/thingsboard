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
package org.thingsboard.server.service.notification.channels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.info.NotificationInfo;
import org.thingsboard.server.common.data.notification.settings.NotificationSettings;
import org.thingsboard.server.common.data.notification.settings.ThirdPartyNotificationDeliveryMethodConfig;
import org.thingsboard.server.common.data.notification.template.ThirdPartyDeliveryMethodNotificationTemplate;
import org.thingsboard.server.dao.notification.NotificationSettingsService;
import org.thingsboard.server.service.notification.NotificationProcessingContext;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ThirdPartyNotificationChannel implements NotificationChannel<User, ThirdPartyDeliveryMethodNotificationTemplate> {

    private static final String TEMPLATE_ID = "IOT_NOTIFY";

    private final NotificationSettingsService notificationSettingsService;

    @Setter
    private RestTemplate restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.of(15, ChronoUnit.SECONDS))
            .setReadTimeout(Duration.of(15, ChronoUnit.SECONDS))
            .build();

    @Override
    public void sendNotification(User recipient, ThirdPartyDeliveryMethodNotificationTemplate processedTemplate,
                                 NotificationProcessingContext ctx) throws Exception {
        String phoneNumber = recipient.getPhone();
        if (StringUtils.isBlank(phoneNumber)) {
            throw new RuntimeException("User does not have phone number");
        }

        ThirdPartyNotificationDeliveryMethodConfig config = ctx.getDeliveryMethodConfig(NotificationDeliveryMethod.THIRD_PARTY);
        String accessToken = fetchAccessToken(config);

        ObjectNode payload = JacksonUtil.newObjectNode();
        payload.put("templateId", TEMPLATE_ID);
        payload.set("params", buildParamsNode(ctx.getRequest().getInfo()));

        ArrayNode phoneNumbers = payload.putArray("phoneNumbers");
        phoneNumbers.add(phoneNumber);

        ObjectNode data = payload.putObject("data");
        data.put("type", resolveNotificationType(ctx));
        data.put("url", config.getUrl());
        data.set("message", buildMessageNode(processedTemplate, ctx.getRequest().getInfo()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(JacksonUtil.toString(payload), headers);
        restTemplate.exchange(new URI(resolveUrl(config.getUrl(), config.getNotifyEndpoint())), HttpMethod.POST, request, String.class);
    }

    @Override
    public void check(TenantId tenantId) {
        NotificationSettings notificationSettings = notificationSettingsService.findNotificationSettings(tenantId);
        if (!notificationSettings.getDeliveryMethodsConfigs().containsKey(NotificationDeliveryMethod.THIRD_PARTY)) {
            throw new RuntimeException("Third-party notification settings are not configured");
        }
    }

    @Override
    public NotificationDeliveryMethod getDeliveryMethod() {
        return NotificationDeliveryMethod.THIRD_PARTY;
    }

    private String fetchAccessToken(ThirdPartyNotificationDeliveryMethodConfig config) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode requestBody = JacksonUtil.newObjectNode();
        requestBody.put("clientId", config.getClientId());
        requestBody.put("clientSecret", config.getClientSecret());

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                new URI(resolveUrl(config.getUrl(), config.getAccessTokenEndpoint())),
                HttpMethod.POST,
                new HttpEntity<>(JacksonUtil.toString(requestBody), headers),
                JsonNode.class
        );

        JsonNode body = response.getBody();
        String accessToken = extractToken(body);
        if (StringUtils.isBlank(accessToken)) {
            throw new RuntimeException("Third-party access token response does not contain a bearer token");
        }
        return accessToken;
    }

    private ObjectNode buildParamsNode(NotificationInfo info) {
        ObjectNode params = JacksonUtil.newObjectNode();
        if (info != null && info.getTemplateData() != null) {
            for (Map.Entry<String, String> entry : info.getTemplateData().entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        return params;
    }

    private JsonNode buildMessageNode(ThirdPartyDeliveryMethodNotificationTemplate processedTemplate, NotificationInfo info) {
        ObjectNode message = JacksonUtil.newObjectNode();
        message.put("body", processedTemplate.getBody());
        if (info != null) {
            message.set("info", JacksonUtil.valueToTree(info));
            message.set("templateData", JacksonUtil.valueToTree(info.getTemplateData()));
        }
        return message;
    }

    private String resolveNotificationType(NotificationProcessingContext ctx) {
        NotificationInfo info = ctx.getRequest().getInfo();
        if (info != null && info.getTemplateData() != null) {
            String type = info.getTemplateData().get("type");
            if (StringUtils.isNotBlank(type)) {
                return type;
            }
        }
        return ctx.getNotificationType().name();
    }

    private String extractToken(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isObject()) {
            for (String fieldName : new String[]{"accessToken", "access_token", "token", "bearerToken"}) {
                JsonNode tokenNode = node.get(fieldName);
                if (tokenNode != null && tokenNode.isTextual()) {
                    return tokenNode.asText();
                }
            }
            for (JsonNode child : node) {
                String token = extractToken(child);
                if (StringUtils.isNotBlank(token)) {
                    return token;
                }
            }
        }
        return null;
    }

    private String resolveUrl(String baseUrl, String endpoint) {
        if (StringUtils.startsWithIgnoreCase(endpoint, "http://") || StringUtils.startsWithIgnoreCase(endpoint, "https://")) {
            return endpoint;
        }
        String normalizedBaseUrl = StringUtils.removeEnd(baseUrl, "/");
        String normalizedEndpoint = StringUtils.prependIfMissing(endpoint, "/");
        return normalizedBaseUrl + normalizedEndpoint;
    }

}
