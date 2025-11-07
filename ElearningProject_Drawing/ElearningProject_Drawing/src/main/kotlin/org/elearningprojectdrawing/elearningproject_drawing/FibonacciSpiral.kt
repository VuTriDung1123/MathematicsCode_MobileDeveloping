import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlin.math.atan

// --- Biến Toàn Cục/Hằng Số (Có thể giữ lại) ---
// Giá trị mặc định cho N
var N: Int = 9
val fibs = mutableListOf<Int>()

// canvas-related parameters
val canvasW = 1000.0
val canvasH = 750.0

// Scaling parameters: adjust as needed.
val xOffset = 150
val yOffset = 50
val amplify = 25.0

// --- Lớp Chính Kế Thừa Application ---
class FibonacciApp : Application() {

    private lateinit var canvas: Canvas
    private lateinit var gc: GraphicsContext

    // --- Hàm Helper: Tạo dãy Fibonacci ---
    fun generateFibonacciNumbers() {
        // Đảm bảo N không nhỏ hơn 2 (để có ít nhất 0, 1)
        if (N < 2) N = 2
        fibs.clear()
        fibs.add(0)
        fibs.add(1)
        for (i in 2 until N) {
            // Kiểm tra tràn số nếu cần, nhưng với Int mặc định không cần thiết cho N nhỏ
            fibs.add(fibs[i-1] + fibs[i-2])
        }
    }

    // --- Hàm Helper: Vẽ Text annotation trong hình vuông ---
    fun drawText(i: Int, gc: GraphicsContext, side: Double) {
        // Chỉ vẽ cho các số lớn hơn 0
        if (fibs[i] == 0) return

        gc.fill = Color.BLACK
        with(gc) {
            // Điều chỉnh kích thước font dựa trên kích thước hình vuông
            val fontSize = when {
                side < 10 -> 8.0
                side < 25 -> 12.0
                side < 50 -> 18.0
                else -> 24.0
            }
            font = Font.font(fontSize)
            // Căn giữa văn bản trong hình vuông
            val text = fibs[i].toString()
            val textWidth = text.length * fontSize * 0.6 // Ước lượng độ rộng
            val textHeight = fontSize
            fillText(text, (side - textWidth) / 2, (side + textHeight) / 2 - 5)
        }
    }

    // --- Hàm Helper: Vẽ cung tròn xoắn ốc (Không đổi) ---
    fun drawArc(gc: GraphicsContext, side: Double) {
        val x = 0.0
        val y = -side
        with(gc) {
            lineWidth = 3.0
            strokeArc(x, y, 2*side, 2*side, -90.0, -90.0, ArcType.OPEN)
        }
    }

    // --- Hàm Chính: Vẽ toàn bộ xoắn ốc ---
    fun drawFibonacciSpiral(gc: GraphicsContext) {
        // Xóa canvas cũ
        gc.clearRect(0.0, 0.0, canvasW, canvasH)

        // Thiết lập lại vị trí ban đầu
        gc.save()
        gc.translate(canvas.width / 2 + xOffset, canvas.height / 2 + yOffset)

        for (i in 1 until fibs.size) { // Lặp qua fibs.size thay vì N
            val side = fibs[i] * amplify

            // Bỏ qua trường hợp side = 0 (cho F(0))
            if (side > 0) {
                with (gc) {
                    stroke = Color.BLUE
                    lineWidth = 1.0
                    strokeRect(0.0, 0.0, side, side)
                    drawText(i, gc, side)

                    stroke = Color.RED
                    drawArc(gc, side)
                }
            }

            // Dịch chuyển và xoay cho hình vuông tiếp theo
            gc.translate(side, side)
            gc.rotate(-90.0)
        }

        // Khôi phục trạng thái ban đầu
        gc.restore()
    }

    // --- Hàm Helper: In dãy số và tỉ lệ ra Console (Không đổi) ---
    private fun printFibonacciSequenceAndRatios() {
        println("\n*** Fibonacci sequence and ratios (N=$N) ***\n")
        println("Length of Fibonacci sequence=${fibs.size}")
        println("Generated sequence:")
        println(fibs)
        println("\nRatio F(n+1)/F(n) (starting from (1,1) pair]:")
        for (i in 2 until fibs.size) {
            println("%5d".format(fibs[i-1]) +
                    "%5d".format(fibs[i]) +
                    "%12.6f".format(fibs[i].toDouble()/fibs[i-1])
            )
        }
    }

    // --- Hàm Cập nhật chính ---
    private fun updateSpiral() {
        generateFibonacciNumbers()
        drawFibonacciSpiral(gc)
        printFibonacciSequenceAndRatios()
    }

    // --- Hàm Start (Override của Application) ---
    override fun start(stage: Stage) {
        // 1. Khởi tạo Canvas và GraphicsContext
        canvas = Canvas(canvasW, canvasH)
        gc = canvas.graphicsContext2D

        // 2. Tạo phần UI nhập liệu
        val inputField = TextField(N.toString()).apply {
            // Chỉ cho phép nhập số
            textFormatter = javafx.scene.control.TextFormatter<String> { change ->
                if (change.controlNewText.matches(Regex("\\d*")) && change.controlNewText.length <= 2) {
                    change
                } else {
                    null
                }
            }
            prefColumnCount = 3
        }

        val updateButton = Button("Vẽ Xoắn ốc").apply {
            setOnAction {
                val input = inputField.text.toIntOrNull()
                if (input != null && input > 1) { // N phải lớn hơn 1
                    N = input
                    updateSpiral()
                } else {
                    // Xử lý lỗi nhập liệu nếu cần (ví dụ: hiển thị cảnh báo)
                    println("Lỗi: Vui lòng nhập số nguyên N > 1.")
                }
            }
        }

        // 3. Xây dựng Layout
        val controlPanel = HBox(10.0, Label("Nhập N:"), inputField, updateButton).apply {
            alignment = Pos.CENTER
            padding = Insets(10.0)
            style = "-fx-background-color: #f0f0f0;"
        }

        val root = BorderPane().apply {
            center = canvas
            bottom = controlPanel
        }

        // 4. Thiết lập Scene và Stage
        val scene = Scene(root, canvasW, canvasH + 50) // Tăng chiều cao để chứa control panel
        scene.fill = Color.WHITE

        // 5. Logic Khởi tạo lần đầu
        updateSpiral()

        with (stage) {
            title = "Fibonacci Spiral - N=${N}"
            this.scene = scene
            show()
        }
    }
}

// --- Hàm Main để chạy ứng dụng JavaFX ---
fun runFibonacci() {
    // Tạo Stage mới và khởi tạo ứng dụng con trên Stage này
    val app = FibonacciApp()
    val newStage = Stage()
    app.start(newStage)
}