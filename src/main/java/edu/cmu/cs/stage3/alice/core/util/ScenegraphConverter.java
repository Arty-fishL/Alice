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

package edu.cmu.cs.stage3.alice.core.util;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Light;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera;
import edu.cmu.cs.stage3.alice.core.camera.PerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.camera.ProjectionCamera;
import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.core.light.AmbientLight;
import edu.cmu.cs.stage3.alice.core.light.DirectionalLight;
import edu.cmu.cs.stage3.alice.core.light.PointLight;
import edu.cmu.cs.stage3.alice.core.light.SpotLight;

public class ScenegraphConverter {
	private static edu.cmu.cs.stage3.alice.scenegraph.Component getFirstChildOfClass(
			final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer, final Class<?> cls) {
		for (int i = 0; i < sgContainer.getChildCount(); i++) {
			final edu.cmu.cs.stage3.alice.scenegraph.Component sgChild = sgContainer.getChildAt(i);
			if (cls.isAssignableFrom(sgChild.getClass())) {
				return sgChild;
			}
		}
		return null;
	}

	private static edu.cmu.cs.stage3.alice.scenegraph.Light getFirstLightChild(
			final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer) {
		return (edu.cmu.cs.stage3.alice.scenegraph.Light) getFirstChildOfClass(sgContainer,
				edu.cmu.cs.stage3.alice.scenegraph.Light.class);
	}

	private static edu.cmu.cs.stage3.alice.scenegraph.AmbientLight getFirstAmbientLightChild(
			final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer) {
		return (edu.cmu.cs.stage3.alice.scenegraph.AmbientLight) getFirstChildOfClass(sgContainer,
				edu.cmu.cs.stage3.alice.scenegraph.AmbientLight.class);
	}

	private static edu.cmu.cs.stage3.alice.scenegraph.Camera getFirstCameraChild(
			final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer) {
		return (edu.cmu.cs.stage3.alice.scenegraph.Camera) getFirstChildOfClass(sgContainer,
				edu.cmu.cs.stage3.alice.scenegraph.Camera.class);
	}

	private static edu.cmu.cs.stage3.alice.scenegraph.Visual getFirstVisualChild(
			final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer) {
		return (edu.cmu.cs.stage3.alice.scenegraph.Visual) getFirstChildOfClass(sgContainer,
				edu.cmu.cs.stage3.alice.scenegraph.Visual.class);
	}

	private static Element internalConvert(final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer,
			final int id) {
		Element element = null;
		if (sgContainer instanceof edu.cmu.cs.stage3.alice.scenegraph.Scene) {
			final edu.cmu.cs.stage3.alice.scenegraph.Scene sgScene = (edu.cmu.cs.stage3.alice.scenegraph.Scene) sgContainer;
			final edu.cmu.cs.stage3.alice.scenegraph.AmbientLight sgAmbientLight = getFirstAmbientLightChild(sgScene);
			final edu.cmu.cs.stage3.alice.scenegraph.Background sgBackground = sgScene.getBackground();
			final World world = new World();
			if (sgBackground != null) {
				world.atmosphereColor.set(sgBackground.getColor());
			}
			if (sgAmbientLight != null) {
				world.ambientLightColor.set(sgAmbientLight.getColor());
			}
			element = world;
		} else if (sgContainer instanceof edu.cmu.cs.stage3.alice.scenegraph.Transformable) {
			final edu.cmu.cs.stage3.alice.scenegraph.Transformable sgTransformable = (edu.cmu.cs.stage3.alice.scenegraph.Transformable) sgContainer;
			final edu.cmu.cs.stage3.alice.scenegraph.Light sgLight = getFirstLightChild(sgTransformable);
			final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = getFirstCameraChild(sgTransformable);
			final edu.cmu.cs.stage3.alice.scenegraph.Visual sgVisual = getFirstVisualChild(sgTransformable);
			Model model = null;
			if (sgLight != null) {
				Light light = null;
				if (sgLight instanceof edu.cmu.cs.stage3.alice.scenegraph.AmbientLight) {
					final AmbientLight ambientLight = new AmbientLight();
					light = ambientLight;
				} else if (sgLight instanceof edu.cmu.cs.stage3.alice.scenegraph.DirectionalLight) {
					final DirectionalLight directionalLight = new DirectionalLight();
					light = directionalLight;
				} else if (sgLight instanceof edu.cmu.cs.stage3.alice.scenegraph.PointLight) {
					PointLight pointLight = null;
					if (sgLight instanceof edu.cmu.cs.stage3.alice.scenegraph.SpotLight) {
						final SpotLight spotLight = new SpotLight();
						pointLight = spotLight;
					} else {
						pointLight = new PointLight();
					}
					light = pointLight;
				}
				model = light;
				sgLight.setBonus(model);
			} else if (sgCamera != null) {
				Camera camera = null;
				if (sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera) {
					final SymmetricPerspectiveCamera symmetricPerspectiveCamera = new SymmetricPerspectiveCamera();
					camera = symmetricPerspectiveCamera;
				} else if (sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera) {
					final PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
					camera = perspectiveCamera;
				} else if (sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera) {
					final OrthographicCamera orthographicCamera = new OrthographicCamera();
					camera = orthographicCamera;
				} else if (sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera) {
					final ProjectionCamera projectionCamera = new ProjectionCamera();
					camera = projectionCamera;
				}
				model = camera;
				sgCamera.setBonus(model);
			} else {
				model = new Model();
			}
			if (sgVisual != null) {
				sgVisual.setBonus(model);
				final edu.cmu.cs.stage3.alice.scenegraph.Appearance sgAppearance = sgVisual.getFrontFacingAppearance();
				final edu.cmu.cs.stage3.alice.scenegraph.Geometry sgGeometry = sgVisual.getGeometry();
				if (sgAppearance != null) {
					final edu.cmu.cs.stage3.alice.scenegraph.TextureMap sgTextureMap = sgAppearance
							.getDiffuseColorMap();
					if (sgTextureMap != null) {
						final TextureMap diffuseColorMap = new TextureMap();
						diffuseColorMap.setParent(model);
						diffuseColorMap.image.set(sgTextureMap.getImage());
						diffuseColorMap.format.set(new Integer(sgTextureMap.getFormat()));
						model.diffuseColorMap.set(diffuseColorMap);
						model.textureMaps.add(diffuseColorMap);
					}
					model.color.set(sgAppearance.getDiffuseColor());
					model.opacity.set(new Double(sgAppearance.getOpacity()));
				}
				if (sgGeometry instanceof edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray) {
					final edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray sgITA = (edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray) sgGeometry;
					final IndexedTriangleArray ita = new IndexedTriangleArray();
					ita.setParent(model);
					ita.vertices.set(sgITA.getVertices());
					ita.indices.set(sgITA.getIndices());
					model.geometry.set(ita);
					model.geometries.add(ita);
				}
			}
			model.setLocalTransformationRightNow(sgTransformable.getLocalTransformation());
			element = model;
		}
		for (int i = 0; i < sgContainer.getChildCount(); i++) {
			final edu.cmu.cs.stage3.alice.scenegraph.Component sgChild = sgContainer.getChildAt(i);
			if (sgChild instanceof edu.cmu.cs.stage3.alice.scenegraph.Transformable) {
				final Model child = (Model) internalConvert((edu.cmu.cs.stage3.alice.scenegraph.Transformable) sgChild,
						i);
				child.setParent(element);
				child.vehicle.set(element);
				if (child.name.getStringValue() == null) {
					child.name.set("yet to be named part " + i);
				}
				if (element instanceof World) {
					((World) element).sandboxes.add(child);
				} else {
					((Model) element).parts.add(child);
				}
			}
		}
		String name = sgContainer.getName();
		if (name != null) {
			final int i = name.indexOf('.');
			if (i != -1) {
				name = name.substring(0, i);
			}
			element.name.set(name);
		}
		return element;
	}

	public static Element convert(final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer) {
		final Element e = internalConvert(sgContainer, 0);
		if (e instanceof Model) {
			((Model) e).isFirstClass.set(Boolean.TRUE);
		}
		if (e.name.getStringValue() == null) {
			e.name.set("yet to be named");
		}
		return e;
	}
	/*
	 * public static void main( String[] args ) { try {
	 * edu.cmu.cs.stage3.alice.scenegraph.Component sgSrc =
	 * edu.cmu.cs.stage3.alice.scenegraph.io.XML.load( new java.io.File( args[ 0
	 * ] ) ); Element dst = convert(
	 * (edu.cmu.cs.stage3.alice.scenegraph.Container)sgSrc ); dst.store( new
	 * java.io.File( args[ 1 ] ) ); } catch( java.io.IOException ioe ) {
	 * ioe.printStackTrace(); } System.exit( 0 ); }
	 */
}
