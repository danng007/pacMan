import java.awt.Rectangle;

public class Sprites {

	protected int spriteGameSpaceX;
	protected int spriteGameSpaceY;
	protected int spriteScreenX;
	protected int spriteScreenY;
	protected int bottomCornerOffset;
	protected char[][] GameSpace;
	protected static final char WALL = '#';
	protected static final char DOOR = '=';
	protected static final char VISITED_TILE = ' ';
	protected static final char PELLET = 'o';
	protected static final char ENERGIZER_PELLET = 'O';
	protected static final char FRUIT = 'F';
	protected int blocksize;

	/**
	 * @author Liam Fraser The constructor for Sprites. Used in addition to the
	 *         child constructors
	 *
	 * @param x
	 *            co-ordinate of the sprite in terms of blocks
	 * @param y
	 *            y co-ordinate of the sprite in terms of blocks
	 * @param blocksize
	 *            the size of the block in pixels, used to determine array
	 *            positioning
	 *
	 */
	public Sprites(int x, int y, int blocksize) {
		this.blocksize = blocksize;
		bottomCornerOffset = blocksize - 1;
		spriteGameSpaceX = x;
		spriteGameSpaceY = y;
		spriteScreenX = toScreenPosition(spriteGameSpaceX);
		spriteScreenY = toScreenPosition(spriteGameSpaceY);
	}
	/**
	 * @author Liam Fraser 
	 * <p>Set the sprite's X position</p>
	 *@param xTilePosition
	 */
	public void setX(int x) {
		spriteGameSpaceX = x;
		spriteScreenX = toScreenPosition(x);
	}
	/**
	 * @author Liam Fraser 
	 * <p>Set the sprite's Y position</p>
	 *@param yTilePosition
	 */ 
	public void setY(int y) {
		spriteGameSpaceY = y;
		spriteScreenY = toScreenPosition(y);
	}

	/**
	 * @author Liam Fraser Method to read the array. Used to store the position
	 *         of walls. Only needs to be called once.
	 *         <p>
	 *         Stores the supplied game space to a variable in this class
	 *         (GameSpace)
	 *         </p>
	 * @param GameSpaceSupplied
	 *
	 */
	public void setMaze(char[][] GameSpaceSupplied) {
		GameSpace = GameSpaceSupplied;
	}

	/**
	 * @author Liam Fraser : Method to convert a screen co-ordinate to a block
	 *         Index
	 * @param screenCoordinate
	 *            : the screen co-ordinate to be converted
	 * @return int : the Array index of the supplied screenCoordinate
	 *
	 */
	protected int toArrayIndex(int screenCoordinate) {
		return screenCoordinate / blocksize;
	}

	/**
	 * @author Liam Fraser : Method to convert a block Index to a screen
	 *         co-ordinate
	 * @param arrayPos
	 *            : The block index to be converted
	 * @return int : the converted screen position
	 *
	 */
	protected int toScreenPosition(int arrayPos) {
		return arrayPos * blocksize;
	}

	/**
	 * @author Liam Fraser: Returns the screenPosition of the sprite (X
	 *         co-ordinate)
	 * @return spriteScreenX, sprites screen position (X)
	 *
	 */
	public int getX() {
		return spriteScreenX;
	}

	/**
	 * @author Liam Fraser: Returns the screenPosition of the sprite (Y
	 *         co-ordinate)
	 * @return spriteScreenY, sprites screen position (Y)
	 *
	 */
	public int getY() {
		return spriteScreenY;
	}

	/**
	 * @author Liam Fraser : Method to return the sprite's block position (X)
	 * 
	 * @return spriteGameSpaceX : the block position
	 *
	 */
	protected int getSpriteGameSpaceX() {
		return spriteGameSpaceX;
	}

	/**
	 * @author Liam Fraser : Method to return the sprite's block position(Y)
	 * 
	 * @return spriteGameSpaceY : the block position
	 *
	 */
	protected int getSpriteGameSpaceY() {
		return spriteGameSpaceY;
	}

	public Rectangle getRectangle() {
		return new Rectangle(spriteScreenX + 6, spriteScreenY + 6, 4, 4);
	}
	/**
	 * @author Liam Fraser 
	 * <p>Resets the Sprite class variables to their initial state from the start of the game</p>
	 *@param xTilePosition,yTilePosition,blocksize
	 */
	public void reinitialise(int x, int y, int blocksize) {
		this.blocksize = blocksize;
		bottomCornerOffset = blocksize - 1;
		spriteGameSpaceX = x;
		spriteGameSpaceY = y;
		spriteScreenX = toScreenPosition(spriteGameSpaceX);
		spriteScreenY = toScreenPosition(spriteGameSpaceY);
	}
}
