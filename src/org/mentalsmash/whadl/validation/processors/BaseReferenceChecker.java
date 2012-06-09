package org.mentalsmash.whadl.validation.processors;

import java.util.Map;

import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.patterns.MultiplePattern;
import org.mentalsmash.whadl.model.patterns.OptionalPattern;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.SinglePattern;
import org.mentalsmash.whadl.validation.EntityContext;
import org.mentalsmash.whadl.validation.UnknownReferenceException;

public class BaseReferenceChecker {
	protected final EntityContext context;

	public BaseReferenceChecker(EntityContext context) {
		this.context = context;
	}

	protected void error(String reference, String where)
			throws UnknownReferenceException {
		throw new UnknownReferenceException(reference, where);
	}
	
	public boolean check(Pattern p, Entity ctxEnt) throws UnknownReferenceException {
		if (p instanceof MultiplePattern) {
			return this.check((MultiplePattern) p,ctxEnt);
		} else if (p instanceof OptionalPattern) {
			return this.check((OptionalPattern) p,ctxEnt);
		} else if (p instanceof SinglePattern){
			return this.check((SinglePattern) p,ctxEnt);
		} else {
			return true;
		}
	}

	public boolean check(MultiplePattern p, Entity ctxEnt) throws UnknownReferenceException {
		if (p == null) {
			error("null", "MultiplePattern");
		}

		for (Pattern pattern : p) {
			this.check(pattern,ctxEnt);
		}

		return true;
	}

	public boolean check(SinglePattern p,Entity ctxEnt) throws UnknownReferenceException {
		if (p == null) {
			error("null", "SinglePattern");
		}

		p.startEvaluation();
		Map<String, Integer> values = p.toMap();
		p.stopEvaluation();
		
		for (String key : values.keySet()) {
			if (!this.context.getSubContext(ctxEnt).contains(key)) {
				error(key, "SinglePattern " + p);
			}
		}

		return true;
	}

	public boolean check(OptionalPattern p,Entity ctxEnt) throws UnknownReferenceException {
		if (p == null) {
			error("null", "OptionalPattern");
		}

		return this.check(p.getOptionalPattern(),ctxEnt);
	}
}
