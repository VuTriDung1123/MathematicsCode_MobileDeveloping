package org.elearningprojectdrawing.elearningproject_drawing

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.geometry.Insets
import javafx.event.EventHandler
import runFibonacci
import runRandomWalk

class LaunchApplication : Application() {
    override fun start(stage: Stage) {
        val root = VBox(20.0) // Container với khoảng cách 20px
        root.alignment = Pos.CENTER // Căn giữa nội dung
        root.padding = Insets(50.0)

        val titleLabel = Label("Elearning Project: Các Dự án Toán học (Dự án 13 và 16)")
        titleLabel.style = "-fx-font: 24px 'Arial'; -fx-font-weight: bold;"

        // Nút 1: Khởi chạy Fibonacci
        val btnFibonacci = Button("1. Dãy Fibonacci & Xoắn ốc")
        btnFibonacci.prefWidth = 300.0
        btnFibonacci.prefHeight = 50.0
        btnFibonacci.onAction = EventHandler {
            // KHÔNG CÓ LỖI: Gọi hàm runFibonacci, hàm này sẽ tạo Stage mới
            runFibonacci()
        }

        // Nút 2: Khởi chạy Random Walk
        val btnRandomWalk = Button("2. Mô phỏng Bước đi Ngẫu nhiên 1D")
        btnRandomWalk.prefWidth = 300.0
        btnRandomWalk.prefHeight = 50.0
        btnRandomWalk.onAction = EventHandler {
            // KHÔNG CÓ LỖI: Gọi hàm runRandomWalk, hàm này sẽ tạo Stage mới
            runRandomWalk()
        }

        // Thêm các controls vào layout
        root.children.addAll(titleLabel, btnFibonacci, btnRandomWalk)

        val scene = Scene(root, 500.0, 350.0)

        stage.title = "Trang Chủ Dự Án"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(LaunchApplication::class.java)
}