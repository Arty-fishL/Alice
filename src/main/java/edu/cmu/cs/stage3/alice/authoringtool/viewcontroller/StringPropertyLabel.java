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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.core.Element;

/**
 * @author Jason Pratt
 */
public class StringPropertyLabel extends PropertyLabel {
	/**
	 *
	 */
	private static final long serialVersionUID = -6036450751409572901L;

	public StringPropertyLabel(final edu.cmu.cs.stage3.alice.core.property.StringProperty property) {
		super(property);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		String propertyName = ((edu.cmu.cs.stage3.alice.core.property.StringProperty) property).getStringValue();
		if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation
				&& property.getName().equals("propertyName")) {
			final edu.cmu.cs.stage3.alice.core.Element element = ((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) property
					.getOwner()).element.getElementValue();
			if (element != null) {
				Class<? extends Element> elementClass = element.getClass();
				while (edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(elementClass)) {
					final String propertyKey = elementClass.getName() + "." + propertyName;
					if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getName(propertyKey) != null) {
						propertyName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
								.getName(propertyKey);
						break;
					}
					elementClass = (Class<? extends Element>) elementClass.getSuperclass();
				}
			}
		}
		setText(propertyName);
	}
}
