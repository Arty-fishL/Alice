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
public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter implements Comparable {
	private final String extension;
	private final String description;

	public ExtensionFileFilter(final String extension, final String description) {
		this.extension = extension.toUpperCase();
		this.description = description;
	}

	@Override
	public boolean accept(final java.io.File f) {
		return f.isDirectory() || getExtension(f).equalsIgnoreCase(extension);
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getExtension() {
		return extension;
	}

	private String getExtension(final java.io.File f) {
		String ext = "";
		final String fullName = f.getName();
		final int i = fullName.lastIndexOf('.');

		if (i > 0 && i < fullName.length() - 1) {
			ext = fullName.substring(i + 1).toUpperCase();
		}
		return ext;
	}

	@Override
	public int compareTo(final Object o) {
		if (o instanceof ExtensionFileFilter) {
			return description.compareTo(((ExtensionFileFilter) o).getDescription());
		}
		return description.compareTo(o.toString());
	}
}
