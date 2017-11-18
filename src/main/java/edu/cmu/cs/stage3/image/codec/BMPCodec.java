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

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 */
public final class BMPCodec extends ImageCodec {

	public BMPCodec() {
	}

	@Override
	public String getFormatName() {
		return "bmp";
	}

	@Override
	public Class<BMPEncodeParam> getEncodeParamClass() {
		return BMPEncodeParam.class;
	}

	@Override
	public Class<Object> getDecodeParamClass() {
		return Object.class;
	}

	@Override
	public boolean canEncodeImage(final RenderedImage im, final ImageEncodeParam param) {
		final SampleModel sampleModel = im.getSampleModel();
		final int dataType = sampleModel.getTransferType();
		if (dataType == DataBuffer.TYPE_USHORT || dataType == DataBuffer.TYPE_SHORT || dataType == DataBuffer.TYPE_INT
				|| dataType == DataBuffer.TYPE_FLOAT || dataType == DataBuffer.TYPE_DOUBLE) {
			return false;
		}

		if (param != null) {
			if (!(param instanceof BMPEncodeParam)) {
				return false;
			}
			final BMPEncodeParam BMPParam = (BMPEncodeParam) param;

			final int version = BMPParam.getVersion();
			if (version == BMPEncodeParam.VERSION_2 || version == BMPEncodeParam.VERSION_4) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected ImageEncoder createImageEncoder(final OutputStream dst, final ImageEncodeParam param) {
		BMPEncodeParam p = null;
		if (param != null) {
			p = (BMPEncodeParam) param;
		}

		return new BMPImageEncoder(dst, p);
	}

	@Override
	protected ImageDecoder createImageDecoder(final InputStream src, final ImageDecodeParam param) {
		return new BMPImageDecoder(src, null);
	}

	@Override
	protected ImageDecoder createImageDecoder(final File src, final ImageDecodeParam param) throws IOException {
		return new BMPImageDecoder(new FileInputStream(src), null);
	}

	@Override
	protected ImageDecoder createImageDecoder(final SeekableStream src, final ImageDecodeParam param) {
		return new BMPImageDecoder(src, null);
	}

	@Override
	public int getNumHeaderBytes() {
		return 2;
	}

	@Override
	public boolean isFormatRecognized(final byte[] header) {
		return header[0] == 0x42 && header[1] == 0x4d;
	}
}
