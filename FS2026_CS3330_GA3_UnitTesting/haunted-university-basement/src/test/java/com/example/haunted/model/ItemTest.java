package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ItemTest {

	@Test
	void itemExposesNameDescriptionAndToString() {
		Item weapon = new Weapon("Stapler of Justice", "A heavy duty stapler.", 4);

		assertEquals("A heavy duty stapler.", weapon.getDescription());
		assertEquals("Stapler of Justice", weapon.toString());
	}

	@Test
	void potionReportsHealingAmount() {
		Potion potion = new Potion("Coffee Potion", "Warm coffee.", 15);

		assertEquals(15, potion.getHealingAmount());
	}
}