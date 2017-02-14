import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class GhostSpriteGenerator {

	BufferedImage spriteSheet;
	BufferedImage spriteSheet2;

	private int ghostHeight = 14;
	private int ghostWidth = 14;
	private int ghostSpriteStartX = 7;
	private int blinkyY = 87;
	private int pinkyY = 107;
	private int inkyY = 127;
	private int clydeY = 147;

	public GhostSpriteGenerator() {
		try {

			spriteSheet2 = ImageIO.read(new File(getClass().getResource(
					"res/sprites/pacmanSprites2.png").toURI()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @author Liam Fraser 
	 * <p>returns an array of all the sprites needed for the ghost in normal mode.</p>
	 *@param String ghost (the name of the ghost to be consumed, in capitals)
	 *@returns BufferedImage[] of ghost sprites
	 */
	public BufferedImage[] getImage(String ghost) {

		BufferedImage[] image = getSpriteFromSheet(ghost);

		return image;
	}
	/**
	 * @author Liam Fraser 
	 * <p>Gets all sprites for the supplied ghost and assigns them to an array</p>
	 *@param String ghost (the name of the ghost to be consumed, in capitals)
	 *@returns BufferedImage[] of ghost sprites
	 */
	private BufferedImage[] getSpriteFromSheet(String sprite) {
		BufferedImage[] fetchedSprites = new BufferedImage[8];
		switch (sprite) {
		case "BLINKY":
			fetchedSprites[0] = spriteSheet2.getSubimage(ghostSpriteStartX,
					blinkyY, ghostWidth, ghostHeight);
			fetchedSprites[1] = spriteSheet2.getSubimage(ghostSpriteStartX + 20,
					blinkyY, ghostWidth, ghostHeight);
			fetchedSprites[2] = spriteSheet2.getSubimage(ghostSpriteStartX + 40,
					blinkyY, ghostWidth, ghostHeight);
			fetchedSprites[3] = spriteSheet2.getSubimage(ghostSpriteStartX + 60,
					blinkyY, ghostWidth, ghostHeight);
			fetchedSprites[4] = spriteSheet2.getSubimage(ghostSpriteStartX + 80,
					blinkyY, ghostWidth, ghostHeight);
			fetchedSprites[5] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 100, blinkyY, ghostWidth, ghostHeight);
			fetchedSprites[6] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 120, blinkyY, ghostWidth, ghostHeight);
			fetchedSprites[7] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 140, blinkyY, ghostWidth, ghostHeight);
			break;
		case "PINKY":
			fetchedSprites[0] = spriteSheet2.getSubimage(ghostSpriteStartX,
					pinkyY, ghostWidth, ghostHeight);
			fetchedSprites[1] = spriteSheet2.getSubimage(ghostSpriteStartX + 20,
					pinkyY, ghostWidth, ghostHeight);
			fetchedSprites[2] = spriteSheet2.getSubimage(ghostSpriteStartX + 40,
					pinkyY, ghostWidth, ghostHeight);
			fetchedSprites[3] = spriteSheet2.getSubimage(ghostSpriteStartX + 60,
					pinkyY, ghostWidth, ghostHeight);
			fetchedSprites[4] = spriteSheet2.getSubimage(ghostSpriteStartX + 80,
					pinkyY, ghostWidth, ghostHeight);
			fetchedSprites[5] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 100, pinkyY, ghostWidth, ghostHeight);
			fetchedSprites[6] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 120, pinkyY, ghostWidth, ghostHeight);
			fetchedSprites[7] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 140, pinkyY, ghostWidth, ghostHeight);
			break;
		case "INKY":
			fetchedSprites[0] = spriteSheet2.getSubimage(ghostSpriteStartX,
					inkyY, ghostWidth, ghostHeight);
			fetchedSprites[1] = spriteSheet2.getSubimage(ghostSpriteStartX + 20,
					inkyY, ghostWidth, ghostHeight);
			fetchedSprites[2] = spriteSheet2.getSubimage(ghostSpriteStartX + 40,
					inkyY, ghostWidth, ghostHeight);
			fetchedSprites[3] = spriteSheet2.getSubimage(ghostSpriteStartX + 60,
					inkyY, ghostWidth, ghostHeight);
			fetchedSprites[4] = spriteSheet2.getSubimage(ghostSpriteStartX + 80,
					inkyY, ghostWidth, ghostHeight);
			fetchedSprites[5] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 100, inkyY, ghostWidth, ghostHeight);
			fetchedSprites[6] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 120, inkyY, ghostWidth, ghostHeight);
			fetchedSprites[7] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 140, inkyY, ghostWidth, ghostHeight);
			break;
		case "CLYDE":
			fetchedSprites[0] = spriteSheet2.getSubimage(ghostSpriteStartX,
					clydeY, ghostWidth, ghostHeight);
			fetchedSprites[1] = spriteSheet2.getSubimage(ghostSpriteStartX + 20,
					clydeY, ghostWidth, ghostHeight);
			fetchedSprites[2] = spriteSheet2.getSubimage(ghostSpriteStartX + 40,
					clydeY, ghostWidth, ghostHeight);
			fetchedSprites[3] = spriteSheet2.getSubimage(ghostSpriteStartX + 60,
					clydeY, ghostWidth, ghostHeight);
			fetchedSprites[4] = spriteSheet2.getSubimage(ghostSpriteStartX + 80,
					clydeY, ghostWidth, ghostHeight);
			fetchedSprites[5] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 100, clydeY, ghostWidth, ghostHeight);
			fetchedSprites[6] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 120, clydeY, ghostWidth, ghostHeight);
			fetchedSprites[7] = spriteSheet2.getSubimage(
					ghostSpriteStartX + 140, clydeY, ghostWidth, ghostHeight);
		}
		return fetchedSprites;
	}
}
