package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

/**
 * <p>Title: ScreenShape</p>
 * <p>Description: ScreenObjects provide a list of ScreenShapes for the
 * StencilPanel to draw </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Caitlin Kelleher
 * @version 1.0
 */

import java.awt.Color;
import java.awt.Shape;

public class ScreenShape {
	protected Color color = null;
	protected Shape shape = null;
	protected boolean isFilled = true;
	protected int index = -1;

	public ScreenShape() {
	}

	public ScreenShape(final Color color, final Shape shape, final boolean isFilled, final int index) {
		this.color = color;
		this.shape = shape;
		this.isFilled = isFilled;
		this.index = index;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setShape(final Shape shape) {
		this.shape = shape;
	}

	public Shape getShape() {
		return shape;
	}

	public void setIsFilled(final boolean isFilled) {
		this.isFilled = isFilled;
	}

	public boolean getIsFilled() {
		return isFilled;
	}

	public int getIndex() {
		return index;
	}
}