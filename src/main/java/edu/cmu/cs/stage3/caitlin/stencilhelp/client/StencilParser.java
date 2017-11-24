package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Point;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ProgressMonitor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication;
import edu.cmu.cs.stage3.caitlin.stencilhelp.client.StencilManager.Stencil;

public class StencilParser { // extends org.xml.sax.helpers.DefaultHandler{
	StringBuffer textBuffer;
	StencilManager stencilManager;
	ObjectPositionManager positionManager;
	StencilApplication stencilApp;

	public StencilParser(final StencilManager stencilManager, final ObjectPositionManager positionManager,
			final StencilApplication stencilApp) {
		this.stencilManager = stencilManager;
		this.positionManager = positionManager;
		this.stencilApp = stencilApp;
	}

	protected void loadStateCapsule(final Node node, final StencilManager.Stencil newStencil) {
		final NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			final Node child = nl.item(i);
			if (child.getNodeValue() != null) {
				final String capsuleString = child.getNodeValue().trim();
				if (capsuleString.length() > 0) {
					final edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule stateCapsule = stencilApp
							.getStateCapsuleFromString(capsuleString);
					newStencil.setEndState(stateCapsule);
				}
			}
		}
	}

	protected void loadNote(final Node node, final StencilManager.Stencil newStencil, final NavigationBar navBar) {
		final NodeList noteParts = node.getChildNodes();

		// get the attributes of the node
		final NamedNodeMap attr = node.getAttributes();
		final Node objectType = attr.getNamedItem("type");
		final Node xPosNode = attr.getNamedItem("xPos");
		final Node yPosNode = attr.getNamedItem("yPos");
		final Node autoAdvanceNode = attr.getNamedItem("autoAdvance");
		final Node advanceEventNode = attr.getNamedItem("advanceEvent");
		final Node hasNextNode = attr.getNamedItem("hasNext");
		boolean hasNext = false;
		int advanceEvent = 0;

		if (hasNextNode != null) {
			if (hasNextNode.getNodeValue().equals("true")) {
				hasNext = true;
			}
		}

		// then we need to get the text of the note too....
		String message = "hello world";
		final Vector<String> msgs = new Vector<String>();
		final Vector colors = new Vector();
		String id = "id";
		for (int i = 0; i < noteParts.getLength(); i++) {
			final Node noteDetails = noteParts.item(i);
			if (noteDetails.getNodeName().equals("id")) {
				id = edu.cmu.cs.stage3.xml.NodeUtilities.getNodeText(noteDetails);
			} else if (noteDetails.getNodeName().equals("message")) {
				message = edu.cmu.cs.stage3.xml.NodeUtilities.getNodeText(noteDetails);
				if (message.length() > 0) {
					msgs.addElement(message);
					/*
					 * NodeList textList = noteDetails.getChildNodes(); for (int
					 * j = 0; j < textList.getLength(); j++) { Node textIHope =
					 * textList.item(j); message = textIHope.getNodeValue(); if
					 * (message != null) message = message.trim(); if
					 * (message.length() > 0) msgs.addElement(message);
					 *
					 * }
					 */
				}

				final NamedNodeMap textAttr = noteDetails.getAttributes();
				if (textAttr != null) {
					final Node textColor = textAttr.getNamedItem("color");
					if (textColor != null) {
						colors.addElement(textColor.getNodeValue());
						// System.out.println("adding color");
					} else {
						colors.addElement(null);
						// System.out.println("adding color");
					}
				} else {
					colors.addElement(null);
					// System.out.println("adding color");
				}
			}
		}

		// create the appropriate note or frame
		// COME BACK - make this save and restore the author's approximate
		// positions for the objects
		if (objectType.getNodeValue().equals("hole")) {
			final Hole hole = new Hole(id, positionManager, stencilApp, stencilManager);
			final Point p = hole.getNotePoint();
			final Point initPos = new Point((int) Double.parseDouble(xPosNode.getNodeValue()),
					(int) Double.parseDouble(yPosNode.getNodeValue()));
			final Note note = new Note(p, initPos, hole, positionManager, stencilManager, hasNext);
			// note.setText(message);
			for (int i = 0; i < msgs.size(); i++) {
				note.addText(msgs.elementAt(i), (String) colors.elementAt(i));
			}
			boolean autoAdvance = false;
			if (autoAdvanceNode != null) {
				if (autoAdvanceNode.getNodeValue().equals("true")) {
					autoAdvance = true;
				}
			}
			if (advanceEventNode != null) {
				if (advanceEventNode.getNodeValue().equals("mousePress")) {
					advanceEvent = Hole.ADVANCE_ON_PRESS;
				} else if (advanceEventNode.getNodeValue().equals("mouseClick")) {
					advanceEvent = Hole.ADVANCE_ON_CLICK;
				} else {
					advanceEvent = Hole.ADVANCE_ON_ENTER;
				}
			}
			hole.setAutoAdvance(autoAdvance, advanceEvent);
			newStencil.addObject(hole);
			newStencil.addObject(note);
		} else if (objectType.getNodeValue().equals("frame")) {
			final Frame frame = new Frame(id, positionManager);
			final Point p = frame.getNotePoint();
			final Point initPos = new Point((int) Double.parseDouble(xPosNode.getNodeValue()),
					(int) Double.parseDouble(yPosNode.getNodeValue()));
			final Note note = new Note(p, initPos, frame, positionManager, stencilManager, hasNext);
			// note.setText(message);
			for (int i = 0; i < msgs.size(); i++) {
				note.addText(msgs.elementAt(i), (String) colors.elementAt(i));
			}
			newStencil.addObject(frame);
			newStencil.addObject(note);
		} else if (objectType.getNodeValue().equals("navBar")) {
			final Point p = navBar.getNotePoint();
			final Point initPos = new Point((int) Double.parseDouble(xPosNode.getNodeValue()),
					(int) Double.parseDouble(yPosNode.getNodeValue()));
			final Note note = new Note(p, initPos, navBar, positionManager, stencilManager, hasNext);
			// note.setText(message);
			for (int i = 0; i < msgs.size(); i++) {
				note.addText(msgs.elementAt(i), (String) colors.elementAt(i));
			}
			newStencil.addObject(note);
		} else {
			final double xRatio = Double.parseDouble(xPosNode.getNodeValue());
			final double yRatio = Double.parseDouble(yPosNode.getNodeValue());
			final Point p = new Point((int) (stencilApp.getScreenSize().getWidth() * xRatio),
					(int) (stencilApp.getScreenSize().getHeight() * yRatio));
			final Note note = new Note(p, new Point(0, 0), null, positionManager, stencilManager, hasNext);
			// note.setText(message);
			for (int i = 0; i < msgs.size(); i++) {
				note.addText(msgs.elementAt(i), (String) colors.elementAt(i));
			}
			newStencil.addObject(note);
		}
	}

	protected StencilManager.Stencil loadStencil(final Node node) {
		final NamedNodeMap attr = node.getAttributes();
		final Node stencilTitle = attr.getNamedItem("title");
		final NavigationBar navBar = new NavigationBar(stencilManager, positionManager);
		if (stencilTitle != null && stencilTitle.getNodeValue() != null) {
			navBar.setTitleString(stencilTitle.getNodeValue());
		}

		final NodeList objects = node.getChildNodes();
		final Node stepsToGoBackNode = attr.getNamedItem("stepsToGoBack");
		int stepsToGoBack = 1;
		if (stepsToGoBackNode != null && stepsToGoBackNode.getNodeValue() != null) {
			stepsToGoBack = Integer.parseInt(stepsToGoBackNode.getNodeValue());
		}
		final StencilManager.Stencil newStencil = stencilManager.newStencil(stepsToGoBack);
		newStencil.addObject(navBar);
		newStencil.addObject(new Menu(stencilManager));
		for (int i = 0; i < objects.getLength(); i++) {
			final Node childNode = objects.item(i);
			if (childNode.getNodeName().equals("note")) {
				loadNote(childNode, newStencil, navBar);
			} else if (childNode.getNodeName().equals("stateCapsule")) {
				loadStateCapsule(childNode, newStencil);
			}
		}
		return newStencil;
	}

	public Vector<Stencil> parseFile(final java.io.File fileToLoad) {
		Document document;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(fileToLoad);
		} catch (final IOException ioe) {
			document = null;
			ioe.printStackTrace();
		} catch (final ParserConfigurationException pce) {
			document = null;
			pce.printStackTrace();
		} catch (final org.xml.sax.SAXException se) {
			document = null;
			se.printStackTrace();
		}

		if (document != null) {
			final Vector<Stencil> stencilList = new Vector<Stencil>();
			final NamedNodeMap attr = document.getDocumentElement().getAttributes();
			final Node readPermission = attr.getNamedItem("access");
			if (readPermission.getNodeValue().equals("read")) {
				stencilManager.setWriteEnabled(false);
			} else {
				stencilManager.setWriteEnabled(true);
			}
			final Node worldToLoad = attr.getNamedItem("world");
			if (worldToLoad != null) {
				stencilManager.setWorld(worldToLoad.getNodeValue());
			}
			// load next and previous stacks
			String nextStack = null;
			String previousStack = null;
			final Node nextStackNode = attr.getNamedItem("nextStack");
			if (nextStackNode != null) {
				nextStack = nextStackNode.getNodeValue();
			}
			final Node previousStackNode = attr.getNamedItem("previousStack");
			if (previousStackNode != null) {
				previousStack = previousStackNode.getNodeValue();
			}
			if (nextStack != null || previousStack != null) {
				stencilManager.setNextAndPreviousStacks(previousStack, nextStack);
			}
			final NodeList stencils = document.getElementsByTagName("stencil");
			final ProgressMonitor monitor = new ProgressMonitor(null, "Loading Tutorial", "", 0, stencils.getLength());
			monitor.setProgress(0);
			monitor.setMillisToDecideToPopup(1000);
			for (int i = 0; i < stencils.getLength(); i++) {
				final Node stencilNode = stencils.item(i);
				final StencilManager.Stencil newStencil = loadStencil(stencilNode);
				stencilList.addElement(newStencil);
				monitor.setProgress(i);
			}
			monitor.close();
			return stencilList;
		} else {
			System.out.println("Could not parse stencil file");
			System.out.flush();
			return null;
		}
	}

	public StencilManager.Stencil getErrorStencil() {
		final NavigationBar navBar = new NavigationBar(stencilManager, positionManager, true);
		navBar.setTitleString("Ooops!");

		final StencilManager.Stencil newStencil = stencilManager.newStencil();
		newStencil.addObject(navBar);

		// create note
		final Point p = new Point((int) ((float) stencilApp.getScreenSize().getWidth() * 0.292),
				(int) ((float) stencilApp.getScreenSize().getHeight() * 0.448));
		final Note note = new Note(p, new Point(0, 0), null, positionManager, stencilManager, false);
		note.addText("The Alice tutorial thinks maybe you didn't follow the instructions carefully.", null);
		note.addText("Please back up to your mistake or restart.", null);
		note.initializeNote();

		newStencil.addObject(note);

		return newStencil;
	}
}