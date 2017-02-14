import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class ConsumableSpriteGenerator {

	int level = 0;
	BufferedImage spriteSheet;

	private int pelletHeight = 8;
	private int pelletWidth = 8;
	private int pelletX = 6;
	private int pelletY = 186;

	private int fruitHeight = 14;
	private int fruitWidth = 14;
	private int cherryX = 176;
	private int cherryY = 169;

	private int strawberryX = 176;
	private int strawberryY = 189;

	private int peachX = 176;
	private int peachY = 208;

	private int appleX = 176;
	private int appleY = 228;

	private int grapesX = 216;
	private int grapesY = 167;

	private int galaxianX = 216;
	private int galaxianY = 189;

	private int bellX = 216;
	private int bellY = 207;

	private int keyX = 216;
	private int keyY = 228;

	/**
	 * @author Matt Addicott : Constructor for sprite generator. Loads in sprite
	 *         sheet which all sprite's are retrieved from.
	 */
	public ConsumableSpriteGenerator() {
		try {
			spriteSheet = ImageIO.read(new File(getClass().getResource(
					"res/sprites/pacmansprites2.png").toURI()));

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Matt Addicott : called by outside classes to obtain a sprite.
	 *         Performs scaling when the same sprite is used in different areas.
	 * 
	 * @param sprite
	 *            : String specifying which sprite is being requested.
	 * @param level
	 *            : Informs the generator what level the game is on which is
	 *            required to decide which fruit to return.
	 * @return image : sprite cut from the sprite sheet and scaled.
	 */
	public Image getImage(String sprite, int level) {
		this.level = level;
		Image image = getSpriteFromSheet(sprite);
		if (sprite.equals("ENERGIZER")) {
			image = image.getScaledInstance(12, 12, Image.SCALE_SMOOTH);
		} else if (sprite.equals("PELLET")) {
			image = image.getScaledInstance(5, 5, Image.SCALE_SMOOTH);
		}
		return image;
	}

	/**
	 * @author Matt Addicott : cuts a sprite from the specified sprite sheet.
	 * @param sprite
	 *            : String specifying which sprite is being requested.
	 * @return fetchedSprite : sprite which was cut from the sprite sheet.
	 */
	private Image getSpriteFromSheet(String sprite) {
		Image fetchedSprite = null;
		switch (sprite) {

		case "PELLET":
			fetchedSprite = spriteSheet.getSubimage(pelletX, pelletY,
					pelletWidth, pelletHeight);
			break;
		case "ENERGIZER":
			fetchedSprite = spriteSheet.getSubimage(pelletX, pelletY,
					pelletWidth, pelletHeight);
			break;
		case "FRUIT":
			switch (level) {
			case 1:
				fetchedSprite = spriteSheet.getSubimage(cherryX, cherryY,
						fruitWidth, fruitHeight);
				break;
			case 2:
				fetchedSprite = spriteSheet.getSubimage(strawberryX,
						strawberryY, fruitWidth, fruitHeight);
				break;
			case 3:
				/*
				 * Peach Intentional drop
				 */
			case 4:
				fetchedSprite = spriteSheet.getSubimage(peachX, peachY,
						fruitWidth, fruitHeight);
				break;
			case 5:
				/*
				 * Apple Intentional drop
				 */
			case 6:
				fetchedSprite = spriteSheet.getSubimage(appleX, appleY,
						fruitWidth, fruitHeight);
				break;
			case 7:
				/*
				 * Grapes Intentional drop
				 */
			case 8:
				fetchedSprite = spriteSheet.getSubimage(grapesX, grapesY,
						fruitWidth, fruitHeight);
				break;
			case 9:
				/*
				 * Galaxian Intentional drop
				 */
			case 10:
				fetchedSprite = spriteSheet.getSubimage(galaxianX, galaxianY,
						fruitWidth, fruitHeight);
				break;
			case 11:
				/*
				 * Bell Intentional drop
				 */
			case 12:
				fetchedSprite = spriteSheet.getSubimage(bellX, bellY,
						fruitWidth, fruitHeight);
				break;
			default:
				fetchedSprite = spriteSheet.getSubimage(keyX, keyY, fruitWidth,
						fruitHeight);
				break;
			}
		}
		return fetchedSprite;
	}
}
