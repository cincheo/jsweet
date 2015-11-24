
module org.jsweet.prosncons {

    interface Options {
        field: string;
    }

    var func: (options: Options) => void = (function() {
        // func signature will be incorrect 
		return function(options: string) {
            console.log('options parameter is invalid', options.toUpperCase());
        };
	});

    func({ field: 'foo', whatever: 'bar' });
}