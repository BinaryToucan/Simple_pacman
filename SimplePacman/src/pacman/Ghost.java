package pacman;

import java.lang.Error;
import java.awt.*;

import static pacman.Resources.SIZE_;

public class Ghost
{
	static public final int IN=0;
	static public final int OUT=1;
	static public final int BLIND=2;
	static public final int EYE=3;

	static public final int[] steps=	{7, 7, 1, 1};
	static public final int[] frames=	{8, 8, 2, 1};

	static public final int INIT_BLIND_COUNT=600;	// remain blind for ??? frames
	int blindCount;

	final SpeedControl speedControl = new SpeedControl();

	private int iX, iY, iDir, iStatus;
	int iBlink, iBlindCount;

	// random calculation factors
	static public final int DIR_FACTOR=2;
	static public final int POS_FACTOR=10;

	Window applet;
	Graphics graphics;

	// the maze the ghosts knows
	Maze maze;

	// the ghost image
	Image imageGhost; 
	Image imageBlind;
	Image imageEye;

	Ghost(Window a, Graphics g, Maze m, Color color)
	{
		applet=a;
		graphics=g;
		maze=m;

		imageGhost=applet.createImage(18,18);
		Images.drawGhost(imageGhost, 0, color);

		imageBlind=applet.createImage(18,18);
		Images.drawGhost(imageBlind,1, Color.white);

		imageEye=applet.createImage(18,18);
		Images.drawGhost(imageEye,2, Color.lightGray);
	}

	public void start(int initialPosition, int round)
	{
		if (initialPosition>=2)
			initialPosition++;
		iX=(8+initialPosition)*SIZE_; iY=8*SIZE_;
		iDir=3;
		iStatus=IN;

		blindCount=INIT_BLIND_COUNT/((round+1)/2);

		speedControl.start(steps[iStatus], frames[iStatus]);
	}

	public void draw()
	{
		maze.drawDot(iX/SIZE_, iY/SIZE_);
		maze.drawDot(iX/SIZE_+(iX%SIZE_>0?1:0), iY/SIZE_+(iY%SIZE_>0?1:0));

		if (iStatus==BLIND && iBlink==1 && iBlindCount%32 < SIZE_)
			graphics.drawImage(imageGhost, iX-1, iY-1, applet);
		else if (iStatus==OUT || iStatus==IN)
			graphics.drawImage(imageGhost, iX-1, iY-1, applet);
		else if (iStatus==BLIND)
			graphics.drawImage(imageBlind, iX-1, iY-1, applet);
		else 
			graphics.drawImage(imageEye, iX-1, iY-1, applet);
	}  

	public void move(int iPacX, int iPacY, int iPacDir)
	{
		if (iStatus==BLIND)
		{
			iBlindCount--;
			if (iBlindCount<blindCount/3)
				iBlink=1;
			if (iBlindCount==0)
				iStatus=OUT;
			if (iBlindCount%2==1)	// blind moves at 1/2 speed
			return;
		}

		if (! speedControl.isMove() )
			// no move
			return;

		if (iX%SIZE_==0 && iY%SIZE_==0)
			// determine direction
		{
			switch (iStatus)
			{
			case IN:
				iDir=inSelect();
				break;
			case OUT:
				iDir=outSelect(iPacX, iPacY, iPacDir);
				break;
			case BLIND:
				iDir=blindSelect(iPacX, iPacY, iPacDir);
				break;
			case EYE:
				iDir=eyeSelect();
			}
		}

		if (iStatus!=EYE)
		{
			iX+= Resources.iXDirection[iDir];
			iY+= Resources.iYDirection[iDir];
		}
		else
		{	
			iX+=2* Resources.iXDirection[iDir];
			iY+=2* Resources.iYDirection[iDir];
		}

	}
	
	/**
	 * count available directions
	 * @return
	 */
	public int inSelect()
	{
		int iM,i,iRand;
		int iDirTotal=0;

		for (i=0; i<4; i++)
		{
			iM=maze.iMaze[iY/SIZE_ + Resources.iYDirection[i]]
			              [iX/SIZE_ + Resources.iXDirection[i]];
			if (iM!=Maze.WALL && i != Resources.iBack[iDir] )
			{
				iDirTotal++;
			}
		}
		// randomly select a direction
		if (iDirTotal!=0)
		{
			iRand=Utilities.randSelect(iDirTotal);
			if (iRand>=iDirTotal)
				throw new Error("iRand out of range");
			//				exit(2);
			for (i=0; i<4; i++)
			{
				iM=maze.iMaze[iY/SIZE_+ Resources.iYDirection[i]]
				              [iX/SIZE_+ Resources.iXDirection[i]];
				if (iM!=Maze.WALL && i != Resources.iBack[iDir] )
				{
					iRand--;
					if (iRand<0)
						// the right selection
					{
						if (iM== Maze.DOOR)  iStatus=OUT;
						iDir=i;
						break;
					}
				}
			}
		}	
		return(iDir);	
	}

	/**
	 * count available directions
	 * @param iPacX
	 * @param iPacY
	 * @param iPacDir
	 * @return
	 */
	public int outSelect(int iPacX, int iPacY, int iPacDir)
	{
		int iM,i,iRand;
		int iDirTotal=0;
		int[] iDirCount=new int [4];

		for (i=0; i<4; i++)
		{
			iDirCount[i]=0;
			iM=maze.iMaze[iY/SIZE_ + Resources.iYDirection[i]]
			              [iX/SIZE_+ Resources.iXDirection[i]];
			if (iM!=Maze.WALL && i!= Resources.iBack[iDir] && iM!= Maze.DOOR )
				// door is not accessible for OUT
			{
				iDirCount[i]++;
				iDirCount[i]+=iDir==iPacDir?
						DIR_FACTOR:0;
				switch (i)
				{
				case 0:	// right
					iDirCount[i] += iPacX > iX ? POS_FACTOR:0;
					break;
				case 1:	// up
					iDirCount[i]+=iPacY<iY?
							POS_FACTOR:0;
					break;
				case 2:	// left
					iDirCount[i]+=iPacX<iX?
							POS_FACTOR:0;
					break;
				case 3:	// down
					iDirCount[i]+=iPacY>iY?
							POS_FACTOR:0;
					break;
				}
				iDirTotal+=iDirCount[i];
			}
		}	
		// randomly select a direction
		if (iDirTotal!=0)
		{	
			iRand=Utilities.randSelect(iDirTotal);
			if (iRand>=iDirTotal)
				throw new Error("iRand out of range");
			// exit(2);
			for (i=0; i<4; i++)
			{
				iM=maze.iMaze[iY/SIZE_ + Resources.iYDirection[i]]
				              [iX/SIZE_ + Resources.iXDirection[i]];
				if (iM!=Maze.WALL && i!= Resources.iBack[iDir] && iM!= Maze.DOOR )
				{	
					iRand-=iDirCount[i];
					if (iRand<0)
						// the right selection
					{
						iDir=i;	break;
					}
				}
			}	
		}
		else	
			throw new Error("iDirTotal out of range");
		// exit(1);
		return(iDir);
	}

	public void blind()
	{
		if (iStatus==BLIND || iStatus==OUT)
		{
			iStatus=BLIND;
			iBlindCount=blindCount;
			iBlink=0;
			// reverse
			if (iX%SIZE_!=0 || iY%SIZE_!=0)
			{
				iDir= Resources.iBack[iDir];
				// a special condition:
				// when ghost is leaving home, it can not go back
				// while becoming blind
				int iM;
				iM=maze.iMaze[iY/SIZE_+ Resources.iYDirection[iDir]]
				              [iX/SIZE_+ Resources.iXDirection[iDir]];
				if (iM == Maze.DOOR)
					iDir=Resources.iBack[iDir];
			}
		}
	}

	/**
	 * count available directions
	 * @return
	 */
	public int eyeSelect()
	{
		int iM,i,iRand;
		int iDirTotal=0;
		int [] iDirCount= new int [4];

		for (i=0; i<4; i++)
		{
			iDirCount[i]=0;
			iM=maze.iMaze[iY/SIZE_ + Resources.iYDirection[i]]
			              [iX/SIZE_ +Resources.iXDirection[i]];
			if (iM!= Maze.WALL && i!= Resources.iBack[iDir])
			{
				iDirCount[i]++;
				switch (i)
				{
				// door position 10,6
				case 0:	// right
					iDirCount[i]+=160>iX?
							POS_FACTOR:0;
					break;
				case 1:	// up
					iDirCount[i]+=96<iY?
							POS_FACTOR:0;
					break;
				case 2:	// left
					iDirCount[i]+=160<iX?
							POS_FACTOR:0;
					break;
				case 3:	// down
					iDirCount[i]+=96>iY?
							POS_FACTOR:0;
					break;
				}
				iDirTotal+=iDirCount[i];
			}	
		}
		// randomly select a direction
		if (iDirTotal!=0)
		{
			iRand= Utilities.randSelect(iDirTotal);
			if (iRand>=iDirTotal)
				throw new Error("RandSelect out of range");
			//				exit(2);
			for (i=0; i<4; i++)
			{
				iM=maze.iMaze[iY/SIZE_+ Resources.iYDirection[i]]
				              [iX/SIZE_+ Resources.iXDirection[i]];
				if (iM!= Maze.WALL && i!= Resources.iBack[iDir])
				{
					iRand-=iDirCount[i];
					if (iRand<0)
						// the right selection
					{
						if (iM== Maze.DOOR)
							iStatus=IN;
						iDir=i;	break;
					}
				}
			}
		}
		else
			throw new Error("iDirTotal out of range");
		return(iDir);	
	}	

	/**
	 * count available directions
	 * @param iPacX
	 * @param iPacY
	 * @param iPacDir
	 * @return
	 */
	public int blindSelect(int iPacX, int iPacY, int iPacDir)
	{
		int iM,i,iRand;
		int iDirTotal=0;
		int [] iDirCount = new int [4];

		for (i=0; i<4; i++)
		{
			iDirCount[i]=0;
			iM=maze.iMaze[iY/SIZE_ + Resources.iYDirection[i]][iX/SIZE_+ Resources.iXDirection[i]];
			if (iM != Maze.WALL && i != Resources.iBack[iDir] && iM != Maze.DOOR)
				// door is not accessible for OUT
			{
				iDirCount[i]++;
				iDirCount[i]+=iDir==iPacDir?
						DIR_FACTOR:0;
				switch (i)
				{
				case 0:	// right
					iDirCount[i]+=iPacX<iX?
							POS_FACTOR:0;
					break;
				case 1:	// up
					iDirCount[i]+=iPacY>iY?
							POS_FACTOR:0;
					break;
				case 2:	// left
					iDirCount[i]+=iPacX>iX?
							POS_FACTOR:0;
					break;
				case 3:	// down
					iDirCount[i]+=iPacY<iY?
							POS_FACTOR:0;
					break;
				}
				iDirTotal+=iDirCount[i];
			}	
		}
		// randomly select a direction
		if (iDirTotal!=0)
		{
			iRand=Utilities.randSelect(iDirTotal);
			if (iRand>=iDirTotal)
				throw new Error("RandSelect out of range");
			//				exit(2);
			for (i=0; i<4; i++)
			{	
				iM=maze.iMaze[iY/16+ Resources.iYDirection[i]]
				              [iX/16+ Resources.iXDirection[i]];
				if (iM!= Maze.WALL && i!= Resources.iBack[iDir])
				{	
					iRand-=iDirCount[i];
					if (iRand<0)
						// the right selection
					{
						iDir=i;	break;
					}
				}
			}
		}
		else
			throw new Error("iDirTotal out of range");
		return(iDir);
	}

	/**
	 * return 1 if caught the pacman
	 * return 2 if being caught by pacman
	 * @param iPacX
	 * @param iPacY
	 * @return
	 */
	int testCollision(int iPacX, int iPacY)
	{
		if (iX<=iPacX+2 && iX>=iPacX-2
				&& iY<=iPacY+2 && iY>=iPacY-2)
		{
			switch (iStatus)
			{
			case OUT:
				return(1);
			case BLIND:
				iStatus=EYE;
				iX=iX/4*4;
				iY=iY/4*4;
				return(2);
			}	
		}
		// nothing
		return(0);
	}
	
	
}


