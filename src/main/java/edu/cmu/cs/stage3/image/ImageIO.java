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
		return load(codecName, inputStream, null);
	}

	public static java.awt.Image load(final String codecName, final java.io.InputStream inputStream,
			final edu.cmu.cs.stage3.image.codec.ImageDecodeParam imageDecodeParam) throws java.io.IOException {
		java.io.BufferedInputStream bufferedInputStream;
		if (inputStream instanceof java.io.BufferedInputStream) {
			bufferedInputStream = (java.io.BufferedInputStream) inputStream;
		} else {
			bufferedInputStream = new java.io.BufferedInputStream(inputStream);
		}
		final edu.cmu.cs.stage3.image.codec.ImageDecoder imageDecoder = edu.cmu.cs.stage3.image.codec.ImageCodec
				.createImageDecoder(codecName, bufferedInputStream, imageDecodeParam);
		final java.awt.image.RenderedImage renderedImage = imageDecoder.decodeAsRenderedImage();

		if (renderedImage instanceof java.awt.Image) {
			return (java.awt.Image) renderedImage;
		} else {
			final java.awt.image.Raster raster = renderedImage.getData();
			final java.awt.image.ColorModel colorModel = renderedImage.getColorModel();
			java.util.Hashtable properties = null;
			final String[] propertyNames = renderedImage.getPropertyNames();
			if (propertyNames != null) {
				properties = new java.util.Hashtable();
				for (final String propertyName : propertyNames) {
					properties.put(propertyName, renderedImage.getProperty(propertyName));
				}
			}
			java.awt.image.WritableRaster writableRaster;
			if (raster instanceof java.awt.image.WritableRaster) {
				writableRaster = (java.awt.image.WritableRaster) raster;
			} else {
				writableRaster = raster.createCompatibleWritableRaster();
			}
			final java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(
					renderedImage.getColorModel(), writableRaster, colorModel.isAlphaPremultiplied(), properties);
			return bufferedImage;
		}
	}

	public static void store(final String codecName, final java.io.OutputStream outputStream,
			final java.awt.Image image) throws InterruptedException, java.io.IOException {
		store(codecName, outputStream, image, null);
	}

	public static void store(final String codecName, final java.io.OutputStream outputStream, java.awt.Image image,
			edu.cmu.cs.stage3.image.codec.ImageEncodeParam imageEncodeParam)
			throws InterruptedException, java.io.IOException {
		final int width = ImageUtilities.getWidth(image);
		final int height = ImageUtilities.getHeight(image);

		java.awt.image.RenderedImage renderedImage;

		if (codecName.equals("jpeg")) {
			final java.awt.Image originalImage = image;
			image = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_3BYTE_BGR);
			final java.awt.Graphics g = image.getGraphics();
			g.drawImage(originalImage, 0, 0, new java.awt.image.ImageObserver() {
				@Override
				public boolean imageUpdate(final java.awt.Image image, final int infoflags, final int x, final int y,
						final int width, final int height) {
					return true;
				}
			});
			// todo: does dispose ensure the image is finished drawing?
			g.dispose();
		}
		if (image instanceof java.awt.image.RenderedImage) {
			renderedImage = (java.awt.image.RenderedImage) image;
		} else {
			final int[] pixels = ImageUtilities.getPixels(image, width, height);
			final java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(width, height,
					java.awt.image.BufferedImage.TYPE_INT_ARGB);
			bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
			renderedImage = bufferedImage;
		}
		if (imageEncodeParam == null) {
			if (codecName.equals("png")) {
				imageEncodeParam = edu.cmu.cs.stage3.image.codec.PNGEncodeParam.getDefaultEncodeParam(renderedImage);
			}
		}
		java.io.BufferedOutputStream bufferedOutputStream;
		if (outputStream instanceof java.io.BufferedOutputStream) {
			bufferedOutputStream = (java.io.BufferedOutputStream) outputStream;
		} else {
			bufferedOutputStream = new java.io.BufferedOutputStream(outputStream);
		}

		final edu.cmu.cs.stage3.image.codec.ImageEncoder imageEncoder = edu.cmu.cs.stage3.image.codec.ImageCodec
				.createImageEncoder(codecName, bufferedOutputStream, imageEncodeParam);
		imageEncoder.encode(renderedImage);
		bufferedOutputStream.flush();
	}
}
