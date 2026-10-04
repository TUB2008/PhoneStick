# PhoneStick 📱💾

[English](README.md)

<p align="center">
  <img src="fastlane/metadata/android/en-US/images/icon.png" width="128" height="128" alt="PhoneStick 图标">
</p>

PhoneStick 利用 Android 内核 ConfigFS 和 USB gadget 驱动，把已 Root 的 Android 设备变身为 USB 大容量存储磁盘或 CD-ROM 光驱。

<p align="center">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_main.png" width="360" alt="PhoneStick 截图">
</p>

## 功能特性

- **Material Design 3**：现代简洁的界面，支持动态浅色/深色主题，以及扇形展开的悬浮操作按钮。
- **挂载原始磁盘镜像**：将 `.img`、`.iso`、`.bin`、`.raw`、`.vhd`、`.qcow2` 文件直接挂载为 USB 闪存盘或 CD-ROM。
- **零拷贝 SAF 路径解析**：把外置存储（SD 卡、下载目录）的 URI 解析为直接的 Linux 内核路径，不复制文件。
- **模拟模式**：支持只读模式与虚拟 CD-ROM 模拟模式。
- **ConfigFS 与 Sysfs 支持**：可直接绑定 Android ConfigFS（`/config/usb_gadget/g1` 与 `/sys/kernel/config`）以及 sysfs LUN 目标。
- **创建空白镜像**：可直接在应用内分配并格式化空白磁盘镜像。

## 安装

- **GitHub Release**：从 [Releases](https://github.com/mingww64/PhoneStick/releases) 下载最新 APK。
- **F-Droid**：构建配方与元数据见 [`metadata/mingww64.phonestick.yml`](metadata/mingww64.phonestick.yml)。

## 环境要求

- 已 Root 的 Android 设备（Magisk / KernelSU / APatch）
- 内核支持 USB 大容量存储 gadget（`CONFIG_USB_F_MASS_STORAGE` 或 ConfigFS）

## 许可证

[MIT License](LICENSE)

原始作品来自 streetwalrus、dratini0、donfanning、Swyter 和 JinbaIttai。
