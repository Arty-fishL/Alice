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
public class StringPropertyViewController extends TextFieldEditablePropertyViewController {
	/**
	 *
	 */
	private static final long serialVersionUID = 2534677580647492902L;
	protected javax.swing.JLabel stringLabel = new javax.swing.JLabel();
	protected boolean emptyStringWritesNull;
	private final java.awt.Dimension minSize = new java.awt.Dimension(20, 16);

	public StringPropertyViewController() {
		stringLabel.setMinimumSize(minSize);
		textField.setColumns(20);
	}

	public void set(final edu.cmu.cs.stage3.alice.core.Property property, final boolean allowExpressions,
			final boolean omitPropertyName, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		set(property, allowExpressions, omitPropertyName, true, factory);
	}

	@Override
	public void set(final edu.cmu.cs.stage3.alice.core.Property property, final boolean includeDefaults,
			final boolean allowExpressions, final boolean omitPropertyName, final boolean emptyStringWritesNull,
			final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		super.set(property, includeDefaults, allowExpressions, true, omitPropertyName, factory);
		this.emptyStringWritesNull = emptyStringWritesNull
				&& !edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitNone(property);
		if (edu.cmu.cs.stage3.alice.core.response.Comment.class.isAssignableFrom(property.getOwner().getClass())
				|| edu.cmu.cs.stage3.alice.core.question.userdefined.Comment.class
						.isAssignableFrom(property.getOwner().getClass())) {
			stringLabel.setForeground(
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("commentForeground"));
			final int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
			stringLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, (int) (13 * fontSize / 12.0)));
		} else {
			stringLabel.setForeground(javax.swing.UIManager.getColor("Label.foreground"));
			stringLabel.setFont(javax.swing.UIManager.getFont("Label.font"));
		}
		refreshGUI();
	}

	public void set(final edu.cmu.cs.stage3.alice.core.Property property, final boolean allowExpressions,
			final boolean omitPropertyName, final boolean emptyStringWritesNull,
			final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		set(property, false, allowExpressions, omitPropertyName, emptyStringWritesNull, factory);
	}

	@Override
	protected void setValueFromString(String valueString) {
		if (valueString.trim().equals("")) {
			if (emptyStringWritesNull) {
				valueString = null;
			}
		}
		((Runnable) factory.createItem(valueString)).run();
	}

	@Override
	protected java.awt.Component getNativeComponent() {
		return stringLabel;
	}

	@Override
	protected Class<String> getNativeClass() {
		return String.class;
	}

	@Override
	protected void updateNativeComponent() {
		stringLabel.setText(property.get().toString());
	}

	@Override
	protected void refreshGUI() {
		// if( (property.getValue() != null) &&
		// (property.getValue().toString()).trim().equals( "" ) ) {
		// stringLabel.setPreferredSize( minSize );
		// } else {
		// stringLabel.setPreferredSize( null );
		// }
		stringLabel.setPreferredSize(null);
		if (isAncestorOf(textField)) {
			remove(textField);
		}
		super.refreshGUI();
	}
}
