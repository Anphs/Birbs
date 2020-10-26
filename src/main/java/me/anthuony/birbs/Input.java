package me.anthuony.birbs;

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
	private final int[] buttonsHeldTicks = new int[NUM_BUTTONS];
	
	private Point2D.Double mousePoint = new Point2D.Double(0, 0), mouseDownPoint;
	private double mouseX, mouseY, changeMouseX, changeMouseY;
	
	private int scroll;
	
	public Input(BirbsContainer bc)
	{
		this.bc = bc;
		mouseX = 0;
		mouseY = 0;
		scroll = 0;
		changeMouseY = 0;
		changeMouseY = 0;
		
		bc.getWindow().getJLayeredPane().addKeyListener(this);
		bc.getWindow().getJLayeredPane().addMouseMotionListener(this);
		bc.getWindow().getJLayeredPane().addMouseListener(this);
		bc.getWindow().getJLayeredPane().addMouseWheelListener(this);
	}
	
	public void update()
	{
		scroll = 0;
		
		if(isButtonDown(MouseEvent.BUTTON1))
		{
			mouseDownPoint = getMousePoint();
		}
		
		if(mouseDownPoint != null)
		{
			changeMouseX = getMousePoint().getX() - mouseDownPoint.getX();
			changeMouseY = getMousePoint().getY() - mouseDownPoint.getY();
		}
		
		System.arraycopy(keys, 0, keysLast, 0, NUM_KEYS);
		
		System.arraycopy(buttons, 0, buttonsLast, 0, NUM_BUTTONS);
		
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
			buttonsHeldTicks[button] = delay;
			return false;
		} else
		{
			buttonsHeldTicks[button]--;
		}
		return buttons[button] && buttonsHeldTicks[button] < 0;
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
		updateMousePoint(e);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		updateMousePoint(e);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		scroll = e.getWheelRotation();
		mouseX = ((e.getX() / bc.getScale()));
		mouseY = (e.getY() / bc.getScale());
		mousePoint = new Point2D.Double(mouseX, mouseY);
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
	
	public Point2D.Double getScaledMousePoint()
	{
		double x = (getMousePoint().getX() - bc.getCameraOffsetX());
		double y = (getMousePoint().getY() - bc.getCameraOffsetY());
		return new Point2D.Double(x, y);
	}
	
	public void updateMousePoint(MouseEvent e)
	{
		mouseX = ((e.getX() / bc.getScale()));
		mouseY = (e.getY() / bc.getScale());
		mousePoint = new Point2D.Double(mouseX, mouseY);
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
	
	public Point2D.Double getMouseDownPoint()
	{
		return mouseDownPoint;
	}
}
