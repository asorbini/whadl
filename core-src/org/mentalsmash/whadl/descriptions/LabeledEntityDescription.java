package org.mentalsmash.whadl.descriptions;

public class LabeledEntityDescription extends BaseDescription {

	private final String label;
	
	public LabeledEntityDescription(String id, String label, String text) {
		super(id, text);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
