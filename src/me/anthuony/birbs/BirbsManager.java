package me.anthuony.birbs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class BirbsManager extends AbstractBirbsManager
{
	
	private static ArrayList<Birb> birbsList = new ArrayList<Birb>();
	private static int birbTotalSpawned;
	
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
			addBirb(bc, bc.getInput().getMousePoint(), 100);
		}
		
		if (bc.getInput().isButtonHeld(MouseEvent.BUTTON3))
		{
			addBirb(bc, bc.getInput().getMousePoint(), 100);
		}
		
		BirbLogic.setBc(bc);
		doBirbLogic();
		
		bc.getWindow().setBirbCount(birbsList.size());
	}
	
	@Override
	public void render(BirbsContainer bc, Renderer r)
	{
	
	}
	
	private void doBirbLogic()
	{
		ThreadGroup tg = new ThreadGroup("Update Locations");
		int np = Runtime.getRuntime().availableProcessors() - 2;
		
		ArrayList<ArrayList<Birb>> birbGroups = new ArrayList<ArrayList<Birb>>();
		ArrayList<BirbLogic> logics = new ArrayList<BirbLogic>();
		
		for (int i = 0; i < birbsList.size(); i++)
		{
			if (i < np)
			{
				birbGroups.add(new ArrayList<Birb>());
			}
			ArrayList<Birb> current = birbGroups.get(i % np);
			current.add(birbsList.get(i));
		}
//		System.out.println(birbGroups.size());
		for (int i = 0; i < birbGroups.size(); i++)
		{
			logics.add(new BirbLogic(birbGroups.get(i), "BirbLogic" + i, tg));
//			logics.add(new BirbLogic(bc, birbsList.get(i)));
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
	
	public void addBirb(BirbsContainer bc, Point p)
	{
		Birb birb = new Birb("Birb" + birbTotalSpawned++, p);
		birbsList.add(birb);
		bc.getWindow().getJLayeredPane().add(birb, JLayeredPane.PALETTE_LAYER);
	}
	
	public void addBirb(BirbsContainer bc, Point p, int multiplier)
	{
		for (int i = 0; i < multiplier; i++)
		{
			addBirb(bc, bc.getInput().getMousePoint());
		}
	}
	
	public void removeBirb(BirbsContainer bc, Birb birb)
	{
		birbsList.remove(birb);
		bc.getWindow().getJLayeredPane().remove(birb);
	}
	
	public void removeAllBirbs(BirbsContainer bc)
	{
		for (int i = birbsList.size() - 1; i >= 0; i--)
		{
			Birb birb = birbsList.get(i);
			removeBirb(bc, birb);
		}
		birbTotalSpawned = 0;
	}
	
	public static void main(String[] args)
	{
		BirbsContainer bc = new BirbsContainer(new BirbsManager());
		bc.start();
	}
}
