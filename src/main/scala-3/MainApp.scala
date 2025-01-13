import scalafx.application.JFXApp3

object MainApp extends JFXApp3 {

  override def start(): Unit = {
    // Initialize the primary stage
    stage = new JFXApp3.PrimaryStage {
      title = "BallQuest"
      width = 800
      height = 600
    }

    // Function to start the game
    def startGame(): Unit = {
      val gameScene = new GameScene(stage)
      stage.scene = gameScene.scene
    }

    // Show Welcome Screen
    WelcomeScreen.show(stage, startGame)
  }
}
