package me.anthuony.birbs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Window
{
	private final JFrame frame;
	private final Canvas canvas;
	private final BufferStrategy bs;
	private final Graphics g;
	private final BufferedImage transparentCursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	private final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(transparentCursorImg, new Point(0, 0), "blank cursor");
	
	public Window(me.anthuony.birbs.BirbsContainer bc)
	{
		
		Dimension s = new Dimension(bc.getWindowWidth(), bc.getWindowHeight());
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(s);
		frame.setLocationRelativeTo(null);
		frame.setTitle(bc.getTitle());
		frame.setLayout(new BorderLayout());
		
		canvas = new Canvas();
		canvas.setPreferredSize(s);
		canvas.setBackground(Color.black);
		
		frame.add(canvas, BorderLayout.CENTER);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setVisible(true);
		canvas.requestFocus();
		
		canvas.createBufferStrategy(2);
		bs = canvas.getBufferStrategy();
		g = bs.getDrawGraphics();
	}
	
	public void update()
	{
		bs.show();
	}
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	public Graphics getG()
	{
		return g;
	}
	
	public JFrame getFrame()
	{
		return frame;
	}
	
	public Cursor getBlankCursor()
	{
		return blankCursor;
	}
}
