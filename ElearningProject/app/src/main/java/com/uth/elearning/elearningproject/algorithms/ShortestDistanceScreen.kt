package com.uth.elearning.elearningproject.algorithms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uth.elearning.elearningproject.common.SectionTitle
import com.uth.elearning.elearningproject.common.SimpleTopAppBar
import kotlin.math.*
import com.uth.elearning.elearningproject.common.AlgorithmParameterCard
import com.uth.elearning.elearningproject.common.ParameterRow
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.uth.elearning.elearningproject.R

private const val EARTH_RADIUS_KM = 6371.009

fun adjustCoordinates(lat: Double, latDir: String, lon: Double, lonDir: String): Pair<Double, Double> {
    var adjustedLat = lat
    var adjustedLon = lon

    val latDirUpper = latDir.uppercase().firstOrNull()?.toString() ?: "B"
    val lonDirUpper = lonDir.uppercase().firstOrNull()?.toString() ?: "Đ"


    if (latDirUpper == "B") {
        adjustedLat = kotlin.math.abs(adjustedLat)
    }

    if (lonDirUpper == "Đ") {
        adjustedLon = kotlin.math.abs(adjustedLon)
    }

    // --- LOGIC CHUẨN CỦA HAVERSINE ---

    if (latDirUpper == "N" && adjustedLat > 0.0) { // "N" (Nam)
        adjustedLat = -adjustedLat
    }

    // Nếu hướng là TÂY, giá trị phải là số âm
    if (lonDirUpper == "T" && adjustedLon > 0.0) { // "T" (Tây)
        adjustedLon = -adjustedLon
    }

    return Pair(adjustedLat, adjustedLon)
}






// --- LOGIC TOÁN HỌC CỐT LÕI ---
fun haversineDistance(lat1: Double, lon1: Double,
                      lat2: Double, lon2: Double): Double {
    val phi1 = lat1 * PI / 180
    val phi2 = lat2 * PI / 180
    val delPhi = phi2 - phi1
    val delLambda = (lon2 - lon1) * PI / 180

    val a = sin(delPhi / 2).pow(2) +
            cos(phi1) * cos(phi2) *
            sin(delLambda / 2).pow(2)

    val c = 2 * asin(sqrt(max(0.0, min(1.0, a))))
    val d = EARTH_RADIUS_KM * c
    return d
}







/**
 * Màn hình Tính Khoảng cách Ngắn nhất
 */
@Composable
fun ShortestDistanceScreen(navController: NavController) {
    var name1 by remember { mutableStateOf("Tháp Eiffel") }
    var lat1Str by remember { mutableStateOf("48.8584") }
    var latDir1 by remember { mutableStateOf("B") } // B (Bắc)
    var lon1Str by remember { mutableStateOf("2.2945") }
    var lonDir1 by remember { mutableStateOf("Đ") } // Đ (Đông)

    var name2 by remember { mutableStateOf("Tượng Nữ thần Tự do") }
    var lat2Str by remember { mutableStateOf("40.6892") }
    var latDir2 by remember { mutableStateOf("B") } // B (Bắc)
    var lon2Str by remember { mutableStateOf("74.0445") }
    var lonDir2 by remember { mutableStateOf("T") } // T (Tây)

    var resultDistance by remember { mutableStateOf<Double?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var adjustedLoc1 by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var adjustedLoc2 by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SimpleTopAppBar(
            title = "Khoảng cách (Haversine)",
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
                text = "Tính khoảng cách ngắn nhất (đường chim bay) giữa hai tọa độ địa lý bằng công thức Haversine.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            AlgorithmParameterCard("Tham số Thuật toán") {
                ParameterRow("Bán kính Trái Đất (R)", "$EARTH_RADIUS_KM km", FontWeight.SemiBold)
                ParameterRow("Công thức Sử dụng", "Haversine (Great-Circle)")
                ParameterRow("Đơn vị Góc (nội bộ)", "Radian")
            }
            Spacer(modifier = Modifier.height(16.dp))

            LocationInputCard(
                title = "Địa điểm 1 (Bắt đầu)",
                name = name1, onNameChange = { name1 = it },
                latStr = lat1Str, onLatStrChange = { lat1Str = it },
                latDir = latDir1, onLatDirChange = { latDir1 = it },
                lonStr = lon1Str, onLonStrChange = { lon1Str = it },
                lonDir = lonDir1, onLonDirChange = { lonDir1 = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LocationInputCard(
                title = "Địa điểm 2 (Kết thúc)",
                name = name2, onNameChange = { name2 = it },
                latStr = lat2Str, onLatStrChange = { lat2Str = it },
                latDir = latDir2, onLatDirChange = { latDir2 = it },
                lonStr = lon2Str, onLonStrChange = { lon2Str = it },
                lonDir = lonDir2, onLonDirChange = { lonDir2 = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    errorMessage = null
                    resultDistance = null
                    adjustedLoc1 = null
                    adjustedLoc2 = null

                    try {
                        val lat1 = lat1Str.toDouble()
                        val lon1 = lon1Str.toDouble()
                        val lat2 = lat2Str.toDouble()
                        val lon2 = lon2Str.toDouble()

                        val (adjLat1, adjLon1) = adjustCoordinates(lat1, latDir1, lon1, lonDir1)
                        val (adjLat2, adjLon2) = adjustCoordinates(lat2, latDir2, lon2, lonDir2)

                        adjustedLoc1 = Pair(adjLat1, adjLon1)
                        adjustedLoc2 = Pair(adjLat2, adjLon2)

                        resultDistance = haversineDistance(adjLat1, adjLon1, adjLat2, adjLon2)

                    } catch (_: NumberFormatException) {
                        errorMessage = "Đầu vào không hợp lệ. Vui lòng đảm bảo tất cả các trường là số hợp lệ."
                    } catch (e: Exception) {
                        errorMessage = "Đã xảy ra lỗi không mong muốn: ${e.message}"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tính Khoảng cách")
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            resultDistance?.let { distance ->
                Spacer(modifier = Modifier.height(24.dp))

                AdjustedCoordinatesDisplay(adjustedLoc1!!, adjustedLoc2!!)
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionTitle("✅ Khoảng cách Tính toán")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format(Locale.US, "d = %10.2f km", distance),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    }
                }
            }


            AlgorithmStepsCard()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * [ĐÃ CẬP NHẬT] Composable riêng cho Card giải thích thuật toán
 * (Đã thêm Hình ảnh và Giải thích Vĩ độ/Kinh độ)
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
                text = "Công thức Haversine tính khoảng cách 'đường chim bay' (great-circle) giữa hai điểm trên bề mặt hình cầu (như Trái Đất) bằng tọa độ kinh độ và vĩ độ của chúng. Các giá trị Vĩ độ/Kinh độ ở Bán cầu Nam và Tây phải là số âm.\n",
                style = MaterialTheme.typography.bodyMedium
            )

            // [THÊM MỚI] Hình ảnh và giải thích Vĩ độ/Kinh độ
            Text(
                text = "Vĩ độ (Latitude - Ký hiệu φ) đo khoảng cách về phía Bắc hoặc Nam của đường xích đạo.\n\nKinh độ (Longitude - Ký hiệu λ) đo khoảng cách về phía Đông hoặc Tây của kinh tuyến gốc",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )


            Image(
                painter = painterResource(id = R.drawable.latitude_and_longitude),
                contentDescription = "Biểu đồ minh họa Vĩ độ và Kinh độ",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Phần giải thích công thức (giữ nguyên)
            Text(
                text =  "Ta có các bước chạy thuật toán:\n" +
                        "1. Chuyển đổi Vĩ độ (φ) và Kinh độ (λ) từ độ sang radian (chia cho 180 và nhân π).\n\n" +
                        "2. Tính giá trị 'a' (haversine của góc giữa hai điểm):\n" +
                        "   a = sin²(Δφ/2) + cos(φ₁) * cos(φ₂) * sin²(Δλ/2)\n\n" +
                        "3. Tính góc 'c' (cung) giữa hai điểm (Đảm bảo ${'$'}a nằm trong khoảng [0, 1]):\n" +
                        "   c = 2 * asin(√max(0.0, min(1.0, a)))\n\n" +
                        "4. Tính khoảng cách 'd' bằng bán kính Trái Đất (R):\n" +
                        "   d = R * c",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}

/**
 * Composable cho Card Nhập liệu Địa điểm (sử dụng RadioButton)
 */
@Composable
fun LocationInputCard(
    title: String,
    name: String, onNameChange: (String) -> Unit,
    latStr: String, onLatStrChange: (String) -> Unit,
    latDir: String, onLatDirChange: (String) -> Unit,
    lonStr: String, onLonStrChange: (String) -> Unit,
    lonDir: String, onLonDirChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle(title)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Tên Địa điểm") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = latStr,
                    onValueChange = { onLatStrChange(it.filter { char -> char.isDigit() || char == '.' || char == '-' }) },
                    label = { Text("Vĩ độ (ví dụ: 51.5)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Row(Modifier.padding(start = 8.dp)) {
                    DirectionRadioButton("B (Bắc)", "B", latDir, onLatDirChange)
                    DirectionRadioButton("N (Nam)", "N", latDir, onLatDirChange)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = lonStr,
                    onValueChange = { onLonStrChange(it.filter { char -> char.isDigit() || char == '.' || char == '-' }) },
                    label = { Text("Kinh độ (ví dụ: 0.12)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Row(Modifier.padding(start = 8.dp)) {
                    DirectionRadioButton("Đ (Đông)", "Đ", lonDir, onLonDirChange)
                    DirectionRadioButton("T (Tây)", "T", lonDir, onLonDirChange)
                }
            }
        }
    }
}

/**
 * Composable hỗ trợ cho việc tạo RadioButton có nhãn
 */
@Composable
private fun RowScope.DirectionRadioButton(
    text: String,
    value: String,
    selectedDirection: String,
    onClick: (String) -> Unit
) {
    Row(
        Modifier
            .selectable(
                selected = (value == selectedDirection),
                onClick = { onClick(value) },
                role = Role.RadioButton
            )
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (value == selectedDirection),
            onClick = { onClick(value) }
        )
        Text(text = text, modifier = Modifier.padding(start = 2.dp), style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * Composable hiển thị Tọa độ đã Hiệu chỉnh
 */
@Composable
fun AdjustedCoordinatesDisplay(loc1: Pair<Double, Double>, loc2: Pair<Double, Double>) {
    SectionTitle("Tọa độ đã Hiệu chỉnh")
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEBE9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Địa điểm 1: \nVĩ độ = ${"%.6f".format(Locale.US, loc1.first)}, \nKinh độ = ${"%.6f".format(Locale.US, loc1.second)}")
            Text(text = "Địa điểm 2: \nVĩ độ = ${"%.6f".format(Locale.US, loc2.first)}, \nKinh độ = ${"%.6f".format(Locale.US, loc2.second)}")
        }
    }
}