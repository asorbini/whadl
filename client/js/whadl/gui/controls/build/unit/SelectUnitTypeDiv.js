var SelectUnitTypeDiv = function(buildNum, army,createUnitCallback) {
	var unitsNum = 0;
	
	var div = $(
		'<div class="dialog">'+
			'<div class="title"><h3>Menu</h3></div>'+
			'<div class="content">'+
				'<ul class="menu">'+
					'<li>'+
						'<span class="label">'+
							'Create new unit'+
						'</span>'+
						'<span class="value">'+
							'<form class="create-unit">'+
								'<select name="unit"></select>'+
							'</form>'+
						'</span>'+
					'</li>'+
				'</ul>'+
			'</div>'+
		'</div>'
	);
	var select = $('div.content > ul.menu > li > span.value > form > select',div);
	
	var ctrlForm = new ControlForm(false,true,['Hide Menu','Show Menu'],$('div.content',div));
	$('div.title',div).append(ctrlForm.form);
	
	for (var i = 0; i < army.units.length; i++) {
		var unit = army.units[i];
		var option = $('<option'+(i==0?" selected":"")+'>'+unit.name+'</option>');
		select.append(option);
	}
	
	var form = $('form.create-unit',div);
	form.append('<input type="submit" value="Create Unit" class="submit"/>');
	div.data('army',army);
	
	form.submit(function(){
		var army = div.data('army');
		var name = $('option:selected',select)[0].text;
		$('form.ctrl > input[name="first"]',div).click();
		createUnitCallback(buildNum, army,name,unitsNum++);
		return false;
	});
	
	this.decreaseUnitsNum = function() {
		unitsNum--;
	};
	
	this.div = div;
};