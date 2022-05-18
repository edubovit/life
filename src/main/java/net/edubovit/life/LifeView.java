package net.edubovit.life;

import javafx.scene.layout.Pane;

public interface LifeView {

    Pane pane();

    void draw(Cell cell);

    void flush();

}
