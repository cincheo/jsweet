/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package source.syntax;

import static jsweet.util.Lang.$export;

import java.util.ArrayList;
import java.util.List;

import def.js.Array;
import source.syntax.function.KeywordWithPackage;

public class Keywords {

    static Array<String> trace = new Array<String>();

    public static void main(String[] arguments) {
        Keywords k = new Keywords("a");
        trace.push(k.in);
        k.m();
        k.m2(1, 2);
        $export("trace", trace.join(","));

        k.iteration();

        assert 2 == new Other1().export();
        assert 2 == new Other1().let();
        
        List<String> s = new ArrayList<>();
        s.add("first");
        String r = new KeywordWithPackage().function(s);
        assert r == "first";
        
        var myVar = "test";
        assert myVar == "test";
    }

    String in;

    Keywords(String in) {
        super();
        this.in = in;
        String arguments = "";
        System.out.println(arguments);
    }

    Keywords(String in, int i) {
        super();
        this.in = in;
    }

    void var(String s, int i) {
        int eval = 4;
        eval = eval + i;
    }

    void f(String in) {
        this.in = in;
    }

    void f(String in, String prefix) {
        this.in = prefix + in;
        var(in, in.length());
    }

    void function(String typeof, int i) {
        typeof = "";
    }

    void iteration() {
        List<String> l = new ArrayList<>();
        l.add("a");
        l.add("b");
        l.add("c");

        String s = "";
        for (String function : l) {
            s += function;
        }
        assert s.equals("abc");
    }

    void m() {
        Integer var = 1;
        String function = "f";
        trace.push("" + var);
        trace.push(function);
    }

    void m2(int var, long function) {
        var = 2;
        var = (int) function;
        trace.push("" + var);
        String constructor = "abc";
        trace.push(constructor);
        String delete = "abc";
        assert delete == "abc";

        var varKeyword = "coucou";
        varKeyword = "otherForVarKeyword";
        $export("varKeyword", varKeyword);

        String with = this.with(3);
        $export("with", with);
    }

    String with(int with) {
        return "test" + with;
    }

}

class Other1 {

    int var;

    String function;

    public int export() {
        int export = 2;
        return export;
    }

    public int let() {
        int let = 2;
        return let;
    }
}

class Other2 {

    public void function() {
    }

    public int var() {
        this.function();
        return 0;
    }

}