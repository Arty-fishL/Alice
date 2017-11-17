package edu.cmu.cs.stage3.progress;

public class ProgressCancelException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 3342083982224152295L;

	public ProgressCancelException() {
	}

	public ProgressCancelException(final String detail) {
		super(detail);
	}
}