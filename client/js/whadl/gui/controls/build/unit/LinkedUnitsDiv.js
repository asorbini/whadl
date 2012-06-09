var LinkedUnitsDiv = function(pattern,buildDiv) {
	
	var div = $(
		'<div class="linked">'+
			'<div class="title">'+
				'<h3>Linked Units</h3>'+
			'</div>'+
			'<div class="content">'+
			'</div>'+
		'</div>'
	);
	
	var ctrlForm = new ControlForm(false,true,['OK','Edit'],$('div.content',div));
	$('div.title',div).append(ctrlForm.form);
	
	var ChoicesBox = function(buildDiv) {
		var div = $('<div class="choices-box"></div>');
		
		var defaultText = 'Select unit\'s types first.';
		
		var selectDiv = $('<div class="select">'+defaultText+'</div>');
		div.append(selectDiv);
		
		var types = new Object();
		var selected = new Array();
		
		this.getUnits = function() {
			var units = new Array();
			
			for (var i=0; i < buildDiv.units.length; i++) {
				var u = new Object();
				u.name = buildDiv.units[i].getName();
				u.type = buildDiv.units[i].unit.name;
				//alert("U.NAME: "+u.name+" - TYPE: "+u.type);
				units.add(u);
			};
			
			return units;
		};
		
		this.selected = function() {
			return selected.length > 0;
		};
		
		this.setTypes = function(t) {
			types = t;
			this.update();
		};
		
		this.sameChoices = function(oldCh, newCh) {
			if (oldCh == null) return newCh == null;
			
			var tested = new Array();
			
			for (var t in oldCh) {
				var newValues = newCh[t];
				if (!newValues) return false;
				
				if (newValues.length != oldCh[t].length) {
					return false;
				}
				
				for (var i = 0; i < newValues.length; i++) {
					if (!oldCh[t].contains(newValues[i])) {
						return false;
					}
				}
				
				tested.add(t);
			}
			
			for (var t in newCh) {
				if (tested.contains(t)) continue;
				else return false;
			}
			
			return true;
		};
		
		
		this.getPossibleChoices = function() {
			var chosable = new Object();
			
			var units = this.getUnits();
			
			for (var i=0; i < units.length; i++) {
				var u = units[i];
				if (types && types[u.type]) {
					if (!chosable[u.type]) {
						chosable[u.type] = new Array();
					}
					
					chosable[u.type].add(u.name);
				}
			}
			
			return chosable;
		};
		
		var last = null;
		
		this.update = function(){
			
			var selectDivs = new Array();

			var chosable = this.getPossibleChoices();
			
			if (this.sameChoices(last,chosable)) {
				return;
			}
			
			var len = 0;
			for (var n in chosable) {
				if (n) {}
				len++;
			};
			
			if (len == 0) {
				
				for (var t in types) {
					if (t) {}
					len++;
				}
				
				if (len == 0) {
					selectDiv.fadeOut(500,function(){
						selectDiv.empty();
						selectDiv.text("Select units' types first");
						selectDiv.fadeIn(500);
					});
					return;
				} else {
					selectDiv.fadeOut(500,function(){
						selectDiv.empty();
						selectDiv.text("No unit available");
						selectDiv.fadeIn(500);
					});
					return;
				}
			}
						
			selectDiv.fadeOut(500, function(){
				var oldDivs = $('div.choice',selectDiv);
				//alert("OLD DIVS: "+oldDivs.length);
				
				selectDiv.empty();
				
				for (var type in chosable) {
					var units = chosable[type];
					var quantity = types[type];
					//alert(quantity + " - " + type);
				
					var opts = new Object();
					var num = 0;
					//var str = '';
					for (var i = 0; i < units.length; i++) {
						opts[num++] = units[i];
						
						//str += units[i] + ' - ';
					}
				
					for (var k = 0; k < quantity; k++) {
						var typeDiv = $('<div class="choice" tt="'+type+'">SELECT</div>');
						typeDiv.data('opts',opts);
						//alert("OPTS: "+str);
						
						
						typeDiv.editable({
							type : 'select',
							options : opts,
							onEdit : function() {
								typeDiv.addClass('edited');
							},
							onSubmit : function(content) {
								var selectedName = content.current;
								
								if (!selected.contains(selectedName)) {
									selected.add(selectedName);
								}
								
								if (selected.contains(content.previous)) {
									selected.remove(content.previous);
								}
								
								typeDiv.text(selectedName);
							}
						});
						typeDiv.addClass("editable");
						selectDivs.add(typeDiv);
					}
				
				}
			
				//if (selectDivs.length > 0) $('div.select :nth-child(1)').fadeOut(500);
			
				for (var i=0; i < selectDivs.length; i++) {
					var d = selectDivs[i];
					var type = d.attr('tt');
					var opts = d.data('opts');
					d.data('opts',null);
					var existing = null;
					
					for (var k=0; k < oldDivs.length; k++) {
						if (oldDivs[k]) {
							var oldType = $(oldDivs[k]).attr('tt');
							//alert("OLD-DIV-CLASS: "+oldType+" - "+type);
							if (oldType==type) {
								existing = oldDivs[k];
								break;
							}
						} else {
							break;
						}
					}
					
					if (existing != null) {
						var oldSelect = $(existing).text();
						
						//alert("already exist (selected: "+oldSelect+")");
						
						if (oldSelect != "SELECT") {
							var contained = false;
							
							for (var idx in opts){
								if (opts[idx] == oldSelect) {
									contained = true;
									break;
								}
							};
							
							if (contained) {
								d.text(oldSelect);
							} else {
								selected.remove(oldSelect);
							}
						}
					}
					
					selectDiv.append(d);
				}
			
				for (var type in types) {
					var q = types[type];
					var j = 0;
					
					do {
						var el = $('div.'+type+'-'+j,selectDiv);
						if (j >= q) {
							el.fadeOut(500,function(){
								el.remove();
							});
						}
					
						j++;
					
					} while (el.length != 0);
				
				};
				
				selectDiv.fadeIn(500);
			
			});
			
		};
				
		this.div = div;
		
		this.setEditable = function(editable) {
			if (editable) {
				selectDiv.editable('enable');
				selectDiv.addClass('editable');
			} else {
				selectDiv.editable('disable');
				selectDiv.removeClass('editable');
			}
		};
		
		this.serialize = function() {
			var result = '';
			for (var i = 0; i < selected.length; i++) {
				result += '$'+removeSpaces(selected[i])+",";
			}
			
			if (result.length > 0) result = result.substring(0,result.length-1);
			
			return result;
		};
	};
	
	var choicesBox = new ChoicesBox(buildDiv);
	
	var typesEditor = new PatternHandlerDiv('Select type',pattern,'linked-choices',function(valuesMap){
		choicesBox.setTypes(valuesMap);
	});
	
	$('div.content',div).append(typesEditor.div);
	if (!typesEditor.isEmpty()){
		$('div.content',div).append(choicesBox.div);
	} else {
		typesEditor.setTitle("None available");
	}
	
	this.serialize = function() {
		if (!typesEditor.isEmpty() && choicesBox.selected()) {
			return 'linked ('+choicesBox.serialize()+')';
		} else {
			return '';
		}
	};
	
	this.setEditable = function(editable) {
		choicesBox.setEditable(editable);
		if (editable) {
			div.fadeTo(500,1);
		} else {
			div.fadeTo(500,0.5);
		}
	};
	
	this.update = function() {
		choicesBox.update();
	};
	
	this.div = div;
	
	
};