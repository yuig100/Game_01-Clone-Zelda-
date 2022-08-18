package com.stonelame.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.stonelame.entities.Ammo;
import com.stonelame.entities.Enemy;
import com.stonelame.entities.Entity;
import com.stonelame.entities.Lifepack;
import com.stonelame.entities.Particle;
import com.stonelame.entities.Player;
import com.stonelame.entities.Weapon;
import com.stonelame.graficos.Spritesheet;
import com.stonelame.main.Game;

public class World {

	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;

	public World(String path) {

		try {

			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			World.WIDTH = map.getWidth();
			World.HEIGHT = map.getHeight();
			World.tiles = new Tile[map.getWidth() * map.getHeight()];

			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());

			for (int xx = 0; xx < map.getWidth(); xx++) {

				for (int yy = 0; yy < map.getHeight(); yy++) {

					int pixelAtual = pixels[xx + (yy * map.getWidth())];

					World.tiles[xx + (yy * World.WIDTH)] = new FloorTile(xx * World.TILE_SIZE, yy * World.TILE_SIZE, Tile.TILE_FLOOR);

					if (pixelAtual == 0xFF000000) {

						// Chão

						World.tiles[xx + (yy * World.WIDTH)] = new FloorTile(xx * World.TILE_SIZE, yy * World.TILE_SIZE, Tile.TILE_FLOOR);

					} else if (pixelAtual == 0xFFFFFFFF) {

						// parede
						World.tiles[xx + (yy * World.WIDTH)] = new WallTile(xx * World.TILE_SIZE, yy * World.TILE_SIZE, Tile.TILE_WALL);

					} else if (pixelAtual == 0xFFFF1C32) {

						// player
						Game.player.setX(xx * World.TILE_SIZE);
						Game.player.setY(yy * World.TILE_SIZE);

					} else if (pixelAtual == 0xFFFF23DE) {

						// Enemy
						Enemy en = new Enemy(xx * World.TILE_SIZE, yy * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE, Entity.ENEMY_EN);

						Game.entities.add(en);
						Game.enemies.add(en);

					} else if (pixelAtual == 0xFF00FF21) {

						// Arma
						Game.entities.add(
								new Weapon(xx * World.TILE_SIZE, yy * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE, Entity.WEAPON_EN));

					} else if (pixelAtual == 0xFFFAFF00) {

						// AMMO
						Game.entities
								.add(new Ammo(xx * World.TILE_SIZE, yy * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE, Entity.AMMO_EN));

					} else if (pixelAtual == 0xFF1C41FF) {

						// LIFE
						Lifepack pack = new Lifepack(xx * World.TILE_SIZE, yy * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE,
								Entity.LIFEPACK_EN);

						Game.entities.add(pack);

					}

				}

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public static void generateParticles(int amount,int x,int y) {
		
		for(int i = 0;i < amount;i++) {
			
			Game.entities.add(new Particle(x,y,1,1,null));
			
		}
		
	}
	
	public static boolean isFree(int xnext, int ynext, int zplayer) {

		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;

		int x2 = (xnext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;

		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext + TILE_SIZE - 1) / TILE_SIZE;

		int x4 = (xnext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (ynext + TILE_SIZE - 1) / TILE_SIZE;

		if (!(((tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile)) || ((tiles[x2 + (y2 * WIDTH)] instanceof WallTile))
				|| ((tiles[x3 + (y3 * WIDTH)] instanceof WallTile))
				|| ((tiles[x4 + (y4 * WIDTH)] instanceof WallTile)))) {

			return true;
		}
		if (zplayer > 0) {

			return true;

		}

		return false;

	}
	
	public static boolean isFreeDynamic(int xnext, int ynext,int width,int height) {

		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;

		int x2 = (xnext + width - 1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;

		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext + height - 1) / TILE_SIZE;

		int x4 = (xnext + width - 1) / TILE_SIZE;
		int y4 = (ynext + height - 1) / TILE_SIZE;

		if (!(((tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile)) || ((tiles[x2 + (y2 * WIDTH)] instanceof WallTile))
				|| ((tiles[x3 + (y3 * WIDTH)] instanceof WallTile))
				|| ((tiles[x4 + (y4 * WIDTH)] instanceof WallTile)))) {

			return true;
		}


		return false;

	}

	public static void restartGame(String level) {

		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0, 0, 16, 16, Game.spritesheet.getSprite(32, 0, TILE_SIZE, TILE_SIZE));
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
		return;

	}

	public void render(Graphics g) {

		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;

		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);

		for (int xx = xstart; xx <= xfinal; xx++) {

			for (int yy = ystart; yy <= yfinal; yy++) {
				if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy * WIDTH)];
				tile.render(g);

			}

		}

	}

	public static void renderMiniMap() {

		for (int i = 0; i < Game.minimapaPixels.length; i++) {

			Game.minimapaPixels[i] = 0;

		}

		for (int xx = 0; xx < WIDTH; xx++) {

			for (int yy = 0; yy < HEIGHT; yy++) {

				if (tiles[xx + (yy * WIDTH)] instanceof WallTile) {

					Game.minimapaPixels[xx + (yy * WIDTH)] = 0xff0000;

				}

			}

		}

		int xPlayer = Game.player.getX() / TILE_SIZE;
		int yPlayer = Game.player.getY() / TILE_SIZE;
		Game.minimapaPixels[xPlayer + (yPlayer * WIDTH)] = 0xFFFFFFFF;
	}
}
