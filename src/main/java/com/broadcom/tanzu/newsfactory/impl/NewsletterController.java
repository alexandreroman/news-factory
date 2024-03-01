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

import com.broadcom.tanzu.newsfactory.NewsletterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Controller
class NewsletterController {
    private final Logger logger = LoggerFactory.getLogger(NewsletterController.class);
    private final NewsletterFactory nf;

    NewsletterController(NewsletterFactory nf) {
        this.nf = nf;
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        var s2 = s.strip();
        return s2.isEmpty() ? null : s2;
    }

    @GetMapping(value = "/")
    String getForm(Model model) {
        model.addAttribute("form", new NewsletterForm());
        return "/views/newsletter-form";
    }

    @PostMapping(value = "/newsletter-add-topic")
    String addTopic(@ModelAttribute NewsletterForm form, Model model) {
        final var topic = trimToNull(form.getNewTopic());
        if (topic != null && !form.getTopics().contains(topic)) {
            logger.debug("Adding topic: {}", topic);
            form.getTopics().add(topic);
        }
        form.setNewTopic(null);
        model.addAttribute("form", form);
        return "/fragments/newsletter-form";
    }

    @PostMapping(value = "/newsletter-remove-topic/{index}")
    String removeTopic(@ModelAttribute NewsletterForm form, Model model, @PathVariable("index") int index) {
        logger.debug("Removing topic: {}", form.getTopics().get(index));
        form.getTopics().remove(index);
        model.addAttribute("form", form);
        return "/fragments/newsletter-form";
    }

    @PostMapping(value = "/newsletter-add-source")
    String addSource(@ModelAttribute NewsletterForm form, Model model) {
        final var source = trimToNull(form.getNewSource());
        final URI sourceUri;
        if (source != null && !form.getSources().contains(source)) {
            try {
                // Validate that this source is a valid URI.
                sourceUri = URI.create(source);
                logger.debug("Adding new source: {}", source);
                form.getSources().add(source);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid source URI: {}", source, e);
            }
        }
        form.setNewSource(null);
        model.addAttribute("form", form);
        return "/fragments/newsletter-form";
    }

    @PostMapping(value = "/newsletter-remove-source/{index}")
    String removeSource(@ModelAttribute NewsletterForm form, Model model, @PathVariable("index") int index) {
        logger.debug("Removing source: {}", form.getSources().get(index));
        form.getSources().remove(index);
        model.addAttribute("form", form);
        return "/fragments/newsletter-form";
    }

    @PostMapping(value = "/newsletter.html")
    String createNewsletter(@ModelAttribute NewsletterForm form, Model model) {
        final var sourceUris = form.getSources().stream().map(URI::create).toList();
        final var newsletter = nf.create(form.getTopics(), sourceUris);
        model.addAttribute("newsletter", newsletter);
        return "/fragments/newsletter";
    }
}

class NewsletterForm {
    private List<String> topics = new ArrayList<>(3);
    private List<String> sources = new ArrayList<>(3);
    private String newTopic;
    private String newSource;

    public NewsletterForm() {
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "NewsletterForm{" +
                "topics=" + topics +
                ", sources=" + sources +
                ", newTopic='" + newTopic + '\'' +
                ", newSource='" + newSource + '\'' +
                '}';
    }

    public String getNewTopic() {
        return newTopic;
    }

    public void setNewTopic(String newTopic) {
        this.newTopic = newTopic;
    }

    public String getNewSource() {
        return newSource;
    }

    public void setNewSource(String newSource) {
        this.newSource = newSource;
    }
}
