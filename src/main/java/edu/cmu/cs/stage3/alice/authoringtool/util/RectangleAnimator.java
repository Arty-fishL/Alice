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
public class RectangleAnimator extends javax.swing.JWindow implements Runnable {
	/**
	 *
	 */
	private static final long serialVersionUID = -7868615367238078961L;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected java.awt.Rectangle sourceBounds;
	protected java.awt.Rectangle targetBounds;
	protected long duration = 300;
	protected long startTime;

	public RectangleAnimator(final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
	}

	public java.awt.Color getColor() {
		return getBackground();
	}

	public void setColor(final java.awt.Color color) {
		setBackground(color);
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}

	public java.awt.Rectangle getSourceBounds() {
		return sourceBounds;
	}

	public void setSourceBounds(final java.awt.Rectangle r) {
		sourceBounds = r;
	}

	public java.awt.Rectangle gettargetBounds() {
		return targetBounds;
	}

	public void setTargetBounds(final java.awt.Rectangle r) {
		targetBounds = r;
	}

	public void animate(final java.awt.Rectangle sourceBounds, final java.awt.Rectangle targetBounds) {
		setSourceBounds(sourceBounds);
		setTargetBounds(targetBounds);

		setBounds(sourceBounds);
		setVisible(true);

		startTime = System.currentTimeMillis();
		authoringTool.getScheduler().addEachFrameRunnable(this);
	}

	public void animate(final java.awt.Rectangle sourceBounds, final java.awt.Rectangle targetBounds,
			final java.awt.Color color) {
		setColor(color);
		animate(sourceBounds, targetBounds);
	}

	@Override
	public void run() {
		final long time = System.currentTimeMillis();
		final long dt = time - startTime;
		if (dt <= duration) {
			final double portion = (double) dt / (double) duration;
			final int x = sourceBounds.x + (int) (portion * (targetBounds.x - sourceBounds.x));
			final int y = sourceBounds.y + (int) (portion * (targetBounds.y - sourceBounds.y));
			final int w = sourceBounds.width + (int) (portion * (targetBounds.width - sourceBounds.width));
			final int h = sourceBounds.height + (int) (portion * (targetBounds.height - sourceBounds.height));
			setBounds(x, y, w, h);
			repaint();
		} else {
			setVisible(false);
			authoringTool.getScheduler().removeEachFrameRunnable(RectangleAnimator.this);
			//
			// authoringTool.getScheduler().addDoOnceRunnable( new Runnable() {
			// public void run() {
			// authoringTool.getScheduler().removeEachFrameRunnable(
			// RectangleAnimator.this );
			// }
			// } );
			//
			// SwingWorker worker = new SwingWorker() { // use a separate thread
			// to avoid concurrent modification of the scheduler
			// public Object construct() {
			// authoringTool.getScheduler().removeEachFrameRunnable(
			// RectangleAnimator.this );
			// return null;
			// }
			// };
			// worker.start();
		}
	}
}
