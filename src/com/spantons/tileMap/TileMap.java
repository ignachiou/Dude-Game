package com.spantons.tileMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import utilities.Multiple;
import utilities.TileWalk;

import com.spantons.main.GamePanel;

public class TileMap {

	private int x;
	private int y;

	// limites
	private int xMin;
	private int yMin;
	private int xMax;
	private int yMax;

	// mapa
	private int[][] map;
	private Set<Integer> unlockedTiles;
	public int tileWidthSize;
	public int tileHeightSize;
	private int numRowsMap;
	private int numColMap;
	
	// dibujado
	private Point2D.Double coorMapTopLeft;
	private Point2D.Double coorMapTopRight;
	private Point2D.Double coorMapBottomLeft;
	private Point2D.Double coorMapBottomRight;
	
	// resolucion arreglada para el tamano del tile
	public int RESOLUTION_WIDTH_FIX;
	public int RESOLUTION_HEIGHT_FIX;

	// tileset
	private TileSet tileSet;
	private Tile[] tiles;

	/****************************************************************************************/
	public TileMap(int _tileWidthSize, int _tileHeightSize, TileSet _tileSet) {

		tileSet = _tileSet;
		tileWidthSize = _tileWidthSize;
		tileHeightSize = _tileHeightSize;
		tiles = tileSet.getTiles();
		
		if (	GamePanel.RESOLUTION_WIDTH % tileWidthSize == 0 
			&& GamePanel.RESOLUTION_HEIGHT % tileHeightSize == 0) {
			RESOLUTION_WIDTH_FIX = GamePanel.RESOLUTION_WIDTH;
			RESOLUTION_HEIGHT_FIX = GamePanel.RESOLUTION_HEIGHT;

		} else {
			Point2D.Double fixResolution = 
					Multiple.findPointCloserTo(
							new Point2D.Double(GamePanel.RESOLUTION_WIDTH,GamePanel.RESOLUTION_HEIGHT), 
							new Point2D.Double(tileWidthSize,tileHeightSize));
			
			RESOLUTION_WIDTH_FIX = (int) fixResolution.x;
			RESOLUTION_HEIGHT_FIX = (int) fixResolution.y;
		}	

	}
	/****************************************************************************************/
	public void loadMap(String s) {

		try {
			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					in));

			// Numero de filas y columnas del mapa
			numColMap = Integer.parseInt(br.readLine());
			numRowsMap = Integer.parseInt(br.readLine());
			// Memoria a la matriz del mapa
			map = new int[numRowsMap][numColMap];
			// Numero de tiles bloqueados
			int numBlockedTiles = Integer.parseInt(br.readLine());
			// Memoria al Set de tiles bloqueados
			unlockedTiles = new HashSet<Integer>();

			// Expresion regular para delimintar los  datos
			String delimsChar = ",";
			// Tiles bloqueados
			String line = br.readLine();
			String[] tokens = line.split(delimsChar);
			// llenamos el set
			for (int i = 0; i < numBlockedTiles; i++) 
				unlockedTiles.add(Integer.parseInt(tokens[i]));
			
			// llenamos la matriz map
			for (int row = 0; row < numRowsMap; row++) {
				line = br.readLine();
				tokens = line.split(delimsChar);
				for (int col = 0; col < numColMap; col++) 
					map[row][col] = Integer.parseInt(tokens[col]);
			}
			
			getBounds();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/****************************************************************************************/
	private void getBounds(){
		
		xMin = (int) -mapToAbsolute(numRowsMap - 1, 0).x - tileHeightSize;
		yMin = -tileHeightSize;
		Point2D.Double fix = Multiple.findPointCloserTo(new Point2D.Double(xMin,yMin), new Point2D.Double(tileWidthSize,tileHeightSize));
		xMin = (int) fix.x;
		yMin = (int) fix.y;
		
		xMax = (int) (-mapToAbsolute(0, numColMap -1).x - RESOLUTION_WIDTH_FIX) 
				+ tileWidthSize * 2;
		yMax = (int) (mapToAbsolute(numRowsMap - 1, numColMap -1).y - RESOLUTION_HEIGHT_FIX)
				+ tileHeightSize * 2;
		fix = Multiple.findPointCloserTo(new Point2D.Double(xMax,yMax), new Point2D.Double(tileWidthSize,tileHeightSize));
		xMax = (int) fix.x;
		yMax = (int) fix.y;
	}
	/****************************************************************************************/
	public Point2D.Double absoluteToMap(double x, double y) {
		int mapX = (int) ((x / (tileWidthSize / 2) + y / (tileHeightSize / 2)) / 2);
		int mapY = (int) ((y / (tileHeightSize / 2) - (x / (tileWidthSize / 2))) / 2);

		return new Point2D.Double(mapX, mapY);
	}
	/****************************************************************************************/
	public Point2D.Double mapToAbsolute(double x, double y) {
		
		int absoluteX = (int) ((x - y) * (tileWidthSize / 2));
		int absoluteY = (int) ((x + y) * (tileHeightSize / 2));

		return new Point2D.Double(absoluteX, absoluteY);
	}
	/****************************************************************************************/
	public void fixBounds() {
		if (x < xMin) x = xMin;
		if (y < yMin) y = yMin;
		if (x > xMax) x = xMax;
		if (y > yMax) y = yMax;
	}
	/****************************************************************************************/
	public void setPosition(int x, int y) {
		// La nueva posicion debe ser multiplo del tileWidth en X y tileHeight en Y
		if (x % tileWidthSize == 0 && y % tileHeightSize == 0) {
			this.x = x;
			this.y = y;

		} else {
			Point2D.Double multiple = 
					Multiple.findPointCloserTo(
							new Point2D.Double(x,y), 
							new Point2D.Double(tileWidthSize,tileHeightSize));
			this.x = (int) multiple.x;
			this.y = (int) multiple.y;
		}	
		
		fixBounds();
	}
	
	/****************************************************************************************/
	public void update(){
		 setPosition(x, y);
	}
	/****************************************************************************************/
	public void draw(Graphics2D g) {
		// Pintamos el fondo de gris
		g.setColor(Color.gray);
		g.fillRect(0, 0, GamePanel.RESOLUTION_WIDTH,
						GamePanel.RESOLUTION_HEIGHT);

		coorMapTopLeft = absoluteToMap(x, y);
		coorMapTopRight = absoluteToMap(x + RESOLUTION_WIDTH_FIX, y);
		coorMapBottomLeft = absoluteToMap(x, y + RESOLUTION_HEIGHT_FIX);
		coorMapBottomRight = absoluteToMap(x + RESOLUTION_WIDTH_FIX, 
				y + RESOLUTION_HEIGHT_FIX);
		
		//Desplazamos cada esquina para tener el buffer con un poquito mas
		coorMapTopLeft = TileWalk.walkTo("NW", coorMapTopLeft, 3);
		coorMapTopRight = TileWalk.walkTo("NE", coorMapTopRight, 3);
		coorMapBottomLeft = TileWalk.walkTo("SW", coorMapBottomLeft, 3);
		coorMapBottomRight = TileWalk.walkTo("SE", coorMapBottomRight, 3);

		//Desplazamos las esquinas inferiores 2 pasos al sur para compensar por los objetos altos
		coorMapBottomLeft = TileWalk.walkTo("S", coorMapBottomLeft, 2);
		coorMapBottomRight = TileWalk.walkTo("S", coorMapBottomRight, 2);

		// banderas de dibujado
		boolean completed, completedRow;
		Point2D.Double firstTileOfRowToDraw, finalTileOfRowToDraw, currentTile;
		Point2D.Double coorAbsolute;
		int rowCounter = 0;

		completed = false;
		firstTileOfRowToDraw = coorMapTopLeft;
		finalTileOfRowToDraw = coorMapTopRight;		
		
		// Para cada fila
		while (!completed) {
			completedRow = false;
			// Seleccionamos la primera casilla
			currentTile = firstTileOfRowToDraw;
			
			// Para cada casilla
			while (!completedRow) {
				
				// Comprobamos que no sea transparente para pintarlo
				if (currentTile.x >= 0 && currentTile.y >= 0
					&& currentTile.x < numColMap
					&& currentTile.y < numRowsMap) {
				
					coorAbsolute = mapToAbsolute(currentTile.x,currentTile.y);
					
					g.drawImage(
						tiles[map[(int) currentTile.y][(int) currentTile.x] - 1].getImage(), 
						(int) (coorAbsolute.x - this.x), 
						(int) (coorAbsolute.y - this.y),
						null);
				}
				
				// Si llego al final de la fila nos salimos
				if (	currentTile.x == finalTileOfRowToDraw.x
					&& currentTile.y == finalTileOfRowToDraw.y)
					completedRow = true;
				else 
					currentTile = TileWalk.walkTo("E", currentTile,1);
			
			}
			
			// Comprobamos si la fila recorrida era la ultima
			if (	firstTileOfRowToDraw.x > coorMapBottomLeft.x && 
				firstTileOfRowToDraw.y > coorMapBottomLeft.y &&
				finalTileOfRowToDraw.x > coorMapBottomRight.x && 
				finalTileOfRowToDraw.y > coorMapBottomRight.y)
				completed = true;
			
			else {
				// Si no lo era, movemos las casillas de inicio y fin
				// hacia abajo para comenzar con la siguiente

				if (rowCounter % 2 != 0) {
					// Fila impar
					firstTileOfRowToDraw = TileWalk.walkTo("SW",
							firstTileOfRowToDraw, 1);
					finalTileOfRowToDraw = TileWalk.walkTo("SE", finalTileOfRowToDraw, 1);

				} else {
					// Fila par
					firstTileOfRowToDraw = TileWalk.walkTo("SE",
							firstTileOfRowToDraw, 1);
					finalTileOfRowToDraw = TileWalk.walkTo("SW", finalTileOfRowToDraw, 1);

				}
				rowCounter++;
			}
		}
	
	}
	/****************************************************************************************/
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getNumColMap() {
		return numColMap;
	}
	
	public int getNumRowsMap() {
		return numRowsMap;
	}
	
	public int getXMin() {
		return xMin;
	}
	
	public int getYMin() {
		return yMin;
	}
	
	public int getXMax() {
		return xMax;
	}
	
	public int getYMax() {
		return yMax;
	}
	
	public int[][] getMap(){
		return map;
	}
	
	public Set<Integer> getUnlockedTiles() {
		return unlockedTiles;
	}
}
