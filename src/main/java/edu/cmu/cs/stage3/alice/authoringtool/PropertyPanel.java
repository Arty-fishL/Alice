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

package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class PropertyPanel extends javax.swing.JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = -1488293266289367456L;
	protected edu.cmu.cs.stage3.alice.core.Element element;
	protected edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor variableGroupEditor = new edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor();

	public PropertyPanel() {
		jbInit();
		propertyGroupCombo.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent ev) {
				final java.awt.CardLayout cardLayout = (java.awt.CardLayout) propertyManipulationPanel.getLayout();
				cardLayout.show(propertyManipulationPanel, (String) ev.getItem());
			}
		});
	}

	public edu.cmu.cs.stage3.alice.core.Element getElement() {
		return element;
	}

	public void setElement(final edu.cmu.cs.stage3.alice.core.Element element) {
		/*
		 * if( element instanceof edu.cmu.cs.stage3.alice.core.World ) { //TODO:
		 * more advanced handling edu.cmu.cs.stage3.alice.core.Element[] scenes
		 * = element.getChildren( edu.cmu.cs.stage3.alice.core.Scene.class );
		 * if( scenes.length > 0 ) { element = scenes[0]; } }
		 */

		this.element = element;

		refreshGUI();
	}

	public void refreshGUI() {
		propertyManipulationPanel.removeAll();
		propertyGroupCombo.removeAllItems();
		if (element != null) {
			final Vector<StringObjectPair> structure = AuthoringToolResources.getPropertyStructure(element, false);
			if (structure != null) {
				for (final Iterator<StringObjectPair> iter = structure.iterator(); iter.hasNext();) {
					final edu.cmu.cs.stage3.util.StringObjectPair sop = iter
							.next();
					final String groupName = sop.getString();
					@SuppressWarnings("unchecked")
					final java.util.Vector<String> propertyNames = (java.util.Vector<String>) sop.getObject();

					final javax.swing.JPanel subPanel = new javax.swing.JPanel();
					subPanel.setBackground(java.awt.Color.white);
					subPanel.setLayout(new java.awt.GridBagLayout());

					if (propertyNames != null) {
						int i = 0;
						for (final Iterator<String> jter = propertyNames.iterator(); jter.hasNext();) {
							final String name = jter.next().toString();
							final edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(name);
							if (property != null) {
								final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
									@Override
									public Object createItem(final Object o) {
										return new Runnable() {
											@Override
											public void run() {
												if (property
														.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Transformable
														&& property == ((edu.cmu.cs.stage3.alice.core.Transformable) property
																.getOwner()).vehicle) {
													((edu.cmu.cs.stage3.alice.core.Transformable) property.getOwner())
															.setVehiclePreservingAbsoluteTransformation(
																	(edu.cmu.cs.stage3.alice.core.ReferenceFrame) o);
												} else {
													final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation response = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
													response.element.set(property.getOwner());
													response.propertyName.set(property.getName());
													response.value.set(o);
													final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
													undoResponse.element.set(property.getOwner());
													undoResponse.propertyName.set(property.getName());
													undoResponse.value.set(property.getValue());
													AuthoringTool.getHack().performOneShot(response, undoResponse,
															new edu.cmu.cs.stage3.alice.core.Property[] { property });
												}
											}
										};
									}
								};
								final javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
										.getPropertyGUI(property, true, false, factory);
								if (gui != null) {
									final java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints(0,
											i, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST,
											java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0);
									subPanel.add(gui, constraints);
									i++;
								} else {
									AuthoringTool.showErrorDialog("Unable to create gui for property: " + property,
											null);
								}
							} else {
								AuthoringTool.showErrorDialog("no property on " + element + " named " + name, null);
							}
						}
						final java.awt.GridBagConstraints glueConstraints = new java.awt.GridBagConstraints(0, i, 1, 1,
								1.0, 1.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH,
								new java.awt.Insets(0, 0, 0, 0), 0, 0);
						subPanel.add(javax.swing.Box.createGlue(), glueConstraints);
					}

					propertyManipulationPanel.add(subPanel, element.name.get() + "'s " + groupName);
					propertyGroupCombo.addItem(element.name.get() + "'s " + groupName);
				}
			}

			if (element instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				variableGroupEditor
						.setVariableObjectArrayProperty(((edu.cmu.cs.stage3.alice.core.Sandbox) element).variables);
				propertyManipulationPanel.add(variableGroupEditor, element.name.get() + "'s variables");
				propertyGroupCombo.addItem(element.name.get() + "'s variables");
			}
		}
		revalidate();
		repaint();
	}

	// //////////////////
	// Autogenerated
	// //////////////////

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel propertySubPanel = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	JComboBox<String> propertyGroupCombo = new JComboBox<>();
	Border border1;
	JScrollPane propertyScrollPane = new JScrollPane();
	Border border2;
	Border border3;
	Border border4;
	Border border5;
	JPanel propertyManipulationPanel = new JPanel();
	Border border6;
	Border border7;
	CardLayout cardLayout1 = new CardLayout();

	private void jbInit() {
		border1 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		border2 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
		border3 = BorderFactory.createCompoundBorder(border2, border1);
		border4 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		border5 = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		border6 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
		border7 = BorderFactory.createCompoundBorder(border6, border5);
		setLayout(borderLayout1);
		propertySubPanel.setLayout(borderLayout2);
		propertySubPanel.setBorder(border1);
		setBackground(new Color(204, 204, 204));
		setMinimumSize(new Dimension(0, 0));
		propertyManipulationPanel.setLayout(cardLayout1);
		borderLayout2.setHgap(8);
		borderLayout2.setVgap(8);
		propertyManipulationPanel.setBackground(Color.white);
		this.add(propertySubPanel, BorderLayout.CENTER);
		propertySubPanel.add(propertyGroupCombo, BorderLayout.NORTH);
		propertySubPanel.add(propertyScrollPane, BorderLayout.CENTER);
		propertyScrollPane.getViewport().add(propertyManipulationPanel, null);
	}
}