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

package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class IsCloseTo extends SubjectObjectQuestion {
	private static Class[] s_supportedCoercionClasses = { IsFarFrom.class };

	@Override
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}

	public final NumberProperty threshold = new NumberProperty(this, "threshold", new Double(1));

	@Override
	public Class getValueClass() {
		return Boolean.class;
	}

	@Override
	protected Object getValue(final Transformable subjectValue, final Transformable objectValue) {
		final double thresholdValue = threshold.doubleValue();
		if (subjectValue.getDistanceSquaredTo(objectValue) < thresholdValue * thresholdValue) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}