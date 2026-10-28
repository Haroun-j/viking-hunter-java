package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;
import static utilz.Constants.UI.URMButtons.*;

public class GameCompletedOverlay {
	private Playing playing;
	private BufferedImage img;
	private UrmButton mainMenu; //credit
	private int imgX, imgY, imgW, imgH;

	public GameCompletedOverlay(Playing playing) {
		this.playing = playing;
		createImg();
		createButtons();
	}

	private void createButtons() {
        int buttonY = (int) (200 * Game.SCALE) + 40;
        
        int menuX = Game.GAME_WIDTH / 2 - URM_SIZE + 60;
        //int  creditX= Game.GAME_WIDTH / 2 + URM_SIZE / 2;

        //credit = new UrmButton(creditX, buttonY, URM_SIZE, URM_SIZE, 1);
        mainMenu = new UrmButton(menuX, buttonY, URM_SIZE, URM_SIZE, 2);
        
        } 	

	private void createImg() {
		img = LoadSave.GetSpriteAtlas(LoadSave.GAME_COMPLETED);
		imgW = (int) (img.getWidth() * Game.SCALE);
		imgH = (int) (img.getHeight() * Game.SCALE);
		imgX = Game.GAME_WIDTH / 2 - imgW / 2;
		imgY = (int) (100 * Game.SCALE);

	}

	public void draw(Graphics g) {
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

		g.drawImage(img, imgX, imgY, imgW, imgH, null);

		mainMenu.draw(g);
		//credit.draw(g);
	}

	public void update() {
		mainMenu.update();
		//credit.update();
	}

	private boolean isIn(UrmButton b, MouseEvent e) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

	public void mouseMoved(MouseEvent e) {
		mainMenu.setMouseOver(false);
		//credit.setMouseOver(false);

		if (isIn(mainMenu, e))
			mainMenu.setMouseOver(true);
		/*else if (isIn(credit, e))
			credit.setMouseOver(true);*/
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(mainMenu, e)) {
			if (mainMenu.isMousePressed()) {
				playing.resetAll();
				playing.resetGameCompleted();
				playing.setGamestate(Gamestate.MENU);

			}
		} /*else if (isIn(credit, e))
			if (credit.isMousePressed()) {
				playing.resetAll();
				playing.resetGameCompleted();
				playing.setGamestate(Gamestate.CREDITS);
			}*/

		//credit.resetBools();
		mainMenu.resetBools();
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(mainMenu, e))
			mainMenu.setMousePressed(true);
		/*else if (isIn(credit, e))
			credit.setMousePressed(true);*/
	}
}
