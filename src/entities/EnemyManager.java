package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gamestates.Playing;
import levels.Level;
import utilz.LoadSave;
import static utilz.Constants.EnemyConstants.*;
import java.awt.Point;

public class EnemyManager {

	private Playing playing;
	private BufferedImage[][] crabbyArr, pinkstarArr, sharkArr;
	private Level currentLevel;
    private int totalMonsters;
    private int killedMonsters = 0;
    
	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
		
	}

	public void loadEnemies(Level level) {
		this.currentLevel = level;
        this.totalMonsters = level.getCrabs().size() + level.getPinkstars().size() + level.getSharks().size();
        this.killedMonsters = 0;
	}

	public void update(int[][] lvlData) {
		boolean isAnyActive = false;
		for (Crabby c : currentLevel.getCrabs())
			if (c.isActive()) {
				c.update(lvlData, playing);
				isAnyActive = true;
			}else if (!c.isCountedAsKilled()) {
	            incrementKilledMonsters();
	            c.setCountedAsKilled(true);
	            System.out.println("Crabby killed at position: " + c.getHitbox().x + ", " + c.getHitbox().y);
	            playing.handleCrabbyKilled(); 
	        }
		for (Pinkstar p : currentLevel.getPinkstars())
			if (p.isActive()) {
				p.update(lvlData, playing);
				isAnyActive = true;
			} else if (!p.isActive() && !p.isCountedAsKilled()) {
	            incrementKilledMonsters(); 
	            p.setCountedAsKilled(true);
	        }

		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				s.update(lvlData, playing);
				isAnyActive = true;
			}  else if (!s.isActive() && !s.isCountedAsKilled()) {
	            incrementKilledMonsters(); 
	            s.setCountedAsKilled(true);
	        }

		if (!isAnyActive)
			playing.setLevelCompleted(true);
	}
	public Point getLastCrabbyPosition() {
	    for (Crabby c : currentLevel.getCrabs()) {
	        if (!c.isActive() && c.isCountedAsKilled()) {
	            return new Point((int) c.getHitbox().x, (int) c.getHitbox().y);
	        }
	    }
	    return null;
	}

	public boolean isLastCrabby() {
	    for (Crabby c : currentLevel.getCrabs()) {
	        if (c.isActive() && !c.isCountedAsKilled()) {
	            return false;
	        }
	    }
	    return true;
	}




	public void draw(Graphics g, int xLvlOffset) {
		drawCrabs(g, xLvlOffset);
		drawPinkstars(g, xLvlOffset);
		drawSharks(g, xLvlOffset);
	}

	private void drawSharks(Graphics g, int xLvlOffset) {
		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				g.drawImage(sharkArr[s.getState()][s.getAniIndex()], (int) s.getHitbox().x - xLvlOffset - SHARK_DRAWOFFSET_X + s.flipX(),
						(int) s.getHitbox().y - SHARK_DRAWOFFSET_Y + (int) s.getPushDrawOffset(), SHARK_WIDTH * s.flipW(), SHARK_HEIGHT, null);
//				s.drawHitbox(g, xLvlOffset);
//				s.drawAttackBox(g, xLvlOffset);
			}
	}

	private void drawPinkstars(Graphics g, int xLvlOffset) {
		for (Pinkstar p : currentLevel.getPinkstars())
			if (p.isActive()) {
				g.drawImage(pinkstarArr[p.getState()][p.getAniIndex()], (int) p.getHitbox().x - xLvlOffset - PINKSTAR_DRAWOFFSET_X + p.flipX(),
						(int) p.getHitbox().y - PINKSTAR_DRAWOFFSET_Y + (int) p.getPushDrawOffset(), PINKSTAR_WIDTH * p.flipW(), PINKSTAR_HEIGHT, null);
//				p.drawHitbox(g, xLvlOffset);
			}
	}

	private void drawCrabs(Graphics g, int xLvlOffset) {
		for (Crabby c : currentLevel.getCrabs())
			if (c.isActive()) {

				g.drawImage(crabbyArr[c.getState()][c.getAniIndex()], (int) c.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X + c.flipX(),
						(int) c.getHitbox().y - CRABBY_DRAWOFFSET_Y + (int) c.getPushDrawOffset(), CRABBY_WIDTH * c.flipW(), CRABBY_HEIGHT, null);

//				c.drawHitbox(g, xLvlOffset);
//				c.drawAttackBox(g, xLvlOffset);
			}

	}

	public boolean checkEnemyHit(Rectangle2D.Float attackBox, int damage) {
	    for (Crabby c : currentLevel.getCrabs()) {
	        if (c.isActive() && c.getState() != DEAD && c.getState() != HIT) {
	            if (attackBox.intersects(c.getHitbox())) {
	                int damageToApply = damage;
	                if (playing.getInventoryOverlay().isItemInUse("ItemA")) {
	                    damageToApply = GetMaxHealth(CRABBY);
	                    //playing.getInventoryOverlay().removeItem("ItemA");
	                    playing.getPlayer().increaseHealth(c.currentHealth);
	                }
	                c.hurt(damageToApply);
	                return true;
	            }
	        }
	    }
	    for (Pinkstar p : currentLevel.getPinkstars()) {
	        if (p.isActive() && p.getState() != DEAD && p.getState() != HIT) {
	            if (attackBox.intersects(p.getHitbox())) {
	                int damageToApply = damage;
	                if (playing.getInventoryOverlay().isItemInUse("ItemA")) {
	                    damageToApply = GetMaxHealth(PINKSTAR);
	                    //playing.getInventoryOverlay().removeItem("ItemA");
	                    playing.getPlayer().increaseHealth(p.currentHealth);
	                    
	                }
	                p.hurt(damageToApply);
	                return true;
	            }
	        }
	    }

		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				if (s.getState() != DEAD && s.getState() != HIT)
					if (attackBox.intersects(s.getHitbox())) {
						//s.hurt(damage);
			            int damageToApply = damage;
			            if (playing.getInventoryOverlay().isItemInUse("Item A")) {
			                damageToApply = GetMaxHealth(SHARK);
			                //playing.getInventoryOverlay().removeItem("Item A");
		                    playing.getPlayer().increaseHealth(s.currentHealth);

			            }
	                    s.hurt(damageToApply);
	                    /*if (damageToApply == GetMaxHealth(SHARK)) {
	                        playing.setLevelCompleted(true); 
	                    }*/
						return true;
					}
			}
		return false;
	}
	public void incrementKilledMonsters() {
	    killedMonsters++;
	}

    public int getTotalMonsters() {
        return totalMonsters;
    }
    public int getKilledMonsters() {
        return killedMonsters;
    }
    public void resetKilledMonsters() {
        killedMonsters = 0;
    }
	private void loadEnemyImgs() {
		crabbyArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE), 9, 5, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
		pinkstarArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.PINKSTAR_ATLAS), 8, 5, PINKSTAR_WIDTH_DEFAULT, PINKSTAR_HEIGHT_DEFAULT);
		sharkArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.SHARK_ATLAS), 8, 5, SHARK_WIDTH_DEFAULT, SHARK_HEIGHT_DEFAULT);
	}

	private BufferedImage[][] getImgArr(BufferedImage atlas, int xSize, int ySize, int spriteW, int spriteH) {
		BufferedImage[][] tempArr = new BufferedImage[ySize][xSize];
		for (int j = 0; j < tempArr.length; j++)
			for (int i = 0; i < tempArr[j].length; i++)
				tempArr[j][i] = atlas.getSubimage(i * spriteW, j * spriteH, spriteW, spriteH);
		return tempArr;
	}

	public void resetAllEnemies() {
		for (Crabby c : currentLevel.getCrabs()) {
			c.resetEnemy();
			c.setCountedAsKilled(false);
		}
		for (Pinkstar p : currentLevel.getPinkstars()) {
			p.resetEnemy();
        	p.setCountedAsKilled(false);
		}

		for (Shark s : currentLevel.getSharks()) {
			s.resetEnemy();
			s.setCountedAsKilled(false);
		}

	}

}
