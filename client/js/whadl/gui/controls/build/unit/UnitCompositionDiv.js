var UnitCompositionDiv = function(unit,onUpdate) {
	var members = new Object();
	var membersDiv = $('<div class="members scrollable"></div>');
	
	var setCost = function(cost) {
		if (typeof(patternEditor) == "object") {
			patternEditor.setCost(cost);
		}
	};
	
	this.setCost = setCost;
	
	var getCost = function() {
		var result = 0;
		if (typeof(patternEditor) == "object") {
			result += patternEditor.getCost();
		}
		
		for (var membType in members) {
			for (var j=0; j < members[membType].length; j++) {
				result += members[membType][j].getCost();
			};
		};
		
		return result;
	};
	
	this.getCost = getCost;
	
	var updateCost = function() {
		var totalCost = getCost();
		setCost(totalCost);
		
		if (onUpdate) {
			onUpdate();
		}
	};
	
	var update = function(membsMap) {
		var names = new Array();
		
		for (var n in membsMap){
			names.add(n);
		}
		
		
		for (var t in members) {
			if (!names.contains(t)) {
				//alert(t);
				for (var i = 0; i < members[t].length; i++) {
					var el = members[t][i];
					el.div.fadeOut(100,function(){
						el.div.remove();
					});
				}
				
				delete members[t];
			}
		}
		
		
		for (var membName in membsMap) {
			var member = unit.members.getByName(membName);
			var q = membsMap[membName];
			
			if (!members[membName]) {
				members[membName] = new Array();
			}
			
			var curr = members[membName].length;
			var missing = q - curr;
			
			for (var i=0; i < missing; i++) {
				var memberNum = curr+i;
				var membDiv = new EditMemberDiv(member,memberNum,updateCost,membersDiv);
				members[membName][memberNum] = membDiv;
				membersDiv.append(membDiv.div);
			};
			
			if (missing < 0 && curr != 0) {
				for (var i=1; i <= -missing; i++) {
					membDiv = members[membName][members[membName].length - i];
					membDiv.div.fadeOut(500,function(){
						members[membName].remove(membDiv);
						membDiv.div.remove();
					});
				};
			}
		};
		
		
		$('div.edit-member > div.title input[name="first"]',membersDiv).each(function(i,el){
			el = $(el);
			el.click(function(e){
//				membersDiv.scrollTo(el,500);
			});
		});
		
		/*$('div.edit-member > div.content  input[name="first"]',membersDiv).each(function(i,el){
			el = $(el);
			el.click(function(e){
				membersDiv.scrollTo(el,500);
			});
		});*/
		
		updateCost();
	};
	
	var patternEditor = new PatternHandlerDiv('Members Selection',unit.composition,'composition-editor',update);
	
	this.compositionEditor = patternEditor;
	
	var div = $('<div class="composition"></div>');
	var divTitle = $('<div class="title"><h3>Composition</h3></div>');
	div.append(divTitle);
	div.append(membersDiv);
	
	if ($('div.members > :nth-child(1) ',div).length > 0) {
		patternEditor.div.insertBefore($('div.members > :nth-child(1)',div));
	} else {
		membersDiv.append(patternEditor.div);
	}

	var ctrlForm = new ControlForm(false,false,['OK','Edit'],membersDiv,[],function(){
		$('form.maximized > input[value="OK"]',membersDiv).click();
	});
	
	divTitle.append(ctrlForm.form);
	
	
	this.serialize = function(indent) {
		if (!indent) {
			indent = '';
		}
		
		var result = '';
		
		for (var membType in members) {
			//alert(membType + " . "+ members[membType].length)
			
			for (var i = 0; i < members[membType].length; i++){
				var serial = members[membType][i].serialize(indent);
				//alert(serial);
				if (serial != '') {
					result += indent+serial+"\n";
				}
			}
		};
		
		//alert(result);
		
		return result;
	};
	
	this.div = div;
};