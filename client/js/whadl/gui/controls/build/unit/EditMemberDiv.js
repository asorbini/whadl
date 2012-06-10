var EditMemberDiv = function(member, memberNum,onSummarize,container) {
		var membName = member.name;
		var div = $('<div class="edit-member '+membName+'"></div>');
		var divTitle = $('<div class="title"><h4>'+membName+' '+(memberNum+1)+'</h4></div>');
		div.append(divTitle);
		$('h4',divTitle).editable().addClass('editable');
		
		var baseInfoList = $('<ul class="base-info"></ul>');
		baseInfoList.append($('<li><span class="label">Type</span><span class="value">'+membName+'</span></li>'));
		
		var content = $('<div class="content"></div>');
		
		div.append(content);
		
		content.append(baseInfoList);
		
		var equipmentEditor = new PatternHandlerDiv("Member's equipment",member.equipment,'equipment',onSummarize);
		content.append(equipmentEditor.div);
		
		
		$('input[name="first"]',equipmentEditor.div).each(function(i,el){
			el = $(el);
			
			var open = false;
			
			el.click(function(){
				if (open) {
					/*container.scrollTo(div,500);*/
					open = false;
				} else {
					/*container.scrollTo(equipmentEditor.div,500);*/
					open = true;
				}
			});

		});
		
		var notFade = new Array();
		notFade[0] = 'pattern';
		
		var ctrlForm = new ControlForm(true,false,['OK','Edit'],content,notFade);
		
		divTitle.append(ctrlForm.form);
		content.hide();
		
		this.div = div;
		
		this.getCost = function() {
			return equipmentEditor.getCost();
		};
		
		
		this.serialize = function(indent) {
			var name = removeSpaces($('h4',divTitle).text());
			
			var eqSer = equipmentEditor.serialize();
			
			if (eqSer == '') {
				return eqSer;
			}
			
			var result = 'member '+membName+' $'+name+' ('+eqSer+');';
			
			return result;
		};
};