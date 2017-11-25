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

/**
 * @author Jason Pratt
 */
public class ResizeMode extends RenderTargetManipulatorMode {
	protected edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable;
	protected edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;
	protected javax.vecmath.Vector3d oldSize;

	public ResizeMode(final edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack,
			final edu.cmu.cs.stage3.alice.core.Scheduler scheduler) {
		this.undoRedoStack = undoRedoStack;
		this.scheduler = scheduler;
	}

	@Override
	public boolean requiresPickedObject() {
		return true;
	}

	@Override
	public boolean hideCursorOnDrag() {
		return true;
	}

	@Override
	public void mousePressed(final java.awt.event.MouseEvent ev,
			final edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable,
			final edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo) {
		this.pickedTransformable = pickedTransformable;
		if (pickedTransformable != null) {
			oldSize = pickedTransformable.getSize();
		}
	}

	@Override
	public void mouseReleased(final java.awt.event.MouseEvent ev) {
		if (pickedTransformable != null && undoRedoStack != null) {
			if (!ev.isPopupTrigger()) { // TODO: this is a hack. this method
										// should never be called if the popup
										// is triggered
				undoRedoStack.push(new SizeUndoableRedoable(pickedTransformable, oldSize, pickedTransformable.getSize(),
						scheduler));
			}

			if (pickedTransformable.poses.size() > 0) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
						"Warning: resizing objects with poses may make those poses unusable.", "Pose warning",
						javax.swing.JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	@Override
	public void mouseDragged(final java.awt.event.MouseEvent ev, final int dx, final int dy) {
		// Unused ?? final javax.vecmath.Vector3d currentSize = pickedTransformable.getSize();
		// if ( (currentSize.x > 0 && currentSize.y > 0 ) || dy < 0 ) { //Aik
		// Min
		if (pickedTransformable != null && dy != 0) {
			final double divisor = ev.isShiftDown() ? 1000.0 : 100.0;
			final double scaleFactor = 1.0 - dy / divisor;
			pickedTransformable.resizeRightNow(scaleFactor);
		}
		// }
	}
}