package com.example.haunted.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestTest {

    private Quest createQuest() {
    	return new Quest(
            "Recover the Gradebook",
            "Find the missing gradebook and defeat the phantom."
        );
    }

    @Test
    void constructorShouldInitializeQuest() {
        Quest quest = createQuest();

        assertEquals("Recover the Gradebook", quest.getName());
        assertEquals(
            "Find the missing gradebook and defeat the phantom.",
            quest.getDescription()
        );
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
        assertFalse(quest.isGradebookRecovered());
        assertFalse(quest.isPhantomDefeated());
        assertFalse(quest.isComplete());
    }

    @Test
    void constructorShouldRejectNullName() {
        assertThrows(
            NullPointerException.class,
            () -> new Quest(null, "Quest description")
        );
    }

    @Test
    void constructorShouldRejectNullDescription() {
        assertThrows(
            NullPointerException.class,
            () -> new Quest("Quest name", null)
        );
    }

    @Test
    void recoveringGradebookShouldSetInProgress() {
        Quest quest = createQuest();

        quest.markGradebookRecovered();

        assertTrue(quest.isGradebookRecovered());
        assertFalse(quest.isPhantomDefeated());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
        assertFalse(quest.isComplete());
    }

    @Test
    void defeatingPhantomShouldSetInProgress() {
        Quest quest = createQuest();

        quest.markPhantomDefeated();

        assertFalse(quest.isGradebookRecovered());
        assertTrue(quest.isPhantomDefeated());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
        assertFalse(quest.isComplete());
    }

    @Test
    void recoveringGradebookAndDefeatingPhantomShouldCompleteQuest() {
        Quest quest = createQuest();

        quest.markGradebookRecovered();
        quest.markPhantomDefeated();

        assertTrue(quest.isGradebookRecovered());
        assertTrue(quest.isPhantomDefeated());
        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
        assertTrue(quest.isComplete());
    }

    @Test
    void defeatingPhantomBeforeRecoveringGradebookShouldCompleteQuest() {
        Quest quest = createQuest();

        quest.markPhantomDefeated();
        quest.markGradebookRecovered();

        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
        assertTrue(quest.isComplete());
    }

    @Test
    void updateStatusShouldKeepQuestNotStartedWhenNothingIsDone() {
        Quest quest = createQuest();

        quest.updateStatus();

        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
        assertFalse(quest.isComplete());
    }

    @Test
    void updatingStatusAgainShouldPreserveCompletedQuest() {
        Quest quest = createQuest();

        quest.markGradebookRecovered();
        quest.markPhantomDefeated();
        quest.updateStatus();

        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
        assertTrue(quest.isComplete());
    }

    @Test
    void markingGradebookRecoveredTwiceShouldKeepQuestInProgress() {
        Quest quest = createQuest();

        quest.markGradebookRecovered();
        quest.markGradebookRecovered();

        assertTrue(quest.isGradebookRecovered());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
    }

    @Test
    void markingPhantomDefeatedTwiceShouldKeepQuestInProgress() {
        Quest quest = createQuest();

        quest.markPhantomDefeated();
        quest.markPhantomDefeated();

        assertTrue(quest.isPhantomDefeated());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
    }
}
