package me.anthuony.birbs;

import javax.swing.*;
import java.awt.*;

public class Window extends JLayeredPane
{
	private final JFrame frame;
	private int birbCount;
	
	public Window(me.anthuony.birbs.BirbsContainer bc)
	{
		Dimension s = new Dimension((int) (bc.getWorldWidth() * bc.getScale()), (int) (bc.getWorldHeight() * bc.getScale()));
		
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
			Font bigWords = new Font("Courier New", Font.BOLD, getWidth() / 20);
			drawCenteredString(g2d, bigWords, "Click Anywhere to Begin", getWidth() / 2, getHeight() / 2);
		} else
		{
			g2d.setColor(Color.GRAY);
			drawCenteredString(g2d, new Font("Courier New", Font.BOLD, getHeight() / 5), "" + birbCount, getWidth() / 2, getHeight() / 2);
		}
		
		super.paintChildren(g);
	}
	
	public void drawBackground(Graphics2D g2d)
	{
		g2d.setPaint(new Color(0, 0, 0, 255));
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}
	
	public void drawCenteredString(Graphics2D g2d, Font f, String str, int x, int y)
	{
		FontMetrics metrics = g2d.getFontMetrics(f);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		x -= metrics.stringWidth(str) / 2;
		y += metrics.getAscent() / 2;
		
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
			g2d.drawString("x: " + x + " y: " + y, 10, getHeight() - 10);
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
}
