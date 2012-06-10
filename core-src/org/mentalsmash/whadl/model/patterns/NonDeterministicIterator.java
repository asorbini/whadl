package org.mentalsmash.whadl.model.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class NonDeterministicIterator implements Iterator<Pattern> {

	private final Iterator<Pattern> it;
	
	public NonDeterministicIterator(MultiplePattern p){
//		ArrayList<Pattern> values = new ArrayList<Pattern>();
//		ArrayList<Integer> used = new ArrayList<Integer>();
//		Random random =new Random();
//		while (values.size() < patterns.length) {
//			int randInt = random.nextInt(patterns.length);
//			if (used.contains(randInt)){
//				continue;
//			}
//			Pattern pat = (Pattern) patterns[randInt];
//			values.add(pat);
//			used.add(randInt);
//		}
//		this.it = values.iterator();
		
		ArrayList<Pattern> list = new ArrayList<Pattern>(p);
		Collections.shuffle(list);
		
		this.it = list.iterator();
		
		
//		System.err.println("NEW ITERATOR "+n+" : "+values);
		Collection<Collection<Pattern>> seq = sequences.get(p);
		if (seq == null) {
			seq = new ArrayList<Collection<Pattern>>();
			sequences.put(p, seq);
		}
		seq.add(list);
		
	}
	
	
	public static Map<MultiplePattern, Collection<Collection<Pattern>>> sequences = new HashMap<MultiplePattern, Collection<Collection<Pattern>>>();
	
	private final int n = (num++);
	
	private static int num = 0;
	
	
	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public Pattern next() {
		return it.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
