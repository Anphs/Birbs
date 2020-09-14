package me.anthuony.birbs;

import java.awt.*;
import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	private me.anthuony.birbs.BirbsContainer bc;
	
	private final int NUM_KEYS = 256;
	private boolean[] keys = new boolean[NUM_KEYS];
	private boolean[] keysLast = new boolean[NUM_KEYS];
	
	private final int NUM_BUTTONS = 5;
	private boolean[] buttons = new boolean[NUM_BUTTONS];
	private boolean[] buttonsLast = new boolean[NUM_BUTTONS];
	private int[] buttonsHeldTicks = new int[NUM_BUTTONS];
	
	private Point mousePoint;
	private int mouseX, mouseY;
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
		
		for (int i = 0; i < NUM_KEYS; i++)
		{
			keysLast[i] = keys[i];
		}
		
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			buttonsLast[i] = buttons[i];
		}
		
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
			System.out.println(button);
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
		mouseX = (int) (e.getX() / bc.getScale());
		mouseY = (int) (e.getY() / bc.getScale());
		mousePoint = new Point(mouseX, mouseY);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		mouseX = (int) (e.getX() / bc.getScale());
		mouseY = (int) (e.getY() / bc.getScale());
		mousePoint = new Point(mouseX, mouseY);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		scroll = e.getWheelRotation();
	}
	
	public int getMouseX()
	{
		return mouseX;
	}
	
	public int getMouseY()
	{
		return mouseY;
	}
	
	public Point getMousePoint()
	{
		return mousePoint;
	}
	
	public int getScroll()
	{
		return scroll;
	}
}
