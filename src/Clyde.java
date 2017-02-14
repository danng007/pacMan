import java.awt.Image;
import java.awt.image.BufferedImage;

public class Clyde extends Ghost {
	private Image image;
private static final int OSCILLATE_LIMIT=20;

	BufferedImage[] directionSprites = new BufferedImage[8];
	private GhostSpriteGenerator spriteGenerator = new GhostSpriteGenerator();
	boolean oscillate = false;
	private int counter=10;

	public Clyde(int x, int y, int blocksize) {
		super(x, y, blocksize);
		
		directionSprites = spriteGenerator.getImage("CLYDE");
	}

	public Image getImage() {
if (isFrightened() &&!isKilled()) {
			
			return getFrightenedImage();
			
		}if (isKilled()) {
			return diedImage();
		}
		else {
			switch (getDirection()) {
			case RIGHT:
				if(oscillate){
					image = directionSprites[6];
					}else{
						image = directionSprites[7];
					}
				break;
			case DOWN:
				if(oscillate){
					image = directionSprites[2];
					}else{
						image = directionSprites[3];
					}
				break;
			case UP:
				if(oscillate){
					image = directionSprites[0];
					}else{
						image = directionSprites[1];
					}
				break;
			default:
				if(oscillate){
				image = directionSprites[4];
				}else{
					image = directionSprites[5];
				}
				break;
			}
		}
		counter--;
		if(counter == 0){oscillate=!oscillate;
		counter = OSCILLATE_LIMIT;
		}
		
		return image;
	}

	/**
	 * @author Liam Fraser
	 * @param pacX
	 *            , pacY
	 *            <p>
	 *            Calculates Clyde's target block.
	 * 
	 *            Takes in pacman's position. If the ghost is within 8 blocks of
	 *            this position, then it enters it's scatter behaviour. Else it
	 *            shares the same targeting mechanism as Blinky
	 *            </p>
	 */
	public void setTarget(int pacX, int pacY) {

		if (inHouse() && !isKilled()) {
			super.setTarget(13, 11);
			return;
		}
		super.setTarget(pacX, pacY);
		if (!isKilled()) {

			if (calculateDistance() <= 8) {
				scatter();
			}
		}

	}
	/**
	 * @author Liam Fraser
	 *            <p>
	 *           	Sets Clydes Scatter position to be the target.
	 *            </p>
	 */
	public void scatter() {
		super.setTarget(0, 33);
	}
	/**
	 * @author Liam Fraser 
	 * <p>Resetss the ghost's variables back to their initial state at the start of the game</p>
	 *@param xTilePosition,yTilePosition,Blocksize
	 */
	public void reinitialise(int x, int y, int blocksize){
		oscillate = false;
		counter=10;
		super.reinitialise(x,y,blocksize);
	}
}
