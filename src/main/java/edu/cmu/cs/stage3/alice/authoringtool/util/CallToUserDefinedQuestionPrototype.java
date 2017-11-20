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

import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class CallToUserDefinedQuestionPrototype extends QuestionPrototype {
	protected edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion actualQuestion;
	protected RefreshListener refreshListener = new RefreshListener();

	public CallToUserDefinedQuestionPrototype(
			final edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion actualQuestion) {
		super(edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class,
				new edu.cmu.cs.stage3.util.StringObjectPair[0], new String[0]);
		this.actualQuestion = actualQuestion;

	}

	public void startListening() {
		if (actualQuestion != null) {
			actualQuestion.requiredFormalParameters.addObjectArrayPropertyListener(refreshListener);
			final Object[] vars = actualQuestion.requiredFormalParameters.getArrayValue();
			for (final Object var : vars) {
				((edu.cmu.cs.stage3.alice.core.Variable) var).name.addPropertyListener(refreshListener);
			}
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("actualQuestion is null", null);
		}
	}

	public void stopListening() {
		if (actualQuestion != null) {
			actualQuestion.requiredFormalParameters.removeObjectArrayPropertyListener(refreshListener);
			final Object[] vars = actualQuestion.requiredFormalParameters.getArrayValue();
			for (final Object var : vars) {
				((edu.cmu.cs.stage3.alice.core.Variable) var).name.removePropertyListener(refreshListener);
			}
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("actualQuestion is null", null);
		}
	}

	protected CallToUserDefinedQuestionPrototype(
			final edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion actualQuestion,
			final edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues, final String[] desiredProperties) {
		super(edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class, knownPropertyValues,
				desiredProperties);
		this.actualQuestion = actualQuestion;
	}

	@Override
	public edu.cmu.cs.stage3.alice.core.Element createNewElement() {
		final java.util.HashMap<String, Object> knownMap = new java.util.HashMap<>();
		for (final StringObjectPair knownPropertyValue : knownPropertyValues) {
			knownMap.put(knownPropertyValue.getString(), knownPropertyValue.getObject());
		}

		final edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion callToUserDefinedQuestion = new edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion();
		callToUserDefinedQuestion.userDefinedQuestion.set(actualQuestion);
		final Object[] params = actualQuestion.requiredFormalParameters.getArrayValue();
		for (final Object param : params) {
			final edu.cmu.cs.stage3.alice.core.Variable formalParameter = (edu.cmu.cs.stage3.alice.core.Variable) param;
			final edu.cmu.cs.stage3.alice.core.Variable actualParameter = new edu.cmu.cs.stage3.alice.core.Variable();
			actualParameter.name.set(formalParameter.name.get());
			actualParameter.valueClass.set(formalParameter.valueClass.get());
			if (!knownMap.containsKey(formalParameter.name.get())) {
				actualParameter.value.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
						.getDefaultValueForClass((Class<?>) formalParameter.valueClass.get()));
			} else {
				actualParameter.value.set(knownMap.get(formalParameter.name.get()));
			}
			callToUserDefinedQuestion.addChild(actualParameter);
			callToUserDefinedQuestion.requiredActualParameters.add(actualParameter);
		}

		return callToUserDefinedQuestion;
	}

	@Override
	public ElementPrototype createCopy(final edu.cmu.cs.stage3.util.StringObjectPair[] newKnownPropertyValues) {
		return super.createCopy(newKnownPropertyValues);
	}

	public edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion getActualQuestion() {
		return actualQuestion;
	}

	private void calculateDesiredProperties() {
		final Object[] params = actualQuestion.requiredFormalParameters.getArrayValue();
		desiredProperties = new String[params.length];
		for (int i = 0; i < params.length; i++) {
			desiredProperties[i] = ((edu.cmu.cs.stage3.alice.core.Variable) params[i]).name.getStringValue();
		}
	}

	// a rather inelegant solution for creating copies of the correct type.
	// subclasses should override this method and call their own constructor

	@Override
	protected ElementPrototype createInstance(final Class<? extends edu.cmu.cs.stage3.alice.core.Element> elementClass,
			final edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues, final String[] desiredProperties) {
		return new CallToUserDefinedQuestionPrototype(actualQuestion, knownPropertyValues, desiredProperties);
	}

	class RefreshListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener,
			edu.cmu.cs.stage3.alice.core.event.PropertyListener {
		@Override
		public void objectArrayPropertyChanging(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		}

		@Override
		public void objectArrayPropertyChanged(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
			try {
				if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED) {
					final edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) ev
							.getItem();
					variable.name.addPropertyListener(this);
				} else if (ev
						.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED) {
					final edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) ev
							.getItem();
					variable.name.removePropertyListener(this);
				}
			} catch (final Throwable t) {
				// BIG FAT HACK
			}
			calculateDesiredProperties();
		}

		@Override
		public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}

		@Override
		public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			calculateDesiredProperties();
		}
	}
}
