module org.jsweet.test.source.tscomparison {
    export class AbstractClass {
        private name: string;

        constructor(name: string)  {
            this.name = name;
        }

        public action() : Object { return null; }
    }

    export class AbstractClassImpl extends AbstractClass {
        public constructor()  {
            super("DemoImpl");
        }

        public action() : string {
            return "done";
        }
    }

    export class AbstractClasses {
        public static main(args: string[])  {
            new AbstractClassImpl();
        }
    }
}
org.jsweet.test.source.tscomparison.AbstractClasses.main(null);

