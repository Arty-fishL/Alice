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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

abstract class ContainerProxy extends ComponentProxy {
	private ComponentProxy[] m_childrenProxies;

	public void handleChildAdd(final ComponentProxy childProxy) {
		m_childrenProxies = null;
	}

	public void handleChildRemove(final ComponentProxy childProxy) {
		m_childrenProxies = null;
	}

	private edu.cmu.cs.stage3.alice.scenegraph.Container getSceneGraphContainer() {
		return (edu.cmu.cs.stage3.alice.scenegraph.Container) getSceneGraphElement();
	}

	public ComponentProxy[] getChildrenProxies() {
		if (m_childrenProxies == null) {
			final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer = getSceneGraphContainer();
			final edu.cmu.cs.stage3.alice.scenegraph.Component[] sgChildren = sgContainer.getChildren();
			m_childrenProxies = new ComponentProxy[sgChildren.length];
			for (int i = 0; i < sgChildren.length; i++) {
				m_childrenProxies[i] = (ComponentProxy) getProxyFor(sgChildren[i]);
			}
		}
		return m_childrenProxies;
	}

	@Override
	public void setup(final RenderContext context) {
		final ComponentProxy[] childrenProxies = getChildrenProxies();
		for (final ComponentProxy childrenProxie : childrenProxies) {
			childrenProxie.setup(context);
		}
	}

	@Override
	public void render(final RenderContext context) {
		final ComponentProxy[] childrenProxies = getChildrenProxies();

		for (final ComponentProxy childrenProxie : childrenProxies) {
			childrenProxie.render(context);
		}

	}

	@Override
	public void pick(final PickContext context, final PickParameters pickParameters) {
		final ComponentProxy[] childrenProxies = getChildrenProxies();
		for (final ComponentProxy childrenProxie : childrenProxies) {
			childrenProxie.pick(context, pickParameters);
		}
	}
}
