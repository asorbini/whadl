(function () {
	var model = WHADL.client.ns('model'),
	
	UnitMemberInstance = model.type('UnitMemberInstance', function (member, name) {
		var that = null,
		BaseEntity = model.require('BaseEntity', UnitMemberInstance);

		that = new BaseEntity(name || member.name + " Member");
		
		return that;
	});
}());