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

package com.broadcom.tanzu.newsfactory.impl.azureopenai;

import com.broadcom.tanzu.newsfactory.AIResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

class AzureOpenAIResources implements AIResources {
    @Value("classpath:/system-prompt.srt")
    private Resource systemPrompt;
    @Value("classpath:/newsletter-prompt.srt")
    private Resource newsletterPrompt;
    @Value("classpath:/summary-prompt-azure-openai.srt")
    private Resource summaryPrompt;

    @Override
    public Resource systemPrompt() {
        return systemPrompt;
    }

    @Override
    public Resource summaryPrompt() {
        return summaryPrompt;
    }

    @Override
    public Resource newsletterPrompt() {
        return newsletterPrompt;
    }
}
