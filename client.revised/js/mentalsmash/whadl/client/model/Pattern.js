(function () {
	var ns = org.mentalsmash.whadl.client.ns('model'),
	
	ConjunctPattern = ns.type('ConjuctPattern', function (args) {
		var i = 0,
		that = args.that,
		json = args.json;

		that.elements = [];

		for (i = 0; i < json.elements.length; i = i + 1) {
			this.elements[i] = ns.i('Pattern', {
				'json' : json.elements[i]
			});
		}

		that.toString = function (indent, u) {
			var i = 0,
			result = indent + "SET {\n";

			for (i = 0; i < this.elements.length; i = i + 1) {
				result += this.elements[i].toString(this.indent(indent, u));
				if (i < (this.elements.lenght - 1)) {
					result += ",";
				}

				result += "\n";
			}

			result += indent + "}";

			return result;
		};

		that.toHTML = function () {
			var i = 0,
			result = '<div class="conjunct-pattern">';

			for (i = 0; i < this.elements.length; i = i + 1) {
				result += this.elements[i].toHTML();
			}

			result += '</div>';

			return result;
		};

		that.toMap = function () {
			var i = 0,
			map = [];

			for (i = 0; i < this.elements; i = i + 1) {
				map = mergeMaps(this.elements[i].toMap(), map);
			}

			return map;
		};

		that.isComplex = function () {
			return true;
		};

		return that;
	}, true),
	
	AlternativePattern = model.type('AlternativePattern', function (args) {
		var	i = 0,
		that = args.that,
		json = args.json;

		this.elements = [];

		for (i = 0; i < json.elements.length; i = i + 1) {
			this.elements[i] = ns.i('Pattern', {
				'json' : json.elements[i]
			});
		}

		that.toString = function (indent, u) {
			var result = indent + "ALTERNATIVES {\n",
			i = 0;

			for (i = 0; i < this.elements.length; i = i + 1) {
				result += this.elements[i].toString(this.indent(indent, u));
				if (i < (this.elements.lenght - 1)) {
					result += ",";
				}

				result += "\n";
			}

			result += indent + "}";

			return result;
		};

		that.toHTML = function () {
			var result = '<div class="alt-pattern">',
			i = 0;

			for (i = 0; i < this.elements.length; i = i + 1) {
				result += this.elements[i].toHTML();
			}

			result += '</div>';

			return result;
		};

		that.toMap = function () {
			var map = [],
			i = 0;

			for (i = 0; i < this.elements; i = i + 1) {
				map = mergeMaps(this.elements[i].toMap(), map);
			}

			return map;
		};

		that.isComplex = function () {
			return true;
		};

		return that;
	}, true),
	
	OptionalPattern = model.type('OptionalPattern', function (args) {
		var that = args.that,
		json = args.json;

		that.element = ns.i('Pattern', {
			'json' : json.optional
		});

		that.toString = function (indent, u) {
			var result = indent + "OPTIONAL [\n";

			result += this.element.toString(this.indent(indent, u));

			result += indent + "]";

			return result;
		};

		that.toHTML = function () {
			var result = '<div class="optional-pattern">';


			result += this.element.toHTML();


			result += '</div>';

			return result;
		};

		that.toMap = function () {
			return this.element.toMap();
		};

		that.isComplex = function () {
			return true;
		};

		return that;
	}, true),
	
	SinglePattern = model.type('SinglePattern', function (args) {
		var that = args.that,
		json = args.json;
		
		that.quantity = json.quantity;
		that.cost = json.cost.value;

		that.toString = function (indent) {
			return indent + this.quantity + " " + this.name + " [" + this.cost + "]";
		};

		that.toHTML = function () {
			if (this.name !== "NONE") {
				var result = '<div class="single-pattern"><ul>';

				result += '<li>' + this.name + '</li>';
				result += '<li>' + this.quantity + '</li>';
				result += '<li>' + this.cost + '</li>';

				result += '</ul></div>';

				return result;
		    } else {
				return '<div class="empty-pattern">NONE</div>';
			}
		};

		that.toMap = function () {
			var map = [{
				'name' : this.name,
				'quantity' : this.quantity,
				'cost' : this.cost
			}];
			return map;
		};

		that.isComplex = function () {
			return false;
		};

		return that;
	}, true),
	
	Pattern = ns.type('Pattern', function (args) {
		var that = args.that || {},
		json = args.json;
		
		selected = false;

		that = ns.i('BaseEntity', {
			'name' : json.name || "UNAMED_PATTERN",
			'json' : json,
			'that' : that
		});
		
		that.type = json.type || "NONE";

		
		that.select = function () {
			selected = true;
		};
		that.unselect = function () {
			selected = false;
		};
		that.selected = function () {
			return selected;
		};

		if (that.type === "alt") {
			return ns.i('AlternativePattern', {
				'json' : json,
				'that' : that
			});
		} else if (that.type === "conjunct") {
			return ns.i('ConjunctPattern', {
				'json' : json,
				'that' : that
			});
		} else if (that.type === "opt") {
			return ns.i('OptionalPattern', {
				'json' : json,
				'that' : that
			});
		} else if (that.type === "single") {
			return ns.i('SinglePattern', {
				'json' : json,
				'that' : that
			});
		} else {
			throw {
				'name' : 'WhadlException',
				'message' : 'Unknown type of pattern found: ' + that.type
			};
		}
	});
}());