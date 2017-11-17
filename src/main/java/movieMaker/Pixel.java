package movieMaker;

import java.awt.Color;

/**
 * Class that references a pixel in a picture. A pixel has an x and y location
 * in a picture. A pixel knows how to get and set the red, green, blue, and
 * alpha values in the picture. A pixel also knows how to get and set the color
 * using a Color object.
 *
 * Copyright Georgia Institute of Technology 2004
 *
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class Pixel {

	// //////////////////////// fields ///////////////////////////////////

	/** the digital picture this pixel belongs to */
	private final DigitalPicture picture;

	/** the x location of this pixel in the picture (0,0) is top left */
	private final int x;

	/** the y location of this pixel in the picture (0,0) is top left */
	private final int y;

	// //////////////////// constructors /////////////////////////////////

	/**
	 * A constructor that take the x and y location for the pixel and the
	 * picture the pixel is coming from
	 *
	 * @param picture
	 *            the picture that the pixel is in
	 * @param x
	 *            the x location of the pixel in the picture
	 * @param y
	 *            the y location of the pixel in the picture
	 */
	public Pixel(final DigitalPicture picture, final int x, final int y) {
		// set the picture
		this.picture = picture;

		// set the x location
		this.x = x;

		// set the y location
		this.y = y;

	}

	// /////////////////////// methods //////////////////////////////

	/**
	 * Method to get the x location of this pixel.
	 *
	 * @return the x location of the pixel in the picture
	 */
	public int getX() {
		return x;
	}

	/**
	 * Method to get the y location of this pixel.
	 *
	 * @return the y location of the pixel in the picture
	 */
	public int getY() {
		return y;
	}

	/**
	 * Method to get the amount of alpha (transparency) at this pixel. It will
	 * be from 0-255.
	 *
	 * @return the amount of alpha (transparency)
	 */
	public int getAlpha() {

		/*
		 * get the value at the location from the picture as a 32 bit int with
		 * alpha, red, green, blue each taking 8 bits from left to right
		 */
		final int value = picture.getBasicPixel(x, y);

		// get the alpha value (starts at 25 so shift right 24)
		// then and it with all 1's for the first 8 bits to keep
		// end up with from 0 to 255
		final int alpha = value >> 24 & 0xff;

		return alpha;
	}

	/**
	 * Method to get the amount of red at this pixel. It will be from 0-255 with
	 * 0 being no red and 255 being as much red as you can have.
	 *
	 * @return the amount of red from 0 for none to 255 for max
	 */
	public int getRed() {

		/*
		 * get the value at the location from the picture as a 32 bit int with
		 * alpha, red, green, blue each taking 8 bits from left to right
		 */
		final int value = picture.getBasicPixel(x, y);

		// get the red value (starts at 17 so shift right 16)
		// then and it with all 1's for the first 8 bits to keep
		// end up with from 0 to 255
		final int red = value >> 16 & 0xff;

		return red;
	}

	/**
	 * Method to get the red value from a pixel represented as an int
	 *
	 * @param value
	 *            the color value as an int
	 * @return the amount of red
	 */
	public static int getRed(final int value) {
		final int red = value >> 16 & 0xff;
		return red;
	}

	/**
	 * Method to get the amount of green at this pixel. It will be from 0-255
	 * with 0 being no green and 255 being as much green as you can have.
	 *
	 * @return the amount of green from 0 for none to 255 for max
	 */
	public int getGreen() {

		/*
		 * get the value at the location from the picture as a 32 bit int with
		 * alpha, red, green, blue each taking 8 bits from left to right
		 */
		final int value = picture.getBasicPixel(x, y);

		// get the green value (starts at 9 so shift right 8)
		final int green = value >> 8 & 0xff;

		return green;
	}

	/**
	 * Method to get the green value from a pixel represented as an int
	 *
	 * @param value
	 *            the color value as an int
	 * @return the amount of green
	 */
	public static int getGreen(final int value) {
		final int green = value >> 8 & 0xff;
		return green;
	}

	/**
	 * Method to get the amount of blue at this pixel. It will be from 0-255
	 * with 0 being no blue and 255 being as much blue as you can have.
	 *
	 * @return the amount of blue from 0 for none to 255 for max
	 */
	public int getBlue() {

		/*
		 * get the value at the location from the picture as a 32 bit int with
		 * alpha, red, green, blue each taking 8 bits from left to right
		 */
		final int value = picture.getBasicPixel(x, y);

		// get the blue value (starts at 0 so no shift required)
		final int blue = value & 0xff;

		return blue;
	}

	/**
	 * Method to get the blue value from a pixel represented as an int
	 *
	 * @param value
	 *            the color value as an int
	 * @return the amount of blue
	 */
	public static int getBlue(final int value) {
		final int blue = value & 0xff;
		return blue;
	}

	/**
	 * Method to get a color object that represents the color at this pixel.
	 *
	 * @return a color object that represents the pixel color
	 */
	public Color getColor() {
		/*
		 * get the value at the location from the picture as a 32 bit int with
		 * alpha, red, green, blue each taking 8 bits from left to right
		 */
		final int value = picture.getBasicPixel(x, y);

		// get the red value (starts at 17 so shift right 16)
		// then and it with all 1's for the first 8 bits to keep
		// end up with from 0 to 255
		final int red = value >> 16 & 0xff;

		// get the green value (starts at 9 so shift right 8)
		final int green = value >> 8 & 0xff;

		// get the blue value (starts at 0 so no shift required)
		final int blue = value & 0xff;

		return new Color(red, green, blue);
	}

	/**
	 * Method to set the pixel color to the passed in color object.
	 *
	 * @param newColor
	 *            the new color to use
	 */
	public void setColor(final Color newColor) {
		// set the red, green, and blue values
		final int red = newColor.getRed();
		final int green = newColor.getGreen();
		final int blue = newColor.getBlue();

		// update the associated picture
		updatePicture(getAlpha(), red, green, blue);
	}

	/**
	 * Method to update the picture based on the passed color values for this
	 * pixel
	 *
	 * @param alpha
	 *            the alpha (transparency) at this pixel
	 * @param red
	 *            the red value for the color at this pixel
	 * @param green
	 *            the green value for the color at this pixel
	 * @param blue
	 *            the blue value for the color at this pixel
	 */
	public void updatePicture(final int alpha, final int red, final int green, final int blue) {
		// create a 32 bit int with alpha, red, green blue from left to right
		final int value = (alpha << 24) + (red << 16) + (green << 8) + blue;

		// update the picture with the int value
		picture.setBasicPixel(x, y, value);
	}

	/**
	 * Method to correct a color value to be within 0 and 255
	 *
	 * @param the
	 *            value to use
	 * @return a value within 0 and 255
	 */
	private static int correctValue(int value) {
		if (value < 0) {
			value = 0;
		}
		if (value > 255) {
			value = 255;
		}
		return value;
	}

	/**
	 * Method to set the red to a new red value
	 *
	 * @param value
	 *            the new value to use
	 */
	public void setRed(final int value) {
		// set the red value to the corrected value
		final int red = correctValue(value);

		// update the pixel value in the picture
		updatePicture(getAlpha(), red, getGreen(), getBlue());
	}

	/**
	 * Method to set the green to a new green value
	 *
	 * @param value
	 *            the value to use
	 */
	public void setGreen(final int value) {
		// set the green value to the corrected value
		final int green = correctValue(value);

		// update the pixel value in the picture
		updatePicture(getAlpha(), getRed(), green, getBlue());
	}

	/**
	 * Method to set the blue to a new blue value
	 *
	 * @param value
	 *            the new value to use
	 */
	public void setBlue(final int value) {
		// set the blue value to the corrected value
		final int blue = correctValue(value);

		// update the pixel value in the picture
		updatePicture(getAlpha(), getRed(), getGreen(), blue);
	}

	/**
	 * Method to set the alpha (transparency) to a new alpha value
	 *
	 * @param value
	 *            the new value to use
	 */
	public void setAlpha(final int value) {
		// make sure that the alpha is from 0 to 255
		final int alpha = correctValue(value);

		// update the associated picture
		updatePicture(alpha, getRed(), getGreen(), getBlue());
	}

	/**
	 * Method to get the distance between this pixel's color and the passed
	 * color
	 *
	 * @param testColor
	 *            the color to compare to
	 * @return the distance between this pixel's color and the passed color
	 */
	public double colorDistance(final Color testColor) {
		final double redDistance = this.getRed() - testColor.getRed();
		final double greenDistance = this.getGreen() - testColor.getGreen();
		final double blueDistance = this.getBlue() - testColor.getBlue();
		final double distance = Math
				.sqrt(redDistance * redDistance + greenDistance * greenDistance + blueDistance * blueDistance);
		return distance;
	}

	/**
	 * Method to compute the color distances between two color objects
	 *
	 * @param color1
	 *            a color object
	 * @param color2
	 *            a color object
	 * @return the distance between the two colors
	 */
	public static double colorDistance(final Color color1, final Color color2) {
		final double redDistance = color1.getRed() - color2.getRed();
		final double greenDistance = color1.getGreen() - color2.getGreen();
		final double blueDistance = color1.getBlue() - color2.getBlue();
		final double distance = Math
				.sqrt(redDistance * redDistance + greenDistance * greenDistance + blueDistance * blueDistance);
		return distance;
	}

	/**
	 * Method to get the average of the colors of this pixel
	 *
	 * @return the average of the red, green, and blue values
	 */
	public double getAverage() {
		final double average = (getRed() + getGreen() + getBlue()) / 3.0;
		return average;
	}

	/**
	 * Method to return a string with information about this pixel
	 *
	 * @return a string with information about this pixel
	 */

	@Override
	public String toString() {
		return "Pixel red=" + getRed() + " green=" + getGreen() + " blue=" + getBlue();
	}

}