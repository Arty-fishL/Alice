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

import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author
 * @version 1.0
 */

public class XMLDirectoryUtilities {

	public XMLDirectoryUtilities() {
	}

	public static Node loadURL(final java.net.URL url) {
		Document document = null;
		if (url != null) {
			try {
				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				final DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(url.openStream());
			} catch (final java.io.IOException ioe) {
				document = null;
				ioe.printStackTrace();
			} catch (final ParserConfigurationException pce) {
				document = null;
				pce.printStackTrace();
			} catch (final org.xml.sax.SAXException se) {
				document = null;
				se.printStackTrace();
			}

			return document;
		}
		return null;
	}

	public static String getPath(final Node node) {
		final NamedNodeMap nodeMap = node.getAttributes();
		final Node pathNode = nodeMap.getNamedItem("path");
		if (pathNode != null) {
			return pathNode.getNodeValue();
		} else {
			return null;
		}
	}

	public static Node loadFile(final String fileName) {
		final java.net.URL url = XMLDirectoryUtilities.class.getResource(fileName);
		return loadURL(url);
	}

	public static Vector getDirectories(final Node node) {
		final NodeList nList = node.getChildNodes();
		final Vector directoryNodes = new Vector();
		for (int i = 0; i < nList.getLength(); i++) {
			final Node kidNode = nList.item(i);
			if (kidNode.getNodeName().equals("directory")) {
				directoryNodes.addElement(kidNode);
			}
		}
		return directoryNodes;
	}

	public static Vector getSetColorNodes(final Node node) {
		final NodeList nList = node.getChildNodes();
		final Vector propertySetNodes = new Vector();
		for (int i = 0; i < nList.getLength(); i++) {
			final Node kidNode = nList.item(i);
			if (kidNode.getNodeName().equals("setColor")) {
				propertySetNodes.addElement(kidNode);
			}
		}
		return propertySetNodes;
	}

	protected static Vector getFilesOfType(final String nodeType, final Node node) {
		final NodeList nList = node.getChildNodes();
		final Vector files = new Vector();
		for (int i = 0; i < nList.getLength(); i++) {
			final Node kidNode = nList.item(i);
			if (kidNode.getNodeName().equals(nodeType)) {
				final NamedNodeMap nodeMap = kidNode.getAttributes();
				final Node pathNode = nodeMap.getNamedItem("path");
				String path = null;
				if (pathNode != null) {
					path = pathNode.getNodeValue();
					final java.net.URL url = PersonBuilder.class.getResource(path);
					if (url != null) {
						files.addElement(url);
					}
				}
			}
		}
		return files;
	}

	public static Vector getImageURLs(final Node node) {
		return getFilesOfType("image", node);
	}

	public static Vector getImages(final Node node) {
		final Vector urls = getImageURLs(node);
		final Vector images = new Vector();
		final java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
		for (int i = 0; i < urls.size(); i++) {
			final java.net.URL url = (java.net.URL) urls.elementAt(i);
			try {
				final java.awt.Image img = tk.createImage(url);
				if (img != null) {
					images.addElement(img);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return images;
	}

	public static Vector getModelURLs(final Node node) {
		return getFilesOfType("model", node);
	}

	public static Vector getXMLURLs(final Node node) {
		return getFilesOfType("xml", node);
	}

	public static Vector getPropertySets(final Node node) {
		return getFilesOfType("propertySet", node);
	}
}