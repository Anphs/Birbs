package me.anthuony.birbs;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	private final me.anthuony.birbs.BirbsContainer bc;
	
	private final int NUM_KEYS = 256;
	private final boolean[] keys = new boolean[NUM_KEYS];
	private final boolean[] keysLast = new boolean[NUM_KEYS];
	
	private final int NUM_BUTTONS = 5;
	private final boolean[] buttons = new boolean[NUM_BUTTONS];
	private final boolean[] buttonsLast = new boolean[NUM_BUTTONS];
	private final double[] buttonsHeldStart = new double[NUM_BUTTONS];
	private double mouseIdleTime = System.nanoTime() / 1.0e9;
	
	private Point2D.Double mousePoint = new Point2D.Double(0, 0), mouseDownPoint;
	private double mouseX, mouseY, changeMouseX, changeMouseY;
	
	private int scroll;
	
	public Input(BirbsContainer bc)
	{
		this.bc = bc;
		mouseX = 0;
		mouseY = 0;
		scroll = 0;
		changeMouseX = 0;
		changeMouseY = 0;
		
		bc.getWindow().getCanvas().addKeyListener(this);
		bc.getWindow().getCanvas().addMouseMotionListener(this);
		bc.getWindow().getCanvas().addMouseListener(this);
		bc.getWindow().getCanvas().addMouseWheelListener(this);
	}
	
	public void update()
	{
		scroll = 0;
		
		if (isButtonDown(MouseEvent.BUTTON1))
		{
			mouseDownPoint = getMousePoint();
		}
		
		if (mouseDownPoint != null)
		{
			changeMouseX = getMousePoint().getX() - mouseDownPoint.getX();
			changeMouseY = getMousePoint().getY() - mouseDownPoint.getY();
		}
		
		System.arraycopy(keys, 0, keysLast, 0, NUM_KEYS);
		
		System.arraycopy(buttons, 0, buttonsLast, 0, NUM_BUTTONS);
		
		updateMousePoint();
	}
	
	public boolean isKey(int keyCode)
	{
		return keys[keyCode];
	}
	
	public boolean isKeyUp(int keyCode)
	{
		return !keys[keyCode] && keysLast[keyCode];
	}
	
	public boolean isKeyDown(int keyCode)
	{
		return keys[keyCode] && !keysLast[keyCode];
	}
	
	public boolean isButton(int button)
	{
		return buttons[button];
	}
	
	public boolean isButtonUp(int button)
	{
		return !buttons[button] && buttonsLast[button];
	}
	
	public boolean isButtonDown(int button)
	{
		return buttons[button] && !buttonsLast[button];
	}
	
	public boolean isButtonHeld(int button, int delay)
	{
		if (isButtonDown(button))
		{
			buttonsHeldStart[button] = System.nanoTime() / 1.0e9;
			return false;
		}
		if (!isButton(button))
		{
			return false;
		}
		return (System.nanoTime() / 1.0e9 - buttonsHeldStart[button]) > delay;
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
	
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		keys[e.getKeyCode()] = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		keys[e.getKeyCode()] = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
	
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		buttons[e.getButton()] = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		buttons[e.getButton()] = false;
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
	
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
	
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		scroll = e.getWheelRotation();
	}
	
	public double getMouseX()
	{
		return mouseX;
	}
	
	public double getMouseY()
	{
		return mouseY;
	}
	
	public Point2D.Double getMousePoint()
	{
		return mousePoint;
	}
	
	public Point2D.Float getScaledMousePoint()
	{
		float x = (float) (getMousePoint().getX() - bc.getCameraOffsetX());
		float y = (float) (getMousePoint().getY() - bc.getCameraOffsetY());
		return new Point2D.Float(x, y);
	}
	
	public void updateMousePoint()
	{
		mouseX = MouseInfo.getPointerInfo().getLocation().getX() / bc.getScale();
		mouseY = MouseInfo.getPointerInfo().getLocation().getY() / bc.getScale();
		
		Point2D.Double newMousePoint = new Point2D.Double(mouseX, mouseY);
		if(!newMousePoint.equals(mousePoint))
		{
			mouseIdleTime = System.nanoTime() / 1.0e9;
		}
		mousePoint = newMousePoint;
	}
	
	public boolean isMouseIdle(double seconds)
	{
		return (System.nanoTime() / 1.0e9 - mouseIdleTime) > seconds;
	}
	
	public double getMouseIdleTime()
	{
		return System.nanoTime() / 1.0e9 - mouseIdleTime;
	}
	
	public double getChangeMouseX()
	{
		return changeMouseX;
	}
	
	public double getChangeMouseY()
	{
		return changeMouseY;
	}
	
	public int getScroll()
	{
		return scroll;
	}
}
