package com.nouradine.agriculture

/**
 * Pure agricultural calculations kept separate from UI so they can be tested.
 */
object AgricultureCalculations {
    /**
     * Returns theoretical irrigation volume in cubic metres:
     * hectares × irrigation depth in millimetres × 10.
     * Returns null for non-finite or non-positive inputs.
     */
    fun irrigationVolumeCubicMetres(areaHectares: Double, depthMillimetres: Double): Double? {
        if (!areaHectares.isFinite() || !depthMillimetres.isFinite()) return null
        if (areaHectares <= 0.0 || depthMillimetres <= 0.0) return null
        return areaHectares * depthMillimetres * 10.0
    }
}
