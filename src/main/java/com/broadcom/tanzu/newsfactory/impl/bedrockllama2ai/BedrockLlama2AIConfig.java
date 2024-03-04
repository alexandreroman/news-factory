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

import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.broadcom.tanzu.newsfactory.AIResources;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "newsletter.ai.model", havingValue = "bedrockllama2", matchIfMissing = false)
class BedrockLlama2AIConfig {

    private ChatClient bedrockChatClient;

	@Bean
    AIResources aiResources() {
        return new BedrockLlama2AIResources();
    }

    @Bean
    @Qualifier("newsFactoryChatClient")
    private ChatClient newsFactoryChatClient() {
    	return getBedrockChatClient();
    }

	private ChatClient getBedrockChatClient() {
		return bedrockChatClient;
	}

	@Autowired
    @Qualifier("bedrockLlama2ChatClient")
	private void setBedrockChatClient(ChatClient bedrockChatClient) {
		this.bedrockChatClient = bedrockChatClient;
	}
    

}
