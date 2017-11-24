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

package edu.cmu.cs.stage3.image.codec;

/*
 * The contents of this file are subject to the  JAVA ADVANCED IMAGING
 * SAMPLE INPUT-OUTPUT CODECS AND WIDGET HANDLING SOURCE CODE  License
 * Version 1.0 (the "License"); You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.sun.com/software/imaging/JAI/index.html
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is JAVA ADVANCED IMAGING SAMPLE INPUT-OUTPUT CODECS
 * AND WIDGET HANDLING SOURCE CODE.
 * The Initial Developer of the Original Code is: Sun Microsystems, Inc..
 * Portions created by: _______________________________________
 * are Copyright (C): _______________________________________
 * All Rights Reserved.
 * Contributor(s): _______________________________________
 */

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

// Too much unused shit in this class, suppress all the warnings

@SuppressWarnings("unused")
public class FPXImage extends SimpleRenderedImage {

	private static final int SUBIMAGE_COLOR_SPACE_COLORLESS = 0;
	private static final int SUBIMAGE_COLOR_SPACE_MONOCHROME = 0;
	private static final int SUBIMAGE_COLOR_SPACE_PHOTOYCC = 0;
	private static final int SUBIMAGE_COLOR_SPACE_NIFRGB = 0;
	private static final String[] COLORSPACE_NAME = { "Colorless", "Monochrome", "PhotoYCC", "NIF RGB" };

	StructuredStorage storage;

	int numResolutions;
	int highestResWidth;
	int highestResHeight;
	float defaultDisplayHeight;
	float defaultDisplayWidth;
	int displayHeightWidthUnits;

	boolean[] subimageValid;
	int[] subimageWidth;
	int[] subimageHeight;

	int[][] subimageColor;

	// subimageNumericalFormat
	int[] decimationMethod;
	float[] decimationPrefilterWidth;
	// subimage ICC profile

	int highestResolution = -1;

	int maxJPEGTableIndex;
	byte[][] JPEGTable;

	// Values from "Subimage 0000 Header" stream
	int numChannels;
	int tileHeaderTableOffset;
	int tileHeaderEntryLength;

	// int[] tileOffset;
	// int[] tileSize;
	// int[] compressionType;
	// int[] compressionSubtype;

	// The "Subimage 0000 Header" stream
	SeekableStream subimageHeaderStream;

	// The "Subimage 0000 Data" stream
	SeekableStream subimageDataStream;

	int resolution;

	// Derived values
	int tilesAcross;

	int[] bandOffsets = { 0, 1, 2 };

	private static final int[] RGBBits8 = { 8, 8, 8 };
	private static final ComponentColorModel colorModelRGB8 = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB), RGBBits8, false, false, Transparency.OPAQUE,
			DataBuffer.TYPE_BYTE);

	public FPXImage(final SeekableStream stream, FPXDecodeParam param) throws IOException {

		storage = new StructuredStorage(stream);

		readImageContents();
		if (param == null) {
			param = new FPXDecodeParam();
		}
		resolution = param.getResolution();
		readResolution();

		bandOffsets = new int[numChannels];
		for (int i = 0; i < numChannels; i++) {
			bandOffsets[i] = i;
		}

		minX = 0;
		minY = 0;

		sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, tileWidth, tileHeight,
				numChannels, numChannels * tileWidth, bandOffsets);
		colorModel = ImageCodec.createComponentColorModel(sampleModel);
	}

	private void readImageContents() throws IOException {
		storage.changeDirectoryToRoot();
		storage.changeDirectory("Data Object Store 000001");
		final SeekableStream imageContents = storage.getStream("Image Contents");

		final PropertySet icps = new PropertySet(imageContents);
		numResolutions = (int) icps.getUI4(0x01000000);
		highestResWidth = (int) icps.getUI4(0x01000002);
		highestResHeight = (int) icps.getUI4(0x01000003);
		// this.defaultDisplayHeight = icps.getR4(0x01000004);
		// this.defaultDisplayWidth = icps.getR4(0x01000005);

		displayHeightWidthUnits = (int) icps.getUI4(0x01000006, 0L);

		/*
		 * System.out.println("\nImage Contents Property Set:\n");
		 * System.out.println("  numResolutions = " + numResolutions);
		 * System.out.println("  highestResWidth = " + highestResWidth);
		 * System.out.println("  highestResHeight = " + highestResHeight);
		 * System.out.println();
		 */

		subimageValid = new boolean[numResolutions];
		subimageWidth = new int[numResolutions];
		subimageHeight = new int[numResolutions];
		subimageColor = new int[numResolutions][];
		// subimageNumericalFormat
		decimationMethod = new int[numResolutions];
		decimationPrefilterWidth = new float[numResolutions];
		// subimage ICC profile

		for (int i = 0; i < numResolutions; i++) {
			final int index = i << 16;
			if (!icps.hasProperty(0x02000000 | index)) {
				break;
			}

			highestResolution = i;
			subimageValid[i] = true;
			subimageWidth[i] = (int) icps.getUI4(0x02000000 | index);
			subimageHeight[i] = (int) icps.getUI4(0x02000001 | index);
			final byte[] subimageColorBlob = icps.getBlob(0x02000002 | index);
			decimationMethod[i] = icps.getI4(0x02000004 | index);
			// decimationPrefilterWidth[i] = icps.getR4(0x02000005 | index);

			final int numSubImages = FPXUtils.getIntLE(subimageColorBlob, 0);
			final int numChannels = FPXUtils.getIntLE(subimageColorBlob, 4);

			// System.out.println(" subimageWidth[" + i + "] = " +
			// subimageWidth[i]);
			// System.out.println(" subimageHeight[" + i + "] = " +
			// subimageHeight[i]);
			// System.out.println(" subimageColor[" + i + "] = ");
			// System.out.println(" numSubimages = " + numSubImages);

			subimageColor[i] = new int[numChannels];
			for (int c = 0; c < numChannels; c++) {
				final int color = FPXUtils.getIntLE(subimageColorBlob, 8 + 4 * c);
				subimageColor[i][c] = color;
				// System.out.println(" channel " + c + " color space " +
				// (color >> 16) +
				// " (" + COLORSPACE_NAME[color >> 16] +")");

				// System.out.println(" channel " + c + " color type " +
				// (color & 0x7fff));
				// if ((color & 0x8000) != 0) {
				// System.out.println(" channel " + c +
				// " has premultiplied opacity");
				// }
			}

			// System.out.println(" decimationMethod[" + i + "] = " +
			// decimationMethod[i]);
			// System.out.println();
		}

		maxJPEGTableIndex = (int) icps.getUI4(0x03000002, -1L);
		// System.out.println("maxJPEGTableIndex = " + maxJPEGTableIndex);
		JPEGTable = new byte[maxJPEGTableIndex + 1][];
		for (int i = 0; i <= maxJPEGTableIndex; i++) {
			final int index = i << 16;
			if (icps.hasProperty(0x03000001 | index)) {
				// System.out.println("Found a table at index " + i);
				JPEGTable[i] = icps.getBlob(0x03000001 | index);
			} else {
				// System.out.println("No table at index " + i);
				JPEGTable[i] = null;
			}
		}
	}

	private void readResolution() throws IOException {
		if (resolution == -1) {
			resolution = highestResolution;
		}

		// System.out.println("Reading resolution " + resolution);

		storage.changeDirectoryToRoot();
		storage.changeDirectory("Data Object Store 000001");
		storage.changeDirectory("Resolution 000" + resolution); // FIX

		subimageHeaderStream = storage.getStream("Subimage 0000 Header");
		subimageHeaderStream.skip(28);
		final int headerLength = subimageHeaderStream.readIntLE();
		width = subimageHeaderStream.readIntLE();
		height = subimageHeaderStream.readIntLE();
		final int numTiles = subimageHeaderStream.readIntLE();
		tileWidth = subimageHeaderStream.readIntLE();
		tileHeight = subimageHeaderStream.readIntLE();
		numChannels = subimageHeaderStream.readIntLE();
		tileHeaderTableOffset = subimageHeaderStream.readIntLE() + 28;
		tileHeaderEntryLength = subimageHeaderStream.readIntLE();

		// System.out.println("\nResolution 000" + resolution + "\n");
		// System.out.println("Subimage 0000 Header:\n");
		// System.out.println(" headerLength = " + headerLength);
		// System.out.println(" width = " + width);
		// System.out.println(" height = " + height);
		// System.out.println(" numTiles = " + numTiles);
		// System.out.println(" tileWidth = " + tileWidth);
		// System.out.println(" tileHeight = " + tileHeight);
		// System.out.println(" numChannels = " + numChannels);
		// System.out.println(" tileHeaderTableOffset = " +
		// tileHeaderTableOffset);
		// System.out.println(" tileHeaderEntryLength = " +
		// tileHeaderEntryLength);

		subimageDataStream = storage.getStream("Subimage 0000 Data");

		// Compute derived values
		tilesAcross = (width + tileWidth - 1) / tileWidth;
	}

	private int getTileOffset(final int tileIndex) throws IOException {
		// return tileOffset[tileIndex];

		subimageHeaderStream.seek(tileHeaderTableOffset + 16 * tileIndex);
		return subimageHeaderStream.readIntLE() + 28;
	}

	private int getTileSize(final int tileIndex) throws IOException {
		// return tileSize[tileIndex];

		subimageHeaderStream.seek(tileHeaderTableOffset + 16 * tileIndex + 4);
		return subimageHeaderStream.readIntLE();
	}

	private int getCompressionType(final int tileIndex) throws IOException {
		// return compressionType[tileIndex];

		subimageHeaderStream.seek(tileHeaderTableOffset + 16 * tileIndex + 8);
		return subimageHeaderStream.readIntLE();
	}

	private int getCompressionSubtype(final int tileIndex) throws IOException {
		// return compressionSubtype[tileIndex];

		subimageHeaderStream.seek(tileHeaderTableOffset + 16 * tileIndex + 12);
		return subimageHeaderStream.readIntLE();
	}

	private static final byte[] PhotoYCCToRGBLUT = { (byte) 0, (byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 3,
			(byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 12, (byte) 13,
			(byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19, (byte) 20, (byte) 22, (byte) 23,
			(byte) 24, (byte) 25, (byte) 26, (byte) 28, (byte) 29, (byte) 30, (byte) 31,

			(byte) 33, (byte) 34, (byte) 35, (byte) 36, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 43,
			(byte) 44, (byte) 45, (byte) 47, (byte) 48, (byte) 49, (byte) 51, (byte) 52, (byte) 53, (byte) 55,
			(byte) 56, (byte) 57, (byte) 59, (byte) 60, (byte) 61, (byte) 63, (byte) 64, (byte) 65, (byte) 67,
			(byte) 68, (byte) 70, (byte) 71, (byte) 72, (byte) 74,

			(byte) 75, (byte) 76, (byte) 78, (byte) 79, (byte) 81, (byte) 82, (byte) 83, (byte) 85, (byte) 86,
			(byte) 88, (byte) 89, (byte) 91, (byte) 92, (byte) 93, (byte) 95, (byte) 96, (byte) 98, (byte) 99,
			(byte) 101, (byte) 102, (byte) 103, (byte) 105, (byte) 106, (byte) 108, (byte) 109, (byte) 111, (byte) 112,
			(byte) 113, (byte) 115, (byte) 116, (byte) 118, (byte) 119,

			(byte) 121, (byte) 122, (byte) 123, (byte) 125, (byte) 126, (byte) 128, (byte) 129, (byte) 130, (byte) 132,
			(byte) 133, (byte) 134, (byte) 136, (byte) 137, (byte) 138, (byte) 140, (byte) 141, (byte) 142, (byte) 144,
			(byte) 145, (byte) 146, (byte) 148, (byte) 149, (byte) 150, (byte) 152, (byte) 153, (byte) 154, (byte) 155,
			(byte) 157, (byte) 158, (byte) 159, (byte) 160, (byte) 162,

			(byte) 163, (byte) 164, (byte) 165, (byte) 166, (byte) 168, (byte) 169, (byte) 170, (byte) 171, (byte) 172,
			(byte) 174, (byte) 175, (byte) 176, (byte) 177, (byte) 178, (byte) 179, (byte) 180, (byte) 182, (byte) 183,
			(byte) 184, (byte) 185, (byte) 186, (byte) 187, (byte) 188, (byte) 189, (byte) 190, (byte) 191, (byte) 192,
			(byte) 194, (byte) 195, (byte) 196, (byte) 197, (byte) 198,

			(byte) 199, (byte) 200, (byte) 201, (byte) 202, (byte) 203, (byte) 204, (byte) 204, (byte) 205, (byte) 206,
			(byte) 207, (byte) 208, (byte) 209, (byte) 210, (byte) 211, (byte) 212, (byte) 213, (byte) 213, (byte) 214,
			(byte) 215, (byte) 216, (byte) 217, (byte) 217, (byte) 218, (byte) 219, (byte) 220, (byte) 221, (byte) 221,
			(byte) 222, (byte) 223, (byte) 223, (byte) 224, (byte) 225,

			(byte) 225, (byte) 226, (byte) 227, (byte) 227, (byte) 228, (byte) 229, (byte) 229, (byte) 230, (byte) 230,
			(byte) 231, (byte) 231, (byte) 232, (byte) 233, (byte) 233, (byte) 234, (byte) 234, (byte) 235, (byte) 235,
			(byte) 236, (byte) 236, (byte) 236, (byte) 237, (byte) 237, (byte) 238, (byte) 238, (byte) 238, (byte) 239,
			(byte) 239, (byte) 240, (byte) 240, (byte) 240, (byte) 241,

			(byte) 241, (byte) 241, (byte) 242, (byte) 242, (byte) 242, (byte) 242, (byte) 243, (byte) 243, (byte) 243,
			(byte) 244, (byte) 244, (byte) 244, (byte) 244, (byte) 245, (byte) 245, (byte) 245, (byte) 245, (byte) 245,
			(byte) 246, (byte) 246, (byte) 246, (byte) 246, (byte) 246, (byte) 247, (byte) 247, (byte) 247, (byte) 247,
			(byte) 247, (byte) 247, (byte) 248, (byte) 248, (byte) 248,

			(byte) 248, (byte) 248, (byte) 248, (byte) 249, (byte) 249, (byte) 249, (byte) 249, (byte) 249, (byte) 249,
			(byte) 249, (byte) 249, (byte) 249, (byte) 250, (byte) 250, (byte) 250, (byte) 250, (byte) 250, (byte) 250,
			(byte) 250, (byte) 250, (byte) 250, (byte) 250, (byte) 251, (byte) 251, (byte) 251, (byte) 251, (byte) 251,
			(byte) 251, (byte) 251, (byte) 251, (byte) 251, (byte) 251,

			(byte) 251, (byte) 251, (byte) 251, (byte) 251, (byte) 252, (byte) 252, (byte) 252, (byte) 252, (byte) 252,
			(byte) 252, (byte) 252, (byte) 252, (byte) 252, (byte) 252, (byte) 252, (byte) 252, (byte) 252, (byte) 252,
			(byte) 252, (byte) 252, (byte) 252, (byte) 253, (byte) 253, (byte) 253, (byte) 253, (byte) 253, (byte) 253,
			(byte) 253, (byte) 253, (byte) 253, (byte) 253, (byte) 253,

			(byte) 253, (byte) 253, (byte) 253, (byte) 253, (byte) 253, (byte) 253, (byte) 253, (byte) 254, (byte) 254,
			(byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254,
			(byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
			(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,

			(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
			(byte) 255 };

	private final byte PhotoYCCToNIFRed(final float scaledY, final float Cb, final float Cr) {
		final float red = scaledY + 1.8215F * Cr - 249.55F;
		if (red < 0.0F) {
			return (byte) 0;
		} else if (red > 360.0F) {
			return (byte) 255;
		} else {
			final byte r = PhotoYCCToRGBLUT[(int) red];
			return r;
		}
	}

	private final byte PhotoYCCToNIFGreen(final float scaledY, final float Cb, final float Cr) {
		final float green = scaledY - .43031F * Cb - .9271F * Cr + 194.14F;
		if (green < 0.0F) {
			return (byte) 0;
		} else if (green > 360.0F) {
			return (byte) 255;
		} else {
			final byte g = PhotoYCCToRGBLUT[(int) green];
			return g;
		}
	}

	private final byte PhotoYCCToNIFBlue(final float scaledY, final float Cb, final float Cr) {
		final float blue = scaledY + 2.2179F * Cb - 345.99F;
		if (blue < 0.0F) {
			return (byte) 0;
		} else if (blue > 360.0F) {
			return (byte) 255;
		} else {
			final byte b = PhotoYCCToRGBLUT[(int) blue];
			return b;
		}
	}

	private final byte YCCToNIFRed(final float Y, final float Cb, final float Cr) {
		final float red = Y + 1.402F * Cr - 255.0F * .701F;
		if (red < 0.0F) {
			return (byte) 0;
		} else if (red > 255.0F) {
			return (byte) 255;
		} else {
			return (byte) red;
		}
	}

	private final byte YCCToNIFGreen(final float Y, final float Cb, final float Cr) {
		final float green = Y - .34414F * Cb - .71414F * Cr + 255.0F * .52914F;
		if (green < 0.0F) {
			return (byte) 0;
		} else if (green > 255.0F) {
			return (byte) 255;
		} else {
			return (byte) green;
		}
	}

	private final byte YCCToNIFBlue(final float Y, final float Cb, final float Cr) {
		final float blue = Y + 1.772F * Cb - 255.0F * .886F;
		if (blue < 0.0F) {
			return (byte) 0;
		} else if (blue > 255.0F) {
			return (byte) 255;
		} else {
			return (byte) blue;
		}
	}

	private Raster getUncompressedTile(final int tileX, final int tileY) throws IOException {
		final int tx = tileXToX(tileX);
		final int ty = tileYToY(tileY);
		final Raster ras = RasterFactory.createInterleavedRaster(DataBuffer.TYPE_BYTE, tileWidth, tileHeight,
				numChannels * tileWidth, numChannels, bandOffsets, new Point(tx, ty));
		// System.out.println("Uncompressed tile.");

		final DataBufferByte dataBuffer = (DataBufferByte) ras.getDataBuffer();
		final byte[] data = dataBuffer.getData();

		final int tileIndex = tileY * tilesAcross + tileX;
		subimageDataStream.seek(getTileOffset(tileIndex));
		subimageDataStream.readFully(data, 0, numChannels * tileWidth * tileHeight);

		// Color convert if subimage is in PhotoYCC format
		if (subimageColor[resolution][0] >> 16 == 2) {
			final int size = tileWidth * tileHeight;
			for (int i = 0; i < size; i++) {
				final float Y = data[3 * i] & 0xff;
				final float Cb = data[3 * i + 1] & 0xff;
				final float Cr = data[3 * i + 2] & 0xff;

				final float scaledY = Y * 1.3584F;
				final byte red = PhotoYCCToNIFRed(scaledY, Cb, Cr);
				final byte green = PhotoYCCToNIFGreen(scaledY, Cb, Cr);
				final byte blue = PhotoYCCToNIFBlue(scaledY, Cb, Cr);

				data[3 * i] = red;
				data[3 * i + 1] = green;
				data[3 * i + 2] = blue;
			}
		}

		return ras;
	}

	private Raster getSingleColorCompressedTile(final int tileX, final int tileY) throws IOException {
		// System.out.println("Single color compressed tile.");

		final int tx = tileXToX(tileX);
		final int ty = tileYToY(tileY);
		final Raster ras = RasterFactory.createInterleavedRaster(DataBuffer.TYPE_BYTE, tileWidth, tileHeight,
				numChannels * tileWidth, numChannels, bandOffsets, new Point(tx, ty));

		final int subimageColorType = subimageColor[resolution][0] >> 16;

		final DataBufferByte dataBuffer = (DataBufferByte) ras.getDataBuffer();
		final byte[] data = dataBuffer.getData();

		final int tileIndex = tileY * tilesAcross + tileX;
		final int color = getCompressionSubtype(tileIndex);
		final byte c0 = (byte) (color >> 0 & 0xff);
		final byte c1 = (byte) (color >> 8 & 0xff);
		final byte c2 = (byte) (color >> 16 & 0xff);
		final byte alpha = (byte) (color >> 24 & 0xff);

		byte red, green, blue;

		// Color convert if subimage is in PhotoYCC format
		if (subimageColor[resolution][0] >> 16 == 2) {
			final float Y = c0 & 0xff;
			final float Cb = c1 & 0xff;
			final float Cr = c2 & 0xff;

			final float scaledY = Y * 1.3584F;
			red = PhotoYCCToNIFRed(scaledY, Cb, Cr);
			green = PhotoYCCToNIFGreen(scaledY, Cb, Cr);
			blue = PhotoYCCToNIFBlue(scaledY, Cb, Cr);
		} else {
			red = c0;
			green = c1;
			blue = c2;
		}

		int index = 0;
		final int pixels = tileWidth * tileHeight;

		if (numChannels == 1) {
		} else if (numChannels == 2) {
		} else if (numChannels == 3) {
			for (int i = 0; i < pixels; i++) {
				data[index + 0] = red;
				data[index + 1] = green;
				data[index + 2] = blue;

				index += 3;
			}
		} else if (numChannels == 4) {
			for (int i = 0; i < pixels; i++) {
				data[index + 0] = red;
				data[index + 1] = green;
				data[index + 2] = blue;
				data[index + 3] = alpha;

				index += 4;
			}
		}

		return ras;
	}

	private Raster getJPEGCompressedTile(final int tileX, final int tileY) throws IOException {
		// System.out.println("JPEG compressed tile.");

		final int tileIndex = tileY * tilesAcross + tileX;

		final int tx = tileXToX(tileX);
		final int ty = tileYToY(tileY);

		final int subtype = getCompressionSubtype(tileIndex);
		final int interleave = subtype >> 0 & 0xff;
		final int chroma = subtype >> 8 & 0xff;
		final int conversion = subtype >> 16 & 0xff;
		final int table = subtype >> 24 & 0xff;

		JPEGImageDecoder dec;
		JPEGDecodeParam param = null;

		if (table != 0) {
			final InputStream tableStream = new ByteArrayInputStream(JPEGTable[table]);
			dec = JPEGCodec.createJPEGDecoder(tableStream);
			final Raster junk = dec.decodeAsRaster();
			param = dec.getJPEGDecodeParam();
		}

		subimageDataStream.seek(getTileOffset(tileIndex));
		if (param != null) {
			dec = JPEGCodec.createJPEGDecoder(subimageDataStream, param);
		} else {
			dec = JPEGCodec.createJPEGDecoder(subimageDataStream);
		}
		final Raster ras = dec.decodeAsRaster().createTranslatedChild(tx, ty);

		final DataBufferByte dataBuffer = (DataBufferByte) ras.getDataBuffer();
		final byte[] data = dataBuffer.getData();

		final int subimageColorType = subimageColor[resolution][0] >> 16;

		final int size = tileWidth * tileHeight;
		if (conversion == 0 && subimageColorType == 2) {
			// System.out.println("Converting PhotoYCC to NIFRGB");
			int offset = 0;
			for (int i = 0; i < size; i++) {
				final float Y = data[offset] & 0xff;
				final float Cb = data[offset + 1] & 0xff;
				final float Cr = data[offset + 2] & 0xff;

				final float scaledY = Y * 1.3584F;
				final byte red = PhotoYCCToNIFRed(scaledY, Cb, Cr);
				final byte green = PhotoYCCToNIFGreen(scaledY, Cb, Cr);
				final byte blue = PhotoYCCToNIFBlue(scaledY, Cb, Cr);

				data[offset] = red;
				data[offset + 1] = green;
				data[offset + 2] = blue;

				offset += numChannels;
			}
		} else if (conversion == 1 && subimageColorType == 3) {
			// System.out.println("Converting YCC to NIFRGB");
			int offset = 0;
			for (int i = 0; i < size; i++) {
				final float Y = data[offset] & 0xff;
				final float Cb = data[offset + 1] & 0xff;
				final float Cr = data[offset + 2] & 0xff;

				final byte red = YCCToNIFRed(Y, Cb, Cr);
				final byte green = YCCToNIFGreen(Y, Cb, Cr);
				final byte blue = YCCToNIFBlue(Y, Cb, Cr);

				data[offset] = red;
				data[offset + 1] = green;
				data[offset + 2] = blue;

				offset += numChannels;
			}
		}

		// Perform special inversion step when output space is
		// NIF RGB (subimageColorType == 3) with premultiplied opacity
		// (numChannels == 4).
		if (conversion == 1 && subimageColorType == 3 && numChannels == 4) {
			// System.out.println("Flipping NIFRGB");

			int offset = 0;
			for (int i = 0; i < size; i++) {
				data[offset + 0] = (byte) (255 - data[offset + 0]);
				data[offset + 1] = (byte) (255 - data[offset + 1]);
				data[offset + 2] = (byte) (255 - data[offset + 2]);

				offset += 4;
			}
		}

		return ras;
	}

	@Override
	public Raster getTile(final int tileX, final int tileY) {
		final int tileIndex = tileY * tilesAcross + tileX;

		try {
			final int ctype = getCompressionType(tileIndex);
			if (ctype == 0) {
				return getUncompressedTile(tileX, tileY);
			} else if (ctype == 1) {
				return getSingleColorCompressedTile(tileX, tileY);
			} else if (ctype == 2) {
				return getJPEGCompressedTile(tileX, tileY);
			}
			return null;
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	Hashtable<String, Object> properties = null;

	private void addLPSTRProperty(final String name, final PropertySet ps, final int id) {
		final String s = ps.getLPSTR(id);
		if (s != null) {
			properties.put(name.toLowerCase(), s);
		}
	}

	private void addLPWSTRProperty(final String name, final PropertySet ps, final int id) {
		final String s = ps.getLPWSTR(id);
		if (s != null) {
			properties.put(name.toLowerCase(), s);
		}
	}

	private void addUI4Property(final String name, final PropertySet ps, final int id) {
		if (ps.hasProperty(id)) {
			final long i = ps.getUI4(id);
			properties.put(name.toLowerCase(), new Integer((int) i));
		}
	}

	private void getSummaryInformation() {
		SeekableStream summaryInformation = null;
		PropertySet sips = null;
		try {
			storage.changeDirectoryToRoot();
			summaryInformation = storage.getStream("SummaryInformation");
			sips = new PropertySet(summaryInformation);
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		addLPSTRProperty("title", sips, 0x000000002);
		addLPSTRProperty("subject", sips, 0x000000003);
		addLPSTRProperty("author", sips, 0x000000004);
		addLPSTRProperty("keywords", sips, 0x000000005);
		addLPSTRProperty("comments", sips, 0x000000006);
		addLPSTRProperty("template", sips, 0x000000007);
		addLPSTRProperty("last saved by", sips, 0x000000008);
		addLPSTRProperty("revision number", sips, 0x000000009);
	}

	private void getImageInfo() {
		SeekableStream imageInfo = null;
		PropertySet iips = null;
		try {
			storage.changeDirectoryToRoot();
			imageInfo = storage.getStream("Image Info");
			if (imageInfo == null) {
				return;
			}
			iips = new PropertySet(imageInfo);
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		addUI4Property("file source", iips, 0x21000000);
		addUI4Property("scene type", iips, 0x21000001);
		// creation path vector
		addLPWSTRProperty("software name/manufacturer/release", iips, 0x21000003);
		addLPWSTRProperty("user defined id", iips, 0x21000004);

		addLPWSTRProperty("copyright message", iips, 0x22000000);
		addLPWSTRProperty("legal broker for the original image", iips, 0x22000001);
		addLPWSTRProperty("legal broker for the digital image", iips, 0x22000002);
		addLPWSTRProperty("authorship", iips, 0x22000003);
		addLPWSTRProperty("intellectual property notes", iips, 0x22000004);
	}

	private synchronized void getProperties() {
		if (properties != null) {
			return;
		}
		properties = new Hashtable<String, Object>();

		getSummaryInformation();
		getImageInfo();

		// Ad hoc properties
		properties.put("max_resolution", new Integer(highestResolution));
	}

	@Override
	public String[] getPropertyNames() {
		getProperties();

		final int len = properties.size();
		final String[] names = new String[len];
		final Enumeration<String> enum0 = properties.keys();

		int count = 0;
		while (enum0.hasMoreElements()) {
			names[count++] = enum0.nextElement();
		}

		return names;
	}

	@Override
	public Object getProperty(final String name) {
		getProperties();
		return properties.get(name.toLowerCase());
	}
}
