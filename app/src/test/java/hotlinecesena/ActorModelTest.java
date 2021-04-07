package hotlinecesena;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import hotlinecesena.model.entities.actors.Actor;
import hotlinecesena.model.entities.actors.ActorStatus;
import hotlinecesena.model.entities.actors.DirectionList;
import hotlinecesena.model.entities.actors.player.PlayerImpl;
import hotlinecesena.model.entities.items.AmmunitionType;
import hotlinecesena.model.entities.items.WeaponImpl;
import hotlinecesena.model.entities.items.WeaponType;
import hotlinecesena.model.inventory.Inventory;
import hotlinecesena.model.inventory.NaiveInventoryImpl;
import javafx.geometry.Point2D;

/**
 * Runs a series of tests on methods common to all Actors.
 * <br>
 * In this case, Player was chosen as an Actor implementation.
 */
@TestInstance(Lifecycle.PER_METHOD)
class ActorModelTest {

    private static final double SPEED = 500;
    private static final int ANGLE = 270;
    private static final double WIDTH = 100;
    private static final double HEIGHT = 300;
    private static final double MAX_HP = 100;
    private Actor actor;

    private void setup() {
        final Inventory inv = new NaiveInventoryImpl(
                new WeaponImpl(WeaponType.PISTOL),
                Map.of(AmmunitionType.PISTOL_AMMO, 30));
        actor = new PlayerImpl(Point2D.ZERO, ANGLE, WIDTH, HEIGHT, SPEED, MAX_HP, inv, Map.of());
    }

    @BeforeEach
    void reset() {
        this.setup();
    }

    @Test
    void actorMoveTest() {
        actor.move(DirectionList.NORTH.get());
        assertThat(actor.getPosition(), equalTo(DirectionList.NORTH.get().multiply(SPEED)));
    }

    @Test
    void actorRotateTest() {
        actor.setAngle(90.0);
        assertThat(actor.getAngle(), comparesEqualTo(90.0));
    }

    @Test
    void actorAttackTest() {
        actor.attack();
        assertEquals(ActorStatus.ATTACKING, actor.getActorStatus());
    }

    @Test
    void actorReloadTest() {
        final double reloadTime = actor.getInventory().getWeapon().get().getReloadTime();
        actor.attack();
        actor.reload();
        assertTrue(actor.getInventory().isReloading());
        actor.getInventory().update(reloadTime);
        assertFalse(actor.getInventory().isReloading());
    }

    @Test
    void actorCannotInitiateReloadingWhileAlreadyReloading() {
        final double reloadTime = actor.getInventory().getWeapon().get().getReloadTime();
        actor.attack();
        actor.reload();
        assertTrue(actor.getInventory().isReloading());
        actor.getInventory().update(reloadTime / 2.0);
        assertTrue(actor.getInventory().isReloading()); //Still reloading
        actor.reload();
        actor.getInventory().update(reloadTime / 2.0);
        assertFalse(actor.getInventory().isReloading());
    }

    @Test
    void actorHurtTest() {
        final double damage = 50.0;
        actor.takeDamage(damage);
        assertThat(actor.getCurrentHealth(), comparesEqualTo(MAX_HP - damage));
        final double unrealDamage = 2000.0;
        actor.takeDamage(unrealDamage);
        assertThat(actor.getCurrentHealth(), comparesEqualTo(0.0));
    }

    @Test
    void actorDoesNotMoveWhenDead() {
        actor.takeDamage(MAX_HP);
        actor.move(DirectionList.SOUTH.get());
        assertThat(actor.getPosition(), equalTo(Point2D.ZERO));
    }

    @Test
    void actorDoesNotRotateWhenDead() {
        actor.takeDamage(MAX_HP);
        actor.setAngle(0.0);
        assertThat(actor.getAngle(), not(comparesEqualTo(0.0)));
    }

    @Test
    void actorDoesNotHealWhenDead() {
        actor.takeDamage(MAX_HP);
        final double unrealHp = 50000.0;
        actor.heal(unrealHp);
        assertThat(actor.getCurrentHealth(), comparesEqualTo(0.0));
    }
}
