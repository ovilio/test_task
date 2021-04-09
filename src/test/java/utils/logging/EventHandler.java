package utils.logging;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import java.util.*;

import static java.lang.String.format;

/**
 * The {@code EventHandler} class provide methods for automated logging of the webdriver actions such as elements search
 * or interactions.
 */
public class EventHandler extends AbstractWebDriverEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(EventHandler.class);
    private static Queue<String> queue = new ArrayDeque<>(2);
    private static String currentURL;
    private static final int titleCharsNumber = 125;

    //region Events listeners methods
    @Override
    public void afterGetText(WebElement element, WebDriver driver, String text) {
        if (text.length() > 1000) text = text.substring(0, 999) + "[...]";
        logWithTime("Text found: \"" + text + '\"');
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        logWithTime("Navigate to " + url);
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        logWithTime("Navigate back");
    }

    @Override
    public void afterNavigateBack(WebDriver driver) {
        logWithTime("Current URL: " + driver.getCurrentUrl());
        EventHandler.currentURL = driver.getCurrentUrl();
    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        logWithTime("Navigate forward");
    }

    @Override
    public void afterNavigateForward(WebDriver driver) {
        logWithTime("Current URL: " + driver.getCurrentUrl());
        EventHandler.currentURL = driver.getCurrentUrl();
    }

    @Override
    public void beforeNavigateRefresh(WebDriver driver) {
        logWithTime("Refresh page");
    }

    @Override
    public void afterNavigateRefresh(WebDriver driver) {
        logWithTime("Current URL: " + driver.getCurrentUrl());
        EventHandler.currentURL = driver.getCurrentUrl();
    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
        printUrlIfChanged(driver);
        logWithTime("Search for element " + by.toString());
    }

    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {
        if (element != null) {
            logWithTime("Found element: " + element.getTagName());
        } else
            logWithTime("Element not found: " + by.toString());
        printUrlIfChanged(driver);
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        printUrlIfChanged(driver);
        String locator = "#" + element.getAttribute("id");
        if (locator.length() == 1) locator = "." + element.getAttribute("class");
        if (locator.length() == 1) locator = "[text='" + element.getText() + "']";
        logWithTime("Click on element " + element.getTagName() + locator);
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {
        logWithTime("Clicked successfully");
    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
        printUrlIfChanged(webDriver);
        logWithTime("Change value of " + webElement.getTagName() + " " + webElement.getAttribute("id"));
    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
        //Selenide clears the input before changing it value, thus this check will skipp empty strings in logs
        String value = webElement.getAttribute("value");
        if (!value.equals(""))
            if (value.length() > 1000)
                value = value.substring(0, 999) + "[...]";
        logWithTime("Changed value of " + webElement.getTagName() + " : " + value);
        printUrlIfChanged(webDriver);
    }

    @Override
    public void afterSwitchToWindow(String s, WebDriver webDriver) {
        logWithTime("Switched to window: " + webDriver.getTitle());
        printUrlIfChanged(webDriver);
    }
    //endregion
    //region Logging methods

    /**
     * The {@code writeToLogAndConsole} method contain code that filter logs output from duplicates caused by Selenide
     * build in functionality of multiple elements polling.
     * The idea is to store previous two events in the queue, check that the current event are not same as previous two,
     * and move queue events forward by deleting the older event from head and adding the new one in the tail.
     */
    public static void logWithTime(String event) {
        //if the event doesn't exist in queue
        if (!queue.contains(event)) {
            //log it in the logger
            logWithTime(CurrentTime.getCurrentTime(), event);
        }
        //skip removing events from queue while the queue is empty
        if (queue.size() > 1)
            //pop head (older) event
            queue.poll();
        //push the event in the end of queue
        queue.offer(event);
    }

    public static void logWithTimeBold(String event) {
        logWithTime("<span style='color:blue;font-weight:bold;'>" + event + "</span>");
    }

    /**
     * The {@code logWithTime()} method takes {@code time} param and append it with {@code event} param.
     * e.g. "[2020/10/22 16:18:49.570] Found element: button"
     * {@see EventHandler.logWithTime()} method to set time value automatically
     */
    public static void logWithTime(String time, String event) {
        String timedEvent;
        if (!time.isEmpty())
            timedEvent = format("[%s] %s", time, event);
        else timedEvent = event;
        log(timedEvent);
    }

    /**
     * The {@code logTitle()} method prints passed string between horizontal lines with constant width.
     * e.g. "────────────────────────── com.spro.autotests.tests.cequity.ibis.assets.CreateAssetForOfferingTest ──────────────────────────"
     */
    public static void logTitle(String event) {
        int lineCharsNumber = (titleCharsNumber - event.length() - 1) / 2;
        String line = new String(new char[lineCharsNumber]).replace("\0", "─");
        EventHandler.log(format("\n%s %s %s", line, event, line));
    }

    /**
     * The {@code logLine()} method prints a horizontal line with constant width.
     * e.g. "─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────"
     */
    public static void logLine() {
        EventHandler.log(new String(new char[titleCharsNumber]).replace("\0", "─"));
    }

    /**
     * The {@code log()} method simply prints passed string.
     */
    public static void log(String event) {
        LOG.debug(event);
        Reporter.log(event + "<br/>");
        System.out.println(event);
    }

    /**
     * The {@code getErrorResponses()} method gathers from logs responses with 4xx and 5xx status codes
     *
     * @return {@code HashMap<{formatted time}, {statusCode} {statusText}: {url}>
     */
    public static Map<String, String> getErrorResponses(WebDriver driver) {
        final String falseFailedUrl = "chromeextensionmm.innocraft.cloud"; //could be extended to list in future if need to filter more URLs
        Map<String, String> resultErrors = new HashMap<>();
        JsonParser jsonParser = new JsonParser();
        //get all logged requests
//        List<String> webDriverLogs = Selenide.getWebDriverLogs(LogType.PERFORMANCE, Level.ALL);
        List<LogEntry> allResponsesLogs = driver.manage().logs().get("performance").getAll();
        if (!allResponsesLogs.isEmpty()) { //this check here for going out from recursive call
            //go through logs
            for (LogEntry entry : allResponsesLogs) {
                JsonObject message = ((JsonObject) jsonParser.parse(entry.getMessage())).getAsJsonObject("message");
                //check that the current log entry is a response
                if (message.get("method").getAsString().equals("Network.responseReceived")) {
                    JsonObject responseJSON = message.getAsJsonObject("params").getAsJsonObject("response");
                    //check that response has error status code
                    String statusCode = responseJSON.get("status").getAsString();
                    if ((statusCode.startsWith("5") || statusCode.startsWith("4")) && !responseJSON.get("url").getAsString().contains(falseFailedUrl)) {
                        //add erroneous response to the result map
                        resultErrors.put(CurrentTime.formatDate(new Date(entry.getTimestamp())),
                                format("%s %s: %s", statusCode, responseJSON.get("statusText").getAsString(), responseJSON.get("url").getAsString()));
                    }
                }
            }
            //a recursive call here is needed for a case when some responses hit logs after the test ends
            resultErrors.putAll(getErrorResponses(driver));
        }
        return resultErrors;
    }
    //endregion

    public void printUrlIfChanged(WebDriver driver) {
        try {
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.equals(currentURL)) {
                logWithTime("URL changed: " + currentUrl);
                EventHandler.currentURL = currentUrl;
            }
        } catch (org.openqa.selenium.NoSuchWindowException ignored) {
            //for a case, when popup window closes after action with element
        }
    }
}
