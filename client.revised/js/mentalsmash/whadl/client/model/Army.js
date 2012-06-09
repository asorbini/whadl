(function () {
	var ns = org.mentalsmash.whadl.client.ns('model'),
	
	Army = ns.type('Army', function (json) {
		var that = null,
		i = 0;
		
		that = ns.i('BaseEntity', {
			'name' : json.name,
			'json' : json
		});

		that.units = [];

		if (that.json.units.length) {
			for (i = 0; i < that.json.units.length; i = i + 1) {
				that.units[i] = ns.i('Unit', {
					'json' : that.json.units[i]
				});
			}
		} else {
			that.units[0] = ns.i('Unit', {
				'json' : that.json.units
			});
		}

		that.toString = function (indent, unit) {
			var i = 0,
			result = indent + "ARMY: " + this.name + "\n";

			for (i = 0; i < this.units.length; i = i + 1) {
				result += this.units[i].toString(this.indent(indent, unit));
			}

			result += indent + "  " + "CONDITIONS: " +
						this.json.conditions + "\n";

			return result;
		};

		that.toHTML = function () {
			var i = 0,
			result = '<div class="army" id="' + this.name + '"><h2>' + this.name + '</h2>';

			for (i = 0; i < this.units.length; i = i + 1) {
				result += this.units[i].toHTML();
			}

			if (this.json.conditions !== "true") {
				result += '<div class="conditions"><h3>CONDITIONS</h3><pre>' +
							this.json.conditions + '</pre></div>';
			}

			result += '</div>';

			return result;
		};

		return that;
	});
}());

