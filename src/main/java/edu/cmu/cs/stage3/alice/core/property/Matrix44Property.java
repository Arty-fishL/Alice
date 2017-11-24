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

public class Matrix44Property extends ObjectProperty {
	public Matrix44Property(final Element owner, final String name, final javax.vecmath.Matrix4d defaultValue) {
		super(owner, name, defaultValue, javax.vecmath.Matrix4d.class);
	}

	public javax.vecmath.Matrix4d getMatrix4dValue() {
		return (javax.vecmath.Matrix4d) getValue();
	}

	public edu.cmu.cs.stage3.math.Matrix44 getMatrix44Value() {
		final javax.vecmath.Matrix4d m = getMatrix4dValue();
		if (m == null || m instanceof edu.cmu.cs.stage3.math.Matrix44) {
			return (edu.cmu.cs.stage3.math.Matrix44) m;
		} else {
			return new edu.cmu.cs.stage3.math.Matrix44(m);
		}
	}

	@Override
	protected void decodeObject(final org.w3c.dom.Element node, final edu.cmu.cs.stage3.io.DirectoryTreeLoader loader,
			final java.util.Vector<PropertyReference> referencesToBeResolved, final double version) throws java.io.IOException {
		final javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
		final org.w3c.dom.NodeList rowNodeList = node.getElementsByTagName("row");
		for (int i = 0; i < 4; i++) {
			final org.w3c.dom.Element rowNode = (org.w3c.dom.Element) rowNodeList.item(i);
			final org.w3c.dom.NodeList itemNodeList = rowNode.getElementsByTagName("item");
			for (int j = 0; j < 4; j++) {
				final org.w3c.dom.Node itemNode = itemNodeList.item(j);
				m.setElement(i, j, Double.parseDouble(getNodeText(itemNode)));
			}
		}
		set(m);
	}

	@Override
	protected void encodeObject(final org.w3c.dom.Document document, final org.w3c.dom.Element node,
			final edu.cmu.cs.stage3.io.DirectoryTreeStorer storer,
			final edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator) throws java.io.IOException {
		final javax.vecmath.Matrix4d m = getMatrix44Value();
		for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
			final org.w3c.dom.Element rowNode = document.createElement("row");
			// rowNode.setAttribute( "index", Integer.toString( rowIndex ) );
			for (int colIndex = 0; colIndex < 4; colIndex++) {
				final org.w3c.dom.Element itemNode = document.createElement("item");
				// itemNode.setAttribute( "index", Integer.toString( colIndex )
				// );
				itemNode.appendChild(createNodeForString(document, Double.toString(m.getElement(rowIndex, colIndex))));
				rowNode.appendChild(itemNode);
			}
			node.appendChild(rowNode);
		}
	}
}
