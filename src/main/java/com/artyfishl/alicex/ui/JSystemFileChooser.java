package com.artyfishl.alicex.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;

import sun.swing.FilePane;

/**
 * Enables the use of the system (Windows) file chooser L&F, 
 * even when the rest of the UI is using a different L&F (ie. Metal).
 * 
 * This is definitely a hack.
 * @see https://stackoverflow.com/a/4350171
 *
 * <p/>
 * FILE: 	JSystemFileChooser.java                   	<br/>
 * CREATED: 18 Nov 2017 17:55                 			<br/>
 * PROJECT: Alice 2.4 Extended Source Project	      	<br/>
 * @author  Jamie Lievesley (jamie@jamiegl.co.uk) as ArtyFishL (arty@artyfishl.com)
 * @version 1.0, 2017/11/18
 */
@SuppressWarnings("restriction")
public class JSystemFileChooser extends JFileChooser {
	private static final long serialVersionUID = -7327271281770903864L;

	
	// Constructors just mirror superclass
	public JSystemFileChooser() {
		super();
	}
	public JSystemFileChooser(final File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
	}
	public JSystemFileChooser(final File currentDirectory) {
		super(currentDirectory);
	}
	public JSystemFileChooser(final FileSystemView fsv) {
		super(fsv);
	}
	public JSystemFileChooser(final String currentDirectory) {
		super(currentDirectory);
	}
	public JSystemFileChooser(final String currentDirectoryPath, final FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}

	
	
	public void updateUI() {
		LookAndFeel old = UIManager.getLookAndFeel();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable ex) {
			old = null;
		}

		super.updateUI();

		if (old != null) {
			FilePane filePane = findFilePane(this);
			filePane.setViewType(FilePane.VIEWTYPE_DETAILS);
			filePane.setViewType(FilePane.VIEWTYPE_LIST);

			Color background = UIManager.getColor("Label.background");
			setBackground(background);
			setOpaque(true);

			try {
				UIManager.setLookAndFeel(old);
			} catch (UnsupportedLookAndFeelException ignored) {
			} // shouldn't get here
		}
	}

	private static FilePane findFilePane(final Container aParent) {
		for (Component comp : aParent.getComponents()) {
			if (FilePane.class.isInstance(comp)) {
				return (FilePane) comp;
			}
			if (comp instanceof Container) {
				Container cont = (Container) comp;
				if (cont.getComponentCount() > 0) {
					FilePane found = findFilePane(cont);
					if (found != null) {
						return found;
					}
				}
			}
		}

		return null;
	}

}
