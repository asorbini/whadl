$(document).ready(function () {
	try {
		var GUI = org.mentalsmash.whadl.gui.GUI,
		
		gui = new GUI("whadl");

		$('body').append(gui);
	} catch (ex) {
		alert(ex.name + ": " + ex.message);
	}
});