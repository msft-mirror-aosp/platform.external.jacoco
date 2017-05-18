/*******************************************************************************
 * Copyright (c) 2009, 2017 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeffry Gaston - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.core.matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IncludeExcludeMatcherTest {

	@Test
	public void testEmpty() {
		assertTrue(new IncludeExcludeMatcher<String>().apply("includeMe"));
	}

	@Test
	public void testSingleExclude() {
		IncludeExcludeMatcher<String> matcher = new IncludeExcludeMatcher<String>()
				.exclude(new WildcardMatcher("excluded"));
		assertTrue(matcher.apply("included"));
		assertFalse(matcher.apply("excluded"));
	}

	@Test
	public void testMultipleExcludes() {
		IncludeExcludeMatcher<String> matcher = new IncludeExcludeMatcher<String>().exclude(
				new WildcardMatcher("excluded"))
						.exclude(new WildcardMatcher("excluded2"));
		assertTrue(matcher.apply("included"));
		assertFalse(matcher.apply("excluded"));
		assertFalse(matcher.apply("excluded2"));
	}

	@Test
	public void testSingleInclude() {
		IncludeExcludeMatcher<String> matcher = new IncludeExcludeMatcher<String>()
				.include(new WildcardMatcher("include me"));
		assertTrue(matcher.apply("include me"));
		assertFalse(matcher.apply("not me"));
	}

	@Test
	public void testIncludesAndExcludes() {
		IncludeExcludeMatcher<String> matcher = new IncludeExcludeMatcher<String>()
				.include(new WildcardMatcher("inclusion1"))
				.include(new WildcardMatcher("me too"))
				.exclude(new WildcardMatcher("not me"))
				.exclude(new WildcardMatcher("nope"));
		assertTrue(matcher.apply("inclusion1"));
		assertTrue(matcher.apply("me too"));
		assertFalse(matcher.apply("not me"));
		assertFalse(matcher.apply("nope"));
		assertFalse(matcher.apply("other"));
	}

	@Test
	public void testExcludedInclusion() {
		IncludeExcludeMatcher<String> matcher = new IncludeExcludeMatcher<String>()
				.include(new WildcardMatcher("a"))
				.exclude(new WildcardMatcher("a"));
		assertFalse(matcher.apply("a"));
		assertFalse(matcher.apply("b"));
	}

}
