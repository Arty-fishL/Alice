package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Element;

public class CameraProperty extends ModelProperty {
	public CameraProperty(final Element owner, final String name, final Camera defaultValue) {
		super(owner, name, defaultValue, Camera.class);
	}

	public Camera getCameraValue() {
		return (Camera) getModelValue();
	}
}
