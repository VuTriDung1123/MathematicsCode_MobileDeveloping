import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.layout.HBox
import javafx.geometry.Pos
import javafx.geometry.Insets
import javafx.stage.Stage
import javafx.scene.paint.Color
import javafx.scene.text.Font
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.math.min
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

// --- LỚP DỮ LIỆU MỚI ---
data class RandomWalkState(
    val step: Int,
    val dist: Double
)

data class PathData(
    val simulationIndex: Int,
    val firstSteps: List<RandomWalkState>,
    val middleSteps: List<RandomWalkState>,
    val lastSteps: List<RandomWalkState>
)

data class RandomWalkSummary(
    val meanDistance: Double,
    val rmsDistance: Double,
    val theoreticalRMS: Double,
    val allPaths: List<List<RandomWalkState>>,
    val avgList: List<RandomWalkState>, // Thêm lại AvgList để build Statistics Table
    val rmsList: List<RandomWalkState>, // Thêm lại RmsList để build Statistics Table
    val expList: List<RandomWalkState>, // Thêm lại ExpList để build Statistics Table
    val avgFirstMean: List<RandomWalkState>,
    val avgFirstRMS: List<RandomWalkState>,
    val avgMiddleMean: List<RandomWalkState>,
    val avgMiddleRMS: List<RandomWalkState>,
    val avgLastMean: List<RandomWalkState>,
    val avgLastRMS: List<RandomWalkState>,
    val individualPathsData: List<PathData>
)

// --- LOGIC TOÁN HỌC ---

fun randomWalk1d(numStep: Int, numSim: Int): RandomWalkSummary {



    //Khởi tạo list
    val allPaths = mutableListOf<List<RandomWalkState>>()
    if (numStep <= 1 || numSim <= 0) {
        return RandomWalkSummary(0.0, 0.0, 0.0, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
    }
    val sumX = DoubleArray(numStep)
    val sumX2 = DoubleArray(numStep)
    val simulationArray = Array(numSim) { DoubleArray(numStep) }




    //Vòng lặp bên ngoài: Chạy qua từng lần mô phỏng
    for (i in 0 until numSim) {
        val currentPath = mutableListOf<RandomWalkState>()
        for (j in 1 until numStep) {
            val step = if (Random.nextBoolean()) 1 else -1
            simulationArray[i][j] = simulationArray[i][j - 1] + step
            sumX[j] += simulationArray[i][j]
            sumX2[j] += simulationArray[i][j].pow(2)
        }

        for (j in 0 until numStep) {
            currentPath.add(RandomWalkState(j, simulationArray[i][j]))
        }
        allPaths.add(currentPath)
    }



    //khởi tạo các list để chứa kết quả thống kê cuối cùng
    val avgList = mutableListOf<RandomWalkState>()
    val rmsList = mutableListOf<RandomWalkState>()
    val expList = mutableListOf<RandomWalkState>()

    val lastIndex = numStep - 1
    for (j in 0 until numStep) {
        val mean = sumX[j] / numSim
        avgList.add(RandomWalkState(j, mean))

        val rms = sqrt(sumX2[j] / numSim)
        rmsList.add(RandomWalkState(j, rms))

        val exp = sqrt(j.toDouble())
        expList.add(RandomWalkState(j, exp))
    }






    //Trích xuất (Slicing) Dữ liệu Thống kê
    val middleIndex = (numStep - 1) / 2
    val startMiddleIndex = (middleIndex - 2).coerceAtLeast(0)
    val endMiddleIndex = (startMiddleIndex + 4).coerceAtMost(lastIndex)

    val avgFirstMean = avgList.take(5)
    val avgFirstRMS = rmsList.take(5)
    val avgMiddleMean = avgList.subList(startMiddleIndex, endMiddleIndex + 1)
    val avgMiddleRMS = rmsList.subList(startMiddleIndex, endMiddleIndex + 1)
    val avgLastMean = avgList.takeLast(5)
    val avgLastRMS = rmsList.takeLast(5)


    //Trích xuất (Slicing) Dữ liệu Đường đi Mẫu
    val individualPaths = mutableListOf<PathData>()
    val numPathsToShow = min(numSim, 3)

    for (i in 0 until numPathsToShow) {
        val path = simulationArray[i]

        val createSteps = { start: Int, end: Int ->
            path.slice(start..end).mapIndexed { index, dist ->
                RandomWalkState(start + index, dist)
            }
        }

        val firstSteps = createSteps(0, min(4, lastIndex))
        val middleSteps = createSteps(startMiddleIndex, endMiddleIndex)
        val lastSteps = createSteps(numStep - 5, lastIndex)


        individualPaths.add(
            PathData(
                simulationIndex = i + 1,
                firstSteps = firstSteps,
                middleSteps = middleSteps,
                lastSteps = lastSteps
            )
        )
    }

    return RandomWalkSummary(
        meanDistance = avgList.getOrElse(lastIndex) { RandomWalkState(0, 0.0) }.dist,
        rmsDistance = rmsList.getOrElse(lastIndex) { RandomWalkState(0, 0.0) }.dist,
        theoreticalRMS = expList.getOrElse(lastIndex) { RandomWalkState(0, 0.0) }.dist,
        allPaths = allPaths,
        avgList = avgList,
        rmsList = rmsList,
        expList = expList,
        avgFirstMean = avgFirstMean, avgFirstRMS = avgFirstRMS,
        avgMiddleMean = avgMiddleMean, avgMiddleRMS = avgMiddleRMS,
        avgLastMean = avgLastMean, avgLastRMS = avgLastRMS,
        individualPathsData = individualPaths
    )
}

// --- GIAO DIỆN VÀ LOGIC CHẠY ---

const val DEFAULT_NUM_STEP = 1000
const val DEFAULT_NUM_SIM = 500

class RandomWalkApp {

    // Hàm tạo biểu đồ (Đã sửa lỗi, dùng hàm mở rộng)
    private fun createChart(
        dataList: List<List<RandomWalkState>>,
        summary: RandomWalkSummary,
        isPathChart: Boolean
    ): LineChart<Number, Number> {
        val xAxis = NumberAxis()
        val yAxis = NumberAxis()
        xAxis.label = "Số bước (N)"
        yAxis.label = if (isPathChart) "Khoảng cách Tích lũy" else "Khoảng cách"

        val chart = LineChart<Number, Number>(xAxis, yAxis)
        chart.title = if (isPathChart)
            "Đường đi Ngẫu nhiên 1D (${summary.allPaths.size} Mô phỏng)"
        else
            "Hội tụ Thống kê (Mean và RMS)"

        chart.setCreateSymbols(false)

        var seriesCounter = 1

        // Dữ liệu cho biểu đồ thống kê
        val statisticsSeries = if (!isPathChart) listOf(summary.avgList, summary.rmsList, summary.expList) else emptyList()

        // Lựa chọn list dữ liệu để vẽ
        val dataToDraw = if (isPathChart) dataList else statisticsSeries

        for (data in dataToDraw) {
            val series = XYChart.Series<Number, Number>()

            series.name = if (isPathChart) "Path $seriesCounter" else when (seriesCounter) {
                1 -> "Simulated Mean"
                2 -> "Simulated RMS"
                3 -> "Theoretical RMS"
                else -> "N/A"
            }

            if (!isPathChart && seriesCounter > 0) {
                series.nodeProperty().addListener { _, _, newNode ->
                    newNode.style = "-fx-stroke-width: 2px;"
                }
            }

            for (state in data) {
                series.data.add(XYChart.Data(state.step, state.dist))
            }
            chart.data.add(series)
            seriesCounter++
        }
        return chart
    }

    private fun buildStatisticsTable(summary: RandomWalkSummary): VBox {
        // Hàm này giữ nguyên logic hiển thị bảng thống kê chi tiết
        val container = VBox(10.0).apply {
            padding = Insets(15.0)
            style = "-fx-border-color: #ddd; -fx-border-width: 1px; -fx-background-color: #f5f5f5;"
        }

        val totalSim = summary.allPaths.size

        container.children.add(Label("Kết quả Thống kê (Trung bình ${totalSim} đoạn)").apply {
            font = Font.font(16.0)
        })

        val resultBox = VBox(5.0).apply { padding = Insets(5.0, 0.0, 5.0, 0.0) }

        val rows = listOf(
            Triple("RMS (Simulated)", "%.4f".format(Locale.US, summary.rmsDistance), Color.BLACK),
            Triple("RMS (Theoretical √N)", "%.4f".format(Locale.US, summary.theoreticalRMS), Color.RED),
            Triple("Trung bình (Cuối)", "%.4f".format(Locale.US, summary.meanDistance), Color.GRAY)
        )

        rows.forEach { (label, value, color) ->
            val row = HBox(10.0).apply {
                alignment = Pos.CENTER_LEFT
                children.addAll(
                    Label(label).apply { prefWidth = 200.0 },
                    Label(value).apply {
                        style = "-fx-font-weight: bold; -fx-text-fill: ${toHex(color)};"
                    }
                )
            }
            resultBox.children.add(row)
        }

        container.children.add(resultBox)

        val rawDataLabel = Label("Dữ liệu thô (3 Đoạn cuối)").apply {
            font = Font.font(14.0); padding = Insets(10.0, 0.0, 5.0, 0.0)
        }
        container.children.add(rawDataLabel)

        summary.individualPathsData.forEach { path ->
            container.children.add(
                buildPathDataTable(
                    "Đoạn ${path.simulationIndex} (Bước ${path.lastSteps.last().step})",
                    path.lastSteps
                )
            )
        }

        return container
    }

    private fun buildPathDataTable(title: String, steps: List<RandomWalkState>): TitledPane {
        // HÀM ĐÃ SỬA LỖI SimpleObjectProperty/SimpleStringProperty
        val table = TableView<RandomWalkState>().apply {
            columns.addAll(
                TableColumn<RandomWalkState, Int>("Bước (N)").apply {
                    setCellValueFactory { cellData -> SimpleObjectProperty(cellData.value.step) }
                    prefWidth = 80.0
                },
                TableColumn<RandomWalkState, String>("Khoảng cách").apply {
                    setCellValueFactory { cellData -> SimpleStringProperty("%.4f".format(Locale.US, cellData.value.dist)) }
                    prefWidth = 150.0
                }
            )
            items.addAll(steps)
            maxHeight = 200.0
        }

        return TitledPane(title, table).apply { isCollapsible = false }
    }

    // HÀM START CHÍNH (Xây dựng UI và biểu đồ)
    fun start(numStep: Int, numSim: Int, primaryStage: Stage) {
        val summary = randomWalk1d(numStep, numSim)

        val pathChart = createChart(summary.allPaths, summary, isPathChart = true)
        val statsChart = createChart(summary.allPaths, summary, isPathChart = false) // Truyền allPaths nhưng logic bên trong dùng statisticsData
        val dataBox = buildStatisticsTable(summary)

        val root = VBox(10.0).apply {
            padding = Insets(10.0)
            children.addAll(pathChart, statsChart, dataBox)
        }

        val scroll = ScrollPane(root).apply { isFitToWidth = true }

        primaryStage.title = "Mô phỏng Random Walk 1D | N=$numStep, Sim=$numSim"
        primaryStage.scene = Scene(scroll, 1200.0, 800.0)
        primaryStage.show()
    }
}

// --- HÀM CHẠY ĐƯỢC GỌI TỪ NÚT BẤM (ĐÃ CHUYỂN showDialog RA NGOÀI) ---

fun runRandomWalk() {
    // Hàm này chỉ gọi showDialog để khởi tạo
    showDialog(null, DEFAULT_NUM_STEP, DEFAULT_NUM_SIM)
}

// HÀM showDialog ĐƯỢC ĐỊNH NGHĨA NGOÀI (ĐÃ SỬA LỖI Unresolved reference 'showDialog')
private fun showDialog(stage: Stage?, initialStep: Int, initialSim: Int) {
    val inputStep = TextField(initialStep.toString())
    val inputSim = TextField(initialSim.toString())

    inputStep.filterInput { it.isDigit() }
    inputSim.filterInput { it.isDigit() }

    val dialogStage = stage ?: Stage()
    dialogStage.title = "Nhập Tham Số Mô phỏng"

    val runSimulation = {
        try {
            val numStep = inputStep.text.toInt()
            val numSim = inputSim.text.toInt()

            if (numStep <= 1 || numSim <= 0 || numSim > 10000) {
                throw NumberFormatException("Số bước > 1, Số mô phỏng > 0 và <= 10000.")
            }

            dialogStage.close()

            val newStage = Stage()
            RandomWalkApp().start(numStep, numSim, newStage)

            // Sau khi mô phỏng xong, gọi lại showDialog để hiển thị nút "Mô phỏng lại"
            showDialog(dialogStage, numStep, numSim)

        } catch (e: NumberFormatException) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Lỗi Đầu Vào"
            alert.headerText = "Tham số Mô phỏng không hợp lệ."
            alert.contentText = e.message ?: "Vui lòng nhập số nguyên hợp lệ."
            alert.showAndWait()
        }
    }

    val runButton = Button(if (stage == null) "CHẠY MÔ PHỎNG" else "MÔ PHỎNG LẠI").apply {
        prefWidth = 200.0
        setOnAction { runSimulation() }
    }

    val root = VBox(15.0).apply {
        padding = Insets(20.0)
        alignment = Pos.TOP_CENTER
        children.addAll(
            Label("Số lượng Bước (N):"), inputStep,
            Label("Số lần Mô phỏng (Sim):"), inputSim,
            runButton
        )
    }

    dialogStage.scene = Scene(root, 350.0, 250.0)
    dialogStage.isResizable = false
    dialogStage.show()
}


// --- HÀM MỞ RỘNG VÀ UTILITY ---

// Hàm mở rộng để lọc input
private fun TextField.filterInput(predicate: (String) -> Boolean) {
    textProperty().addListener { _, _, newValue ->
        if (!predicate(newValue)) {
            val oldValue = null
            text = oldValue
        }
    }
}

// Hàm mở rộng để kiểm tra String có phải là chữ số hay không
private fun String.isDigit(): Boolean {
    return this.all { it.isDigit() }
}

// Hàm chuyển Color sang Hex
private fun toHex(color: Color): String {
    return String.format("#%02X%02X%02X",
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )
}