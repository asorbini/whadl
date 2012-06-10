var GUI = function() {	
	var whadl = new Whadl();
	var contentDiv = $('body > div.content');
	var gui = this;
	var builds = new Array();
	
	var newArmyDiv = null;
		
	var open = true;
	var ctrlForm = new ControlForm(false,false,["Close This","Show"],$('body > div.title'),new Array(),function() {
		open = false;
	});
	$('body > div.title').append(ctrlForm.form);
	$('body > div.title').append($('<div class="clearer"></div>'));
	
	/*var buildsNavigator = $('<ul id="builds-navigator"></ul>');
	contentDiv.append(buildsNavigator);
	
	var buildsContainer = $('<div id="builds" class="scrollable"></div>');
	contentDiv.append(buildsContainer);*/
	
	
	this.setScrollable = function() {
		/*$('#builds').scrollable({
			vertical: true
		});
		$('#builds').navigator('#builds-navigator');*/
	};
	
	
	
	contentDiv.closeIfOpen = function() {
		if (open) {
			ctrlForm.click();
		}
	};
	
	contentDiv.openIfClosed = function() {
		if (!open) {
			ctrlForm.click();
		}
	};
	
	this.init = function() {
		
		contentDiv.hide();
		
		var callback = function(armies){
				newArmyDiv = new SelectBuildTypeDiv(armies, gui.createNewBuild);
				$('body > div.title').delay(1000).fadeOut(500,function(){
					contentDiv.fadeIn(500,function(){
						newArmyDiv.div.hide();;
						newArmyDiv.div.insertBefore(contentDiv);
						newArmyDiv.div.slideDown(500,function(){
							$('div.title input[name="first"]',newArmyDiv.div).click();
							/*$('div.title input[name="first"]',newArmyDiv.div).delay(0).fadeTo(1,1,function(){
								
							})*/
						});
					});
				});
		};
		
		whadl.loadArmies(callback);
	};

	this.insertAfter = function(container,prec,el){
		el.hide(0);
		el.insertAfter(prec);
		el.fadeIn(500);
//		$(contentDiv).delay(300).scrollTo(el,300);
	};
	
	this.notifyNewUnit = function(units) {
		
		for (var i=0; i < units.length; i++) {
			units[i].setUnits(units);
		};
		
	};
	
	this.updatedBuild = function(buildEditor) {
		/*var li  =$('#build-'+buildEditor.getNum()+'-nav');
		li.text(buildEditor.getName());
		alert("updating");*/
	};
	
	var gui = this;
	
	
	this.createNewBuild = function(armyName,buildNum) {
		var army = whadl.armies.getByName(armyName);		
		var editBuildDiv = new EditBuildDiv(gui,army,buildNum,gui.createNewUnit,null,function(){
			var target = null;
			if (editBuild != null) {
				target = editBuild.div;
				gui.removeElement(target, function(){
					for (var i=buildNum; i < builds.length - 1; i++) {
						builds[i] = builds[i+1];
					};
					
					builds.pop();
					newArmyDiv.decreaseBuildsNum();
					
					if (builds.length == 0) {
						newArmyDiv.openIfClosed();
					}
				});	
			}
			
			//gui.removeElement($('#build-'+buildNum+'-nav'));	
		});
		
		$('div.edit-build > div.title > form.maximized > input[name="first"]',contentDiv).click();
		
		builds[buildNum] = editBuildDiv;
		
		/*var name = editBuildDiv.getName();*/
		
		/*var buildLi = $('<li id="build-'+buildNum+'-nav">'+name+'</li>');
		
		buildLi.hide();
		buildsNavigator.append(buildLi);
		buildLi.fadeIn(500);
		
		gui.appendTo(buildsNavigator,buildLi);
		gui.appendTo(buildsContainer,editBuildDiv.div,function() {
			gui.setScrollable();
			editBuildDiv.onCreation();
		});*/
		
		gui.appendTo(contentDiv,editBuildDiv.div,function() {
			editBuildDiv.onCreation();
		});
	};
	
	
	
	this.createNewUnit = function(buildNum,army,unitName,unitNum,onUpdate,onRemove) {
		var unit = army.units.getByName(unitName);
		var editBuildDiv = builds[buildNum];
		
		var editUnitDiv = new EditUnitDiv(editBuildDiv,unit,unitNum,onUpdate,function(editor){
			gui.removeElement(editor.div, function(){
				if ($('div.edit-unit',editBuildDiv.div).length == 0){
					$('div.units > div.content',editBuildDiv.div).append($('<span class="no-units">NO UNIT CREATED</span>'));
				}
				
				if (onRemove) onRemove(editor);
			});
		});
		
		editBuildDiv.units[unitNum] = editUnitDiv;
		editBuildDiv.update();
		
		if ($('span.no-units',editBuildDiv.unitsDiv).length == 1) {
			$('span.no-units',editBuildDiv.unitsDiv).fadeOut(500,function(){
				$('div.content',editBuildDiv.unitsDiv).empty();
				editBuildDiv.unitsDiv.openIfClosed();
				gui.appendTo($('div.units > div.content',editBuildDiv.content),editUnitDiv.div,editUnitDiv.onCreation);
			});
		} else {
			editBuildDiv.unitsDiv.openIfClosed();
			gui.appendTo($('div.units > div.content',editBuildDiv.content),editUnitDiv.div,editUnitDiv.onCreation);
		}
		
		return editUnitDiv;
	};
	
	this.removeElement = function(el, onComplete) {
		el.slideUp(500,function(){
			el.hide(0);
			el.remove();
			if (onComplete) onComplete();
		});
	};
	
	this.appendTo = function(container,el,onComplete) {
		el.hide();
		container.append(el);
		el.fadeIn(500, function(){
			if (onComplete) onComplete();
		});
		//contentDiv.delay(300).scrollTo(el,300);
//		$(contentDiv).delay(500).scrollTo(el,300);
	};
	
};