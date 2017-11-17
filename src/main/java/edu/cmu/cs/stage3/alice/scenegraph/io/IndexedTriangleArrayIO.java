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

package edu.cmu.cs.stage3.alice.scenegraph.io;

public class IndexedTriangleArrayIO {
	private static final String[] s_codecNames = { "obj", "vfb" };
	private static final String[] s_objExtensions = { "obj" };
	private static final String[] s_vfbExtensions = { "vfb" };

	public static String[] getCodecNames() {
		return s_codecNames;
	}

	public static String[] getExtensionsForCodec(final String codecName) {
		if (codecName.equals("obj")) {
			return s_objExtensions;
		} else if (codecName.equals("vfb")) {
			return s_vfbExtensions;
		} else {
			return null;
		}
	}

	public static String mapExtensionToCodecName(final String extension) {
		final String[] codecNames = IndexedTriangleArrayIO.getCodecNames();
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

	private static Object[] decodeOBJ(final java.io.BufferedInputStream bufferedInputStream)
			throws java.io.IOException {
		return OBJ.load(bufferedInputStream);
	}

	private static Object[] decodeVFB(final java.io.BufferedInputStream bufferedInputStream)
			throws java.io.IOException {
		return VFB.load(bufferedInputStream);
	}

	public static edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray decode(final String codecName,
			final java.io.InputStream inputStream) throws java.io.IOException {
		final java.io.BufferedInputStream bufferedInputStream = new java.io.BufferedInputStream(inputStream);
		Object[] array;
		if (codecName.equals("obj")) {
			array = decodeOBJ(bufferedInputStream);
		} else if (codecName.equals("vfb")) {
			array = decodeVFB(bufferedInputStream);
		} else {
			throw new RuntimeException("unknown codec: " + codecName);
		}
		if (array != null) {
			final edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray ita = new edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray();
			ita.setVertices((edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[]) array[0]);
			ita.setIndices((int[]) array[1]);
			return ita;
		} else {
			return null;
		}
	}

	private static void encodeOBJ(final java.io.BufferedOutputStream bufferedOutputStream,
			final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices, final int[] indices)
			throws java.io.IOException {
		OBJ.store(bufferedOutputStream, vertices, indices, null, null);
	}

	private static void encodeVFB(final java.io.BufferedOutputStream bufferedOutputStream,
			final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices, final int[] indices)
			throws java.io.IOException {
		VFB.store(bufferedOutputStream, vertices, indices);
	}

	public static void encode(final String codecName, final java.io.OutputStream outputStream,
			final edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray ita) throws java.io.IOException {
		final java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(outputStream);
		if (codecName.equals("obj")) {
			encodeOBJ(bufferedOutputStream, ita.getVertices(), ita.getIndices());
		} else if (codecName.equals("vfb")) {
			encodeVFB(bufferedOutputStream, ita.getVertices(), ita.getIndices());
		} else {
			throw new RuntimeException("unknown codec: " + codecName);
		}
		bufferedOutputStream.flush();
	}
}
