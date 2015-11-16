var lib = {
	Base : function() {
	},
	sub : {
		C : function() {
		}
	}
}

lib.Base.prototype.m1 = function() {
	console.info("EXPORT baseExecuted=true;")
}

lib.Base.prototype.m2 = function() {
	console.info("EXPORT extensionExecuted=true;")
}

lib.sub.C.prototype.m = function() {
	console.info("EXPORT sub=true;")
}
