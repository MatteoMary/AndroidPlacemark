package org.activities.models

import org.junit.Assert.*
import org.junit.Test
import org.wit.activities.models.AthleteModel

class AthleteModelTest {

    @Test
    fun `creating athlete sets default values`() {
        val athlete = AthleteModel()

        assertEquals(0, athlete.id)
        assertEquals("", athlete.name)
        assertEquals("", athlete.description)
        assertEquals("All-rounder", athlete.role)
        assertTrue(athlete.isActive)
    }

    @Test
    fun `setting name and description updates fields`() {
        val athlete = AthleteModel()
        athlete.name = "Matteo Mary"
        athlete.description = "Middle distance runner"

        assertEquals("Matteo Mary", athlete.name)
        assertEquals("Middle distance runner", athlete.description)
    }

    @Test
    fun `two athletes with same id are considered equal`() {
        val athlete1 = AthleteModel(id = 1, name = "Runner A")
        val athlete2 = AthleteModel(id = 1, name = "Runner B")

        assertEquals(athlete1.id, athlete2.id)
        assertNotEquals(athlete1.name, athlete2.name)
    }
}
