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

package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.core.Element;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 *
 * @author
 * @version 1.0
 */

public class CompositeComponentResponsePanel
		extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -4356033120796665398L;

	public CompositeComponentResponsePanel() {
		super();
	}

	public void set(final edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty elements,
			final CompositeResponsePanel owner,
			final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn) {
		super.set(elements, owner, authoringToolIn);
	}

	@Override
	protected java.awt.Component makeGUI(final edu.cmu.cs.stage3.alice.core.Element currentElement) {
		javax.swing.JComponent toAdd = null;
		if (currentElement instanceof edu.cmu.cs.stage3.alice.core.Response) {
			if (currentElement instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
				toAdd = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(currentElement);
			} else {
				if (currentElement != null) {
					toAdd = new ComponentResponsePanel();
					((ComponentResponsePanel) toAdd).set(currentElement);
				}/* Dead code, as toAdd will be null, so can return that 
				  else {
					return null;
				}
				*/
			}
			return toAdd;
		} else {
			return null;
		}
	}

	@Override
	public void dragOver(final java.awt.dnd.DropTargetDragEvent dtde) {
		/* Unused ??
		final java.awt.Component sourceComponent = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager
				.getCurrentDragComponent();
		*/
		final int action = dtde.getDropAction();
		final boolean isCopy = (action & java.awt.dnd.DnDConstants.ACTION_COPY) > 0;
		final boolean isMove = (action & java.awt.dnd.DnDConstants.ACTION_MOVE) > 0;
		if (!m_owner.isExpanded()) {
			if (m_owner.getParent() instanceof CompositeComponentResponsePanel) {
				((CompositeComponentResponsePanel) m_owner.getParent()).dragOver(dtde);
				return;
			}
		}
		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.responseReferenceFlavor)) {
			try {
				final java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager
						.getCurrentTransferable();
				final edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response) transferable
						.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.responseReferenceFlavor);
				final boolean isValid = checkLoop(response);
				if (isValid) {
					if (isMove) {
						dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
					} else if (isCopy) {
						dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY);
					}
					insertDropPanel(dtde);
				} else {
					dtde.rejectDrag();
				}
			} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
				dtde.rejectDrag();
			} catch (final java.io.IOException e) {
				dtde.rejectDrag();
			} catch (final Throwable t) {
				dtde.rejectDrag();
			}
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
			try {
				final java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager
						.getCurrentTransferable();
				final edu.cmu.cs.stage3.alice.core.CopyFactory copyFactory = (edu.cmu.cs.stage3.alice.core.CopyFactory) transferable
						.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
				final Class<? extends edu.cmu.cs.stage3.alice.core.Element> valueClass = copyFactory.getValueClass();
				if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(valueClass)) {
					dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE); // looks
																			// nicer
					insertDropPanel(dtde);
				} else {
					dtde.rejectDrag();
				}
			} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
				dtde.rejectDrag();
			} catch (final java.io.IOException e) {
				dtde.rejectDrag();
			} catch (final Throwable t) {
				dtde.rejectDrag();
			}
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.questionReferenceFlavor)) {
			dtde.rejectDrag();
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
			try {
				final java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager
						.getCurrentTransferable();
				final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) transferable
						.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
				if (!(element instanceof edu.cmu.cs.stage3.alice.core.Behavior
						|| element instanceof edu.cmu.cs.stage3.alice.core.World
						|| element instanceof edu.cmu.cs.stage3.alice.core.TextureMap
						|| element instanceof edu.cmu.cs.stage3.alice.core.Group)) {
					if (checkLoop(element)) {
						dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE); // looks
																				// nicer
						insertDropPanel(dtde);
					} else {
						dtde.rejectDrag();
					}
				} else {
					dtde.rejectDrag();
				}
			} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
				dtde.rejectDrag();
			} catch (final java.io.IOException e) {
				dtde.rejectDrag();
			} catch (final Throwable t) {
				dtde.rejectDrag();
			}
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor)
				|| edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
						edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)
				|| edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
						edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor)) {
			if (isMove) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
				insertDropPanel(dtde);
			} else if (isCopy) {
				dtde.rejectDrag();
			}
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass(
							edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class));
			dtde.rejectDrag();
			return;
		}
	}

	@Override
	public void drop(final java.awt.dnd.DropTargetDropEvent dtde) {
		HACK_started = false;
		boolean successful = true;
		// java.awt.Component sourceComponent =
		// edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentDragComponent();
		final int action = dtde.getDropAction();
		final boolean isCopy = (action & java.awt.dnd.DnDConstants.ACTION_COPY) > 0;
		final boolean isMove = (action & java.awt.dnd.DnDConstants.ACTION_MOVE) > 0;
		if (!m_owner.isExpanded()) {
			if (m_owner
					.getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel) m_owner
						.getParent()).drop(dtde);
				return;
			}
		}
		final java.awt.datatransfer.Transferable transferable = dtde.getTransferable();

		// System.out.println("checking to see what kind of drop:
		// "+System.currentTimeMillis());
		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
			try {
				final edu.cmu.cs.stage3.alice.core.CopyFactory copyFactory = (edu.cmu.cs.stage3.alice.core.CopyFactory) transferable
						.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
				final Class<? extends Element> valueClass = copyFactory.getValueClass();
				if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(valueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
					successful = true;
					// edu.cmu.cs.stage3.alice.core.Response response =
					// (edu.cmu.cs.stage3.alice.core.Response)copyFactory.manufactureCopy(m_owner.getElement().getRoot());
					final edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response) copyFactory
							.manufactureCopy(m_owner.getElement().getRoot(), null, null, m_owner.getElement());
					if (response != null) {
						performDrop(response, dtde);
					}
				} else {
					successful = false;
					dtde.rejectDrop();
				}
			} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("The drop failed because of a bad flavor.", e);
				successful = false;
			} catch (final java.io.IOException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("The drop failed because of an IO error.", e);
				successful = false;
			} catch (final Throwable t) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("The drop failed.", t);
				successful = false;
			}
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.responseReferenceFlavor)) {
			try {
				// System.out.println("getting response:
				// "+System.currentTimeMillis());
				final edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response) transferable
						.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.responseReferenceFlavor);
				if (response instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
					// System.out.println("checking valid drop:
					// "+System.currentTimeMillis());
					if (!isCopy && !isValidDrop(s_currentComponentPanel.getElement(), response)) {
						// System.err.println("Can't drop on self");
						successful = false;
					}
				}
				if (successful) {
					if (isMove) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					} else if (isCopy) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
					}
					performDrop(response, dtde);
					successful = true;
				}
			} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("The drop failed because of a bad flavor.", e);
				successful = false;
			} catch (final java.io.IOException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("The drop failed because of an IO error.", e);
				successful = false;
			} catch (final Throwable t) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("The drop failed.", t);
				successful = false;
			}
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor)) {
			if (isMove) {
				dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
				successful = true;
			} else if (isCopy) {
				dtde.rejectDrop();
				successful = false;
			}
			if (successful) {
				try {
					final edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) transferable
							.getTransferData(
									edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor);
					if ((responsePrototype.getDesiredProperties() == null
							|| responsePrototype.getDesiredProperties().length < 1)
							&& !edu.cmu.cs.stage3.alice.core.response.Print.class
									.isAssignableFrom(responsePrototype.getResponseClass())) {
						performDrop(responsePrototype.createNewResponse(), dtde);
					} else if (responsePrototype.getDesiredProperties().length > 3) { // Bypass
																						// the
																						// popup
																						// menu
																						// and
																						// just
																						// put
																						// in
																						// defaults
																						// if
																						// it
																						// wants
																						// more
																						// than
																						// 3
																						// parameters
						performDrop(responsePrototype.createNewResponse(), dtde);
					} else {
						final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
							@Override
							public Object createItem(final Object object) {
								return new Runnable() {
									@Override
									public void run() {
										if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) {
											performDrop(
													((edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) object)
															.createNewResponse(),
													dtde);
										} else if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) {
											final edu.cmu.cs.stage3.alice.core.Element newResponse = ((edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object)
													.createNewElement();
											if (newResponse instanceof edu.cmu.cs.stage3.alice.core.Response) {
												performDrop(newResponse, dtde);
											}
										}
									}
								};
							}
						};
						java.util.Vector<Object> structure = null;
						if (edu.cmu.cs.stage3.alice.core.response.Print.class
								.isAssignableFrom(responsePrototype.getResponseClass())) {
							structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
									.makeResponsePrintStructure(factory, componentElements.getOwner());
						} else {
							structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
									.makePrototypeStructure(responsePrototype, factory, componentElements.getOwner());
						}
						final javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
								.makePopupMenu(structure);
						popup.addPopupMenuListener(this);
						inserting = true;
						popup.show(dtde.getDropTargetContext().getComponent(), (int) dtde.getLocation().getX(),
								(int) dtde.getLocation().getY());
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					}
				} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
							.showErrorDialog("The drop failed because of a bad flavor.", e);
					successful = false;
				} catch (final java.io.IOException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
							.showErrorDialog("The drop failed because of an IO error.", e);
					successful = false;
				} catch (final Throwable t) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("The drop failed.", t);
					successful = false;
				}
			}
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
			if (isMove) {
				dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
				successful = true;
			} else if (isCopy) {
				dtde.rejectDrop();
				successful = false;
			}
			if (successful) {
				try {
					edu.cmu.cs.stage3.util.StringObjectPair[] known;
					// Unused ?? final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation animation;
					Class<? extends edu.cmu.cs.stage3.alice.core.response.PropertyAnimation> animationClass;
					final edu.cmu.cs.stage3.alice.core.Property property = (edu.cmu.cs.stage3.alice.core.Property) transferable
							.getTransferData(
									edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
					if (property instanceof edu.cmu.cs.stage3.alice.core.property.VehicleProperty) {
						// System.out.println("new vehicle animation");
						final edu.cmu.cs.stage3.util.StringObjectPair[] newKnown = {
								new edu.cmu.cs.stage3.util.StringObjectPair("element", property.getOwner()),
								new edu.cmu.cs.stage3.util.StringObjectPair("propertyName", property.getName()),
								new edu.cmu.cs.stage3.util.StringObjectPair("duration", new Double(0)) };
						known = newKnown;
						animationClass = edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class;
					} else {
						final edu.cmu.cs.stage3.util.StringObjectPair[] newKnown = {
								new edu.cmu.cs.stage3.util.StringObjectPair("element", property.getOwner()),
								new edu.cmu.cs.stage3.util.StringObjectPair("propertyName", property.getName()) };
						known = newKnown;
						animationClass = edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class;
					}
					final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
						@Override
						public Object createItem(final Object object) {
							return new Runnable() {
								@Override
								public void run() {
									if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) {
										performDrop(
												((edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) object)
														.createNewResponse(),
												dtde);
									} else if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) {
										final edu.cmu.cs.stage3.alice.core.Element newResponse = ((edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object)
												.createNewElement();
										if (newResponse instanceof edu.cmu.cs.stage3.alice.core.Response) {
											performDrop(newResponse, dtde);
										}
									}
								}
							};
						}
					};
					final String[] desired = { "value" };
					// System.out.println("class: "+ animationClass
					// +", known: "+known+", desired: "+desired);
					final edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype rp = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(
							animationClass, known, desired);
					final java.util.Vector<Object> structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
							.makePrototypeStructure(rp, factory, componentElements.getOwner());
					final javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
							.makePopupMenu(structure);
					popup.addPopupMenuListener(this);
					inserting = true;
					popup.show(dtde.getDropTargetContext().getComponent(), (int) dtde.getLocation().getX(),
							(int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
				} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
							.showErrorDialog("The drop failed because of a bad flavor.", e);
					successful = false;
				} catch (final java.io.IOException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
							.showErrorDialog("The drop failed because of an IO error.", e);
					successful = false;
				} catch (final Throwable t) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("The drop failed.", t);
					successful = false;
				}
			}
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor)) {
			if (isMove) {
				dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
				successful = true;
			} else if (isCopy) {
				dtde.rejectDrop();
				successful = false;
			}
			if (successful) {
				try {
					final edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) transferable
							.getTransferData(
									edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor);
					if (!checkLoop(variable)) {
						dtde.rejectDrop();
						successful = false;
					} else {
						final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
							@Override
							public Object createItem(final Object object) {
								return new Runnable() {
									@Override
									public void run() {
										if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) {
											performDrop(
													((edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) object)
															.createNewResponse(),
													dtde);
										} else if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) {
											final edu.cmu.cs.stage3.alice.core.Element newResponse = ((edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object)
													.createNewElement();
											if (newResponse instanceof edu.cmu.cs.stage3.alice.core.Response) {
												performDrop(newResponse, dtde);
											}
										}
									}
								};
							}
						};
						final java.util.Vector<Object> structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
								.makeExpressionResponseStructure(variable, factory, componentElements.getOwner());
						final javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
								.makePopupMenu(structure);
						popup.addPopupMenuListener(this);
						inserting = true;
						popup.show(dtde.getDropTargetContext().getComponent(), (int) dtde.getLocation().getX(),
								(int) dtde.getLocation().getY());
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					}
				} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
							.showErrorDialog("The drop failed because of a bad flavor.", e);
					successful = false;
				} catch (final java.io.IOException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
							.showErrorDialog("The drop failed because of an IO error.", e);
					successful = false;
				} catch (final Throwable t) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("The drop failed.", t);
					successful = false;
				}
			}
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.expressionReferenceFlavor)) {
			// Don't handle expressions besides variables (which are handled
			// above)

			dtde.rejectDrop();
			successful = false;
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {

			try {
				final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) transferable
						.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
				if (element instanceof edu.cmu.cs.stage3.alice.core.Behavior
						|| element instanceof edu.cmu.cs.stage3.alice.core.World
						|| element instanceof edu.cmu.cs.stage3.alice.core.TextureMap) {
					dtde.rejectDrop();
					successful = false;
				}
				if (isMove) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					successful = true;
				} else if (isCopy) {
					dtde.rejectDrop();
					successful = false;
				}
				if (successful) {
					if (element instanceof edu.cmu.cs.stage3.alice.core.Sound) {
						final edu.cmu.cs.stage3.alice.core.response.SoundResponse r = new edu.cmu.cs.stage3.alice.core.response.SoundResponse();
						r.sound.set(element);
						r.subject.set(element.getParent());
						performDrop(r, dtde);
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Pose) {
						final edu.cmu.cs.stage3.alice.core.response.PoseAnimation r = new edu.cmu.cs.stage3.alice.core.response.PoseAnimation();
						r.pose.set(element);
						r.subject.set(element.getParent());
						performDrop(r, dtde);
					} else {
						final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
							@Override
							public Object createItem(final Object object) {
								return new Runnable() {
									@Override
									public void run() {
										if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) {
											performDrop(
													((edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) object)
															.createNewResponse(),
													dtde);
										} else if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) {
											final edu.cmu.cs.stage3.alice.core.Element newResponse = ((edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object)
													.createNewElement();
											if (newResponse instanceof edu.cmu.cs.stage3.alice.core.Response) {
												performDrop(newResponse, dtde);
											}
										}
									}
								};
							}
						};
						final java.util.Vector<Object> structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
								.makeResponseStructure(element, factory, componentElements.getOwner());
						final javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
								.makePopupMenu(structure);
						popup.addPopupMenuListener(this);
						inserting = true;
						popup.show(dtde.getDropTargetContext().getComponent(), (int) dtde.getLocation().getX(),
								(int) dtde.getLocation().getY());
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					}
				}
			} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("The drop failed because of a bad flavor.", e);
				successful = false;
			} catch (final java.io.IOException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("The drop failed because of an IO error.", e);
				successful = false;
			} catch (final Throwable t) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("The drop failed.", t);
				successful = false;
			}
		} else {
			dtde.rejectDrop();
			successful = false;
		}
		dtde.dropComplete(successful);
	}

}