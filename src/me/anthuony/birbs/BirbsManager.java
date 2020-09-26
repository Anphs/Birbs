package me.anthuony.birbs;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BirbsManager extends AbstractBirbsManager
{
	
	@Override
	public void update(BirbsContainer bc, float dt)
	{
//		System.out.println(bc.getHeight());
		
		if (bc.getInput().isKeyDown(KeyEvent.VK_ESCAPE))
		{
			System.exit(0);
		}
		if (bc.getInput().isKey(KeyEvent.VK_R))
		{
			removeAllBirbs(bc);
		}
		
		if (bc.getInput().isKeyDown(KeyEvent.VK_H))
		{
			Birb.toggleHitboxVisible();
		}
		
		if (bc.getInput().isButtonDown(MouseEvent.BUTTON1))
		{
			addBirb(bc, bc.getInput().getMousePoint());
		}
		
		if (bc.getInput().isButtonHeld(MouseEvent.BUTTON1))
		{
			addBirb(bc, bc.getInput().getMousePoint());
		}
		
		if (bc.getInput().isButtonDown(MouseEvent.BUTTON3))
		{
			addBirb(bc, bc.getInput().getMousePoint(), 10);
		}
		
		if (bc.getInput().isButtonHeld(MouseEvent.BUTTON3))
		{
			addBirb(bc, bc.getInput().getMousePoint(), 10);
		}
		
		BirbLogic.setBc(bc);
		doBirbLogic(bc);
		
		bc.getWindow().setBirbCount(bc.getBirbsList().size());
	}
	
	@Override
	public void render(BirbsContainer bc, Renderer r)
	{
	
	}
	
	private void doBirbLogic(BirbsContainer bc)
	{
		ThreadGroup tg = new ThreadGroup("Update Locations");
		int np = Runtime.getRuntime().availableProcessors() - 2;
		
		ArrayList<ArrayList<Birb>> birbGroups = new ArrayList<>();
		ArrayList<BirbLogic> logics = new ArrayList<>();
		
		for (int i = 0; i < bc.getBirbsList().size(); i++)
		{
			if (i < np)
			{
				birbGroups.add(new ArrayList<>());
			}
			ArrayList<Birb> current = birbGroups.get(i % np);
			current.add(bc.getBirbsList().get(i));
		}
		
		for (int i = 0; i < birbGroups.size(); i++)
		{
			logics.add(new BirbLogic(birbGroups.get(i), "BirbLogic" + i, tg));
		}
		int i = 0;
		while (i < logics.size())
		{
//
			if (tg.activeCount() < np)
			{
				BirbLogic logic = logics.get(i);
				logic.start();
				i++;
			}
		}
	}
	
	public void addBirb(BirbsContainer bc, Point2D.Double p)
	{
		Birb birb = new Birb("Birb" + bc.incrementBirbTotalSpawned(), p);
		bc.getBirbsList().add(birb);
		bc.getWindow().getJLayeredPane().add(birb, JLayeredPane.PALETTE_LAYER);
	}
	
	public void addBirb(BirbsContainer bc, Point2D.Double p, int multiplier)
	{
		for (int i = 0; i < multiplier; i++)
		{
			double x = p.getX() + 2 * multiplier * (Math.random() - 0.5);
			double y = p.getY() + 2 * multiplier * (Math.random() - 0.5);
			Point2D.Double newPoint = new Point2D.Double(x, y);
			addBirb(bc, newPoint);
		}
	}
	
	public void removeBirb(BirbsContainer bc, Birb birb)
	{
		bc.getBirbsList().remove(birb);
		bc.getWindow().getJLayeredPane().remove(birb);
	}
	
	public void removeAllBirbs(BirbsContainer bc)
	{
		for (int i = bc.getBirbsList().size() - 1; i >= 0; i--)
		{
			Birb birb = bc.getBirbsList().get(i);
			removeBirb(bc, birb);
		}
		bc.setBirbTotalSpawned(0);
	}
	
	public static void main(String[] args)
	{
		BirbsContainer bc = new BirbsContainer(new BirbsManager());
		bc.start();
	}
}
