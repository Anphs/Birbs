package me.anthuony.birbs;

public abstract class AbstractBirbsManager
{
	public abstract long update(me.anthuony.birbs.BirbsContainer bc);
	
	public abstract long render(me.anthuony.birbs.BirbsContainer bc, Renderer r);
}
