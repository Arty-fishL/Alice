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

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.reference.ObjectArrayPropertyReference;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;

public class ObjectArrayProperty extends ObjectProperty {
	private Object[] m_arrayValueIfNull = null;
	private final java.util.Vector<ObjectArrayPropertyListener> m_objectArrayPropertyListeners = new java.util.Vector<ObjectArrayPropertyListener>();
	private ObjectArrayPropertyListener[] m_objectArrayPropertyListenerArray = null;

	public ObjectArrayProperty(final Element owner, final String name, final Object[] defaultValue,
			final Class<?> valueClass) {
		super(owner, name, defaultValue, valueClass);
	}

	public void addObjectArrayPropertyListener(final ObjectArrayPropertyListener objectArrayPropertyListener) {
		if (m_objectArrayPropertyListeners.contains(objectArrayPropertyListener)) {
			// edu.cmu.cs.stage3.alice.core.Element.warnln( "WARNING: " + this +
			// " already has objectArrayPropertyListener " +
			// objectArrayPropertyListener +
			// "(class="+objectArrayPropertyListener.getClass()+"). NOT added
			// again."
			// );
		} else {
			m_objectArrayPropertyListeners.addElement(objectArrayPropertyListener);
			m_objectArrayPropertyListenerArray = null;
		}
	}

	public void removeObjectArrayPropertyListener(final ObjectArrayPropertyListener objectArrayPropertyListener) {
		m_objectArrayPropertyListeners.removeElement(objectArrayPropertyListener);
		m_objectArrayPropertyListenerArray = null;
	}

	public ObjectArrayPropertyListener[] getObjectArrayPropertyListeners() {
		if (m_objectArrayPropertyListenerArray == null) {
			m_objectArrayPropertyListenerArray = new ObjectArrayPropertyListener[m_objectArrayPropertyListeners.size()];
			m_objectArrayPropertyListeners.copyInto(m_objectArrayPropertyListenerArray);
		}
		return m_objectArrayPropertyListenerArray;
	}

	public Class<?> getComponentType() {
		return getValueClass().getComponentType();
	}

	public void setComponentType(final Class<?> componentType) {
		final Object value = get();
		Object[] prevArray = null;
		int length = 0;
		if (value != null) {
			if (value instanceof Object[]) {
				prevArray = (Object[]) value;
				length = prevArray.length;
				// } else {
				// todo: handle expressions?
			}
		}
		final Object[] currArray = (Object[]) java.lang.reflect.Array.newInstance(componentType, length);
		setValueClass(currArray.getClass());
		if (value != null) {
			System.arraycopy(prevArray, 0, currArray, 0, length);
			set(currArray);
		}
	}

	public Object[] getArrayValue() {
		final Object[] value = (Object[]) getValue();
		if (value != null) {
			return value;
		} else {
			if (m_arrayValueIfNull == null) {
				m_arrayValueIfNull = (Object[]) java.lang.reflect.Array.newInstance(getComponentType(), 0);
			}
			return m_arrayValueIfNull;
		}
	}

	@Override
	protected void decodeObject(final org.w3c.dom.Element node, final edu.cmu.cs.stage3.io.DirectoryTreeLoader loader,
			final java.util.Vector<PropertyReference> referencesToBeResolved, final double version) throws java.io.IOException {
		final String componentTypeName = node.getAttribute("componentClass");
		try {
			final Class<?> arrayComponentCls = Class.forName(componentTypeName);
			final org.w3c.dom.NodeList itemNodeList = node.getElementsByTagName("item");
			final Object[] array = (Object[]) java.lang.reflect.Array.newInstance(arrayComponentCls,
					itemNodeList.getLength());
			int precedingReferenceTotal = 0;
			for (int i = 0; i < array.length; i++) {
				final org.w3c.dom.Element itemNode = (org.w3c.dom.Element) itemNodeList.item(i);
				final String criterionTypeName = itemNode.getAttribute("criterionClass");
				if (criterionTypeName.length() > 0) {
					try {
						final Class<?> criterionType = Class.forName(criterionTypeName);
						final String text = getNodeText(itemNode);
						edu.cmu.cs.stage3.util.Criterion criterion;
						if (criterionType.isAssignableFrom(
								edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion.class)) {
							criterion = new edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion(
									text);
						} else if (criterionType.isAssignableFrom(
								edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion.class)) {
							criterion = new edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion(
									text);
						} else {
							criterion = (edu.cmu.cs.stage3.util.Criterion) getValueOf(criterionType, text);
						}
						referencesToBeResolved.addElement(
								new ObjectArrayPropertyReference(this, criterion, i, precedingReferenceTotal++));
					} catch (final ClassNotFoundException cnfe) {
						throw new RuntimeException(criterionTypeName);
					}
				} else {
					final String itemTypeName = itemNode.getAttribute("class");
					if (itemTypeName.length() > 0) {
						try {
							final Class<?> itemType = Class.forName(itemTypeName);
							array[i] = getValueOf(itemType, getNodeText(itemNode));
						} catch (final ClassNotFoundException cnfe) {
							throw new RuntimeException(itemTypeName);
						}
					} else {
						array[i] = null;
					}
				}
			}
			set(array);
		} catch (final ClassNotFoundException cnfe) {
			throw new RuntimeException(componentTypeName);
		}
	}

	@Override
	protected void encodeObject(final org.w3c.dom.Document document, final org.w3c.dom.Element node,
			final edu.cmu.cs.stage3.io.DirectoryTreeStorer storer,
			final edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator) throws java.io.IOException {
		if (node == null) {
			System.err.println("node==null");
			System.err.println(this);
		}
		if (getComponentType() == null) {
			System.err.println("getComponentType()==null");
			System.err.println(this);
		}
		node.setAttribute("componentClass", getComponentType().getName());
		final Object[] array = getArrayValue();
		if (array != null && array.length > 0) {
			for (final Object item : array) {
				final org.w3c.dom.Element itemNode = document.createElement("item");
				if (item instanceof edu.cmu.cs.stage3.alice.core.Element) {
					encodeReference(document, itemNode, referenceGenerator,
							(edu.cmu.cs.stage3.alice.core.Element) item);
				} else if (item != null) {
					itemNode.setAttribute("class", item.getClass().getName());
					itemNode.appendChild(createNodeForString(document, item.toString()));
				}
				// itemNode.setAttribute( "index", Integer.toString( i ) );
				node.appendChild(itemNode);
			}
		}
	}

	private void onItemChanging(final Object item, final int changeType, final int oldIndex, final int newIndex) {
		if (!m_objectArrayPropertyListeners.isEmpty()) {
			final ObjectArrayPropertyEvent objectArrayPropertyEvent = new ObjectArrayPropertyEvent(this, item,
					changeType, oldIndex, newIndex);
			final ObjectArrayPropertyListener[] objectArrayPropertyListeners = getObjectArrayPropertyListeners();
			for (final ObjectArrayPropertyListener objectArrayPropertyListener : objectArrayPropertyListeners) {
				objectArrayPropertyListener.objectArrayPropertyChanging(objectArrayPropertyEvent);
			}
			/*
			 * java.util.Enumeration enum0 =
			 * m_objectArrayPropertyListeners.elements(); while(
			 * enum0.hasMoreElements() ) { ObjectArrayPropertyListener
			 * objectArrayPropertyListener =
			 * (ObjectArrayPropertyListener)enum0.nextElement();
			 * objectArrayPropertyListener.objectArrayPropertyChanging(
			 * objectArrayPropertyEvent ); }
			 */
		}
	}

	private void onItemChanged(final Object item, final int changeType, final int oldIndex, final int newIndex) {
		if (!m_objectArrayPropertyListeners.isEmpty()) {
			final ObjectArrayPropertyEvent objectArrayPropertyEvent = new ObjectArrayPropertyEvent(this, item,
					changeType, oldIndex, newIndex);
			final ObjectArrayPropertyListener[] objectArrayPropertyListeners = getObjectArrayPropertyListeners();
			for (final ObjectArrayPropertyListener objectArrayPropertyListener : objectArrayPropertyListeners) {
				objectArrayPropertyListener.objectArrayPropertyChanged(objectArrayPropertyEvent);
			}
			/*
			 * java.util.Enumeration enum0 =
			 * m_objectArrayPropertyListeners.elements(); while(
			 * enum0.hasMoreElements() ) { ObjectArrayPropertyListener
			 * objectArrayPropertyListener =
			 * (ObjectArrayPropertyListener)enum0.nextElement();
			 * objectArrayPropertyListener.objectArrayPropertyChanged(
			 * objectArrayPropertyEvent ); }
			 */
		}
	}

	public void add(final int index, final Object o) {
		if (index == -1) {
			add(o);
		} else {
			final Object[] prev = getArrayValue();
			Object[] curr;
			if (prev == null) {
				if (index == 0) {
					add(o);
				} else {
					// todo
					throw new RuntimeException();
				}
			} else {
				onItemChanging(o, ObjectArrayPropertyEvent.ITEM_INSERTED, -1, index);
				final int n = prev.length;
				curr = (Object[]) java.lang.reflect.Array.newInstance(getComponentType(), n + 1);
				if (index > 0) {
					System.arraycopy(prev, 0, curr, 0, index);
				}
				if (index < n) {
					System.arraycopy(prev, index, curr, index + 1, n - index);
				}
				curr[index] = o;
				set(curr);
				onItemChanged(o, ObjectArrayPropertyEvent.ITEM_INSERTED, -1, index);
			}
		}
	}

	public void addValue(final int index, final Object o) {
		add(index, evaluateIfNecessary(o));
	}

	public void add(final Object o) {
		final Object[] prev = getArrayValue();
		Object[] curr;
		int index;
		if (prev == null) {
			index = 0;
		} else {
			index = prev.length;
		}
		onItemChanging(o, ObjectArrayPropertyEvent.ITEM_INSERTED, -1, index);
		if (prev == null) {
			curr = (Object[]) java.lang.reflect.Array.newInstance(getComponentType(), 1);
		} else {
			curr = (Object[]) java.lang.reflect.Array.newInstance(getComponentType(), index + 1);
			System.arraycopy(prev, 0, curr, 0, index);
		}
		curr[index] = o;
		set(curr);
		onItemChanged(o, ObjectArrayPropertyEvent.ITEM_INSERTED, -1, index);
	}

	public void addValue(final Object o) {
		add(evaluateIfNecessary(o));
	}

	public void remove(final int index) {
		final Object[] prev = getArrayValue();
		onItemChanging(prev[index], ObjectArrayPropertyEvent.ITEM_REMOVED, index, -1);
		final int n = prev.length;
		final Object[] curr = (Object[]) java.lang.reflect.Array.newInstance(getComponentType(), n - 1);
		if (index > 0) {
			System.arraycopy(prev, 0, curr, 0, index);
		}
		if (index < n - 1) {
			System.arraycopy(prev, index + 1, curr, index, n - 1 - index);
		}
		set(curr);
		onItemChanged(prev[index], ObjectArrayPropertyEvent.ITEM_REMOVED, index, -1);
	}

	public void remove(final Object o) {
		final Object[] prev = getArrayValue();
		if (prev != null) {
			final int n = prev.length;
			for (int i = 0; i < n; i++) {
				if (prev[i] == o) {
					remove(i);
					break;
				}
			}
		}
	}

	public void removeValue(final Object o) {
		remove(evaluateIfNecessary(o));
	}

	public void set(final int index, Object o) {
		ensureCapacity(index + 1);
		final int n = size();
		if (index >= 0 && index < n) {
			final Object[] prev = getArrayValue();
			onItemChanging(prev[index], ObjectArrayPropertyEvent.ITEM_REMOVED, index, -1);
			onItemChanging(o, ObjectArrayPropertyEvent.ITEM_INSERTED, -1, index);
			final Object[] curr = (Object[]) java.lang.reflect.Array.newInstance(getComponentType(), n);
			System.arraycopy(prev, 0, curr, 0, n);
			if (o instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				if (!edu.cmu.cs.stage3.alice.core.Expression.class.isAssignableFrom(getComponentType())) {
					o = ((edu.cmu.cs.stage3.alice.core.Expression) o).getValue();
				}
			}
			curr[index] = o;
			set(curr);
			onItemChanged(prev[index], ObjectArrayPropertyEvent.ITEM_REMOVED, index, -1);
			onItemChanged(o, ObjectArrayPropertyEvent.ITEM_INSERTED, -1, index);
		} else {
			throw new ArrayIndexOutOfBoundsException("index " + index + " is out of bounds [ 0, " + n + ")");
		}
	}

	public void setValue(final int index, final Object o) {
		set(index, evaluateIfNecessary(o));
	}

	public void clear() {
		final Object[] prev = getArrayValue();
		for (int i = 0; i < prev.length; i++) {
			onItemChanging(prev[i], ObjectArrayPropertyEvent.ITEM_REMOVED, i, -1);
		}
		set(java.lang.reflect.Array.newInstance(getComponentType(), 0));
		for (int i = 0; i < prev.length; i++) {
			onItemChanged(prev[i], ObjectArrayPropertyEvent.ITEM_REMOVED, i, -1);
		}
	}

	public void shift(final int fromIndex, final int toIndex) {
		if (fromIndex != toIndex) {
			final Object[] prev = getArrayValue();

			onItemChanging(prev[fromIndex], ObjectArrayPropertyEvent.ITEM_SHIFTED, fromIndex, toIndex);

			final Object[] curr = (Object[]) java.lang.reflect.Array.newInstance(getComponentType(), prev.length);
			if (fromIndex < toIndex) {
				for (int i = 0; i < fromIndex; i++) {
					curr[i] = prev[i];
				}
				for (int i = fromIndex; i < toIndex; i++) {
					curr[i] = prev[i + 1];
				}
				curr[toIndex] = prev[fromIndex];
				for (int i = toIndex + 1; i < prev.length; i++) {
					curr[i] = prev[i];
				}
			} else {
				for (int i = 0; i < toIndex; i++) {
					curr[i] = prev[i];
				}
				curr[toIndex] = prev[fromIndex];
				for (int i = toIndex + 1; i < fromIndex + 1; i++) {
					curr[i] = prev[i - 1];
				}
				for (int i = fromIndex + 1; i < prev.length; i++) {
					curr[i] = prev[i];
				}
			}
			set(curr);
			onItemChanged(prev[fromIndex], ObjectArrayPropertyEvent.ITEM_SHIFTED, fromIndex, toIndex);
		}
	}

	public Object get(int index) {
		final int n = size();
		if (index == -1) {
			index = n;
		}
		if (index >= 0 && index < n) {
			final Object[] array = getArrayValue();
			return array[index];
		} else {
			return null;
		}
	}

	public Object getValue(final int index) {
		return evaluateIfNecessary(get(index));
	}

	private static boolean areEqual(final Object a, final Object b, final boolean evaluateExpressionIfNecessary) {
		if (evaluateExpressionIfNecessary) {
			if (a == null) {
				return b == null;
			} else {
				if (a.equals(b)) {
					return true;
				} else if (a instanceof edu.cmu.cs.stage3.alice.core.Expression) {
					final edu.cmu.cs.stage3.alice.core.Expression e = (edu.cmu.cs.stage3.alice.core.Expression) a;
					final Object v = e.getValue();
					if (v == null) {
						return b == null;
					} else {
						return v.equals(b);
					}
				} else {
					return false;
				}
			}
		} else {
			return a == b;
		}
	}

	private int indexOf(final Object o, final int index, final boolean evaluateExpressionIfNecessary) {
		final Object[] array = getArrayValue();
		if (array != null) {
			for (int i = index; i < array.length; i++) {
				if (areEqual(o, array[i], evaluateExpressionIfNecessary)) {
					return i;
				}
			}
		}
		return -1;
	}

	public int indexOf(final Object o, final int index) {
		return indexOf(o, index, false);
	}

	public int indexOf(final Object o) {
		return indexOf(o, 0);
	}

	public int indexOfValue(final Object o, final int index) {
		return indexOf(o, index, true);
	}

	public int indexOfValue(final Object o) {
		return indexOfValue(o, size() - 1);
	}

	private int lastIndexOf(final Object o, final int index, final boolean evaluateExpressionIfNecessary) {
		final Object[] array = getArrayValue();
		if (array != null) {
			for (int i = index; i >= 0; i--) {
				if (areEqual(o, array[i], evaluateExpressionIfNecessary)) {
					return i;
				}
			}
		}
		return -1;
	}

	public int lastIndexOf(final Object o, final int index) {
		return lastIndexOf(o, index, false);
	}

	public int lastIndexOf(final Object o) {
		return lastIndexOf(o, size() - 1);
	}

	public int lastIndexOfValue(final Object o, final int index) {
		return lastIndexOf(o, index, true);
	}

	public int lastIndexOfValue(final Object o) {
		return lastIndexOfValue(o, size() - 1);
	}

	public boolean contains(final Object o) {
		return indexOf(o) != -1;
	}

	public boolean containsValue(final Object o) {
		return indexOfValue(o) != -1;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		final Object[] value = getArrayValue();
		if (value != null) {
			return value.length;
		} else {
			return 0;
		}
	}

	public void ensureCapacity(final int minCapacity) {
		final Object[] prev = getArrayValue();
		if (prev.length < minCapacity) {
			final Object[] curr = (Object[]) java.lang.reflect.Array.newInstance(getComponentType(), minCapacity);
			System.arraycopy(prev, 0, curr, 0, prev.length);
			set(curr);
		}
	}
}
