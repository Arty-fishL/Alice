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

package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.PropertyListener;
import edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseListener;

/**
 * @author Dennis Cosgrove
 */
public abstract class Element {
	private static boolean s_printWarnings = false;
	static {
		try {
			s_printWarnings = Boolean.getBoolean("alice.printWarnings");
		} catch (final Throwable t) {
			s_printWarnings = false;
		}
	}

	public static void warn(final Object o) {
		if (s_printWarnings) {
			System.err.print(o);
		}
	}

	public static void warnln(final Object o) {
		if (s_printWarnings) {
			System.err.println(o);
		}
	}

	public static void warnln() {
		if (s_printWarnings) {
			System.err.println();
		}
	}

	public static final Property BONUS_PROPERTY = new Property(Element.class, "BONUS");
	public static final Property NAME_PROPERTY = new Property(Element.class, "NAME");
	private Object m_bonus = null;
	private String m_name = null;
	private java.util.Vector<Object> m_releaseListeners = new java.util.Vector<Object>();
	private ReleaseListener[] m_releaseListenerArray = null;
	private java.util.Vector<Object> m_propertyListeners = new java.util.Vector<Object>();
	private PropertyListener[] m_propertyListenerArray = null;
	private boolean m_isReleased = false;

	public boolean isReleased() {
		return m_isReleased;
	}

	@Override
	protected void finalize() throws Throwable {
		release();
		super.finalize();
	}

	protected void releasePass1() {
	}

	protected void releasePass2() {
		m_bonus = null;
		m_name = null;
	}

	protected void releasePass3() {
		java.util.Enumeration<Object> enum0;
		enum0 = m_propertyListeners.elements();
		while (enum0.hasMoreElements()) {
			final PropertyListener propertyListener = (PropertyListener) enum0.nextElement();
			warnln("WARNING: released element " + this + " still has propertyListener " + propertyListener + ".");
		}
		m_propertyListeners = null;
		m_propertyListenerArray = null;

		enum0 = m_releaseListeners.elements();
		while (enum0.hasMoreElements()) {
			final ReleaseListener releaseListener = (ReleaseListener) enum0.nextElement();
			warnln("WARNING: released element " + this + " still has releaseListener " + releaseListener + ".");
		}
		m_releaseListeners = null;
		m_releaseListenerArray = null;
	}

	public synchronized void release() {
		if (!m_isReleased) {
			final ReleaseEvent releaseEvent = new ReleaseEvent(this);
			final ReleaseListener[] releaseListenersArray = getReleaseListeners();
			for (final ReleaseListener element : releaseListenersArray) {
				element.releasing(releaseEvent);
			}
			releasePass1();
			releasePass2();
			for (final ReleaseListener element : releaseListenersArray) {
				element.released(releaseEvent);
			}
			releasePass3();
			m_isReleased = true;
		}
	}

	public Object getBonus() {
		return m_bonus;
	}

	public void setBonus(final Object bonus) {
		if (m_bonus != bonus) {
			m_bonus = bonus;
			onPropertyChange(BONUS_PROPERTY);
		}
	}

	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		if (notequal(m_name, name)) {
			m_name = name;
			onPropertyChange(NAME_PROPERTY);
		}
	}

	public void addPropertyListener(final PropertyListener propertyListener) {
		m_propertyListeners.addElement(propertyListener);
		m_propertyListenerArray = null;
	}

	public void removePropertyListener(final PropertyListener propertyListener) {
		m_propertyListeners.removeElement(propertyListener);
		m_propertyListenerArray = null;
	}

	public PropertyListener[] getPropertyListeners() {
		if (m_propertyListenerArray == null) {
			m_propertyListenerArray = new PropertyListener[m_propertyListeners.size()];
			m_propertyListeners.copyInto(m_propertyListenerArray);
		}
		return m_propertyListenerArray;
	}

	protected void onPropertyChange(final PropertyEvent propertyEvent) {
		final java.util.Enumeration<Object> enum0 = m_propertyListeners.elements();
		while (enum0.hasMoreElements()) {
			final PropertyListener propertyListener = (PropertyListener) enum0.nextElement();
			propertyListener.changed(propertyEvent);
		}
	}

	protected void onPropertyChange(final Property property) {
		if (isReleased()) {
			warnln("WARNING: scenegraph property change " + property + " on already released " + this + ".");
		} else {
			onPropertyChange(new PropertyEvent(this, property));
		}
	}

	public void addReleaseListener(final ReleaseListener releaseListener) {
		m_releaseListeners.addElement(releaseListener);
		m_releaseListenerArray = null;
	}

	public void removeReleaseListener(final ReleaseListener releaseListener) {
		m_releaseListeners.removeElement(releaseListener);
		m_releaseListenerArray = null;
	}

	public ReleaseListener[] getReleaseListeners() {
		if (m_releaseListenerArray == null) {
			m_releaseListenerArray = new ReleaseListener[m_releaseListeners.size()];
			m_releaseListeners.copyInto(m_releaseListenerArray);
		}
		return m_releaseListenerArray;
	}

	protected static boolean notequal(final Object a, final Object b) {
		if (a == null) {
			return b != null;
		} else {
			return !a.equals(b);
		}
	}

	@Override
	public String toString() {
		if (m_name == null) {
			return super.toString();
		} else {
			return m_name;
		}
	}
}
