package source.api;

import static jsweet.util.Lang.$export;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class ExpressionBuilderTest {

	public static class ExpressionBuilder<T> {

		private final int defaultLevel;
		private final BinaryOperator<T>[] operations;
		private String[] operationStrings;

		private final Stack<UnaryOperator<T>> unary = new Stack<>();
		private final ExpressionBuilder<T> parent;
		private final Stack<Integer> levelHistory;
		private final Deque<T>[] elements;
		private int currentLevel;
		private boolean operationDeclared;

		// public ExpressionBuilder(int defaultLevel, BinaryOperator<T>...
		// precedence) {
		// this(null, defaultLevel, precedence);
		// }

		private ExpressionBuilder(ExpressionBuilder<T> parent, int defaultLevel, BinaryOperator<T>... precedence) {
			this.operations = precedence;
			this.parent = parent;
			this.defaultLevel = defaultLevel;
			this.levelHistory = new Stack<>();
			this.currentLevel = 0;
			this.operationDeclared = true;
			this.elements = new LinkedList[operations.length];
			for (int i = 0; i < elements.length; ++i) {
				this.elements[i] = new LinkedList<>();
			}
		}

		public ExpressionBuilder<T> add(T element) {
			if (element == null) {
				throw new NullPointerException();
			}
			if (!operationDeclared) {
				if (defaultLevel < 0) {
					throw new IllegalArgumentException("two elements in row");
				} else {
					insertOperation(defaultLevel);
				}
			}
			while (!unary.isEmpty()) {
				element = unary.pop().apply(element);
			}
			elements[currentLevel].addLast(element);
			operationDeclared = false;

			return this;
		}

		private T evaluate(int level) {
			Deque<T> q = elements[level];
			BinaryOperator<T> op = operations[level];
			T result = q.pollFirst();
			while (!q.isEmpty()) {
				result = op.apply(result, q.pollFirst());
			}
			return result;
		}

		private void insertOperation(int i) {
			while (currentLevel > i) {
				T result = evaluate(currentLevel);
				currentLevel = levelHistory.pop();
				elements[currentLevel].addLast(result);
			}
			if (i > currentLevel) {
				elements[i].addLast(elements[currentLevel].pollLast());
				levelHistory.add(currentLevel);
			}
			currentLevel = i;
			operationDeclared = true;
		}

		public ExpressionBuilder<T> operation(UnaryOperator<T> op) {
			unary.add(op);
			return this;
		}

		public ExpressionBuilder<T> operation(int number) {
			if (operationDeclared) {
				throw new IllegalStateException("cannot insert two operations in row");
			}
			if (number < 0 || number >= operations.length) {
				throw new ArrayIndexOutOfBoundsException("unknown operation number");
			}
			insertOperation(number);
			return this;
		}

		public ExpressionBuilder<T> push() {
			return new ExpressionBuilder<>(this, defaultLevel, operations);
		}

		public ExpressionBuilder<T> pop() {
			if (parent == null) {
				throw new IllegalStateException("non-matched brackets detected");
			} else {
				parent.add(build());
				return parent;
			}
		}

		public T build() {
			while (true) {
				T result = evaluate(currentLevel);
				if (levelHistory.isEmpty()) {
					return result;
				}
				currentLevel = levelHistory.pop();
				elements[currentLevel].addLast(result);
			}
		}
	}

	public static class Function {

		final String name;
		final UnaryOperator<Double> func;

		private Function(String name, UnaryOperator<Double> func) {
			this.name = name;
			this.func = func;
		}

		public UnaryOperator<Double> getFunc() {
			return func;
		}
	}

	public static Function[] functions = { new Function("sin", Math::sin), new Function("cos", Math::cos), new Function("tg", Math::tan),
			new Function("ctg", x -> 1 / Math.tan(x)), new Function("exp", Math::exp), new Function("sqrt", Math::sqrt), new Function("ln", Math::log),
			new Function("abs", Math::abs) };

	public static void main(String[] args) {
		$export("result", testCalc());
	}

	public static int testCalc() {
		ExpressionBuilder<Double> builder;
		builder = new ExpressionBuilder<>(null, -1, (a, b) -> a + b, // 0
				(a, b) -> a - b, // 1
				(a, b) -> a * b, // 2
				(a, b) -> a / b, // 3
				(a, b) -> Math.pow(a, b) // 4
		);

		// equation is "1-8*9+(5^(1+(2/2))*2^2)+abs(0-cos(0))"
		Double r1 = builder.add(1.).operation(1).add((double) 8f).operation(2).add(9.0).operation(0).push()
				.add(5.000000000000000000000000000000000000000000000000000000000000).operation(4).push().add((double) 1.000f).operation(0).push().add(+2.0)
				.operation(3).add(2.).pop().pop().operation(2).add((double) (int) (float) 2).operation(4).add((double) 2).pop().operation(0)
				.operation(functions[functions.length - 1].func).push().add(-0.0).operation(1).operation(functions[1].getFunc()).add(+0.0).pop().build();

		// result is 30
		return ((int) (double) r1);
	}
}
