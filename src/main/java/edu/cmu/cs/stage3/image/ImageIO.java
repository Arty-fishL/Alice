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

package edu.cmu.cs.stage3.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageIO {
	private static final String[] s_codecNames = { "png", "jpeg", "tiff", "bmp", "gif" };
	private static final String[] s_pngExtensions = { "png" };
	private static final String[] s_jpegExtensions = { "jpeg", "jpg" };
	private static final String[] s_tiffExtensions = { "tiff", "tif" };
	private static final String[] s_bmpExtensions = { "bmp" };
	private static final String[] s_gifExtensions = { "gif" };

	public static String[] getCodecNames() {
		return s_codecNames;
	}

	public static String[] getExtensionsForCodec(final String codecName) {
		if (codecName.equals("png")) {
			return s_pngExtensions;
		} else if (codecName.equals("jpeg")) {
			return s_jpegExtensions;
		} else if (codecName.equals("tiff")) {
			return s_tiffExtensions;
		} else if (codecName.equals("bmp")) {
			return s_bmpExtensions;
		} else if (codecName.equals("gif")) {
			return s_gifExtensions;
		} else {
			return null;
		}
	}

	public static String mapExtensionToCodecName(final String extension) {
		final String[] codecNames = ImageIO.getCodecNames();
		for (final String codecName : codecNames) {
			final String[] extensions = getExtensionsForCodec(codecName);
			for (final String extension2 : extensions) {
				if (extension2.equalsIgnoreCase(extension)) {
					return codecName;
				}
			}
		}
		return null;
	}

	public static java.awt.Image load(final String codecName, final java.io.InputStream inputStream)
			throws java.io.IOException {
		return javax.imageio.ImageIO.read(inputStream);
	}

	public static void store(final String codecName, final java.io.OutputStream outputStream,
			final java.awt.Image image) throws InterruptedException, java.io.IOException {
		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(image, null, null);
		javax.imageio.ImageIO.write(bi, codecName, outputStream);
	}
}
