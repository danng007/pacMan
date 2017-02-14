import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Pacman extends MoveableSprites {

	private int spriteGameSpaceX;
	private int spriteGameSpaceY;
	private static final int BONUS_X = 13;
	private static final int BONUS_Y = 17;

	// Sounds
	private Sound.BonusSound bonus = new Sound().new BonusSound();
	private Sound.PacmanSound pelletSoundA = new Sound().new PacmanSound();
	private Sound.PelletSoundB pelletSoundB = new Sound().new PelletSoundB();
	private boolean playPelletA = true;

	// directions
	private int direction;
	private static final int LEFT = -1;
	private static final int RIGHT = 1;
	private static final int UP = 2;
	private static final int DOWN = -2;

	private int lifeCount = 3;
	private int pelletsTotal = 0;
	private boolean pelletEaten;
	private boolean energiserPelletEaten;

	// image variables
	BufferedImage[] pacSprites = new BufferedImage[9];
	PacmanSpriteGenerator spriteGenerator = new PacmanSpriteGenerator();

	// animation variables
	private boolean incrementingCounter = true;
	int spriteToDraw = 1;
	private boolean isFull = true;

	// game state variables
	private boolean onTitleScreen = true;
	private boolean gameStart = false;
	private boolean gameEnd = false;
	private boolean end = false;
	private boolean onHighScoreScreen = false;

	// bonus item variables
	double randomTime;
	long randomFruitLength;
	long fruitSpawnTime, fruitEndTime;
	boolean fruitSpawned = false;
	private boolean consumableEaten = false;
	private boolean randomMaze = false;
	private boolean fixedMaze = false;

	/**
	 * @author Liam Fraser, Matt Addicott : Constructor for Pacman
	 *         <p>
	 *         extends the super constructor to include the sprites for each
	 *         direction pacman can face
	 *         </p>
	 * @param x
	 *            : The x position of Pacman (in blocks)
	 * @param y
	 *            : the y position of pacman (in blocks)
	 * @param blocksize
	 *            : the size of a block (in pixels)
	 *
	 */
	public Pacman(int x, int y, int blocksize) {
		super(x, y, blocksize);
		direction = LEFT;
		pacSprites = spriteGenerator.getImage();
		pelletSoundA.loadSound();
		pelletSoundB.loadSound();
		bonus.loadSound();
	}

	/**
	 * @author Liam Fraser, Matt Addicott : method to call superclass movement
	 *         method, then checks to see if a new pellet has been eaten
	 *         <p>
	 *         gets the Block position of the sprite, and sees what the tile on
	 *         the map is at that place. The amount of pellets eaten is
	 *         increased if the space contains a pellet, and a boolean is set to
	 *         true to mark that a pellet has been eaten (used for the ghost
	 *         counters) Also responsible for spawning fruit on a specified
	 *         number of eaten pellets and removing them when the timer is
	 *         elapsed.
	 *         </p>
	 * @return GameSpace : the new board, (changed if a pellet is eaten)
	 *
	 */
	public char[][] move() {
		
		move(direction);
		pelletEaten = false;
		consumableEaten = false;

		if ((pelletsTotal == 70 || pelletsTotal == 170) && !fruitSpawned) {
			spawnFruit();
		}
		if (fruitSpawned) {
			if (System.nanoTime() > fruitEndTime) {
				deSpawnFruit();
				fruitSpawned = false;
			}
		}
		encounteredConsumable();
		return GameSpace;
	}

	/**
	 * @author Matt Addicott : Places a fruit in the maze and creates the timer
	 *         to govern when it disappears.
	 */
	private void spawnFruit() {
		GameSpace[BONUS_X][BONUS_Y] = FRUIT;
		randomTime = Math.random() + 9;
		long randomTimeLong = (long) (randomTime * 1000000000);
		randomFruitLength = randomTimeLong;
		fruitSpawnTime = System.nanoTime();
		fruitEndTime = fruitSpawnTime + randomFruitLength;
		fruitSpawned = true;
	}

	/**
	 * @author Matt Addicott : Removes the fruit from the maze.
	 */
	private void deSpawnFruit() {
		GameSpace[BONUS_X][BONUS_Y] = VISITED_TILE;
	}

	/**
	 * @author Matt Addicott : checks whether the index Pac-man occupies also
	 *         contains a consumable and invokes the appropriate behaviours.
	 * @return Gamespace : An updated array of the game with the consumed
	 *         element removed.
	 */
	private char[][] encounteredConsumable() {
		spriteGameSpaceX = getSpriteGameSpaceX();
		spriteGameSpaceY = getSpriteGameSpaceY();
		if (spriteScreenX > 0 && toArrayIndex(spriteScreenX) < 27) {
			if (GameSpace[spriteGameSpaceX][spriteGameSpaceY] == PELLET) {
				if (GameSpace[toArrayIndex(spriteScreenX)][toArrayIndex(spriteScreenY)] == PELLET) {
					GameSpace[spriteGameSpaceX][spriteGameSpaceY] = VISITED_TILE;
					pelletsTotal += 1;
					pelletEaten = true;
					if (playPelletA) {
						pelletSoundA.playSound();
					} else {
						pelletSoundB.playSound();
					}
					playPelletA = !playPelletA;
				}
			} else if (GameSpace[spriteGameSpaceX][spriteGameSpaceY] == ENERGIZER_PELLET) {
				GameSpace[spriteGameSpaceX][spriteGameSpaceY] = VISITED_TILE;
				energiserPelletEaten = true;

				pelletSoundA.playSound();

			} else if (GameSpace[spriteGameSpaceX][spriteGameSpaceY] == FRUIT
					&& fruitSpawned) {
				consumableEaten = true;

				GameSpace[spriteGameSpaceX][spriteGameSpaceY] = VISITED_TILE;
				bonus.playSound();

			} else {
				pelletEaten = false;
				energiserPelletEaten = false;
			}
		}

		return GameSpace;
	}

	/**
	 *  
	 *         <p>
	 *         Sets the direction of the sprite to match the key direction, and
	 *         adjusts the image to match the direction
	 *         </p>
	 * @param KeyEvent
	 *            e : which key has been pressed
	 *
	 */
	public void keyPressed(KeyEvent e) {
		/*
		 * just use key pressed method here.Each time the key been pressed then
		 * the direction been changed.so that can move exactly like pacman game
		 * work.
		 */
		int key = e.getKeyCode();
		if (onTitleScreen) {
			gameStart = true;
		}
		if (key == KeyEvent.VK_LEFT) {
			direction = LEFT;
		}
		if (key == KeyEvent.VK_RIGHT) {
			direction = RIGHT;
		}
		if (key == KeyEvent.VK_UP) {
			direction = UP;
		}
		if (key == KeyEvent.VK_DOWN) {
			direction = DOWN;

		}
		if (key == KeyEvent.VK_C && onTitleScreen) {
			fixedMaze = true;
			gameStart = true;
			onTitleScreen = false;
		} else if (onTitleScreen) {
			gameStart = true;
			onTitleScreen = false;
			randomMaze = true;
		}
		if (onHighScoreScreen) {
			onTitleScreen = true;
			onHighScoreScreen = false;
		}
		if (end) {
			gameEnd = true;
		}

	}

	public void setOnTitleScreen(boolean start) {
		this.onTitleScreen = start;
	}

	public boolean getOnTitleScreen() {
		return onTitleScreen;
	}

	/**
	 * @author Liam Fraser: Method to change the frame of sprite to draw
	 * 
	 *         </p> Cycles through each frame of animation using a counter. When
	 *         it reaches the last sprite, the counter reveres and goes back
	 *         through the images in reverse.</p>
	 *
	 */
	public void changeSprite() {
		if (incrementingCounter) {
			spriteToDraw++;
		} else {
			spriteToDraw--;
		}
		if (spriteToDraw > 2 || spriteToDraw < 1) {
			incrementingCounter = !incrementingCounter;
		}
	}

	public void setFull(boolean isFull) {
		this.isFull = isFull;
	}

	/**
	 * @author Wantao Tang: Method to return pacman's sprite
	 * 
	 *         </p> chooses which sprite to return based on pacman's direction
	 *         and what frame of animation the sprite currently is (determined
	 *         by changeSprite) </p>
	 * 
	 * @return image : the sprite
	 *
	 */
	public Image getFullImage() {
		return pacSprites[0];
	}

	/**
	 * @author Liam Fraser 
	 * <p>Chooses which sprite to draw based on Pacman's direction and currently spriteToDraw value</p>
	 *
	 */
	public Image getImage() {
		if (isFull) {
			return pacSprites[0];

		} else {

			switch (spriteDirectionToDraw()) {
			case RIGHT:
				if (spriteToDraw == 1) {
					return pacSprites[0];
				} else if (spriteToDraw == 2) {
					return pacSprites[3];
				} else {
					return pacSprites[4];
				}
			case DOWN:
				if (spriteToDraw == 1) {
					return pacSprites[0];
				} else if (spriteToDraw == 2) {
					return pacSprites[7];
				} else {
					return pacSprites[8];
				}
			case UP:
				if (spriteToDraw == 1) {
					return pacSprites[0];
				} else if (spriteToDraw == 2) {
					return pacSprites[5];
				} else {
					return pacSprites[6];
				}
			default:
				if (spriteToDraw == 1) {
					return pacSprites[0];
				} else if (spriteToDraw == 2) {
					return pacSprites[1];
				} else {
					return pacSprites[2];
				}
			}

		}
	}

	/**
	 * @author Liam Fraser: Method which returns whether a pellet has been eaten
	 *         in a move.
	 *         <p>
	 *         Returns true if a pellet has been eaten on the current frame,
	 *         false if not
	 *         </p>
	 * @return pelletEaten
	 */
	public boolean isPelletEaten() {
		return pelletEaten;
	}

	/**
	 * @author Liam Fraser: Method to return the total amount of pellets eaten
	 *         <p>
	 *         This can be used to calculate the score (*10 to get the point
	 *         total of one pellet)
	 *         </p>
	 * @return pelletsTotal
	 */
	public int getPelletsTotal() {
		return pelletsTotal;
	}

	public void reduceLifeCount() {
		this.lifeCount--;
	}

	public void giveExtraLife() {
		this.lifeCount++;
	}

	public int getLifeCount() {
		return lifeCount;
	}

	public void resetDirection() {
		direction = LEFT;
	}

	public boolean hasEatenEnergiser() {
		return energiserPelletEaten;
	}

	public boolean hasEatenConsumable() {
		return consumableEaten;
	}

	public boolean getGameStart() {
		return gameStart;
	}

	public boolean getGameEnd() {
		return gameEnd;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public void setGameEnd(boolean gameEnd) {
		this.gameEnd = gameEnd;

	}
	/**
	 * @author Liam Fraser 
	 * <p>Resets pacman's position</p>
	 *@param xTilePos, yTilePos
	 */
	public void reset(int x, int y) {
		spriteGameSpaceX = x;
		spriteGameSpaceY = y;
		spriteScreenX = toScreenPosition(spriteGameSpaceX);
		spriteScreenY = toScreenPosition(spriteGameSpaceY);
	}

	public void resetPelletTotal() {
		pelletsTotal = 0;
	}
	/**
	 * @author Liam Fraser 
	 * <p>Returns whether the user has chosen the default maze</p>
	 *@return True if default
	 */
	public boolean isFixedMaze() {
		return fixedMaze;
	}
	/**
	 * @author Liam Fraser 
	 * <p>Returns whether the user has chosen to use a random maze</p>
	 *@return True if Random
	 */
	public boolean isRandomMaze() {
		return randomMaze;
	}

	public void resetPelletEaten() {
		pelletEaten = false;
	}
	/**
	 * @author Liam Fraser 
	 * <p>Returns the image used for Pacman's life counter</p>
	 */
	public Image getLifeImage() {
		return pacSprites[3];
	}
	/**
	 * @author Liam Fraser 
	 * <p>Resets the Pacman's variables back to their initial state at the start of the game</p>
	 *@param xTilePosition,yTilePosition,Blocksize
	 */
	public void reinitialise(int x, int y, int blockSize) {
		fruitSpawned = false;
		consumableEaten = false;
		randomMaze = false;
		incrementingCounter = true;
		spriteToDraw = 1;
		isFull = true;
		onTitleScreen = true;
		gameStart = false;
		gameEnd = false;
		end = false;
		onHighScoreScreen = false;
		fixedMaze = false;
		lifeCount = 3;
		pelletsTotal = 0;
		super.reinitialise(x, y, blocksize);
	}

	
}
