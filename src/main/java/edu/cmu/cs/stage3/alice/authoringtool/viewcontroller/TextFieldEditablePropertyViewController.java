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
public abstract class TextFieldEditablePropertyViewController extends PropertyViewController {
	/**
	 *
	 */
	private static final long serialVersionUID = 1812628823758613744L;
	protected javax.swing.JTextField textField = new javax.swing.JTextField();
	protected java.awt.event.FocusListener focusListener = new java.awt.event.FocusAdapter() {

		@Override
		public void focusLost(final java.awt.event.FocusEvent ev) {
			stopEditing();
		}
	};
	protected boolean allowEasyEditWithClick = true;

	public TextFieldEditablePropertyViewController() {
		textField.setColumns(5);
		textField.setMinimumSize(new java.awt.Dimension(80, 18));
		textField.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent ev) {
				stopEditing();
				popupButton.requestFocus(); // hack
			}
		});
		textField.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyPressed(final java.awt.event.KeyEvent ev) {
				if (ev.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
					ev.consume();
					cancelEditing();
					popupButton.requestFocus(); // hack
				}
			}
		});
	}

	@Override
	public void set(final edu.cmu.cs.stage3.alice.core.Property property, final boolean includeDefaults,
			final boolean allowExpressions, final boolean includeOther, final boolean omitPropertyName,
			final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		super.set(property, includeDefaults, allowExpressions, includeOther, omitPropertyName, factory);
	}

	@Override
	protected java.awt.event.MouseListener getMouseListener() {
		return new java.awt.event.MouseAdapter() {

			@Override
			public void mousePressed(final java.awt.event.MouseEvent ev) {
				if (allowEasyEditWithClick
						&& (TextFieldEditablePropertyViewController.this.isAncestorOf(getNativeComponent())
								|| TextFieldEditablePropertyViewController.this.property.get() == null)) {
					TextFieldEditablePropertyViewController.this.editValue();
				} else {
					TextFieldEditablePropertyViewController.this.popupButton.doClick();
				}
			}
		};
	}

	public void editValue() {
		if (editingEnabled) {
			if (property.getValue() != null) {
				final String propertyKey = "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + property.getName();
				final Object userRepr = property.getOwner().data.get(propertyKey);
				if (userRepr instanceof String) {
					textField.setText((String) userRepr);
				} else {
					textField.setText(property.getValue().toString());
				}
			}
			if (isAncestorOf(getNativeComponent())) {
				this.remove(getNativeComponent());
			} else if (isAncestorOf(expressionLabel)) {
				this.remove(expressionLabel);
			}
			if (!isAncestorOf(textField)) {
				this.add(textField, java.awt.BorderLayout.CENTER);
			}
			revalidate();
			textField.requestFocus();
			textField.addFocusListener(focusListener);
			textField.selectAll();
		}
	}

	public void stopEditing() {
		textField.removeFocusListener(focusListener);
		final String valueString = textField.getText();
		// begin HACK
		remove(textField);
		add(getNativeComponent(), java.awt.BorderLayout.CENTER);
		getNativeComponent().requestFocus();
		// end HACK
		setValueFromString(valueString);
		refreshGUI();
	}

	public void cancelEditing() {
		textField.removeFocusListener(focusListener);
		// begin HACK
		remove(textField);
		add(getNativeComponent(), java.awt.BorderLayout.CENTER);
		// end HACK
		refreshGUI();
	}

	@Override
	protected void refreshGUI() {
		if (isAncestorOf(textField)) {
			remove(textField);
		}
		super.refreshGUI();
	}

	protected abstract void setValueFromString(String valueString);
}
