package me.anthuony.birbs;

public class ExtraMath
{
	public static int boundNumber(int num, int lower, int upper)
	{
		return Math.min(Math.max(num, lower), upper);
	}
	
	public static float boundNumber(float num, float lower, float upper, float step)
	{
		if(upper - lower < step)
		{
			System.out.println("Step size too big");
		}
		if(upper < lower)
		{
			System.out.println("Upper is smaller than lower bound");
		}
		while(num < lower)
		{
			num += step;
		}
		while(num > upper)
		{
			num -= step;
		}
		return num;
	}
}
