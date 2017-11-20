package edu.cmu.cs.stage3.alice.core.question;

public class CreateCopyOf extends SubjectQuestion {
	private final java.util.Vector<edu.cmu.cs.stage3.alice.core.Model> m_copies = new java.util.Vector<>();

	@Override
	public Class<edu.cmu.cs.stage3.alice.core.Model> getValueClass() {
		return edu.cmu.cs.stage3.alice.core.Model.class;
	}

	@Override
	protected Object getValue(final edu.cmu.cs.stage3.alice.core.Transformable subjectValue) {
		final Class<?>[] classesToShare = { edu.cmu.cs.stage3.alice.core.TextureMap.class,
				edu.cmu.cs.stage3.alice.core.Geometry.class };
		final edu.cmu.cs.stage3.alice.core.Model original = (edu.cmu.cs.stage3.alice.core.Model) subject
				.getTransformableValue();
		final edu.cmu.cs.stage3.alice.core.Model copy = (edu.cmu.cs.stage3.alice.core.Model) original
				.HACK_createCopy(null, original.getParent(), -1, classesToShare, null);
		m_copies.addElement(copy);
		return copy;
	}

	@Override
	protected void started(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.started(world, time);
		m_copies.clear();
	}

	@Override
	protected void stopped(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.stopped(world, time);
		for (int i = 0; i < m_copies.size(); i++) {
			final edu.cmu.cs.stage3.alice.core.Model copy = (edu.cmu.cs.stage3.alice.core.Model) m_copies.elementAt(i);
			copy.vehicle.set(null);
			// copy.getSceneGraphTransformable().setParent( null );
			copy.removeFromParent();
			// todo
			// copy.release();
		}
		m_copies.clear();
	}
}