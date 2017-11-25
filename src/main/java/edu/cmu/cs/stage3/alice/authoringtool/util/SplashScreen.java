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

import java.util.Calendar;

/**
 * @author Jason Pratt
 */
public class SplashScreen extends java.awt.Frame {
	/**
	 *
	 */
	private static final long serialVersionUID = 5074978197408036348L;
	protected java.awt.Image image;
	protected java.awt.Dimension size;
	protected java.awt.Window splashWindow;

	public SplashScreen(final java.awt.Image image) {
		this.image = image;

		final java.awt.MediaTracker tracker = new java.awt.MediaTracker(this);
		tracker.addImage(image, 0);
		try {
			tracker.waitForID(0);
		} catch (final InterruptedException e) {
		}

		size = new java.awt.Dimension(image.getWidth(this), image.getHeight(this));
		if (size.width < 1 || size.height < 1) {
			size = new java.awt.Dimension(256, 256);
		}

		splashWindow = new java.awt.Window(this) {

			/**
			 *
			 */
			private static final long serialVersionUID = 8679053792520117620L;

			@Override
			public void paint(final java.awt.Graphics g) {
				g.drawImage(SplashScreen.this.image, 0, 0, this);
				// g.setColor( java.awt.Color.yellow );
				g.setColor(java.awt.Color.white);
				final String versionString = "version: " + edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion();
				final int stringWidth = g.getFontMetrics().stringWidth(versionString);
				// g.drawString( versionString, 6, size.height - 6 ); //TODO:
				// this makes the Splash Screen unnecessarily specialized. the
				// functionality should be abstracted out.
				g.drawString(versionString, size.width - 6 - stringWidth, size.height - 6); // TODO:
																							// this
																							// makes
																							// the
																							// Splash
																							// Screen
																							// unnecessarily
																							// specialized.
																							// the
																							// functionality
																							// should
																							// be
																							// abstracted
																							// out.
				if (hasNewVersion()) {
					g.drawString("Loading...                      New Alice 2.2 update available ", 10,
							size.height - 6);
				} else {
					g.drawString("Loading...", 10, size.height - 6);
				}
			}
		};
		splashWindow.setSize(size);

		this.setSize(size);
	}

	public boolean hasNewVersion() {
		if (System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) {
			try {
				final StringBuffer sb = new StringBuffer("http://alicedownloads.org/alice.jar");
				final java.net.URL url = new java.net.URL(sb.toString());
				final java.net.URLConnection urlc = url.openConnection();
				final long i = urlc.getLastModified();
				final java.util.Date d = new java.util.Date(i);
				final java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\D");
				final String oldVersion[] = p.split(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion());
				
				final Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				final int month = cal.get(Calendar.MONTH);
				final int year = cal.get(Calendar.YEAR);
				
				if (Integer.valueOf(oldVersion[3]).compareTo(Integer.valueOf(month + 1)) < 0
						&& Integer.valueOf(oldVersion[5]).compareTo(Integer.valueOf(year + 1900)) <= 0) {
					return true;
				}
			} catch (final Exception e) {

			}
		}
		return false;
	}

	public void showSplash() {
		final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		final int x = (screenSize.width - size.width) / 2;
		final int y = (screenSize.height - size.height) / 2;
		splashWindow.setLocation(x, y);
		this.setLocation(x, y);
		splashWindow.setVisible(true);
	}

	public void hideSplash() {
		// splashWindow.setVisible( false );
		splashWindow.dispose();
	}
}