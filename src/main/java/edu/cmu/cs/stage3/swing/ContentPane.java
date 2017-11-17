package edu.cmu.cs.stage3.swing;

public abstract class ContentPane extends javax.swing.JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = -5321568417116182062L;
	public final static int OK_OPTION = javax.swing.JOptionPane.OK_OPTION;
	public final static int CANCEL_OPTION = javax.swing.JOptionPane.CANCEL_OPTION;

	protected javax.swing.JDialog m_dialog;

	public String getTitle() {
		return getClass().getName();
	}

	public boolean isReadyToDispose(final int option) {
		return true;
	}

	public void handleDispose() {
		m_dialog.dispose();
	}

	public void preDialogShow(final javax.swing.JDialog dialog) {
		m_dialog = dialog;
		m_dialog.setLocationRelativeTo(dialog.getOwner());
	}

	public void postDialogShow(final javax.swing.JDialog dialog) {
		m_dialog = null;
	}

	public void addOKActionListener(final java.awt.event.ActionListener l) {
	}

	public void removeOKActionListener(final java.awt.event.ActionListener l) {
	}

	public void addCancelActionListener(final java.awt.event.ActionListener l) {
	}

	public void removeCancelActionListener(final java.awt.event.ActionListener l) {
	}

	public void setDialogTitle(final String title) {
		if (m_dialog != null) {
			m_dialog.setTitle(title);
		} else {
			// todo: throw Exception?
		}
	}

	public void packDialog() {
		if (m_dialog != null) {
			m_dialog.pack();
		} else {
			// todo: throw Exception?
		}
	}
}