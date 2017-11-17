package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;

public class UserDefinedQuestionProperty extends ElementProperty {
	public UserDefinedQuestionProperty(final Element owner, final String name, final UserDefinedQuestion defaultValue) {
		super(owner, name, defaultValue, UserDefinedQuestion.class);
	}

	public UserDefinedQuestion getUserDefinedQuestionValue() {
		return (UserDefinedQuestion) getElementValue();
	}
	// todo: this should not be necessary

	@Override
	protected boolean getValueOfExpression() {
		return false;
	}
}
