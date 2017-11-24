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
public class ResponsePropertyViewController extends PropertyViewController {
	/**
	 *
	 */
	private static final long serialVersionUID = -2184022876864594891L;
	protected javax.swing.JLabel responseLabel = new javax.swing.JLabel();
	protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel responsePanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
	// protected edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse
	// scriptResponse;
	protected edu.cmu.cs.stage3.alice.core.Element root = null;

	public ResponsePropertyViewController() {
		responseLabel.setOpaque(false);
		responsePanel.setOpaque(false);
		responsePanel.setLayout(new java.awt.BorderLayout());
		responsePanel.setBorder(null);
	}

	public void set(final edu.cmu.cs.stage3.alice.core.Property property, final boolean allowExpressions,
			final boolean omitPropertyName, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		super.set(property, true, allowExpressions, false, omitPropertyName, factory);
		setPopupEnabled(true);
		refreshGUI();
	}

	@Override
	protected java.awt.event.MouseListener getMouseListener() {
		return new java.awt.event.MouseAdapter() {

			@Override
			public void mousePressed(final java.awt.event.MouseEvent ev) {
				ResponsePropertyViewController.this.popupButton.doClick();
			}
		};
	}

	public void setRoot(final edu.cmu.cs.stage3.alice.core.Element root) {
		this.root = root;
	}

	@Override
	protected String getHTMLColorString(final java.awt.Color color) {
		final int r = color.getRed();
		final int g = color.getGreen();
		final int b = color.getBlue();
		return new String("#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b));
	}

	@Override
	public void getHTML(final StringBuffer toWriteTo) {
		boolean isEnabled = false;
		if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
			isEnabled = ((edu.cmu.cs.stage3.alice.core.Behavior) property.getOwner()).isEnabled.booleanValue();
		}
		String strikeStart = "";
		String strikeEnd = "";
		if (!isEnabled) {
			strikeStart = "<strike><font color=\""
					+ getHTMLColorString(
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTMLText"))
					+ "\">";
			strikeEnd = "</font></strike>";
		}

		if (responsePanel.getComponentCount() > 0) {
			if (responsePanel.getComponent(
					0) instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel
					&& property.get() instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
				final edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel compPanel = (edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel) responsePanel
						.getComponent(0);
				final int colSpan = edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor
						.getDepthCount(((edu.cmu.cs.stage3.alice.core.response.CompositeResponse) property
								.get()).componentResponses);
				compPanel.getHTML(toWriteTo, colSpan + 1, true, !isEnabled);
			} else {
				java.awt.Color bgColor = responsePanel.getComponent(0).getBackground();
				if (!isEnabled) {
					bgColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTML");
				}
				toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor)
						+ " style=\"border-left: 1px solid #c0c0c0; border-top: 1px solid #c0c0c0; border-right: 1px solid #c0c0c0; border-bottom: 1px solid #c0c0c0;\">"
						+ strikeStart);
				toWriteTo.append(edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
						.getHTMLStringForComponent(responsePanel.getComponent(0)));
				toWriteTo.append(strikeEnd + "</td>\n</tr>\n");
			}
		} else {
			toWriteTo.append(strikeStart);
			super.getHTML(toWriteTo);
			toWriteTo.append(strikeEnd);
		}

	}

	@Override
	protected void updatePopupStructure() {
		popupStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePropertyStructure(property,
				factory, includeDefaults, allowExpressions, includeOther, root);
	}

	@Override
	protected java.awt.Component getNativeComponent() {
		return responsePanel;
	}

	@Override
	protected Class<edu.cmu.cs.stage3.alice.core.Response> getNativeClass() {
		return edu.cmu.cs.stage3.alice.core.Response.class;
	}

	@Override
	protected void updateNativeComponent() {
		responsePanel.removeAll();
		final edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response) property.get();
		final javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(response);
		responsePanel.add(gui, java.awt.BorderLayout.CENTER);
	}
}
