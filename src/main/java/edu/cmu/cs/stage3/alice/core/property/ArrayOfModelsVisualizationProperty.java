package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization;

public class ArrayOfModelsVisualizationProperty extends CollectionOfModelsVisualizationProperty {
	public ArrayOfModelsVisualizationProperty(final Element owner, final String name,
			final ArrayOfModelsVisualization defaultValue) {
		super(owner, name, defaultValue, ArrayOfModelsVisualization.class);
	}

	public ArrayOfModelsVisualization getArrayOfModelsVisualizationValue() {
		return (ArrayOfModelsVisualization) getCollectionOfModelsVisualizationValue();
	}
}
