package org.cedacri.pingpong.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetTypesEnumTest {

    @Test
    @DisplayName("Test if SetTypesEnum values are correctly assigned")
    void testEnumValues() {
        assertEquals(1, SetTypesEnum.BEST_OF_ONE.getValue(), "BEST_OF_ONE should have value 1");
        assertEquals(3, SetTypesEnum.BEST_OF_THREE.getValue(), "BEST_OF_THREE should have value 3");
        assertEquals(5, SetTypesEnum.BEST_OF_FIVE.getValue(), "BEST_OF_FIVE should have value 5");
        assertEquals(7, SetTypesEnum.BEST_OF_SEVEN.getValue(), "BEST_OF_SEVEN should have value 7");
    }

    @Test
    @DisplayName("Test if all SetTypesEnum values exist")
    void testEnumValuesExistence() {
        SetTypesEnum[] values = SetTypesEnum.values();
        assertEquals(4, values.length, "There should be exactly 4 SetTypesEnum values");
        assertArrayEquals(new SetTypesEnum[]{SetTypesEnum.BEST_OF_ONE, SetTypesEnum.BEST_OF_THREE, SetTypesEnum.BEST_OF_FIVE, SetTypesEnum.BEST_OF_SEVEN}, values);
    }

    @Test
    @DisplayName("Test if valueOf works correctly")
    void testEnumValueOf() {
        assertEquals(SetTypesEnum.BEST_OF_ONE, SetTypesEnum.valueOf("BEST_OF_ONE"));
        assertEquals(SetTypesEnum.BEST_OF_THREE, SetTypesEnum.valueOf("BEST_OF_THREE"));
        assertEquals(SetTypesEnum.BEST_OF_FIVE, SetTypesEnum.valueOf("BEST_OF_FIVE"));
        assertEquals(SetTypesEnum.BEST_OF_SEVEN, SetTypesEnum.valueOf("BEST_OF_SEVEN"));
    }

    @Test
    @DisplayName("Test invalid enum valueOf throws exception")
    void testInvalidEnumValueOf() {
        assertThrows(IllegalArgumentException.class, () -> SetTypesEnum.valueOf("BEST_OF_TEN"));
    }
}
