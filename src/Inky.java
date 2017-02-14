import java.awt.Image;
import java.awt.image.BufferedImage;

public class Inky extends Ghost {
	private Image image;
	BufferedImage[] directionSprites = new BufferedImage[8];
	private GhostSpriteGenerator spriteGenerator = new GhostSpriteGenerator();
	boolean oscillate = false;
	private int counter = 10;
	private static final int OSCILLATE_LIMIT=20;
	public Inky(int x, int y, int blocksize) {
		super(x, y, blocksize);
		directionSprites = spriteGenerator.getImage("INKY");
	}

	/**
	 * @author Liam Fraser
	 * @param pacX
	 *            ,pacY, pacDirection, blinkyX
	 *            <p>
	 *            Calculates Inky's target block.
	 * 
	 *            It takes in pacman's position and direction, and blinky's
	 *            position. Method then calculates a vector from blinky's
	 *            position to an offset of pacman's position (determined by his
	 *            direction). This vector is then doubled and The superclass is
	 *            then called with this position as inky's target
	 *            </p>
	 */
	public void setTarget(int pacX, int pacY, int pacDirection, int blinkyX,
			int blinkyY) {
		switch (pacDirection) {
		case UP:
			// Original had glitch where X co-ordinate was affected too.
			// Reflected here with pacX.
			pacX -= 2;
			pacY -= 2;
		case DOWN:
			pacY += 2;
			break;
		case LEFT:
			pacX -= 2;
			break;
		case RIGHT:
			pacX += 2;
			break;
		default:
			setTarget(pacX - 2, pacY);
		}

		int targetX = pacX;
		int targetY = pacY;

		if (blinkyX > pacX) {
			targetX = pacX - (blinkyX - pacX);
		} else if (blinkyX < pacX) {
			targetX = pacX + (pacX - blinkyX);
		}
		if (blinkyY > pacY) {
			targetY = pacY - (blinkyY - pacY);
		} else if (blinkyY < pacY) {
			targetY = pacY + (pacY - blinkyY);
		}

		setTarget(targetX, targetY);
	}

	/**
	 * @author Liam Fraser
	 * 
	 *         <p>
	 *         Checks which sprite to display based on inky's direction and
	 *         mode, then returns it.
	 *         </p>
	 * @return Image
	 */
	public Image getImage() {
		if (isFrightened() && !isKilled()) {

			return getFrightenedImage();

		}
		if (isKilled()) {
			return diedImage();
		} else {
			switch (getDirection()) {
			case RIGHT:
				if (oscillate) {
					image = directionSprites[6];
				} else {
					image = directionSprites[7];
				}
				break;
			case DOWN:
				if (oscillate) {
					image = directionSprites[2];
				} else {
					image = directionSprites[3];
				}
				break;
			case UP:
				if (oscillate) {
					image = directionSprites[0];
				} else {
					image = directionSprites[1];
				}
				break;
			default:
				if (oscillate) {
					image = directionSprites[4];
				} else {
					image = directionSprites[5];
				}
				break;
			}
		}
		counter--;
		if (counter == 0) {
			oscillate = !oscillate;
			counter = OSCILLATE_LIMIT;
		}

		return image;
	}
	/**
	 * @author Liam Fraser 
	 * <p>Resets the ghost's variables back to their initial state at the start of the game</p>
	 *@param xTilePosition,yTilePosition,Blocksize
	 */
	public void reinitialise(int x, int y, int blocksize){
		oscillate = false;
		counter=OSCILLATE_LIMIT;
		super.reinitialise(x,y,blocksize);
	}
}
