package me.anthuony.birbs;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class BirbsManager extends AbstractBirbsManager
{
	
	public static void main(String[] args)
	{
		BirbsContainer bc = new BirbsContainer(new BirbsManager());
		bc.start();
	}
	
	@Override
	public void update(BirbsContainer bc, float dt)
	{
		doInputBinds(bc);
		doBirbLogic(bc);
		if(bc.getPursuitBirb() != null)
		{
			Point2D.Double pursuitBirbPoint = bc.getPursuitBirb().getWorldPoint();
			bc.setCameraOffsetX(bc.getWindowWidth() / bc.getScale() / 2 - pursuitBirbPoint.getX());
			bc.setCameraOffsetY(bc.getWindowHeight() / bc.getScale() / 2 - pursuitBirbPoint.getY());
		}
	}
	
	@Override
	public void render(BirbsContainer bc, Renderer r)
	{
		Graphics2D g2d = (Graphics2D) bc.getWindow().getG();
		AffineTransform original = g2d.getTransform();
		Font bigWords = new Font("Courier New", Font.BOLD, (int) ((19200 * bc.getScale()) / 20));
		Font interfaceFont = new Font("Courier New", Font.BOLD, 20);
		
		if(!bc.getInput().isKey(KeyEvent.VK_E))
		{
			r.drawBackground(g2d);
		}
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setStroke(new BasicStroke((float) (5 * bc.getScale())));
		
		int onScreenCount = 0;
		for (Birb birb : bc.getBirbsList())
		{
			if (birb.isOnScreen() /*&& birb.getID().endsWith("10")*/)
			{
				g2d.setTransform(original);
				g2d.translate(birb.getScreenPoint().getX(), birb.getScreenPoint().getY());
				r.drawBirb(g2d, birb);
				onScreenCount++;
			}
		}
		
		g2d.setTransform(original);
		
		//UI Text
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.setFont(interfaceFont);
		FontMetrics interfaceFontMetrics = g2d.getFontMetrics();
		
		
		ArrayList<String> topLeftText = new ArrayList<>(Arrays.asList(
				"" + bc.getBirbsList().size() + " Birbs in the World",
				"" + onScreenCount + " Birbs on Screen"
		));
		
		if(bc.isDrawUI())
		{
			r.drawLeftAlignedList(g2d, interfaceFont, topLeftText, 10, 0);
			r.drawMousePosition(g2d, interfaceFont);
			r.drawFPS(g2d, interfaceFont);
			r.drawLeftAlignedList(g2d, interfaceFont, bc.getChangelog(), 10, bc.getWindowHeight() - (bc.getChangelog().size() * interfaceFontMetrics.getAscent()) - 10);
			r.drawRightAlignedList(g2d, interfaceFont, bc.getKeybindsHint(), bc.getWindowWidth() - 10, bc.getWindowHeight() - (bc.getKeybindsHint().size() * interfaceFontMetrics.getAscent()) - 10);
			
			//Say click anywhere
			if (bc.getBirbsList().size() == 0)
			{
				r.drawCenteredString(g2d, bigWords, "Click Anywhere to Begin", 19200 / 2.0, 10800 / 2.0);
			}
		}
	}
	
	public void doInputBinds(BirbsContainer bc)
	{
		if (bc.getInput().isKey(KeyEvent.VK_ESCAPE))
		{
			System.exit(0);
		}
		if (bc.getInput().isKey(KeyEvent.VK_R))
		{
			reset(bc);
		}
		if (bc.getInput().isKeyDown(KeyEvent.VK_H))
		{
			bc.toggleHitboxes();
		}
		if (bc.getInput().isKeyDown(KeyEvent.VK_P))
		{
			bc.togglePause();
		}
		if (bc.getInput().isKeyDown(KeyEvent.VK_N))
		{
			bc.toggleNames();
		}
		if (bc.getInput().isKeyDown(KeyEvent.VK_SPACE))
		{
			if(bc.getBirbsList().size() > 0)
			{
				bc.setPursuitBirb(bc.getBirbsList().get((int) (Math.random() * bc.getBirbsList().size())));
			}
		}
		
		if (bc.getInput().isKeyDown(KeyEvent.VK_F1))
		{
			bc.toggleUI();
		}
		
		//Middle Click Pursuit Camera
		if (bc.getInput().isButtonDown(MouseEvent.BUTTON2))
		{
			if(bc.getBirbsList().size() > 0 && bc.getPursuitBirb() == null)
			{
				Birb closest = bc.getBirbsList().get(0);
				Point2D.Double mousePoint = bc.getInput().getScaledMousePoint();
				for (Birb b : bc.getBirbsList())
				{
					if (BirbLogic.getPointDistance(closest.getWorldPoint(), mousePoint) > BirbLogic.getPointDistance(b.getWorldPoint(), mousePoint))
					{
						closest = b;
					}
				}
				bc.setPursuitBirb(closest);
			}
			else
			{
				bc.setPursuitBirb(null);
			}
		}
		
		//Arrow Key Panning
		if(bc.getPursuitBirb() == null)
		{
			if (bc.getInput().isKey(KeyEvent.VK_UP) || bc.getInput().isKey(KeyEvent.VK_W))
			{
				bc.changeCameraOffsetY(bc.getCameraPanningInterval());
			}
			if (bc.getInput().isKey(KeyEvent.VK_DOWN) || bc.getInput().isKey(KeyEvent.VK_S))
			{
				bc.changeCameraOffsetY(-bc.getCameraPanningInterval());
			}
			if (bc.getInput().isKey(KeyEvent.VK_LEFT) || bc.getInput().isKey(KeyEvent.VK_A))
			{
				bc.changeCameraOffsetX(bc.getCameraPanningInterval());
			}
			if (bc.getInput().isKey(KeyEvent.VK_RIGHT) || bc.getInput().isKey(KeyEvent.VK_D))
			{
				bc.changeCameraOffsetX(-bc.getCameraPanningInterval());
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
		}
		else
		{
			if (bc.getInput().isKey(KeyEvent.VK_UP) || bc.getInput().isKey(KeyEvent.VK_W))
			{
				bc.setPursuitBirb(null);
			}
			if (bc.getInput().isKey(KeyEvent.VK_DOWN) || bc.getInput().isKey(KeyEvent.VK_S))
			{
				bc.setPursuitBirb(null);
			}
			if (bc.getInput().isKey(KeyEvent.VK_LEFT) || bc.getInput().isKey(KeyEvent.VK_A))
			{
				bc.setPursuitBirb(null);
			}
			if (bc.getInput().isKey(KeyEvent.VK_RIGHT) || bc.getInput().isKey(KeyEvent.VK_D))
			{
				bc.setPursuitBirb(null);
			}
			if (bc.getInput().isButtonDown(MouseEvent.BUTTON1))
			{
				bc.setPursuitBirb(null);
				bc.setCameraTempOffsetX(bc.getCameraOffsetX());
				bc.setCameraTempOffsetY(bc.getCameraOffsetY());
			}
			if (bc.getInput().isButtonHeld(MouseEvent.BUTTON1, 0))
			{
				bc.setCameraOffsetX(bc.getCameraTempOffsetX() + bc.getInput().getChangeMouseX());
				bc.setCameraOffsetY(bc.getCameraTempOffsetY() + bc.getInput().getChangeMouseY());
			}
		}
		
		//Birb spawning
		if (bc.getInput().isButtonDown(MouseEvent.BUTTON3))
		{
			addBirb(bc, bc.getInput().getScaledMousePoint(), 500);
//			addBirb(bc, new Point2D.Double(19200/2, 10800/2));
//			updateFormations(bc);
		}
		
		if (bc.getInput().isButtonHeld(MouseEvent.BUTTON3, 1))
		{
			addBirb(bc, bc.getInput().getScaledMousePoint());
//			updateFormations(bc);
		}
		
		if (bc.getInput().getScroll() != 0)
		{
			updateScale(bc);
		}
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
			logics.add(new BirbLogic(birbGroups.get(i), "BirbLogic" + i, tg, bc));
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
		Birb birb = new Birb("Birb" + bc.incrementBirbTotalSpawned(), bc.getRandomName(),p);
		bc.getBirbsList().add(birb);
//		if(bc.getBirbTotalSpawned() == 1)
//		{
//			bc.getWindow().getJLayeredPane().add(birb, JLayeredPane.PALETTE_LAYER);
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
		bc.setScale((bc.getScale() - bc.getInput().getScroll() / 10.0));
	}
	
	public void reset(BirbsContainer bc)
	{
		bc.removeAllBirbs();
		bc.setBirbTotalSpawned(0);
		bc.setCameraOffsetX((bc.getWorldWidth() - bc.getWindowWidth() * 10) / -2.0);
		bc.setCameraOffsetY((bc.getWorldHeight() - bc.getWindowHeight() * 10) / -2.0);
		bc.setScale(0.1);
		bc.setPursuitBirb(null);
		updateScale(bc);
	}
	
	public void updateFormations(BirbsContainer bc)
	{
		ArrayList<String> Formations = new ArrayList<>(Arrays.asList(
				"line",
				"circle2",
				"circle",
				"cubic",
				"cubic2"
		));
		
		int birbFormationCount = bc.getBirbsList().size()/Formations.size();
		
		for(int i=0; i<Formations.size(); i++)
		{
			ArrayList<Birb> formationBirbsList = new ArrayList<Birb>();
			for(int j=0; j<birbFormationCount; j++)
			{
				formationBirbsList.add(bc.getBirbsList().get(j + i * birbFormationCount));
			}
			if(i == Formations.size() - 1)
			{
				for(int j = (i + 1) * birbFormationCount; j<bc.getBirbsList().size(); j++)
				{
					formationBirbsList.add(bc.getBirbsList().get(j));
				}
			}
			Formation form = new Formation(Formations.get(i), formationBirbsList);
			form.updateFormationPoints(bc);
		}
	}
}
