package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization;

public class ModelVisualizationProperty extends VisualizationProperty {
	public ModelVisualizationProperty(final Element owner, final String name, final ModelVisualization defaultValue) {
		super(owner, name, defaultValue, ModelVisualization.class);
	}

	public ModelVisualization getModelVisualizationValue() {
		return (ModelVisualization) getVisualizationValue();
	}
}
