package edu.cmu.cs.stage3.media;

import edu.cmu.cs.stage3.media.event.PlayerListener;

public abstract class AbstractPlayer implements Player {
	private boolean m_isAvailable = true;
	private final AbstractDataSource m_dataSource;
	private final java.util.Vector<PlayerListener> m_playerListeners = new java.util.Vector<PlayerListener>();
	private edu.cmu.cs.stage3.media.event.PlayerListener[] m_playerListenerArray;
	private double m_beginTime = 0;
	private double m_endTime = Double.NaN;

	protected AbstractPlayer(final AbstractDataSource dataSource) {
		m_dataSource = dataSource;
	}

	@Override
	public DataSource getDataSource() {
		return m_dataSource;
	}

	@Override
	public boolean isAvailable() {
		return m_isAvailable;
	}

	@Override
	public void setIsAvailable(final boolean isAvailable) {
		m_isAvailable = isAvailable;
	}

	@Override
	public double getBeginTime() {
		return m_beginTime;
	}

	@Override
	public void setBeginTime(final double beginTime) {
		m_beginTime = beginTime;
	}

	@Override
	public double getEndTime() {
		return m_endTime;
	}

	@Override
	public void setEndTime(final double endTime) {
		m_endTime = endTime;
	}

	@Override
	public void startFromBeginning() {
		setCurrentTime(getBeginTime());
		start();
	}

	@Override
	public void addPlayerListener(final edu.cmu.cs.stage3.media.event.PlayerListener l) {
		m_playerListeners.addElement(l);
		m_playerListenerArray = null;
	}

	@Override
	public void removePlayerListener(final edu.cmu.cs.stage3.media.event.PlayerListener l) {
		m_playerListeners.removeElement(l);
		m_playerListenerArray = null;
	}

	@Override
	public edu.cmu.cs.stage3.media.event.PlayerListener[] getPlayerListeners() {
		if (m_playerListenerArray == null) {
			m_playerListenerArray = new edu.cmu.cs.stage3.media.event.PlayerListener[m_playerListeners.size()];
			m_playerListeners.copyInto(m_playerListenerArray);
		}
		return m_playerListenerArray;
	}

	protected void fireDurationUpdated() {
		m_dataSource.fireDurationUpdatedIfNecessary(getDuration());
	}

	protected void fireEndReached() {
		final edu.cmu.cs.stage3.media.event.PlayerEvent e = new edu.cmu.cs.stage3.media.event.PlayerEvent(this);
		final edu.cmu.cs.stage3.media.event.PlayerListener[] playerListeners = getPlayerListeners();
		for (final PlayerListener playerListener : playerListeners) {
			playerListener.endReached(e);
		}
	}

	protected void fireStateChanged() {
		final edu.cmu.cs.stage3.media.event.PlayerEvent e = new edu.cmu.cs.stage3.media.event.PlayerEvent(this);
		final edu.cmu.cs.stage3.media.event.PlayerListener[] playerListeners = getPlayerListeners();
		for (final PlayerListener playerListener : playerListeners) {
			playerListener.stateChanged(e);
		}
	}
}