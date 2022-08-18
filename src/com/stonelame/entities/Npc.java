package com.stonelame.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.stonelame.main.Game;

public class Npc extends Entity {
	
	public String[] frases = new String[5];
	
	public boolean showMessage = false;
	
	public Npc(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		frases[0]="Bem vindo";


	}
	
	public void tick() {
		
		int XPlayer = Game.player.getX();
		int YPlayer = Game.player.getY();
		
		int xNpc = (int)x;
		int yNpc = (int)y;
		
		if(((Math.abs(XPlayer - xNpc)) < 100) && ((Math.abs(YPlayer - yNpc)) < 100)) {
			
			showMessage = true;
			
		}else {
			
			showMessage = false;
			
		}
		
	}
	
	public void render(Graphics g) {
		
		super.render(g);
		
		if(showMessage) {
			
			//g.setColor(Color.white);
			//g.fillRect(9, 9, Game.WIDTH - 18,Game.HEIGHT - 18);
			//g.setColor(Color.red);
			//g.fillRect(10, 10, Game.WIDTH - 10,Game.HEIGHT - 10);
			
			g.setColor(Color.white);
			g.setFont(new Font("Arial",Font.BOLD,8));
			g.drawString(frases[0], (int)x,(int) y);
			
		}
		
	}

}
