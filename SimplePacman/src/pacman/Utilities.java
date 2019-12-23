
package pacman;


/**
 * provide some global public utility functions
 */
public class Utilities
{
	// return a random number within [0..iTotal)
	public static int randSelect(int iTotal)
	{
		double a;
		a=Math.random();
		a=a*iTotal;
		return( (int) a );
	}

}
