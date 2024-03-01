/*
 * Copyright (c) 2024 Broadcom, Inc. or its affiliates
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

package com.broadcom.tanzu.newsfactory.impl;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Component
class ContentFetcher {
    private final Logger logger = LoggerFactory.getLogger(ContentFetcher.class);
    private final RestClient restClient;

    ContentFetcher(RestClient restClient) {
        this.restClient = restClient;
    }

    Optional<String> fetchContent(URI uri) throws IOException {
        final var baseUri = uri.toURL().toExternalForm();
        logger.debug("Fetching content from {}", baseUri);
        final var htmlContent = restClient.get()
                .uri(uri).accept(MediaType.TEXT_HTML).retrieve().body(String.class);
        if (!StringUtils.hasLength(htmlContent)) {
            logger.warn("No content found from {}", baseUri);
            return Optional.empty();
        }
        logger.debug("Read content: {} characters", htmlContent.length());

        final var doc = Jsoup.parse(htmlContent, baseUri);
        final var htmlBodyTag = doc.select("body").first();
        if (htmlBodyTag == null) {
            logger.warn("Found no body tag from {}", baseUri);
            return Optional.empty();
        }
        final var htmlBody = Jsoup.clean(htmlBodyTag.html(), baseUri, Safelist.simpleText());
        return Optional.of(htmlBody);
    }
}
