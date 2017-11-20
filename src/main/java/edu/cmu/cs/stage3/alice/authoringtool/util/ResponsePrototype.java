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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class ResponsePrototype extends ElementPrototype {
	public ResponsePrototype(final Class<? extends Element> responseClass,
			final StringObjectPair[] knownPropertyValues, final String[] desiredProperties) {
		super(responseClass, knownPropertyValues, desiredProperties);
	}

	public Response createNewResponse() {
		return (Response) createNewElement();
	}

	public Class<? extends Element> getResponseClass() {
		return super.getElementClass();
	}

	// a rather inelegant solution for creating copies of the correct type.
	// subclasses should override this method and call their own constructor

	@Override
	protected ElementPrototype createInstance(final Class<? extends Element> elementClass,
			final StringObjectPair[] knownPropertyValues, final String[] desiredProperties) {
		return new ResponsePrototype(elementClass, knownPropertyValues, desiredProperties);
	}
}
