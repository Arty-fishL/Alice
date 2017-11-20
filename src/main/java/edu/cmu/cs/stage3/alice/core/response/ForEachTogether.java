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

public class ForEachTogether extends ForEach {
	// todo:
	// private static Class<?>[] s_supportedCoercionClasses = {
	// ForEachInOrder.class };
	private static Class<?>[] s_supportedCoercionClasses = {};

	@Override
	public Class<?>[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}

	public class RuntimeForEachTogether extends RuntimeForEach {
		private RuntimeResponse[][] m_runtimeResponsesArray = null;
		private int m_listIndex;

		@Override
		protected RuntimeResponse[] getRuntimeResponses() {
			if (m_listIndex >= 0 && m_listIndex < m_runtimeResponsesArray.length) {
				return m_runtimeResponsesArray[m_listIndex];
			} else {
				throw new ArrayIndexOutOfBoundsException(
						m_listIndex + " is out of bounds [0," + m_runtimeResponsesArray.length + ").");
			}
		}

		@Override
		protected int getChildCount() {
			if (m_runtimeResponsesArray != null && m_runtimeResponsesArray.length > 0) {
				return m_runtimeResponsesArray[0].length;
			} else {
				return 0;
			}
		}

		@Override
		protected double getChildTimeRemaining(final int index, final double t) {
			double timeRemaining = 0;
			for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex++) {
				timeRemaining = Math.max(timeRemaining,
						m_runtimeResponsesArray[m_listIndex][index].getTimeRemaining(t));
			}
			return timeRemaining;
		}

		@Override
		protected void childPrologueIfNecessary(final int index, final double t) {
			for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex++) {
				setForkIndex(m_listIndex);
				super.childPrologueIfNecessary(index, t);
			}
			setForkIndex(-1);
		}

		@Override
		protected void childUpdate(final int index, final double t) {
			for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex++) {
				setForkIndex(m_listIndex);
				super.childUpdate(index, t);
			}
			setForkIndex(-1);
		}

		@Override
		protected void childEpilogue(final int index, final double t) {
			for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex++) {
				setForkIndex(m_listIndex);
				super.childEpilogue(index, t);
			}
			setForkIndex(-1);
		}

		@Override
		protected void childrenEpiloguesIfNecessary(final double t) {
			for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex++) {
				super.childrenEpiloguesIfNecessary(t);
			}
		}

		// protected void childrenEpiloguesIfNecessary( double t ) {
		// for( m_listIndex=0; m_listIndex<m_runtimeResponsesArray.length;
		// m_listIndex++ ) {
		// preEach( m_listIndex );
		// super.childrenEpiloguesIfNecessary( t );
		// postEach();
		// }
		// }

		@Override
		public void prologue(final double t) {
			super.prologue(t);
			m_runtimeResponsesArray = new RuntimeResponse[m_listSize][];
			for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex++) {
				setForkIndex(m_listIndex);
				m_runtimeResponsesArray[m_listIndex] = manufactureComponentRuntimeResponses(componentResponses);
			}
			setForkIndex(-1);
		}
	}
}
