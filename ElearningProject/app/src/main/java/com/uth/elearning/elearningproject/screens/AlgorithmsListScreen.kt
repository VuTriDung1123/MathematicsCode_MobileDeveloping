package com.uth.elearning.elearningproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.uth.elearning.elearningproject.AppRoutes
import com.uth.elearning.elearningproject.common.ComponentItem

/**
 * Màn hình danh sách các thuật toán toán học.
 */
@Composable
fun AlgorithmsListScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {

        // ------------------ TOP BAR TUY CHINH ------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nút Quay về Trang chủ (Onboarding Screen)
            IconButton(onClick = { navController.navigate(AppRoutes.ONBOARDING_SCREEN) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay về Trang Giới thiệu" // Đã dịch
                )
            }

            // Tiêu đề chính giữa
            Text(
                text = "DANH SÁCH BÀI TOÁN",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // Danh sách các bài toán


            SectionTitle("Nhóm 1: Thuật toán Cổ điển và Toán học Cổ đại")

            ComponentItem("Dự án 9: Tìm Căn bậc hai bằng Thuật toán Babylon", "Hiển thị xấp xỉ từng bước.") {
                navController.navigate(AppRoutes.SQUAREROOT_SCREEN)
            }
            Spacer(modifier = Modifier.height(16.dp))


            ComponentItem("Dự án 12: Tính Chu vi Trái Đất theo Phương pháp Cổ đại", "Mô phỏng phương pháp của Eratosthenes dùng góc và khoảng cách.") {
                navController.navigate(AppRoutes.EARTHSCIRCUMFERENCE_SCREEN)
            }
            Spacer(modifier = Modifier.height(16.dp))



            SectionTitle("Nhóm 2: Số học và Lý thuyết Số")

            ComponentItem("Dự án 10: Tạo Bộ ba Pythagoras bằng Công thức Euclid", "Sinh các bộ số nguyên (a, b, c) thỏa mãn a² + b² = c².") {
                navController.navigate(AppRoutes.PYTHAGOREANTRIPLES_SCREEN)
            }
            Spacer(modifier = Modifier.height(16.dp))


            ComponentItem("Dự án 11: Tìm Số nguyên tố bằng Sàng Eratosthenes", "Tìm tất cả các số nguyên tố dưới giới hạn một cách hiệu quả.") {
                navController.navigate(AppRoutes.PRIMNUMBER_SCREEN)
            }
            Spacer(modifier = Modifier.height(16.dp))



            SectionTitle("Nhóm 3: Tư duy Toán học Trừu tượng và Mô hình")


            ComponentItem("Dự án 13: Lập trình Dãy số Fibonacci", "Tính các số hạng trong dãy số (mỗi số là tổng của hai số liền trước).") {
                navController.navigate(AppRoutes.FIBONACCI_SCREEN)
            }
            Spacer(modifier = Modifier.height(16.dp))


            ComponentItem("Dự án 16: Mô phỏng Di chuyển Ngẫu nhiên Một chiều", "Mô hình hóa chuỗi các bước ngẫu nhiên trên một đường thẳng.") {
                navController.navigate(AppRoutes.ONEDIMENSIONALRANDOM_SCREEN)
            }
            Spacer(modifier = Modifier.height(16.dp))



            SectionTitle("Nhóm 4: Ứng dụng Toán học trong Khoa học Hiện đại")


            ComponentItem("Dự án 14: Tìm Khoảng cách Ngắn nhất giữa Hai Địa điểm trên Trái Đất", "Sử dụng công thức Haversine để tính khoảng cách vòng cung lớn.") {
                navController.navigate(AppRoutes.SHORTESTDISTANCE_SCREEN)
            }
            Spacer(modifier = Modifier.height(16.dp))


            ComponentItem("Dự án 15: Thực hiện Mã hóa với Hill Cipher", "Mã hóa tin nhắn sử dụng đại số tuyến tính (ma trận).") {
                navController.navigate(AppRoutes.ENCRYPTIONHILLCIPHER_SCREEN)
            }
            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}


@Composable
fun SectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color.Red)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}