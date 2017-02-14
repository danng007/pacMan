import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("unchecked")
/*
 * Author: Munachiso Emelle
 * Maze Generator class
 * 
 * The maze generator works by first creating a 5x9 simple grid model. The edges
 * of a cell represent paths, and every cell is initialised with no edges to
 * begin with. The insides of the cell represent the walls of the maze. Each
 * cell (represented as HashMaps) can have at most 2 edges, and can therefore
 * only be of shape "L" or "_". Depending on the order and placement of the
 * cells the shapes can be any of 4 orientations, 0deg, 90deg, 180deg, 270deg
 * This is achieved by mapping a Boolean object value to the String keys: "top",
 * "right", "left" and "bottom" of the HashMap of each cell, these represent the
 * borders of the cell. The cells are filled randomly from left to right, with
 * map objects using an algorithm to ensure no dead ends are created and to
 * restrict the inputs at certain predefined special cells in the grid as well
 * as limit based on neighbouring cells. Note that this implementation technique
 * will naturally allow for building walls of shape I, L, T, or +. (Rectangular
 * walls may also happen)
 * 
 * Example of limiting factors: Cells that make up the ghost house are protected
 * Pacman's starting location is also protected <- not yet added
 * 
 * Implementation requirements: Two cells with the same edge must have a boolean
 * value of true for that edge. Two or more cells combined must not create a
 * complete square Every cell must have at least one edge Every cell must have
 * an edge that is connected to another edge
 * 
 * A maximum of one tunnel are added to the grid
 * 
 * The simple model is then mirrored on the first column of its left vertical
 * axis to complete a 9x9 simple model map grid This grid is then simultaneously
 * adjusted by height and width, while being scaled to a full grid of 28x31
 * (initally using a scale factor of 3x3)
 */
public class MazeGenerator {
	static final char ENERGIZER_PELLET = 'O';
	static final char PELLET = 'o';
	static final char WALL = '#';
	static final char BLANK = '*';
	static final char DOOR = '=';
	static final char IN_HOUSE = 'H';

	private HashMap<String, Boolean>[][] fullSimpleModelGrid;
	private static final int MAX_X_SIMPLE_MODEL_HALF = 5;
	private int[] numOfCellsInRowOfSimpleGridHalf;
	private static final int MAX_X_SIMPLE_MODEL = 9;
	private static final int MAX_Y_SIMPLE_MODEL = 9;
	private static final int MAX_X_FULL_MODEL = 28;
	private static final int MAX_Y_FULL_MODEL = 31;

	public MazeGenerator() {
		// Initialise each cell in simple 5x9 model of game map.
		HashMap<String, Boolean>[][] rightHalfOfSimpleModelGrid = new HashMap[MAX_X_SIMPLE_MODEL_HALF][MAX_Y_SIMPLE_MODEL];
		this.numOfCellsInRowOfSimpleGridHalf = new int[MAX_Y_SIMPLE_MODEL];
		initRightHalfOfSimpleModelGrid(
				rightHalfOfSimpleModelGrid, numOfCellsInRowOfSimpleGridHalf);
	}

	public char[][] generateAndReturnMaze() {

		char[][] completedMaze;
		boolean valid;
		do {
			HashMap<String, Boolean>[][] rightHalfOfSimpleModelGrid = new HashMap[MAX_X_SIMPLE_MODEL_HALF][MAX_Y_SIMPLE_MODEL];
			this.numOfCellsInRowOfSimpleGridHalf = new int[MAX_Y_SIMPLE_MODEL];
			initRightHalfOfSimpleModelGrid(
					rightHalfOfSimpleModelGrid,
					numOfCellsInRowOfSimpleGridHalf);
			rightHalfOfSimpleModelGrid = generateRandomMazeForSimpleModelHalf(rightHalfOfSimpleModelGrid);
			fullSimpleModelGrid = mirrorSimpleModelHalfIntoFullSimpleModelGrid(rightHalfOfSimpleModelGrid);
			char[][] wipMaze = scaleSimpleModelToFullMaze(fullSimpleModelGrid);
			wipMaze = addArtifactsToMaze(wipMaze);
			wipMaze = moveBlockPiecesInProtectedCells(wipMaze);
			completedMaze = removeDeadEnds(wipMaze);
			// if Pacman isn't in a valid location, re-initialise and retry
			valid = isValidMap(completedMaze);
			
		} while (!valid);
		completedMaze = generateEnergizers(completedMaze);
		return completedMaze;
		// return wipMaze;
	}

	// checks to make sure the map is valid
	private boolean isValidMap(char[][] maze) {
		int pacX = 13;
		int pacY = 23;
		boolean validLine = true;
		if (maze[pacX][pacY] != PELLET) {
			return false;
		} else {
			// If there exists a line in the maze that is completely walls, then
			// maze is invalid.
			for (int y = 1; y < MAX_Y_FULL_MODEL - 3; y++) {
				validLine = false;
				for (int x = 0; x < MAX_X_FULL_MODEL - 1; x++) {
					if (maze[x][y] != WALL) {
						validLine = true;
						if (x > 0 && x < MAX_X_FULL_MODEL - 1 && y > 0
								&& y < MAX_Y_FULL_MODEL) {
							if(maze[x+1][y]==WALL&&maze[x][y+1]==WALL&&maze[x-1][y]==WALL&&maze[x][y-1]==WALL){
								System.out.println("REJECTING MAZE BECAUSE OF STRAY PELLET!");
								return false;
							}
							if (maze[x][y] != IN_HOUSE) {
								if (maze[x + 1][y] == PELLET
										|| maze[x + 1][y] == ENERGIZER_PELLET) {
									if (maze[x + 1][y + 1] == PELLET
											|| maze[x + 1][y + 1] == ENERGIZER_PELLET) {
										if (maze[x][y + 1] == PELLET
												|| maze[x][y + 1] == ENERGIZER_PELLET) {
											// invalid: Block of non wall tiles
											// outside of home
											System.out
													.println("Rejecting maze because of block of blanks");
											return false;
										}
									}
								}
							}
						}
					}
				}
				if (!validLine) {
					System.out
							.println("Rejecting maze because of sectioned off area");
					return false;
				}

			}
			return true;
		}

	}

	private HashMap<String, Boolean>[][] initRightHalfOfSimpleModelGrid(
			HashMap<String, Boolean>[][] rightHalfOfSimpleModelGrid,
			int[] numOfCellsInRowOfSimpleGridHalf) {

		for (int y = 0; y < MAX_Y_SIMPLE_MODEL; y++) {
			this.numOfCellsInRowOfSimpleGridHalf[y] = 0;
		}

		for (int x = 0; x < MAX_X_SIMPLE_MODEL_HALF; x++) {
			for (int y = 0; y < MAX_Y_SIMPLE_MODEL; y++) {
				HashMap<String, Boolean> cell = new HashMap<String, Boolean>();
				cell.put("TOP", false);
				cell.put("RIGHT", false);
				cell.put("BOTTOM", false);
				cell.put("LEFT", false);
				cell.put("blockIDIsSet", false); // used when adding blocks to
													// the grid
				rightHalfOfSimpleModelGrid[x][y] = cell;
			}
		}
		return rightHalfOfSimpleModelGrid;
	}

	private HashMap<String, Boolean>[][] generateRandomMazeForSimpleModelHalf(
			HashMap<String, Boolean>[][] rightHalfOfSimpleModelGrid) {
		// Play a very good game of tetris, slowly filling up each cell in the
		// grid with individual block pieces that sum up to compound tetris
		// shaped blocks
		// works by dropping (gravity pulling left) a random number of blocks
		// between 1 and 5 unto the grid in a random limited area that spans
		// between a cell width of 2 and 4
		// In each cycle (from 0 to numOfBlocksToDrop-1) the blocks are dropped
		// till all blocks are dropped or...
		// a block (that isn't the first block dropped in the cycle) is dropped
		// where it isn't connected to any of the previously dropped blocks in
		// the cycle
		// Note: the disconnected block is not added to the grid and the next
		// cycle starts
		while (emptyCellExists()) {

			// ONE CYCLE
			int mostEmptyRowInGrid = getMostEmptyRow();
			int maxCompoundBlockSpan = getRandomIntValue(3) + 1;
			if (numOfCellsInRowOfSimpleGridHalf[mostEmptyRowInGrid] == 0) {
				// if the row has no block pieces, then limit the span to 2 so
				// it can be filled more quickly.
				maxCompoundBlockSpan = 2;
			}
			int maxNumOfCellsInBlock = getRandomIntValue(3) + 1;
			if (maxNumOfCellsInBlock == 4) {
				maxNumOfCellsInBlock = getRandomIntValue(3) + 1;
			} // makes it less likely to produce blocks with 5 pieces
			int compoundBlockID = getRandomIntValue(999) + 1000;
			int numOfBlockPiecesAlreadyDropped = 0;
			int maxAttemptsToAddBlockPieceToGrid = maxNumOfCellsInBlock;
			int numOfAttemptsUsed = 0;

			boolean potentialTunnelEdgeCreated = false;

			while (numOfBlockPiecesAlreadyDropped < maxNumOfCellsInBlock
					&& numOfAttemptsUsed < maxAttemptsToAddBlockPieceToGrid) {
				// DROP A PIECE

				int minY = mostEmptyRowInGrid;
				if (MAX_Y_SIMPLE_MODEL - minY < maxCompoundBlockSpan) {
					maxCompoundBlockSpan = MAX_Y_SIMPLE_MODEL - minY;
				}
				int y = minY + getRandomIntValue(maxCompoundBlockSpan) - 1; // get
																			// random
																			// y
																			// within
																			// range
																			// of
																			// empty
																			// row
																			// and
																			// max
																			// compound
																			// block
																			// width
				int attemptsToFindNonFullRow = 0;
				int maxPermittedAttemptsToFindNonFullRow = 5;
				while (numOfCellsInRowOfSimpleGridHalf[y] == MAX_X_SIMPLE_MODEL_HALF
						&& attemptsToFindNonFullRow < maxPermittedAttemptsToFindNonFullRow) {
					// while the row is full, keep looking for a non-full row
					// within the chosen block span where a block piece can
					// actually be added
					y = minY + getRandomIntValue(maxCompoundBlockSpan) - 1;
					attemptsToFindNonFullRow++;
				}
				if (attemptsToFindNonFullRow == maxPermittedAttemptsToFindNonFullRow
						&& numOfCellsInRowOfSimpleGridHalf[y] >= MAX_X_SIMPLE_MODEL_HALF) {
					// could not find a non-full row, cells are probably all
					// full
					break;
				}

				int x = numOfCellsInRowOfSimpleGridHalf[y];

				HashMap<String, Boolean> thisCell = rightHalfOfSimpleModelGrid[x][y];

				boolean cellExistsToTheLeft;
				boolean cellExistsAbove;
				boolean cellExistsBelow;
				HashMap<String, Boolean> cellToTheLeft = null;
				HashMap<String, Boolean> cellAbove = null;
				HashMap<String, Boolean> cellBelow = null;

				if (x == 0) {
					// at reflection column of the simple model grid
					cellExistsToTheLeft = false;
				} else if (x == MAX_X_SIMPLE_MODEL_HALF - 1) {
					// at far right column of the simple model grid
					cellExistsToTheLeft = true;
					cellToTheLeft = rightHalfOfSimpleModelGrid[x - 1][y];
				} else {
					// cells not on the edge columns of the simple model grid
					cellExistsToTheLeft = true;
					cellToTheLeft = rightHalfOfSimpleModelGrid[x - 1][y];
				}

				if (y == 0) {
					// at the top row of the simple model grid
					cellExistsAbove = false;
					cellExistsBelow = true;
					cellBelow = rightHalfOfSimpleModelGrid[x][y + 1];
				} else if (y == MAX_Y_SIMPLE_MODEL - 1) {
					// at the bottom row of the simple model grid
					cellExistsAbove = true;
					cellExistsBelow = false;
					cellAbove = rightHalfOfSimpleModelGrid[x][y - 1];
				} else {
					// in the middle rows of the simple model grid
					cellExistsAbove = true;
					cellExistsBelow = true;
					cellAbove = rightHalfOfSimpleModelGrid[x][y - 1];
					cellBelow = rightHalfOfSimpleModelGrid[x][y + 1];
				}

				if (numOfBlockPiecesAlreadyDropped > 1) {
					// any piece dropped after the first piece must attach to a
					// previously dropped piece, else break while loop
					// check if all existing cells with block pieces in contact
					// with this cell and block piece being attached are not in
					// its compound block, if so break while loop
					// The thought process being that it must be in contact with
					// at least one block piece in its compound block (i.e.
					// sharing the same block ID)
					if (((cellExistsToTheLeft
							&& cellToTheLeft.get("blockIDIsSet") && cellToTheLeft
							.get(Integer.toString(compoundBlockID)) == null) || !cellExistsToTheLeft)
							&& ((cellExistsAbove
									&& cellAbove.get("blockIDIsSet") && cellAbove
									.get(Integer.toString(compoundBlockID)) == null) || !cellExistsAbove)
							&& ((cellExistsBelow
									&& cellBelow.get("blockIDIsSet") && cellBelow
									.get(Integer.toString(compoundBlockID)) == null) || !cellExistsBelow)) {
						break;
					}
				}

				// check if cell to the left is part of this compound block
				if (cellExistsToTheLeft
						&& cellToTheLeft.get("blockIDIsSet")
						&& cellToTheLeft.get(Integer.toString(compoundBlockID)) != null) {
					// remove the edge between this cell and the cell to its
					// left
					cellToTheLeft.put("RIGHT", false);
					thisCell.put("LEFT", false);

					if (x == MAX_X_SIMPLE_MODEL_HALF - 1) {
						// this cell is at the far right edge of the grid -
						// randomly decide whether to have a right tunnel edge
						// (path) here or not

						// in deciding whether to have a tunnel edge, the
						// likelihood of not having one should be greater.
						boolean trueOrFalse = !convertIntValToBool(getRandomIntValue(6) - 1); // increases
																								// the
																								// likelihood
																								// of
																								// not
																								// having
																								// an
																								// edge.
						if (!trueOrFalse) {
							potentialTunnelEdgeCreated = true;
						}
						cellToTheLeft.put("RIGHT", potentialTunnelEdgeCreated);
						thisCell.put("LEFT", potentialTunnelEdgeCreated);
					}
				} else if (cellExistsToTheLeft
						&& cellToTheLeft.get("blockIDIsSet")
						&& cellToTheLeft.get(Integer.toString(compoundBlockID)) == null) {
					// cell to the left is part of a different compound block so
					// make sure there is an edge in-between
					cellToTheLeft.put("RIGHT", true);
					thisCell.put("LEFT", true);

					if (x == MAX_X_SIMPLE_MODEL_HALF - 1) {
						// this cell is at the far right edge of the grid -
						// randomly decide whether to have a right tunnel edge
						// (path) here or not
						// in deciding whether to have a tunnel edge, the
						// likelihood of not having one should be greater.
						boolean trueOrFalse = !convertIntValToBool(getRandomIntValue(6) - 1); // increases
																								// the
																								// likelihood
																								// of
																								// not
																								// having
																								// an
																								// edge.
						if (!trueOrFalse) {
							potentialTunnelEdgeCreated = true;
						}
						cellToTheLeft.put("RIGHT", potentialTunnelEdgeCreated);
						thisCell.put("LEFT", potentialTunnelEdgeCreated);
					}
				} else {
					// cell doesn't exist to the left therefore this cell is at
					// the left edge of the half grid - randomly decide whether
					// to have an edge here or not
					// in deciding whether to have an edge, the likelihood of
					// not having one should be greater.
					boolean trueOrFalse = !convertIntValToBool(getRandomIntValue(3) - 1);
					// increases the likelihood of not having an edge.
					thisCell.put("LEFT", trueOrFalse);
				}

				// then check cell above
				if (cellExistsAbove
						&& cellAbove.get("blockIDIsSet")
						&& cellAbove.get(Integer.toString(compoundBlockID)) != null) {
					// remove the edge between this cell and the cell above
					cellAbove.put("BOTTOM", false);
					thisCell.put("TOP", false);
					// System.out.println("block with block id ( "+compoundBlockID+" ) landing at positon ("+x+","+y+") attached to block above at position ("+(x)+","+(y-1)+")");
				} else if (cellExistsAbove
						&& cellAbove.get("blockIDIsSet")
						&& cellAbove.get(Integer.toString(compoundBlockID)) == null) {
					// cell above is part of a different compound block so make
					// sure there is an edge in-between
					cellAbove.put("BOTTOM", true);
					thisCell.put("TOP", true);
				} else {
					// thisCell.put("TOP",
					// convertIntValToBool(getRandomIntValue(2)-1));
					// cell doesn't exist above therefore this cell is at the
					// edge of the grid
					// if not the corner then have a path (edge) at the edge of
					// the grid to avoid dead-ends
					thisCell.put("TOP", true);
				}

				// then check cell below
				if (cellExistsBelow
						&& cellBelow.get("blockIDIsSet")
						&& cellBelow.get(Integer.toString(compoundBlockID)) != null) {
					// remove the edge between this cell and the cell below
					cellBelow.put("TOP", false);
					thisCell.put("BOTTOM", false);
					// System.out.println("block with block id ( "+compoundBlockID+" ) landing at positon ("+x+","+y+") attached to block below at position ("+x+","+(y+1)+")");
				} else if (cellExistsBelow
						&& cellBelow.get("blockIDIsSet")
						&& cellBelow.get(Integer.toString(compoundBlockID)) == null) {
					// cell below is part of a different compound block so make
					// sure there is an edge in-between
					cellBelow.put("TOP", true);
					thisCell.put("BOTTOM", true);
				} else {
					// cell doesn't exist above therefore this cell is at the
					// edge of the grid - always have a path (edge) at the edge
					// of the grid to avoid deadends
					thisCell.put("BOTTOM", true);
				}

				// Got this far, so set block ID
				thisCell.put(Integer.toString(compoundBlockID), true);
				thisCell.put("blockIDIsSet", true);

				rightHalfOfSimpleModelGrid[x][y] = thisCell;
				numOfAttemptsUsed++;
				numOfBlockPiecesAlreadyDropped++;
				this.numOfCellsInRowOfSimpleGridHalf[y]++;
			}
		}
		// System.exit(0);
		return rightHalfOfSimpleModelGrid;
	}

	private boolean emptyCellExists() {
		for (int y = 0; y < MAX_Y_SIMPLE_MODEL; y++) {
			if (numOfCellsInRowOfSimpleGridHalf[y] < MAX_X_SIMPLE_MODEL_HALF) {
				// No row can have more than 5 cells, and anything less means
				// there is and empty cell
				return true;
			}
		}
		return false;
	}

	private int getMostEmptyRow() {
		int mostEmptyRowIndex = 1000;
		int numOfBlockPiecesInMostEmptyRowSoFar = 1000;
		for (int y = 0; y < MAX_Y_SIMPLE_MODEL; y++) {
			if (numOfCellsInRowOfSimpleGridHalf[y] < numOfBlockPiecesInMostEmptyRowSoFar) {
				mostEmptyRowIndex = y;
				numOfBlockPiecesInMostEmptyRowSoFar = numOfCellsInRowOfSimpleGridHalf[y];
			}
		}

		return mostEmptyRowIndex;
	}

	private HashMap<String, Boolean>[][] mirrorSimpleModelHalfIntoFullSimpleModelGrid(
			HashMap<String, Boolean>[][] rightHalfOfSimpleModelGrid) {
		HashMap<String, Boolean>[][] leftHalfOfSimpleModelGrid = new HashMap[MAX_X_SIMPLE_MODEL_HALF - 1][MAX_Y_SIMPLE_MODEL];

		for (int x = 0; x < MAX_X_SIMPLE_MODEL_HALF - 1; x++) {
			for (int y = 0; y < MAX_Y_SIMPLE_MODEL; y++) {
				HashMap<String, Boolean> cell = new HashMap<String, Boolean>();
				HashMap<String, Boolean> mirrorCell = rightHalfOfSimpleModelGrid[MAX_X_SIMPLE_MODEL_HALF
						- x - 2][y];
				cell.put("TOP", mirrorCell.get("TOP"));
				cell.put("RIGHT", mirrorCell.get("LEFT"));
				cell.put("BOTTOM", mirrorCell.get("BOTTOM"));
				cell.put("LEFT", mirrorCell.get("RIGHT"));
				// cell.put("TOP", false);
				// cell.put("RIGHT", false);
				// cell.put("BOTTOM", false);
				// cell.put("LEFT", false);
				leftHalfOfSimpleModelGrid[x][y] = cell;
			}
		}

		// initialise full simple model grid
		HashMap<String, Boolean>[][] fullSimpleModelGrid = new HashMap[MAX_X_SIMPLE_MODEL][MAX_Y_SIMPLE_MODEL];
		// join left and right grids to make full grid
		for (int y = 0; y < MAX_Y_SIMPLE_MODEL; y++) {
			for (int x = 0; x < MAX_X_SIMPLE_MODEL_HALF - 1; x++) {
				// left half has four columns hence the minus one in the for
				// loop.
				HashMap<String, Boolean> cell = leftHalfOfSimpleModelGrid[x][y];
				fullSimpleModelGrid[x][y] = cell;
			}
			for (int x = 0; x < MAX_X_SIMPLE_MODEL_HALF; x++) {
				// right half has five columns
				HashMap<String, Boolean> cell = rightHalfOfSimpleModelGrid[x][y];
				fullSimpleModelGrid[x + 4][y] = cell;
			}
		}

		return fullSimpleModelGrid;
	}

	private char[][] scaleSimpleModelToFullMaze(
			HashMap<String, Boolean>[][] fullSimpleModelGrid) {
		char[][] maze = new char[MAX_X_FULL_MODEL][MAX_Y_FULL_MODEL];
		int scaleSimpleToFullBy = 3;
		int pathWidth = 1;
		int wallWidth = 2;
		int borderWidth = 1;

		// Tunnels
		int[] tunnelRow = getTunnelRow();
		int firstTunnel;

		if (tunnelRow.length > 1) {
			firstTunnel = tunnelRow[0] * scaleSimpleToFullBy;
		} else {
			firstTunnel = tunnelRow[0];

		}

		// draw top border
		int y = 0;
		for (int x = 0; x < MAX_X_FULL_MODEL; x++) {
			maze[x][y] = '#';
		}

		for (y = 0; y < MAX_Y_SIMPLE_MODEL; y++) {
			// add left border
			int x = 0;
			int startingXPos = (x * scaleSimpleToFullBy);
			int startingYPos = (y * scaleSimpleToFullBy);
			for (int step = 0; step < scaleSimpleToFullBy; step++) {
				// if its a tunnel, then draw a path in border instead of a wall
				if (startingYPos + step == firstTunnel) {
					maze[startingXPos][startingYPos + step + borderWidth] = '*';
				} else {
					maze[startingXPos][startingYPos + step + borderWidth] = '#';
				}
			}

			// draw path
			for (x = 0; x < MAX_X_SIMPLE_MODEL; x++) {
				if (fullSimpleModelGrid[x][y].get("TOP")) {
					startingXPos = (x * scaleSimpleToFullBy);
					startingYPos = (y * scaleSimpleToFullBy);
					for (int step = 0; step < scaleSimpleToFullBy; step++) {
						if (startingXPos + step + borderWidth == MAX_X_FULL_MODEL
								- wallWidth
								&& (startingYPos != firstTunnel)) {
							maze[startingXPos + step + borderWidth][startingYPos
									+ borderWidth] = '#';
						} else {
							maze[startingXPos + step + borderWidth][startingYPos
									+ borderWidth] = '*';
						}
					}
				}
				if (x != 0 && fullSimpleModelGrid[x][y].get("RIGHT")) { // avoid
																		// checking
																		// the
																		// path
																		// (edge)
																		// between
																		// the
																		// first
																		// column
																		// and
																		// the
																		// second
																		// column
																		// more
																		// than
																		// once
					startingXPos = (x * scaleSimpleToFullBy);
					startingYPos = (y * scaleSimpleToFullBy);
					for (int step = 0; step < scaleSimpleToFullBy; step++) {
						maze[startingXPos + wallWidth + pathWidth + borderWidth][startingYPos
								+ step + borderWidth + 1] = '*';
					}
				}
				if (y != 0 && fullSimpleModelGrid[x][y].get("BOTTOM")) { // avoid
																			// checking
																			// the
																			// path
																			// (edge)
																			// between
																			// the
																			// first
																			// row
																			// and
																			// the
																			// second
																			// row
																			// more
																			// than
																			// once
					startingXPos = (x * scaleSimpleToFullBy);
					startingYPos = (y * scaleSimpleToFullBy);
					for (int step = 0; step < scaleSimpleToFullBy; step++) {

						maze[startingXPos + step + borderWidth][startingYPos
								+ wallWidth + pathWidth + borderWidth] = '*';
						// this code below is added to prevent it from creating
						// a tunnel when one shouldn't be created
						if (startingXPos == MAX_X_FULL_MODEL - borderWidth
								- wallWidth - pathWidth) {
							break;
						}
					}
				}
				if (fullSimpleModelGrid[x][y].get("LEFT")) {
					startingXPos = (x * scaleSimpleToFullBy);
					startingYPos = (y * scaleSimpleToFullBy);
					for (int step = 0; step < scaleSimpleToFullBy; step++) {

						maze[startingXPos + borderWidth][startingYPos + step
								+ borderWidth] = '*';
					}
				}
			}

			// add right border
			x = MAX_X_FULL_MODEL - 1;
			startingXPos = x;
			startingYPos = (y * scaleSimpleToFullBy);
			for (int step = 0; step < scaleSimpleToFullBy; step++) {
				// if its a tunnel, then draw a path in border instead of a wall
				if (startingYPos + step == firstTunnel) {
					maze[startingXPos][startingYPos + step + borderWidth] = '*';
				} else {
					maze[startingXPos][startingYPos + step + borderWidth] = '#';
				}
			}
		}

		// draw bottom border
		y = MAX_Y_FULL_MODEL - 1;
		for (int x = 0; x < MAX_X_FULL_MODEL; x++) {
			maze[x][y] = '#';
		}

		return maze;
	}

	private char[][] addArtifactsToMaze(char[][] maze) {

		// Tunnels

		for (int y = 0; y < MAX_Y_FULL_MODEL; y++) {
			for (int x = 0; x < MAX_X_FULL_MODEL; x++) {

				// Add pellets
				if (maze[x][y] == BLANK
						&& (x != 0 || x != MAX_X_FULL_MODEL - 1)
						&& !(x > 9 && x < 17 && y > 9 && y < 22)
				// && ((y != firstTunnel && y != secondTunnel) && (x < 7 || x >
				// 19))
				) {
					maze[x][y] = PELLET;
				}

				// Add walls
				if (maze[x][y] != BLANK && maze[x][y] != DOOR
						&& maze[x][y] != PELLET) {
					maze[x][y] = WALL;
				}
			}
		}

		// Add ghost house
		maze[10][12] = WALL;
		maze[11][12] = WALL;
		maze[12][12] = WALL;
		maze[13][12] = DOOR;
		maze[14][12] = DOOR;
		maze[15][12] = WALL;
		maze[16][12] = WALL;
		maze[17][12] = WALL;

		maze[10][13] = WALL;
		maze[11][13] = IN_HOUSE;
		maze[12][13] = IN_HOUSE;
		maze[13][13] = IN_HOUSE;
		maze[14][13] = IN_HOUSE;
		maze[15][13] = IN_HOUSE;
		maze[16][13] = IN_HOUSE;
		maze[17][13] = WALL;

		maze[10][14] = WALL;
		maze[11][14] = IN_HOUSE;
		maze[12][14] = IN_HOUSE;
		maze[13][14] = IN_HOUSE;
		maze[14][14] = IN_HOUSE;
		maze[15][14] = IN_HOUSE;
		maze[16][14] = IN_HOUSE;
		maze[17][14] = WALL;

		maze[10][15] = WALL;
		maze[11][15] = IN_HOUSE;
		maze[12][15] = IN_HOUSE;
		maze[13][15] = IN_HOUSE;
		maze[14][15] = IN_HOUSE;
		maze[15][15] = IN_HOUSE;
		maze[16][15] = IN_HOUSE;
		maze[17][15] = WALL;

		maze[10][16] = WALL;
		maze[11][16] = WALL;
		maze[12][16] = WALL;
		maze[13][16] = WALL;
		maze[14][16] = WALL;
		maze[15][16] = WALL;
		maze[16][16] = WALL;
		maze[17][16] = WALL;

		// Add exit/entry surrouding path for ghost house
		maze[9][11] = BLANK;
		maze[10][11] = BLANK;
		maze[11][11] = BLANK;
		maze[12][11] = BLANK;
		maze[13][11] = BLANK;
		maze[14][11] = BLANK;
		maze[15][11] = BLANK;
		maze[16][11] = BLANK;
		maze[17][11] = BLANK;
		maze[18][11] = BLANK;

		maze[18][11] = BLANK;
		maze[18][12] = BLANK;
		maze[18][13] = BLANK;
		maze[18][14] = BLANK;
		maze[18][15] = BLANK;
		maze[18][16] = BLANK;
		maze[18][17] = BLANK;

		maze[10][17] = BLANK;
		maze[11][17] = BLANK;
		maze[12][17] = BLANK;
		maze[13][17] = BLANK;
		maze[14][17] = BLANK;
		maze[15][17] = BLANK;
		maze[16][17] = BLANK;
		maze[17][17] = BLANK;
		maze[18][17] = BLANK;

		maze[9][11] = BLANK;
		maze[9][12] = BLANK;
		maze[9][13] = BLANK;
		maze[9][14] = BLANK;
		maze[9][15] = BLANK;
		maze[9][16] = BLANK;
		maze[9][17] = BLANK;

		return maze;
	}

	private char[][] removeDeadEnds(char[][] maze) {
		boolean deadEndWasFound;
		do {
			deadEndWasFound = false;
			for (int y = 0; y < MAX_Y_FULL_MODEL; y++) {
				for (int x = 0; x < MAX_X_FULL_MODEL; x++) {
					// remove any dead ends to the left
					if (x > 1
							&& x < MAX_X_FULL_MODEL - 1
							&& y > 0
							&& y < MAX_Y_FULL_MODEL - 1
							&& (maze[x][y] == BLANK || maze[x][y] == PELLET)
							&& maze[x][y - 1] == WALL
							&& maze[x][y + 1] == WALL
							&& maze[x - 1][y] == WALL
							&& (maze[x + 1][y] == BLANK || maze[x + 1][y] == PELLET)
					// && maze[x][y+2] == BLANK
					) {
						maze[x - 1][y] = maze[x][y];
						deadEndWasFound = true;
					}
					// remove any dead ends below
					if (x > 0
							&& x < MAX_X_FULL_MODEL - 1
							&& y > 0
							&& y < MAX_Y_FULL_MODEL - 1
							&& (maze[x][y] == BLANK || maze[x][y] == PELLET)
							&& maze[x - 1][y] == WALL
							&& maze[x + 1][y] == WALL
							&& maze[x][y - 1] == WALL
							&& (maze[x][y + 1] == BLANK || maze[x][y + 1] == PELLET)
					// && maze[x][y+2] == BLANK
					) {
						maze[x][y - 1] = maze[x][y];
						deadEndWasFound = true;
					}
					// remove any dead ends to the right
					if (x > 0
							&& x < MAX_X_FULL_MODEL - 2
							&& y > 0
							&& y < MAX_Y_FULL_MODEL - 1
							&& (maze[x][y] == BLANK || maze[x][y] == PELLET)
							&& maze[x][y - 1] == WALL
							&& maze[x][y + 1] == WALL
							&& maze[x + 1][y] == WALL
							&& (maze[x - 1][y] == BLANK || maze[x - 1][y] == PELLET)
					// && maze[x][y+2] == BLANK
					) {
						maze[x + 1][y] = maze[x][y];
						deadEndWasFound = true;
					}
					// remove any dead ends above
					if (x > 0
							&& x < MAX_X_FULL_MODEL - 1
							&& y > 0
							&& y < MAX_Y_FULL_MODEL - 2
							&& (maze[x][y] == BLANK || maze[x][y] == PELLET)
							&& maze[x - 1][y] == WALL
							&& maze[x + 1][y] == WALL
							&& (maze[x][y - 1] == BLANK || maze[x][y - 1] == PELLET)
							&& maze[x][y + 1] == WALL
					// && maze[x][y-2] == BLANK
					) {
						maze[x][y + 1] = maze[x][y];
						deadEndWasFound = true;
					}
					// remove any dead ends at the far right and left edges of
					// the maze
					if ((x == 1 || x == MAX_X_FULL_MODEL - 2)
							&& y > 0
							&& y < MAX_Y_FULL_MODEL - 2
							&& (maze[x][y] == BLANK || maze[x][y] == PELLET)
							&& maze[x][y - 1] == WALL
							&& maze[x][y + 1] == WALL
							&& (((maze[x + 1][y] == BLANK || maze[x + 1][y] == PELLET) && maze[x - 1][y] == WALL) || (maze[x + 1][y] == WALL && (maze[x - 1][y] == BLANK || maze[x - 1][y] == PELLET)))
					// && maze[x][y-2] == BLANK
					) {
						// clear up the maze if at the lower part of the map
						if (y > MAX_Y_FULL_MODEL / 2) {
							maze[x][y - 1] = maze[x][y];
						}
						// clear down the maze if at the upper part of the map
						else if (y < MAX_Y_FULL_MODEL / 2) {
							maze[x][y + 1] = maze[x][y];
						} else {
							maze[x][y + 1] = maze[x][y];
						}
						deadEndWasFound = true;
					}

				}
			}
		} while (deadEndWasFound);
		return maze;
	}

	private char[][] moveBlockPiecesInProtectedCells(char[][] maze) {

		for (int y = 0; y < MAX_Y_FULL_MODEL; y++) {
			for (int x = 0; x < MAX_X_FULL_MODEL; x++) {

				// Move nearest path to ghost house closer to become exit path
				// from ghost house
				if (y == 10 && (maze[x][y] == PELLET || maze[x][y] == BLANK)) {
					maze[x][y + 1] = maze[x][y];
					maze[x][y] = WALL;
				}

				// If nearest path to the left is too close to house, move
				// further to the left
				if (y > 10 && y < 20 && x == 7 && maze[x - 1][y] == WALL
						&& maze[x - 2][y] == WALL) {
					maze[x - 1][y] = maze[x][y]; // translate left
					maze[x][y] = WALL;
				}

				// Clean up what is left from the previous move
				if (y > 10 && y < 20 && x == 7 && maze[x - 1][y] == WALL
						&& maze[x + 1][y] == WALL && maze[x + 2][y] == WALL
						&& maze[x][y + 1] == WALL && maze[x][y - 1] == WALL) {
					maze[x][y] = WALL; // clean up what is left from translating
										// left
				}
				// More cleaning up what is left from the previous move
				if (y > 10 && y < 20 && x == 8 && maze[x - 1][y] == WALL
						&& (maze[x + 1][y] == PELLET | maze[x + 1][y] == BLANK)
						&& maze[x][y + 1] == WALL && maze[x][y - 1] == WALL) {
					maze[x][y] = WALL; // clean up what is left from translating
										// left
				}
				// If nearest path to the right is too close to house, move
				// further to the right
				if (y > 10 && y < 20 && x == 19 && maze[x + 1][y] == WALL
						&& maze[x + 2][y] == WALL) {
					maze[x + 1][y] = maze[x][y]; // translate right
					maze[x][y] = WALL;
				}

				// Fix up a path for PACMAN'S position.
			}
		}

		return maze;
	}

	private int[] getTunnelRow() {
		int firstPotentialTunnelRow = 0;
		int secondPotentialTunnelRow = 0;
		int thirdPotentialTunnelRow = 0;
		int totalPotentialTunnelRowsFound = 0;

		for (int y = 0; y < MAX_Y_SIMPLE_MODEL; y++) {
			if (fullSimpleModelGrid[MAX_X_SIMPLE_MODEL - 1][y].get("TOP")) {
				totalPotentialTunnelRowsFound++;
				if (firstPotentialTunnelRow == 0) {
					firstPotentialTunnelRow = y;
				} else if (secondPotentialTunnelRow == 0) {
					secondPotentialTunnelRow = y;
				} else {
					thirdPotentialTunnelRow = y;
				}
			}
		}

		int[] rowsArray = new int[Math.min(totalPotentialTunnelRowsFound, 2)];

		if (totalPotentialTunnelRowsFound == 1) {
			rowsArray = new int[1];
			rowsArray[0] = firstPotentialTunnelRow;
		} else if (totalPotentialTunnelRowsFound > 2) {
			rowsArray = new int[2];
			rowsArray[0] = secondPotentialTunnelRow;
			if (convertIntValToBool(getRandomIntValue(2) - 1)) {
				rowsArray[1] = firstPotentialTunnelRow;
			} else {
				rowsArray[1] = thirdPotentialTunnelRow;
			}
		} else {
			rowsArray = new int[2];
			rowsArray[1] = firstPotentialTunnelRow;
			rowsArray[0] = secondPotentialTunnelRow;
		}

		return rowsArray;
	}

	public int getTunnelY(char[][] maze) {
		int y;
		for (y = 0; maze[0][y] != PELLET; y++) {
		}
		maze[0][y] = ' ';
		maze[27][y] = ' ';
		return y;
	}

	private int getRandomIntValue(int maxInt) {

		// Usually this can be a field rather than a method variable
		Random rand = new Random();

		int randomNum = rand.nextInt(maxInt) + 1;
		return randomNum;
	}

	// finds a space with a non-wall then puts an E-pellet in it's place
	private char[][] generateEnergizers(char[][] maze) {
		Random rand = new Random();
		int x;
		int y;
		boolean retry;
		do {
			x = rand.nextInt(6) + 1;
			y = rand.nextInt(8) + 1;
			if (maze[x][y] != PELLET
					|| maze[MAX_X_FULL_MODEL - 2 - x][y] != PELLET) {
				retry = true;
			} else {
				maze[x][y] = ENERGIZER_PELLET;
				maze[MAX_X_FULL_MODEL - 2 - x][y] = ENERGIZER_PELLET;
				retry = false;
			}
		} while (retry);
		do {
			x = rand.nextInt(6) + 1;
			y = rand.nextInt(8) + 20;
			if (maze[x][y] != PELLET
					|| maze[MAX_X_FULL_MODEL - 2 - x][y] != PELLET) {
				retry = true;
			} else {
				maze[x][y] = ENERGIZER_PELLET;
				maze[MAX_X_FULL_MODEL - 2 - x][y] = ENERGIZER_PELLET;
				retry = false;
			}
		} while (retry);
		return maze;
	}

	private static boolean convertIntValToBool(int value) {
		if (value > 0) {
			return true;
		} else {
			return false;
		}
	}

}