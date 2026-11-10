package com.example.haunted.config;

import com.example.haunted.engine.GameEngine;
import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Direction;
import com.example.haunted.model.QuestStatus;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DungeonFactoryTest {

	private static Room lectureHall() {
		return DungeonFactory.createGame().getCurrentRoom().getExit(Direction.EAST)
	}

	@Test
	void playerStartsInStairwellWithStartingStats() {
		GameEngine game = DungeonFactory.createGame();

		assertEquals("stairwell", game.getCurrentRoom().getId());
		assertEquals("Student Explorer", game.getPlayer().getName());
		assertEquals(50, game.getPlayer().getHealth());
		assertEquals(7, game.getPlayer().getAttackPower());
		assertEquals(2, game.getPlayer().getDefensePower());
		assertEquals(8, game.getPlayer().getInventory().getCapacity());
		assertEquals(QuestStatus.NOT_STARTED, game.getQuest().getStatus());
	}

	@Test
	void examArchiveAndFinalChamberStartLocked() {

		Room lectureHall = lectureHall();
		Room examArchive = lectureHall.getExit(Direction.NORTH);
		Room finalChamber = examArchive.getExit(Direction.EAST).getExit(Direction.NORTH);

		assertFalse(lectureHall.isLocked());
		assertTrue(examArchive.isLocked());
		assertEquals("Archive Key", examArchive.getRequiredKeyName());
		assertTrue(finalChamber.isLocked());
		assertEquals("Vault Key", finalChamber.getRequiredKeyName());
	}

	@Test
	void brokenElevatorHasArmedOneTimeElectricTrap() {
		Trap trap = lectureHall().getExit(Direction.SOUTH).getTrap();

		assertEquals("Loose Wires Trap", trap.getName());
		assertEquals(TrapType.ELECTRIC, trap.getType());
		assertEquals(8, trap.getDamage());
		assertTrue(trap.isArmed());
		assertTrue(trap.isOneTimeTrigger());
	}

	@Test
	void keyItemsAndBossAreInTheRightRooms() {
		Room lectureHall = lectureHall();
		Room labStorage = lectureHall.getExit(Direction.EAST);
		Room examArchive = lectureHall.getExit(Direction.NORTH);
		Room deanVault = examArchive.getExit(Direction.EAST);
		Room finalChamber = deanVault.getExit(Direction.NORTH);

		assertTrue(labStorage.findItem("Archive Key").isPresent());
		assertTrue(examArchive.findItem("Lost Gradebook").isPresent());
		assertTrue(deanVault.findItem("Vault Key").isPresent());
		assertInstanceOf(BossMonster.class, finalChamber.findMonster("Final Exam Phantom").orElseThrow());
	}
}