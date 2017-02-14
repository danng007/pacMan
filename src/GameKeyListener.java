import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class GameKeyListener implements KeyListener{

	private KeyEvent event;
	private boolean eventOccurred = false;
	
	/**
	 * @author Matt Addicott : called when a key on the keyboard is pressed. Logs this input and sets the flag to indicate a waiting input.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		event = e;
		eventOccurred = true;
	}

	/**
	 * @author Matt Addicott : called when a key on the keyboard is released. Logs this input and sets the flag to indicate a waiting input.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		event = e;
		eventOccurred = true;	
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * @author Matt Addicott : returns the last key press logged and resets the flag.
	 * @return event : KeyEvent registered from the keyboard.
	 */
	public KeyEvent getEvent(){
		eventOccurred = false;
		return event;
	}
	
	/**
	 * @author Matt Addicott : returns the flag which indicates whether an input
	 * 			is waiting to be passed on.
	 * @return eventOccurred : Boolean flag indicating a waiting input.
	 */
	public boolean newInputReady(){
		return eventOccurred;
	}
}
