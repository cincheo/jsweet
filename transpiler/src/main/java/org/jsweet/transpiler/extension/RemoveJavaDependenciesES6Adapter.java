/*
 * JSweet transpiler - http://www.jsweet.org
 * Copyright (C) 2019 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.transpiler.extension;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;

/**
 * An adapter that removes many uses of Java APIs and replace them with
 * JavaScript ES6 equivalent when possible.
 */

public class RemoveJavaDependenciesES6Adapter extends RemoveJavaDependenciesAdapter {
    private Set<String> setClassNames = Stream.of(
            Set.class, HashSet.class, LinkedHashSet.class, TreeSet.class, AbstractSet.class)
            .map(Class::getName)
            .collect(Collectors.toSet());

    public RemoveJavaDependenciesES6Adapter(PrinterAdapter parentAdapter) {
        super(parentAdapter);
    }

    @Override
    protected void initTypesMapping() {
        setClassNames = Stream.of(Set.class, HashSet.class, AbstractSet.class)
            .map(Class::getName)
            .collect(Collectors.toSet());

        super.initTypesMapping();
        setClassNames.forEach(name -> extTypesMapping.put(name, "any"));
    }

    @Override
    protected RemoveJavaDependenciesES6Adapter print(ExtendedElement expression, boolean delegate) {
        super.print(expression, delegate);
        return this;
    }

    @Override
    protected RemoveJavaDependenciesES6Adapter printTargetForParameter(ExtendedElement expression, boolean delegate) {
        super.printTargetForParameter(expression, delegate);
        return print(expression, delegate);
    }

    @Override
    public boolean substituteNewClass(NewClassElement newClass) {
        String className = newClass.getTypeAsElement().toString();

        if (setClassNames.contains(className)) {
            this.substituteNewSet(newClass);
            return true;
        }

        return super.substituteNewClass(newClass);
    }

    private void substituteNewSet(NewClassElement newClass) {
        boolean ignoreArguments = newClass.getArgumentCount() == 0 ||
                Integer.class.getName().equals(newClass.getArgument(0).getType().toString()) ||
                "int".equals(newClass.getArgument(0).getType().toString());

        if (ignoreArguments) {
            print(String.format(
                    "(() => { const col = %s; col.data = new Set(); col.iterator = %s; return col; })()",
                    createNewCollection(newClass.getType().toString()),
                    "() => { let i = 0; const a = Array.from(col.data.values()); return { next: () => i < a.length? a[i++] : null, hasNext: () => i < a.length }; }"
            ));
        } else {
            print(String.format(
                    "((arg) => { const col = %s; col.data = new Set(); col.iterator = %s; %s return col; })(",
                    createNewCollection(newClass.getType().toString()),
                    "() => { let i = 0; const a = Array.from(col.data.values()); return { next: () => i < a.length? a[i++] : null, hasNext: () => i < a.length }; }",
                    "const it = arg.iterator(); while(it.hasNext()) col.data.add(it.next());"
            )).print(newClass.getArgument(0)).print(")");
        }
    }

    @Override
    public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
        String targetClassName = invocation.getMethod().getEnclosingElement().toString();
        ExtendedElement targetExpression = invocation.getTargetExpression();
        if (targetExpression != null) {
            targetClassName = targetExpression.getTypeAsElement().toString();
        }

        if (setClassNames.contains(targetClassName)) {
            this.substituteMethodOnSet(invocation);
            return true;
        }

        return super.substituteMethodInvocation(invocation);
    }

    private void substituteMethodOnSet(MethodInvocationElement invocation) {
        String targetMethodName = invocation.getMethodName();
        ExtendedElement targetExpression = invocation.getTargetExpression();

        switch (targetMethodName) {
            case "add":
                printMacroName(targetMethodName);
                print("((s, v) => { const n = s.data.size; s.data.add(v); return n !== s.data.size; })(")
                        .print(targetExpression)
                        .print(",")
                        .print(invocation.getArgument(0))
                        .print(")");
                break;
            case "addAll":
                printMacroName(targetMethodName);
                print("((s, c) => { const it = c.iterator(); const n = s.data.size; while(it.hasNext()) s.data.add(it.next()); return n !== s.data.size; })(")
                        .print(targetExpression)
                        .print(",")
                        .print(invocation.getArgument(0))
                        .print(")");
                break;
            case "clear":
                printMacroName(targetMethodName);
                print(targetExpression)
                        .print(".data.clear()");
                break;
            case "contains":
                printMacroName(targetMethodName);
                print(targetExpression)
                        .print(".data.has(")
                        .print(invocation.getArgument(0))
                        .print(")");
                break;
            case "containsAll":
                printMacroName(targetMethodName);
                print("((s, c) => { const it = c.iterator(); while(it.hasNext()) if (!s.has(it.next())) return false; return true; })(")
                        .print(targetExpression)
                        .print(",")
                        .print(invocation.getArgument(0))
                        .print(")");
                break;
            case "equals":
                printMacroName(targetMethodName);
                print("((s1, s2) => { if (!s1 || !s2) return s1 === s2; const it1 = s1.iterator(); const it2 = s2.iterator(); while(it1.hasNext()) if (it1.next() !== it2.next()) return false; return !it2.hasNext(); })(")
                        .print(targetExpression)
                        .print(",")
                        .print(invocation.getArgument(0))
                        .print(")");
                break;
            case "hashCode":
                printMacroName(targetMethodName);
                report(invocation, JSweetProblem.USER_ERROR, "hashCode() is not supported.");
                break;
            case "isEmpty":
                printMacroName(targetMethodName);
                print("(")
                        .print(targetExpression)
                        .print(".data.size === 0")
                        .print(")");
                break;
            case "iterator":
                printMacroName(targetMethodName);
                print(targetExpression).print(".iterator()");
                break;
            case "remove":
                printMacroName(targetMethodName);
                print("(")
                        .print(targetExpression)
                        .print(".data.delete(")
                        .print(invocation.getArgument(0))
                        .print(")")
                        .print(")");
                break;
            case "removeAll":
                    printMacroName(targetMethodName);
                    print("((s, c) => { const it = c.iterator(); const n = s.data.size; while (it.hasNext()) s.data.delete(it.next()); return n !== s.data.size; })(")
                            .print(targetExpression)
                            .print(",")
                            .print(invocation.getArgument(0))
                            .print(")");
                    break;
            case "retainAll":
                printMacroName(targetMethodName);
                print("((s, c) => { const n = s.data.size; const s2 = new Set(); const it = c.iterator(); while(it.hasNext()) s2.add(it.next()); s.data.forEach(v => { if(!s2.has(v)) s.data.delete(v); }); return n !== s.data.size; })(")
                        .print(targetExpression)
                        .print(",")
                        .print(invocation.getArgument(0))
                        .print(")");
                break;
            case "size":
                printMacroName(targetMethodName);
                print(targetExpression).print(".data.size");
                break;
            case "toArray":
                printMacroName(targetMethodName);
                print("(")
                        .print("Array.from(")
                        .print(targetExpression)
                        .print(".data)")
                        .print(")");
                break;
            default:
                report(invocation, JSweetProblem.USER_ERROR, targetMethodName + " is not supported.");
        }
    }

    private String createNewCollection(String className) {
        return String.format("{ className: '%s', data: null, iterator: null }", className);
    }
}
