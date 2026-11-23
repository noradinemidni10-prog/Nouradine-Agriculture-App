package com.nouradine.agriculture

/**
 * Pure agricultural calculations kept separate from UI so they can be tested.
 */
object AgricultureCalculations {
    /**
     * Theoretical irrigation volume in cubic metres:
     * hectares × irrigation depth in millimetres × 10.
     */
    fun irrigationVolumeCubicMetres(areaHectares: Double, depthMillimetres: Double): Double? {
        if (!areaHectares.isFinite() || !depthMillimetres.isFinite()) return null
        if (areaHectares <= 0.0 || depthMillimetres <= 0.0) return null
        return areaHectares * depthMillimetres * 10.0
    }

    /**
     * Product mass required from an already-established recommendation:
     * field area (ha) × product rate (kg/ha).
     * This calculates quantity only; it does not recommend a fertilizer dose.
     */
    fun fertilizerProductKilograms(areaHectares: Double, rateKilogramsPerHectare: Double): Double? {
        if (!areaHectares.isFinite() || !rateKilogramsPerHectare.isFinite()) return null
        if (areaHectares <= 0.0 || rateKilogramsPerHectare <= 0.0) return null
        val total = areaHectares * rateKilogramsPerHectare
        return total.takeIf { it.isFinite() && it > 0.0 }
    }

    /** Number of whole bags needed, rounded up to avoid a shortfall. */
    fun fertilizerBagsRequired(totalKilograms: Double, bagSizeKilograms: Double = 50.0): Int? {
        if (!totalKilograms.isFinite() || !bagSizeKilograms.isFinite()) return null
        if (totalKilograms <= 0.0 || bagSizeKilograms <= 0.0) return null
        val bags = kotlin.math.ceil(totalKilograms / bagSizeKilograms)
        return bags.takeIf { it <= Int.MAX_VALUE }?.toInt()
    }
}
