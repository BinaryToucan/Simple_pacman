
package pacman;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;

import static pacman.Resources.SIZE_;

public class Pacman
{
	// frames to wait after eaten a dot
	static public final int DOT_WAIT=4;

	int iDotWait;

	// current position
	int iX, iY;
	// current direction
	int iDir;

	// the applet this object is associated to
	Window applet;
	Graphics graphics;

	// the pac image
	Image [][] pacmanImages;

	// the knowledge of the maze
	private Maze maze;

	// the knowledge of the power dots
	PowerDot powerDot;

	Pacman(Window a, Graphics g, Maze m, PowerDot p)    {
		applet=a;
		graphics=g;
		maze=m;
		powerDot=p;


		// initialize pac and pac image
		pacmanImages=new Image[4][4];
		for (int i=0; i<4; i++)
			for (int j=0; j<4; j++)
			{
				pacmanImages[i][j]=applet.createImage(18,18); //18 18
				Images.drawPac(pacmanImages[i][j],i,j);
			}	
	}

	public void start()
	{
		iX = 10 * SIZE_;
		iY = 10 * SIZE_;
		iDir = 1;		// downward, illegal and won't move
		iDotWait = 0;
	}

	public void draw()
	{
		maze.drawDot(iX/SIZE_, iY/SIZE_);
		maze.drawDot(iX/SIZE_+(iX%SIZE_>0?1:0), iY/SIZE_+(iY % SIZE_ >0 ? 1 : 0));

		int iImageStep=(iX % SIZE_ + iY % SIZE_)/2; 	// determine shape of PAc
		if (iImageStep<4)
			iImageStep=3-iImageStep;
		else
			iImageStep-=4;
		graphics.drawImage(pacmanImages[iDir][iImageStep], iX-1, iY-1, applet);
	}	

	// return 1 if eat a dot
	// return 2 if eat power dot
	public int move(int iNextDir)
	{
		int eaten=0;

		if (iNextDir!=-1 && iNextDir!=iDir)	// not set or same
			// change direction
		{
			if (iX % SIZE_!=0 || iY % SIZE_!=0)
			{
				// only check go back
				if (iNextDir%2==iDir%2)
					iDir=iNextDir;
			}	
			else    // need to see whether ahead block is OK
			{
				if ( mazeOK(iX/SIZE_+ Resources.iXDirection[iNextDir],
						iY/SIZE_+ Resources.iYDirection[iNextDir]) )
				{
					iDir=iNextDir;
					//iNextDir=-1;
				}
			}
		}
		if (iX%SIZE_ == 0 && iY%SIZE_ == 0)
		{

			// see whether has eaten something
			switch (maze.iMaze[iY/SIZE_][iX/SIZE_])
			{
			case Maze.DOT:
				eaten=1;
				maze.iMaze[iY/SIZE_][iX/SIZE_]=Maze.BLANK;	// eat dot
				maze.iTotalDotcount--;
				iDotWait=DOT_WAIT;
				break;
			case Maze.POWER_DOT:
				eaten=2;
				powerDot.eat(iX/SIZE_, iY/SIZE_);
				maze.iMaze[iY/SIZE_][iX/SIZE_]=Maze.BLANK;
				break;
			}

			if (maze.iMaze[iY/SIZE_ + Resources.iYDirection[iDir]]
			               [iX/SIZE_ + Resources.iXDirection[iDir]]==1)
			{
				return(eaten);	// not valid move
			}
		}
		if (iDotWait==0)
		{
			iX+= Resources.iXDirection[iDir];
			iY+= Resources.iYDirection[iDir];
		}
		else	iDotWait--;
		return(eaten);
	}	

	boolean mazeOK(int iRow, int icol)
	{
		if ( (maze.iMaze[icol][iRow] & ( Maze.WALL | Maze.DOOR)) ==0)
			return(true);
		return(false);
	}
	
	
}









