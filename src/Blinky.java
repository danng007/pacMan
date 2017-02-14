import java.awt.Image;
import java.awt.image.BufferedImage;

public class Blinky extends Ghost {
	private Image image;
	private static final int OSCILLATE_LIMIT=20;

	BufferedImage[] directionSprites = new BufferedImage[8];
	private GhostSpriteGenerator spriteGenerator = new GhostSpriteGenerator();
	
	boolean oscillate = false;
	private int counter=10;

	public Blinky(int x, int y, int blocksize) {
		super(x, y, blocksize);

		directionSprites = spriteGenerator.getImage("BLINKY");
		
	}
	/**
	 * @author Liam Fraser 
	 * <p>checks what mode the ghost is in and what direction it is facing, and returns the correct image</p>
	 *@return Image
	 */
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
	 * <p>Resets the ghost's variables back to their initial state at the start of the game</p>
	 *@param xTilePosition,yTilePosition,Blocksize
	 */
	public void reinitialise(int x, int y, int blocksize){
		oscillate = false;
		counter=10;
		super.reinitialise(x,y,blocksize);
	}
}
