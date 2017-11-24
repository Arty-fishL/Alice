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

/**
 * @author Jason Pratt
 */
public class NumberPropertyViewController extends TextFieldEditablePropertyViewController {
	/**
	 *
	 */
	private static final long serialVersionUID = 2442023266517441600L;
	protected javax.swing.JLabel numberLabel = new javax.swing.JLabel();

	public void set(final edu.cmu.cs.stage3.alice.core.Property property, final boolean includeDefaults,
			final boolean allowExpressions, final boolean omitPropertyName,
			final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		super.set(property, includeDefaults, allowExpressions, true, omitPropertyName, factory);
		allowEasyEditWithClick = false;
		refreshGUI();
	}

	@Override
	protected void setValueFromString(final String valueString) {
		final Double value = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.parseDouble(valueString);

		if (value != null) {
			((Runnable) factory.createItem(value)).run();
			final String propertyKey = "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + property.getName();
			property.getOwner().data.put(propertyKey, valueString);
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
					.showErrorDialog("I don't understand this number: " + valueString, null, false);
		}
	}

	@Override
	protected java.awt.Component getNativeComponent() {
		return numberLabel;
	}

	@Override
	protected java.awt.event.MouseListener getMouseListener() {
		return new java.awt.event.MouseAdapter() {

			@Override
			public void mouseReleased(final java.awt.event.MouseEvent ev) {
				if (ev.getX() >= 0 && ev.getX() < ev.getComponent().getWidth() && ev.getY() >= 0
						&& ev.getY() < ev.getComponent().getHeight()) {
					if (isEnabled()) {
						NumberPropertyViewController.this.popupButton.doClick();
					}
				}
			}
		};
	}

	@Override
	protected Class<Number> getNativeClass() {
		return Number.class;
	}

	@Override
	protected void updateNativeComponent() {
		numberLabel.setText(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(property.get(),
				property, property.getOwner().data));
	}

	@Override
	protected void refreshGUI() {
		if (isAncestorOf(textField)) {
			remove(textField);
		}
		super.refreshGUI();
	}
}
