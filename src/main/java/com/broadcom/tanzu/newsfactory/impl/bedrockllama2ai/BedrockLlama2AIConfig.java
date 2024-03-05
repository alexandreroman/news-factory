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

package com.broadcom.tanzu.newsfactory.impl.bedrockllama2ai;

import com.broadcom.tanzu.newsfactory.AIResources;
import org.springframework.ai.bedrock.llama2.BedrockLlama2ChatClient;
import org.springframework.ai.bedrock.llama2.api.Llama2ChatBedrockApi;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "newsletter.ai.model", havingValue = "bedrockllama2", matchIfMissing = false)
public class BedrockLlama2AIConfig {
    @Bean
    AIResources aiResources() {
        return new BedrockLlama2AIResources();
    }

    @Bean
    ChatClient chatClient(@Value("${spring.ai.bedrock.aws.region}") String region,
                          @Value("${spring.ai.bedrock.llama2.chat.model}") String model) {
        return new BedrockLlama2ChatClient(new Llama2ChatBedrockApi(model, region));
    }
}
