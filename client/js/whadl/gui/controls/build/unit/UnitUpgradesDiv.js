var UnitsUpgradesDiv = function(pattern,updateCostFunction) {
	
	var div = $('<div class="upgrades"></div>');
	var title = $('<div class="title"><h3>Upgrades</h3></div>');
	var content = $('<div class="content"></div>');
	div.append(title);
	div.append(content);
	var ctrlForm = new ControlForm(false,false,["OK","Edit"],content);
	title.append(ctrlForm.form);
	
	var pattEditor = new PatternHandlerDiv('Select',pattern,null,updateCostFunction);
	content.append(pattEditor.div);
	
	if (pattEditor.isEmpty()) {
		pattEditor.setTitle("None available");
	}
	
	this.getCost = function(){
		return pattEditor.getCost();
	};
	
	this.serialize = function(){
		return pattEditor.serialize();
	};
	
	this.div = div;
	
};