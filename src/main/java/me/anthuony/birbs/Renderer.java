package me.anthuony.birbs;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Renderer
{
	private static final int d = 70 / 2;
	private static final int dd = (int) (d / 1.5);
	private static final int ddd = d / 2;
	private static final int[] triangleX = new int[]{dd, -dd, -ddd, -dd};
	private static final int[] triangleY = new int[]{0, ddd, 0, -ddd};
	private static Polygon birbTriangle = new Polygon(triangleX, triangleY, 4);
	BirbsContainer bc;
	
	public Renderer(BirbsContainer bc)
	{
		this.bc = bc;
	}
	
	public void drawRect(Graphics2D g2d)
	{
		g2d.setColor(Color.ORANGE);
		g2d.fillRect(50, 50, 50, 50);
	}
	
	public void drawBackground(Graphics2D g2d)
	{
		g2d.setPaint(new Color(50, 50, 50, 255));
		g2d.fillRect(0, 0, bc.getWindowWidth(), bc.getWindowHeight());
		
		g2d.setPaint(new Color(0, 0, 0, 255));
		g2d.fillRect((int) (bc.getCameraOffsetX() * bc.getScale()), (int) (bc.getCameraOffsetY() * bc.getScale()), (int) (19200 * bc.getScale()), (int) (10800 * bc.getScale()));
	}
	
	public void drawCenteredString(Graphics2D g2d, Font f, String str, int x, int y)
	{
		FontMetrics metrics = g2d.getFontMetrics(f);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		x *= bc.getScale();
		y *= bc.getScale();
		x -= metrics.stringWidth(str) / 2;
		y += metrics.getAscent() / 2;
		x += bc.getCameraOffsetX() * bc.getScale();
		y += bc.getCameraOffsetY() * bc.getScale();
		
		g2d.setFont(f);
		g2d.drawString(str, x, y);
	}
	
	public void drawMousePosition(Graphics g2d)
	{
		g2d.setColor(Color.WHITE);
		Font smallWords = new Font("Courier New", Font.BOLD, 20);
		g2d.setFont(smallWords);
		try
		{
			int x = (int) (bc.getInput().getMousePoint().getX() * bc.getScale());
			int y = (int) (bc.getInput().getMousePoint().getY() * bc.getScale());
			g2d.drawString("x: " + x + " y: " + y, 10, bc.getWindowHeight() - 20);
		} catch (NullPointerException e)
		{
			//Do nothing since the mouse is not over the window
		}
	}
	
	public void drawNotes(Graphics2D g2d)
	{
		ArrayList<String> instructions = new ArrayList<>(Arrays.asList(
//				"Press ESC to close",
//				"I redid the renderer performance has increased by at least 5 times",
//				"Right click to spawn 5000 birbs"
		));
		
		g2d.setFont(new Font("Courier New", Font.BOLD, 20));
		g2d.setColor(Color.WHITE);
		
		for (int i = 0; i < instructions.size(); i++)
		{
			g2d.drawString(instructions.get(i), 10, i * 20 + 20);
		}
	}
	
	public void updateTriangle(BirbsContainer bc)
	{
		int x = (int) (Birb.getBaseWidth() * bc.getScale() / 2);
		int xx = (int) (x / 1.5);
		int xxx = x / 2;
		int[] triangleX = new int[]{xx, -xx, -xxx, -xx};
		int[] triangleY = new int[]{0, xxx, 0, -xxx};
		birbTriangle = new Polygon(triangleX, triangleY, 4);
	}
	
	public void drawBirb(Graphics2D g2d, Birb b)
	{
		g2d.setPaint(b.getBirbColor());
		g2d.rotate(b.getVel().getDirection());
		g2d.drawPolygon(birbTriangle);
	}
}
