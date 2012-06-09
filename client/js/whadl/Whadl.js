var Whadl = function() {
	this.armies = new Array();

	this.loadArmies = function(onComplete) {
		var armies = this.armies;
		
		jQuery.ajax({
			url : '/whadl/armies',
			dataType : 'json',
			success : function(data, textStatus, xmlHttpRequest){				
				for (var i = 0; i < data.length; i++) {
					var a = new Army(data[i]);
					armies[i] = a;
				}
				
				onComplete(armies);
			}
		});
	};
	
	this.validateList = function(list,onComplete) {
		jQuery.ajax({
			url : '/whadl/validator',
			dataType : 'json',
			type: 'POST',
			data: {
				'list' : list
			},
			success : function(data, textStatus, xmlHttpRequest){	
//				alert("RECEIVED RESPONSE: "+data.msg);
				onComplete(data);
			}
		});
	};
	
	
	this.appendArmiesData = function(armies) {
		var htmlResult = "";
		
		for (var i = 0; i < armies.length; i++) {
			var a = armies[i];			
			htmlResult += a.toHTML();
		}
		
		$('#content').append(htmlResult);
	};
};

Whadl.inst = new Whadl();