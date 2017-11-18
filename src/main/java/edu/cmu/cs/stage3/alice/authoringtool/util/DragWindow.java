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

import java.awt.Window;

/**
 * @author Jason Pratt
 */
public class DragWindow extends Window {
	/**
	 *
	 */
	private static final long serialVersionUID = 6287797836316860033L;
	protected java.awt.Image image;
	protected int width;
	protected int height;

	public DragWindow(final java.awt.Frame owner) {
		super(owner);
		setLayout(new java.awt.BorderLayout(0, 0));
	}

	public void setImage(final java.awt.Image image) {
		this.image = image;
		if (image != null) {
			try {
				width = edu.cmu.cs.stage3.image.ImageUtilities.getWidth(image);
				height = edu.cmu.cs.stage3.image.ImageUtilities.getHeight(image);
				setSize(width, height);
			} catch (final InterruptedException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("Interrupted while waiting for drag image to load.", e);
			}
		}
		repaint();
	}

	@Override
	public void paint(final java.awt.Graphics g) {
		g.drawImage(image, 0, 0, this);
	}
}