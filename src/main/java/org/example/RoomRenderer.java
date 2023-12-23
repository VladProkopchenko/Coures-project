package org.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RoomRenderer {
    public void render(ShapeRenderer shapeRenderer, Room room){
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(room.getX(), room.getY(), room.getWidth(), room.getHeight());
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BROWN);
        shapeRenderer.rect( room.getX(),room.getY()*8,(float) (room.getWidth()/54), (float)(room.getHeight()/6.2));
        shapeRenderer.setColor(Color.SKY);
        shapeRenderer.rect(room.getX()*55-1, room.getY()*10, (float) (room.getWidth()/55), (float) (room.getHeight()/1.37));
    }
}
