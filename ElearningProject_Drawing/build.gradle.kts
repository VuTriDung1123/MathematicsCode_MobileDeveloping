plugins {
    // Sử dụng phiên bản Kotlin JVM mới nhất
    kotlin("jvm") version "1.9.22"
    // Áp dụng plugin 'application' để dễ dàng chạy ứng dụng
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Định nghĩa phiên bản JavaFX và danh sách module cần thiết
val javafxVersion = "21.0.1"
val javafxModules = listOf("controls", "graphics")

dependencies {
    // 1. Cần khai báo 'compileOnly' để Kotlin Compiler biết về các class JavaFX
    for (module in javafxModules) {
        compileOnly("org.openjfx:javafx-$module:$javafxVersion:${getJavafxOsClassifier()}")
        // 2. Cần khai báo 'implementation' để Gradle biết các thư viện cần đưa vào ứng dụng khi chạy
        implementation("org.openjfx:javafx-$module:$javafxVersion:${getJavafxOsClassifier()}")
    }

    // Phụ thuộc Kotlin
    testImplementation(kotlin("test"))
}

application {
    // Đặt tên lớp chính (Main class)
    mainClass.set("FibonacciSpiralKt")
}

// *** HÀNH ĐỘNG KHẮC PHỤC LỖI QUAN TRỌNG ***
// Block này đảm bảo Kotlin Compiler thấy các dependency JavaFX.
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        // Lấy tất cả các file .jar của JavaFX đã được định nghĩa trong dependencies (compileOnly)
        val javafxJars = configurations.compileOnly.get()
            .files
            .filter { it.name.contains("javafx") }

        // Thêm các file jar này vào Classpath của Kotlin Compiler
        // Đây là bước giải quyết lỗi 'unresolved supertypes'
        classpath += javafxJars
    }
}

// Hàm để xác định classifier của hệ điều hành
fun getJavafxOsClassifier(): String {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("win") -> "win"
        osName.contains("mac") -> "mac"
        osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> "linux"
        else -> throw IllegalStateException("Unsupported OS: $osName")
    }
}