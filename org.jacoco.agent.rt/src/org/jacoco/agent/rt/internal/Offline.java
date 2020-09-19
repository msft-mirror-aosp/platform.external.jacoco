/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.IExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.MappedExecutionData;
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
	private static int PID = -1;
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
				// BEGIN android-change
				// The list below are not allowed to use the memory-mapped
				// implementation due either:
				//   1) They are loaded at VM initialization time.
				//	a) android.*
				//	b) dalvik.*
				//	c) libcore.*
				//   2) They are used by the memory-mapped execution data
				//      implementation, which would cause a stack overflow due
				//      to the circular dependency.
				//	a) com.android.i18n.*
				//	b) com.android.icu.*
				//	c) java.*
				//	d) org.apache.*
				//	e) sun.*
				// These classes can still have coverage collected through the
				// normal execution data process, but requires a flush to be done
				// instead of simply being available on disk at all times.
				if (classname.startsWith("android/")
					|| classname.startsWith("com/android/i18n/")
					|| classname.startsWith("com/android/icu/")
					|| classname.startsWith("dalvik/")
					|| classname.startsWith("java/")
					|| classname.startsWith("libcore/")
					|| classname.startsWith("org/apache/")
					|| classname.startsWith("sun/")) {
					entry = new ExecutionData(classid, classname, probecount);
				} else {
					try {
						int pid = getPid();
						if (PID != pid) {
							PID = pid;
							rebuildExecutionData(pid);
						}
						entry = new MappedExecutionData(
							classid, classname, probecount);
					} catch (IOException e) {
						// Fall back to non-memory-mapped execution data.
						entry = new ExecutionData(
							classid, classname, probecount);
					}
				}
				// END android-change
				DATA.put(classid, entry);
			} else {
				entry.assertCompatibility(classid, classname, probecount);
			}
			return entry;
		}
	}

	private static void rebuildExecutionData(int pid) throws IOException {
		MappedExecutionData.prepareFile(pid);
		synchronized (DATA) {
			for (IExecutionData execData : DATA.values()) {
				if (execData instanceof MappedExecutionData) {
					// Create new instances of MappedExecutionData using the
					// new file, but don't copy the old data. Old data will
					// remain in its existing file and can be merged during
					// post-processing.
					DATA.put(
						execData.getId(),
						new MappedExecutionData(
							execData.getId(),
							execData.getName(),
							execData.getProbeCount()));
				}
			}
		}
	}

	/**
	 * Helper function to determine the pid of this process.
	 */
	private static int getPid() throws IOException {
		// Read /proc/self and resolve it to obtain its pid.
		return Integer.parseInt(new File("/proc/self").getCanonicalFile().getName());
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
