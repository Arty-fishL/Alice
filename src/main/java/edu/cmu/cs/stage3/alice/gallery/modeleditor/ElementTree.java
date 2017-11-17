package edu.cmu.cs.stage3.alice.gallery.modeleditor;

class ElementTree extends javax.swing.JTree {
	/**
	 *
	 */
	private static final long serialVersionUID = -8710597801480966663L;

	public ElementTree(final ElementTreeModel model) {
		super(model);
	}

	@Override
	public String convertValueToText(final Object value, final boolean selected, final boolean expanded,
			final boolean leaf, final int row, final boolean hasFocus) {
		final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) value;
		if (element != null) {
			return element.name.getStringValue();
		} else {
			return null;
		}
	}
}
