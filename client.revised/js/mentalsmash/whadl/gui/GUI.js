(function () {
	var GUI = org.mentalsmash.whadl.gui.type('GUI', function (id, container) {
		var that = null,
		editors = null,
		WhadlClient = null,
		Army = null,
		ArmyList = null,
		Box = null,
		ListEditor = null,
		whadl = null,
		listEditors = null;
		
		org.mentalsmash.whadl.importNS('client');
		org.mentalsmash.whadl.importNS('client.model');
		editors = org.mentalsmash.whadl.gui.importNS('editors', gui);
		
		WhadlClient = WHADL.client.require('WhadlClient');
		Army = WHADL.client.model.require('Army');
		ArmyList = WHADL.client.model.require('ArmyList');
		Box = gui.require('Box', GUI);
		ListEditor = editors.require('ListEditor', GUI);

		that = new Box({
			id : id,
			classes : ['whadl-gui'],
			menu : true,
			title : {
				h : 'h1'
			}
		});
		
		whadl = new WhadlClient();
		listEditors = [];
		
		that.addListEditor = function (list) {
			var editor = listEditors.getByAttribute('edited', list);
			
			if (!editor) {
				editor = new ListEditor(list);
				listEditors.add(editor);
			}
		};
		
		that.removeListEditor = function (list) {
			var editor = listEditors.getByAttribute('edited', list);
			
			if (!editor) {
				listEditors.add(editor);
			}
		};
		
		that.create = function () {
			
		};
		
		that.start = function () {
			
		};
		
		that.pause = function () {
			
		};
		
		that.stop = function () {
			
		};

		return that;
	});
	
}());