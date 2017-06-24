package source.enums;

public abstract class ComplexInnerEnums {

	public enum Axis {
		ALL, VERTICAL, HORIZONTAL;

		public boolean horizontal() {
			return this.ordinal() == ALL.ordinal() || this.ordinal() == Axis.HORIZONTAL.ordinal();
		}

		public boolean vertical() {
			return this == ALL || this == Axis.VERTICAL;
		}

		public boolean other() {
			return !this.vertical() && !horizontal();
		}

	}

	private String icon;
	private String title;
	private String description;
	private Axis axis;

	/**
	 * Creates a new control.
	 * 
	 * @param icon
	 *            the icon name/path
	 * @param title
	 *            the title of the control
	 * @param description
	 *            a short description
	 * @param axis
	 *            the axis on which the control can operate
	 */
	public ComplexInnerEnums(String icon, String title, String description, Axis axis) {
		super();
		this.icon = icon;
		this.title = title;
		this.description = description;
		this.axis = axis;
	}

	public String getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Axis getAxis() {
		return axis;
	}

}