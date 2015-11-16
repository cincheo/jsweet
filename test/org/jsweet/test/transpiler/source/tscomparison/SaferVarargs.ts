var _exportedVar_firstArg;
var _exportedVar_firstArg2;

module org.jsweet.prosncons {
    function print(...args: any[]) {
        _exportedVar_firstArg = args[0];
    }
    
    function print2(...args: string[]) {
        _exportedVar_firstArg2(args);
    }

	function dump(...args: string[]) {
        // print's args[0] is a string array, where we want it to be a string
		print(args);
        
        // wouldn't compile if uncommented 
        // print2(args);
	}
    
    dump("blah", "bluh");
}