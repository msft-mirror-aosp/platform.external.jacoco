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
package org.jacoco.examples;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.hamcrest.Matcher;
import org.junit.rules.ExternalResource;

/**
 * In-Memory buffer to assert console output.
 */
public class ConsoleOutput extends ExternalResource {

	private final ByteArrayOutputStream buffer;

	public final PrintStream stream;

	public ConsoleOutput() {
		this.buffer = new ByteArrayOutputStream();
		try {
			this.stream = new PrintStream(buffer, true, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError(e.getMessage());
		}
	}

	@Override
	protected void after() {
		buffer.reset();
	}

	public static Matcher<String> containsLine(String line) {
		return containsString(String.format("%s%n", line));
	}

	public static Matcher<String> isEmpty() {
		return is("");
	}

	public String getContents() {
		try {
			return new String(buffer.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			fail(e.getMessage());
			return "";
		}
	}

	public void expect(Matcher<String> matcher) {
		assertThat(getContents(), matcher);
	}

}
