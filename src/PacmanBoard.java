//package Pacman;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PacmanBoard extends JPanel {

	private static final long serialVersionUID = 1L;
	// Blocksize is the size of the tiles on the board.
	private static final char WALL = '#';
	private static final char PELLET = 'o';
	private static final char ENERGIZER_PELLET = 'O';
	private static final char FRUIT = 'F';
	private static final char VISITED_TILE = ' ';
	private static final char DOOR = '=';
	private static final int WALL_WIDTH = 4;

	private static final int PELLET_TILE_OFFSET = 8;
	private static final int ENERGIZER_PELLET_TILE_OFFSET = 5;
	private static final int BLOCKSIZE = 20;
	private static final int INDEX_ZERO = 0;
	private static final int GHOST_SCORE_TIME = GameProcessing.FRAMES_PER_SECOND;
	private static final int VERTICAL_OFFSET = 2 * BLOCKSIZE;
	private static final int MAX_X = 28, MAX_Y = 31;
	// objects
	private Score score;
	private Pacman pacman;
	private Blinky blinky;
	private Pinky pinky;
	private Inky inky;
	private Clyde clyde;
	GameProcessing gameProcessor;

	private ConsumableSpriteGenerator spriteGenerator = new ConsumableSpriteGenerator();
	private GameKeyListener listener;
	private char[][] gameMap;

	private URL titleURL;
	ImageIcon titleImage;

	private File file = new File("highScore.txt");
	private int highScore[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	// Game state variables
	boolean levelDone;
	private int level = 1;
	boolean flashLevel = false;
	Color[] wallColours = { new Color(0, 128, 248), new Color(219, 114, 207),
			new Color(137, 219, 90), new Color(235, 125, 70),
			new Color(171, 94, 221), Color.gray, new Color(209, 104, 232),
			new Color(226, 232, 104) };
	private int colourCounter = 0;
	private Image pelletSprite;
	private Image energizerSprite;
	private Image fruitSprite;
	Image[] deathImages = new Image[14];

	private int animationFrameCounter = 0;

	private boolean energizerPelletFlash = true;
	private int energizerPelletFlashCounter = 10;

	private boolean paint200,paint400,paint800,paint1600;
	private int ghost200X,ghost200Y,ghost400X,ghost400Y,ghost800X,ghost800Y,ghost1600X,ghost1600Y;
	private int counter200;
	private int counter400;
	private int counter800;
	private int counter1600;

	private Image bannerRightSprite;
	private Image ghostIntroScreen;
	private int scoreLabel = 0;
	private int counterBonus= 0;
	private boolean paintBonusScore= false;

	public PacmanBoard(GameKeyListener listener) {

		this.listener = listener;
		addKeyListener(this.listener);
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		
		gameProcessor = new GameProcessing(listener, this);

		loadDeathSprites();

		titleURL = getClass().getResource("res/sprites/titleScreen.png");
		titleImage = new ImageIcon(titleURL);

		pelletSprite = spriteGenerator.getImage("PELLET", level);
		energizerSprite = spriteGenerator.getImage("ENERGIZER", level);
		fruitSprite = spriteGenerator.getImage("FRUIT", level);
		bannerRightSprite = new ImageIcon(getClass().getResource(
				"res/sprites/pacRightBanner2.png")).getImage();
		ghostIntroScreen = new ImageIcon(getClass().getResource(
				"res/sprites/ghostIntroScreen.png")).getImage();
		readInHighScores();
	}
	/**
	 * @author Liam Fraser
	 *         <p>
	 *        Loads the sprites used for the death animation into memory
	 *         </p>
	 */
	private void loadDeathSprites() {

		// if these were stored in an array, indexed by animationFrameCounter,
		// this would be a lot nicer
		deathImages[1] = (new ImageIcon(getClass().getResource(
				"res/sprites/death1.png"))).getImage();
		deathImages[2] = (new ImageIcon(getClass().getResource(
				"res/sprites/death2.png"))).getImage();
		deathImages[3] = (new ImageIcon(getClass().getResource(
				"res/sprites/death3.png"))).getImage();
		deathImages[4] = (new ImageIcon(getClass().getResource(
				"res/sprites/death4.png"))).getImage();
		deathImages[5] = (new ImageIcon(getClass().getResource(
				"res/sprites/death5.png"))).getImage();
		deathImages[6] = (new ImageIcon(getClass().getResource(
				"res/sprites/death6.png"))).getImage();
		deathImages[7] = (new ImageIcon(getClass().getResource(
				"res/sprites/death7.png"))).getImage();
		deathImages[8] = (new ImageIcon(getClass().getResource(
				"res/sprites/death8.png"))).getImage();
		deathImages[9] = (new ImageIcon(getClass().getResource(
				"res/sprites/death9.png"))).getImage();
		deathImages[10] = (new ImageIcon(getClass().getResource(
				"res/sprites/death10.png"))).getImage();
		deathImages[13]=deathImages[12]=deathImages[11] = (new ImageIcon(getClass().getResource(
				"res/sprites/death11.png"))).getImage();
	}
	/**
	 * @author Liam Fraser
	 *         <p>
	 *        Controls what items need to be painted based on game state and displays them on the screen.
	 *         </p>
	 */
	public void paint(Graphics g) {

		super.paint(g);

		Graphics2D painter = (Graphics2D) g;
		painter.drawImage(ghostIntroScreen, MAX_X * BLOCKSIZE
				+ (10 * BLOCKSIZE), 100, this);
		if (pacman.getOnTitleScreen()) {
			painter.drawImage(titleImage.getImage(), 75, 0, MAX_X * BLOCKSIZE,
					MAX_Y * BLOCKSIZE, this);
			Font font = new Font("Courier New", Font.BOLD, 20);
			g.setFont(font);
			Color originalColour = g.getColor();
			g.setColor(Color.RED);
			g.drawString("Press any key to play a random maze", ((MAX_X * BLOCKSIZE)/2)-120, MAX_Y * BLOCKSIZE);
			g.drawString("Press 'c' for the classic maze", ((MAX_X * BLOCKSIZE)/2)-90, MAX_Y * BLOCKSIZE+30);
			g.setColor(originalColour);
		} else {
			drawGameMap(painter);
			updateScore(g);
			updateLives(g);
			if (!gameProcessor.getGameOver()) {
				painter.drawImage(bannerRightSprite, MAX_X * BLOCKSIZE, 0, 200,
						700, this);
				if (!gameProcessor.getIsPacmanDead()) {
					animationFrameCounter = 0;
					painter.drawImage(pacman.getImage(), pacman.getX(),
							pacman.getY() + VERTICAL_OFFSET, BLOCKSIZE,
							BLOCKSIZE, this);
					painter.drawImage(blinky.getImage(), blinky.getX(),
							blinky.getY() + VERTICAL_OFFSET, BLOCKSIZE,
							BLOCKSIZE, this);
					painter.drawImage(pinky.getImage(), pinky.getX(),
							pinky.getY() + VERTICAL_OFFSET, BLOCKSIZE,
							BLOCKSIZE, this);
					painter.drawImage(inky.getImage(), inky.getX(), inky.getY()
							+ VERTICAL_OFFSET, BLOCKSIZE, BLOCKSIZE, this);
					painter.drawImage(clyde.getImage(), clyde.getX(),
							clyde.getY() + VERTICAL_OFFSET, BLOCKSIZE,
							BLOCKSIZE, this);
				} else if (animationFrameCounter == 0) {
					painter.drawImage(pacman.getFullImage(), pacman.getX(),
							pacman.getY() + VERTICAL_OFFSET, BLOCKSIZE,
							BLOCKSIZE, this);
				}
				if (gameProcessor.getIsPacmanDead()) {
					paintDeathAnimation(painter);
					animationFrameCounter++;
				}
				paintLevelLabel(g);
				updateScore(g);
				updateLives(g);
				paintBonusScoreLabels(g);
				painter.drawImage(bannerRightSprite, MAX_X * BLOCKSIZE, 0, 200,
						700, this);

				if (gameProcessor.needsToWait()) {
					paintReady(g);
				}
			} else if (gameProcessor.isOnHighScoreScreen()) {
				highScoreScreen(g);
				painter.drawImage(bannerRightSprite, MAX_X * BLOCKSIZE, 0, 200,
						700, this);
			} else {
				paintGameOver(g);
				painter.drawImage(bannerRightSprite, MAX_X * BLOCKSIZE, 0, 200,
						700, this);
			}
		}
	}
	/**
	 *         <p>
	 *        Paints the the level label onto the screen below the game map.
	 *         </p>
	 */
	private void paintLevelLabel(Graphics g) {
		Font font = new Font("Courier New", Font.BOLD, 24);
		g.setFont(font);
		Color originalColour = g.getColor();
		g.setColor(Color.WHITE);
		g.drawString("Level ", 12 * BLOCKSIZE, 32 * BLOCKSIZE + VERTICAL_OFFSET);
		g.setColor(Color.YELLOW);
		g.drawString("" + level, 16 * BLOCKSIZE, 32 * BLOCKSIZE
				+ VERTICAL_OFFSET);
		g.setColor(originalColour);
	}

	/**
	 * @author Matt Adicott : Method to convert a block Index to a screen
	 *         co-ordinate
	 * @param arrayPos
	 *            : The block index to be converted
	 * @return int : the converted screen position
	 *
	 */
	private int toScreenPosition(int arrayPos) {
		return arrayPos * BLOCKSIZE;
	}
	/**
	 *         <p>
	 *        Draws the high score table onto the screen
	 *         </p>
	 */
	private void highScoreScreen(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, MAX_X * BLOCKSIZE, MAX_Y * BLOCKSIZE + VERTICAL_OFFSET);
		Font font = new Font("Courier New", Font.BOLD, 24);
		g.setFont(font);
		Color myColor = new Color(232, 54, 238);
		g.setColor(myColor);
		g.drawString("Hi-Scores", (14 * BLOCKSIZE) - 50, 100);
		myColor = new Color(148, 0, 211);
		g.setColor(myColor);
		g.drawString("RANK", 11 * BLOCKSIZE - 40, 130);
		g.drawString("SCORE", (14 * BLOCKSIZE) + 50, 130);
		myColor = new Color(169, 27, 126);
		g.setColor(myColor);
		g.drawString("1ST", 11 * BLOCKSIZE - 40, 160);
		g.drawString("" + highScore[0], (14 * BLOCKSIZE) + 50, 160);
		myColor = new Color(190, 35, 104);
		g.setColor(myColor);
		g.drawString("2ND", 11 * BLOCKSIZE - 40, 190);
		g.drawString("" + highScore[1], (14 * BLOCKSIZE) + 50, 190);
		myColor = new Color(235, 37, 36);
		g.setColor(myColor);
		g.drawString("3RD", 11 * BLOCKSIZE - 40, 220);
		g.drawString("" + highScore[2], (14 * BLOCKSIZE) + 50, 220);
		myColor = new Color(242, 126, 34);
		g.setColor(myColor);
		g.drawString("4TH", 11 * BLOCKSIZE - 40, 250);
		g.drawString("" + highScore[3], (14 * BLOCKSIZE) + 50, 250);
		myColor = new Color(255, 237, 29);
		g.setColor(myColor);
		g.drawString("5TH", 11 * BLOCKSIZE - 40, 280);
		g.drawString("" + highScore[4], (14 * BLOCKSIZE) + 50, 280);
		myColor = new Color(115, 229, 11);
		g.setColor(myColor);
		g.drawString("6TH", 11 * BLOCKSIZE - 40, 310);
		g.drawString("" + highScore[5], (14 * BLOCKSIZE) + 50, 310);
		myColor = new Color(66, 178, 26);
		g.setColor(myColor);
		g.drawString("7TH", 11 * BLOCKSIZE - 40, 340);
		g.drawString("" + highScore[6], (14 * BLOCKSIZE) + 50, 340);
		myColor = new Color(80, 242, 248);
		g.setColor(myColor);
		g.drawString("8TH", 11 * BLOCKSIZE - 40, 370);
		g.drawString("" + highScore[7], (14 * BLOCKSIZE) + 50, 370);
		myColor = new Color(45, 162, 252);
		g.setColor(myColor);
		g.drawString("9TH", 11 * BLOCKSIZE - 40, 400);
		g.drawString("" + highScore[8], (14 * BLOCKSIZE) + 50, 400);
		myColor = new Color(14, 85, 230);
		g.setColor(myColor);
		g.drawString("10TH", 11 * BLOCKSIZE - 40, 430);
		g.drawString("" + highScore[9], (14 * BLOCKSIZE) + 50, 430);
	}	

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Method to cause the level to flash 6 times once it has been
	 *         completed.Also used to adjust what colour the next maze will be
	 *         by changing the colourCounter index.
	 *         </p>
	 */
	public void flashLevel() {
		for (int i = 6; i > 0; i--) {
			flashLevel = !flashLevel;
			repaint();
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (colourCounter < 7) {
			colourCounter++;
		} else {
			colourCounter = 0;
		}
	}

	/**
	 * @author Liam Fraser
	 * @param Graphics2D
	 *            object: Painter
	 *            <p>
	 *            Animates the death sprites for pacman. Switches based on an
	 *            animationFrameCounter to give the illusion of fluid animation.
	 *            </p>
	 */
	private void paintDeathAnimation(Graphics2D painter) {
		
		if(animationFrameCounter <14){
			painter.drawImage(deathImages[animationFrameCounter], pacman.getX(), pacman.getY()
					+ VERTICAL_OFFSET, BLOCKSIZE, BLOCKSIZE, this);
		}else{
			animationFrameCounter = 0;
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep(1534 / 13);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Draws the text label "ready" just below the ghost house.
	 *         </p>
	 */
	private void paintReady(Graphics g) {
		Font font = new Font("Courier New", Font.BOLD, 24);
		g.setFont(font);
		Color originalColour = g.getColor();
		g.setColor(Color.ORANGE);
		g.drawString("READY!", 12 * BLOCKSIZE, 20 * BLOCKSIZE);
		g.setColor(originalColour);
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Draws the text label "GAME OVER" just below the ghost house.
	 *         </p>
	 */
	private void paintGameOver(Graphics g) {
		Font font = new Font("Courier New", Font.BOLD, 24);
		g.setFont(font);
		Color originalColour = g.getColor();
		g.setColor(Color.RED);
		g.drawString("GAME OVER", 11 * BLOCKSIZE, 20 * BLOCKSIZE);
		g.setColor(originalColour);
	}

	/**
	 * @author Liam Fraser
	 * @param Graphics2d
	 *            painter
	 *            <p>
	 *            Displays the game map on the screen. This is done by iterating
	 *            through the gameMap array, and painting in different ways,
	 *            depending on the character stored at the current position
	 *            </p>
	 */
	private void drawGameMap(Graphics2D painter) {
		levelDone = true;
		// Iterates over array and paints all elements to the game board.
		for (int posY = INDEX_ZERO; posY < MAX_Y; posY++) {
			for (int posX = INDEX_ZERO; posX < MAX_X; posX++) {
				switch (gameMap[posX][posY]) {

				case WALL:
					if (flashLevel) {
						painter.setColor(Color.WHITE);
					} else {
						painter.setColor(wallColours[colourCounter]);

					}

					drawWall(painter, posX, posY);
					break;
				case PELLET:
					levelDone = false;
					painter.setColor(Color.BLACK);
					drawWall(painter, posX, posY);
					// Paints spherical pellets over the background.
					painter.drawImage(pelletSprite, posX * BLOCKSIZE
							+ PELLET_TILE_OFFSET, posY * BLOCKSIZE
							+ VERTICAL_OFFSET + PELLET_TILE_OFFSET, this);
					break;
				case DOOR:
					painter.setColor(Color.PINK);
					drawWall(painter, posX, posY);
					break;
				case VISITED_TILE:
					painter.setColor(Color.BLACK);
					drawWall(painter, posX, posY);
					break;
				case ENERGIZER_PELLET:

					painter.setColor(Color.BLACK);
					drawWall(painter, posX, posY);
					if (energizerPelletFlash) {
						painter.drawImage(energizerSprite, posX * BLOCKSIZE
								+ ENERGIZER_PELLET_TILE_OFFSET, posY
								* BLOCKSIZE + VERTICAL_OFFSET
								+ ENERGIZER_PELLET_TILE_OFFSET, this);
					}
					break;
				case FRUIT:
					painter.setColor(Color.BLACK);
					drawWall(painter, posX, posY);
					painter.drawImage(fruitSprite, posX * BLOCKSIZE
							+ ENERGIZER_PELLET_TILE_OFFSET, posY * BLOCKSIZE
							+ VERTICAL_OFFSET + ENERGIZER_PELLET_TILE_OFFSET,BLOCKSIZE,BLOCKSIZE,
							this);
					break;
				default:
					// all other characters are for hidden functionality, such as
					// inside the ghostHouse or slowing down in tunnels so need
					// to be invisible
					painter.setColor(Color.BLACK);
					drawWall(painter, posX, posY);
					break;
				}
			}
		}
		if (energizerPelletFlashCounter == 0) {
			energizerPelletFlash = !energizerPelletFlash;
			energizerPelletFlashCounter = GameProcessing.FRAMES_PER_SECOND/10;
		} else {
			energizerPelletFlashCounter--;
		}
	}

	/**
	 * @author Liam Fraser Method to paint a block
	 *         <p>
	 *         Used to paint walls (and doors). Performs a check to see if the
	 *         block to be painted is bordering with any non-Walls, and paints
	 *         the wall against the corresponding block edge if it is. Next it
	 *         checks to see if the block is on a corner of a walled section. If
	 *         it is, it removes pixels from the corner of the block and draws a
	 *         diagonal, to make the wall Seem curved.
	 *         </p>
	 *
	 * @param painter
	 *            , posX, posY
	 *
	 */
	private void drawWall(Graphics2D painter, int posX, int posY) {
		int screenX = toScreenPosition(posX);
		int screenY = toScreenPosition(posY) + VERTICAL_OFFSET;
		Color wallColour = painter.getColor();

		if (posX > 0 && gameMap[posX - 1][posY] != WALL) {
			painter.fillRect(screenX, screenY, WALL_WIDTH, BLOCKSIZE);
		}
		if (posX < MAX_X - 1 && gameMap[posX + 1][posY] != WALL) {
			painter.fillRect(screenX + (BLOCKSIZE - WALL_WIDTH), screenY,
					WALL_WIDTH, BLOCKSIZE);
		}
		if (posY > 0 && gameMap[posX][posY - 1] != WALL) {
			painter.fillRect(screenX, screenY, BLOCKSIZE, WALL_WIDTH);
		}
		if (posY < MAX_Y - 1 && gameMap[posX][posY + 1] != WALL) {
			painter.fillRect(screenX, screenY + (BLOCKSIZE - WALL_WIDTH),
					BLOCKSIZE, WALL_WIDTH);
		}

		if (posX > 0 && posY > 0 && posX < MAX_X - 1 && posY < MAX_Y - 1) {
			if (gameMap[posX - 1][posY] != WALL
					&& gameMap[posX][posY - 1] != WALL) {
				// blank in top left corner
				painter.setColor(Color.BLACK);
				painter.fillRect(screenX, screenY, WALL_WIDTH, WALL_WIDTH);
				painter.setColor(wallColour);
				for (int i = 1; i <= WALL_WIDTH; i++) {
					painter.fillRect(screenX + WALL_WIDTH - i, screenY + i - 1,
							i, 1);
				}

			}
			if (gameMap[posX + 1][posY] != WALL
					&& gameMap[posX][posY - 1] != WALL) {
				// blank in top right corner
				painter.setColor(Color.BLACK);
				painter.fillRect(screenX + (BLOCKSIZE - WALL_WIDTH), screenY,
						WALL_WIDTH, WALL_WIDTH);
				painter.setColor(wallColour);
				for (int i = 1; i <= WALL_WIDTH; i++) {
					painter.fillRect(screenX + (BLOCKSIZE - WALL_WIDTH),
							screenY + i - 1, i, 1);
				}
			}
			if (gameMap[posX + 1][posY] != WALL
					&& gameMap[posX][posY + 1] != WALL) {
				// blank in bottom right corner

				painter.setColor(Color.BLACK);
				painter.fillRect(screenX + (BLOCKSIZE - WALL_WIDTH), screenY
						+ (BLOCKSIZE - WALL_WIDTH), WALL_WIDTH, WALL_WIDTH);
				painter.setColor(wallColour);
				int j = 0;
				for (int i = WALL_WIDTH; i >= 0; i--) {
					painter.fillRect(screenX + (BLOCKSIZE - WALL_WIDTH),
							screenY + (BLOCKSIZE - WALL_WIDTH) + j, i, 1);
					j++;
				}
			}
			if (gameMap[posX - 1][posY] != WALL
					&& gameMap[posX][posY + 1] != WALL) {
				// blank in bottom Left corner
				painter.setColor(Color.BLACK);
				painter.fillRect(screenX, screenY + (BLOCKSIZE - WALL_WIDTH),
						WALL_WIDTH, WALL_WIDTH);
				painter.setColor(wallColour);
				int j = 0;
				for (int i = WALL_WIDTH; i >= 0; i--) {
					painter.fillRect(screenX + j, screenY
							+ (BLOCKSIZE - WALL_WIDTH) + j, i, 1);
					j++;
				}
			}
		} else {
			if (posX == 0) {
				painter.fillRect(screenX, screenY, WALL_WIDTH, BLOCKSIZE);
			}
			if (posY == 0) {
				painter.fillRect(screenX, screenY, BLOCKSIZE, WALL_WIDTH);
			}
			if (posX == MAX_X - 1) {
				painter.fillRect(screenX + (BLOCKSIZE - WALL_WIDTH), screenY,
						WALL_WIDTH, BLOCKSIZE);
			}
			if (posY == MAX_Y - 1) {
				painter.fillRect(screenX, screenY + (BLOCKSIZE - WALL_WIDTH),
						BLOCKSIZE, WALL_WIDTH);
			}
		}
		if (gameMap[posX][posY] == DOOR) {
			painter.setColor(Color.BLACK);
			painter.fillRect(screenX, screenY, BLOCKSIZE, WALL_WIDTH);
			painter.setColor(Color.PINK);
			painter.fillRect(screenX, screenY + WALL_WIDTH, BLOCKSIZE,
					BLOCKSIZE - (2 * WALL_WIDTH));
			painter.setColor(Color.BLACK);
			painter.fillRect(screenX, screenY + BLOCKSIZE - WALL_WIDTH,
					BLOCKSIZE, WALL_WIDTH);
		}
		if (posY < MAX_Y - 1 && posY > 0 && posX < MAX_X - 1 && posX > 0
				&& gameMap[posX][posY - 1] == WALL
				&& gameMap[posX - 1][posY] == WALL
				&& gameMap[posX][posY] != WALL) {
			// smooth out corner in up/left junction
			painter.setColor(wallColours[colourCounter]);
			for (int i = 1; i <= WALL_WIDTH; i++) {
				painter.fillRect(screenX - i, screenY - WALL_WIDTH + i - 1, i,
						1);
			}
		}
		if (posY < MAX_Y - 1 && posY > 0 && posX < MAX_X - 1 && posX > 0
				&& gameMap[posX][posY + 1] == WALL
				&& gameMap[posX + 1][posY] == WALL
				&& gameMap[posX][posY] != WALL) {
			// smooth out corner in bottom/right junction
			painter.setColor(wallColours[colourCounter]);
			int j = 0;
			for (int i = WALL_WIDTH; i >= 0; i--) {
				painter.fillRect(screenX + (BLOCKSIZE), screenY + (BLOCKSIZE)
						+ j, i, 1);
				j++;
			}
		}

		if (posY < MAX_Y - 1 && posY > 0 && posX < MAX_X - 1 && posX > 0
				&& gameMap[posX][posY - 1] == WALL
				&& gameMap[posX + 1][posY] == WALL
				&& gameMap[posX][posY] != WALL) {
			// smooth out corner in top/right wall junction
			painter.setColor(wallColours[colourCounter]);
			for (int i = 1; i <= WALL_WIDTH; i++) {
				painter.fillRect(screenX + (BLOCKSIZE), screenY - WALL_WIDTH
						+ i - 1, i, 1);
			}
		}
		if (posY < MAX_Y - 1 && posY > 0 && posX < MAX_X - 1 && posX > 0
				&& gameMap[posX][posY + 1] == WALL
				&& gameMap[posX - 1][posY] == WALL
				&& gameMap[posX][posY] != WALL) {
			// smooth out corner in bottom/left wall junction
			painter.setColor(wallColours[colourCounter]);
			int j = 0;
			for (int i = WALL_WIDTH; i >= 0; i--) {
				painter.fillRect(screenX + j - WALL_WIDTH, screenY + BLOCKSIZE
						+ j, i, 1);
				j++;
			}
		}
	}

	/**
	 * @author Liam Fraser
	 * @param Graphics
	 *            g
	 *            <p>
	 *            Displays the score and high-score values and labels. The
	 *            High-score value is the maximum of the current high score and
	 *            the user's score.
	 *            </p>
	 */
	private void updateScore(Graphics g) {
		Font font = new Font("Courier New", Font.BOLD, 24);
		g.setFont(font);
		Color originalColour = g.getColor();
		g.setColor(Color.WHITE);
		g.drawString("Score ", 25, 32 * BLOCKSIZE + VERTICAL_OFFSET);
		g.drawString("Hi-Score ", 8 * BLOCKSIZE, 25);
		g.setColor(Color.YELLOW);
		g.drawString("" + Math.max(highScore[0], score.getScore()),
				15 * BLOCKSIZE, 25);
		g.drawString("" + score.getScore(), 100, 32 * BLOCKSIZE
				+ VERTICAL_OFFSET);
		g.setColor(originalColour);
	}

	/**
	 * @author Liam Fraser
	 * @param Graphics
	 *            g
	 *            <p>
	 *            Displays the appropriate amount of pacman heads below the map.
	 *            One for each life that is left.
	 *            </p>
	 */
	private void updateLives(Graphics g) {

		if (pacman.getLifeCount() > 0) {
			g.drawImage(pacman.getLifeImage(), 27 * BLOCKSIZE, 31 * BLOCKSIZE
					+ VERTICAL_OFFSET + 10, this);

		}
		if (pacman.getLifeCount() > 1) {
			g.drawImage(pacman.getLifeImage(), 26 * BLOCKSIZE, 31 * BLOCKSIZE
					+ VERTICAL_OFFSET + 10, this);

		}
		if (pacman.getLifeCount() > 2) {
			g.drawImage(pacman.getLifeImage(), 25 * BLOCKSIZE, 31 * BLOCKSIZE
					+ VERTICAL_OFFSET + 10, this);

		}
		if (pacman.getLifeCount() > 3) {
			g.drawImage(pacman.getLifeImage(), 24 * BLOCKSIZE, 31 * BLOCKSIZE
					+ VERTICAL_OFFSET + 10, this);

		}
		if (pacman.getLifeCount() > 4) {
			g.drawImage(pacman.getLifeImage(), 23 * BLOCKSIZE, 31 * BLOCKSIZE
					+ VERTICAL_OFFSET + 10, this);

		}
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         reads in the high scores from the stored text document and stores
	 *         them in an array
	 *         </p>
	 */
	private void readInHighScores() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
			int listScore = 0;
			int count = 0;
			for (int i = 0; i < 10; i++) {
				listScore = scanner.nextInt();
				if (listScore != 0) {
					highScore[count] = listScore;
					count++;
					listScore = 0;
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("Error reading from Highscores text document");
		}
		scanner.close();
	}

	public void writeHighScore() throws IOException {
		for (int j = 0; j < 10; j++) {
			if (score.getScore() >= highScore[j]) {
				for (int j2 = 9; j2 > j; j2--) {
					highScore[j2] = highScore[j2 - 1];
				}
				highScore[j] = score.getScore();
				break;
			}

		}

		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < 10; i++) {
			outputWriter.write(Integer.toString(highScore[i]) + " ");
		}
		outputWriter.flush();
		outputWriter.close();
	}

	public void setLevel(int level) {
		this.level = level;
		fruitSprite = spriteGenerator.getImage("FRUIT", level);
	}

	public void setMaze(char[][] gameMap) {
		this.gameMap = gameMap;
	}

	public int getAnimationFrameCounter() {
		return animationFrameCounter;
	}

	public void tickFrame() {
		gameProcessor.tickFrame();

	}

	public void setBlinky(Blinky blinky) {
		this.blinky = blinky;

	}

	public void setPacman(Pacman pacman) {
		this.pacman = pacman;

	}

	public void setInky(Inky inky) {
		this.inky = inky;

	}

	public void setPinky(Pinky pinky) {
		this.pinky = pinky;

	}

	public void setClyde(Clyde clyde) {
		this.clyde = clyde;

	}

	public void setScore(Score score) {
		this.score = score;
		readInHighScores();

	}

	/**
	 * @author Liam Fraser
	 * @param :value to be displayed, position X position Y
	 *        <p>
	 *        Flips a boolean and initialises a counter, depending on the score
	 *        value handed to the function. This allows paintGhostScoreLabel to
	 *        display the correct value on screen at the position the dead ghost
	 *        was eaten
	 *        </p>
	 */
	public void setGhostScoreLabel(int value, int posX, int posY) {
		switch (value) {
		case 200:
			paint200 = true;
			ghost200X = posX;
			ghost200Y = posY + 8 + VERTICAL_OFFSET;
			counter200 = GHOST_SCORE_TIME;
			break;
		case 400:
			paint400 = true;
			ghost400X = posX;
			ghost400Y = posY + 8 + VERTICAL_OFFSET;
			counter400 = GHOST_SCORE_TIME;
			break;
		case 800:
			paint800 = true;
			ghost800X = posX;
			ghost800Y = posY + 8 + VERTICAL_OFFSET;
			counter800 = GHOST_SCORE_TIME;
			break;
		case 1600:
			paint1600 = true;
			ghost1600X = posX;
			ghost1600Y = posY + 8 + VERTICAL_OFFSET;
			counter1600 = GHOST_SCORE_TIME;
		}
	}
public void setBonusScoreLabel(int value){
	scoreLabel = value;
	counterBonus = GHOST_SCORE_TIME;
	paintBonusScore = true;
	
}
	/**
	 * @author Liam Fraser
	 * @param Graphics
	 *            g
	 *            <p>
	 *            Paints the score labels for when a consumable is eaten onto the
	 *            screen, for the duration required.
	 *            </p>
	 */
	private void paintBonusScoreLabels(Graphics g) {
		Font font = new Font("Courier New", Font.BOLD, 15);
		g.setFont(font);
		Color originalColour = g.getColor();
		g.setColor(Color.CYAN);
		if (paint200 && counter200 > 0) {
			g.drawString("200", ghost200X, ghost200Y);
			counter200--;
		}
		if (paint400 && counter400 > 0) {
			g.drawString("400", ghost400X, ghost400Y);
			counter400--;
		}
		if (paint800 && counter800 > 0) {
			g.drawString("800", ghost800X, ghost800Y);
			counter800--;
		}
		if (paint1600 && counter1600 > 0) {
			g.drawString("1600", ghost1600X, ghost1600Y);
			counter1600--;
		}if(paintBonusScore && counterBonus > 0){
			g.setColor(Color.PINK);
			g.drawString(Integer.toString(scoreLabel),13*BLOCKSIZE,17*BLOCKSIZE+VERTICAL_OFFSET+10);
			counterBonus--;
		}
		g.setColor(originalColour);
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Resets the wall colour array back to the first value to allow it
	 *         to cycle through all the colours.
	 *         </p>
	 */
	public void resetWallColour() {
		colourCounter = 0;

	}
}
