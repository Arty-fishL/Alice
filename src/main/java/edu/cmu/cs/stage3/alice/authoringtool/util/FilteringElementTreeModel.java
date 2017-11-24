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

import edu.cmu.cs.stage3.alice.core.Element;

/**
 * @author Jason Pratt
 */
public class FilteringElementTreeModel extends TreeModelSupport implements
		edu.cmu.cs.stage3.alice.core.event.PropertyListener, edu.cmu.cs.stage3.alice.core.event.ChildrenListener,
		edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener, javax.swing.tree.TreeModel {
	protected edu.cmu.cs.stage3.alice.core.Element root;
	protected Object[] emptyPath = { new edu.cmu.cs.stage3.alice.core.World() };
	protected java.util.LinkedList inclusionList;
	protected java.util.LinkedList exclusionList;

	public FilteringElementTreeModel() {
	}

	public java.util.LinkedList getInclusionList() {
		return inclusionList;
	}

	public void setInclusionList(final java.util.LinkedList list) {
		inclusionList = list;
		update();
	}

	public java.util.LinkedList getExclusionList() {
		return exclusionList;
	}

	public void setExclusionList(final java.util.LinkedList list) {
		exclusionList = list;
		update();
	}

	public boolean isAcceptedByFilter(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (inclusionList == null) {
			return false;
		}

		// anyone meeting an exclusion Criterion get booted
		if (exclusionList != null) {
			for (final java.util.Iterator iter = exclusionList.iterator(); iter.hasNext();) {
				final Object item = iter.next();
				if (item instanceof edu.cmu.cs.stage3.util.Criterion) {
					if (((edu.cmu.cs.stage3.util.Criterion) item).accept(element)) {
						return false;
					}
				}
			}
		}

		// anyone left who meets an inclusion Criterion is accepted
		if (inclusionList != null) {
			for (final java.util.Iterator iter = inclusionList.iterator(); iter.hasNext();) {
				final Object item = iter.next();
				if (item instanceof edu.cmu.cs.stage3.util.Criterion) {
					if (((edu.cmu.cs.stage3.util.Criterion) item).accept(element)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isElementInTree(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (element == root) {
			return true;
		}

		if (element == null) {
			return false;
		}

		if (isElementInTree(element.getParent()) && isAcceptedByFilter(element)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * if someone alters the inclusion or exclusion lists on their own, they
	 * must call this method to update the tree
	 */
	public void update() {
		clearAllListening(root);
		startListeningToTree(root);

		if (root == null) {
			final javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent(this, emptyPath);
			fireTreeStructureChanged(ev);
		} else {
			final javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent(this, getPath(root));
			fireTreeStructureChanged(ev);
		}
	}

	public void setRoot(final edu.cmu.cs.stage3.alice.core.Element root) {
		clearAllListening(this.root);
		this.root = root;
		startListeningToTree(root);

		if (root == null) {
			final javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent(this, emptyPath);
			fireTreeStructureChanged(ev);
		} else {
			final javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent(this, getPath(root));
			fireTreeStructureChanged(ev);
		}
	}

	public Object[] getPath(final edu.cmu.cs.stage3.alice.core.Element element) {
		final java.util.LinkedList<Element> list = new java.util.LinkedList<Element>();

		edu.cmu.cs.stage3.alice.core.Element e = element;
		while (true) {
			if (e == null) {
				return new Object[] {}; // element's not in the tree
			}
			list.addFirst(e);
			if (e == root) {
				break;
			}
			e = e.getParent();
		}

		return list.toArray();
	}

	public void setListeningEnabled(final boolean enabled) {
		if (enabled) {
			startListeningToTree(root);
		} else {
			stopListeningToTree(root);
		}
	}

	// //////////////////////////////
	// TreeModel Interface
	// //////////////////////////////

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(final Object node) {
		if (node == null) {
			return true;
		}
		/*
		 * might want this if( node instanceof
		 * edu.cmu.cs.stage3.alice.core.Group ) { return false; }
		 */
		if (node == root) {
			return false;
		}
		if (!(node instanceof edu.cmu.cs.stage3.alice.core.Element)) {
			throw new IllegalArgumentException("nodes must be edu.cmu.cs.stage3.alice.core.Elements");
		}

		final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) node;
		final edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for (final Element element2 : children) {
			if (isAcceptedByFilter(element2)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int getChildCount(final Object parent) {
		if (!(parent instanceof edu.cmu.cs.stage3.alice.core.Element)) {
			throw new IllegalArgumentException("nodes must be edu.cmu.cs.stage3.alice.core.Elements");
		}

		int childCount = 0;
		final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) parent;
		final edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for (final Element element2 : children) {
			if (isAcceptedByFilter(element2)) {
				childCount++;
			}
		}

		return childCount;
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		if (!(parent instanceof edu.cmu.cs.stage3.alice.core.Element)) {
			throw new IllegalArgumentException("nodes must be edu.cmu.cs.stage3.alice.core.Elements");
		}

		int childCount = 0;
		final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) parent;
		final edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();

		for (final Element element2 : children) {
			if (isAcceptedByFilter(element2)) {
				if (childCount == index) {
					return element2;
				}
				childCount++;
			}
		}

		return null;
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		if (!(parent instanceof edu.cmu.cs.stage3.alice.core.Element)) {
			throw new IllegalArgumentException("nodes must be edu.cmu.cs.stage3.alice.core.Elements");
		}
		if (!(child instanceof edu.cmu.cs.stage3.alice.core.Element)) {
			throw new IllegalArgumentException("nodes must be edu.cmu.cs.stage3.alice.core.Elements");
		}

		int childCount = 0;
		final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) parent;
		final edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for (final Element element2 : children) {
			if (isAcceptedByFilter(element2)) {
				if (element2 == child) { // should this use .equals? I don't
											// think so...
					return childCount;
				}
				childCount++;
			}
		}

		return -1;
	}

	@Override
	public void valueForPathChanged(final javax.swing.tree.TreePath path, final Object newValue) {
		if (path.getLastPathComponent() instanceof edu.cmu.cs.stage3.alice.core.Element && newValue instanceof String) {
			final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) path
					.getLastPathComponent();
			final Object previousName = element.name.get();
			try {
				element.name.set(newValue);
			} catch (final IllegalArgumentException e) {
				// TODO produce some kind of event so that the interface can
				// take appropriate action.
				element.name.set(previousName);
			}
		} else {
			throw new RuntimeException("FilteringElementTreeModel only allows name changes through the model");
		}
	}

	// /////////////////////////////////////////////
	// AuthoringToolStateListener interface
	// /////////////////////////////////////////////

	@Override
	public void stateChanging(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
		if (ev.getCurrentState() == edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent.RUNTIME_STATE) {
			FilteringElementTreeModel.this.setListeningEnabled(false);
		} else {
			FilteringElementTreeModel.this.setListeningEnabled(true);
		}
	}

	@Override
	public void worldLoading(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldUnLoading(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldStarting(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldStopping(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldPausing(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldSaving(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void stateChanged(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldLoaded(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldUnLoaded(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldStarted(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldStopped(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldPaused(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldSaved(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	// //////////////////////////////////////////////////
	// ChildrenListener and PropertyListener Interface
	// //////////////////////////////////////////////////

	@Override
	public void childrenChanging(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent) {
	}

	@Override
	public void childrenChanged(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent) {
		final edu.cmu.cs.stage3.alice.core.Element element = childrenEvent.getChild();
		final edu.cmu.cs.stage3.alice.core.Element parent = (edu.cmu.cs.stage3.alice.core.Element) childrenEvent
				.getSource();
		final Object[] path = getPath(parent);

		if (childrenEvent.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED) {
			if (isAcceptedByFilter(element)) {
				startListeningToTree(element);

				final int[] childIndices = { getIndexOfChild(parent, element) };
				final Object[] children = { element };
				final javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent(this, path,
						childIndices, children);
				fireTreeNodesInserted(ev);
			}
		} else if (childrenEvent.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) {
			stopListeningToTree(element);

			// this is a cop-out; it would be rather difficult to calculate the
			// previous position in the filtered tree
			// of the already deleted element
			final javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent(this, path);
			fireTreeStructureChanged(ev);
		} else if (childrenEvent.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED) {
			if (isElementInTree(element)) {
				// this one isn't a cop-out
				final javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent(this, path);
				fireTreeStructureChanged(ev);
			}
		}
	}

	@Override
	public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
	}

	@Override
	public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
		// only listen to name changes for now.
		// TODO: this probably wants a more sophisticated mechanism.
		if (propertyEvent.getProperty() == propertyEvent.getProperty().getOwner().name) {
			final edu.cmu.cs.stage3.alice.core.Element element = propertyEvent.getProperty().getOwner();
			final edu.cmu.cs.stage3.alice.core.Element parent = element.getParent();
			Object[] path = getPath(parent);
			int[] childIndices = { getIndexOfChild(parent, element) };
			Object[] children = { element };
			if (path == null || path.length == 0) {
				path = getPath(element);
				childIndices = null;
				children = null;
			}
			final javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent(this, path, childIndices,
					children);
			fireTreeNodesChanged(ev);
		}
	}

	// //////////////////////////////
	// protected methods
	// //////////////////////////////

	protected void startListeningToTree(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null) {
			element.addChildrenListener(this);
			element.name.addPropertyListener(this);

			final edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
			for (final Element element2 : children) {
				if (isAcceptedByFilter(element2)) {
					startListeningToTree(element2);
				}
			}
		}
	}

	protected void stopListeningToTree(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null) {
			element.removeChildrenListener(this);
			element.name.removePropertyListener(this);

			final edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
			for (final Element element2 : children) {
				if (isAcceptedByFilter(element2)) {
					stopListeningToTree(element2);
				}
			}
		}
	}

	protected void clearAllListening(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null) {
			element.removeChildrenListener(this);
			element.name.removePropertyListener(this);

			final edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
			for (final Element element2 : children) {
				clearAllListening(element2);
			}
		}
	}

	/**
	 * Determines how close a subclass is to a superclass
	 *
	 * @returns the depth of the class hierarchy between the given superclass
	 *          and subclass
	 */
	protected static int getClassDepth(final Class superclass, final Class subclass) {
		if (!superclass.isAssignableFrom(subclass)) {
			return -1;
		}

		Class temp = subclass;
		int i = 0;
		while (temp != superclass && superclass.isAssignableFrom(temp)) {
			i++;
			temp = temp.getSuperclass();
		}

		return i;
	}
}
