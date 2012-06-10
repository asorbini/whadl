package org.mentalsmash.whadl.model.patterns;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PatternContentMap implements Map<String, Integer> {

	private final Map<String, Integer> content;

	public PatternContentMap(Map<String, Integer> content) {
		this.content = content;
	}

	@Override
	public void clear() {
		this.content.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.content.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.content.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, Integer>> entrySet() {
		return this.content.entrySet();
	}

	@Override
	public Integer get(Object key) {
		return this.content.get(key);
	}

	@Override
	public boolean isEmpty() {
		return this.content.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return this.content.keySet();
	}

	@Override
	public Integer put(String key, Integer value) {
		return this.content.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Integer> t) {
		this.content.putAll(t);

	}

	@Override
	public Integer remove(Object key) {
		return this.content.remove(key);
	}

	@Override
	public int size() {
		return this.content.size();
	}

	@Override
	public Collection<Integer> values() {
		return this.content.values();
	}
	
	
	public int getContentSize() {
		int result = 0;
		
		for (int val : this.values()) {
			result += val;
		}
		
		return result;
	}
	
	@Override
	public String toString(){
		return this.content.toString();
	}
	
	@Override
	public int hashCode(){
		return this.content.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		return this.content.equals(o);
	}

}
