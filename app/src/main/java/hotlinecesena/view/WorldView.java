package hotlinecesena.view;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import hotlinecesena.controller.GameLoopController;
import hotlinecesena.controller.entities.player.PlayerController;
import hotlinecesena.controller.entities.player.PlayerControllerFactoryFX;
import hotlinecesena.model.dataccesslayer.JSONDataAccessLayer;
import hotlinecesena.model.dataccesslayer.SymbolsType;
import hotlinecesena.model.dataccesslayer.datastructure.DataWorldMap;
import hotlinecesena.view.loader.ImageType;
import hotlinecesena.view.loader.ProxyImage;
import hotlinecesena.view.loader.SceneType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class WorldView {
    private static final String TITLE = "Hotline Cesena";
    private static final int TILE_SIZE = JSONDataAccessLayer.getInstance().getSettings().getTileSize();
    private final Stage primaryStage;
    private BorderPane borderPane;
    private final GridPane gridPane = new GridPane();
    ProxyImage proxyImage = new ProxyImage();
    DataWorldMap world = JSONDataAccessLayer.getInstance().getWorld();
    Map<Pair<Integer, Integer>, SymbolsType> worldMap = world.getWorldMap();
    private final GameLoopController gc = new GameLoopController();
    
    private Map<Pair<Integer, Integer>, ImageView> enemiesPos = new LinkedHashMap<>();
    private Map<Pair<Integer, Integer>, ImageView> itemsPos = new LinkedHashMap<>();
    private Map<Pair<Integer, Integer>, ImageView> obstaclesPos = new LinkedHashMap<>();
    private Map<Pair<Integer, Integer>, ImageView> playersPos = new LinkedHashMap<>();

    public WorldView(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public final void start() {
        primaryStage.setTitle(TITLE);
        borderPane = new BorderPane();
        final Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
        borderPane.setCenter(gridPane);

        final int rows = world.getMaxY() - world.getMinY() + 1;
        final int cols = world.getMaxX() - world.getMinX() + 1;
        for (int row = 0 ; row < rows ; row++) {
            for (int col = 0 ; col < cols ; col++) {
                pickImage(col, row);
            }
        }
        
        addAllToWorldMap(itemsPos);
        addAllToWorldMap(obstaclesPos);
        addAllToWorldMap(enemiesPos);
        addAllToWorldMap(playersPos);
        
        primaryStage.setResizable(false);
        primaryStage.setWidth(1600);
        primaryStage.setHeight(900);
        primaryStage.setX(0);
        primaryStage.setY(0);

        final PlayerController pc = new PlayerControllerFactoryFX(primaryStage.getScene(), gridPane).createPlayerController();
        gc.addMethodToUpdate(pc.getUpdateMethod());
        System.out.println("Obstacles: ");
        for (final var o : JSONDataAccessLayer.getInstance().getPhysics().getObstacles()) {
            System.out.println(o.getMinX() + "; " + o.getMinY());
        }
        System.out.println("Enemies: ");
        for (final var o : JSONDataAccessLayer.getInstance().getEnemy().getEnemies()) {
            System.out.println(o.getPosition());
        }
        gc.loop();
    }
    
    private void addAllToWorldMap(Map<Pair<Integer, Integer>, ImageView> positionsMap) {
		for(Entry<Pair<Integer, Integer>, ImageView> elem : positionsMap.entrySet()) {
			Group group = createBlendGroup(elem.getValue());
			gridPane.add(group, elem.getKey().getKey(), elem.getKey().getValue());
		}
	}

	private void pickImage(final int col, final int row) {
    	final char c = worldMap.get(new Pair<Integer, Integer> (world.getMinX() + col, world.getMinY() + row)).getDecotification();
    	if (c == 'W' || c == '_' || c == '.') {
			createSingleImage(c, col, row);
		}
    	else {
    		savePosition(c, col, row);
		}
    }

    private void createSingleImage(final char c, final int col, final int row) {
        final ImageView tile = new ImageView();
        switch (c) {
		case 'W':
			tile.setImage(proxyImage.getImage(SceneType.GAME, ImageType.WALL));
			break;
		case '_':
			tile.setImage(proxyImage.getImage(SceneType.GAME, ImageType.GRASS));
			break;
		case '.':
			tile.setImage(proxyImage.getImage(SceneType.GAME, ImageType.FLOOR));
			break;	
		}
        tile.setFitHeight(TILE_SIZE);
        tile.setFitWidth(TILE_SIZE);
        gridPane.add(tile, col, row);
    }
    
    private void savePosition(char c, int col, int row) {
    	switch (c) {
		case 'P':
			this.playersPos.put(new Pair<Integer, Integer>(col, row), new ImageView(proxyImage.getImage(SceneType.GAME, ImageType.PLAYER)));
			break;
		case 'E':
			this.enemiesPos.put(new Pair<Integer, Integer>(col, row), new ImageView(proxyImage.getImage(SceneType.GAME, ImageType.ENEMY_1)));
			break;
		case 'M':
			this.itemsPos.put(new Pair<Integer, Integer>(col, row), new ImageView(proxyImage.getImage(SceneType.GAME, ImageType.MEDKIT)));
			break;	
		case 'A':
			this.itemsPos.put(new Pair<Integer, Integer>(col, row), new ImageView(proxyImage.getImage(SceneType.GAME, ImageType.AMMO_PISTOL)));
			break;
		case 'O':
			this.obstaclesPos.put(new Pair<Integer, Integer>(col, row), new ImageView(proxyImage.getImage(SceneType.GAME, ImageType.BOX)));
			break;
    	}
    }

    private Group createBlendGroup(final ImageView imageView) {
    	ImageView bottom = new ImageView(proxyImage.getImage(SceneType.GAME, ImageType.FLOOR));
        ImageView top = imageView;
        bottom.setFitHeight(TILE_SIZE);
        bottom.setFitWidth(TILE_SIZE);
        top.setFitHeight(TILE_SIZE);
        top.setFitWidth(TILE_SIZE);
        top.setBlendMode(BlendMode.SRC_OVER);
        Group blend = new Group(
    			bottom,
    			top
    	);
        return blend;
    }
    
    public GridPane getGridPane() {
    	return this.gridPane;
    }
    
}