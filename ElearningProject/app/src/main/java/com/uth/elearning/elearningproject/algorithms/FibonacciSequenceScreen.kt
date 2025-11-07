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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uth.elearning.elearningproject.common.SectionTitle
import com.uth.elearning.elearningproject.common.SimpleTopAppBar
import java.util.Locale

// --- LOGIC FUNCTIONS AND DATA CLASS ---

/**
 * Lớp dữ liệu để lưu trữ từng bước của dãy Fibonacci và tỷ lệ vàng.
 */
data class FibonacciStep(
    val index: Int,
    val fn: Long,      // F(n)
    val fnMinus1: Long, // F(n-1)
    val ratio: Double? // F(n)/F(n-1)
)






/**
 * Tạo dãy Fibonacci và tính tỷ lệ vàng cho mỗi bước.
 * Sử dụng Long để tránh tràn số cho các số Fibonacci lớn.
 */
fun generateFibonacciSequenceWithRatios(n: Int): List<FibonacciStep> {
    if (n <= 0) return emptyList()
    val fibs = mutableListOf<Long>()
    val steps = mutableListOf<FibonacciStep>()
    // F(0)
    fibs.add(0L)
    steps.add(FibonacciStep(0, 0L, 0L, null))
    if (n == 1) return steps
    // F(1)
    fibs.add(1L)
    steps.add(FibonacciStep(1, 1L, 0L, null))
    // F(2) trở đi
    for (i in 2 until n) {
        val nextFib = fibs[i - 1] + fibs[i - 2]
        // Kiểm tra tràn số (nếu số quá lớn)
        if (nextFib < fibs[i - 1]) {
            break
        }
        fibs.add(nextFib)
        val fn = nextFib
        val fnMinus1 = fibs[i - 1]
        val ratio = if (fnMinus1 != 0L) fn.toDouble() / fnMinus1.toDouble() else null
        steps.add(
            FibonacciStep(
                index = i,
                fn = fn,
                fnMinus1 = fnMinus1,
                ratio = ratio
            )
        )
    }
    return steps
}







// --- COMPOSE SCREEN  ---

/**
 * Code the Fibonacci Sequence
 */
@Composable
fun FibonacciSequenceScreen(navController: NavController) {
    // State cho đầu vào N
    var inputNStr by remember { mutableStateOf("15") }

    // State cho kết quả
    var resultSteps by remember { mutableStateOf(emptyList<FibonacciStep>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SimpleTopAppBar(
            title = "Dãy số Fibonacci",
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

                text = "Tạo dãy số Fibonacci F(n) và tính tỷ lệ F(n)/F(n-1), tỷ lệ này sẽ tiệm cận Tỷ lệ Vàng (≈ 1.618).",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            // Input Field N
            OutlinedTextField(
                value = inputNStr,
                onValueChange = { inputNStr = it.filter { char -> char.isDigit() } },
                label = { Text("Số lượng phần tử (N)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button
            Button(
                onClick = {
                    errorMessage = null
                    resultSteps = emptyList()
                    try {
                        val n = inputNStr.toInt()

                        if (n <= 0) {
                            errorMessage = "Lỗi đầu vào: N phải lớn hơn 0."
                        } else if (n > 92) {
                            errorMessage = "N tối đa là 92. Số lớn hơn sẽ gây tràn số (Long overflow)."
                        }
                        else {
                            resultSteps = generateFibonacciSequenceWithRatios(n)
                        }
                    } catch (_: NumberFormatException) {
                        errorMessage = "Đầu vào không hợp lệ. Vui lòng nhập một số nguyên hợp lệ cho N."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputNStr.isNotEmpty()
            ) {
                Text("Tạo Dãy số")
            }

            // Hiển thị Lỗi
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Hiển thị Kết quả
            if (resultSteps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("Dãy số và Tỷ lệ Vàng (Số lượng: ${resultSteps.size})")
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)) // Cyan Light
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Header
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                            TableHeader("N", 0.5f)
                            TableHeader("F(n-1)", 1.5f)
                            TableHeader("F(n)", 1.5f)
                            TableHeader("Tỷ lệ F(n)/F(n-1)", 2.5f) // VIỆT HÓA
                        }
                        HorizontalDivider(thickness = 2.dp, color = DividerDefaults.color)

                        // Data Rows
                        resultSteps.forEach { step ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(
                                    text = step.index.toString(),
                                    modifier = Modifier.weight(0.5f),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (step.index > 0) step.fnMinus1.toString() else "-",
                                    modifier = Modifier.weight(1.5f),
                                    textAlign = TextAlign.End,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = step.fn.toString(),
                                    modifier = Modifier.weight(1.5f),
                                    textAlign = TextAlign.End,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = if (step.index > 0) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(

                                    text = step.ratio?.let { "%.6f".format(Locale.US, it) } ?: "-",
                                    modifier = Modifier.weight(2.5f),
                                    textAlign = TextAlign.End,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = Color.LightGray.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                val lastRatio = resultSteps.lastOrNull { it.ratio != null }?.ratio
                if (lastRatio != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tỷ lệ F(n)/F(n-1) tiệm cận Tỷ lệ Vàng (≈ 1.618034).",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD8A000),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            //  Card giải thích các bước của thuật toán
            AlgorithmStepsCard()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Card giải thích thuật toán
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
                text = "Giả sử chúng ta muốn xác định có bao nhiêu cặp thỏ sau một số thế hệ. Đây là các bước sử dụng dãy Fibonacci:\n",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "1. Đặt hai số đầu tiên trong dãy. Theo quy ước, chúng thường là 0 và 1 (thay vì 1 và 1).\n\n" +
                        "2. Cộng hai số đầu tiên để được số thứ ba trong dãy.\n\n" +
                        "3. Tạo số tiếp theo bằng cách cộng hai số liền trước. Biểu thức toán học:\n" +
                        "   Fₙ = Fₙ₋₁ + Fₙ₋₂ (với n ≥ 2)\n\n" +
                        "4. Lặp lại bước 3 cho đến khi đạt điều kiện dừng (ví dụ: đạt đến N phần tử).",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun RowScope.TableHeader(text: String, weight: Float) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.weight(weight)
    )
}