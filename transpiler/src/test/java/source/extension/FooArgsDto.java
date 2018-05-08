package source.extension;

import java.util.Date;

import jsweet.lang.Interface;
import jsweet.lang.Wrapped;

public class FooArgsDto {

	@Interface
	public class FooArgsDtoParameter
	{
		public String msg;
		public Date date;
		public Integer quantity;
		public String[] items;

	}
	
	public String msg;
	private Date date;
	protected Integer quantity;
	public String[] items;

	@Wrapped(target=FooArgsDtoParameter.class)
	public FooArgsDto(String msg, Date date, Integer quantity, String[] items)
	{
		super();
		this.msg = msg;
		this.date = date;
		this.quantity = quantity;
		this.items = items;
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

}
