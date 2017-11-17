package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Element;

public class CollectionProperty extends ElementProperty {
	protected CollectionProperty(final Element owner, final String name, final Collection defaultValue,
			final Class cls) {
		super(owner, name, defaultValue, cls);
	}

	public CollectionProperty(final Element owner, final String name, final Collection defaultValue) {
		super(owner, name, defaultValue, Collection.class);
	}

	public Collection getCollectionValue() {
		return (Collection) getElementValue();
	}
}
