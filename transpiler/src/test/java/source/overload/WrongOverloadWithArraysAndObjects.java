package source.overload;

import static jsweet.util.Lang.$export;

public class WrongOverloadWithArraysAndObjects {

	public static void main(String[] args) {
		new ReservationDto();
		new ReservationDto(1L, "1:00", 2.0, new PostalAddressDto(), new String[] { "1", "2" });
	}

}

class MappedDto {

}

class PostalAddressDto {

}

class ReservationDto extends MappedDto {
	public Long timeStamp;
	public String duration;
	public Double price;
	public PostalAddressDto address;
	public String[] careIds;

	public ReservationDto() {
	}

	public ReservationDto(Long timeStamp, String duration, Double price, PostalAddressDto address, String[] careIds) {
		super();
		this.timeStamp = timeStamp;
		this.duration = duration;
		this.price = price;
		this.address = address;
		this.careIds = careIds;
	}

}
