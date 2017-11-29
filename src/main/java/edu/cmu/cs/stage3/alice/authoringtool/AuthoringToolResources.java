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

package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.util.StringObjectPair;
import edu.cmu.cs.stage3.util.StringTypePair;

/**
 * @author Jason Pratt
 */
public class AuthoringToolResources {
	public final static long startTime = System.currentTimeMillis();
	public final static String QUESTION_STRING = "function";
	public static edu.cmu.cs.stage3.util.Criterion characterCriterion = new edu.cmu.cs.stage3.util.Criterion() {
		@Override
		public boolean accept(final Object o) {
			return o instanceof edu.cmu.cs.stage3.alice.core.Sandbox;
		}
	};
	public static java.io.FileFilter resourceFileFilter = new java.io.FileFilter() {
		@SuppressWarnings("unused")
		public String getDescription() {
			return "resource files";
		}

		@Override
		public boolean accept(final java.io.File file) {
			return file.isFile() && file.canRead() && file.getName().toLowerCase().endsWith(".py");
		}
	};

	// preferences
	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration
			.getLocalConfiguration(AuthoringTool.class.getPackage());

	public static class Resources implements java.io.Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = -1296784417803844599L;
		public Vector<Object> propertyStructure;
		public Vector<Object> oneShotStructure;
		public Vector<Object> questionStructure;
		public Vector<Object> worldTreeChildrenPropertiesStructure;
		public Vector<Object> behaviorParameterPropertiesStructure;
		public java.util.HashMap<Object, String> nameMap = new java.util.HashMap<Object, String>();
		public java.util.HashMap<Object, String> htmlNameMap = new java.util.HashMap<Object, String>();
		public java.util.HashMap<Object, String> formatMap = new java.util.HashMap<Object, String>();
		public java.util.HashMap<String, HashMap<Object, String>> propertyValueFormatMap = new java.util.HashMap<>();
		public java.util.HashMap<String, String> unitMap = new java.util.HashMap<String, String>();
		public Class<?>[] classesToOmitNoneFor;
		public edu.cmu.cs.stage3.util.StringTypePair[] propertiesToOmitNoneFor;
		public edu.cmu.cs.stage3.util.StringTypePair[] propertiesToIncludeNoneFor;
		public edu.cmu.cs.stage3.util.StringTypePair[] propertyNamesToOmit;
		public edu.cmu.cs.stage3.util.StringTypePair[] propertiesToOmitScriptDefinedFor;
		public Vector<Object> defaultPropertyValuesStructure;
		public edu.cmu.cs.stage3.util.StringTypePair[] defaultVariableTypes;
		public String[] defaultAspectRatios;
		public Class<?>[] behaviorClasses;
		public String[] parameterizedPropertiesToOmit;
		public String[] responsePropertiesToOmit;
		public String[] oneShotGroupsToInclude;
		public String[] questionPropertiesToOmit;
		public java.util.HashMap<String, Color> colorMap = new java.util.HashMap<String, Color>();
		public java.text.DecimalFormat decimalFormatter = new java.text.DecimalFormat("#0.##");
		public java.util.HashMap<String, Image> stringImageMap = new java.util.HashMap<String, Image>();
		public java.util.HashMap<String, ImageIcon> stringIconMap = new java.util.HashMap<String, ImageIcon>();
		public java.util.HashMap<ImageIcon, ImageIcon> disabledIconMap = new java.util.HashMap<ImageIcon, ImageIcon>();
		public Class<Importer>[] importers;
		public Class<? extends Editor>[] editors;
		public java.util.HashMap<Class<?>, DataFlavor> flavorMap = new java.util.HashMap<Class<?>, DataFlavor>();
		public java.util.HashMap<Integer, String> keyCodesToStrings = new java.util.HashMap<Integer, String>();
		public boolean experimentalFeaturesEnabled;
		public java.util.HashMap<Object, Object> miscMap = new java.util.HashMap<Object, Object>();
		public java.net.URL mainWebGalleryURL = null;
		public java.io.File mainDiskGalleryDirectory = null;
		public java.io.File mainCDGalleryDirectory = null;
	}

	protected static Resources resources;

	protected static java.io.File resourcesDirectory;
	protected static java.io.File resourcesCacheFile;
	protected static java.io.File resourcesPyFile;
	protected static java.io.FilenameFilter pyFilenameFilter = new java.io.FilenameFilter() {
		@Override
		public boolean accept(final java.io.File dir, final String name) {
			return name.toLowerCase().endsWith(".py");
		}
	};

	static {
		resourcesDirectory = new java.io.File(JAlice.getAliceHomeDirectory(), "resources").getAbsoluteFile();
		resourcesCacheFile = new java.io.File(resourcesDirectory, "resourcesCache.bin").getAbsoluteFile();
		resourcesPyFile = new java.io.File(resourcesDirectory, authoringToolConfig.getValue("resourceFile"))
				.getAbsoluteFile();
		if (!resourcesPyFile.canRead()) {
			resourcesPyFile = new java.io.File(resourcesDirectory, "Alice Style.py").getAbsoluteFile();
		}
		loadResourcesPy();
		//
		// if( isResourcesCacheCurrent() ) {
		// try {
		// loadResourcesCache();
		// } catch( Throwable t ) {
		// AuthoringTool.showErrorDialog(
		// "Unable to load resources cache. Reloading resources from " +
		// resourcesPyFile.getAbsolutePath(), t );
		// try {
		// loadResourcesPy();
		// } catch( Throwable t2 ) {
		// AuthoringTool.showErrorDialog( "Unable to load resources from " +
		// resourcesPyFile.getAbsolutePath(), t2 );
		// }
		// deleteResourcesCache();
		// }
		// } else {
		// try {
		// loadResourcesPy();
		// } catch( Throwable t ) {
		// AuthoringTool.showErrorDialog( "Unable to load resources from " +
		// resourcesPyFile.getAbsolutePath(), t );
		// }
		// saveResourcesCache();
		// }
	}

	public static boolean safeIsDataFlavorSupported(final java.awt.dnd.DropTargetDragEvent dtde,
			final java.awt.datatransfer.DataFlavor flavor) {
		try {
			final boolean toReturn = dtde.isDataFlavorSupported(flavor);
			return toReturn;
		} catch (final Throwable t) {
			return false;
		}
	}

	public static java.awt.datatransfer.DataFlavor[] safeGetCurrentDataFlavors(
			final java.awt.dnd.DropTargetDropEvent dtde) {
		try {
			return dtde.getCurrentDataFlavors();
		} catch (final Throwable t) {
			return null;
		}
	}

	public static java.awt.datatransfer.DataFlavor[] safeGetCurrentDataFlavors(
			final java.awt.dnd.DropTargetDragEvent dtde) {
		try {
			return dtde.getCurrentDataFlavors();
		} catch (final Throwable t) {
			return null;
		}
	}

	public static boolean safeIsDataFlavorSupported(final java.awt.dnd.DropTargetDropEvent dtde,
			final java.awt.datatransfer.DataFlavor flavor) {
		try {
			final boolean toReturn = dtde.isDataFlavorSupported(flavor);
			return toReturn;
		} catch (final Throwable t) {
			return false;
		}
	}

	public static boolean safeIsDataFlavorSupported(final java.awt.datatransfer.Transferable transferable,
			final java.awt.datatransfer.DataFlavor flavor) {
		try {
			final boolean toReturn = transferable.isDataFlavorSupported(flavor);
			return toReturn;
		} catch (final Throwable t) {
			return false;
		}
	}

	public static boolean isResourcesCacheCurrent() {
		final long cacheTime = resourcesCacheFile.exists() ? resourcesCacheFile.lastModified() : 0L;
		final long mostCurrentPy = getMostCurrentPyTime(resourcesDirectory, 0L);

		return cacheTime > mostCurrentPy;
	}

	private static long getMostCurrentPyTime(final java.io.File directory, long mostCurrentPy) {
		final java.io.File[] files = directory.listFiles();
		for (final File file : files) {
			if (pyFilenameFilter.accept(directory, file.getName())) {
				mostCurrentPy = Math.max(mostCurrentPy, file.lastModified());
			} else if (file.isDirectory()) {
				mostCurrentPy = Math.max(mostCurrentPy, getMostCurrentPyTime(file, mostCurrentPy));
			}
		}

		return mostCurrentPy;
	}

	public static void loadResourcesPy() {
		resources = new Resources();
		org.python.core.PySystemState.initialize();
		final org.python.core.PySystemState pySystemState = org.python.core.Py.getSystemState();
		org.python.core.__builtin__.execfile(resourcesPyFile.getAbsolutePath(), pySystemState.builtins,
				pySystemState.builtins);
		AuthoringToolResources.initKeyCodesToStrings();
		initWebGalleryURL();
	}

	public static void loadResourcesCache() throws Exception {
		final java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
				new java.io.BufferedInputStream(new java.io.FileInputStream(resourcesCacheFile)));
		resources = (Resources) ois.readObject();
		ois.close();
	}

	public static void saveResourcesCache() {
		try {
			final java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
					new java.io.BufferedOutputStream(new java.io.FileOutputStream(resourcesCacheFile)));
			oos.writeObject(resources);
			oos.flush();
			oos.close();
		} catch (final Throwable t) {
			AuthoringTool.showErrorDialog("Unable to save resources cache to " + resourcesCacheFile.getAbsolutePath(),
					t);
		}
	}

	public static void deleteResourcesCache() {
		try {
			resourcesCacheFile.delete();
		} catch (final Throwable t) {
			AuthoringTool.showErrorDialog("Unable to delete resources cache " + resourcesCacheFile.getAbsolutePath(),
					t);
		}
	}

	public static void setPropertyStructure(final Vector<Object> propertyStructure) {
		if (propertyStructure != null) {
			for (final Iterator<Object> iter = propertyStructure.iterator(); iter.hasNext();) {
				final Object o = iter.next();
				if (o instanceof StringObjectPair) {
					final String className = ((StringObjectPair) o).getString();
					try {
						Class.forName(className);
					} catch (final java.lang.ClassNotFoundException e) {
						throw new IllegalArgumentException("propertyStructure error: " + className + " is not a Class");
					}
				} else {
					throw new IllegalArgumentException("Unexpected object found in propertyStructure: " + o);
				}
			}
		}

		AuthoringToolResources.resources.propertyStructure = propertyStructure;
	}

	@SuppressWarnings("unchecked")
	public static Vector<StringObjectPair> getPropertyStructure(final Class<?> elementClass) {
		if (AuthoringToolResources.resources.propertyStructure != null) {
			for (final Iterator<Object> iter = AuthoringToolResources.resources.propertyStructure.iterator(); iter
					.hasNext();) {
				final Object o = iter.next();
				if (o instanceof StringObjectPair) {
					final String className = ((StringObjectPair) o).getString();
					try {
						final Class<?> c = Class.forName(className);
						if (c.isAssignableFrom(elementClass)) {
							return (Vector<StringObjectPair>) ((StringObjectPair) o).getObject();
						}
					} catch (final java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog("Unexpected object found in propertyStructure: " + o, null);
				}
			}
		}
		return null;
	}

	public static Vector<StringObjectPair> getPropertyStructure(
			final edu.cmu.cs.stage3.alice.core.Element element, final boolean includeLeftovers) {
		final Vector<StringObjectPair> structure = getPropertyStructure(element.getClass());

		if (includeLeftovers && structure != null) {
			final Vector<Property> usedProperties = new Vector<Property>();
			for (final StringObjectPair sop : structure) {
				@SuppressWarnings("unchecked")
				final Vector<String> propertyNames = (Vector<String>) sop.getObject();
				if (propertyNames != null) {
					for (final java.util.Iterator<String> jter = propertyNames.iterator(); jter.hasNext();) {
						final String name = jter.next();
						final edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(name);
						if (property != null) {
							usedProperties.add(property);
						}
					}
				}
			}

			final Vector<String> leftovers = new Vector<String>();
			final edu.cmu.cs.stage3.alice.core.Property[] properties = element.getProperties();
			for (int i = 0; i < properties.length; i++) {
				if (!usedProperties.contains(properties[i])) {
					leftovers.add(properties[i].getName());
				}
			}

			if (leftovers.size() > 0) {
				structure.add(new StringObjectPair("leftovers", leftovers));
			}
		}

		return structure;
	}

	public static void setOneShotStructure(final Vector<Object> oneShotStructure) {
		// validate structure
		if (oneShotStructure != null) {
			for (final Iterator<Object> iter = oneShotStructure.iterator(); iter.hasNext();) {
				final Object classChunk = iter.next();
				if (classChunk instanceof StringObjectPair) {
					final String className = ((StringObjectPair) classChunk).getString();
					final Object groups = ((StringObjectPair) classChunk).getObject();
					// try {
					// Class c = Class.forName( className );
					if (groups instanceof Vector) {
						for (@SuppressWarnings("unchecked")
						final java.util.Iterator<Object> jter = ((Vector<Object>) groups).iterator(); jter.hasNext();) {
							final Object groupChunk = jter.next();
							if (groupChunk instanceof StringObjectPair) {
								final Object responseClasses = ((StringObjectPair) groupChunk)
										.getObject();
								if (responseClasses instanceof Vector) {
									for (@SuppressWarnings("unchecked")
									final java.util.Iterator<Object> kter = ((Vector<Object>) responseClasses)
											.iterator(); kter.hasNext();) {
										final Object className2 = kter.next();
										if (className2 instanceof String
												|| className2 instanceof StringObjectPair) {
											// do nothing
										} else {
											throw new IllegalArgumentException(
													"oneShotStructure error: expected String or StringObjectPair, got: "
															+ className);
										}
									}
								}
							} else {
								throw new IllegalArgumentException(
										"Unexpected object found in oneShotStructure: " + groupChunk);
							}
						}
					} else {
						throw new IllegalArgumentException("oneShotStructure error: expected Vector, got: " + groups);
					}
					// } catch( java.lang.ClassNotFoundException e ) {
					// throw new IllegalArgumentException(
					// "oneShotStructure error: " + className +
					// " is not a Class" );
					// }
				} else {
					throw new IllegalArgumentException("Unexpected object found in oneShotStructure: " + classChunk);
				}
			}
		}

		AuthoringToolResources.resources.oneShotStructure = oneShotStructure;
	}

	@SuppressWarnings("unchecked")
	public static Vector<Object> getOneShotStructure(final Class<?> elementClass) {
		if (AuthoringToolResources.resources.oneShotStructure != null) {
			for (final Iterator<Object> iter = AuthoringToolResources.resources.oneShotStructure.iterator(); iter
					.hasNext();) {
				final Object o = iter.next();
				if (o instanceof StringObjectPair) {
					final String className = ((StringObjectPair) o).getString();
					try {
						final Class<?> c = Class.forName(className);
						if (c.isAssignableFrom(elementClass)) {
							return (Vector<Object>) ((StringObjectPair) o).getObject();
						}
					} catch (final java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog("Unexpected object found in oneShotStructure: " + o, null);
				}
			}
		}

		return null;
	}

	public static void setQuestionStructure(final Vector<Object> questionStructure) {
		// validate structure
		if (questionStructure != null) {
			for (final java.util.Iterator<Object>iter = questionStructure.iterator(); iter.hasNext();) {
				final Object classChunk = iter.next();
				if (classChunk instanceof StringObjectPair) {
					final String className = ((StringObjectPair) classChunk).getString();
					final Object groups = ((StringObjectPair) classChunk).getObject();
					// try {
					// Class c = Class.forName( className );
					if (groups instanceof Vector) {
						for (@SuppressWarnings("unchecked")
						final Iterator<?> jter = ((Vector<Object>) groups).iterator(); jter.hasNext();) {
							final Object groupChunk = jter.next();
							if (groupChunk instanceof StringObjectPair) {
								final Object questionClasses = ((StringObjectPair) groupChunk)
										.getObject();
								if (questionClasses instanceof Vector) {
									for (@SuppressWarnings("unchecked")
									final Iterator<?> kter = ((Vector<Object>) questionClasses)
											.iterator(); kter.hasNext();) {
										final Object className2 = kter.next();
										if (className2 instanceof String) {
											try {
												Class.forName((String) className2);
											} catch (final ClassNotFoundException e) {
												throw new IllegalArgumentException(
														"questionStructure error: " + className2 + " is not a Class");
											}
										} else {
											throw new IllegalArgumentException(
													"questionStructure error: expected String, got: " + className);
										}
									}
								}
							} else {
								throw new IllegalArgumentException(
										"Unexpected object found in questionStructure: " + groupChunk);
							}
						}
					} else {
						throw new IllegalArgumentException("questionStructure error: expected Vector, got: " + groups);
					}
					// } catch( java.lang.ClassNotFoundException e ) {
					// throw new IllegalArgumentException(
					// "questionStructure error: " + className +
					// " is not a Class" );
					// }
				} else {
					throw new IllegalArgumentException("Unexpected object found in questionStructure: " + classChunk);
				}
			}
		}

		AuthoringToolResources.resources.questionStructure = questionStructure;
	}

	@SuppressWarnings("unchecked")
	public static Vector<Object> getQuestionStructure(final Class<?> elementClass) {
		if (AuthoringToolResources.resources.questionStructure != null) {
			for (final java.util.Iterator<Object>iter = AuthoringToolResources.resources.questionStructure.iterator(); iter
					.hasNext();) {
				final Object o = iter.next();
				if (o instanceof StringObjectPair) {
					final String className = ((StringObjectPair) o).getString();
					try {
						final Class<?> c = Class.forName(className);
						if (c.isAssignableFrom(elementClass)) {
							return (Vector<Object>) ((StringObjectPair) o).getObject();
						}
					} catch (final java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog("Unexpected object found in questionStructure: " + o, null);
				}
			}
		}

		return null;
	}

	public static void setDefaultPropertyValuesStructure(final Vector<Object> defaultPropertyValuesStructure) {
		// validate structure
		if (defaultPropertyValuesStructure != null) {
			for (final java.util.Iterator<Object>iter = defaultPropertyValuesStructure.iterator(); iter.hasNext();) {
				final Object classChunk = iter.next();
				if (classChunk instanceof StringObjectPair) {
					// String className =
					// ((StringObjectPair)classChunk).getString();
					final Object properties = ((StringObjectPair) classChunk).getObject();
					// try {
					// Class c = Class.forName( className );
					if (properties instanceof Vector) {
						for (@SuppressWarnings("unchecked")
						final java.util.Iterator<Object>jter = ((Vector<Object>) properties).iterator(); jter
								.hasNext();) {
							final Object propertyChunk = jter.next();
							if (propertyChunk instanceof StringObjectPair) {
								final Object values = ((StringObjectPair) propertyChunk)
										.getObject();
								if (!(values instanceof Vector)) {
									throw new IllegalArgumentException(
											"defaultPropertyValuesStructure error: expected Vector, got: " + values);
									// } else {
									// for( java.util.Iterator<Object>kter =
									// ((Vector)values).iterator();
									// kter.hasNext(); ) {
									// System.out.println( kter.next() );
									// }
								}
							} else {
								throw new IllegalArgumentException(
										"defaultPropertyValuesStructure error: expected StringObjectPair, got: "
												+ propertyChunk);
							}
						}
					} else {
						throw new IllegalArgumentException(
								"defaultPropertyValuesStructure error: expected Vector, got: " + properties);
					}
					// } catch( java.lang.ClassNotFoundException e ) {
					// throw new IllegalArgumentException(
					// "defaultPropertyValuesStructure error: " + className +
					// " is not a Class" );
					// }
				} else {
					throw new IllegalArgumentException(
							"defaultPropertyValuesStructure error: expected StringObjectPair, got: " + classChunk);
				}
			}
		}

		AuthoringToolResources.resources.defaultPropertyValuesStructure = defaultPropertyValuesStructure;
	}

	@SuppressWarnings("unchecked")
	public static Vector<Object> getDefaultPropertyValues(final Class<?> elementClass, final String propertyName) {
		if (AuthoringToolResources.resources.defaultPropertyValuesStructure != null) {
			for (final java.util.Iterator<Object>iter = AuthoringToolResources.resources.defaultPropertyValuesStructure
					.iterator(); iter.hasNext();) {
				final StringObjectPair classChunk = (StringObjectPair) iter
						.next();
				final String className = classChunk.getString();
				try {
					final Class<?> c = Class.forName(className);
					if (c.isAssignableFrom(elementClass)) {
						final Vector<Object> properties = (Vector<Object>) classChunk.getObject();
						for (final java.util.Iterator<Object>jter = properties.iterator(); jter.hasNext();) {
							final StringObjectPair propertyChunk = (StringObjectPair) jter
									.next();
							if (propertyName.equals(propertyChunk.getString())) {
								return (Vector<Object>) propertyChunk.getObject();
							}
						}
					}
				} catch (final java.lang.ClassNotFoundException e) {
					AuthoringTool.showErrorDialog("Can't find class " + className, e);
				}
			}
		}

		return null;
	}

	public static void putName(final Object key, final String prettyName) {
		AuthoringToolResources.resources.nameMap.put(key, prettyName);
	}

	public static String getName(final Object key) {
		return AuthoringToolResources.resources.nameMap.get(key);
	}

	public static boolean nameMapContainsKey(final Object key) {
		return AuthoringToolResources.resources.nameMap.containsKey(key);
	}

	public static void putHTMLName(final Object key, final String prettyName) {
		AuthoringToolResources.resources.htmlNameMap.put(key, prettyName);
	}

	public static String getHTMLName(final Object key) {
		return AuthoringToolResources.resources.htmlNameMap.get(key);
	}

	public static boolean htmlNameMapContainsKey(final Object key) {
		return AuthoringToolResources.resources.htmlNameMap.containsKey(key);
	}

	public static void putFormat(final Object key, final String formatString) {
		AuthoringToolResources.resources.formatMap.put(key, formatString);
	}

	public static String getFormat(final Object key) {
		return AuthoringToolResources.resources.formatMap.get(key);
	}

	public static String getPlainFormat(final Object key) {
		final String format = AuthoringToolResources.resources.formatMap.get(key);
		final StringBuffer sb = new StringBuffer();
		final edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(
				format);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!token.startsWith("<<") || token.startsWith("<<<")) {
				while (token.indexOf("&lt;") > -1) {
					token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<")
							.toString();
				}
				sb.append(token);
			}
		}
		return sb.toString();
	}

	public static boolean formatMapContainsKey(final Object key) {
		return AuthoringToolResources.resources.formatMap.containsKey(key);
	}

	public static void putPropertyValueFormatMap(final String propertyKey, final java.util.HashMap<Object, String> valueReprMap) {
		AuthoringToolResources.resources.propertyValueFormatMap.put(propertyKey, valueReprMap);
	}

	public static java.util.HashMap<Object, String> getPropertyValueFormatMap(final String propertyKey) {
		return AuthoringToolResources.resources.propertyValueFormatMap.get(propertyKey);
	}

	public static boolean propertyValueFormatMapContainsKey(final String propertyKey) {
		return AuthoringToolResources.resources.propertyValueFormatMap.containsKey(propertyKey);
	}

	public static void putUnitString(final String key, final String unitString) {
		AuthoringToolResources.resources.unitMap.put(key, unitString);
	}

	public static String getUnitString(final String key) {
		return AuthoringToolResources.resources.unitMap.get(key);
	}

	public static boolean unitMapContainsKey(final String key) {
		return AuthoringToolResources.resources.unitMap.containsKey(key);
	}

	public static java.util.Set<String> getUnitMapKeySet() {
		return AuthoringToolResources.resources.unitMap.keySet();
	}

	public static java.util.Collection<String> getUnitMapValues() {
		return AuthoringToolResources.resources.unitMap.values();
	}

	public static void setClassesToOmitNoneFor(final Class<?>[] classesToOmitNoneFor) {
		AuthoringToolResources.resources.classesToOmitNoneFor = classesToOmitNoneFor;
	}

	// public static boolean shouldGUIOmitNone( Class<?> valueClass ) {
	// for( int i = 0; i <
	// AuthoringToolResources.resources.classesToOmitNoneFor.length; i++ ) {
	// if(
	// AuthoringToolResources.resources.classesToOmitNoneFor[i].isAssignableFrom(
	// valueClass ) ) {
	// return true;
	// }
	// }
	// return false;
	// }

	public static void setPropertiesToOmitNoneFor(
			final edu.cmu.cs.stage3.util.StringTypePair[] propertiesToOmitNoneFor) {
		AuthoringToolResources.resources.propertiesToOmitNoneFor = propertiesToOmitNoneFor;
	}

	public static void setPropertiesToIncludeNoneFor(
			final edu.cmu.cs.stage3.util.StringTypePair[] propertiesToIncludeNoneFor) {
		AuthoringToolResources.resources.propertiesToIncludeNoneFor = propertiesToIncludeNoneFor;
	}

	public static boolean shouldGUIOmitNone(final edu.cmu.cs.stage3.alice.core.Property property) {
		return !shouldGUIIncludeNone(property);
	}

	public static boolean shouldGUIIncludeNone(final edu.cmu.cs.stage3.alice.core.Property property) {
		if (AuthoringToolResources.resources.propertiesToIncludeNoneFor != null) {
			final Class<?> elementClass = property.getOwner().getClass();
			final String propertyName = property.getName();
			for (final StringTypePair element : AuthoringToolResources.resources.propertiesToIncludeNoneFor) {
				if (element.getType().isAssignableFrom(elementClass) && element.getString().equals(propertyName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean shouldGUIIncludeNone(final Class<?> elementClass, final String propertyName) {
		if (AuthoringToolResources.resources.propertiesToIncludeNoneFor != null) {
			for (final StringTypePair element : AuthoringToolResources.resources.propertiesToIncludeNoneFor) {
				if (element.getType().isAssignableFrom(elementClass) && element.getString().equals(propertyName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void setPropertyNamesToOmit(final edu.cmu.cs.stage3.util.StringTypePair[] propertyNamesToOmit) {
		AuthoringToolResources.resources.propertyNamesToOmit = propertyNamesToOmit;
	}

	public static boolean shouldGUIOmitPropertyName(final edu.cmu.cs.stage3.alice.core.Property property) {
		if (AuthoringToolResources.resources.propertyNamesToOmit != null) {
			final Class<?> elementClass = property.getOwner().getClass();
			final String propertyName = property.getName();
			for (final StringTypePair element : AuthoringToolResources.resources.propertyNamesToOmit) {
				if (element.getType().isAssignableFrom(elementClass) && element.getString().equals(propertyName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void setPropertiesToOmitScriptDefinedFor(
			final edu.cmu.cs.stage3.util.StringTypePair[] propertiesToOmitScriptDefinedFor) {
		AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor = propertiesToOmitScriptDefinedFor;
	}

	public static boolean shouldGUIOmitScriptDefined(final edu.cmu.cs.stage3.alice.core.Property property) {
		if (!authoringToolConfig.getValue("enableScripting").equalsIgnoreCase("true")) {
			return true;
		} else if (AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor != null) {
			final Class<?> elementClass = property.getOwner().getClass();
			final String propertyName = property.getName();
			for (final StringTypePair element : AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor) {
				if (element.getType().isAssignableFrom(elementClass) && element.getString().equals(propertyName)) {
					return true;
				}
			}
		}
		return false;
	}

	// get repr in the context of a property
	public static String getReprForValue(final Object value, final edu.cmu.cs.stage3.alice.core.Property property) {
		return getReprForValue(value, property, null);
	}

	public static String getReprForValue(final Object value, final Class<? extends Element> elementClass, final String propertyName) {
		return getReprForValue(value, elementClass, propertyName, null);
	}

	@SuppressWarnings("unchecked")
	public static String getReprForValue(final Object value, final edu.cmu.cs.stage3.alice.core.Property property,
			final Object extraContextInfo) {
		Class<? extends Element> elementClass = property.getOwner().getClass();
		String propertyName = property.getName();
		if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation
				&& property.getName().equals("value")) {
			final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation propertyAnimation = (edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) property
					.getOwner();
			final Object e = propertyAnimation.element.get();
			if (e instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				elementClass = (Class<? extends Element>) ((edu.cmu.cs.stage3.alice.core.Expression) e).getValueClass();
			} else {
				final Object elementValue = propertyAnimation.element.getElementValue();
				if (elementValue != null) {
					elementClass = (Class<? extends Element>) elementValue.getClass();
				} else {
					elementClass = null;
				}
			}
			propertyName = propertyAnimation.propertyName.getStringValue();
		} else if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment
				&& property.getName().equals("value")) {
			final edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment propertyAssignment = (edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment) property
					.getOwner();
			elementClass = propertyAssignment.element.getElementValue().getClass();
			propertyName = propertyAssignment.propertyName.getStringValue();
		}
		return getReprForValue(value, elementClass, propertyName, extraContextInfo);
	}

	@SuppressWarnings("unchecked")
	public static String getReprForValue(final Object value, Class<? extends Element> elementClass, final String propertyName,
			final Object extraContextInfo) {
		boolean verbose = false;
		Class<?> valueClass = null;
		try {
			valueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed(elementClass, propertyName);
		} catch (final Exception e) { // a bit hackish
			valueClass = Object.class;
		}
		if (valueClass == null) { // another hack
			valueClass = Object.class;
		}
		if (elementClass == null || propertyName == null) {
			return getReprForValue(value);
		}

		if (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class.isAssignableFrom(elementClass)
				&& propertyName.equals("userDefinedResponse")
				|| edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class
						.isAssignableFrom(elementClass) && propertyName.equals("userDefinedQuestion")) {
			verbose = true;
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.Variable && ((edu.cmu.cs.stage3.alice.core.Variable) value)
				.getParent() instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
			verbose = true;
		}

		try {
			while (edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(elementClass)) {
				String propertyKey = elementClass.getName() + "." + propertyName;

				String userRepr = null;
				if (extraContextInfo != null && extraContextInfo.equals("menuContext")) { // if
																							// the
																							// repr
																							// is
																							// going
																							// to
																							// be
																							// shown
																							// in
																							// a
																							// menu
					if (propertyValueFormatMapContainsKey(propertyKey + ".menuContext")) {
						propertyKey = propertyKey + ".menuContext";
					}
				} else if (extraContextInfo instanceof edu.cmu.cs.stage3.alice.core.property.DictionaryProperty) { // if
																													// there
																													// is
																													// extra
																													// info
																													// stored
																													// in
																													// the
																													// element's
																													// data
																													// property
					final edu.cmu.cs.stage3.alice.core.property.DictionaryProperty data = (edu.cmu.cs.stage3.alice.core.property.DictionaryProperty) extraContextInfo;
					if (data.getName().equals("data")) { // sanity check
						final Object repr = data.get("edu.cmu.cs.stage3.alice.authoringtool.userRepr." + propertyName);
						if (repr != null) {
							if (repr instanceof String) {
								if (Number.class.isAssignableFrom(valueClass) && value instanceof Double) { // if
																											// it's
																											// a
																											// number,
																											// check
																											// to
																											// make
																											// sure
																											// the
																											// string
																											// is
																											// still
																											// valid
									final Double d = AuthoringToolResources.parseDouble((String) repr);
									if (d != null && d.equals(value)) {
										userRepr = (String) repr;
									} else {
										data.remove("edu.cmu.cs.stage3.alice.authoringtool.userRepr." + propertyName);
									}
								} else {
									userRepr = (String) repr;
								}
							}
						}
					}
				}

				String reprString = null;
				if (propertyValueFormatMapContainsKey(propertyKey)) {
					final java.util.HashMap<Object, String> map = getPropertyValueFormatMap(propertyKey);
					if (map.containsKey(value)) {
						reprString = map.get(value);
					} else if (value == null) { // is this right for all cases?
						reprString = null;
					} else if (map.containsKey("default")) {
						reprString = map.get("default");
					}
				}

				if (reprString != null) {
					for (final String key : AuthoringToolResources.resources.unitMap.keySet()) {
						final String unitString = getUnitString(key);
						final String unitExpression = "<" + key + ">";
						while (reprString.indexOf(unitExpression) > -1) {
							final StringBuffer sb = new StringBuffer(reprString);
							sb.replace(reprString.indexOf(unitExpression),
									reprString.indexOf(unitExpression) + unitExpression.length(), unitString);
							reprString = sb.toString();
						}
					}

					while (reprString.indexOf("<value>") > -1) {
						final String valueString = userRepr != null ? userRepr : getReprForValue(value);
						final StringBuffer sb = new StringBuffer(reprString);
						sb.replace(reprString.indexOf("<value>"), reprString.indexOf("<value>") + "<value>".length(),
								valueString);
						reprString = sb.toString();
					}
					while (reprString.indexOf("<percentValue>") > -1 && value instanceof Double) {
						final double v = ((Double) value).doubleValue() * 100.0;
						final String valueString = AuthoringToolResources.resources.decimalFormatter.format(v) + "%";
						final StringBuffer sb = new StringBuffer(reprString);
						sb.replace(reprString.indexOf("<percentValue>"),
								reprString.indexOf("<percentValue>") + "<percentValue>".length(), valueString);
						reprString = sb.toString();
					}
					while (reprString.indexOf("<keyCodeValue>") > -1 && value instanceof Integer) {
						final String valueString = java.awt.event.KeyEvent.getKeyText(((Integer) value).intValue());
						final StringBuffer sb = new StringBuffer(reprString);
						sb.replace(reprString.indexOf("<keyCodeValue>"),
								reprString.indexOf("<keyCodeValue>") + "<keyCodeValue>".length(), valueString);
						reprString = sb.toString();
					}

					return reprString;
				}

				elementClass = (Class<? extends Element>) elementClass.getSuperclass();
			}
		} catch (final Throwable t) {
			AuthoringTool.showErrorDialog("Error finding repr for " + value, t);
		}
		return getReprForValue(value, verbose);
	}

	public static String getReprForValue(final Object value) {
		return getReprForValue(value, false);
	}

	protected static void initWebGalleryURL() {
		java.net.URL galleryURL = null;
		try {
			galleryURL = new java.net.URL("http://www.alice.org/gallery/");
			final java.io.File urlFile = new java.io.File(
					edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceHomeDirectory(), "etc/AliceWebGalleryURL.txt")
							.getAbsoluteFile();
			if (urlFile.exists()) {
				if (urlFile.canRead()) {
					final java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(urlFile));
					String urlString = null;
					while (true) {
						urlString = br.readLine();
						if (urlString == null) {
							break;
						} else if (urlString.length() > 0 && urlString.charAt(0) != '#') {
							break;
						}
					}
					br.close();

					if (urlString != null) {
						urlString = urlString.trim();
						if (urlString.length() > 0) {
							try {
								galleryURL = new java.net.URL(urlString);
							} catch (final java.net.MalformedURLException badURL) {
								if (urlString.startsWith("www")) {
									urlString = "http://" + urlString;
									try {
										galleryURL = new java.net.URL(urlString);
									} catch (final java.net.MalformedURLException badURLAgain) {
									}
								}
							}

						}
					}

				}
			}
		} catch (final Throwable t) {
		} finally {
			if (galleryURL != null) {
				setMainWebGalleryURL(galleryURL);
			}
		}
	}

	protected static String stripUnnamedsFromName(final Object value) {
		String toStrip = new String(value.toString());
		// Unused ?? String toReturn = "";
		final String toMatch = "__Unnamed";
		boolean notDone = true;
		while (notDone) {
			int nextIndex = toStrip.indexOf(toMatch);
			if (nextIndex >= 0) {
				final String toAdd = toStrip.substring(0, nextIndex);
				if (toAdd != null) {
					// Unused ?? toReturn += toAdd;
				}
				String newToStrip = toStrip.substring(nextIndex, toStrip.length());
				if (newToStrip != null) {
					toStrip = newToStrip;
				} else {
					notDone = false;
					break;
				}
				nextIndex = toStrip.indexOf(".");
				if (nextIndex >= 0) {
					newToStrip = toStrip.substring(nextIndex + 1, toStrip.length());
					if (newToStrip != null) {
						toStrip = newToStrip;
					} else {
						notDone = false;
						break;
					}
				} else {
					notDone = false;
					break;
				}
			} else {
				// Unused ?? toReturn += toStrip;
				notDone = false;
				break;
			}
		}
		return toStrip;
	}

	public static String getReprForValue(Object value, final boolean verbose) {
		if (nameMapContainsKey(value)) {
			value = getName(value);
		}
		if (formatMapContainsKey(value)) {
			value = getPlainFormat(value);
		}
		if (value instanceof Class) {
			value = ((Class<?>) value).getName();
			if (nameMapContainsKey(value)) {
				value = getName(value);
			}
		}
		if (value instanceof edu.cmu.cs.stage3.util.Enumerable) {
			value = ((edu.cmu.cs.stage3.util.Enumerable) value).getRepr();
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
			String propertyName = ((edu.cmu.cs.stage3.alice.core.question.PropertyValue) value).propertyName
					.getStringValue();
			final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) ((edu.cmu.cs.stage3.alice.core.question.PropertyValue) value).element
					.get();
			Class<?> valueClass = element.getClass();
			if (element instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				valueClass = ((edu.cmu.cs.stage3.alice.core.Expression) element).getValueClass();
			}
			try {
				final Class<?> declaringClass = valueClass.getField(propertyName).getDeclaringClass();
				if (declaringClass != null) {
					final String key = declaringClass.getName() + "." + propertyName;
					if (nameMapContainsKey(key)) {
						propertyName = getName(key);
					}
				}
			} catch (final NoSuchFieldException e) {
				AuthoringTool.showErrorDialog(
						"Error representing PropertyValue: can't find " + propertyName + " on " + valueClass, e);
			}

			value = getReprForValue(element, false) + "." + propertyName;
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.Question && formatMapContainsKey(value.getClass())) {
			String questionRepr = "";
			final edu.cmu.cs.stage3.alice.core.Question question = (edu.cmu.cs.stage3.alice.core.Question) value;
			final String format = getFormat(value.getClass());
			final edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer formatTokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(
					format);
			// int i = 0;
			while (formatTokenizer.hasMoreTokens()) {
				String token = formatTokenizer.nextToken();
				if (token.startsWith("<") && token.endsWith(">")) {
					final edu.cmu.cs.stage3.alice.core.Property property = question
							.getPropertyNamed(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
					if (property != null) {
						questionRepr += getReprForValue(property.get(), property);
					}
				} else {
					while (token.indexOf("&lt;") > -1) {
						token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<")
								.toString();
					}
					questionRepr += token;
				}
			}

			if (questionRepr.length() > 0) {
				value = questionRepr;
			}
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.Element) {
			if (verbose) {
				edu.cmu.cs.stage3.alice.core.Element ancestor = ((edu.cmu.cs.stage3.alice.core.Element) value)
						.getSandbox();
				if (ancestor != null) {
					ancestor = ancestor.getParent();
				}
				value = ((edu.cmu.cs.stage3.alice.core.Element) value).getKey(ancestor);
				value = stripUnnamedsFromName(value);
			} else {
				value = ((edu.cmu.cs.stage3.alice.core.Element) value).name.getStringValue();
			}
		}
		if (value instanceof Number) {
			final double d = ((Number) value).doubleValue();
			// if( d == -.75 ) {
			// value = "-3/4";
			// } else if( d == -.5 ) {
			// value = "-1/2";
			// } else if( d == -.25 ) {
			// value = "-1/4";
			// } else if( d == .25 ) {
			// value = "1/4";
			// } else if( d == .5 ) {
			// value = "1/2";
			// } else if( d == -.75 ) {
			// value = "3/4";
			// } else if( d == -.9 ) {
			// value = "-9/10";
			// } else if( d == -.8 ) {
			// value = "-4/5";
			// } else if( d == -.7 ) {
			// value = "-7/10";
			// } else if( d == -.6 ) {
			// value = "-3/5";
			// } else if( d == -.4 ) {
			// value = "-2/5";
			// } else if( d == -.3 ) {
			// value = "-3/10";
			// } else if( d == -.2 ) {
			// value = "-1/5";
			// } else if( d == -.1 ) {
			// value = "-1/10";
			// } else if( d == .1 ) {
			// value = "1/10";
			// } else if( d == .2 ) {
			// value = "1/5";
			// } else if( d == .3 ) {
			// value = "3/10";
			// } else if( d == .4 ) {
			// value = "2/5";
			// } else if( d == .6 ) {
			// value = "3/5";
			// } else if( d == .7 ) {
			// value = "7/10";
			// } else if( d == .8 ) {
			// value = "4/5";
			// } else if( d == .9 ) {
			// value = "9/10";
			// } else {
			value = AuthoringToolResources.resources.decimalFormatter.format(d);
			// }
		}
		// if( value instanceof edu.cmu.cs.stage3.math.Vector3 ) {
		// edu.cmu.cs.stage3.math.Vector3 vec =
		// (edu.cmu.cs.stage3.math.Vector3)value;
		// value = "Vector3( " + vec.x + ", " + vec.y + ", " + vec.z + " )";
		// }
		// if( value instanceof edu.cmu.cs.stage3.math.Matrix44 ) {
		// edu.cmu.cs.stage3.math.Matrix44 m =
		// (edu.cmu.cs.stage3.math.Matrix44)value;
		// edu.cmu.cs.stage3.math.Vector3 position = m.getPosition();
		// edu.cmu.cs.stage3.math.Quaternion quaternion =
		// m.getAxes().getQuaternion();
		// value = "position: " + decimalFormatter.format( position.x ) + ", " +
		// decimalFormatter.format( position.y ) + ", " +
		// decimalFormatter.format( position.z ) + "; " +
		// "orientation: (" + decimalFormatter.format( quaternion.x ) + ", " +
		// decimalFormatter.format( quaternion.y ) + ", " +
		// decimalFormatter.format( quaternion.z ) + ") " +
		// decimalFormatter.format( quaternion.w );
		// }
		if (value instanceof javax.vecmath.Vector3d) {
			final javax.vecmath.Vector3d vec = (javax.vecmath.Vector3d) value;
			value = "Vector3( " + AuthoringToolResources.resources.decimalFormatter.format(vec.x) + ", "
					+ AuthoringToolResources.resources.decimalFormatter.format(vec.y) + ", "
					+ AuthoringToolResources.resources.decimalFormatter.format(vec.z) + " )";
		}
		if (value instanceof javax.vecmath.Matrix4d) {
			final edu.cmu.cs.stage3.math.Matrix44 m = new edu.cmu.cs.stage3.math.Matrix44(
					(javax.vecmath.Matrix4d) value);
			final edu.cmu.cs.stage3.math.Vector3 position = m.getPosition();
			final edu.cmu.cs.stage3.math.Quaternion quaternion = m.getAxes().getQuaternion();
			value = "position: " + AuthoringToolResources.resources.decimalFormatter.format(position.x) + ", "
					+ AuthoringToolResources.resources.decimalFormatter.format(position.y) + ", "
					+ AuthoringToolResources.resources.decimalFormatter.format(position.z) + ";  " + "orientation: ("
					+ AuthoringToolResources.resources.decimalFormatter.format(quaternion.x) + ", "
					+ AuthoringToolResources.resources.decimalFormatter.format(quaternion.y) + ", "
					+ AuthoringToolResources.resources.decimalFormatter.format(quaternion.z) + ") "
					+ AuthoringToolResources.resources.decimalFormatter.format(quaternion.w);
		}
		if (value instanceof edu.cmu.cs.stage3.math.Quaternion) {
			final edu.cmu.cs.stage3.math.Quaternion quaternion = (edu.cmu.cs.stage3.math.Quaternion) value;
			value = "(" + AuthoringToolResources.resources.decimalFormatter.format(quaternion.x) + ", "
					+ AuthoringToolResources.resources.decimalFormatter.format(quaternion.y) + ", "
					+ AuthoringToolResources.resources.decimalFormatter.format(quaternion.z) + ") "
					+ AuthoringToolResources.resources.decimalFormatter.format(quaternion.w);
		}
		if (value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color) {
			final edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color) value;
			if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK)) {
				value = "black";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE)) {
				value = "blue";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN)) {
				value = "brown";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN)) {
				value = "cyan";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY)) {
				value = "dark gray";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY)) {
				value = "gray";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN)) {
				value = "green";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY)) {
				value = "light gray";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA)) {
				value = "magenta";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE)) {
				value = "orange";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.PINK)) {
				value = "pink";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE)) {
				value = "purple";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.RED)) {
				value = "red";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE)) {
				value = "white";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW)) {
				value = "yellow";
			} else {
				value = "Color(r:" + AuthoringToolResources.resources.decimalFormatter.format(color.getRed()) + ", g:"
						+ AuthoringToolResources.resources.decimalFormatter.format(color.getGreen()) + ", b:"
						+ AuthoringToolResources.resources.decimalFormatter.format(color.getBlue()) + ", a:"
						+ AuthoringToolResources.resources.decimalFormatter.format(color.getAlpha()) + ")";
			}
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.Property) {
			String simpleName = ((edu.cmu.cs.stage3.alice.core.Property) value).getName();
			if (((edu.cmu.cs.stage3.alice.core.Property) value).getDeclaredClass() != null) {
				final String key = ((edu.cmu.cs.stage3.alice.core.Property) value).getDeclaredClass().getName() + "."
						+ ((edu.cmu.cs.stage3.alice.core.Property) value).getName();
				if (nameMapContainsKey(key)) {
					simpleName = getName(key);
				} else {
					simpleName = ((edu.cmu.cs.stage3.alice.core.Property) value).getName();
				}
			}

			if (((edu.cmu.cs.stage3.alice.core.Property) value)
					.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Variable) {
				value = getReprForValue(((edu.cmu.cs.stage3.alice.core.Property) value).getOwner(), verbose);
			} else if (verbose && ((edu.cmu.cs.stage3.alice.core.Property) value).getOwner() != null) {
				value = getReprForValue(((edu.cmu.cs.stage3.alice.core.Property) value).getOwner()) + "." + simpleName;
			} else {
				value = simpleName;
			}
		}
		if (value == null) {
			value = "<None>";
		}

		return value.toString();
	}

	public static String getFormattedReprForValue(final Object value,
			final StringObjectPair[] knownPropertyValues) {
		final String format = AuthoringToolResources.resources.formatMap.get(value);
		final StringBuffer sb = new StringBuffer();
		final edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(
				format);
		while (tokenizer.hasMoreTokens()) {
			final String token = tokenizer.nextToken();
			if (token.startsWith("<<<") && token.endsWith(">>>")) {
				final String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
				for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
					if (knownPropertyValue.getString().equals(propertyName)) {
						sb.append(AuthoringToolResources.getReprForValue(knownPropertyValue.getObject(), true));
						break;
					}
				}
			} else if (token.startsWith("<<") && token.endsWith(">>")) {
				// leave blank
			} else if (token.startsWith("<") && token.endsWith(">")) {
				final String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
				boolean appendedValue = false;
				for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
					if (knownPropertyValue.getString().equals(propertyName)) {
						sb.append(AuthoringToolResources.getReprForValue(knownPropertyValue.getObject(), true));
						appendedValue = true;
						break;
					}
				}
				if (!appendedValue) {
					sb.append(token);
				}
			} else {
				sb.append(token);
			}
		}
		return sb.toString();
	}

	public static String getNameInContext(final edu.cmu.cs.stage3.alice.core.Element element,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		// DEBUG System.out.println( "element: " + element );
		// DEBUG System.out.println( "context: " + context );
		if (element instanceof edu.cmu.cs.stage3.alice.core.Variable) {
			if (element.getParent() != null) {
				final edu.cmu.cs.stage3.alice.core.Element variableRoot = element.getParent();
				// DEBUG System.out.println( "variableRoot: " + variableRoot );
				if (variableRoot instanceof edu.cmu.cs.stage3.alice.core.Response
						&& (context.isDescendantOf(variableRoot) || context == variableRoot)) {
					return element.name.getStringValue();
				}
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Sound
				&& context instanceof edu.cmu.cs.stage3.alice.core.response.SoundResponse) {
			final edu.cmu.cs.stage3.alice.core.Sound sound = (edu.cmu.cs.stage3.alice.core.Sound) element;
			double t = Double.NaN;
			final edu.cmu.cs.stage3.media.DataSource dataSourceValue = sound.dataSource.getDataSourceValue();
			if (dataSourceValue != null) {
				t = dataSourceValue.getDuration(edu.cmu.cs.stage3.media.DataSource.USE_HINT_IF_NECESSARY);
				// t = dataSourceValue.waitForDuration( 100 );
				// if( Double.isNaN( t ) ) {
				// t = dataSourceValue.getDurationHint();
				// }
			}
			return getReprForValue(element, true) + " (" + formatTime(t) + ")";
		}

		return getReprForValue(element, true);
	}

	public static void setDefaultVariableTypes(final edu.cmu.cs.stage3.util.StringTypePair[] defaultVariableTypes) {
		AuthoringToolResources.resources.defaultVariableTypes = defaultVariableTypes;
	}

	public static edu.cmu.cs.stage3.util.StringTypePair[] getDefaultVariableTypes() {
		return AuthoringToolResources.resources.defaultVariableTypes;
	}

	public static void setDefaultAspectRatios(final String[] defaultAspectRatios) {
		AuthoringToolResources.resources.defaultAspectRatios = defaultAspectRatios;
	}

	public static String[] getDefaultAspectRatios() {
		return AuthoringToolResources.resources.defaultAspectRatios;
	}

	public static String[] getInitialVisibleProperties(final Class<?> elementClass) {
		final java.util.LinkedList<String> visible = new java.util.LinkedList<String>();
		final String format = AuthoringToolResources.getFormat(elementClass);
		final edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(
				format);
		while (tokenizer.hasMoreTokens()) {
			final String token = tokenizer.nextToken();
			if (token.startsWith("<<<") && token.endsWith(">>>")) {
				visible.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			} else if (token.startsWith("<<") && token.endsWith(">>")) {
				visible.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			} else if (token.startsWith("<") && token.endsWith(">")) {
				visible.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			}
		}

		return visible.toArray(new String[0]);
	}

	public static String[] getDesiredProperties(final Class<?> elementClass) {
		final java.util.LinkedList<String> desired = new java.util.LinkedList<String>();
		final String format = AuthoringToolResources.getFormat(elementClass);
		final edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(
				format);
		while (tokenizer.hasMoreTokens()) {
			final String token = tokenizer.nextToken();
			if (token.startsWith("<<<") && token.endsWith(">>>")) {
				// skip this one
				// should be in knownPropertyValues
			} else if (token.startsWith("<<") && token.endsWith(">>")) {
				desired.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			} else if (token.startsWith("<") && token.endsWith(">")) {
				desired.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			}
		}

		return desired.toArray(new String[0]);
	}

	public static void setBehaviorClasses(final Class<?>[] behaviorClasses) {
		AuthoringToolResources.resources.behaviorClasses = behaviorClasses;
	}

	public static Class<?>[] getBehaviorClasses() {
		return AuthoringToolResources.resources.behaviorClasses;
	}

	public static void setParameterizedPropertiesToOmit(final String[] parameterizedPropertiesToOmit) {
		AuthoringToolResources.resources.parameterizedPropertiesToOmit = parameterizedPropertiesToOmit;
	}

	public static String[] getParameterizedPropertiesToOmit() {
		return AuthoringToolResources.resources.parameterizedPropertiesToOmit;
	}

	public static void setOneShotGroupsToInclude(final String[] oneShotGroupsToInclude) {
		AuthoringToolResources.resources.oneShotGroupsToInclude = oneShotGroupsToInclude;
	}

	public static String[] getOneShotGroupsToInclude() {
		return AuthoringToolResources.resources.oneShotGroupsToInclude;
	}

	public static void setBehaviorParameterPropertiesStructure(
			final Vector<Object> behaviorParameterPropertiesStructure) {
		AuthoringToolResources.resources.behaviorParameterPropertiesStructure = behaviorParameterPropertiesStructure;
	}

	public static String[] getBehaviorParameterProperties(final Class<?> behaviorClass) {
		if (AuthoringToolResources.resources.behaviorParameterPropertiesStructure != null) {
			for (final java.util.Iterator<Object>iter = AuthoringToolResources.resources.behaviorParameterPropertiesStructure
					.iterator(); iter.hasNext();) {
				final Object o = iter.next();
				if (o instanceof StringObjectPair) {
					final String className = ((StringObjectPair) o).getString();
					try {
						final Class<?> c = Class.forName(className);
						if (c.isAssignableFrom(behaviorClass)) {
							return (String[]) ((StringObjectPair) o).getObject();
						}
					} catch (final java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog(
							"Unexpected object found in behaviorParameterPropertiesStructure: " + o, null);
				}
			}
		}

		return null;
	}

	public static void putColor(final String key, final java.awt.Color color) {
		AuthoringToolResources.resources.colorMap.put(key, color);
	}

	private static float[] rgbToHSL(final java.awt.Color rgb) {
		final float[] rgbF = rgb.getRGBColorComponents(null);
		final float[] hsl = new float[3];
		final float min = Math.min(rgbF[0], Math.min(rgbF[1], rgbF[2]));
		final float max = Math.max(rgbF[0], Math.max(rgbF[1], rgbF[2]));
		final float delta = max - min;

		hsl[2] = (max + min) / 2;

		if (delta == 0) {
			hsl[0] = 0.0f;
			hsl[1] = 0.0f;
		} else {
			if (hsl[2] < 0.5) {
				hsl[1] = delta / (max + min);
				// System.out.println("B: min: "+min+", max: "+max+", delta:
				// "+delta+", H: "+hsl[0]+", S: "+hsl[1]+", L: "+hsl[2]);
			} else {
				hsl[1] = delta / (2 - max - min);
				// System.out.println("A: min: "+min+", max: "+max+", delta:
				// "+delta+", H: "+hsl[0]+", S: "+hsl[1]+", L: "+hsl[2]);
			}
			final float delR = ((max - rgbF[0]) / 6 + delta / 2) / delta;
			final float delG = ((max - rgbF[1]) / 6 + delta / 2) / delta;
			final float delB = ((max - rgbF[2]) / 6 + delta / 2) / delta;
			if (rgbF[0] == max) {
				hsl[0] = delB - delG;
			} else if (rgbF[1] == max) {
				hsl[0] = 1.0f / 3 + delR - delB;
			} else if (rgbF[2] == max) {
				hsl[0] = 2.0f / 3 + delG - delR;
			}

			if (hsl[0] < 0) {
				hsl[0] += 1;
			}
			if (hsl[0] > 1) {
				hsl[0] -= 1;
			}
		}
		// System.out.println("For RGB: "+rgb+" HSL = "+hsl[0]+", "+hsl[1]+",
		// "+hsl[2]);
		return hsl;
	}

	private static float hueToRGB(final float v1, final float v2, float vH) {
		if (vH < 0) {
			vH += 1;
		}
		if (vH > 1) {
			vH -= 1;
		}
		if (6 * vH < 1) {
			return v1 + (v2 - v1) * 6 * vH;
		}
		if (2 * vH < 1) {
			return v2;
		}
		if (3 * vH < 2) {
			return v1 + (v2 - v1) * (2.0f / 3 - vH) * 6;
		}
		return v1;
	}

	private static java.awt.Color hslToRGB(final float[] hsl) {
		// Unused ?? final java.awt.Color rgb = new java.awt.Color(0, 0, 0);
		if (hsl[1] == 0) {
			// System.out.println("For HSL: "+hsl[0]+", "+hsl[1]+", "+hsl[2]+"
			// RGB = "+hsl[2]+", "+hsl[2]+", "+hsl[2]);
			return new java.awt.Color(hsl[2], hsl[2], hsl[2]);
		} else {
			float var_2 = 0.0f;
			if (hsl[2] < 0.5) {
				var_2 = hsl[2] * (1 + hsl[1]);
			} else {
				var_2 = hsl[2] + hsl[1] - hsl[1] * hsl[2];
			}
			final float var_1 = 2 * hsl[2] - var_2;
			final float R = Math.min(1.0f, hueToRGB(var_1, var_2, hsl[0] + 1.0f / 3));
			final float G = Math.min(1.0f, hueToRGB(var_1, var_2, hsl[0]));
			final float B = Math.min(1.0f, hueToRGB(var_1, var_2, hsl[0] - 1.0f / 3));
			// System.out.println("For HSL: "+hsl[0]+", "+hsl[1]+", "+hsl[2]+"
			// RGB = "+R+", "+G+", "+B);
			return new java.awt.Color(R, G, B);
		}
	}

	// static {
	// float[] hsl = rgbToHSL(java.awt.Color.white);
	// hslToRGB(hsl);
	// hsl = rgbToHSL(java.awt.Color.black);
	// hslToRGB(hsl);
	// hsl = rgbToHSL(java.awt.Color.red);
	// hslToRGB(hsl);
	// hsl = rgbToHSL(java.awt.Color.green);
	// hslToRGB(hsl);
	// hsl = rgbToHSL(new java.awt.Color(100, 100, 100));
	// hslToRGB(hsl);
	// hsl = rgbToHSL(new java.awt.Color(.2f, .5f, .5f));
	// hslToRGB(hsl);
	//
	// }
	public static java.awt.Color getColor(final String key) {
		final java.awt.Color toReturn = AuthoringToolResources.resources.colorMap.get(key);
		if (authoringToolConfig.getValue("enableHighContrastMode").equalsIgnoreCase("true")
				&& !key.equalsIgnoreCase("mainFontColor") && !key.equalsIgnoreCase("objectTreeDisabledText")
				&& !key.equalsIgnoreCase("objectTreeSelectedText") && !key.equalsIgnoreCase("disabledHTMLText")
				&& !key.equalsIgnoreCase("disabledHTML") && !key.equalsIgnoreCase("stdErrTextColor")
				&& !key.equalsIgnoreCase("commentForeground") && !key.equalsIgnoreCase("objectTreeSelected")
				&& !key.equalsIgnoreCase("dndHighlight") && !key.equalsIgnoreCase("dndHighlight2")
				&& !key.equalsIgnoreCase("dndHighlight3") && !key.equalsIgnoreCase("guiEffectsShadow")
				&& !key.equalsIgnoreCase("guiEffectsEdge") && !key.equalsIgnoreCase("guiEffectsTroughShadow")
				&& !key.equalsIgnoreCase("guiEffectsDisabledLine")
				&& !key.equalsIgnoreCase("makeSceneEditorBigBackground")
				&& !key.equalsIgnoreCase("makeSceneEditorSmallBackground") && !key.equalsIgnoreCase("objectTreeText")) {
			final float[] hsl = rgbToHSL(toReturn);
			hsl[2] = Math.max(hsl[2], .95f);
			final java.awt.Color convertedColor = hslToRGB(hsl);
			return new java.awt.Color(convertedColor.getRed(), convertedColor.getGreen(), convertedColor.getBlue(),
					toReturn.getAlpha());
		} else {
			return toReturn;
		}
	}

	public static void setMainWebGalleryURL(final java.net.URL url) {
		AuthoringToolResources.resources.mainWebGalleryURL = url;
	}

	public static java.net.URL getMainWebGalleryURL() {
		return AuthoringToolResources.resources.mainWebGalleryURL;
	}

	public static void setMainDiskGalleryDirectory(final java.io.File file) {
		AuthoringToolResources.resources.mainDiskGalleryDirectory = file;
	}

	public static java.io.File getMainDiskGalleryDirectory() {
		return AuthoringToolResources.resources.mainDiskGalleryDirectory;
	}

	public static void setMainCDGalleryDirectory(final java.io.File file) {
		AuthoringToolResources.resources.mainCDGalleryDirectory = file;
	}

	public static java.io.File getMainCDGalleryDirectory() {
		return AuthoringToolResources.resources.mainCDGalleryDirectory;
	}

	public static void autodetectMainCDGalleryDirectory(final String galleryName) {
		final java.io.File[] cdRoots = edu.cmu.cs.stage3.alice.authoringtool.util.CDUtil.getCDRoots();

		for (final File cdRoot : cdRoots) {
			if (cdRoot.exists() && cdRoot.canRead()) {
				final java.io.File potentialDir = new java.io.File(cdRoot, galleryName);
				if (potentialDir.exists() && potentialDir.canRead()) {
					setMainCDGalleryDirectory(potentialDir);
					break;
				}
			}
		}
	}

	public static java.awt.Image getAliceSystemIconImage() {
		return getImageForString("aliceHead");
	}

	public static javax.swing.ImageIcon getAliceSystemIcon() {
		return getIconForString("aliceHead");
	}

	public static java.awt.Image getImageForString(final String s) {
		if (!AuthoringToolResources.resources.stringImageMap.containsKey(s)) {
			java.net.URL resource = AuthoringToolResources.class.getResource("images/" + s + ".gif");
			if (resource == null) {
				resource = AuthoringToolResources.class.getResource("images/" + s + ".png");
			}
			if (resource == null) {
				resource = AuthoringToolResources.class.getResource("images/" + s + ".jpg");
			}
			if (resource != null) {
				final java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(resource);
				AuthoringToolResources.resources.stringImageMap.put(s, image);
			} else {
				return null;
			}
		}

		return AuthoringToolResources.resources.stringImageMap.get(s);
	}

	public static javax.swing.ImageIcon getIconForString(final String s) {
		if (!AuthoringToolResources.resources.stringIconMap.containsKey(s)) {
			java.net.URL resource = AuthoringToolResources.class.getResource("images/" + s + ".gif");
			if (resource == null) {
				resource = AuthoringToolResources.class.getResource("images/" + s + ".png");
			}
			if (resource == null) {
				resource = AuthoringToolResources.class.getResource("images/" + s + ".jpg");
			}
			if (resource != null) {
				AuthoringToolResources.resources.stringIconMap.put(s, new javax.swing.ImageIcon(resource));
			} else {
				return null;
			}
		}

		return AuthoringToolResources.resources.stringIconMap.get(s);
	}

	static final javax.swing.ImageIcon cameraIcon = getIconForString("camera");
	static final javax.swing.ImageIcon ambientLightIcon = getIconForString("ambientLight");
	static final javax.swing.ImageIcon directionalLightIcon = getIconForString("directionalLight");
	static final javax.swing.ImageIcon pointLightIcon = getIconForString("pointLight");
	static final javax.swing.ImageIcon defaultLightIcon = getIconForString("pointLight");
	static final javax.swing.ImageIcon modelIcon = getIconForString("model");
	static final javax.swing.ImageIcon subpartIcon = getIconForString("subpart");
	static final javax.swing.ImageIcon sceneIcon = getIconForString("scene");
	static final javax.swing.ImageIcon folderIcon = getIconForString("folder");
	static final javax.swing.ImageIcon defaultIcon = getIconForString("default");

	public static javax.swing.ImageIcon getIconForValue(final Object value) {
		if (value instanceof edu.cmu.cs.stage3.alice.core.Camera) { // TODO:
																	// perspective
																	// and
																	// orthographic
			return cameraIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.light.AmbientLight) {
			return ambientLightIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.light.DirectionalLight) {
			return directionalLightIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.light.PointLight) {
			return pointLightIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Light) {
			return defaultLightIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
			if (((edu.cmu.cs.stage3.alice.core.Transformable) value)
					.getParent() instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				return subpartIcon;
			} else {
				return modelIcon;
			}
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.World) {
			return sceneIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Group) {
			return folderIcon;
		} else if (value instanceof java.awt.Image) {
			return new javax.swing.ImageIcon((java.awt.Image) value);
		} else if (value instanceof String) {
			return getIconForString((String) value);
		} else if (value instanceof Integer) {
			final String s = AuthoringToolResources.resources.keyCodesToStrings.get(value);
			if (s != null) {
				return getIconForString("keyboardKeys/" + s);
			} else {
				return null;
			}
		} else {
			return defaultIcon;
		}
	}

	public static javax.swing.ImageIcon getDisabledIcon(final javax.swing.ImageIcon inputIcon) {
		return getDisabledIcon(inputIcon, 70);
	}

	public static javax.swing.ImageIcon getDisabledIcon(final javax.swing.ImageIcon inputIcon, final int percentGray) {
		javax.swing.ImageIcon disabledIcon = AuthoringToolResources.resources.disabledIconMap.get(inputIcon);

		if (disabledIcon == null) {
			final javax.swing.GrayFilter filter = new javax.swing.GrayFilter(true, percentGray);
			final java.awt.image.ImageProducer producer = new java.awt.image.FilteredImageSource(
					inputIcon.getImage().getSource(), filter);
			final java.awt.Image grayImage = java.awt.Toolkit.getDefaultToolkit().createImage(producer);
			disabledIcon = new javax.swing.ImageIcon(grayImage);
			AuthoringToolResources.resources.disabledIconMap.put(inputIcon, disabledIcon);
		}

		return disabledIcon;
	}

	public static void openURL(final String urlString) throws java.io.IOException {
		if (System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) {
			final String[] cmdarray = new String[3];
			cmdarray[0] = "rundll32";
			cmdarray[1] = "url.dll,FileProtocolHandler";
			cmdarray[2] = urlString;

			if (urlString.indexOf("&stacktrace") > -1) {
				try {
					java.io.File tempURL = java.io.File.createTempFile("tempURLHolder", ".url");
					tempURL = tempURL.getAbsoluteFile();
					tempURL.deleteOnExit();
					final java.io.PrintWriter urlWriter = new java.io.PrintWriter(
							new java.io.BufferedWriter(new java.io.FileWriter(tempURL)));
					urlWriter.println("[InternetShortcut]");
					urlWriter.println("URL=" + urlString);
					urlWriter.flush();
					urlWriter.close();
					cmdarray[2] = tempURL.getAbsolutePath();
				} catch (final Throwable t) {
					cmdarray[2] = urlString.substring(0, urlString.indexOf("&stacktrace"));
				}
			}

			Runtime.getRuntime().exec(cmdarray);

			// final Process p = Runtime.getRuntime().exec( cmdarray );
			// try {
			// p.waitFor();
			// } catch( InterruptedException e ) {
			// e.printStackTrace();
			// }
			// System.out.println( urlString.length() );
			// System.out.println( urlString );
			// System.out.println( p.exitValue() );
			// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker =
			// new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
			// public Object construct() {
			// java.io.BufferedInputStream bif = new
			// java.io.BufferedInputStream( p.getErrorStream() );
			// java.io.OutputStream os =
			// AuthoringTool.getHack().getStdErrOutputComponent().getStdErrStream();
			// while( true ) {
			// try {
			// if( bif.available() > 0 ) {
			// os.write( bif.read() );
			// }
			// Thread.sleep( 1 );
			// } catch( Exception e ) {
			// e.printStackTrace();
			// break;
			// }
			// }
			// return null;
			// }
			// };
			// worker.start();

			// Runtime.getRuntime().exec(
			// "rundll32 url.dll,FileProtocolHandler " + urlString );
		} else {
			// try netscape
			try {
				final String[] cmd = new String[] { "netscape", urlString };
				Runtime.getRuntime().exec(cmd);
			} catch (final Throwable t) {
				final String lcOSName = System.getProperty("os.name").toLowerCase();
				if (lcOSName.startsWith("mac os x")) {
					Runtime.getRuntime().exec("open " + urlString);
				}
			}
		}
	}

	// public static String cleanURLString( String urlString ) {
	// java.util.HashMap replacementMap = new java.util.HashMap();
	// replacementMap.put( "/", "%2F" );
	// replacementMap.put( " ", "%20" );
	// replacementMap.put( "~", "%7E" );
	// replacementMap.put( "&", "%26" );
	// replacementMap.put( "?", "%3F" );
	// replacementMap.put( "=", "%3D" );
	// replacementMap.put( ";", "%3B" );
	// replacementMap.put( ">", "%3E" );
	// replacementMap.put( "<", "%3C" );
	//
	// StringBuffer sb = new StringBuffer( urlString );
	// for( java.util.Iterator<Object>iter = replacementMap.keySet().iterator();
	// iter.hasNext(); ) {
	// String key = (String)iter.next();
	// String value = (String)replacementMap.get( key );
	// while( true ) {
	// int start = sb.toString().indexOf( key );
	// int end = start + key.length();
	// if( start > -1 ) {
	// sb.replace( start, end, value );
	// } else {
	// break;
	// }
	// }
	// }
	//
	// return sb.toString();
	// }

	public static boolean equals(final Object o1, final Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}

	public static Double parseDouble(final String doubleString) {
		Double number = null;
		if (doubleString.trim().equalsIgnoreCase("infinity")) {
			number = new Double(Double.POSITIVE_INFINITY);
		} else if (doubleString.trim().equalsIgnoreCase("-infinity")) {
			number = new Double(Double.NEGATIVE_INFINITY);
		} else if (doubleString.indexOf('/') > -1) {
			if (doubleString.lastIndexOf('/') == doubleString.indexOf('/')) {
				final String numeratorString = doubleString.substring(0, doubleString.indexOf('/'));
				final String denominatorString = doubleString.substring(doubleString.indexOf('/') + 1);
				try {
					number = new Double(Double.parseDouble(numeratorString) / Double.parseDouble(denominatorString));
				} catch (final NumberFormatException e) {
				}
			}
		} else {
			try {
				number = Double.valueOf(doubleString);
			} catch (final NumberFormatException e) {
			}
		}

		return number;
	}

	/**
	 * gets the the world's dummy object group, and creates it if necessary
	 */
	public static edu.cmu.cs.stage3.alice.core.Group getDummyObjectGroup(
			final edu.cmu.cs.stage3.alice.core.World world) {
		final edu.cmu.cs.stage3.alice.core.Element[] groups = world
				.getChildren(edu.cmu.cs.stage3.alice.core.Group.class);
		for (final Element group : groups) {
			if (group.data.get("dummyObjectGroup") != null && group.data.get("dummyObjectGroup").equals("true")
					&& world.groups.contains(group)) {
				return (edu.cmu.cs.stage3.alice.core.Group) group;
			}
		}

		final edu.cmu.cs.stage3.alice.core.Group dummyObjectGroup = new edu.cmu.cs.stage3.alice.core.Group();
		dummyObjectGroup.name.set("Dummy Objects");
		dummyObjectGroup.data.put("dummyObjectGroup", "true");
		dummyObjectGroup.valueClass.set(edu.cmu.cs.stage3.alice.core.Dummy.class);
		world.addChild(dummyObjectGroup);
		world.groups.add(dummyObjectGroup);
		return dummyObjectGroup;
	}

	public static boolean hasDummyObjectGroup(final edu.cmu.cs.stage3.alice.core.World world) {
		if (world != null) {
			final edu.cmu.cs.stage3.alice.core.Element[] groups = world
					.getChildren(edu.cmu.cs.stage3.alice.core.Group.class);
			for (final Element group : groups) {
				if (group.data.get("dummyObjectGroup") != null && group.data.get("dummyObjectGroup").equals("true")
						&& world.groups.contains(group)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isMethodHookedUp(final edu.cmu.cs.stage3.alice.core.Response response,
			final edu.cmu.cs.stage3.alice.core.World world) {
		return isMethodHookedUp(response, world, new Vector<Element>());
	}

	private static boolean isMethodHookedUp(final edu.cmu.cs.stage3.alice.core.Response response,
			final edu.cmu.cs.stage3.alice.core.World world, final Vector<Element> checkedMethods) {
		final edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = response.getRoot()
				.getPropertyReferencesTo(response, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false,
						true);
		for (final PropertyReference reference : references) {
			final edu.cmu.cs.stage3.alice.core.Element referrer = reference.getProperty().getOwner();
			if (world.behaviors.contains(referrer)) {
				return true;
			} else if (referrer instanceof edu.cmu.cs.stage3.alice.core.Response
					&& !checkedMethods.contains(referrer)) {
				checkedMethods.add(referrer);
				if (isMethodHookedUp((edu.cmu.cs.stage3.alice.core.Response) referrer, world, checkedMethods)) {
					return true;
				}
			}
		}

		return false;
	}

	public static edu.cmu.cs.stage3.alice.core.Response createUndoResponse(
			final edu.cmu.cs.stage3.alice.core.Response response) {
		edu.cmu.cs.stage3.alice.core.Response undoResponse = null;

		final Class<? extends Response> responseClass = response.getClass();
		if (response instanceof edu.cmu.cs.stage3.alice.core.response.ResizeAnimation) {
			final edu.cmu.cs.stage3.alice.core.response.ResizeAnimation resizeResponse = (edu.cmu.cs.stage3.alice.core.response.ResizeAnimation) response;
			final edu.cmu.cs.stage3.alice.core.response.ResizeAnimation undoResizeResponse = new edu.cmu.cs.stage3.alice.core.response.ResizeAnimation();

			undoResizeResponse.amount.set(new Double(1.0 / resizeResponse.amount.doubleValue()));
			undoResizeResponse.asSeenBy.set(resizeResponse.asSeenBy.get());
			undoResizeResponse.dimension.set(resizeResponse.dimension.get());
			undoResizeResponse.likeRubber.set(resizeResponse.likeRubber.get());
			undoResizeResponse.subject.set(resizeResponse.subject.get());

			undoResponse = undoResizeResponse;
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) {
			try {
				undoResponse = responseClass.newInstance();
				final edu.cmu.cs.stage3.alice.core.Direction direction = (edu.cmu.cs.stage3.alice.core.Direction) ((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).direction
						.getValue();
				final edu.cmu.cs.stage3.alice.core.Direction opposite = new edu.cmu.cs.stage3.alice.core.Direction(
						direction.getMoveAxis() == null ? null
								: edu.cmu.cs.stage3.math.Vector3.negate(direction.getMoveAxis()),
						direction.getTurnAxis() == null ? null
								: edu.cmu.cs.stage3.math.Vector3.negate(direction.getTurnAxis()),
						direction.getRollAxis() == null ? null
								: edu.cmu.cs.stage3.math.Vector3.negate(direction.getRollAxis()));
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).subject
						.set(((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).subject
								.get());
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).amount
						.set(((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).amount
								.get());
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).direction
						.set(opposite);
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).asSeenBy
						.set(((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).asSeenBy
								.get());
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).style
						.set(((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).style
								.get());
			} catch (final IllegalAccessException e) {
				AuthoringTool.showErrorDialog("Error creating new response: " + responseClass, e);
			} catch (final InstantiationException e) {
				AuthoringTool.showErrorDialog("Error creating new response: " + responseClass, e);
			}
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.TransformAnimation) {
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			final edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) ((edu.cmu.cs.stage3.alice.core.response.TransformAnimation) response).subject
					.getValue();
			final edu.cmu.cs.stage3.math.Matrix44 localTransformation = transformable.getLocalTransformation();
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).element.set(transformable);
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).propertyName
					.set(transformable.localTransformation.getName());
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).value.set(localTransformation);
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).howMuch
					.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) {
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			final edu.cmu.cs.stage3.alice.core.Element element = ((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).element
					.getElementValue();
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).element.set(element);
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).propertyName
					.set(((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).propertyName.get());
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).value.set(element.getPropertyNamed(
					((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).propertyName.getStringValue())
					.getValue());
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).howMuch
					.set(((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).howMuch.get());
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.SayAnimation
				|| response instanceof edu.cmu.cs.stage3.alice.core.response.ThinkAnimation
				|| response instanceof edu.cmu.cs.stage3.alice.core.response.Wait
				|| response instanceof edu.cmu.cs.stage3.alice.core.response.SoundResponse) {
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.Wait();
			undoResponse.duration.set(new Double(0.0));
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.PoseAnimation) {
			final edu.cmu.cs.stage3.alice.core.response.PoseAnimation poseAnim = (edu.cmu.cs.stage3.alice.core.response.PoseAnimation) response;
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.PoseAnimation();
			final edu.cmu.cs.stage3.alice.core.Transformable subject = (edu.cmu.cs.stage3.alice.core.Transformable) poseAnim.subject
					.get();
			final edu.cmu.cs.stage3.alice.core.Pose currentPose = edu.cmu.cs.stage3.alice.core.Pose
					.manufacturePose(subject, subject);
			((edu.cmu.cs.stage3.alice.core.response.PoseAnimation) undoResponse).subject.set(subject);
			((edu.cmu.cs.stage3.alice.core.response.PoseAnimation) undoResponse).pose.set(currentPose);
			// TODO: handle CompositeAnimations... and everything else...
		}

		if (undoResponse != null) {
			undoResponse.duration.set(response.duration.get());
		} else {
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.Wait();
			undoResponse.duration.set(new Double(0.0));
			AuthoringTool.showErrorDialog("Could not create undoResponse for " + response, null);
		}

		return undoResponse;
	}

	public static void addAffectedProperties(final java.util.List<Property> affectedProperties,
			final edu.cmu.cs.stage3.alice.core.Element element, final String propertyName,
			final edu.cmu.cs.stage3.util.HowMuch howMuch) {
		final edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(propertyName);
		if (property != null) {
			affectedProperties.add(property);
		}
		if (howMuch.getDescend()) {
			for (int i = 0; i < element.getChildCount(); i++) {
				final edu.cmu.cs.stage3.alice.core.Element child = element.getChildAt(i);
				if (child.isFirstClass.booleanValue() && howMuch.getRespectDescendant()) {
					// respect descendant
				} else {
					addAffectedProperties(affectedProperties, child, propertyName, howMuch);
				}
			}
		}
	}

	/**
	 * this method only handles some cases. you cannot depend on it to return
	 * the correct Property array for all responses.
	 */
	public static edu.cmu.cs.stage3.alice.core.Property[] getAffectedProperties(
			final edu.cmu.cs.stage3.alice.core.Response response) {
		edu.cmu.cs.stage3.alice.core.Property[] properties = null;

		if (response instanceof edu.cmu.cs.stage3.alice.core.response.ResizeAnimation) {
			final edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) ((edu.cmu.cs.stage3.alice.core.response.TransformAnimation) response).subject
					.getElementValue();
			final Vector<ObjectProperty> pVector = new Vector<ObjectProperty>();
			pVector.add(transformable.localTransformation);
			if (transformable instanceof edu.cmu.cs.stage3.alice.core.Model) {
				pVector.add(((edu.cmu.cs.stage3.alice.core.Model) transformable).visualScale);
			}
			final edu.cmu.cs.stage3.alice.core.Transformable[] descendants = (edu.cmu.cs.stage3.alice.core.Transformable[]) transformable
					.getDescendants(edu.cmu.cs.stage3.alice.core.Transformable.class);
			for (final Transformable descendant : descendants) {
				pVector.add(descendant.localTransformation);
				if (descendant instanceof edu.cmu.cs.stage3.alice.core.Model) {
					pVector.add(((edu.cmu.cs.stage3.alice.core.Model) descendant).visualScale);
				}
			}
			properties = pVector.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]);
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.TransformAnimation) {
			final edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) ((edu.cmu.cs.stage3.alice.core.response.TransformAnimation) response).subject
					.getElementValue();
			properties = new edu.cmu.cs.stage3.alice.core.Property[] { transformable.localTransformation };
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.TransformResponse) {
			final edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) ((edu.cmu.cs.stage3.alice.core.response.TransformResponse) response).subject
					.getElementValue();
			properties = new edu.cmu.cs.stage3.alice.core.Property[] { transformable.localTransformation };
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) {
			final edu.cmu.cs.stage3.alice.core.Element element = ((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).element
					.getElementValue();
			final String propertyName = ((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).propertyName
					.getStringValue();
			final edu.cmu.cs.stage3.util.HowMuch howMuch = (edu.cmu.cs.stage3.util.HowMuch) ((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).howMuch
					.getValue();

			final java.util.LinkedList<Property> propertyList = new java.util.LinkedList<Property>();
			addAffectedProperties(propertyList, element, propertyName, howMuch);
			properties = propertyList.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]);
		} // TODO: handle everything else

		if (properties == null) {
			properties = new edu.cmu.cs.stage3.alice.core.Property[0];
		}

		return properties;
	}

	public static edu.cmu.cs.stage3.alice.core.Billboard makeBillboard(
			final edu.cmu.cs.stage3.alice.core.TextureMap textureMap, final boolean makeTextureChild) {
		final java.awt.image.ImageObserver sizeObserver = new java.awt.image.ImageObserver() {
			@Override
			public boolean imageUpdate(final java.awt.Image img, final int infoflags, final int x, final int y,
					final int width, final int height) {
				return (infoflags & java.awt.image.ImageObserver.WIDTH & java.awt.image.ImageObserver.HEIGHT) > 0;
			}
		};

		if (textureMap != null) {
			final int imageWidth = textureMap.image.getImageValue().getWidth(sizeObserver);
			final int imageHeight = textureMap.image.getImageValue().getHeight(sizeObserver);
			final double aspectRatio = (double) imageWidth / (double) imageHeight;
			double width, height;
			if (aspectRatio < 1.0) {
				width = 1.0;
				height = 1.0 / aspectRatio;
			} else {
				width = aspectRatio;
				height = 1.0;
			}

			final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] {
					edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0, 0.0, 0.0, 0.0, 0.0, 1.0,
							0.0f, 0.0f),
					edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0, height, 0.0, 0.0, 0.0, 1.0,
							0.0f, 1.0f),
					edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0, height, 0.0, 0.0, 0.0, 1.0,
							1.0f, 1.0f),
					edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0, 0.0, 0.0, 0.0, 0.0, 1.0,
							1.0f, 0.0f),
					edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0, 0.0, 0.0, 0.0, 0.0, -1.0,
							1.0f, 0.0f),
					edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0, height, 0.0, 0.0, 0.0,
							-1.0, 1.0f, 1.0f),
					edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0, height, 0.0, 0.0, 0.0, -1.0,
							0.0f, 1.0f),
					edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0, 0.0, 0.0, 0.0, 0.0, -1.0,
							0.0f, 0.0f), };
			final int[] indices = new int[] { 0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7 };

			final edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray geom = new edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray();
			geom.vertices.set(vertices);
			geom.indices.set(indices);

			final edu.cmu.cs.stage3.alice.core.Billboard billboard = new edu.cmu.cs.stage3.alice.core.Billboard();
			billboard.isFirstClass.set(true);
			billboard.geometries.add(geom);
			billboard.geometry.set(geom);
			billboard.addChild(geom);

			if (makeTextureChild) {
				if (textureMap.getParent() != null) {
					textureMap.removeFromParent();
				}
				billboard.addChild(textureMap);
				billboard.textureMaps.add(textureMap);
				billboard.diffuseColorMap.set(textureMap);
				billboard.name.set(textureMap.name.getStringValue());
				textureMap.name.set(textureMap.name.getStringValue() + "_Texture");
			} else {
				billboard.name.set(textureMap.name.getStringValue() + "_Billboard");
				billboard.diffuseColorMap.set(textureMap);
			}

			return billboard;
		}

		return null;
	}

	public static void centerComponentOnScreen(final java.awt.Component c) {
		final java.awt.Dimension size = c.getSize();
		final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		final int x = screenSize.width / 2 - size.width / 2;
		final int y = screenSize.height / 2 - size.height / 2;

		c.setLocation(x, y);
	}

	public static void ensureComponentIsOnScreen(final java.awt.Component c) {
		final java.awt.Point location = c.getLocation();
		final java.awt.Dimension size = c.getSize();
		final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height -= 28; // hack for standard Windows Task Bar

		if (!(c instanceof java.awt.Window)) {
			javax.swing.SwingUtilities.convertPointToScreen(location, c.getParent());
		}

		if (location.x < 0) {
			location.x = 0;
		} else if (location.x + size.width > screenSize.width) {
			location.x -= location.x + size.width - screenSize.width;
		}
		if (location.y < 0) {
			location.y = 0;
		} else if (location.y + size.height > screenSize.height) {
			location.y -= location.y + size.height - screenSize.height;
		}

		if (!(c instanceof java.awt.Window)) {
			javax.swing.SwingUtilities.convertPointFromScreen(location, c.getParent());
		}

		c.setLocation(location);
	}

	public static String getNameForNewChild(String baseName, final edu.cmu.cs.stage3.alice.core.Element parent) {
		String name = baseName;

		if (name != null) {
			name = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
		}

		if (name == null || parent == null) {
			return name;
		}

		if (parent.getChildNamedIgnoreCase(name) == null && parent.getChildNamedIgnoreCase(name + 1) == null) {
			return name;
		}

		if (baseName.length() < 1) {
			baseName = "copy";
		}

		// take baseName, strip a number off the end if necessary, and use next
		// available number after the stripped number
		int begin = baseName.length() - 1;
		final int end = baseName.length();
		int endDigit = 2;
		while (begin >= 0) {
			try {
				endDigit = Integer.parseInt(baseName.substring(begin, end));
				name = baseName.substring(0, begin);
				begin--;
			} catch (final NumberFormatException e) {
				break;
			}
		}
		baseName = name;
		for (int i = endDigit; i < Integer.MAX_VALUE; i++) {
			name = baseName + i;
			if (parent.getChildNamedIgnoreCase(name) == null) {
				return name;
			}
		}

		throw new RuntimeException(
				"Unable to find a suitable new name; baseName = " + baseName + ", parent = " + parent);
	}

	/*
	 * public static String[] convertToStringArray( Object[] arr ) { String[]
	 * strings = new String[arr.length]; for( int i = 0; i < arr.length; i++ ) {
	 * strings[i] = (String)arr[i]; } return strings; }
	 */

	public static void setWorldTreeChildrenPropertiesStructure(
			final Vector<Object> worldTreeChildrenPropertiesStructure) {
		AuthoringToolResources.resources.worldTreeChildrenPropertiesStructure = worldTreeChildrenPropertiesStructure;
	}

	public static String[] getWorldTreeChildrenPropertiesStructure(final Class<?> elementClass) {
		if (AuthoringToolResources.resources.worldTreeChildrenPropertiesStructure != null) {
			for (final Iterator<Object> iter = AuthoringToolResources.resources.worldTreeChildrenPropertiesStructure
					.iterator(); iter.hasNext();) {
				final Object o = iter.next();
				if (o instanceof StringObjectPair) {
					final String className = ((StringObjectPair) o).getString();
					try {
						final Class<?> c = Class.forName(className);
						if (c.isAssignableFrom(elementClass)) {
							return (String[]) ((StringObjectPair) o).getObject();
						}
					} catch (final java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog(
							"Unexpected object found in worldTreeChildrenPropertiesStructure: " + o, null);
				}
			}
		}

		return null;
	}

	public static void addElementToAppropriateProperty(final edu.cmu.cs.stage3.alice.core.Element element,
			final edu.cmu.cs.stage3.alice.core.Element parent) {
		edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = null;

		if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.World) {
				oap = ((edu.cmu.cs.stage3.alice.core.World) parent).sandboxes;
			} else if (parent instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				oap = ((edu.cmu.cs.stage3.alice.core.Transformable) parent).parts;
			} else if (parent instanceof edu.cmu.cs.stage3.alice.core.Group) {
				oap = ((edu.cmu.cs.stage3.alice.core.Group) parent).values;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).responses;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).behaviors;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Variable) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).variables;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Question) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).questions;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Sound) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).sounds;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.TextureMap) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).textureMaps;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Pose) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				oap = ((edu.cmu.cs.stage3.alice.core.Transformable) parent).poses;
			}
		} else {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).misc;
			}
		}

		if (oap != null) {
			if (!oap.contains(element)) {
				oap.add(element);
			}
		}
	}

	public static double getAspectRatio(final edu.cmu.cs.stage3.alice.core.World world) {
		if (world != null) {
			final edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera[] spCameras = (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera[]) world
					.getDescendants(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera.class);
			if (spCameras.length > 0) {
				return spCameras[0].horizontalViewingAngle.doubleValue()
						/ spCameras[0].verticalViewingAngle.doubleValue();
			}
		}
		return 0.0;
	}

	public static double getCurrentTime() {
		final long timeMillis = System.currentTimeMillis() - startTime;
		return timeMillis / 1000.0;
	}

	public static void setImporterClasses(final Class<Importer>[] importers) {
		AuthoringToolResources.resources.importers = importers;
	}

	public static Class<? extends Importer>[] getImporterClasses() {
		return AuthoringToolResources.resources.importers;
	}

	public static void setEditorClasses(final Class<? extends Editor>[] editors) {
		AuthoringToolResources.resources.editors = editors;
	}

	public static Class<? extends Editor>[] getEditorClasses() {
		return AuthoringToolResources.resources.editors;
	}

	public static void findAssignables(final Class<?> baseClass, final java.util.Set<Class<?>> result,
			final boolean includeInterfaces) {
		if (baseClass != null) {
			if (!result.contains(baseClass)) {
				result.add(baseClass);

				if (includeInterfaces) {
					final Class<?>[] interfaces = baseClass.getInterfaces();
					for (final Class<?> interface1 : interfaces) {
						findAssignables(interface1, result, includeInterfaces);
					}
				}

				findAssignables(baseClass.getSuperclass(), result, includeInterfaces);
			}
		}
	}

	public static java.awt.datatransfer.DataFlavor getReferenceFlavorForClass(final Class<?> c) {
		if (!AuthoringToolResources.resources.flavorMap.containsKey(c)) {
			try {
				AuthoringToolResources.resources.flavorMap.put(c, new java.awt.datatransfer.DataFlavor(
						java.awt.datatransfer.DataFlavor.javaJVMLocalObjectMimeType + "; class=" + c.getName()));
			} catch (final ClassNotFoundException e) {
				AuthoringTool.showErrorDialog("Can't find class " + c.getName(), e);
			}
		}
		return AuthoringToolResources.resources.flavorMap.get(c);
	}

	public static Object getDefaultValueForClass(final Class<?> cls) {
		if (cls == Boolean.class) {
			return Boolean.TRUE;
		} else if (cls == Number.class) {
			return new Double(1);
		} else if (cls == String.class) {
			return new String("default string");
		} else if (cls == javax.vecmath.Vector3d.class) {
			return edu.cmu.cs.stage3.math.MathUtilities.createXAxis();
		} else if (cls == edu.cmu.cs.stage3.math.Vector3.class) {
			return new edu.cmu.cs.stage3.math.Vector3();
		} else if (cls == edu.cmu.cs.stage3.math.Quaternion.class) {
			return new edu.cmu.cs.stage3.math.Quaternion();
		} else if (javax.vecmath.Matrix4d.class.isAssignableFrom(cls)) {
			return new edu.cmu.cs.stage3.math.Matrix44();
		} else if (cls == java.awt.Color.class) {
			return java.awt.Color.white;
		} else if (cls == edu.cmu.cs.stage3.alice.scenegraph.Color.class) {
			return edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE;
		} else if (edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom(cls)) {
			final edu.cmu.cs.stage3.util.Enumerable[] items = edu.cmu.cs.stage3.util.Enumerable.getItems(cls);
			if (items.length > 0) {
				return items[0];
			} else {
				return null;
			}
		} else if (cls == edu.cmu.cs.stage3.alice.core.ReferenceFrame.class) {
			return AuthoringTool.getHack().getWorld();
		} else {
			return null;
		}
	}

	// public static javax.vecmath.Matrix4d getAGoodLookAtMatrix(
	// edu.cmu.cs.stage3.alice.core.Transformable transformable,
	// edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera ) {
	// if( (transformable != null) && (camera != null) ) {
	// edu.cmu.cs.stage3.alice.core.Transformable getAGoodLookDummy = new
	// edu.cmu.cs.stage3.alice.core.Transformable();
	// getAGoodLookDummy.vehicle.set( camera.vehicle.get() );
	// edu.cmu.cs.stage3.math.Sphere bs = transformable.getBoundingSphere();
	// double radius = bs.getRadius();
	// if( (radius == 0.0) || Double.isNaN( radius ) ) {
	// radius = 1.0;
	// }
	// double theta = Math.min( camera.horizontalViewingAngle.doubleValue(),
	// camera.verticalViewingAngle.doubleValue() );
	// double dist = radius/Math.sin( theta/2.0 );
	// double offset = dist/Math.sqrt( 3.0 );
	// javax.vecmath.Vector3d center = bs.getCenter();
	// if( center == null ) { // this should be unnecessary
	// center = transformable.getPosition();
	// }
	//
	// if( center != null ) {
	// if( (! Double.isNaN( center.x ) ) && (! Double.isNaN( center.y ) ) && (!
	// Double.isNaN( center.z ) ) && (! Double.isNaN( offset ) ) ) {
	// getAGoodLookDummy.setPositionRightNow( center.x - offset, center.y +
	// offset, center.z + offset, transformable );
	// getAGoodLookDummy.pointAtRightNow( transformable, new
	// edu.cmu.cs.stage3.math.Vector3( center ) );
	// javax.vecmath.Matrix4d result =
	// getAGoodLookDummy.getLocalTransformation();
	// getAGoodLookDummy.vehicle.set( null );
	// return result;
	// } else {
	// AuthoringTool.showErrorDialog( "bad bounding sphere center: " + center,
	// null );
	// }
	// } else {
	// AuthoringTool.showErrorDialog( "bounding sphere returned null center",
	// null );
	// }
	// }
	//
	// return null;
	// }

	public static double distanceToBackAfterGetAGoodLookAt(
			final edu.cmu.cs.stage3.alice.core.Transformable transformable,
			final edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera) {
		if (transformable != null && camera != null) {
			final edu.cmu.cs.stage3.math.Sphere bs = transformable.getBoundingSphere();
			final double radius = bs.getRadius();
			final double theta = Math.min(camera.horizontalViewingAngle.doubleValue(),
					camera.verticalViewingAngle.doubleValue());
			return radius / Math.sin(theta / 2.0) + radius;
		}

		return 0.0;
	}

	public static boolean areExperimentalFeaturesEnabled() {
		return AuthoringToolResources.resources.experimentalFeaturesEnabled;
	}

	public static void setExperimentalFeaturesEnabled(final boolean enabled) {
		AuthoringToolResources.resources.experimentalFeaturesEnabled = enabled;
	}

	public static void putMiscItem(final Object key, final Object item) {
		AuthoringToolResources.resources.miscMap.put(key, item);
	}

	public static Object getMiscItem(final Object key) {
		return AuthoringToolResources.resources.miscMap.get(key);
	}

	public static void garbageCollectIfPossible(
			final edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references) {
		for (final PropertyReference reference : references) {
			final edu.cmu.cs.stage3.alice.core.Element element = reference.getProperty().getOwner();
			// if( element instanceof
			// edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse )
			// {
			final edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] metaReferences = element.getRoot()
					.getPropertyReferencesTo(element, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS,
							false, true);
			if (metaReferences.length == 0) {
				element.getParent().removeChild(element);
			}
			// }
		}
	}

	public static String formatMemorySize(final long bytes) {
		String sizeString = null;
		if (bytes < 1024) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format(bytes) + " bytes";
		} else if (bytes < 1024L * 1024L) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format((double) bytes / (double) 1024)
					+ " KB";
		} else if (bytes < 1024L * 1024L * 1024L) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format(bytes / ((double) 1024L * 1024L))
					+ " MB";
		} else if (bytes < 1024L * 1024L * 1024L * 1024L) {
			sizeString = AuthoringToolResources.resources.decimalFormatter
					.format(bytes / ((double) 1024L * 1024L * 1024L)) + " GB";
		} else {
			sizeString = AuthoringToolResources.resources.decimalFormatter
					.format(bytes / ((double) 1024L * 1024L * 1024L * 1024L)) + " TB";
		}
		return sizeString;
	}

	public static String formatTime(final double seconds) {
		if (Double.isNaN(seconds)) {
			return "?:??";
		} else {
			final java.text.DecimalFormat decFormatter = new java.text.DecimalFormat(".000");
			final java.text.DecimalFormat secMinFormatter1 = new java.text.DecimalFormat("00");
			final java.text.DecimalFormat secMinFormatter2 = new java.text.DecimalFormat("#0");

			final double secondsFloored = (int) Math.floor(seconds);
			final double decimal = seconds - secondsFloored;
			final double secs = secondsFloored % 60.0;
			final double minutes = (secondsFloored - secs) / 60.0 % 60.0;
			final double hours = (secondsFloored - 60.0 * minutes - secs) / (60.0 * 60.0);

			String timeString = secMinFormatter1.format(secs) + decFormatter.format(decimal);
			if (hours > 0.0) {
				timeString = secMinFormatter1.format(minutes) + ":" + timeString;
				timeString = secMinFormatter2.format(hours) + ":" + timeString;
			} else {
				timeString = secMinFormatter2.format(minutes) + ":" + timeString;
			}

			return timeString;
		}
	}

	public static void printHierarchy(final java.awt.Component c) {
		printHierarchy(c, 0);
	}

	private static void printHierarchy(final java.awt.Component c, final int level) {
		String tabs = "";
		for (int i = 0; i < level; i++) {
			tabs += "--";
		}
		System.out.println(tabs + c.getClass().getName() + "_" + c.hashCode());

		if (c instanceof java.awt.Container) {
			final java.awt.Component[] children = ((java.awt.Container) c).getComponents();
			for (final Component element : children) {
				printHierarchy(element, level + 1);
			}
		}
	}

	private static void initKeyCodesToStrings() {
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_0), "0");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_1), "1");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_2), "2");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_3), "3");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_4), "4");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_5), "5");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_6), "6");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_7), "7");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_8), "8");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_9), "9");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_A), "A");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_B), "B");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_C), "C");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_D), "D");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_E), "E");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_F), "F");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_G), "G");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_H), "H");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_I), "I");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_J), "J");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_K), "K");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_L), "L");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_M), "M");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_N), "N");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_O), "O");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_P), "P");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_Q), "Q");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_R), "R");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_S), "S");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_T), "T");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_U), "U");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_V), "V");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_W), "W");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_X), "X");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_Y), "Y");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_Z), "Z");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_ENTER), "enter");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_SPACE), "space");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_UP), "upArrow");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_DOWN),
				"downArrow");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_LEFT),
				"leftArrow");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_RIGHT),
				"rightArrow");
	}

	public static void copyFile(final java.io.File from, final java.io.File to) throws java.io.IOException {
		if (!to.exists()) {
			to.createNewFile();
		}
		final java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.io.FileInputStream(from));
		final java.io.BufferedOutputStream out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(to));

		int b = in.read();
		while (b != -1) {
			out.write(b);
			b = in.read();
		}

		in.close();
		out.flush();
		out.close();
	}

	// ///////////////////////////
	// HACK code for stencils
	// ///////////////////////////

	public static String getPrefix(final String token) {
		if (token.indexOf("<") > -1 && token.indexOf(">") > token.indexOf("<")) {
			return token.substring(0, token.indexOf("<"));
		} else {
			return token;
		}
	}

	public static String getSpecifier(String token) {
		if (token.indexOf("<") > -1 && token.indexOf(">") > token.indexOf("<")) {
			if (!System.getProperty("os.name").startsWith("Window")) {
				token = token.replaceAll("\\\\", java.io.File.separator);
			}
			return token.substring(token.indexOf("<") + 1, token.indexOf(">"));
		} else {
			return null;
		}
	}

	public static java.awt.Component findElementDnDPanel(final java.awt.Container root,
			final edu.cmu.cs.stage3.alice.core.Element element) {
		final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						final java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o)
								.getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable,
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
							final edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element) transferable
									.getTransferData(
											edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
							if (element.equals(e)) {
								return true;
							}
						}
					} catch (final Exception e) {
						AuthoringTool.showErrorDialog("Error finding ElementDnDPanel.", e);
					}
				}
				return false;
			}
		};
		final java.awt.Component toReturn = findComponent(root, criterion);
		if (toReturn instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) {
			return ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) toReturn)
					.getWorkSpace();
		} else {
			return toReturn;
		}
	}

	public static java.awt.Component findPropertyDnDPanel(final java.awt.Container root,
			final edu.cmu.cs.stage3.alice.core.Element element, final String propertyName) {
		final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						final java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o)
								.getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable,
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
							final edu.cmu.cs.stage3.alice.core.Property p = (edu.cmu.cs.stage3.alice.core.Property) transferable
									.getTransferData(
											edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
							final edu.cmu.cs.stage3.alice.core.Element e = p.getOwner();
							if (element.equals(e) && p.getName().equals(propertyName)) {
								return true;
							}
						}
					} catch (final Exception e) {
						AuthoringTool.showErrorDialog("Error finding PropertyDnDPanel.", e);
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findUserDefinedResponseDnDPanel(final java.awt.Container root,
			final edu.cmu.cs.stage3.alice.core.Response actualResponse) {
		final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						final java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o)
								.getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable,
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
							final edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype) transferable
									.getTransferData(
											edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor);
							if (p.getActualResponse().equals(actualResponse)) {
								return true;
							}
						}
					} catch (final Exception e) {
						AuthoringTool.showErrorDialog("Error finding UserDefinedResponseDnDPanel.", e);
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findUserDefinedQuestionDnDPanel(final java.awt.Container root,
			final edu.cmu.cs.stage3.alice.core.Question actualQuestion) {
		final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						final java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o)
								.getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable,
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
							final edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype) transferable
									.getTransferData(
											edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor);
							if (p.getActualQuestion().equals(actualQuestion)) {
								return true;
							}
						}
					} catch (final Exception e) {
						AuthoringTool.showErrorDialog("Error finding UserDefinedQuestionDnDPanel.", e);
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findPrototypeDnDPanel(final java.awt.Container root, final Class<?> elementClass) {
		final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						final java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o)
								.getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable,
								edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
							final edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) transferable
									.getTransferData(
											edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
							if (p.getElementClass().equals(elementClass)) {
								return true;
							}
						}
					} catch (final Exception e) {
						AuthoringTool.showErrorDialog("Error finding PrototypeDnDPanel.", e);
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findPropertyViewController(final java.awt.Container root,
			final edu.cmu.cs.stage3.alice.core.Element element, final String propertyName) {
		final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) {
					final edu.cmu.cs.stage3.alice.core.Property p = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) o)
							.getProperty();
					if (p.getOwner().equals(element) && p.getName().equals(propertyName)) {
						return true;
					}
				} else if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController) {
					final edu.cmu.cs.stage3.alice.core.Property p = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController) o)
							.getProperty();
					if (p.getOwner().equals(element) && p.getName().equals(propertyName)) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findButton(final java.awt.Container root, final String buttonText) {
		final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof javax.swing.JButton) {
					if (((javax.swing.JButton) o).getText().equals(buttonText)) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findEditObjectButton(final java.awt.Container root,
			final edu.cmu.cs.stage3.alice.core.Element element) {
		final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton) {
					if (((edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton) o).getObject().equals(element)) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findGalleryObject(final java.awt.Container root, final String uniqueIdentifier) {
		final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(final Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) {
					if (((edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) o).getUniqueIdentifier()
							.equals(uniqueIdentifier)) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findComponent(final java.awt.Container root,
			final edu.cmu.cs.stage3.util.Criterion criterion) {
		if (criterion.accept(root)) {
			return root;
		}

		final java.awt.Component[] children = root.getComponents();
		for (final Component element : children) {
			if (element instanceof java.awt.Container) {
				final java.awt.Component result = findComponent((java.awt.Container) element, criterion);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}
}