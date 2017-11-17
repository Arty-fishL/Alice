package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;

public class ModelArrayProperty extends ElementArrayProperty {
	public ModelArrayProperty(final Element owner, final String name, final Model[] defaultValue) {
		super(owner, name, defaultValue, Model[].class);
	}

	public Model[] getModelArrayValue() {
		return (Model[]) getElementArrayValue();
	}
}
