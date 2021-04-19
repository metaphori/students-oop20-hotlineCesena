package hotlinecesena.controller.HUD;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import hotlinecesena.controller.mission.MissionController;
import hotlinecesena.controller.Updatable;
import hotlinecesena.model.dataccesslayer.JSONDataAccessLayer;
import hotlinecesena.model.entities.actors.player.Player;
import hotlinecesena.view.WorldView;
import hotlinecesena.view.loader.ImageType;
import hotlinecesena.view.loader.ProxyImage;
import hotlinecesena.view.loader.SceneType;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import javafx.util.Pair;

public class PlayerStatsController implements Initializable, Updatable{
	
	@FXML
	private ProgressBar lifeBar;
	@FXML
	private Label hpLabel;
	@FXML
	private BorderPane borderPane;
	@FXML
	private Label bulletLabel;
	@FXML
	private CheckBox missionCheckBox;
	@FXML
	private HBox weaponHBox;
	@FXML
	private ImageView weaponImageView;
	@FXML
	private Polygon previousMission;
	@FXML
	private Polygon nextMission;
	@FXML
	private ImageView miniMapImageView;
	
	private ProxyImage proxyImage = new ProxyImage();
	private List<Pair<String, Boolean>> missions;
	private Player player = JSONDataAccessLayer.getInstance().getPlayer().getPly();
	private MissionController missionController;
	private FadeTransition fade = new FadeTransition(Duration.millis(200));
	
	private WorldView worldView;
	private int currentMission = 0;

	public PlayerStatsController(WorldView view, MissionController missionController) {
		this.worldView = view;
		this.missionController = missionController;
		missions = missionController.getMissions();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		borderPane.prefWidthProperty().bind(worldView.getStackPane().widthProperty());
		borderPane.prefHeightProperty().bind(worldView.getStackPane().heightProperty());
		missionCheckBox.setText(missions.get(currentMission).getKey());
		
		fade.setFromValue(1.0);
		fade.setToValue(0.0);
		fade.setCycleCount(2);
		fade.setAutoReverse(true);
		
		Rectangle2D croppedPortion = new Rectangle2D(
				0,0,0,0);
		miniMapImageView.setViewport(croppedPortion);
	}

	@Override
	public Consumer<Double> getUpdateMethod() {
		return deltaTime -> {
			// Update of life bar
			lifeBar.setProgress(player.getCurrentHealth() / player.getMaxHealth());
			hpLabel.setText(((int) player.getCurrentHealth()) + "/" + ((int) player.getMaxHealth()));
			if (lifeBar.getProgress() <= 0.2) {
				lifeBar.setStyle("-fx-accent: red;");
			}
			else {
				lifeBar.setStyle("-fx-accent: green;");
			}
			
			// Update of ammo counter
			player.getInventory().getWeapon().ifPresentOrElse(weapon -> {
                bulletLabel.setText(
                        weapon.getCurrentAmmo()
                        + "/"
                        + player.getInventory().getQuantityOf(weapon.getCompatibleAmmunition())
                        );
                switch (weapon.getWeaponType()) {
                case RIFLE:
                    weaponImageView.setImage(proxyImage.getImage(SceneType.MENU, ImageType.RIFLE));
                    break;
                case SHOTGUN:
                    weaponImageView.setImage(proxyImage.getImage(SceneType.MENU, ImageType.SHOTGUN));
                    break;
                case PISTOL:
                    weaponImageView.setImage(proxyImage.getImage(SceneType.MENU, ImageType.PISTOL));
                    break;
                default:
                    weaponImageView.setImage(proxyImage.getImage(SceneType.GAME, ImageType.BLANK));
                    break;
                }
            }, () -> bulletLabel.setText("0/0"));
			
			// Update of missions view
			missions = missionController.getMissions();
			missionCheckBox.setSelected(missions.get(currentMission).getValue());
			
			worldView.getBorderPane().setOnKeyPressed(e -> {
			    if (e.getCode() == KeyCode.M) {
			    	currentMission++;
			    	if (currentMission == missions.size()) {
						currentMission = 0;
					}
			    	missionCheckBox.setText(missions.get(currentMission).getKey());
			    	fade.setNode(nextMission);
			    	fade.play();
			    }
			    if (e.getCode() == KeyCode.N) {
			    	currentMission--;
			    	if (currentMission == -1) {
						currentMission = missions.size() - 1;
					}
			    	missionCheckBox.setText(missions.get(currentMission).getKey());
			    	fade.setNode(previousMission);
			    	fade.play();
			    }
			});
			
			// Update of MiniMap
			miniMapImageView.setImage(JSONDataAccessLayer.getInstance().getWorld().getImageVIewUpdated());
		};
	}
}
