"Generated from Java with JSweet 1.0.0-SNAPSHOT - http://www.jsweet.org";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
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
                        var AbstractClass = (function () {
                            function AbstractClass(name) {
                                this.name = name;
                            }
                            AbstractClass.prototype.action = function () { return null; };
                            return AbstractClass;
                        })();
                        tscomparison.AbstractClass = AbstractClass;
                        var AbstractClassImpl = (function (_super) {
                            __extends(AbstractClassImpl, _super);
                            function AbstractClassImpl() {
                                _super.call(this, "DemoImpl");
                            }
                            AbstractClassImpl.prototype.action = function () {
                                return "done";
                            };
                            return AbstractClassImpl;
                        })(AbstractClass);
                        tscomparison.AbstractClassImpl = AbstractClassImpl;
                        var AbstractClasses = (function () {
                            function AbstractClasses() {
                            }
                            AbstractClasses.main = function (args) {
                                new AbstractClassImpl();
                            };
                            return AbstractClasses;
                        })();
                        tscomparison.AbstractClasses = AbstractClasses;
                    })(tscomparison = source.tscomparison || (source.tscomparison = {}));
                })(source = transpiler.source || (transpiler.source = {}));
            })(transpiler = test.transpiler || (test.transpiler = {}));
        })(test = jsweet.test || (jsweet.test = {}));
    })(jsweet = org.jsweet || (org.jsweet = {}));
})(org || (org = {}));
org.jsweet.test.transpiler.source.tscomparison.AbstractClasses.main(null);
