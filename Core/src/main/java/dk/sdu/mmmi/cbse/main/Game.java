package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class Game {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();

    private final List<IGamePluginService> gamePluginServices;
    private final List<IEntityProcessingService> entityProcessingServiceList;
    private final List<IPostEntityProcessingService> postEntityProcessingServices;

    Game(List<IGamePluginService> gamePluginServices,
         List<IEntityProcessingService> entityProcessingServiceList,
         List<IPostEntityProcessingService> postEntityProcessingServices) {

        this.gamePluginServices = gamePluginServices;
        this.entityProcessingServiceList = entityProcessingServiceList;
        this.postEntityProcessingServices = postEntityProcessingServices;
    }

    public void start(Stage window) throws Exception {
        setupScene(window);
        initializePlugins();
        renderEntities();
    }

    public void render() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw();
                gameData.getKeys().updateKeyStates();
            }
        }.start();
    }

    private void setupScene(Stage window) {
        Text hudText = new Text(10, 20, "Destroyed asteroids: 0");

        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        gameWindow.getChildren().add(hudText);

        Scene scene = new Scene(gameWindow);

        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode(), true));
        scene.setOnKeyReleased(e -> handleKeyInput(e.getCode(), false));

        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();
    }

    private void handleKeyInput(KeyCode key, boolean pressed) {
        if (key == KeyCode.LEFT) {
            gameData.getKeys().setKeyState(GameKeys.KEY_LEFT, pressed);
        } else if (key == KeyCode.RIGHT) {
            gameData.getKeys().setKeyState(GameKeys.KEY_RIGHT, pressed);
        } else if (key == KeyCode.UP) {
            gameData.getKeys().setKeyState(GameKeys.KEY_UP, pressed);
        } else if (key == KeyCode.DOWN) {
            gameData.getKeys().setKeyState(GameKeys.KEY_DOWN, pressed);
        } else if (key == KeyCode.SPACE) {
            gameData.getKeys().setKeyState(GameKeys.KEY_SPACE, pressed);
        } else if (key == KeyCode.X) {
            gameData.getKeys().setKeyState(GameKeys.KEY_X, pressed);
        }
    }

    private void initializePlugins() {
        for (IGamePluginService plugin : getGamePluginServices()) {
            plugin.start(gameData, world);
        }
    }

    private void renderEntities() {
        for (Entity entity : world.getEntities()) {
            Polygon shape = new Polygon(entity.getPolygonCoordinates());
            polygons.put(entity, shape);
            gameWindow.getChildren().add(shape);
        }
    }

    private void update() {
        for (IEntityProcessingService processor : getEntityProcessingServices()) {
            processor.process(gameData, world);
        }

        for (IPostEntityProcessingService postProcessor : getPostEntityProcessingServices()) {
            postProcessor.process(gameData, world);
        }
    }

    private void draw() {
        // Remove entities no longer in the world
        polygons.keySet().removeIf(entity -> {
            if (!world.getEntities().contains(entity)) {
                gameWindow.getChildren().remove(polygons.get(entity));
                return true;
            }
            return false;
        });

        // Update existing polygons or add new ones
        for (Entity entity : world.getEntities()) {
            Polygon poly = polygons.get(entity);

            if (poly == null) {
                poly = new Polygon(entity.getPolygonCoordinates());
                polygons.put(entity, poly);
                gameWindow.getChildren().add(poly);
            }

            poly.setTranslateX(entity.getX());
            poly.setTranslateY(entity.getY());
            poly.setRotate(entity.getRotation());

// Apply color from entity
            try {
                if (entity.getColor() != null) {
                    poly.setFill(javafx.scene.paint.Color.web(entity.getColor()));
                }
            } catch (IllegalArgumentException e) {
                poly.setFill(javafx.scene.paint.Color.GRAY); // fallback if color is invalid
            }

        }
    }

    public List<IGamePluginService> getGamePluginServices() {
        return gamePluginServices;
    }

    public List<IEntityProcessingService> getEntityProcessingServices() {
        return entityProcessingServiceList;
    }

    public List<IPostEntityProcessingService> getPostEntityProcessingServices() {
        return postEntityProcessingServices;
    }
}
