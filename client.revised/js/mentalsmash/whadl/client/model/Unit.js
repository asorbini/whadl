(function () {
	var model = WHADL.client.ns('model'),
	
	Unit = model.type('Unit', function (json) {
		var that = null,
		BaseEntity = model.require('BaseEntity'),
		Pattern = model.require('Pattern'),
		UnitMember = model.require('UnitMember'),
		i = 0;

		that = new BaseEntity(json.name, json);

		that.members = [];
		that.composition = new Pattern(json.composition);
		that.linked = new Pattern(json.linked);
		that.upgrades = new Pattern(json.upgrades);
		that.slots = new Pattern(json.slots);

		if (that.json.members.length) {
			for (i = 0; i < that.unit.members.length; i = i + 1) {
				that.members[i] = new UnitMember(that.json.members[i]);
			}
		} else {
			that.members[0] = new UnitMember(that.unit.members);
		}

		that.toString = function (indent, u) {
			var i = 0,
			result = indent + "UNIT: " + this.name + "\n";

			result += this.indent(indent, u) + "SLOTS:\n";
			result += that.slots.toString(this.indent(indent, u), u) + "\n";

			result += this.indent(indent, u) + "COMPOSITION:\n";
			result += this.composition.toString(this.indent(indent, u), u) +
						"\n";

			result += this.indent(indent, u) + "LINKED:\n";
			result += this.linked.toString(this.indent(indent, u), u) + "\n";

			for (i = 0; i < this.members.length; i = i + 1) {
				result += this.members[i].toString(this.indent(indent, u), u) +
							"\n";
			}

			result += this.indent(indent, u) + "UPGRADES:\n";
			result += this.upgrades.toString(this.indent(indent, u), u) + "\n";

			result += this.indent(indent, u) + "CONDITIONS: " +
						this.json.conditions + "\n";

			return result;
		};


		that.toHTML = function () {
			var i = 0,
			result = '<div class="unit" id="' + this.name + '"><h3>' + this.name + "</h3>";

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
			for (i = 0; i < this.members.length; i = i + 1) {
				result += this.members[i].toHTML();
			}
			result += '</div>';

			result += '<div class="upgrades">';
			result += '<h4>UPGRADES</h4>';
			result += this.upgrades.toHTML();
			result += '</div>';


			if (this.json.conditions !== "true") {
				result += '<div class="conditions"><h4>CONDITIONS</h4><pre>';
				result += this.json.conditions;
				result += '</pre></div>';
			}

			result += '</div>';

			return result;
		};

		return that;
	});
}());