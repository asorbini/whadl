var Army = function(armyObj) {
	
	this.army = armyObj;
	this.units = new Array();
	this.name = armyObj.name;
	
	if (this.army.units.length) {
		for (var i = 0; i < this.army.units.length; i++){
			this.units[i] = new Unit(this.army.units[i]);
		}
	} else {
		this.units[0] = new Unit(this.army.units);
	}
	
	this.toString = function(indent) {
		
		var result = indent+"ARMY: "+this.army.name+"\n";
		
		for (var i = 0; i < this.units.length; i++){
			result += this.units[i].toString(indent+"  ");
		}
		
		result += indent+"  "+"CONDITIONS: "+this.army.conditions+"\n";
		
		return result;
	};
	
	
	this.toHTML = function() {
		var result = '<div class="army" id="'+this.army.name+'"><h2>'+this.army.name+'</h2>';
		
		for (var i = 0; i < this.units.length; i++){
			result += this.units[i].toHTML();
		}
		
		if (this.army.conditions != "true") {
			result += '<div class="conditions"><h3>CONDITIONS</h3><pre>'+this.army.conditions+'</pre></div>';
		}
		
		result += '</div>';
		
		return result;
	};
	
	
	
};

var Unit = function(unitObj) {
	this.unit = unitObj;
	this.members = new Array();
	this.composition = Pattern.fromJSON(unitObj.composition);
	this.linked = Pattern.fromJSON(unitObj.linked);
	this.upgrades = Pattern.fromJSON(unitObj.upgrades);
	this.slots = Pattern.fromJSON(unitObj.slots);
	this.name = unitObj.name;
	
	if (this.unit.members.length) {
		for (var i = 0; i < this.unit.members.length; i++){
			this.members[i] = new UnitMember(this.unit.members[i]);
		}
	} else {
		this.members[0] = new UnitMember(this.unit.members);
	}
	
	
	this.toString = function(indent) {
		var result = indent+"UNIT: "+this.unit.name+"\n";
		
		result += indent+"  "+"SLOTS:\n";
		result += this.slots.toString(indent+"    ")+"\n";
		
		result += indent+"  "+"COMPOSITION:\n";
		result += this.composition.toString(indent+"    ")+"\n";
		
		result += indent+"  "+"LINKED:\n";
		result += this.linked.toString(indent+"    ")+"\n";
		
		for (var i = 0; i < this.members.length; i++){
			result += this.members[i].toString(indent+"  ")+"\n";
		}
		
		result += indent+"  "+"UPGRADES:\n";
		result += this.upgrades.toString(indent+"    ")+"\n";
		
		result += indent+"  "+"CONDITIONS: "+this.unit.conditions+"\n";
		
		return result;
	};
	
	
	this.toHTML = function() {
		var result = '<div class="unit" id="'+this.unit.name+'"><h3>'+this.unit.name+"</h3>";
		
		result += '<div class="slots">';
		result += '<h4>SLOTS</h4>';
		result += this.slots.toHTML();
		result += '</div>';
		
		result += '<div class="composition">';
		result += '<h4>COMPOSITION</h4>';
		result += this.composition.toHTML();
		result += '</div>';
		
		result += '<div class="linked">';
		result += '<h4>LINKED UNIT</h4>';
		result += this.linked.toHTML();
		result += '</div>';
		
		result += '<div class="members">';
		result += '<h4>MEMBERS</h4>';
		for (var i = 0; i < this.members.length; i++){
			result += this.members[i].toHTML();
		}
		result += '</div>';
		
		result += '<div class="upgrades">';
		result += '<h4>UPGRADES</h4>';
		result += this.upgrades.toHTML();
		result += '</div>';
		
		
		if (this.unit.conditions != "true") {
			result += '<div class="conditions"><h4>CONDITIONS</h4><pre>';
			result += this.unit.conditions;
			result += '</pre></div>';
		}
		
		result+='</div>';
		
		return result;
	};
	
};

var UnitMember = function(memberObj) {
	this.member = memberObj;
	this.equipment = Pattern.fromJSON(memberObj.equipment);
	this.name = memberObj.name;
	
	this.toString = function(indent) {

		var result = indent+"MEMBER: "+this.member.name+"\n";
		result += indent+"  "+"EQUIPMENT:\n";
		result += this.equipment.toString(indent+"    ")+"\n";
		result += indent+"  "+"CONDITIONS: "+this.member.conditions+"\n";
		return result;
	};
	
	this.toHTML = function() {
		var result = '<div class="unit-member" id="'+this.member.name+'"><h5>'+this.member.name+"</h5>";

		result += '<div class="equipment">';
		result += '<h6>EQUIPMENT</h6>';
		result += this.equipment.toHTML();
		result += '</div>';
		
		if (this.member.conditions != "true") {
			result += '<div class="conditions"><h6>CONDITIONS</h6><pre>';
			result += this.member.conditions;
			result += '</pre></div>';
		}
		
		result += '</div>';
		
		return result;
	};
};


var Pattern = new Object();

Pattern.fromJSON = function(patternObj) {
	var type = patternObj.type;
	
	if (type == "alt") {
		return new AlternativePattern(patternObj);
	} else if (type == "conjunct") {
		return new ConjunctPattern(patternObj);
	} else if (type == "opt") {
		return new OptionalPattern(patternObj);
	} else if (type == "single") {
		return new SinglePattern(patternObj);
	}
	
	return null;
};



var ConjunctPattern = function(patternObj) {
	this.elements = new Array();
	this.type = 'conjunct';
	
	for (var i = 0; i < patternObj.elements.length; i++){
		this.elements[i] = Pattern.fromJSON(patternObj.elements[i]);
	}
	
	this.toString = function(indent) {
		var result = indent+"SET {\n";

		for (var i=0; i < this.elements.length; i++) {
			result += this.elements[i].toString(indent+"  ");
			if (i < (this.elements.lenght - 1)) {
				result += ",";
			}
			
			result += "\n";
		};

		result += indent+"}";
		
		return result;
	};
	
	this.toHTML = function() {
		var result = '<div class="conjunct-pattern">';

		for (var i=0; i < this.elements.length; i++) {
			result += this.elements[i].toHTML();
		};

		result += '</div>';
		
		return result;
	};
	
	this.toMap = function() {
		var map = new Array();
		
		for (var i = 0; i < this.elements; i++) {
			map = mergeMaps(this.elements[i].toMap(),map);
		}
		
		return map;
	};
	
	this.isComplex = function() {
		return true;
	};
	
};

var OptionalPattern = function(patternObj) {
	this.type = 'opt';
	this.element = Pattern.fromJSON(patternObj.optional);
	
	this.toString = function(indent) {
		var result = indent+"OPTIONAL [\n";
		
		result += this.element.toString(indent+"  ");
		
		result += indent+"]";
		
		return result;
	};
	
	this.toHTML = function() {
		var result = '<div class="optional-pattern">';

		
		result += this.element.toHTML();
		

		result += '</div>';
		
		return result;
	};
	
	this.toMap = function() {
		return this.element.toMap();
	};
	
	this.isComplex = function() {
		return true;
	};
	
};

var AlternativePattern = function(patternObj) {
	this.type = 'alt';
	this.elements = new Array();
	
	for (var i = 0; i < patternObj.elements.length; i++){
		this.elements[i] = Pattern.fromJSON(patternObj.elements[i]);
	}
	
	this.toString = function(indent) {
		var result = indent+"ALTERNATIVES {\n";

		for (var i=0; i < this.elements.length; i++) {
			result += this.elements[i].toString(indent+"  ");
			if (i < (this.elements.lenght - 1)) {
				result += ",";
			}
			
			result += "\n";
		};

		result += indent+"}";
		
		return result;
	};
	
	this.toHTML = function() {
		var result = '<div class="alt-pattern">';

		for (var i=0; i < this.elements.length; i++) {
			result += this.elements[i].toHTML();
		};

		result += '</div>';
		
		return result;
	};
	
	this.toMap = function() {
		var map = new Array();
		
		for (var i = 0; i < this.elements; i++) {
			map = mergeMaps(this.elements[i].toMap(),map);
		}
		
		return map;
	};
	
	this.isComplex = function() {
		return true;
	};
};

var SinglePattern = function(patternObj) {
	this.type = 'single';
	this.name = patternObj.name;
	this.quantity = patternObj.quantity;
	this.cost = patternObj.cost.value;
	
	this.toString = function(indent){
		return indent+this.quantity+" "+this.name+" ["+this.cost+"]";
	};
	
	this.toHTML = function() {
		if (name != "NONE") {
			var result = '<div class="single-pattern"><ul>';

			result += '<li>'+this.name+'</li>';
			result += '<li>'+this.quantity+'</li>';
			result += '<li>'+this.cost+'</li>';

			result += '</ul></div>';
		
			return result;
	    } else {
			return '<div class="empty-pattern">NONE</div>';
		}
	};
	
	this.toMap = function() {
		var map = new Array();
		map[0] = {
			'name' : this.name,
			'quantity' : this.quantity,
			'cost' : this.cost
		};
		
		return map;
	};
	
	this.isComplex = function() {
		return false;
	};
	
};
