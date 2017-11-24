/*
 * Created on Mar 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.gallery;

import java.io.File;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;

/**
 * @author caitlin
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CaitlinFixer {
	private static edu.cmu.cs.stage3.alice.core.World m_world = null;

	private static void clear(final edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty eap) {
		while (eap.size() > 0) {
			final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) eap.get(0);
			// System.err.println( "removing from parent: " + element );
			element.HACK_removeFromParentWithoutCheckingForExternalReferences();
		}
		eap.clear();
	}

	private static void removeAllMarkersAndLights(final edu.cmu.cs.stage3.alice.core.Element element) {
		final java.util.Vector<Element> v = new java.util.Vector<Element>();
		for (int i = 0; i < element.getChildCount(); i++) {
			final edu.cmu.cs.stage3.alice.core.Element child = element.getChildAt(i);
			if (child instanceof edu.cmu.cs.stage3.alice.core.Light) {
				v.addElement(child);
			} else {
				final String allLowercaseName = child.name.getStringValue().toLowerCase();
				if (allLowercaseName.indexOf("marker") != -1 || allLowercaseName.equals("chase")
						|| allLowercaseName.equals("whole") || allLowercaseName.equals("closeup")
						|| allLowercaseName.equals("close up") || allLowercaseName.equals("far")
						|| allLowercaseName.equals("revolve")) {
					v.addElement(child);
				}
			}
		}
		for (int i = 0; i < v.size(); i++) {
			final edu.cmu.cs.stage3.alice.core.Element elementsToRemoveI = v
					.elementAt(i);
			System.err.println("removing: " + elementsToRemoveI);
			elementsToRemoveI.removeFromParent();
		}
	}

	private static void removeMethodsFromObject(final java.io.File srcRoot, final java.io.File dstRoot,
			final java.io.File srcFile) {
		try {
			final java.io.File dstFile = new java.io.File(dstRoot.getAbsolutePath()
					+ srcFile.getAbsolutePath().substring(srcRoot.getAbsolutePath().length()));
			System.err.println(dstFile);
			dstFile.getParentFile().mkdirs();
			if (dstFile.exists()) {
				// pass
			} else {
				edu.cmu.cs.stage3.alice.core.Element e = edu.cmu.cs.stage3.alice.core.Element.load(srcFile, m_world,
						null);
				e.setParent(m_world);
				if (e instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
					final edu.cmu.cs.stage3.alice.core.Sandbox sandbox = (edu.cmu.cs.stage3.alice.core.Sandbox) e;
					clear(sandbox.variables);
					clear(sandbox.responses);
					clear(sandbox.questions);
					clear(sandbox.behaviors);
					clear(sandbox.sounds);
				}
				final edu.cmu.cs.stage3.alice.core.Model[] models = (edu.cmu.cs.stage3.alice.core.Model[]) e
						.getDescendants(edu.cmu.cs.stage3.alice.core.Model.class);
				for (final Model model : models) {
					clear(model.poses);
				}

				removeAllMarkersAndLights(e);

				final java.util.Dictionary map = new java.util.Hashtable();
				final String srcPath = srcFile.getAbsolutePath();
				final java.io.File thumbnailFile = new java.io.File(srcPath.substring(0, srcPath.length() - 3) + "png");
				if (thumbnailFile.exists()) {
					final java.io.BufferedInputStream bis = new java.io.BufferedInputStream(
							new java.io.FileInputStream(thumbnailFile));
					final int n = bis.available();
					final byte[] data = new byte[n];
					if (bis.read(data, 0, n) == n) {
						map.put("thumbnail.png", data);
					} else {
						System.err.println("did not read entire thumbnail: " + thumbnailFile);
					}
				}
				e.store(dstFile, null, map);
				e.store(dstFile);
				e.removeFromParent();
				e.release();
				e = null;
			}
		} catch (final Throwable t) {
			System.err.println(srcFile);
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private static void removeMethodsFromObjectsInDirectory(final java.io.File srcRoot, final java.io.File dstRoot,
			final java.io.File srcDir) {
		System.err.println("removeMethodsFromObjectsInDirectory: " + srcDir);
		final java.io.File[] directories = srcDir.listFiles(new java.io.FileFilter() {
			@Override
			public boolean accept(final java.io.File file) {
				return file.isDirectory();
			}
		});
		for (final File directorie : directories) {
			removeMethodsFromObjectsInDirectory(srcRoot, dstRoot, directorie);
		}

		final java.io.File[] files = srcDir.listFiles(new java.io.FilenameFilter() {
			@Override
			public boolean accept(final java.io.File dir, final String name) {
				return name.endsWith(".a2c");
			}
		});
		for (final File file : files) {
			removeMethodsFromObject(srcRoot, dstRoot, file);
		}
	}
	/*
	 * public static void main( String[] args ) { try { m_world =
	 * (edu.cmu.cs.stage3
	 * .alice.core.World)edu.cmu.cs.stage3.alice.core.Element.load( new
	 * java.io.File( "c:/program files/alice/required/etc/default.a2w" ), null,
	 * null ); } catch( Throwable t ) { t.printStackTrace(); System.exit( -1 );
	 * } java.io.File srcRoot = new java.io.File( args[ 0 ] ); java.io.File
	 * dstRoot = new java.io.File( args[ 1 ] ); System.err.println( "begin" );
	 * removeMethodsFromObjectsInDirectory( srcRoot, dstRoot, srcRoot );
	 * System.err.println( "end" ); System.exit( 0 ); }
	 */

}
