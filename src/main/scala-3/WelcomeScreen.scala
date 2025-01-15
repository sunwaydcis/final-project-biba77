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

      val layout = new VBox(30, titleText, startButton, instructionsButton) {
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
      scene = new Scene(600, 600) { // Increased height for additional content
        fill = Color.LightYellow

        // Title
        val instructionsTitle = new Text("Instructions & Controls") {
          font = Font(28)
          fill = Color.DarkBlue
        }

        // Movement Controls Section
        val controlsText = new Text("Use the following keys to navigate:\n" +
          "← Left Arrow: Move Left\n" +
          "→ Right Arrow: Move Right\n" +
          "↑ Up Arrow: Jump") {
          font = Font(16)
          fill = Color.Black
        }

        // Spikes Icon and Description
        val spikesIcon = new ImageView(new Image("spike.png")) {
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

        // Coins Icon and Description
        val coinsIcon = new ImageView(new Image("coin.png")) {
          fitWidth = 40
          fitHeight = 40
        }
        val coinsDescription = new Text("Coins can be collected to increase your score!") {
          font = Font(16)
        }

        // Rings Icon and Description
        val ringsIcon = new ImageView(new Image("ring.png")) {
          fitWidth = 40
          fitHeight = 40
        }
        val ringsDescription = new Text("Passing through rings will change the ball's color!") {
          font = Font(16)
        }

        // Game Purpose
        val gamePurpose = new Text("Your goal is to survive till the end and collect as many coins as possible!") {
          font = Font(18)
          fill = Color.DarkGreen
        }

        // Back Button
        val backButton = new Button("Back to Welcome Screen") {
          font = Font(16)
          onAction = _ => instructionsStage.close() // Close the instructions window
        }

        // Layout for individual sections
        val spikesBox = new HBox(10, spikesIcon, spikesDescription) {
          alignment = Pos.CenterLeft
        }
        val plantsBox = new HBox(10, plantsIcon, plantsDescription) {
          alignment = Pos.CenterLeft
        }
        val heartsBox = new HBox(10, heartsIcon, heartsDescription) {
          alignment = Pos.CenterLeft
        }
        val coinsBox = new HBox(10, coinsIcon, coinsDescription) {
          alignment = Pos.CenterLeft
        }
        val ringsBox = new HBox(10, ringsIcon, ringsDescription) {
          alignment = Pos.CenterLeft
        }

        // Combine all instructions into a vertical layout
        content = new VBox(20, instructionsTitle, controlsText, spikesBox, plantsBox, heartsBox, coinsBox, ringsBox, gamePurpose, backButton) {
          alignment = Pos.Center
          prefWidth = 600
          prefHeight = 600
        }
      }
    }
    instructionsStage.showAndWait()
  }
}