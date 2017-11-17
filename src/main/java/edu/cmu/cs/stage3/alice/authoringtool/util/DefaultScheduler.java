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

public class DefaultScheduler implements Runnable {
	private static final long minDelay = 20;

	private double simFPS = 0.0;
	private final double renderFPS = 0.0;
	private int simFrameCount = 0;
	private final int renderFrameCount = 0;
	private long simDT = 0;
	private final long renderDT = 0;
	private long simLastTime = 0;
	private final long renderLastTime = -1;
	private final long lastRenderLastTime = 0;
	private final long idleLastTime = 0;

	private final boolean defaultThreadEnabled = false;

	private final java.util.Set doOnceRunnables = new java.util.HashSet();
	private final java.util.Set eachFrameRunnables = new java.util.HashSet();
	private final java.util.Set eachFrameRunnablesMarkedForRemoval = new java.util.HashSet();

	public boolean addDoOnceRunnable(final Runnable doOnceRunnable) {
		synchronized (doOnceRunnables) {
			return doOnceRunnables.add(doOnceRunnable);
		}
	}

	public boolean addEachFrameRunnable(final Runnable eachFrameRunnable) {
		synchronized (eachFrameRunnables) {
			return eachFrameRunnables.add(eachFrameRunnable);
		}
	}

	public boolean removeEachFrameRunnable(final Runnable eachFrameRunnable) {
		synchronized (eachFrameRunnablesMarkedForRemoval) {
			return eachFrameRunnablesMarkedForRemoval.add(eachFrameRunnable);
		}
	}

	public Runnable[] getEachFrameRunnables() {
		synchronized (eachFrameRunnables) {
			final Runnable[] runnables = new Runnable[eachFrameRunnables.size()];
			int i = 0;
			for (final java.util.Iterator iter = eachFrameRunnables.iterator(); iter.hasNext();) {
				runnables[i++] = (Runnable) iter.next();
			}
			return runnables;
		}
	}

	@Override
	public void run() {
		simulateOnce();
	}

	// synchronized public void idle(
	// edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RendererEvent
	// rendererEvent ) {
	// long time = System.currentTimeMillis();
	//
	// if( time > (idleLastTime + 5) ) {
	// idleLastTime = time;
	// simulateOnce();
	// }
	// }

	synchronized private void simulateOnce() {
		for (final java.util.Iterator iter = doOnceRunnables.iterator(); iter.hasNext();) {
			final Runnable runnable = (Runnable) iter.next();
			try {
				runnable.run();
			} catch (final org.python.core.PyException e) {
				// System.out.println( "PyException: " + e );
				if (org.python.core.Py.matchException(e, org.python.core.Py.SystemExit)) {
					// TODO
				} else {
					org.python.core.Py.printException(e, null,
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getPyStdErr());
				}
			} catch (final Throwable t) {
				System.err.println("Error during simulation:");
				t.printStackTrace();
			}
			iter.remove();
		}

		for (final java.util.Iterator iter = eachFrameRunnablesMarkedForRemoval.iterator(); iter.hasNext();) {
			eachFrameRunnables.remove(iter.next());
		}
		eachFrameRunnablesMarkedForRemoval.clear();

		for (final java.util.Iterator iter = eachFrameRunnables.iterator(); iter.hasNext();) {
			final Runnable runnable = (Runnable) iter.next();
			try {
				runnable.run();
			} catch (final org.python.core.PyException e) {
				// System.out.println( "PyException: " + e );
				if (org.python.core.Py.matchException(e, org.python.core.Py.SystemExit)) {
					// TODO
				} else {
					org.python.core.Py.printException(e, null,
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getPyStdErr());
				}
			} catch (final Throwable t) {
				System.err.println("Error during simulation:");
				t.printStackTrace();
			}
		}

		final long time = System.currentTimeMillis();
		simDT += time - simLastTime;
		simLastTime = time;

		simFrameCount++;
		if (simFrameCount == 5 || simDT > 500) {
			simFPS = simFrameCount / (simDT * .001);
			simFrameCount = 0;
			simDT = 0;
		}
	}

	public double getSimulationFPS() {
		return simFPS;
	}

	/*
	 * public double getRenderFPS() { //TODO handle non-activity better if(
	 * lastRenderLastTime == renderLastTime ) { renderFPS = 0.0;
	 * renderFrameCount = 0; renderDT = 0; renderLastTime = -1; } else {
	 * lastRenderLastTime = renderLastTime; }
	 *
	 * return renderFPS; }
	 */
}
