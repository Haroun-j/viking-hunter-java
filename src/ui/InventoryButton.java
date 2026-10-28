package ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class InventoryButton {

    private int x, y, width, height;
    private BufferedImage buttonImage;
    private Rectangle bounds;
    private boolean mouseOver, mousePressed;

    public InventoryButton(int x, int y, int width, int height, BufferedImage buttonImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonImage = buttonImage;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        if (buttonImage != null) {
            g.drawImage(buttonImage, x, y, width, height, null);
        } else {
            System.out.println("No button image to draw.");
        }
    }

    public void update() {
        // Implement any update logic if needed
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        this.bounds.setLocation(x, y);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        this.bounds.setLocation(x, y);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        this.bounds.setSize(width, height);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        this.bounds.setSize(width, height);
    }

    public BufferedImage getButtonImage() {
        return buttonImage;
    }

    public void setButtonImage(BufferedImage buttonImage) {
        this.buttonImage = buttonImage;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public void resetBools() {
        this.mouseOver = false;
        this.mousePressed = false;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
