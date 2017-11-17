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

package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.cmu.cs.stage3.alice.core.Element;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2001
 * </p>
 * <p>
 * Company: Stage3
 * </p>
 *
 * @author Ben Buchwald
 * @version 1.0
 */

public class BoneSelectDialog extends javax.swing.JDialog {
	/**
	 *
	 */
	private static final long serialVersionUID = 9107195682681851859L;
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JList partsList = new JList();
	JButton selectButton = new JButton();
	JButton skipButton = new JButton();
	JLabel promptLabel = new JLabel();
	JButton limpButton = new JButton();

	public Element selectedPart = null;
	public boolean descend = false;
	JScrollPane jScrollPane1 = new JScrollPane();

	public BoneSelectDialog(final String matching, final Element[] possibilities) {
		try {
			setContentPane(new JPanel());
			jbInit();
			guiInit(matching, possibilities);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void guiInit(final String matching, final Element[] possibilities) {
		final DefaultListModel listOfStuff = new DefaultListModel();
		partsList.setModel(listOfStuff);
		for (final Element possibilitie : possibilities) {
			listOfStuff.addElement(possibilitie);
		}
		promptLabel.setText("Which Part is the bone " + matching + "?");
	}

	private void jbInit() throws Exception {
		getContentPane().setLayout(gridBagLayout1);
		partsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectButton.setText("Select");
		selectButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				selectButton_actionPerformed(e);
			}
		});
		skipButton.setText("Skip Bone");
		skipButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				skipButton_actionPerformed(e);
			}
		});
		promptLabel.setText("jLabel1");
		setModal(true);
		setTitle("Select Matching Bone");
		limpButton.setText("Skip Limb");
		limpButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				limpButton_actionPerformed(e);
			}
		});
		getContentPane().add(selectButton, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		getContentPane().add(skipButton, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		getContentPane().add(promptLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
		getContentPane().add(limpButton, new GridBagConstraints(2, 2, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		getContentPane().add(jScrollPane1, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 60));
		jScrollPane1.getViewport().add(partsList, null);
	}

	void skipButton_actionPerformed(final ActionEvent e) {
		selectedPart = null;
		descend = true;
		setVisible(false);
	}

	void selectButton_actionPerformed(final ActionEvent e) {
		selectedPart = (Element) partsList.getSelectedValue();
		descend = false;
		setVisible(false);
	}

	void limpButton_actionPerformed(final ActionEvent e) {
		selectedPart = null;
		descend = false;
		setVisible(false);
	}

	public Element getSelectedPart() {
		return selectedPart;
	}

	public boolean doDescend() {
		return descend;
	}
}