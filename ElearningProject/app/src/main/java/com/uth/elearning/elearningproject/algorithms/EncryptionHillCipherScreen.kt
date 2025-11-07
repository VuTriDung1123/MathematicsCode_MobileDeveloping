package com.uth.elearning.elearningproject.algorithms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uth.elearning.elearningproject.common.SectionTitle
import com.uth.elearning.elearningproject.common.SimpleTopAppBar
import kotlin.text.StringBuilder
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight
import com.uth.elearning.elearningproject.common.AlgorithmParameterCard
import com.uth.elearning.elearningproject.common.ParameterRow
import java.util.Locale

// --- DATA CLASS VÀ LOGIC HILL CIPHER ENGINE ---
data class Block(
    val t1: Char,
    val t2: Char,
    val t3: Char,
)

/**
 * Lớp quản lý logic Mã hóa/Giải mã Hill Cipher, quản lý trạng thái nội bộ
 * và các ma trận khóa.
 */
class HillCipherEngine {
    private val key = arrayOf(
        intArrayOf(13, 11, 6),
        intArrayOf(15, 21, 8),
        intArrayOf(5, 7, 9)
    )
    private val keyInv = arrayOf(
        intArrayOf(1, 12, 8),
        intArrayOf(20, 0, 6),
        intArrayOf(0, 3, 20)
    )
    private val dim = key.size
    val MODULO = 29
    val ALPHABET = "abcdefghijklmnopqrstuvwxyz .?" // 29 ký tự (0-28)

    // Các biến trạng thái nội bộ (được reset khi gọi processText)
    private var indexVector = IntArray(dim)
    private var processedVector = IntArray(dim)
    private var blocks = mutableListOf<Block>()






    // HÀM TÍNH TOÁN CƠ BẢN
    fun multiplyMatricesModN(
        firstMatrix: Array<IntArray>,
        secondMatrix: Array<IntArray>,
        r1: Int, c1: Int, c2: Int, modulo: Int
    ): Array<IntArray> {
        val product = Array(r1) { IntArray(c2) }
        for (i in 0 until r1) {
            for (j in 0 until c2) {
                var sum = 0
                for (k in 0 until c1) {
                    sum += (firstMatrix[i][k] * secondMatrix[k][j])
                }
                product[i][j] = sum % modulo
            }
        }
        return product
    }
    //  Hàm thực hiện C = (P x Key) mod 29 hoặc (C x KeyInv) mod 29
    private fun processIndexBlock(matrix: Array<IntArray>) {
        // Thực hiện (Vector x Key) mod 29
        for (j in 0 until dim) {
            var sum = 0
            for (i in 0 until dim) {
                sum += indexVector[i] * matrix[i][j]
            }
            processedVector[j] = sum % MODULO
        }
    }




    /**
     * Thực hiện kiểm tra tính hợp lệ của Ma trận Khóa.
     */
    fun runValidation(): String {
        val productMatrix = multiplyMatricesModN(key, keyInv, dim, dim, dim, MODULO)

        val productString = StringBuilder()
        productString.append("Kích thước Ma trận Khóa: $dim x ${dim}\n")
        productString.append("[key * keyInv] mod $MODULO =\n")

        var isIdentity = true
        for (i in 0 until dim) {
            for (j in 0 until dim) {

                productString.append(String.format(Locale.US, "%3d", productMatrix[i][j])).append(" ")

                if ((i == j && productMatrix[i][j] != 1) || (i != j && productMatrix[i][j] != 0)) {
                    isIdentity = false
                }
            }
            productString.append("\n")
        }
        productString.append("\nXác thực: ${if (isIdentity) "Thành công (Ma trận Đơn vị)" else "Thất bại"}")
        return productString.toString()
    }

    // HÀM XỬ LÝ TEXT
    private fun prepareText(text: String) {
        blocks.clear()

        // Chỉ giữ lại các ký tự trong bảng chữ cái và chuyển sang chữ thường
        var processedText = text.lowercase().filter { ALPHABET.contains(it) }
        val tmp = ' ' // Ký tự khoảng trắng dùng để đệm

        // Đệm (padding) để đảm bảo độ dài là bội số của 3
        when(processedText.length % 3) {
            1 -> processedText = processedText + tmp + tmp
            2 -> processedText += tmp
            0 -> Unit // Đã là bội số của 3
        }

        // Chia thành các blocks (luôn là 3 ký tự)
        for (i in processedText.indices step 3) {
            blocks.add(Block(processedText[i], processedText[i + 1], processedText[i + 2]))
        }
    }

    private fun getIndexBlock(block: Block) {
        val (x, y, z) = block
        indexVector[0] = ALPHABET.indexOf(x)
        indexVector[1] = ALPHABET.indexOf(y)
        indexVector[2] = ALPHABET.indexOf(z)
    }




    private fun convertVectorToText(): String {
        return processedVector.joinToString("") { ALPHABET[it].toString() }
    }

    /**
     * Hàm chính để mã hóa hoặc giải mã toàn bộ văn bản. (Giữ nguyên)
     */
    fun processText(inputText: String, isEncrypt: Boolean): String {
        prepareText(inputText)
        val result = StringBuilder()
        val matrix = if (isEncrypt) key else keyInv

        for (block in blocks) {
            getIndexBlock(block)
            processIndexBlock(matrix)
            result.append(convertVectorToText())
        }
        return result.toString()
    }
}


// --- REUSABLE COMPOSABLES CHO MÀN HÌNH NÀY ---
// Thêm hàm sao chép vào Clipboard
private fun copyToClipboard(context: Context, text: String, label: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboardManager.setPrimaryClip(clip)
}

@Composable
fun ValidationCard(validationOutput: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBE7)), // Vàng nhạt
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            SectionTitle("Xác thực Ma trận Khóa")
            Text(
                text = validationOutput,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 *LOGIC RADIO BUTTON
 */
@Composable
fun RadioButtonWithLabel(
    label: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        Modifier
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.RadioButton
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelected)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label)
    }
}


// --- COMPOSE SCREEN CHÍNH ---
/**
 * Mã hóa với Mã hóa Hill Cipher
 */
@Composable
fun EncryptionHillCipherScreen(navController: NavController) {
    val context = LocalContext.current // Lấy context để truy cập Clipboard
    val engine = remember { HillCipherEngine() }
    var inputText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var isEncrypt by remember { mutableStateOf(true) }

    val validationOutput by remember { mutableStateOf(engine.runValidation()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SimpleTopAppBar(
            title = "Mã hóa Hill Cipher",
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

                text = "Mã hóa hoặc giải mã tin nhắn bằng phương pháp Hill Cipher 3x3 (mod 29).",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            // --- HIỂN THỊ THAM SỐ THUẬT TOÁN ---
            AlgorithmParameterCard("Các Hằng số Cốt lõi") { // VIỆT HÓA
                ParameterRow("Kích thước Bảng chữ cái (MODULO)", engine.MODULO.toString()) // VIỆT HÓA
                ParameterRow("Bảng chữ cái (Chỉ số 0-28)", engine.ALPHABET) // VIỆT HÓA
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
                Text(

                    text = "Ma trận Khóa (3x3) và ma trận Nghịch đảo đã được cố định và xác thực.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ------------------ 1. Validation ------------------
            ValidationCard(validationOutput)
            Spacer(modifier = Modifier.height(24.dp))

            // ------------------ 2. Lựa chọn (Mã hóa/Giải mã) ------------------
            SectionTitle("Chọn Chế độ") // VIỆT HÓA
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RadioButtonWithLabel("Mã hóa", selected = isEncrypt) {
                    isEncrypt = true
                }
                RadioButtonWithLabel("Giải mã", selected = !isEncrypt) {
                    isEncrypt = false
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ------------------ 3. Input Text ------------------
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },

                label = { Text("Nhập văn bản (a-z, dấu cách, '.', '?')") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ------------------ 4. Button ------------------
            Button(
                onClick = {
                    resultText = engine.processText(inputText, isEncrypt) // Gọi hàm processText
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputText.isNotEmpty()
            ) {

                Text(if (isEncrypt) "MÃ HÓA VĂN BẢN" else "GIẢI MÃ VĂN BẢN")
            }

            // ------------------ 5. Output Text (VỚI CLIPBOARD BUTTON) ------------------
            if (resultText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                // Tiêu đề với nút sao chép
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    SectionTitle(if (isEncrypt) "Kết quả Mã hóa" else "Kết quả Giải mã")

                    IconButton(
                        onClick = {
                            copyToClipboard(
                                context = context,
                                text = resultText,

                                label = if (isEncrypt) "Văn bản đã Mã hóa" else "Văn bản đã Giải mã"
                            )
                        },
                        // Chỉ làm nút sao chép có thể truy cập được khi có kết quả
                        enabled = resultText.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Sao chép vào clipboard",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Text(
                        text = resultText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            }

            //Card giải thích các bước của thuật toán
            AlgorithmStepsCard()


            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

//Card giải thích thuật toán
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
                text = "Mã hóa Hill Cipher xoay quanh các khái niệm từ đại số tuyến tính và toán học modulo. Thuật toán hoạt động bằng cách nhân ma trận vector văn bản (đã chuyển đổi sang số) với một ma trận khóa.\n",
                style = MaterialTheme.typography.bodyMedium
            )

            // Chia thành hai khối Mã hóa và Giải mã
            Text(
                text = "Để Mã hóa (Encryption):", // VIỆT HÓA
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                //Các bước Mã hóa
                text = "1. Xác định bảng chữ cái (ví dụ: 29 ký tự, bao gồm cả ' ', '.', '?').\n" +
                        "2. Chọn kích thước khối (ví dụ: 3 ký tự mỗi khối).\n" +
                        "3. Tạo ma trận khóa mã hóa (ví dụ: 3x3).\n" +
                        "4. Chuẩn bị văn bản (chia thành các khối 3 ký tự, đệm nếu cần).\n" +
                        "5. Chuyển đổi các khối văn bản thành vector số.\n" +
                        "6. Mã hóa tin nhắn. Với mỗi khối, thực hiện các bước sau:\n" +
                        "   a. Nhân vector văn bản gốc (plaintext) của khối với ma trận khóa, modulo 29, để tạo ra một vector đã mã hóa (ciphertext).\n" +
                        "   b. Chuyển đổi các giá trị số trong vector đã mã hóa trở lại thành ký tự văn bản bằng cách sử dụng sơ đồ ánh xạ ngược.\n" +
                        "   c. Thêm các ký tự đã mã hóa vào một danh sách (mutable list) các ký tự, danh sách này sẽ trở thành thông điệp đã mã hóa (ciphertext) sau khi tất cả các khối đã được xử lý.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            Text(
                text = "Để Giải mã (Decryption):", // VIỆT HÓA
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                //Các bước Giải mã
                text = "1. Tạo ma trận khóa giải mã (ma trận nghịch đảo của khóa mã hóa, mod 29).\n" +
                        "2. Chuẩn bị văn bản mã hóa (chia thành các khối 3 ký tự).\n" +
                        "3. Chuyển đổi các khối văn bản mã hóa thành vector số.\n" +
                        "4. Giải mã tin nhắn. Với mỗi khối, thực hiện các bước sau:\n" +
                        "   a. Nhân vector văn bản mã hóa (ciphertext) với ma trận khóa giải mã, modulo 29, để tạo ra một vector đã giải mã.\n" +
                        "   b. Chuyển đổi các giá trị số trong vector đã giải mã trở lại thành ký tự văn bản bằng cách sử dụng sơ đồ ánh xạ ngược.\n" +
                        "   c. Thêm các ký tự đã giải mã vào một danh sách (mutable list) các ký tự, danh sách này sẽ trở thành thông điệp đã giải mã (plaintext) sau khi tất cả các khối đã được xử lý.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}