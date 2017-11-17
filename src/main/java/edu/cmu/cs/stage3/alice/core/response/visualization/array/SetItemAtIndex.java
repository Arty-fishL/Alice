package edu.cmu.cs.stage3.alice.core.response.visualization.array;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization;
import edu.cmu.cs.stage3.math.HermiteCubic;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;

public class SetItemAtIndex extends ArrayVisualizationWithItemAnimation {
	public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));

	public class RuntimeSetValue extends RuntimeArrayVisualizationWithItemAnimation {
		private Quaternion m_quaternion0;
		private Quaternion m_quaternion1;
		private HermiteCubic m_xHermite;
		private HermiteCubic m_yHermite;
		private HermiteCubic m_zHermite;
		private ArrayOfModelsVisualization m_subject;
		private Model m_item;

		@Override
		public void prologue(final double t) {
			super.prologue(t);
			m_subject = subject.getArrayOfModelsVisualizationValue();
			m_item = item.getModelValue();
			if (m_item != null) {

				// todo?
				m_item.visualization.set(null);

				final Matrix44 transformation0 = m_item.getTransformation(m_subject);
				final Matrix44 transformation1 = new Matrix44(m_subject.getTransformationFor(m_item, index.intValue()));
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
		}

		@Override
		public void update(final double t) {
			super.update(t);
			if (m_item != null) {
				final double portion = getPortion(t);
				final double x = m_xHermite.evaluate(portion);
				final double y = m_yHermite.evaluate(portion);
				final double z = m_zHermite.evaluate(portion);
				m_item.setPositionRightNow(x, y, z, m_subject);
				final edu.cmu.cs.stage3.math.Quaternion q = edu.cmu.cs.stage3.math.Quaternion.interpolate(m_quaternion0,
						m_quaternion1, getPortion(t));
				m_item.setOrientationRightNow(q, m_subject);
			}
		}

		@Override
		public void epilogue(final double t) {
			super.epilogue(t);
			// todo
			final int indexValue = index.intValue();
			final Model[] array = m_subject.getItems().clone();
			for (int i = 0; i < array.length; i++) {
				if (array[i] == m_item) {
					array[i] = null;
				}
			}
			array[indexValue] = m_item;
			m_subject.setItems(array);
		}

	}
}
