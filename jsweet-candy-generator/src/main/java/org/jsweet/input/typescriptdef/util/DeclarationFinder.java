package org.jsweet.input.typescriptdef.util;

import java.util.ArrayList;
import java.util.List;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.QualifiedDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Visitable;

public class DeclarationFinder<T extends Declaration> extends Scanner {

	public interface Matcher<T extends Declaration> {
		void matches(DeclarationFinder<T> finder, Visitable visitable);
	}

	protected List<QualifiedDeclaration<T>> matches = new ArrayList<>();
	private Matcher<T> matcher;
	private boolean match = false;
	private boolean stopScan = false;

	public DeclarationFinder(Context context, Matcher<T> matcher) {
		super(context);
		this.matcher = matcher;
	}

	public DeclarationFinder(Scanner parentScanner, Matcher<T> matcher) {
		super(parentScanner);
		this.matcher = matcher;
	}

	@Override
	public void scan(Visitable visitable) {
		if (visitable != null && !visitable.isHidden()) {
			enter(visitable);
			try {
				matcher.matches(this, visitable);
				if (match) {
					@SuppressWarnings("unchecked")
					T match = (T) visitable;
					matches.add(new QualifiedDeclaration<T>(match, getCurrentDeclarationName()));
				}
				if (!stopScan) {
					visitable.accept(this);
				}
			} finally {
				exit();
			}
		}
	}

	public List<QualifiedDeclaration<T>> getMatches() {
		return matches;
	}

	/**
	 * To be called by a matcher to set the current match state of this finder.
	 * 
	 * @param match
	 *            tells is the matcher matches the current node
	 * @param continueScanning
	 *            tells if the matcher want to continue scanning or stop at the
	 *            current node
	 */
	public void setMatchState(boolean match, boolean continueScanning) {
		this.match = match;
		this.stopScan = !continueScanning;
	}

}
