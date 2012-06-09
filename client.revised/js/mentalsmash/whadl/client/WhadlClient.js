(function () {
	var client = WHADL.ns('client'),
	
	WhadlClient = client.type('WhadlClient', function () {
		var that = null,
		Army = null,
		ArmyList = null,
		armies = [],
		listeners = [],
		stateChanged = null;
		
		client.importNS('model');
		
		Army = client.model.require('Army');
		ArmyList = client.model.require('ArmyList');

		that = {};

		that.states = {
			loaded : 'LOADED',
			validated : 'VALIDATED'
		};

		stateChanged = function (event) {
			var i = 0;
			
			for (i = 0; i < listeners.length; i = i + 1) {
				listeners[i].event(event, that);
			}
		};

		that.listener = function (l) {
			if (!listeners.contains(l)) {
				listeners.add(l);
			}
		};

		that.army = function (a) {
			if (!armies.getByAttribute('name', a.name)) {
				armies.add(a);
			}
		};

		that.load = function () {
			jQuery.ajax({
				url : '/whadl/armies',
				dataType : 'json',
				success : function (data, textStatus, xmlHttpRequest) {				
					var i = 0,
					a = null;

					for (i = 0; i < data.length; i = i + 1) {
						a = new Army(data[i]);
						that.army(a);
					}

					that.stateChanged({
						'state' : that.states.loaded
					});
				}
			});
		};

		that.validate = function (list) {
			var dl = list.whadl();

			jQuery.ajax({
				url : '/whadl/validator',
				dataType : 'json',
				type: 'POST',
				data: {
					'list' : dl
				},
				success : function (data, textStatus, xmlHttpRequest) {	
					that.stateChanged({
						'state' : that.states.validated,
						'list' : list
					});
				}
			});
		};


		return that;
	}, false);
}());