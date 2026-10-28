# Viking Hunter (Legends of the North)

A 2D side-scrolling platformer in plain Java (AWT/Swing, no game library), made for the Java /
object-oriented programming module at CY Tech in spring 2024 as an individual project. You run,
jump and fight through three levels, deal with three kinds of monsters, pick up items and find
the key that opens the door to the next level. The engine base (game loop, tile collisions, PNG
levels, the three enemies, Pixel Frog's pirate sprites) follows Kaarin Gaming's Java 2D
platformer tutorial; I added the inventory and the five items, the NPC with its own inventory
and the steal mechanic, the key-and-door exit, the kill counter and score, the god-mode option,
the music and the AZERTY key mapping. The game guide I handed in with the project (French) is in
[docs/Guide_de_Jeu.pdf](docs/Guide_de_Jeu.pdf).

![Main menu](docs/screenshots/main-menu.jpg)
![Level 1](docs/screenshots/level-1.png)

## Controls

- `Q` / `D`: move left / right, `Z`: jump (AZERTY layout)
- `Space`: attack; it also opens boxes and barrels
- `R`: dash attack, costs 60 of the 200 points of the yellow power bar, which refills over time
- `Esc`: pause / resume
- `I`, or the chest button in the top-left corner: open or close the inventory
- Mouse: menu buttons; click an item in the inventory to select it (up to 4 selected at once);
  click the NPC to open its inventory (requires Item C to be selected)
- Numpad `1` / `2`: steal the first or second item of the NPC (requires Item E to be selected)

## Rules

- The player has 100 HP. A sword hit deals 20 damage and gives 20 points of score.
- Crabby (50 HP, 15 damage) walks and attacks when you are on its row within one tile. Pinkstar
  (60 HP, 20 damage) rolls at you when it sees you. Shark (60 HP, 15 damage) dashes during its
  attack. From level 2 on, cannons fire a 25-damage ball when you are on their row and in front of
  them. Water and spikes kill instantly.
- Red potions give 20 HP, blue ones 15 HP. Boxes and barrels drop a potion when you hit them.
- The key opens the door to the next level. Killing every monster of a level also completes it;
  the skull counter at the top shows the progress.
- Items: A kills the next monster you hit outright and gives you its remaining health; B makes
  spikes harmless; C is consumed to open the NPC's inventory; D ends the game and rolls the
  credits; E lets you steal from the NPC. The inventory holds 5 items, 4 of which can be in use.
- Options has a god-mode checkbox: no damage from monsters and objects (water still kills).

## How it is built

```
src/
  main/        MainClass (entry point), Game (loop), GamePanel, GameWindow
  gamestates/  Menu, Playing, GameOptions, Credits, dispatched on the Gamestate enum
  entities/    Entity, Player, Enemy, Crabby, Pinkstar, Shark, EnemyManager, NPC
  levels/      Level (parses a PNG into tiles, entities and objects), LevelManager
  objects/     Potion, GameContainer, Spike, Cannon, Projectile, Key, ItemA..ItemE,
               SkipLevelObject (the door), trees and grass; ObjectManager
  ui/          overlays and buttons: pause, level completed, game over, inventory, NPC inventory
  inputs/, effects/, audio/   keyboard and mouse, rain and dialogue bubbles, music clip
  utilz/       Constants, HelpMethods (collision tests), LoadSave (sprite atlases, levels)
res/           sprite atlases, levels (lvls/1.png, 2.png, 3.png), UI images, music
```

`Game` runs its own thread with two accumulators: 200 logic updates and 120 repaints per second.
Everything is drawn with Java2D; sprites are cut out of atlases with `getSubimage`. The window is
26 x 14 tiles of 32 px scaled by 1.88, so 1560 x 840. Levels are PNG files where each pixel is a
tile: the red channel is the tile index in `outside_sprites.png`, the green channel places
entities (monsters, NPC, player spawn) and the blue channel places objects (potions, containers,
spikes, cannons, trees, door, key, items). The camera scrolls when the player crosses 25 % or
75 % of the screen. Collisions test the four corners of a hitbox against the tile grid; enemies
check that the floor between them and the player is walkable before charging. Player and enemies
are small state machines (IDLE, RUNNING, ATTACK, HIT, DEAD) driven by the animation index.

## Build and run

Java 17 or newer. From the project root:

```
javac -d bin $(find src -name "*.java")
java -cp "bin:res" main.MainClass          # Linux / macOS
java -cp "bin;res" main.MainClass          # Windows
```

On Windows without `find`: `dir /s /b src\*.java > sources.txt` then `javac -d bin @sources.txt`.
`res/` must be on the classpath because sprites and levels are loaded with `getResourceAsStream`;
the music is read from `res/audio/background_music.wav` relative to the working directory, so run
from the project root. For a single jar: `jar cfe viking-hunter.jar main.MainClass -C bin . -C res .`

## Known issues

- `Playing` pre-fills the inventory (key, items A, C, D, E) for the demo, so with the 5-slot limit
  items found in the level are refused; remove the call to `initializePlayerInventory()` to fix it.
- A test door is spawned near the start of level 1 by `addTemporarySkipLevelObject()`.
- Enemy sight and attacks are horizontal only, so monsters cannot follow you across platforms.
- The options screen only has the god-mode checkbox, debug prints are still in the code and
  there are no tests.
