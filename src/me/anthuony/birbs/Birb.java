package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Birb extends Component
{
	private final static int WIDTH = 70, HEIGHT = 70;
	private final static double maxTurnSpeed = .1, turnNoise = 0;
	private final String ID;
	private Vector vel, acc;
	private Point2D.Double p;
	private Color birbColor;
	private static boolean hitboxVisible;
	
	private static final int d = WIDTH / 2;
	private static final int dd = (int) (d / 1.5);
	private static final int ddd = d / 2;
	private static final int[] triangleX = new int[]{dd, -dd, -ddd, -dd};
	private static final int[] triangleY = new int[]{0, ddd, 0, -ddd};
	private static final Polygon birbTriangle = new Polygon(triangleX, triangleY, 4);
	
	public Birb(String ID, Point2D.Double p)
	{
		this.ID = ID;
		this.p = p;
		this.setSize(WIDTH, HEIGHT);
		this.updateLocationCentered();
		
		double velMag = Math.random() * 2 + 4;
		vel = new Vector(velMag, Math.random() * 2 * Math.PI);
//		vel = new Vector(0, 3 * Math.PI / 2);
		birbColor = new Color(254, 105, 3, /*50 + 30 * (int) velMag*/ 0);
	}
	
	public void updateLocationCentered()
	{
		double x = p.x;
		double y = p.y;
		int cX = (int) (x - this.getWidth() / 2);
		int cY = (int) (y - this.getHeight() / 2);
		this.setLocation(cX, cY);
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		
		//Create original and center stroke
		AffineTransform original = g2d.getTransform();
		g2d.translate(WIDTH / 2, HEIGHT / 2);
		AffineTransform center = g2d.getTransform();
		
		//Hitbox of Birb
		if (hitboxVisible)
		{
			g2d.setTransform(original);
			g2d.setPaint(new Color(255, 255, 255, 150));
			g2d.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
		}
		
		//Birb itself
		g2d.setTransform(center);
		drawBirb(g2d);
	}
	
	public void drawBirb(Graphics2D g2d)
	{
		g2d.setPaint(birbColor);
		g2d.rotate(getVel().getDirection());
		
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setStroke(new BasicStroke(5));
		g2d.drawPolygon(birbTriangle);
	}
	
	public Vector getVel()
	{
		return vel;
	}
	
	public void setVel(Vector vel)
	{
		this.vel = vel;
	}
	
	public Vector getAcc()
	{
		return acc;
	}
	
	public void setAcc(Vector acc)
	{
		this.acc = acc;
	}
	
	public Point2D.Double getPoint()
	{
		return p;
	}
	
	public void setPoint(Point2D.Double p)
	{
		this.p = p;
		this.updateLocationCentered();
	}
	
	public Color getBirbColor()
	{
		return birbColor;
	}
	
	public void setBirbColor(Color birbColor)
	{
		this.birbColor = birbColor;
	}
	
	public static double getMaxTurnSpeed()
	{
		return maxTurnSpeed;
	}
	
	public static double getTurnNoise()
	{
		return turnNoise;
	}
	
	public String getID()
	{
		return ID;
	}
	
	public static void toggleHitboxVisible()
	{
		hitboxVisible = !hitboxVisible;
	}
}
