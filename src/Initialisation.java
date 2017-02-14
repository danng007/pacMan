import javax.swing.JFrame;

public class Initialisation {
	JFrame frame;
	private PacmanBoard gameBackground;
	private GameKeyListener listener;
	static final int ORIGINAL_X = 224;
	static final int ORIGINAL_Y = 288;
	static final int SCALING_FACTOR = 3;
	final int WIDTH = (ORIGINAL_X * SCALING_FACTOR) + 75,
			HEIGHT = 20 * 35 + 15;

	public Initialisation(JFrame frame) {
		this.frame = frame;
		createKeyListener();
		gameBackground = new PacmanBoard(listener);
		createWindow();
		gameBackground.tickFrame();
	}

	/**
	 * @author Matt Addicott : Sets the attributes of the frame and specifies a
	 *         panel which will go within it. Attaches the key listener to the
	 *         frame.
	 */
	private void createWindow() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(WIDTH + 16, HEIGHT + 16);
		frame.getContentPane().add(gameBackground);
		frame.addKeyListener(listener);
	}

	/**
	 * @author Matt Addicott : Creates the key listener which will record player
	 *         input.
	 */
	private void createKeyListener() {
		GameKeyListener listener = new GameKeyListener();
		this.listener = listener;
	}

}
