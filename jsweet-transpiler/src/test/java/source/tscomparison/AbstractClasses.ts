
module org.jsweet.prosncons {

    class AbstractClass {
        private name: string;
        constructor(name: string) {
            this.name = name;
        }

        public action(): Object {
            throw "not implemented";    
        }
    }

    class AbstractClassImpl extends AbstractClass {
        constructor() {
            // strong constraint on constructor inheritance
            super("DemoImpl");
        }
        
        public action(): string {
            // weak constraint on abstract method implementation
            return "demo";    
        }
    }
}