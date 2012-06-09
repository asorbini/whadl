var ExportDiv = function(editor) {
	
	var div = $(
		'<div class="export dialog">'+
			'<div class="title"><h2>Export and validate</h2></div>'+
			'<div class="content">'+
			'</div>'+
		'</div>'
	);
	
	
	var content = $('div.content',div);
	var title = $('div.title',div);
	
	var reviewDiv = $(
		'<div class="review">'+
			'<div class="title"><h3>List definition file</h3></div>'+
			'<div class="content">'+
				'<pre></pre>'+
			'</div>'+
		'</div>'
	);
	
	content.append(reviewDiv);
	
	var resultDiv = $('<div class="result closed"><div class="title"><h3>Validation Results</h3></div><div class="content"></div></div>');
	
	$('div.content',resultDiv).hide();
	
	content.append(resultDiv);
	
	content.hide();
	
	
	var reviewCtrl = null;
	
	var generate = function() {
		var result = editor.serialize();
		$('pre',reviewDiv).text(result);
	};
	
	var validate = function() {
		
		//alert("Validating");
		//validate function
		
		$('div.content > :nth-child(n)',resultDiv).fadeOut(500);
		
		var loadingDiv = $('<div class="loading">Waiting for result...</div>');
		
		loadingDiv.hide();
		$('div.content',resultDiv).append(loadingDiv);
		
		loadingDiv.fadeIn(200);
		
		resultCtrl.openIfClosed();
		
		var listDef = $('pre',reviewDiv).text();
		
		if (listDef == "") {
			generate();
			listDef = $('pre',reviewDiv).text();
		}
		
		var w = Whadl.inst;
		var resContent = $('div.content',resultDiv);
		
		var showResult = function(result){
			//alert("DONE");
			$('div.content > :nth-child(n)',resultDiv).fadeOut(500,function(){
				
			});
			
			var statusP = $(
				'<p class="status '+(result.success?'success':'error')+'">'+
					'<span class="label status">Validated</span>'+
					'<span class="value status '+(result.success?'success':'error')+'">'
						+(result.success?'yes':'no')+
					'</span>'+
				'</p>');
			statusP.hide();
			resContent.append(statusP);
			statusP.fadeIn(300,function(){
				var msgP = $(
					'<p class="message '+(result.success?'success':'error')+'">'+
						'<span class="label message">'+(result.success?'Total Points':'Response')+'</span>'+
						'<span class="value message '+(result.success?'success':'error')+'">'+result.msg+'</span>'+
						'</p>');
				msgP.hide();
				resContent.append(msgP);
				msgP.fadeIn(300);
				$('body > div.content').delay(100).scrollTo(resContent,200);
			});
			$('body > div.content').scrollTo(resContent,500);
		
			//resultCtrl.openIfClosed();
			//alert("OK!");
		};
		
		
		w.validateList(listDef,function(result){
			showResult(result);
		});
	};
	
	
	
	
	reviewCtrl = new ControlForm(true,true,['Hide','Show','Generate','Validate'],$('div.content',reviewDiv),new Array(),null,function(){
		if ($('pre',div).text()=="") {
			generate();
		}
	},function(){
		generate();
		reviewCtrl.openIfClosed();
	},function(){
		reviewCtrl.closeIfOpen();
		validate();
	});
	
	$('div.title',reviewDiv).append(reviewCtrl.form);
	$('div.content',reviewDiv).hide();
	
	var resultCtrl = new ControlForm(true,true,["Hide","Show"],$('div.content',resultDiv));
	
	$('div.title',resultDiv).append(resultCtrl.form);
	
	
	var ctrlForm = new ControlForm(true,true,['Hide','Show'],content,['result']);
	
	title.append(ctrlForm.form);
	
	this.setBuild = function(buildStr) {
		$('pre',div).text(buildStr);
	};
	
	$('pre',div).editable({
		type: 'textarea'
	}).addClass("editable");
	
	
				
	this.div = div;
};