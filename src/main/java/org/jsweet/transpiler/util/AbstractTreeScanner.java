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
package org.jsweet.transpiler.util;

import java.io.File;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.SourcePosition;
import org.jsweet.transpiler.TranspilationHandler;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.DiagnosticSource;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

/**
 * 
 * @author Renaud Pawlak
 */
public abstract class AbstractTreeScanner extends TreeScanner {

	private TranspilationHandler logHandler;

	public void report(JCTree tree, JSweetProblem problem, Object... params) {
		report(tree, null, problem, params);
	}

	public void report(JCTree tree, Name name, JSweetProblem problem, Object... params) {
		if (logHandler == null) {
			System.err.println(problem.getMessage(params));
		} else {
			if (diagnosticSource == null) {
				logHandler.report(problem, null, problem.getMessage(params));
			} else {
				int s = tree.getStartPosition();
				int e = tree.getEndPosition(diagnosticSource.getEndPosTable());
				if (e == -1) {
					e = s;
				}
				if (name != null) {
					e += name.length();
				}
				logHandler.report(problem,
						new SourcePosition(new File(compilationUnit.sourcefile.getName()), tree, diagnosticSource.getLineNumber(s),
								diagnosticSource.getColumnNumber(s, false), diagnosticSource.getLineNumber(e), diagnosticSource.getColumnNumber(e, false)),
						problem.getMessage(params));
			}
		}
	}

	protected Stack<JCTree> stack = new Stack<JCTree>();

	protected Map<String, JCImport> staticImports = new HashMap<>();

	public Map<String, JCImport> getStaticImports() {
		return staticImports;
	}

	protected JCCompilationUnit compilationUnit;

	public JCCompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	protected JSweetContext context;

	public JSweetContext getContext() {
		return context;
	}

	protected DiagnosticSource diagnosticSource;

	private Entry<String, String[]> sourceCache;

	protected String[] getGetSource(JCCompilationUnit compilationUnit) {
		if (sourceCache != null && sourceCache.getKey().equals(compilationUnit.getSourceFile().getName())) {
			return sourceCache.getValue();
		} else {
			try {
				sourceCache = new AbstractMap.SimpleEntry<>(compilationUnit.getSourceFile().getName(),
						FileUtils.readFileToString(new File(compilationUnit.getSourceFile().getName())).split("\\n"));
			} catch (Exception e) {
				return null;
			}
			return sourceCache.getValue();
		}
	}

	public AbstractTreeScanner(TranspilationHandler logHandler, JSweetContext context, JCCompilationUnit compilationUnit) {
		this.logHandler = logHandler;
		this.context = context;
		this.context.symtab = Symtab.instance(context);
		this.context.names = Names.instance(context);
		this.context.types = Types.instance(context);
		this.setCompilationUnit(compilationUnit);
	}

	protected final void setCompilationUnit(JCCompilationUnit compilationUnit) {
		if (compilationUnit != null) {
			this.compilationUnit = compilationUnit;
			for (JCImport i : this.compilationUnit.getImports()) {
				if (i.staticImport) {
					staticImports.put(i.qualid.toString().substring(i.qualid.toString().lastIndexOf(".") + 1), i);
				}
			}
			this.diagnosticSource = new DiagnosticSource(compilationUnit.sourcefile, Log.instance(context));
		} else {
			this.compilationUnit = null;
			this.diagnosticSource = null;
		}
	}

	@Override
	public void scan(JCTree tree) {
		if (tree == null) {
			return;
		}
		enter(tree);
		try {
			tree.accept(this);
		} catch (RollbackException rollback) {
			if (rollback.getTarget() == tree) {
				onRollbacked(tree);
				if (rollback.getOnRollbacked() != null) {
					rollback.getOnRollbacked().accept(tree);
				}
			} else {
				throw rollback;
			}
		} catch (Exception e) {
			report(tree, JSweetProblem.INTERNAL_TRANSPILER_ERROR);
			dumpStackTrace();
			e.printStackTrace();
		} finally {
			exit();
		}
	}

	protected void dumpStackTrace() {
		System.err.println("dumping transpiler's strack trace:");
		for (int i = stack.size() - 1; i >= 0; i--) {
			JCTree tree = stack.get(i);
			if (tree == null) {
				continue;
			}
			String str = tree.toString().trim();
			int intialLength = str.length();
			int index = str.indexOf('\n');
			if (index > 0) {
				str = str.substring(0, index + 1);
			}
			str = str.replace('\n', ' ');
			str = str.substring(0, Math.min(str.length() - 1, 30));
			System.err.print("   [" + stack.get(i).getClass().getSimpleName() + "] " + str + (str.length() < intialLength ? "..." : "") + " ("
					+ compilationUnit.getSourceFile().getName() + ":");
			if (diagnosticSource == null) {
				System.err.println(tree.pos + ")");
			} else {
				System.err.println(diagnosticSource.getLineNumber(tree.pos) + ")");
			}
		}
	}

	protected void onRollbacked(JCTree target) {
	}

	protected void rollback(JCTree target, Consumer<JCTree> onRollbacked) {
		throw new RollbackException(target, onRollbacked);
	}

	protected void enter(JCTree tree) {
		stack.push(tree);
	}

	protected void exit() {
		stack.pop();
	}

	public Stack<JCTree> getStack() {
		return this.stack;
	}

	public JCTree getParent() {
		if (this.stack.size() >= 2) {
			return this.stack.get(this.stack.size() - 2);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends JCTree> T getParent(Class<T> type) {
		for (int i = this.stack.size() - 2; i >= 0; i--) {
			if (type.isAssignableFrom(this.stack.get(i).getClass())) {
				return (T) this.stack.get(i);
			}
		}
		return null;
	}

	public JCTree getFirstParent(Class<?>... types) {
		for (int i = this.stack.size() - 2; i >= 0; i--) {
			for (Class<?> type : types) {
				if (type.isAssignableFrom(this.stack.get(i).getClass())) {
					return this.stack.get(i);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends JCTree> T getParent(Class<T> type, JCTree from) {
		for (int i = this.stack.size() - 1; i >= 0; i--) {
			if (this.stack.get(i) == from) {
				for (int j = i - 1; j >= 0; j--) {
					if (type.isAssignableFrom(this.stack.get(j).getClass())) {
						return (T) this.stack.get(j);
					}
				}
				return null;
			}
		}
		return null;
	}

}
