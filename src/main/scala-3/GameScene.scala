import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Stage

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

  // Add a method to set the ball's color
  def setColor(color: Color): Unit = {
    circle.fill = color
  }

  override def draw(): scalafx.scene.Node = circle
}

//Platform class
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
    image = new Image ("plant.png")
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
    image = new Image("spike.png")
    this.x = Spike.this.x
    this.y = Spike.this.y
    this.fitWidth = Spike.this.width
    this.fitHeight = Spike.this.height
  }

  override def draw(): scalafx.scene.Node = imageView
}

//Heart class
class Heart(x: Double, y: Double, width: Double = 30, height: Double = 30) extends GameObject(x, y, width, height) {
  private val imageView = new ImageView {
    image = new Image("heart .png")
    this.x = Heart.this.x
    this.y = Heart.this.y
    this.fitWidth = Heart.this.width
    this.fitHeight = Heart.this.height
  }

  override def draw(): scalafx.scene.Node = imageView
}

//Ring class
class Ring(x: Double, y: Double, width: Double = 60, height: Double = 70) extends GameObject(x, y, width, height) {
  private val imageView = new ImageView {
    image = new Image("ring.png")
    this.x = Ring.this.x
    this.y = Ring.this.y
    this.fitWidth = Ring.this.width
    this.fitHeight = Ring.this.height
  }

  override def draw(): scalafx.scene.Node = imageView
}

//Coin class
class Coin(x: Double, y: Double, width: Double = 80, height: Double = 80) extends GameObject(x, y, width, height) {
  private val imageView = new ImageView {
    image = new Image("coin.png")
    this.x = Coin.this.x
    this.y = Coin.this.y
    this.fitWidth = Coin.this.width
    this.fitHeight = Coin.this.height
  }

  override def draw(): scalafx.scene.Node = imageView
}


class GameScene(stage: Stage) {

  //Ball
  val ball = new Ball(100, 100, 20)

  //Platforms
  val platforms = Seq(
    new Platform(0, 300, 200, 40),
    new Platform(0, 400, 800, 30),
    new Platform(200, 220, 200, 40),
    new Platform(370, 120, 200, 40)
  )

  //Plants
  val plants = List(
    new Plant(50, 360),
    new Plant(550, 360)
  )

  //Spikes
  val spikes = List(
    new Spike(300, 350),
    new Spike(600, 360)
  )

  //Lives
  var lives = 2
  val hearts = List(
    new Heart(0, 10),
    new Heart(25, 10)
  )

  // Rings
  val rings = List(
    new Ring(367, 160),
    new Ring(535, 55)
  )

  // Coins
  val coins = List(
    new Coin(250, 150),
    new Coin(400, 55),
    new Coin(700, 300)
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

  //Gravity and Physics Variables
  var velocityY = 0.0
  val gravity = 0.5
  var onPlatform = false

  // Define a cooldown variable
  var collisionCooldown: Long = 0 // Tracks the time of the last collision

  // Define gameLoop at the class level
  var gameLoop: AnimationTimer = _

  val scene: Scene = new Scene(800, 600) {
    fill = Color.LightGrey

    content = Seq(ball.draw(), timerText) ++ platforms.map(_.draw()) ++ plants.map(_.draw())
    ++ spikes.map(_.draw()) ++ hearts.take(lives).map(_.draw()) ++ rings.map(_.draw()) ++ coins.map(_.draw())

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
    gameLoop = AnimationTimer { _ =>
      //Applying Gravity
      velocityY += gravity
      ball.centerY += velocityY

      //Collision detection with platforms
      onPlatform = false
      platforms.foreach { platform =>
        //Top collision
        if (ball.collidesWith(platform) && velocityY >= 0) {
          ball.centerY = platform.y - ball.radius //Places the ball on top of the platform
          velocityY = 0 //Stops the downward motion
          onPlatform = true
        }

        //Bottom Collisions
        if (velocityY < 0 &&
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
        ball.centerY = 600 - ball.radius
        velocityY = 0
        onPlatform = true
      }

      //Plants collision detection
      plants.foreach { plant =>
        if (ball.collidesWith(plant)) {
          plant.applyEffect(ball)
        }
      }

      // Spike collision detection
      spikes.foreach { spike =>
        if (ball.collidesWith(spike) && System.nanoTime() - collisionCooldown > 1e9) {
          // Register collision only if 1 second has passed since the last collision
          collisionCooldown = System.nanoTime() // Updates the cooldown

          if (lives == 2) {
            // First collision: decrease lives to 1
            lives -= 1
            content = Seq(ball.draw(), timerText) ++ platforms.map(_.draw()) ++ plants.map(_.draw())
            ++ spikes.map(_.draw()) ++ hearts.take(lives).map(_.draw()) ++ rings.map(_.draw()) ++ coins.map(_.draw())
          } else if (lives == 1) {
            // Second collision: decrease lives to 0 and end the game
            lives -= 1
            content = Seq(ball.draw(), timerText) ++ platforms.map(_.draw()) ++ plants.map(_.draw())
            ++ spikes.map(_.draw()) ++ hearts.take(lives).map(_.draw()) ++ rings.map(_.draw()) ++ coins.map(_.draw())

            // Triggers game over
            gameLoop.stop()
            // Inside your game-over logic
            val restartButton = new Button("Restart") {
              layoutX = 300
              layoutY = 400
              onAction = _ => restartGame() // Call a method to restart the game
            }

            val homeButton = new Button("Home") {
              layoutX = 400
              layoutY = 400
              onAction = _ => goToHome(stage) // Call a method to navigate to the home screen
            }
            val gameOverText = new Text {
              text = "Game Over!"
              font = Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 50)
              font = Font(50)
              fill = Color.Red
              x = 250
              y = 300
            }
            content = Seq(gameOverText, restartButton, homeButton) //"Game Over" message
          }

          //the restartGame and goToHome
          def restartGame(): Unit = {
            // Logic to reset game variables and restart the scene
            lives = 2
            velocityY = 0
            ball.centerX = 100
            ball.centerY = 100
            ball.setColor(Color.Red)
            startTime = System.nanoTime() // Reset the timer
            gameLoop.start() // Restart the game loop
            content = Seq(ball.draw(), timerText) ++ platforms.map(_.draw()) ++ plants.map(_.draw())
            ++ spikes.map(_.draw()) ++ hearts.take(lives).map(_.draw()) ++ rings.map(_.draw()) ++ coins.map(_.draw())
          }

          def goToHome(stage: Stage): Unit = {
            WelcomeScreen.show(stage, startGame = () => {
              val newGameScene = new GameScene(stage)
              stage.scene = newGameScene.scene // Sets the new game scene
            })
          }
        }
      }
      var lastRingChangeTime: Long = 0 // Time of the last color change

      // Rings collision detection
      rings.foreach { ring =>
        if (
          ball.centerX >= ring.x && ball.centerX <= ring.x + ring.width &&
          ball.centerY >= ring.y && ball.centerY <= ring.y + ring.height &&
          System.nanoTime() - lastRingChangeTime > 1e9 // Cooldown of 1 second
        ){
          // Update the last color change time
          lastRingChangeTime = System.nanoTime()
          // Change the ball color to a random color
          val predefinedColors = Seq(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Purple,
          Color.Orange)
          val randomColor = predefinedColors(scala.util.Random.nextInt(predefinedColors.length))
          ball.setColor(randomColor)
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




