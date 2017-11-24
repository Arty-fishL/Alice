package edu.cmu.cs.stage3.alice.scenegraph.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.util.Enumerable;

class PropertyReference {
	public PropertyReference(final edu.cmu.cs.stage3.alice.scenegraph.Property property,
			final edu.cmu.cs.stage3.alice.scenegraph.Element element, final String key) {
		m_property = property;
		m_element = element;
		m_key = key;
	}

	public void resolve(final java.util.Dictionary<String, edu.cmu.cs.stage3.alice.scenegraph.Element> map) {
		final Object value = map.get(m_key);
		if (value != null) {
			m_property.set(m_element, value);
		} else {
			throw new RuntimeException(
					"could resolve reference- property: " + m_property + "; element: " + m_element + "; key: " + m_key);
		}
	}

	private final edu.cmu.cs.stage3.alice.scenegraph.Property m_property;
	private final edu.cmu.cs.stage3.alice.scenegraph.Element m_element;
	// Unused ?? private String m_mixedCasePropertyName;
	private final String m_key;
}

public class XML {
	private static String encodeIntArray(final int[] array, final int offset, final int length,
			final boolean isHexadecimal) {
		final StringBuffer buffer = new StringBuffer();
		int index = offset;
		for (int lcv = 0; lcv < length; lcv++) {
			String s;
			final int value = array[index++];
			if (isHexadecimal) {
				s = Integer.toHexString(value).toUpperCase();
			} else {
				s = Integer.toString(value);
			}
			buffer.append(s);
			if (lcv < length - 1) {
				buffer.append(' ');
			}
		}
		return buffer.toString();
	}

	private static void decodeIntArray(final String s, final int[] array, final int offset, final int length,
			final boolean isHexadecimal) {
		int index = offset;
		int begin = 0;
		for (int lcv = 0; lcv < length; lcv++) {
			int end = s.indexOf(' ', begin);
			if (end == -1) {
				end = s.length();
			}
			final String substr = s.substring(begin, end);
			int value;
			if (isHexadecimal) {
				value = (int) Long.parseLong(substr, 16);
			} else {
				value = Integer.parseInt(substr);
			}
			array[index++] = value;
			begin = end + 1;
		}
	}

	private static String encodeIntArray(final int[] array, final boolean isHexadecimal) {
		return encodeIntArray(array, 0, array.length, isHexadecimal);
	}

	private static void decodeIntArray(final String s, final int[] array, final boolean isHexadecimal) {
		decodeIntArray(s, array, 0, array.length, isHexadecimal);
	}

	private static String encodeDoubleArray(final double[] array, final int offset, final int length) {
		final StringBuffer buffer = new StringBuffer();
		int index = offset;
		for (int lcv = 0; lcv < length; lcv++) {
			buffer.append(Double.toString(array[index++]));
			if (lcv < length - 1) {
				buffer.append(' ');
			}
		}
		return buffer.toString();
	}

	private static void decodeDoubleArray(final String s, final double[] array, final int offset, final int length) {
		int index = offset;
		int begin = 0;
		for (int lcv = 0; lcv < length; lcv++) {
			int end = s.indexOf(' ', begin);
			if (end == -1) {
				end = s.length();
			}
			final String substr = s.substring(begin, end);
			array[index++] = Double.parseDouble(substr);
			begin = end + 1;
		}
	}

	private static String encodeDoubleArray(final double[] array) {
		return encodeDoubleArray(array, 0, array.length);
	}

	private static void decodeDoubleArray(final String s, final double[] array) {
		decodeDoubleArray(s, array, 0, array.length);
	}

	private static String encodeTuple3d(final javax.vecmath.Tuple3d tuple3d) {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(Double.toString(tuple3d.x));
		buffer.append(' ');
		buffer.append(Double.toString(tuple3d.y));
		buffer.append(' ');
		buffer.append(Double.toString(tuple3d.z));
		return buffer.toString();
	}

	private static void decodeTuple3d(final String s, final javax.vecmath.Tuple3d tuple3d) {
		int begin = 0;
		int end = s.indexOf(' ', begin);
		tuple3d.x = Double.parseDouble(s.substring(begin, end));

		begin = end + 1;
		end = s.indexOf(' ', begin);
		tuple3d.y = Double.parseDouble(s.substring(begin, end));

		begin = end + 1;
		end = s.length();
		tuple3d.z = Double.parseDouble(s.substring(begin, end));
	}

	private static String encodeTuple2f(final javax.vecmath.Tuple2f tuple2f) {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(Float.toString(tuple2f.x));
		buffer.append(' ');
		buffer.append(Float.toString(tuple2f.y));
		return buffer.toString();
	}

	private static void decodeTuple2f(final String s, final javax.vecmath.Tuple2f tuple2f) {
		int begin = 0;
		int end = s.indexOf(' ', begin);
		tuple2f.x = Float.parseFloat(s.substring(begin, end));

		begin = end + 1;
		end = s.length();
		tuple2f.y = Float.parseFloat(s.substring(begin, end));
	}

	private static String encodeEnumerable(final edu.cmu.cs.stage3.util.Enumerable enumerable) {
		return enumerable.getRepr();
	}

	private static edu.cmu.cs.stage3.util.Enumerable decodeEnumerable(final Class<?> cls, final String s) {
		final edu.cmu.cs.stage3.util.Enumerable[] array = edu.cmu.cs.stage3.util.Enumerable.getItems(cls);
		for (final Enumerable element : array) {
			if (s.equals(element.getRepr())) {
				return element;
			}
		}
		return null;
	}

	public static final double VERSION = 1.0;

	private static String getKey(final edu.cmu.cs.stage3.alice.scenegraph.Element element) {
		return Integer.toString(element.hashCode());
	}

	private static org.w3c.dom.Element encodeElement(final edu.cmu.cs.stage3.alice.scenegraph.Element element,
			final org.w3c.dom.Document document, final String s, final java.util.Dictionary<String, Object> elementsToBeEncoded) {
		final org.w3c.dom.Element xmlElement = document.createElement(s);
		xmlElement.setAttribute("class", element.getClass().getName());
		xmlElement.setAttribute("key", getKey(element));
		final java.util.Enumeration<edu.cmu.cs.stage3.alice.scenegraph.Property> enum0 = edu.cmu.cs.stage3.alice.scenegraph.Property
				.getProperties(element.getClass()).elements();
		while (enum0.hasMoreElements()) {
			final edu.cmu.cs.stage3.alice.scenegraph.Property property = enum0
					.nextElement();
			final String propertyName = property.getMixedCaseName();
			if (propertyName.equals("Parent")) {
				// pass
			} else if (propertyName.equals("Bonus")) {
				// pass
			} else {
				final org.w3c.dom.Element xmlProperty = document.createElement("property");
				xmlProperty.setAttribute("name", propertyName);
				final Object value = property.get(element);
				if (value != null) {
					final Class<? extends Object> propertyValueClass = value.getClass();
					if (edu.cmu.cs.stage3.alice.scenegraph.Element.class.isAssignableFrom(propertyValueClass)) {
						final String key = Integer.toString(value.hashCode());
						xmlProperty.setAttribute("key", key);
						if (edu.cmu.cs.stage3.alice.scenegraph.Component.class.isAssignableFrom(propertyValueClass)) {
						} else {
							elementsToBeEncoded.put(key, value);
						}
					} else {
						if (javax.vecmath.Matrix4d.class.isAssignableFrom(propertyValueClass)) {
							xmlProperty.setAttribute("class", "javax.vecmath.Matrix4d");
							final javax.vecmath.Matrix4d m = (javax.vecmath.Matrix4d) value;
							final double[] row = new double[4];
							for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
								final org.w3c.dom.Element xmlRow = document.createElement("row");
								m.getRow(rowIndex, row);
								// for( int colIndex=0; colIndex<4; colIndex++ )
								// {
								// org.w3c.dom.Element itemNode =
								// document.createElement( "item" );
								// itemNode.appendChild(
								// document.createTextNode( Double.toString(
								// row[ colIndex ] ) ) );
								// rowNode.appendChild( itemNode );
								// }
								xmlRow.appendChild(document.createTextNode(encodeDoubleArray(row)));
								xmlProperty.appendChild(xmlRow);
							}
						} else if (javax.vecmath.Matrix3d.class.isAssignableFrom(propertyValueClass)) {
							xmlProperty.setAttribute("class", "javax.vecmath.Matrix3d");
							final javax.vecmath.Matrix3d m = (javax.vecmath.Matrix3d) value;
							final double[] row = new double[3];
							for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
								final org.w3c.dom.Element xmlRow = document.createElement("row");
								m.getRow(rowIndex, row);
								// for( int colIndex=0; colIndex<3; colIndex++ )
								// {
								// org.w3c.dom.Element itemNode =
								// document.createElement( "item" );
								// itemNode.appendChild(
								// document.createTextNode( Double.toString(
								// row[ colIndex ] ) ) );
								// rowNode.appendChild( itemNode );
								// }
								xmlRow.appendChild(document.createTextNode(encodeDoubleArray(row)));
								xmlProperty.appendChild(xmlRow);
							}
						} else if (java.awt.Image.class.isAssignableFrom(propertyValueClass)) {
							final java.awt.Image image = (java.awt.Image) value;
							try {
								final int width = edu.cmu.cs.stage3.image.ImageUtilities.getWidth(image);
								final int height = edu.cmu.cs.stage3.image.ImageUtilities.getHeight(image);
								final int[] pixels = edu.cmu.cs.stage3.image.ImageUtilities.getPixels(image, width,
										height);
								xmlProperty.setAttribute("class", "java.awt.Image");
								xmlProperty.setAttribute("width", Integer.toString(width));
								xmlProperty.setAttribute("height", Integer.toString(width));
								int pixelIndex = 0;
								for (int rowIndex = 0; rowIndex < height; rowIndex++) {
									final org.w3c.dom.Element xmlRow = document.createElement("row");
									/*
									 * for( int colIndex=0; colIndex<width;
									 * colIndex++ ) { int pixel = pixels[
									 * pixelIndex++ ]; //int alpha = (pixel >>
									 * 24) & 0xff; //int red = (pixel >> 16) &
									 * 0xff; //int green = (pixel >> 8) & 0xff;
									 * //int blue = (pixel ) & 0xff;
									 *
									 * org.w3c.dom.Element itemNode =
									 * document.createElement( "item" );
									 * itemNode.appendChild(
									 * document.createTextNode(
									 * Integer.toHexString( pixel
									 * ).toUpperCase() ) ); rowNode.appendChild(
									 * itemNode ); }
									 */
									xmlRow.appendChild(
											document.createTextNode(encodeIntArray(pixels, pixelIndex, width, true)));
									pixelIndex += width;
									xmlProperty.appendChild(xmlRow);
								}
							} catch (final InterruptedException ie) {
								throw new RuntimeException();
							}
						} else if (edu.cmu.cs.stage3.alice.scenegraph.Color.class
								.isAssignableFrom(propertyValueClass)) {
							xmlProperty.setAttribute("class", "edu.cmu.cs.stage3.alice.scenegraph.Color");
							final edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color) value;
							final org.w3c.dom.Element xmlRed = document.createElement("red");
							xmlRed.appendChild(document.createTextNode(Double.toString(color.red)));
							xmlProperty.appendChild(xmlRed);
							final org.w3c.dom.Element xmlGreen = document.createElement("green");
							xmlGreen.appendChild(document.createTextNode(Double.toString(color.green)));
							xmlProperty.appendChild(xmlGreen);
							final org.w3c.dom.Element xmlBlue = document.createElement("blue");
							xmlBlue.appendChild(document.createTextNode(Double.toString(color.blue)));
							xmlProperty.appendChild(xmlBlue);
							final org.w3c.dom.Element xmlAlpha = document.createElement("alpha");
							xmlAlpha.appendChild(document.createTextNode(Double.toString(color.alpha)));
							xmlProperty.appendChild(xmlAlpha);
						} else if (int[].class.isAssignableFrom(propertyValueClass)) {
							final int[] array = (int[]) value;
							xmlProperty.setAttribute("class", "[I");
							xmlProperty.setAttribute("length", Integer.toString(array.length));
							xmlProperty.appendChild(document.createTextNode(encodeIntArray(array, false)));
							// for( int i=0; i<array.length; i++ ) {
							// org.w3c.dom.Element itemNode =
							// document.createElement( "item" );
							// itemNode.appendChild( document.createTextNode(
							// Integer.toString( array[ i ] ) ) );
							// xmlProperty.appendChild( itemNode );
							// }
						} else if (double[].class.isAssignableFrom(propertyValueClass)) {
							final double[] array = (double[]) value;
							xmlProperty.setAttribute("class", "[D");
							xmlProperty.setAttribute("length", Integer.toString(array.length));
							xmlProperty.appendChild(document.createTextNode(encodeDoubleArray(array)));
							// for( int i=0; i<array.length; i++ ) {
							// org.w3c.dom.Element itemNode =
							// document.createElement( "item" );
							// itemNode.appendChild( document.createTextNode(
							// Double.toString( array[ i ] ) ) );
							// xmlProperty.appendChild( itemNode );
							// }
							// itemNode.appendChild( );
						} else if (edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[].class
								.isAssignableFrom(propertyValueClass)) {
							xmlProperty.setAttribute("class", "[Ledu.cmu.cs.stage3.alice.scenegraph.Vertex3d;");
							final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] array = (edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[]) value;
							for (final Vertex3d vertex : array) {
								final org.w3c.dom.Element xmlVertex = document.createElement("vertex");
								if (vertex.position != null) {
									final org.w3c.dom.Element xmlPosition = document.createElement("position");
									xmlPosition.appendChild(document.createTextNode(encodeTuple3d(vertex.position)));
									xmlVertex.appendChild(xmlPosition);
									// org.w3c.dom.Element xNode =
									// document.createElement( "x" );
									// xNode.appendChild(
									// document.createTextNode( Double.toString(
									// vertex.position.x ) ) );
									// positionNode.appendChild( xNode );
									// org.w3c.dom.Element yNode =
									// document.createElement( "y" );
									// yNode.appendChild(
									// document.createTextNode( Double.toString(
									// vertex.position.y ) ) );
									// positionNode.appendChild( yNode );
									// org.w3c.dom.Element zNode =
									// document.createElement( "z" );
									// zNode.appendChild(
									// document.createTextNode( Double.toString(
									// vertex.position.z ) ) );
									// positionNode.appendChild( zNode );
								}
								if (vertex.normal != null) {
									final org.w3c.dom.Element xmlNormal = document.createElement("normal");
									xmlNormal.appendChild(document.createTextNode(encodeTuple3d(vertex.normal)));
									xmlVertex.appendChild(xmlNormal);
									// org.w3c.dom.Element normalNode =
									// document.createElement( "normal" );
									// org.w3c.dom.Element xNode =
									// document.createElement( "x" );
									// xNode.appendChild(
									// document.createTextNode( Double.toString(
									// vertex.normal.x ) ) );
									// normalNode.appendChild( xNode );
									// org.w3c.dom.Element yNode =
									// document.createElement( "y" );
									// yNode.appendChild(
									// document.createTextNode( Double.toString(
									// vertex.normal.y ) ) );
									// normalNode.appendChild( yNode );
									// org.w3c.dom.Element zNode =
									// document.createElement( "z" );
									// zNode.appendChild(
									// document.createTextNode( Double.toString(
									// vertex.normal.z ) ) );
									// normalNode.appendChild( zNode );
									// vertexNode.appendChild( normalNode );
								}
								if (vertex.textureCoordinate0 != null) {
									final org.w3c.dom.Element xmlTextureCoordinate0 = document
											.createElement("textureCoordinate0");
									xmlTextureCoordinate0.appendChild(
											document.createTextNode(encodeTuple2f(vertex.textureCoordinate0)));
									xmlVertex.appendChild(xmlTextureCoordinate0);
									// org.w3c.dom.Element
									// textureCoordinate0Node =
									// document.createElement(
									// "textureCoordinate0" );
									// org.w3c.dom.Element xNode =
									// document.createElement( "x" );
									// xNode.appendChild(
									// document.createTextNode( Double.toString(
									// vertex.textureCoordinate0.x ) ) );
									// textureCoordinate0Node.appendChild( xNode
									// );
									// org.w3c.dom.Element yNode =
									// document.createElement( "y" );
									// yNode.appendChild(
									// document.createTextNode( Double.toString(
									// vertex.textureCoordinate0.y ) ) );
									// textureCoordinate0Node.appendChild( yNode
									// );
									// vertexNode.appendChild(
									// textureCoordinate0Node );
								}
								xmlProperty.appendChild(xmlVertex);
							}
						} else if (edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom(propertyValueClass)) {
							xmlProperty.setAttribute("class", propertyValueClass.getName());
							xmlProperty.appendChild(document
									.createTextNode(encodeEnumerable((edu.cmu.cs.stage3.util.Enumerable) value)));
						} else {
							xmlProperty.setAttribute("class", propertyValueClass.getName());
							xmlProperty.appendChild(document.createTextNode(value.toString()));
						}
					}
				}
				xmlElement.appendChild(xmlProperty);
			}
		}
		return xmlElement;
	}

	private static org.w3c.dom.Element encodeComponent(final edu.cmu.cs.stage3.alice.scenegraph.Component component,
			final org.w3c.dom.Document document, final String s, final java.util.Dictionary<String, Object> map) {
		final org.w3c.dom.Element xmlComponent = encodeElement(component, document, s, map);
		if (component instanceof edu.cmu.cs.stage3.alice.scenegraph.Container) {
			final edu.cmu.cs.stage3.alice.scenegraph.Container container = (edu.cmu.cs.stage3.alice.scenegraph.Container) component;
			for (int i = 0; i < container.getChildCount(); i++) {
				xmlComponent.appendChild(encodeComponent(container.getChildAt(i), document, "child", map));
			}
		}
		return xmlComponent;
	}

	private static void internalStore(final edu.cmu.cs.stage3.alice.scenegraph.Component component,
			final java.io.OutputStream os) throws java.lang.NoSuchMethodException,
			java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException,
			javax.xml.parsers.ParserConfigurationException, java.io.IOException, java.io.FileNotFoundException {
		final javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		final javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
		final org.w3c.dom.Document document = builder.newDocument();
		final java.util.Dictionary<String, Object> elementsToBeEncoded = new java.util.Hashtable<String, Object>();
		final org.w3c.dom.Element rootNode = encodeComponent(component, document, "root", elementsToBeEncoded);
		rootNode.setAttribute("version", Double.toString(VERSION));

		final java.util.Dictionary<Object, edu.cmu.cs.stage3.alice.scenegraph.Element> elementsAlreadyEncoded = new java.util.Hashtable<Object, edu.cmu.cs.stage3.alice.scenegraph.Element>();
		while (true) {
			boolean atLeastOneElementHasBeenEncoded = false;
			final java.util.Enumeration<String> enum0 = elementsToBeEncoded.keys();
			while (enum0.hasMoreElements()) {
				final Object key = enum0.nextElement();
				if (elementsAlreadyEncoded.get(key) != null) {
					// pass
				} else {
					final edu.cmu.cs.stage3.alice.scenegraph.Element element = (edu.cmu.cs.stage3.alice.scenegraph.Element) elementsToBeEncoded
							.get(key);
					rootNode.appendChild(encodeElement(element, document, "element", elementsToBeEncoded));
					elementsAlreadyEncoded.put(key, element);
					atLeastOneElementHasBeenEncoded = true;
				}
			}
			if (atLeastOneElementHasBeenEncoded) {
				continue;
			} else {
				break;
			}
		}

		document.appendChild(rootNode);
		document.getDocumentElement().normalize();

		final Class<? extends Document> cls = document.getClass();
		final Class<?>[] parameterTypes = { java.io.OutputStream.class };
		final Object[] args = { os };
		final java.lang.reflect.Method method = cls.getMethod("write", parameterTypes);
		method.invoke(document, args);
	}

	public static void store(final edu.cmu.cs.stage3.alice.scenegraph.Component component,
			final java.io.OutputStream os) {
		try {
			final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
			internalStore(component, baos);
			baos.flush();
			final byte[] ba = baos.toByteArray();

			java.util.zip.ZipOutputStream zos;
			if (os instanceof java.util.zip.ZipOutputStream) {
				zos = (java.util.zip.ZipOutputStream) os;
			} else {
				zos = new java.util.zip.ZipOutputStream(os);
			}
			final java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry("root.xml");
			zos.setMethod(java.util.zip.ZipOutputStream.DEFLATED);
			zos.putNextEntry(zipEntry);
			zos.write(ba, 0, ba.length);
			zos.closeEntry();
			zos.flush();
			zos.finish();
		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}

	public static void store(final edu.cmu.cs.stage3.alice.scenegraph.Component component, final java.io.File file) {
		try {
			final java.io.OutputStream os = new java.io.FileOutputStream(file);
			store(component, os);
			os.close();
		} catch (final java.io.IOException ioe) {
			// } catch( java.io.FileNotFoundException fnfe ) {
			ioe.printStackTrace();
		}
	}

	public static void store(final edu.cmu.cs.stage3.alice.scenegraph.Component component, final String path) {
		store(component, new java.io.File(path));
	}

	private static org.w3c.dom.Element getFirstChild(final org.w3c.dom.Node node, final String tag) {
		org.w3c.dom.Node childNode = node.getFirstChild();
		while (childNode != null) {
			if (childNode instanceof org.w3c.dom.Element) {
				if (childNode.getNodeName().equals(tag)) {
					return (org.w3c.dom.Element) childNode;
				}
			}
			childNode = childNode.getNextSibling();
		}
		return null;
	}

	private static org.w3c.dom.Element[] getChildren(final org.w3c.dom.Node node, final String tag) {
		final java.util.Vector<Node> vector = new java.util.Vector<Node>();
		org.w3c.dom.Node childNode = node.getFirstChild();
		while (childNode != null) {
			if (childNode instanceof org.w3c.dom.Element) {
				if (childNode.getNodeName().equals(tag)) {
					vector.addElement(childNode);
				}
			}
			childNode = childNode.getNextSibling();
		}
		final org.w3c.dom.Element[] array = new org.w3c.dom.Element[vector.size()];
		vector.copyInto(array);
		return array;
	}

	private static String getNodeText(final org.w3c.dom.Node node) {
		// return node.getFirstChild().getNodeValue().trim();
		// System.err.println(
		// ((org.w3c.dom.Text)node.getFirstChild()).getData() );
		// System.err.println( node.getFirstChild().getNodeValue() );
		// return ((org.w3c.dom.Text)node.getFirstChild()).getData();
		final StringBuffer propertyTextBuffer = new StringBuffer();
		final org.w3c.dom.NodeList children = node.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			final org.w3c.dom.Text textNode = (org.w3c.dom.Text) children.item(j);
			propertyTextBuffer.append(textNode.getData().trim());
		}
		return propertyTextBuffer.toString();
	}

	private static Object valueOf(final Class<?> cls, final String text) {
		if (String.class.isAssignableFrom(cls)) {
			return text;
		} else if (cls.equals(Double.class) && text.equals("Infinity")) {
			return new Double(Double.POSITIVE_INFINITY);
		} else if (cls.equals(Double.class) && text.equals("NaN")) {
			return new Double(Double.NaN);
		} else {
			final Class<?>[] parameterTypes = { String.class };
			try {
				final java.lang.reflect.Method valueOfMethod = cls.getMethod("valueOf", parameterTypes);
				final int modifiers = valueOfMethod.getModifiers();
				if (java.lang.reflect.Modifier.isPublic(modifiers) && java.lang.reflect.Modifier.isStatic(modifiers)) {
					final Object[] parameters = { text };
					return valueOfMethod.invoke(null, parameters);
				} else {
					throw new RuntimeException("valueOf method not public static.");
				}
			} catch (final NoSuchMethodException nsme) {
				throw new RuntimeException("NoSuchMethodException:" + cls + " " + text);
			} catch (final IllegalAccessException iae) {
				throw new RuntimeException("IllegalAccessException: " + cls + " " + text);
			} catch (final java.lang.reflect.InvocationTargetException ite) {
				throw new RuntimeException("java.lang.reflect.InvocationTargetException: " + cls + " " + text);
			}
		}
	}

	private static edu.cmu.cs.stage3.alice.scenegraph.Element decodeElement(final org.w3c.dom.Element xmlElement,
			final java.util.Dictionary<String, edu.cmu.cs.stage3.alice.scenegraph.Element> map, final java.util.Vector<PropertyReference> referencesToBeResolved) {
		try {
			final String classname = xmlElement.getAttribute("class");
			final String elementKey = xmlElement.getAttribute("key");
			final String elementName = xmlElement.getAttribute("name");
			final Class<?> cls = Class.forName(classname);
			final edu.cmu.cs.stage3.alice.scenegraph.Element sgElement = (edu.cmu.cs.stage3.alice.scenegraph.Element) cls
					.newInstance();
			sgElement.setName(elementName);
			map.put(elementKey, sgElement);
			final org.w3c.dom.Element[] xmlProperties = getChildren(xmlElement, "property");
			for (final Element xmlProperty : xmlProperties) {
				final String propertyName = xmlProperty.getAttribute("name");
				final edu.cmu.cs.stage3.alice.scenegraph.Property sgProperty = edu.cmu.cs.stage3.alice.scenegraph.Property
						.getPropertyMixedCaseNamed(cls, propertyName);
				if (sgProperty == null) {
					throw new RuntimeException("could not find property named: " + propertyName + " on " + cls + ".");
				}
				final String propertyValueClassname = xmlProperty.getAttribute("class");
				if (propertyValueClassname.length() > 0) {
					final Class<?> propertyValueClass = Class.forName(propertyValueClassname);
					Object value;
					if (javax.vecmath.Matrix4d.class.isAssignableFrom(propertyValueClass)) {
						final javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
						final org.w3c.dom.Element[] xmlRows = getChildren(xmlProperty, "row");
						final double[] row = new double[4];
						for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
							// org.w3c.dom.Element[] xmlItems = getChildren(
							// xmlRows[ rowIndex ], "item" );
							// for( int colIndex=0; colIndex<4; colIndex++ ) {
							// row[ colIndex ] = Double.parseDouble(
							// getNodeText( xmlItems[ colIndex ] ) );
							// }
							decodeDoubleArray(getNodeText(xmlRows[rowIndex]), row);
							m.setRow(rowIndex, row);
						}
						value = m;
					} else if (javax.vecmath.Matrix3d.class.isAssignableFrom(propertyValueClass)) {
						final javax.vecmath.Matrix3d m = new javax.vecmath.Matrix3d();
						final org.w3c.dom.Element[] xmlRows = getChildren(xmlProperty, "row");
						final double[] row = new double[3];
						for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
							// org.w3c.dom.Element[] xmlItems = getChildren(
							// xmlRows[ rowIndex ], "item" );
							// for( int colIndex=0; colIndex<3; colIndex++ ) {
							// row[ colIndex ] = Double.parseDouble(
							// getNodeText( xmlItems[ colIndex ] ) );
							// }
							decodeDoubleArray(getNodeText(xmlRows[rowIndex]), row);
							m.setRow(rowIndex, row);
						}
						value = m;
					} else if (java.awt.Image.class.isAssignableFrom(propertyValueClass)) {
						final int width = Integer.parseInt(xmlProperty.getAttribute("width"));
						final int height = Integer.parseInt(xmlProperty.getAttribute("height"));
						final org.w3c.dom.Element[] xmlRows = getChildren(xmlProperty, "row");
						final int[] pixels = new int[width * height];
						int pixelIndex = 0;
						for (int rowIndex = 0; rowIndex < height; rowIndex++) {
							final String s = getNodeText(xmlRows[rowIndex]);
							decodeIntArray(s, pixels, pixelIndex, width, true);
							pixelIndex += width;
						}
						/*
						 * int width = getChildren( xmlRows[ 0 ], "item"
						 * ).length; int height = xmlRows.length; int[] pixels =
						 * new int[ width * height ]; int index = 0; for( int
						 * rowIndex=0; rowIndex<height; rowIndex++ ) {
						 * org.w3c.dom.Element[] xmlItems = getChildren(
						 * xmlRows[ rowIndex ], "item" ); for( int colIndex=0;
						 * colIndex<width; colIndex++ ) { //todo: what the heck?
						 * Integer.parseInt throws
						 * java.lang.NumberFormatException //pixels[index++] =
						 * Integer.parseInt( getNodeText( xmlItems[ colIndex ]
						 * ), 16 ); pixels[index++] = (int)Long.parseLong(
						 * getNodeText( xmlItems[ colIndex ] ), 16 ); } }
						 */
						value = java.awt.Toolkit.getDefaultToolkit()
								.createImage(new java.awt.image.MemoryImageSource(width, height, pixels, 0, width));
					} else if (edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom(propertyValueClass)) {
						final edu.cmu.cs.stage3.alice.scenegraph.Color sgColor = new edu.cmu.cs.stage3.alice.scenegraph.Color();
						sgColor.red = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "red")));
						sgColor.green = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "green")));
						sgColor.blue = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "blue")));
						sgColor.alpha = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "alpha")));
						value = sgColor;
					} else if (int[].class.isAssignableFrom(propertyValueClass)) {
						final int length = Integer.parseInt(xmlProperty.getAttribute("length"));
						final int[] array = new int[length];
						decodeIntArray(getNodeText(xmlProperty), array, false);
						// org.w3c.dom.Element[] xmlItems = getChildren(
						// xmlProperty, "item" );
						// int[] array = new int[ xmlItems.length ];
						// for( int itemIndex=0; itemIndex<xmlItems.length;
						// itemIndex++ ) {
						// array[ itemIndex ] = Integer.parseInt( getNodeText(
						// xmlItems[ itemIndex ] ) );
						// }
						value = array;
					} else if (double[].class.isAssignableFrom(propertyValueClass)) {
						final int length = Integer.parseInt(xmlProperty.getAttribute("length"));
						final double[] array = new double[length];
						decodeDoubleArray(getNodeText(xmlProperty), array);
						// org.w3c.dom.Element[] xmlItems = getChildren(
						// xmlProperty, "item" );
						// double[] array = new double[ xmlItems.length ];
						// for( int itemIndex=0; itemIndex<xmlItems.length;
						// itemIndex++ ) {
						// array[ itemIndex ] = Double.parseDouble( getNodeText(
						// xmlItems[ itemIndex ] ) );
						// }
						value = array;
					} else if (edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[].class
							.isAssignableFrom(propertyValueClass)) {
						final org.w3c.dom.Element[] xmlVertices = getChildren(xmlProperty, "vertex");
						final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] array = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[xmlVertices.length];
						for (int vertexIndex = 0; vertexIndex < xmlVertices.length; vertexIndex++) {
							final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d vertex = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d();
							final org.w3c.dom.Element xmlVertex = xmlVertices[vertexIndex];
							final org.w3c.dom.Element xmlPosition = getFirstChild(xmlVertex, "position");
							if (xmlPosition != null) {
								vertex.position = new javax.vecmath.Point3d();
								decodeTuple3d(getNodeText(xmlPosition), vertex.position);
								// array[ itemIndex ].position.x =
								// Double.parseDouble( getNodeText(
								// getFirstChild( xmlPosition, "x" ) ) );
								// array[ itemIndex ].position.y =
								// Double.parseDouble( getNodeText(
								// getFirstChild( xmlPosition, "y" ) ) );
								// array[ itemIndex ].position.z =
								// Double.parseDouble( getNodeText(
								// getFirstChild( xmlPosition, "z" ) ) );
							}

							final org.w3c.dom.Element xmlNormal = getFirstChild(xmlVertex, "normal");
							if (xmlNormal != null) {
								vertex.normal = new javax.vecmath.Vector3d();
								decodeTuple3d(getNodeText(xmlNormal), vertex.normal);
								// array[ itemIndex ].normal.x =
								// Double.parseDouble( getNodeText(
								// getFirstChild( xmlNormal, "x" ) ) );
								// array[ itemIndex ].normal.y =
								// Double.parseDouble( getNodeText(
								// getFirstChild( xmlNormal, "y" ) ) );
								// array[ itemIndex ].normal.z =
								// Double.parseDouble( getNodeText(
								// getFirstChild( xmlNormal, "z" ) ) );
							}

							final org.w3c.dom.Element xmlTextureCoordinate0 = getFirstChild(xmlVertex,
									"textureCoordinate0");
							if (xmlTextureCoordinate0 != null) {
								vertex.textureCoordinate0 = new javax.vecmath.TexCoord2f();
								decodeTuple2f(getNodeText(xmlTextureCoordinate0), vertex.textureCoordinate0);
								// array[ itemIndex ].textureCoordinate0.x =
								// Float.parseFloat( getNodeText( getFirstChild(
								// xmlTextureCoordinate0, "x" ) ) );
								// array[ itemIndex ].textureCoordinate0.y =
								// Float.parseFloat( getNodeText( getFirstChild(
								// xmlTextureCoordinate0, "y" ) ) );
							}
							array[vertexIndex] = vertex;
						}
						value = array;
					} else if (edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom(propertyValueClass)) {
						value = decodeEnumerable(propertyValueClass, getNodeText(xmlProperty));
					} else {
						value = valueOf(propertyValueClass, getNodeText(xmlProperty));
					}
					sgProperty.set(sgElement, value);
				} else {
					final String propertyValueKey = xmlProperty.getAttribute("key");
					if (propertyValueKey.length() > 0) {
						referencesToBeResolved
								.addElement(new PropertyReference(sgProperty, sgElement, propertyValueKey));
					} else {
						sgProperty.set(sgElement, null);
					}
				}
			}
			return sgElement;
		} catch (final Throwable t) {
			// throw new RuntimeException();
			t.printStackTrace();
			System.exit(0);
			return null;
		}
	}

	private static edu.cmu.cs.stage3.alice.scenegraph.Component decodeComponent(final org.w3c.dom.Element xmlComponent,
			final java.util.Dictionary<String, edu.cmu.cs.stage3.alice.scenegraph.Element> map, final java.util.Vector<PropertyReference> referencesToBeResolved) {
		final edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = (edu.cmu.cs.stage3.alice.scenegraph.Component) decodeElement(
				xmlComponent, map, referencesToBeResolved);
		final org.w3c.dom.Element[] xmlChildren = getChildren(xmlComponent, "child");
		for (final Element element : xmlChildren) {
			decodeComponent(element, map, referencesToBeResolved)
					.setParent((edu.cmu.cs.stage3.alice.scenegraph.Container) sgComponent);
		}
		return sgComponent;
	}

	private static edu.cmu.cs.stage3.alice.scenegraph.Component internalLoad(final java.io.InputStream is)
			throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
		final javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		final javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
		final org.w3c.dom.Document document = builder.parse(is);
		final org.w3c.dom.Element xmlRoot = document.getDocumentElement();
		// double version = Double.parseDouble( elementNode.getAttribute(
		// "version" ) );
		final java.util.Dictionary<String, edu.cmu.cs.stage3.alice.scenegraph.Element> map = new java.util.Hashtable<String, edu.cmu.cs.stage3.alice.scenegraph.Element>();
		final java.util.Vector<PropertyReference> referencesToBeResolved = new java.util.Vector<PropertyReference>();
		final edu.cmu.cs.stage3.alice.scenegraph.Component sgRoot = decodeComponent(xmlRoot, map,
				referencesToBeResolved);

		final org.w3c.dom.Element[] xmlElements = getChildren(xmlRoot, "element");
		for (final Element xmlElement : xmlElements) {
			decodeElement(xmlElement, map, referencesToBeResolved);
		}

		final java.util.Enumeration<PropertyReference> enum0 = referencesToBeResolved.elements();
		while (enum0.hasMoreElements()) {
			final PropertyReference propertyReference = enum0.nextElement();
			propertyReference.resolve(map);
		}
		return sgRoot;
	}

	public static edu.cmu.cs.stage3.alice.scenegraph.Component load(final java.io.InputStream is) {
		try {
			java.util.zip.ZipInputStream zis;
			if (is instanceof java.util.zip.ZipInputStream) {
				zis = (java.util.zip.ZipInputStream) is;
			} else {
				zis = new java.util.zip.ZipInputStream(is);
			}
			/* Unused ?? final java.util.zip.ZipEntry zipEntry = */ zis.getNextEntry(); // TODO: check to ensure it is root.xml

			final int BUFFER_SIZE = 2048;
			final byte[] buffer = new byte[BUFFER_SIZE];
			final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream(BUFFER_SIZE);
			while (true) {
				final int count = zis.read(buffer, 0, BUFFER_SIZE);
				if (count == -1) {
					break;
				} else {
					baos.write(buffer, 0, count);
				}
			}
			final java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
			return internalLoad(bais);
			// return internalLoad( new java.io.BufferedInputStream( is ) );
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t.toString());
		}
	}

	public static edu.cmu.cs.stage3.alice.scenegraph.Component load(final java.io.File file) {
		try {
			return load(new java.io.FileInputStream(file));
		} catch (final java.io.FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return null;
		}
	}
}