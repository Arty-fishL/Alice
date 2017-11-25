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
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;

public class FontProperty extends ObjectProperty {
	public FontProperty(final Element owner, final String name, final java.awt.Font defaultValue) {
		super(owner, name, defaultValue, java.awt.Font.class);
	}

	public java.awt.Font getFontValue() {
		return (java.awt.Font) getValue();
	}

	@Override
	protected void decodeObject(final org.w3c.dom.Element node, final edu.cmu.cs.stage3.io.DirectoryTreeLoader loader,
			final java.util.Vector<PropertyReference> referencesToBeResolved, final double version) throws java.io.IOException {
		// Unused ?? final org.w3c.dom.Node familyNode = node.getElementsByTagName("family").item(0);
		final org.w3c.dom.Node nameNode = node.getElementsByTagName("name").item(0);
		final org.w3c.dom.Node styleNode = node.getElementsByTagName("style").item(0);
		final org.w3c.dom.Node sizeNode = node.getElementsByTagName("size").item(0);
		// Unused ?? final String family = getNodeText(familyNode);
		final String name = getNodeText(nameNode);
		final int style = Integer.parseInt(getNodeText(styleNode));
		final int size = Integer.parseInt(getNodeText(sizeNode));
		set(new java.awt.Font(name, style, size));
	}

	@Override
	protected void encodeObject(final org.w3c.dom.Document document, final org.w3c.dom.Element node,
			final edu.cmu.cs.stage3.io.DirectoryTreeStorer storer,
			final edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator) throws java.io.IOException {
		final java.awt.Font f = getFontValue();

		final org.w3c.dom.Element familyNode = document.createElement("family");
		familyNode.appendChild(createNodeForString(document, f.getFamily()));
		node.appendChild(familyNode);

		final org.w3c.dom.Element nameNode = document.createElement("name");
		nameNode.appendChild(createNodeForString(document, f.getName()));
		node.appendChild(nameNode);

		final org.w3c.dom.Element styleNode = document.createElement("style");
		styleNode.appendChild(createNodeForString(document, Integer.toString(f.getStyle())));
		node.appendChild(styleNode);

		final org.w3c.dom.Element sizeNode = document.createElement("size");
		sizeNode.appendChild(createNodeForString(document, Integer.toString(f.getSize())));
		node.appendChild(sizeNode);
	}
}
