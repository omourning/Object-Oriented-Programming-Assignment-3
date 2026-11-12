 package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonsterTest {

    private Monster createMonster(int health) {
        return new Monster("Ghost", health, 15, 5, new ArrayList<>());
    }

    @Test
    void constructorShouldInitializeMonsterStats() {
        Monster monster = createMonster(100);

        assertEquals("Ghost", monster.getName());
        assertEquals(100, monster.getHealth());
        assertEquals(100, monster.getMaxHealth());
        assertEquals(15, monster.getAttack());
        assertEquals(5, monster.getDefense());
        assertTrue(monster.isAlive());
    }

    @Test
    void constructorShouldRejectNullName() {
        assertThrows(
            NullPointerException.class,
            () -> new Monster(null, 100, 15, 5, new ArrayList<>())
        );
    }

    @Test
    void constructorShouldRejectNullLootList() {
        assertThrows(
            NullPointerException.class,
            () -> new Monster("Ghost", 100, 15, 5, null)
        );
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
    void takeDamageShouldHandleDifferentAmounts(
            int damage, int expectedHealth) {
        Monster monster = createMonster(100);

        monster.takeDamage(damage);

        assertEquals(expectedHealth, monster.getHealth());
        assertEquals(expectedHealth > 0, monster.isAlive());
    }

    @Test
    void monsterShouldRemainDeadAfterAdditionalDamage() {
        Monster monster = createMonster(50);

        monster.takeDamage(50);
        monster.takeDamage(20);

        assertEquals(0, monster.getHealth());
        assertFalse(monster.isAlive());
    }

    @Test
    void damageShouldNotReduceHealthBelowZero() {
        Monster monster = createMonster(20);

        monster.takeDamage(100);

        assertEquals(0, monster.getHealth());
        assertFalse(monster.isAlive());
    }

    @Test
    void monsterShouldStartWithProvidedLoot() {
        Weapon weapon = new Weapon("Rusty Sword", "An old sword", 3);
        Armor armor = new Armor("Old Armor", "Worn armor", 2);
        List<Item> loot = new ArrayList<>();
        loot.add(weapon);
        loot.add(armor);

        Monster monster = new Monster("Ghost", 50, 10, 3, loot);

        assertEquals(2, monster.getLoot().size());
        assertSame(weapon, monster.getLoot().get(0));
        assertSame(armor, monster.getLoot().get(1));
    }

    @Test
    void monsterShouldKeepItsOwnCopyOfLoot() {
        List<Item> loot = new ArrayList<>();
        loot.add(new Weapon("Sword", "A sword", 5));

        Monster monster = new Monster("Ghost", 50, 10, 3, loot);

        loot.clear();

        assertEquals(1, monster.getLoot().size());
    }

    @Test
    void getLootShouldNotAllowDirectModification() {
        Monster monster = createMonster(50);

        assertThrows(
            UnsupportedOperationException.class,
            () -> monster.getLoot().add(
                new Weapon("Sword", "A sword", 5)
            )
        );
    }

    @Test
    void monsterWithZeroHealthShouldNotBeAlive() {
        Monster monster = createMonster(0);

        assertEquals(0, monster.getHealth());
        assertFalse(monster.isAlive());
    }
}
