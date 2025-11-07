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
import com.uth.elearning.elearningproject.common.SimpleTopAppBar
import com.uth.elearning.elearningproject.common.AlgorithmParameterCard
import com.uth.elearning.elearningproject.common.ParameterRow
import com.uth.elearning.elearningproject.common.SectionTitle
import java.util.Locale

data class PythagoreanStep(
    val index: Int,
    val m: Int,
    val n: Int,
    val a: Int,
    val b: Int,
    val c: Int
)





/**
 * Logic: Tạo một Bộ ba Pythagoras duy nhất bằng Công thức Euclid.
 */
fun generatePythagoreanTriple(m: Int, n: Int): Triple<Int, Int, Int> {
    val a = m * m - n * n
    val b = 2 * m * n
    val c = m * m + n * n
    return Triple(a, b, c)
}
/**
 * Logic: Tạo N bộ ba Pythagoras bằng cách tăng dần m và n (m > n > 0) sau mỗi bước.
 */
fun generatePythagoreanTriplesList(mStart: Int, nStart: Int, numTriples: Int): List<PythagoreanStep> {
    if (numTriples <= 0) {
        return emptyList()
    }
    val results = mutableListOf<PythagoreanStep>()
    var m = mStart
    var n = nStart
    if (m <= n) {
        m = n + 1
    }
    for (i in 1..numTriples) {
        val (a, b, c) = generatePythagoreanTriple(m, n)
        results.add(
            PythagoreanStep(index = i, m = m, n = n, a = a, b = b, c = c)
        )
        n++
        m++
    }
    return results
}







/**
 * Màn hình Tạo Bộ ba Pythagoras
 */
@Composable
fun PythagoreanTriplesScreen(navController: NavController) {
    var mStr by remember { mutableStateOf("2") }
    var nStr by remember { mutableStateOf("1") }
    var numTriplesStr by remember { mutableStateOf("10") }

    var resultTriples by remember { mutableStateOf(emptyList<PythagoreanStep>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SimpleTopAppBar(
            title = "Bộ ba Pythagoras",
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
                text = "Tạo các bộ ba Pythagoras (a, b, c trong đó a² + b² = c²) bằng công thức Euclid. Các tham số m và n tăng 1 sau mỗi bước.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            AlgorithmParameterCard("Công thức Sử dụng") {
                ParameterRow("A", "m² - n²")
                ParameterRow("B", "2mn")
                ParameterRow("C", "m² + n²", FontWeight.SemiBold)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
                ParameterRow("Quy tắc", "m > n > 0")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = mStr,
                    onValueChange = { mStr = it.filter { char -> char.isDigit() } },
                    label = { Text("m Bắt đầu") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = nStr,
                    onValueChange = { nStr = it.filter { char -> char.isDigit() } },
                    label = { Text("n Bắt đầu") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = numTriplesStr,
                onValueChange = { numTriplesStr = it.filter { char -> char.isDigit() } },
                label = { Text("Số bộ ba cần tạo (Tối đa 50)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    errorMessage = null
                    resultTriples = emptyList()
                    try {
                        val m = mStr.toInt()
                        val n = nStr.toInt()
                        val num = numTriplesStr.toInt()

                        if (m <= n || n <= 0 || num <= 0) {
                            errorMessage = "Lỗi đầu vào: m phải > n, cả hai phải > 0. Số bộ ba phải > 0."
                        } else if (num > 50) {
                            errorMessage = "Tạo quá nhiều bộ ba. Tối đa là 50."
                        } else {
                            resultTriples = generatePythagoreanTriplesList(m, n, num)
                        }
                    } catch (_: NumberFormatException) {
                        errorMessage = "Đầu vào không hợp lệ. Vui lòng nhập số nguyên hợp lệ."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = mStr.isNotEmpty() && nStr.isNotEmpty() && numTriplesStr.isNotEmpty()
            ) {
                Text("Tạo Bộ ba")
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (resultTriples.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Bộ ba Pythagoras dùng Công thức Euclid",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Số lượng bộ ba Pythagoras: ${resultTriples.size}\n",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Blue Light
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        resultTriples.forEach { step ->
                            val output = String.format(
                                Locale.US,
                                "i=%2d    m=%2d    n=%2d    " +
                                        "\nBộ ba Pythagoras: (%d, %d, %d)",
                                step.index, step.m, step.n, step.a, step.b, step.c
                            )
                            Text(
                                text = output,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                ),
                                color = Color.Black
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                thickness = 0.5.dp,
                                color = Color.LightGray.copy(alpha = 0.5f)
                            )
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
                text = "Công thức Euclid là một phương pháp cổ điển để tạo ra các bộ ba Pythagoras (a, b, c) bằng cách sử dụng hai số nguyên dương 'm' và 'n' (với m > n > 0).\n",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "1. Chọn một cặp số nguyên dương 'm' và 'n', sao cho m > n > 0.\n\n" +
                        "2. Tính toán các giá trị 'a', 'b', và 'c' bằng công thức:\n" +
                        "   a = m² - n²\n" +
                        "   b = 2mn\n" +
                        "   c = m² + n²\n\n" +
                        "3. Các giá trị (a, b, c) tạo thành một bộ ba Pythagoras.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}