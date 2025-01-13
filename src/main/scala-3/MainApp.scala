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
                          var width: Double,
                          var height: Double
                          ) {
  def draw (): scalafx.scene.Node //to be implemented by subclasses

  //Check if this objects collides with another
  def collidesWith(other: GameObject): Boolean = {
    x < other.x + other.width &&
      x + width > other.x &&
      y < other.y + other.height &&
      y + height > other.y
  }
}

// Ball class
class Ball(startX: Double, startY: Double, initialRadius: Double, var isGrown: Boolean = false)
  extends GameObject(startX - initialRadius, startY - initialRadius, initialRadius * 2, initialRadius * 2) {

  private val circle = new Circle()
  circle.centerX() = startX
  circle.centerY() = startY
  circle.radius() = initialRadius
  circle.fill = Color.Red


  val defaultRadius: Double = 20
  val grownRadius: Double = 30

  def centerX: Double = circle.centerX.value

  def centerX_=(value: Double): Unit = {
    x = value - radius
    circle.centerX = value
  }

  def centerY: Double = circle.centerY.value

  def centerY_=(value: Double): Unit = {
    y = value - radius
    circle.centerY = value
  }

  def radius: Double = circle.radius.value

  def radius_=(value: Double): Unit = {
    circle.radius = value
    width = value * 2
    height = value * 2
  }

  def setRadius(newRadius: Double): Unit = {
    radius = newRadius
  }

  override def draw(): scalafx.scene.Node = circle
}



//Platforms
class Platform(x: Double, y: Double, width: Double, height: Double) extends GameObject(x, y, width, height) {
  private val rectangle = new Rectangle {
    this.x = Platform.this.x
    this.y = Platform.this.y
    this.width = Platform.this.width
    this.height = Platform.this.height
    fill = Color.Brown
  }

  //Draw the platform
  override def draw(): scalafx.scene.Node = rectangle
}

//Plants class
class Plant (x: Double, y: Double, width: Double = 40, height: Double = 40) extends GameObject(x, y, width, height) {
  private val imageView = new ImageView {
    image = new Image ("file:C:\\Users\\User\\Downloads\\plant.png")
    this.x = Plant.this.x
    this.y = Plant.this.y
    this.fitWidth = Plant.this.width
    this.fitHeight = Plant.this.height
  }

  def applyEffect(ball: Ball): Unit = {
    if (!ball.isGrown) {
      ball.setRadius(ball.grownRadius) // Grow the ball
      ball.isGrown = true
    } else {
      ball.setRadius(ball.defaultRadius) // Shrink the ball
      ball.isGrown = false
    }
  }

  override def draw(): scalafx.scene.Node = imageView
  }

// Spike class
class Spike(x: Double, y: Double, width: Double = 40, height: Double = 40) extends GameObject(x, y, width, height) {
  private val imageView = new ImageView {
    image = new Image("file:C:\\Users\\User\\Downloads\\spike.png")
    this.x = Spike.this.x
    this.y = Spike.this.y
    this.fitWidth = Spike.this.width
    this.fitHeight = Spike.this.height
  }

  override def draw(): scalafx.scene.Node = imageView
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
        val ball = new Ball (100, 100, 20)

        //Platforms
        val platforms = Seq(
          new Platform (0, 300, 200, 40),
          new Platform (0, 400, 800, 30),
          new Platform (200, 220, 200, 40),
          new Platform (370, 120, 200, 40)
        )

        //Plants
        val plants = List (
          new Plant (50, 360),
          new Plant (550, 360)
        )

        //Spikes
        val spikes = List (
          new Spike (300, 350),
          new Spike (600, 360)
        )

        
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
        content = Seq(ball.draw(), timerText) ++ platforms.map(_.draw()) ++ plants.map(_.draw()) ++ spikes.map(_.draw())

        //Gravity and Physics Variables
        var velocityY = 0.0
        val gravity = 0.5
        var onPlatform = false

        // Handle Keyboard input for ball movement
        onKeyPressed = keyEvent => {
          keyEvent.code match {
            case KeyCode.Left => ball.centerX -= 20 // Move left
            case KeyCode.Right => ball.centerX += 20 // Move right
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
          ball.centerY += velocityY

          //Collision detection with platforms
          onPlatform = false
          platforms.foreach { platform =>
            //Top collision
            if (ball.collidesWith(platform) && velocityY >=0) {
              ball.centerY = platform.y - ball.radius //Places the ball on top of the platform
              velocityY = 0 //Stops the downward motion
              onPlatform = true
            }

            //Bottom Collisions
            if (velocityY <0 &&
                ball.centerY - ball.radius <= platform.y + platform.height &&
                ball.centerY > platform.y &&
                ball.centerX >= platform.x &&
                ball.centerX <= platform.x + platform.width) {
              velocityY = 0 //Stops downward motion
              ball.centerY = platform.y - ball.radius
            }

            //Side Collisions
            if (ball.centerX + ball.radius >= platform.x &&
                ball.centerX - ball.radius <= platform.x + platform.width &&
                ball.centerY >= platform.y &&
                ball.centerY <= platform.y + platform.height) {
              if (ball.centerX < platform.x) { //Left side
                ball.centerX = platform.x - ball.radius
              } else if (ball.centerX > platform.x + platform.width) {
                ball.centerX = platform.x + platform.width + ball.radius
              }
            }
          }

          // Prevent ball from falling below the ground/platform
          if (ball.centerY + ball.radius > 600) {
            ball.centerY= 600 - ball.radius
            velocityY = 0
            onPlatform = true
          }

          //Plants collision detection
          plants.foreach { plant =>
            if (ball.collidesWith(plant)) {
              plant.applyEffect(ball)
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
