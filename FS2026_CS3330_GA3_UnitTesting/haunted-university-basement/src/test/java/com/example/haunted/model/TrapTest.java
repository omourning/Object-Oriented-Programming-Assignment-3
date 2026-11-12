package com.example.haunted.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrapTest {

    private Trap createTrap() {
        return new Trap(
            "Electric Trap",
            TrapType.ELECTRIC,
            20,
            true,
            true
        );
    }

    @Test
    void constructorShouldInitializeTrapProperties() {
        Trap trap = createTrap();

        assertEquals("Electric Trap", trap.getName());
        assertEquals(TrapType.ELECTRIC, trap.getType());
        assertEquals(20, trap.getDamage());
        assertTrue(trap.isArmed());
        assertTrue(trap.isOneTimeTrigger());
    }

    @Test
    void constructorShouldRejectNullName() {
        assertThrows(
            NullPointerException.class,
            () -> new Trap(null, TrapType.ELECTRIC, 20, true, true)
        );
    }

    @Test
    void constructorShouldRejectNullType() {
        assertThrows(
            NullPointerException.class,
            () -> new Trap("Electric Trap", null, 20, true, true)
        );
    }

    @Test
    void disarmShouldSetArmedToFalse() {
        Trap trap = createTrap();

        trap.disarm();

        assertFalse(trap.isArmed());
    }

    @Test
    void disarmingAnAlreadyDisarmedTrapShouldKeepItDisarmed() {
        Trap trap = new Trap(
            "Steam Trap",
            TrapType.STEAM,
            10,
            false,
            false
        );

        trap.disarm();

        assertFalse(trap.isArmed());
    }

    @Test
    void constructorShouldPreserveUnarmedState() {
        Trap trap = new Trap(
            "Steam Trap",
            TrapType.STEAM,
            10,
            false,
            true
        );

        assertFalse(trap.isArmed());
        assertTrue(trap.isOneTimeTrigger());
    }

    @Test
    void trapShouldSupportDifferentDamageValues() {
        Trap noDamageTrap = new Trap(
            "Weak Trap", TrapType.ELECTRIC, 0, true, false
        );

        Trap strongTrap = new Trap(
            "Strong Trap", TrapType.STEAM, 100, true, false
        );

        assertEquals(0, noDamageTrap.getDamage());
        assertEquals(100, strongTrap.getDamage());
    }

    @Test
    void trapShouldPreserveRepeatTriggerSetting() {
        Trap trap = new Trap(
            "Steam Trap",
            TrapType.STEAM,
            15,
            true,
            false
        );

        assertFalse(trap.isOneTimeTrigger());
    }
}
