package objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import utilz.LoadSave;
import static utilz.Constants.ObjectConstants.*;

public class SkipLevelObject extends GameObject {

    private BufferedImage[] doorImgs;
    private boolean open = false;

    public SkipLevelObject(int x, int y) {
        super(x, y, DOOR);
        initHitbox(DOOR_WIDTH, DOOR_HEIGHT);
        loadImgs();
        doAnimation = false; 
    }

    private void loadImgs() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.DOOR_SPRITE);
        doorImgs = new BufferedImage[4];
        for (int i = 0; i < doorImgs.length; i++)
            doorImgs[i] = temp.getSubimage(i * 79, 0, 72, 72);
    }

    public void update() {
        if (open) {
        	System.out.println("Door is open. Animation index: " + aniIndex);
            updateAnimationTick();
        } /*else {
            System.out.println("Door is not open. Animation index: " + aniIndex);
        }*/
    }

    public void render(Graphics g, int xLvlOffset) {
        g.drawImage(doorImgs[aniIndex], (int) (hitbox.x - xLvlOffset), (int) (hitbox.y), DOOR_WIDTH, DOOR_HEIGHT, null);
    }
    public void open() {
        if (!open) {
            open = true;
            aniTick = 0; 
            aniIndex = 0; 
            doAnimation = true; 
            System.out.println("Door opened. Animation started.");
        }
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isFullyOpen() {
    	return open && aniIndex == 3;
    }
    
    @Override
    protected void updateAnimationTick() {
        if (doAnimation) {
            aniTick++;
            if (aniTick >= 25) {
                aniTick = 0;
                aniIndex++;
                if (aniIndex >= doorImgs.length) {
                    aniIndex = doorImgs.length - 1; 
                    doAnimation = false; 
                }
            }
        }
    }	
}
