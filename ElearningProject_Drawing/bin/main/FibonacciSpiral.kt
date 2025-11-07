import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.text.Font
import javafx.stage.Stage

// number of Fibonacci numbers in the list
val N = 9
val fibs = mutableListOf<Int>() // Đã thêm kiểu dữ liệu Int
// canvas-related parameters
val canvasW = 1000.0
val canvasH = 750.0
// Scaling parameters: adjust as needed.
val xOffset = 150.0 // Thay đổi thành Double
val yOffset = 50.0 // Thay đổi thành Double
val amplify = 25.0

class FibonacciSpiral : Application() {
    override fun start(stage: Stage) {
        // Khởi tạo và thiết lập Canvas
        val root = Pane()
        val canvas = Canvas(canvasW, canvasH)
        val gc = canvas.graphicsContext2D

        // Di chuyển hệ tọa độ về trung tâm và áp dụng offset
        gc.translate(canvas.width / 2 + xOffset,
            canvas.height / 2 + yOffset)
        root.children.add(canvas)

        // Thiết lập Scene và Stage
        val scene1 = Scene(root, canvasW, canvasH)
        scene1.fill = Color.WHITE
        with(stage) {
            title = "Fibonacci Spiral"
            scene = scene1
            show()
        }

        // code for Fibonacci sequence and spiral
        generateFibonacciNumbers()
        drawFibonacciSpiral(gc)
        printFibonacciSequenceAndRatios()
    }
}

fun generateFibonacciNumbers() {
    fibs.clear()
    // Khởi tạo chuỗi Fibonacci: 0, 1, 1, 2, 3, 5, 8, ...
    if (N > 0) fibs.add(0)
    if (N > 1) fibs.add(1)
    for (i in 2 until N) {
        fibs.add(fibs[i - 1] + fibs[i - 2])
    }
    // Bỏ số 0 đầu tiên để phù hợp với việc vẽ hình vuông có kích thước > 0
    if (fibs.size > 0 && fibs[0] == 0) {
        fibs.removeAt(0)
    }
}

fun drawFibonacciSpiral(gc: GraphicsContext) {
    // Đặt nét vẽ cho hình vuông
    gc.stroke = Color.BLUE
    gc.lineWidth = 1.0

    // Vòng lặp bắt đầu từ chỉ mục 0, tương ứng với số Fibonacci đầu tiên (1)
    for (i in 0 until fibs.size) {
      // Lấy độ dài cạnh và phóng đại
      val side = fibs[i].toDouble() * amplify
      
      with(gc) {
          // Vẽ hình vuông
          strokeRect(0.0, 0.0, side, side)
          
          // Vẽ số Fibonacci vào trung tâm hình vuông
          drawText(i, gc, side)
          
          // Vẽ cung tròn
          drawArc(gc, side)
          
          // --- Chuẩn bị cho lần lặp tiếp theo ---
          // Di chuyển hệ tọa độ đến góc tiếp theo của hình vuông.
          
          when (i % 4) {
              // Hướng vẽ: Xuống -> Trái -> Lên -> Phải
              0 -> translate(side, 0.0) 
              1 -> translate(0.0, side) 
              2 -> translate(-side, 0.0) 
              3 -> translate(0.0, -side) 
          }
          
          // Xoay hệ tọa độ ngược chiều kim đồng hồ
          rotate(-90.0)
      }
    }
}

fun drawText(i: Int, gc: GraphicsContext, side: Double) {
    gc.fill = Color.BLACK
    with(gc) {
      // Đặt font dựa trên kích thước hình vuông
      font = when {
            side <= 25 -> Font.font(10.0)
            side <= 50 -> Font.font(14.0)
            else -> Font.font(24.0)
        }
        // Đặt văn bản vào giữa hình vuông
        fillText(fibs[i].toString(), side/2 - font.size/2, side/2 + font.size/4)
    }
}

fun drawArc(gc: GraphicsContext, side: Double) {
    gc.stroke = Color.RED
    gc.lineWidth = 3.0
    
    val startAngle = -90.0
    val length = -90.0 
    
    var x = 0.0
    var y = 0.0

    // Cung tròn phải nằm bên trong hình vuông được vẽ
    when (fibs.size - (N-fibs.size) + i % 4) {
        0 -> { // Arc nằm ở góc dưới bên phải
            x = -side
            y = 0.0
        }
        1 -> { // Arc nằm ở góc trên bên phải
            x = -side
            y = -side
        }
        2 -> { // Arc nằm ở góc trên bên trái
            x = 0.0
            y = -side
        }
        3 -> { // Arc nằm ở góc dưới bên trái
            x = 0.0
            y = 0.0
        }
    }

    // Vẽ cung tròn
    gc.strokeArc(x, y, side * 2, side * 2,
        startAngle, length, ArcType.OPEN)
}


private fun printFibonacciSequenceAndRatios() {
    println("\n*** Fibonacci sequence and ratios ***\n")
    println("Length of Fibonacci sequence=${fibs.size}")
    println("Generated sequence:")
    println(fibs)
    println("\nRatio F(n+1)/F(n) [starting from F(1)=1]:")
    
    // In tiêu đề
    println("%5s%5s%12s".format("F(n)", "F(n+1)", "Ratio"))
    println("--------------------------")
    
    // Bắt đầu từ chỉ mục 1 (số Fibonacci thứ 2 trong chuỗi đã lọc)
    for (i in 1 until fibs.size) {
        val f_n = fibs[i-1]
        val f_n_plus_1 = fibs[i]
        
        println("%5d%5d%12.6f".format(
            f_n,
            f_n_plus_1,
            f_n_plus_1.toDouble() / f_n
        ))
    }
}

fun main() {
    // Khởi chạy ứng dụng JavaFX
    Application.launch(FibonacciSpiral::class.java)
}
