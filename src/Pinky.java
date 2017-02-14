import java.awt.Image;
import java.awt.image.BufferedImage;

public class Pinky extends Ghost {
	private Image image;
	BufferedImage[] directionSprites = new BufferedImage[8];
	private GhostSpriteGenerator spriteGenerator = new GhostSpriteGenerator();
	boolean oscillate = false;
	private int counter=10;
	private static final int OSCILLATE_LIMIT=20;

	public Pinky(int x, int y, int blocksize) {
		super(x, y, blocksize);
		directionSprites = spriteGenerator.getImage("PINKY");
		
	}
	/**
	 * @author Liam Fraser/Wantao Tang
	 *            <p>
	 *            Calculates the correct sprite to display, then returns it.
	 *            </p>
	 *            @return Image
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
	 * @param pacX
	 *            ,pacY, pacDirection
	 *            <p>
	 *            Calculates Pinky's target block.
	 * 
	 *            It takes in pacman's position and direction. Method then sets
	 *            pinky's target to be 4 blocks away from Pacman's current
	 *            location. In the original arcade game a bug existed where a
	 *            space 4 blocks to the left and up of where pacman was was
	 *            targeted,which I have replicated here.
	 *            </p>
	 */
	public void setTarget(int pacX, int pacY, int pacDirection) {

		switch (pacDirection) {
		case UP:
			// Bug in the original arcade game meant that pinky's target was
			// also 4 blocks to the left when pacman was facing up
			setTarget(pacX - 4, pacY - 4);
			break;
		case DOWN:
			setTarget(pacX, pacY + 4);
			break;
		case LEFT:
			setTarget(pacX - 4, pacY);
			break;
		case RIGHT:
			setTarget(pacX + 4, pacY);
			break;
		default:
			setTarget(pacX - 4, pacY);
		}
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
