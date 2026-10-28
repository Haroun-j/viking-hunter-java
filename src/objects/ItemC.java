package objects;

import java.awt.image.BufferedImage;
import main.Game;
import utilz.LoadSave;

public class ItemC extends GameObject {
	
    private BufferedImage sprite;
    private float hoverOffset;
    private int maxHoverOffset, hoverDir = 1;
    
    public ItemC(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = true;
        loadSprite();
        initHitbox(7, 14);	

        xDrawOffset = (int) (3 * Game.SCALE);
        yDrawOffset = (int) (2 * Game.SCALE);	

        maxHoverOffset = (int) (10 * Game.SCALE);
    }

    private void loadSprite() {
        sprite = LoadSave.GetSpriteAtlas(LoadSave.ITEM_C_SPRITE);
    }
    
    public BufferedImage getSprite() {
        return sprite;
    }
    
    public void update() {
        updateAnimationTick();
        updateHover();
    }	
    
    private void updateHover() {
        hoverOffset += (0.075f * Game.SCALE * hoverDir);

        if (hoverOffset >= maxHoverOffset)
            hoverDir = -1;
        else if (hoverOffset < 0)
            hoverDir = 1;

        hitbox.y = y + hoverOffset;
    }
    
    public void reset() {
        active = true;
        aniTick = 0;
        aniIndex = 0;
        hoverOffset = 0;
        hoverDir = 1;
        hitbox.y = y;
    }
    @Override
    public String toString() {
        return "ItemC[x=" + hitbox.x + ", y=" + hitbox.y + ", active=" + active + "]";
    }

}
