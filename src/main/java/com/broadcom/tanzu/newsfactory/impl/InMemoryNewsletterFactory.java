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
import com.broadcom.tanzu.newsfactory.NewsletterFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@Profile("inmemory")
class InMemoryNewsletterFactory implements NewsletterFactory {

    @Override
    public Newsletter create(List<String> topics, List<URI> sources) {
        final var intro = """
                This is a sample of a newsletter.
                The real implementation generates newsletters by leveraging Spring AI.
                """;
        final var outro = "Profile inmemory is enabled.";

        final var entries = List.of(
                new Newsletter.Entry(
                        URI.create("https://spring.io/blog/2024/02/23/spring-ai-0-8-0-released"),
                        "Spring AI 0.8.0 released",
                        "A new project has joined the Spring portfolio."),
                new Newsletter.Entry(
                        URI.create("https://spring.io/blog/2024/02/28/this-week-in-spring-february-27th-2024"),
                        "This week in Spring - Feb 27th, 2024",
                        "What happened this week in the Spring universe? Many things."
                )
        );

        // Simulate a lengthy process.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignore) {
        }

        return new Newsletter(intro, outro, entries);
    }
}
