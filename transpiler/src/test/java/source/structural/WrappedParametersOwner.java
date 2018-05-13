package source.structural;

import java.util.Date;
import java.util.List;

import jsweet.lang.WrapParameters;

public class WrappedParametersOwner {

	public String msg;
	private Date date;
	protected Integer quantity;
	public String[] items;

	@WrapParameters
	public WrappedParametersOwner(String msg, Date date, Integer quantity, String[] items) {
		super();
		this.msg = msg;
		this.date = date;
		this.quantity = quantity;
		this.items = items;
		System.out.println("ctor_params: " + this.msg + ";" + this.date + ";" + this.quantity + ";" + this.items);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * Gets the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	@WrapParameters
	public void action(boolean p1, def.js.Object p2, List<Date> p3) {
		System.out.println("action_params" + p1 + ";" + p2 + ";" + p3);
	}
}
