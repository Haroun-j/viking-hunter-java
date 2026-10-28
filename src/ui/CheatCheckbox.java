package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import utilz.LoadSave;
import gamestates.Playing;


public class CheatCheckbox {
    private int x, y, width, height;
    private BufferedImage checkedImg, uncheckedImg;
    private boolean checked;
    private String cheatCode;
    private Playing playing;

    public CheatCheckbox(int x, int y, int width, int height, String cheatCode, Playing playing) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cheatCode = cheatCode;
        this.playing = playing;
        this.checked = false;
        loadImages();
    }

    private void loadImages() {
        checkedImg = LoadSave.GetSpriteAtlas(LoadSave.CHECKBOX_CHECKED_IMG);
        uncheckedImg = LoadSave.GetSpriteAtlas(LoadSave.CHECKBOX_UNCHECKED_IMG);
    }

    public void draw(Graphics g) {
        if (checked) {
            g.drawImage(checkedImg, x, y, width, height, null);
        } else {
            g.drawImage(uncheckedImg, x, y, width, height, null);
        }
        drawText(g);
    }
    private void drawText(Graphics g) {
        g.drawString(cheatCode, x + width + 10, y + height / 2 + 5);
    }
    public void toggle() {
        checked = !checked;
        activateCheat();
    }

    private void activateCheat() {
        System.out.println("Cheat code activated: " + cheatCode);
        if ("GOD_MODE".equals(cheatCode)) {
            playing.setGodMode(checked);
        }
    }

    public boolean isInBounds(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
