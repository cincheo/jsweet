var Base = function() {
}

Base.prototype.m1 = function() {
	console.info("EXPORT baseExecuted=true;")
}

Base.prototype.m2 = function() {
	console.info("EXPORT extensionExecuted=true;")
}
