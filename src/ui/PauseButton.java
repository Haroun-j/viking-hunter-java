package ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class PauseButton {

	protected int x, y, width, height;
	protected Rectangle bounds;
    private BufferedImage[] buttonImages;
    private boolean mouseOver, mousePressed;
    
	public PauseButton(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		createBounds();
	}
    public PauseButton(int x, int y, int width, int height, BufferedImage spriteSheet) {
        this(x, y, width, height);
        loadButtonImages(spriteSheet);
    }
    
    private void loadButtonImages(BufferedImage spriteSheet) {
        buttonImages = new BufferedImage[3];
        for (int i = 0; i < 3; i++) {
            buttonImages[i] = spriteSheet.getSubimage(i * width, 0, width, height);
        }
    }
    
	private void createBounds() {
		bounds = new Rectangle(x, y, width, height);
	}
	
    public void draw(Graphics g) {
        int index = 0;
        if (mouseOver) index = 1;
        if (mousePressed) index = 2;
        if (buttonImages != null) {
            g.drawImage(buttonImages[index], x, y, width, height, null);
        }
    }
    
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }
    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }
    public void resetBools() {
        mouseOver = false;
        mousePressed = false;
    }
    public void update() {
        // Implement if any update logic is needed
    }
}
