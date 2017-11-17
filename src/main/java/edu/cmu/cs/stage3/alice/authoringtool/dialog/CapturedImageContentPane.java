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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */
public class CapturedImageContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	/**
	 *
	 */
	private static final long serialVersionUID = -1604915747357816340L;
	private final edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel m_capturedImagePanel = new edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel();
	private final javax.swing.JCheckBox m_dontShowCheckBox = new javax.swing.JCheckBox("don\'t show this anymore");
	private final javax.swing.JLabel m_storeLocationLabel = new javax.swing.JLabel("storeLocation");
	private final javax.swing.JButton m_okButton = new javax.swing.JButton("OK");

	public CapturedImageContentPane() {
		setLayout(new java.awt.GridBagLayout());

		m_okButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
				CapturedImageContentPane.this.syncConfigWithCheckBox();
			}
		});

		final java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.fill = java.awt.GridBagConstraints.NONE;
		gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gbc.gridheight = 3;
		gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		gbc.insets.left = 8;
		gbc.insets.top = 8;
		add(m_capturedImagePanel, gbc);
		gbc.insets.right = 8;

		gbc.gridheight = 1;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		add(new javax.swing.JLabel("Image captured to:"), gbc);
		add(m_storeLocationLabel, gbc);
		gbc.weighty = 1.0;
		add(new javax.swing.JLabel(), gbc);
		gbc.weighty = 0.0;

		gbc.anchor = java.awt.GridBagConstraints.CENTER;
		add(m_okButton, gbc);
		gbc.anchor = java.awt.GridBagConstraints.SOUTHWEST;
		gbc.insets.bottom = 8;
		add(m_dontShowCheckBox, gbc);
	}

	@Override
	public String getTitle() {
		return "Image captured and stored";
	}

	@Override
	public void preDialogShow(final javax.swing.JDialog dialog) {
		super.preDialogShow(dialog);
		syncCheckBoxWithConfig();
	}

	public void setStoreLocation(final String storeLocation) {
		m_storeLocationLabel.setText(storeLocation);
	}

	public void setImage(final java.awt.Image image) {
		m_capturedImagePanel.setImage(image);
	}

	@Override
	public void addOKActionListener(final java.awt.event.ActionListener l) {
		m_okButton.addActionListener(l);
	}

	@Override
	public void removeOKActionListener(final java.awt.event.ActionListener l) {
		m_okButton.removeActionListener(l);
	}

	private void syncCheckBoxWithConfig() {
		final edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration
				.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());
		m_dontShowCheckBox
				.setSelected(!authoringToolConfig.getValue("screenCapture.informUser").equalsIgnoreCase("true"));
	}

	private void syncConfigWithCheckBox() {
		final edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration
				.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());
		String s;
		if (m_dontShowCheckBox.isSelected()) {
			s = "false";
		} else {
			s = "true";
		}
		authoringToolConfig.setValue("screenCapture.informUser", s);

	}
}