package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class BulletControlSystem implements IEntityProcessingService, BulletSPI {

    private static final double BULLET_SPEED = 4.0;
    private static final double SPAWN_DISTANCE = 10.0;
    private static final int BULLET_RADIUS = 2;
    private static final String BULLET_COLOR = "GOLD";

    @Override
    public void process(GameData gameData, World world) {
        for (Entity entity : world.getEntities(Bullet.class)) {
            updateBulletPosition(entity);
        }
    }

    private void updateBulletPosition(Entity bullet) {
        double angleRad = Math.toRadians(bullet.getRotation());
        double dx = Math.cos(angleRad) * BULLET_SPEED;
        double dy = Math.sin(angleRad) * BULLET_SPEED;

        bullet.setX(bullet.getX() + dx);
        bullet.setY(bullet.getY() + dy);
    }

    @Override
    public Entity createBullet(Entity shooter, GameData gameData) {
        Bullet newBullet = new Bullet();

        double angleRad = Math.toRadians(shooter.getRotation());
        double offsetX = Math.cos(angleRad) * SPAWN_DISTANCE;
        double offsetY = Math.sin(angleRad) * SPAWN_DISTANCE;

        newBullet.setX(shooter.getX() + offsetX);
        newBullet.setY(shooter.getY() + offsetY);
        newBullet.setRotation(shooter.getRotation());
        newBullet.setRadius(BULLET_RADIUS);
        newBullet.setColor(BULLET_COLOR);

        newBullet.setPolygonCoordinates(
                0, -3,
                1.7634, -2.427,
                2.8533, -0.927,
                2.8533, 0.927,
                1.7634, 2.427,
                0, 3,
                -1.7634, 2.427,
                -2.8533, 0.927,
                -2.8533, -0.927,
                -1.7634, -2.427
        );

        return newBullet;
    }
}
