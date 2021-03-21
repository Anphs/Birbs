package me.anthuony.birbs;

import com.aparapi.Kernel;
import com.aparapi.Range;
import scala.Array;

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
	public void update(BirbsContainer bc)
	{
		doInputBinds(bc);
		
		//Logistics Equation
		if(bc.getInput().isMouseIdle(.1))
		{
			bc.setDpdt(1 / 1000.0 * (bc.getCapacity() - bc.getEntityCount()));
			addBirb(bc, new Point2D.Float((float) (bc.getWorldWidth() / 2.0),(float) (bc.getWorldHeight() / 2.0)), (int) bc.getDpdt());
		}
		
		long t1 = System.currentTimeMillis();
		
//		doBirbLogic(bc);
		doEntityLogic(bc);
		
		long t2 = System.currentTimeMillis();
		
		bc.setKernelTime(t2 - t1);
		
		if (bc.getPursuitBirb() != null)
		{
			Point2D.Float pursuitBirbPoint = bc.getPursuitBirb().getWorldPoint();
			bc.setCameraOffsetX(bc.getWindowWidth() / bc.getScale() / 2 - pursuitBirbPoint.getX());
			bc.setCameraOffsetY(bc.getWindowHeight() / bc.getScale() / 2 - pursuitBirbPoint.getY());
		}
	}
	
	@Override
	public void render(BirbsContainer bc, Renderer r)
	{
		Graphics2D g2d = (Graphics2D) bc.getWindow().getG();
		AffineTransform original = g2d.getTransform();
		Font bigWords = new Font("Courier New", Font.BOLD, (int) ((bc.getWorldHeight() * bc.getScale()) / 20));
		Font interfaceFont = new Font("Courier New", Font.BOLD, 20);
		
		if (!bc.getInput().isKey(KeyEvent.VK_E))
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
				g2d.translate(birb.getXScreen(), birb.getYScreen());
				r.drawBirb(g2d, birb);
				onScreenCount++;
			}
		}
		
		g2d.setTransform(original);
		
		//UI Text
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		if (bc.getInput().getMouseIdleTime() > 3)
		{
			g2d.setColor(new Color(255,255,255, ExtraMath.boundNumber((int)(255 - (bc.getInput().getMouseIdleTime() - 3) * 1000), 0 , 255)));
			bc.getWindow().getFrame().getContentPane().setCursor(bc.getWindow().getBlankCursor());
		}
		else
		{
			g2d.setColor(Color.WHITE);
			bc.getWindow().getFrame().getContentPane().setCursor(Cursor.getDefaultCursor());
		}
		
		g2d.setFont(interfaceFont);
		FontMetrics interfaceFontMetrics = g2d.getFontMetrics();
		
		ArrayList<String> topLeftText = new ArrayList<>(Arrays.asList(
				"" + bc.getBirbsList().size() + " Birbs in the World",
				"" + onScreenCount + " Birbs on Screen",
				"" + (int) bc.getDpdt() + " birbs/update Rate of Spawning"
		));
		
		int xMouse = (int) (bc.getInput().getMousePoint().getX() * bc.getScale());
		int yMouse = (int) (bc.getInput().getMousePoint().getY() * bc.getScale());
		ArrayList<String> topRightText = new ArrayList<>(Arrays.asList(
				"Kernel Processing Time: " + bc.getKernelTime() + "ms",
				"Render Time: " + bc.getRenderTime() + "ms",
				"x: " + xMouse + " y: " + yMouse,
				"FPS: " + bc.getFps()
		));
		
		ArrayList<String> entityStringList = new ArrayList<>();
		for(int i=0; i<bc.getEntityCount(); i++)
		{
			String str = ""+bc.geteChunk(i);
			entityStringList.add(str);
		}
		
		ArrayList<String> chunkInfo = new ArrayList<>();
		for(int i=0; i<bc.getChunkWidth() * bc.getChunkHeight();i ++)
		{
			String str = i + " pos: " + bc.getChunkPos(i) + " count: " + bc.getChunkEntityCount(i);
			chunkInfo.add(str);
		}
		
		ArrayList<String> pursuitBirbHistoryListNames = new ArrayList<>();
		for(Birb b: bc.getPursuitBirbHistoryList())
		{
			if(b != null)
			{
				String name = b.getName();
				if (b == bc.getPursuitBirb())
				{
					name += "   <<<";
				}
				pursuitBirbHistoryListNames.add(name);
			}
		}
		
		if (bc.isDrawUI())
		{
			r.drawLeftAlignedList(g2d, interfaceFont, topLeftText, 10, 0);
			r.drawRightAlignedList(g2d, interfaceFont, topRightText, bc.getWindowWidth() - 10, 0);
			r.drawLeftAlignedList(g2d, interfaceFont, bc.getChangelog(), 10, bc.getWindowHeight() - (bc.getChangelog().size() * interfaceFontMetrics.getAscent()) - 10);
			r.drawRightAlignedList(g2d, interfaceFont, bc.getKeybindsHint(), bc.getWindowWidth() - 10, bc.getWindowHeight() - (bc.getKeybindsHint().size() * interfaceFontMetrics.getAscent()) - 10);
			r.drawLeftAlignedList(g2d, interfaceFont, pursuitBirbHistoryListNames, 10, 100);
			
//			r.drawLeftAlignedList(g2d, interfaceFont, entityStringList, 300, 0);
//			r.drawLeftAlignedList(g2d, interfaceFont, chunkInfo, 400, 0);
			
			//Say click anywhere
			if (bc.getBirbsList().size() == 0)
			{
				r.drawCenteredString(g2d, bigWords, "Click Anywhere to Begin", bc.getWorldWidth() / 2.0, bc.getWorldHeight() / 2.0);
			}
		}
	}
	
	private void doInputBinds(BirbsContainer bc)
	{
		if (bc.getInput().isKey(KeyEvent.VK_ESCAPE))
		{
			bc.setRunning(false);
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
			if (bc.getBirbsList().size() > 0)
			{
				bc.setPursuitBirb(bc.getRandomUniqueBirb(bc.getBirbsList(), bc.getPursuitBirbHistoryList()));
			}
		}
		
		if (bc.getInput().isKeyDown(KeyEvent.VK_F1))
		{
			bc.toggleUI();
		}
		
		//Middle Click Pursuit Camera
		if (bc.getInput().isButtonDown(MouseEvent.BUTTON2))
		{
			if (bc.getBirbsList().size() > 0 && bc.getPursuitBirb() == null)
			{
				Birb closest = bc.getBirbsList().get(0);
				Point2D.Float mousePoint = bc.getInput().getScaledMousePoint();
				for (Birb b : bc.getBirbsList())
				{
					if (BirbLogic.getPointDistance(closest.getWorldPoint(), mousePoint) > BirbLogic.getPointDistance(b.getWorldPoint(), mousePoint))
					{
						closest = b;
					}
				}
				bc.setPursuitBirb(closest);
			} else
			{
				bc.setPursuitBirb(null);
			}
		}
		
		// < > Navigate Pursuit Birb List
		if (bc.getInput().isKeyDown(KeyEvent.VK_COMMA))
		{
			if(bc.getBirbsList().size() > 0)
			{
				if (bc.getPursuitBirbHistoryList().size() > 1 && bc.getPursuitBirbHistoryIndex() > 0)
				{
					bc.setPursuitBirb(bc.getPursuitBirbHistoryList().get(bc.decrementBirbHistoryIndex()));
				}
			}
		}
		if (bc.getInput().isKeyDown(KeyEvent.VK_PERIOD))
		{
			if(bc.getBirbsList().size() > 0)
			{
				if (bc.getPursuitBirbHistoryList().size() - 1 > bc.getPursuitBirbHistoryIndex())
				{
					bc.setPursuitBirb(bc.getPursuitBirbHistoryList().get(bc.incrementBirbHistoryIndex()));
				} else
				{
					bc.setPursuitBirb(bc.getRandomUniqueBirb(bc.getBirbsList(), bc.getPursuitBirbHistoryList()));
				}
			}
		}
		
		//Arrow Key Panning
		if (bc.getPursuitBirb() == null)
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
			
		} else
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
		}
		if (bc.getInput().isButtonHeld(MouseEvent.BUTTON1, 0))
		{
			bc.setCameraOffsetX(bc.getCameraTempOffsetX() + bc.getInput().getChangeMouseX());
			bc.setCameraOffsetY(bc.getCameraTempOffsetY() + bc.getInput().getChangeMouseY());
		}
		
		//Birb spawning
		if (bc.getInput().isButtonDown(MouseEvent.BUTTON3))
		{
			addBirb(bc, bc.getInput().getScaledMousePoint(), 100);
//			updateFormations(bc);
		}
		
		if (bc.getInput().isButtonHeld(MouseEvent.BUTTON3, 1))
		{
			addBirb(bc, bc.getInput().getScaledMousePoint(), 1);
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
		int np = Runtime.getRuntime().availableProcessors();
		
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
			if (tg.activeCount() < np)
			{
				BirbLogic logic = logics.get(i);
				logic.start();
				i++;
			}
		}
	}
	
	private void doEntityLogic(BirbsContainer bc)
	{
//		for(int i=0; i<bc.getEntityCount(); i++)
//		{
//			bc.seteXScreen(i, (float) (3440 * Math.random()));
//			bc.seteYScreen(i, (float) (1440 * Math.random()));
//			bc.seteOnScreen(i, true);
//		}
		
		EntityKernel kernel = bc.getKernel();
		kernel.updateVars(bc);

		Range range = Range.create(bc.getEntityCount());
		kernel.execute(range);
	}
	
	private void addBirb(BirbsContainer bc, float worldX, float worldY)
	{
		Birb birb = new Birb(bc, bc.incrementEntityCount(), bc.getRandomName(), worldX, worldY, 1);
		bc.getBirbsList().add(birb);
	}
	
	private void addBirb(BirbsContainer bc, Point2D.Float p, int multiplier)
	{
		for (int i = 0; i < multiplier; i++)
		{
			float x = (float) (p.getX() + 10 * multiplier * (Math.random() - 0.5)) % bc.getWorldWidth();
			float y = (float) (p.getY() + 10 * multiplier * (Math.random() - 0.5)) % bc.getWorldHeight();
			
			addBirb(bc, x, y);
		}
	}
	
	public void updateScale(BirbsContainer bc)
	{
		bc.setScale((bc.getScale() - bc.getInput().getScroll() / 10.0));
	}
	
	public void reset(BirbsContainer bc)
	{
		bc.removeAllBirbs();
		bc.setEntityCount(0);
		bc.setCameraOffsetX((bc.getWorldWidth() - bc.getWindowWidth() * 10) / -2.0);
		bc.setCameraOffsetY((bc.getWorldHeight() - bc.getWindowHeight() * 10) / -2.0);
		bc.setScale(0.1);
		bc.setPursuitBirb(null);
		bc.getPursuitBirbHistoryList().clear();
		updateScale(bc);
		
		Arrays.fill(bc.geteType(), (byte)0);
		Arrays.fill(bc.geteXWorld(), 0);
		Arrays.fill(bc.geteYWorld(), 0);
		Arrays.fill(bc.geteXScreen(), 0);
		Arrays.fill(bc.geteYScreen(), 0);
		Arrays.fill(bc.geteSpeed(), 0);
		Arrays.fill(bc.geteDirection(), 0);
		Arrays.fill(bc.geteAngularAcceleration(), 0);
		Arrays.fill(bc.geteScale(), 0);
		Arrays.fill(bc.geteChunk(), -100);
		Arrays.fill(bc.geteColor(), null);
		Arrays.fill(bc.geteOnScreen(), false);
		
		Arrays.fill(bc.getChunkPos(), 0);
		Arrays.fill(bc.getChunkEntityCount(), 0);
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
		
		int birbFormationCount = bc.getBirbsList().size() / Formations.size();
		
		for (int i = 0; i < Formations.size(); i++)
		{
			ArrayList<Birb> formationBirbsList = new ArrayList<>();
			for (int j = 0; j < birbFormationCount; j++)
			{
				formationBirbsList.add(bc.getBirbsList().get(j + i * birbFormationCount));
			}
			if (i == Formations.size() - 1)
			{
				for (int j = (i + 1) * birbFormationCount; j < bc.getBirbsList().size(); j++)
				{
					formationBirbsList.add(bc.getBirbsList().get(j));
				}
			}
			Formation form = new Formation(Formations.get(i), formationBirbsList);
			form.updateFormationPoints(bc);
		}
	}
}
