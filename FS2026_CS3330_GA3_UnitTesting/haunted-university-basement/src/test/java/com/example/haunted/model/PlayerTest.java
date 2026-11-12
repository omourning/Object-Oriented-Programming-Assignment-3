package com.example.haunted.model;

import com.example.haunted.config.DungeonFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player createPlayer() {
        return new Player("Gabe", 100, 10, 5, new Inventory(10));
    }

    @Test
    void constructorShouldInitializePlayerStats() {
        Player player = createPlayer();

        assertEquals("Gabe", player.getName());
        assertEquals(100, player.getHealth());
        assertEquals(100, player.getMaxHealth());
        assertEquals(10, player.getBaseAttack());
        assertEquals(5, player.getBaseDefense());
        assertNotNull(player.getInventory());
    }

    @Test
    void constructorShouldRejectNullName() {
        assertThrows(NullPointerException.class,
                () -> new Player(null, 100, 10, 5, new Inventory(10)));
    }

    @Test
    void constructorShouldRejectNullInventory() {
        assertThrows(NullPointerException.class,
                () -> new Player("Gabe", 100, 10, 5, null));
    }

    @Test
    void playerShouldStartWithNoEquipmentOrRoom() {
        Player player = createPlayer();

        assertNull(player.getEquippedWeapon());
        assertNull(player.getEquippedArmor());
        assertNull(player.getCurrentRoom());
    }

    @ParameterizedTest
    @CsvSource({
        "0, 100",
        "10, 90",
        "50, 50",
        "100, 0",
        "150, 0",
        "-5, 100"
    })
    void takeDamageShouldHandleDifferentAmounts(int damage, int expectedHealth) {
        Player player = createPlayer();

        player.takeDamage(damage);

        assertEquals(expectedHealth, player.getHealth());
        assertEquals(expectedHealth > 0, player.isAlive());
    }

    @ParameterizedTest
    @CsvSource({
        "0, 60",
        "10, 70",
        "40, 100",
        "100, 100",
        "-5, 60"
    })
    void healShouldRespectHealthBoundaries(int healing, int expectedHealth) {
        Player player = createPlayer();
        player.takeDamage(40);

        player.heal(healing);

        assertEquals(expectedHealth, player.getHealth());
    }

    @Test
    void lethalDamageShouldMakePlayerNotAlive() {
        Player player = createPlayer();

        player.takeDamage(100);

        assertFalse(player.isAlive());
    }

    @Test
    void healingShouldNotRevivePlayerWithoutPositiveHealth() {
        Player player = createPlayer();
        player.takeDamage(100);

        player.heal(50);

        assertEquals(50, player.getHealth());
        assertTrue(player.isAlive());
    }

    @Test
    void equipWeaponShouldIncreaseAttackPower() {
        Player player = createPlayer();
        Weapon weapon = new Weapon("Magic Sword", "A powerful sword", 7);

        player.equipWeapon(weapon);

        assertSame(weapon, player.getEquippedWeapon());
        assertEquals(17, player.getAttackPower());
    }

    @Test
    void equipArmorShouldIncreaseDefensePower() {
        Player player = createPlayer();
        Armor armor = new Armor("Ghost Armor", "Protective armor", 8);

        player.equipArmor(armor);

        assertSame(armor, player.getEquippedArmor());
        assertEquals(13, player.getDefensePower());
    }

    @Test
    void attackAndDefenseShouldUseBaseStatsWithoutEquipment() {
        Player player = createPlayer();

        assertEquals(10, player.getAttackPower());
        assertEquals(5, player.getDefensePower());
    }

    @Test
    void equipWeaponShouldRejectNull() {
        Player player = createPlayer();

        assertThrows(NullPointerException.class,
                () -> player.equipWeapon(null));
    }

    @Test
    void equipArmorShouldRejectNull() {
        Player player = createPlayer();

        assertThrows(NullPointerException.class,
                () -> player.equipArmor(null));
    }

    @Test
    void setCurrentRoomShouldStoreRoom() {
        Player player = createPlayer();
        Room room = DungeonFactory.createGame().getCurrentRoom();

        player.setCurrentRoom(room);

        assertSame(room, player.getCurrentRoom());
    }

    @Test
    void setCurrentRoomShouldRejectNull() {
        Player player = createPlayer();

        assertThrows(NullPointerException.class,
                () -> player.setCurrentRoom(null));
    }

    @Test
    void playerShouldKeepTheProvidedInventory() {
        Inventory inventory = new Inventory(5);
        Player player = new Player("Gabe", 100, 10, 5, inventory);

        assertSame(inventory, player.getInventory());
        assertEquals(5, player.getInventory().getCapacity());
    }
}
