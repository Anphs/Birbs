package me.anthuony.birbs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Window
{
	private final JFrame frame;
	private Canvas canvas;
	private BufferStrategy bs;
	private Graphics g;
	
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
		frame.requestFocus();
		
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
