package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;

public class IsObjectInView extends SubjectObjectQuestion {
	private boolean isObjectInView(final Transformable subjectValue, final Transformable objectValue) {
		return true;
	}

	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}

	@Override
	protected Object getValue(final Transformable subjectValue, final Transformable objectValue) {
		if (isObjectInView(subjectValue, objectValue)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}