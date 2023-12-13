package org.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RoomRenderer {
    public void render(ShapeRenderer shapeRenderer, Room room){
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(room.getX(), room.getY(), room.getWidth(), room.getHeight());
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(200,200,160,300);
        shapeRenderer.rect(20,200,160,300);
        shapeRenderer.rect(20,520,520,100);
        shapeRenderer.rect(380,200,160,300);
        shapeRenderer.rect(200,80,340,100);
    }
}
