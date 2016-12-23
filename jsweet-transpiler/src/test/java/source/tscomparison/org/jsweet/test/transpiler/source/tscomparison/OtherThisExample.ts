"Generated from Java with JSweet 1.0.0-SNAPSHOT - http://www.jsweet.org";
module org.jsweet.test.transpiler.source.tscomparison {
    export class Operation {
        x: number;

        public constructor(x: number)  {
            this.x = x;
        }

        public add(number: number, d: number = null, ds: number[] = null) : number {
            return number + this.x;
        }
    }

    export class OtherThisExample {
        public static main(args: string[])  {
            var numbers: number[] = [1, 2, 3];
            var twoOperation: Operation = new Operation(2);
            var results: number[] = numbers.map((number,d,ds) => { return twoOperation.add(number,d,ds) });
            _exportedVar_results = results; console.log('%%%results@@@'+_exportedVar_results+'%%%');
        }
    }
}
declare var _exportedVar_results;

org.jsweet.test.transpiler.source.tscomparison.OtherThisExample.main(null);

