package me.anthuony.birbs;

public class Renderer
{
	private int pW, pH;
	private int[] p;
	
	public Renderer(BirbsContainer gc)
	{
//		pW = gc.getWidth();
//		pH = gc.getHeight();
//		p = ((DataBufferInt) gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
	}
	
	public void clear()
	{
		for (int i = 0; i < p.length; i++)
		{
			p[i] = 0xffff00ff;
		}
	}
}