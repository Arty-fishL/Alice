package edu.cmu.cs.stage3.media.event;

public class PlayerEvent extends java.util.EventObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 7358238300251447437L;

	public PlayerEvent(final edu.cmu.cs.stage3.media.Player source) {
		super(source);
	}

	public edu.cmu.cs.stage3.media.Player getPlayer() {
		return (edu.cmu.cs.stage3.media.Player) getSource();
	}
}
