package objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Enemy;
import entities.Player;
import gamestates.Playing;
import levels.Level;
import main.Game;
import utilz.LoadSave;
import static utilz.Constants.ObjectConstants.*;
import static utilz.HelpMethods.CanCannonSeePlayer;
import static utilz.HelpMethods.IsProjectileHittingLevel;
import static utilz.Constants.Projectiles.*;

public class ObjectManager {

	private Playing playing;
	private BufferedImage[][] potionImgs, containerImgs;
	private BufferedImage[] cannonImgs, grassImgs;
	private BufferedImage[][] treeImgs;
	private BufferedImage spikeImg, cannonBallImg;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private ArrayList<Projectile> projectiles = new ArrayList<>();
    private ArrayList<SkipLevelObject> skipLevelObjects = new ArrayList<>();
    
    private BufferedImage[][] keyImgs, itemAImgs, itemBImgs, itemCImgs;
    
    private ArrayList<Key> keys = new ArrayList<>();
    private boolean hasKey = false;
    private BufferedImage keyImage;
    
    private ArrayList<ItemA> itemsA = new ArrayList<>();
    private boolean hasItemA = false;
    private BufferedImage itemAImage;
    
    private ArrayList<ItemB> itemsB = new ArrayList<>();
    private boolean hasItemB = false;
    private BufferedImage itemBImage;
    
    private ArrayList<ItemC> itemsC = new ArrayList<>();
    private boolean hasItemC = false;
    private BufferedImage itemCImage;
    
    private ArrayList<ItemD> itemsD = new ArrayList<>();
    private boolean hasItemD = false;
    private BufferedImage itemDImage;
    
    private ArrayList<ItemE> itemsE = new ArrayList<>();
    private boolean hasItemE = false;
    private BufferedImage itemEImage;



	private Level currentLevel;

	public ObjectManager(Playing playing) {
		this.playing = playing;
		currentLevel = playing.getLevelManager().getCurrentLevel();
		loadImgs();
	}

	public void checkSpikesTouched(Player p) {
		/*for (Spike s : currentLevel.getSpikes())
			if (s.getHitbox().intersects(p.getHitbox()))
				p.kill();*/
	    if (!playing.getInventoryOverlay().isItemInUse("Item B")) {
	        for (Spike s : currentLevel.getSpikes()) {
	            if (s.getHitbox().intersects(p.getHitbox())) {
	                p.kill();
	            }
	        }
	    }
	}

	public void checkSpikesTouched(Enemy e) {
		for (Spike s : currentLevel.getSpikes())
			if (s.getHitbox().intersects(e.getHitbox()))
				e.hurt(200);
	}

	public void checkObjectTouched(Rectangle2D.Float hitbox) {
		for (Potion p : potions)
			if (p.isActive()) {
				if (hitbox.intersects(p.getHitbox())) {
					p.setActive(false);
					applyEffectToPlayer(p);
				}
			}
	    for (SkipLevelObject s : skipLevelObjects) {
	        if (s.isActive() && hitbox.intersects(s.getHitbox())) {
	            //System.out.println("Player touched skip level object.");
	            if (hasKey && !s.isOpen()) {
	                System.out.println("Player has key. Opening door.");
	                s.open();
                    if (keyImage != null) {
                        playing.getInventoryOverlay().removeItem("key");
                        hasKey = false;
                        keyImage = null;
                    }
	            }
	            if (s.isFullyOpen() && !playing.isLevelCompleted()) {
	                System.out.println("Door is fully open. Completing level.");
	                playing.setLevelCompleted(true);
	            }
	        }
	    }
	    for (Key k : keys)
	        if (k.isActive() && hitbox.intersects(k.getHitbox())) {
	            k.setActive(false);
	            hasKey = true;
                keyImage = k.getSprite();
	            playing.addItemToInventory("key",keyImage); 	
	            System.out.println("Player collected key.");

	        }
	    for (ItemA A : itemsA)
	        if (A.isActive() && hitbox.intersects(A.getHitbox())) {
	        	A.setActive(false);
	        	hasItemA = true;
	        	itemAImage = A.getSprite();
	            playing.addItemToInventory("Item A", itemAImage); 	
	            System.out.println("Player collected itemA.");
	            
	        }
	    for (ItemB B : itemsB)
	        if (B.isActive() && hitbox.intersects(B.getHitbox())) {
	        	B.setActive(false);
	        	hasItemB = true;
	        	itemBImage = B.getSprite();
	            playing.addItemToInventory("Item B",itemBImage); 	
	            System.out.println("Player collected itemB.");
	            
	        }
	    for (ItemC C : itemsC)
	        if (C.isActive() && hitbox.intersects(C.getHitbox())) {
	        	C.setActive(false);
	        	hasItemC = true;
	        	itemCImage = C.getSprite();
	            playing.addItemToInventory("Item C",itemCImage); 	
	            System.out.println("Player collected itemC.");
	            
	        }
	    for (ItemD D : itemsD)
	        if (D.isActive() && hitbox.intersects(D.getHitbox())) {
	        	D.setActive(false);
	        	hasItemD = true;
	        	itemDImage = D.getSprite();
	            playing.addItemToInventory("Item D",itemCImage); 	
	            System.out.println("Player collected itemD.");
	            
	        }
	    for (ItemE E : itemsE)
	        if (E.isActive() && hitbox.intersects(E.getHitbox())) {
	        	E.setActive(false);
	        	hasItemE = true;
	        	itemEImage = E.getSprite();
	            playing.addItemToInventory("Item E",itemCImage); 	
	            System.out.println("Player collected itemC.");
	            
	        }
	}

	public void applyEffectToPlayer(Potion p) {
		if (p.getObjType() == RED_POTION)
			playing.getPlayer().changeHealth(RED_POTION_VALUE);
		else
			playing.getPlayer().changeHealth(BLUE_POTION_VALUE);
			//playing.getPlayer().changePower(BLUE_POTION_VALUE);
	}

	public void checkObjectHit(Rectangle2D.Float attackbox) {
		for (GameContainer gc : containers)
			if (gc.isActive() && !gc.doAnimation) {
				if (gc.getHitbox().intersects(attackbox)) {
					gc.setAnimation(true);
	                if (gc.containsKey()) {
	                    keys.add(new Key((int) (gc.getHitbox().x + gc.getHitbox().width / 2), (int) (gc.getHitbox().y - gc.getHitbox().height / 2), KEY));
	                } else {
	                    int type = 0;
	                    if (gc.getObjType() == BARREL)
	                        type = 1;
	                    potions.add(new Potion((int) (gc.getHitbox().x + gc.getHitbox().width / 2), (int) (gc.getHitbox().y - gc.getHitbox().height / 2), type));
	                }
					return;
				}
			}
	}
    public boolean hasKey() {
        return hasKey;
    }
	public void loadObjects(Level newLevel) {
		currentLevel = newLevel;
		potions = new ArrayList<>(newLevel.getPotions());
		containers = new ArrayList<>(newLevel.getContainers());
        skipLevelObjects = new ArrayList<>(newLevel.getSkipLevelObjects());
	    keys = new ArrayList<>(newLevel.getKeys());
		projectiles.clear();
		hasKey = false;
        keyImage = null;
        
	    itemsA = new ArrayList<>(newLevel.getItemsA());
		hasItemA = false;
		itemAImage = null;
		
	    itemsB = new ArrayList<>(newLevel.getItemsB());
		hasItemB = false;
		itemBImage = null;
		
	    itemsC = new ArrayList<>(newLevel.getItemsC());
		hasItemC = false;
		itemCImage = null;

	}

	private void loadImgs() {
		BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImgs = new BufferedImage[2][7];

		for (int j = 0; j < potionImgs.length; j++)
			for (int i = 0; i < potionImgs[j].length; i++)
				potionImgs[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16);

		BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
		containerImgs = new BufferedImage[2][8];

		for (int j = 0; j < containerImgs.length; j++)
			for (int i = 0; i < containerImgs[j].length; i++)
				containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);

		spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);

		cannonImgs = new BufferedImage[7];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);

		for (int i = 0; i < cannonImgs.length; i++)
			cannonImgs[i] = temp.getSubimage(i * 40, 0, 40, 26);

		cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
		treeImgs = new BufferedImage[2][4];
		BufferedImage treeOneImg = LoadSave.GetSpriteAtlas(LoadSave.TREE_ONE_ATLAS);
		for (int i = 0; i < 4; i++)
			treeImgs[0][i] = treeOneImg.getSubimage(i * 39, 0, 39, 92);

		BufferedImage treeTwoImg = LoadSave.GetSpriteAtlas(LoadSave.TREE_TWO_ATLAS);
		for (int i = 0; i < 4; i++)
			treeImgs[1][i] = treeTwoImg.getSubimage(i * 62, 0, 62, 54);

		BufferedImage grassTemp = LoadSave.GetSpriteAtlas(LoadSave.GRASS_ATLAS);
		grassImgs = new BufferedImage[2];
		for (int i = 0; i < grassImgs.length; i++)
			grassImgs[i] = grassTemp.getSubimage(32 * i, 0, 32, 32);	
		
		 BufferedImage keySprite = LoadSave.GetSpriteAtlas(LoadSave.KEY_SPRITE);
		    keyImgs = new BufferedImage[1][1];
		    keyImgs[0][0] = keySprite.getSubimage(0, 0, 110, 130); 
		    
	     BufferedImage itemASprite = LoadSave.GetSpriteAtlas(LoadSave.ITEM_A_SPRITE);
		    itemAImgs = new BufferedImage[1][1];
		    itemAImgs[0][0] = itemASprite.getSubimage(0, 0, 110, 130);

	     BufferedImage itemBSprite = LoadSave.GetSpriteAtlas(LoadSave.ITEM_B_SPRITE);
		    itemBImgs = new BufferedImage[1][1];
		    itemBImgs[0][0] = itemBSprite.getSubimage(0, 0, 110, 130);

	     BufferedImage itemCSprite = LoadSave.GetSpriteAtlas(LoadSave.ITEM_C_SPRITE);
		    itemCImgs = new BufferedImage[1][1];
		    itemCImgs[0][0] = itemCSprite.getSubimage(0, 0, 110, 130);

		}
	
	public ArrayList<SkipLevelObject> getSkipLevelObjects() {
	    return skipLevelObjects;
	}

	public void update(int[][] lvlData, Player player) {
		updateBackgroundTrees();
		for (Potion p : potions)
			if (p.isActive())
				p.update();

		for (GameContainer gc : containers)
			if (gc.isActive())
				gc.update();
		
	    for (Key k : keys)
	        if (k.isActive())
	            k.update();
	    for (ItemA A : itemsA)
	        if (A.isActive())
	            A.update();
	    for (ItemB B : itemsB)
	        if (B.isActive())
	            B.update();
	    for (ItemC C : itemsC)
	        if (C.isActive()) {
	            C.update();
        //System.out.println("Updating ItemC at position: " + C.getHitbox().x + ", " + C.getHitbox().y);
	        }
        
		updateCannons(lvlData, player);
		updateProjectiles(lvlData, player);
        updateSkipLevelObjects();

	}
    private void updateSkipLevelObjects() {
        for (SkipLevelObject s : skipLevelObjects)
            if (s.isActive())
                s.update();
    }
	private void updateBackgroundTrees() {
		for (BackgroundTree bt : currentLevel.getTrees())
			bt.update();
	}

	private void updateProjectiles(int[][] lvlData, Player player) {
		for (Projectile p : projectiles)
			if (p.isActive()) {
				p.updatePos();
				if (p.getHitbox().intersects(player.getHitbox())) {
					player.changeHealth(-25);
					p.setActive(false);
				} else if (IsProjectileHittingLevel(p, lvlData))
					p.setActive(false);
			}
	}

	private boolean isPlayerInRange(Cannon c, Player player) {
		int absValue = (int) Math.abs(player.getHitbox().x - c.getHitbox().x);
		return absValue <= Game.TILES_SIZE * 5;
	}

	private boolean isPlayerInfrontOfCannon(Cannon c, Player player) {
		if (c.getObjType() == CANNON_LEFT) {
			if (c.getHitbox().x > player.getHitbox().x)
				return true;

		} else if (c.getHitbox().x < player.getHitbox().x)
			return true;
		return false;
	}

	private void updateCannons(int[][] lvlData, Player player) {
		for (Cannon c : currentLevel.getCannons()) {
			if (!c.doAnimation)
				if (c.getTileY() == player.getTileY())
					if (isPlayerInRange(c, player))
						if (isPlayerInfrontOfCannon(c, player))
							if (CanCannonSeePlayer(lvlData, player.getHitbox(), c.getHitbox(), c.getTileY()))
								c.setAnimation(true);

			c.update();
			if (c.getAniIndex() == 4 && c.getAniTick() == 0)
				shootCannon(c);
		}
	}

	private void shootCannon(Cannon c) {
		int dir = 1;
		if (c.getObjType() == CANNON_LEFT)
			dir = -1;

		projectiles.add(new Projectile((int) c.getHitbox().x, (int) c.getHitbox().y, dir));
	}
	public void addSkipLevelObject(SkipLevelObject obj) {
	    skipLevelObjects.add(obj);
	}
	public void draw(Graphics g, int xLvlOffset) {
		drawPotions(g, xLvlOffset);
		drawContainers(g, xLvlOffset);
		drawTraps(g, xLvlOffset);
		drawCannons(g, xLvlOffset);
		drawProjectiles(g, xLvlOffset);
		drawGrass(g, xLvlOffset);
        drawSkipLevelObjects(g, xLvlOffset);
        for (Key k : keys)
            if (k.isActive())
                g.drawImage(keyImgs[0][0], (int) (k.getHitbox().x - xLvlOffset), (int) k.getHitbox().y, KEY_WIDTH, KEY_HEIGHT, null);
        for (ItemA itemA : itemsA)
            if (itemA.isActive())
                g.drawImage(itemAImgs[0][0], (int) (itemA.getHitbox().x - xLvlOffset), (int) itemA.getHitbox().y, KEY_WIDTH, KEY_HEIGHT, null);

        for (ItemB itemB : itemsB)
            if (itemB.isActive())
                g.drawImage(itemBImgs[0][0], (int) (itemB.getHitbox().x - xLvlOffset), (int) itemB.getHitbox().y, KEY_WIDTH, KEY_HEIGHT, null);

        for (ItemC itemC : itemsC)
            if (itemC.isActive()) {
                g.drawImage(itemCImgs[0][0], (int) (itemC.getHitbox().x - xLvlOffset), (int) itemC.getHitbox().y, KEY_WIDTH, KEY_HEIGHT, null);
                //System.out.println("Drawing ItemC at position: " + itemC.getHitbox().x + ", " + itemC.getHitbox().y);
            }

	}

	private void drawGrass(Graphics g, int xLvlOffset) {
		for (Grass grass : currentLevel.getGrass())
			g.drawImage(grassImgs[grass.getType()], grass.getX() - xLvlOffset, grass.getY(), (int) (32 * Game.SCALE), (int) (32 * Game.SCALE), null);
	}

	public void drawBackgroundTrees(Graphics g, int xLvlOffset) {
		for (BackgroundTree bt : currentLevel.getTrees()) {

			int type = bt.getType();
			if (type == 9)
				type = 8;
			g.drawImage(treeImgs[type - 7][bt.getAniIndex()], bt.getX() - xLvlOffset + GetTreeOffsetX(bt.getType()), (int) (bt.getY() + GetTreeOffsetY(bt.getType())), GetTreeWidth(bt.getType()),
					GetTreeHeight(bt.getType()), null);
		}
	}

	private void drawProjectiles(Graphics g, int xLvlOffset) {
		for (Projectile p : projectiles)
			if (p.isActive())
				g.drawImage(cannonBallImg, (int) (p.getHitbox().x - xLvlOffset), (int) (p.getHitbox().y), CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT, null);
	}

	private void drawCannons(Graphics g, int xLvlOffset) {
		for (Cannon c : currentLevel.getCannons()) {
			int x = (int) (c.getHitbox().x - xLvlOffset);
			int width = CANNON_WIDTH;

			if (c.getObjType() == CANNON_RIGHT) {
				x += width;
				width *= -1;
			}
			g.drawImage(cannonImgs[c.getAniIndex()], x, (int) (c.getHitbox().y), width, CANNON_HEIGHT, null);
		}
	}

	private void drawTraps(Graphics g, int xLvlOffset) {
		for (Spike s : currentLevel.getSpikes())
			g.drawImage(spikeImg, (int) (s.getHitbox().x - xLvlOffset), (int) (s.getHitbox().y - s.getyDrawOffset()), SPIKE_WIDTH, SPIKE_HEIGHT, null);

	}

	private void drawContainers(Graphics g, int xLvlOffset) {
		for (GameContainer gc : containers)
			if (gc.isActive()) {
				int type = 0;
				if (gc.getObjType() == BARREL)
					type = 1;
				g.drawImage(containerImgs[type][gc.getAniIndex()], (int) (gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset), (int) (gc.getHitbox().y - gc.getyDrawOffset()), CONTAINER_WIDTH,
						CONTAINER_HEIGHT, null);
			}
	}

	private void drawPotions(Graphics g, int xLvlOffset) {
		for (Potion p : potions)
			if (p.isActive()) {
				int type = 0;
				if (p.getObjType() == RED_POTION)
					type = 1;
				g.drawImage(potionImgs[type][p.getAniIndex()], (int) (p.getHitbox().x - p.getxDrawOffset() - xLvlOffset), (int) (p.getHitbox().y - p.getyDrawOffset()), POTION_WIDTH, POTION_HEIGHT,
						null);
			}
	}
    private void drawSkipLevelObjects(Graphics g, int xLvlOffset) {
        for (SkipLevelObject s : skipLevelObjects) {
            if (s.isActive()) {
                s.render(g, xLvlOffset);
            }
        }
    }

	
	public void resetAllObjects() {
			loadObjects(playing.getLevelManager().getCurrentLevel());
			for (Potion p : potions)
				p.reset();
			for (GameContainer gc : containers)
				gc.reset();
			for (Cannon c : currentLevel.getCannons())
				c.reset();
		    for (Key k : keys) {
		        k.reset(); 
	            keyImage = null;
		    }
		    for (SkipLevelObject s : skipLevelObjects) {
		        s.reset(); 
		        hasKey = false;
		    }
		    for (ItemA A : itemsA)
		        A.reset();
		    for (ItemB B : itemsB)
		        B.reset();
		    for (ItemC C : itemsC)
		        C.reset();
		    for (ItemD D : itemsD)
		        D.reset();
		    for (ItemE E : itemsE)
		        E.reset();
		}
	
	public void removeKey(Key key) {
	    keys.remove(key);
	}
	public void removeItemA(ItemA itemA) {
		itemsA.remove(itemA);
	}
	public void removeItemB(ItemB itemB) {
		itemsB.remove(itemB);
	}
	public void removeItemC(ItemC itemC) {
		itemsC.remove(itemC);
	}
	public void removeItemD(ItemD itemD) {
		itemsD.remove(itemD);
	}
	public void removeItemE(ItemE itemE) {
		itemsC.remove(itemE);
	}
	public void addItemC(ItemC itemC) {
	    System.out.println("Adding ItemC at position: " + itemC.getHitbox().x + ", " + itemC.getHitbox().y);
	    itemsC.add(itemC);
	    System.out.println("ItemC added. Total itemsC: " + itemsC.size());
	}



	}

