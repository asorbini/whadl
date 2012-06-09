(function () {
	var Menu = org.mentalsmash.whadl.gui.type('Menu', function () {
		var that = $('<ul class="menu"></ul>'),
		items = [],
		submenus = [];
		
		that.item = function (name, callback) {
			var i = items.getByAttribute('name', name);
			
			if (!i) {
				i = (function () {
					var li = $('<li></li>');
					li.name = name;
					li.callback = callback;
					li.text(name);
					li.click(callback);
					return li;
				}());
				
				items.add(i);
				
				this.append(i);
				
				return i;
			} else if (arguments > 1) {
				throw {
					'name' : 'WhadlException',
					'message' : 'Menu Item already defined : ' + name
				};
			}
		};
		
		that.removeItem = function (name) {
			var i = items.getByAttribute('name', name);
			if (i) {
				items.remove(i);
				return true;
			} else {
				return false;
			}
		};
		
		that.menu = function (name) {
			var m = submenus.getByAttribute('name', name);
			
			if (!m) {
				m = new Menu();
				submenus.add(m);
				this.append(m);
				return m;
			} else {
				return m;
			}
		};
		
		that.removeMenu = function (name) {
			var m = submenus.getByAttribute('name', name);
			if (m) {
				submenus.remove(m);
				return true;
			} else {
				return false;
			}
		};
		
		return that;
	}, true);
}());