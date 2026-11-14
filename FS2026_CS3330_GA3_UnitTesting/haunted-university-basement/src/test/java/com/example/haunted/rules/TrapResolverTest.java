package com.example.haunted.rules;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrapResolverTest {

    private final TrapResolver resolver = new TrapResolver();

    private Player createPlayer() {
        return new Player("Gabe", 100, 10, 5, new Inventory(10));
    }

    private Trap createTrap(boolean armed, boolean oneTime, int damage) {
        return new Trap(
            "Electric Trap",
            TrapType.ELECTRIC,
            damage,
            armed,
            oneTime
        );
    }

    @Test
    void resolveTrapShouldReturnFalseWhenTrapIsNull() {
        Player player = createPlayer();

        InteractionResult result = resolver.resolveTrap(player, null);

        assertFalse(result.isSuccess());
        assertEquals(100, player.getHealth());
        assertEquals("No trap was triggered.", result.getMessage());
    }

    @Test
    void resolveTrapShouldReturnFalseWhenTrapIsDisarmed() {
        Player player = createPlayer();
        Trap trap = createTrap(false, true, 20);

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertFalse(result.isSuccess());
        assertEquals(100, player.getHealth());
        assertFalse(trap.isArmed());
        assertEquals("No trap was triggered.", result.getMessage());
    }

    @Test
    void resolveTrapShouldDamagePlayerWhenTrapIsArmed() {
        Player player = createPlayer();
        Trap trap = createTrap(true, false, 20);

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertTrue(result.isSuccess());
        assertEquals(80, player.getHealth());
        assertTrue(trap.isArmed());
    }

    @Test
    void resolveTrapShouldDisarmOneTimeTrap() {
        Player player = createPlayer();
        Trap trap = createTrap(true, true, 20);

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertTrue(result.isSuccess());
        assertEquals(80, player.getHealth());
        assertFalse(trap.isArmed());
    }

    @Test
    void resolveTrapShouldKeepRepeatableTrapArmed() {
        Player player = createPlayer();
        Trap trap = createTrap(true, false, 20);

        resolver.resolveTrap(player, trap);

        assertTrue(trap.isArmed());

        InteractionResult secondResult = resolver.resolveTrap(player, trap);

        assertTrue(secondResult.isSuccess());
        assertEquals(60, player.getHealth());
        assertTrue(trap.isArmed());
    }

    @Test
    void resolveTrapShouldHandleZeroDamage() {
        Player player = createPlayer();
        Trap trap = createTrap(true, false, 0);

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertTrue(result.isSuccess());
        assertEquals(100, player.getHealth());
        assertTrue(trap.isArmed());
    }

    @Test
    void resolveTrapShouldNotReducePlayerHealthBelowZero() {
        Player player = createPlayer();
        Trap trap = createTrap(true, false, 150);

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertTrue(result.isSuccess());
        assertEquals(0, player.getHealth());
        assertFalse(player.isAlive());
    }

    @Test
    void resolveTrapShouldIncludeTrapNameAndDamageInMessage() {
        Player player = createPlayer();
        Trap trap = createTrap(true, false, 15);

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertEquals(
            "Trap 'Electric Trap' triggered for 15 damage.",
            result.getMessage()
        );
    }
}
