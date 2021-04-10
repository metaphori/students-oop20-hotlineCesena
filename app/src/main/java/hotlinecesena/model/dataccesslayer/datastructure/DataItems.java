package hotlinecesena.model.dataccesslayer.datastructure;

import java.util.Map;
import java.util.Random;
import javafx.geometry.Point2D;
import hotlinecesena.model.dataccesslayer.AbstractData;
import hotlinecesena.model.dataccesslayer.JSONDataAccessLayer;
import hotlinecesena.model.dataccesslayer.SymbolsType;
import hotlinecesena.model.entities.items.ItemsType;
import hotlinecesena.utilities.Utilities;
import static java.util.stream.Collectors.toMap;

public class DataItems extends AbstractData {

	private final Map<Point2D, ItemsType> items;
	
	public DataItems(DataWorldMap world, DataJSONSettings settings) {
		Random rnd = new Random();
		rnd.setSeed(JSONDataAccessLayer.SEED);
		items = world.getWorldMap().entrySet().stream()
				.filter(itm -> isItem(itm.getValue()))
				.collect(toMap(
						itm -> Utilities.convertPairToPoint2D(itm.getKey(), settings.getTileSize()), 
						itm -> ItemsType.values()[rnd.nextInt(ItemsType.values().length)] 
				));
	}
	
	private boolean isItem(SymbolsType type) {
		return type.equals(SymbolsType.AMMO) || type.equals(SymbolsType.MEDIKIT); 
	}

	public Map<Point2D, ItemsType> getItems() {
		return items;
	}
}
