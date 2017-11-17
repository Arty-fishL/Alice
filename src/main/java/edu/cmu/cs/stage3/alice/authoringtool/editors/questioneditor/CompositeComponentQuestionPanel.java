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

package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author David Culyba
 * @version 1.0
 */

public class CompositeComponentQuestionPanel
		extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -462371123261978848L;

	public CompositeComponentQuestionPanel() {
		super();
	}

	public void set(final edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty elements,
			final CompositeQuestionPanel owner,
			final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn) {
		super.set(elements, owner, authoringToolIn);
	}

	@Override
	protected java.awt.Component makeGUI(final edu.cmu.cs.stage3.alice.core.Element currentElement) {
		javax.swing.JComponent toAdd = null;
		if (currentElement instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component) {
			if (currentElement instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) {
				toAdd = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(currentElement);
			} else {
				if (currentElement != null) {
					toAdd = new ComponentQuestionPanel();
					((ComponentQuestionPanel) toAdd).set(currentElement);
				} else {
					return null;
				}
			}
			return toAdd;
		} else {
			return null;
		}
	}

	@Override
	public void dragOver(final java.awt.dnd.DropTargetDragEvent dtde) {
		final java.awt.Component sourceComponent = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager
				.getCurrentDragComponent();
		final int action = dtde.getDropAction();

		final boolean isCopy = (action & java.awt.dnd.DnDConstants.ACTION_COPY) > 0;
		final boolean isMove = (action & java.awt.dnd.DnDConstants.ACTION_MOVE) > 0;
		if (!m_owner.isExpanded()) {
			if (m_owner.getParent() instanceof CompositeComponentQuestionPanel) {
				((CompositeComponentQuestionPanel) m_owner.getParent()).dragOver(dtde);
				return;
			}
		}
		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				QuestionEditor.componentReferenceFlavor)) {
			final java.awt.datatransfer.Transferable currentTransferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager
					.getCurrentTransferable();
			edu.cmu.cs.stage3.alice.core.Element currentQuestion = null;
			if (currentTransferable != null) {
				try {
					currentQuestion = (edu.cmu.cs.stage3.alice.core.Element) currentTransferable
							.getTransferData(QuestionEditor.componentReferenceFlavor);
				} catch (final Exception e) {
					dtde.rejectDrag();
					return;
				}
				if (currentQuestion == null) {
					dtde.rejectDrag();
					return;
				}
				if (currentQuestion instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component) {
					if (!isCopy && !isValidDrop(componentElements.getOwner(), currentQuestion)) {
						dtde.rejectDrag();
						return;
					}
				}
			} else {
				dtde.rejectDrag();
				return;
			}
			if (isMove) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
			} else if (isCopy) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY);
			}
			insertDropPanel(dtde);
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
			try {
				final java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager
						.getCurrentTransferable();
				final edu.cmu.cs.stage3.alice.core.CopyFactory copyFactory = (edu.cmu.cs.stage3.alice.core.CopyFactory) transferable
						.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
				final Class valueClass = copyFactory.getValueClass();
				if (edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(valueClass)) {
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
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
			try {
				final java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager
						.getCurrentTransferable();
				final edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype prototype = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) transferable
						.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
				final Class valueClass = prototype.getElementClass();
				if (edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(valueClass)) {
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
			dtde.rejectDrag();
			return;
		}
	}

	protected edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion getTopQuestion(
			final edu.cmu.cs.stage3.alice.core.Element e) {
		if (e instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
			return (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) e;
		}
		if (e.getParent() != null) {
			return getTopQuestion(e.getParent());
		}
		return null;
	}

	@Override
	public void drop(final java.awt.dnd.DropTargetDropEvent dtde) {
		HACK_started = false;
		boolean successful = true;
		final java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
		final int action = dtde.getDropAction();
		final boolean isCopy = (action & java.awt.dnd.DnDConstants.ACTION_COPY) > 0;
		final boolean isMove = (action & java.awt.dnd.DnDConstants.ACTION_MOVE) > 0;
		if (!m_owner.isExpanded()) {
			if (m_owner.getParent() instanceof CompositeComponentQuestionPanel) {
				((CompositeComponentQuestionPanel) m_owner.getParent()).drop(dtde);
				return;
			}
		}

		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
			try {
				final edu.cmu.cs.stage3.alice.core.CopyFactory copyFactory = (edu.cmu.cs.stage3.alice.core.CopyFactory) transferable
						.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
				final Class valueClass = copyFactory.getValueClass();
				if (edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(valueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
					successful = true;
					final edu.cmu.cs.stage3.alice.core.Element question = copyFactory
							.manufactureCopy(m_owner.getElement().getRoot());
					if (question != null) {
						performDrop(question, dtde);
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
				QuestionEditor.componentReferenceFlavor)) {
			try {
				final edu.cmu.cs.stage3.alice.core.Element question = (edu.cmu.cs.stage3.alice.core.Element) transferable
						.getTransferData(QuestionEditor.componentReferenceFlavor);
				successful = true;
				if (question instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) {
					if (!isCopy && !isValidDrop(componentElements.getOwner(), question)) {
						successful = false;
					}
				}
				if (successful) {
					if (isMove) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					} else if (isCopy) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
					}
					performDrop(question, dtde);
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
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
			if (isMove) {
				dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
				successful = true;
			} else if (isCopy) {
				dtde.rejectDrop();
				successful = false;
			}
			if (successful) {
				try {
					edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype questionPrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) transferable
							.getTransferData(
									edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
					final Class valueClass = questionPrototype.getElementClass();
					if (!edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class
							.isAssignableFrom(valueClass)) {
						dtde.rejectDrop();
						successful = false;
					}
					if (successful) {
						if (edu.cmu.cs.stage3.alice.core.question.userdefined.Return.class
								.isAssignableFrom(valueClass)) {
							final edu.cmu.cs.stage3.util.StringObjectPair[] known = {
									new edu.cmu.cs.stage3.util.StringObjectPair("valueClass",
											getTopQuestion(getComponentProperty().getOwner()).valueClass.get()) };
							final edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype newPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype(
									valueClass, known, questionPrototype.getDesiredProperties());
							questionPrototype = newPrototype;
						}
						if ((questionPrototype.getDesiredProperties() == null
								|| questionPrototype.getDesiredProperties().length < 1)
								&& !edu.cmu.cs.stage3.alice.core.question.userdefined.Print.class
										.isAssignableFrom(questionPrototype.getElementClass())) {
							performDrop(questionPrototype.createNewElement(), dtde);
						} else {
							final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
								@Override
								public Object createItem(final Object object) {
									return new Runnable() {
										@Override
										public void run() {
											if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) {
												final edu.cmu.cs.stage3.alice.core.Element newQuestion = ((edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object)
														.createNewElement();
												// System.out.println("made new
												// question thingy:
												// "+newQuestion);
												if (newQuestion instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component) {
													performDrop(newQuestion, dtde);
												}
											}
										}
									};
								}
							};
							java.util.Vector structure = null;
							if (edu.cmu.cs.stage3.alice.core.question.userdefined.Print.class
									.isAssignableFrom(questionPrototype.getElementClass())) {
								structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
										.makeQuestionPrintStructure(factory, componentElements.getOwner());
							} else {
								structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
										.makePrototypeStructure(questionPrototype, factory,
												componentElements.getOwner());
							}
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
			}
		} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)
				|| edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
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
					edu.cmu.cs.stage3.alice.core.Property property;
					if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
							edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor)) {
						final edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) transferable
								.getTransferData(
										edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor);
						property = variable.value;
					} else {
						property = (edu.cmu.cs.stage3.alice.core.Property) transferable.getTransferData(
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
					}
					final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
						@Override
						public Object createItem(final Object object) {
							return new Runnable() {
								@Override
								public void run() {
									if (object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) {
										final edu.cmu.cs.stage3.alice.core.Element newQuestion = ((edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object)
												.createNewElement();
										if (newQuestion instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component) {
											performDrop(newQuestion, dtde);
										}
									}
								}
							};
						}
					};
					final java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
							.makePropertyAssignmentForUserDefinedQuestionStructure(property, factory,
									componentElements.getOwner());
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
				edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.expressionReferenceFlavor)) {
			// Don't accept expressions besides variables (handled above)

			successful = false;
			dtde.rejectDrop();
		} else {
			dtde.rejectDrop();
		}
		dtde.dropComplete(successful);
	}

}