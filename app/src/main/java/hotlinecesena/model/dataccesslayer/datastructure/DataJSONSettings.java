package hotlinecesena.model.dataccesslayer.datastructure;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hotlinecesena.model.dataccesslayer.AbstractData;
import hotlinecesena.model.dataccesslayer.JSONDataAccessLayer;
import static java.util.stream.Collectors.*;

public class DataJSONSettings  extends AbstractData  {

	//GUI SETTINGS
    @JsonProperty("monitorX")
	private int monitorX;
    @JsonProperty("monitorY")
    private int monitorY;
    @JsonProperty("fullScreen")
    private boolean fullScreen;
    @JsonProperty("volume")
    private int volume;
    @JsonProperty("isMusicActive")
    private boolean isMusicActive;
    @JsonProperty("isEffectActive")
    private boolean isEffectActive;
    //
    
    @JsonProperty("niceseeds")
    private List<Long> niceseeds;
    @JsonProperty("minEnemyForRoom")
    private int minEnemyForRoom;
    @JsonProperty("maxEnemyForRoom")
    private int maxEnemyForRoom;
    @JsonProperty("minObstaclesForRoom")
    private int minObstaclesForRoom;
    @JsonProperty("maxObstaclesForRoom")
    private int maxObstaclesForRoom;
    @JsonProperty("minItemForRoom")
    private int minItemForRoom;
    @JsonProperty("maxItemForRoom")
    private int maxItemForRoom;
    @JsonProperty("minRoomWidth")
    private int minRoomWidth;
    @JsonProperty("maxRoomWidth")
    private int maxRoomWidth;
    @JsonProperty("minRoomHeight")
    private int minRoomHeight;
    @JsonProperty("maxRoomHeight")
    private int maxRoomHeight;
    @JsonProperty("minRoomDoor")
    private int minRoomDoor;
    @JsonProperty("maxRoomDoor")
    private int maxRoomDoor;
    @JsonProperty("minBaseRoom")
    private int minBaseRoom;
    @JsonProperty("maxBaseRoom")
    private int maxBaseRoom;
    @JsonProperty("minRoom")
    private int minRoom;
    @JsonProperty("maxRoom")
    private int maxRoom;
    @JsonProperty("obstaclesEdge")
    private float obstaclesEdge;
    @JsonProperty("minRoomWeapons")
    private int minRoomWeapons;
    @JsonProperty("maxRoomWeapons")
    private int maxRoomWeapons;
    

    @JsonProperty("defaultLanguage")
    private int defaultLanguage;
    @JsonProperty("defaultWidth")
    private int defaultWidth;
    @JsonProperty("defaultHeight")
    private int defaultHeight;
    @JsonProperty("topHudHeight")
    private int topHudHeight;
    @JsonProperty("botHudHeight")
    private int botHudHeight;
    @JsonProperty("resolutions")
    private Map<String, String> resolutions;
    
    public Map<String, String> getResolutions(){
    	return this.resolutions;
    }
    
    public int getBotHudHeight() {
		return botHudHeight;
	}
    
    public int getTopHudHeight() {
		return topHudHeight;
	}

    public int getDefaultWidth() {
		return defaultWidth;
	}

	public int getDefaultHeight() {
		return defaultHeight;
	}

	public int getDefaultLanguage() {
		return defaultLanguage;
	}
    
    public int getMinRoomWeapons() {
		return minRoomWeapons;
	}

	public int getMaxRoomWeapons() {
		return maxRoomWeapons;
	}

	public float getObstaclesEdge() {
		return obstaclesEdge;
	}

	public int getMinEnemyForRoom() {
		return minEnemyForRoom;
	}

	public int getMaxEnemyForRoom() {
		return maxEnemyForRoom;
	}

	public int getMinObstaclesForRoom() {
		return minObstaclesForRoom;
	}

	public int getMaxObstaclesForRoom() {
		return maxObstaclesForRoom;
	}

	public int getMinItemForRoom() {
		return minItemForRoom;
	}

	public int getMaxItemForRoom() {
		return maxItemForRoom;
	}

	public int getMinRoomWidth() {
		return minRoomWidth;
	}

	public int getMaxRoomWidth() {
		return maxRoomWidth;
	}

	public int getMinRoomHeight() {
		return minRoomHeight;
	}

	public int getMaxRoomHeight() {
		return maxRoomHeight;
	}

	public int getMinRoomDoor() {
		return minRoomDoor;
	}

	public int getMaxRoomDoor() {
		return maxRoomDoor;
	}

	public int getMinBaseRoom() {
		return minBaseRoom;
	}

	public int getMaxBaseRoom() {
		return maxBaseRoom;
	}

	public int getMinRoom() {
		return minRoom;
	}

	public int getMaxRoom() {
		return maxRoom;
	}

	public void setMusicActive(boolean isMusicActive) {
		this.isMusicActive = isMusicActive;
	}

	public void setEffectActive(boolean isEffectActive) {
		this.isEffectActive = isEffectActive;
	}

	public boolean isMusicActive() {
		return isMusicActive;
	}

	public boolean isEffectActive() {
		return isEffectActive;
	}

	public List<Long> getNiceseeds() {
		return niceseeds;
	}

	public int getMonitorX() {
		return monitorX;
	}

	public void setMonitorX(int monitorX) {
		this.monitorX = monitorX;
	}

	public int getMonitorY() {
		return monitorY;
	}

	public void setMonitorY(int monitorY) {
		this.monitorY = monitorY;
	}

	public boolean getFullScreen() {
		return this.fullScreen;
	}

	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	@Override
	public void write() throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(JSONDataAccessLayer.FILE_FOLDER_PATH + "settings.json"), this);
	}
}
