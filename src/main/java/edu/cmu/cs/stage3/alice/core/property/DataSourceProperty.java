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

public class DataSourceProperty extends ObjectProperty {

	public DataSourceProperty(final Element owner, final String name,
			final edu.cmu.cs.stage3.media.DataSource defaultValue) {
		super(owner, name, defaultValue, edu.cmu.cs.stage3.media.DataSource.class);
	}

	public edu.cmu.cs.stage3.media.DataSource getDataSourceValue() {
		return (edu.cmu.cs.stage3.media.DataSource) getValue();
	}

	private String getFilename() {
		final edu.cmu.cs.stage3.media.DataSource dataSourceValue = getDataSourceValue();
		return getOwner().name.getStringValue() + '.' + dataSourceValue.getExtension();
	}

	@Override
	protected void decodeObject(final org.w3c.dom.Element node, final edu.cmu.cs.stage3.io.DirectoryTreeLoader loader,
			final java.util.Vector<PropertyReference> referencesToBeResolved, final double version) throws java.io.IOException {
		m_associatedFileKey = null;

		final String filename = getFilename(getNodeText(node));

		final java.io.InputStream is = loader.readFile(filename);
		set(edu.cmu.cs.stage3.media.Manager.createDataSource(is,
				edu.cmu.cs.stage3.io.FileUtilities.getExtension(filename)));
		loader.closeCurrentFile();

		try {
			final String durationHintText = node.getAttribute("durationHint");
			if (durationHintText != null) {
				final double durationHint = Double.parseDouble(durationHintText);
				getDataSourceValue().setDurationHint(durationHint);
			}
		} catch (final Throwable t) {
			// pass
		}

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
		final edu.cmu.cs.stage3.media.DataSource dataSourceValue = getDataSourceValue();
		if (dataSourceValue != null) {
			final double duration = dataSourceValue
					.getDuration(edu.cmu.cs.stage3.media.DataSource.USE_HINT_IF_NECESSARY);
			if (Double.isNaN(duration)) {
				// pass
			} else {
				node.setAttribute("durationHint", Double.toString(duration));
			}

			final String filename = getFilename();
			Object associatedFileAbsolutePath;
			try {
				associatedFileAbsolutePath = storer.getKeepKey(filename);
			} catch (final edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse) {
				associatedFileAbsolutePath = null;
			}
			if (m_associatedFileKey == null || !m_associatedFileKey.equals(associatedFileAbsolutePath)) {
				m_associatedFileKey = null;
				final java.io.OutputStream os = storer.createFile(filename, dataSourceValue.isCompressionWorthwhile());
				final java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(os);
				bos.write(dataSourceValue.getData());
				bos.flush();
				storer.closeCurrentFile();
				m_associatedFileKey = associatedFileAbsolutePath;
			} else {
				try {
					storer.keepFile(filename);
				} catch (final edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse) {
					kfnse.printStackTrace();
				} catch (final edu.cmu.cs.stage3.io.KeepFileDoesNotExistException kfdne) {
					kfdne.printStackTrace();
				}
			}
			node.appendChild(document.createTextNode("java.io.File[" + filename + "]"));
		}
	}

	@Override
	public void keepAnyAssociatedFiles(final edu.cmu.cs.stage3.io.DirectoryTreeStorer storer)
			throws edu.cmu.cs.stage3.io.KeepFileNotSupportedException,
			edu.cmu.cs.stage3.io.KeepFileDoesNotExistException {
		super.keepAnyAssociatedFiles(storer);
		final edu.cmu.cs.stage3.media.DataSource dataSourceValue = getDataSourceValue(); // todo:
		// handle
		// variable
		if (dataSourceValue != null) {
			storer.keepFile(getFilename());
		}
	}
}
