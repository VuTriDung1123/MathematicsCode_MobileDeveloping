// File: com/uth/elearning/elearningproject/algorithms/PrimNumberScreen.kt

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --- LOGIC FUNCTIONS ---






/**
 * Hàm chính: Thực hiện Sàng Eratosthenes để tìm tất cả các số nguyên tố <= n.
 */
fun sieveOfEratosthenes(n: Int): List<Int> {
    if (n <= 1) return emptyList()
    val primes = BooleanArray(n + 1) { true }
    val primeNumbers = mutableListOf<Int>()
    primes[0] = false
    primes[1] = false
    var i = 2
    while (i * i <= n) {
        if (primes[i]) {
            for (j in i * i..n step i) {
                primes[j] = false
            }
        }
        i++
    }
    for ((index, value) in primes.withIndex()) {
        if (value) {
            primeNumbers.add(index)
        }
    }
    return primeNumbers
}




/**
 * Định dạng danh sách số nguyên tố thành một chuỗi với 6 số trên mỗi dòng,
 * mô phỏng đầu ra console.
 */
fun formatPrimesOutput(primeNumbers: List<Int>): String {
    val builder = StringBuilder()
    for (i in primeNumbers.indices) {
        if (i != 0 && i % 6 == 0) {
            builder.append("\n")
        }
        builder.append(String.format(Locale.US, "%8d", primeNumbers[i]))
        builder.append(" ")
    }
    return builder.toString()
}

// --- COMPOSE SCREEN ---

/**
 * Identify Prime Numbers with the Sieve of Eratosthenes
 */
@Composable
fun PrimNumberScreen(navController: NavController) {
    // State cho đầu vào N
    var inputNumStr by remember { mutableStateOf("100") }

    // State cho kết quả (đã định dạng chuỗi) và số lượng số nguyên tố tìm được
    var formattedPrimes by remember { mutableStateOf<String?>(null) }
    var primeCount by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SimpleTopAppBar(
            title = "Sàng Eratosthenes", // VIỆT HÓA
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
                // VIỆT HÓA
                text = "Tìm tất cả các số nguyên tố nhỏ hơn hoặc bằng (N) một cách hiệu quả. Yêu cầu N > 1.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            // Input Field N
            OutlinedTextField(
                value = inputNumStr,
                onValueChange = { inputNumStr = it.filter { char -> char.isDigit() } },
                label = { Text("Nhập giới hạn N (ví dụ: 100)") }, // VIỆT HÓA
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button
            Button(
                onClick = {
                    errorMessage = null
                    formattedPrimes = null
                    primeCount = 0
                    isLoading = true // Bắt đầu loading

                    coroutineScope.launch {
                        try {
                            val n = inputNumStr.toInt()

                            if (n < 2) {
                                // VIỆT HÓA
                                errorMessage = "Lỗi đầu vào: N phải lớn hơn 1."
                            } else if (n > 1000000) { // Tăng giới hạn an toàn vì đã dùng Coroutine
                                // VIỆT HÓA
                                errorMessage = "Giới hạn quá cao (Tối đa 1,000,000)."
                            }
                            else {
                                // Chuyển tính toán nặng sang luồng nền
                                val primeList = withContext(Dispatchers.Default) {
                                    sieveOfEratosthenes(n)
                                }

                                primeCount = primeList.size
                                formattedPrimes = formatPrimesOutput(primeList)
                            }
                        } catch (_: NumberFormatException) {

                            errorMessage = "Đầu vào không hợp lệ. Vui lòng nhập số nguyên."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputNumStr.isNotEmpty() && !isLoading // Vô hiệu hóa khi đang tải
            ) {
                Text(if (isLoading) "ĐANG TÌM KIẾM..." else "Tìm Số Nguyên Tố")
            }

            // Hiển thị thanh loading
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
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
            formattedPrimes?.let { output ->
                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("Kết quả: Các số nguyên tố <= ${inputNumStr.toIntOrNull() ?: "N"}")
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            // VIỆT HÓA
                            text = "Tổng số nguyên tố tìm thấy: $primeCount",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Hiển thị danh sách số nguyên tố đã được định dạng
                        Text(
                            text = output,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
            // Card giải thích các bước của thuật toán
            AlgorithmStepsCard()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Composable riêng cho Card giải thích thuật toán
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

                text = "Để sàng lọc số nguyên tố theo Eratosthenes, hãy bắt đầu bằng cách tạo một danh sách tất cả các số nguyên từ 2 đến một giới hạn nhất định. Sau đó, bắt đầu từ 2, lặp lại việc đánh dấu tất cả các bội số của mỗi số nguyên tố là 'hợp số'. Những số không bị đánh dấu còn lại cuối cùng là các số nguyên tố.\n",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(

                text = "1. Tạo một danh sách các số nguyên liên tiếp từ 2 đến giới hạn (N) đã cho.\n\n" +
                        "2. Bắt đầu với 2 (số nguyên tố đầu tiên), đánh dấu tất cả các bội số của nó là 'hợp số'.\n\n" +
                        "3. Tìm số tiếp theo trong danh sách chưa bị đánh dấu là 'hợp số'. Đây sẽ là số nguyên tố tiếp theo.\n\n" +
                        "4. Đánh dấu tất cả các bội số của số nguyên tố tìm được ở bước 3 là 'hợp số'.\n\n" +
                        "5. Lặp lại bước 3 và 4 cho đến khi bình phương của số nguyên tố tiếp theo vượt quá giới hạn (N) đã cho.\n\n" +
                        "6. Những số không bị đánh dấu còn lại trong danh sách là tất cả các số nguyên tố.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}