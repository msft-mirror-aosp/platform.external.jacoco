/*******************************************************************************
 * Copyright (c) 2009, 2018 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.agent.rt.internal;

import java.util.Properties;

import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.runtime.AgentOptions;
import org.jacoco.core.runtime.RuntimeData;

/**
 * The API for classes instrumented in "offline" mode. The agent configuration
 * is provided through system properties prefixed with <code>jacoco.</code>.
 */
public final class Offline {

	// BEGIN android-change
	// private static final RuntimeData DATA;
	private static final ExecutionDataStore DATA;
	// END android-change
	private static final String CONFIG_RESOURCE = "/jacoco-agent.properties";

	static {
		// BEGIN android-change
		// final Properties config = ConfigLoader.load(CONFIG_RESOURCE,
		//		System.getProperties());
		// DATA = Agent.getInstance(new AgentOptions(config)).getData();
		DATA = new ExecutionDataStore();
		// END android-change
	}

	private Offline() {
		// no instances
	}

	/**
	 * API for offline instrumented classes.
	 * 
	 * @param classid
	 *            class identifier
	 * @param classname
	 *            VM class name
	 * @param probecount
	 *            probe count for this class
	 * @return probe array instance for this class
	 */
	public static boolean[] getProbes(final long classid,
			final String classname, final int probecount) {
		// BEGIN android-change
		// return DATA.getExecutionData(Long.valueOf(classid), classname,
		//		probecount).getProbes();
		synchronized (DATA) {
			return DATA.get(classid, classname, probecount).getProbes();
		}
		// END android-change
	}

	// BEGIN android-change
	/**
	 * Creates a default agent, using config loaded from the classpath resource and the system
	 * properties, and a runtime data instance populated with the execution data accumulated by
	 * the probes.
	 *
	 * @return the new agent
	 */
	static Agent createAgent() {
		final Properties config = ConfigLoader.load(CONFIG_RESOURCE,
				System.getProperties());
		return Agent.getInstance(new AgentOptions(config), new RuntimeData(DATA));
	}
	// END android-change
}
