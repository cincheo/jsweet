var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
/* Generated from Java with JSweet 2.0.0-SNAPSHOT - http://www.jsweet.org */
var source;
(function (source) {
    var generics;
    (function (generics) {
        var LinkedList = java.util.LinkedList;
        var RawTypes = (function (_super) {
            __extends(RawTypes, _super);
            function RawTypes() {
                _super.call(this);
            }
            RawTypes.main = function (args) {
                var r1 = (new RawTypes());
                var r2 = (new RawTypes());
                var f = function (o) { return o; };
                console.info("Hi, random=" + f(4));
                var deque1 = (new LinkedList());
                var deque2 = (new LinkedList());
            };
            return RawTypes;
        }(Cls));
        generics.RawTypes = RawTypes;
        RawTypes["__class"] = "source.generics.RawTypes";
        RawTypes["__interfaces"] = ["source.generics.Itf"];
        var Cls = (function () {
            function Cls() {
            }
            return Cls;
        }());
        generics.Cls = Cls;
        Cls["__class"] = "source.generics.Cls";
    })(generics = source.generics || (source.generics = {}));
})(source || (source = {}));
source.generics.RawTypes.main(null);
