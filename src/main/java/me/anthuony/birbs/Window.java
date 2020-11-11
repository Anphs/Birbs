package me.anthuony.birbs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class Window
{
	private final JFrame frame;
	private final Canvas canvas;
	private final BufferStrategy bs;
	private final Graphics g;
	
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
}
