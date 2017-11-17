package edu.cmu.cs.stage3.alice.gallery.modeleditor;

class ElementTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = -448767655974850984L;

	@Override
	public java.awt.Component getTreeCellRendererComponent(final javax.swing.JTree tree, final Object value,
			final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		openIcon = closedIcon = leafIcon = IconManager.lookupIcon(value);
		final java.awt.Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf,
				row, hasFocus);
		if (value instanceof edu.cmu.cs.stage3.alice.core.TextureMap) {
			final edu.cmu.cs.stage3.alice.core.TextureMap tm = (edu.cmu.cs.stage3.alice.core.TextureMap) value;
			final java.awt.image.BufferedImage image = (java.awt.image.BufferedImage) tm.getSceneGraphTextureMap()
					.getImage();
			final StringBuffer sb = new StringBuffer();
			sb.append(image.getWidth());
			sb.append('x');
			sb.append(image.getHeight());
			setToolTipText(sb.toString());
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Model) {
			final edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model) value;
			final edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
			model.visit(itaCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
			final StringBuffer sb = new StringBuffer();
			sb.append("vertices: ");
			sb.append(itaCounter.getVertexCount());
			sb.append("; triangles: ");
			sb.append(itaCounter.getIndexCount() / 3);
			setToolTipText(sb.toString());
		} else {
			setToolTipText(null);
		}
		return component;
	}
}
