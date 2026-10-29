# SWT301 Java CI/CD Demo

This standalone topic folder demonstrates a Java application and its CI/CD pipeline. It uses Java 17, Maven, JUnit 5, and GitHub Actions.

## The idea in one minute

CI/CD automates the steps between changing code and delivering a usable program.

- **Continuous Integration (CI)** checks each change by compiling the code and running automated tests. If a check fails, the pipeline stops and reports the problem.
- **Continuous Delivery (CD)** packages code that passed those checks and makes the package available to download. This demo delivers a JAR file; it does not deploy to a live server.

The pipeline in this demo is:

```text
Code change / pull request
          ↓
GitHub Actions checks out the project and sets up Java 17
          ↓
Maven cleans old output and compiles the Java source
          ↓
JUnit runs the automated checks
          ↓
If they pass, Maven builds a JAR package
          ↓
GitHub Actions saves the JAR and test report as artifacts
```

## Run the interactive demo in VS Code

1. Open the `ci_cd_demo_java` folder itself in VS Code.
2. Choose **Terminal → Run Build Task**, or press **Ctrl+Shift+B**.
3. Select **Run Java CI/CD demo** if VS Code asks.
4. Read the terminal. The build runs first. When the menu appears, choose an option:
   - Enter `1` to type your own subtotal and discount together on one line. Enter `49.99 20` for a `$49.99` subtotal and `20%` discount; it gives `$39.99`.
   - Enter `2` to run the guided examples. Press Enter at each prompt to continue.
   - Enter `0` to exit.

The custom-order option lets you try your own values. Enter the subtotal first and the discount percentage second, separated by a space. The app repeats your values before calculating so you can check them. If you enter invalid text or a value outside the allowed range, it explains the problem. The guided option demonstrates a 15% discount, no discount, a 100% discount, and invalid input. The task clears its dedicated terminal panel before each run, keeping the output easy to follow.

The task needs Java 17 or newer and Maven. Its launcher checks Java on your PATH and the JDK bundled in the sibling `visual_demo_java` folder, then checks the usual Maven installation and local cache locations.

## What the program does

`OrderCalculator` takes an order subtotal and a discount percentage. It rejects missing values, negative subtotals, and discounts outside 0–100%. It calculates the discount using `BigDecimal` and rounds the final price to two decimal places.

For example:

```text
Subtotal: $120.00
Discount: 15%
Order total: $102.00
```

The JUnit tests check ordinary and boundary cases: a normal discount, zero discount, full discount, invalid discount, negative subtotal, currency rounding, and missing input.

## Understand the files

| File | Purpose |
|---|---|
| `src/main/java/demo/OrderCalculator.java` | Java application and guided checkout walkthrough |
| `src/test/java/demo/OrderCalculatorTest.java` | Automated JUnit checks of the calculator rules |
| `pom.xml` | Declares Java version, JUnit, and Maven build plugins |
| `.vscode/tasks.json` | Adds the **Run Java CI/CD demo** task to VS Code |
| `run-demo.ps1` | Runs Maven's build and tests, then starts the Java walkthrough |
| `.github/workflows/ci-cd.yml` | Tells GitHub Actions to build, test, package, and save results |
| `target/` | Maven-generated output, including the JAR and test reports |

## What happens in the local task

The VS Code task starts `run-demo.ps1`. The script selects a Java 17+ JDK and Maven, then runs:

```powershell
mvn --batch-mode --no-transfer-progress clean verify
```

Maven runs its build phases in order:

1. `clean` removes old generated output so this run starts fresh.
2. `compile` compiles the application's Java source into `.class` files.
3. `test` compiles and runs the JUnit tests. A failing assertion makes the build fail.
4. `package` creates `target/java-ci-cd-demo-1.0.0.jar` after tests pass.

After Maven succeeds, the script launches the Java application and displays its menu. The menu and pauses are only for the presentation; the automated tests and GitHub workflow do not pause or ask for input.

## What happens in GitHub Actions

The workflow file is `.github/workflows/ci-cd.yml`. To run it on GitHub, make this `ci_cd_demo_java` folder the root of a GitHub repository and push it. It runs on pushes and pull requests, or manually from **Actions → Java CI/CD Demo → Run workflow**.

The GitHub runner checks out the repository, sets up Java 17, caches Maven dependencies, and runs the same `mvn clean verify` build. If the build succeeds, the JAR and JUnit report are uploaded in an artifact named `java-ci-cd-demo-results`. If a test fails, the workflow is marked failed and the test report is still uploaded so the problem can be reviewed. No deployment credentials are needed because the demo does not publish to a live service.

Running the VS Code task demonstrates the pipeline locally. It does not start GitHub Actions; the GitHub workflow runs after the project is pushed to GitHub.

## Presentation walkthrough

1. Start **Run Java CI/CD demo** and point out the Java version and Maven build.
2. Show the JUnit result line: `Tests run: 7, Failures: 0, Errors: 0`.
3. Explain that the JAR is created only after the test phase passes.
4. Choose `2`, press Enter at each prompt, and explain the normal, zero, and full discount examples plus invalid input rejection.
5. Choose `1` and enter `49.99 20`; show the calculated `$39.99` result. Then choose `1` again and enter `49.99 120` to show validation.
6. Explain that the same build and tests run automatically in GitHub Actions after a push or pull request.
7. In the GitHub Actions run, show the success status and download the JAR and test report artifact.

To demonstrate a failure, temporarily change the expected value `102.00` in `OrderCalculatorTest` to `100.00`, then run the VS Code task. The test should fail and Maven will not package a new JAR. Restore `102.00` and rerun to show the pipeline passing again.

## View test results in VS Code

Install **Extension Pack for Java** if it is not already installed. Open the **Testing** view (beaker icon) to discover and run the JUnit test cases and view their pass/fail results. The build log and guided application output remain in the task terminal.
