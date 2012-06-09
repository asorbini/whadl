(function () {
	var model = WHADL.client.ns('model'),
	
	ArmyList = model.type('ArmyList', function (army, name) {
		var that = null,
		BaseEntity = model.require('BaseEntity', ArmyList);
		
		that = new BaseEntity(name || army.name + " List");

		return that;
	});
	
}());
