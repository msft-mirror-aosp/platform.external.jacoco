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

/**
 * A simple interface telling whether a predicate matches an input.
 * Once the minimum supported version of Java is Java 1.8, then this can be replaced with the
 * built-in Java predicate.
 * While it could work to add a dependency on a library providing a similar interface, we prefer
 * to keep the number of dependencies low, to avoid forcing other dependencies onto
 * any builds that depend on Jacoco.
 */
public interface Predicate<T> {
	boolean apply(final T s);
}
