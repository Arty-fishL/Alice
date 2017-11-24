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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import com.jamiegl.alicex.ui.JSystemFileChooser;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author David Culyba, Dennis Cosgrove
 */

class AliceWorldFilter implements java.io.FileFilter {
	private final javax.swing.filechooser.FileFilter m_filter;

	public AliceWorldFilter(final javax.swing.filechooser.FileFilter filter) {
		m_filter = filter;
	}

	@Override
	public boolean accept(final java.io.File file) {
		if (m_filter != null) {
			if (file.isDirectory()) {
				return true;
			}
			return m_filter.accept(file);
		}
		return false;
	}
}

class TutorialWorldFilter implements java.io.FileFilter {
	@Override
	public boolean accept(final java.io.File file) {
		if (file.getName().endsWith(".stl")) {
			return true;
		}
		return false;
	}
}

public class StartUpContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	/**
	 *
	 */
	private static final long serialVersionUID = -5048817059625627707L;
	public static final int DO_NOT_CHANGE_TAB_ID = -1;
	public static final int OPEN_TAB_ID = 1;
	public static final int TUTORIAL_TAB_ID = 2;
	public static final int RECENT_TAB_ID = 3;
	public static final int TEMPLATE_TAB_ID = 4;
	public static final int EXAMPLE_TAB_ID = 5;
	public static final int TEXTBOOK_EXAMPLE_TAB_ID = 6;

	private static final String TUTORIAL_STRING = "Tutorial";
	private static final String EXAMPLES_STRING = "Examples";
	private static final String RECENT_STRING = "Recent Worlds";
	private static final String TEXTBOOK_EXAMPLES_STRING = "Textbook";
	private static final String OPEN_STRING = "Open a world";
	private static final String TEMPLATES_STRING = "Templates";

	private final int WIDTH = 480;
	private final int HEIGHT = 500;
	// private final int INSET = 14;

	private static final java.awt.Color SELECTED_COLOR = new java.awt.Color(10, 10, 100);
	private static final java.awt.Color SELECTED_TEXT_COLOR = new java.awt.Color(255, 255, 255);
	private static final java.awt.Color BACKGROUND_COLOR = new java.awt.Color(0, 0, 0);

	private static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration
			.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());

	private final AliceWorldFilter aliceFilter;
	private final javax.swing.filechooser.FileFilter worldFilter;
	private final TutorialWorldFilter tutorialFilter = new TutorialWorldFilter();

	private StartUpIcon currentlySelected;

	private final javax.swing.ImageIcon headerImage;
	private final javax.swing.ImageIcon basicIcon;
	private final javax.swing.ImageIcon directoryIcon;
	private final javax.swing.ImageIcon upDirectoryIcon;
	private final javax.swing.ImageIcon tutorialButtonIcon;

	private java.io.File exampleWorlds = null;
	private java.io.File templateWorlds = null;
	private java.io.File tutorialWorlds = null;
	private java.io.File textbookExampleWorlds = null;

	private final JTabbedPane mainTabPane = new JTabbedPane();
	private final JScrollPane exampleScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private final JScrollPane textbookExampleScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private final JScrollPane recentScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private final JScrollPane templateScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private final JPanel exampleWorldsContainer = new JPanel();
	private final JPanel recentWorldsContainer = new JPanel();
	private final JPanel textbookExampleWorldsContainer = new JPanel();
	private final JPanel templateWorldsContainer = new JPanel();
	private final JPanel tutorialWorldsContainer = new JPanel();
	private final edu.cmu.cs.stage3.awt.DynamicFlowLayout examplePanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(
			java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);
	private final edu.cmu.cs.stage3.awt.DynamicFlowLayout recentPanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(
			java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);
	private final edu.cmu.cs.stage3.awt.DynamicFlowLayout templatePanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(
			java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);
	private final edu.cmu.cs.stage3.awt.DynamicFlowLayout tutorialPanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(
			java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);
	private final edu.cmu.cs.stage3.awt.DynamicFlowLayout textbookPanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(
			java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);

	private final JPanel exampleWorldsDirectoryContainer = new JPanel();
	private final JPanel textbookExampleWorldsDirectoryContainer = new JPanel();
	private final JPanel templateWorldsDirectoryContainer = new JPanel();
	// private JPanel tutorialWorldsDirectoryContainer = new JPanel();
	// private JPanel recentWorldsDirectoryContainer = new JPanel();

	private final JLabel exampleWorldsDirLabel = new JLabel();
	private final JLabel textbookExampleWorldsDirLabel = new JLabel();
	private final JLabel templateWorldsDirLabel = new JLabel();
	private final JLabel tutorialWorldsDirLabel = new JLabel();
	// private JLabel recentWorldsDirLabel = new JLabel();

	private final JButton openButton = new JButton();
	private final JButton cancelButton = new JButton();
	// private JButton refreshButton = new JButton();
	private final JCheckBox stopShowingCheckBox = new JCheckBox();

	private final JLabel headerLabel = new JLabel();

	private final JPanel tutorialButtonPanel = new JPanel();
	private final JButton startTutorialButton = new JButton();
	private final JPanel tutorialTopContainer = new JPanel();
	private final BorderLayout borderLayout1 = new BorderLayout();
	private final JScrollPane tutorialScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private JFileChooser fileChooser = new JSystemFileChooser() {
		/**
		 *
		 */
		private static final long serialVersionUID = 3785998608153962381L;

		@Override
		public void setSelectedFile(final java.io.File file) {
			super.setSelectedFile(file);
			StartUpContentPane.this.handleFileSelectionChange(file);
		}
	};

	private final JPanel buttonPanel = new JPanel();
	private final JLabel jLabel1 = new JLabel();
	protected int currentTab = TUTORIAL_TAB_ID;

	public StartUpContentPane(final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		headerImage = new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class
				.getResource("images/startUpDialog/StartupScreen.png"));
		basicIcon = new javax.swing.ImageIcon(
				edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/startUpDialog/aliceIcon.png"));
		directoryIcon = new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class
				.getResource("images/startUpDialog/directoryIcon.png"));
		upDirectoryIcon = new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class
				.getResource("images/startUpDialog/upDirectoryIcon.png"));
		tutorialButtonIcon = new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class
				.getResource("images/startUpDialog/tutorialButton.png"));

		String[] recentWorldsStrings = authoringToolConfig.getValueList("recentWorlds.worlds");
		final String max = authoringToolConfig.getValue("recentWorlds.maxWorlds");
		final int maxRecentWorlds = Integer.parseInt(max);
		if (maxRecentWorlds > 0 && maxRecentWorlds <= recentWorldsStrings.length) {
			final String[] cappedRecentWorlds = new String[maxRecentWorlds];
			System.arraycopy(recentWorldsStrings, 0, cappedRecentWorlds, 0, maxRecentWorlds);
			recentWorldsStrings = cappedRecentWorlds;
		}
		String filename = authoringToolConfig.getValue("directories.examplesDirectory");

		if (filename != null) {
			exampleWorlds = new java.io.File(filename).getAbsoluteFile();
		}
		filename = authoringToolConfig.getValue("directories.templatesDirectory");
		if (filename != null) {
			templateWorlds = new java.io.File(filename).getAbsoluteFile();
		}
		filename = authoringToolConfig.getValue("directories.textbookExamplesDirectory");
		if (filename != null) {
			textbookExampleWorlds = new java.io.File(filename).getAbsoluteFile();
		}

		worldFilter = authoringTool.getWorldFileFilter();
		aliceFilter = new AliceWorldFilter(worldFilter);

		jbInit();
		guiInit();

		int count = 0;
		final int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		final String font = "SansSerif";// "Dialog";
		if (authoringTool != null) {
			tutorialWorlds = authoringTool.getTutorialDirectory();
			count = buildPanel(tutorialWorldsContainer, buildVectorFromDirectory(tutorialWorlds, tutorialFilter), false,
					null, StartUpIcon.TUTORIAL);
			if (count <= 0) {
				tutorialTopContainer.removeAll();
				final javax.swing.JLabel noTutorialWorldsLabel = new javax.swing.JLabel();
				noTutorialWorldsLabel.setText("No tutorial found.");
				noTutorialWorldsLabel
						.setFont(new java.awt.Font(font, java.awt.Font.BOLD, (int) (18 * fontSize / 12.0)));
				tutorialTopContainer.add(noTutorialWorldsLabel);
			}
		}
		count = buildPanel(exampleWorldsContainer, buildVectorFromDirectory(exampleWorlds, aliceFilter), false, null,
				StartUpIcon.STANDARD);
		if (count <= 0) {
			exampleWorldsContainer.removeAll();
			final javax.swing.JLabel noExampleWorldsLabel = new javax.swing.JLabel();
			noExampleWorldsLabel.setText("No example worlds.");
			noExampleWorldsLabel.setFont(new java.awt.Font(font, java.awt.Font.BOLD, (int) (18 * fontSize / 12.0)));
			exampleWorldsContainer.add(noExampleWorldsLabel);
		}
		count = buildPanel(templateWorldsContainer, buildVectorFromDirectory(templateWorlds, aliceFilter), false, null,
				StartUpIcon.STANDARD);
		if (count <= 0) {
			templateWorldsContainer.removeAll();
			final javax.swing.JLabel noTemplateWorldsLabel = new javax.swing.JLabel();
			noTemplateWorldsLabel.setText("No templates.");
			noTemplateWorldsLabel.setFont(new java.awt.Font(font, java.awt.Font.BOLD, (int) (18 * fontSize / 12.0)));
			templateWorldsContainer.add(noTemplateWorldsLabel);
		}
		count = buildPanel(recentWorldsContainer, buildVectorFromString(recentWorldsStrings), true, null,
				StartUpIcon.STANDARD);
		if (count <= 0) {
			recentWorldsContainer.removeAll();
			final javax.swing.JLabel noRecentWorldsLabel = new javax.swing.JLabel();
			noRecentWorldsLabel.setText("No recent worlds.");
			noRecentWorldsLabel.setFont(new java.awt.Font(font, java.awt.Font.BOLD, (int) (18 * fontSize / 12.0)));
			recentWorldsContainer.add(noRecentWorldsLabel);
		}
		count = buildPanel(textbookExampleWorldsContainer, buildVectorFromDirectory(textbookExampleWorlds, aliceFilter),
				false, null, StartUpIcon.STANDARD);
		if (count <= 0) {
			mainTabPane.remove(textbookExampleWorldsDirectoryContainer);
		}

		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(final java.awt.event.ComponentEvent e) {
				matchSizes();
			}
		});
		tutorialPanelLayout.setHgap(21);
		tutorialPanelLayout.setVgap(10);
		examplePanelLayout.setHgap(21);
		examplePanelLayout.setVgap(10);
		recentPanelLayout.setHgap(21);
		recentPanelLayout.setVgap(10);
		templatePanelLayout.setHgap(21);
		templatePanelLayout.setVgap(10);
		textbookPanelLayout.setHgap(21);
		textbookPanelLayout.setVgap(10);

	}

	@Override
	public void preDialogShow(final javax.swing.JDialog dialog) {
		super.preDialogShow(dialog);
		// initializeFileChooser();
		mainTabPane.setSelectedComponent(getTabForID(currentTab));
	}

	private void handleFileSelectionChange(final java.io.File file) {
		openButton.setEnabled(file != null && file.exists() && !file.isDirectory());

	}
	// todo? isResizable() return false;

	// todo: adjust title based on tab
	@Override
	public String getTitle() {
		return "Welcome to Alice!";
	}

	@Override
	public void addOKActionListener(final java.awt.event.ActionListener l) {
		openButton.addActionListener(l);
	}

	@Override
	public void removeOKActionListener(final java.awt.event.ActionListener l) {
		openButton.removeActionListener(l);
	}

	@Override
	public void addCancelActionListener(final java.awt.event.ActionListener l) {
		cancelButton.addActionListener(l);
	}

	@Override
	public void removeCancelActionListener(final java.awt.event.ActionListener l) {
		cancelButton.removeActionListener(l);
	}

	public boolean isTutorial() {
		if (currentlySelected != null) {
			return currentlySelected.type == StartUpIcon.TUTORIAL;
		} else {
			return getTabID() == TUTORIAL_TAB_ID;
		}
	}

	public boolean isSaveNeeded() {
		if (currentlySelected != null) {
			return currentlySelected.needToSave;
		} else {
			return true;
		}
	}

	public java.io.File getFile() {
		if (getTabID() == OPEN_TAB_ID) {
			return fileChooser.getSelectedFile();
		} else {
			if (currentlySelected != null) {
				return new java.io.File(currentlySelected.file);
			} else {
				return null;
			}
		}
	}

	private int getIDForTab(final java.awt.Component tab) {
		if (tab == fileChooser) {
			return OPEN_TAB_ID;
		}
		if (tab == tutorialTopContainer) {
			return TUTORIAL_TAB_ID;
		}
		if (tab == recentScrollPane) {
			return RECENT_TAB_ID;
		}
		if (tab == exampleWorldsDirectoryContainer) {
			return EXAMPLE_TAB_ID;
		}
		if (tab == textbookExampleWorldsDirectoryContainer) {
			return TEXTBOOK_EXAMPLE_TAB_ID;
		}
		if (tab == templateWorldsDirectoryContainer) {
			return TEMPLATE_TAB_ID;
		}
		return 0;
	}

	private java.awt.Component getTabForID(final int tabID) {
		switch (tabID) {
		case OPEN_TAB_ID:
			return fileChooser;
		case TUTORIAL_TAB_ID:
			return tutorialTopContainer;
		case RECENT_TAB_ID:
			return recentScrollPane;
		case EXAMPLE_TAB_ID:
			return exampleWorldsDirectoryContainer;
		case TEMPLATE_TAB_ID:
			return templateWorldsDirectoryContainer;
		case TEXTBOOK_EXAMPLE_TAB_ID:
			return textbookExampleWorldsDirectoryContainer;
		default:
			return tutorialTopContainer;
		}
	}

	private int getTabID() {
		return getIDForTab(mainTabPane.getSelectedComponent());
	}

	public void setTabID(final int tabID) {
		if (tabID == OPEN_TAB_ID) {
			fileChooser.rescanCurrentDirectory();
		}
		if (tabID != DO_NOT_CHANGE_TAB_ID) {
			currentTab = tabID;
			mainTabPane.setSelectedComponent(getTabForID(currentTab));

		}
	}

	private String makeNameFromFilename(final String filename) {
		String name = filename.substring(0, filename.length() - 4);
		// int count = 0;
		final int last = name.lastIndexOf(java.io.File.separator);
		if (last >= 0 && last < name.length()) {
			name = name.substring(last + 1);
		}
		return name;
	}

	private String makeDirectoryNameFromFilename(String filename) {
		String name = new String(filename);
		// int count = 0;
		if (filename.endsWith(java.io.File.separator)) {
			filename = filename.substring(filename.length());
		}
		final int last = filename.lastIndexOf(java.io.File.separator);
		if (last >= 0 && last < filename.length()) {
			name = filename.substring(last + 1);
		}
		return name;
	}

	private java.util.Vector<StringObjectPair> buildVectorFromString(final String[] files) {
		final java.util.Vector<StringObjectPair> toReturn = new java.util.Vector<StringObjectPair>();
		if (files != null) {
			for (final String file : files) {
				final String name = makeNameFromFilename(file);
				final edu.cmu.cs.stage3.util.StringObjectPair sop = new edu.cmu.cs.stage3.util.StringObjectPair(name,
						file);
				toReturn.add(sop);
			}
		}
		return toReturn;
	}

	private java.util.Vector<StringObjectPair> buildVectorFromDirectory(final java.io.File dir, final java.io.FileFilter f) {
		java.util.Vector<StringObjectPair> toReturn = null;
		if (dir != null && dir.isDirectory()) {
			toReturn = new java.util.Vector<StringObjectPair>();
			final java.io.File[] files = dir.listFiles(f);
			for (final File file : files) {
				String name = "";
				if (file.isDirectory()) {
					name = makeDirectoryNameFromFilename(file.getName());
				} else {
					name = makeNameFromFilename(file.getName());
				}
				final edu.cmu.cs.stage3.util.StringObjectPair sop = new edu.cmu.cs.stage3.util.StringObjectPair(name,
						file.getAbsolutePath());
				toReturn.add(sop);
			}
		}
		return toReturn;
	}

	private javax.swing.ImageIcon getIconFromFile(java.io.File file) {
		final String filename = file.getAbsolutePath();
		javax.swing.ImageIcon icon = null;
		try {
			if (filename.endsWith(".stl")) {
				final javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory
						.newInstance();
				org.w3c.dom.Document document;
				// org.w3c.dom.Element xmlRoot;
				final javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(file);
				final org.w3c.dom.NodeList nl = document.getElementsByTagName("stencilStack");
				if (nl != null && nl.getLength() > 0) {
					final org.w3c.dom.Node n = nl.item(0);
					if (n instanceof org.w3c.dom.Element) {
						final String worldFileName = ((org.w3c.dom.Element) n).getAttribute("world");
						file = new java.io.File(worldFileName);
					}
				}
			}
			final java.util.zip.ZipFile zip = new java.util.zip.ZipFile(file);
			final java.util.zip.ZipEntry entry = zip.getEntry("thumbnail.png");
			if (entry != null) {
				final java.io.InputStream stream = zip.getInputStream(entry);
				final java.awt.Image image = edu.cmu.cs.stage3.image.ImageIO.load("png", stream);
				if (image != null) {
					icon = new javax.swing.ImageIcon(image);
				}
			}
			zip.close();
		} catch (final Exception e) {
			return null;
		}
		return icon;
	}

	protected java.awt.Component getTopContainer(final java.awt.Component innerContainer) {
		if (innerContainer == tutorialWorldsContainer) {
			return tutorialTopContainer;
		} else if (innerContainer == exampleWorldsContainer) {
			return exampleScrollPane;
		} else if (innerContainer == templateWorldsContainer) {
			return templateScrollPane;
		} else if (innerContainer == textbookExampleWorldsContainer) {
			return textbookExampleScrollPane;
		} else {
			return null;
		}

	}

	protected String getBaseDirString(final java.awt.Component topLevelOwner) {
		if (topLevelOwner == tutorialTopContainer) {
			return TUTORIAL_STRING;
		} else if (topLevelOwner == exampleScrollPane) {
			return EXAMPLES_STRING;
		} else if (topLevelOwner == templateScrollPane) {
			return TEMPLATES_STRING;
		} else if (topLevelOwner == textbookExampleScrollPane) {
			return TEXTBOOK_EXAMPLES_STRING;
		} else {
			return "";
		}
	}

	private int buildPanel(final javax.swing.JPanel toBuild, final java.util.Vector<StringObjectPair> toAdd, final boolean needToSave,
			final java.io.File parentDir, final int type) {
		// int width = 0;
		// int currentRow = 0;
		// int currentColumn = 0;
		int count = 0;
		// int maxWidth = WIDTH - 20;
		if (parentDir != null || toAdd != null) {
			toBuild.removeAll();
		}
		if (parentDir != null) {
			final String parentDirName = "Back";
			final StartUpIcon parentDirIcon = new StartUpIcon(parentDirName, upDirectoryIcon,
					parentDir.getAbsolutePath(), false, StartUpIcon.DIRECTORY, getTopContainer(toBuild));
			toBuild.add(parentDirIcon);
			count++;
		}
		if (toAdd != null) {
			for (int i = 0; i < toAdd.size(); i++) {
				final edu.cmu.cs.stage3.util.StringObjectPair sop = toAdd
						.get(i);
				final String name = sop.getString();
				String filename = (String) sop.getObject();
				final java.io.File file = new java.io.File(filename);
				javax.swing.ImageIcon icon = basicIcon;
				if (file.exists() && file.canRead()) {
					filename = file.getAbsolutePath();
					if (file.isDirectory()) {
						final StartUpIcon dirIcon = new StartUpIcon(name, directoryIcon, filename, false,
								StartUpIcon.DIRECTORY, getTopContainer(toBuild));
						toBuild.add(dirIcon);
						count++;
					} else {
						boolean worldIsThere = true;
						if (file.exists() && file.canRead()) {
							icon = getIconFromFile(file);
							if (icon == null) {
								icon = basicIcon;
							}
						} else {
							worldIsThere = false;
						}
						if (worldIsThere) {
							final StartUpIcon sui = new StartUpIcon(name, icon, filename, needToSave, type,
									getTopContainer(toBuild));
							toBuild.add(sui);
							count++;
						}
					}
				}
			}
		}
		toBuild.revalidate();
		return count;
	}

	private void initializeFileChooser() {
		// Unused? final javax.swing.LookAndFeel feel = UIManager.getLookAndFeel();
		// Unused ?? final String font = "SansSerif";
		try {
			if (System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) {
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} else {
				UIManager.setLookAndFeel("apple.laf.AquaLookAndFeel");
			}
			AikMin.setFontSize(12);
		} catch (final Exception e) {
		}

		mainTabPane.remove(fileChooser);
		java.io.File currentDir = fileChooser.getCurrentDirectory();
		fileChooser = new JSystemFileChooser() {
			/**
			 *
			 */
			private static final long serialVersionUID = 7630011804620309330L;

			@Override
			public void setSelectedFile(final java.io.File file) {
				super.setSelectedFile(file);
				StartUpContentPane.this.handleFileSelectionChange(file);
			}
		};
		for (int i = 0; i < fileChooser.getComponentCount(); i++) {
			setButtonBackgroundColors(fileChooser.getComponent(i), fileChooser.getBackground());
		}
		if (currentDir.exists()) {
			fileChooser.setCurrentDirectory(currentDir);
		} else {
			currentDir = new java.io.File(authoringToolConfig.getValue("directories.worldsDirectory"));
			if (currentDir.exists()) {
				fileChooser.setCurrentDirectory(currentDir);
			}
		}
		fileChooser.setFileFilter(worldFilter);
		fileChooser.setBackground(Color.white);
		fileChooser.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				fileChooser_actionPerformed(e);
			}
		});
		fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		mainTabPane.add(fileChooser, OPEN_STRING);
		try {
			AikMin.setFontSize(Integer.parseInt(authoringToolConfig.getValue("fontSize")));
		} catch (final Exception e) {
		}
	}

	private void guiInit() {
		final int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		if (fontSize > 12) {
			setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT + fontSize * 3));
		} else {
			setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));
		}
		headerLabel.setIcon(headerImage);
		startTutorialButton.setIcon(tutorialButtonIcon);
		startTutorialButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		exampleWorldsContainer.setBorder(null);
		tutorialWorldsContainer.setBorder(null);
		recentWorldsContainer.setBorder(null);
		templateWorldsContainer.setBorder(null);
		textbookExampleWorldsContainer.setBorder(null);
		authoringToolConfig.getValue("showStartUpDialog");
		stopShowingCheckBox.setSelected(authoringToolConfig.getValue("showStartUpDialog").equalsIgnoreCase("true"));
		final int selectedTab = Integer.parseInt(authoringToolConfig.getValue("showStartUpDialog_OpenTab"));
		setTabID(selectedTab);

		mainTabPane.setUI(new edu.cmu.cs.stage3.alice.authoringtool.util.AliceTabbedPaneUI());
		mainTabPane.setOpaque(false);
		initializeFileChooser();

	}

	// private static void updateDescendants( java.awt.Component component,
	// Class<?> cls, java.util.Vector v ) {
	// if( cls.isAssignableFrom( component.getClass() ) ) {
	// v.addElement( component );
	// }
	// if( component instanceof java.awt.Container ) {
	// java.awt.Container container = (java.awt.Container)component;
	// for( int i=0; i<container.getComponentCount(); i++ ) {
	// updateDescendants( container.getComponent( i ), cls, v );
	// }
	// }
	// }
	// private static java.awt.Component[] getDescendants( java.awt.Container
	// root, Class<?> cls ) {
	// java.util.Vector v = new java.util.Vector();
	// updateDescendants( root, cls, v );
	// Object[] array = (Object[])java.lang.reflect.Array.newInstance( cls,
	// v.size() );
	// v.copyInto( array );
	// return (java.awt.Component[])array;
	// }

	private void setButtonBackgroundColors(final java.awt.Component c, final java.awt.Color color) {
		if (!(c instanceof java.awt.Button)) {
			c.setBackground(color);
		}
		if (c instanceof java.awt.Container) {
			final java.awt.Container cont = (java.awt.Container) c;
			for (int i = 0; i < cont.getComponentCount(); i++) {
				setButtonBackgroundColors(cont.getComponent(i), color);
			}
		}
	}

	private void jbInit() {
		setLayout(new GridBagLayout());

		// java.awt.Component component1 = Box.createGlue();
		final java.awt.Component component2 = Box.createGlue();
		buttonPanel.setLayout(new GridBagLayout());
		setBackground(Color.white);
		// mainTabPane.setMinimumSize(new Dimension(480, 310));
		// mainTabPane.setPreferredSize(new Dimension(480, 310));
		mainTabPane.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				mainTabPane_stateChanged(e);
			}
		});
		// buttonPanel.setMinimumSize(new Dimension(480, 50));
		buttonPanel.setOpaque(false);
		// buttonPanel.setPreferredSize(new Dimension(480, 50));
		// openButton.setMaximumSize(new Dimension(95, 27));
		final int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		openButton.setMinimumSize(new Dimension(fontSize * 8, fontSize * 2 + 5));
		openButton.setPreferredSize(new Dimension(fontSize * 8, fontSize * 2 + 5));
		openButton.setText("Open");
		openButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
				authoringToolConfig.setValue("showStartUpDialog_OpenTab",
						Integer.toString(getIDForTab(mainTabPane.getSelectedComponent())));
			}
		});

		// cancelButton.setMaximumSize(new Dimension(95, 27));
		cancelButton.setMinimumSize(new Dimension(fontSize * 8, fontSize * 2 + 5));
		cancelButton.setPreferredSize(new Dimension(fontSize * 8, fontSize * 2 + 5));
		cancelButton.setText("Cancel");
		/*
		 * refreshButton.setMaximumSize(new Dimension(90, 22));
		 * refreshButton.setMinimumSize(new Dimension(90, 22));
		 * refreshButton.setPreferredSize(new Dimension(90, 22));
		 * refreshButton.setOpaque(false); refreshButton.setText("Refresh");
		 * refreshButton.addActionListener(new java.awt.event.ActionListener() {
		 * public void actionPerformed(ActionEvent e) {
		 * refreshButton_actionPerformed(e); } });
		 */
		stopShowingCheckBox.setOpaque(false);
		stopShowingCheckBox.setText("Show this dialog at start");
		stopShowingCheckBox.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				stopShowingCheckBox_actionPerformed(e);
			}
		});
		exampleWorldsContainer.setLayout(examplePanelLayout);
		recentWorldsContainer.setLayout(recentPanelLayout);
		exampleScrollPane.setBackground(Color.white);
		exampleScrollPane.setBorder(null);
		exampleScrollPane.setOpaque(false);
		exampleScrollPane.getViewport().setBackground(Color.white);
		exampleWorldsContainer.setBackground(Color.white);
		exampleWorldsContainer.setAlignmentX((float) 0.0);
		exampleWorldsContainer.setAlignmentY((float) 0.0);

		recentScrollPane.setBackground(Color.white);
		recentScrollPane.setBorder(null);
		recentScrollPane.getViewport().setBackground(Color.white);
		recentScrollPane.setOpaque(false);
		recentWorldsContainer.setBackground(Color.white);
		recentWorldsContainer.setAlignmentX((float) 0.0);
		recentWorldsContainer.setAlignmentY((float) 0.0);

		templateWorldsContainer.setLayout(templatePanelLayout);
		templateScrollPane.getViewport().setBackground(Color.white);
		templateScrollPane.setOpaque(false);
		templateScrollPane.setBorder(null);
		templateScrollPane.setBackground(Color.white);
		templateWorldsContainer.setBackground(Color.white);
		templateWorldsContainer.setAlignmentX((float) 0.0);
		templateWorldsContainer.setAlignmentY((float) 0.0);

		textbookExampleWorldsContainer.setLayout(textbookPanelLayout);
		textbookExampleScrollPane.getViewport().setBackground(Color.white);
		textbookExampleScrollPane.setOpaque(false);
		textbookExampleScrollPane.setBorder(null);
		textbookExampleScrollPane.setBackground(Color.white);
		textbookExampleWorldsContainer.setBackground(Color.white);
		textbookExampleWorldsContainer.setAlignmentX((float) 0.0);
		textbookExampleWorldsContainer.setAlignmentY((float) 0.0);

		tutorialButtonPanel.setBackground(Color.white);
		tutorialButtonPanel.setLayout(new GridBagLayout());
		startTutorialButton.setBackground(Color.white);
		startTutorialButton.setBorder(null);
		startTutorialButton.setMaximumSize(new Dimension(120, 90));
		startTutorialButton.setMinimumSize(new Dimension(120, 90));
		startTutorialButton.setPreferredSize(new Dimension(120, 90));
		startTutorialButton.setToolTipText("Start the Alice tutorial");
		startTutorialButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				startTutorialButton_actionPerformed(e);
			}
		});
		tutorialTopContainer.setLayout(borderLayout1);
		tutorialTopContainer.setBackground(Color.white);
		tutorialTopContainer.setOpaque(false);
		tutorialScrollPane.getViewport().setBackground(Color.white);
		tutorialScrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		tutorialScrollPane.setOpaque(false);
		tutorialWorldsContainer.setLayout(tutorialPanelLayout);
		tutorialWorldsContainer.setBackground(Color.white);
		tutorialWorldsContainer.setAlignmentX((float) 0.0);
		tutorialWorldsContainer.setAlignmentY((float) 0.0);
		jLabel1.setText("or continue a tutorial:");

		exampleWorldsDirLabel.setText(getBaseDirString(exampleScrollPane));
		exampleWorldsDirectoryContainer.setLayout(new GridBagLayout());
		exampleWorldsDirectoryContainer.setOpaque(true);
		exampleWorldsDirectoryContainer.setBackground(Color.white);
		exampleWorldsDirectoryContainer.add(exampleWorldsDirLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
		exampleWorldsDirectoryContainer.add(exampleScrollPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		textbookExampleWorldsDirLabel.setText(getBaseDirString(textbookExampleScrollPane));
		textbookExampleWorldsDirectoryContainer.setLayout(new GridBagLayout());
		textbookExampleWorldsDirectoryContainer.setOpaque(true);
		textbookExampleWorldsDirectoryContainer.setBackground(Color.white);
		textbookExampleWorldsDirectoryContainer.add(textbookExampleWorldsDirLabel, new GridBagConstraints(0, 0, 1, 1, 0,
				0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
		textbookExampleWorldsDirectoryContainer.add(textbookExampleScrollPane, new GridBagConstraints(0, 1, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		templateWorldsDirLabel.setText(getBaseDirString(templateScrollPane));
		templateWorldsDirectoryContainer.setLayout(new GridBagLayout());
		templateWorldsDirectoryContainer.setOpaque(true);
		templateWorldsDirectoryContainer.setBackground(Color.white);
		templateWorldsDirectoryContainer.add(templateWorldsDirLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
		templateWorldsDirectoryContainer.add(templateScrollPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		add(buttonPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		buttonPanel.add(openButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.NONE, new Insets(4, 0, 0, 4), 0, 0));
		buttonPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.NONE, new Insets(4, 0, 0, 4), 0, 0));
		buttonPanel.add(Box.createGlue(), new GridBagConstraints(0, 1, 1, 2, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(mainTabPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		mainTabPane.add(tutorialTopContainer, TUTORIAL_STRING);
		mainTabPane.add(recentScrollPane, RECENT_STRING);
		mainTabPane.add(templateWorldsDirectoryContainer, TEMPLATES_STRING);
		mainTabPane.add(exampleWorldsDirectoryContainer, EXAMPLES_STRING);
		mainTabPane.add(textbookExampleWorldsDirectoryContainer, TEXTBOOK_EXAMPLES_STRING);

		mainTabPane.add(fileChooser, OPEN_STRING);
		tutorialTopContainer.add(tutorialButtonPanel, BorderLayout.NORTH);
		tutorialButtonPanel.add(startTutorialButton, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
		tutorialButtonPanel.add(jLabel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.NONE, new Insets(2, 3, 2, 0), 0, 0));
		tutorialButtonPanel.add(component2, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		tutorialTopContainer.add(tutorialScrollPane, BorderLayout.CENTER);
		tutorialScrollPane.getViewport().add(tutorialWorldsContainer, null);
		add(stopShowingCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 0, 0));
		add(headerLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		templateScrollPane.getViewport().add(templateWorldsContainer, null);
		exampleScrollPane.getViewport().add(exampleWorldsContainer, null);
		recentScrollPane.getViewport().add(recentWorldsContainer, null);
		textbookExampleScrollPane.getViewport().add(textbookExampleWorldsContainer, null);

		templateScrollPane.getVerticalScrollBar().setUnitIncrement(50); // Aik
																		// Min
		exampleScrollPane.getVerticalScrollBar().setUnitIncrement(50); // Aik
																		// Min
		recentScrollPane.getVerticalScrollBar().setUnitIncrement(50); // Aik Min
		textbookExampleScrollPane.getVerticalScrollBar().setUnitIncrement(50); // Aik
																				// Min

	}

	private void matchSizes() {
		tutorialWorldsContainer.setSize(recentScrollPane.getVisibleRect().width, tutorialWorldsContainer.getHeight());
		recentWorldsContainer.setSize(recentScrollPane.getVisibleRect().width, recentWorldsContainer.getHeight());
		templateWorldsContainer.setSize(templateScrollPane.getVisibleRect().width, templateWorldsContainer.getHeight());
		exampleWorldsContainer.setSize(exampleScrollPane.getVisibleRect().width, exampleWorldsContainer.getHeight());
		textbookExampleWorldsContainer.setSize(textbookExampleScrollPane.getVisibleRect().width,
				textbookExampleWorldsContainer.getHeight());
	}

	private void setFileChooserButtons() {
		// add(refreshButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
		// GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0,
		// 2, 1, 1), 0, 0));
		// buttonPanel.remove(openButton);
		// buttonPanel.remove(cancelButton);
		// buttonPanel.add(refreshButton, new GridBagConstraints(1, 0, 1, 1,
		// 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new
		// Insets(4, 0, 0, 4), 0, 0));
		// // buttonPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1,
		// 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new
		// Insets(4, 0, 0, 4), 0, 0));
		// buttonPanel.add(Box.createGlue(), new GridBagConstraints(0, 1, 1, 1,
		// 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
		// new Insets(0, 0, 0, 0), 0, 0));
		remove(buttonPanel);
	}

	private void setRegularButtons() {
		// buttonPanel.remove(refreshButton);
		// buttonPanel.add(openButton, new GridBagConstraints(1, 0, 1, 1, 0.0,
		// 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new
		// Insets(4, 0, 0, 4), 0, 0));
		// buttonPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0,
		// 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new
		// Insets(4, 0, 0, 4), 0, 0));
		// buttonPanel.add(Box.createGlue(), new GridBagConstraints(0, 1, 1, 2,
		// 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
		// new Insets(0, 0, 0, 0), 0, 0));
		// remove(refreshButton);
		add(buttonPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

	private void mainTabPane_stateChanged(final ChangeEvent e) {
		if (currentlySelected != null) {
			currentlySelected.deSelect();
			currentlySelected = null;
		}
		if (mainTabPane.getSelectedComponent() == fileChooser) {
			setFileChooserButtons();
			handleFileSelectionChange(fileChooser.getSelectedFile());
		} else {
			setRegularButtons();
			openButton.setEnabled(false);
		}
	}

	private void stopShowingCheckBox_actionPerformed(final ActionEvent e) {
		if (stopShowingCheckBox.isSelected()) {
			authoringToolConfig.setValue("showStartUpDialog", "true");
		} else {
			authoringToolConfig.setValue("showStartUpDialog", "false");
		}
	}

	private void fileChooser_actionPerformed(final ActionEvent e) {
		final String actionCommand = e.getActionCommand();
		if (actionCommand.equals(JFileChooser.APPROVE_SELECTION)) {
			openButton.setEnabled(true);
			openButton.doClick();
		} else if (actionCommand.equals(JFileChooser.CANCEL_SELECTION)) {
			cancelButton.doClick();
		}
	}

	/*
	 * private void refreshButton_actionPerformed(ActionEvent e) {
	 * initializeFileChooser(); setTabID( OPEN_TAB_ID ); }
	 */
	private void startTutorialButton_actionPerformed(final ActionEvent e) {
		openButton.setEnabled(true);
		openButton.doClick();
	}

	protected class StartUpIcon extends javax.swing.JLabel implements java.awt.event.MouseListener {
		/**
		 *
		 */
		private static final long serialVersionUID = 3466707011642568096L;
		protected static final int STANDARD = 1;
		protected static final int TUTORIAL = 2;
		protected static final int DIRECTORY = 3;
		protected boolean isSelected = false;
		protected String file;
		protected boolean needToSave = false;
		protected int type;
		protected java.awt.Component owner;

		public StartUpIcon(final String name, final javax.swing.ImageIcon icon, final String file,
				final boolean needToSave, final int type, final java.awt.Component owner) {
			super(name, icon, SwingConstants.CENTER);
			this.file = file;
			this.needToSave = needToSave;
			this.type = type;
			this.owner = owner;

			setBackground(BACKGROUND_COLOR);
			setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
			setVerticalTextPosition(SwingConstants.BOTTOM);
			setHorizontalTextPosition(SwingConstants.CENTER);
			final java.awt.Dimension size = new java.awt.Dimension(icon.getIconWidth() + 4, icon.getIconHeight() + 24);
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			if (type == DIRECTORY) {
				setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
			}
			setOpaque(false);
			addMouseListener(this);
		}

		protected javax.swing.JPanel getContainer(final java.awt.Component topLevelOwner) {
			if (topLevelOwner == tutorialTopContainer) {
				return tutorialWorldsContainer;
			} else if (topLevelOwner == exampleScrollPane) {
				return exampleWorldsContainer;
			} else if (topLevelOwner == templateScrollPane) {
				return templateWorldsContainer;
			} else if (topLevelOwner == textbookExampleScrollPane) {
				return textbookExampleWorldsContainer;
			} else {
				return null;
			}

		}

		protected javax.swing.JLabel getJLabel(final java.awt.Component topLevelOwner) {
			if (topLevelOwner == tutorialTopContainer) {
				return tutorialWorldsDirLabel;
			} else if (topLevelOwner == exampleScrollPane) {
				return exampleWorldsDirLabel;
			} else if (topLevelOwner == templateScrollPane) {
				return templateWorldsDirLabel;
			} else if (topLevelOwner == textbookExampleScrollPane) {
				return textbookExampleWorldsDirLabel;
			} else {
				return null;
			}
		}

		protected String getRootPath(final java.awt.Component topLevelOwner) {
			if (topLevelOwner == tutorialTopContainer) {
				return tutorialWorlds.getAbsolutePath();
			} else if (topLevelOwner == exampleScrollPane) {
				return exampleWorlds.getAbsolutePath();
			} else if (topLevelOwner == templateScrollPane) {
				return templateWorlds.getAbsolutePath();
				// }else if (topLevelOwner == textbookExampleScrollPane){
				// return textbookExampleWorlds.getAbsolutePath();
			} else {
				return null;
			}

		}

		protected String getRelativePath(final String current, final String root) {
			return current.substring(root.length());
		}

		protected void changeDirectory(final String newDirectory) {
			final java.io.File newDir = new java.io.File(newDirectory);
			final java.io.File parentDir = newDir.getParentFile();
			final JLabel labelToSet = getJLabel(owner);
			final String baseDir = getBaseDirString(owner);
			if (owner instanceof javax.swing.JScrollPane) {
				((javax.swing.JScrollPane) owner).getVerticalScrollBar().setValue(0);
			}
			if (newDir.compareTo(exampleWorlds) == 0 || newDir.compareTo(templateWorlds) == 0 || // newDir.compareTo(textbookExampleWorlds)
																									// ==
																									// 0
																									// ||
					newDir.compareTo(tutorialWorlds) == 0) {
				buildPanel(getContainer(owner), buildVectorFromDirectory(newDir, aliceFilter), needToSave, null,
						StartUpIcon.STANDARD);
				labelToSet.setText(baseDir);
			} else {
				buildPanel(getContainer(owner), buildVectorFromDirectory(newDir, aliceFilter), needToSave, parentDir,
						StartUpIcon.STANDARD);
				labelToSet.setText(baseDir + getRelativePath(newDir.getAbsolutePath(), getRootPath(owner)));
			}

		}

		public void deSelect() {
			if (isSelected) {
				currentlySelected = null;
				isSelected = false;
				setBackground(BACKGROUND_COLOR);
				setOpaque(false);
				this.repaint();
				setForeground((java.awt.Color) javax.swing.UIManager.get("Label.foreground"));
			}
		}

		@Override
		public void mouseClicked(final java.awt.event.MouseEvent e) {
			if (type == DIRECTORY) {
				changeDirectory(file);
			} else {
				if (!isSelected) {
					isSelected = true;
					if (currentlySelected != null) {
						currentlySelected.deSelect();
					}
					if (!openButton.isEnabled()) {
						openButton.setEnabled(true);
					}
					setBackground(SELECTED_COLOR);
					setOpaque(true);
					setForeground(SELECTED_TEXT_COLOR);
					currentlySelected = this;
					currentlySelected.repaint();
				}
				if (e.getClickCount() == 2) {
					openButton.doClick();
				}
			}
		}

		@Override
		public void mouseReleased(final java.awt.event.MouseEvent e) {
		}

		@Override
		public void mousePressed(final java.awt.event.MouseEvent e) {
		}

		@Override
		public void mouseExited(final java.awt.event.MouseEvent e) {
		}

		@Override
		public void mouseEntered(final java.awt.event.MouseEvent e) {
		}
	}
}