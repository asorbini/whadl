var PatternRenderer = {
	
	render : function(pattern) {
		var result = null;
		
		if (pattern.type == 'conjunct') {
			result = this.renderConjunctPattern(pattern);
		} else if (pattern.type == 'alt') {
			result = this.renderAlternativePattern(pattern);
		} else 	if (pattern.type == 'opt') {
			result = this.renderOptionalPattern(pattern);
		} else {
			result = this.renderSimplePattern(pattern);
		}
		
		return result;
	},
	
	renderConjunctPattern : function(pattern) {
		return new ConjuctPatternDiv(pattern);
	},
	
	renderAlternativePattern : function(pattern) {
		return new AlternativePatternDiv(pattern);
	},
	
	renderOptionalPattern : function(pattern) {
		return new OptionalPatternDiv(pattern);
	},
	
	renderSimplePattern : function(pattern) {
		return new SimplePatternDiv(pattern);
	}
};

var SimplePatternDiv = function(pattern) {
	
	this.pattern = pattern;
	this.name = pattern.name;
	
	var div = $('<div class="simple-pattern"></label>');
	
	div.html('<span class="name">'+pattern.name+"</span>");
	var quantityBox = $('<ul class="quantity-selection"></ul>');
	var qLi = $('<li class="selected-quantity"></li>');
	var costLi = $('<li class="cost"></li>');
	quantityBox.append(qLi);
	quantityBox.append(costLi);
	div.append(quantityBox);
	
	var maxQ = pattern.quantity;
	var cost = pattern.cost;
	
	this.maxQuantity = maxQ;
	this.cost = cost;
	
	var qOptions = new Array();
	
	var currentQuantity = 1;
	
	this.setCurrentQuantity = function(quantity) {
		currentQuantity = quantity;
	};
	this.getCurrentQuantity = function() {
		return currentQuantity;
	};
	
	var quantSetter = this.setCurrentQuantity;
	
	for (var i = 1; i <= maxQ; i++) {
		qOptions[i-1] = i+'';
	}
	
	this.currentQuantity = 1;
	
	qLi.text(1);
	qLi.editable({
		type: 'select',
		options: qOptions,
		onEdit : function(content) {
			qLi.addClass('edited');
		},
		onSubmit: function(content) {
			var q = parseInt(content.current);
			costLi.text(q * cost);
			quantSetter(q);
			qLi.removeClass('edited');
		}
	});
	
	qLi.addClass('editable');
	qLi.text(1);
	costLi.text(cost);
	
	this.setEditable = function(editable) {
		if (editable) {
			if (qLi.data('editable.options')){
				qLi.editable('enable');
				qLi.addClass('editable');
			}
			div.fadeTo(500,1);
		} else {
			if (qLi.data('editable.options')) {
				qLi.editable('disable');
			}
			qLi.removeClass('editable');
			div.fadeTo(500,0.5);
		}

	};
		
	this.setMandatory = function(mandatory) {
		if (mandatory) {
			qLi.text(maxQ);
			costLi.text(cost*maxQ);
			quantSetter(maxQ);
			if (qLi.data('editable.options')) {
				qLi.editable('destroy');
			}
			qLi.removeClass('editable');
		} else if (!qLi.hasClass('editable')){
			qLi.text(1);
			costLi.text(cost);
			quantSetter(1);
			qLi.editable({
				type: 'select',
				onEdit : function(content) {
					qLi.addClass('edited');
				},
				options: qOptions,
				onSubmit: function(content) {
					var q = parseInt(content.current);
					costLi.text(q * cost);
					qLi.removeClass('edited');
				}
			});
			qLi.addClass('editable');
		}
	};

	this.div = div;
	this.div.addClass('pattern');
	
	this.getCost = function() {
		return this.getCurrentQuantity() * this.cost;
	};
	
	this.summarize = function() {
		var result = new Array();
		
		result[0] = $(
			'<span class="quantity">'+
				this.getCurrentQuantity()+
			'</span>'+
			'<span class="name">'+
				this.name+
			'</span>'
		);
		
		return result;
	};
	
	this.toMap = function() {
		var map = new Object();
		if (this.name == 'NONE') return map;
		map[this.name] = this.getCurrentQuantity();
		return map;
	};
	
	this.serialize = function() {
		if (this.name == 'NONE' || this.getCurrentQuantity() == 0) return '';
		return ((this.getCurrentQuantity()==1)?'':this.getCurrentQuantity()+' ')+this.name;
	};
};

var OptionalPatternDiv = function(pattern) {
	var div = $('<div class="opt-pattern"></div>');
	
	var inner = PatternRenderer.render(pattern.element);
		
	var check = $('<input type="checkbox"/>');
	
	check.click(function(){
		if (div.hasClass('selected')) {
			inner.div.fadeTo(500,0.5);
			inner.setEditable(false);
		} else {
			inner.div.fadeTo(500,1);
			inner.setEditable(true);
		}
		div.toggleClass('selected');
		div.toggleClass('unselected');
	});
	
	div.addClass('unselected');
	
	div.append($('<div class="check"></div>').append(check));
	div.append(inner.div);
	
	inner.div.fadeTo(0,0.5);
	inner.setEditable(false);
	
	this.setEditable = function(editable) {
		if (div.hasClass('selected')){
			inner.setEditable(editable);
		}
		if (editable) {
			check.removeAttr('disabled');
		} else {
			check.attr('disabled',true);
		}
	};
	
	this.div = div;
	this.div.addClass('pattern');
	
	this.getCost = function() {
		if (div.hasClass('selected')) {
			return inner.getCost();
		} else {
			return 0;
		}
	};
	
	this.summarize = function() {
		if (div.hasClass('selected')){
			return inner.summarize();
		} else {
			return new Array();
		}
	};
	
	this.toMap = function() {
		if (div.hasClass('selected')){
			return inner.toMap();
		} else {
			return new Object();
		}
	};
	
	this.serialize = function() {
		if (div.hasClass('selected')){
			return inner.serialize();
		} else {
			return '';
		}
	};
};

var ConjuctPatternDiv = function(pattern) {
	this.elements = new Array();
	this.pattern = pattern;
	
	var div = $('<div class="conjunct-pattern"></div>');
	for (var i = 0; i < pattern.elements.length; i++) {
		var element = PatternRenderer.render(pattern.elements[i]);
		div.append(element.div);
		if (element.setMandatory) {
			element.setMandatory(true);
		}
		this.elements[i] = element;
	}
		
	this.setEditable = function(editable) {
		for (var i=0; i < this.elements.length; i++) {
			this.elements[i].setEditable(editable);
		};
		
		if (editable) {
			div.fadeTo(500,1);
		} else {
			div.fadeTo(500,0.5);
		}
	};
	this.div = div;
	this.div.addClass('pattern');
	
	this.getCost = function() {
		var result = 0;
		
		for (var i=0; i < this.elements.length; i++) {
			result += this.elements[i].getCost();
		};
		
		return result;
	};
	
	this.summarize = function() {
		var result = new Array();
		var cur = 0;
		
		for (var i=0; i < this.elements.length; i++) {
			var summary = this.elements[i].summarize();
			for (var j=0; j < summary.length; j++) {
				result[cur++] = summary[j];
			};
		};
		
		var keyValues = new Object();
		for (var i=0; i < result.length; i++) {
			var res = result[i];
			var name = $(res[1]).text();
			var q = parseInt($(res[0]).text());
			if (keyValues[name]) {
				keyValues[name] += q;
			} else {
				keyValues[name] = q;
			}
		};
		
		result = new Array();
		
		var i = 0;
		for (var key in keyValues) {
			var q = keyValues[key];
			var elStr = '<span class="quantity">'+q+'</span><span class="name">'+key+'</span>';
			var item = $(elStr);
			result[i++] = item;
		}		
		return result;
	};
	
	this.toMap = function() {
		var map = new Object();
		
		for (var i=0; i < this.elements.length; i++) {
			var res = this.elements[i].toMap();
			for (var key in res) {
				if (!map[key]) {
					map[key] = 0;
				}
				map[key] += res[key];
			};
		};
		
		return map;
	};
	
	this.serialize = function() {
		var result = '(';
		
		for (var i=0; i < this.elements.length; i++) {
			var ser = this.elements[i].serialize();
			if (ser != '') {
				result+=ser;
				if (i != (this.elements.length - 1)) {
					result+=",";
				}
			}
		};
		
		result+=')';
		
		if (result == "()") {
			return '';
		} else {
			return result;
		}
	};
};

var AlternativePatternDiv = function(pattern) {
	this.pattern = pattern;
	
	var container = this;
	
	var PatternChoice = function(index, choice) {
		var choiceDiv = $(
			'<div class="pattern-choice" id="choice-'+i+'">'+
				'<div class="button">'+
					'<input name="choice" type="radio" />'+
				'</div>'+
			'</div>');	
		
		var inner = PatternRenderer.render(choice);
		
		choiceDiv.append(inner.div);
		
		if (inner.setMandatory) {
			inner.setMandatory(true);
		}
		
		var radio = $('input:radio',choiceDiv);
		this.radio = radio;
		
		radio.click(function(){
			container.setSelected(index);
		});
		
		var setEditable = function(editable) {
			inner.setEditable(editable);
		};
		
		setEditable(false);
		
		this.setEditable = setEditable;
		choiceDiv.addClass('not-chosen');
		this.div = choiceDiv;
		
		
		this.getCost = function() {
			return inner.getCost();
		};
		
		this.summarize = function() {
			return inner.summarize();
		};
		
		this.toMap = function() {
			return inner.toMap();
		};
		
		this.serialize = function() {
			return inner.serialize();
		};
		
	};
	
	var ChoicesBox = function(pattern) {
		var choices = new Object();
		var current = null;
		
		var div = $('<div class="pattern choices-box"></div>');
				
		var options = new Object();
		
		for (var i = 0; i < pattern.elements.length; i++) {
			var el = pattern.elements[i];
			choices[el.name] = PatternRenderer.render(el);
			options[i] = el.name;
		}
		
		var selectDiv = $('<div class="select editable">Select</div>');
		
		selectDiv.editable({
			type : 'select',
			options : options,
			onEdit : function() {
				selectDiv.addClass('edited');
			},
			onSubmit : function(content) {
				var selectedName = content.current;
				var selected = choices[selectedName];
				var callback = function(){
					if (current) current.div.remove();
					selected.div.fadeTo(0,0);
					div.append(selected.div);
					current = selected;
					selected.div.fadeTo(500,1);
					container.setSelected();
					selectDiv.text("Change");
					if (current.setMandatory) {
						current.setMandatory(true);
					}
				};
				
				if (current != null) {
					current.div.fadeTo(500,0,callback);
				} else {
					callback();
				}
				
				selectDiv.removeClass('edited');
			}
		});
				
		div.append(selectDiv);
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
		
		this.getCost = function() {
			if (current) {
				return current.getCost();
			} else {
				return 0;
			}
		};
		
		this.summarize = function() {
			if (current) {
				return current.summarize();
			} else {
				return new Array();
			}
		};
		
		this.toMap = function() {
			if (current) {
				return current.toMap();
			} else {
				return new Object();
			}
		};
		
		this.serialize = function() {
			if (current) {
				return current.serialize();
			} else {
				return '';
			}
		};
	};
	
	
	var div = $('<div class="alt-pattern"></div>');
	this.div = div;
	this.choices = new Array();
	
	var complex = false;
	for (var i = 0; i < pattern.elements.length; i++) {
		complex = complex || pattern.elements[i].isComplex();
	}
	this.complex = complex;
	
	if (complex) {
		div.addClass('complex');
		for (var i = 0; i < pattern.elements.length; i++) {
			var choice = new PatternChoice(i,pattern.elements[i]);
			this.choices[i] = choice;
			this.div.append(choice.div);
		}
	} else {
		this.choicesBox = new ChoicesBox(pattern);
		this.div.append(this.choicesBox.div);
	}
	
	this.setEditable = function(editable) {
		if (complex) {
			for (var i = 0; i < this.choices.length; i++){
				this.choices[i].setEditable(editable);
				if (editable) {
					this.choices[i].radio.removeAttr('disabled');
				} else {
					this.choices[i].radio.attr('disabled',true);
				}
			}
		} else {
			this.choicesBox.setEditable(editable);
		}
	};
	
	this.setSelected = function(which) {
		
		if (complex) {
			for (var i=0; i < this.choices.length; i++) {
				if (i == which) {
					this.choices[i].setEditable(true);
					this.choices[i].div.addClass('chosen');
					this.choices[i].div.removeClass('not-chosen');
				} else {
					this.choices[i].setEditable(false);
					this.choices[i].div.addClass('not-chosen');
					this.choices[i].div.removeClass('chosen');
				}
			} 
		} else {
			this.div.removeClass('unselected');
		}
	};
	
	if (!complex) div.addClass('unselected');
	
	this.div.addClass('pattern');
	
	
	this.getCost = function() {
		if (complex) {
			for (var i=0; i < this.choices.length; i++) {
				if (this.choices[i].div.hasClass('chosen')) {
					return this.choices[i].getCost();
				}
			};
			
			return 0;
		} else {
			return this.choicesBox.getCost();
		}
	};
	
	this.summarize = function() {
		if (complex) {
			for (var i=0; i < this.choices.length; i++) {
				var ch = this.choices[i];
				if (ch.div.hasClass('chosen')) {
					return ch.summarize();
				}
			};
			
			return new Array();
			
		} else {
			
			return this.choicesBox.summarize();
			
		}
	};
	
	this.toMap = function() {
		if (complex) {
			for (var i=0; i < this.choices.length; i++) {
				if (this.choices[i].div.hasClass('chosen')) {
					return this.choices[i].toMap();
				}
			};
			
			return new Object();
		} else {
			return this.choicesBox.toMap();
		}
	};
	
	this.serialize = function() {
		if (comples) {
			for (var i=0; i < this.choices.length; i++) {
				if (this.choices[i].div.hasClass('chosen')) {
					return this.choices[i].serialize();
				}
			};
			
			return '';
		} else {
			return this.choicesBox.serialize();
		}
	};
	
};