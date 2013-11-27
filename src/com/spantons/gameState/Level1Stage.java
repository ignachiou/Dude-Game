package com.spantons.gameState;

import java.awt.Graphics2D;

import com.spantons.main.GamePanel;
import com.spantons.tileMap.TileMap;

public class Level1Stage extends Stage {

	private TileMap tileMap;

	public Level1Stage(GameStagesManager gsm) {
		this.gsm = gsm;
		init();
	}

	@Override
	public void init() {
		tileMap = new TileMap(64, 32);
		tileMap.loadMap("/maps/map.txt");
		tileMap.setPosition(-GamePanel.RESOLUTION_WIDTH / 2 , 0);
		//tileMap.setPosition(-GamePanel.RESOLUTION_WIDTH / 2 - 150, 0);
		//tileMap.setPosition(-GamePanel.RESOLUTION_WIDTH / 2, -100);
		//tileMap.setPosition(0, 0);

		// SteveJobs sj = new SteveJobs(tileMap);
		// sj.setPosition(180, 250);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g) {
		// Dibujar tilemap
		tileMap.draw(g);

	}

	@Override
	public void keyPressed(int k) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(int k) {
		// TODO Auto-generated method stub

	}

}
