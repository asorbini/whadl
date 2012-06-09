function removeSpaces(str) {
	return str.replace(/\s/g, "");
}

function mergeMaps(map,container) {
	
	var j = container.length;
	
	for (var i = 0; i < map.length; i++){
		container[j++] = map[i];
	}
	
	return container;
}

Array.prototype.getByName = function(name) {
	for (var i = 0; i < this.length; i++) {
		var cur = this[i];
		if (cur.name) {
			if (cur.name == name) {
				return cur;
			}
		}
	}
	
	return null;
};

Array.prototype.remove = function(obj) {
	var idx = 0;
	var found = false;
	
	for (var i=0; i < this.length; i++) {
		if (this[i] == obj) {
			idx = i;
			found = true;
			break;
		}
	}
	
	if (found) {
		for (var i=idx; i < this.length - 1; i++) {
			this[i] = this[i+1];
		};
		return this.pop();
	}
	
	return null;
};

Array.prototype.add = function(obj) {
	this[this.length] = obj;
};

Array.prototype.contains = function(obj) {
	for (var i=0; i < this.length; i++) {
		if (this[i] == obj) {
			return true;
		}
	}
	
	return false;
};


