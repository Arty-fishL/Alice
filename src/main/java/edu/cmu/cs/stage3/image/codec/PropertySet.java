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

package edu.cmu.cs.stage3.image.codec;

/*
 * The contents of this file are subject to the  JAVA ADVANCED IMAGING
 * SAMPLE INPUT-OUTPUT CODECS AND WIDGET HANDLING SOURCE CODE  License
 * Version 1.0 (the "License"); You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.sun.com/software/imaging/JAI/index.html
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is JAVA ADVANCED IMAGING SAMPLE INPUT-OUTPUT CODECS
 * AND WIDGET HANDLING SOURCE CODE.
 * The Initial Developer of the Original Code is: Sun Microsystems, Inc..
 * Portions created by: _______________________________________
 * are Copyright (C): _______________________________________
 * All Rights Reserved.
 * Contributor(s): _______________________________________
 */

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

class Property {

	private final int type;
	private final int offset;

	public Property(final int type, final int offset) {
		this.type = type;
		this.offset = offset;
	}

	public int getType() {
		return type;
	}

	public int getOffset() {
		return offset;
	}
}

class PropertySet {

	private static final int TYPE_VT_EMPTY = -1;
	private static final int TYPE_VT_NULL = -1;
	private static final int TYPE_VT_I2 = 2;
	private static final int TYPE_VT_I4 = 3;
	private static final int TYPE_VT_R4 = -1;
	private static final int TYPE_VT_R8 = -1;
	private static final int TYPE_VT_CY = -1;
	private static final int TYPE_VT_DATE = -1;
	private static final int TYPE_VT_BSTR = -1;
	private static final int TYPE_VT_ERROR = -1;
	private static final int TYPE_VT_BOOL = -1;
	private static final int TYPE_VT_VARIANT = -1;
	private static final int TYPE_VT_UI1 = -1;
	private static final int TYPE_VT_UI2 = -1;
	private static final int TYPE_VT_UI4 = 19;
	private static final int TYPE_VT_I8 = -1;
	private static final int TYPE_VT_UI8 = -1;
	private static final int TYPE_VT_LPSTR = 30;
	private static final int TYPE_VT_LPWSTR = 31;
	private static final int TYPE_VT_FILETIME = 64;
	private static final int TYPE_VT_BLOB = 65;
	private static final int TYPE_VT_STREAM = -1;
	private static final int TYPE_VT_STORAGE = -1;
	private static final int TYPE_VT_STREAMED_OBJECT = -1;
	private static final int TYPE_VT_STORED_OBJECT = -1;
	private static final int TYPE_VT_BLOB_OBJECT = -1;
	private static final int TYPE_VT_CF = 71;
	private static final int TYPE_VT_CLSID = 72;
	private static final int TYPE_VT_VECTOR = 4096;

	SeekableStream stream;
	Hashtable properties = new Hashtable();

	public PropertySet(final SeekableStream stream) throws IOException {
		this.stream = stream;

		stream.seek(44);
		final int sectionOffset = stream.readIntLE();

		stream.seek(sectionOffset);
		final int sectionSize = stream.readIntLE();
		final int sectionCount = stream.readIntLE();

		for (int i = 0; i < sectionCount; i++) {
			stream.seek(sectionOffset + 8 * i + 8);
			final int pid = stream.readIntLE();
			final int offset = stream.readIntLE();

			stream.seek(sectionOffset + offset);
			final int type = stream.readIntLE();

			final Property p = new Property(type, sectionOffset + offset + 4);
			properties.put(new Integer(pid), p);
		}
	}

	public boolean hasProperty(final int id) {
		final Property p = (Property) properties.get(new Integer(id));
		return p != null;
	}

	public int getI4(final int id) {
		final Property p = (Property) properties.get(new Integer(id));
		try {
			final int offset = p.getOffset();
			stream.seek(offset);
			return stream.readIntLE();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public int getUI1(final int id) {
		final Property p = (Property) properties.get(new Integer(id));
		try {
			final int offset = p.getOffset();
			stream.seek(offset);
			return stream.readUnsignedByte();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public int getUI2(final int id) {
		final Property p = (Property) properties.get(new Integer(id));
		try {
			final int offset = p.getOffset();
			stream.seek(offset);
			return stream.readUnsignedShortLE();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public long getUI4(final int id) {
		final Property p = (Property) properties.get(new Integer(id));
		try {
			final int offset = p.getOffset();
			stream.seek(offset);
			return stream.readUnsignedIntLE();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public long getUI4(final int id, final long defaultValue) {
		final Property p = (Property) properties.get(new Integer(id));
		if (p == null) {
			return defaultValue;
		}

		try {
			final int offset = p.getOffset();
			stream.seek(offset);
			return stream.readUnsignedIntLE();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public String getLPSTR(final int id) {
		final Property p = (Property) properties.get(new Integer(id));
		if (p == null) {
			return null;
		}

		try {
			final int offset = p.getOffset();

			stream.seek(offset);
			final int length = stream.readIntLE();
			final StringBuffer sb = new StringBuffer(length);
			for (int i = 0; i < length; i++) {
				sb.append((char) stream.read());
			}

			return sb.toString();
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getLPWSTR(final int id) {
		final Property p = (Property) properties.get(new Integer(id));
		try {
			final int offset = p.getOffset();

			stream.seek(offset);
			final int length = stream.readIntLE();
			final StringBuffer sb = new StringBuffer(length);
			for (int i = 0; i < length; i++) {
				sb.append(stream.readCharLE());
			}

			return sb.toString();
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public float getR4(final int id) {
		final Property p = (Property) properties.get(new Integer(id));
		try {
			final int offset = p.getOffset();
			stream.seek(offset);
			return stream.readFloatLE();
		} catch (final IOException e) {
			e.printStackTrace();
			return -1.0F;
		}
	}

	public Date getDate(final int id) {
		throw new RuntimeException(JaiI18N.getString("PropertySet0"));
	}

	public Date getFiletime(final int id) {
		throw new RuntimeException(JaiI18N.getString("PropertySet0"));
	}

	public byte[] getBlob(final int id) {
		final Property p = (Property) properties.get(new Integer(id));
		try {
			final int offset = p.getOffset();
			stream.seek(offset);
			final int length = stream.readIntLE();

			final byte[] buf = new byte[length];
			stream.seek(offset + 4);
			stream.readFully(buf);

			return buf;
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int[] getUI1Vector(final int id) {
		throw new RuntimeException(JaiI18N.getString("PropertySet0"));
	}

	public int[] getUI2Vector(final int id) {
		throw new RuntimeException(JaiI18N.getString("PropertySet0"));
	}

	public long[] getUI4Vector(final int id) {
		throw new RuntimeException(JaiI18N.getString("PropertySet0"));
	}

	public float[] getR4Vector(final int id) {
		throw new RuntimeException(JaiI18N.getString("PropertySet0"));
	}

	public String[] getLPWSTRVector(final int id) {
		throw new RuntimeException(JaiI18N.getString("PropertySet0"));
	}
}
