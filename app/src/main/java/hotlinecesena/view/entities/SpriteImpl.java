package hotlinecesena.view.entities;

import java.util.Objects;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 * Sprite implementation for JavaFX.
 *
 */
public final class SpriteImpl implements Sprite {

    private final ImageView imageView;
    private Point2D imageOffset;
    private final Rotate rotate;
    private final Translate trans;

    /**
     * Instantiates a new {@code SpriteImpl} with a given {@link ImageView}.
     * @param view the {@code ImageView} to be used.
     */
    public SpriteImpl(final ImageView view) {
        imageView = view;
        imageOffset = new Point2D(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2);
        rotate = new Rotate();
        trans = (Translate) view.getTransforms().get(0);
        imageView.getTransforms().addAll(rotate, trans);
    }

    /**
     * @throws NullPointerException if the given position is null.
     */
    @Override
    public void updatePosition(final Point2D entityPos) {
        Objects.requireNonNull(entityPos);
        rotate.setPivotX(entityPos.getX() / 2 + imageOffset.getX());
        rotate.setPivotY(entityPos.getY() / 2 + imageOffset.getY());
        trans.setX(entityPos.getX() / 2);
        trans.setY(entityPos.getY() / 2);
    }

    @Override
    public void updateRotation(final double entityAngle) {
        rotate.setAngle(entityAngle);
    }

    @Override
    public void updateImage(final Image image) {
        imageView.setImage(image);
        imageOffset = new Point2D(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2);
    }

    @Override
    public Point2D getPositionRelativeToParent() {
        return imageView.localToParent(imageOffset);
    }

    @Override
    public Point2D getPositionRelativeToScene() {
        return imageView.localToScene(imageOffset);
    }

    @Override
    public Point2D getTranslatePosition() {
        return new Point2D(trans.getX(), trans.getY());
    }
}
