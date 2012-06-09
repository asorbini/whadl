(function(){
	/** org.mentalsmash.Observable
	 args = {
		that : obj,
		log : true | false;
	 }
	**/
	org.mentalsmash.type('Observable', function(args) {
		var that = args.that || {},
		observers = [],
		log = args.log || false;
		notificationsLog = [];
		
		that.addObserver = function(o) {
			if (!observers.contains(o)) {
				observers.add(o);
			}
		};
		
		that.removeObserver = function(o) {
			observers.remove(o);
		};
		
		that.notifyObservers = function(notification) {
			var i = 0,
			el = null;
			
			if (log) {
				notificationsLog.add(notification);
			}
			
			for (i = 0; i < observers.length; i = i + 1) {
				observers[i](this,notification);
			}
		};
		
		return that;
	});
}());