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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.MouseInputListener;

/**
 * @author Jason Pratt
 */
public class AliceMenuItemUI extends javax.swing.plaf.basic.BasicMenuItemUI {

	@Override
	protected MouseInputListener createMouseInputListener(final JComponent c) {
		return new MouseInputHandler();
	}

	protected class MouseInputHandler implements MouseInputListener {
		@Override
		public void mouseReleased(final MouseEvent e) {
			final MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			final Point p = e.getPoint();

			if (p.x >= 0 && p.x < menuItem.getWidth() && p.y >= 0 && p.y < menuItem.getHeight()) {
				final MenuElement[] path = getPath();
				manager.clearSelectedPath();
				menuItem.doClick(0);

				// HACK
				for (final MenuElement element : path) {
					if (element instanceof AlicePopupMenu) {
						((AlicePopupMenu) element).setVisible(false);
					}
					// if( path[i] instanceof AliceMenu ) {
					// ((AliceMenu)path[i]).setPopupMenuVisible( false );
					// }
				}
			} else {
				manager.processMouseEvent(e);
			}
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			final MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			final int modifiers = e.getModifiers();

			// 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2
			if ((modifiers & (InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
				MenuSelectionManager.defaultManager().processMouseEvent(e);
			} else {
				manager.setSelectedPath(getPath());
				// System.out.print( "0_item setSelectedPath: " );
				// printPath( getPath() );
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			final MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			final int modifiers = e.getModifiers();

			// 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2
			if ((modifiers & (InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
				MenuSelectionManager.defaultManager().processMouseEvent(e);
			} else {
				final MenuElement[] path = manager.getSelectedPath();

				if (path.length > 1) {
					final MenuElement[] newPath = new MenuElement[path.length - 1];
					int i;
					int c;

					for (i = 0, c = path.length - 1; i < c; i++) {
						newPath[i] = path[i];
					}

					manager.setSelectedPath(newPath);
				}
			}
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			MenuSelectionManager.defaultManager().processMouseEvent(e);
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
		}

		@Override
		public void mousePressed(final MouseEvent e) {
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
		}
	}

	@Override
	public MenuElement[] getPath() {
		final MenuSelectionManager m = MenuSelectionManager.defaultManager();
		final MenuElement[] oldPath = m.getSelectedPath();
		MenuElement[] newPath;
		final int i = oldPath.length;

		if (i == 0) {
			return new MenuElement[0];
		}

		final Component parent = menuItem.getParent();

		if (oldPath[i - 1].getComponent() == parent) {
			newPath = new MenuElement[i + 1];
			System.arraycopy(oldPath, 0, newPath, 0, i);
			newPath[i] = menuItem;
		} else {
			final java.util.Vector path = new java.util.Vector();
			MenuElement me = menuItem;
			while (me instanceof MenuElement) {
				path.add(0, me);
				if (me instanceof JPopupMenu) {
					final Object o = ((JPopupMenu) me).getInvoker();
					if (o instanceof MenuElement && o != me) {
						me = (MenuElement) o;
					} else {
						me = null;
					}
				} else if (me instanceof JMenuItem) {
					final Object o = ((JMenuItem) me).getParent();
					if (o instanceof MenuElement && o != me) {
						me = (MenuElement) o;
					} else {
						me = null;
					}
				} else {
					me = null;
				}
			}

			newPath = (MenuElement[]) path.toArray(new MenuElement[0]);
		}

		return newPath;
	}

	public void printPath(final javax.swing.MenuElement[] path) {
		System.out.print("path [");
		for (final MenuElement me : path) {
			if (me instanceof javax.swing.JMenu) {
				System.out.print(((javax.swing.JMenu) me).getText() + ", ");
			} else if (me instanceof javax.swing.JPopupMenu) {
				final Object invoker = ((javax.swing.JPopupMenu) me).getInvoker();
				if (invoker instanceof javax.swing.JMenu) {
					System.out.print(((javax.swing.JMenu) invoker).getText() + ".popupMenu, ");
				} else {
					System.out.print("anonymous popupMenu, ");
				}
			} else {
				System.out.print(me.getClass().getName());
			}
		}
		System.out.println("]");
	}
}