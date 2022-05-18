package net.edubovit.life;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static net.edubovit.life.Balance.MAX_FOOD;
import static net.edubovit.life.Balance.MAX_NECRO;

public class PixelLifeView implements LifeView {

    private static final Color ZERO_FOOD_COLOR = Color.SADDLEBROWN;

    private static final Color FULL_FOOD_COLOR = Color.GREEN;

    private static final Color NECRO_COLOR = Color.BLACK;

    private final int width;

    private final int height;

    private final Canvas canvas;

    private final GraphicsContext graphicsContext;

    private final PixelWriter pixelWriter;

    private final Pane pane;

    private final List<Cell> drawQueue;

    public PixelLifeView(int width, int height) {
        this.width = width;
        this.height = height;
        this.canvas = new Canvas(width, height);
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.pixelWriter = graphicsContext.getPixelWriter();
        this.pane = new Pane(canvas);
        this.drawQueue = new ArrayList<>(2 * width * height);
    }

    @Override
    public Pane pane() {
        return pane;
    }

    @Override
    public void draw(Cell cell) {
        drawQueue.add(cell);
    }

    @Override
    public void flush() {
        drawQueue.forEach(this::flush);
        drawQueue.clear();
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
