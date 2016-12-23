package source.api;

import javax.xml.bind.annotation.XmlRootElement;

import jsweet.lang.Erased;

@XmlRootElement
public class ErasingJava {
	private int id;
	private String sportIdent;
	private String name;
	private String firstName;
	private int birthYear;
	private String gender;
	private String clubNumber;
	private String clubName;
	private String categoryName;

	public ErasingJava() {
	}

	public int getId() {
		return id;
	}

	public ErasingJava setId(int id) {
		this.id = id;
		return this;
	}

	public String getSportIdent() {
		return sportIdent;
	}

	public ErasingJava setSportIdent(String sportIdent) {
		this.sportIdent = sportIdent;
		return this;
	}

	public String getName() {
		return name;
	}

	public ErasingJava setName(String name) {
		this.name = name;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public ErasingJava setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public ErasingJava setBirthYear(int birthYear) {
		this.birthYear = birthYear;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public ErasingJava setGender(String gender) {
		this.gender = gender;
		return this;
	}

	public String getClubNumber() {
		return clubNumber;
	}

	public ErasingJava setClubNumber(String clubNumber) {
		this.clubNumber = clubNumber;
		return this;
	}

	public String getClubName() {
		return clubName;
	}

	public ErasingJava setClubName(String clubName) {
		this.clubName = clubName;
		return this;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public ErasingJava setCategoryName(String categoryName) {
		this.categoryName = categoryName;
		return this;
	}

	@Override
	@Erased
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("#").append(id).append(": ").append(firstName).append(" ").append(name).append(" (").append(sportIdent).append(")");
		return builder.toString();
	}

	@Override
	@Erased
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + birthYear;
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
		result = prime * result + ((clubName == null) ? 0 : clubName.hashCode());
		result = prime * result + ((clubNumber == null) ? 0 : clubNumber.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((sportIdent == null) ? 0 : sportIdent.hashCode());
		return result;
	}

	@Override
	@Erased
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ErasingJava)) {
			return false;
		}
		ErasingJava other = (ErasingJava) obj;
		if (birthYear != other.birthYear) {
			return false;
		}
		if (categoryName == null) {
			if (other.categoryName != null) {
				return false;
			}
		} else if (!categoryName.equals(other.categoryName)) {
			return false;
		}
		if (clubName == null) {
			if (other.clubName != null) {
				return false;
			}
		} else if (!clubName.equals(other.clubName)) {
			return false;
		}
		if (clubNumber == null) {
			if (other.clubNumber != null) {
				return false;
			}
		} else if (!clubNumber.equals(other.clubNumber)) {
			return false;
		}
		if (firstName == null) {
			if (other.firstName != null) {
				return false;
			}
		} else if (!firstName.equals(other.firstName)) {
			return false;
		}
		if (gender == null) {
			if (other.gender != null) {
				return false;
			}
		} else if (!gender.equals(other.gender)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (sportIdent == null) {
			if (other.sportIdent != null) {
				return false;
			}
		} else if (!sportIdent.equals(other.sportIdent)) {
			return false;
		}
		return true;
	}
}
