package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

public class WaitAndUpdateThread extends Thread {
	long millis = 0;
	StencilManager.Stencil stencil = null;
	LayoutChangeListener obj = null;

	public WaitAndUpdateThread(final long millis, final StencilManager.Stencil stencil,
			final LayoutChangeListener obj) {
		this.millis = millis;
		this.stencil = stencil;
		this.obj = obj;
	}

	@Override
	public void run() {

		try {
			Thread.sleep(millis);
		} catch (final java.lang.InterruptedException ie) {
		}

		final boolean success = obj.layoutChanged();

		if (success == false) {
			// System.out.println("update thread - still missing");
			stencil.setErrorStencil(true);
		}
	}
}