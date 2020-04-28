package com.java.test.suite;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.java.test.helper.HelperClass;
import com.java.test.model.WatchPage;
 
public class AppTest {

    //Define a global instace for driver to be accessible through all the tests
    public static WebDriver driver = null; 
    // Import Helper class which has all the basic methods implemented
    public HelperClass helper = new HelperClass();

    // Set up method to instantiate the driver and get the home page via URL
    @BeforeClass
    public static void setUp() {
        // Set the driver to the chromedriver to control the automation of the chrome browser
		System.setProperty("webdriver.chrome.driver","src\\test\\resources\\drivers\\chromedriver.exe");
        driver = new ChromeDriver();
 
        driver.get("https://www.ebay.com/b/Rolex-Wristwatches/31387/bn_2989578");
        // Add implicit wait between all the UI operations
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    // Test to verify that the logo is visible on the first page
    @Test
    public void verifyPageContainsEbayLogo() {
        //Get the ebay logo using CSS locator
        By cssLocator = By.cssSelector("img[alt='eBay Logo'");
        // Check the isDisplayed value from the DOM
        boolean isLogoDisplayed = helper.verifyLogo(driver, cssLocator);

        assertEquals(isLogoDisplayed, true, "Logo is not displayed.");
    }
    
    // Test to keep track of the top 5 most viewed watches
    @Test
    public void getTopFiveWatchedWatches() {
        // Keep track of watch pages checked
        int totalWatchesChecked = 0;
        // Number of watch pages that will be checked during the run
        int watchesToCheck = 0;
        //Keep track of most viewed watches
        List<WatchPage> mostViewedWatches = new ArrayList<>(); 

        while (totalWatchesChecked < 250) {
            // Get the list of all the watches displayed on the UI
            By totalWatchesLocator = By.cssSelector(".b-list__items_nofooter > li"); 
            helper.addWaitForLocation(driver, totalWatchesLocator, 30);
            List<WebElement> watches = driver.findElements(totalWatchesLocator);

            // As the upper limit is 250 watches to check, we make sure when we are about to cross the threshold, we only check remaining watches left from 250
            watchesToCheck = (totalWatchesChecked + watches.size() > 250) ? 250 - totalWatchesChecked : watches.size();
            totalWatchesChecked += watchesToCheck;
            // Iterate over all the watch pages on the listing page
            mostViewedWatches = helper.iterateWatchPages(driver, watchesToCheck, watches, mostViewedWatches);

            System.out.println("Watches checked: " + totalWatchesChecked);
            // Get the next button link using css selector to cycle through pages 
            By nextButtonLocator = By.cssSelector("nav > a[rel='next']");
            boolean nextButtonClicked = helper.clickOnButton(driver, nextButtonLocator);
            if(!nextButtonClicked) {
                // If there are no new pages to process then exit the loop
                System.out.println("No new page for processing...exiting!");
                break;
            }
        }

        // Print the top 5 most viewed watches 
        System.out.println("Top 5 Most viewed watches:");
        for (WatchPage watch : mostViewedWatches) {
            System.out.printf("Watch URL: %s \nViews Received: %d\n", watch.getUri(), watch.getWatchCount());
        }
    }
    
    // Tear down method to exit resources like the browser and for other clean up operations
    @AfterClass
    public static void tearDown() {
        // Step Close Driver
        driver.close();
        //Step Quit Driver
        driver.quit();
    }
}
