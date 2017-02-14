import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DefaultMap {

	static final int MAZESIZEX = 28;
	static final int MAZESIZEY = 31;
	static final int FAIL_LIMIT = 10;
	public char[][] gameMap = new char[MAZESIZEX][MAZESIZEY];

	// Default map class

	public DefaultMap() {
		char charRead;

		try {
			FileReader fileReader = new FileReader("defaultMazeLayout.txt");
			BufferedReader reader = new BufferedReader(fileReader);
			for (int y = 0; y < MAZESIZEY; y++) {
				for (int x = 0; x < MAZESIZEX; x++) {
					charRead = (char) reader.read();
					if (charRead != '\n') {
						gameMap[x][y] = charRead;
					} else {
						break;
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public char[][] getMaze() {
		return gameMap;
	}
}
