package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory createInventory(int capacity) {
        return new Inventory(capacity);
    }

    private Weapon createWeapon(String name) {
        return new Weapon(name, "A weapon for testing", 5);
    }

    private Armor createArmor(String name) {
        return new Armor(name, "Armor for testing", 3);
    }

    @Test
    void newInventoryShouldBeEmpty() {
        Inventory inventory = createInventory(5);

        assertTrue(inventory.getItems().isEmpty());
        assertFalse(inventory.isFull());
        assertEquals(5, inventory.getCapacity());
    }

    @Test
    void addItemShouldStoreItem() {
        Inventory inventory = createInventory(5);
        Weapon weapon = createWeapon("Sword");

        assertTrue(inventory.addItem(weapon));
        assertTrue(inventory.contains("Sword"));
        assertEquals(1, inventory.getItems().size());
    }

    @Test
    void addItemShouldAcceptDifferentItemTypes() {
        Inventory inventory = createInventory(5);

        assertTrue(inventory.addItem(createWeapon("Sword")));
        assertTrue(inventory.addItem(createArmor("Armor")));

        assertEquals(2, inventory.getItems().size());
    }

    @Test
    void addItemShouldFailWhenInventoryIsFull() {
        Inventory inventory = createInventory(1);
        inventory.addItem(createWeapon("Sword"));

        assertFalse(inventory.addItem(createArmor("Armor")));
        assertEquals(1, inventory.getItems().size());
    }

    @Test
    void inventoryShouldBecomeFullAtCapacity() {
        Inventory inventory = createInventory(2);

        inventory.addItem(createWeapon("Sword"));
        assertFalse(inventory.isFull());

        inventory.addItem(createArmor("Armor"));
        assertTrue(inventory.isFull());
    }

    @Test
    void findItemShouldIgnoreLetterCase() {
        Inventory inventory = createInventory(5);
        inventory.addItem(createWeapon("Magic Sword"));

        assertTrue(inventory.findItem("magic sword").isPresent());
        assertTrue(inventory.findItem("MAGIC SWORD").isPresent());
    }

    @Test
    void findItemShouldReturnEmptyWhenItemDoesNotExist() {
        Inventory inventory = createInventory(5);

        assertTrue(inventory.findItem("Missing Item").isEmpty());
    }

    @Test
    void containsShouldReturnFalseForMissingItem() {
        Inventory inventory = createInventory(5);

        assertFalse(inventory.contains("Sword"));
    }

    @Test
    void removeItemShouldRemoveExistingItem() {
        Inventory inventory = createInventory(5);
        inventory.addItem(createWeapon("Sword"));

        Item removed = inventory.removeItem("Sword");

        assertNotNull(removed);
        assertEquals("Sword", removed.getName());
        assertFalse(inventory.contains("Sword"));
        assertTrue(inventory.getItems().isEmpty());
    }

    @Test
    void removeItemShouldIgnoreLetterCase() {
        Inventory inventory = createInventory(5);
        inventory.addItem(createWeapon("Sword"));

        Item removed = inventory.removeItem("sWoRd");

        assertNotNull(removed);
        assertFalse(inventory.contains("Sword"));
    }

    @Test
    void removeItemShouldReturnNullWhenItemDoesNotExist() {
        Inventory inventory = createInventory(5);

        assertNull(inventory.removeItem("Missing Item"));
    }

    @Test
    void removingItemShouldMakeSpaceInInventory() {
        Inventory inventory = createInventory(1);
        inventory.addItem(createWeapon("Sword"));

        inventory.removeItem("Sword");

        assertFalse(inventory.isFull());
        assertTrue(inventory.addItem(createArmor("Armor")));
    }

    @Test
    void getItemsShouldNotAllowDirectModification() {
        Inventory inventory = createInventory(5);
        inventory.addItem(createWeapon("Sword"));

        assertThrows(
            UnsupportedOperationException.class,
            () -> inventory.getItems().clear()
        );
    }

    @Test
    void zeroCapacityInventoryShouldBeFullImmediately() {
        Inventory inventory = createInventory(0);

        assertTrue(inventory.isFull());
        assertFalse(inventory.addItem(createWeapon("Sword")));
    }

    @ParameterizedTest
    @CsvSource({
        "1, 1",
        "2, 2",
        "3, 3"
    })
    void inventoryShouldStoreItemsUpToItsCapacity(
            int capacity, int expectedItems) {
        Inventory inventory = createInventory(capacity);

        for (int i = 0; i < capacity; i++) {
            assertTrue(inventory.addItem(createWeapon("Weapon " + i)));
        }

        assertEquals(expectedItems, inventory.getItems().size());
        assertTrue(inventory.isFull());
    }
}
