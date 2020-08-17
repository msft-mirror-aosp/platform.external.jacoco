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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.IExecutionData;
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
	private static final Map<Long, IExecutionData> DATA = new HashMap<Long, IExecutionData>();
	// END android-change
	private static final String CONFIG_RESOURCE = "/jacoco-agent.properties";

	// BEGIN android-change
	// static {
	//	 final Properties config = ConfigLoader.load(CONFIG_RESOURCE,
	//			System.getProperties());
	//	 DATA = Agent.getInstance(new AgentOptions(config)).getData();
	// }
	// END android-change

	private Offline() {
		// no instances
	}

	// BEGIN android-change
	/**
	 * API for offline instrumented classes.
	 * 
	 * @param classid
	 *            class identifier
	 * @param classname
	 *            VM class name
	 * @param probecount
	 *            probe count for this class
	 * @return IExecutionData instance for this class
	 */
	public static IExecutionData getExecutionData(final long classid,
			final String classname, final int probecount) {
		// return DATA.getExecutionData(Long.valueOf(classid), classname,
		//		probecount).getProbes();
		synchronized (DATA) {
			IExecutionData entry = DATA.get(classid);
			if (entry == null) {
				entry = new ExecutionData(classid, classname, probecount);
				DATA.put(classid, entry);
			} else {
				entry.assertCompatibility(classid, classname, probecount);
			}
			return entry;
		}
	}
	// END android-change

	/**
	 * Creates a default agent, using config loaded from the classpath resource and the system
	 * properties, and a runtime data instance populated with the execution data accumulated by
	 * the probes up until this call is made (subsequent probe updates will not be reflected in
	 * this agent).
	 *
	 * @return the new agent
	 */
	static Agent createAgent() {
		final Properties config = ConfigLoader.load(CONFIG_RESOURCE,
				System.getProperties());
		synchronized (DATA) {
			ExecutionDataStore store = new ExecutionDataStore();
			for (IExecutionData data : DATA.values()) {
				store.put(data);
			}
			return Agent.getInstance(new AgentOptions(config), new RuntimeData(store));
		}
	}
	// END android-change
}
