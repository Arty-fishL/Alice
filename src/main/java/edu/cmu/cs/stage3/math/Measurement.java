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

package edu.cmu.cs.stage3.math;

public class Measurement extends Number {
	/**
	 *
	 */
	private static final long serialVersionUID = -1410537674220182689L;
	private final double m_value;
	private final double m_factor;

	public Measurement(final double value, final double factor) {
		m_value = value;
		m_factor = factor;
	}

	@Override
	public byte byteValue() {
		return (byte) doubleValue();
	}

	@Override
	public double doubleValue() {
		return m_value * m_factor;
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	@Override
	public int intValue() {
		return (int) doubleValue();
	}

	@Override
	public long longValue() {
		return (long) doubleValue();
	}

	@Override
	public short shortValue() {
		return (short) doubleValue();
	}

	// todo toString, valueOf
}