package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class RenderCanvas extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderCanvas {

	/**
	 *
	 */
	private static final long serialVersionUID = 2092683606119630989L;

	@Override
	protected native void createNativeInstance(
			edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter renderTargetAdapter);

	@Override
	protected native void releaseNativeInstance();

	@Override
	protected synchronized native boolean acquireDrawingSurface();

	@Override
	protected synchronized native void releaseDrawingSurface();

	@Override
	protected synchronized native void swapBuffers();

	RenderCanvas(
			final edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OnscreenRenderTarget onscreenRenderTarget) {
		super(onscreenRenderTarget);
	}
}
