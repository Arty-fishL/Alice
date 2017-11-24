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

package edu.cmu.cs.stage3.alice.core.geometry;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import java.awt.Shape;
import java.awt.geom.PathIterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.math.MathUtilities;

public class PolygonSegment {
	private final java.util.Vector<Point2d> points;
	private final java.util.Vector<Vector3f> normals;

	private Vertex3d[] sideVertices = null;
	private int[] indices = null;

	public PolygonSegment() {
		points = new java.util.Vector<Point2d>();
		normals = new java.util.Vector<Vector3f>();
	}

	protected Shape getShape() {
		if (points.isEmpty()) {
			return null;
		}
		final java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
		gp.moveTo((float) points.firstElement().x, (float) points.firstElement().y);
		final java.util.ListIterator<Point2d> li = points.listIterator(1);
		while (li.hasNext()) {
			final Point2d cur = li.next();
			gp.lineTo((float) cur.x, (float) cur.y);
		}
		gp.closePath();
		return gp;
	}

	public boolean contains(final double x, final double y) {
		return getShape().contains(x, y);
	}

	protected void addPoint(final Point2d point) {
		points.add(point);
		normals.setSize(normals.size() + 2);
		if (points.size() > 1) {
			final double dx = points.lastElement().x - points.elementAt(points.size() - 2).x;
			final double dy = points.lastElement().y - points.elementAt(points.size() - 2).y;
			final double len = Math.sqrt(dx * dx + dy * dy);
			final Vector3d a = new Vector3d(dx / len, 0, dy / len);
			final Vector3d b = new Vector3d(0, 1, 0);
			final Vector3d c = MathUtilities.crossProduct(a, b);
			normals.setElementAt(new Vector3f(-(float) c.x, -(float) c.z, 0), (points.size() - 2) * 2);
			normals.setElementAt(new Vector3f(-(float) c.x, -(float) c.z, 0), (points.size() - 1) * 2 + 1);
		}
	}

	protected void addQuadraticSpline(final Point2d cp1, final Point2d cp2, final Point2d offset, final int numSegs) {
		if (points.isEmpty()) {
			return;
		}

		normals.setSize(normals.size() + 2 * numSegs);

		final Point2d cp0 = new Point2d(-points.lastElement().x + offset.x,
				-points.lastElement().y + offset.y);

		final Point3d[] newPositions = new Point3d[numSegs + 1];
		final Vector3d[] newNormals = new Vector3d[numSegs + 1];

		edu.cmu.cs.stage3.alice.core.util.Polynomial.evaluateBezierQuadratic(cp0, cp1, cp2, 0, newPositions,
				newNormals);

		normals.setElementAt(new Vector3f(newNormals[0]), (points.size() - 1) * 2);
		for (int i = 1; i <= numSegs; i++) {
			points.add(new Point2d(offset.x - newPositions[i].x, offset.y - newPositions[i].y));
			normals.setElementAt(new Vector3f(newNormals[i]), (points.size() - 1) * 2);
			normals.setElementAt(new Vector3f(newNormals[i]), (points.size() - 1) * 2 + 1);
		}
	}

	protected void close() {
		if (points.isEmpty()) {
			return;
		}

		if (points.size() > 1 && points.lastElement().equals(points.firstElement())) {
			points.setSize(points.size() - 1);
			normals.setSize(normals.size() - 2);
		}
		if (points.size() >= 3) {

			final double dx = points.firstElement().x - points.lastElement().x;
			final double dy = points.firstElement().y - points.lastElement().y;
			final double len = Math.sqrt(dx * dx + dy * dy);
			final Vector3d a = new Vector3d(dx / len, 0, dy / len);
			final Vector3d b = new Vector3d(0, 1, 0);
			final Vector3d c = MathUtilities.crossProduct(a, b);
			normals.setElementAt(new Vector3f(-(float) c.x, -(float) c.z, 0), 1);
			normals.setElementAt(new Vector3f(-(float) c.x, -(float) c.z, 0), (points.size() - 1) * 2);
		} else {
			points.clear();
			normals.clear();
		}
	}

	public boolean parsePathIterator(final PathIterator pi, final Point2d offset, final int curvature) {
		final double[] coords = new double[6];
		int type = -1;

		while (!pi.isDone()) {
			type = pi.currentSegment(coords);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				if (!points.isEmpty()) {
					close();
					return false;
				}
				addPoint(new Point2d(offset.x - coords[0], offset.y - coords[1]));
				break;
			case PathIterator.SEG_LINETO:
				addPoint(new Point2d(offset.x - coords[0], offset.y - coords[1]));
				break;
			case PathIterator.SEG_QUADTO:
				addQuadraticSpline(new Point2d(coords[0], coords[1]), new Point2d(coords[2], coords[3]), offset,
						curvature);
				break;
			case PathIterator.SEG_CUBICTO:
				addPoint(new Point2d(offset.x - coords[0], offset.y - coords[1]));
				addPoint(new Point2d(offset.x - coords[2], offset.y - coords[3]));
				addPoint(new Point2d(offset.x - coords[4], offset.y - coords[5]));
				break;
			case PathIterator.SEG_CLOSE:
				close();
				return true;
			}
			pi.next();
		}
		close();
		return true;
	}

	public boolean isNull() {
		return points.isEmpty();
	}

	public java.util.Vector<Point2d> points() {
		return points;
	}

	public void reverse() {
		java.util.Collections.reverse(points);
		java.util.Collections.reverse(normals);
	}

	public void genSideStrips(final double extz/* , boolean outside */) {
		sideVertices = null;
		indices = null;
		if (points.isEmpty()) {
			return;
		}

		sideVertices = new Vertex3d[points.size() * 4];
		indices = new int[points.size() * 6];

		final java.util.ListIterator<Point2d> li = points.listIterator();
		for (int i = 0; li.hasNext(); i++) {
			final Point2d point = li.next();

			Point3d pos = new Point3d(point.x, point.y, -extz / 2);
			sideVertices[i * 2] = new Vertex3d(pos, new Vector3d(normals.elementAt(i * 2)), null, null,
					new TexCoord2f());
			sideVertices[i * 2 + 1] = new Vertex3d(pos, new Vector3d(normals.elementAt(i * 2 + 1)), null,
					null, new TexCoord2f());
			pos = new Point3d(point.x, point.y, extz / 2);
			sideVertices[points.size() * 2 + i * 2] = new Vertex3d(pos,
					new Vector3d(normals.elementAt(i * 2)), null, null, new TexCoord2f());
			sideVertices[points.size() * 2 + i * 2 + 1] = new Vertex3d(pos,
					new Vector3d(normals.elementAt(i * 2 + 1)), null, null, new TexCoord2f());
		}

		for (int i = 0; i < points.size() - 1; i++) {
			// if (outside) {
			indices[i * 6] = 2 * i;
			indices[i * 6 + 1] = 2 * i + 3;
			indices[i * 6 + 2] = 2 * i + 3 + points.size() * 2;
			indices[i * 6 + 3] = 2 * i;
			indices[i * 6 + 4] = 2 * i + 3 + points.size() * 2;
			indices[i * 6 + 5] = 2 * i + points.size() * 2;
			/*
			 * } else { indices[i*6]=2*i; indices[i*6+1]=2*i+3+points.length*2;
			 * indices[i*6+2]=2*i+3; indices[i*6+3]=2*i;
			 * indices[i*6+4]=2*i+points.length*2;
			 * indices[i*6+5]=2*i+3+points.length*2; }
			 */
		}
		// if (outside) {
		indices[(points.size() - 1) * 6] = 2 * (points.size() - 1);
		indices[(points.size() - 1) * 6 + 1] = 1;
		indices[(points.size() - 1) * 6 + 2] = 1 + points.size() * 2;
		indices[(points.size() - 1) * 6 + 3] = 2 * (points.size() - 1);
		indices[(points.size() - 1) * 6 + 4] = 1 + points.size() * 2;
		indices[(points.size() - 1) * 6 + 5] = 2 * (points.size() - 1) + points.size() * 2;
		/*
		 * } else { indices[(points.length-1)*6]=2*(points.length-1);
		 * indices[(points.length-1)*6+1]=1+points.length*2;
		 * indices[(points.length-1)*6+2]=1;
		 * indices[(points.length-1)*6+3]=2*(points.length-1);
		 * indices[(points.length-1)*6+4]=2*(points.length-1)+points.length*2;
		 * indices[(points.length-1)*6+5]=1+points.length*2; }
		 */

	}

	public Vertex3d[] getSideVertices() {
		return sideVertices;
	}

	public int[] getIndices() {
		return indices;
	}
}