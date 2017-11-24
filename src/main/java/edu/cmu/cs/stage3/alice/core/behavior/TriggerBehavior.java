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

package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;

public class TriggerBehavior extends Behavior {
	public final ResponseProperty triggerResponse = new ResponseProperty(this, "triggerResponse", null);
	public final ObjectProperty multipleRuntimeResponsePolicy = new ObjectProperty(this,
			"multipleRuntimeResponsePolicy", MultipleRuntimeResponsePolicy.ENQUEUE_MULTIPLE,
			MultipleRuntimeResponsePolicy.class);
	private final java.util.Vector<RuntimeResponse> m_runtimeResponses = new java.util.Vector<RuntimeResponse>();
	private Response.RuntimeResponse[] m_runtimeResponseArray = null;

	private Response.RuntimeResponse[] getRuntimeResponseArray() {
		if (m_runtimeResponseArray == null) {
			m_runtimeResponseArray = new Response.RuntimeResponse[m_runtimeResponses.size()];
			m_runtimeResponses.copyInto(m_runtimeResponseArray);
		}
		return m_runtimeResponseArray;
	}

	public void trigger(final double time) {
		// debugln( "trigger: " + time );
		if (m_runtimeResponses.size() > 0) {
			if (multipleRuntimeResponsePolicy.getValue() == MultipleRuntimeResponsePolicy.IGNORE_MULTIPLE) {
				return;
			}
		}
		final Response response = triggerResponse.getResponseValue();
		if (response != null) {
			final Response.RuntimeResponse runtimeResponse = response.manufactureRuntimeResponse();
			m_runtimeResponses.addElement(runtimeResponse);
			m_runtimeResponseArray = null;
		}
	}

	public void trigger() {
		trigger(System.currentTimeMillis() * 0.001);
	}

	@Override
	protected void internalSchedule(final double time, final double dt) {
		final MultipleRuntimeResponsePolicy mrrp = (MultipleRuntimeResponsePolicy) multipleRuntimeResponsePolicy
				.getValue();
		final Response.RuntimeResponse[] rra = getRuntimeResponseArray();
		for (final RuntimeResponse element : rra) {
			final Response.RuntimeResponse runtimeResponse = element;
			if (!runtimeResponse.isActive()) {
				runtimeResponse.prologue(time);
			}
			runtimeResponse.update(time);
			final double timeRemaining = runtimeResponse.getTimeRemaining(time);
			if (timeRemaining <= 0) {
				runtimeResponse.epilogue(time);
				runtimeResponse.HACK_markForRemoval();
			} else {
				if (mrrp != MultipleRuntimeResponsePolicy.INTERLEAVE_MULTIPLE) {
					break;
				}
			}
		}
		if (m_runtimeResponses.size() > 0) {
			synchronized (m_runtimeResponses) {
				final java.util.Enumeration<RuntimeResponse> enum0 = m_runtimeResponses.elements();
				while (enum0.hasMoreElements()) {
					final Response.RuntimeResponse runtimeResponse = enum0.nextElement();
					if (runtimeResponse.HACK_isMarkedForRemoval()) {
						m_runtimeResponses.removeElement(runtimeResponse);
						m_runtimeResponseArray = null;
					}
				}
			}
		}
	}

	@Override
	public void stopAllRuntimeResponses(final double time) {
		final Response.RuntimeResponse[] rra = getRuntimeResponseArray();
		for (final RuntimeResponse runtimeResponse : rra) {
			runtimeResponse.stop(time);
		}
		m_runtimeResponses.removeAllElements();
		m_runtimeResponseArray = null;
	}

	@Override
	protected void started(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.started(world, time);
		m_runtimeResponses.removeAllElements();
		m_runtimeResponseArray = null;
	}
}
