package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.haunted.events.CombatResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Item;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;

class CombatEngineTest {

    private CombatEngine engine() {
        return new CombatEngine(new DamageCalculator(), new QuestTracker());
    }

    private Player playerIn(Room room) {
        Player player = new Player("Tester", 50, 10, 3, new Inventory(8));
        player.setCurrentRoom(room);
        return player;
    }

    private Room roomWithMonster(Monster monster) {
        Room room = new Room("room", "Study Hall", "A dusty classroom.");
        room.addMonster(monster);
        return room;
    }

    @Test
    void attackWithNullMonsterFails() {
        Room room = new Room("room", "Study Hall", "A dusty classroom.");
        Player player = playerIn(room);
        Quest quest = new Quest("Exam Quest", "Complete the basement challenge.");

        CombatResult result = engine().attack(player, quest, null);

        assertFalse(result.isSuccess());
        assertEquals("Monster not found.", result.getMessage());
        assertEquals(0, result.getDamageToMonster());
        assertEquals(0, result.getDamageToPlayer());
        assertFalse(result.isMonsterDefeated());
        assertTrue(result.getDroppedItems().isEmpty());
    }

    @Test
    void attackWhenPlayerIsDefeatedFails() {
        Room room = new Room("room", "Study Hall", "A dusty classroom.");
        Player player = playerIn(room);
        player.takeDamage(999);
        Monster monster = new Monster("Skeleton", 12, 6, 2, List.of());

        CombatResult result = engine().attack(player, new Quest("Exam Quest", "Complete the basement challenge."), monster);

        assertFalse(result.isSuccess());
        assertEquals("Player is defeated.", result.getMessage());
        assertEquals(0, result.getDamageToMonster());
        assertEquals(0, result.getDamageToPlayer());
        assertFalse(result.isMonsterDefeated());
    }

    @Test
    void attackAgainstAlreadyDefeatedMonsterFails() {
        Monster monster = new Monster("Skeleton", 0, 6, 2, List.of());
        Player player = playerIn(new Room("room", "Study Hall", "A dusty classroom."));

        CombatResult result = engine().attack(player, new Quest("Exam Quest", "Complete the basement challenge."), monster);

        assertFalse(result.isSuccess());
        assertEquals("Monster is already defeated.", result.getMessage());
        assertEquals(0, result.getDamageToMonster());
        assertEquals(0, result.getDamageToPlayer());
        assertTrue(result.isMonsterDefeated());
        assertTrue(result.getDroppedItems().isEmpty());
    }

    @Test
    void attackDamagesMonsterAndCounterattacksPlayer() {
        Monster monster = new Monster("Shadow", 20, 8, 2, List.of());
        Room room = roomWithMonster(monster);
        Player player = playerIn(room);

        CombatResult result = engine().attack(player, new Quest("Exam Quest", "Complete the basement challenge."), monster);

        assertTrue(result.isSuccess());
        assertEquals("Attacked Shadow.", result.getMessage());
        assertEquals(8, result.getDamageToMonster());
        assertEquals(5, result.getDamageToPlayer());
        assertFalse(result.isMonsterDefeated());
        assertEquals(12, monster.getHealth());
        assertEquals(45, player.getHealth());
        assertTrue(result.getDroppedItems().isEmpty());
    }

    @Test
    void defeatingMonsterDropsLootAndUpdatesQuest() {
        Item loot = new Weapon("Ritual Knife", "A wicked blade", 4);
        Monster monster = new Monster("Final Exam Phantom", 9, 5, 0, List.of(loot));
        Room room = roomWithMonster(monster);
        Player player = playerIn(room);
        Quest quest = new Quest("Exam Quest", "Complete the basement challenge.");

        CombatResult result = engine().attack(player, quest, monster);

        assertTrue(result.isSuccess());
        assertEquals("Defeated Final Exam Phantom.", result.getMessage());
        assertEquals(9, result.getDamageToMonster());
        assertEquals(0, result.getDamageToPlayer());
        assertTrue(result.isMonsterDefeated());
        assertEquals(1, result.getDroppedItems().size());
        assertSame(loot, result.getDroppedItems().get(0));
        assertSame(loot, room.getItems().get(0));
        assertTrue(quest.isPhantomDefeated());
        assertEquals(com.example.haunted.model.QuestStatus.IN_PROGRESS, quest.getStatus());
    }
}
