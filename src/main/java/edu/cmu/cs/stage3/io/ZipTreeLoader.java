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

package edu.cmu.cs.stage3.io;

/**
 * @author Jason Pratt
 */
public class ZipTreeLoader implements DirectoryTreeLoader {
	protected java.util.zip.ZipInputStream zipIn = null;
	protected String currentDirectory = null;
	protected boolean isLoading = false;
	protected java.util.Hashtable pathnamesToByteArrays = null;
	protected java.util.Vector directories = null;
	private Object pathnameBeingWaitedOn = null;
	protected final static Object WHOLE_FILE = new Object();
	protected ZipLoaderThread loaderThread = null;
	protected java.io.InputStream currentlyOpenStream = null;

	/**
	 * pathname can be a String (representing a file on disk), a java.io.File, a
	 * java.net.URL, or a java.io.InputStream
	 */
	@Override
	public void open(final Object pathname) throws IllegalArgumentException, java.io.IOException {
		if (zipIn != null) {
			close();
		}

		java.io.InputStream in = null;
		if (pathname instanceof String) {
			in = new java.io.FileInputStream((String) pathname);
		} else if (pathname instanceof java.io.File) {
			in = new java.io.FileInputStream((java.io.File) pathname);
		} else if (pathname instanceof java.net.URL) {
			in = ((java.net.URL) pathname).openStream();
		} else if (pathname instanceof java.io.InputStream) {
			in = (java.io.InputStream) pathname;
		} else if (pathname == null) {
			throw new IllegalArgumentException("pathname is null");
		} else {
			throw new IllegalArgumentException(
					"pathname must be an instance of String, java.io.File, java.net.URL, or java.io.InputStream");
		}

		zipIn = new java.util.zip.ZipInputStream(new java.io.BufferedInputStream(in));
		currentDirectory = "";
		pathnamesToByteArrays = new java.util.Hashtable();
		directories = new java.util.Vector();
		loaderThread = new ZipLoaderThread();
		loaderThread.start();
	}

	@Override
	public void close() throws java.io.IOException {
		if (zipIn != null) {
			closeCurrentFile();
			if (isLoading) {
				loaderThread.stopEarly();
				waitFor(ZipTreeLoader.WHOLE_FILE);
			}
			zipIn.close();
			zipIn = null;
			pathnamesToByteArrays = null;
			directories = null;
			loaderThread = null;
		}
	}

	@Override
	public void setCurrentDirectory(String pathname) throws IllegalArgumentException {
		if (pathname == null) {
			pathname = "";
		} else if (pathname.length() > 0) {
			if (!(pathname.charAt(0) == '/' || pathname.charAt(0) == '\\')) {
				pathname = currentDirectory + pathname;
			}

			pathname = getCanonicalPathname(pathname);

			if (!pathname.endsWith("/")) {
				pathname = pathname + "/";
			}
			if (!pathname.startsWith("/")) {
				pathname = "/" + pathname + "/"; // todo: replace with line
													// below? dennisc
				// pathname = "/" + pathname;
			}
		}

		currentDirectory = pathname;
	}

	@Override
	public String getCurrentDirectory() {
		return currentDirectory;
	}

	@Override
	public java.io.InputStream readFile(final String filename) throws IllegalArgumentException, java.io.IOException {
		closeCurrentFile();

		final String pathname = getCanonicalPathname(currentDirectory + filename);
		waitFor(pathname);

		final byte[] fileContents = (byte[]) pathnamesToByteArrays.get(pathname);
		if (fileContents == null) {
			throw new java.io.FileNotFoundException("Not Found: " + pathname);
		}

		currentlyOpenStream = new java.io.ByteArrayInputStream(fileContents);
		return currentlyOpenStream;
	}

	@Override
	public void closeCurrentFile() throws java.io.IOException {
		if (currentlyOpenStream != null) {
			currentlyOpenStream.close();
			currentlyOpenStream = null;
		}
	}

	/**
	 * Warning: the entire zip stream must be read into memory before this
	 * method will complete
	 */
	@Override
	public String[] getFilesInCurrentDirectory() {
		waitFor(ZipTreeLoader.WHOLE_FILE);

		final java.util.Vector files = new java.util.Vector();
		for (final java.util.Enumeration enum0 = pathnamesToByteArrays.keys(); enum0.hasMoreElements();) {
			final String filename = (String) enum0.nextElement();
			final int index = filename.indexOf(currentDirectory);
			if (index == 0) {
				final String tail = filename.substring(currentDirectory.length());
				if (tail.indexOf('/') == -1) {
					files.addElement(tail);
				}
			}
		}

		final String[] filenames = new String[files.size()];
		int i = 0;
		for (final java.util.Enumeration enum0 = files.elements(); enum0.hasMoreElements();) {
			filenames[i++] = (String) enum0.nextElement();
		}

		return filenames;
	}

	/**
	 * Warning: the entire zip stream must be read into memory before this
	 * method will complete
	 */
	@Override
	public String[] getDirectoriesInCurrentDirectory() {
		waitFor(ZipTreeLoader.WHOLE_FILE);

		final java.util.Vector dirs = new java.util.Vector();
		for (final java.util.Enumeration enum0 = directories.elements(); enum0.hasMoreElements();) {
			final String dirname = (String) enum0.nextElement();
			final int index = dirname.indexOf(currentDirectory);
			if (index == 0) {
				final String tail = dirname.substring(currentDirectory.length());
				if (tail.length() > 0) {
					if (tail.indexOf('/') == tail.lastIndexOf('/')) {
						dirs.addElement(tail);
					}
				}
			}
		}

		final String[] dirnames = new String[dirs.size()];
		int i = 0;
		for (final java.util.Enumeration enum0 = dirs.elements(); enum0.hasMoreElements();) {
			dirnames[i++] = (String) enum0.nextElement();
		}

		return dirnames;
	}

	protected String getCanonicalPathname(String pathname) {
		pathname = pathname.replace('\\', '/');

		// remove double separators
		int index;
		while ((index = pathname.indexOf("//")) != -1) {
			pathname = pathname.substring(0, index + 1) + pathname.substring(index + 2);
		}

		if (pathname.charAt(0) == '/') {
			pathname = pathname.substring(1);
		}

		return pathname;
	}

	synchronized protected void waitFor(final Object pathname) {
		if (pathnamesToByteArrays.get(pathname) != null) {
			return;
		} else if (directories.indexOf(pathname) != -1) {
			return;
		} else if (pathname == ZipTreeLoader.WHOLE_FILE && !isLoading) {
			return;
		} else {
			pathnameBeingWaitedOn = pathname;
			try {
				wait();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	synchronized protected void finishedLoading(final Object pathname) {
		if (pathname.equals(pathnameBeingWaitedOn) || pathname.equals(ZipTreeLoader.WHOLE_FILE)) {
			notifyAll();
		}
	}

	class ZipLoaderThread extends Thread {
		private boolean stoppedEarly = false;

		@Override
		public void run() {
			setPriority(Thread.MAX_PRIORITY);
			isLoading = true;

			java.util.zip.ZipEntry entry;
			while (true) {
				try {
					if (stoppedEarly) {
						break;
					}

					entry = zipIn.getNextEntry();
					if (entry == null) {
						break;
					}

					final String pathname = getCanonicalPathname(entry.getName());
					if (entry.isDirectory()) {
						directories.addElement(pathname);
						finishedLoading(pathname);
						continue;
					}

					// not sure if this is the best way to do this.
					int size = 1024;
					int off = 0;
					byte[] entryContents = new byte[size];
					while (zipIn.available() == 1) {
						if (off == size) {
							size += 1024;
							final byte[] temp = entryContents;
							entryContents = new byte[size];
							System.arraycopy(temp, 0, entryContents, 0, size - 1024);
						}
						final int theByte = zipIn.read();
						if (theByte == -1) {
							break;
						}
						entryContents[off++] = (byte) theByte;
					}
					if (off < size) {
						final byte[] temp = entryContents;
						entryContents = new byte[off];
						System.arraycopy(temp, 0, entryContents, 0, off);
					}

					zipIn.closeEntry();

					pathnamesToByteArrays.put(pathname, entryContents);
					finishedLoading(pathname);
				} catch (final java.io.IOException e) {
					e.printStackTrace();
				}
			}

			stoppedEarly = false;
			isLoading = false;
			finishedLoading(ZipTreeLoader.WHOLE_FILE);
		}

		public void stopEarly() {
			stoppedEarly = true;
		}
	}

	@Override
	public boolean isKeepFileSupported() {
		return false;
	}

	@Override
	public Object getKeepKey(final String filename) throws KeepFileNotSupportedException {
		throw new KeepFileNotSupportedException();
	}
}
