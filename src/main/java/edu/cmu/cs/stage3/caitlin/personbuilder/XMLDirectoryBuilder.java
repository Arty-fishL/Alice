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

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author
 * @version 1.0
 */

public class XMLDirectoryBuilder {

	public XMLDirectoryBuilder() {
		final File mainFile = loadMainDirectory();
		generateDocument(mainFile);
	}

	protected void generateDocument(final File mainFile) {
		Document document;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (final ParserConfigurationException pce) {
			document = null;
		}

		if (document != null) {
			final Element root = document.createElement("directory");
			root.setAttribute("path", mainFile.getName());
			if (mainFile.isDirectory()) {
				final File[] kidFiles = mainFile.listFiles();
				for (final File kidFile : kidFiles) {
					createChildren(document, root, kidFile, mainFile.getName());
				}
			}

			document.appendChild(root);
			document.getDocumentElement().normalize();

			try {
				final java.io.FileWriter fileWriter = new java.io.FileWriter(
						mainFile.getAbsolutePath() + File.separator + "structure.xml");
				edu.cmu.cs.stage3.xml.Encoder.write(document, fileWriter);
				fileWriter.close();
			} catch (final java.io.IOException ioe) {
				ioe.printStackTrace();
			}

			// try {
			// ((com.sun.xml.tree.XmlDocument)document).write( new
			// java.io.PrintWriter(new java.io.BufferedWriter(new
			// java.io.FileWriter(mainFile.getAbsolutePath() + File.separator +
			// "structure.xml"))) );
			// } catch (java.io.IOException ioe)
			// {System.err.println("problems creating printwriter");};
		}
	}

	protected void createChildren(final Document document, final Element element, final File file, final String dir) {
		if (file.isDirectory()) {
			final Element childElement = document.createElement("directory");
			childElement.setAttribute("path", dir + '/' + file.getName());
			element.appendChild(childElement);

			final File[] kidFiles = file.listFiles();
			for (final File kidFile : kidFiles) {
				createChildren(document, childElement, kidFile, dir + '/' + file.getName());
			}
		} else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".gif")) {
			final Element childElement = document.createElement("image");
			childElement.setAttribute("path", dir + '/' + file.getName());
			element.appendChild(childElement);
		} else if (file.getName().endsWith(".xml")) {
			final Element childElement = document.createElement("xml");
			childElement.setAttribute("path", dir + '/' + file.getName());
			element.appendChild(childElement);
		} else if (file.getName().endsWith(".a2c")) {
			final Element childElement = document.createElement("model");
			childElement.setAttribute("path", dir + '/' + file.getName());
			element.appendChild(childElement);
		}

	}

	protected File loadMainDirectory() {
		final java.net.URL imageURL = java.lang.ClassLoader
				.getSystemResource("edu\\cmu\\cs\\stage3\\caitlin\\personbuilder\\images");
		final File mainFile = new java.io.File(imageURL.getFile());

		return mainFile;
	}
}