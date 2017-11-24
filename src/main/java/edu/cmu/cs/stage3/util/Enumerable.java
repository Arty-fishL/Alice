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

package edu.cmu.cs.stage3.util;

import java.lang.reflect.Field;

public abstract class Enumerable implements java.io.Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -5624399045630086607L;
	private String m_repr = null;

	public static Enumerable[] getItems(final Class cls) {
		final java.util.Vector<Object> v = new java.util.Vector<Object>();
		final java.lang.reflect.Field[] fields = cls.getFields();
		for (final Field field : fields) {
			final int modifiers = field.getModifiers();
			if (java.lang.reflect.Modifier.isPublic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers)
					&& java.lang.reflect.Modifier.isStatic(modifiers)) {
				try {
					v.addElement(field.get(null));
				} catch (final IllegalAccessException iae) {
					iae.printStackTrace();
				}
			}
		}
		final Enumerable[] array = new Enumerable[v.size()];
		v.copyInto(array);
		return array;
	}

	public String getRepr() {
		if (m_repr == null) {
			final java.lang.reflect.Field[] fields = getClass().getFields();
			for (final Field field : fields) {
				final int modifiers = field.getModifiers();
				if (java.lang.reflect.Modifier.isPublic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers)
						&& java.lang.reflect.Modifier.isStatic(modifiers)) {
					try {
						if (equals(field.get(null))) {
							m_repr = field.getName();
							return m_repr;
						}
					} catch (final IllegalAccessException iae) {
						iae.printStackTrace();
					}
				}
			}
			return "unknown";
		} else {
			return m_repr;
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + getRepr() + "]";
	}

	protected static Enumerable valueOf(final String s, final Class cls) {
		final String[] markers = { cls.getName() + "[", "]" };
		final int begin = s.indexOf(markers[0]) + markers[0].length();
		final int end = s.indexOf(markers[1]);
		final String fieldName = s.substring(begin, end);
		final java.lang.reflect.Field[] fields = cls.getFields();
		for (final Field field : fields) {
			final int modifiers = field.getModifiers();
			if (java.lang.reflect.Modifier.isPublic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers)
					&& java.lang.reflect.Modifier.isStatic(modifiers)) {
				if (fieldName.equals(field.getName())) {
					try {
						return (Enumerable) field.get(null);
					} catch (final IllegalAccessException iae) {
						iae.printStackTrace();
					}
				}
			}
		}
		return null;
	}
}
