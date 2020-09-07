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

import static def.dom.Globals.console;
import static def.dom.Globals.document;
import static jsweet.util.StringTypes.click;
import static jsweet.util.StringTypes.div;

import java.util.function.Consumer;

import def.dom.Element;
import def.dom.HTMLDivElement;
import def.dom.HTMLElement;
import def.dom.NodeList;
import def.dom.NodeListOf;
import def.js.Date;
import def.js.Math;

public class FinalVariables {

    int finalField;
    int modifiedField;

    void m1(HTMLElement e) {
        modifiedField++;

        NodeList nodes = document.querySelectorAll("form .form-control");
        for (int i = 0; i < nodes.length; i++) {
            HTMLElement element = (HTMLElement) nodes.$get(i);
            element.addEventListener("keyup", (evt) -> {
                console.log("typing...");
                element.classList.add("hit");
            });
        }
    }

    void m2(HTMLElement e) {
        NodeListOf<Element> elements = document.querySelectorAll("form .form-control");
        for (Element element : elements) {
            element.addEventListener("keyup", (evt) -> {
                console.log("typing...");
                // element.classList.add("hit");
            });
        }
    }

    private HTMLDivElement spinner;

    Promise<Double> spawnProgressBar(int index) {

        HTMLDivElement progressBackground = document.createElement(div);
        progressBackground.classList.add("background");

        HTMLDivElement progressText = document.createElement(div);
        progressText.classList.add("text");

        HTMLDivElement progress = document.createElement(div);
        progress.classList.add("progress");
        progress.appendChild(progressBackground);
        progress.appendChild(progressText);

        HTMLDivElement bar = document.createElement(div);
        bar.classList.add("bar");
        bar.id = "bar" + index;
        bar.dataset.$set("progress", "0");
        bar.addEventListener(click, (event) -> {
            double newProgress = Math.round((100 * (bar.clientHeight - event.clientY) / bar.clientHeight));
            console.log("clicked on " + event.offsetY + " percent=" + newProgress + " height=" + bar.clientHeight);
            bar.dataset.$set("progress", "" + newProgress);
            return null;
        });
        bar.appendChild(progress);
        this.spinner.appendChild(bar);

        double startTime = new Date().getTime();
        return new Promise<Double>((Callback<Consumer<Double>, Consumer<Object>>) //
        (resolve, reject) -> {
            this.onProgress(bar, resolve, reject, startTime);
        });
    }

    void onProgress(HTMLDivElement progressBar, Consumer<Double> resolve, Consumer<Object> reject, double startTime) {

    }

    void handler(ANonFunctionalInterface i) {

    }

    void explicitFinal() {
        final String explicitFinalString = "abc";
        handler(new ANonFunctionalInterface() {
            @Override
            public void m() {
                System.out.println(explicitFinalString);
            }
        });
    }

    void implicitFinal() {
        String implicitFinalString = "abc";
        handler(new ANonFunctionalInterface() {
            @Override
            public void m() {
                System.out.println(implicitFinalString);
            }
        });
    }

    String notFinal() {
        String notFinalString = "abc";
        notFinalString = "bcd";
        return notFinalString;
    }

}

// subset of promises API
class Promise<R> {
    public Promise(Callback<java.util.function.Consumer<R>, java.util.function.Consumer<Object>> callback) {
    }
}

@java.lang.FunctionalInterface
interface Callback<T1, T2> {
    public void apply(T1 p1, T2 p2);
}

interface ANonFunctionalInterface {
    void m();
}
