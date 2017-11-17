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

package edu.cmu.cs.stage3.alice.core.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;

public abstract class WorldListener {
	private edu.cmu.cs.stage3.alice.core.World m_world;
	private final edu.cmu.cs.stage3.alice.core.event.ChildrenListener m_childrenListener = new edu.cmu.cs.stage3.alice.core.event.ChildrenListener() {
		@Override
		public void childrenChanging(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e) {
			WorldListener.this.handleChildrenChanging(e);
		}

		@Override
		public void childrenChanged(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e) {
			if (e.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED) {
				WorldListener.this.hookUp(e.getChild());
			} else if (e.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) {
				WorldListener.this.unhookUp(e.getChild());
			}
			WorldListener.this.handleChildrenChanged(e);
		}
	};
	private final edu.cmu.cs.stage3.alice.core.event.PropertyListener m_propertyListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		@Override
		public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent e) {
			WorldListener.this.handlePropertyChanging(e);
		}

		@Override
		public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent e) {
			WorldListener.this.handlePropertyChanged(e);
		}
	};
	private final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener m_objectArrayPropertyListener = new edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener() {
		@Override
		public void objectArrayPropertyChanging(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e) {
			WorldListener.this.handleObjectArrayPropertyChanging(e);
		}

		@Override
		public void objectArrayPropertyChanged(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e) {
			WorldListener.this.handleObjectArrayPropertyChanged(e);
		}
	};

	protected abstract void handleChildrenChanging(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e);

	protected abstract void handleChildrenChanged(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e);

	protected abstract void handlePropertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent e);

	protected abstract void handlePropertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent e);

	protected abstract void handleObjectArrayPropertyChanging(
			edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e);

	protected abstract void handleObjectArrayPropertyChanged(
			edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e);

	protected abstract boolean isPropertyListeningRequired(edu.cmu.cs.stage3.alice.core.Property property);

	protected abstract boolean isObjectArrayPropertyListeningRequired(
			edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap);

	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return m_world;
	}

	public void setWorld(final edu.cmu.cs.stage3.alice.core.World world) {
		if (m_world != world) {
			if (m_world != null) {
				unhookUp(m_world);
			}
			m_world = world;
			if (m_world != null) {
				hookUp(m_world);
			}
		}
	}

	private boolean isChildrenListenerHookedUp(final edu.cmu.cs.stage3.alice.core.Element element) {
		final edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = element.getChildrenListeners();
		for (final ChildrenListener childrenListener : childrenListeners) {
			if (childrenListener == m_childrenListener) {
				return true;
			}
		}
		return false;
	}

	private boolean isPropertyListenerHookedUp(final edu.cmu.cs.stage3.alice.core.Property property) {
		final edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propertyListeners = property.getPropertyListeners();
		for (final PropertyListener propertyListener : propertyListeners) {
			if (propertyListener == m_propertyListener) {
				return true;
			}
		}
		return false;
	}

	private boolean isObjectArrayPropertyListenerHookedUp(
			final edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap) {
		final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener[] oapListeners = oap
				.getObjectArrayPropertyListeners();
		for (final ObjectArrayPropertyListener oapListener : oapListeners) {
			if (oapListener == m_objectArrayPropertyListener) {
				return true;
			}
		}
		return false;
	}

	private void unhookUp(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (isChildrenListenerHookedUp(element)) {
			element.removeChildrenListener(m_childrenListener);
			// System.err.println( "-" + element );
		}
		final edu.cmu.cs.stage3.alice.core.Property[] properties = element.getProperties();
		for (final Property propertyI : properties) {
			if (isPropertyListeningRequired(propertyI)) {
				if (isPropertyListenerHookedUp(propertyI)) {
					propertyI.removePropertyListener(m_propertyListener);
				}
			}
			if (propertyI instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
				final edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oapI = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) propertyI;
				if (isObjectArrayPropertyListeningRequired(oapI)) {
					if (isObjectArrayPropertyListenerHookedUp(oapI)) {
						oapI.removeObjectArrayPropertyListener(m_objectArrayPropertyListener);
					}
				}
			}
		}
		final edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for (final Element element2 : children) {
			unhookUp(element2);
		}
	}

	private void hookUp(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (isChildrenListenerHookedUp(element)) {
			// pass
		} else {
			// System.err.println( "+" + element );
			element.addChildrenListener(m_childrenListener);
		}
		final edu.cmu.cs.stage3.alice.core.Property[] properties = element.getProperties();
		for (final Property propertyI : properties) {
			if (isPropertyListeningRequired(propertyI)) {
				if (isPropertyListenerHookedUp(propertyI)) {
					// pass
				} else {
					propertyI.addPropertyListener(m_propertyListener);
				}
			}
			if (propertyI instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
				final edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oapI = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) propertyI;
				if (isObjectArrayPropertyListeningRequired(oapI)) {
					if (isObjectArrayPropertyListenerHookedUp(oapI)) {
						// pass
					} else {
						oapI.addObjectArrayPropertyListener(m_objectArrayPropertyListener);
					}
				}
			}
		}
		final edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for (final Element element2 : children) {
			hookUp(element2);
		}
	}
}