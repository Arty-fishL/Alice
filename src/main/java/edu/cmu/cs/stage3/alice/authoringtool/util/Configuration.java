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

import java.util.Iterator;

import org.w3c.dom.Element;

/**
 * @author Jason Pratt
 */
public final class Configuration {
	public final static int VIS_OPEN = 1;
	public final static int VIS_ADVANCED = 2;
	public final static int VIS_HIDDEN = 4;
	public final static int VIS_ALL = VIS_OPEN | VIS_ADVANCED | VIS_HIDDEN;

	// static access
	public static String getValue(final Package p, final String relativeKey) {
		return _getValue(p.getName() + "." + relativeKey);
	}

	public static String[] getValueList(final Package p, final String relativeKey) {
		return _getValueList(p.getName() + "." + relativeKey);
	}

	public static void setValue(final Package p, final String relativeKey, final String value) {
		_setValue(p.getName() + "." + relativeKey, value);
	}

	public static void setValueList(final Package p, final String relativeKey, final String[] values) {
		_setValueList(p.getName() + "." + relativeKey, values);
	}

	public static void addToValueList(final Package p, final String relativeKey, final String item) {
		_addToValueList(p.getName() + "." + relativeKey, item);
	}

	public static void removeFromValueList(final Package p, final String relativeKey, final String item) {
		_removeFromValueList(p.getName() + "." + relativeKey, item);
	}

	public static boolean isList(final Package p, final String relativeKey) {
		return _isList(p.getName() + "." + relativeKey);
	}

	public static boolean keyExists(final Package p, final String relativeKey) {
		return _keyExists(p.getName() + "." + relativeKey);
	}

	public static void deleteKey(final Package p, final String relativeKey) {
		_deleteKey(p.getName() + "." + relativeKey);
	}

	public static String[] getSubKeys(final Package p, final String relativeKey, final int visibility) {
		return _getSubKeys(p.getName() + "." + relativeKey, visibility);
	}

	public static void setVisibility(final Package p, final String relativeKey, final int visibility) {
		_setVisibility(p.getName() + "." + relativeKey, visibility);
	}

	// instance access
	private final String keyPrefix;

	private Configuration(final Package p) {
		keyPrefix = p.getName() + ".";
	}

	public static Configuration getLocalConfiguration(final Package p) {
		return new Configuration(p);
	}

	public String getValue(final String relativeKey) {
		return _getValue(keyPrefix + relativeKey);
	}

	public String[] getValueList(final String relativeKey) {
		return _getValueList(keyPrefix + relativeKey);
	}

	public void setValue(final String relativeKey, final String value) {
		_setValue(keyPrefix + relativeKey, value);
	}

	public void setValueList(final String relativeKey, final String[] values) {
		_setValueList(keyPrefix + relativeKey, values);
	}

	public void addToValueList(final String relativeKey, final String item) {
		_addToValueList(keyPrefix + relativeKey, item);
	}

	public void removeFromValueList(final String relativeKey, final String item) {
		_removeFromValueList(keyPrefix + relativeKey, item);
	}

	public boolean isList(final String relativeKey) {
		return _isList(keyPrefix + relativeKey);
	}

	public boolean keyExists(final String relativeKey) {
		return _keyExists(keyPrefix + relativeKey);
	}

	public void deleteKey(final String relativeKey) {
		_deleteKey(keyPrefix + relativeKey);
	}

	public String[] getSubKeys(final String relativeKey, final int visibility) {
		return _getSubKeys(keyPrefix + relativeKey, visibility);
	}

	public void setVisibility(final String relativeKey, final int visibility) {
		_setVisibility(keyPrefix + relativeKey, visibility);
	}

	// internals
	private static final java.io.File configLocation = new java.io.File(
			edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory(), "AlicePreferences.xml")
					.getAbsoluteFile();
	private static Key root;

	static {
		root = new Key();
		root.name = "<root>";
		root.subKeys = new java.util.HashMap<String, Key>();
		final java.io.File aliceHasNotExitedFile = new java.io.File(
				edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory(), "aliceHasNotExited.txt");
		if (aliceHasNotExitedFile.canRead()) {
			try {
				storeConfig();
			} catch (final java.io.IOException e2) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("Unable to create new preferences file.", e2);
			}
		} else {
			try {
				loadConfig(configLocation);
			} catch (final Exception e) {

			}
		}
	}

	private static class Key {
		public String name;
		public int visibility;
		public String value;
		public java.util.ArrayList<String> valueList;
		public java.util.HashMap<String, Key> subKeys;

		public Key getSubKey(final String name) {
			if (subKeys != null) {
				final int i = name.indexOf('.');
				if (i == -1) {
					return (Key) subKeys.get(name);
				} else {
					final Key subKey = (Key) subKeys.get(name.substring(0, i));
					if (subKey != null) {
						return subKey.getSubKey(name.substring(i + 1));
					}
				}
			}
			return null;
		}

		public Key createSubKey(final String name) {
			if (subKeys == null) {
				subKeys = new java.util.HashMap<String, Key>();
			}

			final int i = name.indexOf('.');
			if (i == -1) {
				Key subKey = (Key) subKeys.get(name);
				if (subKey == null) {
					subKey = new Key();
					subKey.name = name;
					subKeys.put(name, subKey);
				}
				return subKey;
			} else {
				Key subKey = (Key) subKeys.get(name.substring(0, i));
				if (subKey == null) {
					subKey = new Key();
					subKey.name = name.substring(0, i);
					subKeys.put(subKey.name, subKey);
				}
				return subKey.createSubKey(name.substring(i + 1));
			}
		}

		public void deleteSubKey(final String name) {
			if (subKeys != null) {
				final int i = name.indexOf('.');
				if (i == -1) {
					subKeys.remove(name);
				} else {
					final Key subKey = (Key) subKeys.get(name.substring(0, i));
					if (subKey != null) {
						subKey.deleteSubKey(name.substring(i + 1));
					}
				}
			}
		}

		@Override
		public String toString() {
			final StringBuffer s = new StringBuffer();
			s.append("\nname: " + name + "\n");
			s.append("visibility: " + visibility + "\n");
			s.append("value: " + value + "\n");
			s.append("valueList: " + valueList + "\n");
			s.append("subKeys: " + subKeys + "\n");
			return s.toString();
		}
	}

	// somebody is going to hate me for the underscores,
	// but I needed to avoid duplication with the instance access methods,
	// so I borrowed a convention from python.

	private static String _getValue(final String keyName) {
		final Key key = root.getSubKey(keyName);
		if (key != null) {
			return key.value;
		}
		return null;
	}

	private static String[] _getValueList(final String keyName) {
		final Key key = root.getSubKey(keyName);
		if (key != null) {
			if (key.valueList != null) {
				return (String[]) key.valueList.toArray(new String[0]);
			}
		}
		return null;
	}

	private static void _setValue(final String keyName, final String value) {
		Key key = root.getSubKey(keyName);

		if (key == null) {
			key = root.createSubKey(keyName);
		}

		final String oldValue = key.value;
		final String[] oldValueList = _getValueList(keyName);
		fireChanging(keyName, _isList(keyName), oldValue, value, oldValueList, null);

		if (key.valueList != null) {
			key.valueList = null;
		}
		key.value = value;

		fireChanged(keyName, _isList(keyName), oldValue, value, oldValueList, null);
	}

	private static void _setValueList(final String keyName, final String[] values) {
		Key key = root.getSubKey(keyName);

		if (key == null) {
			key = root.createSubKey(keyName);
		}

		final String oldValue = key.value;
		final String[] oldValueList = _getValueList(keyName);
		fireChanging(keyName, _isList(keyName), oldValue, null, oldValueList, values);

		if (key.value != null) {
			key.value = null;
		}

		if (key.valueList == null) {
			key.valueList = new java.util.ArrayList<String>(values == null ? 0 : values.length);
		} else {
			key.valueList.clear();
		}
		if (values != null) {
			for (final String value : values) {
				key.valueList.add(value);
			}
		}

		fireChanged(keyName, _isList(keyName), oldValue, null, oldValueList, values);
	}

	private static void _addToValueList(final String keyName, final String item) {
		Key key = root.getSubKey(keyName);

		if (key == null) {
			key = root.createSubKey(keyName);
		}

		final String oldValue = key.value;
		final String[] oldValueList = _getValueList(keyName);
		fireChanging(keyName, _isList(keyName), oldValue, null, oldValueList, null);

		if (key.value != null) {
			key.value = null;
		}

		if (key.valueList == null) {
			key.valueList = new java.util.ArrayList<String>();
		}
		if (item != null) {
			key.valueList.add(item);
		}

		fireChanged(keyName, _isList(keyName), oldValue, null, oldValueList, _getValueList(keyName));
	}

	private static void _removeFromValueList(final String keyName, final String item) {
		Key key = root.getSubKey(keyName);

		if (key == null) {
			key = root.createSubKey(keyName);
		}

		final String oldValue = key.value;
		final String[] oldValueList = _getValueList(keyName);
		fireChanging(keyName, _isList(keyName), oldValue, null, oldValueList, null);

		if (key.value != null) {
			key.value = null;
		}

		if (key.valueList == null) {
			key.valueList = new java.util.ArrayList<String>();
		} else {
			if (item != null) {
				key.valueList.remove(item);
			}
		}

		fireChanged(keyName, _isList(keyName), oldValue, null, oldValueList, _getValueList(keyName));
	}

	private static boolean _isList(final String keyName) {
		final Key key = root.getSubKey(keyName);
		if (key != null) {
			return key.valueList != null;
		}
		return false;
	}

	private static boolean _keyExists(final String keyName) {
		return root.getSubKey(keyName) != null;
	}

	private static void _deleteKey(final String keyName) {
		root.deleteSubKey(keyName);
	}

	private static String[] _getSubKeys(final String keyName, final int visibility) {
		final Key key = root.getSubKey(keyName);
		if (key != null) {
			final java.util.ArrayList<String> list = new java.util.ArrayList<>(key.subKeys.size());
			// Wrong ?? for (final java.util.Iterator<Key> iter = key.subKeys.keySet().iterator(); iter.hasNext();) {
			for (final java.util.Iterator<Key> iter = key.subKeys.values().iterator(); iter.hasNext();) {
				final Key subKey = (Key) iter.next();
				if ((subKey.visibility & visibility) > 0) {
					list.add(subKey.name);
				}
			}
			return (String[]) list.toArray(new String[0]);
		}
		return null;
	}

	private static void _setVisibility(final String keyName, final int visibility) {
		final Key key = root.getSubKey(keyName);
		if (key != null) {
			key.visibility = visibility;
		}
	}

	// IO
	private static void loadConfig(final java.io.File file) throws java.io.IOException {
		loadConfig(file.toURI().toURL());
	}

	private static void loadConfig(final java.net.URL url) throws java.io.IOException {
		final java.io.BufferedInputStream bis = new java.io.BufferedInputStream(url.openStream());
		loadConfig(bis);
		bis.close();
	}

	private static void loadConfig(final java.io.InputStream is) throws java.io.IOException {
		root.subKeys = new java.util.HashMap<String, Key>();

		final javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		try {
			final javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
			final org.w3c.dom.Document document = builder.parse(is);

			final org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.normalize();

			final org.w3c.dom.NodeList childNodes = rootElement.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				final org.w3c.dom.Node childNode = childNodes.item(i);
				if (childNode instanceof org.w3c.dom.Element) {
					final org.w3c.dom.Element childElement = (org.w3c.dom.Element) childNode;
					final String tagName = childElement.getTagName();
					if (tagName.equals("key")) {
						final Key subKey = loadKey(childElement);
						if (subKey != null && subKey.name != null) {
							root.subKeys.put(subKey.name, subKey);
							// System.out.println( "loaded subKey: " + subKey );
						}
					}
				}
			}
			// } catch( org.xml.sax.SAXParseException spe ) {
			// if( spe.getException() != null ) {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file; line " + spe.getLineNumber() +
			// ", uri " + spe.getSystemId() + "\nmessage: " + spe.getMessage(),
			// spe.getException() );
			// } else {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file; line " + spe.getLineNumber() +
			// ", uri " + spe.getSystemId() + "\nmessage: " + spe.getMessage(),
			// spe );
			// }
			// } catch( org.xml.sax.SAXException sxe ) {
			// if( sxe.getException() != null ) {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file.", sxe.getException() );
			// } else {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file.", sxe );
			// }
			// } catch( javax.xml.parsers.ParserConfigurationException pce ) {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file.", pce );
			// }
		} catch (final Exception e) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					"Alice had trouble reading your preferences but will continue to run normally",
					"Unable to load preferences", javax.swing.JOptionPane.WARNING_MESSAGE);
		}
	}

	private static Key loadKey(final org.w3c.dom.Element keyElement) {
		final Key key = new Key();

		final String visibility = keyElement.getAttribute("visibility").trim();
		if (visibility.equals("open")) {
			key.visibility = VIS_OPEN;
		} else if (visibility.equals("advanced")) {
			key.visibility = VIS_ADVANCED;
		} else if (visibility.equals("hidden")) {
			key.visibility = VIS_HIDDEN;
		}

		final java.util.HashMap<String, Object> map = parseSingleNode(keyElement);

		final org.w3c.dom.Element nameElement = (org.w3c.dom.Element) map.get("name");
		if (nameElement != null) {
			final org.w3c.dom.Text textNode = (org.w3c.dom.Text) parseSingleNode(nameElement).get("text");
			if (textNode != null) {
				key.name = textNode.getData().trim();
			}
		}

		final org.w3c.dom.Element valueElement = (org.w3c.dom.Element) map.get("value");
		if (valueElement != null) {
			final org.w3c.dom.Element listElement = (org.w3c.dom.Element) parseSingleNode(valueElement).get("list");
			if (listElement != null) {
				key.valueList = new java.util.ArrayList<String>();
				@SuppressWarnings("unchecked")
				final java.util.ArrayList<org.w3c.dom.Element> items = (java.util.ArrayList<org.w3c.dom.Element>) parseSingleNode(listElement).get("items");
				if (items != null) {
					for (final Iterator<Element> iter = items.iterator(); iter.hasNext();) {
						final org.w3c.dom.Element itemElement = (org.w3c.dom.Element) iter.next();
						if (itemElement != null) {
							final org.w3c.dom.Text textNode = (org.w3c.dom.Text) parseSingleNode(itemElement)
									.get("text");
							if (textNode != null) {
								key.valueList.add(textNode.getData().trim());
							}
						}
					}
				}
			} else {
				final org.w3c.dom.Text textNode = (org.w3c.dom.Text) parseSingleNode(valueElement).get("text");
				if (textNode != null) {
					key.value = textNode.getData().trim();
				}
			}
		}

		@SuppressWarnings("unchecked")
		final java.util.ArrayList<Key> keys = (java.util.ArrayList<Key>) map.get("keys");
		if (keys != null) {
			for (final Iterator<Key> iter = keys.iterator(); iter.hasNext();) {
				final org.w3c.dom.Element subKeyElement = (org.w3c.dom.Element) iter.next();
				if (subKeyElement != null) {
					if (key.subKeys == null) {
						key.subKeys = new java.util.HashMap<String, Key>();
					}
					final Key subKey = loadKey(subKeyElement);
					if (subKey != null && subKey.name != null) {
						key.subKeys.put(subKey.name, subKey);
					}
				}
			}
		}

		return key;
	}

	/**
	 * maps:
	 *
	 * "name" -> <name> Element "value" -> <value> Element "list" -> <list>
	 * Element "items" -> ArrayList of <item> Elements "keys" -> ArrayList of
	 * <key> Elements "text" -> last Text Node encountered
	 */
	private static java.util.HashMap<String, Object> parseSingleNode(final org.w3c.dom.Node node) {
		// TODO: check for efficiency problems with creating this HashMap for
		// each Node...
		final java.util.HashMap<String, Object> map = new java.util.HashMap<>();

		final org.w3c.dom.NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			final org.w3c.dom.Node childNode = childNodes.item(i);

			if (childNode instanceof org.w3c.dom.Element) {
				final org.w3c.dom.Element childElement = (org.w3c.dom.Element) childNode;
				final String tagName = childElement.getTagName();
				if (tagName.equals("name")) {
					map.put("name", childElement);
				} else if (tagName.equals("value")) {
					map.put("value", childElement);
				} else if (tagName.equals("list")) {
					map.put("list", childElement);
				} else if (tagName.equals("item")) {
					@SuppressWarnings("unchecked")
					java.util.ArrayList<org.w3c.dom.Element> list = (java.util.ArrayList<org.w3c.dom.Element>) map.get("items");
					if (list == null) {
						list = new java.util.ArrayList<org.w3c.dom.Element>();
						map.put("items", list);
					}
					list.add(childElement);
				} else if (tagName.equals("key")) {
					@SuppressWarnings("unchecked")
					java.util.ArrayList<Element> list = (java.util.ArrayList<Element>) map.get("keys");
					if (list == null) {
						list = new java.util.ArrayList<Element>();
						map.put("keys", list);
					}
					list.add(childElement);
				}
			} else if (childNode instanceof org.w3c.dom.Text) {
				map.put("text", childNode);
			}
		}

		return map;
	}

	// Unused ??
	@SuppressWarnings("unused")
	private static org.w3c.dom.Element getChildElementNamed(final String name, final org.w3c.dom.Node node) {
		final org.w3c.dom.NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			final org.w3c.dom.Node childNode = childNodes.item(i);
			if (childNode instanceof org.w3c.dom.Element) {
				final org.w3c.dom.Element childElement = (org.w3c.dom.Element) childNode;
				final String tagName = childElement.getTagName();
				if (tagName.equals(name)) {
					return childElement;
				}
			}
		}
		return null;
	}

	public static void storeConfig() throws java.io.IOException {
		if (configLocation.getParentFile().exists() && configLocation.getParentFile().canWrite()) {
			if (configLocation.exists()) {
				if (configLocation.canWrite()) {
					storeConfig(configLocation);
				}
			} else {
				storeConfig(configLocation);
			}
		}
	}

	private static void storeConfig(final java.io.File file) throws java.io.IOException {
		final java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file));
		storeConfig(bos);
		bos.flush();
		bos.close();
	}

	private static void storeConfig(final java.io.OutputStream os) throws java.io.IOException {
		final javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		try {
			final javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
			final org.w3c.dom.Document document = builder.newDocument();

			final org.w3c.dom.Element rootElement = document.createElement("configuration");
			document.appendChild(rootElement);

			if (root.subKeys != null) {
				for (final Iterator<Key> iter = root.subKeys.values().iterator(); iter.hasNext();) {
					final Key key = (Key) iter.next();
					rootElement.appendChild(makeKeyElement(document, key));
				}
			}

			document.getDocumentElement().normalize();

			edu.cmu.cs.stage3.xml.Encoder.write(document, os);
			// try {
			// Class documentClass = document.getClass();
			// java.lang.reflect.Method writeMethod = documentClass.getMethod(
			// "write", new Class[] { java.io.OutputStream.class } );
			// writeMethod.invoke( document, new Object[] { os } );
			// } catch( Exception e ) {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Unable to store preferences. Cannot invoke 'write' method.", e
			// );
			// }
			// ((org.apache.crimson.tree.XmlDocument)document).write( os );
			// ((com.sun.xml.tree.XmlDocument)document).write( os );
		} catch (final javax.xml.parsers.ParserConfigurationException pce) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error parsing preferences file.", pce);
		}
	}

	private static org.w3c.dom.Element makeKeyElement(final org.w3c.dom.Document document, final Key key) {
		final org.w3c.dom.Element keyElement = document.createElement("key");
		if ((key.visibility & VIS_OPEN) > 0) {
			keyElement.setAttribute("visibility", "open");
		} else if ((key.visibility & VIS_ADVANCED) > 0) {
			keyElement.setAttribute("visibility", "advanced");
		} else if ((key.visibility & VIS_HIDDEN) > 0) {
			keyElement.setAttribute("visibility", "hidden");
		} else {
			keyElement.setAttribute("visibility", "open");
		}

		final org.w3c.dom.Element nameElement = document.createElement("name");
		nameElement.appendChild(document.createTextNode(key.name));
		keyElement.appendChild(nameElement);

		if (key.value != null) {
			final org.w3c.dom.Element valueElement = document.createElement("value");
			valueElement.appendChild(document.createTextNode(key.value));
			keyElement.appendChild(valueElement);
		} else if (key.valueList != null) {
			final org.w3c.dom.Element valueElement = document.createElement("value");
			final org.w3c.dom.Element listElement = document.createElement("list");
			for (final Iterator<String> iter = key.valueList.iterator(); iter.hasNext();) {
				final org.w3c.dom.Element itemElement = document.createElement("item");
				itemElement.appendChild(document.createTextNode((String) iter.next()));
				listElement.appendChild(itemElement);
			}
			valueElement.appendChild(listElement);
			keyElement.appendChild(valueElement);
		}

		if (key.subKeys != null) {
			for (final Iterator<Key> iter = key.subKeys.values().iterator(); iter.hasNext();) {
				final Key subKey = (Key) iter.next();
				keyElement.appendChild(makeKeyElement(document, subKey));
			}
		}

		return keyElement;
	}

	// Listening
	protected static java.util.HashSet<edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener> listeners = new java.util.HashSet<>();

	public static void addConfigurationListener(
			final edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener listener) {
		listeners.add(listener);
	}

	public static void removeConfigurationListener(
			final edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener listener) {
		listeners.remove(listener);
	}

	protected static void fireChanging(final String keyName, final boolean isList, final String oldValue,
			final String newValue, final String[] oldValueList, final String[] newValueList) {
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent
		// ev = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent(
		// keyName, isList, oldValue, newValue, oldValueList, newValueList );
		// for( java.util.Iterator iter = listeners.iterator(); iter.hasNext();
		// ) {
		// ((edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener)iter.next()).changing(
		// ev );
		// }
		final edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent(
				keyName, isList, oldValue, newValue, oldValueList, newValueList);
		final Object[] listenerArray = listeners.toArray();
		for (final Object element : listenerArray) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener) element).changing(ev);
		}
	}

	protected static void fireChanged(final String keyName, final boolean isList, final String oldValue,
			final String newValue, final String[] oldValueList, final String[] newValueList) {
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent
		// ev = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent(
		// keyName, isList, oldValue, newValue, oldValueList, newValueList );
		// for( java.util.Iterator iter = listeners.iterator(); iter.hasNext();
		// ) {
		// ((edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener)iter.next()).changed(
		// ev );
		// }
		final edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent(
				keyName, isList, oldValue, newValue, oldValueList, newValueList);
		final Object[] listenerArray = listeners.toArray();
		for (final Object element : listenerArray) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener) element).changed(ev);
		}
	}
}