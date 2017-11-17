package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.visualization.SetOfModelsVisualization;

public class SetOfModelsVisualizationProperty extends CollectionOfModelsVisualizationProperty {
	public SetOfModelsVisualizationProperty(final Element owner, final String name,
			final SetOfModelsVisualization defaultValue) {
		super(owner, name, defaultValue, SetOfModelsVisualization.class);
	}

	public SetOfModelsVisualization getSetOfModelsVisualizationValue() {
		return (SetOfModelsVisualization) getCollectionOfModelsVisualizationValue();
	}
}
