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

package edu.cmu.cs.stage3.alice.authoringtool.util;

public abstract class RenderTargetPickManipulator extends ScreenWrappingMouseListener {
	protected edu.cmu.cs.stage3.alice.core.Transformable ePickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.core.Transformable lastEPickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgPickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget = null;
	protected java.util.HashSet objectsOfInterest = new java.util.HashSet();
	protected java.util.HashSet listeners = new java.util.HashSet();
	protected java.awt.Cursor invisibleCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(
			java.awt.Toolkit.getDefaultToolkit().getImage(""), new java.awt.Point(0, 0), "invisible cursor");
	protected java.awt.Cursor savedCursor = java.awt.Cursor.getDefaultCursor();
	protected java.awt.Point originalMousePoint;
	protected boolean hideCursorOnDrag = true;
	protected boolean popupEnabled = false;
	protected boolean enabled = true;
	protected boolean pickAllForOneObjectOfInterest = true;
	protected boolean ascendTreeEnabled = true;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo;

	public RenderTargetPickManipulator(
			final edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget) {
		setRenderTarget(renderTarget);
	}

	public void setEnabled(final boolean b) {
		enabled = b;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setPickAllForOneObjectOfInterestEnabled(final boolean b) {
		pickAllForOneObjectOfInterest = b;
	}

	public boolean isPickAllForOneObjectOfInterestEnabled() {
		return pickAllForOneObjectOfInterest;
	}

	public void setRenderTarget(final edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget) {
		if (this.renderTarget != null) {
			this.renderTarget.getAWTComponent().removeMouseListener(this);
			if (popupEnabled) {
				this.renderTarget.getAWTComponent().removeMouseListener(popupMouseListener);
			}
		}

		this.renderTarget = renderTarget;
		if (renderTarget != null) {
			renderTarget.getAWTComponent().addMouseListener(this);
			if (popupEnabled) {
				this.renderTarget.getAWTComponent().addMouseListener(popupMouseListener);
			}
		}
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public edu.cmu.cs.stage3.alice.core.Transformable getCorePickedTransformable() {
		return ePickedTransformable;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Transformable getSceneGraphPickedTransformable() {
		return sgPickedTransformable;
	}

	public void addRenderTargetPickManipulatorListener(
			final edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public void removeRenderTargetPickManipulatorListener(
			final edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * when objectsOfInterest is empty, all objects are of interest. when
	 * objectsOfInterest has one element, it will always be picked. when
	 * objectsOfInterest has more than one element, mouseDragged behavior will
	 * only occur if an objectOfInterest is picked only firstClass objects will
	 * be considered
	 */
	public boolean addObjectOfInterest(final edu.cmu.cs.stage3.alice.core.Transformable trans) {
		return objectsOfInterest.add(trans);
	}

	/**
	 * when objectsOfInterest is empty, all objects are of interest. when
	 * objectsOfInterest has one element, it will always be picked. when
	 * objectsOfInterest has more than one element, mouseDragged behavior will
	 * only occur if an objectOfInterest is picked only firstClass objects will
	 * be considered
	 */
	public boolean removeObjectOfInterest(final edu.cmu.cs.stage3.alice.core.Transformable trans) {
		return objectsOfInterest.remove(trans);
	}

	public void clearObjectsOfInterestList() {
		objectsOfInterest.clear();
	}

	public boolean getHideCursorOnDrag() {
		return hideCursorOnDrag;
	}

	public void setHideCursorOnDrag(final boolean b) {
		hideCursorOnDrag = b;
	}

	public void setAscendTreeEnabled(final boolean b) {
		ascendTreeEnabled = b;
	}

	public boolean isAscendTreeEnabled() {
		return ascendTreeEnabled;
	}

	public boolean isPopupEnabled() {
		return popupEnabled;
	}

	public synchronized void setPopupEnabled(final boolean b) {
		if (renderTarget != null) {
			if (b && !popupEnabled) {
				renderTarget.getAWTComponent().addMouseListener(popupMouseListener);
			} else if (!b && popupEnabled) {
				renderTarget.getAWTComponent().removeMouseListener(popupMouseListener);
			}
		}
		popupEnabled = b;
	}

	@Override
	public void mousePressed(final java.awt.event.MouseEvent ev) {
		if (enabled) {
			super.mousePressed(ev);

			firePrePick();
			if (objectsOfInterest.size() == 1 && pickAllForOneObjectOfInterest) {
				ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable) objectsOfInterest.iterator().next();
				sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
			} else {
				// implementors are responsible for pushing their own undos onto
				// the stack
				if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() != null) {
					if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack() != null) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack()
								.setIsListening(false);
					}
				}

				pickInfo = renderTarget.pick(ev.getX(), ev.getY(),
						edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget.SUB_ELEMENT_IS_NOT_REQUIRED,
						edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget.ONLY_FRONT_MOST_VISUAL_IS_REQUIRED);

				if (pickInfo != null) {
					// DEBUG System.out.println( "info not null" );
					final edu.cmu.cs.stage3.alice.scenegraph.Visual[] visuals = pickInfo.getVisuals();
					// DEBUG System.out.println( "visuals: " + visuals );
					// DEBUG if( visuals != null ) {
					// DEBUG System.out.println( "visuals.length: " +
					// visuals.length );
					// DEBUG }
					if (visuals != null && visuals.length >= 1) {

						ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable) visuals[0].getBonus();
						if (ePickedTransformable == null) {
							sgPickedTransformable = (edu.cmu.cs.stage3.alice.scenegraph.Transformable) visuals[0]
									.getParent();
							ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable) sgPickedTransformable
									.getBonus();
						} else {
							sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
						}
						if (ascendTreeEnabled) {
							while (ePickedTransformable != null
									&& ePickedTransformable
											.getParent() instanceof edu.cmu.cs.stage3.alice.core.Transformable
									&& !ePickedTransformable.doEventsStopAscending()
									&& !objectsOfInterest.contains(ePickedTransformable)) {
								// DEBUG System.out.println( "moving up from: "
								// + sgPickedTransformable );
								sgPickedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable) ePickedTransformable
										.getParent()).getSceneGraphTransformable();
								ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable) sgPickedTransformable
										.getBonus();
							}
						}

						if (!objectsOfInterest.isEmpty() && !objectsOfInterest.contains(ePickedTransformable)) {
							abortAction();
						}
					} else {
						// DEBUG System.out.println(
						// "visuals null or zero-length: " + visuals );
						sgPickedTransformable = null;
						ePickedTransformable = null;
					}
				} else {
					// DEBUG System.out.println( "pickInfo null" );
					sgPickedTransformable = null;
					ePickedTransformable = null;
				}
			}
			firePostPick(pickInfo);

			originalMousePoint = ev.getPoint();
			if (!isActionAborted() && hideCursorOnDrag && doWrap
					&& !ev.getComponent().getCursor().equals(invisibleCursor)) {
				savedCursor = ev.getComponent().getCursor();
				ev.getComponent().setCursor(invisibleCursor);
			}
		}
	}

	@Override
	public void mouseReleased(final java.awt.event.MouseEvent ev) {
		if (!isActionAborted() && hideCursorOnDrag && doWrap) {
			ev.getComponent().setCursor(savedCursor);
			// TODO: position mouse based on object of interest's position in
			// the picture plane; for now, it does a rough approximation
			final java.awt.Point tempPoint = ev.getPoint();

			javax.swing.SwingUtilities.convertPointToScreen(tempPoint, ev.getComponent());
			javax.swing.SwingUtilities.convertPointToScreen(originalMousePoint, ev.getComponent());
			edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation(tempPoint.x, originalMousePoint.y);
			// robot.mouseMove( tempPoint.x, originalMousePoint.y );
		}

		lastEPickedTransformable = ePickedTransformable;

		ePickedTransformable = null;
		sgPickedTransformable = null;
		pickInfo = null;
		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() != null) {
			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack() != null) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().setIsListening(true);
			}
		}

		super.mouseReleased(ev);
	}

	@Override
	public void abortAction() {
		component.setCursor(savedCursor);
		super.abortAction();
	}

	protected void firePrePick() {
		final edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent(
				renderTarget, null);
		for (final java.util.Iterator iter = listeners.iterator(); iter.hasNext();) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener) iter.next())
					.prePick(ev);
		}
	}

	protected void firePostPick(final edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo) {
		final edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent(
				renderTarget, pickInfo);
		for (final java.util.Iterator iter = listeners.iterator(); iter.hasNext();) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener) iter.next())
					.postPick(ev);
		}
	}

	// TODO: this should be handled elsewhere
	protected final java.awt.event.MouseListener popupMouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
		Runnable emptyRunnable = new Runnable() {
			@Override
			public void run() {
			}
		};

		@Override
		protected void popupResponse(final java.awt.event.MouseEvent e) {
			if (lastEPickedTransformable != null) {
				final javax.swing.JPopupMenu popup = createPopup(lastEPickedTransformable);
				popup.show(e.getComponent(), e.getX(), e.getY());
				PopupMenuUtilities.ensurePopupIsOnScreen(popup);
			}
		}

		private javax.swing.JPopupMenu createPopup(final edu.cmu.cs.stage3.alice.core.Element element) {
			final java.util.Vector popupStructure = ElementPopupUtilities.getDefaultStructure(element);
			return edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.makeElementPopupMenu(element,
					popupStructure);
		}
	};
}