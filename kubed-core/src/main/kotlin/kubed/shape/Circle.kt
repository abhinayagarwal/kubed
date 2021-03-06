package kubed.shape

class Circle<T> : Shape<Circle<T>, T>() {
    var radius: (T, Int) -> Double = { _, _ -> throw IllegalStateException("radius must be specified") }
    var centerX: (T, Int) -> Double = { _, _ -> 0.0 }
    var centerY: (T, Int) -> Double = { _, _ -> 0.0 }

    fun radius(r: Double) = radius { _, _ -> r }
    fun radius(r: (T, Int) -> Double): Circle<T> {
        radius = r
        return this
    }

    fun centerX(r: Double) = radius { _, _ -> r }
    fun centerX(r: (T, Int) -> Double): Circle<T> {
        centerX = r
        return this
    }

    fun centerY(r: Double) = radius { _, _ -> r }
    fun centerY(r: (T, Int) -> Double): Circle<T> {
        centerY = r
        return this
    }

    override operator fun invoke(d: T, i: Int): javafx.scene.shape.Shape {
        val circle = javafx.scene.shape.Circle()
        circle.radius = radius(d, i)
        circle.centerX = centerX(d, i)
        circle.centerY = centerY(d, i)
        apply(d, i, circle)
        return circle
    }
}