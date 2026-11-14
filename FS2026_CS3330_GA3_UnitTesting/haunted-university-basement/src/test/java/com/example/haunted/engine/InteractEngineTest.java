package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Armor;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Key;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.QuestTracker;

class InteractEngineTest {

    private InteractionEngine engine() {
        return new InteractionEngine(new QuestTracker());
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
    void pickUpItemAddsItToInventoryAndMarksQuestProgress() {
        Room room = roomNamed("room", "Study Hall");
        room.addItem(new Key("Lost Gradebook", "A missing grade ledger."));
        Player player = playerIn(room);
        Quest quest = new Quest("Exam Quest", "Recover the gradebook.");

        InteractionResult result = engine().pickUpItem(player, quest, "Lost Gradebook");

        assertTrue(result.isSuccess());
        assertEquals("Picked up Lost Gradebook.", result.getMessage());
        assertTrue(player.getInventory().contains("Lost Gradebook"));
        assertTrue(room.getItems().isEmpty());
        assertTrue(quest.isGradebookRecovered());
    }

    @Test
    void pickUpItemFailsWhenItemIsMissing() {
        Room room = roomNamed("room", "Study Hall");
        Player player = playerIn(room);
        Quest quest = new Quest("Exam Quest", "Recover the gradebook.");

        InteractionResult result = engine().pickUpItem(player, quest, "Lost Gradebook");

        assertFalse(result.isSuccess());
        assertEquals("Item not found in the room.", result.getMessage());
        assertFalse(player.getInventory().contains("Lost Gradebook"));
    }

    @Test
    void pickUpItemFailsWhenInventoryIsFull() {
        Room room = roomNamed("room", "Study Hall");
        room.addItem(new Weapon("Torch", "A glowing weapon.", 2));
        Player player = playerIn(room);
        Quest quest = new Quest("Exam Quest", "Recover the gradebook.");
        player.getInventory().addItem(new Weapon("Slot 1", "A filler.", 1));
        player.getInventory().addItem(new Weapon("Slot 2", "A filler.", 1));
        player.getInventory().addItem(new Weapon("Slot 3", "A filler.", 1));
        player.getInventory().addItem(new Weapon("Slot 4", "A filler.", 1));
        player.getInventory().addItem(new Weapon("Slot 5", "A filler.", 1));
        player.getInventory().addItem(new Weapon("Slot 6", "A filler.", 1));
        player.getInventory().addItem(new Weapon("Slot 7", "A filler.", 1));
        player.getInventory().addItem(new Weapon("Slot 8", "A filler.", 1));

        InteractionResult result = engine().pickUpItem(player, quest, "Torch");

        assertFalse(result.isSuccess());
        assertEquals("Inventory is full.", result.getMessage());
        assertTrue(room.findItem("Torch").isPresent());
        assertFalse(player.getInventory().contains("Torch"));
    }

    @Test
    void useItemHealsPlayerWhenPotionIsUsed() {
        Room room = roomNamed("room", "Study Hall");
        Player player = playerIn(room);
        player.takeDamage(12);
        player.getInventory().addItem(new Potion("Health Potion", "Restores health.", 10));

        InteractionResult result = engine().useItem(player, "Health Potion");

        assertTrue(result.isSuccess());
        assertEquals("Used Health Potion.", result.getMessage());
        assertEquals(48, player.getHealth());
    }

    @Test
    void useItemFailsForNonPotionItems() {
        Room room = roomNamed("room", "Study Hall");
        Player player = playerIn(room);
        player.getInventory().addItem(new Key("Archive Key", "Opens a locked room."));

        InteractionResult result = engine().useItem(player, "Archive Key");

        assertFalse(result.isSuccess());
        assertEquals("That item cannot be used.", result.getMessage());
    }

    @Test
    void equipItemEquipsWeaponOrArmor() {
        Room room = roomNamed("room", "Study Hall");
        Player player = playerIn(room);
        Weapon weapon = new Weapon("Bronze Blade", "A sturdy blade.", 4);
        Armor armor = new Armor("Leather Vest", "A light armor.", 2);
        player.getInventory().addItem(weapon);
        player.getInventory().addItem(armor);

        InteractionResult weaponResult = engine().equipItem(player, "Bronze Blade");
        InteractionResult armorResult = engine().equipItem(player, "Leather Vest");

        assertTrue(weaponResult.isSuccess());
        assertEquals("Equipped weapon Bronze Blade.", weaponResult.getMessage());
        assertSame(weapon, player.getEquippedWeapon());

        assertTrue(armorResult.isSuccess());
        assertEquals("Equipped armor Leather Vest.", armorResult.getMessage());
        assertSame(armor, player.getEquippedArmor());
    }

    @Test
    void equipItemFailsForNonEquippableItems() {
        Room room = roomNamed("room", "Study Hall");
        Player player = playerIn(room);
        player.getInventory().addItem(new Potion("Health Potion", "Restores health.", 5));

        InteractionResult result = engine().equipItem(player, "Health Potion");

        assertFalse(result.isSuccess());
        assertEquals("That item cannot be equipped.", result.getMessage());
    }

    @Test
    void unlockRoomSucceedsWhenKeyMatchesAndRoomIsLocked() {
        Room start = roomNamed("start", "Start Room");
        Room target = roomNamed("target", "Archive Room");
        target.setLocked(true, "Archive Key");
        start.connect(Direction.NORTH, target);
        Player player = playerIn(start);
        player.getInventory().addItem(new Key("Archive Key", "Opens the archive."));

        InteractionResult result = engine().unlockRoom(player, Direction.NORTH);

        assertTrue(result.isSuccess());
        assertEquals("Unlocked Archive Room.", result.getMessage());
        assertFalse(target.isLocked());
    }

    @Test
    void unlockRoomFailsWhenNoDirectionOrWrongKey() {
        Room start = roomNamed("start", "Start Room");
        Room target = roomNamed("target", "Archive Room");
        target.setLocked(true, "Archive Key");
        start.connect(Direction.NORTH, target);
        Player player = playerIn(start);

        InteractionResult wrongKeyResult = engine().unlockRoom(player, Direction.NORTH);
        assertFalse(wrongKeyResult.isSuccess());
        assertEquals("You do not have the correct key.", wrongKeyResult.getMessage());

        Room roomWithoutExit = roomNamed("room", "Study Hall");
        Player otherPlayer = playerIn(roomWithoutExit);
        InteractionResult noExitResult = engine().unlockRoom(otherPlayer, Direction.NORTH);
        assertFalse(noExitResult.isSuccess());
        assertEquals("There is no room in that direction.", noExitResult.getMessage());
    }
}

