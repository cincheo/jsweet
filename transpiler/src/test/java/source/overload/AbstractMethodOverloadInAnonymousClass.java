package source.overload;

public abstract class AbstractMethodOverloadInAnonymousClass {

	public abstract double m(double t);

	public abstract double m(double t, double u, double v);

	public static AbstractMethodOverloadInAnonymousClass create(final int v) {
		return new AbstractMethodOverloadInAnonymousClass() {
			public double m(double t) {
				return m(t, 0, 1);
			}

			public double m(double t, double u, double v) {
				return v;
			}
		};
	}

}
