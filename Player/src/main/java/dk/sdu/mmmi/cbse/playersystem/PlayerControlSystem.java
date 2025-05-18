package dk.sdu.mmmi.cbse.playersystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class PlayerControlSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {

        for (Entity player : world.getEntities(Player.class)) {

            player.setPolygonCoordinates(-5, -5, 10, 0, -5, 5);
            player.setColor("CYAN");

            GameKeys keys = gameData.getKeys();

            handleRotation(keys, player);
            handleMovement(keys, player);
            handleShooting(keys, player, gameData, world);
            handleBoost(keys, player);

            clampToScreenBounds(player, gameData);
        }
    }

    private void handleRotation(GameKeys keys, Entity player) {
        if (keys.isKeyHeld(GameKeys.KEY_LEFT)) {
            player.setRotation(player.getRotation() - 5);
        }
        if (keys.isKeyHeld(GameKeys.KEY_RIGHT)) {
            player.setRotation(player.getRotation() + 5);
        }
    }

    private void handleMovement(GameKeys keys, Entity player) {
        if (keys.isKeyHeld(GameKeys.KEY_UP)) {
            movePlayer(player, 1);
        }
        if (keys.isKeyHeld(GameKeys.KEY_DOWN)) {
            movePlayer(player, -1);
        }
    }

    private void movePlayer(Entity player, double direction) {
        double changeX = Math.cos(Math.toRadians(player.getRotation())) * direction;
        double changeY = Math.sin(Math.toRadians(player.getRotation())) * direction;
        player.setX(player.getX() + changeX);
        player.setY(player.getY() + changeY);
    }

    private void handleShooting(GameKeys keys, Entity player, GameData gameData, World world) {
        if (keys.isKeyJustPressed(GameKeys.KEY_SPACE)) {
            getBulletSPIs().stream().findFirst().ifPresent(
                    spi -> world.addEntity(spi.createBullet(player, gameData))
            );
        }
    }

    private void handleBoost(GameKeys keys, Entity player) {
        if (keys.isKeyHeld(GameKeys.KEY_X)) {
            movePlayer(player, 2);

            player.setPolygonCoordinates(-7, -7, 13, -2, -7, 7);
            player.setColor("GREEN");
        }
    }

    private void clampToScreenBounds(Entity player, GameData gameData) {
        if (player.getX() < 0) {
            player.setX(1);
        }
        if (player.getX() > gameData.getDisplayWidth()) {
            player.setX(gameData.getDisplayWidth() - 1);
        }
        if (player.getY() < 0) {
            player.setY(1);
        }
        if (player.getY() > gameData.getDisplayHeight()) {
            player.setY(gameData.getDisplayHeight() - 1);
        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
    }
}
