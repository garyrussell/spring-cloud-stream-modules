/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.cloud.stream.module.file.source;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.module.MaxMessagesProperties;
import org.springframework.cloud.stream.module.PeriodicTriggerConfiguration;
import org.springframework.cloud.stream.module.file.FileConsumerProperties;
import org.springframework.cloud.stream.module.file.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.file.FileInboundChannelAdapterSpec;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.Trigger;
import org.springframework.util.StringUtils;

/**
 * Creates a {@link FileReadingMessageSource} bean and registers it as a
 * Inbound Channel Adapter that sends messages to the Source output channel.
 *
 * @author Gary Russell
 */
@EnableBinding(Source.class)
@Import(PeriodicTriggerConfiguration.class)
@EnableConfigurationProperties({ FileSourceProperties.class,
	FileConsumerProperties.class, MaxMessagesProperties.class })
public class FileSource {

	@Autowired
	private FileSourceProperties properties;

	@Autowired
	private FileConsumerProperties fileConsumerProperties;

	@Autowired
	private MaxMessagesProperties maxMessagesProperties;

	@Autowired
	private Trigger trigger;

	@Bean
	public PollerMetadata poller() {
		return Pollers.trigger(trigger).maxMessagesPerPoll(this.maxMessagesProperties.getMaxMessages()).get();
	}

	@Autowired
	Source source;

	@Bean
	public IntegrationFlow fileSourceFlow() {
		FileInboundChannelAdapterSpec messageSourceSpec = Files.inboundAdapter(new File(this.properties.getDirectory()));

		if (StringUtils.hasText(this.properties.getFilenamePattern())) {
			messageSourceSpec.patternFilter(this.properties.getFilenamePattern(), this.properties.isPreventDuplicates());
		}
		else if (this.properties.getFilenameRegex() != null) {
			messageSourceSpec.regexFilter(this.properties.getFilenameRegex().pattern(),
					this.properties.isPreventDuplicates());
		}

		IntegrationFlowBuilder flowBuilder = IntegrationFlows
				.from(messageSourceSpec,
					new Consumer<SourcePollingChannelAdapterSpec>() {

						@Override
						public void accept(SourcePollingChannelAdapterSpec sourcePollingChannelAdapterSpec) {
							sourcePollingChannelAdapterSpec
									.autoStartup(false)
									.poller(poller());
						}

					});
		return FileUtils.enhanceFlowForReadingMode(flowBuilder, this.fileConsumerProperties)
			.channel(source.output())
			.get();
	}

}
