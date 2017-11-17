package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.camera.ProjectionCamera;

public class ProjectionViewVolumeDecorator extends ViewVolumeDecorator {
	private ProjectionCamera m_projectionCamera = null;

	@Override
	protected Camera getCamera() {
		return getProjectionCamera();
	}

	public ProjectionCamera getProjectionCamera() {
		return m_projectionCamera;
	}

	public void setProjectionCamera(final ProjectionCamera projectionCamera) {
		if (projectionCamera != m_projectionCamera) {
			m_projectionCamera = projectionCamera;
			markDirty();
			updateIfShowing();
		}
	}

	@Override
	protected double[] getXYNearAndXYFar(final double zNear, final double zFar) {
		// todo
		final double angle = 0.5;
		final double aspect = 4.0 / 3.0;
		final double yNear = zNear * Math.tan(angle);
		final double yFar = zFar * Math.tan(angle);
		final double xNear = aspect * yNear;
		final double xFar = aspect * yFar;
		final double[] r = { xNear, yNear, xFar, yFar };
		return r;
	}
}
