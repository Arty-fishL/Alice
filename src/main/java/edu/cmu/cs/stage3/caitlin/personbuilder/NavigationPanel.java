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

package edu.cmu.cs.stage3.caitlin.personbuilder;

import java.awt.Color;
import java.net.URL;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Node;

//TODO: disable the next and previous buttons for first/last step

public class NavigationPanel extends JPanel implements java.awt.event.ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1009088692302173720L;
	protected Vector<JButton> stepButtons = new Vector<JButton>();
	protected Vector<ImageIcon> stepImages = new Vector<ImageIcon>();
	protected Vector<ImageIcon> selStepImages = new Vector<ImageIcon>();
	protected JButton backButton = null; // new JButton("back");
	protected JButton nextButton = null; // new JButton("next");
	protected ImageIcon spacerIcon = null;
	protected ImageIcon noBackImage = null;
	protected ImageIcon noNextImage = null;
	protected ImageIcon backImage = null;
	protected ImageIcon nextImage = null;
	protected int stepIndex = 0;
	protected AllStepsPanel allStepsPanel;

	public NavigationPanel(final Node root, final AllStepsPanel allStepsPanel) {
		createGuiElements(root);
		this.allStepsPanel = allStepsPanel;
		addGuiElements();
		setSelectedStep(0, 1);
	}

	public void setFirstStep() {
		final int index = allStepsPanel.getSelected();
		allStepsPanel.setSelected(0);
		setSelectedStep(0, index - 1);
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent ae) {
		final Object actionObj = ae.getSource();
		int index = stepButtons.indexOf(actionObj);
		if (index > 0 && index < stepButtons.size() - 1) {
		} else if (index == 0) {
			index = allStepsPanel.getSelected();
			index--;
		} else if (index == stepButtons.size() - 1) {
			index = allStepsPanel.getSelected();
			index++;
		}

		// make this set the appropriate step image.
		final int prevStep = allStepsPanel.getSelected() - 1;
		allStepsPanel.setSelected(index);
		final int curStep = allStepsPanel.getSelected() - 1;

		setSelectedStep(curStep, prevStep);
	}

	protected void setSelectedStep(final int curStep, final int prevStep) {
		if (prevStep >= 0 && curStep != prevStep) {
			final JButton curButton = stepButtons.elementAt(prevStep + 1);
			final ImageIcon curImage = stepImages.elementAt(prevStep);
			curButton.setIcon(curImage);
		}

		if (prevStep < selStepImages.size() && curStep != prevStep) {
			final JButton newButton = stepButtons.elementAt(curStep + 1);
			final ImageIcon newImage = selStepImages.elementAt(curStep);
			newButton.setIcon(newImage);
		}

		if (curStep == 0) {
			backButton.setIcon(noBackImage);
		} else {
			backButton.setIcon(backImage);
		}

		if (curStep == selStepImages.size() - 1) {
			nextButton.setIcon(noNextImage);
		} else {
			nextButton.setIcon(nextImage);
		}
	}

	private void addGuiElements() {
		// this.setLayout(new java.awt.GridLayout(1, stepButtons.size() +
		// stepButtons.size()-1, 0,0));
		setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
		setBackground(new Color(155, 159, 206));
		for (int i = 0; i < stepButtons.size(); i++) {
			this.add(stepButtons.elementAt(i));
			if (i != stepButtons.size() - 1) {
				final JLabel spLabel = new JLabel(spacerIcon);
				this.add(spLabel);
			}
		}
	}

	private void createGuiElements(final Node root) {
		final Vector<URL> imageURLs = XMLDirectoryUtilities.getImageURLs(root);
		for (int i = 0; i < imageURLs.size(); i++) {
			final java.net.URL url = imageURLs.elementAt(i);
			if (url.toString().indexOf("nextBtn.jpg") != -1) {
				nextImage = new ImageIcon(url);
				nextButton = new JButton(nextImage);
				nextButton.setBorderPainted(false);
				nextButton.setBorder(null);
				nextButton.addActionListener(this);
			} else if (url.toString().indexOf("backBtn.jpg") != -1) {
				backImage = new ImageIcon(url);
				backButton = new JButton(backImage);
				backButton.setBorderPainted(false);
				backButton.setBorder(null);
				backButton.addActionListener(this);
				stepButtons.addElement(backButton);
			} else if (url.toString().indexOf("noBackBtn.jpg") != -1) {
				noBackImage = new ImageIcon(url);
			} else if (url.toString().indexOf("noNextBtn.jpg") != -1) {
				noNextImage = new ImageIcon(url);
			} else if (url.toString().indexOf("spacer.jpg") != -1) {
				spacerIcon = new ImageIcon(url);
			}
		}

		final Vector<Node> stepNodes = XMLDirectoryUtilities.getDirectories(root);
		for (int i = 0; i < stepNodes.size(); i++) {
			final Node currentStepNode = stepNodes.elementAt(i);
			final Vector<URL> currentStepImages = XMLDirectoryUtilities.getImageURLs(currentStepNode);
			if (currentStepImages != null && currentStepImages.size() > 1) {
				final ImageIcon icon = new ImageIcon(currentStepImages.elementAt(0));
				final ImageIcon selIcon = new ImageIcon(currentStepImages.elementAt(1));
				stepImages.addElement(icon);
				selStepImages.addElement(selIcon);
				final JButton stepBtn = new JButton(icon);
				stepBtn.setBorderPainted(false);
				stepBtn.addActionListener(this);
				stepBtn.setBorder(null);
				stepButtons.addElement(stepBtn);
			}
		}

		stepButtons.addElement(nextButton);
	}
}