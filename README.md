# 📊 Finance Management App

A simple desktop app to manage personal expenses, built with Java and JavaFX.  
This app allows you to track your expenses in an easy-to-use interface without needing any external database.

---

## 🚀 Features
- Add, edit, and delete expenses.
- Categorize expenses for better tracking.
- Simple, lightweight, and works offline.

---

## 📦 Requirements
- Java JDK 17 or later  
- JavaFX SDK (tested with version 24.0.1)  
- Git Bash or Command Prompt (Windows)  

---

## 🛠 Setup Instructions

### 1️⃣ Clone the repository
```bash
git clone https://github.com/echoingwhispers4141/finance-management-app.git
cd finance-management-app
```

### 2️⃣ Download JavaFX SDK
- Go to [JavaFX Downloads](https://gluonhq.com/products/javafx/)  
- Download the latest version for your OS.  
- Extract the folder.  
- Place it inside the project directory and rename it to:
```
javafx-sdk-24.0.1
```
💡 *The JavaFX SDK is not included in this repository to keep it lightweight.*

---

### 3️⃣ Compile the source code  
Using Command Prompt:  
```bash
javac --module-path "javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml -d out src\*.java
```

---

### 4️⃣ Run the application  
Using Command Prompt:  
```bash
java --module-path "javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp out ExpenseManagerAppFX
```
Or simply double-click the `run.bat` file (ensure paths in it are correct).

---

## 📂 Folder Structure
```
finance-management-app/
│  run.bat           # Batch file to compile & run the app
│  README.md         # This file
│
├─ src/              # All source files (.java)
├─ out/              # Compiled classes (created after compilation)
└─ javafx-sdk-24.0.1/# JavaFX SDK (user downloads manually)
```

---

## 📌 Notes
- The app runs entirely in memory — no database required.  
- Works on Windows. For macOS/Linux, update paths accordingly.  
- Tested with JavaFX 24.0.1 and JDK 17.

---

## 👨‍💻 Author
**Kalpit**  
[GitHub Profile](https://github.com/echoingwhispers4141)
