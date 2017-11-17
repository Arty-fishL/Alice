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

/**
 * @author Jason Pratt
 */
public class CreateNewWrapperTreeModel implements javax.swing.tree.TreeModel {
	protected javax.swing.tree.TreeModel wrappedTreeModel;
	protected Object createNewObject;

	public CreateNewWrapperTreeModel(final javax.swing.tree.TreeModel treeModelToWrap, final Object createNewObject) {
		if (treeModelToWrap != null && createNewObject != null) {
			wrappedTreeModel = treeModelToWrap;
			this.createNewObject = createNewObject;
		} else {
			throw new IllegalArgumentException("treeModelToWrap and createNewObject cannot be null");
		}
	}

	@Override
	public Object getRoot() {
		return wrappedTreeModel.getRoot();
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		if (parent == wrappedTreeModel.getRoot() && index == wrappedTreeModel.getChildCount(parent)) {
			return createNewObject;
		} else {
			return wrappedTreeModel.getChild(parent, index);
		}
	}

	@Override
	public int getChildCount(final Object parent) {
		if (parent == wrappedTreeModel.getRoot()) {
			return wrappedTreeModel.getChildCount(parent) + 1;
		} else {
			return wrappedTreeModel.getChildCount(parent);
		}
	}

	@Override
	public boolean isLeaf(final Object node) {
		if (node == createNewObject) {
			return true;
		} else {
			return wrappedTreeModel.isLeaf(node);
		}
	}

	@Override
	public void valueForPathChanged(final javax.swing.tree.TreePath path, final Object newValue) {
		if (!(path.getLastPathComponent() == createNewObject)) {
			wrappedTreeModel.valueForPathChanged(path, newValue);
		}
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		if (child == createNewObject) {
			return wrappedTreeModel.getChildCount(parent);
		} else {
			return wrappedTreeModel.getIndexOfChild(parent, child);
		}
	}

	@Override
	public void addTreeModelListener(final javax.swing.event.TreeModelListener l) {
		wrappedTreeModel.addTreeModelListener(l);
	}

	@Override
	public void removeTreeModelListener(final javax.swing.event.TreeModelListener l) {
		wrappedTreeModel.removeTreeModelListener(l);
	}
}