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

import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.IntegerProperty;

public class KeyClickBehavior extends TriggerBehavior implements java.awt.event.KeyListener {
	private static Class<?>[] s_supportedCoercionClasses = { KeyIsPressedBehavior.class };

	@Override
	public Class<?>[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}

	public final IntegerProperty keyCode = new IntegerProperty(this, "keyCode", null);
	public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null,
			edu.cmu.cs.stage3.alice.core.RenderTarget[].class);
	private edu.cmu.cs.stage3.alice.core.RenderTarget[] m_renderTargets = null;

	@Override
	public void manufactureAnyNecessaryDetails() {
		if (details.size() == 1) {
			final edu.cmu.cs.stage3.alice.core.Variable code = new edu.cmu.cs.stage3.alice.core.Variable();
			code.name.set("code");
			code.setParent(this);
			code.valueClass.set(Integer.class);
			details.add(code);
		}
	}

	@Override
	public void manufactureDetails() {
		super.manufactureDetails();
		final edu.cmu.cs.stage3.alice.core.Variable keyChar = new edu.cmu.cs.stage3.alice.core.Variable();
		keyChar.name.set("keyChar");
		keyChar.setParent(this);
		keyChar.valueClass.set(Character.class);
		details.add(keyChar);

		final edu.cmu.cs.stage3.alice.core.Variable code = new edu.cmu.cs.stage3.alice.core.Variable();
		code.name.set("code");
		code.setParent(this);
		code.valueClass.set(Integer.class);
		details.add(code);
	}

	private void updateDetails(final java.awt.event.KeyEvent keyEvent) {
		for (int i = 0; i < details.size(); i++) {
			final Variable detail = (Variable) details.get(i);
			if (detail.name.getStringValue().equals("keyChar")) {
				detail.value.set(new Character(keyEvent.getKeyChar()));
			} else if (detail.name.getStringValue().equals("code")) {
				detail.value.set(new Integer(keyEvent.getKeyCode()));
			}
		}
	}

	private boolean checkKeyCode(final java.awt.event.KeyEvent keyEvent) {
		final int actualValue = keyEvent.getKeyCode();
		final int requiredValue = keyCode.intValue(actualValue);
		return actualValue == requiredValue;
	}

	@Override
	public void keyPressed(final java.awt.event.KeyEvent keyEvent) {
	}

	@Override
	public void keyReleased(final java.awt.event.KeyEvent keyEvent) {
		updateDetails(keyEvent);
		if (checkKeyCode(keyEvent)) {
			trigger(keyEvent.getWhen() * 0.001);
		}
	}

	@Override
	public void keyTyped(final java.awt.event.KeyEvent keyEvent) {
	}

	@Override
	protected void started(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.started(world, time);
		m_renderTargets = (edu.cmu.cs.stage3.alice.core.RenderTarget[]) renderTargets.get();
		if (m_renderTargets == null) {
			m_renderTargets = (edu.cmu.cs.stage3.alice.core.RenderTarget[]) world
					.getDescendants(edu.cmu.cs.stage3.alice.core.RenderTarget.class);
		}
		for (final RenderTarget m_renderTarget : m_renderTargets) {
			m_renderTarget.addKeyListener(this);
		}
	}

	@Override
	protected void stopped(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.stopped(world, time);
		for (final RenderTarget m_renderTarget : m_renderTargets) {
			m_renderTarget.removeKeyListener(this);
		}
		m_renderTargets = null;
	}
}