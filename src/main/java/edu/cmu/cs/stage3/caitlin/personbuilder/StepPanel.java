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

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.w3c.dom.Node;

public class StepPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = -4752979295821815207L;
	ImageIcon backImage = null;
	ImageIcon nextImage = null;
	Vector<JPanel> choosers = new Vector<JPanel>();

	public StepPanel(final Node stepNode, final ImageIcon nextImage, final ImageIcon backImage,
			final ModelWrapper modelWrapper) {
		setBackground(new java.awt.Color(155, 159, 206));
		this.backImage = backImage;
		this.nextImage = nextImage;

		choosers = getChoosers(stepNode, modelWrapper);
		addChoosers(choosers);
	}

	public void resetDefaults() {
		for (int i = 0; i < choosers.size(); i++) {
			if (choosers.elementAt(i) instanceof ItemChooser) {
				final ItemChooser chooser = (ItemChooser) choosers.elementAt(i);
				chooser.resetDefaults();
			}
		}
	}

	private Vector<JPanel> getChoosers(final Node stepNode, final ModelWrapper modelWrapper) {

		final Vector<JPanel> choosers = new Vector<JPanel>();

		final Vector<Node> colorNodes = XMLDirectoryUtilities.getSetColorNodes(stepNode);
		for (int i = 0; i < colorNodes.size(); i++) {
			final ColorSelector colorSelector = new ColorSelector(modelWrapper);
			choosers.addElement(colorSelector);
		}

		final Vector<Node> chooserNodes = XMLDirectoryUtilities.getDirectories(stepNode);
		// Unused ?? final double incr = 3.0 / chooserNodes.size();
		for (int i = 0; i < chooserNodes.size(); i++) {
			final Node chooserNode = chooserNodes.elementAt(i);
			if (chooserNode.getNodeName().equals("directory")) {
				final ItemChooser chooser = new ItemChooser(chooserNode, nextImage, backImage, modelWrapper);
				choosers.addElement(chooser);
			}
		}
		return choosers;
	}

	private void addChoosers(final Vector<JPanel> choosers) {
		setLayout(new java.awt.GridLayout(3, 1));
		for (int i = 0; i < choosers.size(); i++) {
			this.add(choosers.elementAt(i));
		}
	}
}