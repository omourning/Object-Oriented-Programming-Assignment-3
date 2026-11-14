package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.rules.TrapResolver;

class MovementEngine {
	private static MovementEngine engine() {
		return new MovementEngine(new TrapResolver());
	}

	private static Player playerIn(Room room) {
		Player player = new Player("Tester", 50, 7, 2, new Inventory(8));
		player.setCurrentRoom(room);
		return player;
	}

	private static Room startRoom() {
		return new Room("start", "Start Room", "Starting room.");
	}

	private static Room targetNorthOf(Room start) {
		Room target = new Room("target", "Target Room", "Destination room.");
		start.connect(Direction.NORTH, target);
		target.connect(Direction.SOUTH, start);
	}

	@ParameterizedTest
	@EnumSource(Direction.class)
	void movingWithNoExitFailsAndPlayerStays(Direction direction) {
		Room start = startRoom();
		Player player = playerIn(start);

		MoveResult result = engine().move(player, direction);

		assertFalse(result.isSuccess());
		assertEquals("There is no room in that direction.", result.getMessage());
		assertFalse(result.isTrapTriggered());
		assertEquals(0, result.getTrapDamage());
		assertSame(start, player.getCurrentRoom());
	}

	@Test
	void lockedRoomBlocksMovement() {
		Room start = startRoom();
//		Room examArchive = lectureHall.getExit(Direction.NORTH);
//		Room finalChamber = examArchive.getExit(Direction.EAST).getExit(Direction.NORTH);
//
//		assertFalse(lectureHall.isLocked());
//		assertTrue(examArchive.isLocked());
//		assertEquals("Archive Key", examArchive.getRequiredKeyName());
//		assertTrue(finalChamber.isLocked());
//		assertEquals("Vault Key", finalChamber.getRequiredKeyName());
	}

	@Test
	void movingIntoSafeRoomSucceeds() {
		Room start = startRoom();
//		Room examArchive = lectureHall.getExit(Direction.NORTH);
//		Room finalChamber = examArchive.getExit(Direction.EAST).getExit(Direction.NORTH);
//
//		assertFalse(lectureHall.isLocked());
//		assertTrue(examArchive.isLocked());
//		assertEquals("Archive Key", examArchive.getRequiredKeyName());
//		assertTrue(finalChamber.isLocked());
//		assertEquals("Vault Key", finalChamber.getRequiredKeyName());
	}

	@ParameterizedTest(name = "trap armed={0} leaves {1} HP, triggered={2}")
	@CsvSource({

	})
	void trapOnlyTriggersWhenArmed(boolean armed, int expectedHealth, boolean expectedTriggered) {
		Room start = startRoom();
	}

	@Test
	void trapMessageIncludesTrapDetails() {
		Room start = startRoom();
//		Room examArchive = lectureHall.getExit(Direction.NORTH);
//		Room finalChamber = examArchive.getExit(Direction.EAST).getExit(Direction.NORTH);
//
//		assertFalse(lectureHall.isLocked());
//		assertTrue(examArchive.isLocked());
//		assertEquals("Archive Key", examArchive.getRequiredKeyName());
//		assertTrue(finalChamber.isLocked());
//		assertEquals("Vault Key", finalChamber.getRequiredKeyName());
	}

	@Test
	void oneTimeTrapOnlyHitsOnFirstEntry() {
		Room start = startRoom();
//		Room examArchive = lectureHall.getExit(Direction.NORTH);
//		Room finalChamber = examArchive.getExit(Direction.EAST).getExit(Direction.NORTH);
//
//		assertFalse(lectureHall.isLocked());
//		assertTrue(examArchive.isLocked());
//		assertEquals("Archive Key", examArchive.getRequiredKeyName());
//		assertTrue(finalChamber.isLocked());
//		assertEquals("Vault Key", finalChamber.getRequiredKeyName());
	}

}
