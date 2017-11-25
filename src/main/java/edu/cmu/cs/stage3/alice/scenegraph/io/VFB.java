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

import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;

public class VFB {
	public static Vertex3d[] loadVertices(final java.io.InputStream is)
			throws java.io.IOException, java.io.FileNotFoundException {
		return (Vertex3d[]) load(new java.io.BufferedInputStream(is))[0];
	}

	public static int[] loadIndices(final java.io.InputStream is)
			throws java.io.IOException, java.io.FileNotFoundException {
		return (int[]) load(new java.io.BufferedInputStream(is))[1];
	}

	public static Object[] load(final java.io.BufferedInputStream bis)
			throws java.io.IOException, java.io.FileNotFoundException {
		final int nByteCount = bis.available();
		final byte[] byteArray = new byte[nByteCount];
		bis.read(byteArray);
		int nByteIndex;
		for (nByteIndex = 0; nByteIndex < nByteCount; nByteIndex += 4) {
			byte b;
			b = byteArray[nByteIndex];
			byteArray[nByteIndex] = byteArray[nByteIndex + 3];
			byteArray[nByteIndex + 3] = b;
			b = byteArray[nByteIndex + 1];
			byteArray[nByteIndex + 1] = byteArray[nByteIndex + 2];
			byteArray[nByteIndex + 2] = b;
		}
		final java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(byteArray);
		final java.io.DataInputStream dis = new java.io.DataInputStream(bais);
		final Object[] verticesAndIndices = { null, null };
		final int nVersion = dis.readInt();
		if (nVersion == 1) {
			final int vertexCount = dis.readInt();
			final Vertex3d[] vertices = new Vertex3d[vertexCount];
			for (int i = 0; i < vertices.length; i++) {
				vertices[i] = new Vertex3d(
						Vertex3d.FORMAT_POSITION | Vertex3d.FORMAT_NORMAL | Vertex3d.FORMAT_TEXTURE_COORDINATE_0);
				vertices[i].position.x = -dis.readFloat();
				vertices[i].position.y = dis.readFloat();
				vertices[i].position.z = dis.readFloat();
				vertices[i].normal.x = -dis.readFloat();
				vertices[i].normal.y = dis.readFloat();
				vertices[i].normal.z = dis.readFloat();
				vertices[i].textureCoordinate0.x = dis.readFloat();
				vertices[i].textureCoordinate0.y = dis.readFloat();
			}

			final int faceCount = dis.readInt();
			// Unused ?? final int faceDataCount = dis.readInt();
			final int verticesPerFace = dis.readInt();
			final int[] indices = new int[faceCount * 3];
			int i = 0;
			for (int f = 0; f < faceCount; f++) {
				int length;
				if (verticesPerFace == 0) {
					length = dis.readInt();
				} else {
					length = verticesPerFace;
				}
				indices[i + 0] = dis.readInt();
				indices[i + 1] = dis.readInt();
				indices[i + 2] = dis.readInt();
				i += 3;
				for (int lcv = 3; lcv < length; lcv++) {
					dis.readInt();
				}
			}
			verticesAndIndices[0] = vertices;
			verticesAndIndices[1] = indices;
		}
		return verticesAndIndices;
	}

	private static void store(final java.io.BufferedOutputStream bos, final int i) throws java.io.IOException {
		bos.write((byte) (i & 0x000000FF));
		bos.write((byte) (i >> 8 & 0x000000FF));
		bos.write((byte) (i >> 16 & 0x000000FF));
		bos.write((byte) (i >> 24 & 0x000000FF));
	}

	private static void store(final java.io.BufferedOutputStream bos, final float f) throws java.io.IOException {
		store(bos, Float.floatToIntBits(f));
	}

	public static void store(final java.io.OutputStream os, final Vertex3d[] vertices, final int[] indices)
			throws java.io.IOException {
		final java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(os);
		store(bos, 1);
		if (vertices != null) {
			store(bos, vertices.length);
			for (final Vertex3d vertice : vertices) {
				store(bos, (float) vertice.position.x);
				store(bos, (float) vertice.position.y);
				store(bos, (float) vertice.position.z);
				store(bos, (float) vertice.normal.x);
				store(bos, (float) vertice.normal.y);
				store(bos, (float) vertice.normal.z);
				store(bos, vertice.textureCoordinate0.x);
				store(bos, vertice.textureCoordinate0.y);
			}
		} else {
			store(bos, 0);
		}
		if (indices != null) {
			store(bos, indices.length / 3);
			store(bos, indices.length);
			store(bos, 3);
			for (int i = 0; i < indices.length; i += 3) {
				store(bos, indices[i + 2]);
				store(bos, indices[i + 1]);
				store(bos, indices[i]);
			}
		} else {
			store(bos, 0);
		}
		bos.flush();
	}
}