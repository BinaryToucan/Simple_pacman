
package wrapper;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import pacman.MainFrame;

/**
 * a java applet class for pacman
 */
public class PacmanApplet extends Applet
implements ActionListener
{
	//private static final long serialVersionUID = -749993332452315528L;

	private static MainFrame pacMan=null;

	public void init()
	{
		setSize(50,50);
		// create button
		setLayout(new FlowLayout(FlowLayout.CENTER));
		Button play=new Button("PLAY");
		add(play);

		play.addActionListener(this);

		//      newG5ame();
	}

	void newGame()
	{
		pacMan=new MainFrame();
	}

	/////////////////////////////////////////////////
	// handles button event
	/////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e)
	{
		if ( pacMan != null && ! pacMan.isFinalized() )
			// another is running
			return;
		newGame();
	}

}
