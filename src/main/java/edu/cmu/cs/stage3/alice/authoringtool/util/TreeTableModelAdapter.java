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

/*
 * %W% %E%
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * This is a wrapper class takes a TreeTableModel and implements the table model
 * interface. The implementation is trivial, with all of the event dispatching
 * support provided by the superclass: the AbstractTableModel.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */

public class TreeTableModelAdapter extends AbstractTableModel implements TypedTableModel {
	/**
	 *
	 */
	private static final long serialVersionUID = 2853123205024708059L;
	JTree tree;
	TreeTableModel treeTableModel;

	public TreeTableModelAdapter(final TreeTableModel treeTableModel, final JTree tree) {
		this.tree = tree;
		this.treeTableModel = treeTableModel;

		tree.addTreeExpansionListener(new TreeExpansionListener() {
			// Don't use fireTableRowsInserted() here;
			// the selection model would get updated twice.
			@Override
			public void treeExpanded(final TreeExpansionEvent event) {
				TreeTableModelAdapter.this.fireTableDataChanged();
			}

			@Override
			public void treeCollapsed(final TreeExpansionEvent event) {
				TreeTableModelAdapter.this.fireTableDataChanged();
			}
		});
	}

	// Wrappers, implementing TableModel interface.

	@Override
	public int getColumnCount() {
		return treeTableModel.getColumnCount();
	}

	@Override
	public String getColumnName(final int column) {
		return treeTableModel.getColumnName(column);
	}

	@Override
	public Class<?> getColumnClass(final int column) {
		return treeTableModel.getColumnClass(column);
	}

	@Override
	public int getRowCount() {
		return tree.getRowCount();
	}

	public Object nodeForRow(final int row) {
		final TreePath treePath = tree.getPathForRow(row);
		return treePath.getLastPathComponent();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		return treeTableModel.getValueAt(nodeForRow(row), column);
	}

	@Override
	public Class<?> getTypeAt(final int row, final int column) {
		return treeTableModel.getTypeAt(nodeForRow(row), column);
	}

	@Override
	public boolean isNullValidAt(final int row, final int column) {
		return treeTableModel.isNullValidAt(nodeForRow(row), column);
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return treeTableModel.isCellEditable(nodeForRow(row), column);
	}

	@Override
	public void setValueAt(final Object value, final int row, final int column) {
		treeTableModel.setValueAt(value, nodeForRow(row), column);
	}
}
