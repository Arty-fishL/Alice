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

package edu.cmu.cs.stage3.alice.core;

import java.util.Vector;

import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.alice.core.event.MessageListener;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;

public class World extends ReferenceFrame {
	private static boolean HACK_s_isPropetryListeningDisabledWhileWorldIsRunning = false;
	static {
		try {
			HACK_s_isPropetryListeningDisabledWhileWorldIsRunning = Boolean
					.getBoolean("alice.isPropetryListeningDisabledWhileWorldIsRunning");
		} catch (final Throwable t) {
			HACK_s_isPropetryListeningDisabledWhileWorldIsRunning = false;
		}
	}

	public final ElementArrayProperty sandboxes = new ElementArrayProperty(this, "sandboxes", null, Sandbox[].class);
	public final ElementArrayProperty groups = new ElementArrayProperty(this, "groups", null, Group[].class);
	public final ColorProperty atmosphereColor = new ColorProperty(this, "atmosphereColor",
			edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK);
	public final ColorProperty ambientLightColor = new ColorProperty(this, "ambientLightColor",
			edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);
	public final NumberProperty ambientLightBrightness = new NumberProperty(this, "ambientLightBrightness",
			new Double(0.2));
	public final ObjectProperty fogStyle = new ObjectProperty(this, "fogStyle",
			edu.cmu.cs.stage3.alice.core.FogStyle.NONE, edu.cmu.cs.stage3.alice.core.FogStyle.class);
	public final NumberProperty fogDensity = new NumberProperty(this, "fogDensity", new Double(0));
	public final NumberProperty fogNearDistance = new NumberProperty(this, "fogNearDistance", new Double(0));
	public final NumberProperty fogFarDistance = new NumberProperty(this, "fogFarDistance", new Double(1));

	public final ObjectArrayProperty bubbles = new ObjectArrayProperty(this, "bubbles", null,
			edu.cmu.cs.stage3.alice.core.bubble.Bubble[].class);

	public final NumberProperty speedMultiplier = new NumberProperty(this, "speedMultiplier", new Double(1));

	private edu.cmu.cs.stage3.alice.scenegraph.Scene m_sgScene;
	private edu.cmu.cs.stage3.alice.scenegraph.AmbientLight m_sgAmbientLight;
	private edu.cmu.cs.stage3.alice.scenegraph.Background m_sgBackground;
	private edu.cmu.cs.stage3.alice.scenegraph.LinearFog m_sgLinearFog;
	private edu.cmu.cs.stage3.alice.scenegraph.ExponentialFog m_sgExponentialFog;

	private final edu.cmu.cs.stage3.alice.scenegraph.collision.CollisionManager m_collisionManager = new edu.cmu.cs.stage3.alice.scenegraph.collision.CollisionManager();
	private edu.cmu.cs.stage3.alice.scenegraph.Visual[][] m_collisions = {};

	private final java.util.Vector<Object[]> m_capsulePropertyValuePairs = new java.util.Vector<Object[]>();
	private final java.util.Hashtable<Element, Boolean> m_capsuleElements = new java.util.Hashtable<>();
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory m_renderTargetFactory = null;

	private edu.cmu.cs.stage3.alice.scripting.ScriptingFactory m_scriptingFactory;
	private edu.cmu.cs.stage3.alice.scripting.Interpreter m_interpreter;

	private final java.util.Vector<MessageListener> m_messageListeners = new java.util.Vector<MessageListener>();
	private edu.cmu.cs.stage3.alice.core.event.MessageListener[] m_messageListenerArray = null;

	private Sandbox m_currentSandbox = null;

	private final edu.cmu.cs.stage3.alice.core.bubble.BubbleManager m_bubbleManager = new edu.cmu.cs.stage3.alice.core.bubble.BubbleManager();

	private Clock m_clock = null;
	private boolean m_isRunning = false;

	public World() {
		super();
		m_sgScene = new edu.cmu.cs.stage3.alice.scenegraph.Scene();
		m_sgScene.setBonus(this);
		m_sgBackground = new edu.cmu.cs.stage3.alice.scenegraph.Background();
		m_sgBackground.setBonus(this);
		m_sgScene.setBackground(m_sgBackground);
		m_sgAmbientLight = new edu.cmu.cs.stage3.alice.scenegraph.AmbientLight();
		m_sgAmbientLight.setBonus(this);
		m_sgAmbientLight.setParent(m_sgScene);
		m_sgLinearFog = new edu.cmu.cs.stage3.alice.scenegraph.LinearFog();
		m_sgLinearFog.setBonus(this);
		m_sgExponentialFog = new edu.cmu.cs.stage3.alice.scenegraph.ExponentialFog();
		m_sgExponentialFog.setBonus(this);
		atmosphereColor.set(m_sgBackground.getColor());
		ambientLightColor.set(m_sgAmbientLight.getColor());
		ambientLightBrightness.set(new Double(m_sgAmbientLight.getBrightness()));
		fogNearDistance.set(new Double(m_sgLinearFog.getNearDistance()));
		fogFarDistance.set(new Double(m_sgLinearFog.getFarDistance()));
		fogDensity.set(new Double(m_sgExponentialFog.getDensity()));
		fogStyle.set(FogStyle.NONE);
	}

	public Clock getClock() {
		return m_clock;
	}

	public void setClock(final Clock clock) {
		m_clock = clock;
	}

	public edu.cmu.cs.stage3.alice.core.bubble.BubbleManager getBubbleManager() {
		return m_bubbleManager;
	}

	private edu.cmu.cs.stage3.alice.scripting.Interpreter getInterpreter() {
		if (m_scriptingFactory != null) {
			if (m_interpreter == null) {
				m_interpreter = m_scriptingFactory.manufactureInterpreter();
				m_interpreter.setWorld(this);
			}
		}
		return m_interpreter;
	}

	@Override
	public edu.cmu.cs.stage3.alice.scripting.Code compile(final String script, final Object source,
			final edu.cmu.cs.stage3.alice.scripting.CompileType compileType) {
		return getInterpreter().compile(script, source, compileType);
	}

	@Override
	public Object eval(final edu.cmu.cs.stage3.alice.scripting.Code code) {
		return getInterpreter().eval(code);
	}

	@Override
	public void exec(final edu.cmu.cs.stage3.alice.scripting.Code code) {
		getInterpreter().exec(code);
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Visual[][] getCollisions() {
		return m_collisions;
	}

	public void addCollisionManagementFor(final Transformable t) {
		if (t != null) {
			final edu.cmu.cs.stage3.alice.scenegraph.Visual[] sgVisuals = t.getAllSceneGraphVisuals();
			for (final Visual sgVisual : sgVisuals) {
				final edu.cmu.cs.stage3.math.Sphere sphere = sgVisual.getBoundingSphere();
				if (sphere != null && sphere.getRadius() > 0) {
					m_collisionManager.activateObject(sgVisual);
				}
			}
			for (int i = 0; i < sgVisuals.length; i++) {
				for (int j = i + 1; j < sgVisuals.length; j++) {
					final edu.cmu.cs.stage3.math.Sphere sphereI = sgVisuals[i].getBoundingSphere();
					if (sphereI != null && sphereI.getRadius() > 0) {
						final edu.cmu.cs.stage3.math.Sphere sphereJ = sgVisuals[j].getBoundingSphere();
						if (sphereJ != null && sphereJ.getRadius() > 0) {
							m_collisionManager.deactivatePair(sgVisuals[i], sgVisuals[j]);
						}
					}
				}
			}
		}
	}

	public void removeCollisionManagementFor(final Transformable t) {
		// edu.cmu.cs.stage3.alice.scenegraph.Visual[] sgVisuals =
		// t.getAllSceneGraphVisuals();
		// for( int i=0; i<sgVisuals.length; i++ ) {
		// m_collisionManager.dectivateObject( sgVisuals[ i ] );
		// }
	}

	@Override
	protected void internalRelease(final int pass) {
		switch (pass) {
		case 1:
			if (m_interpreter != null) {
				m_interpreter.release();
				m_interpreter = null;
			}
			if (m_sgExponentialFog != null) {
				m_sgExponentialFog.setParent(null);
			}
			if (m_sgLinearFog != null) {
				m_sgLinearFog.setParent(null);
			}
			m_sgAmbientLight.setParent(null);
			m_sgScene.setBackground(null);
			break;
		case 2:
			m_sgScene.release();
			m_sgScene = null;
			m_sgAmbientLight.release();
			m_sgAmbientLight = null;
			m_sgBackground.release();
			m_sgBackground = null;
			if (m_sgExponentialFog != null) {
				m_sgExponentialFog.release();
				m_sgExponentialFog = null;
			}
			if (m_sgLinearFog != null) {
				m_sgLinearFog.release();
				m_sgLinearFog = null;
			}
			break;
		}
		super.internalRelease(pass);
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Scene getSceneGraphScene() {
		return m_sgScene;
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.Container getSceneGraphContainer() {
		return m_sgScene;
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame getSceneGraphReferenceFrame() {
		return m_sgScene;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.AmbientLight getSceneGraphAmbientLight() {
		return m_sgAmbientLight;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Background getSceneGraphBackground() {
		return m_sgBackground;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.ExponentialFog getSceneGraphExponentialFog() {
		return m_sgExponentialFog;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.LinearFog getSceneGraphLinearFog() {
		return m_sgLinearFog;
	}

	@Override
	public void addAbsoluteTransformationListener(
			final edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener absoluteTransformationListener) {
	}

	@Override
	public void removeAbsoluteTransformationListener(
			final edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener absoluteTransformationListener) {
	}

	@Override
	protected void nameValueChanged(final String value) {
		super.nameValueChanged(value);
		if (value != null) {
			m_sgScene.setName(value + ".m_sgScene");
			m_sgBackground.setName(value + ".m_sgBackground");
			m_sgAmbientLight.setName(value + ".m_sgAmbientLight");
			m_sgExponentialFog.setName(value + ".m_sgExponentialFog");
			m_sgLinearFog.setName(value + ".m_sgLinearFog");
		} else {
			m_sgScene.setName(null);
			m_sgBackground.setName(null);
			m_sgAmbientLight.setName(null);
			m_sgExponentialFog.setName(null);
			m_sgLinearFog.setName(null);
		}
	}

	protected void atmosphereColorValueChanged(final edu.cmu.cs.stage3.alice.scenegraph.Color value) {
		m_sgBackground.setColor(value);
		m_sgLinearFog.setColor(value);
		m_sgExponentialFog.setColor(value);
	}

	protected void ambientLightColorValueChanged(final edu.cmu.cs.stage3.alice.scenegraph.Color value) {
		m_sgAmbientLight.setColor(value);
	}

	protected void ambientLightBrightnessValueChanged(final Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		m_sgAmbientLight.setBrightness(d);
	}

	protected void fogDensityValueChanged(final Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		m_sgExponentialFog.setDensity(d);
	}

	protected void fogNearDistanceValueChanged(final Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		m_sgLinearFog.setNearDistance(d);
	}

	protected void fogFarDistanceValueChanged(final Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		m_sgLinearFog.setFarDistance(d);
	}

	protected void fogStyleValueChanged(final FogStyle value) {
		if (value == FogStyle.EXPONENTIAL) {
			m_sgLinearFog.setParent(null);
			m_sgExponentialFog.setParent(m_sgScene);
		} else if (value == FogStyle.LINEAR) {
			m_sgLinearFog.setParent(m_sgScene);
			m_sgExponentialFog.setParent(null);
		} else {
			m_sgLinearFog.setParent(null);
			m_sgExponentialFog.setParent(null);
		}
	}

	@Override
	protected void propertyChanged(final Property property, final Object value) {
		if (property == atmosphereColor) {
			atmosphereColorValueChanged((edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == ambientLightColor) {
			ambientLightColorValueChanged((edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == ambientLightBrightness) {
			ambientLightBrightnessValueChanged((Number) value);
		} else if (property == fogStyle) {
			fogStyleValueChanged((FogStyle) value);
		} else if (property == fogDensity) {
			fogDensityValueChanged((Number) value);
		} else if (property == fogNearDistance) {
			fogNearDistanceValueChanged((Number) value);
		} else if (property == fogFarDistance) {
			fogFarDistanceValueChanged((Number) value);
		} else if (property == bubbles) {
			m_bubbleManager.setBubbles((edu.cmu.cs.stage3.alice.core.bubble.Bubble[]) bubbles.getArrayValue());
		} else {
			super.propertyChanged(property, value);
		}
	}

	@Override
	public edu.cmu.cs.stage3.math.Matrix44 getTransformation(final ReferenceFrame asSeenBy) {
		if (asSeenBy == null) {
			return edu.cmu.cs.stage3.math.Matrix44.IDENTITY;
		} else {
			return super.getTransformation(asSeenBy);
		}
	}

	@Override
	protected void internalFindAccessibleExpressions(final Class<?> cls, final Vector<Expression> v) {
		for (int i = 0; i < sandboxes.size(); i++) {
			final Sandbox sandbox = (Sandbox) sandboxes.get(i);
			for (int j = 0; j < sandbox.variables.size(); j++) {
				internalAddExpressionIfAssignableTo((Expression) sandbox.variables.get(j), cls, v);
			}
			for (int j = 0; j < sandbox.questions.size(); j++) {
				internalAddExpressionIfAssignableTo((Expression) sandbox.questions.get(j), cls, v);
			}
		}
		super.internalFindAccessibleExpressions(cls, v);
	}

	public edu.cmu.cs.stage3.alice.scripting.ScriptingFactory getScriptingFactory() {
		return m_scriptingFactory;
	}

	public void setScriptingFactory(final edu.cmu.cs.stage3.alice.scripting.ScriptingFactory scriptingFactory) {
		m_scriptingFactory = scriptingFactory;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory getRenderTargetFactory() {
		return m_renderTargetFactory;
	}

	public void setRenderTargetFactory(
			final edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory renderTargetFactory) {
		m_renderTargetFactory = renderTargetFactory;
		final RenderTarget[] renderTargets = (RenderTarget[]) getDescendants(RenderTarget.class);
		for (final RenderTarget renderTarget : renderTargets) {
			renderTarget.commit(m_renderTargetFactory);
		}
	}

	public void sendMessage(final Element source, final String message, final Transformable fromWho,
			final Transformable toWhom, final long when) {
		final edu.cmu.cs.stage3.alice.core.event.MessageEvent messageEvent = new edu.cmu.cs.stage3.alice.core.event.MessageEvent(
				source, message, fromWho, toWhom, when);
		for (int i = 0; i < m_messageListeners.size(); i++) {
			final edu.cmu.cs.stage3.alice.core.event.MessageListener messageListener = m_messageListeners
					.elementAt(i);
			messageListener.messageSent(messageEvent);
		}
	}

	public void addMessageListener(final edu.cmu.cs.stage3.alice.core.event.MessageListener messageListener) {
		m_messageListeners.addElement(messageListener);
		m_messageListenerArray = null;
	}

	public void removeMessageListener(final edu.cmu.cs.stage3.alice.core.event.MessageListener messageListener) {
		m_messageListeners.removeElement(messageListener);
		m_messageListenerArray = null;
	}

	public edu.cmu.cs.stage3.alice.core.event.MessageListener[] getMessageListeners() {
		if (m_messageListenerArray == null) {
			m_messageListenerArray = new edu.cmu.cs.stage3.alice.core.event.MessageListener[m_messageListeners.size()];
			m_messageListeners.copyInto(m_messageListenerArray);
		}
		return m_messageListenerArray;
	}

	public void preserve() {
		m_capsulePropertyValuePairs.clear();
		m_capsuleElements.clear();
		final Element[] elements = getDescendants();
		for (final Element element : elements) {
			final edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = element
					.getChildrenListeners();
			if (childrenListeners != null && childrenListeners.length != 0) {
				warnln("WARNING: " + element.getKey() + " has CHILDREN listeners: ");
				for (final ChildrenListener childrenListener : childrenListeners) {
					warnln("\t" + childrenListener.getClass());
				}
			}
			m_capsuleElements.put(element, Boolean.TRUE);
			final Property[] properties = element.getProperties();
			for (final Property property : properties) {
				final edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propertyListeners = property
						.getPropertyListeners();
				if (propertyListeners != null && propertyListeners.length != 0) {
					warnln("WARNING: " + element.getKey() + "." + property.getName() + " has PROPERTY listeners: ");
					for (final PropertyListener propertyListener : propertyListeners) {
						warnln("\t" + propertyListener.getClass());
					}
				}
				if (property instanceof ObjectArrayProperty) {
					final ObjectArrayProperty objectArrayPropery = (ObjectArrayProperty) property;
					final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener[] objectArrayProperyListeners = objectArrayPropery
							.getObjectArrayPropertyListeners();
					if (objectArrayProperyListeners != null && objectArrayProperyListeners.length != 0) {
						warnln("WARNING: " + element.getKey() + "." + objectArrayPropery.getName()
								+ " has OBJECT ARRAY PROPERTY listeners: ");
						for (final ObjectArrayPropertyListener objectArrayProperyListener : objectArrayProperyListeners) {
							warnln("\t" + objectArrayProperyListener.getClass());
						}
					}
				}
				final Object[] tuple = { property, property.get() };
				m_capsulePropertyValuePairs.addElement(tuple);
			}
		}
	}

	public void restore() {
		final java.util.Enumeration<Object[]> preserves = m_capsulePropertyValuePairs.elements();
		while (preserves.hasMoreElements()) {
			final Object[] tuple = preserves.nextElement();
			final Property property = (Property) tuple[0];
			final Object value = tuple[1];
			property.set(value);
		}
		final Element[] elements = getDescendants();
		for (final Element element : elements) {
			if (m_capsuleElements.get(element) == null) {
				element.removeFromParent();
			}
		}
		for (final Element element : elements) {
			if (m_capsuleElements.get(element) == null) {
				element.release();
			}
		}
	}

	@Override
	protected void scheduleBehaviors(final double t) {
		super.scheduleBehaviors(t);
		for (int i = 0; i < sandboxes.size(); i++) {
			m_currentSandbox = (Sandbox) sandboxes.get(i);
			m_currentSandbox.scheduleBehaviors(t);
		}
	}

	public Sandbox getCurrentSandbox() {
		// todo:
		// this hack is placed here for script to execute on when the world
		// stops
		// if( m_currentSandbox != null ) {
		return m_currentSandbox;
		// } else {
		// return this;
		// }
	}
	// public void setCurrentSandbox( Sandbox currentSandbox ) {
	// m_currentSandbox = currentSandbox;
	// }

	public boolean isRunning() {
		return m_isRunning;
	}

	public void start() {
		if (HACK_s_isPropetryListeningDisabledWhileWorldIsRunning) {
			Property.HACK_disableListening();
		}
		if (m_scriptingFactory != null) {
			getInterpreter().start();
			final edu.cmu.cs.stage3.alice.scripting.Code code = script
					.getCode(edu.cmu.cs.stage3.alice.scripting.CompileType.EXEC_MULTIPLE);
			if (code != null) {
				exec(code);
			}
		}
		bubbles.clear();
		started(this, m_clock.getTime());
		m_isRunning = true;
	}

	public void schedule() {
		m_currentSandbox = this;
		scheduleBehaviors(m_clock.getTime());
		m_collisions = m_collisionManager.update(256);
		m_currentSandbox = null;
	}

	public void stop() {
		bubbles.clear();
		m_isRunning = false;
		if (m_scriptingFactory != null) {
			getInterpreter().stop();
		}
		stopped(this, m_clock.getTime());
		if (HACK_s_isPropetryListeningDisabledWhileWorldIsRunning) {
			Property.HACK_enableListening();
		}
	}
}
