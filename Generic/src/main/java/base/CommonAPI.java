package base;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommonAPI {
    public static WebDriver driver;
    /**
     * @param browser  the browser you want to execute your test case
     * @param platform in the operating system you want to execute your test case
     * @return WebDriver Object
     */
    public static WebDriver getLocalDriver(String browser,String platform){
        ChromeOptions options =new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--incognito");
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        if(platform.equalsIgnoreCase("windows")&& browser.equalsIgnoreCase("chrome")){
            System.setProperty("webdriver.chrome.driver","..\\Generic\\src\\main\\resources\\chromedriver1.exe");
            driver=new ChromeDriver(options);
        }else if(platform.equalsIgnoreCase("mac")&& browser.equalsIgnoreCase("chrome")){
            System.setProperty("webdriver.chrome.driver","..\\Generic\\src\\main\\resources\\chromedriver");
            driver= new ChromeDriver(options);
        }else if(platform.equalsIgnoreCase("windows")&& browser.equalsIgnoreCase("firefox")){
            System.setProperty("webdriver.gecko.driver","..\\Generic\\src\\main\\resources\\geckodriver.exe");
            driver= new FirefoxDriver();
        }else if(platform.equalsIgnoreCase("mac")&& browser.equalsIgnoreCase("firefox")){
            System.setProperty("webdriver.gecko.driver","..\\Generic\\src\\main\\resources\\geckodriver");
            driver= new FirefoxDriver();
        }
        driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        return  driver;
    }
    @AfterMethod
    public void cleanUp() {
        driver.close();
        driver.quit();
    }
    public void sleepFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String SAUCE_URL="http://nusrat1995:dbc7b863-b668-4d5e-ab39-34ab595b88ea@ondemand.saucelabs.com:80/wd/hub";
    public static String BROWSERSTACK_URL="";
    public static WebDriver getCloudDriver(String browser,String browserVersion,String platform,
                                           String envName) throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("name","Colud Execution");
        desiredCapabilities.setCapability("browserName",browser);
        desiredCapabilities.setCapability("browser_version",browserVersion);
        desiredCapabilities.setCapability("os",platform);
        desiredCapabilities.setCapability("os_version","Windows 10");

        if(envName.equalsIgnoreCase("saucelabs")){
            desiredCapabilities.setCapability("resolution","1600x1200");
            driver= new RemoteWebDriver(new URL(SAUCE_URL),desiredCapabilities);//in this line we are launching the driver
        }else if(envName.equalsIgnoreCase("browserstack")){
            desiredCapabilities.setCapability("resolution","1024x768");
            driver=new RemoteWebDriver(new URL(BROWSERSTACK_URL),desiredCapabilities);
        }
        return driver;
    }


    /**
     * @param platform       -
     * @param url            -
     * @param browser        -
     * @param cloud          -
     * @param browserVersion -
     * @param envName        -
     * @return
     * @throws MalformedURLException
     * @Parameters - values are coming from the runner.xml file of the project modules
     */
    @Parameters({"platform", "url", "browser", "cloud", "browserVersion", "envName"})
    @BeforeMethod
    public static WebDriver setupDriver(String platform, String url, String browser,
                                        boolean cloud, String browserVersion, String envName) throws MalformedURLException {
        if (cloud) {
            driver = getCloudDriver(browser,browserVersion,platform,envName);
        } else {
            driver = getLocalDriver(browser, platform);
        }
        driver.get(url);
        return driver;
    }
    public static void captureScreenshot(WebDriver driver, String screenshotName) {
        DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy-HH.mm.ss");
        Date date = new Date();
        String uniqueName = dateFormat.format(date);
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(file, new File(System.getProperty("user.dir") + "/screnshots/" + screenshotName + uniqueName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
