(function(){
	var editors = WHADL.gui.ns('editors',true),

	Editor = editors.type('Editor', function(edited) {
		var that = null,
		Box = null;
		
		Box = WHADL.gui.require('Box');

		that = new Box();
		
		that.edited = edited;
		
		return that;
	},true);
}());