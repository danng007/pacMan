import java.awt.Rectangle;

public class MoveableSprites extends Sprites{
	protected int targetX;
	protected int targetY;
	protected static final int LEFT = -1;
	protected static final int RIGHT = 1;
	protected static final int UP = 2;
	protected static final int DOWN = -2;
	private static final int NONE = 0;
	protected int direction;
	protected int oldDirection;
	protected int dx, dy;
	protected int gateX1, gateX2, gateY1, gateY2;
	private static final int PIXELS_TO_MOVE = 1;
	protected int currentLevel = 1;


	/**
	 * @author Liam Fraser The constructor for moveableSprites. Used in addition
	 *         to the child constructors
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
	public MoveableSprites(int x, int y, int blocksize) {
		super(x,y,blocksize);
		direction = LEFT;
	}
	public void resetDxDy(){
		dx = 0;
		dy=0;
	}

	/**
	 * @author Wantao Tang :Method to make the sprite aware of the gate
	 *         positions
	 *         <p>
	 *         assigns the positions to protected variables. Giving the sprite
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
	 * @author Wantao Tang :Check whether the ghost is at the left gate
	 *         <p>
	 *         If the ghosts registered position in terms of blocks is the same
	 *         as the gate, then return true.
	 *         </p>
	 * @return boolean : True if at gate
	 *
	 */
	private boolean atLeftGate() {
		if (toArrayIndex(spriteScreenX+blocksize-1) <= gateX1 && toArrayIndex(spriteScreenY) == gateY1) {
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
		if (toArrayIndex(spriteScreenX)
				>= gateX2 && toArrayIndex(spriteScreenY)== gateY2) {
			return true;
		}
		return false;
	}

	/**
	 * @author Liam Fraser : Method to move the sprite
	 *         <p>
	 *         stores the current direction, then tests the new direction to see
	 *         if a movement can be made. If it can't it tests the old
	 *         direction. if this doesn't work then movement stops.
	 *         </p>
	 * @param direction
	 *            : The direction that the user wants to move
	 *
	 */
	public void move(int direction) {

		// Adjust directional values

		oldDirection = this.direction;

		spriteScreenX += dx;
		spriteScreenY += dy;
		if (toArrayIndex(spriteScreenX) == toArrayIndex(spriteScreenX
				+ bottomCornerOffset)
				&& toArrayIndex(spriteScreenY) == toArrayIndex(spriteScreenY
						+ bottomCornerOffset)) {
			if(!atLeftGate()&&!atRightGate()){
			spriteGameSpaceX = toArrayIndex(spriteScreenX);
			spriteGameSpaceY = toArrayIndex(spriteScreenY);
			}
		}

		switch (direction) {
		case LEFT:
			teleportIfAtGate();
			if (validLeft()) {
				dx = -PIXELS_TO_MOVE;
				dy = 0;
				this.direction = direction;
			} else if (valid(oldDirection)) {
				dx = 0;
				dy = 0;
				move(oldDirection);
			} else {
				dx = 0;
				dy = 0;
			}
			break;

		case RIGHT:
			teleportIfAtGate();
			if (validRight()) {
				dx = PIXELS_TO_MOVE;
				dy = 0;
				this.direction = direction;
			} else if (valid(oldDirection)) {
				dx = 0;
				dy = 0;
				move(oldDirection);
			} else {

				dx = 0;
				dy = 0;
			}
			break;

		case UP:
			teleportIfAtGate();
			if (validUp()) {
				this.direction = direction;
				dx = 0;
				dy = -PIXELS_TO_MOVE;
			} else if (valid(oldDirection)) {
				dx = 0;
				dy = 0;
				move(oldDirection);
			} else {
				dx = 0;
				dy = 0;
			}
			break;

		case DOWN:
			teleportIfAtGate();
			if (validDown()) {
				this.direction = direction;
				dx = 0;
				dy = PIXELS_TO_MOVE;
			} else if (valid(oldDirection)) {
				dx = 0;
				dy = 0;
				move(oldDirection);
			} else {
				dx = 0;
				dy = 0;
			}
			break;
		case NONE:
			if (valid(oldDirection)) {
				dx = 0;
				dy = 0;
				move(oldDirection);
			} else {
				dx = 0;
				dy = 0;
			}
		}

	}

	/**
	 * @author Liam Fraser :
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
	private void teleportIfAtGate(){
		if (atLeftGate()) {
			if(direction !=RIGHT){
			if(toArrayIndex(spriteScreenX+bottomCornerOffset)<0){
			spriteGameSpaceX = gateX2-1;
			spriteGameSpaceY = gateY2;
			spriteScreenX = toScreenPosition(spriteGameSpaceX)+blocksize +blocksize-1;
			spriteScreenY = toScreenPosition(spriteGameSpaceY);
			}
			}
		}else if (atRightGate()) {
			if(direction!=LEFT){
				if(toArrayIndex(spriteScreenX)>27){
			spriteGameSpaceX = gateX1+1;
			spriteGameSpaceY = gateY1;
			spriteScreenX = toScreenPosition(spriteGameSpaceX)-blocksize-blocksize+1;
			spriteScreenY = toScreenPosition(spriteGameSpaceY);
				}
		}}
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
	protected boolean validUp() {
		if(atLeftGate()|| atRightGate()){
			return false;
		}
		if (GameSpace[toArrayIndex(spriteScreenX)][toArrayIndex(spriteScreenY - PIXELS_TO_MOVE)] != WALL
				&& GameSpace[toArrayIndex(spriteScreenX + bottomCornerOffset)][toArrayIndex(spriteScreenY
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
	protected boolean validDown() {
		if(atLeftGate()|| atRightGate()){
			return false;
		}
		if (GameSpace[toArrayIndex(spriteScreenX)][toArrayIndex(spriteScreenY) + 1] != WALL
				&& GameSpace[toArrayIndex(spriteScreenX + bottomCornerOffset)][toArrayIndex(spriteScreenY
						+ bottomCornerOffset + PIXELS_TO_MOVE)] != WALL
				&& GameSpace[toArrayIndex(spriteScreenX + bottomCornerOffset)][toArrayIndex(spriteScreenY
						+ bottomCornerOffset + PIXELS_TO_MOVE)] != DOOR
				&& GameSpace[spriteGameSpaceX][spriteGameSpaceY] + 1 != DOOR) {

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
	protected boolean validLeft() {
		if(atLeftGate()){
			return true;
		}
		if (GameSpace[toArrayIndex(spriteScreenX - PIXELS_TO_MOVE)][toArrayIndex(spriteScreenY)] != WALL
				&& GameSpace[toArrayIndex(spriteScreenX + bottomCornerOffset) - 1][toArrayIndex(spriteScreenY
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
	protected boolean validRight() {
		if(atRightGate()){
			return true;
		}
		if (GameSpace[toArrayIndex(spriteScreenX) + 1][toArrayIndex(spriteScreenY)] != WALL
				&& GameSpace[toArrayIndex(spriteScreenX + bottomCornerOffset
						+ PIXELS_TO_MOVE)][toArrayIndex(spriteScreenY + bottomCornerOffset)] != WALL) {
			return true;
		}
		return false;
	}

	public boolean isCollision(Rectangle pacmanSprite, Rectangle ghostSprite) {
		if (pacmanSprite.intersects(ghostSprite)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * @author Liam Fraser
	 * @param
	 *             <p>
	 *           Returns whether the sprite is moving
	 *            </p>
	 *            @return boolean : true if sprite is not moving
	 */
	public boolean isStop(){
		if (dx == 0 && dy ==0) {
			return true;
		}
		else {
			return false;
		}
	}

	protected int spriteDirectionToDraw(){
		return this.direction;
	}
	/**
	 * @author Liam Fraser : Method to return the direction the sprite is
	 *         heading
	 * 
	 * @return direction : the direction the sprite is heading
	 *
	 */
	public int getDirection() {
		return direction;
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
	
	public void setCurrentLevel(int levelNumber){
		currentLevel = levelNumber;
	}
	public void reinitialise(int x, int y, int blocksize) {
		direction = LEFT;
		super.reinitialise(x,y,blocksize);
		
	}
}
