package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.scenegraph.Visual;

public class IsCollidingWith extends SubjectObjectQuestion {
	private edu.cmu.cs.stage3.alice.core.World m_world = null;

	@Override
	public Class getValueClass() {
		return Boolean.class;
	}

	@Override
	protected Object getValue(final edu.cmu.cs.stage3.alice.core.Transformable subjectValue,
			final edu.cmu.cs.stage3.alice.core.Transformable objectValue) {
		final edu.cmu.cs.stage3.alice.core.World world = subjectValue.getWorld();
		final edu.cmu.cs.stage3.alice.scenegraph.Visual[] subjectSGVisuals = subjectValue.getAllSceneGraphVisuals();
		final edu.cmu.cs.stage3.alice.scenegraph.Visual[] objectSGVisuals = subjectValue.getAllSceneGraphVisuals();
		final edu.cmu.cs.stage3.alice.scenegraph.Visual[][] collisions = world.getCollisions();
		for (final Visual[] pair : collisions) {
			final Object a = ((edu.cmu.cs.stage3.alice.core.Model) pair[0].getBonus()).getSandbox();
			final Object b = ((edu.cmu.cs.stage3.alice.core.Model) pair[1].getBonus()).getSandbox();
			if (a == subjectValue) {
				if (b == objectValue) {
					return Boolean.TRUE;
				}
			} else if (b == subjectValue) {
				if (a == objectValue) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	@Override
	protected void started(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.started(world, time);
		m_world = world;
		m_world.addCollisionManagementFor(subject.getTransformableValue());
		m_world.addCollisionManagementFor(object.getTransformableValue());
	}

	@Override
	protected void stopped(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.stopped(world, time);
		if (m_world != world) {
			throw new Error();
		}
		m_world.removeCollisionManagementFor(subject.getTransformableValue());
		m_world.removeCollisionManagementFor(object.getTransformableValue());
		m_world = null;
	}
}