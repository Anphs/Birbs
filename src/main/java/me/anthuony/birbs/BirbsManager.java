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
		doInputBinds(bc);
		
		BirbLogic.setBc(bc);
		doBirbLogic(bc);
		
		bc.getWindow().setBirbCount(bc.getBirbsList().size());
		BirbLogic.incrementColorOffset();
	}
	
	@Override
	public void render(BirbsContainer bc, Renderer r)
	{
	
	}
	
	public void doInputBinds(BirbsContainer bc)
	{
		if (bc.getInput().isKeyDown(KeyEvent.VK_ESCAPE))
		{
			System.exit(0);
		}
		if (bc.getInput().isKey(KeyEvent.VK_R))
		{
			reset(bc);
		}
		
		if (bc.getInput().isKeyDown(KeyEvent.VK_H))
		{
			Birb.toggleHitboxVisible();
		}
		
		if (bc.getInput().isButtonDown(MouseEvent.BUTTON3))
		{
			addBirb(bc, bc.getInput().getScaledMousePoint(), 5000);
//			addBirb(bc, new Point2D.Double(19200/2, 10800/2));
			Formation form = new Formation("line", bc.getBirbsList());
			form.updateFormationPoints(bc);
		}
		
		if (bc.getInput().isButtonHeld(MouseEvent.BUTTON3, 30))
		{
			addBirb(bc, bc.getInput().getScaledMousePoint());
			Formation form = new Formation("line", bc.getBirbsList());
			form.updateFormationPoints(bc);
		}
		
		if (bc.getInput().isButtonDown(MouseEvent.BUTTON1))
		{
			bc.setCameraTempOffsetX(bc.getCameraOffsetX());
			bc.setCameraTempOffsetY(bc.getCameraOffsetY());
		}
		
		if (bc.getInput().isButtonHeld(MouseEvent.BUTTON1, 0))
		{
			bc.setCameraOffsetX(bc.getCameraTempOffsetX() + bc.getInput().getChangeMouseX());
			bc.setCameraOffsetY(bc.getCameraTempOffsetY() + bc.getInput().getChangeMouseY());
		}
		
		if(bc.getInput().getScroll() != 0)
		{
			updateScale(bc);
		}
		Birb.setOffsetX(bc.getCameraOffsetX());
		Birb.setOffsetY(bc.getCameraOffsetY());
		bc.getWindow().setOffsetX(bc.getCameraOffsetX());
		bc.getWindow().setOffsetY(bc.getCameraOffsetY());
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
//		if(bc.getBirbTotalSpawned() == 1)
//		{
			bc.getWindow().getJLayeredPane().add(birb, JLayeredPane.PALETTE_LAYER);
//		}
	}
	
	public void addBirb(BirbsContainer bc, Point2D.Double p, int multiplier)
	{
		for (int i = 0; i < multiplier; i++)
		{
			double x = p.getX() + 10 * multiplier * (Math.random() - 0.5);
			double y = p.getY() + 10 * multiplier * (Math.random() - 0.5);
			Point2D.Double newPoint = new Point2D.Double(x, y);
			addBirb(bc, newPoint);
		}
	}
	
	public void updateScale(BirbsContainer bc)
	{
		bc.setScale((bc.getScale() - bc.getInput().getScroll()/10.0));
		bc.getWindow().setScale(bc.getScale());
	}
	
	public void reset(BirbsContainer bc)
	{
		bc.removeAllBirbs();
		bc.setCameraOffsetX((bc.getWorldWidth() - bc.getWindowWidth() * 10) / -2.0);
		bc.setCameraOffsetY((bc.getWorldHeight() - bc.getWindowHeight() * 10) / -2.0);
		bc.setScale(0.1);
		updateScale(bc);
	}
	
	public static void main(String[] args)
	{
		BirbsContainer bc = new BirbsContainer(new BirbsManager());
		bc.start();
		Birb.setScale(bc.getScale());
	}
}
