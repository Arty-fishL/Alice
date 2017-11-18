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

/**
 * @author Jason Pratt
 */
public class ExtensionGroupFileFilter extends javax.swing.filechooser.FileFilter implements Comparable<Object> {
	private final java.util.ArrayList<ExtensionFileFilter> extensions;
	private String description = "";
	private String baseDescription = "";

	public ExtensionGroupFileFilter(final String baseDescription) {
		extensions = new java.util.ArrayList<>();
		this.baseDescription = baseDescription;
	}

	public ExtensionGroupFileFilter(final java.util.ArrayList<ExtensionFileFilter> extensions, final String baseDescription) {
		this.extensions = extensions;
		this.baseDescription = baseDescription;
	}

	public void addExtensionFileFilter(final ExtensionFileFilter ext) {
		extensions.add(ext);
	}

	private void recalculateDescription() {
		final StringBuffer d = new StringBuffer(baseDescription);
		d.append(" (");

		final java.util.Iterator<ExtensionFileFilter> iter = extensions.iterator();
		if (iter.hasNext()) {
			final ExtensionFileFilter ext = (ExtensionFileFilter) iter.next();
			d.append(ext.getExtension());
		}
		while (iter.hasNext()) {
			final ExtensionFileFilter ext = (ExtensionFileFilter) iter.next();
			d.append(";" + ext.getExtension());
		}

		d.append(")");

		description = d.toString();
	}

	@Override
	public boolean accept(final java.io.File f) {
		for (final java.util.Iterator<ExtensionFileFilter> iter = extensions.iterator(); iter.hasNext();) {
			final ExtensionFileFilter ext = (ExtensionFileFilter) iter.next();
			if (ext.accept(f)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getDescription() {
		recalculateDescription();
		return description;
	}

	@Override
	public int compareTo(final Object o) {
		// account for change in java 1.5 VM
		if (o instanceof String) {
			return description.compareTo((String) o);
		} else {
			return -1;
		}
	}
}
