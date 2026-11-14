package com.example.haunted.rules;

import com.example.haunted.model.Armor;
import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Weapon;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DamageCalculatorTest {

    private final DamageCalculator calculator = new DamageCalculator();

    private Player createPlayer(int attack, int defense) {
        return new Player("Gabe", 100, attack, defense, new Inventory(10));
    }

    private Monster createMonster(int attack, int defense) {
        return new Monster("Ghost", 100, attack, defense, new ArrayList<>());
    }

    @Test
    void playerDamageShouldSubtractMonsterDefense() {
        Player player = createPlayer(15, 5);
        Monster monster = createMonster(10, 5);

        assertEquals(10, calculator.calculatePlayerDamage(player, monster));
    }

    @Test
    void playerDamageShouldBeAtLeastOne() {
        Player player = createPlayer(5, 10);
        Monster monster = createMonster(10, 10);

        assertEquals(1, calculator.calculatePlayerDamage(player, monster));
    }

    @Test
    void playerDamageShouldIncludeWeaponBonus() {
        Player player = createPlayer(10, 5);
        player.equipWeapon(new Weapon("Sword", "A sharp sword", 5));
        Monster monster = createMonster(10, 3);

        assertEquals(12, calculator.calculatePlayerDamage(player, monster));
    }

    @Test
    void monsterDamageShouldSubtractPlayerDefense() {
        Monster monster = createMonster(15, 5);
        Player player = createPlayer(10, 5);

        assertEquals(10, calculator.calculateMonsterDamage(monster, player));
    }

    @Test
    void monsterDamageShouldBeAtLeastOne() {
        Monster monster = createMonster(3, 5);
        Player player = createPlayer(10, 10);

        assertEquals(1, calculator.calculateMonsterDamage(monster, player));
    }

    @Test
    void monsterDamageShouldIncludePlayerArmorBonus() {
        Monster monster = createMonster(15, 5);
        Player player = createPlayer(10, 5);
        player.equipArmor(new Armor("Armor", "Protective armor", 5));

        assertEquals(5, calculator.calculateMonsterDamage(monster, player));
    }

    @Test
    void bossDamageShouldUseCurrentAttack() {
        BossMonster boss = new BossMonster(
            "Final Exam Phantom",
            100,
            20,
            5,
            new ArrayList<>(),
            10
        );

        Player player = createPlayer(10, 5);

        // Above half health, the boss uses its normal attack (20).
        assertEquals(15, calculator.calculateMonsterDamage(boss, player));

        // At half health, the boss gains its enraged attack bonus (+10).
        boss.takeDamage(50);

        assertEquals(25, calculator.calculateMonsterDamage(boss, player));
    	}
    }
