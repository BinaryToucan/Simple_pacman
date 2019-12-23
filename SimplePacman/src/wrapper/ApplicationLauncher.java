
package wrapper;

import javax.swing.SwingUtilities;

import pacman.MainFrame;

/**
 * the java application class of pacman 
 */
public class ApplicationLauncher
{
	public static void main (String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new MainFrame();
			}
		});
	}
	
	
}


