/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015-2016 Center for Open Middleware.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.1.0
 *   Bundle      : it-harvester-notification-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.commons.testing.Utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@RunWith(JMockit.class)
public class ConnectionManagerTest {

	@Mocked private Connection connection;
	@Mocked private Channel channel;

	private ConnectionManager sut;

	@Before
	public void setUp() {
		this.sut=new ConnectionManager("name", "localhost", 5726, "/");
	}

	@Test
	public void testConnect$failConnection$couldNotCreateConnection$IOException() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				throw new IOException("Could not connect");
			}
		};
		try {
			this.sut.connect();
		} catch (final ControllerException e) {
			assertThat(e.getMessage(),equalTo("Could not connect to broker at localhost:5726 using virtual host /"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Could not connect"));
		}
	}

	@Test
	public void testConnect$failConnection$couldNotCreateConnection$TimeoutException() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				throw new TimeoutException("Could not connect");
			}
		};
		try {
			this.sut.connect();
			fail("Should have failed to connect");
		} catch (final ControllerException e) {
			assertThat(e.getMessage(),equalTo("Could not connect to broker at localhost:5726 using virtual host /"));
			assertThat(e.getCause(),instanceOf(TimeoutException.class));
			assertThat(e.getCause().getMessage(),equalTo("Could not connect"));
			assertThat(e.getBrokerHost(),equalTo("localhost"));
			assertThat(e.getBrokerPort(),equalTo(5726));
			assertThat(e.getVirtualHost(),equalTo("/"));
		}
	}

	@Test
	public void testConnectIsSafe() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock(invocations=1)
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();this.result=ConnectionManagerTest.this.channel;
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
		}};
		this.sut.connect();
		this.sut.connect();
	}

	@Test
	public void testDisconnectIsSafe() throws Exception {
		this.sut.disconnect();
	}

	@Test
	public void testDisconnectUnlocksOnUnexpectedFailure() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock(invocations=1)
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();this.result=ConnectionManagerTest.this.channel;
			ConnectionManagerTest.this.connection.isOpen();returns(true);this.result=new RuntimeException("Failure");
		}};
		this.sut.connect();
		try {
			this.sut.disconnect();
			fail("Should have failed to disconnect");
		} catch (final RuntimeException e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testDefaultChannelIsAlwaysOpen() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();this.result=ConnectionManagerTest.this.channel;
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
			ConnectionManagerTest.this.channel.isOpen();returns(false,true);
		}};
		this.sut.connect();
		assertThat(this.sut.channel().isOpen(),equalTo(true));
	}

	@Test
	public void testRetrievingTheDefaultChannelMayBreak() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel);this.result=new IOException("Failure");
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
			ConnectionManagerTest.this.channel.isOpen();returns(false,true);
		}};
		this.sut.connect();
		try {
			this.sut.channel();
			fail("Should have failed to create a channel");
		} catch(final ControllerException e) {
			assertThat(e.getMessage(),equalTo("Could not create channel using connection "+String.format("%08X",this.connection.hashCode())+" to broker at localhost:5726 using virtual host /"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
			assertThat(e.getBrokerHost(),equalTo("localhost"));
			assertThat(e.getBrokerPort(),equalTo(5726));
			assertThat(e.getVirtualHost(),equalTo("/"));
		}
	}

	@Test
	public void testChannelCreationRequiresAChannelFromTheConnection() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();this.result=null;
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
		}};
		try {
			this.sut.connect();
			fail("Should have failed to connect");
		} catch(final ControllerException e) {
			assertThat(e.getMessage(),equalTo("Could not create channel using connection "+String.format("%08X",this.connection.hashCode())+" to broker at localhost:5726 using virtual host /"));
			assertThat(e.getCause(),instanceOf(NullPointerException.class));
			assertThat(e.getCause().getMessage(),equalTo("No channel available"));
			assertThat(e.getBrokerHost(),equalTo("localhost"));
			assertThat(e.getBrokerPort(),equalTo(5726));
			assertThat(e.getVirtualHost(),equalTo("/"));
		}
	}

	@Test
	public void testThreadLocalChannelLifecycle(@Mocked final Channel currentChannel) throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel,currentChannel,ConnectionManagerTest.this.channel);
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
			currentChannel.isOpen();this.times=1;this.result=true;
			currentChannel.close();this.times=1;
		}};
		this.sut.connect();
		final Channel aChannel=this.sut.currentChannel();
		assertThat(aChannel,sameInstance(currentChannel));
		this.sut.discardChannel();
		assertThat(this.sut.currentChannel(),sameInstance(this.channel));
	}
	@Test
	public void testThreadLocalChannelLifecycleIsThreadSafe() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		final Channel currentChannel=new MockUp<Channel>() {
			@Mock(invocations=250)
			boolean isOpen() {
				return true;
			}
			@Mock(invocations=250)
			void close() {
				try {
					TimeUnit.MICROSECONDS.sleep(2500);
				} catch (final InterruptedException e) {
				}
			}
		}.getMockInstance();
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel,currentChannel);this.times=251;
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
		}};
		this.sut.connect();
		final ExecutorService executor = Executors.newFixedThreadPool(100);
		for(int i=0;i<500;i++) {
			final int times=i;
			executor.execute(new Runnable(){
				@Override
				public void run() {
					try {
						if(times%2==0) {
							ConnectionManagerTest.this.sut.currentChannel();
						}
						ConnectionManagerTest.this.sut.discardChannel();
					} catch (final ControllerException e) {
						e.printStackTrace();
					}
				}});
		}
		executor.shutdown();
		while(!executor.isTerminated()) {
			executor.awaitTermination(5,TimeUnit.SECONDS);
		}
		this.sut.disconnect();
	}

	@Test
	public void testCurrentChannelKeepsThreadLocalChannels(@Mocked final Channel currentChannel) throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel,currentChannel);this.times=2;
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
		}};
		this.sut.connect();
		assertThat(this.sut.currentChannel(),sameInstance(currentChannel));
		assertThat(this.sut.currentChannel(),sameInstance(currentChannel));
	}

	@Test
	public void testCurrentChannelRequiresBeingConnected() throws Exception {
		try {
			this.sut.currentChannel();
			fail("Should not return a thread-local channel if not connected");
		} catch (final IllegalStateException e) {
			assertThat(e.getMessage(),equalTo("No connection available"));
		}
	}

	@Test
	public void testDiscardChannelDoesNothingIfNoCurrentChannelIsAvailable() throws Exception {
		this.sut.discardChannel();
	}

	@Test
	public void testCloseQuietlyObservesChannelStatus(@Mocked final Channel currentChannel) throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel,currentChannel);
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
			currentChannel.isOpen();this.times=1;this.result=false;
			currentChannel.close();this.times=0;
		}};
		this.sut.connect();
		final Channel aChannel=this.sut.currentChannel();
		assertThat(aChannel,sameInstance(currentChannel));
		this.sut.discardChannel();
	}

	@Test
	public void testCloseQuietlySwallowsRegularExceptions(@Mocked final Channel currentChannel) throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel,currentChannel);
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
			currentChannel.isOpen();this.times=1;this.result=true;
			currentChannel.close();this.times=1;this.result=new IOException("Failure");
		}};
		this.sut.connect();
		final Channel aChannel=this.sut.currentChannel();
		assertThat(aChannel,sameInstance(currentChannel));
		this.sut.discardChannel();
	}

	@Test
	public void testCloseQuietlyFailsOnRuntimeExceptions(@Mocked final Channel currentChannel) throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel,currentChannel);
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
			currentChannel.isOpen();this.times=1;this.result=true;
			currentChannel.close();this.times=1;this.result=new Error("Failure");
		}};
		this.sut.connect();
		final Channel aChannel=this.sut.currentChannel();
		assertThat(aChannel,sameInstance(currentChannel));
		try {
			this.sut.discardChannel();
			fail("Should fail on runtime exception");
		} catch(final AssertionError e) {
		} catch (final Error e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testCloseConnectionQuietlySwallowsRegularExceptions() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel);
			ConnectionManagerTest.this.connection.isOpen();this.result=true;
			ConnectionManagerTest.this.channel.isOpen();this.times=1;this.result=true;
			ConnectionManagerTest.this.channel.close();this.times=1;
			ConnectionManagerTest.this.connection.close();this.result=new IOException("Failure");
		}};
		this.sut.connect();
		this.sut.disconnect();
	}

	@Test
	public void testIsConnectedIsFalseIfDisconnected() {
		assertThat(this.sut.isConnected(),equalTo(false));
	}

	@Test
	public void testIsConnectedIsFalseIfConnectionIsNotOpen() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel);
			ConnectionManagerTest.this.connection.isOpen();returns(true,false);
		}};
		this.sut.connect();
		assertThat(this.sut.isConnected(),equalTo(false));
	}

	@Test
	public void testIsConnectedUnlocksOnConnectionFailure() throws Exception {
		new MockUp<ConnectionFactory>() {
			@Mock
			public void setHost(final String host) {
			}
			@Mock
			public void setPort(final int port) {
			}
			@Mock
			public void setVirtualHost(final String virtualHost) {
			}
			@Mock
			public void setThreadFactory(final ThreadFactory threadFactory) {
			}
			@Mock
			public Connection newConnection() throws IOException, TimeoutException {
				return ConnectionManagerTest.this.connection;
			}
		};
		new Expectations() {{
			ConnectionManagerTest.this.connection.createChannel();returns(ConnectionManagerTest.this.channel);
			ConnectionManagerTest.this.connection.isOpen();returns(true);this.result=new RuntimeException("Failure");
		}};
		this.sut.connect();
		try {
			this.sut.isConnected();
		} catch (final RuntimeException e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testHasCustomToString() {
		assertThat(this.sut.toString(),not(equalTo(Utils.defaultToString(this.sut))));
	}

}
