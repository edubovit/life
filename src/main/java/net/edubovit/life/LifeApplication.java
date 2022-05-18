package net.edubovit.life;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import net.edubovit.life.entity.EntityType;

import static net.edubovit.life.Balance.GROW_FOOD_PERIOD;

public class LifeApplication extends Application {

    public static final int WIDTH = 2400;

    public static final int HEIGHT = 1200;

    private int clickMode = 1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        var lifeView = new PixelLifeView(WIDTH, HEIGHT);
        var lifeField = new LifeField(WIDTH, HEIGHT, lifeView);
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
                    GROW_FOOD_PERIOD -= 1000;
                } else if (event.isShiftDown()) {
                    GROW_FOOD_PERIOD -= 100;
                } else if (event.isControlDown()) {
                    GROW_FOOD_PERIOD -= 10;
                } else {
                    GROW_FOOD_PERIOD -= 1;
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
            }
        });
        scene.setOnMouseClicked(event -> {
            lifeField.bornEntity((int) Math.round(event.getX()), (int) Math.round(event.getY()), EntityType.byDigit(clickMode));
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
