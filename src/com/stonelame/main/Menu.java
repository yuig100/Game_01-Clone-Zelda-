package com.stonelame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.stonelame.world.World;

public class Menu {

	public String[] options = { "novo jogo", "continue", "sair" };

	public int currentOption = 0, maxOption = options.length - 1;

	public boolean up, down, enter;

	private static boolean pause = false;

	public static boolean saveExists = false;

	public static boolean saveGame = false;

	public void tick() {

		File file = new File("save.txt");

		if (file.exists()) {

			saveExists = true;

		} else {

			saveExists = false;

		}

		if (up) {

			up = false;
			currentOption--;
			if (currentOption < 0) {

				currentOption = maxOption;

			}

		}

		if (down) {

			down = false;

			currentOption++;
			if (currentOption > maxOption) {

				currentOption = 0;

			}

		}

		if (enter) {
			enter = false;
			if (options[currentOption] == "novo jogo") {

				Game.gameState = "NORMAL";
				setPause(false);
				file = new File("save.txt");
				file.delete();

			} else if (options[currentOption] == "continue") {

				if (file.exists()) {

					String saver = loadGame(10);
					applySave(saver);

				}

			} else if (options[currentOption] == "sair") {

				System.exit(1);

			}

		}

	}

	public static void applySave(String str) {

		String[] spl = str.split("/");

		for (int i = 0; i < spl.length; i++) {

			String[] spl2 = spl[i].split(":");
			switch (spl2[0]) {

			case "level":
				World.restartGame("level" + spl2[1] + ".png");
				Game.gameState = "NORMAL";
				setPause(false);
				break;
			case "vida":
				Game.player.life = Integer.parseInt(spl2[1]);
				break;

			}

		}

	}

	public static String loadGame(int encode) {

		String line = "";
		File file = new File("save.txt");
		if (file.exists()) {

			try {

				String singleLine = null;

				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));

				try {

					while ((singleLine = reader.readLine()) != null) {

						String[] trans = singleLine.split(":");

						char[] val = trans[1].toCharArray();

						trans[1] = "";

						for (int i = 0; i < val.length; i++) {

							val[i] -= encode;

							trans[1] += val[i];

						}

						line += trans[0];
						line += ":";
						line += trans[1];
						line += "/";

					}

				} catch (IOException e) {

				}

			} catch (FileNotFoundException e) {

			}

		}
		return line;
	}

	public static void saveGame(String[] val1, int[] val2, int encode) {

		BufferedWriter write = null;

		try {

			write = new BufferedWriter(new FileWriter("save.txt"));

		} catch (IOException e) {

			e.printStackTrace();

		}

		for (int i = 0; i < val1.length; i++) {

			String current = val1[i];
			current += ":";
			char[] value = Integer.toString(val2[i]).toCharArray();

			for (int j = 0; j < value.length; j++) {

				value[j] += encode;
				current += value[j];

			}

			try {

				write.write(current);

				if (i < val1.length - 1) {

					write.newLine();

				}

			} catch (IOException e) {

			}

			try {

				write.flush();
				write.close();

			} catch (IOException e) {

			}
		}

	}

	public void render(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 100));
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);

		g.setColor(Color.red);
		g.setFont(new Font("arial", Font.BOLD, 36));
		g.drawString(">Stone Lame<", ((Game.WIDTH * Game.SCALE) / 2) - 130, ((Game.HEIGHT * Game.SCALE) / 2) - 140);

		// Op��es Menu
		g.setColor(Color.white);

		if (isPause() == false) {

			g.drawString("Novo Jogo", ((Game.WIDTH * Game.SCALE) / 2) - 70, ((Game.HEIGHT * Game.SCALE) / 2) - 40);

		} else {

			g.drawString("Resumir", ((Game.WIDTH * Game.SCALE) / 2) - 70, ((Game.HEIGHT * Game.SCALE) / 2) - 40);

		}

		g.drawString("continue", ((Game.WIDTH * Game.SCALE) / 2) - 70, ((Game.HEIGHT * Game.SCALE) / 2) - 0);

		g.drawString("Sair", ((Game.WIDTH * Game.SCALE) / 2) - 70, ((Game.HEIGHT * Game.SCALE) / 2) + 40);

		if (options[currentOption] == "novo jogo") {

			g.drawString(">", ((Game.WIDTH * Game.SCALE) / 2) - 100, 200);

		} else if (options[currentOption] == "continue") {

			g.drawString(">", ((Game.WIDTH * Game.SCALE) / 2) - 100, 240);

		} else if (options[currentOption] == "sair") {

			g.drawString(">", ((Game.WIDTH * Game.SCALE) / 2) - 100, 280);

		}

	}

	public static boolean isPause() {
		return pause;
	}

	public static void setPause(boolean pause) {
		Menu.pause = pause;
	}

}
