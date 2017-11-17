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

package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AffectorProxy;

public abstract class AbstractProxyRenderer extends AbstractRenderer {
	private final Map<Element, AbstractProxy> m_sgElementToProxyMap = new HashMap<Element, AbstractProxy>();
	@SuppressWarnings("unused")
	private final List<?> m_queuedPropertyChanges = new ArrayList<Object>();

	@Override
	protected void dispatchPropertyChange(final PropertyEvent propertyEvent) {
		final Property property = propertyEvent.getProperty();
		final Element sgElement = (Element) propertyEvent.getSource();
		if (sgElement.isReleased()) {
			// pass
		} else {
			final AbstractProxy proxy = getProxyFor(sgElement);
			proxy.changed(property, property.get(sgElement));
			markAllRenderTargetsDirty();
		}
	}

	@Override
	protected void dispatchRelease(final edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent) {
		final Element sgElement = (Element) releaseEvent.getSource();
		final AbstractProxy proxy = getProxyFor(sgElement);
		proxy.release();
	}

	protected abstract AbstractProxy createProxyFor(Element sgElement);

	public AbstractProxy getProxyFor(final edu.cmu.cs.stage3.alice.scenegraph.Element sgElement) {
		AbstractProxy proxy;
		if (sgElement != null) {
			proxy = m_sgElementToProxyMap.get(sgElement);
			if (proxy == null) {
				proxy = createProxyFor(sgElement);
				if (proxy != null) {
					m_sgElementToProxyMap.put(sgElement, proxy);
					proxy.initialize(sgElement, this);
					createNecessaryProxies(sgElement);
				} else {
					edu.cmu.cs.stage3.alice.scenegraph.Element
							.warnln("warning: could not create proxy for: " + sgElement);
				}
			} else {
				if (proxy.getSceneGraphElement() == null) {
					proxy = null;
					edu.cmu.cs.stage3.alice.scenegraph.Element.warnln(sgElement + "'s proxy has null for a sgElement");
				}
			}
		} else {
			proxy = null;
		}
		return proxy;
	}

	public AbstractProxy[] getProxiesFor(final edu.cmu.cs.stage3.alice.scenegraph.Element[] sgElements,
			final Class<? extends AffectorProxy> componentType) {
		if (sgElements != null) {
			final AbstractProxy[] proxies = (AbstractProxy[]) java.lang.reflect.Array.newInstance(componentType,
					sgElements.length);
			for (int i = 0; i < sgElements.length; i++) {
				proxies[i] = getProxyFor(sgElements[i]);
			}
			return proxies;
		} else {
			return null;
		}
	}

	public void forgetProxyFor(final edu.cmu.cs.stage3.alice.scenegraph.Element sgElement) {
		m_sgElementToProxyMap.remove(sgElement);
	}

	public void createNecessaryProxies(final edu.cmu.cs.stage3.alice.scenegraph.Element sgElement) {
		getProxyFor(sgElement);
		if (sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Container) {
			final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer = (edu.cmu.cs.stage3.alice.scenegraph.Container) sgElement;
			for (int i = 0; i < sgContainer.getChildCount(); i++) {
				final edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = sgContainer.getChildAt(i);
				getProxyFor(sgComponent);
				if (sgComponent instanceof edu.cmu.cs.stage3.alice.scenegraph.Container) {
					createNecessaryProxies(sgComponent);
				}
			}
		}
	}
}
