import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle

object MainApp extends JFXApp3{

  override def start(): Unit = {
    //Primary Stage
    stage = new JFXApp3.PrimaryStage{
      title = "BallQuest"
      width = 800
      height = 600
      scene = new Scene {
        fill = Color.LightGrey
      }
    }
  }
}
