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

package edu.cmu.cs.stage3.alice.core.property;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;

public class DictionaryProperty extends ObjectProperty {
	public DictionaryProperty(final Element owner, final String name, final Dictionary<?,?> defaultValue) {
		super(owner, name, defaultValue, Dictionary.class);
	}

	public Dictionary<?,?> getDictionaryValue() {
		return (Dictionary<?,?>) getValue();
	}
	
	/** Unused ??
	private static Object valueOf(final Class<?> cls, final String text) {
		final Class<?>[] parameterTypes = { String.class };
		try {
			final java.lang.reflect.Method valueOfMethod = cls.getMethod("valueOf", parameterTypes);
			final int modifiers = valueOfMethod.getModifiers();
			if (java.lang.reflect.Modifier.isPublic(modifiers) && java.lang.reflect.Modifier.isStatic(modifiers)) {
				final Object[] parameters = { text };
				return valueOfMethod.invoke(null, parameters);
			} else {
				throw new RuntimeException("valueOf method not public static.");
			}
		} catch (final NoSuchMethodException nsme) {
			nsme.printStackTrace();
		} catch (final IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (final java.lang.reflect.InvocationTargetException ite) {
			ite.printStackTrace();
		}
		return null;
	}
	*/

	@Override
	protected void decodeObject(final org.w3c.dom.Element node, final edu.cmu.cs.stage3.io.DirectoryTreeLoader loader,
			final Vector<PropertyReference> referencesToBeResolved, final double version) throws java.io.IOException {
		final Dictionary<Object, Object> dict = new Hashtable<Object, Object>();
		final org.w3c.dom.NodeList entryNodeList = node.getElementsByTagName("entry");
		for (int i = 0; i < entryNodeList.getLength(); i++) {
			final org.w3c.dom.Element entryNode = (org.w3c.dom.Element) entryNodeList.item(i);
			final org.w3c.dom.Element keyNode = (org.w3c.dom.Element) entryNode.getElementsByTagName("key").item(0);
			final String keyTypeName = keyNode.getAttribute("class");
			Object key;
			try {
				final Class<?> keyType = Class.forName(keyTypeName);
				if (keyType == String.class) {
					key = getNodeText(keyNode);
				} else {
					key = getValueOf(keyType, getNodeText(keyNode));
				}
			} catch (final ClassNotFoundException cnfe) {
				throw new RuntimeException(keyTypeName);
			}

			final org.w3c.dom.Element valueNode = (org.w3c.dom.Element) entryNode.getElementsByTagName("value").item(0);
			final String valueTypeName = valueNode.getAttribute("class");
			Object value;
			try {
				final Class<?> valueType = Class.forName(valueTypeName);
				if (valueType == String.class) {
					value = getNodeText(valueNode);
				} else {
					value = getValueOf(valueType, getNodeText(valueNode));
				}
			} catch (final ClassNotFoundException cnfe) {
				throw new RuntimeException(valueTypeName);
			}
			dict.put(key, value);
		}
		set(dict);
	}

	@Override
	protected void encodeObject(final org.w3c.dom.Document document, final org.w3c.dom.Element node,
			final edu.cmu.cs.stage3.io.DirectoryTreeStorer storer,
			final edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator) throws java.io.IOException {
		final Dictionary<?, ?> dict = getDictionaryValue();
		if (dict != null) {
			final Enumeration<?> enum0 = dict.keys();
			while (enum0.hasMoreElements()) {
				final Object key = enum0.nextElement();
				final Object value = dict.get(key);

				final org.w3c.dom.Element entryNode = document.createElement("entry");

				final org.w3c.dom.Element keyNode = document.createElement("key");
				keyNode.setAttribute("class", key.getClass().getName());
				keyNode.appendChild(createNodeForString(document, key.toString()));

				final org.w3c.dom.Element valueNode = document.createElement("value");
				valueNode.setAttribute("class", value.getClass().getName());
				valueNode.appendChild(createNodeForString(document, value.toString()));

				entryNode.appendChild(keyNode);
				entryNode.appendChild(valueNode);
				node.appendChild(entryNode);
			}
		}
	}

	public Enumeration<?> elements() {
		final Dictionary<?, ?> dict = getDictionaryValue();
		if (dict != null) {
			return dict.elements();
		} else {
			return null;
		}
	}

	public Object get(final Object key) {
		final Dictionary<?, ?> dict = getDictionaryValue();
		if (dict != null) {
			return dict.get(key);
		} else {
			return null;
		}
	}

	public boolean isEmpty() {
		final Dictionary<?, ?> dict = getDictionaryValue();
		if (dict != null) {
			return dict.isEmpty();
		} else {
			return true;
		}
	}

	public Enumeration<?> keys() {
		final Dictionary<?, ?> dict = getDictionaryValue();
		if (dict != null) {
			return dict.keys();
		} else {
			return null;
		}
	}

	public Object put(final Object key, final Object value) {
		final Dictionary<?, ?> dict = getDictionaryValue();
		final Dictionary<Object, Object> newDict = new Hashtable<Object, Object>();
		if (dict != null) {
			// todo: optimize?
			final Enumeration<?> enum0 = dict.keys();
			while (enum0.hasMoreElements()) {
				final Object k = enum0.nextElement();
				final Object v = dict.get(k);
				newDict.put(k, v);
			}
		}
		final Object o = newDict.put(key, value);
		set(newDict);
		return o;
	}

	public Object remove(final Object key) {
		final Dictionary<?, ?> dict = getDictionaryValue();
		final Dictionary<Object, Object> newDict = new Hashtable<Object, Object>();
		Object value = null;
		if (dict != null) {
			// todo: optimize?
			final Enumeration<?> enum0 = dict.keys();
			while (enum0.hasMoreElements()) {
				final Object k = enum0.nextElement();
				final Object v = dict.get(k);
				if (k.equals(key)) {
					value = v;
				} else {
					newDict.put(k, v);
				}
			}
		}
		if (value != null) {
			set(newDict);
		}
		return value;
	}

	public int size() {
		final Dictionary<?, ?> dict = getDictionaryValue();
		if (dict != null) {
			return dict.size();
		} else {
			return 0;
		}
	}
}
