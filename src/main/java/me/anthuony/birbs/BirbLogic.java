package me.anthuony.birbs;

import java.awt.*;
import java.util.List;

public class BirbLogic extends Thread
{
    private final BirbsContainer bc;
    private final List<Birb> list;

    public BirbLogic(List<Birb> birbsList, String name, ThreadGroup tg, BirbsContainer bc)
    {
        super(tg, name);
        this.list = birbsList;
        this.bc = bc;
    }

    public void run()
    {
        int windowWidth = bc.getWindowWidth();
        int windowHeight = bc.getWindowHeight();
        int worldWidth = bc.getWorldWidth();
        int worldHeight = bc.getWorldHeight();
        float cameraOffsetX = (float) bc.getCameraOffsetX();
        float cameraOffsetY = (float) bc.getCameraOffsetY();
        float cameraScale = (float) bc.getScale();
        float deltaTime = (float) bc.getUPDATE_CAP();
        boolean paused = bc.isPaused();
        int birbWidth = Birb.getBaseWidth();
        int birbHeight = Birb.getBaseHeight();

        for (Birb birb : list)
        {
            float xW = birb.getXWorld();
            float yW = birb.getYWorld();
            float deltaXW;
            float deltaYW;
            float xF = birb.getXForce();
            float yF = birb.getYForce();
            float speed = birb.getSpeed();
            float direction = birb.getDirection();
            float avgHeading = birb.getDirection();
            float count = 1;
            float forceDirection;
            float newDirection;
            float scale = birb.getScale();

            if(!paused)
            {
                //Find New Position
                deltaXW = (float) (speed * Math.cos(direction) * deltaTime);
                deltaYW = (float) (speed * Math.sin(direction) * deltaTime);
                xW += deltaXW;
                yW += deltaYW;

                //Adjust for World Boundaries
                xW = (float) ((Math.abs((xW + worldWidth + birbWidth / 2.0) % worldWidth)) - birbWidth / 2.0);
                yW = (float) ((Math.abs((yW + worldHeight + birbHeight / 2.0) % worldHeight)) - birbHeight / 2.0);

                //Update Entity World Location
                birb.setXWorld(xW);
                birb.setYWorld(yW);

                //Reset Forces
                birb.resetForces();

                //Add momentum
                birb.addXForce(deltaXW * 10);
                birb.addYForce(deltaYW * 10);
            }

            //Calculate Screen Position
            float sX = (xW + cameraOffsetX) * cameraScale;
            float sY = (yW + cameraOffsetY) * cameraScale;

            //Update Entity Screen Location
            birb.setXScreen(sX);
            birb.setYScreen(sY);

            //Find if Entity Should be Rendered
            birb.setOnScreen(sX > -birbWidth * cameraScale && sX < windowWidth + birbWidth * cameraScale && sY > -birbHeight * cameraScale && sY < windowHeight + birbHeight * cameraScale);

            //If on Screen
            if(birb.isOnScreen())
            {
                //Update Birb Color
                int b = (int) Math.abs((xW / worldWidth) * 255);
                int g = (int) Math.abs((yW / worldHeight) * 255);
                int r = 255 - b;

                b = ExtraMath.boundNumber(b, 0, 255);
                g = ExtraMath.boundNumber(g, 0, 255);
                r = ExtraMath.boundNumber(r, 0, 255);

                birb.setColor(new Color(r, g, b, 255));
            }
        }
    }
}
