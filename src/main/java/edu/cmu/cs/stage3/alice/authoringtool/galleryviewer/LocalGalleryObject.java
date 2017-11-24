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

package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

/**
 * @author David Culyba
 *
 */

public class LocalGalleryObject extends GalleryObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1990014467173330417L;
	public static final String tumbnailFilename = "thumbnail.png";

	@Override
	protected String getToolTipString() {
		return "<html><body><p>Object</p><p>Click to add this object to the world</p></body></html>";
	}

	@Override
	protected void guiInit() {
		super.guiInit();
		if (data != null) {
			if (data.type == GalleryViewer.CD) {
				location = "CD-ROM";
			} else {
				location = "your computer";
			}
		}
	}

	@Override
	public void set(final GalleryViewer.ObjectXmlData dataIn) throws java.lang.IllegalArgumentException {
		super.set(dataIn);
		if (data != null) {
			if (data.type == GalleryViewer.CD) {
				location = "CD-ROM";
			} else {
				location = "your computer";
			}
		}
	}

	public static java.awt.Image retrieveImage(final String root, final String filename, final long timestamp) {
		final String imageFilename = root + filename;
		javax.swing.ImageIcon toReturn = null;
		if (imageFilename != null) {
			try {
				if (imageFilename.indexOf(".a2c") == imageFilename.length() - 4
						|| imageFilename.indexOf(".a2w") == imageFilename.length() - 4) {
					final java.util.zip.ZipFile zip = new java.util.zip.ZipFile(imageFilename);
					try {
						final java.util.zip.ZipEntry entry = zip.getEntry(tumbnailFilename);
						if (entry != null) {
							final java.io.InputStream stream = zip.getInputStream(entry);
							final java.awt.Image thumbImage = edu.cmu.cs.stage3.image.ImageIO.load("png", stream);
							if (thumbImage != null) {
								toReturn = new javax.swing.ImageIcon(thumbImage);
							} else {
								return null;
							}
						} else {
							return null;
						}
					} finally {
						zip.close();
					}
				} else {
					toReturn = new javax.swing.ImageIcon(imageFilename);
				}
			} catch (final Exception e) {
				return null;
			}
			if (toReturn.getIconHeight() < 10 || toReturn.getIconWidth() < 10) {
				return null;
			}
		}
		return toReturn.getImage();
	}

	@Override
	public void loadImage() {
		String tempFilename = null;
		if (data != null && data.imageFilename != null) {
			tempFilename = rootPath + data.imageFilename;
		}
		final String imageFilename = tempFilename;
		final Runnable doLoad = new Runnable() {
			@Override
			public void run() {
				if (imageFilename != null) {
					try {
						if (imageFilename.indexOf(".a2c") == imageFilename.length() - 4
								|| imageFilename.indexOf(".a2w") == imageFilename.length() - 4) {
							final java.util.zip.ZipFile zip = new java.util.zip.ZipFile(imageFilename);
							final java.util.zip.ZipEntry entry = zip.getEntry(tumbnailFilename);
							if (entry != null) {
								final java.io.InputStream stream = zip.getInputStream(entry);
								final java.awt.Image thumbImage = edu.cmu.cs.stage3.image.ImageIO.load("png", stream);
								if (thumbImage != null) {
									image = new javax.swing.ImageIcon(thumbImage);
								} else {
									image = GalleryViewer.noImageIcon;
								}
							} else {
								image = GalleryViewer.noImageIcon;
							}
							zip.close();
						} else {
							image = new javax.swing.ImageIcon(imageFilename);
						}
					} catch (final Exception e) {
						image = GalleryViewer.noImageIcon;
					}
				} else {
					image = GalleryViewer.noImageIcon;
				}
				if (image.getIconHeight() < 10 || image.getIconWidth() < 10) {
					image = GalleryViewer.noImageIcon;
				}
				setImage(image);
			}
		};
		final Thread t = new Thread(doLoad);
		t.start();
	}

}