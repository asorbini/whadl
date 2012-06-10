var EditBuildDiv = function(gui,army,buildNumber,createUnitCallback,onUpdate,onRemove) {
	var editor = this;
	
	var units = new Array();
	this.units = units;
	
	var div = $(
		'<div class="edit-build">'+
		'</div>'
	);
	
	var content = $('<div class="content"></div>');
	
	var unitsDiv = $(
		'<div class="units">'+
			'<div class="title"><h3>Units</h3></div>'+
			'<div class="content">'+
				'<span class="no-units">NO UNIT CREATED</span>'+
			'</div>'+
		'</div>');	
	
	var unitsCtrl = new ControlForm(false,false,['Hide','Open'],$('div.content',unitsDiv));
	$('div.title',unitsDiv).append(unitsCtrl.form);
	
	unitsDiv.openIfClosed = function() {
		unitsCtrl.openIfClosed();
	};
	
		
	//	var minimized = false;
	
	var rand=Math.floor(Math.random()*101);
	this.name = 'New '+army.army.name+' List '+rand;
	
	this.getName = function() {
		return this.name;
	};
	
	this.setName = function(name) {
//		alert("setting name to "+name);
		this.name = name;
		gui.updatedBuild(this);
	};
	
	this.getNum = function() {
		return buildNumber;
	};
	
	var buildTitle = $('<div class="title"><h2>'+this.name+'</h2></div>');
	$('h2',buildTitle).editable({
		onSubmit : function(content){
			editor.setName(content.current);
		}
	}).addClass('editable');

	
	
	
	var summaryList = $('<ul class="base-info"></ul>');
	var typeLi = $('<li><span class="label">Type</span><span class="value">'+army.name+'</span>');
	var costLi = $('<li><span class="label">Total Points</span><span class="value">0</span>');
	summaryList.append(typeLi);
	summaryList.append(costLi);
	
	var ctrlForm = null;
	
	
	var setCost = function(cost) {
		$('span.value',costLi).text(cost);
	};
	var getCost = function() {
		var result = 0;
		
		for (var i=0; i < units.length; i++) {
			result += units[i].getCost();
		};
		
		return result;
	};
	
	var lastLength = 0;
	
	var update = function() {
		//alert('EditBuildDiv.update');
		var cost = getCost();
		setCost(cost);
		
		if (units.length != lastLength) {
			lastLength = units.length;
			//alert("notifying units");
			for (var i=0; i < units.length; i++) {
				units[i].setUnits(units);
			};
		}
	};
	
	this.update = update;
	var selectUnitDiv = null;
	
	selectUnitDiv = new SelectUnitTypeDiv(buildNumber,army,function(buildNum,army,unitName,unitNum){
		if (ctrlForm.isMinimized()) {
			minMaxButton.click();
		}
		
		$('div.edit-unit > div.title > form.maximized > input[name="first"]',div).click();
		
		createUnitCallback(buildNum,army,unitName,unitNum,function(){
			update();
			if (onUpdate) onUpdate(editor);
		}, function(editor) {
			if (units.contains(editor)) {
				units.remove(editor);
				update();
				selectUnitDiv.decreaseUnitsNum();
			}
		});
	});
	
	this.serialize = function() {
		
		var result = 'build '+army.name+' $'+removeSpaces($('h2',buildTitle).text())+' {\n';
//		result+="   "+'army '+army.name+';\n';
		
		for (var i=0; i < this.units.length; i++) {
			result += this.units[i].serialize('   ')+'\n';
		};
		
		result += '}';
		return result;
	};
	
	var exportDiv = new ExportDiv(this);
	$('div.title',exportDiv.div).hide();
	
	selectUnitDiv.div.hide();
	summaryList.hide();
	unitsDiv.hide();
	
	div.append(buildTitle);
	div.append(content);
	content.append(summaryList);
	content.append(selectUnitDiv.div);
	/*$('form.ctrl > input[name="first"]',exportDiv.div).click();
	$('div.content',exportDiv.div).hide(0);*/
	content.append(unitsDiv);
	
	var rmFunc = function() {
		if (onRemove) onRemove(editor);
	};
	
	
	ctrlForm = new ControlForm(false,false,['OK','Edit','Delete','Export'],content,new Array(),function(){
		$('div.edit-unit > div.title > form.maximized > input[name="first"]').click();
	},null,(onRemove)?rmFunc:null);
	
	buildTitle.append(ctrlForm.form);
	
	this.army = army;
	
	this.div = div;
	
	this.content = content;
	
	this.unitsDiv = unitsDiv;
	
	this.onCreation = function() {
		content.append(exportDiv.div);
		
		/*summaryList.fadeIn(100,function(){
			selectUnitDiv.div.fadeIn(100,function(){
				unitsDiv.fadeIn(100, function(){
					$('div.title',exportDiv.div).fadeIn(100);
				});
			});
		});*/
		
		summaryList.fadeIn(300);
			selectUnitDiv.div.delay(100).fadeIn(300);
				unitsDiv.delay(200).fadeIn(300);
					$('div.title',exportDiv.div).delay(300).fadeIn(100);

	};
	
	
};