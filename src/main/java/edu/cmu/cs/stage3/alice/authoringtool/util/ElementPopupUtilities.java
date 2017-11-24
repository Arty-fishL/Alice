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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.util.ListIterator;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class ElementPopupUtilities {
	// preferences
	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration
			.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.class.getPackage());

	protected static Class<?>[] elementPopupRunnableParams = new Class[] { edu.cmu.cs.stage3.alice.core.Element.class };
	private static Runnable emptyRunnable = new Runnable() {
		@Override
		public void run() {
		}
	};

	public static void createAndShowElementPopupMenu(final edu.cmu.cs.stage3.alice.core.Element element,
			final java.util.Vector<?> structure, final java.awt.Component component, final int x, final int y) {
		final javax.swing.JPopupMenu popup = makeElementPopupMenu(element, structure);
		popup.show(component, x, y);
		PopupMenuUtilities.ensurePopupIsOnScreen(popup);
	}

	/**
	 * @deprecated use makeElementPopupMenu
	 */
	@Deprecated
	public static javax.swing.JPopupMenu makeElementPopup(final edu.cmu.cs.stage3.alice.core.Element element,
			final java.util.Vector<Object> structure) {
		return makeElementPopupMenu(element, structure);
	}

	public static javax.swing.JPopupMenu makeElementPopupMenu(final edu.cmu.cs.stage3.alice.core.Element element,
			final java.util.Vector<?> structure) {
		if (element != null && structure != null) {
			final Object[] initArgs = new Object[] { element };
			substituteRunnables(initArgs, structure);
			return PopupMenuUtilities.makePopupMenu(structure);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static void substituteRunnables(final Object[] initArgs, final java.util.Vector<?> structure) {
		for (final java.util.ListIterator<Object> iter = (ListIterator<Object>) structure.listIterator(); iter.hasNext();) {
			final Object o = iter.next();
			if (o instanceof Class && ElementPopupRunnable.class.isAssignableFrom((Class<?>) o)) {
				try {
					final ElementPopupRunnable r = (ElementPopupRunnable) ((Class<?>) o)
							.getConstructor(elementPopupRunnableParams).newInstance(initArgs);
					final edu.cmu.cs.stage3.util.StringObjectPair newPair = new edu.cmu.cs.stage3.util.StringObjectPair(
							r.getDefaultLabel(), r);
					iter.set(newPair);
				} catch (final NoSuchMethodException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error building popup.", e);
				} catch (final IllegalAccessException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error building popup.", e);
				} catch (final InstantiationException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error building popup.", e);
				} catch (final java.lang.reflect.InvocationTargetException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error building popup.", e);
				}
			} else if (o instanceof ElementPopupRunnable) {
				final ElementPopupRunnable r = (ElementPopupRunnable) o;
				final edu.cmu.cs.stage3.util.StringObjectPair newPair = new edu.cmu.cs.stage3.util.StringObjectPair(
						r.getDefaultLabel(), r);
				iter.set(newPair);
			} else if (o instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
				final edu.cmu.cs.stage3.util.StringObjectPair pair = (edu.cmu.cs.stage3.util.StringObjectPair) o;
				if (pair.getObject() instanceof Class
						&& ElementPopupRunnable.class.isAssignableFrom((Class<?>) pair.getObject())) {
					try {
						final edu.cmu.cs.stage3.util.StringObjectPair newPair = new edu.cmu.cs.stage3.util.StringObjectPair(
								pair.getString(), ((Class<?>) pair.getObject()).getConstructor(elementPopupRunnableParams)
										.newInstance(initArgs));
						iter.set(newPair);
						// pair.setObject(
						// ((Class)pair.getObject()).getConstructor(
						// elementPopupRunnableParams ).newInstance( initArgs )
						// );
					} catch (final NoSuchMethodException e) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error building popup.", e);
					} catch (final IllegalAccessException e) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error building popup.", e);
					} catch (final InstantiationException e) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error building popup.", e);
					} catch (final java.lang.reflect.InvocationTargetException e) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error building popup.", e);
					}
				} else if (pair.getObject() instanceof java.util.Vector) {
					substituteRunnables(initArgs, (java.util.Vector<Object>) pair.getObject());
				}
			}
		}
	}

	public static java.util.Vector<StringObjectPair> makeCoerceToStructure(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null && element.isCoercionSupported()) {
			final java.util.Vector<StringObjectPair> structure = new java.util.Vector<StringObjectPair>();
			final java.util.Vector<StringObjectPair> subStructure = new java.util.Vector<StringObjectPair>();

			final Class<?>[] classes = element.getSupportedCoercionClasses();
			if (classes != null) {
				for (final Class<?> c : classes) {
					if (element instanceof edu.cmu.cs.stage3.alice.core.response.TurnAnimation) {
						final edu.cmu.cs.stage3.alice.core.response.TurnAnimation turnAnimation = (edu.cmu.cs.stage3.alice.core.response.TurnAnimation) element;
						if (turnAnimation.direction.get() == edu.cmu.cs.stage3.alice.core.Direction.FORWARD
								|| turnAnimation.direction.get() == edu.cmu.cs.stage3.alice.core.Direction.BACKWARD) {
							if (edu.cmu.cs.stage3.alice.core.response.RollAnimation.class.isAssignableFrom(c)) {
								continue;
							}
						}
					}
					final String repr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(c);
					final Runnable runnable = new Runnable() {
						@Override
						public void run() {
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack()
									.startCompound();
							element.coerceTo(c);
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack()
									.stopCompound();
						}
					};
					subStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair(repr, runnable));
				}
				if (subStructure.size() > 0) {
					structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("change to", subStructure));
					return structure;
				} else {
					return null;
				}
			}
		}

		return null;
	}

	public static java.util.Vector<Object> getDefaultStructure(final edu.cmu.cs.stage3.alice.core.Element element) {
		return getDefaultStructure(element, true, null, null, null);
	}

	public static java.util.Vector<Object> getDefaultStructure(final edu.cmu.cs.stage3.alice.core.Element element,
			final boolean elementEnabled, final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool,
			final javax.swing.JTree jtree, final javax.swing.tree.TreePath treePath) {
		if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
			return getDefaultResponseStructure((edu.cmu.cs.stage3.alice.core.Response) element);
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Question) {
			return getDefaultQuestionStructure((edu.cmu.cs.stage3.alice.core.Question) element);
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.characterCriterion.accept(element)) {
			return getDefaultCharacterStructure(element, elementEnabled, authoringTool, jtree, treePath);
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.World) {
			return getDefaultWorldStructure((edu.cmu.cs.stage3.alice.core.World) element);
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Group) {
			return getDefaultGroupStructure((edu.cmu.cs.stage3.alice.core.Group) element, jtree, treePath);
		} else {
			return getDefaultElementStructure(element, jtree, treePath);
		}
	}

	public static java.util.Vector<Object> getDefaultCharacterStructure(final edu.cmu.cs.stage3.alice.core.Element element,
			final boolean elementEnabled, final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool,
			final javax.swing.JTree jtree, final javax.swing.tree.TreePath treePath) {
		final java.util.Vector<Object> popupStructure = new java.util.Vector<Object>();
		popupStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair(
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(element), null));
		popupStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair("separator", javax.swing.JSeparator.class));
		// popupStructure.add( MakeCopyRunnable.class );
		// popupStructure.add( MakeSharedCopyRunnable.class );
		if (elementEnabled) {
			popupStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair("methods",
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
							.makeDefaultOneShotStructure(element)));
		}
		if (jtree != null && treePath != null) {
			final Runnable renameRunnable = new RenameRunnable(element, jtree, treePath);
			popupStructure.add(renameRunnable);
		}
		if (element instanceof edu.cmu.cs.stage3.alice.core.Sandbox
				&& authoringToolConfig.getValue("enableScripting").equalsIgnoreCase("true")) {
			popupStructure
					.add(edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.EditScriptRunnable.class);
		}
		// popupStructure.add( PrintStatisticsRunnable.class );

		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.characterCriterion.accept(element)) {
			if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				if (!(element instanceof edu.cmu.cs.stage3.alice.core.Camera)) {
					popupStructure.add(GetAGoodLookAtRunnable.class);
					popupStructure.add(StorePoseRunnable.class);
				}
			}
			// popupStructure.add( EditCharacterRunnable.class );
			popupStructure.add(DeleteRunnable.class);
			popupStructure.add(SaveCharacterRunnable.class);
			// if( authoringTool != null ) {
			// Runnable setScopeRunnable = new SetElementScopeRunnable( element,
			// authoringTool );
			// popupStructure.add( setScopeRunnable );
			// }
		} else {
			popupStructure.add(DeleteRunnable.class);
		}
		// TODO: get this in for BVW
		// java.util.Vector copyOverStructure = new java.util.Vector();
		// copyOverStructure.add( CopyOverFromCharacterLoadRunnable.class );
		// copyOverStructure.add( CopyOverFromImportLoadRunnable.class );
		// popupStructure.add( new edu.cmu.cs.stage3.util.StringObjectPair(
		// "copy over", copyOverStructure ) );

		return popupStructure;
	}

	public static java.util.Vector<Object> getDefaultWorldStructure(final edu.cmu.cs.stage3.alice.core.World world) {
		final java.util.Vector<Object> popupStructure = new java.util.Vector<Object>();
		popupStructure.add(EditScriptRunnable.class);
		return popupStructure;
	}

	public static java.util.Vector<Object> getDefaultResponseStructure(final edu.cmu.cs.stage3.alice.core.Response response) {
		final java.util.Vector<Object> structure = new java.util.Vector<Object>();
		structure.add(MakeCopyRunnable.class);
		structure.add(DeleteRunnable.class);
		structure.add(ToggleCommentingRunnable.class);
		final java.util.Vector<StringObjectPair> coerceToStructure = makeCoerceToStructure(response);
		if (coerceToStructure != null) {
			structure.addAll(coerceToStructure);
		}

		return structure;
	}

	public static java.util.Vector<Object> getDefaultQuestionStructure(final edu.cmu.cs.stage3.alice.core.Question question) {
		final java.util.Vector<Object> structure = new java.util.Vector<Object>();
		// structure.add( MakeCopyRunnable.class );
		structure.add(DeleteRunnable.class);
		// structure.add( ToggleCommentingRunnable.class );
		final java.util.Vector<StringObjectPair> coerceToStructure = makeCoerceToStructure(question);
		if (coerceToStructure != null) {
			structure.addAll(coerceToStructure);
		}

		return structure;
	}

	public static java.util.Vector<Object> getDefaultGroupStructure(final edu.cmu.cs.stage3.alice.core.Group group,
			final javax.swing.JTree jtree, final javax.swing.tree.TreePath treePath) {
		final java.util.Vector<Object> structure = new java.util.Vector<Object>();

		structure.add(new edu.cmu.cs.stage3.util.StringObjectPair(
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(group), emptyRunnable));
		structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("separator", javax.swing.JSeparator.class));
		structure.add(SortGroupAlphabeticallyRunnable.class);
		if (jtree != null && treePath != null) {
			final Runnable renameRunnable = new RenameRunnable(group, jtree, treePath);
			structure.add(renameRunnable);
		}
		structure.add(DeleteRunnable.class);

		return structure;
	}

	public static java.util.Vector<Object> getDefaultElementStructure(final edu.cmu.cs.stage3.alice.core.Element element,
			final javax.swing.JTree jtree, final javax.swing.tree.TreePath treePath) {
		final java.util.Vector<Object> structure = new java.util.Vector<Object>();
		if (jtree != null && treePath != null) {
			final Runnable renameRunnable = new RenameRunnable(element, jtree, treePath);
			structure.add(renameRunnable);
		}
		structure.add(DeleteRunnable.class);

		return structure;
	}

	public static abstract class ElementPopupRunnable implements Runnable {
		protected edu.cmu.cs.stage3.alice.core.Element element;

		protected ElementPopupRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			this.element = element;
		}

		public edu.cmu.cs.stage3.alice.core.Element getElement() {
			return element;
		}

		public abstract String getDefaultLabel();
	}

	// TODO: there are issues with Models whose world and scenegraph trees don't
	// match
	public static class DeleteRunnable extends ElementPopupRunnable {
		public final static edu.cmu.cs.stage3.util.Criterion namedHeadCriterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					if ("head"
							.equalsIgnoreCase(((edu.cmu.cs.stage3.alice.core.Transformable) o).name.getStringValue())) {
						return true;
					}
				}
				return false;
			}
		};
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public DeleteRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			super(element);
			authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
		}

		public DeleteRunnable(final edu.cmu.cs.stage3.alice.core.Element element,
				final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
			super(element);
			this.authoringTool = authoringTool;
		}

		@Override
		public String getDefaultLabel() {
			return "delete";
		}

		@Override
		public void run() {
			if (element instanceof edu.cmu.cs.stage3.alice.core.Camera) {
				final String message = "The Camera is a critical part of the World.  Very bad things can happen if you delete the Camera.\nAre you sure you want to delete it?";
				final int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(message, "Delete Camera?",
						javax.swing.JOptionPane.YES_NO_OPTION);
				if (result != javax.swing.JOptionPane.YES_OPTION) {
					return;
				}
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.light.DirectionalLight) {
				if (element.getRoot()
						.getDescendants(edu.cmu.cs.stage3.alice.core.light.DirectionalLight.class).length == 1) {
					final String message = "You are about to delete the last directional light in the World.  If you do this, everything will probably become very dark.\nAre you sure you want to delete it?";
					final int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(message, "Delete Light?",
							javax.swing.JOptionPane.YES_NO_OPTION);
					if (result != javax.swing.JOptionPane.YES_OPTION) {
						return;
					}
				}
			}

			edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = element.getRoot()
					.getPropertyReferencesTo(element, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true,
							true);

			if (references.length > 0) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.garbageCollectIfPossible(references);
				references = element.getRoot().getPropertyReferencesTo(element,
						edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
			}

			if (references.length > 0) {
				for (final PropertyReference reference : references) {
					final edu.cmu.cs.stage3.alice.core.Element refReferenceI = reference.getReference();
					final edu.cmu.cs.stage3.alice.core.Property refPropertyI = reference.getProperty();
					final edu.cmu.cs.stage3.alice.core.Element refOwnerI = refPropertyI.getOwner();
					if (reference.getProperty().isAlsoKnownAs(edu.cmu.cs.stage3.alice.core.Sandbox.class,
							"textureMaps")) {
						if (refOwnerI instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
							refReferenceI.setParent(refOwnerI);
						}
					}
					// if( references[ i ].getProperty().isAlsoKnownAs(
					// edu.cmu.cs.stage3.alice.core.Sandbox.class, "geometries"
					// ) ) {
					// System.err.println( "geometries: " + references[ i ] );
					// }
					if (reference.getProperty().isAlsoKnownAs(edu.cmu.cs.stage3.alice.core.Model.class, "geometry")) {
						if (refOwnerI instanceof edu.cmu.cs.stage3.alice.core.Model) {
							refReferenceI.setParent(refOwnerI);
						}
					}
				}
				references = element.getRoot().getPropertyReferencesTo(element,
						edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
			}
			if (references.length > 0) {
				edu.cmu.cs.stage3.alice.authoringtool.dialog.DeleteContentPane.showDeleteDialog(this, authoringTool);

				// String text =
				// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(
				// element, true ) +
				// " cannot be deleted, because the following references are
				// being made to it or its parts:";
				// javax.swing.JList list = new javax.swing.JList( references );
				// list.setCellRenderer( new
				// edu.cmu.cs.stage3.alice.authoringtool.util.PropertyReferenceListCellRenderer()
				// );
				// javax.swing.JScrollPane scrollPane = new
				// javax.swing.JScrollPane( list );
				// list.setVisibleRowCount( 4 );
				// Object[] message = new Object[] { text, scrollPane };
				// edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
				// message, "Cannot delete " +
				// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(
				// element, true ), javax.swing.JOptionPane.ERROR_MESSAGE );
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().startCompound();

				if (element instanceof edu.cmu.cs.stage3.alice.core.Group) {
					for (int i = 0; i < element.getChildCount(); i++) {
						if (element.getChildAt(i) instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
							if (element.getChildAt(i) instanceof edu.cmu.cs.stage3.alice.core.Model) {
								final edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model) element
										.getChildAt(i);
								if (model.vehicle.get() instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame) {
									final edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = calculateAffectedProperties(
											model);
									authoringTool.performOneShot(createDestroyResponse(model),
											createDestroyUndoResponse(model), affectedProperties);
								} else {
									model.vehicle.set(null);
								}
							} else {
								((edu.cmu.cs.stage3.alice.core.Transformable) element).vehicle.set(null);
							}
						}
					}
				}

				if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					if (element instanceof edu.cmu.cs.stage3.alice.core.Model) {
						final edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model) element;
						if (model.vehicle.get() instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame) {
							final edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = calculateAffectedProperties(
									model);
							authoringTool.performOneShot(createDestroyResponse(model), createDestroyUndoResponse(model),
									affectedProperties);
						} else {
							model.vehicle.set(null);
						}
					} else {
						((edu.cmu.cs.stage3.alice.core.Transformable) element).vehicle.set(null);
					}
				}
				final edu.cmu.cs.stage3.alice.core.Element parent = element.getParent();
				if (parent != null) {
					// is this too liberal?
					final edu.cmu.cs.stage3.alice.core.Property[] properties = parent.getProperties();
					for (final Property propertie : properties) {
						if (propertie.get() == element) {
							propertie.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
									.getDefaultValueForClass(propertie.getValueClass()));
						} else if (propertie instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
							final edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) propertie;
							int j = 0;
							while (j < oap.size()) {
								if (oap.get(j) == element) {
									oap.remove(j);
								} else {
									j++;
								}
							}
						}
					}
					// element.removeFromParent();
					parent.removeChild(element);
				}

				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().stopCompound();
			}
		}

		protected edu.cmu.cs.stage3.alice.core.Response createDestroyResponse(
				final edu.cmu.cs.stage3.alice.core.Model model) {
			final edu.cmu.cs.stage3.alice.core.response.TurnAnimation turnAnimation = new edu.cmu.cs.stage3.alice.core.response.TurnAnimation();
			turnAnimation.subject.set(model);
			turnAnimation.direction.set(edu.cmu.cs.stage3.alice.core.Direction.LEFT);
			turnAnimation.amount.set(new Double(10.0));
			turnAnimation.style
					.set(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY);
			final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation opacityAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			opacityAnimation.element.set(model);
			opacityAnimation.propertyName.set("opacity");
			opacityAnimation.value.set(new Double(0.0));
			opacityAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			opacityAnimation.style
					.set(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY);
			final edu.cmu.cs.stage3.alice.core.response.DoTogether doTogether = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
			doTogether.componentResponses.add(turnAnimation);
			doTogether.componentResponses.add(opacityAnimation);
			final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation vehicleAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			vehicleAnimation.element.set(model);
			vehicleAnimation.propertyName.set("vehicle");
			vehicleAnimation.value.set(null);
			vehicleAnimation.duration.set(new Double(0.0));
			vehicleAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
			final edu.cmu.cs.stage3.alice.core.response.Wait wait = new edu.cmu.cs.stage3.alice.core.response.Wait();
			wait.duration.set(new Double(.2));
			final edu.cmu.cs.stage3.alice.core.response.DoInOrder doInOrder = new edu.cmu.cs.stage3.alice.core.response.DoInOrder();
			doInOrder.componentResponses.add(wait);
			final edu.cmu.cs.stage3.alice.core.Element[] heads = model.search(namedHeadCriterion);
			if (heads != null && heads.length > 0) {
				final edu.cmu.cs.stage3.alice.core.Element head = heads[0];
				if (head instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					final edu.cmu.cs.stage3.alice.core.Camera camera = authoringTool.getCurrentCamera();
					if (camera != null) {
						final edu.cmu.cs.stage3.alice.core.response.PointAtAnimation pointAt = new edu.cmu.cs.stage3.alice.core.response.PointAtAnimation();
						pointAt.subject.set(head);
						pointAt.target.set(camera);
						pointAt.duration.set(new Double(.5));
						doInOrder.componentResponses.add(pointAt);
						final edu.cmu.cs.stage3.alice.core.response.Wait wait2 = new edu.cmu.cs.stage3.alice.core.response.Wait();
						wait2.duration.set(new Double(.4));
						doInOrder.componentResponses.add(wait2);
					}
				}
			}
			doInOrder.componentResponses.add(doTogether);
			doInOrder.componentResponses.add(vehicleAnimation);
			return doInOrder;
		}

		protected edu.cmu.cs.stage3.alice.core.Response createDestroyUndoResponse(
				final edu.cmu.cs.stage3.alice.core.Model model) {
			final edu.cmu.cs.stage3.alice.core.response.TurnAnimation turnAnimation = new edu.cmu.cs.stage3.alice.core.response.TurnAnimation();
			turnAnimation.subject.set(model);
			turnAnimation.direction.set(edu.cmu.cs.stage3.alice.core.Direction.RIGHT);
			turnAnimation.amount.set(new Double(5.0));
			turnAnimation.style
					.set(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY);
			final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation opacityAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			opacityAnimation.element.set(model);
			opacityAnimation.propertyName.set("opacity");
			opacityAnimation.value.set(model.opacity.get());
			opacityAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS); // won't
																										// work
																										// correctly
																										// if
																										// children
																										// have
																										// different
																										// opacities
			opacityAnimation.style
					.set(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY);
			opacityAnimation.duration.set(new Double(.8));
			final edu.cmu.cs.stage3.alice.core.response.DoTogether doTogether = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
			doTogether.componentResponses.add(turnAnimation);
			doTogether.componentResponses.add(opacityAnimation);
			final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation vehicleAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			vehicleAnimation.element.set(model);
			vehicleAnimation.propertyName.set("vehicle");
			vehicleAnimation.value.set(model.vehicle.get());
			vehicleAnimation.duration.set(new Double(0.0));
			vehicleAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
			final edu.cmu.cs.stage3.alice.core.response.DoInOrder doInOrder = new edu.cmu.cs.stage3.alice.core.response.DoInOrder();
			doInOrder.componentResponses.add(vehicleAnimation);
			doInOrder.componentResponses.add(doTogether);
			final edu.cmu.cs.stage3.alice.core.Element[] heads = model.search(namedHeadCriterion);
			if (heads != null && heads.length > 0) {
				final edu.cmu.cs.stage3.alice.core.Element head = heads[0];
				if (head instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					final edu.cmu.cs.stage3.alice.core.response.Wait wait2 = new edu.cmu.cs.stage3.alice.core.response.Wait();
					wait2.duration.set(new Double(.4));
					doInOrder.componentResponses.add(wait2);
					final edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation povAnimation = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
					povAnimation.subject.set(head);
					povAnimation.pointOfView.set(((edu.cmu.cs.stage3.alice.core.Transformable) head).getPointOfView());
					povAnimation.duration.set(new Double(.5));
					doInOrder.componentResponses.add(povAnimation);
				}
			}
			return doInOrder;
		}

		protected edu.cmu.cs.stage3.alice.core.Property[] calculateAffectedProperties(
				final edu.cmu.cs.stage3.alice.core.Model model) {
			final java.util.Vector<ObjectProperty> properties = new java.util.Vector<ObjectProperty>();
			properties.add(model.localTransformation);
			properties.add(model.vehicle);
			final edu.cmu.cs.stage3.alice.core.Element[] descendants = model.getDescendants(); // TODO:
																								// getDescendants(
																								// HowMuch
																								// )
			for (final Element descendant : descendants) {
				if (descendant instanceof edu.cmu.cs.stage3.alice.core.Model) {
					properties.add(((edu.cmu.cs.stage3.alice.core.Model) descendant).opacity);
					properties.add(((edu.cmu.cs.stage3.alice.core.Model) descendant).localTransformation); // HACK:
																											// for
																											// handling
																											// the
																											// head-look;
																											// should
																											// specific
																											// to
																											// just
																											// the
																											// head
				}
			}
			return properties
					.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]);
		}
	}

	public static class RenameRunnable extends ElementPopupRunnable {
		private final javax.swing.JTree jtree;
		private final javax.swing.tree.TreePath treePath;

		public RenameRunnable(final edu.cmu.cs.stage3.alice.core.Element element, final javax.swing.JTree jtree,
				final javax.swing.tree.TreePath treePath) {
			super(element);
			this.jtree = jtree;
			this.treePath = treePath;
		}

		@Override
		public String getDefaultLabel() {
			return "rename";
		}

		@Override
		public void run() {
			jtree.startEditingAtPath(treePath);
		}
	}

	public static class MakeCopyRunnable extends ElementPopupRunnable {
		public MakeCopyRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			super(element);
		}

		@Override
		public String getDefaultLabel() {
			return "make copy";
		}

		@Override
		public void run() {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().startCompound();

			final String name = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
					.getNameForNewChild(element.name.getStringValue(), element.getParent());

			// should createCopyNamed handle this?
			if (element.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
				final int index = ((edu.cmu.cs.stage3.alice.core.response.CompositeResponse) element
						.getParent()).componentResponses.indexOf(element);
				final edu.cmu.cs.stage3.alice.core.Element copy = element.HACK_createCopy(name, element.getParent(),
						index + 1, null, null);
				((edu.cmu.cs.stage3.alice.core.response.CompositeResponse) element.getParent()).componentResponses
						.add(index + 1, copy);
			} else if (element.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) {
				final int index = ((edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) element
						.getParent()).components.indexOf(element);
				final edu.cmu.cs.stage3.alice.core.Element copy = element.HACK_createCopy(name, element.getParent(),
						index + 1, null, null);
				((edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) element.getParent()).components
						.add(index + 1, copy);
			} else {
				final edu.cmu.cs.stage3.alice.core.Element copy = element.createCopyNamed(name);
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.addElementToAppropriateProperty(copy,
						copy.getParent());
			}

			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().stopCompound();
		}
	}

	public static class MakeSharedCopyRunnable extends ElementPopupRunnable {
		protected Class<?>[] classesToShare = { edu.cmu.cs.stage3.alice.core.Geometry.class,
				edu.cmu.cs.stage3.alice.core.Sound.class, edu.cmu.cs.stage3.alice.core.TextureMap.class };

		public MakeSharedCopyRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			super(element);
		}

		@Override
		public String getDefaultLabel() {
			return "make shared copy";
		}

		@Override
		public void run() {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().startCompound();

			final String name = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
					.getNameForNewChild(element.name.getStringValue(), element.getParent());
			final edu.cmu.cs.stage3.alice.core.Element copy = element.createCopyNamed(name, classesToShare);
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.addElementToAppropriateProperty(copy,
					copy.getParent());

			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().stopCompound();
		}
	}

	public static class PrintStatisticsRunnable extends ElementPopupRunnable {
		public PrintStatisticsRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			super(element);
		}

		@Override
		public String getDefaultLabel() {
			return "print statistics";
		}

		@Override
		public void run() {
			final edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
			final edu.cmu.cs.stage3.alice.core.util.TextureMapCounter textureMapCounter = new edu.cmu.cs.stage3.alice.core.util.TextureMapCounter();

			element.visit(itaCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			element.visit(textureMapCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);

			System.out.println("Statistics for "
					+ edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(element) + ":");
			System.out.println("  object count: " + itaCounter.getShownIndexedTriangleArrayCount());
			System.out.println("    face count: " + itaCounter.getShownIndexCount() / 3);
			System.out.println("  vertex count: " + itaCounter.getShownVertexCount());
			System.out.println(" texture count: " + textureMapCounter.getTextureMapCount());
			System.out.println("texture memory: " + textureMapCounter.getTextureMapMemoryCount() + " bytes");
		}
	}

	public static class SaveCharacterRunnable extends ElementPopupRunnable {
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public SaveCharacterRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			this(element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack());
		}

		public SaveCharacterRunnable(final edu.cmu.cs.stage3.alice.core.Element element,
				final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
			super(element);
			this.authoringTool = authoringTool;
		}

		@Override
		public String getDefaultLabel() {
			return "save object...";
		}

		@Override
		public void run() {
			authoringTool.saveCharacter(element);
			// final edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker
			// worker = new
			// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
			// public Object construct() {
			// return new Integer( authoringTool.saveCharacter( element ) );
			// }
			// };
			// worker.start();
		}
	}

	/*
	 * public static class EditCharacterRunnable extends ElementPopupRunnable {
	 * protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
	 * authoringTool;
	 *
	 * public EditCharacterRunnable( edu.cmu.cs.stage3.alice.core.Element
	 * element ) { this( element,
	 * edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() ); }
	 *
	 * public EditCharacterRunnable( edu.cmu.cs.stage3.alice.core.Element
	 * element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
	 * authoringTool ) { super( element ); this.authoringTool = authoringTool; }
	 *
	 * public String getDefaultLabel() { return "edit character..."; }
	 *
	 * public void run() { authoringTool.editCharacter(
	 * (edu.cmu.cs.stage3.alice.core.Transformable)element ); } }
	 */

	public static class ToggleCommentingRunnable extends ElementPopupRunnable {
		public ToggleCommentingRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			super(element);
			if (!(element instanceof edu.cmu.cs.stage3.alice.core.Code)) {
				throw new IllegalArgumentException(
						"ToggleCommentRunnable only accepts Responses or User-Defined Questions; found: " + element);
			}
		}

		@Override
		public String getDefaultLabel() {
			final edu.cmu.cs.stage3.alice.core.Code code = (edu.cmu.cs.stage3.alice.core.Code) element;
			if (code.isCommentedOut.booleanValue()) {
				return "enable";
			} else {
				return "disable";
			}
		}

		@Override
		public void run() {
			final edu.cmu.cs.stage3.alice.core.Code code = (edu.cmu.cs.stage3.alice.core.Code) element;
			code.isCommentedOut.set(code.isCommentedOut.booleanValue() ? Boolean.FALSE : Boolean.TRUE);
		}
	}

	public static class SetElementScopeRunnable extends ElementPopupRunnable {
		private final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public SetElementScopeRunnable(final edu.cmu.cs.stage3.alice.core.Element element,
				final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
			super(element);
			this.authoringTool = authoringTool;
		}

		@Override
		public String getDefaultLabel() {
			return "switch to this element's scope";
		}

		@Override
		public void run() {
			authoringTool.setElementScope(element);
		}
	}

	public static class StorePoseRunnable extends ElementPopupRunnable {
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public StorePoseRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			this(element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack());
		}

		public StorePoseRunnable(final edu.cmu.cs.stage3.alice.core.Element element,
				final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
			super(element);
			this.authoringTool = authoringTool;
		}

		@Override
		public String getDefaultLabel() {
			return "capture pose";
		}

		@Override
		public void run() {
			if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				final edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) element;
				final edu.cmu.cs.stage3.alice.core.Pose pose = edu.cmu.cs.stage3.alice.core.Pose
						.manufacturePose(transformable, transformable);
				pose.name.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild("pose",
						element));
				element.addChild(pose);
				transformable.poses.add(pose);
			}
		}
	}

	public static class EditScriptRunnable extends ElementPopupRunnable {
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public EditScriptRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			this(element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack());
		}

		public EditScriptRunnable(final edu.cmu.cs.stage3.alice.core.Element element,
				final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
			super(element);
			this.authoringTool = authoringTool;
		}

		@Override
		public String getDefaultLabel() {
			return "edit script";
		}

		@Override
		public void run() {
			if (element instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				authoringTool.editObject(((edu.cmu.cs.stage3.alice.core.Sandbox) element).script);
			}
		}
	}

	// public static class CopyOverFromImportLoadRunnable extends
	// ElementPopupRunnable {
	// protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
	// authoringTool;
	//
	// public CopyOverFromImportLoadRunnable(
	// edu.cmu.cs.stage3.alice.core.Element element ) {
	// this( element,
	// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
	// }
	//
	// public CopyOverFromImportLoadRunnable(
	// edu.cmu.cs.stage3.alice.core.Element element,
	// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
	// super( element );
	// this.authoringTool = authoringTool;
	// }
	//
	// public String getDefaultLabel() {
	// return "from ASE";
	// }
	//
	// public void run() {
	// authoringTool.copyOverFromImportLoad( element );
	// // final edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker =
	// new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
	// // public Object construct() {
	// // authoringTool.copyOverFromImportLoad( element );
	// // return null;
	// // }
	// // };
	// // worker.start();
	// }
	// }
	//
	// public static class CopyOverFromCharacterLoadRunnable extends
	// ElementPopupRunnable {
	// protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
	// authoringTool;
	//
	// public CopyOverFromCharacterLoadRunnable(
	// edu.cmu.cs.stage3.alice.core.Element element ) {
	// this( element,
	// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
	// }
	//
	// public CopyOverFromCharacterLoadRunnable(
	// edu.cmu.cs.stage3.alice.core.Element element,
	// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
	// super( element );
	// this.authoringTool = authoringTool;
	// }
	//
	// public String getDefaultLabel() {
	// return "from object";
	// }
	//
	// public void run() {
	// final edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new
	// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
	// public Object construct() {
	// authoringTool.copyOverFromCharacterLoad( element );
	// return null;
	// }
	// };
	// worker.start();
	// }
	// }

	public static class GetAGoodLookAtRunnable extends ElementPopupRunnable {
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public GetAGoodLookAtRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			this(element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack());
		}

		public GetAGoodLookAtRunnable(final edu.cmu.cs.stage3.alice.core.Element element,
				final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
			super(element);
			this.authoringTool = authoringTool;
		}

		@Override
		public String getDefaultLabel() {
			return "Camera get a good look at this";
		}

		@Override
		public void run() {
			if (authoringTool
					.getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
				if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					authoringTool.getAGoodLookAt((edu.cmu.cs.stage3.alice.core.Transformable) element,
							(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) authoringTool
									.getCurrentCamera());
				} else {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
							.showErrorDialog("Can't get a good look: element is not a Transformable", null);
				}
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("Can't get a good look: camera is not symmetric perspective", null);
			}
		}
	}

	public static class SortGroupAlphabeticallyRunnable extends ElementPopupRunnable {
		protected java.util.Comparator<Object> sorter = new java.util.Comparator<Object>() {
			@Override
			public int compare(final Object o1, final Object o2) {
				if (o1 instanceof edu.cmu.cs.stage3.alice.core.Element
						&& o2 instanceof edu.cmu.cs.stage3.alice.core.Element) {
					final String name1 = ((edu.cmu.cs.stage3.alice.core.Element) o1).name.getStringValue();
					final String name2 = ((edu.cmu.cs.stage3.alice.core.Element) o2).name.getStringValue();
					return name1.compareTo(name2);
				} else {
					return 0;
				}
			}
		};

		public SortGroupAlphabeticallyRunnable(final edu.cmu.cs.stage3.alice.core.Element element) {
			super(element);
		}

		@Override
		public String getDefaultLabel() {
			return "sort alphabetically";
		}

		@Override
		public void run() {
			if (element instanceof edu.cmu.cs.stage3.alice.core.Group) {
				final edu.cmu.cs.stage3.alice.core.Group group = (edu.cmu.cs.stage3.alice.core.Group) element;
				final Object[] values = group.values.getArrayValue();
				java.util.Arrays.sort(values, sorter);
				group.values.clear(); // HACK; shouldn't have to do this
				group.values.set(values);
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
						"Unable to sort " + element + " alphabetically because it is not a Group.", null);
			}
		}
	}
}