package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import utils.logging.EventHandler;

import java.util.Map;
import java.util.logging.Level;

import static org.testng.Reporter.setCurrentTestResult;

/**
 * Base class for tests inheritance, contains all configuration methods for web driver.
 * Please be careful while editing and test twice after changes.
 */
public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeClass
    public void setDriver(ITestContext context) {
        setupBrowser();
        context.setAttribute("WebDriver", driver);
    }

    @AfterMethod(alwaysRun = true)
    public void after(ITestResult result) {
        if (null != result) {
            printErrorResponses(result);
        }
    }

    @AfterMethod(alwaysRun = true, dependsOnMethods = {"after"})
    public void killDriver() {
        driver.close();
    }

    public void setupBrowser() {
        System.setProperty("webdriver.chrome.driver", "./src/test/resources/chromedriver.exe");

        //configuration for Chrome browser internal requests logging. Contains some magic, pls be careful while editing
        ChromeOptions chromeOptions = new ChromeOptions();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        chromeOptions.setExperimentalOption("w3c", false);
        chromeOptions.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

        //create new driver instance
        WebDriver chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.manage().window().maximize();
        //pointing EventHandler for driver
        EventFiringWebDriver eventDriver = new EventFiringWebDriver(chromeDriver);
        eventDriver.register(new EventHandler());
        //setting the driver variable
        driver = eventDriver;
        System.setProperty("org.uncommons.reportng.escape-output", "false"); // set false -> do not escape html symbols in reportNG html reports
    }

    /**
     * The {@code printErrorResponses()} method gets all requests\response data from browser after test run, filter it, and printing erroneous URLs in test log.
     */
    public void printErrorResponses(ITestResult result) {
        setCurrentTestResult(result);
        Map<String, String> errorResponses = EventHandler.getErrorResponses(driver);

        if (errorResponses.isEmpty())
            EventHandler.log("No erroneous requests occurred during the test execution.");
        else {
            EventHandler.logTitle("Erroneous requests occurred during test run:");
            for (Map.Entry<String, String> entry : errorResponses.entrySet()) {
                EventHandler.logWithTime(entry.getKey(), entry.getValue());
            }
        }
    }
}