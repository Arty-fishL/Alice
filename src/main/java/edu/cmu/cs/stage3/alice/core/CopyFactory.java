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

import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.reference.ObjectArrayPropertyReference;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.progress.ProgressObserver;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.util.HowMuch;

class CopyReferenceGenerator extends edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceGenerator {
	private final Class[] m_classesToShare;

	public CopyReferenceGenerator(final Element internalRoot, final Class[] classesToShare) {
		super(internalRoot);
		m_classesToShare = classesToShare;
	}

	@Override
	protected boolean isExternal(final Element element) {
		if (element.isAssignableToOneOf(m_classesToShare)) {
			return true;
		}
		return super.isExternal(element);
	}
}

class VariableCriterion implements edu.cmu.cs.stage3.util.Criterion {
	private final edu.cmu.cs.stage3.util.Criterion m_wrappedCriterion;
	private final String m_name;

	public VariableCriterion(final String name, final edu.cmu.cs.stage3.util.Criterion wrappedCriterion) {
		m_name = name;
		m_wrappedCriterion = wrappedCriterion;
	}

	public String getName() {
		return m_name;
	}

	public edu.cmu.cs.stage3.util.Criterion getWrappedCriterion() {
		return m_wrappedCriterion;
	}

	@Override
	public boolean accept(final Object o) {
		if (o instanceof Variable) {
			final Variable variable = (Variable) o;
			if (m_name != null) {
				return m_name.equalsIgnoreCase(variable.name.getStringValue());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "VariableCriterion[" + m_name + "]";
	}

}

class CodeCopyReferenceGenerator extends CopyReferenceGenerator {
	public CodeCopyReferenceGenerator(final Element internalRoot, final Class[] classesToShare) {
		super(internalRoot, classesToShare);
	}

	@Override
	public edu.cmu.cs.stage3.util.Criterion generateReference(final edu.cmu.cs.stage3.alice.core.Element element) {
		edu.cmu.cs.stage3.util.Criterion criterion = super.generateReference(element);
		if (element instanceof Variable) {
			final Element parent = element.getParent();
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				// pass
			} else {
				criterion = new VariableCriterion(element.name.getStringValue(), criterion);
			}
		}
		return criterion;
	}
}

// class CodeCopyReferenceResolver extends
// edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver {
// private Element m_parentToBe;
// public CodeCopyReferenceResolver( Element internalRoot, Element externalRoot,
// Element parentToBe ) {
// super( internalRoot, externalRoot );
// m_parentToBe = parentToBe;
// }
// private Element lookup( Element element, VariableCriterion variableCriterion
// ) {
// System.err.println( "lookup: " + element );
// if( element != null ) {
// for( int i=0; i<element.getChildCount(); i++ ) {
// Element child = element.getChildAt( i );
// if( variableCriterion.accept( child ) ) {
// return child;
// }
// }
// return lookup( element.getParent(), variableCriterion );
// } else {
// return null;
// }
// }
// public Element resolveReference( edu.cmu.cs.stage3.util.Criterion criterion )
// throws edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException {
// if( criterion instanceof VariableCriterion ) {
// VariableCriterion variableCriterion = (VariableCriterion)criterion;
// Element variable = lookup( m_parentToBe, variableCriterion );
// if( variable == null ) {
// throw new edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException(
// variableCriterion, "could not resolve variable criterion: " +
// variableCriterion );
// } else {
// return variable;
// }
// } else {
// return super.resolveReference( criterion );
// }
// }
// }
public class CopyFactory {
	private class ElementCapsule {
		private class PropertyCapsule {
			private final String m_name;
			private Object m_value;

			private PropertyCapsule(final Property property, final ReferenceGenerator referenceGenerator) {
				m_name = property.getName();
				if (property instanceof ObjectArrayProperty) {
					final ObjectArrayProperty oap = (ObjectArrayProperty) property;
					if (oap.get() != null) {
						final Object[] array = new Object[oap.size()];
						for (int i = 0; i < oap.size(); i++) {
							array[i] = getValueToTuckAway(oap.get(i), referenceGenerator);
						}
						m_value = array;
					} else {
						m_value = null;
					}
				} else {
					m_value = getValueToTuckAway(property.get(), referenceGenerator);
				}
			}

			private Object getCopyIfPossible(final Object o) {
				if (o instanceof Cloneable) {
					// todo
					// return o.clone();
					// check for constructor that takes object of this class,
					// too
					return o;
				} else {
					return o;
				}
			}

			private Object getValueToTuckAway(final Object value, final ReferenceGenerator referenceGenerator) {
				if (value instanceof Element) {
					return referenceGenerator.generateReference((Element) value);
				} else {
					return getCopyIfPossible(value);
				}
			}

			private Object getValueForCopy(final Property property, final Object value,
					final java.util.Vector referencesToBeResolved) {
				if (value instanceof Criterion) {
					referencesToBeResolved.addElement(new PropertyReference(property, (Criterion) value));
					return null;
				} else {
					return getCopyIfPossible(value);
				}
			}

			private Object getValueForCopy(final ObjectArrayProperty oap, final Object value, final int i,
					final java.util.Vector referencesToBeResolved) {
				if (value instanceof Criterion) {
					referencesToBeResolved.addElement(new ObjectArrayPropertyReference(oap, (Criterion) value, i, 0));
					return null;
				} else {
					return getCopyIfPossible(value);
				}
			}

			public void set(final Element element, final java.util.Vector referencesToBeResolved) {
				final Property property = element.getPropertyNamed(m_name);
				if (property instanceof ObjectArrayProperty) {
					final ObjectArrayProperty oap = (ObjectArrayProperty) property;
					if (m_value != null) {
						final Object[] src = (Object[]) m_value;
						final Object[] dst = (Object[]) java.lang.reflect.Array.newInstance(oap.getComponentType(),
								src.length);
						for (int i = 0; i < src.length; i++) {
							dst[i] = getValueForCopy(oap, src[i], i, referencesToBeResolved);
						}
						oap.set(dst);
					} else {
						oap.set(null);
					}
				} else {
					property.set(getValueForCopy(property, m_value, referencesToBeResolved));
				}
			}
		}

		private final Class m_cls;
		private final PropertyCapsule[] m_propertyCapsules;
		private final ElementCapsule[] m_childCapsules;

		private ElementCapsule(final Element element, final ReferenceGenerator referenceGenerator,
				final Class[] classesToShare, final HowMuch howMuch) {
			m_cls = element.getClass();

			// todo: handle howMuch
			final Element[] elementChildren = element.getChildren();
			m_childCapsules = new ElementCapsule[elementChildren.length];
			for (int i = 0; i < m_childCapsules.length; i++) {
				if (elementChildren[i].isAssignableToOneOf(classesToShare)) {
					m_childCapsules[i] = null;
				} else {
					m_childCapsules[i] = new ElementCapsule(elementChildren[i], referenceGenerator, classesToShare,
							howMuch);
				}
			}

			final Property[] elementProperties = element.getProperties();
			m_propertyCapsules = new PropertyCapsule[elementProperties.length];
			for (int i = 0; i < m_propertyCapsules.length; i++) {
				m_propertyCapsules[i] = new PropertyCapsule(elementProperties[i], referenceGenerator);
			}
		}

		private String getName() {
			for (final PropertyCapsule propertyCapsule : m_propertyCapsules) {
				if (propertyCapsule.m_name.equals("name")) {
					return (String) propertyCapsule.m_value;
				}
			}
			return null;
		}

		private Element internalManufacture(final java.util.Vector referencesToBeResolved) {
			Element element;
			try {
				element = (Element) m_cls.newInstance();
			} catch (final Throwable t) {
				throw new RuntimeException();
			}
			for (final ElementCapsule m_childCapsule : m_childCapsules) {
				if (m_childCapsule != null) {
					element.addChild(m_childCapsule.internalManufacture(referencesToBeResolved));
				}
			}
			for (final PropertyCapsule m_propertyCapsule : m_propertyCapsules) {
				m_propertyCapsule.set(element, referencesToBeResolved);
			}
			return element;
		}

		private Element lookup(final Element element, final VariableCriterion variableCriterion) {
			if (element != null) {
				for (int i = 0; i < element.getChildCount(); i++) {
					final Element child = element.getChildAt(i);
					if (variableCriterion.accept(child)) {
						return child;
					}
				}
				return lookup(element.getParent(), variableCriterion);
			} else {
				return null;
			}
		}

		// todo: update progressObserver
		private Element manufacture(final ReferenceResolver referenceResolver, final ProgressObserver progressObserver,
				final Element parentToBe) throws UnresolvablePropertyReferencesException {
			final java.util.Vector referencesToBeResolved = new java.util.Vector();
			final Element element = internalManufacture(referencesToBeResolved);
			final java.util.Vector referencesLeftUnresolved = new java.util.Vector();
			element.setParent(parentToBe);
			try {
				if (referenceResolver instanceof edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver) {
					final edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver drr = (edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver) referenceResolver;
					if (drr.getInternalRoot() == null) {
						drr.setInternalRoot(element);
					}
				}
				final java.util.Enumeration enum0 = referencesToBeResolved.elements();
				while (enum0.hasMoreElements()) {
					final PropertyReference propertyReference = (PropertyReference) enum0.nextElement();
					try {
						final Criterion criterion = propertyReference.getCriterion();
						if (criterion instanceof VariableCriterion) {
							final VariableCriterion variableCriterion = (VariableCriterion) criterion;
							final Element variable = lookup(propertyReference.getProperty().getOwner(),
									variableCriterion);
							if (variable != null) {
								propertyReference.getProperty().set(variable);
							} else {
								// System.err.println("Cannot make a copy of
								// this item. Try again using the clipboard");
								// throw new
								// edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException(
								// variableCriterion,
								// "could not resolve variable criterion: " +
								// variableCriterion );
							}
						} else {
							propertyReference.resolve(referenceResolver);
						}
					} catch (final UnresolvableReferenceException ure) {
						referencesLeftUnresolved.addElement(propertyReference);
					}
				}
			} finally {
				element.setParent(null);
			}
			if (referencesLeftUnresolved.size() > 0) {
				final PropertyReference[] propertyReferences = new PropertyReference[referencesLeftUnresolved.size()];
				referencesLeftUnresolved.copyInto(propertyReferences);
				final StringBuffer sb = new StringBuffer();
				sb.append("PropertyReferences: \n");
				for (final PropertyReference propertyReference : propertyReferences) {
					sb.append(propertyReference);
					sb.append("\n");
				}
				throw new UnresolvablePropertyReferencesException(propertyReferences, element, sb.toString());
			}
			return element;
		}
	}

	private final ElementCapsule m_capsule;
	private final Class m_valueClass;
	private Class HACK_m_hackValueClass;

	public CopyFactory(final Element element, final Element internalReferenceRoot, final Class[] classesToShare,
			final HowMuch howMuch) {
		ReferenceGenerator referenceGenerator;
		if (element instanceof Response
				|| element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component) {
			referenceGenerator = new CodeCopyReferenceGenerator(internalReferenceRoot, classesToShare);
		} else {
			referenceGenerator = new CopyReferenceGenerator(internalReferenceRoot, classesToShare);
		}
		m_capsule = new ElementCapsule(element, referenceGenerator, classesToShare, howMuch);
		m_valueClass = element.getClass();
		HACK_m_hackValueClass = null;
		try {
			if (element instanceof Expression) {
				HACK_m_hackValueClass = ((Expression) element).getValueClass();
			}
		} catch (final Throwable t) {
			// pass
		}
	}

	public Class getValueClass() {
		return m_valueClass;
	}

	public Class HACK_getExpressionValueClass() {
		return HACK_m_hackValueClass;
	}

	public Element manufactureCopy(final ReferenceResolver referenceResolver, final ProgressObserver progressObserver,
			final Element parentToBe) throws UnresolvablePropertyReferencesException {
		return m_capsule.manufacture(referenceResolver, progressObserver, parentToBe);
	}

	public Element manufactureCopy(final Element externalRoot, final Element internalRoot,
			final ProgressObserver progressObserver, final Element parentToBe)
			throws UnresolvablePropertyReferencesException {
		// ReferenceResolver referenceResolver;
		// if( Response.class.isAssignableFrom( m_valueClass ) ||
		// edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(
		// m_valueClass ) ) {
		// referenceResolver = new CodeCopyReferenceResolver( internalRoot,
		// externalRoot, parentToBe );
		// } else {
		// referenceResolver = ;
		// }
		// return manufactureCopy( referenceResolver, progressObserver,
		// parentToBe );
		return manufactureCopy(
				new edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver(internalRoot, externalRoot),
				progressObserver, parentToBe);
	}

	public Element manufactureCopy(final Element externalRoot, final Element internalRoot,
			final ProgressObserver progressObserver) throws UnresolvablePropertyReferencesException {
		return manufactureCopy(externalRoot, internalRoot, progressObserver, null);
	}

	public Element manufactureCopy(final Element externalRoot, final Element internalRoot)
			throws UnresolvablePropertyReferencesException {
		return manufactureCopy(externalRoot, internalRoot, null);
	}

	public Element manufactureCopy(final Element externalRoot) throws UnresolvablePropertyReferencesException {
		return manufactureCopy(externalRoot, (Element) null);
	}

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.alice.core.CopyFactory[" + m_valueClass + "]";
	}
}
