package com.nouradine.agriculture

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AgricultureCalculationsTest {
    @Test
    fun calculatesOneHectareAtTenMillimetres() {
        assertEquals(100.0, AgricultureCalculations.irrigationVolumeCubicMetres(1.0, 10.0)!!, 0.0001)
    }

    @Test
    fun calculatesFractionalAreaAndDepth() {
        assertEquals(37.5, AgricultureCalculations.irrigationVolumeCubicMetres(0.75, 5.0)!!, 0.0001)
    }

    @Test
    fun rejectsZeroAndNegativeInputs() {
        assertNull(AgricultureCalculations.irrigationVolumeCubicMetres(0.0, 10.0))
        assertNull(AgricultureCalculations.irrigationVolumeCubicMetres(1.0, -2.0))
    }

    @Test
    fun rejectsNonFiniteInputs() {
        assertNull(AgricultureCalculations.irrigationVolumeCubicMetres(Double.NaN, 10.0))
        assertNull(AgricultureCalculations.irrigationVolumeCubicMetres(1.0, Double.POSITIVE_INFINITY))
    }
}
