package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Entity {
    //Entity Types:
    //1 = Birb
    private final BirbsContainer bc;
    private final int entityID;
    private final int type;
    private float xWorld, yWorld, xScreen, yScreen, xForce, yForce, speed, direction, angularAcceleration, scale;
    private Chunk chunk;
    private Color color;
    private boolean onScreen;

    public Entity(BirbsContainer bc, int entityID, int type, float xWorld, float yWorld, float scale) {
        this.bc = bc;
        this.entityID = entityID;
        this.type = type;
        this.xWorld = ExtraMath.boundNumber(xWorld, 0, bc.getWorldWidth(), bc.getWorldWidth());
        this.yWorld = ExtraMath.boundNumber(yWorld, 0, bc.getWorldHeight(), bc.getWorldHeight());
        this.scale = scale;
        this.setChunk(Chunk.calculateChunk(this.xWorld, this.yWorld, bc.getChunkSize(), bc.getChunkWidth()));
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getXWorld() {
        return this.xWorld;
    }

    public void setXWorld(float xWorld) {
        this.xWorld = xWorld;
    }

    public float getYWorld() {
        return this.yWorld;
    }

    public void setYWorld(float yWorld) {
        this.yWorld = yWorld;
    }

    public float getXScreen() {
        return this.xScreen;
    }

    public void setXScreen(float xScreen) {
        this.xScreen = xScreen;
    }

    public float getYScreen() {
        return this.yScreen;
    }

    public void setYScreen(float yScreen) {
        this.yScreen = yScreen;
    }

    public boolean isOnScreen() {
        return this.onScreen;
    }

    public void setOnScreen(boolean onScreen) {
        this.onScreen = onScreen;
    }

    public float getDirection() {
        return this.direction;
    }

    public void setDirection(float direction) {
        this.direction = ExtraMath.boundNumber(direction, 0, (float) (2 * Math.PI), (float) (2 * Math.PI));
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Point2D.Float getWorldPoint() {
        return new Point2D.Float(getXWorld(), getYWorld());
    }

    public int getEntityID() {
        return entityID;
    }

    public void addXForce(float magnitude) {
        this.xForce += magnitude;
    }

    public void addYForce(float magnitude) {
        this.yForce += magnitude;
    }

    public void resetForces() {
        this.xForce = 0;
        this.yForce = 0;
    }

    public void applyForceTo(Entity e) {
        if (e != null) {
            float distanceX;
            float distanceY;
            float distance;

            float distanceX1 = e.getXWorld() - this.getXWorld();
            float distanceX2;
            if (e.getXWorld() > this.getXWorld()) {
                distanceX2 = bc.getWorldWidth() - e.getXWorld() + this.getXWorld();
            } else {
                distanceX2 = bc.getWorldWidth() - this.getXWorld() + e.getXWorld();
            }
            if (Math.abs(distanceX1) < Math.abs(distanceX2)) {
                distanceX = distanceX1;
            } else {
                distanceX = distanceX2;
            }

            float distanceY1 = e.getYWorld() - this.getYWorld();
            float distanceY2;
            if (e.getYWorld() > this.getYWorld()) {
                distanceY2 = bc.getWorldHeight() - e.getYWorld() + this.getYWorld();
            } else {
                distanceY2 = bc.getWorldHeight() - this.getYWorld() + e.getYWorld();
            }
            if (Math.abs(distanceY1) < Math.abs(distanceY2)) {
                distanceY = distanceY1;
            } else {
                distanceY = distanceY2;
            }

            distance = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
            if (e.getType() == 1 && this.getType() == 1) {
                if (distance < Birb.getInteractionRange()) {
                    float cohesionForceMagnitude = (Birb.getInteractionRange() * (distance / 100000));
                    float separationForceMagnitude = (Birb.getInteractionRange() / (distance * 5));

                    if (bc.isDoCohesion() && Float.isFinite(cohesionForceMagnitude)) {
                        //Brings entities closer
                        e.addXForce(-distanceX * cohesionForceMagnitude);
                        e.addYForce(-distanceY * cohesionForceMagnitude);
                    }
                    if (bc.isAvoidOthers() && Float.isFinite(separationForceMagnitude)) {
                        //Makes sure they don't get too close
                        e.addXForce(distanceX * separationForceMagnitude);
                        e.addYForce(distanceY * separationForceMagnitude);
                    }
                }
            }
        }
    }

    public int getType() {
        return type;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        if (chunk >= 0) {
            Chunk.assignChunk(this, this.chunk, bc.getChunkList().get(chunk));
            this.chunk = bc.getChunkList().get(chunk);
        } else {
            System.out.println("Negative chunk x:" + xWorld + " y: " + yWorld);
        }
    }

    public float getXForce() {
        return xForce;
    }

    public float getYForce() {
        return yForce;
    }

    public float getDistance(Entity e) {
        float x1 = this.getXWorld();
        float y1 = this.getYWorld();
        float x2 = e.getXWorld();
        float y2 = e.getYWorld();

        return (float) Point2D.distance(x1, y1, x2, y2);
    }
}
