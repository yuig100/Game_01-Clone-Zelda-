package com.stonelame.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.stonelame.main.Game;

public class UI {

	public void render(Graphics g) {

		// barra vermelha indicando perda de vida
		g.setColor(Color.red);
		g.fillRect(5, 5, 70, 8);

		// barra verde indicando quantidade de vida
		g.setColor(Color.GREEN);
		g.fillRect(5, 5, (int) ((Game.player.life / Game.player.maxlife) * 70), 8);

		// contador de vida
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 9));
		g.drawString((int) Game.player.life + "/" + (int) Game.player.maxlife, 25, 13);
	}

}
