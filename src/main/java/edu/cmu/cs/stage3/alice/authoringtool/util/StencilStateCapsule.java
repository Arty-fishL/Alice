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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.util.List;
import java.util.Map;

/**
 * @author Jason Pratt
 */
public class StencilStateCapsule implements edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule {
	protected List<String> existantElements = new java.util.ArrayList<String>();
	protected List<String> nonExistantElements = new java.util.ArrayList<String>();
	protected Map<String, String> propertyValues = new java.util.HashMap<String, String>();

	protected Map<String, Integer> elementPositions = new java.util.HashMap<String, Integer>();

	/**
	 * elementKey should be the element's key, relative to the world; i.e.
	 * element.getKey( world ).
	 */
	public void addExistantElement(final String elementKey) {
		existantElements.add(elementKey);
	}

	/**
	 * elementKey should be the element's key, relative to the world; i.e.
	 * element.getKey( world ).
	 */
	public void removeExistantElement(final String elementKey) {
		existantElements.remove(elementKey);
	}

	public String[] getExistantElements() {
		return existantElements.toArray(new String[0]);
	}

	/**
	 * elementKey should be the element's key, relative to the world; i.e.
	 * element.getKey( world ).
	 */
	public void addNonExistantElement(final String elementKey) {
		nonExistantElements.add(elementKey);
	}

	/**
	 * elementKey should be the element's key, relative to the world; i.e.
	 * element.getKey( world ).
	 */
	public void removeNonExistantElement(final String elementKey) {
		nonExistantElements.remove(elementKey);
	}

	public String[] getNonExistantElements() {
		return nonExistantElements.toArray(new String[0]);
	}

	/**
	 * propertyKey should be of the form (element key relative to
	 * world).(property name). i.e. "Bunny.Head.color"
	 *
	 * valueRepr should be the String representation of the property's value as
	 * returned by AuthoringToolResources.getReprForValue( property.get(), true
	 * )
	 */
	public void putPropertyValue(final String propertyKey, final String valueRepr) {
		propertyValues.put(propertyKey, valueRepr);
	}

	/**
	 * propertyKey should be of the form (element key relative to
	 * world).(property name). i.e. "Bunny.Head.color"
	 */
	public void removePropertyValue(final String propertyKey) {
		propertyValues.remove(propertyKey);
	}

	/**
	 * elementKey should be the element's key, relative to the world; i.e.
	 * element.getKey( world ).
	 *
	 * position should be the element's index relative to its parent
	 */
	public void putElementPosition(final String elementKey, final int position) {
		elementPositions.put(elementKey, new Integer(position));
	}

	/**
	 * elementKey should be the element's key, relative to the world; i.e.
	 * element.getKey( world ).
	 */
	public void removeElementPosition(final String elementKey) {
		elementPositions.remove(elementKey);
	}

	public int getElementPosition(final String elementKey) {
		final Integer value = elementPositions.get(elementKey);
		if (value != null) {
			return value.intValue();
		} else {
			return -1;
		}
	}

	/**
	 * propertyKey should be of the form (element key relative to
	 * world).(property name). i.e. "Bunny.Head.color"
	 */
	public String getPropertyValue(final String propertyKey) {
		return propertyValues.get(propertyKey);
	}

	public java.util.Set<String> getPropertyValueKeySet() {
		return propertyValues.keySet();
	}

	public java.util.Set<String> getElementPositionKeySet() {
		return elementPositions.keySet();
	}

	@Override
	public void parse(final String storableRepr) {
		final java.util.StringTokenizer st = new java.util.StringTokenizer(storableRepr, "|", false);
		String token = st.nextToken();
		if (!"existantElements".equals(token)) {
			throw new IllegalArgumentException("expected \"existantElements\"; got " + token);
		}

		java.util.StringTokenizer st2 = new java.util.StringTokenizer(st.nextToken(), "?", false);
		while (st2.hasMoreTokens()) {
			addExistantElement(st2.nextToken());
		}

		token = st.nextToken();
		if (!"nonExistantElements".equals(token)) {
			throw new IllegalArgumentException("expected \"nonExistantElements\"; got " + token);
		}
		st2 = new java.util.StringTokenizer(st.nextToken(), "?", false);
		while (st2.hasMoreTokens()) {
			addNonExistantElement(st2.nextToken());
		}

		token = st.nextToken();
		if (!"propertyValues".equals(token)) {
			throw new IllegalArgumentException("expected \"propertyValues\"; got " + token);
		}
		st2 = new java.util.StringTokenizer(st.nextToken(), "?", false);
		while (st2.hasMoreTokens()) {
			token = st2.nextToken();
			final int colonIndex = token.indexOf(":");
			final String propertyKey = token.substring(0, colonIndex);
			final String valueRepr = token.substring(colonIndex + 1);
			putPropertyValue(propertyKey, valueRepr);
		}

		if (st.hasMoreTokens()) {
			token = st.nextToken();
			if (!"elementPositions".equals(token)) {
				throw new IllegalArgumentException("expected \"elementPositions\"; got " + token);
			}
			st2 = new java.util.StringTokenizer(st.nextToken(), "?", false);
			while (st2.hasMoreTokens()) {
				token = st2.nextToken();
				final int colonIndex = token.indexOf(":");
				final String elementKey = token.substring(0, colonIndex);
				final String valueRepr = token.substring(colonIndex + 1);
				putElementPosition(elementKey, Integer.parseInt(valueRepr));
			}
		}

	}

	@Override
	public String getStorableRepr() {
		// MAKE THIS INSERT A ? when there aren't any values to put in
		String storableRepr = "";

		storableRepr += "existantElements|?";

		for (final String string : existantElements) {
			storableRepr += string + "?";
		}
		storableRepr += "|";

		storableRepr += "nonExistantElements|?";
		for (final String string : nonExistantElements) {
			storableRepr += string + "?";
		}
		storableRepr += "|";

		storableRepr += "propertyValues|?";
		for (final String propertyKey : propertyValues.keySet()) {
			final String valueRepr = propertyValues.get(propertyKey);
			storableRepr += propertyKey + ":" + valueRepr + "?";
		}
		storableRepr += "|";

		storableRepr += "elementPositions|?";
		for (final String elementKey : elementPositions.keySet()) {
			final Integer valueRepr = elementPositions.get(elementKey);
			storableRepr += elementKey + ":" + valueRepr + "?";
		}
		storableRepr += "|";

		return storableRepr;
	}
}