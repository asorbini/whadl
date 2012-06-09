var PatternHandlerDiv = function(title,pattern,divClass,onSummarize) {
	
	var empty = pattern.type == 'single' && pattern.name == "NONE";
	
	var handler = this;
	
	var div = $('<div class="pattern-editor"></div>');
	var titleDiv = $('<div class="title"><span class="title">'+title+'</span></div>');
	div.append(titleDiv);
	
	var summaryDiv = $(
		'<div class="summary">'+
			'<ul class="summary">'+
				'<li><span class="quantity">0</span><span class="name">None selected</span></li>'+
			'</ul>'+
			'<div class="cost">0</div>'+
		'</div>'
	);
	div.append(summaryDiv);
	
	if (divClass) {
		div.addClass(divClass);
	}
	
	var patternBox = PatternRenderer.render(pattern);
	patternBox.div.hide();
	div.append(patternBox.div);
	
/*	var ctrlForm = $(
		'<form class="ctrl">'+
		'</form>'
	);*/
	
	var ctrlForm = new ControlForm(true,false,['OK','Edit'],patternBox.div,new Array(),function(){
		handler.summarize();
	},function(){
		patternBox.div.fadeIn(300);
		/*$('body').delay(320).scrollTo(patternBox.div,300);*/
	});
	
	/*var editOkButton = $('<input type="submit" value="Edit" class="submit pattern-editor-button"/>');
	ctrlForm.append(editOkButton);*/
	$('div.title',div).append(ctrlForm.form);
	
	
	//var okButton = $('<input type="submit" value="OK" class="submit"/>');
	
	
	this.div = div;
	
	this.getCost = function() {
		return patternBox.getCost();
	};
	
	this.setCost = function(cost) {
		$('div.cost',summaryDiv).text(cost);
	};
	
	this.toMap = function() {
		return patternBox.toMap();
	};
	
	this.summarize = function() {
		var summary = patternBox.summarize();
		
		var summaryList = $('ul.summary',summaryDiv);
		summaryList.empty();
		for (var i=0; i < summary.length; i++) {
			var el = summary[i];
			var li = $('<li></li>').append(el);
			li.hide();
			summaryList.append(li);
			li.fadeIn(800);
		};
		
		if ($('li',summaryList).length == 0) {
			var li = $('<li><span class="quantity">0</span><span class="name">None selected</span></li>');
			li.hide();
			summaryList.append(li);
			li.fadeIn(800);
		}
		
		summaryDiv.append($('<div class="clearer"></div>'));
		
		var cost = patternBox.getCost();
		
		this.setCost(cost);
		
		summaryDiv.fadeIn();
		
		var valuesMap = this.toMap();
		
		if (typeof(onSummarize) == "function") onSummarize(valuesMap);
	};
	
	if (empty) {
		ctrlForm.disable();
		summaryDiv.hide();
		div.fadeTo(0,0.5);
	} else {
		this.summarize();
	}
	
	this.isEmpty = function(){
		return empty;
	};
	
	this.setTitle = function(title) {
		$('span.title',titleDiv).text(title);
	};
	
	this.serialize = function() {
		var values = patternBox.toMap();
		var result = '(';
		var i = 0;
		for (var key in values){
			var q = values[key];
			result+=(q==1?'':q+' ')+key+",";
			i++;
		}
		
		if (i == 0) {
			return '';
		}
		
		result = result.substring(0,result.length-1)+")";
		
		return result;
	};
	
};