package org.mentalsmash.whadl.descriptions;

public class BaseDescription implements Description {

	protected String id;
	protected String text;
	
	public BaseDescription(String id, String text) {
		this.id = id;
		this.text = text;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String toString() {
		return this.id+" : \n"+this.text+"\n";
	}
	
	

}
