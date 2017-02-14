import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;
import java.util.Random;

import javax.swing.ImageIcon;

/**
 * @author Liam Fraser Parent of each of the different types of ghosts. Controls
 *         their movements, adjusts target position and checks for collision.
 * 
 */
public class Ghost {
	private int targetX;
	private int targetY;
	private int ghostGameMapX;
	private int ghostGameMapY;
	private int ghostScreenX;
	private int ghostScreenY;
	private int bottomCornerOffset;
	private char[][] GameMap;
	protected static final int LEFT = -1;
	protected static final int RIGHT = 1;
	protected static final int UP = 2;
	protected static final int DOWN = -2;
	protected static final int NONE = 0;
	private static final char WALL = '#';
	private static final char DOOR = '=';
	static final char DEAD_ZONE = 'D';
	static final char IN_TUNNEL = 'T';

	private static final int DEFAULT_SPEED = 1;
	private static final int GHOST_DEATH_SPEED = 2;
	private static final char VISITED = 'v';
	private static final char VISITED_TWICE = 'V';
	private static final char IN_HOUSE = 'H';
	private static final int FRIGHT_INDEX_LIMIT = 15;
	private boolean chase = false;
	private int direction;
	private int dx, dy = 0;
	private int blocksize;
	protected int frightCounter = 0;
	protected int frightTimes[] = { 0, 6, 5, 4, 3, 2, 5, 2, 2, 1, 5, 2, 1, 1,
			3, 1 };
	private int frightFlashes[] = { 0, 5, 5, 5, 5, 5, 5, 5, 5, 3, 5, 5, 3, 3,
			5, 3 };
	private boolean displayFlash = false;
	private int flashCounter = GameProcessing.FRAMES_PER_SECOND / 5;
	private long timeToSwitchMode;
	private int pelletsTracked = 0;
	protected int gateX1, gateX2, gateY1, gateY2;
	boolean frightened = false;
	private boolean killed = false;
	private boolean ghostTrappedInLoop = false;
	private Image frightImage;
	private Image frightFlashImage;
	protected Image leftEyesImage;
	private int[] directionSwitcher = { UP, RIGHT, DOWN, LEFT, UP, RIGHT, DOWN,
			LEFT };
	private int pixelsToMove = 1;
	private boolean moveThisFrame = true;
	private boolean usingOwnCounter = true;

	private char visitedTiles[][] = new char[28][31];
	private boolean randomCorrectionsNeeded;

	private URL rightURL;
	private URL leftURL;
	private URL upURL;
	private URL downURL;
	ImageIcon leftdied;
	ImageIcon rightdied;
	ImageIcon updied;
	ImageIcon downdied;
	private Image image;
	private boolean oscillateUp;
	private boolean oscillateDown;
	private boolean inBetweenTiles;
	private int level;
	private int frightIndex = 0;

	/**
	 * @author Liam Fraser The constructor for the ghosts. Used in addition to
	 *         their own constructors (which contain the image ghosts).
	 * 
	 *
	 * @param x
	 *            co-ordinate of the ghost in terms of blocks
	 * @param y
	 *            y co-ordinate of the ghost in terms of blocks
	 * @param blocksize
	 *            the size of the block in pixels, used to determine array
	 *            positioning
	 *
	 */
	public Ghost(int x, int y, int blocksize) {
		this.blocksize = blocksize;
		bottomCornerOffset = blocksize - 1;

		ghostGameMapX = x;
		ghostGameMapY = y;
		ghostScreenX = toScreenPosition(ghostGameMapX);
		ghostScreenY = toScreenPosition(ghostGameMapY);
		direction = 0;
		URL frightImgLoc = getClass().getResource(
				"res/sprites/blueGhostSprite.png");
		ImageIcon frightImageIcon = new ImageIcon(frightImgLoc);
		frightImage = frightImageIcon.getImage();
		frightFlashImage = (new ImageIcon(getClass().getResource(
				"res/sprites/frightFlash.png"))).getImage();
		URL leftEyesImgLoc = getClass().getResource("res/sprites/eyesLeft.png");
		ImageIcon leftEyesIcon = new ImageIcon(leftEyesImgLoc);
		leftEyesImage = leftEyesIcon.getImage();
		leftURL = getClass().getResource("res/sprites/diedLeft.png");
		rightURL = getClass().getResource("res/sprites/diedRight.png");
		upURL = getClass().getResource("res/sprites/diedUp.png");
		downURL = getClass().getResource("res/sprites/diedDown.png");
		leftdied = new ImageIcon(leftURL);
		rightdied = new ImageIcon(rightURL);
		updied = new ImageIcon(upURL);
		downdied = new ImageIcon(downURL);
		image = leftdied.getImage();

	}

	/**
	 * @author Liam Fraser Method to read the array. Used to store the position
	 *         of walls. Only needs to be called once.
	 *         <p>
	 *         Stores the supplied game space to a variable in this class
	 *         (GameMap)
	 *         </p>
	 * @param GameMapSupplied
	 *
	 */
	public void setMaze(char[][] GameMapSupplied) {
		GameMap = GameMapSupplied;
	}

	public void setLevel(int level) {
		this.level = level;
	}



	/**
	 * @author Wantao Tang :Method to make the ghosts aware of the gate
	 *         positions
	 *         <p>
	 *         assigns the positions to protected variables. Giving the ghosts
	 *         an awareness of the gates allows the move function to take
	 *         warping into consideration
	 *         </p>
	 * @param gateX1
	 *            :firstGate X position
	 * @param gateY1
	 *            : first gate Y position
	 * @param gateX2
	 *            : second gate X position
	 * @param gateY2
	 *            : second gate Y position
	 *
	 */
	public void setGate(int gateX1, int gateY1, int gateX2, int gateY2) {
		this.gateX1 = gateX1;
		this.gateY1 = gateY1;
		this.gateX2 = gateX2;
		this.gateY2 = gateY2;
	}

	/**
	 * @author Liam Fraser
	 *         <p
	 *         disables the ghost's internal counter (used to adjust dependency
	 *         on the global counter when pacman dies)
	 * 
	 *         </p>
	 * 
	 *
	 */

	public void disableCounter() {
		usingOwnCounter = false;
	}

	/**
	 * @author Liam Fraser
	 *         <p
	 *         enables the ghost's internal counter (used to adjust dependency
	 *         on the global counter when pacman dies)
	 * 
	 *         </p>
	 * 
	 *
	 */
	public void enableCounter() {
		usingOwnCounter = true;
	}

	/**
	 * @author Liam Fraser
	 *         <p
	 *         returns whether the ghost's internal counter is being used. *
	 *         </p>
	 * @return boolean usingOwnCounter
	 *
	 */
	public boolean isUsingCounter() {
		return usingOwnCounter;
	}

	/**
	 * @author Liam Fraser Method to set the co-ordinates that the ghost is
	 *         targeting
	 *         <p>
	 *         Sets the class' targetX/Y variables according to what is
	 *         supplied.
	 * 
	 *         </p>
	 * @param targetX
	 * @param targetY
	 *
	 */
	public void setTarget(int targetX, int targetY) {
		if (inHouse() && !killed) {
			targetX = 13;
			targetY = 12;
		}
		if (ghostGameMapX == 13 && ghostGameMapY == 12 && !killed) {
			targetX = 12;
			targetY = 11;
		}
		this.targetX = targetX;
		this.targetY = targetY;
	}

	protected void testTarget(int targetX, int targetY) {
		this.targetX = targetX;
		this.targetY = targetY;

	}

	/**
	 * @author Liam Fraser Method to change the amount of time the ghost needs
	 *         to spend in it's current behaviour
	 *         <p>
	 *         Called by PacmanBoard whenever the timer has elapsed and a new
	 *         time needs to be set (as each behaviour mode changes length
	 *         multiple times in a level)
	 *         </p>
	 * @param framesPerSecond
	 * @param scatterTime
	 *            :how many seconds to wait before changing modes
	 *
	 */
	public void setTimer(int framesPerSecond, long scatterTime) {
		timeToSwitchMode = framesPerSecond * scatterTime;
	}

	/**
	 * @author Liam Fraser Method to perform a check whether enough time has
	 *         passed to switch behaviour
	 *         <p>
	 *         Works by decrementing the amount of time left, then checking if
	 *         it has elapsed. If it has, it flips the boolean that describes
	 *         its current mode, reverses it's direction (if valid) and returns
	 *         true if the mode has changed.
	 * 
	 *         </p>
	 * @return boolean : whether the ghost has changed mode
	 *
	 */
	public boolean switchModeCheck() {
		--timeToSwitchMode;
		if (timeToSwitchMode <= 0) {
			// flip the behaviour mode
			reverseDirection();
			chase = !chase;
			return true;
		}
		return false;
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Changes the mode of the ghost from chase to scatter and vice
	 *         versa
	 *         </p>
	 *
	 */
	public void swapChaseMode() {
		chase = !chase;
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Reverses the ghost's direction if valid.
	 *         </p>
	 *
	 */
	private void reverseDirection() {
		int reverseDirection = direction * -1;
		if (!frightened && !killed && toArrayIndex(ghostScreenX) > 0
				&& toArrayIndex(ghostScreenX) < 27 && !inHouse()) {

			if (!atLeftGate() && !atRightGate() && valid(reverseDirection)) {
				direction = reverseDirection;
			}
		}

	}

	/**
	 * @author Liam Fraser : Method to move the ghost
	 *         <p>
	 *         adjusts the direction the ghost is moving according to the
	 *         calculated direction it should go. It then calculates whether it
	 *         should change direction (if it is at a junction or dead end).
	 *         Also changes the position of the ghost if it is at a gate
	 *         </p>
	 * 
	 *
	 */
	public void move() {

		// Adjust directional values
		if (!killed) {
			pixelsToMove = DEFAULT_SPEED;
		} else {
			pixelsToMove = GHOST_DEATH_SPEED;
		}
		if (killed) {
			moveThisFrame = true;
		} else if (frightened || inTunnel() || inHouse()) {
			moveThisFrame = !moveThisFrame;
		} else {
			moveThisFrame = true;
		}
		while (pixelsToMove > 0 && moveThisFrame) {
			boolean changedArrayPos = false;
			switch (direction) {
			case LEFT:
				dx = -DEFAULT_SPEED;
				dy = 0;
				break;

			case RIGHT:
				dx = DEFAULT_SPEED;
				dy = 0;
				break;

			case UP:
				dx = 0;
				dy = -DEFAULT_SPEED;
				break;

			case DOWN:
				dx = 0;
				dy = DEFAULT_SPEED;
				break;

			default:
				// choose a direction to move at the start
				if(!inHouse() && validLeft()){
					direction = LEFT;
				}
				else{
				chooseDirectionToMove();
				}				
			}
			ghostScreenX += dx;
			ghostScreenY += dy;

			if (toArrayIndex(ghostScreenX) == toArrayIndex(ghostScreenX
					+ bottomCornerOffset)
					&& toArrayIndex(ghostScreenY) == toArrayIndex(ghostScreenY
							+ bottomCornerOffset)) {
				ghostGameMapX = toArrayIndex(ghostScreenX);
				ghostGameMapY = toArrayIndex(ghostScreenY);
				if (killed) {
					testIfAlreadyVisitedTile();

				}
				if (!atLeftGate() && !atRightGate()) {
					changedArrayPos = true;
				}
			}
			if (direction == LEFT && atLeftGate()) {
				ghostGameMapX = gateX2 - 1;
				ghostGameMapY = gateY2;
				ghostScreenX = toScreenPosition(gateX2) + blocksize + blocksize
						- 1;
				ghostScreenY = toScreenPosition(gateY2);
				return;
			} else if (direction == RIGHT && atRightGate()) {
				ghostGameMapX = gateX1;
				ghostGameMapY = gateY1;
				ghostScreenX = toScreenPosition(ghostGameMapX) - blocksize
						- blocksize + 1;
				ghostScreenY = toScreenPosition(ghostGameMapY);
				return;
			} else if (changedArrayPos) {
				if (ghostTrappedInLoop) {
					dealWithLooping();
				} else if (!frightened || killed || inHouse()) {
					chooseDirectionToMove();
				} else {
					chooseRandomDirection();
				}
			}
			--pixelsToMove;
		}

	}
	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *         Checks whether the ghost is currently in a tile marked as a tunnel.
	 *          </p>
	 *          @return boolean: true if the ghost is in a tunnel
	 */
	private boolean inTunnel() {
		if (!atLeftGate() && !atRightGate()) {
			if (GameMap[ghostGameMapX][ghostGameMapY] == IN_TUNNEL) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Controls the ghost's vertical oscillation when inside of the
	 *         ghost house. Also controls a boolean (inBetweenTiles, retrieved
	 *         using isInBetweenTiles()) to inform whether the ghost is
	 *         completely contained in a tile.
	 *         </p>
	 *
	 */

	public void oscillate() {
		if (moveThisFrame) {
			if (oscillateUp) {
				if (GameMap[toArrayIndex(ghostScreenX)][toArrayIndex(ghostScreenY - 2)] != WALL
						&& GameMap[toArrayIndex(ghostScreenX)][toArrayIndex(ghostScreenY - 2)] != DOOR) {
					--ghostScreenY;
					direction = UP;
				} else {
					oscillateUp = false;
					oscillateDown = true;
					++ghostScreenY;
					direction = DOWN;
				}
			} else if (oscillateDown) {
				if (GameMap[toArrayIndex(ghostScreenX)][toArrayIndex(ghostScreenY
						+ bottomCornerOffset + 2)] != WALL) {
					++ghostScreenY;
					direction = DOWN;
				} else {
					oscillateUp = true;
					oscillateDown = false;
					--ghostScreenY;
					direction = UP;
				}
			} else {
				oscillateUp = true;
			}
			if (toArrayIndex(ghostScreenX) == toArrayIndex(ghostScreenX
					+ bottomCornerOffset)
					&& toArrayIndex(ghostScreenY) == toArrayIndex(ghostScreenY
							+ bottomCornerOffset)) {
				ghostGameMapX = toArrayIndex(ghostScreenX);
				ghostGameMapY = toArrayIndex(ghostScreenY);
				inBetweenTiles = false;
			} else {
				inBetweenTiles = true;
			}
		}
		moveThisFrame = !moveThisFrame;
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Method that changes the path finding of the ghosts if they are
	 *         trapped in a loop. Instead of travelling down a path which
	 *         minmises the euclidean x,y distance to the target spot, they
	 *         choose the direction which makes their y position closest to the
	 *         target. When the ghost is level with the target, it goes back to
	 *         normal tracking. If this doesn't work, it defaults to choosing
	 *         random directions until free of the loop
	 *         </p>
	 *
	 */
	private void dealWithLooping() {
		if (ghostGameMapY > targetY && !randomCorrectionsNeeded) {
			// Ghost is below target
			if (direction != DOWN && validUp()) {
				direction = UP;
			} else if (direction != RIGHT && validLeft()) {
				direction = LEFT;
			} else if (direction != LEFT && validRight()) {
				direction = RIGHT;
			} else if (direction != UP && validDown()) {
				direction = DOWN;
			}
		} else if (ghostGameMapY < targetY && !randomCorrectionsNeeded) {
			// ghost is above the target
			if (direction != UP && validDown()) {
				direction = DOWN;
			} else if (direction != RIGHT && validLeft()) {
				direction = LEFT;
			} else if (direction != LEFT && validRight()) {
				direction = RIGHT;
			} else if (direction != DOWN && validUp()) {
				direction = UP;
			}
		} else {
			if (ghostGameMapY == targetY && hasVisitedTwice()) {
				// if loop is to the left or right of the home
				System.out.println("Can't escape! :(");
				randomCorrectionsNeeded = true;
			} else {
				System.out.println("Maybe this way...");
				randomCorrectionsNeeded = false;
			}
			if (randomCorrectionsNeeded) {
				chooseRandomDirection();
			} else {
				System.out.println("i've sorted myself out!");
				ghostTrappedInLoop = false;
				chooseDirectionToMove();
			}
		}

	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Detects whether the ghost has already revisited it's current
	 *          tile in it's current death mode.
	 *          </p>
	 * @return boolean: true if has already revisited
	 */
	private boolean hasVisitedTwice() {
		if (!atLeftGate() && !atRightGate()) {
			if (visitedTiles[ghostGameMapX][ghostGameMapY] == VISITED_TWICE) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Detects whether the ghost has already visited it's current tile
	 *          in it's current death mode. Sets a boolean ghostTrappedInLoop to
	 *          say that a change in behavior is required to escape from the
	 *          loop
	 *          </p>
	 * 
	 */
	private void testIfAlreadyVisitedTile() {
		if (!atLeftGate() && !atRightGate()) {
			if (visitedTiles[ghostGameMapX][ghostGameMapY] == VISITED) {
				ghostTrappedInLoop = true;
				visitedTiles[ghostGameMapX][ghostGameMapY] = VISITED_TWICE;
			} else if (visitedTiles[ghostGameMapX][ghostGameMapY] != VISITED_TWICE) {
				visitedTiles[ghostGameMapX][ghostGameMapY] = VISITED;
			}
		}
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         chooses a random direction to move in, then cycles through the
	 *         list of directions from that point until a valid direction is
	 *         found.
	 *         </p>
	 *
	 */
	private void chooseRandomDirection() {
		Random rand = new Random();
		if (isAtDeadEnd()) {
			System.out.println("I@M IN A DEAD IND");
			direction = direction * -1;
			return;
		}
		int randDirection = rand.nextInt(4) + 1;
		int directionIndex = 0;
		int testDirection = UP;
		boolean badDirection = false;
		switch (randDirection) {
		case 1:
			if (direction == DOWN) {
				badDirection = true;
			} else {
				testDirection = UP;
			}
			directionIndex = 0;
			break;
		case 4:
			if (direction == LEFT) {
				badDirection = true;
			} else {
				testDirection = RIGHT;
			}
			directionIndex = 1;
			break;
		case 2:
			if (direction == UP) {
				badDirection = true;
			} else {
				testDirection = DOWN;
			}
			directionIndex = 2;
			break;
		case 3:
			if (direction == RIGHT) {
				badDirection = true;
			} else {
				testDirection = LEFT;
			}
			directionIndex = 3;
			break;
		}
		while (!valid(testDirection) || badDirection) {
			++directionIndex;
			testDirection = directionSwitcher[directionIndex];
			if (direction == testDirection * -1) {
				badDirection = true;
			} else {
				badDirection = false;
			}
		}
		direction = testDirection;

	}

	/**
	 * @author Liam Fraser : Abstract Method to assess whether a direction is
	 *         valid
	 *         <p>
	 *         Calls an appropriate method to determine whether a direction is
	 *         valid
	 *         </p>
	 * @param direction
	 *            : The direction to test
	 * @return boolean : True if supplied direction is valid
	 *
	 */
	protected boolean valid(int direction) {
		switch (direction) {
		case UP:
			return validUp();
		case DOWN:
			return validDown();
		case LEFT:
			return validLeft();
		case RIGHT:
			return validRight();
		}
		return true;

	}

	/**
	 * @author Liam Fraser : Method that chooses the most appropriate direction
	 *         to move
	 *         <p>
	 *         if a direction is valid and is not the opposite direction to
	 *         current travel, the sprites array position is adjusted to match
	 *         the position it would be if it headed in this direction . The
	 *         distance between this point and the target is then calculated.
	 *         The minimum of these is then calculated, and the direction
	 *         changed to match it.
	 *         </p>
	 * 
	 *
	 */
	// move to the tile with the smallest Straight Line Distance to the target
	private void chooseDirectionToMove() {
		int rightDistance = 1000;
		int leftDistance = 1000;
		int upDistance = 1000;
		int downDistance = 1000;

		if (validRight() && direction != LEFT) {

			ghostGameMapX++;
			rightDistance = calculateDistance();
			ghostGameMapX--;
		}
		if (validLeft() && direction != RIGHT) {
			ghostGameMapX--;
			leftDistance = calculateDistance();
			ghostGameMapX++;
		}
		if (validDown() && direction != UP) {
			ghostGameMapY++;
			downDistance = calculateDistance();
			ghostGameMapY--;
		}
		if (validUp() && direction != DOWN) {
			ghostGameMapY--;
			upDistance = calculateDistance();
			ghostGameMapY++;
		}
		int minDistance = Math.min(Math.min(rightDistance, leftDistance),
				Math.min(downDistance, upDistance));
		if (minDistance == upDistance) {
			direction = UP;
		} else if (minDistance == leftDistance) {
			direction = LEFT;
		} else if (minDistance == downDistance) {
			direction = DOWN;
		} else {
			direction = RIGHT;
		}
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Calculates the euclidean distance to the specified target using
	 *         pythagoras' theorem
	 *         </p>
	 * @return int: the distance between the ghost and the target
	 */
	protected int calculateDistance() {
		int distanceX = targetX - ghostGameMapX;
		int distanceY = targetY - ghostGameMapY;
		// euclidean distance between points
		return (int) Math.sqrt((distanceX * distanceX)
				+ (distanceY * distanceY));
	}

	/**
	 * @author Liam Fraser : Method to test whether the up direction is a valid
	 *         move
	 *         <p>
	 *         Tests whether the top left corner or bottom right corner of the
	 *         sprite will collide with a wall if the sprite moves in this
	 *         direction.
	 *         </p>
	 * @return boolean : true if direction is valid to move
	 *
	 */
	private boolean validUp() {
		if (GameMap[toArrayIndex(ghostScreenX)][toArrayIndex(ghostScreenY)] != DEAD_ZONE
				&& GameMap[toArrayIndex(ghostScreenX)][toArrayIndex(ghostScreenY) - 1] != WALL
				&& GameMap[toArrayIndex(ghostScreenX + bottomCornerOffset)][toArrayIndex(ghostScreenY
						+ bottomCornerOffset) - 1] != WALL) {
			return true;
		}
		return false;
	}

	/**
	 * @author Liam Fraser : Method to test whether the down direction is a
	 *         valid move
	 *         <p>
	 *         Tests whether the top left corner or bottom right corner of the
	 *         sprite will collide with a wall or House door if the sprite moves
	 *         in this direction.
	 *         </p>
	 * @return boolean : true if direction is valid to move
	 *
	 */
	private boolean validDown() {

		if (killed) {
			if (GameMap[toArrayIndex(ghostScreenX)][toArrayIndex(ghostScreenY) + 1] != WALL
					&& GameMap[toArrayIndex(ghostScreenX + bottomCornerOffset)][toArrayIndex(ghostScreenY
							+ bottomCornerOffset) + 1] != WALL) {
				return true;
			} else {
				return false;

			}
		} else if (GameMap[toArrayIndex(ghostScreenX)][toArrayIndex(ghostScreenY) + 1] != WALL
				&& GameMap[toArrayIndex(ghostScreenX + bottomCornerOffset)][toArrayIndex(ghostScreenY
						+ bottomCornerOffset) + 1] != WALL
				&& GameMap[toArrayIndex(ghostScreenX + bottomCornerOffset)][toArrayIndex(ghostScreenY
						+ bottomCornerOffset) + 1] != DOOR
				&& GameMap[ghostGameMapX][ghostGameMapY + 1] != DOOR) {
			return true;
		}
		return false;
	}

	/**
	 * @author Liam Fraser : Method to test whether the Left direction is a
	 *         valid move
	 *         <p>
	 *         Tests whether the top left corner or bottom right corner of the
	 *         sprite will collide with a wall if the sprite moves in this
	 *         direction.
	 *         </p>
	 * @return boolean : true if direction is valid to move
	 *
	 */
	private boolean validLeft() {
		if (GameMap[toArrayIndex(ghostScreenX) - 1][toArrayIndex(ghostScreenY)] != WALL
				&& GameMap[toArrayIndex(ghostScreenX + bottomCornerOffset) - 1][toArrayIndex(ghostScreenY
						+ bottomCornerOffset)] != WALL) {
			return true;
		}
		return false;
	}

	/**
	 * @author Liam Fraser : Method to test whether the right direction is a
	 *         valid move
	 *         <p>
	 *         Tests whether the top left corner or bottom right corner of the
	 *         sprite will collide with a wall if the sprite moves in this
	 *         direction.
	 *         </p>
	 * @return boolean : true if direction is valid to move
	 *
	 */
	private boolean validRight() {
		if (GameMap[toArrayIndex(ghostScreenX) + 1][toArrayIndex(ghostScreenY)] != WALL
				&& GameMap[toArrayIndex(ghostScreenX + bottomCornerOffset) + 1][toArrayIndex(ghostScreenY
						+ bottomCornerOffset)] != WALL) {
			return true;
		}
		return false;
	}

	/**
	 * @author Liam Fraser : Method to convert a screen co-ordinate to a block
	 *         Index
	 * @param screenCoordinate
	 *            : the screen co-ordinate to be converted
	 * @return int : the Array index of the supplied screenCoordinate
	 *
	 */
	private int toArrayIndex(int screenCoordinate) {
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
	private int toScreenPosition(int arrayPos) {
		return arrayPos * blocksize;
	}

	public Rectangle getRectangle() {

		return new Rectangle(ghostScreenX, ghostScreenY, blocksize - 1,
				blocksize - 1);

	}

	/**
	 * @author: Liam Fraser
	 * @param x
	 *            , y (block positions of the ghost)
	 *            <p>
	 *            Sets the position of the ghost in blocks and converts to a
	 *            screen position
	 *            </p>
	 */
	public void setCoordinate(int x, int y) {
		ghostGameMapX = x;
		ghostGameMapY = y;
		ghostScreenX = toScreenPosition(x);
		ghostScreenY = toScreenPosition(y);
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         changes the status of the ghost to killed
	 *         </p>
	 *
	 */
	public void kill() {
		killed = true;
	}

	/**
	 * @author Liam Fraser
	 * @param int x, int y : The co-ordinates of the Ghost's initial position
	 *        (in blocks)
	 *        <p>
	 *        resets the ghosts position, mode,pellets tracked and direction
	 *        </p>
	 *
	 */
	public void reset(int x, int y) {
		ghostGameMapX = x;
		ghostGameMapY = y;
		ghostScreenX = toScreenPosition(x);
		ghostScreenY = toScreenPosition(y);
		dx = 0;
		dy = 0;
		direction = NONE;
		chase = false;
		frightened = false;
		killed = false;
		pelletsTracked = 0;
		ghostTrappedInLoop = false;
		oscillateUp = false;
		oscillateDown = false;
		visitedTiles = new char[28][31];
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Increases the number of pellets the ghost has tracked
	 *         </p>
	 *
	 */
	public void increasePelletsTracked() {
		pelletsTracked++;
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Initiates fright mode.
	 *         </p>
	 *
	 */
	public void frightInit(int framesPerSecond) {
		if (toArrayIndex(ghostScreenX) > 0 && toArrayIndex(ghostScreenX) < 27
				&& !inHouse()) {
			reverseDirection();
		}

		frightened = true;
		if (level <= FRIGHT_INDEX_LIMIT) {
			frightIndex = level;
		}
		frightCounter = frightTimes[frightIndex] * framesPerSecond;

		// chooseRandomDirection();

	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         reduces a counter. If this counter becomes less than one, then
	 *         the ghost is no longer frightened
	 *         </p>
	 *
	 */
	public void recoveredFromFrightCheck() {
		--frightCounter;
		if (frightCounter < 1) {
			frightened = false;
		}
	}

	/**
	 * @author <p>
	 *         If the ghosts registered position in terms of blocks is the same
	 *         as the Left gate, then return true.
	 *         </p>
	 * @return boolean : True if at gate
	 *
	 */
	private boolean atLeftGate() {
		if (ghostGameMapX <= gateX1 && ghostGameMapY == gateY1) {
			return true;
		}
		return false;
	}

	/**
	 * @author Wantao Tang :Check whether the ghost is at the right gate
	 *         <p>
	 *         If the ghosts registered position in terms of blocks is the same
	 *         as the gate, then return true.
	 *         </p>
	 * @return boolean : True if at gate
	 *
	 */
	private boolean atRightGate() {
		if (ghostGameMapX >= gateX2 && ghostGameMapY == gateY2) {
			return true;
		}
		return false;
	}

	/**
	 * @author Liam Fraser : Checks whether the ghost is in the central house
	 *         <p>
	 *         checks the ghosts block position against the Mazes registered
	 *         value at that space
	 *         </p>
	 * @return boolean : true if value on space is the IN_HOUSE character
	 *
	 */

	/**
	 * @author Liam Fraser: Returns what mode the ghost is in (chase = true,
	 *         scatter = false)
	 * @return chase : the mode of the ghost
	 *
	 */
	public boolean isChase() {
		return chase;
	}

	/**
	 * @author Liam Fraser: Returns the screenPosition of the ghost (X
	 *         co-ordinate)
	 * @return ghostScreenX, ghosts screen position (X)
	 *
	 */
	public int getX() {
		return ghostScreenX;
	}

	/**
	 * @author Liam Fraser: Returns the array position of the ghost (X
	 *         co-ordinate)
	 * @return ghostGameMapX : ghosts X position (in terms of blocks)
	 *
	 */
	public int getBlockX() {
		return ghostGameMapX;
	}

	/**
	 * @author Liam Fraser: Returns the array position of the ghost (Y
	 *         co-ordinate)
	 * @return ghostGameMapY : ghosts Y block position
	 *
	 */
	public int getBlockY() {
		return ghostGameMapY;
	}

	/**
	 * @author Liam Fraser: Returns the screenPosition of the ghost (Y
	 *         co-ordinate)
	 * @return ghostScreenY : Ghosts Y screen position
	 *
	 */
	public int getY() {
		return ghostScreenY;
	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         checks if the ghost is currently inside the ghost house
	 *         </p>
	 * @return boolean : true if in house
	 *
	 */
	protected boolean inHouse() {
		if (atLeftGate() || atRightGate()) {
			return false;
		}
		if (GameMap[ghostGameMapX][ghostGameMapY] == IN_HOUSE
				|| GameMap[ghostGameMapX][ghostGameMapY] == DOOR) {

			return true;
		}
		return false;
	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         returns the amount of pellets tracked by the ghost
	 *         </p>
	 * @return pelletsTracked
	 */
	public int getPelletsTracked() {
		return pelletsTracked;
	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         returns whether or not the ghost is frightened
	 *         </p>
	 * @return frightened
	 */
	public boolean isFrightened() {
		return frightened;
	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         returns the frightened image
	 *         </p>
	 * @return frightImage
	 */
	public Image getFrightenedImage() {
		if (frightCounter < 2 * GameProcessing.FRAMES_PER_SECOND
				&& displayFlash) {
			return frightFlashImage;
		} else {
			return frightImage;
		}
	}

	protected Image getFrightFlashImage() {
		return frightFlashImage;
	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         returns whether the ghost is killed
	 *         </p>
	 * @return killed
	 */
	public boolean isKilled() {
		return killed;
	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         returns the direction the ghost is travelling
	 *         </p>
	 * @return direction
	 */
	protected int getDirection() {
		return direction;

	}

	public Image diedImage() {
		switch (getDirection()) {
		case RIGHT:
			image = rightdied.getImage();
			break;
		case DOWN:
			image = downdied.getImage();
			break;
		case UP:
			image = updied.getImage();
			break;
		default:
			image = leftdied.getImage();
			break;
		}
		return image;
	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Causes the ghost to flash by decrementing a counter and changing
	 *          it's state every time it reaches zero. Counter is reset to
	 *          represent the time duration to the next flash
	 *          </p>
	 */
	public void alterFlashCounter() {
		--flashCounter;
		if (flashCounter <= 0) {
			displayFlash = !displayFlash;
			flashCounter = GameProcessing.FRAMES_PER_SECOND
					/ frightFlashes[frightIndex];
		}
	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Assesses whether the ghost has reached a dead end by counting
	 *          the number of valid directions.
	 *          </p>
	 */
	private boolean isAtDeadEnd() {
		int countPaths = 0;
		if (GameMap[ghostGameMapX + 1][ghostGameMapY] != WALL) {
			countPaths++;
		}
		if (GameMap[ghostGameMapX - 1][ghostGameMapY] != WALL) {
			countPaths++;
		}
		if (GameMap[ghostGameMapX][ghostGameMapY + 1] != WALL) {
			countPaths++;
		}
		if (GameMap[ghostGameMapX][ghostGameMapY - 1] != WALL) {
			countPaths++;
		}
		if (countPaths > 1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         returns whether the ghost is inbetween two tiles, as movement
	 *         decisions are based on blocks
	 *         </p>
	 *
	 */
	public boolean isInBetweenTiles() {
		return inBetweenTiles;
	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Reinitialises the state of the ghost back to the values at the
	 *          start of the game.
	 *          </p>
	 */
	public void reinitialise(int x, int y, int blocksize) {
		this.blocksize = blocksize;
		bottomCornerOffset = blocksize - 1;

		ghostGameMapX = x;
		ghostGameMapY = y;
		ghostScreenX = toScreenPosition(ghostGameMapX);
		ghostScreenY = toScreenPosition(ghostGameMapY);
		direction = 0;
		pixelsToMove = 1;
		moveThisFrame = true;
		usingOwnCounter = true;
		frightened = false;
		killed = false;
		ghostTrappedInLoop = false;
		pelletsTracked = 0;
		chase = false;
		level = 1;

		dx = 0;
		dy = 0;

		frightCounter = 0;

	}
}
