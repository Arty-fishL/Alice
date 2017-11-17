package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.visualization.StackOfModelsVisualization;

public class StackOfModelsVisualizationProperty extends CollectionOfModelsVisualizationProperty {
	public StackOfModelsVisualizationProperty(final Element owner, final String name,
			final StackOfModelsVisualization defaultValue) {
		super(owner, name, defaultValue, StackOfModelsVisualization.class);
	}

	public StackOfModelsVisualization getStackOfModelsVisualizationValue() {
		return (StackOfModelsVisualization) getCollectionOfModelsVisualizationValue();
	}
}
