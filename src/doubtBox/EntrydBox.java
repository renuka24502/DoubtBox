package doubtBox;

/**
*The EntrydBox opens a splash screen by making a constructor call to Splash class
* to implement splash screen and after complete loading of splash screen it opens the
* Login Frame 
*
* @author  Gulshan Singh
* @since   2017-07-30
*/
public class EntrydBox 
{
	 /**
	   * This is the main method which makes with the use of thread and Splash class
	   *  implements the splash screen. After complete loading of splash screen 
	   *  it opens theLogin Frame 
	   * @param args Unused.
	   * @return Nothing.
	   */
	public static void main(String[] args) 
	{
		Splash s = new Splash();
		s.setVisible(true);
		
		Thread t = Thread.currentThread();
		
		try {
			t.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.dispose();
		
		LoginFrame frm = new LoginFrame();
	}
}