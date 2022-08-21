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
package org.jsweet.test.transpiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jsweet.transpiler.ModuleKind;
import org.junit.Ignore;
import org.junit.Test;

import source.svregression.DefaultFieldInitialization;

public class AAARegressionTests extends AbstractTest {


    @Test
    public void testDefaultFieldInitialization() {
        eval(ModuleKind.none, (logHandler, r) -> {
            logHandler.assertNoProblems();
            assertEquals((int) 44, (int) r.get("1ary"));
            assertEquals((int) 23, (int) r.get("0ary"));
        }, getSourceFile(DefaultFieldInitialization.class));
    }
}
