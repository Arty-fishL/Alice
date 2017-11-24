package edu.cmu.cs.stage3.progress;

import java.awt.event.ActionListener;

public abstract class ProgressPane extends edu.cmu.cs.stage3.swing.ContentPane
		implements edu.cmu.cs.stage3.progress.ProgressObserver {
	/**
	 *
	 */
	private static final long serialVersionUID = 2589725328539629433L;
	private final javax.swing.JLabel m_descriptionLabel;
	private final javax.swing.JProgressBar m_progressBar;
	private final javax.swing.JButton m_cancelButton;

	private final String m_title;
	private final String m_preDescription;

	private final java.util.Vector<ActionListener> m_okActionListeners = new java.util.Vector<ActionListener>();
	private final java.util.Vector<ActionListener> m_cancelActionListeners = new java.util.Vector<ActionListener>();

	private final int UNKNOWN_TOTAL_MAX = 100;
	private int m_total;

	private boolean m_isCanceled = false;
	private boolean m_isFinished = false;

	public ProgressPane(final String title, final String preDescription) {
		m_title = title;
		m_preDescription = preDescription;

		m_descriptionLabel = new javax.swing.JLabel();

		m_progressBar = new javax.swing.JProgressBar();
		m_progressBar.setPreferredSize(new java.awt.Dimension(240, 16));

		m_cancelButton = new javax.swing.JButton("Cancel");
		m_cancelButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
				onCancel();
			}
		});

		setLayout(new java.awt.GridBagLayout());

		final java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();

		gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;

		gbc.weightx = 1.0;

		gbc.insets.top = 10;
		gbc.insets.left = 10;
		gbc.insets.right = 10;
		add(m_descriptionLabel, gbc);

		gbc.insets.top = 0;
		gbc.insets.bottom = 10;
		add(m_progressBar, gbc);

		gbc.weighty = 1.0;
		add(new javax.swing.JLabel(), gbc);
		gbc.weighty = 0.0;

		gbc.weightx = 0.0;
		gbc.fill = java.awt.GridBagConstraints.NONE;
		gbc.anchor = java.awt.GridBagConstraints.CENTER;
		add(m_cancelButton, gbc);

		// int width = 200;
		// int height = edu.cmu.cs.stage3.math.GoldenRatio.getShorterSideLength(
		// width );
		// setPreferredSize( new java.awt.Dimension( width, height ) );
	}

	@Override
	public void handleDispose() {
		onCancel();
		// DO NOT CALL SUPER
		// super.handleDispose();
	}

	protected abstract void construct() throws ProgressCancelException;

	@Override
	public void preDialogShow(final javax.swing.JDialog dialog) {
		super.preDialogShow(dialog);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					construct();
					fireOKActionListeners();
				} catch (final ProgressCancelException pce) {
					fireCancelActionListeners();
				}
			}
		}).start();
	}

	public boolean isCancelEnabled() {
		return m_cancelButton.isEnabled();
	}

	public void setIsCancelEnabled(final boolean isCancelEnabled) {
		m_cancelButton.setEnabled(isCancelEnabled);
	}

	@Override
	public boolean isReadyToDispose(final int option) {
		if (m_isFinished) {
			return true;
		} else {
			if (isCancelEnabled()) {
				return true;
			} else {
				if (option == edu.cmu.cs.stage3.swing.ContentPane.CANCEL_OPTION) {
					return false;
				} else {
					return true;
				}
			}
		}
	}

	@Override
	public String getTitle() {
		return m_title;
	}

	@Override
	public void addOKActionListener(final java.awt.event.ActionListener l) {
		m_okActionListeners.addElement(l);
	}

	@Override
	public void removeOKActionListener(final java.awt.event.ActionListener l) {
		m_okActionListeners.removeElement(l);
	}

	@Override
	public void addCancelActionListener(final java.awt.event.ActionListener l) {
		m_cancelActionListeners.addElement(l);
	}

	@Override
	public void removeCancelActionListener(final java.awt.event.ActionListener l) {
		m_cancelActionListeners.removeElement(l);
	}

	@Override
	public void progressBegin(final int total) {
		m_descriptionLabel.setText(m_preDescription);
		progressUpdateTotal(total);
		m_isCanceled = false;
		m_isFinished = false;
	}

	@Override
	public void progressUpdateTotal(final int total) {
		m_total = total;
		if (m_total == edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL) {
			m_progressBar.setMaximum(UNKNOWN_TOTAL_MAX);
		} else {
			m_progressBar.setMaximum(m_total);
		}
	}

	@Override
	public void progressUpdate(final int current, final String description) throws ProgressCancelException {
		if (m_isCanceled) {
			m_isCanceled = false;
			throw new ProgressCancelException();
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (m_preDescription != null) {
					if (description != null) {
						m_descriptionLabel.setText(m_preDescription + description);
					} else {
						m_descriptionLabel.setText(m_preDescription);
					}
				}

				if (m_total == edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL) {
					m_progressBar.setValue(current % UNKNOWN_TOTAL_MAX);
				} else {
					m_progressBar.setValue(current);
				}
			}
		});
	}

	@Override
	public void progressEnd() {
	}

	private void onCancel() {
		m_isCanceled = true;
	}

	private void fireActionListeners(final java.util.Vector<ActionListener> actionListeners, final java.awt.event.ActionEvent e) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < actionListeners.size(); i++) {
					final java.awt.event.ActionListener l = actionListeners
							.elementAt(i);
					l.actionPerformed(e);
				}
			}
		});
	}

	protected void fireOKActionListeners() {
		fireActionListeners(m_okActionListeners,
				new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, "OK"));
	}

	protected void fireCancelActionListeners() {
		fireActionListeners(m_cancelActionListeners,
				new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, "Cancel"));
	}
}