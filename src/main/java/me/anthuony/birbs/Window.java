package me.anthuony.birbs;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Window extends JLayeredPane
{
	private final JFrame frame;
	private int birbCount;
	private double offsetX = 0, offsetY = 0, scale = 1;
	
	public Window(me.anthuony.birbs.BirbsContainer bc)
	{
		Dimension s = new Dimension((int) bc.getWindowWidth(), (int) bc.getWindowHeight());
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(s);
		frame.setLocationRelativeTo(null);
		frame.setTitle(bc.getTitle());
		frame.setLayout(new BorderLayout());
		
		this.setPreferredSize(s);
		this.setBackground(Color.GRAY);
		this.setLayout(null);
		this.setFocusable(true);
		this.requestFocus();
		
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		
		//Background
		drawBackground(g2d);
		
		//Mouse Position
		drawMousePosition(g2d);
		
		//Say click anywhere or birb count
		if (birbCount == 0)
		{
			g2d.setColor(Color.WHITE);
			Font bigWords = new Font("Courier New", Font.BOLD, (int) ((19200 * scale) / 20));
			drawCenteredString(g2d, bigWords, "Click Anywhere to Begin", 19200 / 2, 10800 / 2);
		} else
		{
			g2d.setColor(Color.GRAY);
			drawCenteredString(g2d, new Font("Courier New", Font.BOLD, (int)(10800 * scale) / 5), "" + birbCount, 19200 / 2, 10800 / 2);
		}
		
		//Notes
		ArrayList<String> instructions = new ArrayList<>(Arrays.asList(
//				"Use the scroll wheel to zoom in and out",
//				"Left click drag to pan around the world",
//				"Right click to spawn 100 birbs"
		));
		
		g2d.setFont(new Font("Courier New", Font.BOLD, 20));
		g2d.setColor(Color.WHITE);
		
		for(int i=0; i<instructions.size(); i++) {
			g2d.drawString(instructions.get(i), 10, i*20+20);
		}
		
		super.paintChildren(g);
	}
	
	public void drawBackground(Graphics2D g2d)
	{
		g2d.setPaint(new Color(50, 50, 50, 255));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		g2d.setPaint(new Color(0, 0, 0, 255));
		g2d.fillRect((int)(offsetX * scale) , (int)(offsetY * scale), (int)(19200 * scale), (int)(10800 * scale));
	}
	
	public void drawCenteredString(Graphics2D g2d, Font f, String str, int x, int y)
	{
		FontMetrics metrics = g2d.getFontMetrics(f);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		x *= scale;
		y *= scale;
		x -= metrics.stringWidth(str) / 2;
		y += metrics.getAscent() / 2;
		x += offsetX * scale;
		y += offsetY * scale;
		
		g2d.setFont(f);
		g2d.drawString(str, x, y);
	}
	
	public void drawMousePosition(Graphics g2d)
	{
		g2d.setColor(Color.WHITE);
		try
		{
			int x = (int) getMousePosition().getX();
			int y = (int) getMousePosition().getY();
			int X = (int) (x - offsetX * scale);
			int Y = (int) (y - offsetY * scale);
			g2d.drawString("x: " + x + " y: " + y, 10, getHeight() - 10);
//			g2d.drawString("X: " + X + " Y: " + Y, 10, getHeight() - 25);
		} catch (NullPointerException e)
		{
			//Do nothing since the mouse is not over the window
		}
	}
	
	public void update()
	{
		frame.repaint();
	}
	
	public JLayeredPane getJLayeredPane()
	{
		return this;
	}
	
	public void setBirbCount(int birbCount)
	{
		this.birbCount = birbCount;
	}
	
	public void setOffsetX(double offsetX)
	{
		this.offsetX = offsetX;
	}
	
	public void setOffsetY(double offsetY)
	{
		this.offsetY = offsetY;
	}
	
	public void setScale(double scale)
	{
		this.scale = scale;
	}
}
