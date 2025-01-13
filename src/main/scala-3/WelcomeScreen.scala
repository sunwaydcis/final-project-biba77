import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Stage

object WelcomeScreen {

  def show(stage: Stage, startGame: () => Unit): Unit = {
    val welcomeScene = new Scene(800, 600) {
      fill = Color.LightGrey

      val titleText = new Text("Welcome to BallQuest!") {
        font = Font(36)
        fill = Color.Black
      }

      val startButton = new Button("Start Game") {
        font = Font(18)
        onAction = _ => {
          startGame() // Calls the function passed from MainApp
        }
      }

      val layout = new VBox(20, titleText, startButton) {
        alignment = Pos.Center
        prefWidth = 800
        prefHeight = 600
      }
      content = layout
    }

    stage.scene = welcomeScene
  }
}
