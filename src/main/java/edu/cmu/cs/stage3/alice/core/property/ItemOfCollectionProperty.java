package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Element;

public class ItemOfCollectionProperty extends ObjectProperty {
	private Collection m_collection = null;

	public ItemOfCollectionProperty(final Element owner, final String name) {
		super(owner, name, null, Object.class);
	}

	public void setCollection(final Collection collection) {
		m_collection = collection;
	}

	@Override
	public Class<?> getValueClass() {
		if (m_collection != null) {
			return m_collection.valueClass.getClassValue();
		} else {
			return super.getValueClass();
		}
	}
}
