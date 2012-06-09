var EditUnitDiv = function(buildDiv,unit,unitNumber,onUpdate, onRemove) {
	this.unit = unit;
		
	var editor = this;
	
	var u = unit.unit;
	
	var baseCost = parseInt(u.cost.value);
	
	var div = $(
		'<div class="edit-unit">'+
		'</div>'
	);
	var content = $('<div class="content"></div>');
	
	var rand=Math.floor(Math.random()*101);
	
	var unitName = 'New ' + u.name+' Unit '+rand;
	
	this.getName = function() {
		return unitName;
	};
	
	var unitTitle = $('<div class="title"><h3>'+unitName+'</h3></div>');
	$('h3',unitTitle).editable({
		onSubmit : function(content) {
			unitName = content.current;
		}
	}).addClass('editable');
	
	var baseInfoList = $('<ul class="base-info"></ul>');		
	var unitTypeLi = $('<li><span class="label">Type</span><span class="value">'+unit.name+'</span></li>');
	
	var slotOptions = new Object();
	
	if (typeof(u.slots) == "object") {
		slotOptions['0'] = u.slots.name;
	} else {
		for (var i = 0; i < u.slots; i++) {
			var slot = u.slots[i];
			slotOptions[i+''] = slot.name;
		}
	}
	var unitSlotLi = $('<li><span class="label">Slot</span><span class="value">'+slotOptions['0']+'</span></li>');
	if (!slotOptions[0] == "NONE") {
		$('span.value',unitSlotLi).editable({
			type : 'select',
			options : slotOptions
		}).addClass('editable');
	}
	
	var totalCostLi = $('<li><span class="label">Unit\s cost</span><span class="value"></span>');
	
	var setCost = function(cost) {
		$('span.value',totalCostLi).text(cost);
	};
	
	var getCost = function() {
		var result = baseCost;
		
		if (compositionDiv) {
			result += compositionDiv.getCost();
		}
		
		/*if (linkedUnitsDiv) {
			result += linkedUnitsDiv.getCost();
		}*/
		
		if (upgradesPatternDiv) {
			result += upgradesPatternDiv.getCost();
		}
		
		return result;
	};
	
	this.getCost = getCost;
	
	var updateCost = function() {
		var cost = getCost();
		//alert("UNIT "+getName()+" : "+cost);
		setCost(cost);
		
		if (onUpdate) onUpdate();
	};
	
	
	//var compositionPatternDiv = new PatternHandlerDiv('Unit\' Composition',unit.composition,'composition');
	
	var compositionDiv = new UnitCompositionDiv(unit,updateCost);
	
	//var linkedUnitsDiv = new PatternHandlerDiv('Linked Units',unit.linked,'linked',updateCost);
	
	var linkedUnitsDiv = new LinkedUnitsDiv(unit.linked,buildDiv);
	
	//var upgradesPatternDiv = new PatternHandlerDiv('Unit\'s Upgrades',unit.upgrades,'upgrades',updateCost);
	var upgradesPatternDiv = new UnitsUpgradesDiv(unit.upgrades,updateCost);
	
	/*var memsDivs = new Array();
	
	for (var i=0; i < unit.members.length; i++) {
		var mem = unit.members[i];
		var memEqDiv = new PatternHandlerDiv('Member '+mem.name+' equipment',mem.equipment,'mem-equip');
		memsDivs[i] = memEqDiv;
	};*/
	
	baseInfoList.append(unitTypeLi);
	baseInfoList.append(unitSlotLi);
	baseInfoList.append(totalCostLi);
	
	div.append(unitTitle);
	
	//baseInfoList.hide();
	content.append(baseInfoList);
	//compositionDiv.div.hide();
	content.append(compositionDiv.div);
	//upgradesPatternDiv.div.hide();
	content.append(upgradesPatternDiv.div);
	//linkedUnitsDiv.div.hide();
	content.append(linkedUnitsDiv.div);
	
	//$('form.maximized > input[name="first"]',upgradesPatternDiv.div).click();
	//$('form.maximized > input[name="first"]',linkedUnitsDiv.div).click();
	
	
	div.append(content);
	
	var ctrlForm = new ControlForm(false,true,['OK','Edit','Delete'],content,new Array(),function(){
		$('div.composition > div.title > form.maximized > input[name="first"]',div).click();
		$('div.upgrades > div.title > form.maximized > input[name="first"]',div).click();
		$('div.linked > div.title > form.maximized > input[name="first"]',div).click();
	},null,function(){
		if (onRemove) onRemove(editor);
	});
	
	unitTitle.append(ctrlForm.form);
	
	updateCost();
	
	this.div = div;
	
	
	this.serialize = function(indent) {
		if (!indent) {
			indent = '';
		}
		
		var result = indent+'unit '+unit.name+' $'+removeSpaces($('h3',unitTitle).text())+' {\n'+
//					indent+'   '+'type '+unit.name+';\n'+
					indent+'   '+'slot '+$('span.value',unitSlotLi).text()+';\n';
					
		var compSer = compositionDiv.compositionEditor.serialize();
		
		if (compSer != '') {
			result += indent+'   '+'composition '+compSer+';\n';
		}
		
		var linkedSer = linkedUnitsDiv.serialize();
		
		if (linkedSer != '') {
			result += indent+'   '+linkedSer+';\n';
		}
		
		var upsSer = upgradesPatternDiv.serialize();
		
		if (upsSer != '') {
			result+= indent+'   '+'upgrades '+upsSer+';\n';
		}
					
		result += compositionDiv.serialize(indent+'   ')+
				  indent+'}';
				
		return result;
	};
	
	this.setUnits = function(units) {
		//alert("EditUnitDiv.setUnits");
		linkedUnitsDiv.update(units);
	};
	
	this.onCreation = function() {
		//$('div.content > :nth-child(n)',div).fadeIn(300);
		
/*		baseInfoList.fadeIn(300);
			compositionDiv.div.delay(100).fadeIn(300)
				upgradesPatternDiv.div.delay(200).fadeIn(300);
					linkedUnitsDiv.div.delay(300).fadeIn(100);*/

	};
	
};