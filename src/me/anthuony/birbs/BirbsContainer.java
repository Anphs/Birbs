package me.anthuony.birbs;

import java.util.ArrayList;

public class BirbsContainer implements Runnable
{
	
	private Window window;
	//	private Renderer renderer;
	private Input input;
	private final AbstractBirbsManager world;
	
	private final double UPDATE_CAP = 1.0 / 60.0;
	private int worldWidth = 1920, worldHeight = 1080;
	private float scale = 1f;
	private String title = "Birbs";
	
	private ArrayList<Birb> birbsList = new ArrayList<>();
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
	
	public float getScale()
	{
		return scale;
	}
	
	public void setScale(float scale)
	{
		this.scale = scale;
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
	
	public void setBirbTotalSpawned(int birbTotalSpawned)
	{
		this.birbTotalSpawned = birbTotalSpawned;
	}
}
