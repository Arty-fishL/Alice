package edu.cmu.cs.stage3.alice.gallery.batch;

import java.io.File;

public abstract class LinkBatch {
	public void forEachLink(final java.io.File dir, final LinkHandler linkHandler) {
		final java.io.File[] dirs = dir.listFiles(new java.io.FileFilter() {
			@Override
			public boolean accept(final java.io.File file) {
				return file.isDirectory();
			}
		});
		for (final File dir2 : dirs) {
			forEachLink(dir2, linkHandler);
		}

		final java.io.File[] files = dir.listFiles(new java.io.FilenameFilter() {
			@Override
			public boolean accept(final java.io.File dir, final String name) {
				return name.endsWith(".link");
			}
		});
		for (final File fileI : files) {
			try {
				final java.io.BufferedReader r = new java.io.BufferedReader(new java.io.FileReader(fileI));
				final String s = r.readLine();
				linkHandler.handleLink(fileI, s);
			} catch (final java.io.IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
