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

/**
 * @author Jason Pratt, David Culyba, Dennis Cosgrove
 */

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jamiegl.alicex.ui.JSystemFileChooser;

import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter;

class CustomCheckBox extends JCheckBox implements java.awt.image.ImageObserver {
	private static final long serialVersionUID = 8159237289542332665L;
	
	@SuppressWarnings("unused")
	private int index = 0;
	/* private */java.awt.Image image;
	/* private */JComponent gui;
	/* private */Object object;

	public void setIndex(final int index) {
		this.index = index;
	}

	@Override
	public void paint(final java.awt.Graphics g) {
		super.paint(g);
		if (image != null) {
			g.drawImage(image, 14, 0, java.awt.Color.white, this);
		}
	}

	@Override
	public java.awt.Dimension getPreferredSize() {
		if (image == null) {
			return super.getPreferredSize();
		} else {
			final int x = image.getWidth(this);
			final int y = image.getHeight(this);
			return new java.awt.Dimension(x + 14, y);
		}
	}

	@Override
	public java.awt.Dimension getMinimumSize() {
		if (image == null) {
			return super.getMinimumSize();
		} else {
			final int x = image.getWidth(this);
			final int y = image.getHeight(this);
			return new java.awt.Dimension(x + 14, y);
		}
	}

	@Override
	public java.awt.Dimension getMaximumSize() {
		if (image == null) {
			return super.getMaximumSize();
		} else {
			final int x = image.getWidth(this);
			final int y = image.getHeight(this);
			return new java.awt.Dimension(x + 14, y);
		}
	}

	@Override
	public boolean imageUpdate(final java.awt.Image img, final int infoflags, final int x, final int y, final int width,
			final int height) {
		return true;
	}
}

class CustomListButton extends JButton implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -6791947644115470260L;
	private final Vector<CustomCheckBox> checkBoxes = new Vector<>();

	public CustomListButton() {
		addActionListener(this);
		setHorizontalAlignment(SwingConstants.LEFT);
		// this.setBorder(null);
	}

	public void addCheckBox(final CustomCheckBox c) {
		checkBoxes.add(c);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		boolean areAllSelected = true;
		for (int i = 0; i < checkBoxes.size(); i++) {
			final CustomCheckBox currentCheckBox = (CustomCheckBox) checkBoxes.get(i);
			if (!currentCheckBox.isSelected()) {
				areAllSelected = false;
				break;
			}
		}
		if (areAllSelected) {
			for (int i = 0; i < checkBoxes.size(); i++) {
				final CustomCheckBox currentCheckBox = (CustomCheckBox) checkBoxes.get(i);
				currentCheckBox.setSelected(false);
			}
		} else {
			for (int i = 0; i < checkBoxes.size(); i++) {
				final CustomCheckBox currentCheckBox = (CustomCheckBox) checkBoxes.get(i);
				currentCheckBox.setSelected(true);
			}
		}
	}
}

public class ExportCodeForPrintingContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	/**
	 *
	 */
	private static final long serialVersionUID = 7624340118756537381L;

	private final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool m_authoringTool;

	private final JFileChooser m_pathFileChooser = new JSystemFileChooser();

	private final JTextField m_authorNameTextField = new JTextField();
	private final JTextField m_pathTextField = new JTextField();
	private final JPanel m_elementsToBeExportedPanel = new JPanel();

	private final Vector<ActionListener> m_okActionListeners = new Vector<>();
	private final JButton m_exportButton = new JButton("Export Code");
	private final JButton m_cancelButton = new JButton("Cancel");

	public ExportCodeForPrintingContentPane(final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		m_authoringTool = authoringTool;

		final ArrayList<ExtensionFileFilter> extensions = new ArrayList<>();
		extensions.add(new ExtensionFileFilter("htm", "*.htm"));
		extensions.add(new ExtensionFileFilter("html", "*.html"));
		m_pathFileChooser.setFileFilter(
				new ExtensionGroupFileFilter(extensions, "Web pages"));

		final Configuration authoringToolConfig = Configuration
				.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());
		final String path = authoringToolConfig.getValue("directories.worldsDirectory");
		if (path != null) {
			final java.io.File dir = new java.io.File(path);
			if (dir != null && dir.exists() && dir.isDirectory()) {
				try {
					m_pathFileChooser.setCurrentDirectory(dir);
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					// for some reason this can potentially fail in jdk1.4.2_04
				} catch (final java.lang.IndexOutOfBoundsException e) {
					// and on JDK 1.6 it's this one (added by Michael Vorburger)
					// - just sometimes
				}
			}
		}

		m_pathFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		m_pathFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		m_pathFileChooser.setApproveButtonText("Set File");

		final JButton selectAllButton = new JButton("Select All");
		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ExportCodeForPrintingContentPane.this.setAllSelected(true);
			}
		});

		final JButton deselectAllButton = new JButton("Deselect All");
		deselectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ExportCodeForPrintingContentPane.this.setAllSelected(false);
			}
		});

		m_authorNameTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				m_exportButton.doClick();
			}
		});

		m_pathTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				m_exportButton.doClick();
			}
		});

		final JButton browseButton = new JButton("Browse...");
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ExportCodeForPrintingContentPane.this.handleBrowseButton();
			}
		});

		m_exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ExportCodeForPrintingContentPane.this.handleExportButton();
			}
		});

		final JScrollPane whatToPrintScrollPane = new JScrollPane(m_elementsToBeExportedPanel);
		m_elementsToBeExportedPanel.setLayout(new java.awt.GridBagLayout());
		m_elementsToBeExportedPanel.setBackground(java.awt.Color.white);
		// int height = 200;
		// int width = edu.cmu.cs.stage3.math.GoldenRatio.getLongerSideLength(
		// height );
		// m_elementsToBeExportedPanel.setPreferredSize( new java.awt.Dimension(
		// width, height ) );

		final JPanel selectPanel = new JPanel();
		selectPanel.setLayout(new java.awt.GridBagLayout());
		final GridBagConstraints gbcSelect = new GridBagConstraints();
		gbcSelect.anchor = GridBagConstraints.NORTHWEST;
		gbcSelect.fill = GridBagConstraints.BOTH;
		gbcSelect.gridwidth = GridBagConstraints.REMAINDER;
		selectPanel.add(selectAllButton, gbcSelect);
		selectPanel.add(deselectAllButton, gbcSelect);
		gbcSelect.weighty = 1.0;
		selectPanel.add(new JLabel(), gbcSelect);

		final JPanel pathPanel = new JPanel();
		pathPanel.setLayout(new java.awt.GridBagLayout());
		final GridBagConstraints gbcPath = new GridBagConstraints();
		gbcPath.anchor = GridBagConstraints.NORTHWEST;
		gbcPath.fill = GridBagConstraints.BOTH;
		gbcPath.gridwidth = GridBagConstraints.RELATIVE;
		pathPanel.add(new JLabel("Export to:"), gbcPath);
		gbcPath.gridwidth = GridBagConstraints.REMAINDER;
		gbcPath.weightx = 1.0;
		pathPanel.add(m_pathTextField, gbcPath);
		gbcPath.weightx = 0.0;

		final JPanel authorPanel = new JPanel();
		authorPanel.setLayout(new java.awt.GridBagLayout());
		final GridBagConstraints gbcAuthor = new GridBagConstraints();
		gbcAuthor.anchor = GridBagConstraints.NORTHWEST;
		gbcAuthor.fill = GridBagConstraints.BOTH;
		gbcAuthor.gridwidth = GridBagConstraints.RELATIVE;
		authorPanel.add(new JLabel("Author's name:"), gbcAuthor);
		gbcAuthor.gridwidth = GridBagConstraints.REMAINDER;
		gbcAuthor.weightx = 1.0;
		authorPanel.add(m_authorNameTextField, gbcAuthor);
		gbcAuthor.weightx = 0.0;

		final JPanel okCancelPanel = new JPanel();
		okCancelPanel.setLayout(new java.awt.GridBagLayout());
		final GridBagConstraints gbcOKCancel = new GridBagConstraints();
		gbcOKCancel.insets.left = 8;
		okCancelPanel.add(m_exportButton, gbcOKCancel);
		okCancelPanel.add(m_cancelButton, gbcOKCancel);

		setLayout(new java.awt.GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets.left = 8;
		gbc.insets.top = 8;
		gbc.insets.right = 8;

		gbc.gridwidth = GridBagConstraints.RELATIVE;
		add(new JLabel("What to export:"), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel(), gbc);

		gbc.insets.top = 0;
		gbc.weighty = 1.0;
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.weightx = 1.0;
		add(whatToPrintScrollPane, gbc);
		gbc.weightx = 0.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(selectPanel, gbc);
		gbc.weighty = 0.0;

		gbc.insets.top = 8;

		gbc.gridwidth = GridBagConstraints.RELATIVE;
		add(pathPanel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(browseButton, gbc);

		gbc.gridwidth = GridBagConstraints.RELATIVE;
		add(authorPanel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel(), gbc);

		gbc.insets.bottom = 8;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(okCancelPanel, gbc);

		final int height = 400;
		final int width = edu.cmu.cs.stage3.math.GoldenRatio.getLongerSideLength(height);
		setPreferredSize(new java.awt.Dimension(width, height));
	}

	@Override
	public void preDialogShow(final JDialog dialog) {
		super.preDialogShow(dialog);
		initialize("");
	}

	@Override
	public void postDialogShow(final JDialog dialog) {
		super.postDialogShow(dialog);
	}

	@Override
	public String getTitle() {
		return "Export to HTML...";
	}

	private void fireOKActionListeners() {
		final ActionEvent e = new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, "OK");
		for (int i = 0; i < m_okActionListeners.size(); i++) {
			final ActionListener l = (ActionListener) m_okActionListeners.elementAt(i);
			l.actionPerformed(e);
		}
	}

	@Override
	public void addOKActionListener(final ActionListener l) {
		m_okActionListeners.addElement(l);
	}

	@Override
	public void removeOKActionListener(final ActionListener l) {
		m_okActionListeners.removeElement(l);
	}

	@Override
	public void addCancelActionListener(final ActionListener l) {
		m_cancelButton.addActionListener(l);
	}

	@Override
	public void removeCancelActionListener(final ActionListener l) {
		m_cancelButton.removeActionListener(l);
	}

	public void initialize(final String authorName) {
		setAllSelected(true);
		m_authorNameTextField.setText(authorName);
		m_authorNameTextField.setName(authorName);

		final java.io.File file = new java.io.File(m_pathFileChooser.getCurrentDirectory(),
				getWorldName(m_authoringTool.getCurrentWorldLocation()) + ".html");
		m_pathTextField.setText(file.getAbsolutePath());

		final edu.cmu.cs.stage3.alice.core.World world = m_authoringTool.getWorld();
		final Vector<Object> objectsToEdit = new Vector<>();
		addObjectsToEdit(world, objectsToEdit);
		if (world != null) {
			for (int i = 0; i < world.sandboxes.size(); i++) {
				addObjectsToEdit((edu.cmu.cs.stage3.alice.core.Sandbox) world.sandboxes.get(i), objectsToEdit);
			}
		}
		buildWhatToPrintPanel(objectsToEdit);
	}

	public java.io.File getFileToExportTo() {
		final String path = m_pathTextField.getText();
		return new java.io.File(path);
	}

	private void setAllSelected(final boolean isSelected) {
		for (int i = 0; i < m_elementsToBeExportedPanel.getComponentCount(); i++) {
			final java.awt.Component componentI = m_elementsToBeExportedPanel.getComponent(i);
			if (componentI instanceof CustomCheckBox) {
				((CustomCheckBox) componentI).setSelected(isSelected);
			}
		}
	}

	private void addObjectsToEdit(final edu.cmu.cs.stage3.alice.core.Sandbox sandbox, final Vector<Object> toAddTo) {
		if (sandbox != null
				&& (sandbox.behaviors.size() != 0 || sandbox.responses.size() != 0 || sandbox.questions.size() != 0)) {
			toAddTo.add(sandbox.name.getStringValue());
			for (int i = sandbox.behaviors.size() - 1; i >= 0; i--) {
				toAddTo.add(sandbox.behaviors.get(i));
			}
			for (int i = 0; i < sandbox.responses.size(); i++) {
				toAddTo.add(sandbox.responses.get(i));
			}
			for (int i = 0; i < sandbox.questions.size(); i++) {
				toAddTo.add(sandbox.questions.get(i));
			}
		}
	}

	protected void buildWhatToPrintPanel(final Vector<Object> objectsToPrint) {
		m_elementsToBeExportedPanel.removeAll();
		CustomListButton currentButton = null;
		int count = 0;
		boolean isWorld = false;
		for (int i = 0; i < objectsToPrint.size(); i++) {
			final Object currentObject = objectsToPrint.elementAt(i);
			JComponent toAdd = null;
			int leftIndent = 0;
			if (currentObject instanceof String) {
				currentButton = new CustomListButton();
				currentButton.setText(currentObject.toString());
				toAdd = currentButton;
				leftIndent = 0;
				if (currentButton.getText().equalsIgnoreCase("world")) {
					isWorld = true;
				} else {
					isWorld = false;
				}
			} else {
				// Unused ?? final String checkBoxText = "";
				toAdd = new CustomCheckBox();
				if (currentButton != null) {
					currentButton.addCheckBox((CustomCheckBox) toAdd);
				}
				if (currentObject instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
					final JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
							.getGUI(currentObject);
					((CustomCheckBox) toAdd).image = m_authoringTool.getJAliceFrame().getImageForComponent(gui);
					((CustomCheckBox) toAdd).gui = gui;
					((CustomCheckBox) toAdd).object = currentObject;
					toAdd.setOpaque(false);
				} else if (currentObject instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
					final edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype(
							(edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) currentObject);
					final JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
							.getGUI(callToUserDefinedResponsePrototype);
					((CustomCheckBox) toAdd).image = m_authoringTool.getJAliceFrame().getImageForComponent(gui);
					((CustomCheckBox) toAdd).gui = gui;
					((CustomCheckBox) toAdd).object = currentObject;
					toAdd.setOpaque(false);
				} else if (currentObject instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
					final edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype(
							(edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) currentObject);
					final JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
							.getGUI(callToUserDefinedQuestionPrototype);
					((CustomCheckBox) toAdd).image = m_authoringTool.getJAliceFrame().getImageForComponent(gui);
					((CustomCheckBox) toAdd).gui = gui;
					((CustomCheckBox) toAdd).object = currentObject;
					toAdd.setOpaque(false);
				}
				if (isWorld) {
					((CustomCheckBox) toAdd).setSelected(true);
				} else {
					((CustomCheckBox) toAdd).setSelected(false);
				}
				((CustomCheckBox) toAdd).setIndex(i);
			}
			java.awt.Color bgColor = java.awt.Color.white;
			if (currentObject instanceof edu.cmu.cs.stage3.alice.core.Response) {
				bgColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
						.getColor("userDefinedResponseEditor");
			} else if (currentObject instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
				bgColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
						.getColor("userDefinedQuestionEditor");
			} else if (currentObject instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BasicBehaviorPanel) {
				bgColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("behavior");
			}
			// toAdd.setBorder(BorderFactory.createLineBorder(bgColor,
			// 2));
			toAdd.setBackground(bgColor);
			m_elementsToBeExportedPanel.add(toAdd, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, leftIndent, 0, 0), 0, 0));
			count++;
		}
		m_elementsToBeExportedPanel.add(Box.createVerticalGlue(), new GridBagConstraints(0, count, 1, 1,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		m_elementsToBeExportedPanel.revalidate();
		m_elementsToBeExportedPanel.repaint();
	}

	private void handleBrowseButton() {
		// todo extract directory from text editor and
		// m_pathFileChooser.setCurrentDirectory
		final int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(m_pathFileChooser, null);
		if (result == JFileChooser.APPROVE_OPTION) {
			final java.io.File file = m_pathFileChooser.getSelectedFile();
			if (file != null) {
				String path = file.getAbsolutePath();
				if (path.endsWith(".html") || path.endsWith(".htm")) {
					// pass
				} else {
					path += ".html";
				}
				m_pathTextField.setText(path);
			}
		}
	}

	private void handleWriteProblem(final java.io.File file) {
		edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("Cannot write to: \"" + file.getAbsolutePath() + "\"",
				"Cannot write", JOptionPane.ERROR_MESSAGE);
	}

	private void handleExportButton() {
		if (m_authorNameTextField.getText().length() == 0) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					"You must enter the author's name before proceeding.", "You have not entered the author's name",
					JOptionPane.ERROR_MESSAGE);
		} else {
			final java.io.File file = getFileToExportTo();
			if (file.exists()) {
				final int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(
						"You are about to save over an existing file. Are you sure you want to?", "Save Over Warning",
						JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					// pass
				} else {
					return;
				}
			} else {
				try {
					file.createNewFile();
				} catch (final Throwable t) {
					handleWriteProblem(file);
					return;
				}
			}
			if (file.canWrite()) {
				fireOKActionListeners();
			} else {
				handleWriteProblem(file);
			}
		}
	}

	private static String getWorldName(final java.io.File worldFile) {
		if (worldFile != null) {
			return edu.cmu.cs.stage3.io.FileUtilities.getBaseName(worldFile);
		} else {
			return "Unnamed World";
		}
	}

	private JComponent getComponentForObject(final Object toFind) {
		for (int i = 0; i < m_elementsToBeExportedPanel.getComponentCount(); i++) {
			final java.awt.Component c = m_elementsToBeExportedPanel.getComponent(i);
			if (c instanceof CustomCheckBox) {
				if (((CustomCheckBox) c).object == toFind) {
					return ((CustomCheckBox) c).gui;
				}
			}
		}
		return null;
	}

	public void getHTML(final StringBuffer buffer, final java.io.File worldFile, final boolean addHeaderAndFooter,
			final boolean addAuthor, final edu.cmu.cs.stage3.progress.ProgressObserver progressObserver)
			throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		if (progressObserver != null) {
			progressObserver.progressBegin(m_elementsToBeExportedPanel.getComponentCount());
		}

		final String worldName = getWorldName(worldFile);
		if (addHeaderAndFooter) {
			buffer.append("<html>\n<head>\n<title>" + worldName + "'s Code</title>\n</head>\n<body>\n");
		}

		buffer.append("<h1>" + worldName + "'s Code</h1>\n");
		if (addAuthor) {
			buffer.append("<h1> Created by: " + m_authorNameTextField.getText() + "</h1>\n");
		}
		boolean notOnBehaviorsYet = true;
		boolean notOnResponsesYet = true;
		boolean notOnQuestionsYet = true;
		String currentTitle = "";
		boolean anyItemsYet = false;
		for (int i = 0; i < m_elementsToBeExportedPanel.getComponentCount(); i++) {
			if (m_elementsToBeExportedPanel.getComponent(i) instanceof CustomListButton) {
				final String name = ((CustomListButton) m_elementsToBeExportedPanel.getComponent(i)).getText();
				currentTitle = "<h2>" + name + "</h2>\n";
				anyItemsYet = false;
				notOnBehaviorsYet = true;
				notOnResponsesYet = true;
				notOnQuestionsYet = true;
			} else if (m_elementsToBeExportedPanel.getComponent(i) instanceof CustomCheckBox) {
				final CustomCheckBox currentBox = (CustomCheckBox) m_elementsToBeExportedPanel.getComponent(i);
				if (currentBox.isSelected()) {
					if (!anyItemsYet) {
						buffer.append(currentTitle);
						anyItemsYet = true;
					}
					if (!(currentBox.object instanceof edu.cmu.cs.stage3.alice.core.Behavior)) {
						final java.awt.Component currentEditor = m_authoringTool
								.getEditorForElement((edu.cmu.cs.stage3.alice.core.Element) currentBox.object);
						if (currentEditor instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor) {
							if (currentEditor instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor) {
								if (notOnResponsesYet) {
									buffer.append("<h3>Methods</h3>\n");
									notOnResponsesYet = false;
								}
							} else {
								if (notOnQuestionsYet) {
									final String cappedQuestionString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING
											.substring(0, 1).toUpperCase()
											+ edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING
													.substring(1);
									buffer.append("<h3>" + cappedQuestionString + "s</h3>\n");
									notOnQuestionsYet = false;
								}
							}
							buffer.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">\n");
							((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor) currentEditor)
									.getHTML(buffer, true);
							buffer.append("\n</table>\n<br>\n<br>\n");
						}
					} else {
						if (notOnBehaviorsYet) {
							buffer.append("<h3>Events</h3>\n");
							notOnBehaviorsYet = false;
						}
						final JComponent component = getComponentForObject(currentBox.object);
						if (component instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BasicBehaviorPanel) {
							final edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BasicBehaviorPanel behaviorPanel = (edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BasicBehaviorPanel) component;
							buffer.append(
									"<table  style=\"border-left: 1px solid #c0c0c0; border-top: 1px solid #c0c0c0; border-bottom: 1px solid #c0c0c0; border-right: 1px solid #c0c0c0\" cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">\n");
							behaviorPanel.getHTML(buffer, true);
							buffer.append("\n</table>\n<br>\n<br>\n");
						}
					}
				}
			}
			if (progressObserver != null) {
				progressObserver.progressUpdate(i, null);
			}
		}
		if (addHeaderAndFooter) {
			buffer.append("</body>\n</html>\n");
		}
		if (progressObserver != null) {
			progressObserver.progressEnd();
		}
	}
}