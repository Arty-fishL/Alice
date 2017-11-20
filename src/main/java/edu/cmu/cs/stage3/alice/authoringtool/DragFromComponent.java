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

package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class DragFromComponent extends JPanel
		implements edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -133787715790524483L;
	public final static int PROPERTIES_TAB = 0;
	public final static int ANIMATIONS_TAB = 1;
	public final static int QUESTIONS_TAB = 2;
	public final static int OTHER_TAB = 3;

	protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration config = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration
			.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());

	protected edu.cmu.cs.stage3.alice.core.Element element;
	protected edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor variableGroupEditor = new edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor();
	protected edu.cmu.cs.stage3.alice.authoringtool.dialog.NewResponseContentPane newResponseContentPane;
	protected edu.cmu.cs.stage3.alice.authoringtool.dialog.NewQuestionContentPane newQuestionContentPane;
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty vars;
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty responses;
	protected ResponsesListener responsesListener = new ResponsesListener();
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty questions;
	protected QuestionsListener questionsListener = new QuestionsListener();
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty poses;
	protected PosesListener posesListener = new PosesListener();
	protected GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
			GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
	protected GridBagConstraints glueConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	protected Border spacingBorder = BorderFactory.createEmptyBorder(4, 0, 8, 0);
	protected edu.cmu.cs.stage3.alice.core.event.ChildrenListener parentListener = new edu.cmu.cs.stage3.alice.core.event.ChildrenListener() {
		private edu.cmu.cs.stage3.alice.core.Element parent;

		@Override
		public void childrenChanging(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) {
			if (ev.getChild() == element
					&& ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) {
				parent = element.getParent();
			}
		}

		@Override
		public void childrenChanged(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) {
			if (ev.getChild() == element
					&& ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) {
				DragFromComponent.this.setElement(null);
				parent.removeChildrenListener(this);
			}
		}
	};
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener nameListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		@Override
		public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}

		@Override
		public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			ownerLabel.setText(ev.getValue().toString() + "'s details");
		}
	};
	protected JButton newAnimationButton = new JButton("create new method");
	protected JButton newQuestionButton = new JButton(
			"create new " + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING);
	protected JButton capturePoseButton = new JButton("capture pose");
	protected edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse newlyCreatedAnimation;
	protected edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion newlyCreatedQuestion;
	protected edu.cmu.cs.stage3.alice.core.Pose newlyCreatedPose;
	protected AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundsPanel soundsPanel;
	protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapsPanel textureMapsPanel;
	protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ObjectArrayPropertyPanel miscPanel;

	protected HashSet<JPanel> panelsToClean = new HashSet<>();

	public DragFromComponent(final AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
		variableGroupEditor.setAuthoringTool(authoringTool);
		newResponseContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.NewResponseContentPane();
		newQuestionContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.NewQuestionContentPane();
		soundsPanel = new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundsPanel(authoringTool);
		textureMapsPanel = new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapsPanel(authoringTool);
		miscPanel = new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ObjectArrayPropertyPanel("Misc",
				authoringTool);
		jbInit();
		guiInit();
	}

	private void guiInit() {
		newAnimationButton.setBackground(new Color(240, 240, 255));
		newAnimationButton.setMargin(new Insets(2, 4, 2, 4));
		newAnimationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ev) {
				if (responses != null) {
					newResponseContentPane.reset(responses.getOwner());
					final int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(newResponseContentPane);
					if (result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
						authoringTool.getUndoRedoStack().startCompound();
						try {
							final edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse response = new edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse();
							response.name.set(newResponseContentPane.getNameValue());
							responses.getOwner().addChild(response);
							responses.add(response);
						} finally {
							authoringTool.getUndoRedoStack().stopCompound();
						}
					}
				}
			}
		});
		newQuestionButton.setBackground(new Color(240, 240, 255));
		newQuestionButton.setMargin(new Insets(2, 4, 2, 4));
		newQuestionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ev) {
				if (questions != null) {
					newQuestionContentPane.reset(questions.getOwner());
					final int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(newQuestionContentPane);
					if (result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
						authoringTool.getUndoRedoStack().startCompound();
						try {
							final edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion question = new edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion();
							question.name.set(newQuestionContentPane.getNameValue());
							question.valueClass.set(newQuestionContentPane.getTypeValue());
							questions.getOwner().addChild(question);
							questions.add(question);
						} finally {
							authoringTool.getUndoRedoStack().stopCompound();
						}
					}
				}
			}
		});

		capturePoseButton.setBackground(new Color(240, 240, 255));
		capturePoseButton.setMargin(new Insets(2, 4, 2, 4));
		capturePoseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ev) {
				if (poses != null) {
					authoringTool.getUndoRedoStack().startCompound();
					try {
						final edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) poses
								.getOwner();
						final edu.cmu.cs.stage3.alice.core.Pose pose = edu.cmu.cs.stage3.alice.core.Pose
								.manufacturePose(transformable, transformable);
						pose.name.set(AuthoringToolResources.getNameForNewChild("pose", poses.getOwner()));
						poses.getOwner().addChild(pose);
						poses.add(pose);
					} finally {
						authoringTool.getUndoRedoStack().stopCompound();
					}
				}
			}
		});

		tabbedPane.setUI(new edu.cmu.cs.stage3.alice.authoringtool.util.AliceTabbedPaneUI());
		tabbedPane.setOpaque(false);
		tabbedPane.setSelectedIndex(ANIMATIONS_TAB);

		// to make tab color match
		propertiesScrollPane.setBackground(Color.white);
		animationsScrollPane.setBackground(Color.white);
		questionsScrollPane.setBackground(Color.white);
		otherScrollPane.setBackground(Color.white);

		soundsPanel.setExpanded(false);
		textureMapsPanel.setExpanded(false);
		miscPanel.setExpanded(false);

		// tooltips
		final String cappedQuestionString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING
				.substring(0, 1).toUpperCase()
				+ edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING.substring(1);
		comboPanel.setToolTipText(
				"<html><font face=arial size=-1>This area displays the details<p>of the Selected Object.</font></html>");
		tabbedPane.setToolTipTextAt(PROPERTIES_TAB,
				"<html><font face=arial size=-1>Open the Properties Tab<p>of the Selected Object.<p><p>Use this tab to view and edit<p>the Properties of the Selected Object.</font></html>");
		tabbedPane.setToolTipTextAt(ANIMATIONS_TAB,
				"<html><font face=arial size=-1>Open the Methods Tab<p>of the Selected Object.<p><p>Use this tab to view and edit<p>the Methods of the Selected Object.</font></html>");
		tabbedPane.setToolTipTextAt(QUESTIONS_TAB,
				"<html><font face=arial size=-1>Open the " + cappedQuestionString + "s"
						+ " Tab<p>of the Selected Object.<p><p>Use this tab to view and edit<p>the "
						+ cappedQuestionString + "s" + " of the Selected Object.</font></html>");
		newAnimationButton.setToolTipText(
				"<html><font face=arial size=-1>Create a New Blank Method<p>and Open it for Editing.</font></html>");
		newQuestionButton.setToolTipText("<html><font face=arial size=-1>Create a New Blank " + cappedQuestionString
				+ "<p>and Open it for Editing.</font></html>");
		propertiesPanel.setToolTipText(
				"<html><font face=arial size=-1>Properties Tab<p><p>This tab allows you to view and edit<p>the Properties of the Selected Object.</font></html>");
		animationsPanel.setToolTipText(
				"<html><font face=arial size=-1>Methods Tab<p><p>Methods are the actions that an object knows how to do.<p>Most objects come with default methods, and you can<p>create your own methods as well.</font></html>");
		questionsPanel.setToolTipText("<html><font face=arial size=-1>" + cappedQuestionString + "s" + " Tab<p><p>"
				+ cappedQuestionString + "s"
				+ " are the things that an object can<p>answer about themselves or the world.</font></html>");
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	// ElementSelectionListener interface
	@Override
	public void elementSelected(final edu.cmu.cs.stage3.alice.core.Element element) {
		setElement(element);
		authoringTool.hackStencilUpdate();
	}

	// the swing worker was removed to fix alice locking up if you don't wait
	// for her to
	// settle down after a world load. as a bonus, the every now and then
	// selection failure
	// on world load also went a away.
	// TODO: figure out why the worker was necessary in the first place
	// dennisc
	// public void elementSelected( final edu.cmu.cs.stage3.alice.core.Element
	// element ) {
	// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new
	// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
	// public Object construct() {
	// setElement( element );
	// authoringTool.hackStencilUpdate();
	// return null;
	// }
	// };
	// worker.start();
	// }

	public edu.cmu.cs.stage3.alice.core.Element getElement() {
		return element;
	}

	synchronized public void setElement(final edu.cmu.cs.stage3.alice.core.Element element) {
		vars = null;
		if (responses != null) {
			responses.removeObjectArrayPropertyListener(responsesListener);
			responses = null;
		}
		if (questions != null) {
			questions.removeObjectArrayPropertyListener(questionsListener);
			questions = null;
		}
		if (poses != null) {
			poses.removeObjectArrayPropertyListener(posesListener);
			poses = null;
		}
		if (this.element != null) {
			if (this.element.getParent() != null) {
				this.element.getParent().removeChildrenListener(parentListener);
			}
			this.element.name.removePropertyListener(nameListener);
		}

		this.element = element;

		if (element != null) {
			ownerLabel.setText(AuthoringToolResources.getReprForValue(element) + "'s details");
			if (element.getParent() != null) {
				element.getParent().addChildrenListener(parentListener);
			}
			element.name.addPropertyListener(nameListener);
			if (element.getSandbox() == element) { // HACK: only show
													// user-defined stuff for
													// sandboxes
				vars = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) element
						.getPropertyNamed("variables");
				responses = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) element
						.getPropertyNamed("responses");
				if (responses != null) {
					responses.addObjectArrayPropertyListener(responsesListener);
				}
				questions = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) element
						.getPropertyNamed("questions");
				if (questions != null) {
					questions.addObjectArrayPropertyListener(questionsListener);
				}
			}
			if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				poses = ((edu.cmu.cs.stage3.alice.core.Transformable) element).poses;
				if (poses != null) {
					poses.addObjectArrayPropertyListener(posesListener);
				}
			}
		} else {
			ownerLabel.setText("");
		}

		final int selectedIndex = tabbedPane.getSelectedIndex();
		refreshGUI();
		tabbedPane.setSelectedIndex(selectedIndex);
	}

	public void selectTab(final int index) {
		tabbedPane.setSelectedIndex(index);
	}

	public int getSelectedTab() {
		return tabbedPane.getSelectedIndex();
	}

	public String getKeyForComponent(final Component c) {
		final edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
		if (c == variableGroupEditor.getNewVariableButton()) {
			return "newVariableButton";
		} else if (c == newAnimationButton) {
			return "newAnimationButton";
		} else if (c == newQuestionButton) {
			return "newQuestionButton";
		} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
			try {
				final Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) c)
						.getTransferable();
				if (AuthoringToolResources.safeIsDataFlavorSupported(transferable,
						ElementReferenceTransferable.variableReferenceFlavor)) {
					final edu.cmu.cs.stage3.alice.core.Variable v = (edu.cmu.cs.stage3.alice.core.Variable) transferable
							.getTransferData(ElementReferenceTransferable.variableReferenceFlavor);
					return "variable<" + v.getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable,
						ElementReferenceTransferable.textureMapReferenceFlavor)) {
					final edu.cmu.cs.stage3.alice.core.TextureMap t = (edu.cmu.cs.stage3.alice.core.TextureMap) transferable
							.getTransferData(ElementReferenceTransferable.textureMapReferenceFlavor);
					return "textureMap<" + t.getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable,
						AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.Sound.class))) {
					final edu.cmu.cs.stage3.alice.core.Sound s = (edu.cmu.cs.stage3.alice.core.Sound) transferable
							.getTransferData(AuthoringToolResources
									.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.Sound.class));
					return "sound<" + s.getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable,
						ElementReferenceTransferable.elementReferenceFlavor)) {
					final edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element) transferable
							.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
					return "misc<" + e.getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable,
						edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
					final edu.cmu.cs.stage3.alice.core.Property p = (edu.cmu.cs.stage3.alice.core.Property) transferable
							.getTransferData(
									edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
					return "property<" + p.getName() + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable,
						edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
					final edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype) transferable
							.getTransferData(
									edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor);
					return "userDefinedResponse<" + p.getActualResponse().getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable,
						edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
					final edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype) transferable
							.getTransferData(
									edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor);
					return "userDefinedQuestion<" + p.getActualQuestion().getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable,
						edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
					final ElementPrototype p = (ElementPrototype) transferable.getTransferData(
							edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
					if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(p.getElementClass())) {
						return "responsePrototype<" + p.getElementClass().getName() + ">";
					} else if (edu.cmu.cs.stage3.alice.core.Question.class.isAssignableFrom(p.getElementClass())) {
						return "questionPrototype<" + p.getElementClass().getName() + ">";
					} else {
						return null;
					}
				} else {
					return null;
				}
			} catch (final Exception e) {
				AuthoringTool.showErrorDialog("Error examining DnDGroupingPanel.", e);
				return null;
			}
		} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton) {
			final Object o = ((edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton) c).getObject();
			if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
				final edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element) o;
				return "editObjectButton<" + e.getKey(world) + ">";
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public Component getComponentForKey(final String key) {
		final String prefix = AuthoringToolResources.getPrefix(key);
		final String spec = AuthoringToolResources.getSpecifier(key);
		final edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
		if (key.equals("newVariableButton")) {
			return variableGroupEditor.getNewVariableButton();
		} else if (key.equals("newAnimationButton")) {
			return newAnimationButton;
		} else if (key.equals("newQuestionButton")) {
			return newQuestionButton;
		} else if (prefix.equals("variable") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(variableGroupEditor, e);
			}
		} else if (prefix.equals("textureMap") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(textureMapsPanel, e);
			}
		} else if (prefix.equals("sound") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(soundsPanel, e);
			}
		} else if (prefix.equals("misc") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(miscPanel, e);
			}
		} else if (prefix.equals("property") && spec != null) {
			return AuthoringToolResources.findPropertyDnDPanel(propertiesPanel, element, spec);
		} else if (prefix.equals("userDefinedResponse") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Response actualResponse = (edu.cmu.cs.stage3.alice.core.Response) world
					.getDescendantKeyed(spec);
			if (actualResponse != null) {
				return AuthoringToolResources.findUserDefinedResponseDnDPanel(animationsPanel, actualResponse);
			}
		} else if (prefix.equals("userDefinedQuestion") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Question actualQuestion = (edu.cmu.cs.stage3.alice.core.Question) world
					.getDescendantKeyed(spec);
			if (actualQuestion != null) {
				return AuthoringToolResources.findUserDefinedQuestionDnDPanel(questionsPanel, actualQuestion);
			}
		} else if (prefix.equals("responsePrototype") && spec != null) {
			try {
				final Class<?> elementClass = Class.forName(spec);
				if (elementClass != null) {
					return AuthoringToolResources.findPrototypeDnDPanel(animationsPanel, elementClass);
				}
			} catch (final Exception e) {
				AuthoringTool.showErrorDialog("Error while looking for ProtoypeDnDPanel using class " + spec, e);
			}
		} else if (prefix.equals("questionPrototype") && spec != null) {
			try {
				final Class<?> elementClass = Class.forName(spec);
				if (elementClass != null) {
					return AuthoringToolResources.findPrototypeDnDPanel(questionsPanel, elementClass);
				}
			} catch (final Exception e) {
				AuthoringTool.showErrorDialog("Error while looking for ProtoypeDnDPanel using class " + spec, e);
			}
		} else if (prefix.equals("editObjectButton") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				if (e instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
					return AuthoringToolResources.findEditObjectButton(animationsPanel, e);
				} else if (e instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
					return AuthoringToolResources.findEditObjectButton(questionsPanel, e);
				} else {
					return AuthoringToolResources.findEditObjectButton(propertiesPanel, e);
				}
			}
		}

		return null;
	}

	public Component getPropertyViewComponentForKey(final String key) {
		final String prefix = AuthoringToolResources.getPrefix(key);
		final String spec = AuthoringToolResources.getSpecifier(key);
		final edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
		if (key.equals("newVariableButton")) {
			return variableGroupEditor.getNewVariableButton();
		} else if (key.equals("newAnimationButton")) {
			return newAnimationButton;
		} else if (key.equals("newQuestionButton")) {
			return newQuestionButton;
		} else if (prefix.equals("variable") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findPropertyViewController(variableGroupEditor, e, "value");
			}
		} else if (prefix.equals("textureMap") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(textureMapsPanel, e);
			}
		} else if (prefix.equals("sound") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(soundsPanel, e);
			}
		} else if (prefix.equals("misc") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(miscPanel, e);
			}
		} else if (prefix.equals("property") && spec != null) {
			return AuthoringToolResources.findPropertyViewController(propertiesPanel, element, spec);
		} else if (prefix.equals("userDefinedResponse") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Response actualResponse = (edu.cmu.cs.stage3.alice.core.Response) world
					.getDescendantKeyed(spec);
			if (actualResponse != null) {
				return AuthoringToolResources.findUserDefinedResponseDnDPanel(animationsPanel, actualResponse);
			}
		} else if (prefix.equals("userDefinedQuestion") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Question actualQuestion = (edu.cmu.cs.stage3.alice.core.Question) world
					.getDescendantKeyed(spec);
			if (actualQuestion != null) {
				return AuthoringToolResources.findUserDefinedQuestionDnDPanel(questionsPanel, actualQuestion);
			}
		} else if (prefix.equals("responsePrototype") && spec != null) {
			try {
				final Class<?> elementClass = Class.forName(spec);
				if (elementClass != null) {
					return AuthoringToolResources.findPrototypeDnDPanel(animationsPanel, elementClass);
				}
			} catch (final Exception e) {
				AuthoringTool.showErrorDialog("Error while looking for ProtoypeDnDPanel using class " + spec, e);
			}
		} else if (prefix.equals("questionPrototype") && spec != null) {
			try {
				final Class<?> elementClass = Class.forName(spec);
				if (elementClass != null) {
					return AuthoringToolResources.findPrototypeDnDPanel(questionsPanel, elementClass);
				}
			} catch (final Exception e) {
				AuthoringTool.showErrorDialog("Error while looking for ProtoypeDnDPanel using class " + spec, e);
			}
		} else if (prefix.equals("editObjectButton") && spec != null) {
			final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				if (e instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
					return AuthoringToolResources.findEditObjectButton(animationsPanel, e);
				} else if (e instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
					return AuthoringToolResources.findEditObjectButton(questionsPanel, e);
				} else {
					return AuthoringToolResources.findEditObjectButton(propertiesPanel, e);
				}
			}
		}

		return null;
	}

	protected void cleanPanels() {
		for (final Iterator<JPanel> iter = panelsToClean.iterator(); iter.hasNext();) {
			final JPanel panel = iter.next();
			final Component[] children = panel.getComponents();
			for (final Component element2 : children) {
				if (element2 instanceof edu.cmu.cs.stage3.alice.authoringtool.util.Releasable) {
					((edu.cmu.cs.stage3.alice.authoringtool.util.Releasable) element2).release();
				}
			}
			panel.removeAll();
		}
		panelsToClean.clear();
	}

	synchronized public void refreshGUI() {
		cleanPanels();
		propertiesPanel.removeAll();
		animationsPanel.removeAll();
		questionsPanel.removeAll();
		if (element != null) {
			constraints.gridy = 0;
			// Variable panel
			if (vars != null) {
				variableGroupEditor.setVariableObjectArrayProperty(vars);
				propertiesPanel.add(variableGroupEditor, constraints);
				constraints.gridy++;
			}

			// poses
			if (poses != null) {
				final JPanel subPanel = new JPanel();
				subPanel.setBackground(Color.white);
				subPanel.setLayout(new GridBagLayout());
				subPanel.setBorder(spacingBorder);
				panelsToClean.add(subPanel);

				int count = 0;
				final Object[] poseArray = poses.getArrayValue();
				for (final Object element2 : poseArray) {
					final edu.cmu.cs.stage3.alice.core.Pose pose = (edu.cmu.cs.stage3.alice.core.Pose) element2;

					final JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(pose);
					if (gui != null) {
						final GridBagConstraints constraints = new GridBagConstraints(0, count, 1, 1, 1.0, 0.0,
								GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0);
						subPanel.add(gui, constraints);
						count++;
						if (newlyCreatedPose == pose
								&& gui instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel) {
							((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel) gui).editName();
							newlyCreatedPose = null;
						}
					} else {
						AuthoringTool.showErrorDialog("Unable to create gui for pose: " + pose, null);
					}
				}

				final GridBagConstraints c = new GridBagConstraints(0, count, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(4, 2, 2, 2), 0, 0);
				subPanel.add(capturePoseButton, c);

				propertiesPanel.add(subPanel, constraints);
				constraints.gridy++;
			}

			// property panels
			final Vector<StringObjectPair> propertyStructure = AuthoringToolResources.getPropertyStructure(element, false);
			if (propertyStructure != null) {
				for (final Iterator<StringObjectPair> iter = propertyStructure.iterator(); iter.hasNext();) {
					final StringObjectPair sop = (StringObjectPair) iter.next();
					final String groupName = sop.getString();
					@SuppressWarnings("unchecked")
					final Vector<String> propertyNames = (Vector<String>) sop.getObject();

					final JPanel subPanel = new JPanel();
					JPanel toAdd = subPanel;
					subPanel.setBackground(Color.white);
					subPanel.setLayout(new GridBagLayout());
					subPanel.setBorder(spacingBorder);
					panelsToClean.add(subPanel);
					if (groupName.compareTo("seldom used properties") == 0) {
						final edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel expandPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel();
						expandPanel.setTitle("Seldom Used Properties");
						expandPanel.setContent(subPanel);
						expandPanel.setExpanded(false);
						toAdd = expandPanel;
					}

					if (propertyNames != null) {
						int i = 0;
						for (final Iterator<String> jter = propertyNames.iterator(); jter.hasNext();) {
							final String name = (String) jter.next();
							final edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(name);
							if (property != null) {
								final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory oneShotFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
									@Override
									public Object createItem(final Object o) {
										return new Runnable() {
											@Override
											public void run() {
												if (property
														.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Transformable
														&& property == ((edu.cmu.cs.stage3.alice.core.Transformable) property
																.getOwner()).vehicle) {
													((edu.cmu.cs.stage3.alice.core.Transformable) property.getOwner())
															.setVehiclePreservingAbsoluteTransformation(
																	(edu.cmu.cs.stage3.alice.core.ReferenceFrame) o);
												} else if (property.getName().equals("localTransformation")) { // Animate
																												// and
																												// undo
																												// the
																												// point
																												// of
																												// view
																												// when
																												// set
													final edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation povAnim = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
													povAnim.subject.set(property.getOwner());
													povAnim.pointOfView.set(o);
													povAnim.asSeenBy.set(element.getParent());
													final edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
													undoResponse.subject.set(property.getOwner());
													undoResponse.pointOfView
															.set(((edu.cmu.cs.stage3.alice.core.Transformable) property
																	.getOwner()).localTransformation
																			.getMatrix4dValue());
													undoResponse.asSeenBy
															.set(((edu.cmu.cs.stage3.alice.core.Transformable) property
																	.getOwner()).vehicle.getValue());
													final edu.cmu.cs.stage3.alice.core.Property[] properties = new edu.cmu.cs.stage3.alice.core.Property[] {
															((edu.cmu.cs.stage3.alice.core.Transformable) property
																	.getOwner()).localTransformation };
													authoringTool.performOneShot(povAnim, undoResponse, properties);
												} else {
													final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation response = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
													response.element.set(property.getOwner());
													response.propertyName.set(property.getName());
													response.value.set(o);
													final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
													undoResponse.element.set(property.getOwner());
													undoResponse.propertyName.set(property.getName());
													undoResponse.value.set(property.getValue());
													// this is over-reaching for
													// some properties
													final Vector<Property> pVector = new Vector<Property>();
													pVector.add(property);
													final edu.cmu.cs.stage3.alice.core.Element[] descendants = property
															.getOwner().getDescendants();
													for (final Element descendant : descendants) {
														final edu.cmu.cs.stage3.alice.core.Property p = descendant
																.getPropertyNamed(property.getName());
														if (p != null) {
															pVector.add(p);
														}
													}
													final edu.cmu.cs.stage3.alice.core.Property[] properties = pVector
															.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]);
													authoringTool.performOneShot(response, undoResponse, properties);
												}
											}
										};
									}
								};
								final JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
										.getPropertyGUI(property, true, false, oneShotFactory);
								if (gui != null) {
									final GridBagConstraints constraints = new GridBagConstraints(0, i, 1, 1, 1.0, 0.0,
											GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0,
											0);
									subPanel.add(gui, constraints);
									i++;
								} else {
									AuthoringTool.showErrorDialog("Unable to create gui for property: " + property,
											null);
								}
							} else {
								AuthoringTool.showErrorDialog("no property on " + element + " named " + name, null);
							}
						}
					}

					propertiesPanel.add(toAdd, constraints);
					constraints.gridy++;
				}
			}

			// sounds/texture/misc
			if (element instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				final edu.cmu.cs.stage3.alice.core.Sandbox sandbox = (edu.cmu.cs.stage3.alice.core.Sandbox) element;
				soundsPanel.setSounds(sandbox.sounds);
				propertiesPanel.add(soundsPanel, constraints);
				constraints.gridy++;
				textureMapsPanel.setTextureMaps(sandbox.textureMaps);
				propertiesPanel.add(textureMapsPanel, constraints);
				constraints.gridy++;
				if (sandbox.misc.size() > 0) {
					miscPanel.setObjectArrayProperty(sandbox.misc);
					propertiesPanel.add(miscPanel, constraints);
					constraints.gridy++;
				}
				propertiesPanel.add(Box.createVerticalStrut(8), constraints);
				constraints.gridy++;
			}

			if (element.data.get("modeled by") != null) {
				propertiesPanel.add(new JLabel("modeled by:  " + element.data.get("modeled by")), constraints);
				constraints.gridy++;
			}
			if (element.data.get("painted by") != null) {
				propertiesPanel.add(new JLabel("painted by:  " + element.data.get("painted by")), constraints);
				constraints.gridy++;
			}
			if (element.data.get("programmed by") != null) {
				propertiesPanel.add(new JLabel("programmed by:  " + element.data.get("programmed by")), constraints);
				constraints.gridy++;
			}
			if (element.data.get("modeled by") != null) {
				final java.text.NumberFormat formatter = new java.text.DecimalFormat("#.####");
				propertiesPanel.add(
						new JLabel("depth:  "
								+ formatter.format(((edu.cmu.cs.stage3.alice.core.Model) element).getDepth())),
						constraints);
				constraints.gridy++;
				propertiesPanel.add(
						new JLabel("height:  "
								+ formatter.format(((edu.cmu.cs.stage3.alice.core.Model) element).getHeight())),
						constraints);
				constraints.gridy++;
				propertiesPanel.add(
						new JLabel("width:  "
								+ formatter.format(((edu.cmu.cs.stage3.alice.core.Model) element).getWidth())),
						constraints);
				constraints.gridy++;
			}
			glueConstraints.gridy = constraints.gridy;
			propertiesPanel.add(Box.createGlue(), glueConstraints);

			constraints.gridy = 0;

			// user-defined responses
			if (responses != null) {
				final JPanel subPanel = new JPanel();
				subPanel.setBackground(Color.white);
				subPanel.setLayout(new GridBagLayout());
				subPanel.setBorder(spacingBorder);
				panelsToClean.add(subPanel);

				int count = 0;
				final Object[] responseArray = responses.getArrayValue();
				for (final Object element2 : responseArray) {
					// Unused ?? final Class<CallToUserDefinedResponse> responseClass = edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class;
					final edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response) element2;

					if (response instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
						final edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype(
								(edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) response);
						final JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
								.getGUI(callToUserDefinedResponsePrototype);
						if (gui != null) {
							final edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton editButton = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
									.getEditObjectButton(response, gui);
							editButton.setToolTipText(
									"<html><font face=arial size=-1>Open this method for editing.</font></html>");
							final JPanel guiPanel = new JPanel();
							panelsToClean.add(guiPanel);
							guiPanel.setBackground(Color.white);
							guiPanel.setLayout(new GridBagLayout());
							guiPanel.add(gui, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							guiPanel.add(editButton,
									new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHWEST,
											GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
							final GridBagConstraints constraints = new GridBagConstraints(0, count, 1, 1, 1.0, 0.0,
									GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0);
							subPanel.add(guiPanel, constraints);
							count++;
							if (newlyCreatedAnimation == response
									&& gui instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel) {
								// ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel)gui).editName();
								authoringTool.editObject(newlyCreatedAnimation);
								newlyCreatedAnimation = null;
							}
						} else {
							AuthoringTool
									.showErrorDialog("Unable to create gui for callToUserDefinedResponsePrototype: "
											+ callToUserDefinedResponsePrototype, null);
						}
					} else {
						AuthoringTool.showErrorDialog("Response is not a userDefinedResponse: " + response, null);
					}
				}

				final GridBagConstraints c = new GridBagConstraints(0, count, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(4, 2, 2, 2), 0, 0);
				subPanel.add(newAnimationButton, c);

				animationsPanel.add(subPanel, constraints);
				constraints.gridy++;
			}

			// response panels
			final Vector<Object> oneShotStructure = AuthoringToolResources.getOneShotStructure(element.getClass());
			if (oneShotStructure != null) {
				for (final Iterator<Object> iter = oneShotStructure.iterator(); iter.hasNext();) {
					final StringObjectPair sop = (StringObjectPair) iter.next();
					// Unused ?? final String groupName = sop.getString();
					@SuppressWarnings("unchecked")
					final Vector<String> responseNames = (Vector<String>) sop.getObject();
					final JPanel subPanel = new JPanel();
					final JPanel toAdd = subPanel;
					subPanel.setBackground(Color.white);
					subPanel.setLayout(new GridBagLayout());
					subPanel.setBorder(spacingBorder);
					panelsToClean.add(subPanel);

					if (responseNames != null) {

						int i = 0;
						for (final Iterator<String> jter = responseNames.iterator(); jter.hasNext();) {
							final Object item = jter.next();
							if (item instanceof String) { // ignore hierarchy
															// for now
								final String className = (String) item;
								try {
									if (!className
											.startsWith("edu.cmu.cs.stage3.alice.core.response.PropertyAnimation")) { // ignore
																														// property
																														// animations
																														// for
																														// now
										final Class<?> responseClass = Class.forName(className);
										final LinkedList<StringObjectPair> known = new LinkedList<StringObjectPair>();
										final String format = AuthoringToolResources.getFormat(responseClass);
										final edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(
												format);
										while (tokenizer.hasMoreTokens()) {
											final String token = tokenizer.nextToken();
											if (token.startsWith("<<<") && token.endsWith(">>>")) {
												final String propertyName = token.substring(token.lastIndexOf("<") + 1,
														token.indexOf(">"));
												known.add(new StringObjectPair(propertyName, element));
											}
										}
										final StringObjectPair[] knownPropertyValues = known
												.toArray(new StringObjectPair[0]);

										final String[] desiredProperties = AuthoringToolResources
												.getDesiredProperties(responseClass);
										@SuppressWarnings("unchecked")
										final edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype = 
												new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(
												(Class<? extends Element>) responseClass, knownPropertyValues, desiredProperties);
										final JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
												.getGUI(responsePrototype);
										if (gui != null) {
											final GridBagConstraints constraints = new GridBagConstraints(0, i, 1, 1,
													1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
													new Insets(2, 2, 2, 2), 0, 0);
											subPanel.add(gui, constraints);
											i++;
										} else {
											AuthoringTool.showErrorDialog(
													"Unable to create gui for responsePrototype: " + responsePrototype,
													null);
										}
									}
								} catch (final ClassNotFoundException e) {
									AuthoringTool.showErrorDialog("Error while looking for class " + className, e);
								}
							}
						}
					}

					animationsPanel.add(toAdd, constraints);
					constraints.gridy++;
				}
			}
			glueConstraints.gridy = constraints.gridy;
			animationsPanel.add(Box.createGlue(), glueConstraints);

			// user-defined questions
			constraints.gridy = 0;
			if (questions != null) {
				final JPanel subPanel = new JPanel();
				subPanel.setBackground(Color.white);
				subPanel.setLayout(new GridBagLayout());
				subPanel.setBorder(spacingBorder);
				panelsToClean.add(subPanel);

				int count = 0;
				final Object[] questionsArray = questions.getArrayValue();
				for (final Object element2 : questionsArray) {
					// Unused ?? final Class<CallToUserDefinedQuestion> questionClass = edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class;
					final edu.cmu.cs.stage3.alice.core.Question question = (edu.cmu.cs.stage3.alice.core.Question) element2;

					if (question instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
						final edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype(
								(edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) question);
						final JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
								.getGUI(callToUserDefinedQuestionPrototype);
						if (gui != null) {
							final edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton editButton = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
									.getEditObjectButton(question, gui);
							editButton.setToolTipText(
									"<html><font face=arial size=-1>Open this question for editing.</font></html>");
							final JPanel guiPanel = new JPanel();
							panelsToClean.add(guiPanel);
							guiPanel.setBackground(Color.white);
							guiPanel.setLayout(new GridBagLayout());
							guiPanel.add(gui, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							guiPanel.add(editButton,
									new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHWEST,
											GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
							final GridBagConstraints constraints = new GridBagConstraints(0, count, 1, 1, 1.0, 0.0,
									GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0);
							subPanel.add(guiPanel, constraints);
							count++;
							if (newlyCreatedQuestion == question) {
								authoringTool.editObject(newlyCreatedQuestion);
								newlyCreatedQuestion = null;
							}
						} else {
							AuthoringTool
									.showErrorDialog("Unable to create gui for callToUserDefinedQuestionPrototype: "
											+ callToUserDefinedQuestionPrototype, null);
						}
					} else {
						throw new RuntimeException("ERROR: question is not a userDefinedQuestion");
					}
				}

				final GridBagConstraints c = new GridBagConstraints(0, count, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(4, 2, 2, 2), 0, 0);
				subPanel.add(newQuestionButton, c);

				questionsPanel.add(subPanel, constraints);
				constraints.gridy++;
			}

			// question panels
			final Vector<Object> questionStructure = AuthoringToolResources.getQuestionStructure(element.getClass());
			if (questionStructure != null) {
				for (final Iterator<Object> iter = questionStructure.iterator(); iter.hasNext();) {
					final StringObjectPair sop = (StringObjectPair) iter.next();
					final String groupName = sop.getString();
					@SuppressWarnings("unchecked")
					final Vector<String> questionNames = (Vector<String>) sop.getObject();

					final JPanel subPanel = new JPanel();
					subPanel.setBackground(Color.white);
					subPanel.setLayout(new GridBagLayout());
					panelsToClean.add(subPanel);

					final edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel expandPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel();
					expandPanel.setTitle(groupName);
					expandPanel.setContent(subPanel);

					if (questionNames != null) {
						int i = 0;
						for (final Iterator<String> jter = questionNames.iterator(); jter.hasNext();) {
							final String className = (String) jter.next();
							try {
								final Class<?> questionClass = Class.forName(className);
								@SuppressWarnings("unused")
								final edu.cmu.cs.stage3.alice.core.Question tempQuestion = // Unused ??
										(edu.cmu.cs.stage3.alice.core.Question) questionClass.newInstance();
								final LinkedList<StringObjectPair> known = new LinkedList<StringObjectPair>();
								final String format = AuthoringToolResources.getFormat(questionClass);
								final edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(
										format);
								while (tokenizer.hasMoreTokens()) {
									final String token = tokenizer.nextToken();
									if (token.startsWith("<<<") && token.endsWith(">>>")) {
										final String propertyName = token.substring(token.lastIndexOf("<") + 1,
												token.indexOf(">"));
										known.add(new StringObjectPair(propertyName, element));
									}
								}
								if (edu.cmu.cs.stage3.alice.core.question.PartKeyed.class
										.isAssignableFrom(questionClass)) { // special
																			// case
																			// hack
									known.add(new StringObjectPair("key", ""));
								}
								final StringObjectPair[] knownPropertyValues = known
										.toArray(new StringObjectPair[0]);

								String[] desiredProperties = AuthoringToolResources.getDesiredProperties(questionClass);
								if (edu.cmu.cs.stage3.alice.core.question.PartKeyed.class
										.isAssignableFrom(questionClass)) { // special
																			// case
																			// hack
									desiredProperties = new String[0];
								}
								@SuppressWarnings("unchecked")
								final ElementPrototype elementPrototype = new ElementPrototype((Class<? extends Element>) questionClass,
										knownPropertyValues, desiredProperties);
								final JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
										.getGUI(elementPrototype);
								if (gui != null) {
									final GridBagConstraints constraints = new GridBagConstraints(0, i, 1, 1, 1.0, 0.0,
											GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0,
											0);
									subPanel.add(gui, constraints);
									i++;
								} else {
									AuthoringTool.showErrorDialog(
											"Unable to create gui for elementPrototype: " + elementPrototype, null);
								}
							} catch (final ClassNotFoundException e) {
								AuthoringTool.showErrorDialog("Unable to create gui for class: " + className, e);
							} catch (final IllegalAccessException e) {
								AuthoringTool.showErrorDialog("Unable to create gui for class: " + className, e);
							} catch (final InstantiationException e) {
								AuthoringTool.showErrorDialog("Unable to create gui for class: " + className, e);
							}
						}
					}

					questionsPanel.add(expandPanel, constraints);
					constraints.gridy++;
				}
			}
			glueConstraints.gridy = constraints.gridy;
			questionsPanel.add(Box.createGlue(), glueConstraints);

			// other panels
			// constraints.gridy = 0;
			// if( element instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
			// edu.cmu.cs.stage3.alice.core.Sandbox sandbox =
			// (edu.cmu.cs.stage3.alice.core.Sandbox)element;
			// soundsPanel.setSounds( sandbox.sounds );
			// otherPanel.add( soundsPanel, constraints );
			// constraints.gridy++;
			// textureMapsPanel.setTextureMaps( sandbox.textureMaps );
			// otherPanel.add( textureMapsPanel, constraints );
			// constraints.gridy++;
			// miscPanel.setObjectArrayProperty( sandbox.misc );
			// otherPanel.add( miscPanel, constraints );
			// constraints.gridy++;
			// }
			// glueConstraints.gridy = constraints.gridy;
			// otherPanel.add( Box.createGlue(), glueConstraints );
		}
		revalidate();
		repaint();
	}

	public class ResponsesListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		@Override
		public void objectArrayPropertyChanging(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		}

		@Override
		public void objectArrayPropertyChanged(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
			if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED) {
				newlyCreatedAnimation = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) ev.getItem();
			}
			final int selectedIndex = tabbedPane.getSelectedIndex();
			refreshGUI();
			tabbedPane.setSelectedIndex(selectedIndex);
		}
	}

	public class QuestionsListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		@Override
		public void objectArrayPropertyChanging(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		}

		@Override
		public void objectArrayPropertyChanged(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
			if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED) {
				newlyCreatedQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) ev
						.getItem();
			}
			final int selectedIndex = tabbedPane.getSelectedIndex();
			refreshGUI();
			tabbedPane.setSelectedIndex(selectedIndex);
		}
	}

	public class PosesListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		@Override
		public void objectArrayPropertyChanging(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		}

		@Override
		public void objectArrayPropertyChanged(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
			if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED) {
				newlyCreatedPose = (edu.cmu.cs.stage3.alice.core.Pose) ev.getItem();
			}
			final int selectedIndex = tabbedPane.getSelectedIndex();
			refreshGUI();
			tabbedPane.setSelectedIndex(selectedIndex);
		}
	}

	// //////////////////
	// Autogenerated
	// //////////////////

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel propertySubPanel = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	Border border1;
	// JScrollPane propertyScrollPane = new JScrollPane();
	Border border2;
	Border border3;
	Border border4;
	Border border5;
	Border border6;
	Border border7;
	JPanel comboPanel = new JPanel();
	JLabel ownerLabel = new JLabel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JTabbedPane tabbedPane = new JTabbedPane();
	JScrollPane propertiesScrollPane = new JScrollPane();
	JScrollPane animationsScrollPane = new JScrollPane();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JPanel propertiesPanel = new JPanel();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	JPanel animationsPanel = new JPanel();
	JScrollPane questionsScrollPane = new JScrollPane();
	JPanel questionsPanel = new JPanel();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	JScrollPane otherScrollPane = new JScrollPane();
	JPanel otherPanel = new JPanel();
	GridBagLayout gridBagLayout5 = new GridBagLayout();
	Border border8;
	Border border9;

	private void jbInit() {
		border1 = BorderFactory.createEmptyBorder(2, 0, 0, 0);
		border2 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
		border3 = BorderFactory.createCompoundBorder(border2, border1);
		border4 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		border5 = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		border6 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
		border7 = BorderFactory.createCompoundBorder(border6, border5);
		border8 = BorderFactory.createEmptyBorder();
		border9 = BorderFactory.createLineBorder(Color.black, 1);
		setLayout(borderLayout1);
		propertySubPanel.setLayout(borderLayout2);
		propertySubPanel.setBorder(border1);
		propertySubPanel.setMinimumSize(new Dimension(0, 0));
		propertySubPanel.setOpaque(false);
		setBackground(new Color(204, 204, 204));
		setMinimumSize(new Dimension(0, 0));
		borderLayout2.setHgap(8);
		borderLayout2.setVgap(6);
		comboPanel.setLayout(gridBagLayout1);
		ownerLabel.setForeground(Color.black);
		ownerLabel.setText("owner\'s details");
		propertiesPanel.setBackground(Color.white);
		propertiesPanel.setLayout(gridBagLayout2);
		animationsPanel.setBackground(Color.white);
		animationsPanel.setLayout(gridBagLayout3);
		questionsPanel.setBackground(Color.white);
		questionsPanel.setLayout(gridBagLayout4);
		otherPanel.setBackground(Color.white);
		otherPanel.setLayout(gridBagLayout5);
		propertiesScrollPane.getViewport().setBackground(Color.white);
		propertiesScrollPane.setBorder(null);
		animationsScrollPane.getViewport().setBackground(Color.white);
		animationsScrollPane.setBorder(null);
		questionsScrollPane.getViewport().setBackground(Color.white);
		questionsScrollPane.setBorder(null);
		otherScrollPane.getViewport().setBackground(Color.white);
		otherScrollPane.setBorder(null);
		comboPanel.setOpaque(false);
		this.add(propertySubPanel, BorderLayout.CENTER);
		propertySubPanel.add(comboPanel, BorderLayout.NORTH);
		comboPanel.add(ownerLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 8, 0, 0), 0, 0));
		propertySubPanel.add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.add(propertiesScrollPane, "properties");
		propertiesScrollPane.getViewport().add(propertiesPanel, null);
		tabbedPane.add(animationsScrollPane, "methods");
		tabbedPane.add(questionsScrollPane,
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING + "s");
		// tabbedPane.add(otherScrollPane, "other");
		otherScrollPane.getViewport().add(otherPanel, null);
		questionsScrollPane.getViewport().add(questionsPanel, null);
		animationsScrollPane.getViewport().add(animationsPanel, null);
	}
}