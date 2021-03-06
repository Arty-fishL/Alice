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
public class TextureMapViewController extends javax.swing.JPanel implements
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1521231675162715886L;
	protected edu.cmu.cs.stage3.alice.core.TextureMap textureMap;
	protected ElementDnDPanel textureMapDnDPanel;

	public TextureMapViewController() {
		setLayout(new java.awt.GridBagLayout());
		setOpaque(false);
	}

	public void setTextureMap(final edu.cmu.cs.stage3.alice.core.TextureMap textureMap) {
		this.textureMap = textureMap;
		textureMapDnDPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getElementDnDPanel(textureMap);
		add(textureMapDnDPanel,
				new java.awt.GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER,
						java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		// TODO: add mini image
	}

	@Override
	public void goToSleep() {
		if (textureMapDnDPanel != null) {
			textureMapDnDPanel.goToSleep();
		}
	}

	@Override
	public void wakeUp() {
		if (textureMapDnDPanel != null) {
			textureMapDnDPanel.wakeUp();
		}
	}

	@Override
	public void clean() {
		goToSleep();
		if (textureMapDnDPanel != null) {
			remove(textureMapDnDPanel);
			textureMapDnDPanel = null;
		}
	}

	@Override
	public void die() {
		clean();
	}

	@Override
	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

}