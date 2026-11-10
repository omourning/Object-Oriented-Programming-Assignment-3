package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;
import com.example.haunted.rules.TrapResolver;

class MovementEngineTest {

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
		return target;
	}

	@ParameterizedTest
	@EnumSource(Direction.class)
	void movingWithNoExitFailsAndPlayerStays(Direction direction) {
		Room start = startRoom();
		Player player = player = playerIn(start);

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
		targetNorthOf(start).setLocked(true, "Archive Key");
		Player player = playerIn(start);

		MoveResult result = engine().move(player, Direction.NORTH);

		assertFalse(result.isSuccess());
		assertEquals("The room is locked.", result.getMessage());
		assertSame(start, player.getCurrentRoom());
	}

	@Test
	void movingIntoSafeRoomSucceeds() {
		Room start = startRoom();
		Room target = targetNorthOf(start);
		Player player = playerIn(start);

		MoveResult result = engine().move(player, Direction.NORTH);

		assertTrue(result.isSuccess());
		assertEquals("Moved to Target Room.", result.getMessage());
		assertFalse(result.isTrapTriggered());
		assertEquals(0, result.getTrapDamage());
		assertSame(target, player.getCurrentRoom());
		assertEquals(50, player.getHealth());
	}

	@ParameterizedTest(name = "trap armed={0} leaves {1} HP, triggered={2}")
	@CsvSource({ "true, 42, true", "false, 50, false" })
	void trapOnlyTriggerWhenArmed(boolean armed, int expectedHealth, boolean expectedTriggered) {
		Room start = startRoom();
		targetNorthOf(start).setTrap(new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, armed, true));
		Player player = playerIn(start);

		MoveResult result = engine().move(player, Direction.NORTH);

		assertTrue(result.isSuccess());
		assertEquals(expectedTriggered, result.isTrapTriggered());
		assertEquals(expectedTriggered ? 8 : 0, result.getTrapDamage());
		assertEquals(expectedHealth, player.getHealth());
	}

	@Test
	void trapMessageIncludesTrapDetails() {

		Room start = startRoom();
		targetNorthOf(start).setTrap(new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, true, true));

		MoveResult result = engine().move(playerIn(start), Direction.NORTH);

		assertEquals("Moved to Target Room. Trap 'Loose Wires Trap' triggered for 8 damage.", result.getMessage());
	}

	@Test
	void oneTimeTrapOnlyHitsOnFirstEntry() {

		Room start = startRoom();
		targetNorthOf(start).setTrap(new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, true, true));
		Player player = playerIn(start);
		MovementEngine engine = engine();

		engine.move(player, Direction.NORTH);
		engine.move(player, Direction.SOUTH);
		MoveResult second = engine.move(player, Direction.NORTH);

		assertFalse(second.isTrapTriggered());
		assertEquals(42, player.getHealth());
	}

}