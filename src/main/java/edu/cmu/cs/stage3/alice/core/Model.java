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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.FillingStyleProperty;
import edu.cmu.cs.stage3.alice.core.property.GeometryProperty;
import edu.cmu.cs.stage3.alice.core.property.Matrix33Property;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ShadingStyleProperty;
import edu.cmu.cs.stage3.alice.core.property.TextureMapProperty;
import edu.cmu.cs.stage3.alice.core.property.VisualizationProperty;
import edu.cmu.cs.stage3.math.Sphere;

public class Model extends Transformable {
	public final ColorProperty color = new ColorProperty(this, "color", edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);
	public final ColorProperty ambientColor = new ColorProperty(this, "ambientColor", null);
	public final NumberProperty opacity = new NumberProperty(this, "opacity", new Double(1));
	public final FillingStyleProperty fillingStyle = new FillingStyleProperty(this, "fillingStyle",
			edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID);
	public final ShadingStyleProperty shadingStyle = new ShadingStyleProperty(this, "shadingStyle",
			edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH);
	public final ColorProperty specularHighlightColor = new ColorProperty(this, "specularHighlightColor",
			edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK);
	public final NumberProperty specularHighlightExponent = new NumberProperty(this, "specularHighlightExponent",
			new Double(0));
	public final ColorProperty emissiveColor = new ColorProperty(this, "emissiveColor",
			edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK);
	public final TextureMapProperty diffuseColorMap = new TextureMapProperty(this, "diffuseColorMap", null);
	public final TextureMapProperty opacityMap = new TextureMapProperty(this, "opacityMap", null);
	public final TextureMapProperty emissiveColorMap = new TextureMapProperty(this, "emissiveColorMap", null);
	public final TextureMapProperty specularHighlightColorMap = new TextureMapProperty(this,
			"specularHighlightColorMap", null);
	public final TextureMapProperty bumpMap = new TextureMapProperty(this, "bumpMap", null);
	public final TextureMapProperty detailMap = new TextureMapProperty(this, "detailMap", null);
	public final TextureMapProperty interactionMap = new TextureMapProperty(this, "interactionMap", null);
	public final Matrix33Property visualScale = new Matrix33Property(this, "visualScale",
			edu.cmu.cs.stage3.math.Matrix33.IDENTITY);
	public final BooleanProperty isShowing = new BooleanProperty(this, "isShowing", Boolean.TRUE);
	public final ElementArrayProperty disabledAffectors = new ElementArrayProperty(this, "disabledAffectors", null,
			Affector[].class);
	public final GeometryProperty geometry = new GeometryProperty(this, "geometry", null);
	public final VisualizationProperty visualization = new VisualizationProperty(this, "visualization", null);

	private edu.cmu.cs.stage3.alice.scenegraph.Visual m_sgVisual;
	private edu.cmu.cs.stage3.alice.scenegraph.Appearance m_sgAppearance;

	@Override
	protected void internalRelease(final int pass) {
		switch (pass) {
		case 1:
			m_sgAppearance.setDiffuseColorMap(null);
			m_sgAppearance.setOpacityMap(null);
			m_sgAppearance.setEmissiveColorMap(null);
			m_sgAppearance.setSpecularHighlightColorMap(null);
			m_sgAppearance.setBumpMap(null);
			m_sgAppearance.setDetailMap(null);
			m_sgVisual.setFrontFacingAppearance(null);
			m_sgVisual.setGeometry(null);
			m_sgVisual.setParent(null);
			break;
		case 2:
			m_sgVisual.release();
			m_sgVisual = null;
			m_sgAppearance.release();
			m_sgAppearance = null;
			break;
		}
		super.internalRelease(pass);
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Visual getSceneGraphVisual() {
		return m_sgVisual;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Appearance getSceneGraphAppearance() {
		return m_sgAppearance;
	}

	public Model() {
		super();
		m_sgAppearance = new edu.cmu.cs.stage3.alice.scenegraph.Appearance();
		m_sgAppearance.setBonus(this);
		m_sgVisual = new edu.cmu.cs.stage3.alice.scenegraph.Visual();
		m_sgVisual.setParent(getSceneGraphTransformable());
		m_sgVisual.setFrontFacingAppearance(m_sgAppearance);
		m_sgVisual.setBonus(this);
		color.set(m_sgAppearance.getDiffuseColor());
		ambientColor.set(m_sgAppearance.getAmbientColor());
		opacity.set(new Double(m_sgAppearance.getOpacity()));
		fillingStyle.set(m_sgAppearance.getFillingStyle());
		shadingStyle.set(m_sgAppearance.getShadingStyle());
		specularHighlightColor.set(m_sgAppearance.getSpecularHighlightColor());
		specularHighlightExponent.set(new Double(m_sgAppearance.getSpecularHighlightExponent()));
		emissiveColor.set(m_sgAppearance.getEmissiveColor());
		visualScale.set(m_sgVisual.getScale());
		isShowing.set(new Boolean(m_sgVisual.getIsShowing()));
	}

	@Override
	protected void nameValueChanged(final String value) {
		super.nameValueChanged(value);
		if (value != null) {
			m_sgVisual.setName(value + ".m_sgVisual");
			m_sgAppearance.setName(value + ".m_sgAppearance");
		} else {
			m_sgVisual.setName(null);
			m_sgAppearance.setName(null);
		}
	}

	private static edu.cmu.cs.stage3.alice.scenegraph.TextureMap getSceneGraphTextureMap(final TextureMap textureMap) {
		if (textureMap != null) {
			return textureMap.getSceneGraphTextureMap();
		} else {
			return null;
		}
	}

	private static edu.cmu.cs.stage3.alice.scenegraph.Geometry getSceneGraphGeometry(final Geometry geometry) {
		if (geometry != null) {
			return geometry.getSceneGraphGeometry();
		} else {
			return null;
		}
	}

	@Override
	public void propertyCreated(final Property property) {
		if (property.getName().equals("color")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("ambientColor")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("opacity")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("fillingStyle")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("shadingStyle")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("specularHighlightColor")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("specularHighlightExponent")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("emissiveColor")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("diffuseColorMap")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("opacityMap")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("emissiveColorMap")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("specularHighlightColorMap")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("bumpMap")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("detailMap")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("interactionMap")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("visualScale")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("isShowing")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("disabledAffectors")) {
			property.setIsAcceptingOfHowMuch(true);
		} else if (property.getName().equals("geometry")) {
			property.setIsAcceptingOfHowMuch(true);
		} else {
			super.propertyCreated(property);
		}
	}

	@Override
	protected void propertyChanging(final edu.cmu.cs.stage3.alice.core.Property property, final Object value) {
		if (property == visualization) {
			final Visualization prev = visualization.getVisualizationValue();
			if (prev != null) {
				prev.unhook(this);
			}
		} else {
			super.propertyChanging(property, value);
		}
	}

	@Override
	protected void propertyChanged(final edu.cmu.cs.stage3.alice.core.Property property, final Object value) {
		if (property == color) {
			m_sgAppearance.setDiffuseColor((edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == ambientColor) {
			m_sgAppearance.setAmbientColor((edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == opacity) {
			m_sgAppearance.setOpacity(((Number) value).doubleValue());
		} else if (property == fillingStyle) {
			m_sgAppearance.setFillingStyle((edu.cmu.cs.stage3.alice.scenegraph.FillingStyle) value);
		} else if (property == shadingStyle) {
			m_sgAppearance.setShadingStyle((edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle) value);
		} else if (property == specularHighlightColor) {
			m_sgAppearance.setSpecularHighlightColor((edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == specularHighlightExponent) {
			m_sgAppearance.setSpecularHighlightExponent(((Number) value).doubleValue());
		} else if (property == emissiveColor) {
			m_sgAppearance.setEmissiveColor((edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == diffuseColorMap) {
			m_sgAppearance.setDiffuseColorMap(getSceneGraphTextureMap((TextureMap) value));
		} else if (property == opacityMap) {
			m_sgAppearance.setOpacityMap(getSceneGraphTextureMap((TextureMap) value));
		} else if (property == emissiveColorMap) {
			m_sgAppearance.setEmissiveColorMap(getSceneGraphTextureMap((TextureMap) value));
		} else if (property == specularHighlightColorMap) {
			m_sgAppearance.setSpecularHighlightColorMap(getSceneGraphTextureMap((TextureMap) value));
		} else if (property == bumpMap) {
			m_sgAppearance.setBumpMap(getSceneGraphTextureMap((TextureMap) value));
		} else if (property == detailMap) {
			m_sgAppearance.setDetailMap(getSceneGraphTextureMap((TextureMap) value));
		} else if (property == interactionMap) {
			// todo
		} else if (property == visualScale) {
			m_sgVisual.setScale((javax.vecmath.Matrix3d) value);
		} else if (property == isShowing) {
			m_sgVisual.setIsShowing(((Boolean) value).booleanValue());
		} else if (property == disabledAffectors) {
			if (value != null) {
				final Affector[] affectors = (Affector[]) value;
				final edu.cmu.cs.stage3.alice.scenegraph.Affector[] sgAffectors = new edu.cmu.cs.stage3.alice.scenegraph.Affector[affectors.length];
				for (int i = 0; i < sgAffectors.length; i++) {
					sgAffectors[i] = affectors[i].getSceneGraphAffector();
				}
				m_sgVisual.setDisabledAffectors(sgAffectors);
			} else {
				m_sgVisual.setDisabledAffectors(null);
			}
		} else if (property == geometry) {
			m_sgVisual.setGeometry(getSceneGraphGeometry((Geometry) value));
		} else {
			super.propertyChanged(property, value);
		}
	}

	protected void scaleVisualRightNow(final javax.vecmath.Vector3d scale, final ReferenceFrame asSeenBy) {
		final javax.vecmath.Matrix4d scaleMatrix = new javax.vecmath.Matrix4d(scale.x, 0, 0, 0, 0, scale.y, 0, 0, 0, 0,
				scale.z, 0, 0, 0, 0, 1);
		final edu.cmu.cs.stage3.math.Matrix44 m = calculateTransformation(scaleMatrix, asSeenBy);
		final javax.vecmath.Matrix3d visScale = visualScale.getMatrix3dValue();
		// todo?
		visualScale.set(edu.cmu.cs.stage3.math.MathUtilities.multiply(visScale, m.getAxes()));
	}

	@Override
	protected void updateBoundingSphere(final edu.cmu.cs.stage3.math.Sphere sphere, final ReferenceFrame asSeenBy,
			final edu.cmu.cs.stage3.util.HowMuch howMuch, final boolean ignoreHidden) {
		super.updateBoundingSphere(sphere, asSeenBy, howMuch, ignoreHidden);
		if (ignoreHidden && !isShowing.booleanValue()) {
			// pass
		} else {
			final edu.cmu.cs.stage3.math.Sphere localSphere = m_sgVisual.getBoundingSphere();
			if (localSphere != null) {
				localSphere.transform(getTransformation(asSeenBy));
				sphere.union(localSphere);
			}
		}
	}

	@Override
	protected void updateBoundingBox(final edu.cmu.cs.stage3.math.Box box, final ReferenceFrame asSeenBy,
			final edu.cmu.cs.stage3.util.HowMuch howMuch, final boolean ignoreHidden) {
		super.updateBoundingBox(box, asSeenBy, howMuch, ignoreHidden);
		if (ignoreHidden && !isShowing.booleanValue()) {
			// pass
		} else {
			final edu.cmu.cs.stage3.math.Box localBox = m_sgVisual.getBoundingBox();
			if (localBox != null) {
				localBox.transform(getTransformation(asSeenBy));
				box.union(localBox);
			}
		}
	}

	@Override
	protected void HACK_copyOverTextureMapReferences(final Element dst,
			final java.util.Dictionary<TextureMap, TextureMap> srcTextureMapToDstTextureMapMap) {
		super.HACK_copyOverTextureMapReferences(dst, srcTextureMapToDstTextureMapMap);
		if (dst instanceof Model) {
			TextureMap tm = diffuseColorMap.getTextureMapValue();
			if (tm != null) {
				tm = (TextureMap) srcTextureMapToDstTextureMapMap.get(tm);
			}
			((Model) dst).diffuseColorMap.set(tm);
		}
	}

	public static double getDistanceBetween(final Model a, final Model b) {
		final edu.cmu.cs.stage3.alice.scenegraph.Visual[] aSGVisuals = a.getAllSceneGraphVisuals();
		final edu.cmu.cs.stage3.alice.scenegraph.Visual[] bSGVisuals = b.getAllSceneGraphVisuals();
		final edu.cmu.cs.stage3.math.Sphere[] aSpheres = new edu.cmu.cs.stage3.math.Sphere[aSGVisuals.length];
		final edu.cmu.cs.stage3.math.Sphere[] bSpheres = new edu.cmu.cs.stage3.math.Sphere[bSGVisuals.length];
		for (int i = 0; i < aSGVisuals.length; i++) {
			aSpheres[i] = aSGVisuals[i].getBoundingSphere();
			if (aSpheres[i] != null) {
				aSpheres[i].transform(aSGVisuals[i].getAbsoluteTransformation());
			}
		}
		for (int i = 0; i < bSGVisuals.length; i++) {
			bSpheres[i] = bSGVisuals[i].getBoundingSphere();
			if (bSpheres[i] != null) {
				bSpheres[i].transform(bSGVisuals[i].getAbsoluteTransformation());
			}
		}
		double dMin = Double.MAX_VALUE;
		for (final Sphere aSphere : aSpheres) {
			if (aSphere != null && aSphere.getCenter() != null && aSphere.getRadius() > 0) {
				for (final Sphere bSphere : bSpheres) {
					if (bSphere != null && bSphere.getCenter() != null && bSphere.getRadius() > 0) {
						double d = edu.cmu.cs.stage3.math.MathUtilities
								.subtract(aSphere.getCenter(), bSphere.getCenter()).lengthSquared();
						d = Math.sqrt(d);
						d -= aSphere.getRadius();
						d -= bSphere.getRadius();
						dMin = Math.min(dMin, d);
					}
				}
			}
		}
		return dMin;
	}
}