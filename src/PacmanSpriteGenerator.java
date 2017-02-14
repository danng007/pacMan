import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class PacmanSpriteGenerator {

	BufferedImage spriteSheet2;
	BufferedImage[] fetchedSprite = new BufferedImage[9];

	private int pacHeight = 13;
	private int pacWidth = 13;
	private int pacFullX = 47;
	private int pacFullY = 7;
	private int pacClosingX = 7;
	private int pacOpeningX = 26;
	private int pacLeftFacingY = 7;
	private int pacRightFacingY = 27;
	private int pacUpFacingY = 48;
	private int pacDownFacingY = 67;

	/**
	 * @author Matt Addicott : Constructor for sprite generator. Loads in sprite
	 *         sheet which all sprite's are retrieved from.
	 */
	public PacmanSpriteGenerator() {
		try {

			spriteSheet2 = ImageIO.read(new File(getClass().getResource(
					"res/sprites/pacmanSprites2.png").toURI()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Matt Addicott : called by outside classes to obtain an array of
	 *         sprites.
	 * 
	 * @return image : sprite's cut from the sprite sheet.
	 */
	public BufferedImage[] getImage() {
		BufferedImage[] image = getSpriteFromSheet();
		return image;
	}

	/**
	 * @author Matt Addicott : cuts a sprite from the specified sprite sheet.
	 * @return fetchedSprite : sprite's which were cut from the sprite sheet,
	 *         describing all possible positions.
	 */
	private BufferedImage[] getSpriteFromSheet() {

		fetchedSprite[0] = spriteSheet2.getSubimage(pacFullX, pacFullY,
				pacWidth, pacHeight);
		fetchedSprite[1] = spriteSheet2.getSubimage(pacClosingX,
				pacLeftFacingY, pacWidth, pacHeight);
		fetchedSprite[2] = spriteSheet2.getSubimage(pacOpeningX,
				pacLeftFacingY, pacWidth, pacHeight);
		fetchedSprite[3] = spriteSheet2.getSubimage(pacClosingX,
				pacRightFacingY, pacWidth, pacHeight);
		fetchedSprite[4] = spriteSheet2.getSubimage(pacOpeningX+1,
				pacRightFacingY, pacWidth, pacHeight);
		fetchedSprite[5] = spriteSheet2.getSubimage(pacClosingX, pacUpFacingY,
				pacWidth, pacHeight);
		fetchedSprite[6] = spriteSheet2.getSubimage(pacOpeningX, pacUpFacingY,
				pacWidth, pacHeight);
		fetchedSprite[7] = spriteSheet2.getSubimage(pacClosingX,
				pacDownFacingY, pacWidth, pacHeight);
		fetchedSprite[8] = spriteSheet2.getSubimage(pacOpeningX,
				pacDownFacingY, pacWidth, pacHeight);
		return fetchedSprite;
	}
}
