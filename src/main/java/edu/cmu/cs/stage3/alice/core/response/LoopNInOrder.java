/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;

public class LoopNInOrder extends DoInOrder {
	/** @deprecated */
	@Deprecated
	public final NumberProperty count = new NumberProperty(this, "count", null);

	public final VariableProperty index = new VariableProperty(this, "index", null);

	public final NumberProperty start = new NumberProperty(this, "start", new Double(0));
	public final NumberProperty end = new NumberProperty(this, "end", new Double(Double.POSITIVE_INFINITY));
	public final NumberProperty increment = new NumberProperty(this, "increment", new Double(1));

	private static Class[] s_supportedCoercionClasses = {};

	@Override
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}

	public LoopNInOrder() {
		// count.deprecate();
	}

	@Override
	protected void loadCompleted() {
		super.loadCompleted();
		if (index.get() == null) {
			if (count.get() != null) {
				end.set(count.get());
			}
			final edu.cmu.cs.stage3.alice.core.Variable indexVariable = new edu.cmu.cs.stage3.alice.core.Variable();
			indexVariable.valueClass.set(Number.class);
			indexVariable.name.set("index");
			indexVariable.setParent(this);
			index.set(indexVariable);
		}
	}

	@Override
	protected void internalFindAccessibleExpressions(final Class cls, final java.util.Vector v) {
		internalAddExpressionIfAssignableTo((edu.cmu.cs.stage3.alice.core.Expression) index.get(), cls, v);
		super.internalFindAccessibleExpressions(cls, v);
	}

	public class RuntimeLoopNInOrder extends RuntimeDoInOrder {
		private int m_endTest;

		private double getIndexValue() {
			final edu.cmu.cs.stage3.alice.core.Variable indexVariable = index.getVariableValue();
			final Number number = (Number) indexVariable.value.getValue();
			return number.doubleValue();
		}

		private void setIndexValue(final double value) {
			final edu.cmu.cs.stage3.alice.core.Variable indexVariable = index.getVariableValue();
			indexVariable.value.set(new Double(value));
		}

		@Override
		protected boolean preLoopTest(final double t) {
			return getIndexValue() < m_endTest;
		}

		@Override
		protected boolean postLoopTest(final double t) {
			setIndexValue(getIndexValue() + increment.doubleValue(1));
			return true;
		}

		@Override
		protected boolean isCullable() {
			return false;
		}

		@Override
		public void prologue(final double t) {
			final edu.cmu.cs.stage3.alice.core.Behavior currentBehavior = getCurrentBehavior();
			if (currentBehavior != null) {
				final edu.cmu.cs.stage3.alice.core.Variable indexVariable = index.getVariableValue();
				final edu.cmu.cs.stage3.alice.core.Variable indexRuntimeVariable = new edu.cmu.cs.stage3.alice.core.Variable();
				indexRuntimeVariable.valueClass.set(indexVariable.valueClass.get());
				indexRuntimeVariable.value.set(start.getNumberValue());
				currentBehavior.pushEach(indexVariable, indexRuntimeVariable);
			}
			m_endTest = (int) end.doubleValue(Double.POSITIVE_INFINITY);
			super.prologue(t);
		}

		@Override
		public void epilogue(final double t) {
			super.epilogue(t);
			final edu.cmu.cs.stage3.alice.core.Behavior currentBehavior = getCurrentBehavior();
			if (currentBehavior != null) {
				currentBehavior.popStack();
			}
		}
	}
}
