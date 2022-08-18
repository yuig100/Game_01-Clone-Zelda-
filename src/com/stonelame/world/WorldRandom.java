package com.stonelame.world;

import com.stonelame.main.Game;

public class WorldRandom {

	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;

	public WorldRandom(String path) {

		Game.player.setX(0);
		Game.player.setY(0);

		WIDTH = 20;
		HEIGHT = 20;
		tiles = new Tile[WIDTH * HEIGHT];

		for (int xx = 0; xx < WIDTH; xx++) {

			for (int yy = 0; yy < HEIGHT; yy++) {

				tiles[xx + yy * WIDTH] = new WallTile(xx * TILE_SIZE, yy * TILE_SIZE, Tile.TILE_WALL);

			}

		}

		int dir = 0;
		int xx = 0, yy = 0;

		for (int i = 0; i < 40; i++) {

			tiles[xx + yy * WIDTH] = new FloorTile(xx * TILE_SIZE, yy * TILE_SIZE, Tile.TILE_FLOOR);

			if (dir == 0) {

				// direita

				if (xx < WIDTH) {

					xx++;

				}

			} else if (dir == 1) {

				// esquerda

				if (xx > 0) {

					xx--;

				}

			} else if (dir == 2) {

				// baixo
				if (yy < HEIGHT) {

					yy++;

				}

			} else if (dir == 3) {

				// cima
				if (yy > 0) {

					yy--;

				}

			}

			if (Game.rand.nextInt(100) < 30) {

				dir = Game.rand.nextInt(4);

			}

		}

	}
}
