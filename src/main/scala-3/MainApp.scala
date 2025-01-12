import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.text.{Font, Text}
import scala.collection.mutable.ListBuffer


//Base class for GameObject
abstract class GameObject (
                          var x: Double,
                          var y: Double,
                          val width: Double,
                          val height: Double
                          ) {
  def draw (): Unit //to be implemented by subclasses

  //Check if this objects collides with another
  def collidesWith(other: GameObject): Boolean = {
    x < other.x + other.width &&
      x + width > other.x &&
      y < other.y + other.height &&
      y + height > other.y
  }
}

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
        val defaultRadius = 20
        val grownRadius = 30
        var isGrown = false //states to track the ball size

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
        platforms += new Rectangle {
          x = 370
          y = 120
          width = 200
          height = 40
          fill = Color.Brown
        }

        //Plants
        val plant1 = new ImageView {
          image = new Image ("file:C:\\Users\\User\\Downloads\\plant.png")
              x = 50
              y = 360
              fitWidth = 40
              fitHeight = 40
        }
        val plant2 = new ImageView {
          image = new Image ("file:C:\\Users\\User\\Downloads\\plant.png")
              x = 550
              y = 360
              fitWidth =40
              fitHeight = 40
        }

        //Spikes
        val spike1 = new ImageView {
          image = new Image("file:C:\\Users\\User\\Downloads\\spike.png")
          x = 300
          y = 350
          fitWidth = 40
          fitHeight = 40
        }
        val spike2 = new ImageView {
          image = new Image("file:C:\\Users\\User\\Downloads\\spike.png")
          x = 600
          y = 360
          fitWidth = 40
          fitHeight = 40
        }

        val spikes = List (spike1, spike2)
        val plants = List (plant1, plant2)
        
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
        content = Seq(ball, timerText) ++ platforms ++ plants ++ spikes

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
            if (velocityY >= 0 &&
                ball.centerY.value + ball.radius.value >= platform.y.value &&
                ball.centerY.value <= platform.y.value + platform.height.value &&
                ball.centerX.value >= platform.x.value &&
                ball.centerX.value <= platform.x.value + platform.width.value) {
              ball.centerY.value = platform.y.value - ball.radius.value //Places the ball on top of the platform
              velocityY = 0 //Stops the downward motion
              onPlatform = true
            }

            //Bottom Collisions
            if (velocityY <0 &&
                ball.centerY.value - ball.radius.value <= platform.y.value + platform.height.value &&
                ball.centerY.value > platform.y.value &&
                ball.centerX.value >= platform.x.value &&
                ball.centerX.value <= platform.x.value + platform.width.value) {
              velocityY = 0 //Stops downward motion
              ball.centerY.value = platform.y.value - ball.radius.value
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

          //Plants collision detection
          plants.foreach { plant =>
            //Get precise dimensions of the plant
            val plantX = plant.x.value
            val plantY = plant.y.value
            val plantWidth = plant.fitWidth.value
            val plantHeight = plant.fitHeight.value

            //Get plants bounding box
            val plantBounds = plant.getBoundsInParent
            if (ball.centerX.value + ball.radius.value >= plantX &&
                ball.centerX.value - ball.radius.value <= plantX + plantWidth &&
                ball.centerY.value + ball.radius.value >= plantY &&
                ball.centerY.value - ball.radius.value <= plantY + plantHeight) {
              if (!isGrown) {
                ball.radius.value = grownRadius //Grow the ball
              } else {
                ball.radius.value = defaultRadius //Shrink the ball
                isGrown = false
              }
            }
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
