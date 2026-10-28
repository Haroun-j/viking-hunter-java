package gamestates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

import main.Game;
import ui.PauseButton;
import ui.UrmButton;
import ui.CheatCheckbox;
import utilz.LoadSave;
import static utilz.Constants.UI.URMButtons.*;

public class GameOptions extends State implements Statemethods {

	private BufferedImage backgroundImg, optionsBackgroundImg, backButtonImg ;
	private int bgX, bgY, bgW, bgH;
	private UrmButton menuB;
	private List<CheatCheckbox> cheatCheckboxes;
	private Playing playing;
	private PauseButton backButton;


	public GameOptions(Game game, Playing playing) {
		super(game);
		this.playing = playing;
		loadImgs();
		loadButton();
        loadCheatCheckboxes();
        loadBackButton();
	}

	private void loadButton() {
		int menuX = (int) (387 * Game.SCALE);
		int menuY = (int) (325 * Game.SCALE);

		menuB = new UrmButton(menuX, menuY, URM_SIZE, URM_SIZE, 2);
	}

	private void loadImgs() {
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG);
		optionsBackgroundImg = LoadSave.GetSpriteAtlas(LoadSave.OPTIONS_MENU);
		backButtonImg = LoadSave.GetSpriteAtlas(LoadSave.BACK_BUTTON);

		bgW = (int) (optionsBackgroundImg.getWidth() * Game.SCALE);
		bgH = (int) (optionsBackgroundImg.getHeight() * Game.SCALE);
		bgX = Game.GAME_WIDTH / 2 - bgW / 2;
		bgY = (int) (33 * Game.SCALE);
	}
	
	private void loadBackButton() {
		int backX = (int) (10 * Game.SCALE)+ 25; 
		int backY = (int) (10 * Game.SCALE) + 25;
		backButton = new PauseButton(backX, backY, 150, 118, backButtonImg);
	}
	
    private void loadCheatCheckboxes() {
        cheatCheckboxes = new ArrayList<>();
        cheatCheckboxes.add(new CheatCheckbox(100, 170, 32, 32, "GOD_MODE", playing));
        //cheatCheckboxes.add(new CheatCheckbox(100, 210, 32, 32, "UNLIMITED_HEALTH", playing));
        //cheatCheckboxes.add(new CheatCheckbox(100, 250, 32, 32, "UNLIMITED_AMMO", playing));
    }
    
	@Override
	public void update() {
		menuB.update();
        backButton.update();
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		g.drawImage(optionsBackgroundImg, bgX, bgY, bgW, bgH, null);

		backButton.draw(g);
		
		menuB.draw(g);
		
        for (CheatCheckbox checkbox : cheatCheckboxes) {
            checkbox.draw(g);
        }	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (isIn(e, menuB)) {
			menuB.setMousePressed(true);
		} 
		if (isIn(e, backButton)) {
			backButton.setMousePressed(true);
		}
        for (CheatCheckbox checkbox : cheatCheckboxes) {
            if (checkbox.isInBounds(e.getX(), e.getY())) {
                checkbox.toggle();
            }
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isIn(e, menuB)) {
			if (menuB.isMousePressed())
				Gamestate.state = Gamestate.MENU;
		}
		if (isIn(e, backButton)) {
			if (backButton.isMousePressed())
				Gamestate.state = Gamestate.MENU;
		}
		menuB.resetBools();
		backButton.resetBools();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		menuB.setMouseOver(false);
		backButton.setMouseOver(false);

		if (isIn(e, menuB))
			menuB.setMouseOver(true);
		if (isIn(e, backButton))
			backButton.setMouseOver(true);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			Gamestate.state = Gamestate.MENU;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private boolean isIn(MouseEvent e, PauseButton b) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

}
