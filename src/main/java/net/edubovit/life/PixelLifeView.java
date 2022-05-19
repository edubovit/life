package net.edubovit.life;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

import static net.edubovit.life.Balance.GROW_FOOD_PERIOD;
import static net.edubovit.life.Balance.MAX_FOOD;
import static net.edubovit.life.Balance.MAX_NECRO;

public class PixelLifeView implements LifeView {

    private static final Color ZERO_FOOD_COLOR = Color.SADDLEBROWN;

    private static final Color FULL_FOOD_COLOR = Color.GREEN;

    private static final Color NECRO_COLOR = Color.BLACK;

    private final int width;

    private final int height;

    private final Canvas canvas;

    private final Canvas infoPanel;

    private final PixelWriter pixelWriter;

    private final Pane pane;

    private final List<Cell> drawQueue;

    public PixelLifeView(int width, int height) {
        this.width = width;
        this.height = height;
        this.canvas = new Canvas(width, height);
        this.infoPanel = new Canvas(150, height);
        this.pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        this.pane = new HBox(canvas, infoPanel);
        this.drawQueue = new ArrayList<>(2 * width * height);
    }

    @Override
    public Pane pane() {
        return pane;
    }

    @Override
    public synchronized void draw(Cell cell) {
        drawQueue.add(cell);
    }

    @Override
    public void flush() {
        drawQueue.forEach(this::flush);
        drawQueue.clear();
    }

    @Override
    public void renderStatistics(float tps, int entities) {
        var context = infoPanel.getGraphicsContext2D();
        context.setFill(Color.WHITE);
        context.setFont(Font.font("Consolas"));
        context.fillRect(0, 0, 150, height);
        context.strokeText(String.format("TPS: %.1f", tps), 10, 20);
        context.strokeText(String.format("Entities: %d", entities), 10, 50);
        context.strokeText(String.format("Grow Period: %d", GROW_FOOD_PERIOD), 10, 80);
    }

    private void flush(Cell cell) {
        if (cell.hasEntity()) {
            pixelWriter.setColor(cell.getX(), cell.getY(), cell.getEntity().getColor());
        } else {
            pixelWriter.setColor(cell.getX(), cell.getY(), colorOfCell(cell));
        }
    }

    private Color colorOfCell(Cell cell) {
        return combineColors(new ColorWeight[]{
                new ColorWeight(ZERO_FOOD_COLOR, (float) (MAX_FOOD - cell.getFood()) / MAX_FOOD),
                new ColorWeight(FULL_FOOD_COLOR, (float) cell.getFood() / MAX_FOOD),
                new ColorWeight(NECRO_COLOR, 2f * cell.getNecro() / MAX_NECRO)
        });
    }

    private Color combineColors(ColorWeight[] colorWeights) {
        double red = 0;
        double green = 0;
        double blue = 0;
        float weightSum = 0;
        for (var colorWeight : colorWeights) {
            weightSum += colorWeight.weight;
        }
        for (var colorWeight : colorWeights) {
            red += colorWeight.color.getRed() * colorWeight.weight / weightSum;
            green += colorWeight.color.getGreen() * colorWeight.weight / weightSum;
            blue += colorWeight.color.getBlue() * colorWeight.weight / weightSum;
        }
        return Color.color(red, green, blue);
    }

    private record ColorWeight(Color color, float weight) {
    }

}
