package edu.cmu.cs.stage3.alice.core.response.visualization.list;

public class Clear extends ListVisualizationAnimation {
	public class RuntimeClear extends RuntimeListVisualizationAnimation {

		@Override
		public void epilogue(final double t) {
			super.epilogue(t);
			getCollection().values.clear();
		}
	}
}
