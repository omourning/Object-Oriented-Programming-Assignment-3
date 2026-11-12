package com.example.haunted.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    private Room createRoom() {
        return new Room("room1", "Classroom", "An old classroom.");
    }

    @Test
    void constructorShouldInitializeRoomProperties() {
        Room room = createRoom();

        assertEquals("room1", room.getId());
        assertEquals("Classroom", room.getName());
        assertEquals("An old classroom.", room.getDescription());
        assertTrue(room.getExits().isEmpty());
        assertTrue(room.getItems().isEmpty());
        assertTrue(room.getMonsters().isEmpty());
        assertFalse(room.isLocked());
        assertNull(room.getTrap());
    }

    @Test
    void constructorShouldRejectNullId() {
        assertThrows(
            NullPointerException.class,
            () -> new Room(null, "Classroom", "Description")
        );
    }

    @Test
    void constructorShouldRejectNullName() {
        assertThrows(
            NullPointerException.class,
            () -> new Room("room1", null, "Description")
        );
    }

    @Test
    void constructorShouldRejectNullDescription() {
        assertThrows(
            NullPointerException.class,
            () -> new Room("room1", "Classroom", null)
        );
    }

    @Test
    void connectShouldCreateExit() {
        Room room = createRoom();
        Room hallway = new Room("room2", "Hallway", "A dark hallway.");

        room.connect(Direction.NORTH, hallway);

        assertSame(hallway, room.getExit(Direction.NORTH));
        assertEquals(1, room.getExits().size());
    }

    @Test
    void getExitShouldReturnNullForUnconnectedDirection() {
        Room room = createRoom();

        assertNull(room.getExit(Direction.SOUTH));
    }

    @Test
    void connectShouldRejectNullDirection() {
        Room room = createRoom();

        assertThrows(
            NullPointerException.class,
            () -> room.connect(null, createRoom())
        );
    }

    @Test
    void connectShouldRejectNullRoom() {
        Room room = createRoom();

        assertThrows(
            NullPointerException.class,
            () -> room.connect(Direction.NORTH, null)
        );
    }

    @Test
    void getExitsShouldNotAllowDirectModification() {
        Room room = createRoom();

        assertThrows(
            UnsupportedOperationException.class,
            () -> room.getExits().clear()
        );
    }

    @Test
    void addItemShouldStoreItem() {
        Room room = createRoom();
        Weapon weapon = new Weapon("Sword", "A rusty sword", 5);

        room.addItem(weapon);

        assertEquals(1, room.getItems().size());
        assertSame(weapon, room.findItem("Sword").orElse(null));
    }

    @Test
    void addItemShouldRejectNull() {
        Room room = createRoom();

        assertThrows(
            NullPointerException.class,
            () -> room.addItem(null)
        );
    }

    @Test
    void findItemShouldIgnoreLetterCase() {
        Room room = createRoom();
        room.addItem(new Weapon("Magic Sword", "A glowing sword", 8));

        assertTrue(room.findItem("magic sword").isPresent());
        assertTrue(room.findItem("MAGIC SWORD").isPresent());
    }

    @Test
    void findItemShouldReturnEmptyWhenItemIsMissing() {
        Room room = createRoom();

        assertTrue(room.findItem("Missing Item").isEmpty());
    }

    @Test
    void removeItemByNameShouldRemoveItem() {
        Room room = createRoom();
        room.addItem(new Weapon("Sword", "A rusty sword", 5));

        Item removed = room.removeItemByName("Sword");

        assertNotNull(removed);
        assertEquals("Sword", removed.getName());
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    void removeItemByNameShouldIgnoreLetterCase() {
        Room room = createRoom();
        room.addItem(new Weapon("Sword", "A rusty sword", 5));

        Item removed = room.removeItemByName("sWoRd");

        assertNotNull(removed);
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    void removeItemByNameShouldReturnNullWhenMissing() {
        Room room = createRoom();

        assertNull(room.removeItemByName("Missing Item"));
    }

    @Test
    void getItemsShouldNotAllowDirectModification() {
        Room room = createRoom();
        room.addItem(new Weapon("Sword", "A rusty sword", 5));

        assertThrows(
            UnsupportedOperationException.class,
            () -> room.getItems().clear()
        );
    }

    @Test
    void addMonsterShouldStoreMonster() {
        Room room = createRoom();
        Monster monster = new Monster(
            "Ghost", 50, 10, 3, new ArrayList<>()
        );

        room.addMonster(monster);

        assertEquals(1, room.getMonsters().size());
        assertSame(monster, room.findMonster("Ghost").orElse(null));
    }

    @Test
    void addMonsterShouldRejectNull() {
        Room room = createRoom();

        assertThrows(
            NullPointerException.class,
            () -> room.addMonster(null)
        );
    }

    @Test
    void findMonsterShouldIgnoreLetterCase() {
        Room room = createRoom();
        room.addMonster(
            new Monster("Phantom", 50, 10, 3, new ArrayList<>())
        );

        assertTrue(room.findMonster("phantom").isPresent());
        assertTrue(room.findMonster("PHANTOM").isPresent());
    }

    @Test
    void findMonsterShouldReturnEmptyWhenMissing() {
        Room room = createRoom();

        assertTrue(room.findMonster("Missing Monster").isEmpty());
    }

    @Test
    void hasLivingMonstersShouldReturnFalseWhenRoomIsEmpty() {
        Room room = createRoom();

        assertFalse(room.hasLivingMonsters());
    }

    @Test
    void hasLivingMonstersShouldReturnTrueWhenMonsterIsAlive() {
        Room room = createRoom();
        room.addMonster(
            new Monster("Ghost", 50, 10, 3, new ArrayList<>())
        );

        assertTrue(room.hasLivingMonsters());
    }

    @Test
    void hasLivingMonstersShouldReturnFalseWhenAllMonstersAreDead() {
        Room room = createRoom();
        Monster monster = new Monster(
            "Ghost", 50, 10, 3, new ArrayList<>()
        );
        monster.takeDamage(50);
        room.addMonster(monster);

        assertFalse(room.hasLivingMonsters());
    }

    @Test
    void getMonstersShouldNotAllowDirectModification() {
        Room room = createRoom();

        assertThrows(
            UnsupportedOperationException.class,
            () -> room.getMonsters().clear()
        );
    }

    @Test
    void setLockedShouldStoreLockAndRequiredKey() {
        Room room = createRoom();

        room.setLocked(true, "Basement Key");

        assertTrue(room.isLocked());
        assertEquals("Basement Key", room.getRequiredKeyName());
    }

    @Test
    void unlockShouldSucceedWhenRoomIsAlreadyUnlocked() {
        Room room = createRoom();

        assertTrue(room.unlock("Any Key"));
        assertFalse(room.isLocked());
    }

    @Test
    void unlockShouldSucceedWithCorrectKeyIgnoringCase() {
        Room room = createRoom();
        room.setLocked(true, "Basement Key");

        assertTrue(room.unlock("basement key"));
        assertFalse(room.isLocked());
    }

    @Test
    void unlockShouldFailWithIncorrectKey() {
        Room room = createRoom();
        room.setLocked(true, "Basement Key");

        assertFalse(room.unlock("Office Key"));
        assertTrue(room.isLocked());
    }

    @Test
    void unlockShouldFailWhenRequiredKeyIsNullAndRoomIsLocked() {
        Room room = createRoom();
        room.setLocked(true, null);

        assertFalse(room.unlock("Any Key"));
        assertTrue(room.isLocked());
    }

    @Test
    void setLockedFalseShouldUnlockRoom() {
        Room room = createRoom();
        room.setLocked(true, "Basement Key");

        room.setLocked(false, "Basement Key");

        assertFalse(room.isLocked());
    }

    @Test
    void setTrapShouldStoreTrap() {
        Room room = createRoom();
        Trap trap = new Trap(
        	    "Electric Trap", TrapType.ELECTRIC, 10, true, true
        	);
        room.setTrap(trap);

        assertSame(trap, room.getTrap());
    }

    @Test
    void setTrapShouldAllowRemovingTrap() {
        Room room = createRoom();
        Trap trap = new Trap(
        	    "Electric Trap", TrapType.ELECTRIC, 10, true, true
        	);
        room.setTrap(trap);

        room.setTrap(null);

        assertNull(room.getTrap());
    }
}
