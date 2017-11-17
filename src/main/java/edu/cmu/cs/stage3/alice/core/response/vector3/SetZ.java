package edu.cmu.cs.stage3.alice.core.response.vector3;

public class SetZ extends Vector3Response {
	public class RuntimeSetZ extends RuntimeVector3Response {

		@Override
		protected void set(final javax.vecmath.Vector3d vector3, final double v) {
			vector3.z = v;
		}
	}
}
