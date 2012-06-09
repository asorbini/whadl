(function () {
	var model = WHADL.client.ns('model'),
	
	UnitInstance = model.type('UnitInstance', function (unit, name) {
		var that = null,
		BaseEntity = model.require('BaseEntity');

		that = new BaseEntity(name || unit.name + " Unit");
		return that;
	});
}());