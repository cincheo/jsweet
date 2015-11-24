"Generated from Java with JSweet 1.0.0-SNAPSHOT - http://www.jsweet.org";
var org;
(function (org) {
    var jsweet;
    (function (jsweet) {
        var test;
        (function (test) {
            var transpiler;
            (function (transpiler) {
                var source;
                (function (source) {
                    var tscomparison;
                    (function (tscomparison) {
                        var Operation = (function () {
                            function Operation(x) {
                                this.x = x;
                            }
                            Operation.prototype.add = function (number, d, ds) {
                                if (d === void 0) { d = null; }
                                if (ds === void 0) { ds = null; }
                                return number + this.x;
                            };
                            return Operation;
                        })();
                        tscomparison.Operation = Operation;
                        var OtherThisExample = (function () {
                            function OtherThisExample() {
                            }
                            OtherThisExample.main = function (args) {
                                var numbers = [1, 2, 3];
                                var twoOperation = new Operation(2);
                                var results = numbers.map(function (number, d, ds) { return twoOperation.add(number, d, ds); });
                                _exportedVar_results = results;
                                console.log('%%%results@@@' + _exportedVar_results + '%%%');
                            };
                            return OtherThisExample;
                        })();
                        tscomparison.OtherThisExample = OtherThisExample;
                    })(tscomparison = source.tscomparison || (source.tscomparison = {}));
                })(source = transpiler.source || (transpiler.source = {}));
            })(transpiler = test.transpiler || (test.transpiler = {}));
        })(test = jsweet.test || (jsweet.test = {}));
    })(jsweet = org.jsweet || (org.jsweet = {}));
})(org || (org = {}));
org.jsweet.test.transpiler.source.tscomparison.OtherThisExample.main(null);
