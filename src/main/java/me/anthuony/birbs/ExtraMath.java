package me.anthuony.birbs;

public class ExtraMath
{
	public static int boundNumber(int num, int lower, int upper)
	{
		return Math.min(Math.max(num, lower), upper);
	}
}
