
package pacman;

/**
 * the tables are used to speed up computation
 */
public class Resources
{
	public static final int SIZE_ = 16;
	// for direction computation
	public static final int[] iXDirection={1,0,-1,0};
	public static final int[] iYDirection={0,-1,0,1};

	// backward direction
	public static final int[] iBack={2,3,0,1};

	// direction code
	public static final int RIGHT=0;
	public static final int UP=1;
	public static final int LEFT=2;
	public static final int DOWN=3;

	// the maze difinition string
	public static final String[] MazeDefine=
	{
		"XXXXXXXXXXXXXXXXXXXXX",	// 1
		"X.........X.........X",	// 2
		"XOXXX.XXX.X.XXX.XXXOX",	// 3
		"X......X..X.........X",	// 4
		"XXX.XX.X.XXX.XX.X.X.X",	// 5
		"X....X..........X.X.X",	// 6
		"X.XX.X.XXX-XXX.XX.X.X",	// 7
		"X.XX.X.X     X......X",	// 8
		"X.XX...X     X.XXXX.X",	// 9
		"X.XX.X.XXXXXXX.XXXX.X",	// 10
		"X....X.... .........X",	// 11
		"XXX.XX.XXXXXXX.X.X.XX",	// 12
		"X.........X....X....X",	// 13
		"XOXXXXXXX.X.XXXXXXXOX",	// 14
		"X...................X",	// 15
		"XXXXXXXXXXXXXXXXXXXXX",	// 16
	};


}

