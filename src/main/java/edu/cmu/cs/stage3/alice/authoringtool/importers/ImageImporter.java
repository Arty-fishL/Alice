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

package edu.cmu.cs.stage3.alice.authoringtool.importers;

/**
 * @author Jason Pratt
 */
public class ImageImporter extends edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter {

	@Override
	public java.util.Map<String, String> getExtensionMap() {
		final java.util.HashMap<String, String> knownCodecPrettyNames = new java.util.HashMap<String, String>();
		knownCodecPrettyNames.put("BMP", "Windows Bitmap");
		knownCodecPrettyNames.put("GIF", "Graphic Interchange Format");
		knownCodecPrettyNames.put("JPEG", "Joint Photographic Experts Group format");
		knownCodecPrettyNames.put("PNG", "Portable Network Graphics format");
		knownCodecPrettyNames.put("TIFF", "Tagged Image File Format");

		final java.util.HashMap<String, String> map = new java.util.HashMap<String, String>();

		final String[] codecNames = edu.cmu.cs.stage3.image.ImageIO.getCodecNames();
		for (final String codecName : codecNames) {
			String prettyName = knownCodecPrettyNames.get(codecName.toUpperCase());
			if (prettyName == null) {
				prettyName = codecName;
			}
			final String[] extensions = edu.cmu.cs.stage3.image.ImageIO.getExtensionsForCodec(codecName);
			for (final String extension : extensions) {
				map.put(extension.toUpperCase(), prettyName);
			}
		}

		return map;
	}

	@Override
	protected edu.cmu.cs.stage3.alice.core.Element load(final java.io.InputStream istream, final String ext)
			throws java.io.IOException {
		final String codecName = edu.cmu.cs.stage3.image.ImageIO.mapExtensionToCodecName(ext);
		if (codecName == null) {
			throw new IllegalArgumentException("Unsupported Extension: " + ext);
		}

		java.io.BufferedInputStream bis;
		if (istream instanceof java.io.BufferedInputStream) {
			bis = (java.io.BufferedInputStream) istream;
		} else {
			bis = new java.io.BufferedInputStream(istream);
		}
		final java.awt.Image image = edu.cmu.cs.stage3.image.ImageIO.load(codecName, bis);

		final edu.cmu.cs.stage3.alice.core.TextureMap texture = new edu.cmu.cs.stage3.alice.core.TextureMap();

		if (image instanceof java.awt.image.BufferedImage) {
			final java.awt.image.BufferedImage bi = (java.awt.image.BufferedImage) image;
			if (bi.getColorModel().hasAlpha()) {
				texture.format.set(new Integer(edu.cmu.cs.stage3.alice.scenegraph.TextureMap.RGBA));
			}
		}

		texture.name.set(plainName);
		texture.image.set(image);

		return texture;
	}
}