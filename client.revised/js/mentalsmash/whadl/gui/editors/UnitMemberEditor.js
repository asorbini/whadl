(function () {
	var editors = WHADL.gui.ns('editors', true),
	
	UnitMemberEditor = editors.type('UnitMemberEditor', function (unitED, member, memberInstance) {
		var that = null,
		UnitEditor = null,
		UnitMember = null,
		UnitMemberInstance = null;
		
		WHADL.client.importNS('model');

		UnitEditor = editors.require('UnitEditor');
		UnitMember = WHADL.client.model.require('UnitMember');
		UnitMemberInstance = WHADL.client.model.require('UnitMemberInstance');
		
		that = {};
		return that;
	}, true);
}());

