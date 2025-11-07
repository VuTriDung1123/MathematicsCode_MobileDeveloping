// File: com/uth/elearning/elearningproject/algorithms/SquareRootScreen.kt

package com.uth.elearning.elearningproject.algorithms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uth.elearning.elearningproject.common.SectionTitle
import com.uth.elearning.elearningproject.common.SimpleTopAppBar
import com.uth.elearning.elearningproject.common.AlgorithmParameterCard
import com.uth.elearning.elearningproject.common.ParameterRow
import java.util.Locale








/**
 * Tính căn bậc hai bằng Thuật toán Babylon và trả về các bước lặp.
 */
fun babylonianSquareRootWithSteps(num: Double): Pair<Double, List<String>> {
    if (num < 0 || num.isNaN() || num.isInfinite()) {
        return Pair(Double.NaN, listOf("Đầu vào phải là một số không âm."))
    }
    if (num == 0.0) {
        return Pair(0.0, listOf("Kết quả: 0.0"))
    }
    val TOL = 0.000001
    var iter = 0 // Bắt đầu từ 0
    var guess = num / 2.0
    val steps = mutableListOf<String>()
    // Thêm bước 0 (Ước tính ban đầu)
    steps.add(String.format(Locale.US, "Lần %d (Ban đầu): %.6f", iter, guess))
    iter++
    // Vòng lặp
    while (kotlin.math.abs(guess * guess - num) > TOL) {
        // 1. Tính toán 'guess' MỚI
        guess = (guess + num / guess) / 2.0
        // 2. Kiểm tra xem 'guess' MỚI này đã đạt TOL chưa
        // Nếu ĐÃ ĐẠT, đây là kết quả cuối cùng -> Thoát vòng lặp
        if (kotlin.math.abs(guess * guess - num) <= TOL) {
            break
        }
        // 3. Nếu 'guess' VẪN CHƯA ĐẠT, thêm nó vào danh sách
        steps.add(String.format(Locale.US, "Lần %d: Ước tính=%.6f", iter, guess))
        iter++
        if (iter > 100) {
            steps.add("Đã đạt số lần lặp tối đa.")
            return Pair(guess, steps) // Thoát sớm
        }
    }






    steps.add(String.format(Locale.US, "Kết quả Cuối cùng (Lần %d): %.6f", iter, guess))
    return Pair(guess, steps)
}








/**
 * Màn hình tính căn bậc hai bằng thuật toán Babylon.
 */
@Composable
fun SquareRootScreen(navController: NavController) {
    var inputNumStr by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Double?>(null) }
    var steps by remember { mutableStateOf(emptyList<String>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SimpleTopAppBar(
            title = "Căn bậc hai (Babylon)",
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
                text = "Tìm căn bậc hai của một số không âm bằng Phương pháp Babylon.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            AlgorithmParameterCard("Thông số Thuật toán") {
                ParameterRow("Dung sai (TOL)", "0.000001")
                ParameterRow("Số lần lặp Tối đa", "100")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputNumStr,
                onValueChange = { inputNumStr = it.filter { char -> char.isDigit() || char == '.' } },
                label = { Text("Nhập một số không âm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    errorMessage = null
                    steps = emptyList()
                    result = null
                    try {
                        val num = inputNumStr.toDouble()
                        if (num < 0) {
                            errorMessage = "Vui lòng nhập một số không âm."
                        } else {
                            val (finalResult, newSteps) = babylonianSquareRootWithSteps(num)
                            result = finalResult
                            steps = newSteps
                        }
                    } catch (_: NumberFormatException) {
                        errorMessage = "Đầu vào không hợp lệ. Vui lòng nhập một số hợp lệ."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputNumStr.isNotEmpty()
            ) {
                Text("Tính Căn bậc hai")
            }

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }

            if (result != null && result!!.isFinite()) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "✅ Kết quả Cuối cùng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Căn bậc hai (ước tính) của $inputNumStr:", color = Color.DarkGray)
                        Text(
                            text = String.format(Locale.US, "%.6f", result!!),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            if (steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("Các bước Tính toán")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        steps.forEachIndexed { index, step ->
                            val isFinal = step.startsWith("Kết quả Cuối cùng")
                            Text(
                                text = step,
                                fontSize = 14.sp,
                                fontWeight = if (isFinal) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isFinal) Color.Red else Color.Black
                            )
                            if (index < steps.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    thickness = 0.5.dp,
                                    color = Color.LightGray.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
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
                text = "Thuật toán Babylon dùng để xấp xỉ (ước tính) căn bậc hai của một số dương (N) qua các bước lặp. Thuật toán bắt đầu với một 'ước tính' ban đầu, sau đó liên tục 'tinh chỉnh' ước tính đó cho đến khi đủ gần với kết quả thực tế.\n",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "1. Bắt đầu với một ước tính ban đầu, 'guess', cho căn bậc hai của N. (Thường đặt là guess = N / 2).\n\n" +
                        "2. Kiểm tra xem giá trị tuyệt đối của (guess * guess - N) có nhỏ hơn 'giá trị dung sai' (TOL) hay không. Nếu có, kết thúc vòng lặp và trả về 'guess'.\n\n" +
                        "3. Nếu không, cập nhật 'guess' bằng công thức: \nguess = (guess + N / guess) / 2.0.\n\n" +
                        "4. Lặp lại bước 2 và 3 cho đến khi điều kiện dừng (ở bước 2) được thỏa mãn.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}
