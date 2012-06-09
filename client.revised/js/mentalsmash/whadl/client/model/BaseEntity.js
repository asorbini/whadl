(function () {
	var ns = org.mentalsmash.whadl.client.ns('model'),
	
	BaseEntity = ns.type('BaseEntity', function (args) {
		var that = args.that || {};
		
		that = org.mentalsmash.i('Observable',{
			'that' : that
		});
		
		that.name = args.name || "NO_NAME";
		
		if (args.json) {
			that.json = args.json;
		}
		
		that.toString = function (indent) {
			indent = this.indent(indent, "");
			return indent + "ENTITY_" + this.name;
		};
		
		that.indent = function (str, unit) {
			str = (typeof str === 'string') ? str : "";
			unit = (typeof unit === 'string') ? unit : "  ";
			return str + unit;
		};
		
		return that;
	}, true);
}());