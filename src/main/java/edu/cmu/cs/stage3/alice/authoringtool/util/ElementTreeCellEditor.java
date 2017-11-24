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

import javax.swing.event.CellEditorListener;

/**
 * @author Jason Pratt
 */
public class ElementTreeCellEditor extends ElementTreeCellRenderer implements javax.swing.tree.TreeCellEditor {
	/**
	 *
	 */
	private static final long serialVersionUID = -8359889682103239140L;
	protected javax.swing.JTextField textField;
	protected java.util.HashSet<CellEditorListener> cellEditorListeners;
	protected edu.cmu.cs.stage3.alice.core.Element element;
	protected long lastClickTime;
	protected long editDelay = 500;
	// protected boolean isSelected; // determined solely from isCellEditable
	// calls, not from getTreeCellEditorComponent

	synchronized protected void initializeIfNecessary() {
		if (textField == null) {
			textField = new javax.swing.JTextField();
			cellEditorListeners = new java.util.HashSet<CellEditorListener>();

			elementPanel.remove(elementLabel);
			elementPanel.add(textField,
					new java.awt.GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER,
							java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 2, 0, 2), 0, 0));

			textField.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent ev) {
					stopCellEditing();
				}
			});
			textField.addKeyListener(new java.awt.event.KeyAdapter() {

				@Override
				public void keyPressed(final java.awt.event.KeyEvent ev) {
					if (ev.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
						cancelCellEditing();
					}
				}
			});
			textField.addFocusListener(new java.awt.event.FocusAdapter() {

				@Override
				public void focusLost(final java.awt.event.FocusEvent ev) {
					if (!ev.isTemporary()) {
						stopCellEditing();
					}
				}
			});

			dndPanel.addMouseListener(new java.awt.event.MouseAdapter() {

				@Override
				public void mousePressed(final java.awt.event.MouseEvent ev) {
					ElementTreeCellEditor.this.stopCellEditing();
				}
			});
		}
	}

	@Override
	public java.awt.Component getTreeCellEditorComponent(final javax.swing.JTree tree, final Object value,
			final boolean isSelected, final boolean isExpanded, final boolean isLeaf, final int row) {
		initializeIfNecessary();
		if (value instanceof edu.cmu.cs.stage3.alice.core.Element) {
			element = (edu.cmu.cs.stage3.alice.core.Element) value;
			iconLabel.setIcon(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue(element));
			textField.setText(element.name.getStringValue());
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error: not an Element: " + value,
					null);
		}
		return this;
	}

	@Override
	public void addCellEditorListener(final javax.swing.event.CellEditorListener listener) {
		initializeIfNecessary();
		cellEditorListeners.add(listener);
	}

	@Override
	public void removeCellEditorListener(final javax.swing.event.CellEditorListener listener) {
		initializeIfNecessary();
		cellEditorListeners.remove(listener);
	}

	@Override
	public void cancelCellEditing() {
		initializeIfNecessary();
		textField.setText(element.name.getStringValue());
		fireCellEditingCancelled();
	}

	@Override
	public boolean stopCellEditing() {
		initializeIfNecessary();
		try {
			element.name.set(textField.getText());
			fireCellEditingStopped();
			return true;
		} catch (final edu.cmu.cs.stage3.alice.core.IllegalNameValueException e) {
			// ErrorDialog.showErrorDialog( e.getMessage(), e );
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(e.getMessage(), "Error setting name",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	@Override
	public Object getCellEditorValue() {
		initializeIfNecessary();
		return textField.getText();
	}

	@Override
	synchronized public boolean isCellEditable(final java.util.EventObject ev) {
		boolean isSelected = false;
		if (ev instanceof java.awt.event.MouseEvent && ev.getSource() instanceof javax.swing.JTree) {
			final java.awt.event.MouseEvent mev = (java.awt.event.MouseEvent) ev;
			final javax.swing.JTree tree = (javax.swing.JTree) ev.getSource();
			final int row = tree.getRowForLocation(mev.getX(), mev.getY());
			isSelected = tree.isRowSelected(row);
		}

		if (ev instanceof java.awt.event.MouseEvent) {
			final long time = System.currentTimeMillis();

			final java.awt.event.MouseEvent mev = (java.awt.event.MouseEvent) ev;
			if (javax.swing.SwingUtilities.isLeftMouseButton(mev)) {
				if (mev.getClickCount() > 2) {
					return true;
				} else if (isSelected && time - lastClickTime > editDelay) {
					return true;
				}
			}
			lastClickTime = time;
		} else if (ev == null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean shouldSelectCell(final java.util.EventObject ev) {
		return true;
	}

	public void selectText() {
		initializeIfNecessary();
		// System.out.println( "selectAll" );
		textField.selectAll();
	}

	protected void fireCellEditingCancelled() {
		initializeIfNecessary();
		final javax.swing.event.ChangeEvent ev = new javax.swing.event.ChangeEvent(this);
		for (final java.util.Iterator<CellEditorListener> iter = cellEditorListeners.iterator(); iter.hasNext();) {
			iter.next().editingCanceled(ev);
		}
	}

	protected void fireCellEditingStopped() {
		initializeIfNecessary();
		final javax.swing.event.ChangeEvent ev = new javax.swing.event.ChangeEvent(this);
		for (final java.util.Iterator<CellEditorListener> iter = cellEditorListeners.iterator(); iter.hasNext();) {
			iter.next().editingStopped(ev);
		}
	}
}