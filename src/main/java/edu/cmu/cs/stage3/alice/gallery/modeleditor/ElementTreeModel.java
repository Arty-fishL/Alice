package edu.cmu.cs.stage3.alice.gallery.modeleditor;

import javax.swing.event.TreeModelListener;

import edu.cmu.cs.stage3.alice.core.Element;

class ElementTreeModel implements javax.swing.tree.TreeModel {
	private edu.cmu.cs.stage3.alice.core.Element m_root;
	private final java.util.Vector<TreeModelListener> m_treeModelListeners = new java.util.Vector<TreeModelListener>();

	private Object[] getPath(edu.cmu.cs.stage3.alice.core.Element element) {
		final java.util.Vector<Element> v = new java.util.Vector<Element>();
		while (element != m_root.getParent()) {
			v.insertElementAt(element, 0);
			element = element.getParent();
		}
		return v.toArray();
	}

	private boolean isAccepted(final edu.cmu.cs.stage3.alice.core.Element e) {
		if (e instanceof edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray) {
			return false;
		} else if (e instanceof edu.cmu.cs.stage3.alice.core.Response) {
			return e instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
		} else if (e instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component) {
			return false;
		} else {
			return true;
		}
	}

	private void fireTreeStructureChanged(final Object[] path) {
		final javax.swing.event.TreeModelEvent e = new javax.swing.event.TreeModelEvent(this, path);
		final java.util.Enumeration<TreeModelListener> enum0 = m_treeModelListeners.elements();
		while (enum0.hasMoreElements()) {
			final javax.swing.event.TreeModelListener l = enum0.nextElement();
			l.treeStructureChanged(e);
		}
	}

	public void setRoot(final edu.cmu.cs.stage3.alice.core.Element root) {
		m_root = root;
		fireTreeStructureChanged(getPath(m_root));
	}

	@Override
	public void addTreeModelListener(final javax.swing.event.TreeModelListener l) {
		m_treeModelListeners.addElement(l);
	}

	@Override
	public void removeTreeModelListener(final javax.swing.event.TreeModelListener l) {
		m_treeModelListeners.removeElement(l);
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		final edu.cmu.cs.stage3.alice.core.Element parentElement = (edu.cmu.cs.stage3.alice.core.Element) parent;
		// return parentElement.getChildAt( index );
		int i = 0;
		for (int lcv = 0; lcv < parentElement.getChildCount(); lcv++) {
			final edu.cmu.cs.stage3.alice.core.Element childAtLCV = parentElement.getChildAt(lcv);
			if (isAccepted(childAtLCV)) {
				if (i == index) {
					return childAtLCV;
				}
				i++;
			}
		}
		return null;
	}

	@Override
	public int getChildCount(final Object parent) {
		final edu.cmu.cs.stage3.alice.core.Element parentElement = (edu.cmu.cs.stage3.alice.core.Element) parent;
		// return parentElement.getChildCount();
		int i = 0;
		for (int lcv = 0; lcv < parentElement.getChildCount(); lcv++) {
			final edu.cmu.cs.stage3.alice.core.Element childAtLCV = parentElement.getChildAt(lcv);
			if (isAccepted(childAtLCV)) {
				i++;
			}
		}
		return i;
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		final edu.cmu.cs.stage3.alice.core.Element parentElement = (edu.cmu.cs.stage3.alice.core.Element) parent;
		// return parentElement.getIndexOfChild(
		// (edu.cmu.cs.stage3.alice.core.Element)child );
		int i = 0;
		for (int lcv = 0; lcv < parentElement.getChildCount(); lcv++) {
			final edu.cmu.cs.stage3.alice.core.Element childAtLCV = parentElement.getChildAt(lcv);
			if (childAtLCV == child) {
				return i;
			}
			if (isAccepted(childAtLCV)) {
				i++;
			}
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return m_root;
	}

	@Override
	public boolean isLeaf(final Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public void valueForPathChanged(final javax.swing.tree.TreePath path, final Object newValue) {
		// System.out.println( "*** valueForPathChanged : " + path + " --> " +
		// newValue );
	}

	public void removeDescendant(final edu.cmu.cs.stage3.alice.core.Element descendant) {
		final Object[] path = getPath(descendant.getParent());
		descendant.removeFromParent();
		fireTreeStructureChanged(path);
	}
}
