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

import java.util.ListIterator;

/**
 * @author Jason Pratt
 */
public class EditorManager {
	protected AuthoringTool authoringTool;
	protected java.util.List<Editor> availableEditors = new java.util.ArrayList<>();
	protected java.util.List<Editor> inUseEditors = new java.util.ArrayList<>();

	public EditorManager(final AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
	}

	public Editor getBestEditorInstance(final Class<?> objectClass) {
		return getEditorInstance(edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor(objectClass));
	}

	public Editor getEditorInstance(final Class<? extends Editor> editorClass) {
		if (editorClass == null) {
			return null;
		}
		for (final ListIterator<Editor> iter = availableEditors.listIterator(); iter.hasNext();) {
			final Editor editor = iter.next();
			if (editor.getClass() == editorClass) {
				iter.remove();
				inUseEditors.add(editor);
				return (edu.cmu.cs.stage3.alice.authoringtool.Editor) editor;
			}
		}

		final Editor editor = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities
				.getEditorFromClass(editorClass);
		if (editor != null) {
			authoringTool.addAuthoringToolStateListener(editor);
			inUseEditors.add(editor);
			editor.setAuthoringTool(authoringTool);
		}
		return editor;
	}

	public void releaseEditorInstance(final Editor editor) {
		if (inUseEditors.contains(editor)) {
			inUseEditors.remove(editor);
			if (!availableEditors.contains(editor)) {
				availableEditors.add(editor);
			}
		}
	}

	public void preloadEditor(final Class<? extends Editor> editorClass) {
		releaseEditorInstance(getEditorInstance(editorClass));
	}
}