package me.anthuony.birbs;

public abstract class AbstractBirbsManager
{
	public abstract void update(me.anthuony.birbs.BirbsContainer bc);
	
	public abstract void render(me.anthuony.birbs.BirbsContainer bc, Renderer r);
}
