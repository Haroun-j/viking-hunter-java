	package gamestates;
	
	import java.awt.Color;
	import java.awt.Graphics;
	import java.awt.event.KeyEvent;
	import java.awt.event.MouseEvent;
	import java.awt.geom.Rectangle2D;
	import java.awt.image.BufferedImage;
	import java.util.Random;
	import java.util.ArrayList;
	import java.util.List;
	
	import entities.EnemyManager;
	import entities.NPC;
	import entities.Player;
	import levels.LevelManager;
	import main.Game;
	import objects.Key;
	
	import objects.ItemA;
	import objects.ItemB;
	import objects.ItemC;
	import objects.ItemD;
	import objects.ItemE;
	
	
	import objects.ObjectManager;
	import objects.SkipLevelObject;
	import ui.GameCompletedOverlay;
	import ui.GameOverOverlay;
	import ui.LevelCompletedOverlay;
	import ui.NPCInventoryOverlay;
	import ui.PauseOverlay;
	import utilz.LoadSave;
	import effects.DialogueEffect;
	import effects.Rain;
	
	import java.awt.Font;
	import java.awt.FontMetrics;
	
	import static utilz.Constants.Environment.*;
	import static utilz.Constants.Dialogue.*;
	import static utilz.Constants.ObjectConstants.*;
	
	import ui.InventoryButton;
	import ui.InventoryOverlay;
	import java.awt.Point;
	
	public class Playing extends State implements Statemethods {
	
		private Player player;
		private LevelManager levelManager;
		private EnemyManager enemyManager;
		private ObjectManager objectManager;
		private NPC interactingNPC;
		private boolean showNPCInventory = false;
		private PauseOverlay pauseOverlay;
		private GameOverOverlay gameOverOverlay;
		private GameCompletedOverlay gameCompletedOverlay;
		private LevelCompletedOverlay levelCompletedOverlay;
		private Rain rain;
	
		private boolean paused = false;
	
		private int xLvlOffset;
		private int leftBorder = (int) (0.25 * Game.GAME_WIDTH);
		private int rightBorder = (int) (0.75 * Game.GAME_WIDTH);
		private int maxLvlOffsetX;
	
		private BufferedImage backgroundImg, bigCloud, smallCloud, shipImgs[];
		private BufferedImage[] questionImgs, exclamationImgs;
		private ArrayList<DialogueEffect> dialogEffects = new ArrayList<>();
	
		private int[] smallCloudsPos;
		private Random rnd = new Random();
	
		private boolean gameOver;
		private boolean lvlCompleted;
		private boolean gameCompleted;
		private boolean playerDying;
		private boolean drawRain;
		private BufferedImage skullIcon;
	
		// Ship will be decided to drawn here. It's just a cool addition to the game
		// for the first level. Hinting on that the player arrived with the boat.
	
		// If you would like to have it on more levels, add a value for objects when
		// creating the level from lvlImgs. Just like any other object.
	
		// Then play around with position values so it looks correct depending on where
		// you want
		// it.
	
		private boolean drawShip = true;
		private int shipAni, shipTick, shipDir = 1;
		private float shipHeightDelta, shipHeightChange = 0.05f * Game.SCALE;
		private int initialScore;
		private boolean godMode = false;
		
		private InventoryButton inventoryButton;
		private InventoryOverlay inventoryOverlay;
		private boolean showInventory = false;
		
	    private boolean dialogueVisible = false;
	    
	    private List<NPC> interactedNPCs = new ArrayList<>();
	
		public Playing(Game game) {
			super(game);
			initClasses();
			addTemporarySkipLevelObject();
			backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
			bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
			smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
			smallCloudsPos = new int[8];
			for (int i = 0; i < smallCloudsPos.length; i++)
				smallCloudsPos[i] = (int) (90 * Game.SCALE) + rnd.nextInt((int) (100 * Game.SCALE));
	
			shipImgs = new BufferedImage[4];
			BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SHIP);
			for (int i = 0; i < shipImgs.length; i++)
				shipImgs[i] = temp.getSubimage(i * 78, 0, 78, 72);
	
			loadDialogue();
			calcLvlOffset();
			loadStartLevel();
			setDrawRainBoolean();
			skullIcon = LoadSave.GetSpriteAtlas(LoadSave.SKULL_ICON);
		    //addItemCManually();
		    initializePlayerInventory(); 
	
	
		}
		private void initializePlayerInventory() {
		    BufferedImage key = LoadSave.GetSpriteAtlas(LoadSave.KEY_SPRITE);
		    BufferedImage itemAImage = LoadSave.GetSpriteAtlas(LoadSave.ITEM_A_SPRITE);
		    BufferedImage itemCImage = LoadSave.GetSpriteAtlas(LoadSave.ITEM_C_SPRITE);
		    BufferedImage itemDImage = LoadSave.GetSpriteAtlas(LoadSave.ITEM_D_SPRITE);
		    BufferedImage itemEImage = LoadSave.GetSpriteAtlas(LoadSave.ITEM_E_SPRITE);
	
	
	
		    inventoryOverlay.addItem("Key", key);
		    inventoryOverlay.addItem("ItemA", itemAImage);
		    inventoryOverlay.addItem("ItemC", itemCImage);
		    inventoryOverlay.addItem("ItemD", itemDImage);
		    inventoryOverlay.addItem("ItemE", itemEImage);
	
		    
		}
		private void loadDialogue() {
			loadDialogueImgs();
	
			// Load dialogue array with premade objects, that gets activated when needed.
			// This is a simple
			// way of avoiding ConcurrentModificationException error. (Adding to a list that
			// is being looped through.
	
			for (int i = 0; i < 10; i++)
				dialogEffects.add(new DialogueEffect(0, 0, EXCLAMATION));
			for (int i = 0; i < 10; i++)
				dialogEffects.add(new DialogueEffect(0, 0, QUESTION));
	
			for (DialogueEffect de : dialogEffects)
				de.deactive();
		}
	
		private void loadDialogueImgs() {
			BufferedImage qtemp = LoadSave.GetSpriteAtlas(LoadSave.QUESTION_ATLAS);
			questionImgs = new BufferedImage[5];
			for (int i = 0; i < questionImgs.length; i++)
				questionImgs[i] = qtemp.getSubimage(i * 14, 0, 14, 12);
	
			BufferedImage etemp = LoadSave.GetSpriteAtlas(LoadSave.EXCLAMATION_ATLAS);
			exclamationImgs = new BufferedImage[5];
			for (int i = 0; i < exclamationImgs.length; i++)
				exclamationImgs[i] = etemp.getSubimage(i * 14, 0, 14, 12);
		}
	
		public void loadNextLevel() {
			levelManager.setLevelIndex(levelManager.getLevelIndex() + 1);
		    if (levelManager.getLevelIndex() >= levelManager.getAmountOfLevels()) {
		        gameCompleted = true;
		        levelManager.setLevelIndex(0); 
		    }
			levelManager.loadNextLevel();
			player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
			initialScore = player.getScore();
			resetAll(false);
			drawShip = false;
		}
	
		private void loadStartLevel() {
			enemyManager.loadEnemies(levelManager.getCurrentLevel());
			objectManager.loadObjects(levelManager.getCurrentLevel());
			initialScore = player.getScore();
		}
	
		private void calcLvlOffset() {
			maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
		}
		public void handleCrabbyKilled() {
		    if (enemyManager.isLastCrabby()) {
		        Point lastCrabbyPosition = enemyManager.getLastCrabbyPosition();
		        if (lastCrabbyPosition != null) {
		            System.out.println("Last crabby killed at position: " + lastCrabbyPosition);
		            ItemC itemC = new ItemC(lastCrabbyPosition.x, lastCrabbyPosition.y, ITEM_C);
		            objectManager.addItemC(itemC);
		        } else {
		            System.out.println("No last crabby position found.");
		        }
		    } else {
		        System.out.println("Not the last crabby.");
		    }
		}
	
	
		/*public void addItemCManually() {
		    ItemC itemC = new ItemC(500, 500, ITEM_C); // Add ItemC at position (500, 500)
		    objectManager.addItemC(itemC);
		    System.out.println("ItemC manually added at position: 500, 500");
		}*/
	
	
	
		private void initClasses() {
			levelManager = new LevelManager(game);
			enemyManager = new EnemyManager(this);
			objectManager = new ObjectManager(this);
	
			player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
			player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
			player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
	
			pauseOverlay = new PauseOverlay(this);
			gameOverOverlay = new GameOverOverlay(this);
			levelCompletedOverlay = new LevelCompletedOverlay(this);
			gameCompletedOverlay = new GameCompletedOverlay(this);
	
			rain = new Rain();
			
	        BufferedImage inventoryButtonImg = LoadSave.GetSpriteAtlas(LoadSave.INVENTORY_BUTTON);
	        inventoryButton = new InventoryButton(50, 170, 70, 70, inventoryButtonImg); 
	        inventoryOverlay = new InventoryOverlay(LoadSave.GetSpriteAtlas(LoadSave.INVENTORY_IMAGE),120,200);
	        
	
		}
	
		@Override
		public void update() {
			if (paused)
				pauseOverlay.update();
			
			else if (lvlCompleted)
				levelCompletedOverlay.update();
			else if (gameCompleted)
				gameCompletedOverlay.update();
			else if (gameOver)
				gameOverOverlay.update();
			else if (playerDying)
				player.update();
			else {
				updateDialogue();
				if (drawRain)
					rain.update(xLvlOffset);
				levelManager.update();
				objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
				player.update();
				enemyManager.update(levelManager.getCurrentLevel().getLevelData());
				checkCloseToBorder();
				if (drawShip)
					updateShipAni();
				
	            inventoryButton.update();
	            
	            if (interactingNPC != null) {
	                interactingNPC.update();
	                checkPlayerNPCProximity();
	            }
		
			}
		}
		private void checkPlayerNPCProximity() {
		    if (interactingNPC != null) {
		        Rectangle2D.Float npcHitbox = interactingNPC.getHitbox();
		        Rectangle2D.Float playerHitbox = player.getHitbox();
		        if (npcHitbox.intersects(playerHitbox)) {
		            if (!interactedNPCs.contains(interactingNPC)) {
		                interactedNPCs.add(interactingNPC);
		                System.out.println("First interaction with NPC.");
		            } else if (inventoryOverlay.isItemInUse("ItemC")) {
		                interactingNPC.getNpcInventoryOverlay().setVisible(true);
		                inventoryOverlay.useItem("ItemC");
		                System.out.println("NPC inventory displayed using ItemC.");
		            }
		            interactingNPC.setShowDialogue(true);
		            dialogueVisible = true;
		        } else {
		            interactingNPC.setShowDialogue(false);
		            dialogueVisible = false;
		        }
		    }
		}

	
	
		private void stealItemFromNPC(int itemIndex) {
		    if (!inventoryOverlay.isItemInUse("ItemE")) {
		        System.out.println("Cannot steal item. ItemE is not selected.");
		        return;
		    }

		    if (interactingNPC != null) {
		        NPCInventoryOverlay npcInventory = interactingNPC.getNpcInventoryOverlay();
		        List<NPCInventoryOverlay.InventoryItem> npcItems = npcInventory.getCollectedItems();

		        if (npcItems.size() > itemIndex && inventoryOverlay.getCollectedItems().size() < 5) {
		            NPCInventoryOverlay.InventoryItem item = npcItems.get(itemIndex);
		            inventoryOverlay.addItem(item.getId(), item.getImage());
		            npcInventory.removeItem(item.getId());
		            System.out.println("Stolen item: " + item.getId());
		        } else {
		            System.out.println("Cannot steal item. Either the item does not exist or the player's inventory is full.");
		        }
		    }
		}



		private void giveItemToPlayer(NPC npc) {
		    NPCInventoryOverlay npcInventory = npc.getNpcInventoryOverlay();
		    List<NPCInventoryOverlay.InventoryItem> npcItems = npcInventory.getCollectedItems();
	
		    if (!npcItems.isEmpty() && inventoryOverlay.getCollectedItems().isEmpty()) {
		        NPCInventoryOverlay.InventoryItem item = npcItems.get(0);
		        //inventoryOverlay.addItem(item.getId(), item.getImage());
		        //npcInventory.removeItem(item.getId());
		        npc.setDialogueText("bonjour, je vois que votre inventaire est vide, voici un item");
		    }
		}
	
		public boolean isLevelCompleted() {
		    return lvlCompleted;
		}
	
		private void updateShipAni() {
			shipTick++;
			if (shipTick >= 35) {
				shipTick = 0;
				shipAni++;
				if (shipAni >= 4)
					shipAni = 0;
			}
	
			shipHeightDelta += shipHeightChange * shipDir;
			shipHeightDelta = Math.max(Math.min(10 * Game.SCALE, shipHeightDelta), 0);
	
			if (shipHeightDelta == 0)
				shipDir = 1;
			else if (shipHeightDelta == 10 * Game.SCALE)
				shipDir = -1;
	
		}
	
		private void updateDialogue() {
			for (DialogueEffect de : dialogEffects)
				if (de.isActive())
					de.update();
		}
	
		private void drawDialogue(Graphics g, int xLvlOffset) {
			for (DialogueEffect de : dialogEffects)
				if (de.isActive()) {
					if (de.getType() == QUESTION)
						g.drawImage(questionImgs[de.getAniIndex()], de.getX() - xLvlOffset, de.getY(), DIALOGUE_WIDTH, DIALOGUE_HEIGHT, null);
					else
						g.drawImage(exclamationImgs[de.getAniIndex()], de.getX() - xLvlOffset, de.getY(), DIALOGUE_WIDTH, DIALOGUE_HEIGHT, null);
				}
		}
	
		public void addDialogue(int x, int y, int type) {
			dialogEffects.add(new DialogueEffect(x, y - (int) (Game.SCALE * 15), type));
			for (DialogueEffect de : dialogEffects)
				if (!de.isActive())
					if (de.getType() == type) {
						de.reset(x, -(int) (Game.SCALE * 15));
						return;
					}
		}
	
		private void checkCloseToBorder() {
			int playerX = (int) player.getHitbox().x;
			int diff = playerX - xLvlOffset;
	
			if (diff > rightBorder)
				xLvlOffset += diff - rightBorder;
			else if (diff < leftBorder)
				xLvlOffset += diff - leftBorder;
	
			xLvlOffset = Math.max(Math.min(xLvlOffset, maxLvlOffsetX), 0);
		}
	
		@Override
		public void draw(Graphics g) {
			g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
			drawClouds(g);
			if (drawRain)
				rain.draw(g, xLvlOffset);
	
			if (drawShip)
				g.drawImage(shipImgs[shipAni], (int) (100 * Game.SCALE) - xLvlOffset, (int) ((288 * Game.SCALE) + shipHeightDelta), (int) (78 * Game.SCALE), (int) (72 * Game.SCALE), null);
	
			levelManager.draw(g, xLvlOffset);
			objectManager.draw(g, xLvlOffset);
			enemyManager.draw(g, xLvlOffset);
			player.render(g, xLvlOffset);
			objectManager.drawBackgroundTrees(g, xLvlOffset);
			///drawDialogue(g, xLvlOffset);
			drawMonsterKillCount(g);
	        inventoryButton.draw(g);
	
	        /*if (inventoryButton != null) {
	            inventoryButton.draw(g);
	        } else {
	            System.out.println("Inventory button is null.");
	        }*/
	
	        if (showInventory) {
	            inventoryOverlay.draw(g);
	        }	
	        
	        if (showNPCInventory && interactingNPC != null) {
	            interactingNPC.getNpcInventoryOverlay().draw(g);
	        }
	        
			if (paused) {
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
				pauseOverlay.draw(g);
			} else if (gameOver)
				gameOverOverlay.draw(g);
			else if (lvlCompleted)
				levelCompletedOverlay.draw(g);
			else if (gameCompleted)
				gameCompletedOverlay.draw(g);
	
		}
		private void drawMonsterKillCount(Graphics g) {
		    int killedMonsters = enemyManager.getKilledMonsters();
		    int totalMonsters = enemyManager.getTotalMonsters();
		    String monsterCountText = killedMonsters + " / " + totalMonsters;
	
		    g.setColor(Color.WHITE);
		    g.setFont(new Font("Arial", Font.BOLD, (int) (20 * Game.SCALE)-5));
		    FontMetrics fm = g.getFontMetrics();
		    int textWidth = fm.stringWidth(monsterCountText);
	
		    int x = (Game.GAME_WIDTH / 2) - (textWidth / 2) + 35;
		    int y = (int) (20 * Game.SCALE) + 25;
		    g.drawString(monsterCountText, x, y);
		    g.drawImage(skullIcon, x+75, y-32, 55, 40, null);
		}
		private void drawClouds(Graphics g) {
			for (int i = 0; i < 4; i++)
				g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int) (xLvlOffset * 0.3), (int) (204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);
	
			for (int i = 0; i < smallCloudsPos.length; i++)
				g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int) (xLvlOffset * 0.7), smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
		}
		
		public void addTemporarySkipLevelObject() {
		    objectManager.addSkipLevelObject(new SkipLevelObject(200, 200)); 
		}
		
		public void setGameCompleted() {
			gameCompleted = true;
			Gamestate.state = Gamestate.CREDITS;
		}
	
		public void resetGameCompleted() {
			gameCompleted = false;
		}
	
		public void resetAll(boolean resetScore) {
			gameOver = false;
			paused = false;
			lvlCompleted = false;
			playerDying = false;
			drawRain = false;
	
			setDrawRainBoolean();
	
			player.resetAll();
			enemyManager.resetAllEnemies();
			objectManager.resetAllObjects();
			dialogEffects.clear();
			resetKilledMonsters(); 
		    //inventoryOverlay.reset();
		    
		    
		    if (resetScore) {
		        player.resetScore(); 
		    } else {
		        player.setScore(initialScore); 
		    }
		}
		public void resetAll() {
		    resetAll(false); 
		}
		private void setDrawRainBoolean() {
			// This method makes it rain 20% of the time you load a level.
			if (rnd.nextFloat() >= 0.8f)
				drawRain = true;
		}
	
		public void setGameOver(boolean gameOver) {
			this.gameOver = gameOver;
		    if (gameOver) {
		        player.setScore(initialScore); 
		    }
		}
	
		public void checkObjectHit(Rectangle2D.Float attackBox) {
			objectManager.checkObjectHit(attackBox);
			
		    for (Key key : levelManager.getCurrentLevel().getKeys()) {
		        if (key.getHitbox().intersects(attackBox)) {
		            collectItem("key", key.getSprite()); 
		            objectManager.removeKey(key); 
		            System.out.println("Key collected.");
		        }
		    }
		    for (ItemA itemA : levelManager.getCurrentLevel().getItemsA()) {
		        if (itemA.getHitbox().intersects(attackBox)) {
		            collectItem("Item A", itemA.getSprite()); 
		            objectManager.removeItemA(itemA); 
		            System.out.println("itemA collected.");
		        }
		    }
		    for (ItemB itemB : levelManager.getCurrentLevel().getItemsB()) {
		        if (itemB.getHitbox().intersects(attackBox)) {
		            collectItem("Item B",itemB.getSprite()); 
		            objectManager.removeItemB(itemB); 
		            System.out.println("itemB collected.");
		        }
		    }
		    for (ItemC itemC : levelManager.getCurrentLevel().getItemsC()) {
		        if (itemC.getHitbox().intersects(attackBox)) {
		            collectItem("Item C",itemC.getSprite()); 
		            objectManager.removeItemC(itemC); 
		            System.out.println("itemC collected.");
		        }
		    }
		    for (ItemD itemD : levelManager.getCurrentLevel().getItemsD()) {
		        if (itemD.getHitbox().intersects(attackBox)) {
		            collectItem("Item D",itemD.getSprite()); 
		            objectManager.removeItemD(itemD); 
		            System.out.println("itemC collected.");
		        }
		    }
		    for (ItemE itemE : levelManager.getCurrentLevel().getItemsE()) {
		        if (itemE.getHitbox().intersects(attackBox)) {
		            collectItem("Item E",itemE.getSprite()); 
		            objectManager.removeItemE(itemE); 
		            System.out.println("itemC collected.");
		        }
		    }
		}
	
		public void checkEnemyHit(Rectangle2D.Float attackBox) {
		    int damage = 20;
		    boolean hit = enemyManager.checkEnemyHit(attackBox, damage);
		    if (hit) {
		        player.incrementScore(damage);
		    }	
		    }
	
		public void checkPotionTouched(Rectangle2D.Float hitbox) {
			objectManager.checkObjectTouched(hitbox);
		}
	
		public void checkSpikesTouched(Player p) {
		    if (!godMode) {
		        objectManager.checkSpikesTouched(p);
		    }
	    }
	
		// @Override
		// public void mouseClicked(MouseEvent e) {
			// if (!gameOver) {
				// if (e.getButton() == MouseEvent.BUTTON1)
					// player.setAttacking(true);
				// else if (e.getButton() == MouseEvent.BUTTON3)
			//		player.powerAttack();
			//}
		//}
		private void handleInventoryClick(MouseEvent e) {
		    for (InventoryOverlay.InventoryItem item : inventoryOverlay.getCollectedItems()) {
		        int itemX = item.getPosX();
		        int itemY = item.getPosY();
		        int itemWidth = 40;
		        int itemHeight = 40;

		        if (e.getX() >= itemX && e.getX() <= itemX + itemWidth && e.getY() >= itemY && e.getY() <= itemY + itemHeight) {
		            if (item.getId().equals("ItemC")) {
		                inventoryOverlay.toggleItemInUse(item.getId());
		                return;
		            } else if (item.getId().equals("ItemD")) {
		                gameCompleted = true;
		                Gamestate.state = Gamestate.CREDITS;
		                System.out.println("Game Completed! ItemD selected.");
		            } else {
		                inventoryOverlay.toggleItemInUse(item.getId());
		            }
		            return;
		        }
		    }
		}



	
	
	
	
	
	
		@Override
		public void keyPressed(KeyEvent e) {
			if (!gameOver && !gameCompleted && !lvlCompleted)
				switch (e.getKeyCode()) {
				case KeyEvent.VK_Q:
					player.setLeft(true);
					break;
				case KeyEvent.VK_D:
	
					player.setRight(true);
					break;
				case KeyEvent.VK_Z:
					player.setJump(true);
					break;
				case KeyEvent.VK_SPACE:
					player.setAttacking(true);
					break;
				case KeyEvent.VK_R:
					player.powerAttack();
					break;
				case KeyEvent.VK_ESCAPE:
					paused = !paused;
					break;
	            case KeyEvent.VK_I:
	                showInventory = !showInventory;
	                System.out.println("Inventory toggled by key: " + showInventory);
	                break;
	            case KeyEvent.VK_NUMPAD1:
	                stealItemFromNPC(0);
	                break;
	            case KeyEvent.VK_NUMPAD2:
	                stealItemFromNPC(1);
	                break;
				}
		}
	
		@Override
		public void keyReleased(KeyEvent e) {
			if (!gameOver && !gameCompleted && !lvlCompleted)
				switch (e.getKeyCode()) {
				case KeyEvent.VK_Q:
					player.setLeft(false);
					break;
				case KeyEvent.VK_D:
					player.setRight(false);
					break;
				case KeyEvent.VK_Z:
					player.setJump(false);
					break;
				case KeyEvent.VK_SPACE:
					player.setAttacking(true);
					break;
				case KeyEvent.VK_R:
					player.powerAttack();
					break;
				}
		}
	
	
		private void checkNPCInteraction(MouseEvent e) {
		    int adjustedX = e.getX() + xLvlOffset + 80;
		    int adjustedY = e.getY();
		    for (NPC npc : levelManager.getCurrentLevel().getNPCs()) {
		        Rectangle2D.Float hitbox = npc.getHitbox();
		        if (hitbox.contains(adjustedX, adjustedY)) {
		            if (inventoryOverlay.isItemInUse("ItemC")) {
		                interactWithNPC(npc);
		                inventoryOverlay.useItem("ItemC"); // Remove ItemC after use
		                System.out.println("NPC inventory displayed using ItemC.");
		            } else {
		                System.out.println("ItemC is not selected. Cannot interact with NPC.");
		            }
		            return;
		        }
		    }
		}

 



	
	
	
	
	
	    private boolean isMouseOverItem(MouseEvent e, NPCInventoryOverlay.InventoryItem item) {
	        int itemX = item.getPosX();
	        int itemY = item.getPosY();
	        int itemWidth = 40;
	        int itemHeight = 40;
	        return e.getX() >= itemX && e.getX() <= itemX + itemWidth && e.getY() >= itemY && e.getY() <= itemY + itemHeight;
	    }
	
	
	    public void interactWithNPC(NPC npc) {
	        if (this.interactingNPC != null) {
	            this.interactingNPC.getNpcInventoryOverlay().setVisible(false);
	        }
	        this.interactingNPC = npc;
	        showNPCInventory = true;
	        npc.getNpcInventoryOverlay().setVisible(true);
	        System.out.println("NPC inventory visibility: " + npc.getNpcInventoryOverlay().isVisible());
	    }

		@Override
		public void mousePressed(MouseEvent e) {
			if (gameOver)
				gameOverOverlay.mousePressed(e);
			else if (paused)
				pauseOverlay.mousePressed(e);
			else if (lvlCompleted)
				levelCompletedOverlay.mousePressed(e);
			else if (gameCompleted)
				gameCompletedOverlay.mousePressed(e);
			else if (isIn(e, inventoryButton)) {
	            inventoryButton.setMousePressed(true);
	        }else if (showInventory) {
	            handleInventoryClick(e);
	        } else {
	            checkNPCInteraction(e);
	        }
		}
	
		@Override
		public void mouseReleased(MouseEvent e) {
			if (gameOver)
				gameOverOverlay.mouseReleased(e);
			else if (paused)
				pauseOverlay.mouseReleased(e);
			else if (lvlCompleted)
				levelCompletedOverlay.mouseReleased(e);
			else if (gameCompleted)
				gameCompletedOverlay.mouseReleased(e);
		    else if (isIn(e, inventoryButton)) {
		        if (inventoryButton.isMousePressed()) {
		            showInventory = !showInventory;
		            System.out.println("Inventory toggled: " + showInventory);
		        }
		    }
		    inventoryButton.resetBools();
	
		}
	
		@Override
		public void mouseMoved(MouseEvent e) {
			inventoryButton.setMouseOver(false);
			if (gameOver)
				gameOverOverlay.mouseMoved(e);
			else if (paused)
				pauseOverlay.mouseMoved(e);
			else if (lvlCompleted)
				levelCompletedOverlay.mouseMoved(e);
			else if (gameCompleted)
				gameCompletedOverlay.mouseMoved(e);
			else if (isIn(e, inventoryButton)) {
	            inventoryButton.setMouseOver(true);
	        }
		}
		private boolean isIn(MouseEvent e, InventoryButton inventoryButton2) {
		    return inventoryButton2.getBounds().contains(e.getX(), e.getY());
		}
		
	    public void addItemToInventory(String id, BufferedImage item) {
	        inventoryOverlay.addItem(id, item);
	    }
	    
		public void setLevelCompleted(boolean levelCompleted) {
			if (levelManager.getLevelIndex() + 1 >= levelManager.getAmountOfLevels()) {
				// No more levels
				gameCompleted = true;
				levelManager.setLevelIndex(0);
				levelManager.loadNextLevel();
				resetAll();
				return;
			}
			this.lvlCompleted = levelCompleted;
		}
	
		public void setMaxLvlOffset(int lvlOffset) {
			this.maxLvlOffsetX = lvlOffset;
		}
	
		public void unpauseGame() {
			paused = false;
		}
	
		public void windowFocusLost() {
			player.resetDirBooleans();
		}
	
		public Player getPlayer() {
			return player;
		}
	
		public EnemyManager getEnemyManager() {
			return enemyManager;
		}
	
		public ObjectManager getObjectManager() {
			return objectManager;
		}
	
		public LevelManager getLevelManager() {
			return levelManager;
		}
	
		public void setPlayerDying(boolean playerDying) {
			this.playerDying = playerDying;
		}
		// Add to Playing class
		public void resetKilledMonsters() {
		    enemyManager.resetKilledMonsters();
		}
	
		public void resetScore() {
		    player.resetScore();
		}
		
		public void setGodMode(boolean godMode) {
		    this.godMode = godMode;
		    if (godMode) {
		        player.setInvincible(true);
		    } else {
		        player.setInvincible(false);
		    }
		}
		
		public void collectItem(String id,BufferedImage itemImage) {
		    addItemToInventory(id,itemImage);
		}
		
		public InventoryOverlay getInventoryOverlay() {
		    return inventoryOverlay;
		}
	
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}