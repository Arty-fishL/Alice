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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;

import edu.cmu.cs.stage3.util.StringTypePair;

/**
 * @author Jason Pratt
 */
public class TypeChooser extends javax.swing.JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 4056975601661381544L;
	private Class type;
	private final javax.swing.ButtonGroup buttonGroup;
	private final java.util.HashMap<String, Class> typeMap = new java.util.HashMap<String, Class>();
	private final java.util.HashSet<ChangeListener> changeListeners = new java.util.HashSet<ChangeListener>();
	private final JRadioButton numberButton = new JRadioButton("Number");
	private final JRadioButton booleanButton = new JRadioButton("Boolean");
	private final JRadioButton objectButton = new JRadioButton("Object");
	private final JRadioButton otherButton = new JRadioButton("Other...");
	private final JComboBox<String> otherCombo = new JComboBox<String>();
	private final edu.cmu.cs.stage3.alice.authoringtool.util.CheckForValidityCallback okButtonCallback;

	public TypeChooser(final edu.cmu.cs.stage3.alice.authoringtool.util.CheckForValidityCallback okButtonCallback) {
		otherCombo.setEditable(true);
		this.okButtonCallback = okButtonCallback;
		setLayout(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		// gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		add(numberButton, gbc);
		add(booleanButton, gbc);
		add(objectButton, gbc);
		add(otherButton, gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.weightx = 1.0;
		add(otherCombo, gbc);

		buttonGroup = new javax.swing.ButtonGroup();
		buttonGroup.add(numberButton);
		buttonGroup.add(booleanButton);
		buttonGroup.add(objectButton);
		buttonGroup.add(otherButton);

		final java.awt.event.ActionListener radioListener = new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent ev) {
				if (ev.getSource() == numberButton) {
					type = Number.class;
					otherCombo.setEnabled(false);
					fireStateChanged(numberButton);
					checkTypeValidity();
				} else if (ev.getSource() == booleanButton) {
					type = Boolean.class;
					otherCombo.setEnabled(false);
					fireStateChanged(booleanButton);
					checkTypeValidity();
				} else if (ev.getSource() == objectButton) {
					type = edu.cmu.cs.stage3.alice.core.Model.class;
					otherCombo.setEnabled(false);
					fireStateChanged(objectButton);
					checkTypeValidity();
				} else if (ev.getSource() == otherButton) {
					otherCombo.setEnabled(true);
					TypeChooser.this.parseOtherType();
				}

			}
		};
		numberButton.addActionListener(radioListener);
		booleanButton.addActionListener(radioListener);
		objectButton.addActionListener(radioListener);
		otherButton.addActionListener(radioListener);

		final edu.cmu.cs.stage3.util.StringTypePair[] defaultVariableTypes = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
				.getDefaultVariableTypes();
		for (final StringTypePair defaultVariableType : defaultVariableTypes) {
			typeMap.put(defaultVariableType.getString().trim(), defaultVariableType.getType());
			otherCombo.addItem(defaultVariableType.getString());
		}

		((javax.swing.JTextField) otherCombo.getEditor().getEditorComponent()).getDocument()
				.addDocumentListener(new javax.swing.event.DocumentListener() {
					@Override
					public void changedUpdate(final javax.swing.event.DocumentEvent ev) {
						TypeChooser.this.parseOtherType();
					}

					@Override
					public void insertUpdate(final javax.swing.event.DocumentEvent ev) {
						TypeChooser.this.parseOtherType();
					}

					@Override
					public void removeUpdate(final javax.swing.event.DocumentEvent ev) {
						TypeChooser.this.parseOtherType();
					}
				});

		numberButton.setSelected(true);
		type = Number.class;
		otherCombo.setEnabled(false);
	}

	protected void parseOtherType() {
		final String typeString = ((javax.swing.JTextField) otherCombo.getEditor().getEditorComponent()).getText()
				.trim();
		Class newType = typeMap.get(typeString);
		if (newType == null) {
			try {
				newType = Class.forName(typeString);
			} catch (final ClassNotFoundException e) {
				newType = null;
			}
		}

		if (newType == null) {
			otherCombo.getEditor().getEditorComponent().setForeground(java.awt.Color.red);
		} else {
			otherCombo.getEditor().getEditorComponent().setForeground(java.awt.Color.black);
		}

		if (type != newType) {
			type = newType;
			fireStateChanged(otherCombo);
		}
		checkTypeValidity();
	}

	private void checkTypeValidity() {
		okButtonCallback.setValidity(this, type != null);
	}

	public Class getType() {
		return type;
	}

	public void addCurrentTypeToList() {
		if (otherButton.isSelected() && type != null) {
			final String typeString = ((javax.swing.JTextField) otherCombo.getEditor().getEditorComponent()).getText()
					.trim();
			if (!typeMap.containsKey(typeString)) {
				otherCombo.addItem(typeString);
				typeMap.put(typeString, type);
			}
		}
	}

	public void addChangeListener(final javax.swing.event.ChangeListener listener) {
		changeListeners.add(listener);
	}

	public void removeChangeListener(final javax.swing.event.ChangeListener listener) {
		changeListeners.remove(listener);
	}

	protected void fireStateChanged(final Object source) {
		final javax.swing.event.ChangeEvent ev = new javax.swing.event.ChangeEvent(source);
		for (final java.util.Iterator<ChangeListener> iter = changeListeners.iterator(); iter.hasNext();) {
			iter.next().stateChanged(ev);
		}
	}
}