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

import java.util.ArrayList;
import java.util.List;

/**
 * An IncludeExcludeMatcher matches a given input if
 * at least one inclusion matches and no exclusions match.
 */
public class IncludeExcludeMatcher<T> implements Predicate<T> {
	private List<Predicate<T>> inclusions = new ArrayList<Predicate<T>>();
	private List<Predicate<T>> exclusions = new ArrayList<Predicate<T>>();

	/**
	 * Includes the given matcher
	 * @param inclusion new matcher to include
	 * @return this object (for chaining several calls)
	 */
	public IncludeExcludeMatcher include(Predicate<T> inclusion) {
		inclusions.add(inclusion);
		return this;
	}

	/**
	 * Excludes a given matcher
	 * @param exclusion
	 * @return this object (for chaining several calls)
	 */
	public IncludeExcludeMatcher exclude(Predicate<T> exclusion) {
		exclusions.add(exclusion);
		return this;
	}

	/**
	 * Tells whether this matcher matches this string
	 * @param input the string match
	 * @return whether the matcher matches
	 */
	@Override
	public boolean apply(T input) {
		// doesn't match if an exclusion matches
		for (Predicate<T> exclusion : exclusions) {
			if (exclusion.apply(input)) {
				return false;
			}
		}
		// does match if an inclusion matches
		for (Predicate<T> inclusion : inclusions) {
			if (inclusion.apply(input)) {
				return true;
			}
		}
		// no match; choose a default based on whether any includes were given
		return (inclusions.size() == 0);
	}
}
