//package Pacman;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

public class GameProcessing extends JPanel {
	private MazeGenerator maze = new MazeGenerator();
	// Blocksize is the size of the tiles on the board.
	protected static final int LEFT = -1;
	private static final int BLOCKSIZE = 20;
	public static final int FRAMES_PER_SECOND = 150;
	private static final int TIME_BETWEEN_FRAMES = 1000 / FRAMES_PER_SECOND;
	private static final int ESCAPE_TIMER_VALUE = FRAMES_PER_SECOND * 4;
	private static final int FRAMES_PER_SPRITE = 7;

	private static final int PACMAN_INIT_X = 13;
	private static final int PACMAN_INIT_Y = 23;

	private static final int BLINKY_INIT_X = 14;
	private static final int BLINKY_INIT_Y = 11;

	private static final int INKY_INIT_X = 12;
	private static final int INKY_INIT_Y = 14;

	private static final int PINKY_INIT_X = 13;
	private static final int PINKY_INIT_Y = 14;

	private static final int CLYDE_INIT_X = 15;
	private static final int CLYDE_INIT_Y = 14;
	
	private static final int SCATTER_INDEX_LVL_2 = 8;
	private static final int SCATTER_INDEX_LVL_5 = 8;
	private static final int FINAL_SPEED_LEVEL = 6;
	private static final int ELROY_FRAMES_TO_SKIP = 20;
	private static final int BLINKY_CORNER_X = 25;
	private static final int BLINKY_CORNER_Y = -2;
	private static final int PINKY_CORNER_X = 2;
	private static final int PINKY_CORNER_Y = -2;
	private static final int INKY_CORNER_Y = 33;
	
	private static final int CLYDE_LVL2_RELEASE_PELLETS = 50;
	private static final int INKY_PELLET_RELEASE_VALUE = 30;
	private static final int PINKY_DEATH_RELEASE_VALUE = 7;
	private static final int INKY_DEATH_RELEASE_VALUE = 17;
	private static final int CLYDE_DEATH_RELEASE_VALUE = 32;
	
	private static final int MAZE_WIDTH = 28;

	private static final char PELLET = 'o';
	private static final int ELROY_LIMIT = 21;
	
	private static final int READY_SOUND_LENGTH = 4221;

	// objects
	private Pacman pacman = new Pacman(PACMAN_INIT_X, PACMAN_INIT_Y, BLOCKSIZE);
	private Ghost ghostMode = new Ghost(0, 0, BLOCKSIZE);
	private Blinky blinky = new Blinky(BLINKY_INIT_X, BLINKY_INIT_Y, BLOCKSIZE);
	private Inky inky = new Inky(INKY_INIT_X, INKY_INIT_Y, BLOCKSIZE);
	private Pinky pinky = new Pinky(PINKY_INIT_X, PINKY_INIT_Y, BLOCKSIZE);
	private Clyde clyde = new Clyde(CLYDE_INIT_X, CLYDE_INIT_Y, BLOCKSIZE);
	private PacmanBoard window;
	private DefaultMap fixedMaze;
	private Score score;
	private GameKeyListener listener;
	private char[][] gameMap = new char[28][31];

	
//Ghost behaviour controls
	private boolean isDead = false;

	private long scatterTimes[] = { 7, 20, 7, 20, 5, 20, 5,
			Long.MAX_VALUE / FRAMES_PER_SECOND, 7, 20, 7, 20, 5, 1033, 1,
			Long.MAX_VALUE / FRAMES_PER_SECOND, 5, 20, 5, 20, 5, 1027 -1, 1,
			Long.MAX_VALUE / FRAMES_PER_SECOND };
	private long pacmanMovementSpeeds[] = { 0, 5, 10, 10, 10, Long.MAX_VALUE };
	private int ghostNormalMovementSpeeds[] = { 0, 4, 7, 7, 20, 20 };
	private int elroy1Pellets[] = { 0, 20, 30, 40, 40, 40, 50, 50, 50, 60, 60,
			60, 80, 80, 80, 100, 100, 100, 100, 120, 120, 120 };
	private int elroy2Pellets[] = { 0, 10, 15, 20, 20, 20, 25, 25, 25, 30, 30,
			30, 40, 40, 40, 50, 50, 50, 50, 60, 60, 60 };
	private int elroyCounter = 1;
	private int elroy1MoveCounter = ELROY_FRAMES_TO_SKIP;
	private int elroy2MoveCounter = ELROY_FRAMES_TO_SKIP;
	boolean elroy1 = false;
	boolean elroy2 = false;
	private int modeIndex;
	private long pacmanMoveCounter;
	private boolean pinkyCanMove = false;
	private long ghostMoveCounter;
	private boolean inkyCanMove;
	private boolean clydeCanMove;

	// Game state variables
	private boolean needToWait;
	private boolean onTitleScreen;
	private boolean gameOver;
	private boolean collided;
	private boolean levelDone;
	private boolean onHighScoreScreen;
	private int level;

	// Sounds
	private static Sound.IntroductionSound waitSound = new Sound().new IntroductionSound();
	private static Sound.DeathSound deathSound = new Sound().new DeathSound();
	private static Sound.LifeSound lifeSiren = new Sound().new LifeSound();
	private Sound.GhostEatenSound eatGhost = new Sound().new GhostEatenSound();
	private Sound.GhostSound ghostSound = new Sound().new GhostSound();
	private Sound.FrightSound frightSound = new Sound().new FrightSound();
	private Sound.GhostKilledSound killedGhostSound = new Sound().new GhostKilledSound();
	//Other
	private int animationFrameCounter;
	private int switchCounter;
	private int pelletTimer;
	private int pelletsLeft = 1;

	public GameProcessing(GameKeyListener listener, PacmanBoard pacmanBoard) {
		window = pacmanBoard;
		this.listener = listener;
		addKeyListener(this.listener);
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		ghostSound.loadSound();
		frightSound.loadSound();
		// deathSprite = spriteGenerator.getImage("HE DED", level);
		eatGhost.loadSound();
		killedGhostSound.loadSound();
		lifeSiren.loadSound();
		ghostMode.disableCounter();
		initGame();

	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          initialises the game by resetting all values and objects back to
	 *          the state they need to be in at the start of the game. Allows
	 *          for multiple consecutive games to be played without restarting
	 *          the program
	 *          </p>
	 */
	private void initGame() {
		pacman.reinitialise(PACMAN_INIT_X, PACMAN_INIT_Y, BLOCKSIZE);
		ghostMode.reinitialise(0, 0, BLOCKSIZE);
		blinky.reinitialise(BLINKY_INIT_X, BLINKY_INIT_Y, BLOCKSIZE);
		pinky.reinitialise(PINKY_INIT_X, PINKY_INIT_Y, BLOCKSIZE);
		inky.reinitialise(INKY_INIT_X, INKY_INIT_Y, BLOCKSIZE);
		clyde.reinitialise(CLYDE_INIT_X, CLYDE_INIT_Y, BLOCKSIZE);
		score = new Score();
		window.resetWallColour();

		onTitleScreen = true;
		level = 0;
		animationFrameCounter = 0;

		gameMap = maze.generateAndReturnMaze();

		int mazeTunnelY = maze.getTunnelY(gameMap);
		pacman.setMaze(gameMap);
		blinky.setMaze(gameMap);
		pinky.setMaze(gameMap);
		inky.setMaze(gameMap);
		clyde.setMaze(gameMap);
		ghostMoveCounter = ghostNormalMovementSpeeds[1];
		pacman.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
		blinky.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
		pinky.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
		inky.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
		clyde.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
		window.setPacman(pacman);
		window.setBlinky(blinky);
		window.setInky(inky);
		window.setPinky(pinky);
		window.setClyde(clyde);
		window.setScore(score);
		ghostMode.setTimer(FRAMES_PER_SECOND, scatterTimes[modeIndex]);
		window.setLevel(level);
		switchCounter = FRAMES_PER_SPRITE;
		pelletTimer = ESCAPE_TIMER_VALUE;
		needToWait = true;
		modeIndex = 0;
		gameOver = false;
		collided = true;
		levelDone = false;
		onHighScoreScreen = false;
		inkyCanMove = false;
		clydeCanMove = false;
		pacmanMoveCounter = pacmanMovementSpeeds[1];
		startNewLevel();
	}

	/**
	 * @author Liam Fraser Method to check and adjust the behaviour mode of each
	 *         ghost
	 *         <p>
	 *         Ghosts switch between chase and scatter mode at several
	 *         predetermined intervals in every level. these times are stored in
	 *         the array "scatterTimes", which is indexed by each ghosts
	 *         ModeIndex.
	 * 
	 *         Whenever the time set in the array has elapsed for the ghost, the
	 *         index is incremented by one, and the behaviour changed. The
	 *         target is set according to what mode the ghost is in. (GameSpace)
	 *         </p>
	 *
	 * @param NULL
	 *
	 */
	private void ghostModeCheck() {
		if (!ghostMode.frightened) {
			if (ghostMode.switchModeCheck()) {
				modeIndex++;
				ghostMode.setTimer(FRAMES_PER_SECOND, scatterTimes[modeIndex]);
			}
		}

		if (ghostMode.isChase() && !blinky.isKilled()) {
			blinky.setTarget(pacman.getSpriteGameSpaceX(),
					pacman.getSpriteGameSpaceY());
		} else if (blinky.isKilled()) {
			killGhost(blinky);
		} else {
			blinky.setTarget(BLINKY_CORNER_X, BLINKY_CORNER_Y);
		}

		if (!pinky.inHouse() && !pinky.isKilled()) {
			if (ghostMode.isChase()) {
				pinky.setTarget(pacman.getSpriteGameSpaceX(),
						pacman.getSpriteGameSpaceY(), pacman.getDirection());
			} else {
				pinky.setTarget(PINKY_CORNER_X, PINKY_CORNER_Y);
			}
		} else if (pinky.isKilled()) {
			killGhost(pinky);
		} else {
			pinky.setTarget(0, 0);

		}

		if (!inky.inHouse() && !inky.isKilled()) {
			if (ghostMode.isChase()) {
				inky.setTarget(pacman.getSpriteGameSpaceX(),
						pacman.getSpriteGameSpaceY(), pacman.getDirection(),
						blinky.getBlockX(), blinky.getBlockY());
			} else {
				inky.setTarget(MAZE_WIDTH -1, INKY_CORNER_Y);
			}
		} else if (inky.isKilled()) {
			killGhost(inky);
		} else {
			inky.setTarget(13, 12);
		}

		if (!clyde.inHouse() && !clyde.isKilled()) {

			if (ghostMode.isChase()) {
				clyde.setTarget(pacman.getSpriteGameSpaceX(),
						pacman.getSpriteGameSpaceY());
			} else {
				clyde.scatter();
			}
		} else if (clyde.isKilled()) {
			killGhost(clyde);
		} else {
			clyde.setTarget(13, 11);
		}

	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Controls all the processing that needs to be done on every
	 *          frame.
	 *          </p>
	 */

	public void tickFrame() {
		// THE LOOP FOR THE GAME

		while (true) {
			long time = System.nanoTime();	
			if (listener.newInputReady()) {
				KeyEvent e = listener.getEvent();
				switch (e.getID()) {
				case KeyEvent.KEY_PRESSED:
					pacman.keyPressed(e);
				
				}
			}
			if (pacman.getGameEnd()) {
				initGame();
				pacman.setGameEnd(false);
			}
			if (onTitleScreen) {
				if (pacman.isRandomMaze()) {
					pacman.setMaze(gameMap);
					blinky.setMaze(gameMap);
					pinky.setMaze(gameMap);
					inky.setMaze(gameMap);
					clyde.setMaze(gameMap);
					countPellets();
				} else if (pacman.isFixedMaze()) {
					fixedMaze = new DefaultMap();
					gameMap = fixedMaze.getMaze();
					countPellets();
					pacman.setMaze(gameMap);
					blinky.setMaze(gameMap);
					pinky.setMaze(gameMap);
					inky.setMaze(gameMap);
					clyde.setMaze(gameMap);
					pacman.setGate(0, 14, MAZE_WIDTH -1, 14);
					blinky.setGate(0, 14, MAZE_WIDTH -1, 14);
					pinky.setGate(0, 14, MAZE_WIDTH -1, 14);
					inky.setGate(0, 14, MAZE_WIDTH -1, 14);
					clyde.setGate(0, 14, MAZE_WIDTH -1, 14);
					window.setMaze(gameMap);
				}
			}
			if (!onTitleScreen) {
				if (needToWait) {

					waitTillReady();
				} else {
					if (!gameOver) {
						if (pelletsLeft < elroy1Pellets[elroyCounter]) {
							elroy1 = true;
							if (pelletsLeft < elroy2Pellets[elroyCounter]) {
								elroy2 = true;
							}
						}
						if (!clyde.inHouse()) {
							if (elroy1) {
								--elroy1MoveCounter;
								if (elroy1MoveCounter <= 1) {
									// gives 5% speed boost
									blinky.move();
									elroy1MoveCounter = ELROY_FRAMES_TO_SKIP;
								}
							}
							if (elroy2) {
								--elroy2MoveCounter;
								if (elroy2MoveCounter <= 1) {
									// gives 5% speed boost
									blinky.move();
									elroy2MoveCounter = ELROY_FRAMES_TO_SKIP;
								}
							}
						}
						if (!pacman.isPelletEaten()) {
							pelletTimer--;
							awardLifeIfNeeded();
						} else {
							score.addPellet();
							pelletsLeft--;
							pelletTimer = ESCAPE_TIMER_VALUE;
							if (ghostMode.isUsingCounter()) {
								ghostMode.increasePelletsTracked();
							}
						}
						pacman.setFull(false);
						if (blinky.isKilled() || pinky.isKilled()
								|| inky.isKilled() || clyde.isKilled()) {

							if (!killedGhostSound.clip.isActive()) {
								killedGhostSound.playSound();
							}
						} else if (!ghostMode.isFrightened()) {
							if (!ghostSound.clip.isActive()) {
								ghostSound.playSound();
							}
						} else if (!frightSound.clip.isActive()) {
							frightSound.playSound();
						}
						if (collidedWithGhosts()) {
							processCollision();
						}
						ghostModeCheck();
						if (pacmanMoveCounter != 1) {
							pacmanMoveCounter--;
							pacman.move();
						} else {
							if (level < FINAL_SPEED_LEVEL) {
								pacmanMoveCounter = pacmanMovementSpeeds[level];
							} else {
								pacmanMoveCounter = pacmanMovementSpeeds[FINAL_SPEED_LEVEL - 1];
							}
							pacman.resetPelletEaten();
						}
						if (ghostMoveCounter != 1) {
							ghostMoveCounter--;
							blinky.move();

							if (!clydeCanMove) {
								clyde.oscillate();
							}
							if (!pinkyCanMove) {
								assessIfPinkyCanMove();
								if (!pinkyCanMove) {
									pinky.oscillate();
								}
							} else {
								if (!pinky.isInBetweenTiles()) {
									pinky.move();
								} else {
									pinky.oscillate();
								}
							}
							if (!inkyCanMove) {
								inky.oscillate();

								if (!pinky.inHouse()) {
									assessIfInkyCanMove();
								}
							} else {
								if (!inky.isInBetweenTiles()) {
									inky.move();
								} else {
									inky.oscillate();
								}
								if (!clydeCanMove) {
									assessIfClydeCanMove();
								} else {
									if (!clyde.isInBetweenTiles()) {
										clyde.move();
									} else {
										clyde.oscillate();
									}
								}
							}
						} else {
							if (level < FINAL_SPEED_LEVEL) {
								ghostMoveCounter = ghostNormalMovementSpeeds[level];
							} else {
								ghostMoveCounter = ghostNormalMovementSpeeds[FINAL_SPEED_LEVEL - 1];
							}
						}
						if (pacman.hasEatenEnergiser()) {
							ghostMode.frightInit(FRAMES_PER_SECOND);
							blinky.frightInit(FRAMES_PER_SECOND);
							inky.frightInit(FRAMES_PER_SECOND);
							pinky.frightInit(FRAMES_PER_SECOND);
							clyde.frightInit(FRAMES_PER_SECOND);
							score.addEngergiser();
							awardLifeIfNeeded();
						}
						ghostMode.recoveredFromFrightCheck();
						blinky.recoveredFromFrightCheck();
						pinky.recoveredFromFrightCheck();
						inky.recoveredFromFrightCheck();
						clyde.recoveredFromFrightCheck();
						score.setFright(ghostMode.isFrightened());
					}
					if (switchCounter == 0) {
						pacman.changeSprite();
						switchCounter = FRAMES_PER_SPRITE;
					} else {
						if (!pacman.isStop()) {
							switchCounter--;
						}
					}
					if (ghostMode.isFrightened()) {
						assessFrightFlashing();
					}
					if (pacman.hasEatenConsumable()) {
						window.setBonusScoreLabel(score.addBonusScore());
						awardLifeIfNeeded();
					}
				}
			}
			if (pacman.getGameStart()) {
				onTitleScreen = false;
			}
			window.repaint();
			if (pelletsLeft <= 0) {
				startNewLevel();
			}// Remove time taken to perform loop from time to wait for a
				// smoother frame rate
			long finishTime = System.nanoTime();
			try {
				TimeUnit.MICROSECONDS.sleep((TIME_BETWEEN_FRAMES * 1000)
						- ((finishTime - time) / 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (gameOver) {
				if (!onHighScoreScreen) {
					waitTillReady();
				}
				onHighScoreScreen = true;
				pacman.setEnd(true);
			}
		}
	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Assess whether the conditions have been filled so that Pinky can
	 *          move. I.E if the global timer has expired, or it's pellet
	 *          release value has been reached
	 *          </p>
	 */
	private void assessIfPinkyCanMove() {
		if (!ghostMode.isUsingCounter() || pelletTimer < 1) {
			pinkyCanMove = true;
			pelletTimer = ESCAPE_TIMER_VALUE;
		} else if (ghostMode.getPelletsTracked() >= PINKY_DEATH_RELEASE_VALUE) {
			pinkyCanMove = true;
			pelletTimer = ESCAPE_TIMER_VALUE;
		} else {
			pinkyCanMove = false;
		}
	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         assesses if the conditions for Inky to move have been fulfilled.
	 *         </p>
	 */

	private void assessIfInkyCanMove() {
		if (pelletTimer < 1) {
			pelletTimer = ESCAPE_TIMER_VALUE;
			inkyCanMove = true;
		} else if (ghostMode.isUsingCounter()) {
			if (ghostMode.getPelletsTracked() >= INKY_DEATH_RELEASE_VALUE) {
				inkyCanMove = true;
				pelletTimer = ESCAPE_TIMER_VALUE;
			}
			return;
		} else {
			if (level > 1) {
				inkyCanMove = true;
			} else {
				if (pacman.isPelletEaten()) {
					inky.increasePelletsTracked();
					if (inky.getPelletsTracked() > INKY_PELLET_RELEASE_VALUE) {
						inkyCanMove = true;
						pelletTimer = ESCAPE_TIMER_VALUE;
					}
				}
			}
		}

	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         asseses if the conditions for Clyde to move have been fulfilled.
	 *         </p>
	 */

	private void assessIfClydeCanMove() {
		if (pelletTimer < 1) {
			pelletTimer = ESCAPE_TIMER_VALUE;
			clydeCanMove = true;
		} else if (ghostMode.isUsingCounter()) {
			if (ghostMode.getPelletsTracked() >= CLYDE_DEATH_RELEASE_VALUE) {
				clydeCanMove = true;
				pelletTimer = ESCAPE_TIMER_VALUE;
			}
			return;
		} else {
			if (pacman.isPelletEaten()) {
				clyde.increasePelletsTracked();
				if (level > 1) {
					if (clyde.getPelletsTracked() >= CLYDE_LVL2_RELEASE_PELLETS) {
						clydeCanMove = true;
						pelletTimer = ESCAPE_TIMER_VALUE;
					}
				}
				if (clyde.getPelletsTracked() > 59) {
					clydeCanMove = true;
					pelletTimer = ESCAPE_TIMER_VALUE;
				}
			}
		}

	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         processes what type of collision happened and calls the
	 *         appropriate methods to handle it
	 *         </p>
	 */
	private void processCollision() {
		if (collidedGhostIs(blinky)) {
			if (blinky.isFrightened()) {
				killGhost(blinky);
			} else if (!blinky.isKilled()) {
				killPacman();
			}
		} else if (collidedGhostIs(inky)) {
			if (inky.isFrightened()) {
				killGhost(inky);

			} else if (!inky.isKilled()) {
				killPacman();
			}
		} else if (collidedGhostIs(pinky)) {
			if (pinky.isFrightened()) {
				killGhost(pinky);
			} else if (!pinky.isKilled()) {
				killPacman();
			}
		} else if (collidedGhostIs(clyde)) {
			if (clyde.isFrightened()) {
				killGhost(clyde);
			} else if (!clyde.isKilled()) {
				killPacman();
			}
		}
	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         method to kill a ghost and control it's tracking back to the
	 *         house
	 *         </p>
	 * @param Ghost
	 *            : the ghost that has been killed
	 */
	private void killGhost(Ghost ghost) {
		if (!ghost.isKilled()) {
			window.setGhostScoreLabel(score.addGhostScore(), ghost.getX(),
					ghost.getY());
			awardLifeIfNeeded();
			eatGhost.playSound();
			try {
				TimeUnit.MILLISECONDS.sleep(574);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		ghost.kill();
		if (!ghost.inHouse()) {
			ghost.setTarget(13, 12);
		} else {
			if (ghost instanceof Blinky) {
				ghost.setTarget(PINKY_INIT_X, PINKY_INIT_Y);
			} else if (ghost instanceof Pinky) {
				ghost.setTarget(PINKY_INIT_X, PINKY_INIT_Y);
			} else if (ghost instanceof Inky) {
				ghost.setTarget(INKY_INIT_X, INKY_INIT_Y);
			} else if (ghost instanceof Clyde) {
				ghost.setTarget(CLYDE_INIT_X, CLYDE_INIT_Y);
			}
		}
		if (ghost.getBlockX() == PINKY_INIT_X
				&& ghost.getBlockY() == PINKY_INIT_Y) {

			if (ghost instanceof Pinky) {

				ghost.reset(PINKY_INIT_X, PINKY_INIT_Y);
			} else if (ghost instanceof Blinky) {
				ghost.reset(PINKY_INIT_X, PINKY_INIT_Y);
			}
		} else if (ghost.getBlockX() == INKY_INIT_X
				&& ghost.getBlockY() == INKY_INIT_Y) {
			if (ghost instanceof Inky) {
				ghost.reset(INKY_INIT_X, INKY_INIT_Y);
			}
		} else if (ghost.getBlockX() == CLYDE_INIT_X
				&& ghost.getBlockY() == CLYDE_INIT_Y) {
			if (ghost instanceof Clyde) {
				ghost.reset(CLYDE_INIT_X, CLYDE_INIT_Y);
			}
		}
	}

	/**
	 * @author Liam Fraser:
	 *         <p>
	 *         returns whether a ghost has been collided with
	 *         </p>
	 * @return boolean : true if collided with a ghost
	 */
	private boolean collidedWithGhosts() {
		return (collidedGhostIs(pinky) || collidedGhostIs(inky)
				|| collidedGhostIs(blinky) || collidedGhostIs(clyde));
	}

	/**
	 * @author Liam Fraser
	 * @param: Ghost : the ghost to check
	 *         <p>
	 *         check collision with an individual ghost
	 *         </p>
	 * @return boolean : true if collided with the ghost
	 */
	private boolean collidedGhostIs(Ghost ghost) {
		return pacman.isCollision(pacman.getRectangle(), ghost.getRectangle());
	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Kills pacman, checks if the game is over and resets the board
	 *         positions and variables.
	 *         </p>
	 *
	 */
	private void killPacman() {
		if (collided) {
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			isDead = true;
			window.repaint();
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			deathSound.loadSound();
			deathSound.playSound();
			// Death animation
			animationFrameCounter = window.getAnimationFrameCounter();
			while (animationFrameCounter < 14) {
				animationFrameCounter = window.getAnimationFrameCounter();
				window.repaint();
			}
			try {
				TimeUnit.MILLISECONDS.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isDead = false;
			pacman.reduceLifeCount();
			pacman.setX(PACMAN_INIT_X);
			pacman.setY(PACMAN_INIT_Y);
			pacman.resetDxDy();
			pacman.setFull(true);
			ghostMode.reset(0, 0);
			blinky.reset(BLINKY_INIT_X, BLINKY_INIT_Y);
			pinky.reset(PINKY_INIT_X, PINKY_INIT_Y);
			inky.reset(INKY_INIT_X, INKY_INIT_Y);
			disableIndividualGhostCounters();
			pinkyCanMove = false;
			inkyCanMove = false;
			clyde.reset(CLYDE_INIT_X, CLYDE_INIT_Y);
			clydeCanMove = false;
			modeIndex = 0;
			ghostMode.setTimer(FRAMES_PER_SECOND, scatterTimes[modeIndex]);
			needToWait = true;
			pelletTimer = ESCAPE_TIMER_VALUE;

		}
		if (pacman.getLifeCount() == 0) {
			gameOver = true;
			try {
				window.writeHighScore();
			} catch (IOException e) {
				System.out.println("error writing high score");
			}
		}
	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Disables the individual pellet counters for the ghosts, and
	 *          activates the global one
	 *          </p>
	 */
	private void disableIndividualGhostCounters() {
		blinky.disableCounter();
		pinky.disableCounter();
		inky.disableCounter();
		clyde.disableCounter();
		ghostMode.enableCounter();

	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Enables the individual pellet counters for the ghosts, and
	 *          deactivates the global one
	 *          </p>
	 */
	private void enableIndividualGhostCounters() {
		blinky.enableCounter();
		pinky.enableCounter();
		inky.enableCounter();
		clyde.enableCounter();
		ghostMode.disableCounter();

	}

	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Performs all the processing required when starting a new
	 *          level:Calling methods to flash the level, refresh the map, give
	 *          map details to all objects, reseting objects to initial
	 *          positions and behaviours, changing speeds and incrementing the
	 *          level.
	 *          </p>
	 */
	public void startNewLevel() {
		if (!onTitleScreen) {
			window.flashLevel();
		}
		if (pacman.isFixedMaze()) {
			fixedMaze = new DefaultMap();
			gameMap = fixedMaze.getMaze();
		} else if (pacman.isRandomMaze()) {
			gameMap = maze.generateAndReturnMaze();
			int mazeTunnelY = maze.getTunnelY(gameMap);
			pacman.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
			blinky.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
			pinky.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
			inky.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
			clyde.setGate(0, mazeTunnelY, MAZE_WIDTH -1, mazeTunnelY);
		}
		countPellets();
		window.setMaze(gameMap);
		enableIndividualGhostCounters();
		pacman.setMaze(gameMap);
		blinky.setMaze(gameMap);
		pinky.setMaze(gameMap);
		inky.setMaze(gameMap);
		clyde.setMaze(gameMap);
		level++;
		if (level == 1) {
			modeIndex = 0;
		} else if (level < 5) {
			modeIndex = SCATTER_INDEX_LVL_2;
		} else {
			modeIndex = SCATTER_INDEX_LVL_5;
		}
		if (level <= ELROY_LIMIT) {
			elroyCounter = level;
		} else {
			elroyCounter = ELROY_LIMIT;
		}
		elroy1 = false;
		elroy2 = false;
		ghostMode.reset(0, 0);
		blinky.reset(BLINKY_INIT_X, BLINKY_INIT_Y);
		inky.reset(INKY_INIT_X, INKY_INIT_Y);
		pinky.reset(PINKY_INIT_X, PINKY_INIT_Y);
		clyde.reset(CLYDE_INIT_X, CLYDE_INIT_Y);
		clydeCanMove = false;
		inkyCanMove = false;
		pacman.resetDirection();
		pacman.setX(PACMAN_INIT_X);
		pacman.setY(PACMAN_INIT_Y);
		pacman.resetDxDy();
		pacman.setFull(true);
		ghostMode.setTimer(FRAMES_PER_SECOND, scatterTimes[modeIndex]);
		score.setLevel(level);
		pacman.setCurrentLevel(level);
		pacman.resetPelletTotal();

		window.setLevel(level);
		pelletTimer = ESCAPE_TIMER_VALUE;
		if (level < 6) {
			pacmanMoveCounter = pacmanMovementSpeeds[level];
			ghostMoveCounter = ghostNormalMovementSpeeds[level];
		}
		needToWait = true;
		window.repaint();

	}

	/**
	 * @author Liam Fraser
	 *         <p>
	 *         Pauses the game and plays the intermission sound clip
	 *         </p>
	 */
	private void waitTillReady() {
		if (!onHighScoreScreen) {
			waitSound.loadSound();
			waitSound.playSound();
		}
		try {
			//Pause game for exact duration of sound clip
			TimeUnit.MILLISECONDS.sleep(READY_SOUND_LENGTH);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		needToWait = false;
	}
	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Awards an extra live if the score reaches the threshold, and the player has less than 5 lives.
	 *          </p>
	 */
	private void awardLifeIfNeeded() {
		if (score.getScore() > score.getLifeScore()) {
			if (pacman.getLifeCount() < 5) {
				pacman.giveExtraLife();
				lifeSiren.playSound();
			}
			score.setLifeScore(score.getLifeScore() * 2);
		}
	}
	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Method to assess with the ghosts need to swap to flashing sprites when they are in fright mode.
	 *          </p>
	 */
	private void assessFrightFlashing() {
		blinky.alterFlashCounter();
		pinky.alterFlashCounter();
		inky.alterFlashCounter();
		clyde.alterFlashCounter();
	}
	/**
	 * @author: Liam Fraser
	 * 
	 *          <p>
	 *          Counts how many pellets are left to consume in the maze.
	 *          </p>
	 */
	private void countPellets() {
		pelletsLeft = 0;
		for (int x = 0; x < DefaultMap.MAZESIZEX; x++) {
			for (int y = 0; y < DefaultMap.MAZESIZEY; y++) {
				if (gameMap[x][y] == PELLET) {
					pelletsLeft++;
				}
			}
		}
	}

	public Pacman getPacman() {
		return pacman;
	}

	public Blinky getBlinky() {
		return blinky;
	}

	public Inky getInky() {
		return inky;
	}

	public Pinky getPinky() {
		return pinky;
	}

	public Clyde getClyde() {
		return clyde;
	}

	public boolean getOnTitleScreen() {
		return onTitleScreen;
	}

	public boolean getGameOver() {
		return gameOver;
	}

	public boolean getLevelDone() {
		return levelDone;
	}

	public boolean getIsPacmanDead() {
		return isDead;
	}

	public boolean needsToWait() {
		return needToWait;
	}

	public boolean isOnHighScoreScreen() {
		return onHighScoreScreen;
	}

	public char[][] getGameMap() {
		return gameMap;
	}

	public Score getScore() {
		return score;
	}
}
