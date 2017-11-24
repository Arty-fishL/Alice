/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.core.criterion;

public class ElementNameContainsCriterion implements edu.cmu.cs.stage3.util.Criterion {
	private final String m_contains;
	private final boolean m_ignoreCase;

	public ElementNameContainsCriterion(final String contains) {
		this(contains, true);
	}

	public ElementNameContainsCriterion(final String contains, final boolean ignoreCase) {
		m_contains = contains;
		m_ignoreCase = ignoreCase;
	}

	public String getContains() {
		return m_contains;
	}

	public boolean getIgnoreCase() {
		return m_ignoreCase;
	}

	@Override
	public boolean accept(final Object o) {
		if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
			final String name = ((edu.cmu.cs.stage3.alice.core.Element) o).name.getStringValue();
			if (m_contains == null) {
				return name == null;
			} else if (name == null) {
				return false;
			} else {
				if (m_ignoreCase) {
					return name.toLowerCase().indexOf(m_contains.toLowerCase()) != -1;
				} else {
					return name.indexOf(m_contains) != -1;
				}
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + m_contains + "]";
	}

	protected static ElementNameContainsCriterion valueOf(final String s, final Class<ElementNameContainsCriterion> cls) {
		final String beginMarker = cls.getName() + "[";
		final String endMarker = "]";
		final int begin = s.indexOf(beginMarker) + beginMarker.length();
		final int end = s.lastIndexOf(endMarker);
		try {
			final Class[] types = { String.class };
			final Object[] values = { s.substring(begin, end) };
			final java.lang.reflect.Constructor<ElementNameContainsCriterion> constructor = cls.getConstructor(types);
			return constructor.newInstance(values);
		} catch (final Throwable t) {
			throw new RuntimeException();
		}
	}

	public static ElementNameContainsCriterion valueOf(final String s) {
		return valueOf(s, edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion.class);
	}
}