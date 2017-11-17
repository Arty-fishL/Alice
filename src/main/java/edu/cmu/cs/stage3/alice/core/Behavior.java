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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;

public abstract class Behavior extends Element {
	public final BooleanProperty isEnabled = new BooleanProperty(this, "isEnabled", Boolean.TRUE);
	public final ElementArrayProperty details = new ElementArrayProperty(this, "details", null, Expression[].class);

	private double m_prevT;
	// private boolean m_exceptionHasBeenPreviouslyThrown = false;
	private boolean m_isActive = false;

	private class RuntimeStack {
		private final Item m_front = new Single();

		public void clear() {
			m_front.setNext(null);
		}

		public void push(final Item item) {
			final Item t = top();
			t.setNext(item);
			item.setPrev(t);
		}

		public void pop() {
			final Item t = top();
			t.getPrev().setNext(null);
			t.setPrev(null);
		}

		public Item top() {
			Item prev = m_front;
			while (true) {
				final Item curr = prev.getNext();
				if (curr == null) {
					return prev;
				} else {
					prev = curr;
				}
			}
		}

		public java.util.Stack getCurrentStack() {
			final java.util.Stack stack = new java.util.Stack();
			Item prev = m_front;
			while (true) {
				final Item curr = prev.getNext();
				if (curr == null) {
					break;
				} else {
					prev = curr;
					stack.push(prev);
				}
			}
			return stack;
		}
	}

	private abstract class Item {
		private Item m_prev;

		public Item getPrev() {
			return m_prev;
		}

		protected void setPrev(final Item prev) {
			m_prev = prev;
		}

		public abstract Item getNext();

		public abstract void setNext(Item next);

		public Variable lookup(final Variable variable) {
			if (m_prev == null) {
				return variable;
			} else {
				return m_prev.lookup(variable);
			}
		}

		public Variable lookup(final String name) {
			if (m_prev == null) {
				return null;
			} else {
				return m_prev.lookup(name);
			}
		}
	}

	private class Fork extends Item {
		public Fork(final int n) {
			m_nexts = new Item[n];
			m_index = -1;
		}

		private final Item[] m_nexts;
		private int m_index;

		@Override
		public Item getNext() {
			if (m_index < 0 || m_index >= m_nexts.length) {
				// throw new ArrayIndexOutOfBoundsException( m_index +
				// " not in range [0," + m_nexts.length +")" );
				m_index = 0;
			}
			return m_nexts[m_index];
		}

		@Override
		public void setNext(final Item item) {
			m_nexts[m_index] = item;
		}

		public void setIndex(final int index) {
			m_index = index;
		}
	}

	private class Single extends Item {
		private Item m_next;

		@Override
		public Item getNext() {
			return m_next;
		}

		@Override
		public void setNext(final Item next) {
			m_next = next;
		}
	}

	private class Context extends Single {
		private final java.util.Dictionary m_variableMap = new java.util.Hashtable();
		private final java.util.Dictionary m_nameMap = new java.util.Hashtable();
		private boolean m_isCeiling = true;

		@Override
		public Variable lookup(final Variable variable) {
			final Variable runtimeVariable = (Variable) m_variableMap.get(variable);
			if (runtimeVariable != null) {
				return runtimeVariable;
			} else {
				if (m_isCeiling) {
					return variable;
				} else {
					return getPrev().lookup(variable);
				}
			}
		}

		@Override
		public Variable lookup(final String name) {
			final Variable runtimeVariable = (Variable) m_nameMap.get(name);
			if (runtimeVariable != null) {
				return runtimeVariable;
			} else {
				if (m_isCeiling) {
					return null;
				} else {
					return getPrev().lookup(name);
				}
			}
		}
	}

	public void openFork(final Object key, final int n) {
		final Fork fork = new Fork(n);
		m_forkMap.put(key, fork);
		m_stack.push(fork);
	}

	public void setForkIndex(final Object key, final int i) {
		final Fork fork = (Fork) m_forkMap.get(key);
		fork.setIndex(i);
	}

	public void closeFork(final Object key) {
		if (m_isActive) {
			m_stack.pop();
			m_forkMap.remove(key);
		}
	}

	public java.util.Stack getCurrentStack() {
		return m_stack.getCurrentStack();
	}

	// private java.util.Stack m_stack = new java.util.Stack();
	private final RuntimeStack m_stack = new RuntimeStack();
	private final java.util.Hashtable m_detailNameMap = new java.util.Hashtable();
	private final java.util.Hashtable m_forkMap = new java.util.Hashtable();

	public void manufactureAnyNecessaryDetails() {
	}

	protected void enabled() {
	}

	protected void disabled() {
	}

	@Override
	protected void propertyChanged(final Property property, final Object value) {
		if (property == isEnabled) {
			if (m_isActive) {
				if (value == Boolean.TRUE) {
					enabled();
				} else {
					disabled();
				}
			}
		} else {
			super.propertyChanged(property, value);
		}
	}

	public void manufactureDetails() {
	}

	public void preSchedule(final double t) {
	}

	public void postSchedule(final double t) {
	}

	protected abstract void internalSchedule(double time, double dt);

	public abstract void stopAllRuntimeResponses(double time);

	public void schedule(final double time) {
		// if( m_exceptionHasBeenPreviouslyThrown ) {
		// return;
		// }
		if (isEnabled.booleanValue()) {
			final double dt = time - m_prevT;
			if (dt > 0) {
				// try {
				internalSchedule(time, dt);
				// } catch( Throwable t ) {
				// t.printStackTrace();
				// m_exceptionHasBeenPreviouslyThrown = true;
				// }
				m_prevT = time;
			} else {
				// pass
			}
		}
	}
	/*
	 * public Expression lookup( String key ) { Expression value = null; if(
	 * true ) { //todo... only if configuring current runtime response? value =
	 * (Expression)m_detailMap.get( key ); } if( value == null ) {
	 * Response.RuntimeResponse currentRuntimeResponse =
	 * getCurrentRuntimeResponse(); if( currentRuntimeResponse != null ) {
	 * Response.RuntimeResponse leaf = currentRuntimeResponse.getLeaf(); if(
	 * leaf != null ) { value = leaf.lookup( key ); } else { //debugln(
	 * "WARNING: leaf is null" ); } } else { //debugln(
	 * "WARNING: currentRuntimeResponse is null: " + this ); } } return value; }
	 */

	private Variable createRuntimeVariable(final Variable other) {
		final Variable v = new Variable();
		v.name.set(other.name.getStringValue());
		final Class cls = other.getValueClass();
		v.valueClass.set(cls);
		final Object value = other.getValue();
		v.value.set(value);
		return v;
	}

	public Variable stackLookup(final Variable variable) {
		final Variable returnValue = m_stack.top().lookup(variable);
		return returnValue;
	}

	public Variable stackLookup(final String name) {
		return m_stack.top().lookup(name);
	}

	/*
	 * public Variable stackLookup( Variable variable ) { for( int
	 * i=m_stack.size()-1; i>=0; i-- ) { Context context =
	 * (Context)m_stack.elementAt( i ); Variable runtimeVariable =
	 * (Variable)context.variableMap.get( variable ); if( runtimeVariable !=
	 * null ) { return runtimeVariable; } else { if( context.isCeiling ) {
	 * break; } } } return null; } public Variable stackLookup( String name ) {
	 * for( int i=m_stack.size()-1; i>=0; i-- ) { Context context =
	 * (Context)m_stack.elementAt( i ); Variable runtimeVariable =
	 * (Variable)context.nameMap.get( name ); if( runtimeVariable != null ) {
	 * return runtimeVariable; } else { if( context.isCeiling ) { break; } } }
	 * return null; }
	 */
	public Expression detailLookup(final String name) {
		return (Expression) m_detailNameMap.get(name);
	}

	public void pushEach(final Variable variable, final Variable runtimeVariable) {
		final Context context = new Context();
		context.m_isCeiling = false;
		context.m_variableMap.put(variable, runtimeVariable);
		context.m_nameMap.put(variable.name.getStringValue(), runtimeVariable);
		m_stack.push(context);
	}

	public void pushStack(final Variable[] variables, final boolean isCeiling) {
		final Context context = new Context();
		context.m_isCeiling = isCeiling;
		for (final Variable variable : variables) {
			final Variable runtimeVariable = createRuntimeVariable(variable);
			context.m_variableMap.put(variable, runtimeVariable);
			context.m_nameMap.put(variable.name.getStringValue(), runtimeVariable);
		}
		m_stack.push(context);
	}

	public void pushStack(final Variable[] actualRequired, final Variable[] actualKeyword,
			final Variable[] formalRequired, final Variable[] formalKeyword, final Variable[] localVariables,
			final boolean isCeiling) {
		final Context context = new Context();
		context.m_isCeiling = isCeiling;
		for (final Variable formal : formalRequired) {
			final String nameValue = formal.name.getStringValue();
			for (int j = 0; j < actualRequired.length; j++) {
				final Variable actual = actualRequired[j];
				if (nameValue.equals(actual.name.getStringValue())) {
					final Variable runtime = createRuntimeVariable(actual);
					context.m_nameMap.put(nameValue, runtime);
					context.m_variableMap.put(formal, runtime);
					break;
				} else if (j == actualRequired.length - 1) {
					throw new RuntimeException("missing required parameter: " + nameValue);
				}
			}
		}
		// todo: keyword
		for (final Variable localVariable : localVariables) {
			final Variable runtime = createRuntimeVariable(localVariable);
			context.m_nameMap.put(localVariable.name.getStringValue(), runtime);
			context.m_variableMap.put(localVariable, runtime);
		}
		m_stack.push(context);
	}

	public void popStack() {
		if (m_isActive) {
			final Object context = m_stack.top();
			// System.err.println( "popStack: " + context.hashCode() );
			// Thread.dumpStack();
			m_stack.pop();
		}
	}

	@Override
	protected void internalFindAccessibleExpressions(final Class cls, final java.util.Vector v) {
		for (int i = 0; i < details.size(); i++) {
			internalAddExpressionIfAssignableTo((Expression) details.get(i), cls, v);
		}
		super.internalFindAccessibleExpressions(cls, v);
	}

	@Override
	protected void started(final World world, final double time) {
		super.started(world, time);
		m_prevT = time;
		m_stack.clear();
		m_detailNameMap.clear();
		m_forkMap.clear();
		for (int i = 0; i < details.size(); i++) {
			final Expression detail = (Expression) details.get(i);
			m_detailNameMap.put(detail.name.getStringValue(), detail);
		}
		m_isActive = true;
		// m_exceptionHasBeenPreviouslyThrown = false;
	}

	@Override
	protected void stopped(final World world, final double time) {
		super.stopped(world, time);
		m_isActive = false;
		stopAllRuntimeResponses(time);
		m_stack.clear();
		m_detailNameMap.clear();
		m_forkMap.clear();
	}
}
