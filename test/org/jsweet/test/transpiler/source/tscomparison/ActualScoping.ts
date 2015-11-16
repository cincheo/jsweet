"use script"

module org.jsweet.prosncons {
    if (true) {
        var scoped = true;
    } else {
        // scoped is not defined - potential problem
        scoped = false;
    }

    for (var i = 0; i < 10; i++) {
        var div = document.querySelector("#container div:nth-child(" + i + ")");
        div.addEventListener("click",(e) => {
            /*
             * div is not div, as expected!!
             * div is the last div value in the parent scope, which means, the tenth div, which is not what we want
             */
            div.textContent = "clicked";
        });
    }
}