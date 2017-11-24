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

import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.util.Enumerable;
import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class PopupMenuUtilities {
	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration
			.getLocalConfiguration(AuthoringTool.class.getPackage());

	protected static java.util.HashMap<Class<?>, List<Object>> recentlyUsedValues = new java.util.HashMap<>();

	protected static java.util.Hashtable<Runnable, ActionListener> runnablesToActionListeners = new java.util.Hashtable<>();
	public final static PopupItemFactory oneShotFactory = new PopupItemFactory() {
		@Override
		public Object createItem(final Object o) {
			return new Runnable() {
				@Override
				public void run() {
					if (o instanceof ResponsePrototype) {
						final edu.cmu.cs.stage3.alice.core.Response response = 
								((ResponsePrototype) o).createNewResponse();
						final edu.cmu.cs.stage3.alice.core.Response undoResponse = 
								AuthoringToolResources.createUndoResponse(response);
						final edu.cmu.cs.stage3.alice.core.Property[] properties = AuthoringToolResources
								.getAffectedProperties(response);
						AuthoringTool.getHack().performOneShot(response,
								undoResponse, properties);
					}
				}
			};
		}
	};

	public final static javax.swing.Icon currentValueIcon = AuthoringToolResources
			.getIconForValue("currentValue");
	public final static Object NO_CURRENT_VALUE = new Object();

	protected static edu.cmu.cs.stage3.util.Criterion isNamedElement = new edu.cmu.cs.stage3.util.Criterion() {
		@Override
		public boolean accept(final Object o) {
			if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
				if (((edu.cmu.cs.stage3.alice.core.Element) o).name.get() != null) {
					return true;
				}
			}
			return false;
		}
	};
	// protected static edu.cmu.cs.stage3.util.NotCriterion isNotActualParameter
	// = new edu.cmu.cs.stage3.util.NotCriterion( new
	// edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
	// edu.cmu.cs.stage3.alice.core.ActualParameter.class ) );
	protected static edu.cmu.cs.stage3.util.Criterion isNotActualParameter = new edu.cmu.cs.stage3.util.Criterion() {
		@Override
		public boolean accept(final Object o) {
			if (o instanceof edu.cmu.cs.stage3.alice.core.Variable) {
				final edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) o;
				if (variable.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
					final edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse callToUserDefinedResponse = (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) variable
							.getParent();
					if (callToUserDefinedResponse.requiredActualParameters.contains(variable)) {
						return false;
					} else if (callToUserDefinedResponse.keywordActualParameters.contains(variable)) {
						return false;
					}
				} else if (variable
						.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) {
					final edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion callToUserDefinedQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) variable
							.getParent();
					if (callToUserDefinedQuestion.requiredActualParameters.contains(variable)) {
						return false;
					} else if (callToUserDefinedQuestion.keywordActualParameters.contains(variable)) {
						return false;
					}
				}
			}

			return true;
		}
	};

	protected static java.util.HashMap<String, String> specialStringMap = new java.util.HashMap<>();

	static {
		javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false); // since
																			// we
																			// mix
																			// heavy
																			// and
																			// lightweight
																			// components

		specialStringMap.put("<keyCode>", "a key");
		specialStringMap.put("<keyCode>", "a key");
		specialStringMap.put("<mouse>", "the mouse");
		specialStringMap.put("<onWhat>", "something");
		specialStringMap.put("<condition>", "something");
		specialStringMap.put("<variable>", "a variable");
		specialStringMap.put("<arrowKeys>", "the arrow keys");
		specialStringMap.put("<button>", "");
	}

	public static void addRecentlyUsedValue(final Class<?> valueClass, final Object value) {
		if (!recentlyUsedValues.containsKey(valueClass)) {
			recentlyUsedValues.put(valueClass, new java.util.ArrayList<Object>());
		}
		final java.util.List<Object> recentList = recentlyUsedValues.get(valueClass);
		while (recentList.contains(value)) {
			recentList.remove(value);
		}
		recentList.add(0, value);
	}

	public static void clearRecentlyUsedValues() {
		recentlyUsedValues.clear();
	}

	public static void createAndShowPopupMenu(final Vector<?> structure, final java.awt.Component component, final int x,
			final int y) {
		final javax.swing.JPopupMenu popup = makePopupMenu(structure);
		popup.show(component, x, y);
		ensurePopupIsOnScreen(popup);
	}

	public static JPopupMenu makePopupMenu(final Vector<?> structure) {
		final AliceMenuWithDelayedPopup menu = makeMenu("", structure);
		if (menu != null) {
			return menu.getPopupMenu();
		}
		return null;
	}

	// public static JMenu makeMenu( String title, Vector structure ) {
	// if( structure == null || structure.isEmpty() ) {
	// return null;
	// } else {
	// JMenu menu = new AliceMenu( title );
	// // JMenu menu = new JMenu( title );
	// menu.setUI( new AliceMenuUI() );
	// menu.setDelay( 0 );
	//
	// for( java.util.Enumeration enum0. = structure.elements();
	// enum0.hasMoreElements(); ) {
	// Object o = enum0.nextElement();
	// if( !(o instanceof StringObjectPair) ) {
	// throw new IllegalArgumentException(
	// "structure must be made only of StringObjectPairs, found: " + o );
	// }
	//
	// StringObjectPair pair = (StringObjectPair)o;
	// String name = pair.getString();
	//
	// //hack
	// if( name != null ) {
	// for( java.util.Iterator iter = specialStringMap.keySet().iterator();
	// iter.hasNext(); ) {
	// String s = (String)iter.next();
	// if( name.indexOf( s ) > -1 ) {
	// StringBuffer sb = new StringBuffer( name );
	// sb.replace( name.indexOf( s ), name.indexOf( s ) + s.length(),
	// (String)specialStringMap.get( s ) );
	// name = sb.toString();
	// }
	// }
	// }
	//
	// Object content = pair.getObject();
	// if( content instanceof Vector ) {
	// JMenu submenu = makeMenu( name, (Vector<Object>)content );
	// if( submenu != null ) {
	// menu.add( submenu );
	// }
	// } else if( content instanceof java.awt.event.ActionListener ) {
	// JMenuItem menuItem = makeMenuItem( name );
	// menuItem.addActionListener( (java.awt.event.ActionListener)content );
	// menu.add( menuItem );
	// } else if( content instanceof Runnable ) {
	// JMenuItem menuItem = makeMenuItem( name );
	// java.awt.event.ActionListener listener =
	// (java.awt.event.ActionListener)runnablesToActionListeners.get( content );
	// if( listener == null ) {
	// listener = new PopupMenuItemActionListener( (Runnable)content, menu
	// /*MEMFIX*/ );
	// //MEMFIX runnablesToActionListeners.put( content, listener );
	// }
	// menuItem.addActionListener( listener );
	// menu.add( menuItem );
	// } else if( content == javax.swing.JSeparator.class ) {
	// menu.addSeparator();
	// } else if( content instanceof java.awt.Component ) {
	// menu.add( (java.awt.Component)content );
	// } else if( content == null ) {
	// javax.swing.JLabel label = new javax.swing.JLabel( name );
	// label.setBorder( javax.swing.BorderFactory.createEmptyBorder( 1, 4, 1, 4
	// ) );
	// menu.add( label );
	// }
	// }
	//
	// return menu;
	// }
	// }

	public static AliceMenuWithDelayedPopup makeMenu(final String title, final Vector<?> structure) {
		if (structure == null || structure.isEmpty()) {
			return null;
		} else {
			final AliceMenuWithDelayedPopup menu = new AliceMenuWithDelayedPopup(title, structure);
			menu.setUI(new AliceMenuUI());
			menu.setDelay(0);
			return menu;
		}
	}

	public static void populateDelayedMenu(final AliceMenuWithDelayedPopup menu, final Vector<?> structure) {
		for (final Enumeration<?> enum0 = structure.elements(); enum0.hasMoreElements();) {
			final Object o = enum0.nextElement();
			if (!(o instanceof StringObjectPair)) {
				throw new IllegalArgumentException("structure must be made only of StringObjectPairs, found: " + o);
			}

			final StringObjectPair pair = (StringObjectPair) o;
			String name = pair.getString();

			// hack
			if (name != null) {
				for (final java.util.Iterator<String> iter = specialStringMap.keySet().iterator(); iter.hasNext();) {
					final String s = (String) iter.next();
					if (name.indexOf(s) > -1) {
						final StringBuffer sb = new StringBuffer(name);
						sb.replace(name.indexOf(s), name.indexOf(s) + s.length(), (String) specialStringMap.get(s));
						name = sb.toString();
					}
				}
			}

			Object content = pair.getObject();
			if (content instanceof DelayedBindingPopupItem) {
				content = ((DelayedBindingPopupItem) content).createItem();
			}

			javax.swing.Icon icon = null;
			if (content instanceof PopupItemWithIcon) {
				icon = ((PopupItemWithIcon) content).getIcon();
				content = ((PopupItemWithIcon) content).getItem();
			}

			if (content instanceof Vector) {
				@SuppressWarnings("unchecked")
				final JMenu submenu = makeMenu(name, (Vector<Object>) content);
				if (submenu != null) {
					menu.add(submenu);
				}
			} else if (content instanceof java.awt.event.ActionListener) {
				final JMenuItem menuItem = makeMenuItem(name, icon);
				menuItem.addActionListener((java.awt.event.ActionListener) content);
				menu.add(menuItem);
			} else if (content instanceof Runnable) {
				final JMenuItem menuItem = makeMenuItem(name, icon);
				java.awt.event.ActionListener listener = runnablesToActionListeners
						.get(content);
				if (listener == null) {
					listener = new PopupMenuItemActionListener((Runnable) content, menu /* MEMFIX */);
					// MEMFIX runnablesToActionListeners.put( content, listener
					// );
				}
				menuItem.addActionListener(listener);
				menu.add(menuItem);
			} else if (content == javax.swing.JSeparator.class) {
				menu.addSeparator();
			} else if (content instanceof java.awt.Component) {
				menu.add((java.awt.Component) content);
			} else if (content == null) {
				final javax.swing.JLabel label = new javax.swing.JLabel(name);
				label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4));
				menu.add(label);
			}
		}
	}

	// public static JPopupMenu makePopupMenu(
	// edu.cmu.cs.stage3.alice.authoringtool.util.Callback callback, Object
	// context, Vector structure ) {
	// return makeMenu( "", callback, context, structure ).getPopupMenu();
	// }
	//
	// public static JMenu makeMenu( String title,
	// edu.cmu.cs.stage3.alice.authoringtool.util.Callback callback, Object
	// context, Vector structure ) {
	// if( structure == null || structure.isEmpty() ) {
	// return null;
	// } else {
	// JMenu menu = new JMenu( title );
	//
	// for( java.util.Enumeration enum0. = structure.elements();
	// enum0.hasMoreElements(); ) {
	// Object o = enum0.nextElement();
	// if( !(o instanceof StringObjectPair) ) {
	// throw new IllegalArgumentException(
	// "structure must be made only of StringObjectPairs" );
	// }
	//
	// StringObjectPair pair = (StringObjectPair)o;
	// String name = pair.getString();
	// Object content = pair.getObject();
	// if( content instanceof Vector ) {
	// JMenu submenu = makeMenu( name, callback, context,
	// (Vector<Object>)content );
	// if( submenu != null ) {
	// menu.add( submenu );
	// }
	// } else if( content == javax.swing.JSeparator.class ) {
	// menu.addSeparator();
	// } else {
	// JMenuItem menuItem = makeMenuItem( name );
	// java.awt.event.ActionListener listener = new
	// PopupMenuItemCallbackActionListener( callback, context, content );
	// menuItem.addActionListener( listener );
	// menu.add( menuItem );
	// }
	// }
	//
	// return menu;
	// }
	// }

	public static JMenuItem makeMenuItem(final String text) {
		return makeMenuItem(text, null);
	}

	public static JMenuItem makeMenuItem(final String text, final javax.swing.Icon icon) {
		JMenuItem item;
		if (icon != null) {
			item = new JMenuItem(text, icon);
		} else {
			item = new JMenuItem(text);
		}
		item.setUI(new AliceMenuItemUI());
		return item;
	}

	@SuppressWarnings("unchecked")
	public static boolean isStringInStructure(final String s, final Vector<Object> structure) {
		for (final Enumeration<Object> enum0 = structure.elements(); enum0.hasMoreElements();) {
			final StringObjectPair pair = (StringObjectPair) enum0.nextElement();
			final String string = pair.getString();
			final Object content = pair.getObject();
			if (string == s) {
				return true;
			} else if (content instanceof Vector) {
				if (isStringInStructure(s, (Vector<Object>) content)) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static boolean isObjectInStructure(final Object o, final Vector<Object> structure) {
		for (final Enumeration<Object> enum0 = structure.elements(); enum0.hasMoreElements();) {
			final StringObjectPair pair = (StringObjectPair) enum0.nextElement();
			final Object content = pair.getObject();
			if (content == o) {
				return true;
			} else if (content instanceof Vector) {
				if (isObjectInStructure(o, (Vector<Object>) content)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Vector<Object> makeElementStructure(final Element root,
			final edu.cmu.cs.stage3.util.Criterion criterion, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context, final Object currentValue) {
		DelayedBindingPopupItem delayedBindingPopupItem;
		final Vector<Object> structure = new Vector<>();
		// System.out.println("making structure: root: "+root+", context:
		// "+context);
		if (criterion.accept(root)) {
			if (root.equals(currentValue)) {
				structure.addElement(new StringObjectPair("the entire " + (String) root.name.getValue(),
						new PopupItemWithIcon(factory.createItem(root), currentValueIcon)));
			} else {
				structure.addElement(
						new StringObjectPair("the entire " + (String) root.name.getValue(), factory.createItem(root)));
			}
			if (root.getChildCount() > 0) {
				final Element[] children = root.getChildren();
				for (final Element child : children) {
					if (child.getChildCount() == 0) {
						if (criterion.accept(child)) {
							if (child.equals(currentValue)) {
								structure.addElement(new StringObjectPair((String) child.name.getValue(),
										new PopupItemWithIcon(factory.createItem(child), currentValueIcon)));
							} else {
								structure.addElement(new StringObjectPair((String) child.name.getValue(),
										factory.createItem(child)));
							}
						}
					} else {
						if (child.search(criterion).length > 0 || criterion.accept(child)) {
							delayedBindingPopupItem = new DelayedBindingPopupItem() {
								@Override
								public Object createItem() {
									final Vector<Object> subStructure = makeElementStructure(child, criterion,
											factory, context, currentValue);
									if (subStructure.size() == 1 && criterion.accept(child)) {
										if (child.equals(currentValue)) {
											return new PopupItemWithIcon(factory.createItem(child), currentValueIcon);
										} else {
											return factory.createItem(child);
										}
									} else {
										return subStructure;
									}
								}
							};
							structure.addElement(
									new StringObjectPair((String) child.name.getValue(), delayedBindingPopupItem));
						}
					}
				}

				if (structure.size() > 1) {
					structure.insertElementAt(new StringObjectPair("Separator", javax.swing.JSeparator.class), 1);
				}
			}
		} else {
			final Element[] children = root.getChildren();
			for (final Element child : children) {
				if (child.getChildCount() == 0) {
					if (criterion.accept(child)) {
						if (child.equals(currentValue)) {
							structure.addElement(new StringObjectPair((String) child.name.getValue(),
									new PopupItemWithIcon(factory.createItem(child), currentValueIcon)));
						} else {
							structure.addElement(
									new StringObjectPair((String) child.name.getValue(), factory.createItem(child)));
						}
					}
				} else {
					if (child.search(criterion).length > 0 || criterion.accept(child)) {
						delayedBindingPopupItem = new DelayedBindingPopupItem() {
							@Override
							public Object createItem() {
								final Vector<Object> subStructure = makeElementStructure(child, criterion, factory,
										context, currentValue);
								if (subStructure.size() == 1 && criterion.accept(child)) {
									if (child.equals(currentValue)) {
										return new PopupItemWithIcon(factory.createItem(child), currentValueIcon);
									} else {
										return factory.createItem(child);
									}
								} else {
									return subStructure;
								}
							}
						};
						structure.addElement(
								new StringObjectPair((String) child.name.getValue(), delayedBindingPopupItem));
					}
				}
			}
		}

		return structure;
	}

	public static Vector<Object> makeFlatElementStructure(final Element root,
			final edu.cmu.cs.stage3.util.Criterion criterion, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context, final Object currentValue) {
		final Vector<Object> structure = new Vector<>();

		final edu.cmu.cs.stage3.alice.core.Element[] elements = root.search(criterion);
		for (final Element element : elements) {
			String text = AuthoringToolResources.getReprForValue(element);
			if (context != null) {
				text = AuthoringToolResources.getNameInContext(element, context);
			}
			if (element.equals(currentValue)) {
				structure.addElement(new StringObjectPair(text,
						new PopupItemWithIcon(factory.createItem(element), currentValueIcon)));
			} else {
				structure.addElement(new StringObjectPair(text, factory.createItem(element)));
			}
		}

		return structure;
	}

	// public static Vector<Object> makeFlatExpressionStructure( Class
	// valueClass, PopupItemFactory factory,
	// edu.cmu.cs.stage3.alice.core.Element context ) {
	// Vector<Object> structure = new Vector<>();
	//
	// if( context != null ) {
	// edu.cmu.cs.stage3.alice.core.Expression[] expressions =
	// context.findAccessibleExpressions( valueClass );
	// for( int i = 0; i < expressions.length; i++ ) {
	// String text =
	// AuthoringToolResources.getReprForValue(
	// expressions[i] );
	// if( context != null ) {
	// text =
	// AuthoringToolResources.getNameInContext(
	// expressions[i], context );
	// }
	// if( expressions[i] instanceof
	// edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion ) {
	// edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype
	// prototype = new
	// edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype(
	// (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)expressions[i]
	// );
	// if( prototype.getDesiredProperties().length > 0 ) {
	// structure.addElement( new StringObjectPair( text, makePrototypeStructure(
	// prototype, factory, context ) ) );
	// } else {
	// structure.addElement( new StringObjectPair( text, factory.createItem(
	// prototype.createNewElement() ) ) );
	// }
	// } else {
	// structure.addElement( new StringObjectPair( text, factory.createItem(
	// expressions[i] ) ) );
	// }
	// }
	// }
	//
	// return structure;
	// }
	public static Vector<Object> makeFlatExpressionStructure(final Class<?> valueClass, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context, final Object currentValue) {
		return makeFlatExpressionStructure(valueClass, null, factory, context, currentValue);
	}

	public static Vector<Object> makeFlatExpressionStructure(final Class<?> valueClass,
			final edu.cmu.cs.stage3.util.Criterion criterion, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context, final Object currentValue) {
		final Vector<Object> structure = new Vector<>();
		if (context != null) {
			final edu.cmu.cs.stage3.alice.core.Expression[] expressions = context.findAccessibleExpressions(valueClass);
			for (final Expression expression : expressions) {
				if (criterion == null || criterion.accept(expression)) {
					String text = AuthoringToolResources
							.getReprForValue(expression);
					if (context != null) {
						text = AuthoringToolResources.getNameInContext(expression,
								context);
					}
					if (expression instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
						final edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype prototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype(
								(edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) expression);
						if (prototype.getDesiredProperties().length > 0) {
							structure.addElement(
									new StringObjectPair(text, makePrototypeStructure(prototype, factory, context)));
						} else {
							structure.addElement(
									new StringObjectPair(text, factory.createItem(prototype.createNewElement())));
						}
					} else {
						if (expression.equals(currentValue)) {
							structure.addElement(new StringObjectPair(text,
									new PopupItemWithIcon(factory.createItem(expression), currentValueIcon)));
						} else {
							structure.addElement(new StringObjectPair(text, factory.createItem(expression)));
						}
					}
				}
			}
		}

		return structure;
	}

	public static Vector<Object> makeElementAndExpressionStructure(final Element root, final Class<?> valueClass,
			final PopupItemFactory factory, final boolean makeFlat, final edu.cmu.cs.stage3.alice.core.Element context,
			final Object currentValue) {
		Vector<Object> structure;
		if (makeFlat) {
			structure = makeFlatElementStructure(root,
					new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(valueClass), factory, context,
					currentValue);
		} else {
			structure = makeElementStructure(root, new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(valueClass),
					factory, context, currentValue);
		}

		final Vector<Object> expressionStructure = makeFlatExpressionStructure(valueClass, factory, context,
				currentValue);
		if (expressionStructure != null && expressionStructure.size() > 0) {
			// Unused ?? final String className = valueClass.getName();
			if (structure.size() > 0) {
				structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
			}
			structure.add(new StringObjectPair("expressions", expressionStructure));
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ResponsePrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makeResponseStructure(final edu.cmu.cs.stage3.alice.core.Element element,
			final PopupItemFactory factory, final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();
		Class<?> valueClass = element.getClass();
		if (element instanceof edu.cmu.cs.stage3.alice.core.Expression) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.Expression) element).getValueClass();
		}

		final Vector<Object> oneShotStructure = AuthoringToolResources.getOneShotStructure(valueClass);
		if (oneShotStructure != null && oneShotStructure.size() > 0) {
			boolean isFirst = true;
			final String[] groupsToUse = AuthoringToolResources
					.getOneShotGroupsToInclude();
			for (int i = 0; i < oneShotStructure.size(); i++) {
				final StringObjectPair sop = (StringObjectPair) oneShotStructure
						.get(i); // pull
									// off
									// first
									// group,
									// usually
									// "common animations"
				final String currentGroupName = sop.getString();
				boolean useIt = false;
				for (final String element2 : groupsToUse) {
					if (currentGroupName.compareTo(element2) == 0) {
						useIt = true;
						continue;
					}
				}
				if (useIt) {
					if (!isFirst) {
						structure.add(
								new StringObjectPair("separator", javax.swing.JSeparator.class));
					} else {
						isFirst = false;
					}
					@SuppressWarnings("unchecked")
					final Vector<Object> responseNames = (Vector<Object>) sop.getObject();
					structure.addAll(makeOneShotStructure(responseNames, element, factory, context));
				}
			}
			if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization) {
				final edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization visualization = (edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization) element;
				final edu.cmu.cs.stage3.alice.core.Collection collection = visualization.getItemsCollection();
				if (collection instanceof edu.cmu.cs.stage3.alice.core.List
						|| collection instanceof edu.cmu.cs.stage3.alice.core.Array) {
					if (collection != null && collection.values.size() > 0 && edu.cmu.cs.stage3.alice.core.Model.class
							.isAssignableFrom(collection.valueClass.getClassValue())) {
						if (structure.size() > 0) {
							structure.add(new StringObjectPair("Separator",
									javax.swing.JSeparator.class));
						}
						final DelayedBindingPopupItem delayedBindingPopupItem = new DelayedBindingPopupItem() {
							@Override
							public Object createItem() {
								final Vector<Object> subStructure = new Vector<>();
								edu.cmu.cs.stage3.alice.core.Question question = null;
								final Object[] items = collection.values.getArrayValue();
								if (collection instanceof edu.cmu.cs.stage3.alice.core.List) {
									// Item at beginning
									question = new edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning();
									((edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning) question).subject
											.set(visualization);
									Vector<Object> responseStructure = PopupMenuUtilities
											.makeResponseStructure(question, factory, context);
									subStructure.add(new StringObjectPair("item at the beginning", responseStructure));

									// Item at index
									for (int i = 0; i < items.length; i++) {
										question = new edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex();
										((edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex) question).subject
												.set(visualization);
										((edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex) question).index
												.set(new Double(i));
										responseStructure = PopupMenuUtilities.makeResponseStructure(question, factory,
												context);
										subStructure.add(new StringObjectPair("item " + i, responseStructure));
									}

									// Item at end
									question = new edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd();
									((edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd) question).subject
											.set(visualization);
									responseStructure = PopupMenuUtilities.makeResponseStructure(question, factory,
											context);
									subStructure.add(new StringObjectPair("item at the end", responseStructure));
								} else if (collection instanceof edu.cmu.cs.stage3.alice.core.Array) {
									for (int i = 0; i < items.length; i++) {
										question = new edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex();
										((edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex) question).subject
												.set(visualization);
										((edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex) question).index
												.set(new Double(i));
										final Vector<Object> responseStructure = PopupMenuUtilities
												.makeResponseStructure(question, factory, context);
										subStructure.add(new StringObjectPair("element " + i, responseStructure));
									}
								}
								return subStructure;
							}
						};
						structure.add(new StringObjectPair("item responses", delayedBindingPopupItem));
					}
				}
			}
			if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization) {
				final edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization visualization = (edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization) element;
				structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
				final edu.cmu.cs.stage3.alice.core.Question question = new edu.cmu.cs.stage3.alice.core.question.visualization.model.Item();
				((edu.cmu.cs.stage3.alice.core.question.visualization.model.Item) question).subject.set(visualization);
				final Vector<Object> responseStructure = PopupMenuUtilities.makeResponseStructure(question, factory,
						context);
				structure.add(new StringObjectPair("item", responseStructure));
			}
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ResponsePrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makeExpressionResponseStructure(
			final edu.cmu.cs.stage3.alice.core.Expression expression, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		// if it's a variable, allow user to set it
		if (expression instanceof edu.cmu.cs.stage3.alice.core.Variable) {
			final StringObjectPair[] known = {
					new StringObjectPair("element", expression),
					new StringObjectPair("propertyName", "value"),
					new StringObjectPair("duration", new Integer(0)) };
			final String[] desired = { "value" };
			final ResponsePrototype rp = new ResponsePrototype(
					edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class, known, desired);
			final Vector<Object> setValueStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
					.makePrototypeStructure(rp, factory, context);
			if (setValueStructure != null && !setValueStructure.isEmpty()) {
				structure.add(new StringObjectPair("set value", setValueStructure));
			}
		}

		// cascade to appropriate responses
		final Vector<Object> oneShotStructure = AuthoringToolResources.getOneShotStructure(expression.getValueClass());
		if (oneShotStructure != null && oneShotStructure.size() > 0) {
			boolean isFirst = true;
			final String[] groupsToUse = AuthoringToolResources
					.getOneShotGroupsToInclude();
			for (int i = 0; i < oneShotStructure.size(); i++) {
				final StringObjectPair sop = (StringObjectPair) oneShotStructure
						.get(i); // pull
									// off
									// first
									// group,
									// usually
									// "common animations"
				final String currentGroupName = sop.getString();
				boolean useIt = false;
				for (final String element : groupsToUse) {
					if (currentGroupName.compareTo(element) == 0) {
						useIt = true;
						continue;
					}
				}
				if (useIt) {
					if (!isFirst) {
						structure.add(
								new StringObjectPair("separator", javax.swing.JSeparator.class));
					} else {
						isFirst = false;
					}
					@SuppressWarnings("unchecked")
					final Vector<Object> responseNames = (Vector<Object>) sop.getObject();
					final Vector<Object> subStructure = makeOneShotStructure(responseNames, expression, factory,
							context);

					if (subStructure.size() > 0) {
						if (structure.size() > 0) {
							structure.add(new StringObjectPair("Separator",
									javax.swing.JSeparator.class));
						}
						structure.addAll(subStructure);
					}
					// structure.addAll( makeOneShotStructure( responseNames,
					// expression, factory, context ) );
				}
			}

		}

		if (expression instanceof edu.cmu.cs.stage3.alice.core.Variable) {
			final edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) expression;
			if (edu.cmu.cs.stage3.alice.core.Collection.class.isAssignableFrom(expression.getValueClass())) {
				final edu.cmu.cs.stage3.alice.core.Collection collection = (edu.cmu.cs.stage3.alice.core.Collection) variable.value
						.get();
				if (collection instanceof edu.cmu.cs.stage3.alice.core.List
						|| collection instanceof edu.cmu.cs.stage3.alice.core.Array) {
					if (collection != null && collection.values.size() > 0 && edu.cmu.cs.stage3.alice.core.Model.class
							.isAssignableFrom(collection.valueClass.getClassValue())) {
						if (structure.size() > 0) {
							structure.add(new StringObjectPair("Separator",
									javax.swing.JSeparator.class));
						}
						final DelayedBindingPopupItem delayedBindingPopupItem = new DelayedBindingPopupItem() {
							@Override
							public Object createItem() {
								final Vector<Object> subStructure = new Vector<>();
								final Object[] items = collection.values.getArrayValue();
								for (int i = 0; i < items.length; i++) {
									edu.cmu.cs.stage3.alice.core.Question question = null;
									if (collection instanceof edu.cmu.cs.stage3.alice.core.List) {
										question = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex();
										((edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex) question).list
												.set(expression);
										((edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex) question).index
												.set(new Double(i));
									} else if (collection instanceof edu.cmu.cs.stage3.alice.core.Array) {
										question = new edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex();
										((edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex) question).array
												.set(expression);
										((edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex) question).index
												.set(new Double(i));
									}
									final Vector<Object> responseStructure = PopupMenuUtilities
											.makeResponseStructure(question, factory, context);
									subStructure.add(new StringObjectPair("item" + i, responseStructure));
								}
								return subStructure;
							}
						};
						structure.add(new StringObjectPair("item responses", delayedBindingPopupItem));
					}
				}
			}
		}

		return structure;
	}

	public static Vector<Object> makeOneShotStructure(final Vector<Object> responseNames,
			final edu.cmu.cs.stage3.alice.core.Element element, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();
		if (responseNames != null) {
			// Unused ?? final int i = 0;
			for (final Iterator<Object> iter = responseNames.iterator(); iter.hasNext();) {
				final Object item = iter.next();
				if (item instanceof String) {
					final String className = (String) item;
					try {
						if (className.startsWith("edu.cmu.cs.stage3.alice.core.response.PropertyAnimation")) {
							final String propertyName = AuthoringToolResources
									.getSpecifier(className);
							if (propertyName.equals("vehicle")) {
								final StringObjectPair[] knownPropertyValues = new StringObjectPair[] {
										new StringObjectPair("element", element),
										new StringObjectPair("propertyName", propertyName),
										new StringObjectPair("duration", new Double(0.0)), };
								final String[] desiredProperties = new String[] { "value" };
								final ResponsePrototype responsePrototype = new ResponsePrototype(
										edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class,
										knownPropertyValues, desiredProperties);
								final String responseName = AuthoringToolResources
										.getFormattedReprForValue(
												edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class,
												knownPropertyValues);
								final Vector<Object> subStructure = makePrototypeStructure(responsePrototype, factory,
										context);
								structure.add(new StringObjectPair(responseName, subStructure));
							} else {
								final StringObjectPair[] knownPropertyValues = new StringObjectPair[] {
										new StringObjectPair("element", element),
										new StringObjectPair("propertyName", propertyName) };
								final String[] desiredProperties = new String[] { "value" };
								final ResponsePrototype responsePrototype = new ResponsePrototype(
										edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class,
										knownPropertyValues, desiredProperties);
								final String responseName = AuthoringToolResources
										.getFormattedReprForValue(
												edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class,
												knownPropertyValues);
								final Vector<Object> subStructure = makePrototypeStructure(responsePrototype, factory,
										context);
								structure.add(new StringObjectPair(responseName, subStructure));
							}
						} else {
							@SuppressWarnings("unchecked")
							final Class<? extends edu.cmu.cs.stage3.alice.core.Element> responseClass = 
									(Class<? extends Element>) Class.forName(className);
							final java.util.LinkedList<StringObjectPair> known = new java.util.LinkedList<StringObjectPair>();
							final String format = AuthoringToolResources
									.getFormat(responseClass);
							final edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(
									format);
							while (tokenizer.hasMoreTokens()) {
								final String token = tokenizer.nextToken();
								// System.out.println("token: "+token);
								if (token.startsWith("<<<") && token.endsWith(">>>")) {
									final String propertyName = token.substring(token.lastIndexOf("<") + 1,
											token.indexOf(">"));
									// System.out.println("property name:
									// "+propertyName+", element "+element);
									known.add(new StringObjectPair(propertyName, element));
								}
							}
							final StringObjectPair[] knownPropertyValues = known
									.toArray(new StringObjectPair[0]);
							final String[] desiredProperties = AuthoringToolResources
									.getDesiredProperties(responseClass);
							final ResponsePrototype responsePrototype = new ResponsePrototype(
									responseClass, knownPropertyValues, desiredProperties);
							final String responseName = AuthoringToolResources
									.getFormattedReprForValue(responseClass, knownPropertyValues);

							if (responsePrototype.getDesiredProperties().length > 0) {
								final Vector<Object> subStructure = makePrototypeStructure(responsePrototype, factory,
										context);
								structure.add(new StringObjectPair(responseName, subStructure));
							} else {
								structure.add(new StringObjectPair(responseName,
										factory.createItem(responsePrototype)));
							}
						}
					} catch (final Throwable t) {
						AuthoringTool
								.showErrorDialog("Error creating popup item.", t);
					}
				} else if (item instanceof StringObjectPair) {
					try {
						final String label = ((StringObjectPair) item).getString();
						@SuppressWarnings("unchecked")
						final Vector<Object> subResponseNames = (Vector<Object>) ((StringObjectPair) item).getObject();
						final Vector<Object> subStructure = makeOneShotStructure(subResponseNames, element, factory,
								context);
						structure.add(new StringObjectPair(label, subStructure));
					} catch (final Throwable t) {
						AuthoringTool
								.showErrorDialog("Error creating popup item.", t);
					}
				}
			}
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makePropertyAssignmentForUserDefinedQuestionStructure(
			final edu.cmu.cs.stage3.alice.core.Property property, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		final StringObjectPair[] known = {
				new StringObjectPair("element", property.getOwner()),
				new StringObjectPair("propertyName", property.getName()) };
		final String[] desired = { "value" };
		final ElementPrototype ep = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment.class, known, desired);
		final Vector<Object> setValueStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
				.makePrototypeStructure(ep, factory, context);
		if (setValueStructure != null && !setValueStructure.isEmpty()) {
			structure.add(new StringObjectPair("set value", setValueStructure));
		}

		return structure;
	}

	// this method is overly complex, mostly following the "Big Ball Of Mud"
	// Pattern
	// http://www.laputan.org/mud/mud.html
	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and
	 * rerfturn a Runnable
	 */
	public static Vector<Object> makePrototypeStructure(final ElementPrototype elementPrototype,
			final PopupItemFactory factory, final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();
		final String[] desiredProperties = elementPrototype.getDesiredProperties();
		if (desiredProperties == null || desiredProperties.length == 0) {
			structure.add(new StringObjectPair("no properties to set on " + elementPrototype.getElementClass().getName()
					+ "; please report this bug", factory.createItem(elementPrototype))); // this
																							// should
																							// not
																							// be
																							// reached
		} else if (desiredProperties.length > 0) {
			final String preRepr = elementPrototype.getElementClass().getName() + "." + desiredProperties[0];
			String propertyRepr = AuthoringToolResources.getReprForValue(preRepr);
			if (propertyRepr.equals(preRepr)) {
				propertyRepr = desiredProperties[0];
			}
			structure.add(new StringObjectPair(propertyRepr, null));
			// structure.add( new StringObjectPair(
			// "Separator", javax.swing.JSeparator.class ) );
			Class<?> preValueClass = null;
			Vector<Object> preDefaultStructure = null;
			if (edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class
					.isAssignableFrom(elementPrototype.getElementClass())) {
				preDefaultStructure = getDefaultValueStructureForPropertyAnimation(
						elementPrototype.getKnownPropertyValues());
				preValueClass = getValueClassForPropertyAnimation(elementPrototype.getKnownPropertyValues());
			} else if (edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment.class
					.isAssignableFrom(elementPrototype.getElementClass())) {
				preDefaultStructure = getDefaultValueStructureForPropertyAnimation(
						elementPrototype.getKnownPropertyValues());
				preValueClass = getValueClassForPropertyAnimation(elementPrototype.getKnownPropertyValues());
			} else if (edu.cmu.cs.stage3.alice.core.question.list.ListBooleanQuestion.class
					.isAssignableFrom(elementPrototype.getElementClass())
					|| edu.cmu.cs.stage3.alice.core.question.list.ListNumberQuestion.class
							.isAssignableFrom(elementPrototype.getElementClass())
					|| edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion.class
							.isAssignableFrom(elementPrototype.getElementClass())
					|| edu.cmu.cs.stage3.alice.core.response.list.ListResponse.class
							.isAssignableFrom(elementPrototype.getElementClass())) {
				if (desiredProperties[0].equals("item")) {
					preValueClass = getValueClassForList(elementPrototype.getKnownPropertyValues());
					preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
				} else if (desiredProperties[0].equals("index")) { // a bit
																	// hackish.
					preValueClass = edu.cmu.cs.stage3.alice.core.Element
							.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
					preDefaultStructure = getDefaultValueStructureForCollectionIndexProperty(
							elementPrototype.getKnownPropertyValues());
				} else {
					preValueClass = edu.cmu.cs.stage3.alice.core.Element
							.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
					preDefaultStructure = getDefaultValueStructureForProperty(elementPrototype.getElementClass(),
							desiredProperties[0]);
				}
			} else if (edu.cmu.cs.stage3.alice.core.question.array.ArrayNumberQuestion.class
					.isAssignableFrom(elementPrototype.getElementClass())
					|| edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion.class
							.isAssignableFrom(elementPrototype.getElementClass())
					|| edu.cmu.cs.stage3.alice.core.response.array.ArrayResponse.class
							.isAssignableFrom(elementPrototype.getElementClass())) {
				if (desiredProperties[0].equals("item")) {
					preValueClass = getValueClassForArray(elementPrototype.getKnownPropertyValues());
					preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
				} else if (desiredProperties[0].equals("index")) {
					preValueClass = edu.cmu.cs.stage3.alice.core.Element
							.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
					preDefaultStructure = getDefaultValueStructureForCollectionIndexProperty(
							elementPrototype.getKnownPropertyValues());
				} else {
					preValueClass = edu.cmu.cs.stage3.alice.core.Element
							.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
					preDefaultStructure = getDefaultValueStructureForProperty(elementPrototype.getElementClass(),
							desiredProperties[0]);
				}
			} else if (elementPrototype instanceof CallToUserDefinedResponsePrototype) {
				final edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse actualResponse = ((CallToUserDefinedResponsePrototype) elementPrototype)
						.getActualResponse();
				final Object[] params = actualResponse.requiredFormalParameters.getArrayValue();
				for (final Object param : params) {
					if (((edu.cmu.cs.stage3.alice.core.Variable) param).name.getStringValue()
							.equals(desiredProperties[0])) {
						preValueClass = (Class<?>) ((edu.cmu.cs.stage3.alice.core.Variable) param).valueClass.getValue();
						break;
					}
				}
				preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
			} else if (elementPrototype instanceof CallToUserDefinedQuestionPrototype) {
				final edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion actualQuestion = ((CallToUserDefinedQuestionPrototype) elementPrototype)
						.getActualQuestion();
				final Object[] params = actualQuestion.requiredFormalParameters.getArrayValue();
				for (final Object param : params) {
					if (((edu.cmu.cs.stage3.alice.core.Variable) param).name.getStringValue()
							.equals(desiredProperties[0])) {
						preValueClass = (Class<?>) ((edu.cmu.cs.stage3.alice.core.Variable) param).valueClass.getValue();
						break;
					}
				}
				preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
			} else if (edu.cmu.cs.stage3.alice.core.question.IsEqualTo.class
					.isAssignableFrom(elementPrototype.getElementClass())
					|| edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo.class
							.isAssignableFrom(elementPrototype.getElementClass())) {
				preValueClass = getValueClassForComparator(elementPrototype.knownPropertyValues);
				preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
			} else {
				preValueClass = edu.cmu.cs.stage3.alice.core.Element
						.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
				preDefaultStructure = getDefaultValueStructureForProperty(elementPrototype.getElementClass(),
						desiredProperties[0]);
			}

			// hack so we can use these in an inner class

			final Class<?> valueClass = preValueClass;

			final PopupItemFactory recursiveFactory = new PopupItemFactory() {
				@Override
				public Object createItem(Object o) {
					if (!valueClass.isInstance(o)) { // Add the question here?
						if (valueClass.isAssignableFrom(javax.vecmath.Vector3d.class)
								&& o instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
							final edu.cmu.cs.stage3.alice.core.question.Position positionQuestion = new edu.cmu.cs.stage3.alice.core.question.Position();
							positionQuestion.subject.set(o);
							o = positionQuestion;
						} else if (valueClass.isAssignableFrom(javax.vecmath.Matrix4d.class)
								&& o instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
							final edu.cmu.cs.stage3.alice.core.question.PointOfView POVQuestion = new edu.cmu.cs.stage3.alice.core.question.PointOfView();
							POVQuestion.subject.set(o);
							o = POVQuestion;
						} else if (valueClass.isAssignableFrom(edu.cmu.cs.stage3.math.Quaternion.class)
								&& o instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
							final edu.cmu.cs.stage3.alice.core.question.Quaternion quaternionQuestion = new edu.cmu.cs.stage3.alice.core.question.Quaternion();
							quaternionQuestion.subject.set(o);
							o = quaternionQuestion;
						}
					}
					if (desiredProperties.length == 1) { // end of the line
						// DEBUG System.out.println( "end of the line: " +
						// desiredProperties[0] + ", " + o );
						return factory.createItem(elementPrototype
								.createCopy(new StringObjectPair(desiredProperties[0], o)));
					} else { // recurse
						// DEBUG System.out.println( "recursive: " +
						// desiredProperties[0] + ", " + o );
						return makePrototypeStructure(
								elementPrototype.createCopy(
										new StringObjectPair(desiredProperties[0], o)),
								factory, context);
					}
				}
			};
			// hack so we can use these in an inner class
			final Vector<Object> defaultStructure = processStructure(preDefaultStructure, recursiveFactory,
					NO_CURRENT_VALUE);
			// compute recent values
			final Vector<Object> recentlyUsedStructure = new Vector<>();
			if (recentlyUsedValues.containsKey(preValueClass)) {
				final java.util.List<Object> recentList = recentlyUsedValues.get(preValueClass);
				int count = 0;
				for (final java.util.Iterator<Object> iter = recentList.iterator(); iter.hasNext();) {
					if (count > Integer.parseInt(authoringToolConfig.getValue("maxRecentlyUsedValues"))) {
						break;
					}
					final Object value = iter.next();
					if (!structureContains(preDefaultStructure, value)) {
						recentlyUsedStructure.add(value);
						count++;
					}
				}
			}

			if (!defaultStructure.isEmpty()) {
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				structure.addAll(defaultStructure);
			}

			// add recent values if there are any
			if (!recentlyUsedStructure.isEmpty()) {
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				addLabelsToValueStructure(recentlyUsedStructure, elementPrototype.getElementClass(),
						desiredProperties[0]);
				structure.addAll(processStructure(recentlyUsedStructure, recursiveFactory, NO_CURRENT_VALUE));
			}

			// elements
			// create criterion; handle exceptions
			final edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
					valueClass);
			final edu.cmu.cs.stage3.util.Criterion elementIsNamed = new edu.cmu.cs.stage3.util.Criterion() { // HACK;
				// shouldn't
				// have
				// to
				// cull
				// unnamed
				// elements
				@Override
				public boolean accept(final Object o) {
					if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
						if (((edu.cmu.cs.stage3.alice.core.Element) o).name.get() != null) {
							return true;
						}
					}
					return false;
				}
			};
			final InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
			edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
					new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, elementIsNamed, inAppropriateOAPCriterion });
			final Class<?> elementClass = elementPrototype.getElementClass();
			final StringObjectPair[] knownPropertyValues = elementPrototype
					.getKnownPropertyValues();

			// Don't get self criterion stuff!
			if (edu.cmu.cs.stage3.alice.core.response.AbstractPointAtAnimation.class.isAssignableFrom(elementClass)
					&& desiredProperties[0].equals("target")
					|| edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class
							.isAssignableFrom(elementPrototype.elementClass) && desiredProperties[0].equals("value")) {
				for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
					final String propertyName = knownPropertyValue.getString();
					if (propertyName.equals("subject") || propertyName.equals("element")) {
						final Object transformableValue = knownPropertyValue.getObject();
						final edu.cmu.cs.stage3.util.Criterion notSelf = new edu.cmu.cs.stage3.util.Criterion() {
							@Override
							public boolean accept(final Object o) {
								if (o == transformableValue) {
									return false;
								}
								return true;
							}
						};
						criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
								new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, elementIsNamed,
										inAppropriateOAPCriterion, notSelf });
						break;
					}
				}
			}
			// make structure
			if (edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(valueClass)
					|| valueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Element.class)) {
				final edu.cmu.cs.stage3.alice.core.Element[] elements = context.getRoot().search(criterion);
				if (elements.length > 0) {
					if (elements.length < 10) {
						if (structure.size() > 0) {
							structure.add(new StringObjectPair("Separator",
									javax.swing.JSeparator.class));
						}
						structure.addAll(makeFlatElementStructure(context.getRoot(), criterion, recursiveFactory,
								context, NO_CURRENT_VALUE));
					} else {
						if (structure.size() > 0) {
							structure.add(new StringObjectPair("Separator",
									javax.swing.JSeparator.class));
						}
						structure.addAll(makeElementStructure(context.getRoot(), criterion, recursiveFactory, context,
								NO_CURRENT_VALUE));
					}
				}
			}
			if (!Object.class.isAssignableFrom(valueClass)) {
				// Okay, this needs to build a structure to find models, get
				// their position and pass them to the vector3d property
				if (javax.vecmath.Vector3d.class.isAssignableFrom(valueClass)
						|| valueClass.isAssignableFrom(javax.vecmath.Vector3d.class)) {
					final edu.cmu.cs.stage3.util.Criterion modelCriterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
							new edu.cmu.cs.stage3.util.Criterion[] {
									new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
											edu.cmu.cs.stage3.alice.core.Model.class),
									elementIsNamed, inAppropriateOAPCriterion });
					final edu.cmu.cs.stage3.alice.core.Element[] elements = context.getRoot().search(modelCriterion);
					if (elements.length > 0) {
						if (elements.length < 10) {
							if (structure.size() > 0) {
								structure.add(new StringObjectPair("Separator",
										javax.swing.JSeparator.class));
							}
							structure.addAll(makeFlatElementStructure(context.getRoot(), modelCriterion,
									recursiveFactory, context, NO_CURRENT_VALUE));
						} else {
							if (structure.size() > 0) {
								structure.add(new StringObjectPair("Separator",
										javax.swing.JSeparator.class));
							}
							structure.addAll(makeElementStructure(context.getRoot(), modelCriterion, recursiveFactory,
									context, NO_CURRENT_VALUE));
						}
					}
				}
				if (valueClass.isAssignableFrom(javax.vecmath.Matrix4d.class)) {
					final edu.cmu.cs.stage3.util.Criterion modelCriterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
							new edu.cmu.cs.stage3.util.Criterion[] {
									new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
											edu.cmu.cs.stage3.alice.core.Model.class),
									elementIsNamed, inAppropriateOAPCriterion });
					final edu.cmu.cs.stage3.alice.core.Element[] elements = context.getRoot().search(modelCriterion);
					if (elements.length > 0) {
						if (elements.length < 10) {
							if (structure.size() > 0) {
								structure.add(new StringObjectPair("Separator",
										javax.swing.JSeparator.class));
							}
							structure.addAll(makeFlatElementStructure(context.getRoot(), modelCriterion,
									recursiveFactory, context, NO_CURRENT_VALUE));
						} else {
							if (structure.size() > 0) {
								structure.add(new StringObjectPair("Separator",
										javax.swing.JSeparator.class));
							}
							structure.addAll(makeElementStructure(context.getRoot(), modelCriterion, recursiveFactory,
									context, NO_CURRENT_VALUE));
						}
					}
				}
				if (valueClass.isAssignableFrom(edu.cmu.cs.stage3.math.Quaternion.class)) {
					final edu.cmu.cs.stage3.util.Criterion modelCriterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
							new edu.cmu.cs.stage3.util.Criterion[] {
									new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
											edu.cmu.cs.stage3.alice.core.Model.class),
									elementIsNamed, inAppropriateOAPCriterion });
					final edu.cmu.cs.stage3.alice.core.Element[] elements = context.getRoot().search(modelCriterion);
					if (elements.length > 0) {
						if (elements.length < 10) {
							if (structure.size() > 0) {
								structure.add(new StringObjectPair("Separator",
										javax.swing.JSeparator.class));
							}
							structure.addAll(makeFlatElementStructure(context.getRoot(), modelCriterion,
									recursiveFactory, context, NO_CURRENT_VALUE));
						} else {
							if (structure.size() > 0) {
								structure.add(new StringObjectPair("Separator",
										javax.swing.JSeparator.class));
							}
							structure.addAll(makeElementStructure(context.getRoot(), modelCriterion, recursiveFactory,
									context, NO_CURRENT_VALUE));
						}
					}
				}
			}
			// else{
			// System.out.println("bleh!");
			// }

			// import or record sound if necessary
			final PopupItemFactory metaFactory = new PopupItemFactory() {
				@Override
				public Object createItem(final Object o) {
					return new Runnable() {
						@Override
						public void run() {
							((Runnable) factory.createItem(elementPrototype
									.createCopy(new StringObjectPair(desiredProperties[0], o))))
											.run();
						}
					};
				}
			};
			if (valueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Sound.class) && valueClass != Object.class) {
				final java.io.File soundDir = new java.io.File(
						edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceHomeDirectory(), "sounds")
								.getAbsoluteFile();
				if (soundDir.exists() && soundDir.isDirectory()) {
					final java.util.ArrayList<File> sounds = new java.util.ArrayList<File>();
					final java.io.File[] fileList = soundDir.listFiles();
					for (final File element : fileList) {
						if (element.isFile() && element.canRead()) {
							sounds.add(element);
						}
					}

					if (sounds.size() > 0) {
						if (structure.size() > 0) {
							structure.add(new StringObjectPair("Separator",
									javax.swing.JSeparator.class));
						}
						for (final java.util.Iterator<File> iter = sounds.iterator(); iter.hasNext();) {
							final java.io.File soundFile = iter.next();
							String name = soundFile.getName();
							name = name.substring(0, name.lastIndexOf('.'));
							final Runnable importSoundRunnable = new Runnable() {
								@Override
								public void run() {
									final edu.cmu.cs.stage3.alice.core.Sound sound = (edu.cmu.cs.stage3.alice.core.Sound) AuthoringTool
											.getHack().importElement(soundFile, context.getSandbox());
									if (sound != null) {
										((Runnable) factory.createItem(
												elementPrototype.createCopy(new StringObjectPair(
														desiredProperties[0], sound)))).run();
									}
									// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker
									// worker =
									// AuthoringTool.getHack().importElement(
									// soundFile, context.getSandbox() );
									// ((Runnable)factory.createItem(
									// elementPrototype.createCopy( new
									// StringObjectPair(
									// desiredProperties[0], worker.get() ) )
									// )).run();
								}
							};
							structure.add(new StringObjectPair(name, importSoundRunnable));
						}
					}
				}

				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				final Runnable importRunnable = new Runnable() {
					@Override
					public void run() {
						// PropertyPopupPostWorker postWorker = new
						// PropertyPopupPostWorker( metaFactory );
						final PropertyPopupPostImportRunnable propertyPopupPostImportRunnable = new PropertyPopupPostImportRunnable(
								metaFactory);
						AuthoringTool.getHack()
								.setImportFileFilter("Sound Files");
						AuthoringTool.getHack().importElement(null,
								context.getSandbox(), propertyPopupPostImportRunnable);
					}
				};
				final Runnable recordRunnable = new Runnable() {
					@Override
					public void run() {
						final edu.cmu.cs.stage3.alice.core.Sound sound = AuthoringTool
								.getHack().promptUserForRecordedSound(context.getSandbox());
						if (sound != null) {
							((Runnable) factory.createItem(elementPrototype.createCopy(
									new StringObjectPair(desiredProperties[0], sound)))).run();
						}
					}
				};
				structure.add(new StringObjectPair("import sound file...", importRunnable));
				structure.add(new StringObjectPair("record new sound...", recordRunnable));
			}

			// expressions
			final Vector<Object> expressionStructure = makeFlatExpressionStructure(valueClass, recursiveFactory,
					context, NO_CURRENT_VALUE);
			if (expressionStructure != null && expressionStructure.size() > 0) {
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				structure.add(new StringObjectPair("expressions", expressionStructure));
			}

			// Null
			boolean nullValid;
			if (elementPrototype instanceof edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype) {
				nullValid = true;
			} else if (elementPrototype instanceof edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype) {
				nullValid = true;
			} else {
				nullValid = AuthoringToolResources
						.shouldGUIIncludeNone(elementPrototype.getElementClass(), desiredProperties[0]);
				// nullValid =
				// edu.cmu.cs.stage3.alice.core.Element.isNullValidForPropertyNamed(
				// elementPrototype.getElementClass(), desiredProperties[0] );
			}
			if (nullValid) {
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				final String nullRepr = AuthoringToolResources
						.getReprForValue(null, elementClass, desiredProperties[0], "menuContext");
				if (desiredProperties.length == 1) { // end of the line
					structure.add(new StringObjectPair(nullRepr, factory.createItem(elementPrototype
							.createCopy(new StringObjectPair(desiredProperties[0], null)))));
				} else { // recurse
					structure
							.add(new StringObjectPair(nullRepr,
									makePrototypeStructure(elementPrototype.createCopy(
											new StringObjectPair(desiredProperties[0], null)),
											factory, context)));
				}
			}

			// Other...
			final PopupItemFactory otherFactory = new PopupItemFactory() {
				@Override
				public Object createItem(final Object o) {
					return new Runnable() {
						@Override
						public void run() {
							((Runnable) factory.createItem(elementPrototype
									.createCopy(new StringObjectPair(desiredProperties[0], o))))
											.run();
						}
					};
				}
			};
			if (desiredProperties.length == 1 && edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
					.isOtherDialogSupportedForClass(valueClass)) {
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				final Runnable runnable = new Runnable() {
					@Override
					public void run() {
						// TODO: context doesn't really want to be passed
						// here... it wants to be the response that will be
						// made...
						edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.showOtherDialog(valueClass, null,
								otherFactory, context);
					}
				};
				structure.add(new StringObjectPair("other...", runnable));
			}

			// allow user to create new list
			if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(valueClass) && desiredProperties.length == 1) {
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				final Runnable createNewListRunnable = new Runnable() {
					@Override
					public void run() {
						final AuthoringTool authoringTool = AuthoringTool
								.getHack();
						final edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty variables = context
								.getSandbox().variables;
						final edu.cmu.cs.stage3.alice.core.Variable variable = authoringTool
								.showNewVariableDialog("Create new list", context.getRoot(), true, true);
						if (variable != null) {
							if (variables != null) {
								authoringTool.getUndoRedoStack().startCompound();
								try {
									variables.getOwner().addChild(variable);
									variables.add(variable);
								} finally {
									authoringTool.getUndoRedoStack().stopCompound();
								}
							}
							((Runnable) factory.createItem(elementPrototype.createCopy(
									new StringObjectPair(desiredProperties[0], variable))))
											.run();
						}
					}
				};
				structure.add(new StringObjectPair("create new list...", createNewListRunnable));
			}
		}

		return structure;
	}

	/**
	 * @deprecated use makePropertyStructure( final
	 *             edu.cmu.cs.stage3.alice.core.Property property, final
	 *             PopupItemFactory factory, boolean includeDefaults, boolean
	 *             includeExpressions, boolean includeOther,
	 *             edu.cmu.cs.stage3.alice.core.Element root )
	 */
	@Deprecated
	public static Vector<Object> makePropertyStructure(final edu.cmu.cs.stage3.alice.core.Property property,
			final PopupItemFactory factory, final boolean includeDefaults, final boolean includeExpressions,
			final boolean includeOther) {
		return makePropertyStructure(property, factory, includeDefaults, includeExpressions, includeOther, null);
	}

	/**
	 * the PopupItemFactory should accept a target value and return a Runnable
	 *
	 * root is used to create an Element hierarchy if needed. if root is null,
	 * property.getElement().getRoot() is used.
	 */
	public static Vector<Object> makePropertyStructure(final edu.cmu.cs.stage3.alice.core.Property property,
			final PopupItemFactory factory, final boolean includeDefaults, final boolean includeExpressions,
			final boolean includeOther, edu.cmu.cs.stage3.alice.core.Element root) {
		if (root == null) {
			root = property.getOwner().getRoot();
		}
		Vector<Object> structure = new Vector<>();
		final Class<?> targetValueClass = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities
				.getDesiredValueClass(property);
		if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(targetValueClass)) { // lists
																							// are
																							// special
			final edu.cmu.cs.stage3.alice.core.Element parent = property.getOwner().getParent();
			final edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = parent
					.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
			if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion
					&& references.length > 0) {
				final Class<?> itemValueClass = references[0].getProperty().getValueClass();
				final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
					@Override
					public boolean accept(final Object o) {
						if (o instanceof edu.cmu.cs.stage3.alice.core.Variable) {
							final edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List) ((edu.cmu.cs.stage3.alice.core.Variable) o)
									.getValue();
							if (list != null) {
								if (itemValueClass.isAssignableFrom(list.valueClass.getClassValue())) {
									return true;
								}
							}
						}
						return false;
					}
				};
				structure = makeFlatExpressionStructure(targetValueClass, criterion, factory, property.getOwner(),
						property.get());

				if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
					if (structure.size() > 0) {
						structure.add(
								new StringObjectPair("Separator", javax.swing.JSeparator.class));
					}
					if (property.get() == null) {
						structure.add(new StringObjectPair(
								AuthoringToolResources.getReprForValue(null,
										property, "menuContext"),
								new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
					} else {
						structure.add(new StringObjectPair(
								AuthoringToolResources.getReprForValue(null,
										property, "menuContext"),
								factory.createItem(null)));
					}
				}
			} else { // not an anonymous list question; accept all lists
				structure = makeFlatExpressionStructure(targetValueClass, factory, property.getOwner(), property.get());

				if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
					if (structure.size() > 0) {
						structure.add(
								new StringObjectPair("Separator", javax.swing.JSeparator.class));
					}
					if (property.get() == null) {
						structure.add(new StringObjectPair(
								AuthoringToolResources.getReprForValue(null,
										property, "menuContext"),
								new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
					} else {
						structure.add(new StringObjectPair(
								AuthoringToolResources.getReprForValue(null,
										property, "menuContext"),
								factory.createItem(null)));
					}
				}

				// allow user to create new list
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				final Runnable createNewListRunnable = new Runnable() {
					@Override
					public void run() {
						final AuthoringTool authoringTool = AuthoringTool
								.getHack();
						final edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty variables = property.getOwner()
								.getSandbox().variables;
						final edu.cmu.cs.stage3.alice.core.Variable variable = authoringTool
								.showNewVariableDialog("Create new list", property.getOwner().getRoot(), true, true);
						if (variable != null) {
							if (variables != null) {
								variables.getOwner().addChild(variable);
								variables.add(variable);
							}
							((Runnable) factory.createItem(variable)).run();
						}
					}
				};
				structure.add(new StringObjectPair("create new list...", createNewListRunnable));
			}
		} else if (edu.cmu.cs.stage3.alice.core.Array.class.isAssignableFrom(targetValueClass)) { // arrays
																									// are
																									// special
																									// too
			final edu.cmu.cs.stage3.alice.core.Element parent = property.getOwner().getParent();
			final edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = parent
					.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
			if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion
					&& references.length > 0) {
				final Class<?> itemValueClass = references[0].getProperty().getValueClass();
				final edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
					@Override
					public boolean accept(final Object o) {
						if (o instanceof edu.cmu.cs.stage3.alice.core.Variable) {
							final edu.cmu.cs.stage3.alice.core.Array array = (edu.cmu.cs.stage3.alice.core.Array) ((edu.cmu.cs.stage3.alice.core.Variable) o)
									.getValue();
							if (array != null) {
								if (itemValueClass.isAssignableFrom(array.valueClass.getClassValue())) {
									return true;
								}
							}
						}
						return false;
					}
				};
				structure = makeFlatExpressionStructure(targetValueClass, criterion, factory, property.getOwner(),
						property.get());

				if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
					if (structure.size() > 0) {
						structure.add(
								new StringObjectPair("Separator", javax.swing.JSeparator.class));
					}
					if (property.get() == null) {
						structure.add(new StringObjectPair(
								AuthoringToolResources.getReprForValue(null,
										property, "menuContext"),
								new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
					} else {
						structure.add(new StringObjectPair(
								AuthoringToolResources.getReprForValue(null,
										property, "menuContext"),
								factory.createItem(null)));
					}
				}
			} else { // not an anonymous array question; accept all arrays
				structure = makeFlatExpressionStructure(targetValueClass, factory, property.getOwner(), property.get());

				if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
					if (structure.size() > 0) {
						structure.add(
								new StringObjectPair("Separator", javax.swing.JSeparator.class));
					}
					if (property.get() == null) {
						structure.add(new StringObjectPair(
								AuthoringToolResources.getReprForValue(null,
										property, "menuContext"),
								new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
					} else {
						structure.add(new StringObjectPair(
								AuthoringToolResources.getReprForValue(null,
										property, "menuContext"),
								factory.createItem(null)));
					}
				}
			}
		} else {
			if (includeDefaults) {
				if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(targetValueClass)) {
					final edu.cmu.cs.stage3.alice.core.Element context = root;
					final edu.cmu.cs.stage3.alice.core.Property referenceProperty = property;
					final PopupItemFactory userDefinedResponsePopupFactory = new PopupItemFactory() {
						@Override
						public Object createItem(final Object o) {
							final edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse userDefinedResponse = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) o;
							final CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new CallToUserDefinedResponsePrototype(
									userDefinedResponse);
							callToUserDefinedResponsePrototype.calculateDesiredProperties();
							final PopupItemFactory prototypePopupFactory = new PopupItemFactory() {
								@Override
								public Object createItem(final Object prototype) {
									final CallToUserDefinedResponsePrototype completedPrototype = (CallToUserDefinedResponsePrototype) prototype;
									return factory.createItem(completedPrototype.createNewElement());
								}
							};
							if (userDefinedResponse.requiredFormalParameters.size() > 0) {
								if (referenceProperty
										.getOwner() instanceof edu.cmu.cs.stage3.alice.core.behavior.MouseButtonClickBehavior
										|| referenceProperty
												.getOwner() instanceof edu.cmu.cs.stage3.alice.core.behavior.MouseButtonIsPressedBehavior) {
									return makePrototypeStructure(callToUserDefinedResponsePrototype,
											prototypePopupFactory, referenceProperty.getOwner());
								} else {
									return makePrototypeStructure(callToUserDefinedResponsePrototype,
											prototypePopupFactory, context);
								}
							} else {
								return prototypePopupFactory.createItem(callToUserDefinedResponsePrototype);
							}
						}
					};
					final edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion criterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
							edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class);
					structure.addAll(makeElementStructure(root, criterion, userDefinedResponsePopupFactory, root,
							property.get()));
				} else {
					// default and recently used values

					final Vector<Object> defaultStructure = getDefaultValueStructureForProperty(property);
					final Vector<Object> recentlyUsedStructure = new Vector<>();
					if (recentlyUsedValues.containsKey(targetValueClass)) {
						final java.util.List<Object> recentList = recentlyUsedValues.get(targetValueClass);
						int count = 0;
						for (final java.util.Iterator<Object> iter = recentList.iterator(); iter.hasNext();) {
							if (count > Integer.parseInt(authoringToolConfig.getValue("maxRecentlyUsedValues"))) {
								break;
							}
							final Object value = iter.next();
							if (!structureContains(defaultStructure, value)) {
								recentlyUsedStructure.add(value);
								count++;
							}
						}
					}

					// make sure current value is represented
					final Object currentValue = property.get();
					final Vector<Object> unlabeledDefaultValueStructure = getUnlabeledDefaultValueStructureForProperty(
							property.getOwner().getClass(), property.getName(), property); // very
					// hackish
					if (!(unlabeledDefaultValueStructure.contains(currentValue)
							|| recentlyUsedStructure.contains(currentValue))) {
						if (!(currentValue instanceof edu.cmu.cs.stage3.alice.core.Expression)) {
							recentlyUsedStructure.add(0, currentValue);
							if (recentlyUsedStructure.size() > Integer
									.parseInt(authoringToolConfig.getValue("maxRecentlyUsedValues"))) {
								recentlyUsedStructure.remove(recentlyUsedStructure.size() - 1);
							}
						}
					}

					if (structure.size() > 0) {
						structure.add(
								new StringObjectPair("Separator", javax.swing.JSeparator.class));
					}
					structure.addAll(processStructure(defaultStructure, factory, property.get()));
					if (!recentlyUsedStructure.isEmpty() && !property.getName().equalsIgnoreCase("keyCode")) {
						if (structure.size() > 0) {
							structure.add(new StringObjectPair("Separator",
									javax.swing.JSeparator.class));
						}
						addLabelsToValueStructure(recentlyUsedStructure, property.getOwner().getClass(),
								property.getName());
						structure.addAll(processStructure(recentlyUsedStructure, factory, property.get()));
					}

					// Elements
					if (edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(targetValueClass)
							|| targetValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Element.class)) {
						edu.cmu.cs.stage3.util.Criterion criterion;
						if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Behavior
								&& edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(targetValueClass)) {
							criterion = new edu.cmu.cs.stage3.util.Criterion() { // object
																					// must
																					// be
																					// top-level
																					// response
								@Override
								public boolean accept(final Object o) {
									if (o instanceof edu.cmu.cs.stage3.alice.core.Response) {
										if (!(((edu.cmu.cs.stage3.alice.core.Response) o)
												.getParent() instanceof edu.cmu.cs.stage3.alice.core.Response)) {
											if (!(o instanceof edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse)) {
												return true;
											}
										}
									}
									return false;
								}
							};
						} else if (property
								.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation
								&& property.getName().equals("element")) {
							final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation propertyAnimation = (edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) property
									.getOwner();
							final String propertyName = propertyAnimation.propertyName.getStringValue();
							final edu.cmu.cs.stage3.util.Criterion hasProperty = new edu.cmu.cs.stage3.util.Criterion() {
								@Override
								public boolean accept(final Object o) {
									if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
										if (((edu.cmu.cs.stage3.alice.core.Element) o)
												.getPropertyNamed(propertyName) != null) {
											return true;
										}
									}
									return false;
								}
							};
							criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
									new edu.cmu.cs.stage3.util.Criterion[] { hasProperty, isNamedElement });
						} else if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PointAtAnimation
								&& property.getName().equals("target")) {
							final Object transformableValue = property.getOwner().getPropertyNamed("subject").get();
							final edu.cmu.cs.stage3.util.Criterion notSelf = new edu.cmu.cs.stage3.util.Criterion() {
								@Override
								public boolean accept(final Object o) {
									if (o == transformableValue) {
										return false;
									}
									return true;
								}
							};
							final edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
									targetValueClass);
							final InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
							criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
									new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, notSelf, isNamedElement,
											inAppropriateOAPCriterion });
						} else if (property instanceof edu.cmu.cs.stage3.alice.core.property.VehicleProperty) {
							final Object transformableValue = property.getOwner();
							final edu.cmu.cs.stage3.util.Criterion notSelf = new edu.cmu.cs.stage3.util.Criterion() {
								@Override
								public boolean accept(final Object o) {
									if (o == transformableValue) {
										return false;
									}
									return true;
								}
							};
							final edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
									targetValueClass);
							final InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
							criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
									new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, notSelf, isNamedElement,
											inAppropriateOAPCriterion });
						} else if (property
								.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PointAtConstraint
								&& property.getName().equals("target")) {
							final Object transformableValue = property.getOwner().getPropertyNamed("subject").get();
							final edu.cmu.cs.stage3.util.Criterion notSelf = new edu.cmu.cs.stage3.util.Criterion() {
								@Override
								public boolean accept(final Object o) {
									if (o == transformableValue) {
										return false;
									}
									return true;
								}
							};
							final edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
									targetValueClass);
							final InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
							criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
									new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, notSelf, isNamedElement,
											inAppropriateOAPCriterion });
						} else {
							final edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
									targetValueClass);
							if (edu.cmu.cs.stage3.alice.core.Expression.class.isAssignableFrom(targetValueClass)) {
								criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
										new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, isNamedElement });
							} else {
								final edu.cmu.cs.stage3.util.criterion.NotCriterion notExpression = new edu.cmu.cs.stage3.util.criterion.NotCriterion(
										new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
												edu.cmu.cs.stage3.alice.core.Expression.class));
								final InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
								criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(
										new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, notExpression,
												isNamedElement, inAppropriateOAPCriterion });
							}
						}
						final edu.cmu.cs.stage3.alice.core.Element[] elements = root.search(criterion);

						if (structure.size() > 0 && elements.length > 0) {
							structure.add(new StringObjectPair("Separator",
									javax.swing.JSeparator.class));
						}
						if (elements.length < 10) {
							structure.addAll(makeFlatElementStructure(root, criterion, factory, root, property.get()));
						} else {
							structure.addAll(makeElementStructure(root, criterion, factory, root, property.get()));
						}

						// import a sound if necessary
						if (targetValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Sound.class)) {
							final edu.cmu.cs.stage3.alice.core.World world = root.getWorld();
							if (structure.size() > 0) {
								structure.add(new StringObjectPair("Separator",
										javax.swing.JSeparator.class));
							}
							final Runnable runnable = new Runnable() {
								@Override
								public void run() {
									final PropertyPopupPostImportRunnable propertyPopupPostImportRunnable = new PropertyPopupPostImportRunnable(
											factory);
									AuthoringTool.getHack()
											.setImportFileFilter("Sound Files");
									AuthoringTool.getHack().importElement(null,
											world, propertyPopupPostImportRunnable); // should
																						// probably
																						// somehow
																						// hook
																						// the
																						// sound
																						// up
																						// to
																						// the
																						// object
																						// playing
																						// it
								}
							};
							structure
									.add(new StringObjectPair("import sound file...", runnable));
						}
					}
				}
			}
			// TODO check out to see if shouldGUIOmitNone needs to be handled
			if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				if (property.get() == null) {
					structure
							.add(new StringObjectPair(
									AuthoringToolResources.getReprForValue(null,
											property, "menuContext"),
									new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
				} else {
					structure.add(new StringObjectPair(
							AuthoringToolResources.getReprForValue(null, property,
									"menuContext"),
							factory.createItem(null)));
				}
			}
			// TODO check out makeFlatExpressionStructure
			if (includeExpressions) {

				final Vector<Object> expressionStructure = makeFlatExpressionStructure(targetValueClass, factory,
						property.getOwner(), property.get());
				if (expressionStructure != null && expressionStructure.size() > 0) {
					if (structure.size() > 0) {
						structure.add(
								new StringObjectPair("Separator", javax.swing.JSeparator.class));
					}
					structure.add(new StringObjectPair("expressions", expressionStructure));
				} else if (structure.size() == 0) {
					final javax.swing.JLabel label = new javax.swing.JLabel("no expressions for this type");
					label.setForeground(java.awt.Color.gray);
					label.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 2));
					structure.add(new StringObjectPair("", label));
				}
			}

			if (Boolean.class.isAssignableFrom(targetValueClass) && includeExpressions) {
				final Vector<Object> booleanLogicStructure = makeBooleanLogicStructure(property.get(), factory,
						property.getOwner());
				if (booleanLogicStructure != null) {
					if (structure.size() > 0) {
						structure.add(
								new StringObjectPair("Separator", javax.swing.JSeparator.class));
					}
					structure.add(new StringObjectPair("logic", booleanLogicStructure));
				}
			}

			if (Number.class.isAssignableFrom(targetValueClass) && includeExpressions) {
				final Vector<Object> mathStructure = makeCommonMathQuestionStructure(property.get(), factory,
						property.getOwner());
				if (mathStructure != null) {
					if (structure.size() > 0) {
						structure.add(
								new StringObjectPair("Separator", javax.swing.JSeparator.class));
					}
					structure.add(new StringObjectPair("math", mathStructure));
				}
			}

			if (!AuthoringToolResources.shouldGUIOmitScriptDefined(property)) {
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				final Runnable scriptDefinedRunnable = new Runnable() {
					@Override
					public void run() {
						edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.showScriptDefinedPropertyDialog(property,
								factory);
					}
				};
				structure.add(new StringObjectPair("script-defined...", scriptDefinedRunnable));
			}
			// TODO: make this mroe intelligent
			// Other...

			if (includeOther && edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory
					.isOtherDialogSupportedForClass(targetValueClass)) {
				if (structure.size() > 0) {
					structure.add(
							new StringObjectPair("Separator", javax.swing.JSeparator.class));
				}
				final Class<?> finalOtherValueClass = targetValueClass;
				final Runnable runnable = new Runnable() {
					@Override
					public void run() {
						edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.showOtherPropertyDialog(property, factory,
								null, finalOtherValueClass);
					}
				};
				structure.add(new StringObjectPair("other...", runnable));
			}
		}

		if (structure.size() == 0) {
			final javax.swing.JLabel label = new javax.swing.JLabel("nothing to choose");
			label.setForeground(java.awt.Color.gray);
			label.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
			structure.add(new StringObjectPair("", label));
		}

		return structure;
	}

	public static Vector<Object> makeListQuestionStructure(final edu.cmu.cs.stage3.alice.core.Variable listVariable,
			final PopupItemFactory factory, final Class<?> returnValueClass,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		final edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List) listVariable.getValue();
		final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory prototypeToItemFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
			@Override
			public Object createItem(final Object object) {
				final edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object;
				return factory.createItem(ep.createNewElement());
			}
		};

		if (returnValueClass.isAssignableFrom(list.valueClass.getClassValue())) {
			final Runnable firstItemRunnable = new Runnable() {
				@Override
				public void run() {
					final edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning itemAtBeginning = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning();
					itemAtBeginning.list.set(listVariable);
					((Runnable) factory.createItem(itemAtBeginning)).run();
				}
			};
			final Runnable lastItemRunnable = new Runnable() {
				@Override
				public void run() {
					final edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd itemAtEnd = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd();
					itemAtEnd.list.set(listVariable);
					((Runnable) factory.createItem(itemAtEnd)).run();
				}
			};
			final Runnable randomItemRunnable = new Runnable() {
				@Override
				public void run() {
					final edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex itemAtRandomIndex = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex();
					itemAtRandomIndex.list.set(listVariable);
					((Runnable) factory.createItem(itemAtRandomIndex)).run();
				}
			};

			final StringObjectPair[] known = new StringObjectPair[] {
					new StringObjectPair("list", listVariable) };
			final String[] desired = new String[] { "index" };
			final ElementPrototype ithPrototype = new ElementPrototype(
					edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex.class, known, desired);
			final Vector<Object> ithStructure = makePrototypeStructure(ithPrototype, prototypeToItemFactory, context);

			structure.add(new StringObjectPair("first item from list", firstItemRunnable));
			structure.add(new StringObjectPair("last item from list", lastItemRunnable));
			structure.add(new StringObjectPair("random item from list", randomItemRunnable));
			structure.add(new StringObjectPair("ith item from list", ithStructure));
		}
		if (edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(list.valueClass.getClassValue())) {
			// public static Vector<Object> makePropertyValueStructure( final
			// edu.cmu.cs.stage3.alice.core.Element element, Class<?> valueClass,
			// final PopupItemFactory factory,
			// edu.cmu.cs.stage3.alice.core.Element context ) {
			// Vector<Object> structure = new Vector<>();
			//
			// Class elementClass = null;
			// if( element instanceof edu.cmu.cs.stage3.alice.core.Expression )
			// {
			// elementClass =
			// ((edu.cmu.cs.stage3.alice.core.Expression)element).getValueClass();
			// } else {
			// elementClass = element.getClass();
			// }
			//
			// StringObjectPair[] known = new
			// StringObjectPair[] { new
			// StringObjectPair( "element", element ) };
			// String[] desired = new String[] { "propertyName" };
			// ElementPrototype prototype = new ElementPrototype(
			// edu.cmu.cs.stage3.alice.core.question.PropertyValue.class, known,
			// desired );
			//
			// String[] propertyNames = getPropertyNames( elementClass,
			// valueClass );
			//
			// String prefix =
			// AuthoringToolResources.getReprForValue(
			// element, false ) + ".";
			// for( int i = 0; i < propertyNames.length; i++ ) {
			// if( (! propertyNames[i].equals( "visualization" )) && (!
			// propertyNames[i].equals( "isFirstClass" )) ) { // HACK
			// suppression
			// String propertyName =
			// AuthoringToolResources.getReprForValue(
			// propertyNames[i], false );
			// structure.add( new StringObjectPair( prefix + propertyName,
			// factory.createItem( prototype.createCopy( new
			// StringObjectPair( "propertyName",
			// propertyName ) ) ) ) );
			// }
			// }
			//
			// return structure;
		}
		if (returnValueClass.isAssignableFrom(Boolean.class)) {
			final Runnable isEmptyRunnable = new Runnable() {
				@Override
				public void run() {
					final edu.cmu.cs.stage3.alice.core.question.list.IsEmpty isEmpty = new edu.cmu.cs.stage3.alice.core.question.list.IsEmpty();
					isEmpty.list.set(listVariable);
					((Runnable) factory.createItem(isEmpty)).run();
				}
			};

			final StringObjectPair[] known = new StringObjectPair[] {
					new StringObjectPair("list", listVariable) };
			final String[] desired = new String[] { "item" };
			final ElementPrototype containsPrototype = new ElementPrototype(
					edu.cmu.cs.stage3.alice.core.question.list.Contains.class, known, desired);
			final Vector<Object> containsStructure = makePrototypeStructure(containsPrototype, prototypeToItemFactory,
					context);

			if (structure.size() > 0) {
				structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
			}
			structure.add(new StringObjectPair("is list empty", isEmptyRunnable));
			structure.add(new StringObjectPair("list contains", containsStructure));
		}

		if (returnValueClass.isAssignableFrom(Number.class)) {
			final Runnable sizeRunnable = new Runnable() {
				@Override
				public void run() {
					final edu.cmu.cs.stage3.alice.core.question.list.Size size = new edu.cmu.cs.stage3.alice.core.question.list.Size();
					size.list.set(listVariable);
					((Runnable) factory.createItem(size)).run();
				}
			};

			final StringObjectPair[] known = new StringObjectPair[] {
					new StringObjectPair("list", listVariable) };
			final String[] desired = new String[] { "item" };

			final ElementPrototype firstIndexOfItemPrototype = new ElementPrototype(
					edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem.class, known, desired);
			final Vector<Object> firstIndexOfItemStructure = makePrototypeStructure(firstIndexOfItemPrototype,
					prototypeToItemFactory, context);

			final ElementPrototype lastIndexOfItemPrototype = new ElementPrototype(
					edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem.class, known, desired);
			final Vector<Object> lastIndexOfItemStructure = makePrototypeStructure(lastIndexOfItemPrototype,
					prototypeToItemFactory, context);

			if (structure.size() > 0) {
				structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
			}
			structure.add(new StringObjectPair("size of list", sizeRunnable));
			structure.add(new StringObjectPair("first index of", firstIndexOfItemStructure));
			structure.add(new StringObjectPair("last index of", lastIndexOfItemStructure));
		}

		return structure;
	}

	public static Vector<Object> makeArrayQuestionStructure(final edu.cmu.cs.stage3.alice.core.Variable arrayVariable,
			final PopupItemFactory factory, final Class<?> returnValueClass,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		final edu.cmu.cs.stage3.alice.core.Array array = (edu.cmu.cs.stage3.alice.core.Array) arrayVariable.getValue();

		final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory prototypeToItemFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
			@Override
			public Object createItem(final Object object) {
				final edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object;
				return factory.createItem(ep.createNewElement());
			}
		};

		if (returnValueClass.isAssignableFrom(array.valueClass.getClassValue())) {
			final StringObjectPair[] known = new StringObjectPair[] {
					new StringObjectPair("array", arrayVariable) };
			final String[] desired = new String[] { "index" };
			final ElementPrototype ithPrototype = new ElementPrototype(
					edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex.class, known, desired);
			final Vector<Object> ithStructure = makePrototypeStructure(ithPrototype, prototypeToItemFactory, context);

			structure.add(new StringObjectPair("ith item from array", ithStructure));
		}

		if (returnValueClass.isAssignableFrom(Number.class)) {
			final Runnable sizeRunnable = new Runnable() {
				@Override
				public void run() {
					final edu.cmu.cs.stage3.alice.core.question.array.Size size = new edu.cmu.cs.stage3.alice.core.question.array.Size();
					size.array.set(arrayVariable);
					((Runnable) factory.createItem(size)).run();
				}
			};

			/* Unused ??
			final StringObjectPair[] known = new StringObjectPair[] {
					new StringObjectPair("array", arrayVariable) };
			final String[] desired = new String[] { "item" };*/

			if (structure.size() > 0) {
				structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
			}
			structure.add(new StringObjectPair("size of array", sizeRunnable));
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makeCommonMathQuestionStructure(final Object firstOperand,
			final PopupItemFactory factory, final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		if (firstOperand instanceof Number) {
			// accept
		} else if (firstOperand instanceof edu.cmu.cs.stage3.alice.core.Expression && Number.class
				.isAssignableFrom(((edu.cmu.cs.stage3.alice.core.Expression) firstOperand).getValueClass())) {
			// accept
		} else if (firstOperand == null) {
			// accept
		} else {
			throw new IllegalArgumentException("firstOperand must represent a Number");
		}

		final String firstOperandRepr = AuthoringToolResources
				.getReprForValue(firstOperand, false);

		// edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory
		// mathFactory = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
		// public Object createItem( final Object object ) {
		// edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep =
		// (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)object;
		// return factory.createItem( ep.createNewElement() );
		// }
		// };

		final StringObjectPair[] known = new StringObjectPair[] {
				new StringObjectPair("a", firstOperand) };
		final String[] desired = new String[] { "b" };

		final ElementPrototype addPrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.NumberAddition.class, known, desired);
		final Vector<Object> addStructure = makePrototypeStructure(addPrototype, factory, context);
		final ElementPrototype subtractPrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.NumberSubtraction.class, known, desired);
		final Vector<Object> subtractStructure = makePrototypeStructure(subtractPrototype, factory, context);
		final ElementPrototype multiplyPrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.NumberMultiplication.class, known, desired);
		final Vector<Object> multiplyStructure = makePrototypeStructure(multiplyPrototype, factory, context);
		final ElementPrototype dividePrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.NumberDivision.class, known, desired);
		final Vector<Object> divideStructure = makePrototypeStructure(dividePrototype, factory, context);

		structure.add(new StringObjectPair(firstOperandRepr + " +", addStructure));
		structure.add(new StringObjectPair(firstOperandRepr + " -", subtractStructure));
		structure.add(new StringObjectPair(firstOperandRepr + " *", multiplyStructure));
		structure.add(new StringObjectPair(firstOperandRepr + " /", divideStructure));

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makeBooleanLogicStructure(final Object firstOperand, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();
		// Unused ?? final boolean isNone = false;
		if (firstOperand instanceof Boolean) {
			// accept
		} else if (firstOperand instanceof edu.cmu.cs.stage3.alice.core.Expression && Boolean.class
				.isAssignableFrom(((edu.cmu.cs.stage3.alice.core.Expression) firstOperand).getValueClass())) {
			// accept
		} else if (firstOperand == null) {
			return null;
		} else {
			throw new IllegalArgumentException("firstOperand must represent a Boolean");
		}

		final String firstOperandRepr = AuthoringToolResources
				.getReprForValue(firstOperand, false);

		// edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory
		// logicFactory = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
		// public Object createItem( final Object object ) {
		// edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep =
		// (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)object;
		// return factory.createItem( ep.createNewElement() );
		// }
		// };

		final StringObjectPair[] known = new StringObjectPair[] {
				new StringObjectPair("a", firstOperand) };
		final String[] desired = new String[] { "b" };

		final ElementPrototype andPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.And.class,
				known, desired);
		final Vector<Object> andStructure = makePrototypeStructure(andPrototype, factory, context);
		final ElementPrototype orPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.Or.class, known,
				desired);
		final Vector<Object> orStructure = makePrototypeStructure(orPrototype, factory, context);
		final ElementPrototype notPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.Not.class,
				known, new String[0]);
		final Object notItem = factory.createItem(notPrototype);
		final ElementPrototype equalPrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.IsEqualTo.class, known, desired);
		final Vector<Object> equalStructure = makePrototypeStructure(equalPrototype, factory, context);
		final ElementPrototype notEqualPrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo.class, known, desired);
		final Vector<Object> notEqualStructure = makePrototypeStructure(notEqualPrototype, factory, context);

		structure.add(new StringObjectPair(firstOperandRepr + " and", andStructure));
		structure.add(new StringObjectPair(firstOperandRepr + " or", orStructure));
		structure.add(new StringObjectPair("not " + firstOperandRepr, notItem));
		structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
		structure.add(new StringObjectPair(firstOperandRepr + " ==", equalStructure));
		structure.add(new StringObjectPair(firstOperandRepr + " !=", notEqualStructure));

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makeComparatorStructure(final Object firstOperand, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		final String firstOperandRepr = AuthoringToolResources
				.getReprForValue(firstOperand, false);

		// edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory
		// comparatorFactory = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
		// public Object createItem( final Object object ) {
		// edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep =
		// (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)object;
		// return factory.createItem( ep.createNewElement() );
		// }
		// };

		final StringObjectPair[] known = new StringObjectPair[] {
				new StringObjectPair("a", firstOperand) };
		final String[] desired = new String[] { "b" };
		final ElementPrototype equalPrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.IsEqualTo.class, known, desired);
		final Vector<Object> equalStructure = makePrototypeStructure(equalPrototype, factory, context);
		final ElementPrototype notEqualPrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo.class, known, desired);
		final Vector<Object> notEqualStructure = makePrototypeStructure(notEqualPrototype, factory, context);

		structure.add(new StringObjectPair(firstOperandRepr + " ==", equalStructure));
		structure.add(new StringObjectPair(firstOperandRepr + " !=", notEqualStructure));

		if (firstOperand instanceof Number
				|| firstOperand instanceof edu.cmu.cs.stage3.alice.core.Expression && Number.class
						.isAssignableFrom(((edu.cmu.cs.stage3.alice.core.Expression) firstOperand).getValueClass())) {
			// ElementPrototype equalToPrototype = new ElementPrototype(
			// edu.cmu.cs.stage3.alice.core.question.NumberIsEqualTo.class,
			// known, desired );
			// Vector<Object> equalToStructure = makePrototypeStructure(
			// equalToPrototype, factory, context );
			// ElementPrototype notEqualToPrototype = new ElementPrototype(
			// edu.cmu.cs.stage3.alice.core.question.NumberIsNotEqualTo.class,
			// known, desired );
			// Vector<Object> notEqualToStructure = makePrototypeStructure(
			// notEqualToPrototype, factory, context );

			final ElementPrototype lessThanPrototype = new ElementPrototype(
					edu.cmu.cs.stage3.alice.core.question.NumberIsLessThan.class, known, desired);
			final Vector<Object> lessThanStructure = makePrototypeStructure(lessThanPrototype, factory, context);
			final ElementPrototype greaterThanPrototype = new ElementPrototype(
					edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThan.class, known, desired);
			final Vector<Object> greaterThanStructure = makePrototypeStructure(greaterThanPrototype, factory,
					context);
			final ElementPrototype lessThanOrEqualPrototype = new ElementPrototype(
					edu.cmu.cs.stage3.alice.core.question.NumberIsLessThanOrEqualTo.class, known, desired);
			final Vector<Object> lessThanOrEqualStructure = makePrototypeStructure(lessThanOrEqualPrototype, factory,
					context);
			final ElementPrototype greaterThanOrEqualPrototype = new ElementPrototype(
					edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThanOrEqualTo.class, known, desired);
			final Vector<Object> greaterThanOrEqualStructure = makePrototypeStructure(greaterThanOrEqualPrototype,
					factory, context);

			// structure.add( new StringObjectPair( firstOperandRepr + " ==",
			// equalToStructure ) );
			// structure.add( new StringObjectPair( firstOperandRepr + " !=",
			// notEqualToStructure ) );
			structure.add(new StringObjectPair(firstOperandRepr + " <", lessThanStructure));
			structure.add(new StringObjectPair(firstOperandRepr + " >", greaterThanStructure));
			structure.add(new StringObjectPair(firstOperandRepr + " <=", lessThanOrEqualStructure));
			structure.add(new StringObjectPair(firstOperandRepr + " >=", greaterThanOrEqualStructure));
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makePartsOfPositionStructure(final Object position, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		if (position instanceof javax.vecmath.Vector3d) {
			// accept
		} else if (position instanceof edu.cmu.cs.stage3.alice.core.Expression && javax.vecmath.Vector3d.class
				.isAssignableFrom(((edu.cmu.cs.stage3.alice.core.Expression) position).getValueClass())) {
			// accept
		} else if (position == null) {
			// accept
		} else {
			throw new IllegalArgumentException("position must represent a javax.vecmath.Vector3d");
		}

		final String positionRepr = AuthoringToolResources
				.getReprForValue(position, false);

		final StringObjectPair[] known = new StringObjectPair[] {
				new StringObjectPair("vector3", position) };

		final ElementPrototype xPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.vector3.X.class,
				known, new String[0]);
		final Object xItem = factory.createItem(xPrototype);
		final ElementPrototype yPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.vector3.Y.class,
				known, new String[0]);
		final Object yItem = factory.createItem(yPrototype);
		final ElementPrototype zPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.vector3.Z.class,
				known, new String[0]);
		final Object zItem = factory.createItem(zPrototype);

		structure.add(new StringObjectPair(positionRepr + "'s distance right", xItem));
		structure.add(new StringObjectPair(positionRepr + "'s distance up", yItem));
		structure.add(new StringObjectPair(positionRepr + "'s distance forward", zItem));

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makeResponsePrintStructure(final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		final StringObjectPair[] known = new StringObjectPair[0];

		final Runnable textStringRunnable = new Runnable() {
			@Override
			public void run() {
				final ElementPrototype elementPrototype = new ElementPrototype(
						edu.cmu.cs.stage3.alice.core.response.Print.class, known, new String[] { "text" });
				// Unused ?? final java.awt.Frame jAliceFrame = AuthoringTool.getHack().getJAliceFrame();
				final String text = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog("Enter text to print:",
						"Enter Text String", javax.swing.JOptionPane.PLAIN_MESSAGE);
				if (text != null) {
					((Runnable) factory.createItem(
							elementPrototype.createCopy(new StringObjectPair("text", text))))
									.run();
				}
			}
		};
		structure.add(new StringObjectPair("text string...", textStringRunnable));

		final ElementPrototype elementPrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.response.Print.class, known, new String[] { "object" });
		structure.add(new StringObjectPair("object",
				PopupMenuUtilities.makePrototypeStructure(elementPrototype, factory, context)));

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makeQuestionPrintStructure(final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		final StringObjectPair[] known = new StringObjectPair[0];

		final Runnable textStringRunnable = new Runnable() {
			@Override
			public void run() {
				final ElementPrototype elementPrototype = new ElementPrototype(
						edu.cmu.cs.stage3.alice.core.question.userdefined.Print.class, known, new String[] { "text" });
				// Unused ?? final java.awt.Frame jAliceFrame = AuthoringTool.getHack().getJAliceFrame();
				final String text = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog("Enter text to print:",
						"Enter Text String", javax.swing.JOptionPane.PLAIN_MESSAGE);
				if (text != null) {
					((Runnable) factory.createItem(
							elementPrototype.createCopy(new StringObjectPair("text", text))))
									.run();
				}
			}
		};
		structure.add(new StringObjectPair("text string...", textStringRunnable));

		final ElementPrototype elementPrototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.userdefined.Print.class, known, new String[] { "object" });
		structure.add(new StringObjectPair("object",
				PopupMenuUtilities.makePrototypeStructure(elementPrototype, factory, context)));

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and
	 * return a Runnable
	 */
	public static Vector<Object> makePropertyValueStructure(final edu.cmu.cs.stage3.alice.core.Element element,
			final Class<?> valueClass, final PopupItemFactory factory,
			final edu.cmu.cs.stage3.alice.core.Element context) {
		final Vector<Object> structure = new Vector<>();

		Class<?> elementClass = null;
		if (element instanceof edu.cmu.cs.stage3.alice.core.Expression) {
			elementClass = ((edu.cmu.cs.stage3.alice.core.Expression) element).getValueClass();
		} else {
			elementClass = element.getClass();
		}

		final StringObjectPair[] known = new StringObjectPair[] {
				new StringObjectPair("element", element) };
		final String[] desired = new String[] { "propertyName" };
		final ElementPrototype prototype = new ElementPrototype(
				edu.cmu.cs.stage3.alice.core.question.PropertyValue.class, known, desired);

		final String[] propertyNames = getPropertyNames(elementClass, valueClass);
		final String prefix = AuthoringToolResources.getReprForValue(element,
				false) + ".";
		for (int i = 0; i < propertyNames.length; i++) {
			if (!propertyNames[i].equals("visualization") && !propertyNames[i].equals("isFirstClass")) { // HACK
																											// suppression
				final String propertyName = AuthoringToolResources
						.getReprForValue(propertyNames[i], false);
				structure.add(new StringObjectPair(prefix + propertyName, factory.createItem(prototype
						.createCopy(new StringObjectPair("propertyName", propertyName)))));
			}
		}

		return structure;
	}

	// HACK until this method is on Element
	private static String[] getPropertyNames(final Class<?> elementClass, final Class<?> valueClass) {
		try {
			final edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) elementClass
					.newInstance();
			final edu.cmu.cs.stage3.alice.core.Property[] properties = element.getProperties();
			final Vector<Object> propertyNames = new Vector<>();
			for (final Property propertie : properties) {
				if (valueClass.isAssignableFrom(propertie.getValueClass())) {
					propertyNames.add(propertie.getName());
				}
			}
			return (String[]) propertyNames.toArray(new String[0]);
		} catch (final InstantiationException ie) {
			return null;
		} catch (final IllegalAccessException iae) {
			return null;
		}
	}

	public static Vector<Object> getDefaultValueStructureForProperty(final Class<?> elementClass,
			final String propertyName) {
		return getDefaultValueStructureForProperty(elementClass, propertyName, null);
	}

	public static Vector<Object> getDefaultValueStructureForProperty(
			final edu.cmu.cs.stage3.alice.core.Property property) {
		return getDefaultValueStructureForProperty(property.getOwner().getClass(), property.getName(), property);
	}

	// property may be null if it is not available. if it is available, though,
	// it will be used to derive the value class.
	public static Vector<Object> getDefaultValueStructureForProperty(final Class<?> elementClass,
			final String propertyName, final edu.cmu.cs.stage3.alice.core.Property property) {
		final Vector<Object> structure = new Vector<>(
				getUnlabeledDefaultValueStructureForProperty(elementClass, propertyName, property));
		addLabelsToValueStructure(structure, elementClass, propertyName);
		return structure;
	}

	// property may be null if it is not available. if it is available, though,
	// it will be used to derive the value class.
	public static Vector<Object> getUnlabeledDefaultValueStructureForProperty(Class<?> elementClass, String propertyName,
			final edu.cmu.cs.stage3.alice.core.Property property) {
		if (property != null) {
			if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation
					&& property.getName().equals("value")) {
				final edu.cmu.cs.stage3.alice.core.response.PropertyAnimation propertyAnimation = (edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) property
						.getOwner();
				elementClass = propertyAnimation.element.getElementValue().getClass();
				if (propertyAnimation.element.getElementValue() instanceof edu.cmu.cs.stage3.alice.core.Variable) {
					final edu.cmu.cs.stage3.alice.core.Variable var = (edu.cmu.cs.stage3.alice.core.Variable) propertyAnimation.element
							.getElementValue();
					elementClass = var.getValueClass();
				}
				propertyName = propertyAnimation.propertyName.getStringValue();

			} else if (property
					.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment
					&& property.getName().equals("value")) {
				final edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment propertyAssignment = (edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment) property
						.getOwner();
				elementClass = propertyAssignment.element.getElementValue().getClass();
				if (propertyAssignment.element.getElementValue() instanceof edu.cmu.cs.stage3.alice.core.Variable) {
					final edu.cmu.cs.stage3.alice.core.Variable var = (edu.cmu.cs.stage3.alice.core.Variable) propertyAssignment.element
							.getElementValue();
					elementClass = var.getValueClass();
				}
				propertyName = propertyAssignment.propertyName.getStringValue();
			}
		}
		Vector<Object> structure = AuthoringToolResources.getDefaultPropertyValues(elementClass, propertyName);
		if (structure == null) {
			structure = new Vector<>();
		}
		if (structure.size() < 1) {
			Class<?> valueClass;
			if (property != null) {
				if (property
						.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion
						&& (property.getName().equals("a") || property.getName().equals("b"))) {
					Object otherValue;
					if (property.getName().equals("a")) {
						otherValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion) property
								.getOwner()).b.get();
					} else {
						otherValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion) property
								.getOwner()).a.get();
					}

					if (otherValue instanceof edu.cmu.cs.stage3.alice.core.Expression) {
						valueClass = ((edu.cmu.cs.stage3.alice.core.Expression) otherValue).getValueClass();
					} else if (otherValue != null) {
						valueClass = otherValue.getClass();
					} else {
						valueClass = property.getValueClass();
					}
				} else {
					valueClass = property.getValueClass();
				}
			} else {
				valueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed(elementClass,
						propertyName);
			}
			structure.addAll(getUnlabeledDefaultValueStructureForClass(valueClass));
		}

		return structure;
	}

	public static Vector<Object> getDefaultValueStructureForCollectionIndexProperty(
			final StringObjectPair[] knownPropertyValues) {
		Object collection = null;
		for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
			if (knownPropertyValue.getString().equals("list")) {
				collection = knownPropertyValue.getObject();
				break;
			} else if (knownPropertyValue.getString().equals("array")) {
				collection = knownPropertyValue.getObject();
				break;
			}
		}

		edu.cmu.cs.stage3.alice.core.Collection realCollection = null;
		if (collection instanceof edu.cmu.cs.stage3.alice.core.Variable) {
			realCollection = (edu.cmu.cs.stage3.alice.core.Collection) ((edu.cmu.cs.stage3.alice.core.Variable) collection)
					.getValue();
		} else if (collection instanceof edu.cmu.cs.stage3.alice.core.Collection) {
			realCollection = (edu.cmu.cs.stage3.alice.core.Collection) collection;
		}

		final Vector<Object> structure = new Vector<>();
		if (realCollection != null) {
			final int size = realCollection.values.size();
			for (int i = 0; i < size && i < 10; i++) {
				structure.add(new StringObjectPair(Integer.toString(i), new Double(i)));
			}
		}

		if (structure.size() < 1) {
			structure.add(new StringObjectPair("0", new Double(0.0)));
		}

		return structure;
	}

	// public static Vector<Object>
	// getDefaultValueStructureForListIndexProperty(
	// StringObjectPair[] knownPropertyValues ) {
	// Object list = null;
	// for( int i = 0; i < knownPropertyValues.length; i++ ) {
	// if( knownPropertyValues[i].getString().equals( "list" ) ) {
	// list = knownPropertyValues[i].getObject();
	// break;
	// }
	// }
	//
	// edu.cmu.cs.stage3.alice.core.List realList = null;
	// if( list instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
	// realList =
	// (edu.cmu.cs.stage3.alice.core.List)((edu.cmu.cs.stage3.alice.core.Variable)list).getValue();
	// } else if( list instanceof edu.cmu.cs.stage3.alice.core.List ) {
	// realList = (edu.cmu.cs.stage3.alice.core.List)list;
	// }
	//
	// Vector<Object> structure = new Vector<>();
	// if( realList != null ) {
	// int size = realList.values.size();
	// for( int i = 0; (i < size) && (i < 10); i++ ) {
	// structure.add( new StringObjectPair( Integer.toString( i ), new Double(
	// (double)i ) ) );
	// }
	// }
	//
	// if( structure.size() < 1 ) {
	// structure.add( new StringObjectPair( "0", new Double( 0.0 ) ) );
	// }
	//
	// return structure;
	// }
	//
	// public static Vector<Object>
	// getDefaultValueStructureForArrayIndexProperty(
	// StringObjectPair[] knownPropertyValues ) {
	// Object array = null;
	// for( int i = 0; i < knownPropertyValues.length; i++ ) {
	// if( knownPropertyValues[i].getString().equals( "array" ) ) {
	// array = knownPropertyValues[i].getObject();
	// break;
	// }
	// }
	//
	// edu.cmu.cs.stage3.alice.core.Array realArray = null;
	// if( array instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
	// realArray =
	// (edu.cmu.cs.stage3.alice.core.Array)((edu.cmu.cs.stage3.alice.core.Variable)array).getValue();
	// } else if( array instanceof edu.cmu.cs.stage3.alice.core.Array ) {
	// realArray = (edu.cmu.cs.stage3.alice.core.Array)array;
	// }
	//
	// Vector<Object> structure = new Vector<>();
	// if( realArray != null ) {
	// int size = realArray.values.size();
	// for( int i = 0; (i < size) && (i < 10); i++ ) {
	// structure.add( new StringObjectPair( Integer.toString( i ), new Double(
	// (double)i ) ) );
	// }
	// }
	//
	// if( structure.size() < 1 ) {
	// structure.add( new StringObjectPair( "0", new Double( 0.0 ) ) );
	// }
	//
	// return structure;
	// }

	public static Vector<Object> getDefaultValueStructureForClass(final Class<?> valueClass) {
		final Vector<Object> structure = getUnlabeledDefaultValueStructureForClass(valueClass);
		addLabelsToValueStructure(structure);
		return structure;
	}

	public static Vector<Object> getUnlabeledDefaultValueStructureForClass(final Class<?> valueClass) {
		final Vector<Object> structure = new Vector<>();

		if (Boolean.class.isAssignableFrom(valueClass)) {
			structure.add(Boolean.TRUE);
			structure.add(Boolean.FALSE);
		} else if (edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom(valueClass)) {
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.RED);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.PINK);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY);
			structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY);
		} else if (Number.class.isAssignableFrom(valueClass)) {
			structure.add(new Double(.25));
			structure.add(new Double(.5));
			structure.add(new Double(1.0));
			structure.add(new Double(2.0));
		} else if (edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom(valueClass)) {
			final edu.cmu.cs.stage3.util.Enumerable[] enumItems = edu.cmu.cs.stage3.util.Enumerable
					.getItems(valueClass);
			for (final Enumerable enumItem : enumItems) {
				structure.add(enumItem);
			}
		} else if (edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom(valueClass)) {
			structure.add(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY);
			structure.add(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY);
			structure.add(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY);
			structure.add(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY);
		} else if (edu.cmu.cs.stage3.math.Vector3.class.isAssignableFrom(valueClass)) {
			structure.add(new edu.cmu.cs.stage3.math.Vector3(0.0, 0.0, 0.0));
		} else if (edu.cmu.cs.stage3.math.Vector4.class.isAssignableFrom(valueClass)) {
			structure.add(new edu.cmu.cs.stage3.math.Vector4(0.0, 0.0, 0.0, 0.0));
		} else if (edu.cmu.cs.stage3.math.Matrix33.class.isAssignableFrom(valueClass)) {
			structure.add(edu.cmu.cs.stage3.math.Matrix33.IDENTITY);
		} else if (edu.cmu.cs.stage3.math.Matrix44.class.isAssignableFrom(valueClass)) {
			structure.add(edu.cmu.cs.stage3.math.Matrix44.IDENTITY);
		} else if (edu.cmu.cs.stage3.math.Quaternion.class.isAssignableFrom(valueClass)) {
			structure.add(new edu.cmu.cs.stage3.math.Quaternion());
		} else if (String.class.isAssignableFrom(valueClass)) {
			structure.add("default string");
		}

		return structure;
	}

	// have to special-case PropertyAnimations
	private static Vector<Object> getDefaultValueStructureForPropertyAnimation(
			final StringObjectPair[] knownPropertyValues) {
		Vector<Object> structure = new Vector<>();
		edu.cmu.cs.stage3.alice.core.Element element = null;
		String propertyName = null;
		for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
			if (knownPropertyValue.getString().equals("element")) {
				element = (edu.cmu.cs.stage3.alice.core.Element) knownPropertyValue.getObject();
				break;
			}
		}
		for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
			if (knownPropertyValue.getString().equals("propertyName")) {
				propertyName = (String) knownPropertyValue.getObject();
				break;
			}
		}
		if (element != null && propertyName != null) {
			Class<?> elementClass = element.getClass();
			if (element instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				elementClass = ((edu.cmu.cs.stage3.alice.core.Expression) element).getValueClass();
			}
			structure = getDefaultValueStructureForProperty(elementClass, propertyName,
					element.getPropertyNamed(propertyName));
		}

		return structure;
	}

	@SuppressWarnings("unchecked")
	private static void addLabelsToValueStructure(final Vector<Object> structure) {
		for (final java.util.ListIterator<Object> iter = structure.listIterator(); iter.hasNext();) {
			final Object item = iter.next();
			if (item instanceof StringObjectPair) {
				final StringObjectPair sop = (StringObjectPair) item;
				if (sop.getObject() instanceof Vector) {
					addLabelsToValueStructure((Vector<Object>) sop.getObject());
				}
			} else if (item instanceof Vector) {
				AuthoringTool
						.showErrorDialog("Unexpected Vector found while processing value structure", null);
			} else {
				final String text = AuthoringToolResources.getReprForValue(item);
				iter.set(new StringObjectPair(text, item));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void addLabelsToValueStructure(final Vector<Object> structure, final Class<?> elementClass,
			final String propertyName) {
		for (final java.util.ListIterator<Object> iter = structure.listIterator(); iter.hasNext();) {
			final Object item = iter.next();
			if (item instanceof StringObjectPair) {
				final StringObjectPair sop = (StringObjectPair) item;
				if (sop.getObject() instanceof Vector) {
					addLabelsToValueStructure((Vector<Object>) sop.getObject(), elementClass, propertyName);
				}
			} else if (item instanceof Vector) {
				AuthoringTool
						.showErrorDialog("Unexpected Vector found while processing value structure", null);
			} else {
				final String text = AuthoringToolResources.getReprForValue(item,
						elementClass, propertyName, "menuContext");
				iter.set(new StringObjectPair(text, item));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static Vector<Object> processStructure(final Vector<Object> structure,
			final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory, final Object currentValue) {
		final Vector<Object> processed = new Vector<>();
		for (final Iterator<Object> iter = structure.iterator(); iter.hasNext();) {
			final Object item = iter.next();
			if (item instanceof StringObjectPair) {
				final StringObjectPair sop = (StringObjectPair) item;
				if (sop.getObject() instanceof Vector) {
					processed.add(new StringObjectPair(sop.getString(),
							processStructure((Vector<Object>) sop.getObject(), factory, currentValue)));
				} else {
					if (currentValue == null && sop.getObject() == null
							|| currentValue != null && currentValue.equals(sop.getObject())) {
						processed.add(new StringObjectPair(sop.getString(),
								new PopupItemWithIcon(factory.createItem(sop.getObject()), currentValueIcon)));
					} else {
						processed.add(new StringObjectPair(sop.getString(),
								factory.createItem(sop.getObject())));
					}
				}
			} else {
				AuthoringTool
						.showErrorDialog("Unexpected Vector found while processing value structure", null);
			}
		}
		return processed;
	}

	@SuppressWarnings("unchecked")
	public static boolean structureContains(final Vector<Object> structure, final Object value) {
		for (final Iterator<Object> iter = structure.iterator(); iter.hasNext();) {
			final Object item = iter.next();
			if (item instanceof StringObjectPair) {
				final StringObjectPair sop = (StringObjectPair) item;
				if (sop.getObject() instanceof Vector) {
					if (structureContains((Vector<Object>) sop.getObject(), value)) {
						return true;
					}
				} else if (sop.getObject() == null) {
					if (value == null) {
						return true;
					}
				} else {
					if (sop.getObject().equals(value)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// have to special-case PropertyAnimations
	private static Class<?> getValueClassForPropertyAnimation(
			final StringObjectPair[] knownPropertyValues) {
		Class<?> valueClass = null;
		edu.cmu.cs.stage3.alice.core.Element element = null;
		String propertyName = null;
		for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
			if (knownPropertyValue.getString().equals("element")) {
				element = (edu.cmu.cs.stage3.alice.core.Element) knownPropertyValue.getObject();
				break;
			}
		}
		for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
			if (knownPropertyValue.getString().equals("propertyName")) {
				propertyName = (String) knownPropertyValue.getObject();
				break;
			}
		}
		if (element instanceof edu.cmu.cs.stage3.alice.core.Variable && "value".equals(propertyName)) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.Variable) element).getValueClass();
		} else if (element != null && propertyName != null) {
			Class<?> elementClass = element.getClass();
			if (element instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				elementClass = ((edu.cmu.cs.stage3.alice.core.Expression) element).getValueClass();
			}
			valueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed(elementClass, propertyName);
		}

		return valueClass;
	}

	private static Class<?> getValueClassForList(final StringObjectPair[] knownPropertyValues) {
		Class<?> valueClass = null;
		Object list = null;
		for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
			if (knownPropertyValue.getString().equals("list")) {
				list = knownPropertyValue.getObject();
				break;
			}
		}
		if (list instanceof edu.cmu.cs.stage3.alice.core.Variable) {
			final edu.cmu.cs.stage3.alice.core.List realList = (edu.cmu.cs.stage3.alice.core.List) ((edu.cmu.cs.stage3.alice.core.Variable) list)
					.getValue();
			if (realList != null) {
				valueClass = realList.valueClass.getClassValue();
			}
		} else if (list instanceof edu.cmu.cs.stage3.alice.core.List) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.List) list).valueClass.getClassValue();
		} else { // bail
			valueClass = Object.class;
		}

		return valueClass;
	}

	private static Class<?> getValueClassForArray(final StringObjectPair[] knownPropertyValues) {
		Class<?> valueClass = null;
		Object array = null;
		for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
			if (knownPropertyValue.getString().equals("array")) {
				array = knownPropertyValue.getObject();
				break;
			}
		}

		if (array instanceof edu.cmu.cs.stage3.alice.core.Variable) {
			final edu.cmu.cs.stage3.alice.core.Array realArray = (edu.cmu.cs.stage3.alice.core.Array) ((edu.cmu.cs.stage3.alice.core.Variable) array)
					.getValue();
			valueClass = realArray.valueClass.getClassValue();
		} else if (array instanceof edu.cmu.cs.stage3.alice.core.Array) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.Array) array).valueClass.getClassValue();
		} else { // bail
			valueClass = Object.class;
		}

		return valueClass;
	}

	private static Class<?> generalizeValueClass(final Class<?> valueClass) {
		Class<?> newValueClass = valueClass;
		if (java.lang.Number.class.isAssignableFrom(valueClass)) {
			newValueClass = java.lang.Number.class;
		} else if (edu.cmu.cs.stage3.alice.core.Model.class.isAssignableFrom(valueClass)) {
			newValueClass = edu.cmu.cs.stage3.alice.core.Model.class;
		}
		return newValueClass;
	}

	private static Class<?> getValueClassForComparator(
			final StringObjectPair[] knownPropertyValues) {
		Class<?> valueClass = null;
		Object operand = null;
		for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
			if (knownPropertyValue.getString().equals("a")) {
				operand = knownPropertyValue.getObject();
				break;
			}
		}
		if (operand instanceof edu.cmu.cs.stage3.alice.core.Expression) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.Expression) operand).getValueClass();
		} else if (operand != null) {
			valueClass = operand.getClass();
		}
		return generalizeValueClass(valueClass);
	}

	public static Class<?> getDesiredValueClass(final edu.cmu.cs.stage3.alice.core.Property property) {
		Class<?> targetValueClass = property.getValueClass();
		if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion
				&& (property.getName().equals("a") || property.getName().equals("b"))) {
			Object otherValue;
			Object ourValue;
			if (property.getName().equals("a")) {
				otherValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion) property
						.getOwner()).b.get();
				ourValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion) property
						.getOwner()).a.get();
			} else {
				otherValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion) property
						.getOwner()).a.get();
				ourValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion) property
						.getOwner()).b.get();
			}
			Class<?> otherValueClass;
			if (otherValue instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				otherValueClass = ((edu.cmu.cs.stage3.alice.core.Expression) otherValue).getValueClass();
			} else if (otherValue != null) {
				otherValueClass = otherValue.getClass();
			} else {
				otherValueClass = property.getValueClass();
			}
			if (ourValue instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				targetValueClass = ((edu.cmu.cs.stage3.alice.core.Expression) ourValue).getValueClass();
			} else if (ourValue != null) {
				targetValueClass = ourValue.getClass();
			} else {
				targetValueClass = property.getValueClass();
			}
			if (targetValueClass != otherValueClass) {
				targetValueClass = otherValueClass;
			}
			targetValueClass = generalizeValueClass(targetValueClass);
		}
		return targetValueClass;
	}

	public static Vector<Object> makeDefaultOneShotStructure(final edu.cmu.cs.stage3.alice.core.Element element) {
		// return makeResponseStructure( element, oneShotFactory,
		// AuthoringTool.getHack().getWorld()
		// );
		return makeResponseStructure(element, oneShotFactory, element.getRoot());
	}

	public static void ensurePopupIsOnScreen(final JPopupMenu popup) {
		final java.awt.Point location = popup.getLocation(null);
		final java.awt.Dimension size = popup.getSize(null);
		final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height -= 28; // hack for standard Windows Task Bar

		javax.swing.SwingUtilities.convertPointToScreen(location, popup);

		if (location.x < 0) {
			location.x = 0;
		} else if (location.x + size.width > screenSize.width) {
			location.x -= location.x + size.width - screenSize.width;
		}
		if (location.y < 0) {
			location.y = 0;
		} else if (location.y + size.height > screenSize.height) {
			location.y -= location.y + size.height - screenSize.height;
		}

		popup.setLocation(location);
	}

	public static java.awt.event.ActionListener getPopupMenuItemActionListener(final Runnable runnable) {
		return new PopupMenuItemActionListener(runnable);
	}

	public static javax.swing.JPopupMenu makeDisabledPopup(final String s) {
		final javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
		final javax.swing.JMenuItem item = makeMenuItem(s, null);
		item.setEnabled(false);
		popup.add(item);
		return popup;
	}

	public static edu.cmu.cs.stage3.util.Criterion getAvailableExpressionCriterion(final Class<?> valueClass,
			edu.cmu.cs.stage3.alice.core.Element context) {
		// DEBUG System.out.println( "getAvailableExpressionCriterion( " +
		// valueClass + ", " + context + " )" );
		if (context == null) { // SERIOUS HACK
			AuthoringTool
					.showErrorDialog("Error: null context while looking for expressions; using World", null);
			context = AuthoringTool.getHack().getWorld();
		}
		final edu.cmu.cs.stage3.util.Criterion isAccessible = new edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAccessibleFromCriterion(
				context);
		final edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion isAssignable = new edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion(
				valueClass);
		return new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion(new edu.cmu.cs.stage3.util.Criterion[] {
				isNotActualParameter, isAccessible, isNamedElement, isAssignable });
	}

	public static void printStructure(final Vector<Object> structure) {
		printStructure(structure, 0);
	}

	@SuppressWarnings("unchecked")
	private static void printStructure(final Vector<Object> structure, final int indent) {
		String tabs = "";
		for (int i = 0; i < indent; i++) {
			tabs = tabs + "\t";
		}
		for (final Iterator<Object> iter = structure.iterator(); iter.hasNext();) {
			final Object item = iter.next();
			if (item instanceof StringObjectPair) {
				final StringObjectPair sop = (StringObjectPair) item;
				if (sop.getObject() instanceof Vector) {
					printStructure((Vector<Object>) sop.getObject(), indent + 1);
				} else {
					System.out.println(tabs + sop.getString() + " : " + sop.getObject());
				}
			} else {
				AuthoringTool
						.showErrorDialog("unexpected object found while printing structure: " + item, null);
			}
		}
	}
}

class PopupMenuItemActionListener implements java.awt.event.ActionListener {
	protected Runnable runnable;
	protected javax.swing.JMenu menu; // MEMFIX

	public PopupMenuItemActionListener(final Runnable runnable) {
		this.runnable = runnable;
	}

	public PopupMenuItemActionListener(final Runnable runnable, final javax.swing.JMenu menu) { // MEMFIX
		this.runnable = runnable;
		this.menu = menu;
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		try {
			runnable.run();
			if (menu != null) { // MEMFIX
				menu.getPopupMenu().setInvoker(null); // MEMFIX
			}
			// runnable = null; //MEMFIX
		} catch (final Throwable t) {
			AuthoringTool.showErrorDialog("Error encountered while responding to popup menu item.", t);
		}
	}
}

class PopupMenuItemCallbackActionListener implements java.awt.event.ActionListener {
	protected edu.cmu.cs.stage3.alice.authoringtool.util.Callback callback;
	protected Object context;
	protected Object source;

	public PopupMenuItemCallbackActionListener(final edu.cmu.cs.stage3.alice.authoringtool.util.Callback callback,
			final Object context, final Object source) {
		this.callback = callback;
		this.context = context;
		this.source = source;
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		callback.callback(context, source);
	}
}
