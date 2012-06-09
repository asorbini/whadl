(function () {
	var editors = WHADL.gui.ns('editors', true),
	
	UnitEditor = editors.type('UnitEditor', function (listED, unit, unitInst) {
		var that = null,
		ListEditor = null,
		Unit = null,
		UnitInstance = null;
		
		WHADL.importNS('client.model');
		
		ListEditor = editors.require('ListEditor', UnitEditor);
		Unit = WHADL.client.model.require('Unit');
		UnitInstance = WHADL.client.model.require('UnitInstance');
		
		that = {};
		
		return that;
	}, true);
}());