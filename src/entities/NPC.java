package entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import utilz.LoadSave;
import ui.NPCInventoryOverlay;


public class NPC {
    private float x, y;
    private int width, height;
    private BufferedImage[] animations;
    private int aniIndex, aniTick;
    private int aniSpeed = 25;
    private int aniCount = 5; 
    private NPCInventoryOverlay npcInventoryOverlay;
    private boolean showDialogue;
    private String dialogueText = "bonjour";
    
    public NPC(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 244; 
        this.height = 186; 
        loadAnimations();
        npcInventoryOverlay = new NPCInventoryOverlay(LoadSave.GetSpriteAtlas(LoadSave.INVENTORY_IMAGE_NPC), 1100, 100);
        
        BufferedImage itemCImage = LoadSave.GetSpriteAtlas(LoadSave.ITEM_C_SPRITE);
        npcInventoryOverlay.addItem("ItemC", itemCImage);
        
        BufferedImage itemDImage = LoadSave.GetSpriteAtlas(LoadSave.ITEM_D_SPRITE);
        npcInventoryOverlay.addItem("ItemD", itemDImage);
    }

    private void loadAnimations() {
        BufferedImage spriteSheet = LoadSave.GetSpriteAtlas(LoadSave.NPC_SPRITE);
        animations = new BufferedImage[aniCount];
        for (int i = 0; i < aniCount; i++) {
            animations[i] = spriteSheet.getSubimage(i * width, 0, width, height);
        }
    }

    public void update() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= aniCount) {
                aniIndex = 0;
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        g.drawImage(animations[aniIndex], (int) (x - xLvlOffset-130), (int) y -70, width, height, null);
        if (showDialogue) {
            drawDialogue(g, xLvlOffset);
        }
    }
    
    public void drawDialogue(Graphics g, int xLvlOffset) {
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        int textWidth = g.getFontMetrics().stringWidth(dialogueText);
        int textX = (int) (x - xLvlOffset - textWidth / 2);
        int textY = (int) (y - 75);
        g.drawString(dialogueText, textX, textY);
    }
    public void setDialogueText(String dialogueText) {
        this.dialogueText = dialogueText;
    }

    public NPCInventoryOverlay getNpcInventoryOverlay() {
        return npcInventoryOverlay;
    }


    public void toggleInventory() {
    	npcInventoryOverlay.setVisible(!npcInventoryOverlay.isVisible());
    }



    public Rectangle2D.Float getHitbox() {
        Rectangle2D.Float hitbox = new Rectangle2D.Float(x, y, width, height);
        //System.out.println("NPC hitbox: " + hitbox); 
        return hitbox;
    }
    
    public void setShowDialogue(boolean showDialogue) {
        this.showDialogue = showDialogue;
    }
}
