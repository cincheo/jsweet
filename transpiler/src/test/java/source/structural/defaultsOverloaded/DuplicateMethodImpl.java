package source.structural.defaultsOverloaded;

import java.util.List;
import java.util.Map;

public class DuplicateMethodImpl extends AbstractDuplicateMethod implements DuplicateMethod {

	@Override
	public DuplicateMethod getNombrePleinSignes() {
		// TODO Auto-generated method stub
		return (DuplicateMethod) super.getNombrePleinSignes();
	}

	@Override
	public DuplicateMethod getNombrePleinSignes(List<?> unNombrePleinSignes) {
		// TODO Auto-generated method stub
		return (DuplicateMethod) super.getNombrePleinSignes(unNombrePleinSignes);
	}

	@Override
	public DuplicateMethod getNombrePleinSignes(Map<String, ?> unNombrePleinSignes) {
		// TODO Auto-generated method stub
		return (DuplicateMethod) super.getNombrePleinSignes(unNombrePleinSignes);
	}
}