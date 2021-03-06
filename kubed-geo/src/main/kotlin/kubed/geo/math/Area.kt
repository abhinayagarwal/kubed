package kubed.geo.math

import kubed.geo.GeometryStream
import kubed.math.QUARTER_PI
import kubed.math.TAU
import kubed.math.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Area : GeometryStream {
    val area: Double
        get() = areaAccumulator.sum

    private val areaAccumulator = Accumulator()
    internal val areaRingAccumulator = Accumulator()
    private var lambda00 = Double.NaN
    private var phi00 = Double.NaN
    private var lambda0 = Double.NaN
    private var cosPhi0 = Double.NaN
    private var sinPhi0 = Double.NaN

    private var streamingPolygon = false
    private var firstPoint = false

    override fun polygonStart() {
        areaRingAccumulator.set(0.0)
        streamingPolygon = true
    }

    override fun polygonEnd() {
        val ars = areaRingAccumulator.sum
        areaAccumulator += if(ars < 0) (TAU + ars) else areaRingAccumulator.sum
        streamingPolygon = false
    }

    override fun lineStart() {
        if(streamingPolygon) firstPoint = true
    }

    override fun lineEnd() {
        if(streamingPolygon) {
            point(lambda00, phi00, 0.0)
        }
    }

    override fun point(x: Double, y: Double, z: Double) {
        val lambda = x.toRadians()
        val phi = y.toRadians() / 2 + QUARTER_PI // half the angular distance from south pole

        if(firstPoint) {
            lambda00 = x
            phi00 = y

            lambda0 = lambda
            cosPhi0 = cos(phi)
            sinPhi0 = sin(phi)

            firstPoint = false
        }
        else {
            // Spherical excess E for a spherical triangle with vertices: south pole,
            // previous point, current point.  Uses a formula derived from Cagnoli’s
            // theorem.  See Todhunter, Spherical Trig. (1871), Sec. 103, Eq. (2).
            val dLambda = lambda - lambda0
            val sdLambda = if (dLambda >= 0) 1 else -1
            val adLambda = sdLambda * dLambda
            val cosPhi = cos(phi)
            val sinPhi = sin(phi)
            val k = sinPhi0 * sinPhi
            val u = cosPhi0 * cosPhi + k * cos(adLambda)
            val v = k * sdLambda * sin(adLambda)
            areaRingAccumulator += atan2(v, u)

            // Advance the previous points.
            lambda0 = lambda
            cosPhi0 = cosPhi
            sinPhi0 = sinPhi
        }
    }

    override fun sphere() {
        areaAccumulator += TAU
    }
}