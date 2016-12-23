var tscomparison;
(function (tscomparison) {
    var ActualScoping = (function () {
        function ActualScoping() {
        }
        ActualScoping.main = function (args) {
            if ((true)) {
                var scoped = true;
            }
            else {
                var scoped = false;
            }
            for (var i = 0; i < 10; i++) {
                var div = document.querySelector("#container div:nth-child(" + i + ")");
                div.addEventListener("click", (function (div) {
                    return function (e) {
                        div.textContent = "clicked";
                    };
                })(div));
            }
        };
        return ActualScoping;
    })();
    tscomparison.ActualScoping = ActualScoping;
})(tscomparison || (tscomparison = {}));
tscomparison.ActualScoping.main(null);
//# sourceMappingURL=ActualScoping.js.map