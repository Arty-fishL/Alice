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

import edu.cmu.cs.stage3.alice.core.Element;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */
public class SimulationExceptionPanel extends javax.swing.JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 7108576967755882659L;
	private final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool m_authoringTool;
	private edu.cmu.cs.stage3.alice.core.SimulationException m_simulationException;
	private final edu.cmu.cs.stage3.alice.authoringtool.util.HighlightingGlassPane m_glassPane;

	private final javax.swing.JLabel m_descriptionLabel = new javax.swing.JLabel();
	// private javax.swing.JPanel m_stackPanel = new javax.swing.JPanel();

	public SimulationExceptionPanel(final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		m_authoringTool = authoringTool;
		m_glassPane = new edu.cmu.cs.stage3.alice.authoringtool.util.HighlightingGlassPane(authoringTool);

		setLayout(new java.awt.GridBagLayout());

		final java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;

		add(new javax.swing.JLabel("Alice has detected a problem with your world:"), gbc);
		gbc.insets.left = 8;
		add(m_descriptionLabel, gbc);
		// add( m_stackPanel, gbc );
	}

	public void setSimulationException(final edu.cmu.cs.stage3.alice.core.SimulationException simulationException) {
		m_simulationException = simulationException;

		m_descriptionLabel.setText(simulationException.getMessage());

		// java.util.Stack stack = m_simulationException.getStack();
		// java.util.Enumeration enum = stack.elements();
		// while( enum.hasMoreElements() ) {
		// System.err.println( enum.nextElement() );
		// }

		final edu.cmu.cs.stage3.alice.core.Element element = simulationException.getElement();
		final edu.cmu.cs.stage3.alice.core.World world = m_authoringTool.getWorld();
		edu.cmu.cs.stage3.alice.core.Element ancestor = null;
		final edu.cmu.cs.stage3.alice.core.Element[] userDefinedResponses = world
				.getDescendants(edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class);
		for (final Element userDefinedResponse : userDefinedResponses) {
			if (userDefinedResponse.isAncestorOf(element)) {
				ancestor = userDefinedResponse;
				break;
			}
		}
		if (ancestor == null) {
			final edu.cmu.cs.stage3.alice.core.Element[] userDefinedQuestions = world
					.getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class);
			for (final Element userDefinedQuestion : userDefinedQuestions) {
				if (userDefinedQuestion.isAncestorOf(element)) {
					ancestor = userDefinedQuestion;
					break;
				}
			}
		}

		String highlightID = null;
		if (m_simulationException instanceof edu.cmu.cs.stage3.alice.core.SimulationPropertyException) {
			final edu.cmu.cs.stage3.alice.core.SimulationPropertyException spe = (edu.cmu.cs.stage3.alice.core.SimulationPropertyException) m_simulationException;
			highlightID = "editors:element<" + ancestor.getKey(world) + ">:elementTile<" + element.getKey(world)
					+ ">:property<" + spe.getProperty().getName() + ">";
		}
		if (highlightID == null) {
			if (element != null && ancestor != null) {
				highlightID = "editors:element<" + ancestor.getKey(world) + ">:elementTile<" + element.getKey(world)
						+ ">";
			}
		}
		m_glassPane.setHighlightID(highlightID);
	}

	public void setErrorHighlightingEnabled(final boolean enabled) {
		m_glassPane.setHighlightingEnabled(enabled);
	}

	public java.awt.Rectangle getErrorRect() {
		return m_glassPane.getHighlightRect();
	}
}