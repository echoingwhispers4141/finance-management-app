project: Finance Management App
description: >
  A simple desktop app to manage personal expenses using Java and JavaFX.

requirements:
  - Java JDK 17 or later
  - JavaFX SDK (tested with version 24.0.1)
  - Git Bash or Command Prompt

setup_instructions:
  - step: Clone or download this repository
    command: git clone https://github.com/echoingwhispers4141/finance-management-app.git

  - step: Download JavaFX SDK
    details: >
      Visit https://gluonhq.com/products/javafx/
      and download the latest version for your OS.
      Extract the folder and place it inside the project directory, named "javafx-sdk-24.0.1"

  - step: Compile Java files
    terminal:
      - cd finance-management-app
      - javac --module-path "javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml -d out src/*.java

  - step: Run the app
    terminal:
      - java --module-path "javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp out ExpenseManagerAppFX
    or:
      - Double-click the run.bat file (make sure paths are set correctly)

folder_structure:
  - src/: All source files (.java)
  - out/: Output folder for compiled classes (created after compilation)
  - javafx-sdk-24.0.1/: JavaFX SDK folder (user must add manually)
  - run.bat: Batch file to run the app
  - README.yaml: This file

notes:
  - JavaFX SDK is not uploaded to GitHub — user must download it separately.
  - The app runs fully in-memory, no external database is required.

author:
  name: Sudeep Phogat
  github: https://github.com/echoingwhispers4141
