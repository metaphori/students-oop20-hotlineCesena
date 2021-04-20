package hotlinecesena.controller.generator;

import java.util.HashMap;
import java.util.Map;

import hotlinecesena.model.dataccesslayer.SymbolsType;
import javafx.util.Pair;

public abstract class AbstractRoom implements Room {
	
	protected Map<Pair<Integer, Integer>, SymbolsType> map = new HashMap<>();
	protected Pair<Integer, Integer> center = new Pair<>(0, 0);
	
	public AbstractRoom() {
		
	}

	public abstract void generate();
	public abstract Room deepCopy();
	
	public AbstractRoom(Map<Pair<Integer, Integer>, SymbolsType> map, Pair<Integer, Integer> center) {
		this.map = map;
		this.center = center;
	}

	public Pair<Integer, Integer> getCenter() {
		return center;
	}
	
	public void setCenter(Pair<Integer, Integer> center) {
		this.center = center;
	}
	
	public Map<Pair<Integer, Integer>, SymbolsType> getMap() {
		return map;
	}
}
