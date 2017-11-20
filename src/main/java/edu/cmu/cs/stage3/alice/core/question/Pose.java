package edu.cmu.cs.stage3.alice.core.question;

public class Pose extends SubjectQuestion {

	@Override
	public Class<edu.cmu.cs.stage3.alice.core.Pose> getValueClass() {
		return edu.cmu.cs.stage3.alice.core.Pose.class;
	}

	@Override
	protected Object getValue(final edu.cmu.cs.stage3.alice.core.Transformable subjectValue) {
		return edu.cmu.cs.stage3.alice.core.Pose.manufacturePose(subjectValue, subjectValue);
	}
}