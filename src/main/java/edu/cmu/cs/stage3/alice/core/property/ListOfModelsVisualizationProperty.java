package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization;

public class ListOfModelsVisualizationProperty extends CollectionOfModelsVisualizationProperty {
	public ListOfModelsVisualizationProperty(final Element owner, final String name,
			final ListOfModelsVisualization defaultValue) {
		super(owner, name, defaultValue, ListOfModelsVisualization.class);
	}

	public ListOfModelsVisualization getListOfModelsVisualizationValue() {
		return (ListOfModelsVisualization) getCollectionOfModelsVisualizationValue();
	}
}
