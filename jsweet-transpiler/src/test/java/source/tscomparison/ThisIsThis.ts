module org.jsweet.prosncons {
    class ThisIsThis {

        private i: number = 8;

        public getAction(): () => void {
            return this.action;
        }

        public action(): void {
            console.log(this.i); // this is window in this calling context
        }
    }

    var instance: ThisIsThis = new ThisIsThis();
    instance.getAction()();
}