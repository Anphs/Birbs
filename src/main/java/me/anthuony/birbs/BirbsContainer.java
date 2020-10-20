package me.anthuony.birbs;

import java.util.ArrayList;

public class BirbsContainer implements Runnable
{
	
	private Window window;
	//	private Renderer renderer;
	private Input input;
	private final AbstractBirbsManager world;
	
	private final double UPDATE_CAP = 1.0 / 60.0;
	private int windowWidth = 1920, windowHeight = 1080;
	private int worldWidth = 19200, worldHeight = 10800;
	private double cameraOffsetX = 0, cameraOffsetY = 0, cameraTempOffsetX = 0, cameraTempOffsetY = 0;
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
		if(scale <= minScale)
		{
			this.scale = minScale;
		}
		else if(scale >= maxScale)
		{
			this.scale = maxScale;
		}
		else
		{
			this.scale = scale;
		}
		Birb.setScale(this.scale);
//		if(getInput().getScroll() < 0 && scale >= minScale && scale <= maxScale)
//		{
//			setCameraOffsetX(getCameraOffsetX() - (1920 * (.125)) / getScale());
//			setCameraOffsetY(getCameraOffsetY() - (1080 * (.125)) / getScale());
//		}
//		else if(getInput().getScroll() > 0 && scale >= minScale && scale <= maxScale)
//		{
//			setCameraOffsetX(getCameraOffsetX() + (1920 * (.125)) / getScale());
//			setCameraOffsetY(getCameraOffsetY() + (1080 * (.125)) / getScale());
//		}
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
