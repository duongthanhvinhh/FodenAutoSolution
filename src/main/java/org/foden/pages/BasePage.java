package org.foden.pages;

import com.google.common.base.Function;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.foden.constants.FrameworkConstants;
import org.foden.driver.DriverManager;
import org.foden.utils.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.foden.enums.TimeEntity;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import static org.foden.utils.FileDirectoryUtils.getDownloadPath;
import static org.testng.Assert.assertTrue;

public class BasePage {

    final static Logger logger = LoggerFactory.getLogger(BasePage.class);
    public static WebDriver driver;
    private Map<String, Long> startTimes;
    private Map<String, Double> cumulativeTimes;
    public BasePage(){
        startTimes = new HashMap<>();
        cumulativeTimes = new HashMap<>();
    }
    
    public void init(){
        this.driver = DriverManager.getDriver();
        PageFactory.initElements(driver,this);
    }

    private static final SoftAssert softAssert = new SoftAssert();

    public static void stopSoftAssertAll(){
        softAssert.assertAll();
    }

    private static long totalWaitTime = 0;

    public void closeAlert() {
        if (driver == null) {
            throwNullPointerExeptionForNullDriver();
        }
        if (driver.switchTo().alert() != null) {
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
        }
    }

    public void click(By byLocator) {
        WebElement element = findElement(byLocator);
        if (element == null) {
            logErrorForNotFindingElement(byLocator);
            return;
        }
        click(element);
    }

    public String getText(By byLocator) {
        WebElement element = findElementThatIsPresent(byLocator, 0);
        if (element == null) {
            logErrorForNotFindingElement(byLocator);
            return "";
        }
        return element.getText();
    }

    private boolean isElementCurrentlyDisplayed(By byLocator) {
        WebElement element = findElementThatIsPresent(byLocator, 0);
        if ((element != null) && element.isDisplayed()) {
            return true;
        }
        return false;
    }

    public boolean isDisplayed(By byLocator) {
        return isElementCurrentlyDisplayed(byLocator);
    }

    public WebElement findElement(By byLocator) {
        if (driver == null) {
            throwNullPointerExeptionForNullDriver();
        }
        WebElement element = null;
        try {
            element = driver.findElement(byLocator);
        }catch (Exception e) {
            throw new NullPointerException( "[ERR] Cannot find element with locator " + byLocator);
        }
        return element;
    }

    public List<WebElement> findElements(By byLocator) {
        if (driver == null) {
            throwNullPointerExeptionForNullDriver();
        }
        List<WebElement> element;
        if (!isElementEnabledWithinWait(byLocator, 0)) {

        }
        //change implicit wait timeout to 0
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        element = driver.findElements(byLocator);

        //restore implicit wait timeout
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FrameworkConstants.getImplicitwaitTimeout()));

        return element;
    }

    public WebElement findElement(By byLocator, int maxWaitTime) {
        if (driver == null) {
            throwNullPointerExeptionForNullDriver();
        }
        WebElement element;
        element = driver.findElement(byLocator);
        flash(driver, element);
        return element;
    }

    private boolean isElementEnabledWithinWait(By byLocator, int maxWaitTime) {
        if (isWaitForSuccessful(ExpectedConditions.visibilityOfElementLocated(byLocator), maxWaitTime)) {
            return true;
        }
        return false;
    }

    private boolean isWaitForSuccessful(ExpectedCondition<WebElement> condition, Integer maxWaitTime) {
        boolean result = true;

        if (driver == null) {
            throwNullPointerExeptionForNullDriver();
        }

        if (maxWaitTime == null) {
            maxWaitTime = 3;
        }

        // Fix 60 second wait 
        if (maxWaitTime == 0) {
            maxWaitTime = 1;
        }

        // Current implicitlyWait value is in IMPLICTLY_WAIT_TIMEOUT

        // Set a new implicitlyWait value
        // Fix for SE4 
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(maxWaitTime));

        // Perform actions that require the new implicitlyWait value

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(maxWaitTime));
        try {
            // 60 second time out
            wait.until(condition);

        } catch (TimeoutException e) {
            result = false;
        }

        // Restore the original implicitlyWait value - SE4 PMG
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FrameworkConstants.getImplicitwaitTimeout()));
        return result;
    }

    /**
     * Checks if the current operating system is Linux Docker container (i.e., not a
     * Linux server). Used to spik highlights and and clipboard paste in Jenkins
     * runs
     *
     * @return true if the operating system is Linux; false otherwise.
     * @author Foden Duong
     */
    public boolean isDocker() {
        String osName = System.getProperty("os.name");
        if (osName != null && osName.equalsIgnoreCase("linux")) {
            return true;
        }
        return false;
    }

    // Default for click is to perform the page sync
    final boolean SKIP_PAGE_SYNC = true;


    public void click(WebElement element){
        click(element, SKIP_PAGE_SYNC);
    }

    public void click(String locator) {
        WebElement element = getElement(getByLocator(locator));
        click(element, SKIP_PAGE_SYNC);
    }

    public void click(String locator, boolean skipPageSync) {
        WebElement element = getElement(getByLocator(locator));
        click(element, skipPageSync);
    }

    @Step ("ClickToElement {0} Element")
    public void clickToElement(WebElement element) {
        click(element);
    }

    @Step ("ClickToElement {0} byLocator")
    public void clickToElement(By byLocator) {
        WebElement element = getElement(byLocator);
        clickToElement(element);
    }

    @Step ("ClickToElement {0} string Locator")
    public void clickToElement(String locator) {
        WebElement element = getElement(getByLocator(locator));
        clickToElement(element);
    }

    public boolean isElementDisplayed(WebElement element) {
        waitUntilLoadedAndElementVisible(element);
        return element.isDisplayed();
    }

    public boolean isElementDisplayed(By locator) {
        waitUntilLoadedAndElementVisible(locator);
        return getElement(locator).isDisplayed();
    }

    public boolean isElementDisplayed(String locator) {
        waitUntilLoadedAndElementVisible(getByLocator(locator));
        return getElement(locator).isDisplayed();
    }

    /**
     * Waits until a WebElement's y-coordinate stabilizes.
     *
     * @param element The WebElement to monitor.
     */
    public void waitUntilNotMoving(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            Long lastLocation = (Long) js.executeScript("return arguments[0].getBoundingClientRect().top;", element);

            do {

                // Sleep for a bit to ensure stability (adjust as needed)
                Thread.sleep(250); // Wait for an additional 100 milliseconds

                Long currentLocation = (Long) js.executeScript("return arguments[0].getBoundingClientRect().top;", element);

                // Check if the y-coordinate remains stable
                if (currentLocation == lastLocation) {
                    // The y-coordinate is still changing, continue waiting
                    break;
                }

                lastLocation = currentLocation;

                // If the y-coordinate is still changing, continue waiting
            } while (true);

        } catch (InterruptedException e) {
            // Thread.currentThread().interrupt();

        }
    }

    // Pagesync can be overridden for pagination
    public void click(WebElement element, boolean skipPagesync) {

        String tagName = element.getTagName();
        String locator = element.toString().substring(element.toString().indexOf("->") + 2);

        if(!isElementInViewPort(element)){
            System.out.println(element+ " is off screen. Using jsScroll...");
            jsScroll(element); // Ensure we do not have element off screen.
        }

        try {
            if(waitUntilLoadedAndElementClickable(element)){
                if (tagName.contains("svg") || tagName.equals("path")) {
                    Actions actions = new Actions(driver);
                    actions.click(element).build().perform();
                } else {
                    System.out.println("  Clicking Element: " + locator);
                    highlightOn(driver,element);
                    element.click();
                }
            } else {
                Allure.addAttachment(element + " is not clickable. Trying to click by JS", "");
                jsClick(element);
            }
        } catch (Exception e) {
            //replace all exception cases
            Allure.addAttachment(e.getMessage() + " --> Use jsClick to handle","");
            highlightWarn(driver, element);
            jsClick(element);
        }

        // Skip li and span checkboxes
        if (tagName.equals("li") || (tagName.equals("span"))) {
            System.out.println("Click skipped for List and Span elements with Element " + locator);
            return;
        }

        // Allow page sync to skip on Pagination
        if (skipPagesync) {

            // Workaround: Slow down pagination to keep over loading the DB connections
            sleep(128); // Wait increased from 1/8 to 1/4 second when repeatedly clicking pagination

            Allure.addAttachment("Click skipped Pagesync", "Element " + locator);
        } else {
            pageSync(driver);
        }
    }

    public static String convertToXPath(String input) {
        String[] parts = input.split("\\s+");
        StringBuilder xpathBuilder = new StringBuilder("//");

        for (String part : parts) {
            if (part.contains("=")) {
                String[] attribute = part.split("=", 2);
                String attributeName = attribute[0].trim();
                String attributeValue = attribute[1].trim().replaceAll("^\"|\"$", "");
                xpathBuilder.append("[@").append(attributeName).append("='").append(attributeValue).append("']");
            } else {
                xpathBuilder.append(part);
            }
        }

        return xpathBuilder.toString();
    }

    public static String convertToCSS(String input) {
        String[] parts = input.split("(?<=\\])\\s*(?=[^\\[]*$)");
        StringBuilder cssBuilder = new StringBuilder();

        for (String part : parts) {
            if (part.startsWith("[")) {
                cssBuilder.append(part);
            } else {
                cssBuilder.append(part);
            }
        }

        return cssBuilder.toString();
    }

    public static void printAllAttributes(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String attributesScript = "var element = arguments[0];" + "var attributesList = [];"
                + "for (var i = 0; i < element.attributes.length; i++) {"
                + "    attributesList.push(element.attributes[i].name + ': ' + element.attributes[i].value);" + "}"
                + "return attributesList;";

        // Get all attributes as a list of strings
        @SuppressWarnings("unchecked")
        List<String> attributes = (List<String>) js.executeScript(attributesScript, element);


        System.out.println("Element Attributes:");
        for (String attribute : attributes) {
            System.out.println("  " + attribute);
        }
    }

    public void click(By byLocator, int maxWaitTime) {
        WebElement element = findElement(byLocator, maxWaitTime);
        if (element == null) {
            logErrorForNotFindingElement(byLocator);
            return;
        }
        click(element);
    }

    public void clear(WebElement element) {
        waitUntilLoadedAndElementVisible(element);
        highlightOn(driver, element);
        element.sendKeys(Keys.chord(Keys.LEFT_CONTROL + "A" + Keys.DELETE));
    }

    public void sendText(String inputText, By byLocator) {
        WebElement element = findElement(byLocator);
        if (element == null) {
            logErrorForNotFindingElement(byLocator);
            return;
        }
        highlightOn(driver, element);

        element.sendKeys(inputText);

    }

    public void sendTextToElement(WebElement element, String inputText) {
        if (!checkForElementVisibility(element)) {
            throw new RuntimeException("[ERR] Element " + element + " is not visible for sending text.");
        }
        highlightOn(driver, element);
        if(!getElementAttribute(element,"value").isEmpty()){
            clear(element);
        }
        element.sendKeys(inputText);
    }

    public void sendTextToElement(String locator, String inputText) {
        WebElement element = getElement(locator);
        waitUntilLoadedAndElementVisible(element);
        highlightOn(driver, element);
        clear(element);
        element.sendKeys(inputText);
    }

    @Step("Send Text '{0}'' to '{1}'" )
    public void sendText(String inputText, WebElement element) {
        if (element == null) {
            logErrorForNotFindingElement(element);
            return;
        }
        highlightOn(driver, element);
        clear(element);
        element.sendKeys(inputText);

    }

    public String getText(WebElement element) {
        String text = "";
        try {
            highlightFail (driver, element);
            text = element.getText();
            highlightPass (driver, element);
            return text;

        } catch (Exception e) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement callingMethod = stackTrace[2]; // Index 2 represents the calling method in the stack trace
            Allure.addAttachment("getText() failed ", "Did not get text from '" +element + "'  '" + callingMethod.getClassName() + "' class  '"
                    + callingMethod.getMethodName() + "' method at line no:" + callingMethod.getLineNumber());
            return "";
        }

    }

    protected void logErrorForNotFindingElement(By byLocator) {
        logger.error("Could not find element based on locator: " + byLocator.toString());
    }

    protected void logErrorForNotFindingElement(WebElement element) {
        // logger.error("Could not find element based on locator: " +
        // getBy("element").toString());
    }

    protected void logErrorForNotFindingElement() {
        logger.error("Element is null and can not be acted upon");
    }

    protected void throwNullPointerExeptionForNullDriver() {
        throw new NullPointerException(
                "The Driver object you are using is null.  Please make sure you are passing the correct driver instance into the BasePage.");
    }

    public boolean waitUntilLoadedAndElementClickable(By byLocator) {
        return waitUntilLoadedAndElementClickable(getElement(byLocator));
    }

    public boolean waitUntilLoadedAndElementClickable(WebElement ele) {
        boolean status = false;
        startTimer("waitUntilLoadedAndElementClickable locator");
        try {
            Wait<WebDriver> wait =
                    new FluentWait<>(driver).withTimeout(Duration.ofSeconds(15))
                            .pollingEvery(Duration.ofMillis(500))
                            .ignoring(NoSuchElementException.class);
            wait.until((Function<WebDriver, WebElement>) driver -> ele);
            status=true;
        }catch (Exception e){
            System.out.println("    **** WARNING: waitUntilLoadedAndElementClickable Element - not clickable: "
                    + ele.toString());
            Allure.addAttachment("WARNING:waitUntilLoadedAndElementClickable ",
                    " waitUntilLoadedAndElementClickable Element - not clickable: " + ele);
        }
        return status;
    }

    public void waitUntilElementIsDisappearedInDOM(String locator) {
        changeImplicitWait(5);
        String loadingStripe = "xpath=//div[contains(@class,'loading stripe')]";
        List<WebElement> element = driver.findElements(getByLocator(locator));
        int count = 0;
        if (element.size() != 0) {
            do {
                if (!getElementAttribute(getByLocator(loadingStripe), "class").contains("stripe-visible")) {
                    break;
                }
                sleep(500);
                count++;
            } while (count < 20);
        }
        changeImplicitWait(FrameworkConstants.getImplicitwaitTimeout());
    }

    public void waitUntilElementIsDisappearedInDOM(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(30));
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    private String getElementAttribute(By byLocator, String attribute) {
        return getElement(byLocator).getAttribute(attribute);
    }

    private String getElementAttribute(WebElement element, String attribute) {
        return element.getAttribute(attribute);
    }

    public void refreshPageOnce() {
        driver.navigate().refresh();
        waitPageContentLoaded();
    }

    public void waitUntilLoadedAndElementVisible(By byLocator) {
        waitUntilLoadedAndElementVisible(getElement(byLocator));
    }

    public boolean waitUntilLoadedAndElementVisible(WebElement ele) {
        startTimer("waitUntilLoadedAndElementVisible ele");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_15.getSeconds()));
        try {
            wait.until(ExpectedConditions.visibilityOf(ele));
            jsScroll(ele);
            return true;  
        } catch (Exception e) {
            Allure.addAttachment("WARNING waitUntilLoadedAndElementVisible Element:",
                    "    **** waitUntilLoadedAndElementVisible Element - invisible **** : " + ele);
        }
        return false; 
    }

    public By getByLocator(String locatorType) {
        By by;
        if (locatorType.startsWith("xpath=") || locatorType.startsWith("Xpath=") || locatorType.startsWith("XPATH=")) {
            by = By.xpath(locatorType.substring(6));
        } else if (locatorType.startsWith("css=") || locatorType.startsWith("Css=") || locatorType.startsWith("CSS=")) {
            by = By.cssSelector(locatorType.substring(4));
        } else if (locatorType.startsWith("id=") || locatorType.startsWith("Id=") || locatorType.startsWith("ID=")) {
            by = By.id(locatorType.substring(3));
        } else if (locatorType.startsWith("class=") || locatorType.startsWith("Class=")
                || locatorType.startsWith("CLASS=")) {
            by = By.className(locatorType.substring(6));
        } else if (locatorType.startsWith("name=") || locatorType.startsWith("Name=")
                || locatorType.startsWith("NAME=")) {
            by = By.name(locatorType.substring(5));
        } else {
            throw new RuntimeException("The locator is not valid");
        }
        return by;
    }

    public WebElement getElement(By byLocator) {
        waitUntilElementPresent(byLocator);
        return driver.findElement(byLocator);
    }

    public WebElement getElement(String locator) {
        waitUntilElementPresent(getByLocator(locator));
        return driver.findElement(getByLocator(locator));
    }

    public List<WebElement> getListElement(By byLocator) {
        waitUntilElementPresent(byLocator);
        return driver.findElements(byLocator);
    }

    public List<WebElement> getListElement(String locator) {
        waitUntilElementPresent(getByLocator(locator));
        return driver.findElements(getByLocator(locator));
    }

    public String getLocatorByDynamic(String dynamicLocator, String... dynamicValues) {
        dynamicLocator = String.format(dynamicLocator, (Object[]) dynamicValues);
        return dynamicLocator;
    }

    public String getElementText(WebElement element) {
        waitUntilLoadedAndElementVisible(element);
        return element.getText().trim();
    }

    public String getElementText(By byLocator) {
        waitUntilLoadedAndElementVisible(byLocator);
        return getElement(byLocator).getText().trim();
    }

    public String getElementText(String locator) {
        waitUntilLoadedAndElementVisible(getByLocator(locator));
        return getElement(locator).getText().trim();
    }

    public void waitUntilElementPresent(By locator) {
        System.out.println("    waitUntilElementPresent");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_15.getSeconds()));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void waitUntilLoadedAndTextPresentInElement(By locator, String text) {
        System.out.println("    waitUntilLoadedAndTextPresentInElement: '" + text + "'");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_15.getSeconds()));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    // methods for visibility of element having return type boolean

    public boolean checkForElementVisibility(By locator) {
        try { 
            System.out.println("checkForElementVisibility By locator: " + locator.toString());
        } catch (Exception e) {
            System.out.println("checkForElementVisibility By locator: ");
        }
        try {
            changeImplicitWait(5);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_5.getSeconds()));
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            driver.findElement(locator);
            flash(driver, driver.findElement(locator));
            changeImplicitWait(FrameworkConstants.getImplicitwaitTimeout());
            return true;
        } catch (Exception e) {
            changeImplicitWait(FrameworkConstants.getImplicitwaitTimeout());
            return false;

        }

    }

    public boolean checkForElementVisibility(String locator) {
        try { 
            System.out.println("checkForElementVisibility By locator: " + locator);
        } catch (Exception e) {
            System.out.println("checkForElementVisibility By locator: ");
        }
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_5.getSeconds()));
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(getByLocator(locator)));
            flash(driver, getElement(locator));
            return true;
        } catch (Exception e) {
            return false;

        }

    }

    public boolean checkForElementVisibility(WebElement ele) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_30.getSeconds()));
            wait.until(ExpectedConditions.visibilityOf(ele));
            flash(driver, ele);
            return true;
        } catch (Exception e) {
            if (e.getMessage().contains("stale element reference")){
                try {
                    System.out.println("Checking visibility: Stale exception found, try to getTagName() to refesh element");
                    ele.getTagName(); // Very important -> This will trigger PageFactory to find the element again.
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_10.getSeconds()));
                    wait.until(ExpectedConditions.visibilityOf(ele));
                    flash(driver, ele);
                    System.out.println("Checking visibility: Visible after getTagName()");
                    return true;
                } catch (Exception err){
                    System.out.println("Checking visibility: Still got exception after getTagName(): " + err.toString());
                    return false;
                }
            }
            System.out.println("Checking visibility, Element is NOT displayed: " + e.toString());
            return false;
        }
    }

    public boolean checkForElementVisibilityWithCustomWaitTime(WebElement ele, int timeInSeconds) {
        changeImplicitWait(0);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds((long) timeInSeconds));
            wait.until(ExpectedConditions.visibilityOf(ele));
            return true;
        } catch (Exception e) {
            System.out.println("Element is NOT displayed: " + e.getMessage());
            return false;
        }finally {
            changeImplicitWait(FrameworkConstants.getImplicitwaitTimeout());
        }

    }

    /**
     * Check for presence of a webelement which was initiated using FindElement
     * rather than @FindBy annotation
     * If web element you want to check is a @FindBy web element
     * please use checkForPageFactoryElementPresence instead
     *
     * @param ele the web element you want to check the presence
     */
    public boolean checkForElementPresence(WebElement ele) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_5.getSeconds()));
            wait.until(ExpectedConditions.presenceOfElementLocated((By) ele));
            return true;
        } catch (Exception e) {
            return false;

        }

    }

    public boolean checkForElementPresence(String locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_5.getSeconds()));
            wait.until(ExpectedConditions.presenceOfElementLocated((By) getElement(getByLocator(locator))));
            return true;
        } catch (Exception e) {
            return false;

        }

    }

    /**
     * check for the presence of @FindBy WebElement at the moment
     * FindBy Web Element is special, we can't apply Wait if the element is not
     * already present
     * So use this method if the web element you want to check is a @FindBy
     * WebElement
     * If web element you want to check is normal webelement using FindElement
     * method
     * please use checkForElementPresence instead
     *
     * @param ele the web element you want to check the presence
     * @author Foden Duong
     */
    public boolean checkForPageFactoryElementPresence(WebElement ele) {
        // Method used for PageFactory Proxy WebElement
        return checkForPageFactoryElementPresenceWithCustomWaitTime(ele, 0); // 1 time check
    }

    /**
     * check for the presence of @FindBy WebElement with custom wait time
     * FindBy Web Element is special, we can't apply Wait if the element is not
     * already present
     * So use this method if the web element you want to check is a @FindBy
     * WebElement
     * If web element you want to check is normal webelement using FindElement
     * method
     * please use checkForElementPresence instead
     *
     * @param ele the web element you want to check the presence
     *            timeInSeconds time in second to expect element to be presented
     *            note: if element is found present, function will escape
     *            immediately
     * @author Foden Duong
     */
    public boolean checkForPageFactoryElementPresenceWithCustomWaitTime(WebElement ele, int timeInSeconds) {
        // Method used for PageFactory Proxy WebElement
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(timeInSeconds));
        boolean result = false;
        System.out.println("Check for page factory a element presence...");
        try { 
            // If the element is not presented ele.getText() will throw exception
            ele.getText();
            System.out.println("Element is presented: " + ele.toString());
            result = true;
        } catch (Exception e) {
            System.out.println("Check for page factory element presence: Fail - " + e.getMessage());
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FrameworkConstants.getImplicitwaitTimeout()));
        return result;
    }

    public boolean checkForPageFactoryElementPresenceWithCustomWaitTime(By byLocator, int timeInSeconds) {
        // Method used for PageFactory Proxy byLocator
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(timeInSeconds)); //Checking if element presents in timeInSeconds
        boolean result = false;
        System.out.println("Check for page factory a element presence...");
        try {
            // If the element is not presented ele.getText() will throw exception
            List<WebElement> elements = driver.findElements(byLocator);
            if(elements.size()>0){
                System.out.println("Element is presented: " + elements.get(0).toString());
                result = true;
            }
        } catch (Exception e) {
            System.out.println("Check for page factory element presence: Fail - " + e.getMessage());
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FrameworkConstants.getImplicitwaitTimeout()));
        return result;
    }

    public boolean checkForElementPresence(By locator) {
        System.out.println("    checkForElementPresence");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_5.getSeconds()));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            driver.findElement(locator);
            return true;
        } catch (Exception e) {
            return false;

        }
    }

    /**
     * Check for presence of a webelement which was initiated using FindElement
     * rather than @FindBy annotation
     * If web element you want to check is a @FindBy web element
     * please use checkForPageFactoryElementPresence instead
     *
     * @param ele the web element you want to check the presence
     *            timeInSeconds time wait until found
     */
    public boolean checkForElementPresenceWithCustomWaitTime(WebElement ele, int timeInSeconds) {
        System.out.println("    checkForElementPresenceWithCustomWaitTime");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated((By) ele));
            return true;
        } catch (Exception e) {
            return false;

        }

    }

    public boolean checkForElementPresenceWithCustomWaitTime(By locator, int timeInSeconds) {
        System.out.println("    checkForElementPresenceWithCustomWaitTime");
        changeImplicitWait(timeInSeconds);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            System.out.println("        Element presented");
            changeImplicitWait(FrameworkConstants.getImplicitwaitTimeout());
            return true;
        } catch (Exception e) {
            System.out.println("        Element NOT presented");
            changeImplicitWait(FrameworkConstants.getImplicitwaitTimeout());
            return false;
        }
    }

    public boolean checkForElementTextContains(WebElement element, String text, boolean isCaseSensitive) {
        try {
            waitUntilLoadedAndElementVisible(element);
            jsScroll(element);

            String elementText = element.getText();
            if (isCaseSensitive)
                return elementText.contains(text);
            else
                return elementText.toLowerCase().contains(text.toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkForElementTextEquals(WebElement element, String text, boolean isCaseSensitive) {
        try {
            waitUntilLoadedAndElementVisible(element);
            jsScroll(element);

            String elementText = element.getText();

            if (isCaseSensitive)
                return elementText.equals(text);
            else
                return elementText.equalsIgnoreCase(text);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkForElementInnerTextContains(WebElement element, String text, boolean isCaseSensitive) {
        try {
            waitUntilLoadedAndElementVisible(element);
            jsScroll(element);

            String elementText = element.getAttribute("innerText");
            if (isCaseSensitive)
                return elementText.contains(text);
            else
                return elementText.toLowerCase().contains(text.toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkForElementValueTextContains(WebElement element, String text, boolean isCaseSensitive) {
        try {
            waitUntilLoadedAndElementVisible(element);
            jsScroll(element);

            String elementText = element.getAttribute("value");
            if (isCaseSensitive)
                return elementText.contains(text);
            else
                return elementText.toLowerCase().contains(text.toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean waitForPageToLoad() {

        System.out.println("    waitForPageToLoad");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    logger.info("Page is still loading");
                    return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
                } catch (Exception e) {
                    // no jQuery present
                    return true;
                }
            }
        };

        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                logger.info("Page is still loading");
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
                        .equals("complete");
            }
        };
        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }

    @Step("Hover And Click")
    public void hoverAndClick(WebElement mainElement, WebElement subElement) {
        Actions actions = new Actions(driver);
        actions.moveToElement(mainElement).moveToElement(subElement).click().build().perform();
        // wait for the page to update after the hover
        pageSync(this.driver);
    }

    @Step("Double-Click")
    public void doubleClick(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).doubleClick().build().perform();
        // wait for the page to update after the doubleclick 
        pageSync(this.driver);
    }

    public void sendKeys(Keys textToSend) {
        Actions actions = new Actions(driver);
        actions.sendKeys(textToSend).build().perform();
    }

    @Step("Mouse Hover")
    public void mousehover(WebElement mainElement) {
        Actions actions = new Actions(driver);
        actions.moveToElement(mainElement).build().perform();
        // wait for the page to update after the hover 
        pageSync(this.driver);
    }

    @Step("Mouse Hover By JavaScript")
    public void mouseHoverByJavaScript(WebElement targetElement) {

        String javaScript = "var evObj = document.createEvent('MouseEvents');"
                + "evObj.initMouseEvent('mouseover',true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
                + "arguments[0].dispatchEvent(evObj);";

        ((JavascriptExecutor) driver).executeScript(javaScript, targetElement);
        // wait for the page to update after the hover 
        pageSync(this.driver);
    }

    public void navigateBack() throws InterruptedException {
        driver.navigate().back();
        pageSync(driver);
    }

    public void navigateToUrl(String url) {
        driver.navigate().to(url);
        pageSync(driver);
    }

    public void navigateForward() {
        driver.navigate().forward();
        pageSync(driver);
    }

    /**
     * Waits for the spinner elements on the page to stabilize, and counts the
     * number of span elements on the webpage. Exits the loop when the element count
     * has been stable for a specified number of consecutive iterations or when the
     * timeout expires.
     * Fixed to exit at 120 seconds. 
     *
     * @param driver the WebDriver instance to use for locating the spinner and span
     *               elements
     * @author Foden Duong
     */
    public static String LAST_URL = "";

    public void pageSync(WebDriver driver) {

        // Skip Spinner Count if Popup fade exist
        By byFade = By.xpath("//div[contains(@class,'fade')]");

        // Do not wait for Spinners when a popup fade exists - set ImplicitWait to an
        // instant wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // - SE4 PMG

        try {
            // Check if a Modal Popup exists.
            // This means 50% grey (fade) object is blocking user interaction, so we can skip
            // waiting for the spinners.

            @SuppressWarnings("unused")
            WebElement fade = driver.findElement(byFade);

            // 50% grey (fade) object is blocking user interaction element did exist. Is it
            // temporary?
            sleep(500); // Wait 1/2 second for the fade to disappear.
            @SuppressWarnings("unused")
            WebElement fade2 = driver.findElement(byFade);

            // System.out.println(" pageSync() Detected a Modal popup overlay fade");
            // Allure.addAttachment("pageSync() Detected a Modal popup overlay fade, "");
        } catch (Exception e) {
            // Eat the error wait for Spinners - No fade object exists, or it disappeared
            // System.out.println(" No fade detected");
        } finally {
            // Reset the timeout
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FrameworkConstants.getImplicitwaitTimeout()));
            
        }

        long timeout = 60000; // Boosted from 25 second to 60 for Download of 1000s vins timeout - 3 X worst performance (8 sec.) on a good day 
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime;
        int count = -1;
        int stableCount = 0;
        int prevCount = -1;
        int endLoop = 2; // Three stable counts
        String currentUrl = "";

        while (stableCount < endLoop) {
            currentUrl = driver.getCurrentUrl();

            try {
                count = driver.findElements(By.tagName("span")).size();
            } catch (Exception e) {
                count = 0;
            }

            // Exit if the page is the login and count is stable
            if (currentUrl.contains("login")) {
                // New magic_Url might hit login for just a moment.
                // Extend the login timeout to a minute
                if (count > 4) {  // Login Page has 5 elements

                    // A stable count of 5 or more 3 times.
                    Allure.addAttachment("Login page detected in pageSync()", "'" + currentUrl + "'");
                    break;
                } else {
                    sleep(1000); // extend to 1 sec pause loops at login
                }
            }


            if ((count == prevCount) && (count > 24)) { // No data Found = 25
                stableCount++;
            } else {
                stableCount = 0;
            }

            prevCount = count;

            sleep(250);
            currentTime = System.currentTimeMillis();

            if ((currentTime - startTime) > timeout) {
                Allure.addAttachment("     pageSync TIME OUT. SLA EXCEEDED after " + ((currentTime - startTime) / 1000)
                        + " seconds.", "url " + currentUrl + "  | Span Count :" + count);
                break;
            }

            LAST_URL = currentUrl;
        }
        waitPageContentLoaded();
    }

    private String getEnvironmentOnUrl(String url){
        return url.substring(url.indexOf("-") + 1, url.indexOf("."));
    }

    /**
     * Waits up to 10 seconds for the spinner to disappear, and outputs the maximum
     * number of spinners on the page if it's greater than 0.
     *
     * @param driver the WebDriver instance to use for finding the spinner
     *               element(s)
     * @author Foden Duong
     */
    public void waitForSpinners(WebDriver driver) {

        try {

            int timeout = 60;
            long startTime = System.currentTimeMillis();
            long currentTime = System.currentTimeMillis();
            int maxSpinners = 0;
            boolean detected = false;

            while (currentTime - startTime < timeout * 1000) {
                List<WebElement> spinnerElements = null;
                List<WebElement> loadingElements = null;
                // Instant count
                try {
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // - SE4 PMG
                    spinnerElements = driver.findElements(
                            By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), 'spinner')]"));

                    loadingElements = driver.findElements(
                            By.xpath("//*[contains(concat(' ', normalize-space(@data-qa), ' '), '-loading-')]"));
                } catch (Exception e) {
                    // Eat the error
                    // System.out.println("    waiting for spinners error...");
                } finally {
                    // Reset the timeout
                    driver.manage().timeouts()
                            .implicitlyWait(Duration.ofSeconds(FrameworkConstants.getImplicitwaitTimeout())); // - SE4 PMG
                }

                int numSpinners = 0;

                if (spinnerElements != null) {
                    numSpinners += spinnerElements.size();
                }

                if (loadingElements != null) {
                    numSpinners += loadingElements.size();
                }

                if (numSpinners > 0 && numSpinners > maxSpinners) {
                    // Highlight spinner(s) on
                    highlightOn(driver, spinnerElements.get(0));
                    maxSpinners = numSpinners;
                    if (!detected) {
                        // Allure.addAttachment("    Detected " + maxSpinners + " spinner(s)", "");
                        detected = true;
                    }
                }

                if (numSpinners == 0) {
                    if (detected) {
                        currentTime = System.currentTimeMillis();
                        // Allure.addAttachment("    Waited on " + maxSpinners + " spinners for "
                        // 		+ (currentTime - startTime) / 1000 + " seconds.", "");
                    }
                    return; // Spinner is gone
                }  // System.out.println("Detecting " + maxSpinners + " spinnners for " +
                // (currentTime - startTime) / 1000 + " seconds.");


                // Highlight spinner(s) off
                sleep(200); // Throttle for highlight.
                assert spinnerElements != null;
                highlightOff(driver, spinnerElements.get(0));
                currentTime = System.currentTimeMillis();
            }

            Allure.addAttachment(
                    "waitForSpinners() timed out after " + timeout + " seconds waiting for " + maxSpinners + " spinners to disappear.", "");

        } catch (Exception e) {
            // Spinners gone - Eat the error
        }
    }

    /**
     * Loops the refresh() and pageSync() methods with a 500ms pause for up to 5
     * seconds,
     * and exits the loop if the page source changes.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     * @author Foden Duong
     */
    /**
     * Loops the refresh() and pageSync() methods with a 500ms pause for up to 5
     * seconds, and exits the loop if the page source changes.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     * @author Foden Duong
     */
    /**
     * Loops the refresh() and pageSync() methods with a 500ms pause for up to 5
     * seconds, and exits the loop if the page source changes.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     * @author Foden Duong
     */
    public void refreshPage() throws InterruptedException {
        String currentSource = driver.getPageSource();
        int loopCount = 0;
        int minimumPageChange = 96;
        do {
            driver.navigate().refresh();
            pageSync(driver);

            String newSource = driver.getPageSource();
            if (!newSource.equals(currentSource)) {
                int diffPercentage = (int) (((double) countDifferentChars(currentSource, newSource))
                        / currentSource.length() * 100);

                // Change of less than 70% means the page is not updated significantly
                if (diffPercentage > minimumPageChange) { // If 75% of the characters are different
                    System.out.println("Page source changed significantly: " + diffPercentage + "% difference");
                    break;
                } else {
                    System.out.println("Page source only changed: " + diffPercentage + "% difference");
                }
            }

        } while (loopCount++ < 3);

        waitPageContentLoaded();
    }

    /**
     * Counts the number of different characters between two strings.
     *
     * @param s1 the first string
     * @param s2 the second string
     * @return the number of different characters
     */
    private int countDifferentChars(String s1, String s2) {
        int count = 0;
        for (int i = 0; i < s1.length(); i++) {
            try {
                if (s1.charAt(i) != s2.charAt(i)) {
                    count++;
                }
            } catch (Exception e) {
                // Ignore exception
            }
        }
        return count;
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getPageUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isUrlContains(String partialURL){
        return getPageUrl().contains(partialURL);
    }

    public void jsClick(By byLocator) {
        WebElement element = findElement(byLocator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        pageSync(driver);
    }

    public void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        pageSync(driver);
    }

    public void jsClick(String locator) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", getElement(locator));
        pageSync(driver);
    }

    public void jsScroll(By byLocator) {

        WebElement element = findElement(byLocator);
        jsScroll(element);
    }

    public void jsScrollAtLast() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");

    }

    public void jsScrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        sleep(1000);

    }

    public void jsScroll(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        sleep(1000);
    }

    public void jsScroll(WebElement element, int waitInMilliseconds) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        sleep(waitInMilliseconds);
    }

    public void jsScroll(String locator) {
        WebElement element = getElement(locator);
        jsScroll(element);
    }

    public void switchToFrame(int Index) {
        JavascriptExecutor exe = (JavascriptExecutor) driver;
        Integer numberOfFrames = Integer.parseInt(exe.executeScript("return window.length").toString());
        System.out.println("Number of iframes on the page are " + numberOfFrames);

        // By finding all the web elements using iframe tag
        List<WebElement> iframeElements = driver.findElements(By.tagName("iframe"));
        System.out.println("The total number of iframes are " + iframeElements.size());
        driver.switchTo().frame(Index);
    }


    public boolean isElementInViewPort(WebElement element) {

        boolean isElementInViewport = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var rect = arguments[0].getBoundingClientRect();\n" +
                        "return (\n" +
                        "    rect.top >= 0 &&\n" +
                        "    rect.left >= 0 &&\n" +
                        "    rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&\n" +
                        "    rect.right <= (window.innerWidth || document.documentElement.clientWidth)\n" +
                        ");", element);
        if (isElementInViewport) {
            System.out.println("Element is in the viewport");
        } else {
            System.out.println("Element is not in the viewport");
        }
        return isElementInViewport;
    }

    public List<String> getAllSectionsFromApp() {
        List<String> liValue = new ArrayList<String>();
        List<WebElement> li = driver
                .findElements(By.xpath("//*[@class='test-id__section-header-title slds-truncate']"));
        for (WebElement we : li) {
            liValue.add(we.getText());
        }
        return liValue;
    }


    public String getTableValue(int row, int column) {

        String tablevalue = driver.findElement(By.xpath("//table//tbody/tr[" + row + "]/td[" + column + "]")).getText()
                .toString();
        return tablevalue;

    }

    public void clickDropDownIconInRelatedList(String RelatedList) {
        String xpath = "//*[*[text()='" + RelatedList + "']]/../../../..//*[contains(@class,'button')]";
        WebElement element = driver.findElement(By.xpath(xpath));
        waitUntilLoadedAndElementClickable(By.xpath(xpath));
        element.click();
    }

    public void selectValueFromDropDownInTheRelatedList(String value) {
        String xpath = "//*[@class='uiMenuItem']//a[@title='" + value + "']";
        WebElement element = driver.findElement(By.xpath(xpath));
        waitUntilElementPresent(By.xpath(xpath));
        findElementThatIsPresent(By.xpath(xpath), TimeEntity.SEC_3.getSeconds());
        element.click();
    }

    public WebElement findElementThatIsPresent(final By byLocator, int maxWaitTime) {
        if (driver == null) {
            throwNullPointerExeptionForNullDriver();
        }
        FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(maxWaitTime))
                .pollingEvery(Duration.ofMillis(200));

        try {
            return wait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver webDriver) {
                    List<WebElement> elems = driver.findElements(byLocator);
                    if (elems.size() > 0) {
                        return elems.get(0);
                    } else {
                        return null;
                    }
                }
            });
        } catch (Exception e) {
            return null;
        }
    }

    public void selectValueFromDropDownInTheRelatedList1(String value) {
        String xpath = "//a[@title='" + value + "']";
        WebElement element = driver.findElement(By.xpath(xpath));
        element.click();
    }

    public void switchToWindowURL(String expectedURL) {
        Set<String> handleID = driver.getWindowHandles();
        Iterator<String> it = handleID.iterator();
        String actualURL = null;

        first: while (it.hasNext()) {
            driver.switchTo().window(it.next());
            actualURL = driver.getCurrentUrl();

            if (actualURL.contains(expectedURL)) {
                break first;
            }
        }
    }

    public void switchToDefaultContent() {
        try {
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            logger.info("Could not switch to default content");
            e.printStackTrace();
        }

    }

    public void switchToIframe() {
        try {
            WebElement iframe = driver.findElement(By.xpath("//div[@class='content']//iframe"));
            driver.switchTo().frame(iframe);
        } catch (Exception e) {
            logger.info("Could not switch to iframe");
            e.printStackTrace();
        }
    }

    public void waitUntilPageTitleIsPresent(String title) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.titleIs(title));
    }

    /**
     * @param locator
     * @return List<WebElement> : Each element in list repre a value in drop down
     */
    public List<WebElement> getAllValues(By locator) {
        Select select = new Select(driver.findElement(locator));
        return select.getOptions();
    }

    /**
     * @param locator      :- unique
     * @param visibleValue :- UI value
     */
    public void selectByVisibleText(By locator, String visibleValue) {
        Select select = new Select(driver.findElement(locator));
        select.selectByVisibleText(visibleValue);
    }

    /**
     * @param locator
     * @param index   :- Index of the value to be selected
     */
    public void selectByIndex(By locator, int index) {
        Select select = new Select(driver.findElement(locator));
        select.selectByIndex(index);

    }

    /**
     * @param locator
     * @param valueAttribute :- Corresponding value attribute
     */
    public void selectByValue(By locator, String valueAttribute) {
        Select select = new Select(driver.findElement(locator));
        select.selectByValue(valueAttribute);
    }

    public void selectByValue(WebElement element, String valueAttribute) {
        Select select = new Select(element);
        select.selectByValue(valueAttribute);
    }

    public static String getVisibleAreaScreenshot(WebDriver driver, String screenshotName) {
        String date = new SimpleDateFormat("hh_mm_ss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        // String dest =
        // System.getProperty("user.dir")+"//VisibleViewScreenshots//"+screenshotName+"_"+date+".png";
        String dest = ".//VisibleViewScreenshots//" + screenshotName + "_" + date + ".png";
        File destination = new File(dest);
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest;
    }

    /**
     * match a string to the text of a table cell and return that row.
     *
     * @param table          the table the data is in
     * @param cellTextEquals the text to identify the row e.g. unique ID
     * @param intCellToFind  the column index in which to look for that text
     * @return table row
     */
    protected WebElement getRowFromTable(WebElement table, String cellTextEquals, int intCellToFind) {
        WebElement tableBody = table.findElement(By.tagName("tbody"));
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        for (WebElement row : rows) {
            List<WebElement> td = row.findElements(By.tagName("td"));
            if (td.size() > 0 && td.get(intCellToFind).getText().equals(cellTextEquals)) {
                return row;
            }
        }
        return null;
    }

    /**
     * match a string to the text of a cell and return the text of another specified
     * cell in that row
     *
     * @param table           the table the data is in
     * @param cellTextEquals  the text to identify the row e.g. unique ID
     * @param intCellToFind   the column index in which to look for that text
     * @param intCellToReturn the column index of the cell text you want to return
     * @return text of a specific cell in an html table
     */
    protected String getTextFromTableCell(WebElement table, String cellTextEquals, int intCellToFind,
                                          int intCellToReturn) {
        WebElement row = getRowFromTable(table, cellTextEquals, intCellToFind);
        List<WebElement> td = row.findElements(By.tagName("td"));
        if (td.get(intCellToFind).getText().equals(cellTextEquals)) {
            return td.get(intCellToReturn).getText();
        }
        return null;
    }

    public String getRowsPerPage(String rowCount, WebElement pageDropDown, By totalRowCount)
            throws InterruptedException {

        Select dropdown = new Select(pageDropDown);
        dropdown.selectByValue(rowCount);
        int Count = driver.findElements(totalRowCount).size();
        String value = Integer.toString(Count - 1);
        return value;

    }

    public boolean getPagination(String rowCount, By totalRow, By next, By lastPageNo) throws InterruptedException {

        int pagination = driver.findElements(totalRow).size();
        WebElement NextButton = driver.findElement(next);
        String LastPageNumber = driver.findElement(lastPageNo).getText();
        // check if pagination link exists
        if (pagination > 0) {

            for (int i = 1; i < Integer.parseInt(LastPageNumber); i++) {
                if (rowCount.equals(Integer.toString(pagination)))
                    System.out.println("per page");
                if (NextButton.isEnabled()) {
                    NextButton.click();
                    System.out.println("per page next");

                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean getReversePagination(By previous, By lastPage) throws InterruptedException {
        WebElement prevButton = driver.findElement(previous);
        // String firstPageNumber = driver.findElement(firstPage).getText();
        String LastPageNumber = driver.findElement(lastPage).getText();
        // check if pagination link exists
        if (Integer.parseInt(LastPageNumber) > 0) {
            for (int i = Integer.parseInt(LastPageNumber); i > 0; i--) {
                if (prevButton.isEnabled()) {
                    prevButton.click();

                }
            }
            return true;
        } else
            return false;

    }


    @Step("Assertion Steps message {0} and condition is {1}")
    public void pageAssertion(String message, boolean condition) {
        Assert.assertTrue(condition, message);
    }

    public File getTheNewestFile(String filePath, String ext, By downloadData) {
        WebElement exportData = driver.findElement(downloadData);
        waitUntilLoadedAndElementClickable(downloadData);
        exportData.click();
        driver.findElement(By.xpath("//span[text()='Download']")).click();
        File theNewestFile = null;
        File dir = new File(filePath);
        FileFilter fileFilter = new WildcardFileFilter("*." + ext);
        File[] files = dir.listFiles(fileFilter);

        assert files != null;
        if (files.length > 0) {
            /** The newest file comes first **/
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            theNewestFile = files[0];
        }
        return theNewestFile;
    }

    public List<String[]> readCSVFile(File file) {
        List<String[]> data = new ArrayList<String[]>();
        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader).build();
            data = csvReader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void sleep(Integer ms) {
        ms = ms != null ? ms : 4000;
        try {
            totalWaitTime += ms;
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized static String getDownloadedDocumentName(String downloadDir) {
        File dir = new File(downloadDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile.toString();
    }

    public static void waitForTheFileDownloaded(String filePath, String fileName) throws Exception {
        final int SLEEP_TIME_MILLIS = 1000;
        final int timeout = 60 * SLEEP_TIME_MILLIS;
        int timeElapsed = 0;
        while (timeElapsed < timeout) {
            File file = new File(filePath);
            String[] fileNames = file.list();
            if (file.exists() && fileNames != null) {
                if(fileName.contains(fileNames[0])){
                    sleep(3000);
                    break;
                }
            } else {
                timeElapsed += SLEEP_TIME_MILLIS;
                Thread.sleep(SLEEP_TIME_MILLIS);
            }
        }
    }

    public synchronized File getTheNewestDownloadedFile(String filePath, String ext, By downloadData) throws Exception {
        int noOfFiles, newNoOfFiles;
        pageSync(driver);
        click (downloadData);
        //pageSync(driver);  - Redundant, but keeping.
        noOfFiles = noOfCSVFiles(getDownloadPath());
        pageSync(driver);
        By downloadButton = By.xpath("//button[string()='Download']");
        if(!checkForElementVisibility(downloadButton)){
            click (downloadData);
        }
        click(downloadButton);
        // Continue to check for Spinners
        pageSync(driver);
        int count=0;
        do{
            count++;
            sleep(1000);
            newNoOfFiles = noOfCSVFiles(getDownloadPath());
            if(count>60){
                break;
            }
        }while (newNoOfFiles == noOfFiles);
        String fileName = getDownloadedDocumentName(filePath);

        pageSync(driver);

        File theNewestFile;
        File dir = new File(filePath);
        FileFilter fileFilter = new WildcardFileFilter("*." + ext);
        File[] files = dir.listFiles(fileFilter);

        if (files != null && files.length > 0) {
            for (File file : files) {
                assert fileName != null;
                if (file.getName().contains(fileName)) {
                    theNewestFile = file;
                    Assert.assertTrue (true,"Downloaded File '" + theNewestFile + "'' found in the given path: " + dir); 
                    break;
                }
            }

        } else {
            Assert.fail("Downloaded File not found in the given location: " + dir); 
        }
        System.out.println("The Newest File: " + fileName);
        return new File(fileName);
    }

    public int noOfCSVFiles(String folderPath) {
        File dir = new File(folderPath);
        int count=0;
        try {
            count= Objects.requireNonNull(dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".csv"))).length;
            System.out.println("The number of CSV files in the directory is: " + count);
            return count;
        }catch(Exception e) {
            return count = 0;
        }
    }

    /*
     * Author: Sharanappa Talawar Utility method to wait for element before
     * performing the action
     *
     * @param : locator
     *
     * @return: WebElement
     */
    public WebElement waitForElementToLoad(By locator) {
        WebElement waitElement = null;
        if (driver == null) {
            throwNullPointerExeptionForNullDriver();
        }

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(200)).ignoring(NoSuchElementException.class)
                .ignoring(TimeoutException.class);

        // to check whether element is loaded or not
        try {
            waitElement = wait.until(new Function<WebDriver, WebElement>() {

                @Override
                public WebElement apply(WebDriver driver) {
                    return driver.findElement(locator);
                }
            });
        } catch (Exception e) {
            throw new NoSuchElementException("[ERR] Element with locator " + locator + " is not found.");
        }
        return waitElement;
    }


    public WebElement waitForElementToLoad(WebElement ele) {
        WebElement waitElement = null;
        if (driver == null) {
            throwNullPointerExeptionForNullDriver();
        }

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(200)).ignoring(NoSuchElementException.class)
                .ignoring(TimeoutException.class);

        // to check whether element is loaded or not
        try {
            waitElement = wait.until(new Function<WebDriver, WebElement>() {

                @Override
                public WebElement apply(WebDriver driver) {
                    return ele;
                }
            });
        } catch (Exception e) {
            throw new NoSuchElementException("[ERR] Element " + ele.toString() + " is not found.");
        }
        return waitElement;
    }

    /**
     *
     * Highlights the given web element using the specified color, or a default
     * color if no color is specified.
     *
     * @param driver  The WebDriver instance to use.
     * @param element The web element to highlight.
     * @param color   An optional parameter to specify the color to use for
     *                highlighting the web element. If not provided, a default color
     *                of "#51c5da" is used.
     * @return True if the highlighting was successful, false otherwise.
     * @author Foden Duong
     */
    public boolean highlightOn(WebDriver driver, WebElement element, String color) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            if (color == null || color.isEmpty()) {
                color = "#51c5da";
            }
            js.executeScript("arguments[0].setAttribute('style', 'border: 2px solid " + color + ";');", element);
            return true;
        } catch (Exception e) {
            // ignore error and continue execution
            return false;
        }
    }

    /**
     * Highlights the web element located by the given locator in Yellow.
     *
     * @param driver  The WebDriver instance to use.
     * @param locator The locator string for the web element to highlight.
     * @return True if the highlighting was successful, false otherwise.
     * @author Foden Duong
     */
    private boolean highlightWarn(WebDriver driver, String locator) {
        return highlightOn(driver, locator, "orange"); // Warn
    }

    /**
     * Highlights the web element located by the given locator in Yellow.
     *
     * @param driver  The WebDriver instance to use.
     * @param element The web element to highlight.
     * @return True if the highlighting was successful, false otherwise.
     * @author Foden Duong
     */
    private boolean highlightWarn(WebDriver driver, WebElement element) {
        return highlightOn(driver, element, "orange"); // Warn
    }


    /**
     * Highlights the web element located by the given locator in Yellow.
     *
     * @param driver  The WebDriver instance to use.
     * @param element The web element to highlight.
     * @return True if the highlighting was successful, false otherwise.
     * @author Foden Duong
     */
    private boolean highlightPass(WebDriver driver, WebElement element) {
        return highlightOn(driver, element, "green"); // Warn
    }


    /**
     * Highlights the web element located by the given locator in Yellow.
     *
     * @param driver  The WebDriver instance to use.
     * @param element The web element to highlight.
     * @return True if the highlighting was successful, false otherwise.
     * @author Foden Duong
     */
    private boolean highlightFail(WebDriver driver, WebElement element) {
        return highlightOn(driver, element, "red"); // Warn
    }

    /**
     *
     * Highlights the web element located by the given locator using the specified
     * color, or a default color if no color is specified.
     *
     * @param driver  The WebDriver instance to use.
     * @param locator The locator string for the web element to highlight.
     * @param color   An optional parameter to specify the color to use for
     *                highlighting the web element. If not provided, a default color
     *                of "#52c6da" is used.
     * @return True if the highlighting was successful, false otherwise.
     * @throws IllegalArgumentException if the locator is not a valid xpath or CSS
     *                                  selector.
     * @author Foden Duong
     */
    private boolean highlightOn(WebDriver driver, String locator, String... color) {
        String highlightColor = (color.length > 0) ? color[0] : "#52c6da";
        try {
            By by;
            if (locator.startsWith("//") || locator.startsWith("(")) { // Check if the locator is an xpath
                by = By.xpath(locator);
            } else if (locator.startsWith(".") || locator.startsWith("#")) { // Assume the locator is a CSS selector
                by = By.cssSelector(locator);
            } else { // Throw an exception for unknown locator types
                throw new IllegalArgumentException("Invalid locator type. Must be a valid xpath or CSS selector.");
            }
            WebElement element = driver.findElement(by);
            return highlightOn(driver, element, highlightColor);
        } catch (Exception e) {
            System.out.println("Error occurred while highlighting element: '" + locator + "'  " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * Highlights the given web element using the default color.
     *
     * @param driver  The WebDriver instance to use.
     * @param element The web element to highlight.
     * @return True if the highlighting was successful, false otherwise.
     * @author Foden Duong
     */
    public boolean highlightOn(WebDriver driver, WebElement element) {
        return highlightOn(driver, element, "#52c6da"); // FV Blue
    }

    /**
     *
     * Removes the highlight from the given web element.
     *
     * @param driver  The WebDriver instance to use.
     * @param element The web element to remove the highlight from.
     * @return True if the highlight was successfully removed, false otherwise.
     * @author Foden Duong
     */
    public boolean highlightOff(WebDriver driver, WebElement element) {

        if (!isDocker()) {

            JavascriptExecutor JS = (JavascriptExecutor) driver;
            try {
                JS.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "");
                return true;
            } catch (Exception e) {
                // ignore error and continue execution
                return false;
            }

        }

        return false;

    }

    /**
     * Flashes the given web element using the default color.
     *
     * @param driver  The WebDriver instance to use.
     * @param element The web element to flash.
     * @return True if the flashing was successful, false otherwise.
     */

    public boolean flash(WebDriver driver, WebElement element) {
        return flash(driver, element, "green"); // Green
    }

    /**
     * Flashes the given web element using the specified color, or a default color
     * if no color is specified.
     *
     * @param driver  The WebDriver instance to use.
     * @param element The web element to flash.
     * @param color   An optional parameter to specify the color to use for flashing
     *                the web element. If not provided, a default color of "#51c5da"
     *                is used.
     * @return True if the flashing was successful, false otherwise.
     * @author Foden Duong
     */
    public boolean flash(WebDriver driver, WebElement element, String color) {

        if (!isDocker()) {

            JavascriptExecutor js = (JavascriptExecutor) driver;

            try {

                // Save the current border style and color
                String originalBorderStyle = (String) js.executeScript("return arguments[0].style.borderStyle;",
                        element);
                String originalBorderColor = (String) js.executeScript("return arguments[0].style.borderColor;",
                        element);
                String originalBorderSize = (String) js.executeScript("return arguments[0].style.borderWidth;",
                        element);

                if (color == null || color.isEmpty()) {
                    color = "green";
                }

                js.executeScript("arguments[0].setAttribute('style', 'border: 4px solid " + color + ";');", element);
                Thread.currentThread().sleep(50); // Faster flash
                js.executeScript("arguments[0].setAttribute('style', 'border: " + originalBorderSize + " "
                        + originalBorderStyle + " " + originalBorderColor + ";');", element);
                return true;

            } catch (Exception e) {
                // ignore error and continue execution
                return false;
            }
        }

        return false;

    }

    /**
     * Starts a timer with the specified name. This method should be called before
     * the code that you want to time.
     *
     * @param timerName the name of the timer to start
     */
    public void startTimer(String timerName) {
        startTimes.put(timerName, System.nanoTime());
    }

    /**
     * Stops the timer with the specified name and outputs the elapsed time to the
     * console. This method should be called after the code that you want to time.
     *
     * @param timerName the name of the timer to stop
     */
    public void stopTimer(String timerName) {
        long endTime = System.nanoTime();
        double elapsedSeconds = (endTime - startTimes.get(timerName)) / 1e9;
        cumulativeTimes.put(timerName, cumulativeTimes.getOrDefault(timerName, 0.0) + elapsedSeconds);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callingFunction = stackTrace[2].getMethodName();
        System.out.printf("    Timer '%s': %.1f seconds in %s() function %n", timerName, elapsedSeconds,
                callingFunction);
        System.out.printf("    Cumulative time for '%s': %.1f seconds %n", timerName, cumulativeTimes.get(timerName));
    }

    public String getText(String locator) {
        By byLocator = By.xpath(locator);
        String text = "";
        WebElement element = findElementThatIsPresent(byLocator, 0);
        if (element == null) {
            logErrorForNotFindingElement(byLocator);
            return "";
        }
        try {
            text = element.getText();
        } catch (Exception e) {
            System.out.print("Recover from " + e);
            pageSync(driver);

            text = element.getText();

        }
        /*
         * finally { return text; }
         */
        return text;
    }

    public void pageRefresh() throws InterruptedException {
        driver.navigate().refresh();
        pageSync(driver);
    }

    public void changeImplicitWait(int secondsOrMilliseconds) {
        // Any valur over the default od 50 is interpreted an Milliseconds
        if (secondsOrMilliseconds > 50){
            //milliseconds
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(secondsOrMilliseconds));
        }else{
            // Seconds
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(secondsOrMilliseconds));
        }
    }

    /**
     * waitForStripeLoadingDisappear methods
     * Author: Foden Duong
     * This method waits for a Stripe loading bar to disappear on a webpage.
     * Overloaded to allow the specification of a timeout in either seconds or milliseconds.
     */
    public void waitForStripeLoadingDisappear() {
        waitForStripeLoadingDisappear(5);
    }

    /**
     * waitForStripeLoadingDisappear with custom time limit
     * Author: Foden Duong
     * This method waits for a Stripe loading bar to disappear on a webpage.
     * The method allows for specifying a custom time limit in seconds or milliseconds.
     * @param secondsOrMilliseconds The time limit for the Stripe loading bar to disappear.
     */
    public void waitForStripeLoadingDisappear(int secondsOrMilliseconds) {
        changeImplicitWait(secondsOrMilliseconds); // Milliseconds
        String loadingStripe = "xpath=//div[contains(@class,'loading stripe')]";

        List<WebElement> elements = driver.findElements(getByLocator("css=.stripe-visible"));
        //Allure.addAttachment(elements.toString(), "***********Waiting for Stripe Loading appears*********");
        int count = 0;
        if (elements.size() != 0) {
            do {
                sleep(250);
                //System.out.println("***********Waiting for Stripe Loading disappears*********");
                if (!getElementAttribute(getByLocator(loadingStripe), "class").contains("stripe-visible")) {
                    //System.out.println("***********The Stripe Loading is disappeared*********");
                    Allure.addAttachment(elements.toString(), "*********** The Striped Loading Bar Was Detected *********");
                    break;
                }
                count++;
            } while (count < 100);
        }

        changeImplicitWait(FrameworkConstants.getImplicitwaitTimeout());
    }


    /**
     * Convert String to Date type for comparison Dates by date's conditions
     *
     * @param value      the date in string and shouldn't include text or space
     * @param dateFormat should follow formats (M/d/yyyy or d/M/yyyy or yyyy/M/d)
     */
    public LocalDate parseToDate(String value, String dateFormat) {
        LocalDate date = null;
        if (value != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
            try {
                date = LocalDate.parse(value, dtf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * Wait for number of elements to appear
     *
     * @param locator
     */
    public void waitForNumOfElementsToLoad(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_15.getSeconds()));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, 0));
    }

    public void waitUntilElementChecked(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeEntity.SEC_1.getSeconds()));
        wait.until(ExpectedConditions.elementToBeSelected(element));
    }

    /*
     * Utility method for resource monitoring
     *
     * @author Sunny Neluballi
     */
    private static void startResourceMonitoring() {
        try {
            ProcessBuilder processBuilder;
            if (isWindows()) {
                processBuilder = new ProcessBuilder("tasklist");
            } else {
                processBuilder = new ProcessBuilder("top", "-b");
            }

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while (!Thread.currentThread().isInterrupted() && (line = reader.readLine()) != null) {
                if (line.contains("java") || line.contains("driver"))
                    System.out.println(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    public void expandNavigation() {

        waitPageContentLoaded();
        WebElement sideBarElement = getElement("css=#root aside");

        String attributeValue = sideBarElement.getAttribute("class");

        // Now, classAttributeValue contains the value of the class attribute
        System.out.println("Class Attribute Value: " + attributeValue);

        // click expand navigation button only when left navigation is minimised
        if (attributeValue.contains("sidebar-minimized")) {
            click(By.xpath("//span[@class='faNavClickableCircle']"));
        }
    }

    public void uploadFile(String xpath, String filePath) {
        WebElement fileInput = driver.findElement(By.xpath(xpath));

        // Conditionally set the file detector based on the operating system
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            ((RemoteWebElement) fileInput).setFileDetector(new LocalFileDetector());
        }
        // Upload the file
        fileInput.sendKeys(filePath);
    }

    public void uploadFile(WebElement element, String filePath) {
        // Conditionally set the file detector based on the operating system
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            ((RemoteWebElement) element).setFileDetector(new LocalFileDetector());
        }
        // Upload the file
        element.sendKeys(filePath);
    }

    // this method return table columns
    public List<WebElement> getTableColumns(String xpathTableDiv) {
        WebElement tblHeader = getElement("xpath=" + xpathTableDiv + "//div[@class='thead']");
        return tblHeader.findElements(By.xpath(".//div[@role='columnheader']"));
    }

    // this method return table rows
    public List<WebElement> getTableRows(String xpathTableDiv) {
        WebElement tblRows = getElement("xpath=" + xpathTableDiv + "//div[@role='rowgroup']");
        return tblRows.findElements(By.xpath(".//div[@role='row']"));
    }

    public int getIndexFromList(List<WebElement> listEles, String search) throws Exception {
        for (int i = 0; i < listEles.size(); i++) {
            WebElement ele = listEles.get(i);
            if (ele.getText().contains(search)) {
                return i;
            }
        }
        throw new Exception("[ERR] Text: " + search + " is not found in table");
    }

    private List<WebElement> getRowCells(WebElement row) {
        return row.findElements(By.xpath(".//div[@role='cell']"));
    }

    private WebElement getRowCell(WebElement row, int columnIndex) {
        List<WebElement> rowCell = getRowCells(row);
        return rowCell.get(columnIndex);
    }

    public WebElement findRowByTableColumn(String xpathTableDiv, String searchColumn, String searchUniqueValue)
            throws Exception {
        List<WebElement> tblHeaders = getTableColumns(xpathTableDiv);
        List<WebElement> tblRows = getTableRows(xpathTableDiv);
        int columnFound = getIndexFromList(tblHeaders, searchColumn);

        for (int i = 0; i < tblRows.size(); i++) {
            WebElement row = tblRows.get(i);
            WebElement cell = getRowCell(row, columnFound);
            if (cell.getText().contains(searchUniqueValue)) {
                return row;
            }
        }
        return null;
    }

    // search a value in DIV table column and return selected RowCell
    public WebElement getRowCellByTableColumn(String xpathTableDiv, String searchColumn, String searchUniqueValue,
                                              String cellColum) throws Exception {
        List<WebElement> tblHeaders = getTableColumns(xpathTableDiv);
        int columnCellFound = getIndexFromList(tblHeaders, cellColum);
        WebElement rowFound = findRowByTableColumn(xpathTableDiv, searchColumn, searchUniqueValue);
        if (columnCellFound < 0 || rowFound == null) {
            throw new Exception("Could not find row contains: " + searchUniqueValue);
        }
        return getRowCell(rowFound, columnCellFound);
    }

    private String getDomain(String url) {
        return url.replaceAll("(.com)+(.*)", "");
    }

    public void waitForFileDownloaded() {
        String downloadPath = FileDirectoryUtils.getDownloadPath();
        File dir = new File(downloadPath);
        do{
            sleep(250);
        }while(Arrays.stream(Objects.requireNonNull(dir.listFiles())).findAny().isEmpty());
    }

    protected void waitForElementUnload(By locator) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        try {
            FluentWait<WebDriver> wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(30))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);
            wait.until(ExpectedConditions.numberOfElementsToBe(locator, 0));
            System.out.println("All elements with locator " + locator + " have disappeared");

        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds((FrameworkConstants.getImplicitwaitTimeout())));
    }

    public void waitPageContentLoaded() {
        try {
            By loader = By.xpath("//div[@class='loader' or @class='loading' or @class='circle' or @class='spinner' or ./*[name()='svg' and @data-icon='spinner']] | //button//*[name()='svg' and contains(@class, 'fa-spin')]");
            waitForElementUnload(loader);
        } catch (Exception e) {
            System.out.println("Error when loaded page content " + e.getMessage());
        }
    }

    public String getCellDataByColumnName(String columnName){
        List<WebElement> columns = getListElement("xpath=//div[@class='table']//div[@class='tr headers']//div[@class='header-content']");
        int columnIndex = 0;
        for(int i = 0;i<columns.size();i++){
            if(columns.get(i).getText().equals(columnName)){
                columnIndex = i;
                break;
            }
        }
        return getElementText(getListElement(("xpath=//div[@class='table']//div[@class='tbody']//div[@role='cell']")).get(columnIndex));
    }

    public void waitSpinnerDisappear() {
        try {
            By spinner = By.xpath("//*[local-name()='svg'][@data-icon='spinner']");
            waitForElementUnload(spinner);
        } catch (Exception e) {
            System.out.println("Error when wait spinner unloaded " + e.getMessage());
        }
    }

    public WebElement reloadPageForElement(String xpath) throws Exception {
        if (xpath==null || xpath.trim().length()<1) {
            xpath = "//div[contains(@class,'main-panel')]";
        }
        WebElement ele = null;
        int retry = 0;
        do {
            driver.navigate().refresh();
            waitPageContentLoaded();
            ele = driver.findElement(By.xpath(xpath));
            retry++;
        } while (ele == null && retry<3);
        if (ele == null) {
            throw new Exception("After refresh, couldn't find element " + xpath);
        }
        Allure.addAttachment("Reload Page", "Found Element " + xpath);
        return ele;
    }

    public String getAccessToken() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String)js.executeScript("return window.localStorage.getItem('access_token');");
    }

    public String getActiveOrg() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String)js.executeScript("return window.localStorage.getItem('currentOrganization');");
    }

    public String sendBrowserAPI(String method, String url, String payload) {
        try {
            String token = getAccessToken();
            String org = getActiveOrg();
            String js = "var xhr = new XMLHttpRequest();\n" +
                    "xhr.open('" + method +"', '" + url + "', false);\n" +
                    "xhr.setRequestHeader('content-type', 'application/json');\n" +
                    "xhr.setRequestHeader('Authorization', 'Bearer "+token+"');\n" +
                    "xhr.setRequestHeader('X-Active-Org', '" + org + "');\n" +
                    "xhr.send('" + payload +"');\n" +
                    "return xhr.response";
            return (String)((JavascriptExecutor)driver).executeScript(js);
        } catch (Exception e) {
            throw e;
        }
    }

    public JSONObject parseJSONObject(String data) throws Exception {
        Object obj = new JSONTokener(data).nextValue();
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        } else {
            throw new Exception("Cannot parse JSON Object");
        }
    }

    public JSONArray parseJSONArray(String data) throws Exception {
        Object obj = new JSONTokener(data).nextValue();
        if (obj instanceof JSONObject) {
            throw new Exception("Cannot parse JSON Array");
        } else {
            return (JSONArray) obj;
        }
    }

    public String getValue(JSONObject jsonObject, String k) {
        try{
            Iterator<?> keys = jsonObject.keys();
            while (keys.hasNext()){
                String key = (String)keys.next();
                if (key.equals(k)) {
                    String value = jsonObject.get(key).toString();
                    return value;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public String getValueFromObjKey(String jsonData, String k) throws Exception {
        JSONObject obj = parseJSONObject(jsonData);
        return getValue(obj, k);
    }

    public void refreshPageUntilElementHaveValue(By element, String value) {
        RetryWorker.buildNewRetry().setActionWithAssertTrue(() -> {
            refreshPageOnce();
            WebElement location = getElement(element);
            // return location.getText().equalsIgnoreCase(value);
            return location.getText().toLowerCase().contains(value.toLowerCase());
        }).runWithTimeout(Duration.ofMinutes(3));
    }

    public String removeSpacesAmongWords(String input) {
        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word);
        }
        return result.toString();
    }

    /**
     * Support to get the column number that contains the value of Header Name
     * Support for getting xpath on table faster and flexible
     * @param headerName: a string with the column name
     * @return [<columnNumber>]
     * ex: //div[@role='row'][1]//div[@role='cell'][<columnNumber>]  -  find the location of the first row at the column that has [<columnNumber>]
     */
    public String getColumnNumberXpathByHeaderName(String headerName){
        return "[count(//*[@role='columnheader' and .='"+headerName+"']/preceding-sibling::div)+1]";
    }

    public static void logMessage(String content){
        System.out.println(content);
    }
    public static void logInformation(String content){
        logMessage("\u001B[34m"+content+"\u001B[0m");
    }
    public static void logInformationToReport(String name, String content){
        logMessage("\u001B[34m"+name+": "+content+"\u001B[0m");
        Allure.addAttachment(name, content);
    }
    public static void logPassedMessage(String content){
        logMessage("\u001B[32m"+content+"\u001B[0m");
        Allure.addAttachment("Passed Message", content);
    }
    public static void logFailedMessage(String content){
        logMessage("\u001B[31m"+content+"\u001B[0m");
        Allure.addAttachment("Failed Message", content);
    }
    public static void logWarningMessage(String content){
        logMessage("\u001B[33m"+content+"\u001B[0m");
    }

    /**
     * Support to extract Id from the string that has form "string (id)", "(id) string"
     * @param input: a string with id
     * @return id
     */
    public String extractId(String input) {
        Pattern pattern = Pattern.compile("\\((\\w+-?\\w+)\\)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }  else {
            logWarningMessage("No match found with (<ID>)");
            return input;
        }
    }
}
