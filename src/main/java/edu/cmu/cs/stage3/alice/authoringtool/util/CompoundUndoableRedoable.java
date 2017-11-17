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

public class CompoundUndoableRedoable implements ContextAssignableUndoableRedoable {
	protected java.util.ArrayList items = new java.util.ArrayList();
	protected Object context;

	public CompoundUndoableRedoable() {
	}

	@Override
	public void setContext(final Object context) {
		this.context = context;
	}

	public void addItem(final UndoableRedoable item) {
		if (item instanceof ChildChangeUndoableRedoable) {
			final ChildChangeUndoableRedoable ccur = (ChildChangeUndoableRedoable) item;
		}
		items.add(item);
	}

	@Override
	public void undo() {
		final java.util.ListIterator iter = items.listIterator();
		while (iter.hasNext()) {
			iter.next();
		}
		while (iter.hasPrevious()) {
			final edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable item = (edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable) iter
					.previous();
			item.undo();
		}
	}

	@Override
	public void redo() {
		final java.util.ListIterator iter = items.listIterator();
		while (iter.hasNext()) {
			final edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable item = (edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable) iter
					.next();
			item.redo();
		}
	}

	@Override
	public Object getAffectedObject() {
		return this;
	}

	@Override
	public Object getContext() {
		return context;
	}
}