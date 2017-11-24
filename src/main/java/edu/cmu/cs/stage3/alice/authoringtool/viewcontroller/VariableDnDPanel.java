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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class VariableDnDPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5799461014568454103L;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.core.Variable variable;
	protected javax.swing.JLabel nameLabel = new javax.swing.JLabel();
	protected javax.swing.JTextField textField = new javax.swing.JTextField();
	protected java.awt.event.FocusListener focusListener = new java.awt.event.FocusAdapter() {

		@Override
		public void focusLost(final java.awt.event.FocusEvent ev) {
			stopEditing();
		}
	};
	protected java.util.Vector<StringObjectPair> popupStructure = new java.util.Vector<StringObjectPair>();
	protected NamePropertyListener namePropertyListener = new NamePropertyListener();

	public VariableDnDPanel() {
		setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("variableDnDPanel"));

		add(nameLabel, java.awt.BorderLayout.CENTER);
		addDragSourceComponent(nameLabel);

		final java.awt.event.MouseListener mouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {

			@Override
			public void popupResponse(final java.awt.event.MouseEvent ev) {
				VariableDnDPanel.this.updatePopupStructure();
				edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(popupStructure,
						VariableDnDPanel.this, ev.getX(), ev.getY());
			}
		};
		addMouseListener(mouseListener);
		nameLabel.addMouseListener(mouseListener);
		grip.addMouseListener(mouseListener);

		textField.setColumns(5);

		textField.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent ev) {
				stopEditing();
			}
		});
		textField.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyPressed(final java.awt.event.KeyEvent ev) {
				if (ev.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
					cancelEditing();
				}
			}
		});
	}

	public void set(final edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool,
			final edu.cmu.cs.stage3.alice.core.Variable variable) {
		if (this.variable != null) {
			this.variable.name.removePropertyListener(namePropertyListener);
		}

		this.authoringTool = authoringTool;
		this.variable = variable;
		nameLabel.setText(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(variable));

		if (variable != null) {
			String iconName;

			if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(variable.getValueClass())) {
				final edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List) variable.getValue();
				if (list != null) {
					iconName = "types/lists/" + list.valueClass.getClassValue().getName();
				} else {
					iconName = "types/lists";
				}
			} else {
				iconName = "types/" + variable.getValueClass().getName();
			}
			javax.swing.ImageIcon icon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
					.getIconForValue(iconName);
			if (icon == null) {
				if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(variable.getValueClass())) {
					icon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
							.getIconForValue("types/lists/other");
				} else {
					icon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("types/other");
				}
			}
			if (icon != null) {
				nameLabel.setIcon(icon);
			}
			setTransferable(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory
					.createTransferable(variable));

			variable.name.addPropertyListener(namePropertyListener);
		} else {
			setTransferable(null);
		}
	}

	public void editName() {
		if (variable.name.getStringValue() != null) {
			textField.setText(variable.name.getStringValue());
		}
		if (isAncestorOf(nameLabel)) {
			this.remove(nameLabel);
		}
		if (!isAncestorOf(textField)) {
			this.add(textField, java.awt.BorderLayout.CENTER);
		}
		textField.requestFocus();
		textField.addFocusListener(focusListener);
		revalidate();
	}

	public void stopEditing() {
		final String prevName = variable.name.getStringValue();
		textField.removeFocusListener(focusListener);
		final String valueString = textField.getText();
		try {
			variable.name.set(valueString);
			remove(textField);
			add(nameLabel, java.awt.BorderLayout.CENTER);
			nameLabel.requestFocus();
		} catch (final edu.cmu.cs.stage3.alice.core.IllegalNameValueException e) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(e.getMessage(), "Error setting name",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			textField.setText(prevName);
			// textField.selectAll();
		}

		// // begin HACK
		// remove( textField );
		// add( nameLabel, java.awt.BorderLayout.CENTER );
		// nameLabel.requestFocus();
		// // end HACK
		// valueString = valueString.trim();
		// if( ! valueString.equals( "" ) ) {
		// try {
		// variable.name.set( valueString );
		// } catch( edu.cmu.cs.stage3.alice.core.IllegalNameValueException e ) {
		// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
		// e.getMessage(), e );
		// }
		// }
		revalidate();
	}

	public void cancelEditing() {
		textField.removeFocusListener(focusListener);
		// begin HACK
		remove(textField);
		add(nameLabel, java.awt.BorderLayout.CENTER);
		// end HACK
		revalidate();
	}

	public void updatePopupStructure() {
		popupStructure.clear();

		if (variable != null) {
			popupStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair("rename", new Runnable() {
				@Override
				public void run() {
					VariableDnDPanel.this.editName();
				}
			}));

			final edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel watcherPanel = authoringTool
					.getWatcherPanel();
			if (watcherPanel.isVariableBeingWatched(variable)) {
				popupStructure
						.add(new edu.cmu.cs.stage3.util.StringObjectPair("stop watching this variable", new Runnable() {
							@Override
							public void run() {
								watcherPanel.removeVariableBeingWatched(variable);
							}
						}));
			} else {
				popupStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair("watch this variable", new Runnable() {
					@Override
					public void run() {
						watcherPanel.addVariableToWatch(variable);
					}
				}));
			}
			popupStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair("delete", new Runnable() {
				@Override
				public void run() {
					final edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable deleteRunnable = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable(
							variable, authoringTool);
					deleteRunnable.run();
				}
			}));
		}
	}

	@Override
	public void goToSleep() {
	}

	@Override
	public void wakeUp() {
	}

	@Override
	public void clean() {
		setTransferable(null);
	}

	@Override
	public void die() {
		clean();
	}

	@Override
	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

	class NamePropertyListener implements edu.cmu.cs.stage3.alice.core.event.PropertyListener {
		@Override
		public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}

		@Override
		public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			nameLabel.setText(variable.name.getStringValue());
		}
	}
}
