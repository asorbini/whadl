var SelectBuildTypeDiv = function(armies,createBuildCallback) {
	var buildsNum = 0;
	
	var div = $(
		'<div id="main-menu" class="dialog">'+
			'<div class="title"><h3>Whadl Editor</h3></div>'+
			'<div class="content">'+
				'<ul class="menu">'+
					'<li>'+
						'<span class="label">'+
							'Create new list'+
						'</span>'+
						'<span class="value">'+
							'<form class="create-build">'+
								'<select name="army"></select>'+
							'</form>'+
						'</span>'+
					'</li>'+
				'</ul>'+
			'</div>'+
		'</div>'
	);
	var select = $('div.content > ul.menu > li > span.value > form > select',div);
	
	var ctrlForm = new ControlForm(true,true,['Hide Menu','Show Menu'],$('div.content',div));
	$('div.title',div).append(ctrlForm.form);
	$('div.content',div).hide();
	
	for (var i = 0; i < armies.length; i++){
		var name = armies[i].army.name;
		if (name == 'BaseArmy') continue;
		var opt = $('<option'+(i==0?" selected":"")+'>'+name+'</option>');
		select.append(opt);	
	}
	$('ul.menu form.create-build',div).append('<input type="submit" value="Create List" class="submit"/>');
	
	$('ul.menu form.create-build',div).submit(function(){
		var name = $('option:selected',select)[0].text;
		$('form.ctrl > input[name="first"]',div).click();
		createBuildCallback(name,buildsNum++);
		return false;
	});
	
	this.decreaseBuildsNum = function() {
		buildsNum--;
	};
	
	this.openIfClosed = function() {
		ctrlForm.openIfClosed();
	};
	
	this.div = div;
};