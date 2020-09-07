package source.structural.defaultsOverloaded;

import java.util.List;
import java.util.Map;

public interface DuplicateMethod extends DuplicateMethodInterface {

	@Override
	 DuplicateMethod getNombrePleinSignes() ;
	@Override
	default DuplicateMethod getNombrePleinSignes(List<?> unNombrePleinSignes) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	default DuplicateMethod getNombrePleinSignes(Map<String, ?> unNombrePleinSignes) {
		// TODO Auto-generated method stub
		return null;
	}

}