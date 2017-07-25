package source.extension;

import java.util.Date;

/**
 * A Hello World DTO.
 * 
 * @author Renaud Pawlak
 */
public class HelloWorldDto {

	private String msg;
	private Date date;

	public HelloWorldDto(String msg) {
		super();
		this.msg = msg;
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
