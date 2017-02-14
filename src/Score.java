public class Score {
	private int score;
	private int level = 1;
	private boolean fright = false;
	private int[] ghostValues = { 200, 400, 800, 1600 };
	private int ghostsConsumedInFright = 0;
	private static final int PELLET_VALUE = 10;
	private static final int ENERGISER_VALUE = 50;
	private static final int INITIAL_LIFE_SCORE = 10000;
	private int[] bonusScoreValues = { 0, 100, 300, 500, 500, 700, 700, 1000,
			1000, 2000, 2000, 3000, 3000, 5000 };
	private int lifeScore;

	public Score() {
		lifeScore = INITIAL_LIFE_SCORE;
	}

	/**
	 * Author:Liam Fraser
	 * 
	 * <p>
	 * informs the score class what the current level is, so it can give the
	 * bonus items the appropriate score value.
	 * </p>
	 * 
	 * @param int level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Author:Liam Fraser
	 * 
	 * <p>
	 * informs the class whether a ghost is in fright mode. Resets the counter
	 * used for tracking how many ghosts are eaten if no ghost is in fright
	 * mode.
	 * </p>
	 * 
	 * @param boolean ghostFright
	 */
	public void setFright(boolean ghostFright) {

		fright = ghostFright;
		if (fright == false) {
			ghostsConsumedInFright = 0;
		}
	}

	/**
	 * Author:Liam Fraser
	 * 
	 * <p>
	 * Adds the value of a pellet to the total score.
	 * </p>
	 * 
	 */
	public void addPellet() {
		score += PELLET_VALUE;
	}

	/**
	 * Author:Liam Fraser
	 * 
	 * <p>
	 * Adds the value of an Energiser pellet to the total score
	 * </p>
	 * 
	 */
	public void addEngergiser() {
		score += ENERGISER_VALUE;
	}

	/**
	 * Author:Liam Fraser
	 * 
	 * <p>
	 * Adds the appropriate score for a consumed bonus item(fruit,keys etc.) to
	 * the score.
	 * </p>
	 * @return 
	 * 
	 */
	public int addBonusScore() {
		if (level > 11) {
			score += 5000;
			return 5000;
		} else {
			score += bonusScoreValues[level];
			return bonusScoreValues[level];
		}
	}

	/**
	 * Author:Liam Fraser
	 * 
	 * <p>
	 * Adds the value of a ghost to the score. Calculates the correct value
	 * based on the amount of ghosts consumed in the current instance of fright.
	 * </p>
	 * @return 
	 * 
	 */
	public int addGhostScore() {		
		int ghostScoreValue = ghostValues[ghostsConsumedInFright];
		score += ghostScoreValue;
		if (ghostsConsumedInFright < 3) {
			++ghostsConsumedInFright;
		}
		return ghostScoreValue;
	}
	/**
	 * Author:Liam Fraser
	 * 
	 * <p>
	 * sets the score required to get an extra life.
	 * </p>
	 * @param int score
	 */
	public void setLifeScore(int score) {
		lifeScore = score;
	}
	/**
	 * Author:Liam Fraser
	 * 
	 * <p>
	 * returns the score required for an extra life.
	 * </p>
	 * @return int lifeScore
	 */
	public int getLifeScore() {
		return lifeScore;
	}
	/**
	 * Author:Liam Fraser
	 * 
	 * <p>
	 * returns the current score.
	 * </p>
	 * @return int score
	 */
	public int getScore() {
		return score;
	}
}
