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
	
	private Point2D.Double mousePoint;
	private double mouseX, mouseY;
	private int scroll;
	
	public Input(BirbsContainer bc)
	{
		this.bc = bc;
		mouseX = 0;
		mouseY = 0;
		scroll = 0;
		
		bc.getWindow().getJLayeredPane().addKeyListener(this);
		bc.getWindow().getJLayeredPane().addMouseMotionListener(this);
		bc.getWindow().getJLayeredPane().addMouseListener(this);
		bc.getWindow().getJLayeredPane().addMouseWheelListener(this);
	}
	
	public void update()
	{
		scroll = 0;
		
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
	
	public boolean isButtonHeld(int button)
	{
		if (isButtonDown(button))
		{
			buttonsHeldTicks[button] = 30;
			return false;
		} else
		{
			buttonsHeldTicks[button]--;
//			System.out.println(buttonsHeldTicks[button]);
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
		mouseX = (e.getX() / bc.getScale());
		mouseY = (e.getY() / bc.getScale());
		mousePoint = new Point2D.Double(mouseX, mouseY);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		mouseX = (e.getX() / bc.getScale());
		mouseY = (e.getY() / bc.getScale());
		mousePoint = new Point2D.Double(mouseX, mouseY);
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
	
	public int getScroll()
	{
		return scroll;
	}
}
