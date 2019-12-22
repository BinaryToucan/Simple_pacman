
package pacman;

public class SpeedControl
{
	// move steps per frames
	int steps;
	int frames;

	int frameCount;
	int stepCount;

	float frameStepRatio;

	SpeedControl()
	{
		start(1,1);
	}

	public void start(int s, int f)
	throws Error
	{
		if (f<s)
			throw new Error("Cspeed.init(...): frame must >= step");

		steps=s;
		frames=f;
		frameStepRatio=(float)frames/(float)steps;

		stepCount=steps;
		frameCount=frames;
	}

	// return 1 if move, 0 not move
	public boolean isMove()	
	{
		frameCount--;

		float ratio=(float)frameCount/(float)stepCount;

		if (frameCount==0)
			frameCount=frames;

		if (ratio < frameStepRatio)
		{
			stepCount--;
			if (stepCount==0)
				stepCount=steps;
			return true;
		}
		
		return false;
	}
	
	
}
