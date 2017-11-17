package edu.cmu.cs.stage3.alice.core.summary;

public abstract class ElementSummary {
	private edu.cmu.cs.stage3.alice.core.Element m_element;
	private final int m_elementCount = -1;

	protected edu.cmu.cs.stage3.alice.core.Element getElement() {
		return m_element;
	}

	protected void setElement(final edu.cmu.cs.stage3.alice.core.Element element) {
		m_element = element;
	}

	public int getElementCount() {
		if (m_element != null) {
			return m_element.getElementCount();
		} else {
			return m_elementCount;
		}
	}

	public void encode(final java.io.OutputStream os) {
	}

	public void decode(final java.io.InputStream is) {
	}
}
