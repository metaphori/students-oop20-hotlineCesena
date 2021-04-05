package hotlinecesena.controller.generator;

import java.util.List;
import java.util.Map;

import hotlinecesena.model.dataccesslayer.SIMBOLS_TYPE;
import javafx.util.Pair;

public interface WorldGeneratorBuilder {
	
	WorldGeneratorBuilder addSingleBaseRoom(Room r);
	
	WorldGeneratorBuilder addSomeBaseRoom(List<Room> list);
	
	WorldGeneratorBuilder generateRooms(int nRoomsMin, int nRoomsMax);
	
	WorldGeneratorBuilder generatePlayer();

	WorldGeneratorBuilder generateEnemy(int minRoom, int maxRoom);

	WorldGeneratorBuilder generateObstacoles(int minRoom, int maxRoom);

	WorldGeneratorBuilder generateAmmo(int minRoom, int maxRoom);
	
	WorldGeneratorBuilder generateMedikit(int minRoom, int maxRoom);

	public WorldGeneratorBuilder finishes();

	WorldGeneratorBuilder build();

	Map<Pair<Integer, Integer>, SIMBOLS_TYPE> getMap();

	int getMinX();

	int getMaxX();

	int getMinY();

	int getMaxY();
}