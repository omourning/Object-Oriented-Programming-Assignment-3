package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.haunted.events.CombatResult;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Key;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;
import com.example.haunted.rules.TrapResolver;

class GameEngineTest {

    private GameEngine engine(Player player, Quest quest) {
        QuestTracker tracker = new QuestTracker();
        return new GameEngine(
                player,
                quest,
                new MovementEngine(new TrapResolver()),
                new CombatEngine(new DamageCalculator(), tracker),
                new InteractionEngine(tracker));
    }

    private Player playerIn(Room room) {
        Player player = new Player("Tester", 50, 10, 3, new Inventory(8));
        player.setCurrentRoom(room);
        return player;
    }

    private Room roomNamed(String id, String name) {
        return new Room(id, name, "A room in the basement.");
    }

    @Test
    void moveDelegatesToMovementEngine() {
        Room start = roomNamed("start", "Start Room");
        Room target = roomNamed("target", "Target Room");
        start.connect(Direction.NORTH, target);
        Player player = playerIn(start);
        GameEngine game = engine(player, new Quest("Exam Quest", "Complete the challenge."));

        MoveResult result = game.move(Direction.NORTH);

        assertTrue(result.isSuccess());
        assertEquals("Moved to Target Room.", result.getMessage());
        assertSame(target, player.getCurrentRoom());
        assertSame(target, game.getCurrentRoom());
    }

    @Test
    void pickUpItemUsesInteractionEngineAndUpdatesRoomState() {
        Room room = roomNamed("room", "Study Hall");
        Weapon sword = new Weapon("Rusty Sword", "A basic blade.", 3);
        room.addItem(sword);
        Player player = playerIn(room);
        GameEngine game = engine(player, new Quest("Exam Quest", "Complete the challenge."));

        InteractionResult result = game.pickUpItem("Rusty Sword");

        assertTrue(result.isSuccess());
        assertEquals("Picked up Rusty Sword.", result.getMessage());
        assertTrue(player.getInventory().contains("Rusty Sword"));
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    void equipItemAndUseItemWorkThroughInteractionEngine() {
        Room room = roomNamed("room", "Study Hall");
        Player player = playerIn(room);
        Weapon sword = new Weapon("Bronze Blade", "A sturdy blade.", 4);
        Potion potion = new Potion("Healing Draught", "Restores vitality.", 10);
        player.getInventory().addItem(sword);
        player.getInventory().addItem(potion);
        player.takeDamage(6);
        GameEngine game = engine(player, new Quest("Exam Quest", "Complete the challenge."));

        InteractionResult equipResult = game.equipItem("Bronze Blade");
        InteractionResult useResult = game.useItem("Healing Draught");

        assertTrue(equipResult.isSuccess());
        assertEquals("Equipped weapon Bronze Blade.", equipResult.getMessage());
        assertSame(sword, player.getEquippedWeapon());
        assertTrue(useResult.isSuccess());
        assertEquals("Used Healing Draught.", useResult.getMessage());
        assertEquals(44, player.getHealth());
    }

    @Test
    void unlockRoomUsesKeyFromInventory() {
        Room start = roomNamed("start", "Start Room");
        Room target = roomNamed("target", "Archive Room");
        target.setLocked(true, "Archive Key");
        start.connect(Direction.NORTH, target);
        Player player = playerIn(start);
        player.getInventory().addItem(new Key("Archive Key", "Opens the archive."));
        GameEngine game = engine(player, new Quest("Exam Quest", "Complete the challenge."));

        InteractionResult result = game.unlockRoom(Direction.NORTH);

        assertTrue(result.isSuccess());
        assertEquals("Unlocked Archive Room.", result.getMessage());
        assertFalse(target.isLocked());
    }

    @Test
    void attackFindsMonsterByNameInCurrentRoom() {
        Room room = roomNamed("room", "Study Hall");
        Monster monster = new Monster("Shadow", 20, 8, 2, java.util.List.of());
        room.addMonster(monster);
        Player player = playerIn(room);
        GameEngine game = engine(player, new Quest("Exam Quest", "Complete the challenge."));

        CombatResult result = game.attack("Shadow");

        assertTrue(result.isSuccess());
        assertEquals("Attacked Shadow.", result.getMessage());
        assertEquals(8, result.getDamageToMonster());
        assertEquals(5, result.getDamageToPlayer());
        assertEquals(12, monster.getHealth());
        assertEquals(45, player.getHealth());
    }

    @Test
    void gameStateChecksReflectPlayerAndQuestStatus() {
        Room room = roomNamed("room", "Study Hall");
        Player player = playerIn(room);
        Quest quest = new Quest("Exam Quest", "Complete the challenge.");
        GameEngine game = engine(player, quest);

        assertFalse(game.isGameOver());
        assertFalse(game.isGameWon());
        assertSame(player, game.getPlayer());
        assertSame(quest, game.getQuest());
        assertSame(room, game.getCurrentRoom());

        quest.markGradebookRecovered();
        quest.markPhantomDefeated();
        assertTrue(game.isGameWon());

        player.takeDamage(999);
        assertTrue(game.isGameOver());
        assertFalse(game.isGameWon());
    }
}
