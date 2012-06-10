package org.mentalsmash.whadl.model.patterns;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class NoMatchException extends Exception {
	private static final long serialVersionUID = -3278347402140527191L;
	private final Pattern ref;
	private final Map<String, Integer> elements;
	
	public NoMatchException(Pattern ref, Map<String, Integer> elements){
		super("Cannot match "+elements+" with "+ref);
		
		this.ref = ref;
		this.elements = elements;
	}

	public Pattern getRef() {
		return ref;
	}

	public Map<String, Integer> getElements() {
		return elements;
	}
}
