import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx. Includes. jfxKeyEvent2sfx

object MainApp extends JFXApp3{

  override def start(): Unit = {
    //Primary Stage
    stage = new JFXApp3.PrimaryStage{
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

        //Scene content
        content = List (ball)

        //Handle Keyboard input for ball movement
        onKeyPressed = keyEvent => {
          keyEvent.code match {
            case KeyCode.Up    => ball.centerY.value -= 10 // Move up
            case KeyCode.Down  => ball.centerY.value += 10 // Move down
            case KeyCode.Left  => ball.centerX.value -= 10 // Move left
            case KeyCode.Right => ball.centerX.value += 10 // Move right
            case _ => // Do nothing for other keys
          }
        }
      }
    }
  }
}
