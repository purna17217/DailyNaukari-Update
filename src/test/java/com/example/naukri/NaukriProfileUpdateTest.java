package com.example.naukri;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;

public class NaukriProfileUpdateTest {

    private WebDriver driver;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();

        // Use headless in CI (Jenkins) when CI env var present
        if (System.getenv("CI") != null) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // optional: disable-gpu on some linux agents
        options.addArguments("--disable-gpu");

        // Create driver (Selenium/Manager or WebDriverManager handles binary)
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        try { driver.manage().window().maximize(); } catch (Exception ignored) {}
    }

    @Test
    public void updateResumeHeadline() throws Exception {
        String username = System.getenv("NAUKRI_USER");
        String password = System.getenv("NAUKRI_PASS");

        Assert.assertNotNull(username, "NAUKRI_USER env var is not set");
        Assert.assertNotNull(password, "NAUKRI_PASS env var is not set");

        System.out.println("🔐 Logging into Naukri...");
        driver.get("https://www.naukri.com/nlogin/login");

        WebElement userField = driver.findElement(By.id("usernameField"));
        WebElement passField = driver.findElement(By.id("passwordField"));
        userField.sendKeys(username);
        passField.sendKeys(password);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // wait a bit for navigation (better to use explicit wait for network / element)
        Thread.sleep(4000);

        driver.get("https://www.naukri.com/mnjuser/profile");
        Thread.sleep(3000);

        driver.findElement(By.cssSelector("#lazyResumeHead .edit.icon")).click();
        Thread.sleep(2000);

        WebElement headlineBox = driver.findElement(By.id("resumeHeadlineTxt"));
        String currentHeadline = headlineBox.getAttribute("value");
        String newHeadline = currentHeadline.endsWith(" ") ? currentHeadline.trim() : currentHeadline + " ";

        headlineBox.clear();
        headlineBox.sendKeys(newHeadline);
        System.out.println("✏️ New headline: " + newHeadline);

        WebElement saveButton = driver.findElement(By.cssSelector("button.btn-dark-ot[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveButton);
        Thread.sleep(500);
        saveButton.click();
        Thread.sleep(2000);

        System.out.println("🎉 Headline updated and saved!");
    }

    @AfterMethod
    public void onFailure(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            try {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File dest = new File("error-" + System.currentTimeMillis() + ".png");
                Files.copy(src.toPath(), dest.toPath());
                System.out.println("📸 Screenshot saved: " + dest.getAbsolutePath());
            } catch (Exception e) {
                System.out.println("⚠️ Could not save screenshot: " + e.getMessage());
            }
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("Browser closed.");
    }
}
