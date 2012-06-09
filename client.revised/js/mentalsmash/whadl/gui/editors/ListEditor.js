(function () {
	var ns = WHADL.gui.ns('editors', true),

	/*** org.mentalsmash.whadl.gui.editors.ListEditor ***
		
		args = {
			name : 'string',
			list : org.mentalsmash.whadl.client.model.ArmyList
		}
		
	***/
	ListEditor = ns.type('ListEditor', function (args) {
		var that = null,
		Army = null,
		ArmyList = null;
		
		org.mentalsmash.whadl.require('client.model');
		Army = org.mentalsmash.whadl.client.model.require('Army');
		ArmyList = org.mentalsmash.whadl.client.model.require('ArmyList');
		
		that = {};
		return that;
	});
}());