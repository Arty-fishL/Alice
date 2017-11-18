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

import java.util.Iterator;

import edu.cmu.cs.stage3.alice.core.Element;

/**
 * @author Jason Pratt
 */
public class ElementUsageGraph extends javax.swing.JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = -199509150466060142L;
	protected edu.cmu.cs.stage3.alice.core.Element root;
	protected edu.cmu.cs.stage3.util.Criterion elementCriterion;
	protected edu.cmu.cs.stage3.util.Criterion acceptAllCriterion = new edu.cmu.cs.stage3.util.Criterion() {
		@Override
		public boolean accept(final Object o) {
			return true;
		}
	};
	protected ClassNameComparator classNameComparator = new ClassNameComparator();
	protected float saturation = .5f;
	protected float brightness = .9f;
	protected java.util.HashMap<Class<? extends Element>, Integer> classCountMap = new java.util.HashMap<>();
	protected java.awt.GridBagConstraints labelConstraints = new java.awt.GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
			java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0);
	protected java.awt.GridBagConstraints barConstraints = new java.awt.GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
			java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 8, 0), 0, 12);
	protected java.util.Random random = new java.util.Random();

	public ElementUsageGraph() {
		setBackground(java.awt.Color.white);
		setLayout(new java.awt.GridBagLayout());
		setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createLineBorder(java.awt.Color.black),
				javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
	}

	public ElementUsageGraph(final edu.cmu.cs.stage3.alice.core.Element root,
			final edu.cmu.cs.stage3.util.Criterion elementCriterion) {
		this();
		setRoot(root);
		setElementCriterion(elementCriterion);
	}

	public void setRoot(final edu.cmu.cs.stage3.alice.core.Element root) {
		this.root = root;
	}

	public edu.cmu.cs.stage3.alice.core.Element getRoot() {
		return root;
	}

	public void setElementCriterion(final edu.cmu.cs.stage3.util.Criterion elementCriterion) {
		this.elementCriterion = elementCriterion;
	}

	public edu.cmu.cs.stage3.util.Criterion getElementCriterion() {
		return elementCriterion;
	}

	@SuppressWarnings("unchecked")
	public void refresh() {
		classCountMap.clear();
		removeAll();
		int gridy = 0;

		final edu.cmu.cs.stage3.util.Criterion criterion = elementCriterion != null ? elementCriterion
				: acceptAllCriterion;
		if (root != null) {
			int maxCount = 0;
			final edu.cmu.cs.stage3.alice.core.Element[] elements = root.search(criterion);
			for (final Element element : elements) {
				final Class<? extends Element> c = element.getClass();
				if (classCountMap.containsKey(c)) {
					final Integer count = (Integer) classCountMap.get(c);
					classCountMap.put(c, new Integer(count.intValue() + 1));
					maxCount = Math.max(maxCount, count.intValue() + 1);
				} else {
					classCountMap.put(c, new Integer(1));
					maxCount = Math.max(maxCount, 1);
				}
			}

			final javax.swing.JLabel totalLabel = new javax.swing.JLabel("Total: " + elements.length);
			barConstraints.gridy = gridy++;
			add(totalLabel, barConstraints);

			final java.util.List<Class<? extends Element>> classList = new java.util.ArrayList<>(classCountMap.keySet());
			java.util.Collections.sort(classList, classNameComparator);
			for (final Iterator<Class<? extends Element>> iter = classList.iterator(); iter.hasNext();) {
				final Class<? extends Element> c = (Class<? extends Element>) iter.next();
				String name = c.getName();
				name = name.substring(name.lastIndexOf('.') + 1);
				final int count = ((Integer) classCountMap.get(c)).intValue();
				final double portion = (double) count / (double) maxCount;
				random.setSeed(name.hashCode());
				final java.awt.Color color = java.awt.Color.getHSBColor(random.nextFloat(), saturation, brightness);

				final javax.swing.JLabel label = new javax.swing.JLabel(name + ": " + count);
				labelConstraints.gridy = gridy++;
				add(label, labelConstraints);

				final HorizontalBar bar = new HorizontalBar(portion, color);
				barConstraints.gridy = gridy++;
				add(bar, barConstraints);
			}
		}

		revalidate();
		repaint();
	}

	protected class HorizontalBar extends javax.swing.JPanel {
		/**
		 *
		 */
		private static final long serialVersionUID = 8485144729715257982L;
		protected double portion;
		protected java.awt.Color color;

		public HorizontalBar(final double portion, final java.awt.Color color) {
			this.portion = portion;
			this.color = color;

			setOpaque(false);
		}

		@Override
		public void paintComponent(final java.awt.Graphics g) {
			super.paintComponent(g);

			g.setColor(color);
			final java.awt.Dimension size = getSize();
			int width = size.width - 1;
			if (width > 2) {
				width = (int) Math.round((width - 1.0) * portion);
			}
			g.fill3DRect(0, 0, width, size.height - 1, true);
		}
	}

	@SuppressWarnings("rawtypes")
	protected class ClassNameComparator implements java.util.Comparator {
		@Override
		public int compare(final Object o1, final Object o2) {
			if (o1 instanceof Class && o2 instanceof Class) {
				String name1 = ((Class) o1).getName();
				name1 = name1.substring(name1.lastIndexOf('.') + 1);
				String name2 = ((Class) o2).getName();
				name2 = name2.substring(name2.lastIndexOf('.') + 1);
				return name1.compareTo(name2);
			} else {
				return 0;
			}
		}
	}
}