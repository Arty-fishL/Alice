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

package edu.cmu.cs.stage3.alice.scripting.jython;

/**
 * @author Dennis Cosgrove
 */
public class ScriptingFactory implements edu.cmu.cs.stage3.alice.scripting.ScriptingFactory {
	private final java.util.Vector<Interpreter> m_interpreters = new java.util.Vector<Interpreter>();
	private Interpreter[] m_interpreterArray = null;

	public ScriptingFactory() {
		java.util.Properties preProperties;
		try {
			preProperties = System.getProperties();
		} catch (final java.security.AccessControlException ace) {
			preProperties = new java.util.Properties();
			preProperties.setProperty("python.home", System.getProperty("python.home"));
		}
		final java.util.Properties postProperties = null;
		final String[] argv = { "" };
		org.python.core.PySystemState.initialize(preProperties, postProperties, argv, null);

		// todo: remove
		final org.python.core.PySystemState systemState = org.python.core.Py.getSystemState();
		final String pythonHome = preProperties.getProperty("python.home");
		final String pathname = pythonHome + "/lib/alice/__init__.py";
		try {
			final java.io.File f = new java.io.File(pathname);
			final java.io.InputStream is = new java.io.FileInputStream(f.getAbsoluteFile());
			final java.io.BufferedReader br = new java.io.BufferedReader(
					new java.io.InputStreamReader(new java.io.BufferedInputStream(is)));
			try {
				final StringBuffer sb = new StringBuffer();
				while (true) {
					final String s = br.readLine();
					if (s != null) {
						sb.append(s);
						sb.append('\n');
					} else {
						break;
					}
				}
				if (sb.length() > 0) {
					final String script = sb.substring(0, sb.length() - 1);
					final org.python.core.PyCode code = org.python.core.__builtin__.compile(script,
							"<jython-2.1/lib/alice/__init__.py>", "exec");
					org.python.core.Py.exec(code, systemState.builtins, systemState.builtins);
				}
			} finally {
				br.close();
			}
		} catch (final java.io.IOException ioe) {
			throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper(ioe, "IOException attempting to load " + pathname);
		}
	}

	@Override
	public synchronized edu.cmu.cs.stage3.alice.scripting.Interpreter manufactureInterpreter() {
		final Interpreter interpreter = new Interpreter(this);
		m_interpreters.addElement(interpreter);
		m_interpreterArray = null;
		return interpreter;
	}

	/* package protected */synchronized void releaseInterpreter(
			final edu.cmu.cs.stage3.alice.scripting.Interpreter interpreter) {
		m_interpreterArray = null;
		m_interpreters.removeElement(interpreter);
	}

	@Override
	public synchronized edu.cmu.cs.stage3.alice.scripting.Interpreter[] getInterpreters() {
		if (m_interpreterArray == null) {
			m_interpreterArray = new Interpreter[m_interpreters.size()];
			m_interpreters.copyInto(m_interpreterArray);
		}
		return m_interpreterArray;
	}

	private java.io.OutputStream m_stdout = null;
	private java.io.OutputStream m_stderr = null;

	@Override
	public java.io.OutputStream getStdOut() {
		return m_stdout;
	}

	@Override
	public void setStdOut(final java.io.OutputStream stdout) {
		m_stdout = stdout;
		org.python.core.Py.getSystemState().stdout = new org.python.core.PyFile(stdout);
	}

	@Override
	public java.io.OutputStream getStdErr() {
		return m_stderr;
	}

	@Override
	public void setStdErr(final java.io.OutputStream stderr) {
		m_stderr = stderr;
		org.python.core.Py.getSystemState().stderr = new org.python.core.PyFile(stderr);
	}
}
