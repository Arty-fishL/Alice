package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Stack;

public class StackProperty extends CollectionProperty {
	public StackProperty(final Element owner, final String name, final Stack defaultValue) {
		super(owner, name, defaultValue, Stack.class);
	}

	public Stack getStackValue() {
		return (Stack) getCollectionValue();
	}
}
