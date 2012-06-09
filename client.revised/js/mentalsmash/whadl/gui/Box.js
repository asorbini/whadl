(function () {
	var gui = WHADL.ns('gui'),
	
	Box = gui.type('Box', function (opts) {
		var that = null,
		Menu = gui.require('Menu', Box),
		i = 0,
		c = null,
		content = null,
		title = null,
		menu = null;
		
		/**
			opts = {
				id : 'string',
				classes : ['string', 'string',...],
				title : {
					h : 'h1' | 'h2' | 'h3' | 'h4' | 'h5',
					elements : [obj1, obj2, ...]
				},
				menu : true | false,
				content : {
					elements : [obj1, obj2, ...],
				},
				filler : function (box) {}
			}	
		**/
		opts.id = opts.id || null;
		opts.classes = opts.classes || null;
		opts.title = opts.title || null;
		opts.menu = opts.menu || false;
		opts.content = opts.content || null;
		opts.filler = opts.filler || null;
		
		that = $('<div class="box"></div>');
		
		if (opts.id) {
			that.attr('id', opts.id);
		}
		
		if (opts.classes) {
			for (i = 0; i < opts.classes.length; i = i + 1) {
				c = opts.classes[i];
				if (typeof c === 'string') {
					that.addClass(c);
				}
			}
		}
		
		content = (function () {
			var c = $('<div class="content"></div>'),
			i = 0,
			el = null;
			
			if (opts.content) {
				opts.content.elements = opts.content.elements || [];

				for (i = 0; i < opts.content.elements.length; i = i + 1) {
					el = opts.content.elements[i];
					content.append(el);
				}
			}
		}());
		
		title = (function () {
			var t = null,
			h = null,
			i = 0,
			el = null;
			
			if (opts.title) {
				opts.title.h = opts.title.h || 'h1';
				opts.title.elements = opts.title.elements || [];

				t = $('<div class="title"></div>');
				h = $('<' + opts.title.h + '></' + opts.title.h + '>');
				t.append(h);
				for (i = 0; i < opts.title.elements.length; i = i + 1) {
					el = opts.title.elements[i];
					t.append(el);
				}
				return t;
			} else {
				return null;
			}
			
		}());
		
		menu = (function () {
			if (opts.menu) {
				return new Menu();
			} else {
				return null;
			}
		}());
		
		if (title) {
			that.append(title);
		}
		
		if (menu) {
			that.append(menu);
		}
		
		that.append(content);
		
		if (opts.filler) {
			opts.filler(that);
		}
		
		return that;
	}, true);
}());