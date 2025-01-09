import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.scene.text.{Font, Text}
import scala.collection.mutable.ListBuffer

object MainApp extends JFXApp3{

  override def start(): Unit = {
    //Primary Stage
    stage = new JFXApp3.PrimaryStage {
      title = "BallQuest"
      width = 800
      height = 600
      scene = new Scene {
        fill = Color.LightGrey

        //Ball
        val ball = new Circle {
          centerX = 100
          centerY = 100
          radius = 20
          fill = Color.Red
        }

        //Platforms
        val platforms = ListBuffer[Rectangle]()
        platforms += new Rectangle {
          x = 0
          y = 300
          width = 200
          height = 40
          fill = Color.Brown
        }
        platforms += new Rectangle {
          x = 0
          y = 400
          width = 800
          height = 30
          fill = Color.Brown
        }
        platforms += new Rectangle {
          x = 200
          y = 220
          width = 200
          height = 40
          fill = Color.Brown
        }

        //Timer variables
        var startTime = System.nanoTime()
        val timerText = new Text {
          text = "Time: 0"
          font = Font(20)
          fill = Color.Black
          x = 700
          y = 20
        }

        //Scene content
        content = ball +: timerText +: platforms

        //Gravity and Physics Variables
        var velocityY = 0.0
        val gravity = 0.5
        var onPlatform = false

        // Handle Keyboard input for ball movement
        onKeyPressed = keyEvent => {
          keyEvent.code match {
            case KeyCode.Left => ball.centerX.value -= 20 // Move left
            case KeyCode.Right => ball.centerX.value += 20 // Move right
            case KeyCode.Up =>
              if (onPlatform) {
                velocityY = -10
                onPlatform = false
              }
            case _ => // Do nothing for other keys
          }
        }

        //Game Physics and Collisions detected
        val gameLoop = AnimationTimer { _ =>
          //Applying Gravity
          velocityY += gravity
          ball.centerY.value += velocityY

          //Collision detection with platforms
          onPlatform = false
          platforms.foreach { platform =>
            //Top collision
            if (ball.centerY.value + ball.radius.value >= platform.y.value &&
                ball.centerY.value <= platform.y.value + platform.height.value &&
                ball.centerX.value >= platform.x.value &&
                ball.centerX.value <= platform.x.value + platform.width.value) {
              ball.centerY.value = platform.y.value - ball.radius.value //Places the ball on top of the platform
              velocityY = 0 //Stops the downward motion
              onPlatform = true
            }

            //Side Collisions
            if (ball.centerX.value + ball.radius.value >= platform.x.value &&
                ball.centerX.value - ball.radius.value <= platform.x.value + platform.width.value &&
                ball.centerY.value >= platform.y.value &&
                ball.centerY.value <= platform.y.value + platform.height.value) {
              if (ball.centerX.value < platform.x.value) { //Left side
                ball.centerX.value = platform.x.value - ball.radius.value
              } else if (ball.centerX.value > platform.x.value + platform.width.value) {
                ball.centerX.value = platform.x.value + platform.width.value + ball.radius.value
              }
            }
          }

          // Prevent ball from falling below the ground/platform
          if (ball.centerY.value + ball.radius.value > 600) {
            ball.centerY.value = 600 - ball.radius.value
            velocityY = 0
            onPlatform = true
          }


          // Timer Update
          val elapsedTime = (System.nanoTime() - startTime) / 1e9
          timerText.text = f"Time: $elapsedTime%.1f"
        }

        // Start the AnimationTimer
        gameLoop.start()
      }
    }
  }
}
