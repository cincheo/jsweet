package source.enums.samepackage;

public enum Varbits
{
	TEST(1);

	private final int id;

	Varbits(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
}