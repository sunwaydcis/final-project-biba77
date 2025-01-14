import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, VBox}
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

      // Instructions Button
      val instructionsButton = new Button("Instructions") {
        font = Font(18)
        onAction = _ => showInstructions()
      }

      val layout = new VBox(20, titleText, startButton, instructionsButton) {
        alignment = Pos.Center
        prefWidth = 800
        prefHeight = 600
      }
      content = layout
    }

    stage.scene = welcomeScene
  }

  def showInstructions(): Unit = {
    lazy val instructionsStage: Stage = new Stage {
      title = "Game Instructions"
      scene = new Scene(600, 400) {
        fill = Color.LightYellow

        // Title
        val instructionsTitle = new Text("Instructions") {
          font = Font(24)
          fill = Color.DarkBlue
        }

        // Spikes Icon and Description
        val spikesIcon = new ImageView(
          new Image("spike.png")) {
          fitWidth = 40
          fitHeight = 40
        }
        val spikesDescription = new Text("Spikes will reduce your lives. Avoid them!") {
          font = Font(16)
        }

        // Plants Icon and Description
        val plantsIcon = new ImageView(new Image("plant.png")) {
          fitWidth = 40
          fitHeight = 40
        }
        val plantsDescription = new Text("Plants will grow or shrink the ball!") {
          font = Font(16)
        }

        // Hearts Icon and Description
        val heartsIcon = new ImageView(new Image("heart .png")) {
          fitWidth = 40
          fitHeight = 40
        }
        val heartsDescription = new Text("Hearts represent your lives. You start with two!") {
          font = Font(16)
        }

        // Game Purpose
        val gamePurpose = new Text("Your goal is to survive till the end (and as fast as possible!)") {
          font = Font(18)
          fill = Color.DarkGreen
        }

        // Back Button
        val backButton = new Button("Back to Welcome Screen") {
          font = Font(16)
          onAction = _ => instructionsStage.close() // Close the instructions window
        }

        // Layout
        val spikesBox = new HBox(10, spikesIcon, spikesDescription) {
          alignment = Pos.CenterLeft
        }
        val plantsBox = new HBox(10, plantsIcon, plantsDescription) {
          alignment = Pos.CenterLeft
        }
        val heartsBox = new HBox(10, heartsIcon, heartsDescription) {
          alignment = Pos.CenterLeft
        }

        content = new VBox(20, instructionsTitle, spikesBox, plantsBox, heartsBox, gamePurpose, backButton) {
          alignment = Pos.Center
          prefWidth = 600
          prefHeight = 400
        }
      }
    }
    instructionsStage.showAndWait()
  }
}
