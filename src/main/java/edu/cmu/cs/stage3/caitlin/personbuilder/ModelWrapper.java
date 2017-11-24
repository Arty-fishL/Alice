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

package edu.cmu.cs.stage3.caitlin.personbuilder;

import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Node;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;

public class ModelWrapper {
	protected edu.cmu.cs.stage3.alice.core.World miniWorld;
	protected edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera;
	protected edu.cmu.cs.stage3.alice.core.light.DirectionalLight directionalLight;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget;
	protected java.awt.event.MouseListener renderTargetMouseListener;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected RotateManipulator rtom;
	protected Model person = null;
	protected Model template = null;
	protected edu.cmu.cs.stage3.alice.core.CopyFactory personFactory = null;
	protected java.awt.Image[] textureLayers = new java.awt.Image[10]; // what
																		// is
																		// the
																		// right
																		// value
																		// for
																		// this?
	protected Hashtable<String, Model> partsTable = new Hashtable<>();
	protected java.net.URL url = null; // HACK

	protected Vector<String> propertyNameList = new Vector<String>();
	protected Vector<String> propertyValueList = new Vector<String>();
	protected Vector<String> propertyDescList = new Vector<String>();
	protected Vector<ItemChooser> itemChoosersWithAlts = new Vector<ItemChooser>();

	public ModelWrapper(final Node root) {
		worldInit();
		try {
			loadInitModel(root);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		makeNewPerson();
	}

	public void registerItemChooserWithAlt(final ItemChooser itemChooser) {
		itemChoosersWithAlts.addElement(itemChooser);
	}

	protected void replaceModel(final String modelName, final Model model) {
		if (model != null) {
			if (model.name.getStringValue().equals(modelName)) {
				if (person != null) {
					final edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion nameCriterion = new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion(
							modelName);
					final Element[] parts = person.search(nameCriterion,
							edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
					model.isFirstClass.set(false);
					if (parts.length > 0) {
						Element part = null;
						part = parts[0];
						if (part != null) {
							final edu.cmu.cs.stage3.math.Vector3 posToParent = ((Model) part)
									.getPosition((edu.cmu.cs.stage3.alice.core.ReferenceFrame) part.getParent());
							final edu.cmu.cs.stage3.math.Matrix33 orientToParent = ((Model) part).getOrientationAsAxes(
									(edu.cmu.cs.stage3.alice.core.ReferenceFrame) part.getParent());
							part.replaceWith(model);
							if (part instanceof Model) {
								model.vehicle.set(((Model) part).vehicle.get());
								((Model) part).vehicle.set(null);
								if (posToParent != null) {
									model.setPositionRightNow(posToParent,
											(edu.cmu.cs.stage3.alice.core.ReferenceFrame) model.getParent());
								}
								if (orientToParent != null) {
									model.setOrientationRightNow(orientToParent,
											(edu.cmu.cs.stage3.alice.core.ReferenceFrame) model.getParent());
								}

								final edu.cmu.cs.stage3.alice.core.TextureMap tMap = person.diffuseColorMap
										.getTextureMapValue();
								person.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
							}
						}

					} else {
						System.out.println(model.name.getStringValue() + " is not found");
					}
				}
			}
		}
	}

	public void switchToAltModel(final String modelName) {
		for (int i = 0; i < itemChoosersWithAlts.size(); i++) {
			final ItemChooser itemChooser = itemChoosersWithAlts.elementAt(i);
			final edu.cmu.cs.stage3.alice.core.Model model = itemChooser.getAltModel();
			replaceModel(modelName, model);

		}
	}

	public void switchToOrigModel(final String modelName) {
		for (int i = 0; i < itemChoosersWithAlts.size(); i++) {
			final ItemChooser itemChooser = itemChoosersWithAlts.elementAt(i);
			final edu.cmu.cs.stage3.alice.core.Model model = itemChooser.getOriginalModel();
			replaceModel(modelName, model);

		}
	}

	public void resetWorld() {
		partsTable = new Hashtable<>();
		textureLayers = new java.awt.Image[10];
		makeNewPerson();
	}

	protected void worldInit() {
		miniWorld = new edu.cmu.cs.stage3.alice.core.World();
		camera = new edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera();
		directionalLight = new edu.cmu.cs.stage3.alice.core.light.DirectionalLight();

		camera.vehicle.set(miniWorld);
		camera.setPositionRightNow(0.0, 1.5, 6.0);
		camera.verticalViewingAngle.set(new Double(Math.PI / 6.0));
		directionalLight.vehicle.set(camera);
		directionalLight.setOrientationRightNow(camera.getOrientationAsQuaternion());
		directionalLight.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.FORWARD, .15);
		directionalLight.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.LEFT, .075);
		directionalLight.color.set(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);

		final java.awt.Color dkBlue = new java.awt.Color(12, 36, 106);
		miniWorld.atmosphereColor.set(new edu.cmu.cs.stage3.alice.scenegraph.Color(dkBlue));
		miniWorld.ambientLightColor.set(edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY);
	}

	protected void makeNewPerson() {
		if (personFactory == null) {
			try {
				personFactory = template.createCopyFactory();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		try {
			person = (Model) personFactory.manufactureCopy(null);
			addModelToWorld(person, "none", null);
		} catch (final edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
			throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
		}
	}

	protected void loadInitModel(final Node root) {
		final Vector<URL> modelURLs = XMLDirectoryUtilities.getModelURLs(root);
		for (int i = 0; i < modelURLs.size(); i++) {
			url = modelURLs.elementAt(i);
			try {
				template = (Model) Element.load(url, null);
			} catch (final java.io.IOException ioe) {
				ioe.printStackTrace();
			} catch (final edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
				upre.printStackTrace();
			}
		}
	}

	/* Unused ??
	private void initializeModels(final Model part, final String parentName,
			final edu.cmu.cs.stage3.math.Vector3 position) {
		// check to see if anything should be parented to this
		final Model partsToAttach = (Model) partsTable.get(part.getKey());
		if (partsToAttach != null && partsToAttach.getParent() == null) {
			addChildToModel(part, partsToAttach, position);
		}
	}
	*/

	/* Unused ??
	private Element[] removeModelFromWorld(final Model model) {
		if (model != null) {
			final Element[] kids = model.getChildren();
			model.removeFromParent();
			model.vehicle.set(null);
			return kids;
		}
		return null;
	}
	*/

	/* Unused ??
	private void removeAllKids(final Model parent) {
		if (parent.getChildCount() > 0) {
			final Element[] oldKids = parent.getChildren();
			for (final Element oldKid : oldKids) {
				if (oldKid instanceof Model) {
					oldKid.removeFromParent();
					final Model oldModel = (Model) oldKid;
					oldModel.vehicle.set(null);
					parent.removeChild(oldModel);
				}
			}
		}
	}
	*/

	/* Unused ??
	private void addKidsToModel(final Model newParent, final Element[] kids) {
		// remove any old kids the newParent might already have
		removeAllKids(newParent);
		if (newParent != null && kids != null) {
			for (final Element kid : kids) {
				if (kid instanceof Model) {
					final Model kidModel = (Model) kid;
					addChildToModel(newParent, kidModel, null);
				}
			}
		}
	}
	*/

	/* Unused ??
	private void addChildToModel(final Model parent, final Model child, final edu.cmu.cs.stage3.math.Vector3 position) {
		parent.addChild(child);
		parent.parts.add(child);
		child.setParent(parent);
		child.isFirstClass.set(false);
		child.vehicle.set(parent);
		if (position != null) {
			child.setPositionRightNow(position, parent);
		}
	}
	*/

	private void addModelToWorld(final Model model, final String parent,
			final edu.cmu.cs.stage3.math.Vector3 position) {
		if (parent.equals("none")) {
			person = model;
			regenerateTexture();
			person.vehicle.set(miniWorld);
			camera.pointAtRightNow(person);
		}
	}

	public void removeModel() {
		if (person != null) {
			person.vehicle.set(null);
		}
	}

	private void renderInit() {
		authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
		renderTarget = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
		if (renderTarget != null) {
			renderTarget.addCamera(camera.getSceneGraphCamera());
			rtom = new RotateManipulator(renderTarget);
			rtom.setTransformableToRotate(person);
		} else {
			System.err.println("PersonBuilder unable to create renderTarget");
		}
	}

	public java.awt.Component getRenderPanel() {
		renderInit();
		return renderTarget.getAWTComponent();
	}

	public Model getModel() {
		return person;
		// if ((person != null) &&
		// (person.name.getStringValue().equals("NoName"))) {
		// return null;
		// } else
		// return person;
	}

	protected void regenerateTexture() {
		java.awt.image.BufferedImage finalTexture = null;
		java.awt.Graphics2D g2 = null;

		for (int i = 0; i < textureLayers.length; i++) {
			if (textureLayers[0] == null) {
				final java.awt.Image im = person.diffuseColorMap.getTextureMapValue().image.getImageValue();
				textureLayers[0] = im;
			}
			if (textureLayers[i] != null) {
				if (finalTexture == null) {
					finalTexture = new java.awt.image.BufferedImage(textureLayers[i].getHeight(null),
							textureLayers[i].getWidth(null), java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
					g2 = finalTexture.createGraphics();
				}
				g2.drawImage(textureLayers[i], 0, 0, null);
			}
		}

		if (finalTexture != null) {
			person.diffuseColorMap.getTextureMapValue().image.set(finalTexture);
			person.diffuseColorMap.getTextureMapValue().touchImage();
		}
	}

	public void addTexture(final java.awt.Image texture, final int level) {
		textureLayers[level] = texture;
		regenerateTexture();
	}

	public void clearLevel(final int level) {
		textureLayers[level] = null;
	}

	public void addModel(final Model modelToAdd, final String parentName,
			final edu.cmu.cs.stage3.math.Vector3 position) {
		if (person != null) {
			final edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion nameCriterion = new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion(
					parentName);
			final Element[] parents = person.search(nameCriterion,
					edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			if (parents.length > 0) {
				modelToAdd.setParent(parents[0]);
				((edu.cmu.cs.stage3.alice.core.Model) parents[0]).parts.add(modelToAdd);
				modelToAdd.vehicle.set(parents[0]);
				modelToAdd.isFirstClass.set(false);

				modelToAdd.setPositionRightNow(position, (edu.cmu.cs.stage3.alice.core.ReferenceFrame) parents[0]);
			}
		}
		final edu.cmu.cs.stage3.alice.core.TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
		modelToAdd.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
		person.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
	}

	public void removeModel(final String modelName) {
		if (person != null) {
			final edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion nameCriterion = new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion(
					modelName);
			final Element[] models = person.search(nameCriterion,
					edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			if (models.length > 0) {
				models[0].getParent().removeChild(models[0]);
				((edu.cmu.cs.stage3.alice.core.Model) models[0]).vehicle.set(null);
			}
		}
		final edu.cmu.cs.stage3.alice.core.TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
		person.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
	}

	/* Unused ??
	private void rootModelChanged() {
		regenerateTexture();

		for (int i = 0; i < propertyNameList.size(); i++) {
			final String propName = propertyNameList.elementAt(i);
			final String propValue = propertyValueList.elementAt(i);
			final String propDesc = propertyDescList.elementAt(i);

			setPropertyValue(propName, propValue, propDesc);
		}

	}
	*/

	public String getModelName() {
		if (person != null) {
			return person.name.getStringValue();
		} else {
			return "";
		}
	}

	public void setModel(final Model part, final String parentName) {
		if (parentName.equals("none") && person == null) {
		} else {
			if (person != null) {
				final edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion nameCriterion = new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion(
						parentName);
				final Element[] parents = person.search(nameCriterion,
						edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
				if (parents.length > 0) {
					final Element child = parents[0].getChildNamed(part.name.getStringValue());
					if (child != null) {
						part.isFirstClass.set(false);
						final edu.cmu.cs.stage3.math.Vector3 posToParent = ((Model) child)
								.getPosition((edu.cmu.cs.stage3.alice.core.ReferenceFrame) parents[0]);
						child.replaceWith(part);
						if (child instanceof Model) {
							part.vehicle.set(((Model) child).vehicle.get());
							((Model) child).vehicle.set(null);
							if (posToParent != null) {
								part.setPositionRightNow(posToParent,
										(edu.cmu.cs.stage3.alice.core.ReferenceFrame) part.getParent());
							}
						}
					}

				} else {
					System.out.println(part.name.getStringValue() + " is not found");
				}
			}
			final edu.cmu.cs.stage3.alice.core.TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
			person.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
		}
	}

	private void setPropertyValue(final String propertyName, final String propertyValue,
			final String propertyDescription) {
		final edu.cmu.cs.stage3.alice.core.Property property = person.getPropertyNamed(propertyName);
		if (property != null && property instanceof edu.cmu.cs.stage3.alice.core.property.StringProperty) {
			property.set(propertyValue);
		} else if (property != null && property instanceof edu.cmu.cs.stage3.alice.core.property.DictionaryProperty) {
			((edu.cmu.cs.stage3.alice.core.property.DictionaryProperty) property).put(propertyDescription,
					propertyValue);
		}
	}

	public void setProperty(final String propertyName, final String propertyValue, final String propertyDesc) {
		final int propertyIndex = propertyNameList.indexOf(propertyName);

		// we already have this property in the list
		if (propertyIndex != -1) {
			propertyValueList.setElementAt(propertyValue, propertyIndex);
			if (propertyDesc != null) {
				propertyDescList.setElementAt(propertyDesc, propertyIndex);
			} else {
				propertyDescList.setElementAt("", propertyIndex);
			}
		} else {
			propertyNameList.addElement(propertyName);
			propertyValueList.addElement(propertyValue);
			if (propertyDesc != null) {
				propertyDescList.addElement(propertyDesc);
			} else {
				propertyDescList.addElement("");
			}
		}

		setPropertyValue(propertyName, propertyValue, propertyDesc);
	}

	public void setColor(final java.awt.Color color) {
		final java.awt.image.BufferedImage baseColor = new java.awt.image.BufferedImage(512, 512,
				java.awt.image.BufferedImage.TYPE_INT_ARGB);
		final java.awt.Graphics2D g = (java.awt.Graphics2D) baseColor.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, 512, 512);
		addTexture(baseColor, 0);
		// person.color.set(new edu.cmu.cs.stage3.alice.scenegraph.Color(color),
		// edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
	}

}