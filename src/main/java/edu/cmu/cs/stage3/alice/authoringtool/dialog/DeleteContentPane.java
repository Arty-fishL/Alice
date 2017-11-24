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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ListModel;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.PropertyReferenceListCellRenderer;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.reference.ObjectArrayPropertyReference;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */
public class DeleteContentPane extends edu.cmu.cs.stage3.swing.ContentPane implements
		edu.cmu.cs.stage3.alice.core.event.PropertyListener, edu.cmu.cs.stage3.alice.core.event.ChildrenListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -1218329362966652209L;
	public final static int LESS_DETAIL_MODE = 0;
	public final static int MORE_DETAIL_MODE = 1;

	protected int mode = -1;
	protected static edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel errorIconPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel();
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable deleteRunnable;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.HighlightingGlassPane glassPane;
	protected edu.cmu.cs.stage3.alice.core.Element danglingElementToClear;

	public static void showDeleteDialog(
			final edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable deleteRunnable,
			final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		final DeleteContentPane dcp = new DeleteContentPane(authoringTool);
		dcp.setDeleteRunnable(deleteRunnable);
		dcp.refresh();
		if (edu.cmu.cs.stage3.swing.DialogManager.showDialog(dcp) == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
			deleteRunnable.run();
		}

		// todo?
		// if( javax.swing.SwingUtilities.isEventDispatchThread() ) {
		// final DeleteDialog finalDd = dd;
		// final edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker =
		// new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
		// public Object construct() {
		// finalDd.setVisible( true );
		// return finalDd;
		// }
		// };
		// worker.start();
		// } else {
		// dd.setVisible( true );
		// }
	}

	public DeleteContentPane(final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		DeleteContentPane.authoringTool = authoringTool;
		jbInit();
		final java.net.URL errorImageResources = edu.cmu.cs.stage3.alice.authoringtool.JAlice.class
				.getResource("images/error.gif");
		errorIconPanel.setImage(java.awt.Toolkit.getDefaultToolkit().createImage(errorImageResources));
		iconPanel.add(errorIconPanel, java.awt.BorderLayout.CENTER);

		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		messageArea.setOpaque(false);

		referencesList.setCellRenderer(new PropertyReferenceListCellRenderer());
		referencesList.addListSelectionListener(new ReferencesSelectionListener());
		glassPane = new edu.cmu.cs.stage3.alice.authoringtool.util.HighlightingGlassPane(authoringTool);

		setPreferredSize(new java.awt.Dimension(600, 300));
	}

	@Override
	public String getTitle() {
		return "Alice - Can't Delete";
	}

	@Override
	public void addOKActionListener(final java.awt.event.ActionListener l) {
		okayButton.addActionListener(l);
	}

	@Override
	public void removeOKActionListener(final java.awt.event.ActionListener l) {
		okayButton.removeActionListener(l);
	}

	@Override
	public void addCancelActionListener(final java.awt.event.ActionListener l) {
		cancelButton.addActionListener(l);
	}

	@Override
	public void removeCancelActionListener(final java.awt.event.ActionListener l) {
		cancelButton.removeActionListener(l);
	}

	@Override
	public void postDialogShow(final javax.swing.JDialog dialog) {
		glassPane.setHighlightingEnabled(false);
		stopListening();
		TEMP_checkForListening();
		super.postDialogShow(dialog);
	}

	public void setDeleteRunnable(
			final edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable deleteRunnable) {
		this.deleteRunnable = deleteRunnable;
	}

	public void TEMP_checkForListening() {
		final edu.cmu.cs.stage3.alice.core.Element[] elements = authoringTool.getWorld().getDescendants();
		for (final Element element : elements) {
			final edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = element
					.getChildrenListeners();
			for (final ChildrenListener childrenListener : childrenListeners) {
				if (childrenListener == this) {
					System.out.println("############################  child listener: " + element);
				}
			}
			final edu.cmu.cs.stage3.alice.core.Property[] properties = element.getProperties();
			for (final Property propertie : properties) {
				final edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propertyListeners = propertie
						.getPropertyListeners();
				for (final PropertyListener propertyListener : propertyListeners) {
					if (propertyListener == this) {
						System.out.println("********************************   property listener: " + propertie);
					}
				}
			}
		}
	}

	public void startListening() {
		final javax.swing.ListModel<PropertyReference> list = referencesList.getModel();
		for (int i = 0; i < list.getSize(); i++) {
			if (list.getElementAt(i) instanceof PropertyReference) {
				final PropertyReference reference = list
						.getElementAt(i);
				final edu.cmu.cs.stage3.alice.core.Element source = reference.getProperty().getOwner();
				listenUpToRootElement(source);
				if (source instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
					final edu.cmu.cs.stage3.alice.core.Property[] properties = source.getParent().getProperties();
					for (final Property propertie : properties) {
						if (propertie.get() == source) {
							propertie.addPropertyListener(this);
						}
					}
				} else if (reference instanceof ObjectArrayPropertyReference) {
					final ObjectArrayPropertyReference oAPR = (ObjectArrayPropertyReference) reference;
					final PropertyReference[] references = oAPR.getReference()
							.getPropertyReferencesTo(deleteRunnable.getElement(),
									edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
					if (references != null && references.length > 0) {
						reference.getProperty().addPropertyListener(this);
					}

				} else {
					reference.getProperty().addPropertyListener(this);

				}
			}
		}

	}

	public void stopListening() {
		final ListModel<PropertyReference> list = referencesList.getModel();
		for (int i = 0; i < list.getSize(); i++) {
			if (list.getElementAt(i) instanceof PropertyReference) {
				final PropertyReference reference = list
						.getElementAt(i);
				final edu.cmu.cs.stage3.alice.core.Element source = reference.getProperty().getOwner();
				stopListeningUpToRootElement(source);
				if (source instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
					if (source.getParent() != null) {
						final edu.cmu.cs.stage3.alice.core.Property[] properties = source.getParent().getProperties();
						for (final Property propertie : properties) {
							if (propertie.get() == source) {
								propertie.removePropertyListener(this);
							}
						}
					}
				} else if (reference instanceof ObjectArrayPropertyReference) {
					// Unused ?? final ObjectArrayPropertyReference oAPR = (ObjectArrayPropertyReference) reference;
					final PropertyReference[] references = reference
							.getProperty().getOwner().getPropertyReferencesTo(deleteRunnable.getElement(),
									edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
					if (references != null && references.length > 0) {
						reference.getProperty().removePropertyListener(this);
					}

				} else {
					reference.getProperty().removePropertyListener(this);

				}
			}
		}

	}

	@Override
	public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
	}

	@Override
	public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
		// System.out.println("prop changed!: "+propertyEvent);
		refresh();
	}

	@Override
	public void childrenChanging(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent) {
		danglingElementToClear = childrenEvent.getParent();
	}

	@Override
	public void childrenChanged(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent) {
		// System.out.println("whoa! child change!: "+childrenEvent);
		stopListeningUpToRootElement(danglingElementToClear);
		refresh();
	}

	protected void listenUpToRootElement(final edu.cmu.cs.stage3.alice.core.Element current) {
		if (current != null) {
			boolean alreadyChildrenListening = false;
			final edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = current
					.getChildrenListeners();
			for (final ChildrenListener childrenListener : childrenListeners) {
				if (childrenListener == this) {
					alreadyChildrenListening = true;
				}
			}
			if (!alreadyChildrenListening) {
				current.addChildrenListener(this);
			}
			final edu.cmu.cs.stage3.alice.core.Property[] properties = current.getProperties();
			for (final Property propertie : properties) {
				final edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propListeners = propertie
						.getPropertyListeners();
				boolean alreadyPropListening = false;
				for (final PropertyListener propListener : propListeners) {
					if (propListener == this) {
						alreadyPropListening = true;
					}
				}
				if (!alreadyPropListening) {
					propertie.addPropertyListener(this);
				}
			}
			listenUpToRootElement(current.getParent());
		}
	}

	protected void stopListeningUpToRootElement(final edu.cmu.cs.stage3.alice.core.Element current) {
		if (current != null) {
			boolean alreadyChildrenListening = false;
			final edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = current
					.getChildrenListeners();
			for (final ChildrenListener childrenListener : childrenListeners) {
				if (childrenListener == this) {
					alreadyChildrenListening = true;
				}
			}
			if (alreadyChildrenListening) {
				current.removeChildrenListener(this);
			}
			final edu.cmu.cs.stage3.alice.core.Property[] properties = current.getProperties();
			for (final Property propertie : properties) {
				final edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propListeners = propertie
						.getPropertyListeners();
				boolean alreadyPropListening = false;
				for (final PropertyListener propListener : propListeners) {
					if (propListener == this) {
						alreadyPropListening = true;
					}
				}
				if (alreadyPropListening) {
					propertie.removePropertyListener(this);
				}
			}
			stopListeningUpToRootElement(current.getParent());
		}
	}

	public void refresh() {
		if (deleteRunnable != null) {
			final edu.cmu.cs.stage3.alice.core.Element element = deleteRunnable.getElement();
			final String elementRepr = AuthoringToolResources
					.getReprForValue(element);

			PropertyReference[] references = element.getRoot()
					.getPropertyReferencesTo(element, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true,
							true);

			if (references.length > 0) {
				AuthoringToolResources.garbageCollectIfPossible(references);
				references = element.getRoot().getPropertyReferencesTo(element,
						edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
			}

			stopListening();
			referencesList.setListData(references);
			startListening();

			if (references.length > 0) {
				okayButton.setEnabled(false);
				removeReferenceButton.setEnabled(true);
				removeAllReferenceButton.setEnabled(true);
				messageArea.setText(elementRepr
						+ " cannot be deleted because other parts of the World contain references to it. You will need to remove these references in order to delete "
						+ elementRepr + ".\n\n"
						+ "Select each reference below, and either remove the reference manually, or click the Remove Reference button to have the reference removed by the system.");
				referencesList.setSelectedIndex(0);
			} else {
				okayButton.setEnabled(true);
				removeReferenceButton.setEnabled(false);
				removeAllReferenceButton.setEnabled(false);
				messageArea.setText("All references have now been deleted.  Click Okay to delete " + elementRepr + ".");
				setDialogTitle("Alice - Can Delete");
			}
		}
	}

	public static String getHighlightID(final PropertyReference reference) {
		String highlightID = null;
		if (reference != null) {
			// System.out.println("\nreference is: "+reference);
			final edu.cmu.cs.stage3.alice.core.Element source = reference.getProperty().getOwner();
			final edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
			// System.out.println("source is: "+source);
			if (source == world) {
				highlightID = "details<>:viewController<>:property<" + reference.getProperty().getName() + ">";
			}

			if (highlightID == null) {
				if (source instanceof edu.cmu.cs.stage3.alice.core.Model) {
					highlightID = "details<" + source.getKey(world) + ">:viewController<>:property<"
							+ reference.getProperty().getName() + ">";
				}
			}

			if (highlightID == null) {
				if (source instanceof edu.cmu.cs.stage3.alice.core.Variable) {
					final edu.cmu.cs.stage3.alice.core.Element sourceParent = source.getParent();
					// System.out.println("parent: "+sourceParent);
					if (sourceParent == world) {
						highlightID = "details<>:viewController<>:variable<"
								+ reference.getProperty().getOwner().getKey(world) + ">";
					} else if (sourceParent instanceof edu.cmu.cs.stage3.alice.core.Model) {
						highlightID = "details<" + sourceParent.getKey(world) + ">:viewController<>:variable<"
								+ reference.getProperty().getOwner().getKey(sourceParent) + ">";
					} else if (sourceParent instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse
							|| sourceParent instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
						final edu.cmu.cs.stage3.alice.core.Element[] userDefinedResponses = world
								.getDescendants(edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class);
						for (final Element userDefinedResponse : userDefinedResponses) {
							// System.out.println(userDefinedResponses[i]);
							if (userDefinedResponse == sourceParent || userDefinedResponse.isAncestorOf(sourceParent)) {
								highlightID = "editors:element<" + userDefinedResponse.getKey(world) + ">:elementTile<"
										+ source.getKey(world) + ">:property<" + reference.getProperty().getName()
										+ ">";
								break;
							}
						}
						if (highlightID == null) {
							final edu.cmu.cs.stage3.alice.core.Element[] behaviors = world
									.getDescendants(edu.cmu.cs.stage3.alice.core.Behavior.class);
							for (final Element behavior : behaviors) {
								// System.out.println(behaviors[i]);
								if (behavior.isAncestorOf(sourceParent)) {
									highlightID = "behaviors:elementTile<" + source.getKey(world) + ">:property<"
											+ reference.getProperty().getName() + ">";
									break;
								}
							}
						}
					}

				}
			}
			if (highlightID == null) {
				if (source instanceof edu.cmu.cs.stage3.alice.core.Collection) {
					final edu.cmu.cs.stage3.alice.core.Element sourceParent = source.getParent();
					// System.out.println("parent: "+sourceParent);
					if (sourceParent instanceof edu.cmu.cs.stage3.alice.core.Variable) {
						final edu.cmu.cs.stage3.alice.core.Element variableParent = sourceParent.getParent();
						if (variableParent == world) {
							highlightID = "details<>:viewController<>:variable<" + sourceParent.getKey(world) + ">";
						} else if (variableParent instanceof edu.cmu.cs.stage3.alice.core.Model) {
							highlightID = "details<" + variableParent.getKey(world) + ">:viewController<>:variable<"
									+ sourceParent.getKey(variableParent) + ">";
						}
					}

				}
			}
			if (highlightID == null) {
				if (source instanceof edu.cmu.cs.stage3.alice.core.List) {
					final edu.cmu.cs.stage3.alice.core.Element sourceParent = source.getParent();
					// System.out.println("parent: "+sourceParent);
					if (sourceParent == world) {
						highlightID = "details<>:viewController<>:variable<"
								+ reference.getProperty().getOwner().getKey(world) + ">";
					} else if (sourceParent instanceof edu.cmu.cs.stage3.alice.core.Model) {
						highlightID = "details<" + sourceParent.getKey(world) + ">:viewController<>:variable<"
								+ reference.getProperty().getOwner().getKey(sourceParent) + ">";
					}

				}
			}

			if (highlightID == null) {
				final edu.cmu.cs.stage3.util.Criterion userDefinedResponsesCriterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
						edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class);
				final edu.cmu.cs.stage3.util.Criterion userDefinedQuestionsCriterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
						edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion.class);
				final edu.cmu.cs.stage3.util.Criterion[] searchCriterion = { userDefinedResponsesCriterion,
						userDefinedQuestionsCriterion };
				final edu.cmu.cs.stage3.alice.core.Element[] userDefinedResponsesAndQuestions = world
						.search(new edu.cmu.cs.stage3.util.criterion.MatchesAnyCriterion(searchCriterion));
				final edu.cmu.cs.stage3.alice.core.Element sourceParent = source.getParent();
				for (final Element userDefinedResponsesAndQuestion : userDefinedResponsesAndQuestions) {
					if (userDefinedResponsesAndQuestion.isAncestorOf(source)) {
						highlightID = "editors:element<" + userDefinedResponsesAndQuestion.getKey(world) + ">";
						break;
					}
				}
				if (highlightID == null) {
					final edu.cmu.cs.stage3.alice.core.Element[] behaviors = world
							.getDescendants(edu.cmu.cs.stage3.alice.core.Behavior.class);
					for (final Element behavior : behaviors) {
						if (behavior.isAncestorOf(source)) {
							highlightID = "behaviors";
							break;
						}
					}
				}
				if (reference.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty
						|| reference
								.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.UserDefinedQuestionProperty
						|| source instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
					// need to check to see if it's just a call, or it's part of
					// a property...
					final edu.cmu.cs.stage3.alice.core.Property[] properties = sourceParent.getProperties();
					boolean setIt = false;
					for (final Property propertie : properties) {
						if (propertie.get() == source) {
							highlightID += ":elementTile<" + sourceParent.getKey(world) + ">:property<"
									+ propertie.getName() + ">";
							setIt = true;
						}
					}
					if (!setIt) {
						highlightID += ":elementTile<" + source.getKey(world) + ">";
					}
				} else {
					highlightID += ":elementTile<" + source.getKey(world) + ">:property<"
							+ reference.getProperty().getName() + ">";
				}

			}

		}
		// System.out.println("returning: "+highlightID);
		return highlightID;
	}

	public static javax.swing.ImageIcon getDeleteIcon(
			final PropertyReference reference) {
		final javax.swing.ImageIcon toReturn = new javax.swing.ImageIcon();
		if (reference != null) {
			final String id = getHighlightID(reference);
			final java.awt.Image image = authoringTool.getImageForID(id);
			if (image != null) {
				toReturn.setImage(image);
			}
		}
		return toReturn;
	}

	public static String getDeleteString(final PropertyReference reference) {
		String highlightID = null;
		if (reference != null) {
			final edu.cmu.cs.stage3.alice.core.Element source = reference.getProperty().getOwner();
			final edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
			final String ourName = reference.getReference().name.getStringValue();

			if (source == world) {
				highlightID = "The World's " + reference.getProperty().getName() + " is set to " + ourName;
			}

			if (highlightID == null) {
				if (source instanceof edu.cmu.cs.stage3.alice.core.Model) {
					highlightID = "The " + source.name.getStringValue() + "'s " + reference.getProperty().getName()
							+ " is set to " + ourName;
				}
			}

			if (highlightID == null) {
				if (source instanceof edu.cmu.cs.stage3.alice.core.Variable) {
					final edu.cmu.cs.stage3.alice.core.Element sourceParent = source.getParent();
					if (sourceParent == world) {
						highlightID = "The World's variable \""
								+ reference.getProperty().getOwner().name.getStringValue() + "\" is set to " + ourName;
					} else if (sourceParent instanceof edu.cmu.cs.stage3.alice.core.Model) {
						highlightID = "The " + sourceParent.name.getStringValue() + "'s variable \""
								+ reference.getProperty().getOwner().name.getStringValue() + "\" is set to " + ourName;
					} else if (sourceParent instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse
							|| sourceParent instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
						final edu.cmu.cs.stage3.alice.core.Element[] userDefinedResponses = world
								.getDescendants(edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class);
						for (final Element userDefinedResponse : userDefinedResponses) {
							// System.out.println(userDefinedResponses[i]);
							if (userDefinedResponse == sourceParent || userDefinedResponse.isAncestorOf(sourceParent)) {
								highlightID = "The method \"" + userDefinedResponse.getKey() + "\" contains "
										+ source.getRepr() + " which is set to " + ourName;
								break;
							}
						}
						if (highlightID == null) {
							final edu.cmu.cs.stage3.alice.core.Element[] behaviors = world
									.getDescendants(edu.cmu.cs.stage3.alice.core.Behavior.class);
							for (final Element behavior : behaviors) {
								// System.out.println(behaviors[i]);
								if (behavior.isAncestorOf(sourceParent)) {
									highlightID = "The behavior " + source.getRepr() + ", property: "
											+ reference.getProperty().getName();
									break;
								}
							}
						}
					}

				}
			}
			if (highlightID == null) {
				if (source instanceof edu.cmu.cs.stage3.alice.core.Collection) {
					final edu.cmu.cs.stage3.alice.core.Element sourceParent = source.getParent();
					if (sourceParent instanceof edu.cmu.cs.stage3.alice.core.Variable) {
						final edu.cmu.cs.stage3.alice.core.Element variableParent = sourceParent.getParent();
						if (variableParent == world) {
							highlightID = "The World's variable \"" + sourceParent.name.getStringValue()
									+ "\" element number " + ((edu.cmu.cs.stage3.alice.core.Collection) source)
											.getIndexOfChild(reference.getReference())
									+ " is set to " + ourName;
						} else if (variableParent instanceof edu.cmu.cs.stage3.alice.core.Model) {
							highlightID = "The " + variableParent.name.getStringValue() + "'s variable \""
									+ sourceParent.name.getStringValue() + "\" element number "
									+ ((edu.cmu.cs.stage3.alice.core.Collection) source)
											.getIndexOfChild(reference.getReference())
									+ " is set to " + ourName;
						}
					}

				}
			}

			if (highlightID == null) {
				final edu.cmu.cs.stage3.util.Criterion userDefinedResponsesCriterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
						edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class);
				final edu.cmu.cs.stage3.util.Criterion userDefinedQuestionsCriterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
						edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion.class);
				final edu.cmu.cs.stage3.util.Criterion[] searchCriterion = { userDefinedResponsesCriterion,
						userDefinedQuestionsCriterion };
				final edu.cmu.cs.stage3.alice.core.Element[] userDefinedResponsesAndQuestions = world
						.search(new edu.cmu.cs.stage3.util.criterion.MatchesAnyCriterion(searchCriterion));
				final edu.cmu.cs.stage3.alice.core.Element sourceParent = source.getParent();
				for (final Element userDefinedResponsesAndQuestion : userDefinedResponsesAndQuestions) {
					if (userDefinedResponsesAndQuestion.isAncestorOf(source)) {
						highlightID = "The method \"" + userDefinedResponsesAndQuestion.getKey() + "\"";
						break;
					}
				}
				if (highlightID == null) {
					final edu.cmu.cs.stage3.alice.core.Element[] behaviors = world
							.getDescendants(edu.cmu.cs.stage3.alice.core.Behavior.class);
					for (final Element behavior : behaviors) {
						if (behavior.isAncestorOf(source)) {
							highlightID = "The behavior";
							break;
						}
					}
				}
				if (reference.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty
						|| reference
								.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.UserDefinedQuestionProperty
						|| source instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
					// need to check to see if it's just a call, or it's part of
					// a property...
					final edu.cmu.cs.stage3.alice.core.Property[] properties = sourceParent.getProperties();
					boolean setIt = false;
					for (final Property propertie : properties) {
						if (propertie.get() == source) {
							highlightID += " has a line of code "
									+ AuthoringToolResources
											.getReprForValue(source.getClass())
									+ " who's " + propertie.getName() + " is set to " + ourName;
							setIt = true;
						}
					}
					if (!setIt) {
						highlightID += ":elementTile<" + source.getKey(world) + ">";
					}
				} else {
					highlightID += ":elementTile<" + source.getKey(world) + ">:property<"
							+ reference.getProperty().getName() + ">";
				}
				// System.out.println("made it: "+highlightID);

			}

		}
		return highlightID;
	}

	class ReferencesSelectionListener implements javax.swing.event.ListSelectionListener {
		@Override
		public void valueChanged(final javax.swing.event.ListSelectionEvent ev) {
			String highlightID = null;
			final PropertyReference reference = referencesList
					.getSelectedValue();
			highlightID = getHighlightID(reference);
			glassPane.setHighlightID(highlightID);
			if (highlightID != null) {
				glassPane.setHighlightingEnabled(true);
			}
		}
	}

	void removeReferenceButton_actionPerformed(final java.awt.event.ActionEvent e) {
		final PropertyReference reference = referencesList
				.getSelectedValue();
		final edu.cmu.cs.stage3.alice.core.Element source = reference.getProperty().getOwner();
		final edu.cmu.cs.stage3.alice.core.Element sourceParent = source.getParent();
		if (reference != null) {
			if (source instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse
					|| source instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion
					|| source instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
				// need to check to see if it's just a call, or it's part of a
				// property...
				if (sourceParent instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
					source.removeFromParent();
				} else {
					final edu.cmu.cs.stage3.alice.core.Property[] properties = sourceParent.getProperties();
					for (final Property propertie : properties) {
						if (propertie.get() == source) {
							propertie.removePropertyListener(this);
							propertie.set(AuthoringToolResources
									.getDefaultValueForClass(propertie.getValueClass()));
						}
					}

				}
			} else if (reference instanceof ObjectArrayPropertyReference) {
				final ObjectArrayPropertyReference oAPR = (ObjectArrayPropertyReference) reference;
				oAPR.getObjectArrayProperty().set(oAPR.getIndex(), null);
				final PropertyReference[] references = oAPR.getProperty()
						.getOwner().getPropertyReferencesTo(deleteRunnable.getElement(),
								edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
				if (references == null || references.length < 1) {
					reference.getProperty().removePropertyListener(this);
				}

			} else {
				reference.getProperty().removePropertyListener(this);
				reference.getProperty().set(AuthoringToolResources
						.getDefaultValueForClass(reference.getProperty().getValueClass()));

			}
			// Delay code
			// final edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker
			// worker = new
			// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
			// public Object construct() {
			// try{
			// Thread.sleep(500);
			// }
			// catch (Exception e){}
			// refresh();
			// return null;
			// }
			// };
			// worker.start();
			refresh();

		}
	}

	void removeAllReferenceButton_actionPerformed(final java.awt.event.ActionEvent e) {
		stopListening();
		for (int i = 0; i < referencesList.getModel().getSize(); i++) {
			final PropertyReference reference = referencesList
					.getModel().getElementAt(i);
			final edu.cmu.cs.stage3.alice.core.Element source = reference.getProperty().getOwner();
			final edu.cmu.cs.stage3.alice.core.Element sourceParent = source.getParent();
			if (reference != null) {
				if (source instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse
						|| source instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion
						|| source instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
					// need to check to see if it's just a call, or it's part of
					// a property...
					if (sourceParent instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
						source.removeFromParent();
					} else {
						final edu.cmu.cs.stage3.alice.core.Property[] properties = sourceParent.getProperties();
						for (final Property propertie : properties) {
							if (propertie.get() == source) {
								propertie.removePropertyListener(this);
								propertie.set(AuthoringToolResources
										.getDefaultValueForClass(propertie.getValueClass()));
							}
						}

					}
				} else if (reference instanceof ObjectArrayPropertyReference) {
					final ObjectArrayPropertyReference oAPR = (ObjectArrayPropertyReference) reference;
					oAPR.getObjectArrayProperty().set(oAPR.getIndex(), null);
					final PropertyReference[] otherReferences = oAPR
							.getProperty().getOwner().getPropertyReferencesTo(deleteRunnable.getElement(),
									edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
					if (otherReferences == null || otherReferences.length < 1) {
						reference.getProperty().removePropertyListener(this);
					}
				} else {
					reference.getProperty().removePropertyListener(this);
					reference.getProperty().set(AuthoringToolResources
							.getDefaultValueForClass(reference.getProperty().getValueClass()));
				}
			}
		}
		refresh();
	}

	// ///////////////////
	// Autogenerated
	// ///////////////////

	java.awt.BorderLayout borderLayout1 = new java.awt.BorderLayout();
	javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
	javax.swing.JPanel mainPanel = new javax.swing.JPanel();
	javax.swing.JButton okayButton = new javax.swing.JButton();
	javax.swing.JButton removeReferenceButton = new javax.swing.JButton();
	javax.swing.JButton removeAllReferenceButton = new javax.swing.JButton();
	javax.swing.JPanel iconPanel = new javax.swing.JPanel();
	javax.swing.JPanel messagePanel = new javax.swing.JPanel();
	javax.swing.JPanel referencesPanel = new javax.swing.JPanel();
	java.awt.GridBagLayout gridBagLayout1 = new java.awt.GridBagLayout();
	java.awt.BorderLayout borderLayout2 = new java.awt.BorderLayout();
	java.awt.BorderLayout borderLayout3 = new java.awt.BorderLayout();
	java.awt.BorderLayout borderLayout4 = new java.awt.BorderLayout();
	javax.swing.JScrollPane referencesScrollPane = new javax.swing.JScrollPane();
	javax.swing.JTextArea messageArea = new javax.swing.JTextArea();
	javax.swing.border.Border border1;
	javax.swing.border.Border border2;
	javax.swing.JButton cancelButton = new javax.swing.JButton();
	java.awt.GridBagLayout gridBagLayout2 = new java.awt.GridBagLayout();
	javax.swing.JList<PropertyReference> referencesList = new javax.swing.JList<>();

	private void jbInit() {
		border1 = javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10);
		border2 = javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10);
		setLayout(borderLayout1);
		okayButton.setText("OK");
		removeReferenceButton.setText("Remove Reference");
		removeReferenceButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
				removeReferenceButton_actionPerformed(e);
			}
		});

		removeAllReferenceButton.setText("Remove All References");
		removeAllReferenceButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
				removeAllReferenceButton_actionPerformed(e);
			}
		});

		mainPanel.setLayout(gridBagLayout1);
		iconPanel.setLayout(borderLayout2);
		messagePanel.setLayout(borderLayout3);
		referencesPanel.setLayout(borderLayout4);
		messageArea.setText("Message goes here.");
		mainPanel.setBorder(border1);
		buttonPanel.setBorder(border2);
		buttonPanel.setLayout(gridBagLayout2);
		cancelButton.setText("Cancel");
		referencesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		add(buttonPanel, java.awt.BorderLayout.SOUTH);
		add(mainPanel, java.awt.BorderLayout.CENTER);
		buttonPanel.add(okayButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(8, 8, 8, 4), 0, 0));
		buttonPanel.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(8, 0, 8, 0), 0, 0));
		buttonPanel.add(removeReferenceButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(8, 0, 8, 8), 0, 0));
		buttonPanel.add(removeAllReferenceButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(8, 0, 8, 8), 0, 0));
		mainPanel.add(iconPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		mainPanel.add(messagePanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		messagePanel.add(messageArea, java.awt.BorderLayout.CENTER);
		mainPanel.add(referencesPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(10, 0, 0, 0), 0, 0));
		referencesPanel.add(referencesScrollPane, java.awt.BorderLayout.CENTER);
		referencesScrollPane.getViewport().add(referencesList, null);
	}
}