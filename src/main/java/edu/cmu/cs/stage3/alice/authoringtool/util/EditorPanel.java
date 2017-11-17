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

//TODO: handle null edits better in the stack

/**
 * @author Jason Pratt
 */
public class EditorPanel extends javax.swing.JPanel
		implements edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 5756362286458797818L;
	protected edu.cmu.cs.stage3.alice.authoringtool.Editor activeEditor = null;
	protected java.lang.reflect.Method activeEditorSetMethod = null;
	protected java.util.HashMap cachedEditors = new java.util.HashMap();
	protected EditStack editStack = new EditStack();
	protected java.util.HashSet listenerSet = new java.util.HashSet();
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

	protected final edu.cmu.cs.stage3.alice.core.event.ChildrenListener deletionListener = new edu.cmu.cs.stage3.alice.core.event.ChildrenListener() {
		@Override
		public void childrenChanging(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) {
		}

		@Override
		public void childrenChanged(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) {
			if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED
					&& ev.getChild() == EditorPanel.this.getElementBeingEdited()) {
				EditorPanel.this.editElement(null);
				ev.getParent().removeChildrenListener(this);
			}
		}
	};

	public final javax.swing.AbstractAction backAction = new javax.swing.AbstractAction() {
		/**
		 *
		 */
		private static final long serialVersionUID = 3888131160821465826L;

		@Override
		public void actionPerformed(final java.awt.event.ActionEvent e) {
			EditorPanel.this.back();
		}
	};

	public final javax.swing.AbstractAction forwardAction = new javax.swing.AbstractAction() {
		/**
		 *
		 */
		private static final long serialVersionUID = 3778334131384409523L;

		@Override
		public void actionPerformed(final java.awt.event.ActionEvent e) {
			EditorPanel.this.forward();
		}
	};

	public EditorPanel(final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
		setLayout(new java.awt.BorderLayout());
		actionInit();
	}

	private void actionInit() {
		backAction.putValue(javax.swing.Action.ACTION_COMMAND_KEY, "back");
		backAction.putValue(javax.swing.Action.NAME, "Back");
		backAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Back to last item");
		backAction.setEnabled(false);

		forwardAction.putValue(javax.swing.Action.ACTION_COMMAND_KEY, "forward");
		forwardAction.putValue(javax.swing.Action.NAME, "Forward");
		forwardAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Forward to next item");
		forwardAction.setEnabled(false);
	}

	public void addEditorPanelListener(
			final edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelListener listener) {
		listenerSet.add(listener);
	}

	public void removeEditorPanelListener(
			final edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelListener listener) {
		listenerSet.remove(listener);
	}

	public edu.cmu.cs.stage3.alice.authoringtool.Editor loadEditor(final Class editorClass) {
		edu.cmu.cs.stage3.alice.authoringtool.Editor editor = null;

		if (editorClass != null) {
			editor = (edu.cmu.cs.stage3.alice.authoringtool.Editor) cachedEditors.get(editorClass);
			if (editor == null) {
				try {
					editor = EditorUtilities.getEditorFromClass(editorClass);
					if (editor == null) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
								.showErrorDialog("Can't create editor of type " + editorClass.getName(), null);
					} else {
						cachedEditors.put(editorClass, editor);
						authoringTool.addAuthoringToolStateListener(editor);
						editor.setAuthoringTool(authoringTool);
					}
				} catch (final Throwable t) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
							.showErrorDialog("Error creating editor for type " + editorClass.getName(), t);
				}
			}
		}

		return editor;
	}

	public void editElement(final edu.cmu.cs.stage3.alice.core.Element element) {
		editElement(element, true);
	}

	public void editElement(final edu.cmu.cs.stage3.alice.core.Element element, final Class editorClass) {
		editElement(element, editorClass, true);
	}

	protected void editElement(final edu.cmu.cs.stage3.alice.core.Element element, final boolean performPush) {
		if (element == null) {
			editElement(null, null, performPush);
		} else {
			final Class bestEditorClass = EditorUtilities.getBestEditor(element.getClass());
			if (bestEditorClass == null) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
						.showErrorDialog("No editor found for " + element.getClass(), null);
			}
			editElement(element, bestEditorClass, performPush);
		}
	}

	protected void editElement(final edu.cmu.cs.stage3.alice.core.Element element, final Class editorClass,
			final boolean performPush) {
		if (getElementBeingEdited() != null && getElementBeingEdited().getParent() != null) {
			getElementBeingEdited().getParent().removeChildrenListener(deletionListener);
		}

		final edu.cmu.cs.stage3.alice.authoringtool.Editor editor = loadEditor(editorClass);

		// if we have a new editor...
		if (activeEditor != editor) {
			// clean up old editor if necessary
			if (activeEditor != null) {
				try {
					activeEditorSetMethod.invoke(activeEditor, new Object[] { null });
				} catch (final java.lang.reflect.InvocationTargetException ite) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error cleaning editor.", ite);
				} catch (final IllegalAccessException iae) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error cleaning editor.", iae);
				}
			}

			// setup new editor
			removeAll();
			activeEditor = editor;
			if (activeEditor != null) {
				activeEditorSetMethod = EditorUtilities.getSetMethodFromClass(editorClass);
				add(java.awt.BorderLayout.CENTER, activeEditor.getJComponent());
			} else {
				activeEditorSetMethod = null;
				for (final java.util.Iterator iter = listenerSet.iterator(); iter.hasNext();) {
					final edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelEvent(
							null);
					((edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelListener) iter.next())
							.elementChanged(ev);
				}
			}
			revalidate();
			repaint();
		}

		// if the new editor isn't null, start editing the element
		if (activeEditor != null && activeEditor.getObject() != element) {
			try {
				activeEditorSetMethod.invoke(activeEditor, new Object[] { element });
				if (performPush && element != null) {
					editStack.push(new EditItem(element, editorClass));
					updateActions();
				}
				for (final java.util.Iterator iter = listenerSet.iterator(); iter.hasNext();) {
					final edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelEvent(
							element);
					((edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelListener) iter.next())
							.elementChanged(ev);
				}
				if (element != null && element.getParent() != null) {
					element.getParent().addChildrenListener(deletionListener);
				}
			} catch (final java.lang.reflect.InvocationTargetException ite) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error intializing editor.", ite);
			} catch (final IllegalAccessException iae) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error intializing editor.", iae);
			}
		}
	}

	public edu.cmu.cs.stage3.alice.core.Element getElementBeingEdited() {
		if (activeEditor != null) {
			return (edu.cmu.cs.stage3.alice.core.Element) activeEditor.getObject();
		} else {
			return null;
		}
	}

	public edu.cmu.cs.stage3.alice.authoringtool.Editor getActiveEditor() {
		return activeEditor;
	}

	public void back() {
		editStack.undo();
		updateActions();
	}

	public void forward() {
		editStack.redo();
		updateActions();
	}

	public boolean canGoBack() {
		return editStack.getCurrentUndoableRedoableIndex() > 0;
	}

	public boolean canGoForward() {
		return editStack.getCurrentUndoableRedoableIndex() != editStack.size() - 1;
	}

	protected void updateActions() {
		backAction.setEnabled(canGoBack());
		forwardAction.setEnabled(canGoForward());
	}

	class EditStack extends DefaultUndoRedoStack {

		/**
		 *
		 */
		private static final long serialVersionUID = -2136443448865489925L;

		@Override
		public UndoableRedoable undo() {
			final UndoableRedoable ur = super.undo();
			final UndoableRedoable newItem = editStack.getCurrentUndoableRedoable();
			if (ur != null) {
				editElement(((EditItem) newItem).getElement(), ((EditItem) newItem).getEditorClass(), false);
			}
			return ur;
		}

		@Override
		public UndoableRedoable redo() {
			final UndoableRedoable ur = super.redo();
			if (ur != null) {
				editElement(((EditItem) ur).getElement(), ((EditItem) ur).getEditorClass(), false);
			}
			return ur;
		}
	}

	class EditItem implements UndoableRedoable {
		protected edu.cmu.cs.stage3.alice.core.Element element;
		protected Class editorClass;

		public EditItem(final edu.cmu.cs.stage3.alice.core.Element element, final Class editorClass) {
			this.element = element;
			this.editorClass = editorClass;
		}

		@Override
		public void undo() {
		}

		@Override
		public void redo() {
		}

		@Override
		public Object getAffectedObject() {
			return element;
		}

		@Override
		public Object getContext() {
			return EditorPanel.this;
		}

		public edu.cmu.cs.stage3.alice.core.Element getElement() {
			return element;
		}

		public Class getEditorClass() {
			return editorClass;
		}
	}

	// /////////////////////////////////////////////
	// AuthoringToolStateListener interface
	// /////////////////////////////////////////////

	@Override
	public void stateChanging(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
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
		editStack.clear();
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
