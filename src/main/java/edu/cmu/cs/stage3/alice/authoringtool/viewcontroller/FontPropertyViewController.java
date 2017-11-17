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
public class FontPropertyViewController extends javax.swing.JButton implements
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1578493966627461367L;
	protected edu.cmu.cs.stage3.alice.core.Property property;
	protected boolean omitPropertyName;
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener propertyListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		@Override
		public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}

		@Override
		public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			FontPropertyViewController.this.refreshGUI();
		}
	};

	public FontPropertyViewController() {
		addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent ev) {
				if (property != null) {
					final boolean isFor3DText = property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Text3D;
					String sampleText = null;
					if (isFor3DText) { // special-cased
						sampleText = ((edu.cmu.cs.stage3.alice.core.Text3D) property.getOwner()).text.getStringValue();
					}
					java.awt.Font currentFont = null;
					if (property.getValue() instanceof java.awt.Font) {
						currentFont = (java.awt.Font) property.getValue();
					}
					final edu.cmu.cs.stage3.alice.authoringtool.dialog.FontPanel fontPanel = new edu.cmu.cs.stage3.alice.authoringtool.dialog.FontPanel(
							currentFont, !isFor3DText, true, sampleText);
					if (edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(fontPanel, "Choose a Font",
							javax.swing.JOptionPane.OK_CANCEL_OPTION,
							javax.swing.JOptionPane.PLAIN_MESSAGE) == javax.swing.JOptionPane.OK_OPTION) {
						final java.awt.Font font = fontPanel.getChosenFont();
						if (font != null) {
							property.set(font);
						}
					}
				}
			}
		});
	}

	public void set(final edu.cmu.cs.stage3.alice.core.Property property, final boolean omitPropertyName) {
		clean();
		this.property = property;
		this.omitPropertyName = omitPropertyName;
		setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
				.getColor("propertyViewControllerBackground"));
		setMargin(new java.awt.Insets(0, 4, 0, 4));
		startListening();
		refreshGUI();
	}

	@Override
	public void goToSleep() {
		stopListening();
	}

	@Override
	public void wakeUp() {
		startListening();
	}

	@Override
	public void clean() {
		stopListening();
	}

	@Override
	public void die() {
		stopListening();
	}

	@Override
	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

	public void startListening() {
		if (property != null) {
			property.addPropertyListener(propertyListener);
		}
	}

	public void stopListening() {
		if (property != null) {
			property.removePropertyListener(propertyListener);
		}
	}

	protected void refreshGUI() {
		final Object value = property.get();
		final StringBuffer repr = new StringBuffer();

		if (!omitPropertyName) {
			repr.append(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(property) + " = ");
		}

		if (value instanceof edu.cmu.cs.stage3.alice.core.Expression) {
			repr.append(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
					.getNameInContext((edu.cmu.cs.stage3.alice.core.Element) value, property.getOwner()));
		} else if (value == null) {
			repr.append("<None>");
		} else if (value instanceof java.awt.Font) {
			final java.awt.Font font = (java.awt.Font) value;
			repr.append(font.getFontName());
			if (!(property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Text3D)) {
				repr.append(", " + font.getSize());
			}
		} else {
			throw new RuntimeException("Bad value: " + value);
		}

		setText(repr.toString());
		revalidate();
		repaint();
	}
}
