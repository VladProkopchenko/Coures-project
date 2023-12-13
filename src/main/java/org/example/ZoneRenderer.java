package org.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.List;

public class ZoneRenderer {
    public void render(ShapeRenderer shapeRenderer, List<Zone> zones){
        for(Zone zone : zones){
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.rect(zone.getX(),zone.getY(),zone.getWidth(),zone.getHeight());
            /*shapeRenderer.rect(200,200,160,300);
            shapeRenderer.rect(20,200,160,300);
            shapeRenderer.rect(20,520,520,100);
            shapeRenderer.rect(380,200,160,300);
            shapeRenderer.rect(200,80,340,100);*/
        }
    }
}
