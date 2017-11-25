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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 * @deprecated use ElementNamePropertyViewController
 */
@Deprecated
public class RightClickNameEditor extends StringPropertyViewController {
	/**
	 *
	 */
	private static final long serialVersionUID = -5400581337153285060L;

	public RightClickNameEditor(final edu.cmu.cs.stage3.alice.core.Element element) {
		super.set(element.name, false, true, true, new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
			@Override
			public Object createItem(final Object value) {
				return new Runnable() {
					@Override
					public void run() {
						element.name.set(value);
					}
				};
			}
		});
		final java.util.Vector<StringObjectPair> x;
		popupStructure = x = new java.util.Vector<StringObjectPair>();
		x.add(new edu.cmu.cs.stage3.util.StringObjectPair("Rename", new Runnable() {
			@Override
			public void run() {
				RightClickNameEditor.this.editValue();
			}
		}));
		setPopupEnabled(false);
	}

	@Override
	protected java.awt.event.MouseListener getMouseListener() {
		return new java.awt.event.MouseAdapter() {

			@Override
			public void mouseReleased(final java.awt.event.MouseEvent ev) {
				if (isEnabled() && ev.isPopupTrigger()) {
					RightClickNameEditor.this.popupButton.doClick();
				}
			}
		};
	}

	@Override
	protected void updatePopupStructure() {
	}
}
