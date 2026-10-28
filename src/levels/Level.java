	package levels;
	
	import java.awt.Color;
	import java.awt.Point;
	import java.awt.image.BufferedImage;
	import java.util.ArrayList;
	
	import entities.Crabby;
	import entities.Pinkstar;
	import entities.Shark;
	import entities.NPC;
	
	import main.Game;
	import objects.BackgroundTree;
	import objects.Cannon;
	import objects.GameContainer;
	import objects.Grass;
	import objects.Potion;
	import objects.Key;
	import objects.SkipLevelObject;
	import objects.Spike;
	
	import objects.ItemA;
	import objects.ItemB;
	import objects.ItemC;
	import objects.ItemD;
	import objects.ItemE;

	
	
	import static utilz.Constants.EnemyConstants.*;
	import static utilz.Constants.ObjectConstants.*;
	
	public class Level {
	
		private BufferedImage img;
		private int[][] lvlData;
	
		private ArrayList<Crabby> crabs = new ArrayList<>();
		private ArrayList<Pinkstar> pinkstars = new ArrayList<>();
		private ArrayList<Shark> sharks = new ArrayList<>();
		private ArrayList<Potion> potions = new ArrayList<>();
		private ArrayList<Spike> spikes = new ArrayList<>();
		private ArrayList<GameContainer> containers = new ArrayList<>();
		private ArrayList<Cannon> cannons = new ArrayList<>();
		private ArrayList<BackgroundTree> trees = new ArrayList<>();
		private ArrayList<Grass> grass = new ArrayList<>();
		private ArrayList<SkipLevelObject> skipLevelObjects = new ArrayList<>();
		
		private ArrayList<NPC> npcs = new ArrayList<>();
		
		private ArrayList<Key> keys = new ArrayList<>();
		
		private ArrayList<ItemA> itemsA = new ArrayList<>();
		private ArrayList<ItemB> itemsB = new ArrayList<>();
		private ArrayList<ItemC> itemsC = new ArrayList<>();
		private ArrayList<ItemD> itemsD = new ArrayList<>();
		private ArrayList<ItemE> itemsE = new ArrayList<>();

	
	
	
		private int lvlTilesWide;
		private int maxTilesOffset;
		private int maxLvlOffsetX;
		private Point playerSpawn;
	
		public Level(BufferedImage img) {
			this.img = img;
			lvlData = new int[img.getHeight()][img.getWidth()];
			loadLevel();
			calcLvlOffsets();
		}
	
		private void loadLevel() {
	
			//Set<Color> uniqueColors = new HashSet<>();
			
			for (int y = 0; y < img.getHeight(); y++)
				for (int x = 0; x < img.getWidth(); x++) {
					Color c = new Color(img.getRGB(x, y));
					//uniqueColors.add(c);
					int red = c.getRed();
					int green = c.getGreen();
					int blue = c.getBlue();
	
					loadLevelData(red, x, y);
					loadEntities(green, x, y);
					loadObjects(blue, x, y);
				}
			/*for (Color color : uniqueColors) {
	            System.out.println("Unique Color: " + color);
	        }*/
		}
	
		private void loadLevelData(int redValue, int x, int y) {
			if (redValue >= 50)
				lvlData[y][x] = 0;
			else
				lvlData[y][x] = redValue;
			switch (redValue) {
			case 0, 1, 2, 3, 30, 31, 33, 34, 35, 36, 37, 38, 39 -> 
			grass.add(new Grass((int) (x * Game.TILES_SIZE), (int) (y * Game.TILES_SIZE) - Game.TILES_SIZE, getRndGrassType(x)));
			}
		}
	
		private int getRndGrassType(int xPos) {
			return xPos % 2;
		}
	
		private void loadEntities(int greenValue, int x, int y) {
			switch (greenValue) {
			case CRABBY -> {crabs.add(new Crabby(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
			System.out.println("position: " + x * Game.TILES_SIZE +"------------" +  y * Game.TILES_SIZE + "  -------");
										}
			case PINKSTAR -> pinkstars.add(new Pinkstar(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
			case SHARK -> sharks.add(new Shark(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
			case 100 -> playerSpawn = new Point(x * Game.TILES_SIZE, y * Game.TILES_SIZE);
			case NPC -> {
	            NPC npc = new NPC(x * Game.TILES_SIZE, y * Game.TILES_SIZE);
	            npcs.add(npc);
	            System.out.println("NPC added at: " + x * Game.TILES_SIZE + ", " + y * Game.TILES_SIZE); // Add this line
	        }
			}
		}
	
		private void loadObjects(int blueValue, int x, int y) {
			switch (blueValue) {
			case RED_POTION, BLUE_POTION -> potions.add(new Potion(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
			case BOX, BARREL -> containers.add(new GameContainer(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue,false));
			case SPIKE -> spikes.add(new Spike(x * Game.TILES_SIZE, y * Game.TILES_SIZE, SPIKE));
			case CANNON_LEFT, CANNON_RIGHT -> cannons.add(new Cannon(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
			case TREE_ONE, TREE_TWO, TREE_THREE -> trees.add(new BackgroundTree(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
	        case DOOR ->skipLevelObjects.add(new SkipLevelObject(x * Game.TILES_SIZE, y * Game.TILES_SIZE)); 
	        case KEY -> keys.add(new Key(x * Game.TILES_SIZE, y * Game.TILES_SIZE, KEY));
	        case ITEM_A -> itemsA.add(new ItemA(x * Game.TILES_SIZE, y * Game.TILES_SIZE, ITEM_A));
	        case ITEM_B -> itemsB.add(new ItemB(x * Game.TILES_SIZE, y * Game.TILES_SIZE, ITEM_B));
	        case ITEM_C -> {itemsC.add(new ItemC(x * Game.TILES_SIZE, y * Game.TILES_SIZE, ITEM_C));
	        System.out.println(x * Game.TILES_SIZE+"++++++"+ y * Game.TILES_SIZE);}
			}
		}
		
		
		public ArrayList<SkipLevelObject> getSkipLevelObjects() {
		    return skipLevelObjects;
		}
		public ArrayList<Key> getKeys() {
		    return keys;
		}
		public ArrayList<ItemA> getItemsA() {
		    return itemsA;
		}
		public ArrayList<ItemB> getItemsB() {
		    return itemsB;
		}
		public ArrayList<ItemC> getItemsC() {
		    return itemsC;
		}
		public ArrayList<ItemD> getItemsD() {
		    return itemsD;
		}
		public ArrayList<ItemE> getItemsE() {
		    return itemsE;
		}
		
		private void calcLvlOffsets() {
			lvlTilesWide = img.getWidth();
			maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
			maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
		}
	
		public int getSpriteIndex(int x, int y) {
			return lvlData[y][x];
		}
	
		public int[][] getLevelData() {
			return lvlData;
		}
	
		public int getLvlOffset() {
			return maxLvlOffsetX;
		}
	
		public Point getPlayerSpawn() {
			return playerSpawn;
		}
	
		public ArrayList<Crabby> getCrabs() {
			return crabs;
		}
	
		public ArrayList<Shark> getSharks() {
			return sharks;
		}
	
		public ArrayList<Potion> getPotions() {
			return potions;
		}
	
		public ArrayList<GameContainer> getContainers() {
			return containers;
		}
	
		public ArrayList<Spike> getSpikes() {
			return spikes;
		}
	
		public ArrayList<Cannon> getCannons() {
			return cannons;
		}
	
		public ArrayList<Pinkstar> getPinkstars() {
			return pinkstars;
		}
	
		public ArrayList<BackgroundTree> getTrees() {
			return trees;
		}
	
		public ArrayList<Grass> getGrass() {
			return grass;
		}
		public ArrayList<NPC> getNPCs() {
			return npcs; 
		}
	
	
	}
