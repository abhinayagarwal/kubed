package kubed.demo

import javafx.animation.Timeline
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
import kubed.color.Hsl
import kubed.interpolate.color.interpolateHcl
import kubed.scale.LinearScale
import kubed.scale.PointScale
import kubed.scale.scaleLinear
import kubed.scale.scalePoint
import kubed.selection.selectAll
import kubed.shape.circle
import kubed.transition.transition
import kotlin.math.abs


class CyclingTransitionDemo : Application() {
    override fun start(primaryStage: Stage?) {
        val margin = 40.0

        val root = Group()
        root.translateX = margin
        root.translateY = margin

        val width = 800.0
        val height = 400.0

        val y = scalePoint<Double> {
            domain((0..50).map(Int::toDouble))
            range(listOf(0.0, height))
        }

        val z = scaleLinear(::interpolateHcl) {
            domain(listOf(10.0, 0.0))
            range(listOf(Hsl(62.0, 1.0, 0.9).toColor(), Hsl(228.0, 0.3, 0.2).toColor()))
        }

        val c = circle<Double> {
            radius(25.0)
            stroke(Color.BLACK)

            translateY { d, _ -> y(d) }
            fill { d, _ -> z(abs(d % 20 - 10)) }
        }

        root.selectAll<Double>("Circle")
             .data(y.domain)
             .enter().append { d, _, _ -> c(d) }
             .transition()
             .delay { d, _, _ -> Duration.millis(d * 40) }
             .duration(Duration.millis(2500.0))
             .cycleCount(Timeline.INDEFINITE)
             .autoReverse(true)
             .translateX(width)

        val scene = Scene(root)
        primaryStage?.width = width + margin * 2
        primaryStage?.height = height + margin * 2

        primaryStage?.scene = scene
        primaryStage?.show()
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(CyclingTransitionDemo::class.java, *args)
        }
    }
}

