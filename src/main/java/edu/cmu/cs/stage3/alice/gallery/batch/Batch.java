package edu.cmu.cs.stage3.alice.gallery.batch;

import java.io.File;

import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;

public abstract class Batch {
	private final edu.cmu.cs.stage3.alice.core.World m_world;

	public Batch() {
		m_world = new edu.cmu.cs.stage3.alice.core.World();
		m_world.name.set("World");

		initialize(m_world);
	}

	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return m_world;
	}

	protected void initialize(final edu.cmu.cs.stage3.alice.core.World world) {
	}

	public void forEachElement(final java.io.File root, final ElementHandler elementHandler) {
		final java.io.File[] directories = root.listFiles(new java.io.FileFilter() {
			@Override
			public boolean accept(final java.io.File file) {
				return file.isDirectory();
			}
		});
		for (final File directorie : directories) {
			forEachElement(directorie, elementHandler);
		}

		final java.io.File[] files = root.listFiles(new java.io.FilenameFilter() {
			@Override
			public boolean accept(final java.io.File dir, final String name) {
				return name.endsWith(".a2c");
			}
		});
		for (final File fileI : files) {
			try {
				final edu.cmu.cs.stage3.alice.core.Element element = edu.cmu.cs.stage3.alice.core.Element.load(fileI,
						m_world);
				elementHandler.handleElement(element, fileI);
				element.release();
			} catch (final edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
				System.err.println(fileI);
				final edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] propertyReferences = upre
						.getPropertyReferences();
				for (final PropertyReference propertyReference : propertyReferences) {
					System.err.println("\t" + propertyReference);
				}
				upre.printStackTrace();
			} catch (final Throwable t) {
				System.err.println(fileI);
				t.printStackTrace();
			}
		}
	}
}
