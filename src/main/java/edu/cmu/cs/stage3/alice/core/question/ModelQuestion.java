package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Model;

public abstract class ModelQuestion extends edu.cmu.cs.stage3.alice.core.Question {

	@Override
	public Class<Model> getValueClass() {
		return Model.class;
	}
}