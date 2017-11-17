package edu.cmu.cs.stage3.alice.gallery.modeleditor;

class ElementTreeCellEditor extends javax.swing.tree.DefaultTreeCellEditor {
	public ElementTreeCellEditor(final javax.swing.JTree tree, final ElementTreeCellRenderer renderer) {
		super(tree, renderer);
	}

	@Override
	public boolean isCellEditable(final java.util.EventObject e) {
		if (e == null) {
			return true;
		} else {
			return super.isCellEditable(e);
		}
	}

	@Override
	protected boolean canEditImmediately(final java.util.EventObject event) {
		if (event instanceof java.awt.event.MouseEvent) {
			final java.awt.event.MouseEvent me = (java.awt.event.MouseEvent) event;
			if ((me.getModifiers() & java.awt.event.InputEvent.BUTTON1_MASK) != 0) {
				return me.getClickCount() > 0;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	protected void prepareForEditing() {
		super.prepareForEditing();
		if (editingComponent instanceof javax.swing.JTextField) {
			((javax.swing.JTextField) editingComponent).selectAll();
		}
	}

	@Override
	public java.awt.Component getTreeCellEditorComponent(final javax.swing.JTree tree, final Object value,
			final boolean selected, final boolean expanded, final boolean leaf, final int row) {
		final java.awt.Component editor = super.getTreeCellEditorComponent(tree, value, selected, expanded, leaf, row);
		editingIcon = IconManager.lookupIcon(value);
		return editor;
	}
}
