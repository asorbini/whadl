"use strict";

if (typeof Object.create !== 'function') {
	Object.create = function (o) {
    	function F() {}
    	F.prototype = o || Object.prototype;
    	return new F();
	};
}

if (typeof Array.prototype.indexOf !== 'function') {
	Array.prototype.indexOf = function (o) {
		var i = 0;
		
		for (; i < this.length; i = i + 1) {
			if (this[i] === o) {
				return i;
			}
		}
		return -1;
	};
}

Array.prototype.contains = function (o) {
	return (this.indexOf(o) > -1);
};

Array.prototype.getByAttribute = function (attr, value) {
	var el, i;
	
	for (i = 0; i < this.length; i = i + 1) {
		el = this[i];
		if (el[attr]) {
			if (el[attr] === value) {
				return el;
			}
		}
	}
	
	return null;
};

Array.prototype.add = function (el) {
	this[this.length] = el;
};

Array.prototype.remove = function (el) {
	var i = this.indexOf(el);
	
	if (i === -1) {
		return false;
	}
	
	for (;i < this.length - 1; i = i + 1) {
		this[i] = this[i + 1];
	}
	
	this.pop();
	
	return true;
};

Array.prototype.removeByAttribute = function (attr, value) {
	var el = this.getByAttribute(attr, value);
	if (el) {
		return this.remove(el);
	} else {
		return false;
	}
};