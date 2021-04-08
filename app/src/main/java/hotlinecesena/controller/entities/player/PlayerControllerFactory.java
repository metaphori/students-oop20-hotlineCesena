package hotlinecesena.controller.entities.player;

import hotlinecesena.view.entities.Sprite;

public interface PlayerControllerFactory {

    PlayerController createPlayerController(final Sprite sprite);
}
