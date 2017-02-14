
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class PacmanFrame extends JFrame{
	//Pacman dimensions (original) = 224 * 288
	static final int ORIGINAL_X = 224;
	static final int ORIGINAL_Y = 288;
	static final int SCALING_FACTOR = 2;
	static final int WIDTH = ORIGINAL_X * SCALING_FACTOR+200;
	static final int HEIGHT =  ORIGINAL_Y * SCALING_FACTOR;
	private static JFrame frame = new JFrame("Pacman");
	
	
	public static void main(String[] args) {

		  BufferedImage image = null;
	        try {
	            image = ImageIO.read(
	                frame.getClass().getResource("/res/sprites/pacmanIcon.png"));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        frame.setIconImage(image);
		new Initialisation(frame);
				
	}

}
