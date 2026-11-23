package com.nouradine.agriculture

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AgricultureCalculationsTest {
    @Test fun irrigationVolumeUsesHectaresAndMillimetres() {
        assertEquals(100.0, AgricultureCalculations.irrigationVolumeCubicMetres(1.0, 10.0)!!, 0.0001)
        assertEquals(37.5, AgricultureCalculations.irrigationVolumeCubicMetres(0.75, 5.0)!!, 0.0001)
    }

    @Test fun irrigationRejectsInvalidValues() {
        assertNull(AgricultureCalculations.irrigationVolumeCubicMetres(0.0, 10.0))
        assertNull(AgricultureCalculations.irrigationVolumeCubicMetres(-1.0, 10.0))
        assertNull(AgricultureCalculations.irrigationVolumeCubicMetres(Double.NaN, 10.0))
        assertNull(AgricultureCalculations.irrigationVolumeCubicMetres(1.0, Double.POSITIVE_INFINITY))
    }

    @Test fun fertilizerQuantityAndWholeBagsAreCalculated() {
        assertEquals(120.0, AgricultureCalculations.fertilizerProductKilograms(2.0, 60.0)!!, 0.0001)
        assertEquals(3, AgricultureCalculations.fertilizerBagsRequired(120.0, 50.0))
        assertEquals(1, AgricultureCalculations.fertilizerBagsRequired(0.1, 50.0))
    }

    @Test fun fertilizerCalculationsRejectInvalidValues() {
        assertNull(AgricultureCalculations.fertilizerProductKilograms(0.0, 60.0))
        assertNull(AgricultureCalculations.fertilizerProductKilograms(1.0, Double.NaN))
        assertNull(AgricultureCalculations.fertilizerBagsRequired(-1.0))
        assertNull(AgricultureCalculations.fertilizerBagsRequired(10.0, 0.0))
    }
}
