package source.overload;

public abstract class AbstractMethodOverloadInInnerClass {

	public abstract double m(double t);

	public abstract double m(double t, double u, double v);

	public static AbstractMethodOverloadInInnerClass create(final int v) {
		return new AbstractMethodOverloadInInnerClass() {
			public double m(double t) {
				return m(t, 0, 1);
			}

			public double m(double t, double u, double v) {
				return v;
			}
		};
	}

}
