package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;

public class ModelProperty extends TransformableProperty {
	protected ModelProperty(final Element owner, final String name, final Model defaultValue, final Class<?> valueClass) {
		super(owner, name, defaultValue, valueClass);
	}

	public ModelProperty(final Element owner, final String name, final Model defaultValue) {
		this(owner, name, defaultValue, Model.class);
	}

	public Model getModelValue() {
		return (Model) getTransformableValue();
	}
}
