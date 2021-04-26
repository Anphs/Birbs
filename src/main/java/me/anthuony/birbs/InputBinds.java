package me.anthuony.birbs;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class InputBinds
{
	public static void doInputBinds(BirbsContainer bc, BirbsManager bm)
	{
		if (bc.getInput().isKey(KeyEvent.VK_ESCAPE))
		{
			bc.setRunning(false);
		}
		if (bc.getInput().isKey(KeyEvent.VK_R))
		{
			bm.reset(bc);
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
			if (bc.getEntityList().size() > 0)
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
			if (bc.getEntityList().size() > 0 && bc.getPursuitBirb() == null)
			{
				Birb closest = (Birb) bc.getEntityList().get(0);
				Point2D.Float mousePoint = bc.getInput().getScaledMousePoint();
				for (Entity e : bc.getEntityList())
				{
					if(e.getType() == 1)
					{
						Birb b = (Birb) e;
						if (BirbLogic.getPointDistance(closest.getWorldPoint(), mousePoint) > BirbLogic.getPointDistance(b.getWorldPoint(), mousePoint))
						{
							closest = b;
						}
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
			if(bc.getEntityList().size() > 0)
			{
				if (bc.getPursuitBirbHistoryList().size() > 1 && bc.getPursuitBirbHistoryIndex() > 0)
				{
					bc.setPursuitBirb(bc.getPursuitBirbHistoryList().get(bc.decrementBirbHistoryIndex()));
				}
			}
		}
		if (bc.getInput().isKeyDown(KeyEvent.VK_PERIOD))
		{
			if(bc.getEntityList().size() > 0)
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
			bm.addBirb(bc, bc.getInput().getScaledMousePoint(), 1);
//			updateFormations(bc);
		}
		
		if (bc.getInput().isButtonHeld(MouseEvent.BUTTON3, 1))
		{
			bm.addBirb(bc, bc.getInput().getScaledMousePoint(), 1);
//			updateFormations(bc);
		}
		
		if (bc.getInput().getScroll() != 0)
		{
			bm.updateScale(bc);
		}
	}
}
