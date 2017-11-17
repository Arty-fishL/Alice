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

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

public class ImageProperty extends ObjectProperty {
	public ImageProperty(final Element owner, final String name, final java.awt.Image defaultValue) {
		super(owner, name, defaultValue, java.awt.Image.class);
	}

	public java.awt.Image getImageValue() {
		return (java.awt.Image) getValue();
	}

	private String getCodecName(final String path) {
		final String extension = path.substring(path.lastIndexOf('.') + 1);
		return edu.cmu.cs.stage3.image.ImageIO.mapExtensionToCodecName(extension);
	}

	@Override
	protected void decodeObject(final org.w3c.dom.Element node, final edu.cmu.cs.stage3.io.DirectoryTreeLoader loader,
			final java.util.Vector referencesToBeResolved, final double version) throws java.io.IOException {
		m_associatedFileKey = null;
		final String filename = getFilename(getNodeText(node));
		final java.io.InputStream is = loader.readFile(filename);
		set(edu.cmu.cs.stage3.image.ImageIO.load(getCodecName(filename), is));
		loader.closeCurrentFile();
		try {
			m_associatedFileKey = loader.getKeepKey(filename);
		} catch (final edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse) {
			m_associatedFileKey = null;
		}
	}

	@Override
	protected void encodeObject(final org.w3c.dom.Document document, final org.w3c.dom.Element node,
			final edu.cmu.cs.stage3.io.DirectoryTreeStorer storer,
			final edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator) throws java.io.IOException {
		final java.awt.Image imageValue = getImageValue(); // todo: handle
															// variable
		final String codecName = "png";
		final String extension = "png";
		final String filename = getName() + "." + extension;
		Object associatedFileKey;
		try {
			associatedFileKey = storer.getKeepKey(filename);
		} catch (final edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse) {
			associatedFileKey = null;
		}
		if (m_associatedFileKey == null || !m_associatedFileKey.equals(associatedFileKey)) {
			m_associatedFileKey = null;
			final java.io.OutputStream os = storer.createFile(filename, false);
			final java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(os);
			final java.io.DataOutputStream dos = new java.io.DataOutputStream(bos);
			try {
				edu.cmu.cs.stage3.image.ImageIO.store(codecName, dos, imageValue);
			} catch (final InterruptedException ie) {
				// todo
			}
			dos.flush();
			storer.closeCurrentFile();
			m_associatedFileKey = associatedFileKey;
		} else {
			if (storer.isKeepFileSupported()) {
				try {
					storer.keepFile(filename);
				} catch (final edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse) {
					throw new Error(storer + " returns true for isKeepFileSupported(), but then throws " + kfnse);
				} catch (final edu.cmu.cs.stage3.io.KeepFileDoesNotExistException kfdne) {
					throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper(kfdne, filename);
				}
			}
		}
		node.appendChild(createNodeForString(document, "java.io.File[" + filename + "]"));
	}

	@Override
	public void keepAnyAssociatedFiles(final edu.cmu.cs.stage3.io.DirectoryTreeStorer storer)
			throws edu.cmu.cs.stage3.io.KeepFileNotSupportedException,
			edu.cmu.cs.stage3.io.KeepFileDoesNotExistException {
		super.keepAnyAssociatedFiles(storer);
		final java.awt.Image imageValue = getImageValue(); // todo: handle
															// variable
		if (imageValue instanceof java.awt.image.RenderedImage) {
			final String extension = "png";
			final String filename = getName() + "." + extension;
			storer.keepFile(filename);
		}
	}
}
