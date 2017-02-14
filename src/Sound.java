import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	protected AudioInputStream inputStream;
	protected String pathToAudioFile;
	protected Clip clip;

	/**
	 * @author Matt Addicott : Obtains the requested audio file and stores a
	 *         clip of that sound.
	 */
	public void loadSound() {
		try {
			AudioInputStream inputStream = AudioSystem
					.getAudioInputStream(getClass().getClassLoader()
							.getResource(pathToAudioFile));

			AudioFormat format = inputStream.getFormat();
			DataLine.Info line = new DataLine.Info(Clip.class, format);
			AudioSystem.getLine(line);

			clip = AudioSystem.getClip();
			clip.open(inputStream);
		} catch (UnsupportedAudioFileException | LineUnavailableException
				| IOException e) {
			System.out
					.println("Error:: Exception in class Sound in method loadSoundFile.");
		}
	}

	/**
	 * @author Matt Addicott : Resets the position of an audio clip to account
	 *         for any previous plays, and then plays it.
	 */
	public void playSound() {
		clip.setFramePosition(0);
		clip.start();
	}

	/**
	 * @author Matt Addicott : Closes the stored clip, releasing its resources.
	 */
	public void closeSound() {
		clip.close();
	}

	public class BonusSound extends Sound {
		public BonusSound() {
			pathToAudioFile = "res/sounds/bonusSound.wav";
		}
	}

	public class DeathSound extends Sound {
		public DeathSound() {
			pathToAudioFile = "res/sounds/pacmanDeathSound.wav";
		}
	}

	public class FrightSound extends Sound {
		public FrightSound() {
			pathToAudioFile = "res/sounds/ghostFrightenedSound.wav";
		}
	}

	public class GhostEatenSound extends Sound {
		public GhostEatenSound() {
			pathToAudioFile = "res/sounds/eatGhostSound.wav";
		}
	}

	public class GhostKilledSound extends Sound {
		public GhostKilledSound() {
			pathToAudioFile = "res/sounds/killedGhostSound.wav";
		}
	}

	public class GhostSound extends Sound {
		public GhostSound() {
			pathToAudioFile = "res/sounds/normalGhostSoundWithSilence.wav";
		}
	}

	public class LifeSound extends Sound {
		public LifeSound() {
			pathToAudioFile = "res/sounds/lifeSirenSound.wav";
		}
	}

	public class PacmanSound extends Sound {
		public PacmanSound() {
			pathToAudioFile = "res/sounds/pelletEatSoundA.wav";
		}
	}

	public class PelletSoundB extends Sound {
		public PelletSoundB() {
			pathToAudioFile = "res/sounds/pelletEatSoundB.wav";
		}
	}

	public class IntroductionSound extends Sound {
		public IntroductionSound() {
			pathToAudioFile = "res/sounds/pacman_beginning.wav";
		}
	}

}
