package com.example.haunted;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class MainTest {

	@Test
	void mainRunsWithoutErrors() {
		assertDoesNotThrow(() -> Main.main(new String[0]));
	}
}