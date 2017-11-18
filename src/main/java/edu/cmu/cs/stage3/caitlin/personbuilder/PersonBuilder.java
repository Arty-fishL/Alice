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

import edu.cmu.cs.stage3.util.StringObjectPair;

public class PersonBuilder extends javax.swing.JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1014460200664339903L;

	public static java.util.Vector<StringObjectPair> getAllBuilders() {
		final java.util.Vector<StringObjectPair> builders = new java.util.Vector<>();
		String name = "";
		javax.swing.ImageIcon icon = null;
		final org.w3c.dom.Document doc = (org.w3c.dom.Document) XMLDirectoryUtilities.loadFile("images/builders.xml");
		final org.w3c.dom.Node root = doc.getDocumentElement();

		final org.w3c.dom.NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			final org.w3c.dom.Node node = nl.item(i);
			if (node.getNodeName().equals("builder")) {
				final org.w3c.dom.NamedNodeMap nodeMap = node.getAttributes();
				for (int j = 0; j < nodeMap.getLength(); j++) {
					final org.w3c.dom.Node attr = nodeMap.item(j);
					if (attr.getNodeName().equals("name")) {
						name = attr.getNodeValue();
					} else if (attr.getNodeName().equals("icon")) {
						final String iconName = attr.getNodeValue();
						icon = new javax.swing.ImageIcon(PersonBuilder.class.getResource("images/" + iconName),
								iconName);
					}
				}
				final StringObjectPair sop = new StringObjectPair(name, icon);
				builders.addElement(sop);
			}
		}

		return builders;
	}

	private AllStepsPanel allStepsPanel = null;
	private NavigationPanel navPanel = null;
	private RenderPanel renderPanel = null;
	private NamePanel namePanel = null;
	private ModelWrapper modelWrapper = null;
	// Unused ?? private String builderName = "";

	public PersonBuilder(final String builderName, final edu.cmu.cs.stage3.progress.ProgressObserver progressObserver)
			throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		// Unused ?? this.builderName = builderName;
		final String builderFile = "images/" + builderName + ".xml";
		int progressOffset = 0;
		// progressObserver.progressBegin(
		// edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL );
		progressObserver.progressBegin(7);
		try {
			final org.w3c.dom.Document doc = (org.w3c.dom.Document) XMLDirectoryUtilities.loadFile(builderFile);
			progressObserver.progressUpdate(progressOffset++, null);
			final org.w3c.dom.Node root = doc.getDocumentElement();
			progressObserver.progressUpdate(progressOffset++, null);
			modelWrapper = new ModelWrapper(root);
			progressObserver.progressUpdate(progressOffset++, null);
			allStepsPanel = new AllStepsPanel(root, modelWrapper, progressObserver, progressOffset);
			navPanel = new NavigationPanel(root, allStepsPanel);
			renderPanel = new RenderPanel(modelWrapper);
			namePanel = new NamePanel();
			renderPanel.initialize();

			setLayout(new java.awt.BorderLayout());
			add(allStepsPanel, java.awt.BorderLayout.EAST);
			add(renderPanel, java.awt.BorderLayout.CENTER);
			add(navPanel, java.awt.BorderLayout.NORTH);
			add(namePanel, java.awt.BorderLayout.SOUTH);
		} finally {
			progressObserver.progressEnd();
		}
		setBackground(new java.awt.Color(155, 159, 206));
		setSize(500, 500);
	}

	public void reset() {
		modelWrapper.resetWorld();
		try {
			allStepsPanel.resetDefaults();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		navPanel.setFirstStep();
	}

	public edu.cmu.cs.stage3.alice.core.Model getModel() {
		final edu.cmu.cs.stage3.alice.core.Model model = modelWrapper.getModel();
		String text = namePanel.getCreatedBy();
		if (text.length() == 0) {
			text = "Anonymous";
		}
		model.data.put("created by", text);

		text = namePanel.getName();
		if (text.length() == 0) {
			text = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog("What would you like to name your character?");
		}
		text = text.trim();
		if (text.startsWith("\"") || text.startsWith("'")) {
			text = text.substring(1);
		}
		if (text.endsWith("\"") || text.endsWith("'")) {
			text = text.substring(0, text.length() - 1);
		}
		model.name.set(text);

		return model;
	}

}