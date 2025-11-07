package com.uth.elearning.elearningproject.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.elearning.elearningproject.R

/**
 * Màn hình giới thiệu
 */
@Composable
fun OnboardingScreen(onNavigate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //
            Image(
                painter = painterResource(id = R.drawable.book),
                contentDescription = "Logo Sách",
                modifier = Modifier.size(240.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "GIẢI CÁC BÀI TOÁN TOÁN HỌC BẰNG CODE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Khám phá các thuật toán toán học cổ điển được triển khai bằng Kotlin, dựa trên nội dung Phần 2, Chương 4 của sách 'Kotlin from Scratch'. Mỗi màn hình là một bản demo tương tác cho một vấn đề cụ thể.", // Đã dịch
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }

        Button(
            onClick = onNavigate,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3797EF)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Bắt đầu", fontSize = 18.sp, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboarding() {
    MaterialTheme {
        OnboardingScreen {}
    }
}