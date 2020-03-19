package source.structural.defaultsOverloaded;

import java.util.List;
import java.util.Map;

public interface DuplicateMethodInterface {

	DuplicateMethodInterface getNombrePleinSignes();

	DuplicateMethodInterface getNombrePleinSignes(List<?> unNombrePleinSignes);
	DuplicateMethodInterface getNombrePleinSignes(Map<String, ?> unNombrePleinSignes);
}
