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
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;

/**
 * @author Jason Pratt
 */
public class WorldDifferencesCapsule implements edu.cmu.cs.stage3.alice.core.event.PropertyListener,
		edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener,
		edu.cmu.cs.stage3.alice.core.event.ChildrenListener,
		edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener {
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.core.World world;
	protected java.util.HashMap<Object, Object> changedProperties = new java.util.HashMap<Object, Object>();
	protected java.util.ArrayList<Object> changedObjectArrayProperties = new java.util.ArrayList<Object>();
	protected java.util.ArrayList<Object> changedElements = new java.util.ArrayList<Object>();

	// need to keep track of where things are moving
	@SuppressWarnings("rawtypes")
	protected java.util.HashMap changedElementPositions = new java.util.HashMap(); // Unused ??

	// need to keep track of the order of changes so restore happens in the
	// right order
	protected java.util.ArrayList<String> changeOrder = new java.util.ArrayList<String>();

	protected boolean isListening;

	protected static final String elementChange = "elementChange";
	protected static final String propertyChange = "propertyChange";
	protected static final String objectArrayChange = "objectArrayChange";

	public WorldDifferencesCapsule(final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool,
			final edu.cmu.cs.stage3.alice.core.World world) {
		this.authoringTool = authoringTool;
		this.world = world;

		startListening();
	}

	public edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule getStateCapsule() {
		// System.out.println("\nCURRENT STATE: ");

		final edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule capsule = new edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule();

		java.util.Iterator<Object> iter = changedElements.iterator();

		iter = changedObjectArrayProperties.iterator();
		// System.out.println("\tChanged Object Array Properties:");
		while (iter.hasNext()) {
			final Object obj = iter.next();

			if (obj instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) {
				final int type = ((edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) obj).changeType;
				// Unused ?? final int oldPos = ((edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) obj).oldIndex;
				final int newPos = ((edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) obj).newIndex;
				Object o = ((edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) obj).value;
				// System.out.println( (
				// (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).property.getName());
				edu.cmu.cs.stage3.alice.core.Element value = null;
				if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
					value = (edu.cmu.cs.stage3.alice.core.Element) o;
				}

				o = ((edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) obj)
						.getAffectedObject();
				/* Unused ??
				edu.cmu.cs.stage3.alice.core.Element affected = null;
				if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
					affected = (edu.cmu.cs.stage3.alice.core.Element) o;
				}
				*/

				if (type == 1) {
					// System.out.println("\t\t INSERT: " + value.getKey() +
					// " in " + affected.getKey(this.world) + " POSITION: " +
					// newPos);
					capsule.addExistantElement(value.getKey(world));

					capsule.putElementPosition(value.getKey(world), newPos);

					// CALL to a user-defined response is handled a little
					// differently to get the parameters
					if (value instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
						final edu.cmu.cs.stage3.alice.core.Property params = value
								.getPropertyNamed("requiredActualParameters");
						final Object udobj = params.getValue();
						if (udobj instanceof edu.cmu.cs.stage3.alice.core.Variable[]) {
							final edu.cmu.cs.stage3.alice.core.Variable vars[] = (edu.cmu.cs.stage3.alice.core.Variable[]) udobj;
							if (vars != null) {
								for (final Variable var : vars) {
									// System.out.println("\t\t\tSet Property: "
									// + vars[i].getKey(world) + " to " +
									// vars[i].getValue());
									final String valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
											.getReprForValue(var.getValue(), true);
									capsule.putPropertyValue(var.getKey(world), valueRepr);
								}
							}
						}
					} else {
						// Properties
						final String[] visProps = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
								.getInitialVisibleProperties(value.getClass());
						if (visProps != null) {
							for (final String visProp2 : visProps) {
								final edu.cmu.cs.stage3.alice.core.Property visProp = ((edu.cmu.cs.stage3.alice.core.Response) value)
										.getPropertyNamed(visProp2);
								final String valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
										.getReprForValue(visProp.get(), true);
								capsule.putPropertyValue(value.getKey(world) + "." + visProp.getName(), valueRepr);
							}
						}
					}
				} else if (type == 2) {
					capsule.putElementPosition(value.getKey(world), newPos);
				} else {
					if (world.isAncestorOf(value)) {
					} else {
						capsule.addNonExistantElement(value.getKey());
					}
				}
			}
		}

		iter = changedProperties.keySet().iterator();
		while (iter.hasNext()) {
			final Object o = iter.next();
			final String keyAndProp = (String) o;

			final int lastPd = keyAndProp.lastIndexOf(".");
			final String key = keyAndProp.substring(0, lastPd);
			final String propName = keyAndProp.substring(lastPd + 1, keyAndProp.length());

			if (propName.indexOf("data") == -1) {
				final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(key);
				edu.cmu.cs.stage3.alice.core.Property p = null;
				if (e != null) {
					p = e.getPropertyNamed(propName);
				}
				if (p != null) {
					if (p.get() instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
						final edu.cmu.cs.stage3.alice.core.Property resp = ((edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) p
								.get()).getPropertyNamed("userDefinedResponse");
						String valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
								.getReprForValue(resp.get(), true);
						capsule.putPropertyValue(resp.getOwner().getKey(world) + ".userDefinedResponse", valueRepr);

						final edu.cmu.cs.stage3.alice.core.Property pars = ((edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) p
								.get()).getPropertyNamed("requiredActualParameters");

						final Object udobj = pars.getValue();
						if (udobj instanceof edu.cmu.cs.stage3.alice.core.Variable[]) {
							final edu.cmu.cs.stage3.alice.core.Variable vars[] = (edu.cmu.cs.stage3.alice.core.Variable[]) udobj;
							if (vars != null) {
								for (final Variable var : vars) {
									// System.out.println("\t\t\tSet Property: "
									// + vars[i].getKey(world) + " to " +
									// vars[i].getValue());
									valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
											.getReprForValue(var.getValue(), true);
									capsule.putPropertyValue(var.getKey(world), valueRepr);
								}
							}
						}

					} else {
						final String valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
								.getReprForValue(p.get(), true);
						// System.out.println("\t\t PROPERTY: " + key + "." +
						// propName + " SET TO: " + valueRepr);
						capsule.putPropertyValue(key + "." + propName, valueRepr);
					}
				}
			}

		}

		return capsule;
	}

	// START HERE.

	public java.util.Vector<Property> getChangedPropertiesNamed(final String propertyName) {
		final java.util.Vector<Property> props = new java.util.Vector<Property>();

		final java.util.Set<Object> changedProps = changedProperties.keySet();
		final java.util.Iterator<Object> iter = changedProps.iterator();
		while (iter.hasNext()) {
			final String propAndKey = (String) iter.next();
			final int endName = propAndKey.lastIndexOf(".");
			final String elName = propAndKey.substring(0, endName);
			final String propName = propAndKey.substring(endName + 1, propAndKey.length());
			if (propAndKey.endsWith(propertyName)) {
				final edu.cmu.cs.stage3.alice.core.Element el = world.getDescendantKeyed(elName);
				props.addElement(el.getPropertyNamed(propName));
			}
		}

		return props;
	}

	public boolean otherPropertyChangesMade(final java.util.Set<String> correctPropertyChangeSet) {

		final java.util.Set<Object> changedProps = changedProperties.keySet();
		final java.util.Iterator<Object> iter = changedProps.iterator();
		while (iter.hasNext()) {
			String propAndKey = (String) iter.next();

			// System.out.println("propAndKey: " + propAndKey);

			// to capture info, triggerResponses are saved differently
			if (propAndKey.endsWith("triggerResponse")) {
				final int lastPd = propAndKey.lastIndexOf(".");
				final String key = propAndKey.substring(0, lastPd);
				final String propName = propAndKey.substring(lastPd + 1, propAndKey.length());

				final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(key);
				edu.cmu.cs.stage3.alice.core.Property p = null;
				if (e != null) {
					p = e.getPropertyNamed(propName);
				}

				if (p.get() instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
					final edu.cmu.cs.stage3.alice.core.Property resp = ((edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) p
							.get()).getPropertyNamed("userDefinedResponse");

					propAndKey = resp.getOwner().getKey(world) + ".userDefinedResponse";
				}
			}

			// System.out.println(correctPropertyChangeSet);

			if (correctPropertyChangeSet != null && !correctPropertyChangeSet.contains(propAndKey)) {

				// right now, changes to the world are illegal
				if (propAndKey.endsWith("data")) {
				}
				if (propAndKey.endsWith("localTransformation")) {
					return true;
				} else if (propAndKey.endsWith("name")) {
					return true;
				} else if (propAndKey.endsWith("isCommentedOut")) {
					return true;
				} else {
					final String key = propAndKey.substring(0, propAndKey.lastIndexOf("."));
					final edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(key);
					// System.out.println(key + " " + e);
					if (e instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
						return true;
					}
				}

			}
		}

		// System.out.println("no illegal property changes");
		return false;
	}

	public boolean otherElementsShifted(final java.util.Set<String> correctElementsShifted) {
		// java.util.Set actualElementsShifted =
		// this.changedElementPositions.keySet();
		//
		// for( java.util.Iterator iter =
		// changedObjectArrayProperties.iterator(); iter.hasNext(); ) {
		// Object obj = iter.next();
		// if (obj instanceof
		// edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)
		// {
		// Object o = (
		// (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).value;
		// int type = (
		// (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).changeType;
		// edu.cmu.cs.stage3.alice.core.Element e =
		// (edu.cmu.cs.stage3.alice.core.Element)o;
		// String name = "";
		//
		// // this change was a position shift
		// if (type == 2) {
		// if ( correctElementsShifted.contains(e.getKey(world)) ) {
		// System.out.println("this is a legit addition");
		// } else {
		// return true;
		// }
		// }
		// }
		// }
		return false;
	}

	public boolean otherElementsInsertedOrDeleted(final String[] insertedNames, final String[] deletedNames) {

		final java.util.List<String> insertedList = java.util.Arrays.asList(insertedNames);
		final java.util.List<String> deletedList = java.util.Arrays.asList(deletedNames);

		final java.util.Vector<String> illegalInserts = new java.util.Vector<String>();

		for (final java.util.Iterator<Object> iter = changedObjectArrayProperties.iterator(); iter.hasNext();) {
			final Object obj = iter.next();
			// System.out.println(obj);
			if (obj instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) {
				final Object o = ((edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) obj).value;
				final int type = ((edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) obj).changeType;
				final edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element) o;
				String name = "";

				// System.out.println("value " + o + " " + type);
				if (e.isDescendantOf(world)) {
					name = e.getKey(world);
				} else {
					name = e.getKey();
				}

				if (!insertedList.contains(name) && type == 1) {
					// System.out.println("this was inserted and shouldn't have
					// been: "
					// + name);
					illegalInserts.addElement(name);
				}
				if (!deletedList.contains(name) && type == 3) {
					if (illegalInserts.contains(name)) {
						// an illegal insertion is being removed
						// System.out.println("removing an illegal delete: " +
						// name);
						illegalInserts.remove(name);
					} else {
						// this is an illegal delete
						// System.out.println("this was deleted and shouldn't
						// have been: \n"
						// + obj + "\n");

						// return true;
					}
				}
			}
		}

		// check to make sure that everything that was supposed to be removed
		// was.
		boolean insertsStillPresent = false;
		for (int i = 0; i < illegalInserts.size(); i++) {
			final edu.cmu.cs.stage3.alice.core.Element e = world
					.getDescendantKeyed(illegalInserts.elementAt(i));
			if (e != null) {
				insertsStillPresent = true;
			}

		}

		if (insertsStillPresent) {
			// System.out.println("something inserted or deleted that shouldn't
			// have been");
			return true;
		}
		return false;
	}

	synchronized public void restoreWorld() {
		setIsListening(false);

		final java.util.Iterator<Object> elementIterator = changedElements.iterator();

		final java.util.Iterator<Object> objectArrayIterator = changedObjectArrayProperties.iterator();

		for (final java.util.Iterator<String> iter = changeOrder.iterator(); iter.hasNext();) {
			final String changeType = iter.next();
			if (changeType.equals(WorldDifferencesCapsule.elementChange)) {
				if (elementIterator.hasNext()) {
					((UndoableRedoable) elementIterator.next()).undo();
				}
			} else if (changeType.equals(WorldDifferencesCapsule.objectArrayChange)) {
				if (objectArrayIterator.hasNext()) {
					((UndoableRedoable) objectArrayIterator.next()).undo();
				}
			}
		}

		for (final java.util.Iterator<Object> iter = changedProperties.keySet().iterator(); iter.hasNext();) {
			final String propertyKey = (String) iter.next();
			final Object oldValue = changedProperties.get(propertyKey);

			final int dotIndex = propertyKey.lastIndexOf(".");
			final String elementKey = propertyKey.substring(0, dotIndex);
			final String propertyName = propertyKey.substring(dotIndex + 1);

			final edu.cmu.cs.stage3.alice.core.Element propertyOwner = world.getDescendantKeyed(elementKey);
			if (propertyOwner != null) {
				// propertyOwner.setPropertyNamed( propertyName, oldValue );
				final edu.cmu.cs.stage3.alice.core.Property property = propertyOwner.getPropertyNamed(propertyName);
				// System.out.println("changing: " + property.getOwner().getKey(
				// world ) + "." + property.getName());
				property.set(oldValue);
			}
		}

		clear();

		setIsListening(true);
	}

	synchronized public void startListening() {
		authoringTool.addAuthoringToolStateListener(this);
		listenTo(world);
		setIsListening(true);
	}

	synchronized public void stopListening() {
		authoringTool.removeAuthoringToolStateListener(this);
		stopListeningTo(world);
		setIsListening(false);
	}

	synchronized public void dispose() {
		clear();
		stopListening();
		authoringTool = null;
		world = null;
	}

	synchronized public void setWorld(final edu.cmu.cs.stage3.alice.core.World world) {
		if (this.world != null) {
			stopListeningTo(this.world);
		}

		this.world = world;
		listenTo(world);
	}

	synchronized public void clear() {
		changedProperties.clear();
		changedObjectArrayProperties.clear();
		changedElements.clear();
		// removedElements.clear();
		// addedElements.clear();
	}

	synchronized protected void setIsListening(final boolean isListening) {
		this.isListening = isListening;
	}

	protected Object preChangeValue;

	@Override
	synchronized public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
		if (isListening) {
			// Unused ?? final edu.cmu.cs.stage3.alice.core.Property property = propertyEvent.getProperty();
			preChangeValue = propertyEvent.getProperty().get();
		}
	}

	@Override
	synchronized public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
		if (isListening) {
			final edu.cmu.cs.stage3.alice.core.Property property = propertyEvent.getProperty();
			final String propertyRepr = property.getOwner().getKey(world) + "." + property.getName();
			if (changedProperties.containsKey(propertyRepr)) {
				if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.equals(property.get(),
						changedProperties.get(propertyRepr))) {
					// if changing back to original value, remove entry
					changedProperties.remove(propertyRepr);
				}
			} else {
				changedProperties.put(propertyRepr, preChangeValue);
			}
		}
	}

	@Override
	synchronized public void objectArrayPropertyChanging(
			final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
	}

	@Override
	synchronized public void objectArrayPropertyChanged(
			final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		if (isListening) {
			changedObjectArrayProperties.add(0,
					new edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable(
							ev.getObjectArrayProperty(), ev.getChangeType(), ev.getOldIndex(), ev.getNewIndex(),
							ev.getItem()));
			changeOrder.add(0, objectArrayChange);
		}
	}

	@Override
	synchronized public void childrenChanging(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent) {
	}

	@Override
	synchronized public void childrenChanged(final edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent) {
		if (isListening) {
			changedElements.add(0,
					new edu.cmu.cs.stage3.alice.authoringtool.util.ChildChangeUndoableRedoable(childrenEvent));
			changeOrder.add(0, elementChange);

			final int changeType = childrenEvent.getChangeType();
			if (changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED) {
				listenTo(childrenEvent.getChild());
			} else if (changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) {
				stopListeningTo(childrenEvent.getChild());
			}
		}
	}

	synchronized public void listenTo(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null) {
			final edu.cmu.cs.stage3.alice.core.Element[] elements = element.getDescendants();
			for (final Element element2 : elements) {
				final edu.cmu.cs.stage3.alice.core.Property[] properties = element2.getProperties();
				for (final Property propertie : properties) {
					if (propertie instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
						((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) propertie)
								.addObjectArrayPropertyListener(this);
					} else {
						propertie.addPropertyListener(this);
					}
				}
				boolean alreadyChildrenListening = false;
				final edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = element2
						.getChildrenListeners();
				for (final ChildrenListener childrenListener : childrenListeners) {
					if (childrenListener == this) {
						alreadyChildrenListening = true;
					}
				}
				if (!alreadyChildrenListening) {
					element2.addChildrenListener(this);
				}
			}
		}
	}

	synchronized public void stopListeningTo(final edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null) {
			final edu.cmu.cs.stage3.alice.core.Element[] elements = element.getDescendants();
			for (final Element element2 : elements) {
				final edu.cmu.cs.stage3.alice.core.Property[] properties = element2.getProperties();
				for (final Property propertie : properties) {
					if (propertie instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
						((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) propertie)
								.removeObjectArrayPropertyListener(this);
					} else {
						propertie.removePropertyListener(this);
					}
				}
				element2.removeChildrenListener(this);
			}
		}
	}

	// /////////////////////////////////////////////
	// AuthoringToolStateListener interface
	// /////////////////////////////////////////////
	@Override
	public void stateChanged(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
		if (ev.getCurrentState() == edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent.RUNTIME_STATE) {
			setIsListening(false);
		} else {
			setIsListening(true);
		}
	}

	@Override
	public void worldUnLoading(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
		// TODO
	}

	@Override
	public void worldLoaded(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
		// TODO
	}

	@Override
	public void stateChanging(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldLoading(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldStarting(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldStopping(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldPausing(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldSaving(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldUnLoaded(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldStarted(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldStopped(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldPaused(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void worldSaved(final edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
}