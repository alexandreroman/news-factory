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

import com.broadcom.tanzu.newsfactory.AIResources;
import com.broadcom.tanzu.newsfactory.Newsletter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
class ContentSummarizer {
    private final Logger logger = LoggerFactory.getLogger(ContentSummarizer.class);

    private final ChatClient chatClient;
    private final ContentFetcher fetcher;
    private final AIResources aiResources;

    @Autowired
    ContentSummarizer(ChatClient chatClient, ContentFetcher fetcher, AIResources aiResources) {
        this.chatClient = chatClient;
        this.fetcher = fetcher;
        this.aiResources = aiResources;
    }

    Newsletter.Entry summarizeContent(List<String> topics, URI source) throws IOException {
        logger.info("Fetching content from {}", source);
        final var content = fetcher.fetchContent(source)
                .orElseThrow(() -> new IOException("Failed to fetch content from " + source));

        final var sysMsg = new SystemPromptTemplate(aiResources.systemPrompt()).createMessage();
        logger.debug("Created system prompt: {}", sysMsg);

        final var outputParser = new BeanOutputParser<>(Newsletter.Entry.class);
        final var userMsg = new PromptTemplate(aiResources.summaryPrompt()).
                createMessage(Map.of("content", content,
                        "source", source.toURL().toExternalForm(),
                        "topics", String.join(", ", topics),
                        "output", outputParser.getFormat())
                );
        logger.debug("Created user prompt: {}", userMsg);

        final var prompt = new Prompt(List.of(sysMsg, userMsg));
        logger.info("Generating content summary for {}", source);
        final var outputMsg = chatClient.call(prompt).getResult().getOutput();

        logger.debug("Got output after summarizing content from {}: {}", source, outputMsg.getContent());

        final var entry = outputParser.parse(outputMsg.getContent());
        if (!StringUtils.hasLength(entry.title()) || !StringUtils.hasLength(entry.content())) {
            throw new IOException("Failed to summarize content from " + source + ": missing title or content");
        }
        return entry;
    }
}
