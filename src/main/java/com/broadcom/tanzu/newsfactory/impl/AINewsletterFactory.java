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

import com.broadcom.tanzu.newsfactory.Newsletter;
import com.broadcom.tanzu.newsfactory.NewsletterException;
import com.broadcom.tanzu.newsfactory.NewsletterFactory;
import com.broadcom.tanzu.newsfactory.NewsletterProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
@Profile("!inmemory")
class AINewsletterFactory implements NewsletterFactory {
    private final Logger logger = LoggerFactory.getLogger(AINewsletterFactory.class);
    private final ContentSummarizer summarizer;
    private final ChatClient cs;
    private final NewsletterProps np;

    @Value("classpath:/system-prompt.srt")
    private Resource systemPromptRes;
    @Value("classpath:/newsletter-prompt.srt")
    private Resource newsletterPromptRes;

    AINewsletterFactory(ContentSummarizer summarizer, ChatClient cs, NewsletterProps np) {
        this.summarizer = summarizer;
        this.cs = cs;
        this.np = np;
    }

    @Override
    public Newsletter create(List<String> topics, List<URI> sources) {
        logger.info("Starting newsletter generation covering topics {} with sources: {}",
                String.join(", ", topics), sources);

        final var introFuture = CompletableFuture.supplyAsync(() -> generateIntro(topics));
        final var outro = np.disclaimer();
        final var entries = sources.parallelStream().map(source -> {
            try {
                return generateEntry(topics, source);
            } catch (IOException e) {
                logger.warn("Failed to generate newsletter entry for {}", source, e);
                return null;
            }
        }).filter(Objects::nonNull).toList();

        if (entries.isEmpty()) {
            throw new NewsletterException("Failed to create newsletter entries", null);
        }

        final Newsletter newsletter;
        try {
            newsletter = new Newsletter(introFuture.join(), outro, entries);
        } catch (Exception e) {
            throw new NewsletterException("Failed to create newsletter introduction", e);
        }

        logger.info("Newsletter generation done");
        return newsletter;
    }

    private String generateIntro(List<String> topics) {
        final var sysMsg = new SystemPromptTemplate(systemPromptRes).createMessage();
        logger.debug("Created system prompt: {}", sysMsg);

        final var newsletterMsg = new PromptTemplate(newsletterPromptRes).
                createMessage(Map.of(
                        "topics", String.join(", ", topics))
                );
        logger.debug("Created newsletter prompt: {}", newsletterMsg);

        final var prompt = new Prompt(List.of(sysMsg, newsletterMsg));
        return cs.call(prompt).getResult().getOutput().getContent();
    }

    private Newsletter.Entry generateEntry(List<String> topics, URI source) throws IOException {
        return summarizer.summarizeContent(topics, source);
    }
}
