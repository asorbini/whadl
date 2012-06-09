var ControlForm = function(min,scroll,buttonValues,content,notFade,onClose,onOpen,onRemove,onExport) {
	this.buttons = new Array();
	
	var minimized = min;
	
	this.isMin = function() {
		return minimized;
	};
	
	this.setMin = function(min) {
		if (min != minimized) {
			this.buttons[0].click();
		}
		
		minimized = min;
	};
	
	this.openIfClosed = function() {
		if (minimized) {
			this.buttons[0].click();
		}
	};
	
	this.closeIfOpen = function(onComplete) {
		if (!minimized) {
			this.buttons[0].click();
		}
		
		if (onComplete) onComplete();
	};
	
	var form = $('<form class="ctrl"></form>');
	if (min) {
		form.addClass('minimized');
	} else {
		form.addClass('maximized');
	}
	var minMaxButton = $('<input class="submit" name="first" type="submit" value="'+(min?buttonValues[1]:buttonValues[0])+'"/>');
	this.buttons[0] = minMaxButton;
	form.append(minMaxButton);
	
	this.click = function() {
		minMaxButton.click();
	};
	
	var removeButton = $('<input class="submit" name="second" type="submit" value="'+buttonValues[2]+'"/>');
	
	if (onRemove) {
		this.buttons[1] = removeButton;
		form.append(removeButton);
		removeButton.click(function(){
			onRemove();
			return false;
		});
	}
	
	var exportButton = $('<input class="submit" name="third" type="submit" value="'+buttonValues[3]+'"/>');
	
	if (onExport) {
		this.buttons[2] = exportButton;
		form.append(exportButton);
		exportButton.click(function(){
			onExport();
			return false;
		});
	}
	
	/*var h = 0;
	content.children().each(function(i,el){
		el = $(el);
		h += el.height();
	})
	
	alert("HEIGHT: "+h);
	
	var cssH = content.css('height');
	var padd = content.css('padding-top');*/
	
	var minimize = function() {
		/*$(content).children().each(function(i,el){
			el = $(el);
			var fade = true;
			
			if (notFade) {
				for (var i=0; i < notFade.length; i++) {
					if (el.hasClass(notFade[i])){
						fade = false;
						break;
					}
				};
			}
			
			if (fade) {
				$(el).fadeOut(200, function() {
					
				});
			}
		});*/
		
		content = $(content);
		
	/*	var w = content.width();
		content.width(w);*/
		
		
		$(content).slideUp(500,function(){
			//$(content).hide();
			minMaxButton.attr('value',buttonValues[1]);
			minimized = true;
			form.addClass('minimized');
			form.removeClass('maximized');
			//content.width('auto');
		});
		
		if (onClose) onClose();
	};
	
	var maximize = function() {
		content = $(content);
		
/*		var w = form.parent().innerWidth();
		content.width(w);*/
		
		/*content.css('height',h+'px');
		content.css('padding-top',0);*/

		$(content).slideDown(500,function(){
			//content.width('auto');
			/*content.css('height',cssH);
			content.css('padding-top',padd);*/
			/*$(content).children().each(function(i,el){
				el = $(el);
				
				var fade = true;
				
				if (notFade) {
					for (var i=0; i < notFade.length; i++) {
						if (el.hasClass(notFade[i])){
							fade = false;
							break;
						}
					};
				}
				
				if (fade) {
					$(el).fadeIn(200, function() {
						
					});
				}
			});*/
			
			var limit = 1000;
			var i = 0;
			
			function searchScrollable(el) {
				if (i++ > limit) {
					return $('body > div.content');
				}
				
				if (el.parent(".scrollable").length > 0) {
					return $(el.parent());
				} else {
					return searchScrollable($(el.parent()));
				}
			}
			
			
			if (scroll) {
				var toScroll = searchScrollable($(content));
				//alert(toScroll.attr('class'));
				toScroll.delay(500).scrollTo($(content),300);
			}
			
			minMaxButton.attr('value',buttonValues[0]);
			minimized = false;
			form.addClass('maximized');
			form.removeClass('minimized');
		});
		
		if (onOpen) onOpen();
	};
	
	this.isMinimized = function() {
		return minimized;
	};
	
	if (!min) {
		minMaxButton.toggle(minimize,maximize); 
	} else {
		minMaxButton.toggle(maximize,minimize); 
	}
	
	this.disable = function() {
		minMaxButton.attr('disabled',true);
	};
	
	this.enable = function() {
		minMaxButton.attr('disabled',false);
	};	
	
	this.form = form;
};