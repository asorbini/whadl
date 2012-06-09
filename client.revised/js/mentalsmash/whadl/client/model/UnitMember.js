(function () {
	var model = WHADL.client.ns('model'),
		
	UnitMember = model.type('UnitMember', function (json) {
		var that = null,
		BaseEntity = model.require('BaseEntity', UnitMember),
		Pattern = model.require('Pattern');

		that = new BaseEntity(json.name, json);

		that.equipment = new Pattern(json.equipment);

		that.toString = function (indent, u) {
			var result = indent + "MEMBER: " + this.member.name + "\n";
			result += this.indent(indent, u) + "EQUIPMENT:\n";
			result += this.equipment.toString(this.indent(indent, u), u) + "\n";
			result += this.indent(indent, u) + "CONDITIONS: " +
						this.json.conditions + "\n";
			return result;
		};

		that.toHTML = function () {
			var result = '<div class="unit-member" id="' + this.name + '"><h5>' + this.name + "</h5>";

			result += '<div class="equipment">';
			result += '<h6>EQUIPMENT</h6>';
			result += this.equipment.toHTML();
			result += '</div>';

			if (this.json.conditions !== "true") {
				result += '<div class="conditions"><h6>CONDITIONS</h6><pre>';
				result += this.json.conditions;
				result += '</pre></div>';
			}

			result += '</div>';

			return result;
		};

		return that;
	});
}());