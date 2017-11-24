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

import java.util.Vector;

import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;

public class OBJ {
	private static double getNextNumber(final java.io.StreamTokenizer streamTokenizer) {
		try {
			streamTokenizer.nextToken();
			if (streamTokenizer.ttype == java.io.StreamTokenizer.TT_NUMBER) {
				final double f = streamTokenizer.nval;
				streamTokenizer.nextToken();
				if (streamTokenizer.ttype == java.io.StreamTokenizer.TT_WORD) {
					if (streamTokenizer.sval.startsWith("E")) {
						final int exponent = Integer.parseInt(streamTokenizer.sval.substring(1));
						return f * Math.pow(10, exponent);
					}
				}
				streamTokenizer.pushBack();
				return f;
			}
		} catch (final java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		return Double.NaN;
	}

	public static Object[] load(final java.io.InputStream is) throws java.io.IOException {
		final java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(is));
		final java.io.StreamTokenizer st = new java.io.StreamTokenizer(r);
		st.commentChar('#');
		st.slashSlashComments(false);
		st.slashStarComments(false);
		st.whitespaceChars('/', '/');
		st.parseNumbers();
		final java.util.Vector<double[]> xyzs = new java.util.Vector<double[]>();
		final java.util.Vector<double[]> ijks = new java.util.Vector<double[]>();
		final java.util.Vector<double[]> uvs = new java.util.Vector<double[]>();
		final java.util.Vector<Vector<Integer>> fs = new java.util.Vector<Vector<Integer>>();
		while (st.nextToken() == java.io.StreamTokenizer.TT_WORD) {
			if (st.sval.startsWith("vt")) {
				final double uv[] = new double[3];
				uv[0] = getNextNumber(st);
				uv[1] = getNextNumber(st);
				uvs.addElement(uv);
			} else if (st.sval.startsWith("vn")) {
				final double ijk[] = new double[3];
				ijk[0] = getNextNumber(st);
				ijk[1] = getNextNumber(st);
				ijk[2] = getNextNumber(st);
				ijks.addElement(ijk);
			} else if (st.sval.startsWith("v")) {
				final double xyz[] = new double[3];
				xyz[0] = getNextNumber(st);
				xyz[1] = getNextNumber(st);
				xyz[2] = getNextNumber(st);
				xyzs.addElement(xyz);
			} else if (st.sval.startsWith("f")) {
				final java.util.Vector<Integer> f = new java.util.Vector<Integer>();
				while (st.nextToken() == java.io.StreamTokenizer.TT_NUMBER) {
					f.addElement(new Integer((int) st.nval - 1));
				}
				st.pushBack();
				fs.addElement(f);
			} else {
				break;
			}
		}
		final int nVertexCount = xyzs.size();
		final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[nVertexCount];
		final double ijkDefault[] = new double[3];
		ijkDefault[0] = 0;
		ijkDefault[1] = 1;
		ijkDefault[2] = 0;
		final double uvDefault[] = new double[2];
		uvDefault[0] = 0;
		uvDefault[1] = 0;
		for (int v = 0; v < nVertexCount; v++) {
			final double xyz[] = xyzs.elementAt(v);
			double ijk[];
			double uv[];
			try {
				ijk = ijks.elementAt(v);
			} catch (final ArrayIndexOutOfBoundsException e) {
				ijk = ijkDefault;
			}
			try {
				uv = uvs.elementAt(v);
			} catch (final ArrayIndexOutOfBoundsException e) {
				uv = uvDefault;
			}
			vertices[v] = edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(xyz[0], xyz[1], xyz[2], ijk[0],
					ijk[1], ijk[2], (float) uv[0], (float) uv[1]);
		}
		// todo
		final int[] indices = new int[fs.size() * 3];
		int i = 0;
		for (int f = 0; f < fs.size(); f++) {
			final java.util.Vector face = fs.elementAt(f);
			switch (face.size()) {
			case 3:
				indices[i++] = ((Integer) face.elementAt(0)).intValue();
				indices[i++] = ((Integer) face.elementAt(1)).intValue();
				indices[i++] = ((Integer) face.elementAt(2)).intValue();
				break;
			case 6:
				indices[i++] = ((Integer) face.elementAt(0)).intValue();
				indices[i++] = ((Integer) face.elementAt(2)).intValue();
				indices[i++] = ((Integer) face.elementAt(4)).intValue();
				break;
			case 9:
				indices[i++] = ((Integer) face.elementAt(0)).intValue();
				indices[i++] = ((Integer) face.elementAt(3)).intValue();
				indices[i++] = ((Integer) face.elementAt(6)).intValue();
				break;
			default:
				throw new RuntimeException("unhandled face index size");
			}
		}
		final Object[] array = { vertices, indices };
		return array;
	}

	public static void store(final java.io.OutputStream os,
			final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices, final int[] indices,
			final javax.vecmath.Matrix4d m, final String groupNames) throws java.io.IOException {
		if (vertices != null && indices != null) {
			final java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(os);
			final java.io.PrintWriter pw = new java.io.PrintWriter(bos);
			if (groupNames != null) {
				pw.println("g " + groupNames);
			}
			for (final Vertex3d vertice : vertices) {
				double x = vertice.position.x;
				double y = vertice.position.y;
				double z = vertice.position.z;
				double i = vertice.normal.x;
				double j = vertice.normal.y;
				double k = vertice.normal.z;
				final double u = vertice.textureCoordinate0.x;
				final double v = vertice.textureCoordinate0.y;
				if (m != null) {
					final javax.vecmath.Vector4d xyzw = edu.cmu.cs.stage3.math.MathUtilities.multiply(x, y, z, 1, m);
					final javax.vecmath.Vector4d ijkw = edu.cmu.cs.stage3.math.MathUtilities.multiply(i, j, k, 0, m);
					x = xyzw.x;
					y = xyzw.y;
					z = xyzw.z;
					i = ijkw.x;
					j = ijkw.y;
					k = ijkw.z;
				}
				pw.print("v ");
				pw.print(x);
				pw.print(" ");
				pw.print(y);
				pw.print(" ");
				pw.print(z);
				pw.println();
				pw.print("vt ");
				pw.print(u);
				pw.print(" ");
				pw.print(v);
				pw.println();
				pw.print("vn ");
				pw.print(i);
				pw.print(" ");
				pw.print(j);
				pw.print(" ");
				pw.print(k);
				pw.println();
			}
			for (int i = 0; i < indices.length; i += 3) {
				pw.print("f ");
				for (int j = 0; j < 3; j++) {
					final int a = indices[i + j] - vertices.length;
					pw.print(a + "/" + a + "/" + a + " ");
				}
				pw.println();
			}
			pw.flush();
		}
	}

	private static void store(final java.io.OutputStream os,
			final edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray ita, final javax.vecmath.Matrix4d m,
			final String groupNames) throws java.io.IOException {
		store(os, ita.getVertices(), ita.getIndices(), m, groupNames);
	}

	private static void store(final java.io.OutputStream os, final edu.cmu.cs.stage3.alice.scenegraph.Visual visual,
			final javax.vecmath.Matrix4d m, final String groupNames) throws java.io.IOException {
		final edu.cmu.cs.stage3.alice.scenegraph.Geometry geometry = visual.getGeometry();
		if (geometry instanceof edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray) {
			store(os, (edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray) geometry, m, groupNames);
		}
	}

	private static void store(final java.io.OutputStream os,
			final edu.cmu.cs.stage3.alice.scenegraph.Transformable transformable,
			final edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame root, String groupNames)
			throws java.io.IOException {
		String name = transformable.getName();
		if (name != null) {
			final int k = name.indexOf(".m_sgTransformable");
			if (k != -1) {
				name = name.substring(0, k);
			}
		} else {
			name = "null";
		}
		if (groupNames.length() > 0) {
			// groups heirarchy should be space delimitted, but for uniqueness
			// in 3dsmax we use underscores
			groupNames = name + "_" + groupNames;
		} else {
			groupNames = name;
		}
		for (int i = 0; i < transformable.getChildCount(); i++) {
			final edu.cmu.cs.stage3.alice.scenegraph.Component child = transformable.getChildAt(i);
			if (child instanceof edu.cmu.cs.stage3.alice.scenegraph.Transformable) {
				store(os, (edu.cmu.cs.stage3.alice.scenegraph.Transformable) child, root, groupNames);
			} else if (child instanceof edu.cmu.cs.stage3.alice.scenegraph.Visual) {
				store(os, (edu.cmu.cs.stage3.alice.scenegraph.Visual) child, transformable.getTransformation(root),
						groupNames);
			}
		}
	}

	public static void store(final java.io.OutputStream os,
			final edu.cmu.cs.stage3.alice.scenegraph.Transformable transformable) throws java.io.IOException {
		store(os, transformable, transformable, "");
	}
}