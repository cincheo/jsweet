module tscomparison {
    export class ActualScoping {
        public static main(args: string[])  {
            if((true)) {
                var scoped: boolean = true;
            } else {
                var scoped: boolean = false;
            }
            for(var i: number = 0; i < 10; i++) {
                var div: Element = document.querySelector("#container div:nth-child(" + i + ")");
                div.addEventListener("click", ((div) => { return (e) => {
                    div.textContent = "clicked";
                }})(div));
            }
        }
    }
}
tscomparison.ActualScoping.main(null);

