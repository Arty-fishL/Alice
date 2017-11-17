package edu.cmu.cs.stage3.alice.core.response.visualization.model;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization;
import edu.cmu.cs.stage3.math.HermiteCubic;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;

public class SetItem extends ModelVisualizationWithItemAnimation {
	public class RuntimeSetItem extends RuntimeModelVisualizationWithItemAnimation {
		private Quaternion m_quaternion0;
		private Quaternion m_quaternion1;
		private HermiteCubic m_xHermite;
		private HermiteCubic m_yHermite;
		private HermiteCubic m_zHermite;
		private ModelVisualization m_subject;
		private Model m_value;

		@Override
		public void prologue(final double t) {
			m_subject = subject.getModelVisualizationValue();
			m_value = item.getModelValue();
			final Model prev = m_subject.getItem();
			if (prev != null && prev != m_value) {
				prev.visualization.set(null);
			}
			if (m_value != null) {

				// todo?
				m_value.visualization.set(null);

				final Matrix44 transformation0 = m_value.getTransformation(m_subject);
				final Matrix44 transformation1 = new Matrix44(m_subject.getTransformationFor(m_value));
				m_quaternion0 = transformation0.getAxes().getQuaternion();
				m_quaternion1 = transformation1.getAxes().getQuaternion();
				final double dx = transformation0.m30 - transformation1.m30;
				final double dy = transformation0.m31 - transformation1.m31;
				final double dz = transformation0.m32 - transformation1.m32;
				final double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
				final double s = distance / 2;
				m_xHermite = new HermiteCubic(transformation0.m30, transformation1.m30, transformation0.m20 * s,
						transformation1.m20 * s);
				m_yHermite = new HermiteCubic(transformation0.m31, transformation1.m31, transformation0.m21 * s,
						transformation1.m21 * s);
				m_zHermite = new HermiteCubic(transformation0.m32, transformation1.m32, transformation0.m22 * s,
						transformation1.m22 * s);
			}
			super.prologue(t);
		}

		@Override
		public void update(final double t) {
			super.update(t);
			if (m_value != null) {
				final double portion = getPortion(t);
				final double x = m_xHermite.evaluate(portion);
				final double y = m_yHermite.evaluate(portion);
				final double z = m_zHermite.evaluate(portion);
				m_value.setPositionRightNow(x, y, z, m_subject);
				final edu.cmu.cs.stage3.math.Quaternion q = edu.cmu.cs.stage3.math.Quaternion.interpolate(m_quaternion0,
						m_quaternion1, getPortion(t));
				m_value.setOrientationRightNow(q, m_subject);
			}
		}

		@Override
		public void epilogue(final double t) {
			super.epilogue(t);
			m_subject.setItem(m_value);
		}
	}
}
