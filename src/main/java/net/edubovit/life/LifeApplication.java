package net.edubovit.life;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import net.edubovit.life.entity.EntityType;

import static java.lang.Math.max;
import static net.edubovit.life.Balance.GROW_FOOD_PERIOD;

public class LifeApplication extends Application {

    private static final int DEFAULT_WIDTH = 500;

    private static final int DEFAULT_HEIGHT = 500;

    private int clickMode = 1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String widthParameter = getParameters().getNamed().get("width");
        String heightParameter = getParameters().getNamed().get("height");
        int width = widthParameter == null || widthParameter.isBlank() ? DEFAULT_WIDTH : Integer.parseInt(widthParameter);
        int height = heightParameter == null || heightParameter.isBlank() ? DEFAULT_HEIGHT : Integer.parseInt(heightParameter);
        var lifeView = new PixelLifeView(width, height);
        var lifeField = new LifeField(width, height, lifeView);
        lifeField.redrawAll();

        var pane = lifeView.pane();
        var scene = new Scene(pane);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ADD) {
                if (event.isShiftDown() && event.isControlDown()) {
                    GROW_FOOD_PERIOD += 1000;
                } else if (event.isShiftDown()) {
                    GROW_FOOD_PERIOD += 100;
                } else if (event.isControlDown()) {
                    GROW_FOOD_PERIOD += 10;
                } else {
                    GROW_FOOD_PERIOD += 1;
                }
                System.out.println("GROW_FOOD_PERIOD is now " + GROW_FOOD_PERIOD);
            } else if (event.getCode() == KeyCode.SUBTRACT) {
                if (event.isShiftDown() && event.isControlDown()) {
                    GROW_FOOD_PERIOD = max(1, GROW_FOOD_PERIOD - 1000);
                } else if (event.isShiftDown()) {
                    GROW_FOOD_PERIOD = max(1, GROW_FOOD_PERIOD - 100);
                } else if (event.isControlDown()) {
                    GROW_FOOD_PERIOD = max(1, GROW_FOOD_PERIOD - 10);
                } else {
                    GROW_FOOD_PERIOD = max(1, GROW_FOOD_PERIOD - 1);
                }
                System.out.println("GROW_FOOD_PERIOD is now " + GROW_FOOD_PERIOD);
            } else if (event.getCode() == KeyCode.DIGIT1) {
                clickMode = 1;
                System.out.println("clickMode is now 1");
            } else if (event.getCode() == KeyCode.DIGIT2) {
                clickMode = 2;
                System.out.println("clickMode is now 2");
            } else if (event.getCode() == KeyCode.DIGIT3) {
                clickMode = 3;
                System.out.println("clickMode is now 3");
            } else if (event.getCode() == KeyCode.DIGIT4) {
                clickMode = 4;
                System.out.println("clickMode is now 4");
            }
        });
        scene.setOnMouseClicked(event -> {
            if (event.getX() < width && event.getY() < height) {
                lifeField.bornEntity((int) Math.round(event.getX()), (int) Math.round(event.getY()), EntityType.byDigit(clickMode));
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();

        var emulator = new LifeEmulator(lifeField);

        var animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                emulator.run();
            }
        };
        animationTimer.start();
    }

}
