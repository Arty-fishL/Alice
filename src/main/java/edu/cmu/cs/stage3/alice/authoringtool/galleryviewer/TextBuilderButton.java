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

package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import javax.swing.ImageIcon;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;

/**
 * @author culyba
 *
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TextBuilderButton extends GenericBuilderButton {
	/**
	 *
	 */
	private static final long serialVersionUID = 8715597075164160494L;
	protected AuthoringTool authoringTool;

	public TextBuilderButton() {
	}

	@Override
	protected String getToolTipString() {
		return "Click to create 3D text";
	}

	public void set(final GalleryViewer.ObjectXmlData dataIn, final ImageIcon icon, final AuthoringTool authoringTool)
			throws IllegalArgumentException {
		super.set(dataIn, icon);
		this.authoringTool = authoringTool;
	}

	@Override
	public void respondToMouse() {
		if (authoringTool != null) {
			authoringTool.getActions().add3DTextAction.actionPerformed(null);
		}
	}

}
