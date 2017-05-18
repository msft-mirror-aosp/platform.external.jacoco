/*******************************************************************************
 * Copyright (c) 2009, 2017 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.core.matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WildcardMatcherTest {

	@Test
	public void testEmpty() {
		assertTrue(new WildcardMatcher("").apply(""));
		assertFalse(new WildcardMatcher("").apply("abc"));
	}

	@Test
	public void testExact() {
		assertTrue(new WildcardMatcher("abc/def.txt").apply("abc/def.txt"));
	}

	@Test
	public void testCaseSensitive() {
		assertFalse(new WildcardMatcher("abcdef").apply("abcDef"));
		assertFalse(new WildcardMatcher("ABCDEF").apply("AbCDEF"));
	}

	@Test
	public void testQuote() {
		assertFalse(new WildcardMatcher("rst.xyz").apply("rstAxyz"));
		assertTrue(new WildcardMatcher("(x)+").apply("(x)+"));
	}

	@Test
	public void testWildcards() {
		assertTrue(new WildcardMatcher("*").apply(""));
		assertTrue(new WildcardMatcher("*").apply("java/lang/Object"));
		assertTrue(new WildcardMatcher("*Test").apply("jacoco/MatcherTest"));
		assertTrue(new WildcardMatcher("Matcher*").apply("Matcher"));
		assertTrue(new WildcardMatcher("Matcher*").apply("MatcherTest"));
		assertTrue(new WildcardMatcher("a*b*a").apply("a-b-b-a"));
		assertFalse(new WildcardMatcher("a*b*a").apply("alaska"));
		assertTrue(new WildcardMatcher("Hello?orld").apply("HelloWorld"));
		assertFalse(new WildcardMatcher("Hello?orld").apply("HelloWWWorld"));
		assertTrue(new WildcardMatcher("?aco*").apply("jacoco"));
	}

	@Test
	public void testMultiExpression() {
		assertTrue(new WildcardMatcher("Hello:World").apply("World"));
		assertTrue(new WildcardMatcher("Hello:World").apply("World"));
		assertTrue(new WildcardMatcher("*Test:*Foo").apply("UnitTest"));
	}

	@Test
	public void testDollar() {
		assertTrue(new WildcardMatcher("*$*").apply("java/util/Map$Entry"));
		assertTrue(new WildcardMatcher("*$$$*")
				.apply("org/example/Enity$$$generated123"));
	}

}
