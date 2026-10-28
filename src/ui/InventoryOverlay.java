package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import utilz.LoadSave;

public class InventoryOverlay {

    private BufferedImage inventoryImage;
    private int x, y, width, height;
    private List<InventoryItem> collectedItems;
    private int maxItemsInUse = 4;
    private int maxCollectedItems = 5;
    private int itemUseOffset = 50;
    private boolean visible = false;
    
    public InventoryOverlay(BufferedImage inventoryImage, int x, int y) {
        this.inventoryImage = inventoryImage;
        collectedItems = new ArrayList<>();
        this.x = x;  // 120
        this.y = y; // 200 
        this.width = inventoryImage.getWidth();
        this.height = inventoryImage.getHeight();
    }

    public void draw(Graphics g) {
        g.drawImage(inventoryImage, x, y, width, height, null);
        drawCollectedItems(g);	
    }

    private void drawCollectedItems(Graphics g) {
        int itemX = x + 25;
        int itemY = y + 30;
        int itemWidth = 40;
        int itemHeight = 40;
        int itemsPerRow = 4;
        int rowOffset = 25;

        int count = 0;
        for (InventoryItem item : collectedItems) {
            if (!item.isInUse()) {
                item.setPosition(itemX, itemY);
                g.drawImage(item.getImage(), itemX, itemY, itemWidth, itemHeight, null);
                itemX += 50;
                count++;
                if (count % itemsPerRow == 0) {
                    itemX = x + 25;
                    itemY += itemHeight + rowOffset;
                }
            }
        }

        int inUseX = 145;
        int inUseY = 485;
        for (InventoryItem item : collectedItems) {
            if (item.isInUse()) {
                item.setPosition(inUseX, inUseY); 
                g.drawImage(item.getImage(), inUseX, inUseY, itemWidth, itemHeight, null);
                inUseX += itemUseOffset;
            }
        }
    }
    public int getMaxCollectedItems() {
        return maxCollectedItems;
    }
    public void toggleItemInUse(String id) {
        for (InventoryItem item : collectedItems) {
            if (item.getId().equals(id)) {
                if (item.isInUse()) {
                    item.setInUse(false);
                    System.out.println(id + " unselected");
                } else {
                    long inUseCount = collectedItems.stream().filter(InventoryItem::isInUse).count();
                    if (inUseCount < maxItemsInUse) {
                        item.setInUse(true);
                        System.out.println(id + " selected");
                    } else {
                        System.out.println(id + " cannot be selected, max in use items reached");
                    }
                }
                return;
            }
        }
    }


    public void useItem(String id) {
        for (InventoryItem item : collectedItems) {
            if (item.getId().equals(id)) {
                collectedItems.remove(item);
                System.out.println(id + " used and removed from inventory");
                return;
            }
        }
        System.out.println("Item " + id + " not found in inventory");
    }




    public boolean isItemInUse(String itemName) {
        for (InventoryItem item : collectedItems) {
            if (item.getId().equals(itemName) && item.isInUse()) {
                return true;
            }
        }
        return false;
    }
    
    private String getItemName(String itemId) {
        return collectedItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .map(InventoryItem::getId)
                .findFirst()
                .orElse("Unknown Item");
    }

    public void addItem(String id, BufferedImage itemImage) {
        if (collectedItems.size() < maxCollectedItems) {
            collectedItems.add(new InventoryItem(id, itemImage));
            System.out.println(id + " added to inventory");
        } else {
            System.out.println(id + " cannot be added, inventory is full");
        }
    }

    public void removeItem(String itemId) {
        collectedItems.removeIf(item -> item.getId().equals(itemId));
    }

    
    public boolean hasItem(String itemId) {
        return collectedItems.stream().anyMatch(item -> item.getId().equals(itemId));
    }

    
    public void reset() {
        collectedItems.clear();
    }
    
    public List<InventoryItem> getCollectedItems() {
        return collectedItems;
    }

    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) { 
        this.visible = visible;
    }
    
    public class InventoryItem {
    	
    	private String id;
        private BufferedImage image;
        private boolean inUse;
        private int posX, posY;

        public InventoryItem(String id, BufferedImage image) {
        	this.id = id;
            this.image = image;
            this.inUse = false;
            this.posX = 0;
            this.posY = 0;
        }

        public String getId() {
            return id;
        }

        public BufferedImage getImage() {
            return image;
        }

        public boolean isInUse() {
            return inUse;
        }

        public void setInUse(boolean inUse) {
            this.inUse = inUse;
        }

        public int getPosX() {
            return posX;
        }

        public int getPosY() {
            return posY;
        }

        public void setPosition(int x, int y) {
            this.posX = x;
            this.posY = y;
        }
    }

}
