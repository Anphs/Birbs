package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Birb extends Component
{
	private static final int baseWidth = 70, baseHeight = 70;
	private static final int WIDTH= 70, HEIGHT = 70;
	private static double scale = .1;
	private final static double maxTurnSpeed = .1, turnNoise = 0;
	private final String ID;
	private Vector vel, acc;
	private Point2D.Double point, formationPoint;
	private static double offsetX = 0, offsetY = 0;
	private Color birbColor;
	private static boolean hitboxVisible;
	private double speedMultiplier = 1;
	
	private static final int d = WIDTH / 2;
	private static final int dd = (int) (d / 1.5);
	private static final int ddd = d / 2;
	private static final int[] triangleX = new int[]{dd, -dd, -ddd, -dd};
	private static final int[] triangleY = new int[]{0, ddd, 0, -ddd};
	private static Polygon birbTriangle = new Polygon(triangleX, triangleY, 4);
	
	public Birb(String ID, Point2D.Double point)
	{
		this.ID = ID;
		this.point = point;
		update();
		
		double velMag = Math.random() * 2 + 4;
		velMag = 20;
		vel = new Vector(velMag, Math.random() * 2 * Math.PI);
//		vel = new Vector(0, 3 * Math.PI / 2);
		birbColor = new Color(254, 105, 3, /*50 + 30 * (int) velMag*/ 0);
	}
	
	public void update()
	{
		this.setSize(getScaledWidth(), getScaledHeight());
		this.updateLocationCentered();
	}
	
	public void updateLocationCentered()
	{
		double x = point.x;
		double y = point.y;
		double cX = (x - (this.getWidth() / 2.0));
		double cY = (y - (this.getHeight() / 2.0));
		cX += offsetX;
		cY += offsetY;
		cX *= scale;
		cY *= scale;
		cX = Math.round(cX);
		cY = Math.round(cY);
		this.setLocation((int) cX, (int) cY);
	}
	
	public static void updateTriangle()
	{
		int x = getScaledWidth() / 2;
		int xx = (int) (x / 1.5);
		int xxx = x / 2;
		int[] triangleX = new int[]{xx, -xx, -xxx, -xx};
		int[] triangleY = new int[]{0, xxx, 0, -xxx};
		birbTriangle = new Polygon(triangleX, triangleY, 4);
	}
	
	public void paint(Graphics g)
	{
		this.setSize(getScaledWidth(), getScaledHeight());
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		
		//Create original and center stroke
		AffineTransform original = g2d.getTransform();
		g2d.translate(getScaledWidth() / 2, getScaledHeight() / 2);
		AffineTransform center = g2d.getTransform();
		
		//Hitbox of Birb
		if (hitboxVisible)
		{
			g2d.setTransform(original);
			g2d.setPaint(new Color(255, 255, 255, 150));
			g2d.drawRect(0, 0, getScaledWidth() - 1, getScaledHeight() - 1);
		}
//		g2d.setTransform(original);
//		g2d.fillRect(0, 0, 1000, 1000);
		
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
		g2d.setStroke(new BasicStroke((float) (5 * scale)));
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
		return point;
	}
	
	public void setWorldPoint(Point2D.Double p)
	{
		this.point = p;
		this.update();
	}
	
	public double getAvoidRadius()
	{
		return (int)vel.getMagnitude()*15+150;
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
	
	public double getSpeedMultiplier()
	{
		return speedMultiplier;
	}
	
	public void setSpeedMultiplier(double speedMultiplier)
	{
		this.speedMultiplier = speedMultiplier;
	}
	
	public static int getScaledWidth()
	{
		return (int) (baseWidth * scale);
	}
	
	public static int getScaledHeight()
	{
		return (int) (baseHeight * scale);
	}
	
	public static void setScale(double scale)
	{
		Birb.scale = scale;
		updateTriangle();
	}
	
	public static void setOffsetX(double offsetX)
	{
		Birb.offsetX = offsetX;
	}
	
	public static void setOffsetY(double offsetY)
	{
		Birb.offsetY = offsetY;
	}
	
	public Point2D.Double getFormationPoint()
	{
		return formationPoint;
	}
	
	public void setFormationPoint(Point2D.Double seekPoint)
	{
		this.formationPoint = seekPoint;
	}
}
