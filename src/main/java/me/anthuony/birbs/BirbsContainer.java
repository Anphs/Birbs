package me.anthuony.birbs;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BirbsContainer implements Runnable
{
	
	private Window window;
	//	private Renderer renderer;
	private Input input;
	private final AbstractBirbsManager world;
	
	private final double UPDATE_CAP = 1.0 / 60.0;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int windowWidth = screenSize.width, windowHeight = screenSize.height;
	private int worldWidth = 19200, worldHeight = 10800;
	private double cameraOffsetX = (worldWidth - windowWidth * 10) / -2.0, cameraOffsetY = (worldHeight - windowHeight * 10) / -2.0, cameraTempOffsetX = 0, cameraTempOffsetY = 0;
	private double scale = .1, minScale = .1, maxScale = 1.5;
	private String title = "Birbs";
	
	private final ArrayList<Birb> birbsList = new ArrayList<>();
	private int birbTotalSpawned;
	
	double frameTime = 0;
	int frames = 0;
	int fps = 0;
	
	public BirbsContainer(AbstractBirbsManager world)
	{
		this.world = world;
	}
	
	public void start()
	{
		window = new Window(this);
//		renderer = new Renderer(this);
		input = new Input(this);
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop()
	{
	
	}
	
	public void run()
	{
		boolean running = true;
		
		boolean render = false;
		double startTime = 0;
		double endTime = System.nanoTime() / 1.0e9;
		double processedTime = 0;
		double unProcessedTime = 0;
		
		while (running)
		{
			render = false;
			
			startTime = System.nanoTime() / 1.0e9;
			processedTime = startTime - endTime;
			endTime = startTime;
			
			unProcessedTime += processedTime;
			frameTime += processedTime;
			
			while (unProcessedTime >= UPDATE_CAP)
			{
				unProcessedTime -= UPDATE_CAP;
				render = true;
				world.update(this, (float) UPDATE_CAP);
				input.update();
				
				if (frameTime >= 1.0)
				{
					frameTime = 0;
					fps = frames;
					frames = 0;
					System.out.println("FPS: " + fps);
				}
			}
			
			if (render)
			{
//				renderer.clear();
//				world.render(this, renderer);
				window.update();
				frames++;
			} else
			{
				try
				{
					Thread.sleep(1);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		dispose();
	}
	
	public void dispose()
	{
	
	}
	
	public int getWorldWidth()
	{
		return worldWidth;
	}
	
	public void setWorldWidth(int worldWidth)
	{
		this.worldWidth = worldWidth;
	}
	
	public int getWorldHeight()
	{
		return worldHeight;
	}
	
	public void setWorldHeight(int worldHeight)
	{
		this.worldHeight = worldHeight;
	}
	
	public double getScale()
	{
		return scale;
	}
	
	public void setScale(double scale)
	{
		Point2D.Double zoomPoint = input.getScaledMousePoint();
//		zoomPoint = new Point2D.Double(getWorldWidth()/2.0,getWorldHeight()/2.0);
//		zoomPoint = new Point2D.Double(getWorldWidth()/2.0 + getCameraOffsetX(), getWorldHeight()/2.0 + getCameraOffsetY());
		
		//Zoom In
		if(getInput().getScroll() < 0 && scale >= minScale && scale <= maxScale)
		{
			double diffX = ((zoomPoint.getX() + getCameraOffsetX()) * -.1) * (1.0 / scale);
			double diffY = ((zoomPoint.getY() + getCameraOffsetY()) * -.1) * (1.0 / scale);
			setCameraOffsetX(getCameraOffsetX() + diffX);
			setCameraOffsetY(getCameraOffsetY() + diffY);
		}
		//Zoom out
		else if(getInput().getScroll() > 0 && scale >= minScale && scale <= maxScale)
		{
			double diffX = ((zoomPoint.getX() + getCameraOffsetX()) * -.1) * (1.0 / scale);
			double diffY = ((zoomPoint.getY() + getCameraOffsetY()) * -.1) * (1.0 / scale);
			setCameraOffsetX(getCameraOffsetX() - diffX);
			setCameraOffsetY(getCameraOffsetY() - diffY);
		}
		scale = Math.round(scale * 10)/10.0;
		this.scale = Math.max(minScale, scale);
		this.scale = Math.min(maxScale, this.scale);
		Birb.setScale(this.scale);
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public Window getWindow()
	{
		return window;
	}
	
	public Input getInput()
	{
		return input;
	}
	
	public ArrayList<Birb> getBirbsList()
	{
		return birbsList;
	}
	
	public int getBirbTotalSpawned()
	{
		return birbTotalSpawned;
	}
	
	public int incrementBirbTotalSpawned()
	{
		birbTotalSpawned++;
		return birbTotalSpawned-1;
	}
	
	public void removeBirb(Birb birb)
	{
		birbsList.remove(birb);
		window.getJLayeredPane().remove(birb);
	}
	
	public void removeAllBirbs()
	{
		for (int i = birbsList.size() - 1; i >= 0; i--)
		{
			Birb birb = birbsList.get(i);
			removeBirb(birb);
		}
		setBirbTotalSpawned(0);
	}
	
	public void setBirbTotalSpawned(int birbTotalSpawned)
	{
		this.birbTotalSpawned = birbTotalSpawned;
	}
	
	public AbstractBirbsManager getWorld()
	{
		return world;
	}
	
	public double getCameraOffsetX()
	{
		return cameraOffsetX;
	}
	
	public double getCameraOffsetY()
	{
		return cameraOffsetY;
	}
	
	public void setCameraOffsetX(double x)
	{
		cameraOffsetX = x;
	}
	
	public void setCameraOffsetY(double y)
	{
		cameraOffsetY = y;
	}
	
	public double getCameraTempOffsetX()
	{
		return cameraTempOffsetX;
	}
	
	public void setCameraTempOffsetX(double cameraTempOffsetX)
	{
		this.cameraTempOffsetX = cameraTempOffsetX;
	}
	
	public double getCameraTempOffsetY()
	{
		return cameraTempOffsetY;
	}
	
	public void setCameraTempOffsetY(double cameraTempOffsetY)
	{
		this.cameraTempOffsetY = cameraTempOffsetY;
	}
	
	public int getWindowWidth()
	{
		return windowWidth;
	}
	
	public int getWindowHeight()
	{
		return windowHeight;
	}
}
