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

package org.springframework.cloud.stream.module.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.stream.annotation.Bindings;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.modules.test.PropertiesInitializer;
import org.springframework.cloud.stream.tuple.Tuple;
import org.springframework.cloud.stream.tuple.TupleBuilder;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.test.util.TestUtils;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LogSinkApplication.class, initializers = PropertiesInitializer.class)
@DirtiesContext
public class LogSinkApplicationTests {

	@Autowired
	@Bindings(LogSink.class)
	private Sink sink;

	@SuppressWarnings("unused")
	@Autowired
	private Sink same;

	@Autowired
	private LoggingHandler logSinkHandler;

	@BeforeClass
	public static void setUp() {
		Properties properties = new Properties();
		properties.put("name", "foo");
		properties.put("level", "WARN");
		properties.put("expression", "payload.toUpperCase()");
		PropertiesInitializer.PROPERTIES = properties;
	}

	@Test
	public void test() {
		assertNotNull(this.sink.input());
		assertEquals(LoggingHandler.Level.WARN, this.logSinkHandler.getLevel());
		Log logger = TestUtils.getPropertyValue(this.logSinkHandler, "messageLogger", Log.class);
		assertEquals("foo", TestUtils.getPropertyValue(logger, "name"));
		logger = spy(logger);
		new DirectFieldAccessor(this.logSinkHandler).setPropertyValue("messageLogger", logger);
		String json = "{ \"id\" : \"foo\", \"bar\" : \"baz\"  }";
		GenericMessage<String> message = new GenericMessage<>(json);
		this.sink.input().send(message);
		ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
		verify(logger).warn(captor.capture());
		assertEquals("BAZ", captor.getValue());
//		this.logSinkHandler.setExpression("#this");
//		this.sink.input().send(message);
//		verify(logger, times(2)).warn(captor.capture());
//		assertSame(message, captor.getValue());

		Tuple tuple = TupleBuilder.fromString(json);
		this.sink.input().send(new GenericMessage<>(tuple));
		verify(logger, times(2)).warn(captor.capture());
		assertEquals("BAZ", captor.getValue());
	}

}
