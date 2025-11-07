// File: com/uth/elearning/elearningproject/algorithms/OneDimensionalRandomScreen.kt

package com.uth.elearning.elearningproject.algorithms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uth.elearning.elearningproject.common.SectionTitle
import com.uth.elearning.elearningproject.common.SimpleTopAppBar
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import com.uth.elearning.elearningproject.common.AlgorithmParameterCard
import com.uth.elearning.elearningproject.common.ParameterRow
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.uth.elearning.elearningproject.R
import kotlin.math.min

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
    // Thống kê chung
    val meanDistance: Double,
    val rmsDistance: Double,
    val theoreticalRMS: Double,
    val avgFirstMean: List<RandomWalkState>,
    val avgFirstRMS: List<RandomWalkState>,
    val avgMiddleMean: List<RandomWalkState>,
    val avgMiddleRMS: List<RandomWalkState>,
    val avgLastMean: List<RandomWalkState>,
    val avgLastRMS: List<RandomWalkState>,
    // Dữ liệu 3 đoạn đầu tiên
    val individualPathsData: List<PathData>
)








//Logic toán học
fun randomWalk1d(numStep: Int, numSim: Int): RandomWalkSummary {
    val empty = emptyList<RandomWalkState>()
    if (numStep <= 1 || numSim <= 0) {
        return RandomWalkSummary(0.0, 0.0, 0.0,
            empty, empty, empty,
            empty, empty, empty,
            emptyList())
    }
    val sumX = DoubleArray(numStep)
    val sumX2 = DoubleArray(numStep)
    // simulationArray[i][j]
    // i = đoạn (mô phỏng) thứ i
    // j = bước thứ j
    val simulationArray = Array(numSim) { DoubleArray(numStep) }

    for (i in 0 until numSim) {
        for (j in 1 until numStep) {
            val step = if (Random.nextBoolean()) 1 else -1
            simulationArray[i][j] = simulationArray[i][j - 1] + step
            sumX[j] += simulationArray[i][j]
            sumX2[j] += simulationArray[i][j].pow(2)
        }
    }

    val avgList = mutableListOf<RandomWalkState>()
    val rmsList = mutableListOf<RandomWalkState>()
    val expList = mutableListOf<RandomWalkState>()

    for (j in 0 until numStep) {
        val mean = sumX[j] / numSim
        avgList.add(RandomWalkState(j, mean))

        val rms = sqrt(sumX2[j] / numSim)
        rmsList.add(RandomWalkState(j, rms))

        val exp = sqrt(j.toDouble())
        expList.add(RandomWalkState(j, exp))
    }

    val lastIndex = numStep - 1

    val avgFirstMean = avgList.take(5)
    val avgFirstRMS = rmsList.take(5)
    val avgLastMean = avgList.takeLast(5)
    val avgLastRMS = rmsList.takeLast(5)

    val middleIndex = (numStep - 1) / 2
    val startMiddleIndex = (middleIndex - 2).coerceAtLeast(0)
    val endMiddleIndex = (startMiddleIndex + 4).coerceAtMost(lastIndex)

    val avgMiddleMean = avgList.subList(startMiddleIndex, endMiddleIndex + 1)
    val avgMiddleRMS = rmsList.subList(startMiddleIndex, endMiddleIndex + 1)

    val individualPaths = mutableListOf<PathData>()
    val numPathsToShow = min(numSim, 3) // Hiển thị 3 đoạn, hoặc ít hơn nếu numSim < 3

    for (i in 0 until numPathsToShow) {
        val path = simulationArray[i] // Lấy dữ liệu của Đoạn i

        val firstSteps = path.take(5).mapIndexed { stepIndex, dist ->
            RandomWalkState(stepIndex, dist)
        }

        val lastStepsRaw = path.takeLast(5)
        val lastSteps = lastStepsRaw.mapIndexed { index, dist ->
            val stepIndex = numStep - 5 + index
            RandomWalkState(stepIndex, dist)
        }

        val middleStepsRaw = path.slice(startMiddleIndex..endMiddleIndex)
        val middleSteps = middleStepsRaw.mapIndexed { index, dist ->
            val stepIndex = startMiddleIndex + index
            RandomWalkState(stepIndex, dist)
        }

        individualPaths.add(
            PathData(
                simulationIndex = i + 1, // Hiển thị Đoạn 1, 2, 3 (thay vì 0, 1, 2)
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
        avgFirstMean = avgFirstMean,
        avgFirstRMS = avgFirstRMS,
        avgMiddleMean = avgMiddleMean,
        avgMiddleRMS = avgMiddleRMS,
        avgLastMean = avgLastMean,
        avgLastRMS = avgLastRMS,
        individualPathsData = individualPaths
    )
}










/**
 * Màn hình Mô phỏng Di chuyển Ngẫu nhiên 1D
 */
@Composable
fun OneDimensionalRandomScreen(navController: NavController) {
    var numStepStr by remember { mutableStateOf("1000") }
    var numSimStr by remember { mutableStateOf("500") }

    var summary by remember { mutableStateOf<RandomWalkSummary?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SimpleTopAppBar(
            title = "Mô phỏng Di chuyển Ngẫu nhiên 1D",
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mô phỏng đường đi của một hạt thực hiện các bước ngẫu nhiên (+1 hoặc -1). Tập trung vào sự hội tụ thống kê (Khoảng cách Trung bình và RMS).",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            AlgorithmParameterCard("Quy tắc Mô phỏng") {
                ParameterRow("Giá trị Bước", "+1 hoặc -1 (50% xác suất)")
                ParameterRow("Mục tiêu Lý thuyết (RMS)", "Khoảng cách RMS ≈ √N", FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            InputRowRandomWalk("Số lượng Bước (N)", numStepStr) { numStepStr = it }
            InputRowRandomWalk("Số lần Mô phỏng", numSimStr) { numSimStr = it }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    errorMessage = null
                    summary = null

                    coroutineScope.launch {
                        try {
                            val numStep = numStepStr.toInt()
                            val numSim = numSimStr.toInt()

                            if (numStep <= 1 || numSim <= 0) {
                                errorMessage = "Lỗi: Số bước > 1 và Số mô phỏng > 0."
                            } else if (numSim > 10000) {
                                errorMessage = "Tối đa 10,000 mô phỏng để đảm bảo hiệu suất."
                            }
                            else {
                                isLoading = true
                                summary = withContext(Dispatchers.Default) {
                                    randomWalk1d(numStep, numSim)
                                }
                            }
                        } catch (_: NumberFormatException) {
                            errorMessage = "Đầu vào không hợp lệ. Vui lòng nhập số nguyên."
                        } catch (e: Exception) {
                            errorMessage = "Đã xảy ra lỗi: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && numStepStr.isNotEmpty() && !isLoading
            ) {
                Text(if (isLoading) "ĐANG MÔ PHỎNG..." else "CHẠY MÔ PHỎNG")
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                Text("Đang chạy ${numSimStr.toIntOrNull() ?: 0} mô phỏng...", modifier = Modifier.padding(top = 8.dp))
            }

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }

            summary?.let { s ->
                Spacer(modifier = Modifier.height(32.dp))
                SectionTitle("Kết quả Thống kê (Trung bình của ${numSimStr.toIntOrNull() ?: 0} đoạn)")
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4C3)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ResultRow("Khoảng cách Trung bình (Cuối)", "%.4f".format(Locale.US, s.meanDistance), Color.Gray)
                        ResultRow("Khoảng cách RMS (Cuối)", "%.4f".format(Locale.US, s.rmsDistance), Color.Black)
                        ResultRow("RMS Lý thuyết (√N)", "%.4f".format(Locale.US, s.theoreticalRMS), Color(0xFFD32F2F))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                SectionTitle("Hội tụ Thống kê (5 bước đầu - Trung bình)")
                Spacer(modifier = Modifier.height(8.dp))
                ConvergenceTable(s.avgFirstMean, s.avgFirstRMS)

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("Hội tụ Thống kê (5 bước giữa - Trung bình)")
                Spacer(modifier = Modifier.height(8.dp))
                ConvergenceTable(s.avgMiddleMean, s.avgMiddleRMS)

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("Hội tụ Thống kê (5 bước cuối - Trung bình)")
                Spacer(modifier = Modifier.height(8.dp))
                ConvergenceTable(s.avgLastMean, s.avgLastRMS)

                s.individualPathsData.forEach { pathData ->
                    Spacer(modifier = Modifier.height(32.dp))
                    SectionTitle("Dữ liệu Đoạn ${pathData.simulationIndex} (Dữ liệu thô)")

                    Spacer(modifier = Modifier.height(8.dp))
                    PathDataTable(title = "5 bước đầu (Đoạn ${pathData.simulationIndex})", steps = pathData.firstSteps)

                    Spacer(modifier = Modifier.height(16.dp))
                    PathDataTable(title = "5 bước giữa (Đoạn ${pathData.simulationIndex})", steps = pathData.middleSteps)

                    Spacer(modifier = Modifier.height(16.dp))
                    PathDataTable(title = "5 bước cuối (Đoạn ${pathData.simulationIndex})", steps = pathData.lastSteps)
                }

            }

            AlgorithmStepsCard()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Composable riêng cho Card giải thích thuật toán
 */
@Composable
private fun AlgorithmStepsCard() {
    Spacer(modifier = Modifier.height(24.dp))
    SectionTitle("Cách thức hoạt động của Thuật toán")
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Di chuyển ngẫu nhiên (Random Walk) là một quá trình mô tả các bước đi ngẫu nhiên. Mô hình 1D đơn giản hóa điều này thành một hạt di chuyển ngẫu nhiên trên một đường thẳng (tới hoặc lùi) theo các bước nhỏ, rời rạc (Δx = ±1) với xác suất bằng nhau (p = 0.5).\n",
                style = MaterialTheme.typography.bodyMedium
            )

            // Giả sử bạn có file 'img_random_walk_chart.jpg' trong res/drawable
            // (Nếu bạn chưa có, hãy đổi tên file ảnh đã gửi và thêm vào)
            Image(
                painter = painterResource(id = R.drawable.onedimensional),
                contentDescription = "Biểu đồ minh họa Di chuyển Ngẫu nhiên 1D",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Text(
                text = "Sau ${'$'}n bước, hạt sẽ cách vị trí ban đầu bao xa?\n\n" +
                        "Chúng ta không thể lấy trung bình cộng của các khoảng cách (vì các bước âm và dương sẽ triệt tiêu lẫn nhau, cho kết quả ≈ 0). Thay vào đó, chúng ta sử dụng 'Root-Mean-Square' (RMS):\n\n" +
                        "1. Bình phương tất cả các khoảng cách từ các mô phỏng (để loại bỏ số âm).\n\n" +
                        "2. Lấy trung bình cộng của các khoảng cách đã bình phương đó.\n\n" +
                        "3. Lấy căn bậc hai của kết quả ở bước 2 để ra được khoảng cách RMS.\n\n" +
                        "Theo lý thuyết, khoảng cách RMS sau ${'$'}n bước phải xấp xỉ bằng căn bậc hai của ${'$'}n (√n).",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}


@Composable
fun InputRowRandomWalk(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter { char -> char.isDigit() }) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

@Composable
fun ResultRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Text(
            text = value,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontFamily = FontFamily.Monospace
        )
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
}

/**
 * Bảng Thống kê (ConvergenceTable)
 * Dùng cho dữ liệu Trung bình (Mean) và RMS
 */
@Composable
fun ConvergenceTable(meanValues: List<RandomWalkState>, rmsValues: List<RandomWalkState>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)) // Màu xanh nhạt
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                Text("Bước", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Trung bình", Modifier.weight(1.5f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                Text("RMS", Modifier.weight(1.5f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            }
            HorizontalDivider(thickness = 2.dp, color = DividerDefaults.color)

            val combinedList = meanValues.zip(rmsValues)
            combinedList.forEach { (mean, rms) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(mean.step.toString(), Modifier.weight(1f), fontFamily = FontFamily.Monospace)
                    Text("%.4f".format(Locale.US, mean.dist), Modifier.weight(1.5f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                    Text("%.4f".format(Locale.US, rms.dist), Modifier.weight(1.5f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace, color = Color.Black)
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

/**
 * Bảng Dữ liệu Đoạn (PathDataTable)
 * Dùng cho dữ liệu thô của 3 đoạn đầu tiên
 */
@Composable
fun PathDataTable(title: String, steps: List<RandomWalkState>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)) // Màu tím nhạt
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                Text("Bước", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Khoảng cách (Thô)", Modifier.weight(1.5f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            }
            HorizontalDivider(thickness = 2.dp, color = DividerDefaults.color)

            steps.forEach { step ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(step.step.toString(), Modifier.weight(1f), fontFamily = FontFamily.Monospace)
                    Text("%.4f".format(Locale.US, step.dist), Modifier.weight(1.5f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace, color = Color.Black)
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}