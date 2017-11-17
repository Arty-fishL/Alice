package edu.cmu.cs.stage3.media.nullmedia;

public class DataSource extends edu.cmu.cs.stage3.media.AbstractDataSource {
	private final byte[] m_data;

	public DataSource(final byte[] data, final String extension) {
		super(extension);
		m_data = data;
	}

	@Override
	public byte[] getData() {
		return m_data;
	}

	@Override
	protected edu.cmu.cs.stage3.media.Player createPlayer() {
		return new Player(this);
	}

	public double waitForDuration(final long timeout) {
		return getDurationHint();
	}
}