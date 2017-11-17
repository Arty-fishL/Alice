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

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;

/**
 * @author Jason Pratt
 */

public class MainUndoRedoStack extends edu.cmu.cs.stage3.alice.authoringtool.util.DefaultUndoRedoStack
		implements edu.cmu.cs.stage3.alice.core.event.PropertyListener,
		edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener,
		edu.cmu.cs.stage3.alice.core.event.ChildrenListener,
		edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 5556519069695182440L;
	protected AuthoringTool authoringTool;
	private boolean isListening = true;
	private boolean inCompound = false;
	private edu.cmu.cs.stage3.alice.authoringtool.util.CompoundUndoableRedoable compoundItem;
	private int unmodifiedIndex = -1;
	private boolean scriptHasChanged = false;
	protected java.util.HashSet listeners = new java.util.HashSet();

	public MainUndoRedoStack(final AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
	}

	public void addUndoRedoListener(final edu.cmu.cs.stage3.alice.authoringtool.event.UndoRedoListener listener) {
		listeners.add(listener);
	}

	public void removeUndoRedoListener(final edu.cmu.cs.stage3.alice.authoringtool.event.UndoRedoListener listener) {
		listeners.remove(listener);
	}

	public int getUnmodifiedIndex() {
		return unmodifiedIndex;
	}

	public boolean isScriptDirty() {
		return scriptHasChanged;
	}

	synchronized protected void fireChange() {
		for (final java.util.Iterator iter = listeners.iterator(); iter.hasNext();) {
			((edu.cmu.cs.stage3.alice.authoringtool.event.UndoRedoListener) iter.next()).onChange();
		}
	}

	synchronized public void setUnmodified() {
		unmodifiedIndex = getCurrentUndoableRedoableIndex();
		scriptHasChanged = false;
		fireChange();
	}

	synchronized public void setIsListening(final boolean isListening) {
		this.isListening = isListening;
	}

	synchronized public boolean getIsListening() {
		return isListening;
	}

	synchronized public void startCompound() {
		if (!inCompound) {
			compoundItem = new edu.cmu.cs.stage3.alice.authoringtool.util.CompoundUndoableRedoable();
			compoundItem.setContext(authoringTool.getContext());
			push(compoundItem);
			inCompound = true;
		}
	}

	synchronized public void stopCompound() {
		inCompound = false;
	}

	@Override
	synchronized public edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable undo() {
		stopCompound();
		final boolean temp = isListening;
		isListening = false;
		final edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable ur = super.undo();
		loadContext(ur.getContext());
		isListening = temp;
		fireChange();
		return ur;
	}

	@Override
	synchronized public edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable redo() {
		stopCompound();
		final boolean temp = isListening;
		isListening = false;
		final int currentIndex = getCurrentUndoableRedoableIndex();
		if (currentIndex < size() - 1) {
			loadContext(
					((edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable) get(currentIndex + 1)).getContext());
		}
		final edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable ur = super.redo();
		isListening = temp;
		fireChange();
		return ur;
	}

	synchronized protected void loadContext(final Object context) {
		authoringTool.setContext(context);
	}

	@Override
	synchronized public void push(final edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable ur) {
		// Thread.dumpStack();
		if (ur instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ContextAssignableUndoableRedoable) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.ContextAssignableUndoableRedoable) ur)
					.setContext(authoringTool.getContext());
		}
		if (inCompound) {
			compoundItem.addItem(ur);
		} else {
			super.push(ur);
		}
		fireChange();
	}

	@Override
	synchronized public edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable pop_() {
		stopCompound();
		final edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable ur = super.pop_();
		fireChange();
		return ur;
	}

	@Override
	synchronized public void clear() {
		super.clear();
		setUnmodified();
		fireChange();
	}

	@Override
	synchronized public edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable removeUndoable(final int index) {
		final edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable ur = super.removeUndoable(index);
		fireChange();
		return ur;
	}

	protected Object preChangeValue;

	@Override
	synchronized public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
		if (isListening) {
			// TODO: I need to be getting a clone here...?
			preChangeValue = propertyEvent.getProperty().get();
		}
	}

	@Override
	synchronized public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
		if (isListening) {
			// ObjectArrayProperties are handled separately
			if (propertyEvent.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
				return;
			}

			// if the property change is for a property being changed by a
			// currently running one-shot, we ignore the change
			if (authoringTool.getOneShotScheduler().isPropertyAffected(propertyEvent.getProperty())) {
				return;
			}

			// don't push script changes onto the stack, but realize that
			// they've been made
			if (propertyEvent.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.ScriptProperty) {
				scriptHasChanged = true;
				fireChange();
				return;
			}

			push(new edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable(propertyEvent.getProperty(),
					preChangeValue, propertyEvent.getProperty().get()));
			// DEBUG System.out.println( "context: " + context );
			// DEBUG System.out.println(
			// "undoRedoStack.propertyChanged pushed: " +
			// propertyEvent.getProperty() + ", " + preChangeValue + ", " +
			// propertyEvent.getProperty().get() );
		}
	}

	@Override
	synchronized public void objectArrayPropertyChanging(
			final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
	}

	@Override
	synchronized public void objectArrayPropertyChanged(
			final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		if (isListening) {
			// if the property change is for a property being changed by a
			// currently running one-shot, we ignore the change
			if (authoringTool.getOneShotScheduler().isPropertyAffected(ev.getObjectArrayProperty())) {
				return;
			}

			push(new edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable(
					ev.getObjectArrayProperty(), ev.getChangeType(), ev.getOldIndex(), ev.getNewIndex(), ev.getItem()));
		}
	}

	@Override
	synchronized public void childrenChanging(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent) {
	}

	@Override
	synchronized public void childrenChanged(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent) {
		if (isListening) {
			push(new edu.cmu.cs.stage3.alice.authoringtool.util.ChildChangeUndoableRedoable(childrenEvent));
		}

		final int changeType = childrenEvent.getChangeType();
		if (changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED) {
			listenTo(childrenEvent.getChild());
		} else if (changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) {
			stopListeningTo(childrenEvent.getChild());
		}
	}

	synchronized public void listenTo(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null) {
			final edu.cmu.cs.stage3.alice.core.Element[] elements = element.getDescendants();
			for (final Element element2 : elements) {
				final edu.cmu.cs.stage3.alice.core.Property[] properties = element2.getProperties();
				for (final Property propertie : properties) {
					propertie.addPropertyListener(this);
					if (propertie instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
						((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) propertie)
								.addObjectArrayPropertyListener(this);
					}
				}
				boolean alreadyChildrenListening = false;
				final edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = element2
						.getChildrenListeners();
				for (final ChildrenListener childrenListener : childrenListeners) {
					if (childrenListener == this) {
						alreadyChildrenListening = true;
					}
				}
				if (!alreadyChildrenListening) {
					element2.addChildrenListener(this);
				}
			}
		}
	}

	synchronized public void stopListeningTo(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null) {
			final edu.cmu.cs.stage3.alice.core.Element[] elements = element.getDescendants();
			for (final Element element2 : elements) {
				final edu.cmu.cs.stage3.alice.core.Property[] properties = element2.getProperties();
				for (final Property propertie : properties) {
					propertie.removePropertyListener(this);
					if (propertie instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
						((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) propertie)
								.removeObjectArrayPropertyListener(this);
					}
				}
				element2.removeChildrenListener(this);
			}
		}
	}

	// /////////////////////////////////////////////
	// AuthoringToolStateListener interface
	// /////////////////////////////////////////////
	@Override
	public void stateChanged(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
		if (ev.getCurrentState() == edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent.RUNTIME_STATE) {
			stopListeningTo(ev.getWorld());
		} else {
			listenTo(ev.getWorld());
		}
	}

	@Override
	public void worldUnLoading(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
		stopListeningTo(ev.getWorld());
	}

	@Override
	public void worldLoaded(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
		listenTo(ev.getWorld());
	}

	@Override
	public void stateChanging(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldLoading(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
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
}
