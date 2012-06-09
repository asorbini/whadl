var org = (function() {
	var NS = function (name, visible, parent) {
		var that = Object.create(),
		subNSs = [],
		types = [],
		splitName = null,
		requireNS = null,
		requireType = null; 
		
		that.parent = parent || null;
		that.name = name;
		that.visible = visible || true;
		
		splitName = function (name) {
			var namespace, obj, local;
			
			if (name.lastIndexOf('.') > 0) {
				namespace = name.substring(0, name.lastIndexOf('.'));
				obj = name.substring(name.lastIndexOf('.') + 1);
				
			} else {
				namespace = '';
				local = name; 
			}
			
			return {
				'ns' : namespace,
				'local' : obj
			};
		};
		
		that.ns = function (name, visible) {
			var split = splitName(name),
			subns = null;
			
			if (split.ns !== '') {
				subns = this.importNS(split.ns);
				return subns.ns(split.local);
			}
			
			if (types.getByAttribute('type', name)) {
				throw {
					'name' : 'NSException',
					'message' :
						this.toString() + "." + name +
							" is already defined as a type"
				};
			}
			
			subns = subNSs.getByAttribute('name', name);
			
			if (!subns) {
				subns = NS(name, visible, this);
				subNSs.add(subns);
				if (subns.visible) {
					this[name] = subns;
				}
			}
			
			return subns;
		};
		
		that.type = function (name, constructor, visible) {
			var split = splitName(name),
			subns = null,
			t = null,
			Constructor = null;
			
			visible = visible || true;
			
			if (split.ns !== '') {
				subns = this.importNS(split.ns);
				return subns.type(split.local, constructor);
			}
			
			if (subNSs.getByAttribute('name', name)) {
				throw {
					'name' : 'NSException',
					'message' :
						this.toString() + "." + name +
							" is already defined as a namespace"
				};
			}
			
			t = types.getByAttribute('type', name);
			
			if (t && arguments.length > 1) {
				throw {
					'name' : 'NSException',
					'message' :
						this.toString() + "." + name +
							" type already defined"
				};
			} else if (t) {
				return t;
			} else if (arguments.length <= 1) {
				throw {
					'name' : 'NSException',
					'message' :
						'No constructor supplied for type ' +
							this.toString() + '.' + name
				};
			} else if (constructor.type && constructor.type !== name) {
				throw {
					'name' : 'NSException',
					'message' :
						'Supplied constructor is already binded ' +
						'to another type: ' + 
						constructor.type + ' (supplied: ' + name + ')'
				};
			} else if (!constructor.type || constructor.type === name) {
				Constructor = constructor;
				constructor = function (args) {
					args.proto = args.proto || {};
					args.that = args.that || Object.create(args.proto);
					
					return new Constructor(args);
				};
				
				
				if (visible) {
					this[name] = constructor;
				}

				constructor.type = name;
				constructor.visible = visible || true;
				constructor.ns = this;
				
				if (!types.contains(constructor)) {
					types.add(constructor);
				}
				
				return constructor;
			}
			
		};
		
		that.t = that.type;
		
		that.require = function (name, requirer) {
			if (subNSs.getByAttribute('name', name)) {
				return requireNS(name, requirer);
			} else {
				return requireType(name, requirer);
			}
		};
		
		that.r = that.require;
		
		requireNS = function (name, requirer) {
			var subs = name.split('.'),
			s = null,
			subns = null;
			
			if (subs.length > 1) {
				s = subNSs.getByAttribute('name', subs[0]);
				if (!s) {
					throw {
						'name' : 'NSException',
						'message' : this.toString() + "." + subs[0] +
						" namespace not found"
					};
				}
				return s.importNS(subs.slice(1).join('.'));
			} else {
				subns = subNSs.getByAttribute('name', name);
				if (!subns) {
					throw {
						'name' : 'NSException',
						'message' : this.toString() + "." + name +
						" namespace not found"
					};
				} else {
					if (!subns.visible) {
						if (!requirer || (requirer !== this &&
								!this.isParent(requirer))) {
							throw {
								'name' : 'NSException',
								'message' : 
									this.toString() + "." + name +
									" is not visible to : " + 
									(requirer || "NO REQUIRER SUPPLIED")
							};
						}
					}
					
					return subns;
				}
			}
		};
		
		requireType = function (name, requirer) {
			var split = splitName(name),
			subns = null,
			t = null;
			
			if (split.ns !== '') {
				subns = this.importNS(split.ns);
				return subns.require(split.local);
			} else {
				t = types.getByAttribute('type', name);
				if (!t) {
					throw {
						'name' : 'NSException',
						'message' :
							this.toString() + "." + name +
								" type not found"
					};
				} else if (!t.visible) {
					if (!requirer ||
							(requirer.ns !== this &&
								!this.isParent(requirer.ns))) {
						throw {
							'name' : 'NSException',
							'message' :
								this.toString() + "." + name +
									" is not visible to : " +
										(requirer || "NO REQUIRER SUPPLIED")
						};
					}
				}
				
				return t;
			}
		};
		
		that.instance = function (type,args) {
			var t = this.r(type, this);
			return t(args);
		};
		
		that.i = instance;
		
		that.isParent = function (ns) {
			if (ns.parent === this) {
				return true;
			} else {
				if (ns.parent !== null) {
					return this.isParent(ns.parent);
				} else {
					return false;
				}
			}
		};
		
		that.toString = function () {
			return ((this.parent) ? this.parent.toString() + ".":"") +
						this.name;
		};
		
		return that;
	},
	
	root = function (name) {
		return new NS('name');
	},
	
	that = root('org');
	
	that.ns('mentalsmash');
	that.mentalsmash.root = root;
	
	return that;
}());
