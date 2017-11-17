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

package edu.cmu.cs.stage3.alice.moviemaker;

public class MovieMaker extends edu.cmu.cs.stage3.alice.player.DefaultPlayer {
	private final String m_directoryPath;
	private final String m_fileName;
	private final java.text.NumberFormat m_numberFormat;
	private final String m_extension;
	@SuppressWarnings("unused")
	private final int m_index;

	public MovieMaker(final Class<?> rendererClass, final String directoryPath, final String fileName,
			final String localizedPattern, final String extension) {
		super(rendererClass);
		m_directoryPath = directoryPath;
		m_fileName = fileName;
		m_numberFormat = java.text.NumberFormat.getInstance();
		if (m_numberFormat instanceof java.text.DecimalFormat) {
			final java.text.DecimalFormat df = (java.text.DecimalFormat) m_numberFormat;
			df.applyLocalizedPattern(localizedPattern);
		}
		m_extension = extension;
		m_index = 0;
	}

	@Override
	protected edu.cmu.cs.stage3.alice.core.Clock newClock() {
		return new edu.cmu.cs.stage3.alice.core.clock.FixedFrameRateClock(30);
	}

	@Override
	protected int getDesiredFrameWidth() {
		return 640 + 8;
	}

	@Override
	protected int getDesiredFrameHeight() {
		return 480 + 34;
	}

	private void getPathForIndex(final StringBuffer sb, final int index) {
		sb.append(m_directoryPath);
		sb.append(java.io.File.separatorChar);
		sb.append(m_fileName);
		sb.append(m_numberFormat.format(index));
		sb.append(".");
		sb.append(m_extension);
	}

	public String getPathForIndex(final int index) {
		final StringBuffer sb = new StringBuffer();
		getPathForIndex(sb, index);
		return sb.toString();
	}

	@Override
	protected void handleRenderTarget(final edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget) {
		super.handleRenderTarget(renderTarget);
		renderTarget
				.addRenderTargetListener(new edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener() {
					private int m_index = 0;

					@Override
					public void cleared(
							final edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent renderTargetEvent) {
					}

					@Override
					public void rendered(
							final edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent renderTargetEvent) {
						final java.awt.Image image = renderTargetEvent.getRenderTarget().getOffscreenImage();
						final String path = getPathForIndex(m_index++);
						try {
							final java.awt.image.ImageObserver observer = new java.awt.image.ImageObserver() {
								@Override
								public boolean imageUpdate(final java.awt.Image arg0, final int arg1, final int arg2,
										final int arg3, final int arg4, final int arg5) {
									return false;
								}
							};
							final java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(
									image.getWidth(observer), image.getHeight(observer),
									java.awt.image.BufferedImage.TYPE_3BYTE_BGR);
							bi.getGraphics().drawImage(image, 0, 0, observer);
							edu.cmu.cs.stage3.image.ImageIO.store("bmp", new java.io.FileOutputStream(path), bi);
						} catch (final InterruptedException ie) {
							throw new RuntimeException(ie);
						} catch (final java.io.IOException ioe) {
							throw new RuntimeException(ioe);
						}
						System.err.println(image);
					}

				});
	}
	/*
	 * public static void main(String[] args) { Class rendererClass =
	 * edu.cmu.cs.
	 * stage3.alice.scenegraph.renderer.directx7renderer.Renderer.class; //Class
	 * rendererClass =
	 * edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer.class;
	 * java.io.File file = new java.io.File( args[ 0 ] ); MovieMaker player =
	 * new MovieMaker( rendererClass, args[ 1 ], args[ 2 ], args[ 3 ], args[ 4 ]
	 * ); try { player.loadWorld( file, null ); player.startWorld(); } catch(
	 * java.io.IOException ioe ) { ioe.printStackTrace(); } }
	 */

}
