package edu.cmu.cs.stage3.media.event;

public class DataSourceEvent extends java.util.EventObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 873199011784517537L;

	public DataSourceEvent(final edu.cmu.cs.stage3.media.DataSource source) {
		super(source);
	}

	public edu.cmu.cs.stage3.media.DataSource getDataSource() {
		return (edu.cmu.cs.stage3.media.DataSource) getSource();
	}
}
