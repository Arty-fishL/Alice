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

package edu.cmu.cs.stage3.alice.core.question.time;

public class TimeElapsedSinceWorldStart extends edu.cmu.cs.stage3.alice.core.question.NumberQuestion {
	private edu.cmu.cs.stage3.alice.core.Clock m_clock;

	@Override
	public Object getValue() {
		if (m_clock != null) {
			return new Double(m_clock.getTimeElapsed());
		} else {
			return null;
		}
	}

	@Override
	protected void started(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.started(world, time);
		if (world != null) {
			m_clock = world.getClock();
		}
	}

	@Override
	protected void stopped(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		m_clock = null;
		super.stopped(world, time);
	}
}