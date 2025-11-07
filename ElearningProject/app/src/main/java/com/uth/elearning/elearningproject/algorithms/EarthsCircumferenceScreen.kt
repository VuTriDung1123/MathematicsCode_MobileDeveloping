package com.uth.elearning.elearningproject.algorithms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uth.elearning.elearningproject.R
import com.uth.elearning.elearningproject.common.SectionTitle
import com.uth.elearning.elearningproject.common.SimpleTopAppBar
import kotlin.math.atan
import com.uth.elearning.elearningproject.common.AlgorithmParameterCard
import com.uth.elearning.elearningproject.common.ParameterRow
import java.util.Locale

// --- LOGIC FUNCTIONS AND DATA CLASS ---

data class EarthMetrics(
    val alpha: Double,
    val circumference: Int,
    val radius: Int
)






//Logic toán
fun calculateEarthMetrics(s1: Double, h1: Double,
                          s2: Double, h2: Double, d: Double
): EarthMetrics {
    if (h1 <= 0.0 || h2 <= 0.0) {
        return EarthMetrics(alpha = 0.0, circumference = 0, radius = 0)
    }

    val theta1 = atan(s1 / h1)
    val theta2 = atan(s2 / h2)

    val alpha = kotlin.math.abs(theta2 - theta1)

    // Đảm bảo alpha > 0 để tránh chia cho 0
    val circumference = if (alpha > 0) (2 * Math.PI * d / alpha).toInt() else 0
    val radius = if (alpha > 0) (d / alpha).toInt() else 0

    return EarthMetrics(alpha, circumference, radius)
}








// --- COMPOSE SCREEN ---
/**
 * Calculate Earth’s Circumference the Ancient Way
 */
@Composable
fun EarthsCircumferenceScreen(navController: NavController) {
    // Khối 2: State cho input giả định (sử dụng String)
    var s1Str by remember { mutableStateOf("0.0") }
    var h1Str by remember { mutableStateOf("7.0") }
    var s2Str by remember { mutableStateOf("0.884") }
    var h2Str by remember { mutableStateOf("7.0") }
    var dStr by remember { mutableStateOf("800.0") }

    // Khối 2: State cho kết quả giả định
    var simulatedResult by remember { mutableStateOf<EarthMetrics?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SimpleTopAppBar(
            title = "Chu Vi Trái Đất",
            onBackClick = { navController.popBackStack() },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ước tính chu vi và bán kính bằng phương pháp Eratosthenes dựa trên chiều dài bóng (s), chiều cao vật thể (h), và khoảng cách (d).",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            // BLOCK 1: TÍNH SẴN CHO TRÁI ĐẤT (DÙNG GIÁ TRỊ CỐ ĐỊNH)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)) // Vàng nhạt
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("🌎 Thông số Trái Đất Mẫu")
                    Spacer(modifier = Modifier.height(8.dp))

                    // THÊM DỮ LIỆU INPUT CỐ ĐỊNH
                    AlgorithmParameterCard("Thông số Đầu vào Cố định") {
                        ParameterRow("Bóng 1 (s1)", "0.0 m")
                        ParameterRow("Chiều cao 1 (h1)", "7.0 m")
                        ParameterRow("Bóng 2 (s2)", "0.884 m")
                        ParameterRow("Chiều cao 2 (h2)", "7.0 m")
                        ParameterRow("Khoảng cách (d)", "800.0 km")
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color
                        )
                        ParameterRow("Bán kính Trái Đất (R)", "6371.009 km", FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // END THÊM

                    val fixedMetrics = remember {
                        calculateEarthMetrics(
                            s1 = 0.0, h1 = 7.0,
                            s2 = 0.884, h2 = 7.0,
                            d = 800.0 // km
                        )
                    }

                    OutputRow("Góc (Alpha)", "${"%.5f".format(Locale.US, fixedMetrics.alpha)} radian")
                    OutputRow("Chu vi Ước tính", "${fixedMetrics.circumference} km")
                    OutputRow("Bán kính Ước tính", "${fixedMetrics.radius} km")

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Chu vi Trái Đất Thực tế: ~40075 km",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            SectionTitle("🪐 Tính toán cho Hành tinh Giả định")
            Spacer(modifier = Modifier.height(16.dp))


            // BLOCK 2: INPUT CHO HÀNH TINH GIẢ ĐỊNH

            // Input Fields (s1, h1, s2, h2, d)
            InputRow(label = "Chiều dài bóng 1 (s1)", value = s1Str, onValueChange = { s1Str = it })
            InputRow(label = "Chiều cao vật thể 1 (h1)", value = h1Str, onValueChange = { h1Str = it })
            InputRow(label = "Chiều dài bóng 2 (s2)", value = s2Str, onValueChange = { s2Str = it })
            InputRow(label = "Chiều cao vật thể 2 (h2)", value = h2Str, onValueChange = { h2Str = it })
            InputRow(label = "Khoảng cách giữa hai điểm (d) [km]", value = dStr, onValueChange = { dStr = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Button Tính toán
            Button(
                onClick = {
                    errorMessage = null
                    try {
                        val s1 = s1Str.toDouble()
                        val h1 = h1Str.toDouble()
                        val s2 = s2Str.toDouble()
                        val h2 = h2Str.toDouble()
                        val d = dStr.toDouble()

                        if (h1 == 0.0 || h2 == 0.0) {
                            errorMessage = "Chiều cao (h1 hoặc h2) không được bằng không."
                        } else if (d <= 0.0) {
                            errorMessage = "Khoảng cách (d) phải lớn hơn không."
                        } else {
                            simulatedResult = calculateEarthMetrics(s1, h1, s2, h2, d)
                        }

                    } catch (_: NumberFormatException) {
                        errorMessage = "Lỗi đầu vào. Vui lòng nhập số hợp lệ (ví dụ: 7.0)."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tính Toán Thông số Hành tinh")
            }

            // Hiển thị Lỗi
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Hiển thị Kết quả Giả định
            simulatedResult?.let { metrics ->
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "✅ Kết quả Tính toán",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutputRow("Góc (Alpha)", "${"%.5f".format(Locale.US, metrics.alpha)} radian")
                        OutputRow("Chu vi", "${metrics.circumference} km")
                        OutputRow("Bán kính", "${metrics.radius} km")
                    }
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), // Màu xám nhạt
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                // Mô tả thuật toán
                text = "Thuật toán này mô phỏng phương pháp của Eratosthenes để đo chu vi Trái Đất bằng cách so sánh bóng của hai cột tại hai địa điểm khác nhau (cách nhau một khoảng d).",
                style = MaterialTheme.typography.bodyMedium
            )

            // --- VỊ TRÍ CHÈN HÌNH ẢNH MỚI ---
            Image(
                // Giả định: tên resource trong res/drawable là 'img_eratosthenes_diagram'
                painter = painterResource(id = R.drawable.earthcircum),
                contentDescription = "Sơ đồ đo Chu vi Trái Đất của Eratosthenes",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
            // --- KẾT THÚC CHÈN HÌNH ẢNH ---

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                // Các bước tóm tắt với công thức
                text = "1. Tính góc của mặt trời tại mỗi địa điểm (θ₁, θ₂) bằng chiều dài bóng (s) và chiều cao cột (h):\n" +
                        "   θ = tan⁻¹(s / h)\n\n" +
                        "2. Góc ở tâm Trái Đất (α) là hiệu số giữa hai góc đó:\n" +
                        "   α = |θ₂ - θ₁|\n\n" +
                        "3. Chu vi và Bán kính được tính bằng góc α (tính bằng radian) và khoảng cách d:\n" +
                        "   Chu vi = 2πd / α\n" +
                        "   Bán kính = d / α",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp // Tăng khoảng cách dòng
            )
        }
    }
}


// --- REUSABLE COMPOSABLES CHO INPUT VÀ OUTPUT ---

@Composable
fun InputRow(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter { char -> char.isDigit() || char == '.' }) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

@Composable
fun OutputRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Text(text = value, textAlign = TextAlign.End)
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
}