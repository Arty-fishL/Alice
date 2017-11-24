package edu.cmu.cs.stage3.scheduler;

public abstract class AbstractScheduler implements Scheduler {
	private final java.util.Vector<Runnable> m_eachFrameRunnables = new java.util.Vector<Runnable>();
	private final java.util.Vector<Runnable> m_eachFrameRunnablesMarkedForRemoval = new java.util.Vector<Runnable>();
	private Runnable[] m_cachedEachFrameRunnables;

	@Override
	public void addEachFrameRunnable(final Runnable runnable) {
		synchronized (m_eachFrameRunnables) {
			m_eachFrameRunnables.addElement(runnable);
			m_cachedEachFrameRunnables = null;
		}
	}

	@Override
	public void markEachFrameRunnableForRemoval(final Runnable runnable) {
		synchronized (m_eachFrameRunnablesMarkedForRemoval) {
			m_eachFrameRunnablesMarkedForRemoval.addElement(runnable);
			m_cachedEachFrameRunnables = null;
		}
	}

	private Runnable[] getEachFrameRunnables() {
		if (m_cachedEachFrameRunnables == null) {
			synchronized (m_eachFrameRunnables) {
				synchronized (m_eachFrameRunnablesMarkedForRemoval) {
					if (m_eachFrameRunnablesMarkedForRemoval.size() > 0) {
						final java.util.Enumeration<Runnable> enum0 = m_eachFrameRunnablesMarkedForRemoval.elements();
						while (enum0.hasMoreElements()) {
							m_eachFrameRunnables.removeElement(enum0.nextElement());
						}
						m_eachFrameRunnablesMarkedForRemoval.clear();
					}
					m_cachedEachFrameRunnables = new Runnable[m_eachFrameRunnables.size()];
					m_eachFrameRunnables.copyInto(m_cachedEachFrameRunnables);
				}
			}
		}
		return m_cachedEachFrameRunnables;
	}

	protected abstract void handleCaughtThowable(Runnable source, Throwable t);

	@Override
	public void run() {
		final Runnable[] eachFrameRunnables = getEachFrameRunnables();
		for (final Runnable eachFrameRunnable : eachFrameRunnables) {
			try {
				eachFrameRunnable.run();
			} catch (final Throwable t) {
				handleCaughtThowable(eachFrameRunnable, t);
			}
		}
	}
}
