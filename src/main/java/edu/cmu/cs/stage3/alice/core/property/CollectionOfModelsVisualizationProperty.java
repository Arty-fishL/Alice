package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization;

public abstract class CollectionOfModelsVisualizationProperty extends VisualizationProperty {
	protected CollectionOfModelsVisualizationProperty(final Element owner, final String name,
			final CollectionOfModelsVisualization defaultValue, final Class<?> valueClass) {
		super(owner, name, defaultValue, valueClass);
	}

	public CollectionOfModelsVisualization getCollectionOfModelsVisualizationValue() {
		return (CollectionOfModelsVisualization) getVisualizationValue();
	}
}
