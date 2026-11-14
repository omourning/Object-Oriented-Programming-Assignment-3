package com.example.haunted.rules;

import com.example.haunted.model.Item;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestStatus;
import com.example.haunted.model.Weapon;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class QuestTrackerTest {

    private final QuestTracker tracker = new QuestTracker();

    private Quest createQuest() {
        return new Quest("Main Quest", "Recover the gradebook and defeat the phantom.");
    }

    private Monster createMonster(String name, int health) {
        return new Monster(name, health, 10, 2, new ArrayList<>());
    }

    @Test
    void updateQuestForItemShouldRecognizeLostGradebook() {
        Quest quest = createQuest();
        Item item = new Weapon("Lost Gradebook", "The missing gradebook", 0);

        tracker.updateQuestForItem(quest, item);

        assertTrue(quest.isGradebookRecovered());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
    }

    @Test
    void updateQuestForItemShouldIgnoreLetterCase() {
        Quest quest = createQuest();
        Item item = new Weapon("lost gradebook", "The missing gradebook", 0);

        tracker.updateQuestForItem(quest, item);

        assertTrue(quest.isGradebookRecovered());
    }

    @Test
    void updateQuestForItemShouldIgnoreOtherItems() {
        Quest quest = createQuest();
        Item item = new Weapon("Rusty Sword", "An old sword", 5);

        tracker.updateQuestForItem(quest, item);

        assertFalse(quest.isGradebookRecovered());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
    }

    @Test
    void updateQuestForItemShouldIgnoreNullItem() {
        Quest quest = createQuest();

        tracker.updateQuestForItem(quest, null);

        assertFalse(quest.isGradebookRecovered());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
    }

    @Test
    void updateQuestForMonsterShouldRecognizeDefeatedFinalExamPhantom() {
        Quest quest = createQuest();
        Monster monster = createMonster("Final Exam Phantom", 0);

        tracker.updateQuestForMonster(quest, monster);

        assertTrue(quest.isPhantomDefeated());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
    }

    @Test
    void updateQuestForMonsterShouldIgnoreLetterCase() {
        Quest quest = createQuest();
        Monster monster = createMonster("final exam phantom", 0);

        tracker.updateQuestForMonster(quest, monster);

        assertTrue(quest.isPhantomDefeated());
    }

    @Test
    void updateQuestForMonsterShouldIgnoreLivingPhantom() {
        Quest quest = createQuest();
        Monster monster = createMonster("Final Exam Phantom", 50);

        tracker.updateQuestForMonster(quest, monster);

        assertFalse(quest.isPhantomDefeated());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
    }

    @Test
    void updateQuestForMonsterShouldIgnoreOtherDefeatedMonsters() {
        Quest quest = createQuest();
        Monster monster = createMonster("Hallway Ghost", 0);

        tracker.updateQuestForMonster(quest, monster);

        assertFalse(quest.isPhantomDefeated());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
    }

    @Test
    void updateQuestForMonsterShouldIgnoreNullMonster() {
        Quest quest = createQuest();

        tracker.updateQuestForMonster(quest, null);

        assertFalse(quest.isPhantomDefeated());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
    }

    @Test
    void completingBothObjectivesShouldCompleteQuest() {
        Quest quest = createQuest();
        Item item = new Weapon("Lost Gradebook", "The missing gradebook", 0);
        Monster monster = createMonster("Final Exam Phantom", 0);

        tracker.updateQuestForItem(quest, item);
        tracker.updateQuestForMonster(quest, monster);

        assertTrue(quest.isGradebookRecovered());
        assertTrue(quest.isPhantomDefeated());
        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
        assertTrue(quest.isComplete());
    }
}
