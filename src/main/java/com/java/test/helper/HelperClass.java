package com.java.test.helper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.java.test.model.WatchPage;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HelperClass {

    public boolean verifyLogo(WebDriver driver, By locator) {
        // Helper method which waits for 5 seconds for logo to be present in DOM, and return its isDisplayed property
        addWaitForLocation(driver, locator, 5);
        WebElement element = driver.findElement(locator);
        return element.isDisplayed();
    }

    public void addWaitForVisibility(WebDriver driver, By locator, int seconds) {
        // Helper method which add wait of desired seconds to wait for the element to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void addWaitForLocation(WebDriver driver, By locator, int seconds) {
        // Helper method which add wait of desired seconds to wait for the element to be located inside DOM
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public List<WatchPage> iterateWatchPages(WebDriver driver, int watchesToCheck, List<WebElement> watches, List<WatchPage> mostViewedWatches) {
        for(int i = 0; i < watchesToCheck; i++) {
            
            // Wait for 30 seconds for the element to be present on the page
            By watchLinkLocator = By.cssSelector(".s-item__link");
            addWaitForLocation(driver, watchLinkLocator, 30);
            // Find the anchor tag which has the link inside the list elements
            WebElement anchorElement = watches.get(i).findElement(watchLinkLocator);
            // Grab the URL for the watch page
            String watchPageUri = anchorElement.getAttribute("href");

            // Open the page in a new tab using JavaScript as this helps to maintain the original list and element references on the original page
            ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", watchPageUri);
            // Get the list of Window Handles (Tabs opened) to easily switch between them
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            // Switch to the newly opened tab
            driver.switchTo().window(tabs.get(1));
            
            By viewPerHourLocator;
            /* Observed during testing, some watch pages have an older html structure whereas some pages have a newer structure, because of this
                the view per hour element has the classes, xpath and id different, URL having the /itm/ in the endpoint is a good differentiating factor */ 
            if(watchPageUri.contains("/itm/")) {
                // Get the view per page on the watch page using the css selector
                viewPerHourLocator = By.cssSelector("#vi_notification_new > span");  
            }
            else {
                viewPerHourLocator = By.cssSelector(".item-image-wrapper > .banner-status");
            }

            Integer views = getViewPerHour(driver, viewPerHourLocator);
            // Store all the necessary details in an Object to quickly retrieve the required values
            WatchPage page = new WatchPage(driver.getTitle(), watchPageUri, views);

            System.out.println("URL to the Watch Page: " + page.getUri() + "\nNumber of views: " + page.getWatchCount());
           
            if(views != 0) {
                // Add the pages with views per hour to the list
                mostViewedWatches = topFiveViewed(mostViewedWatches, page);
            }

            //Once done, close the watch page tab
            driver.close();
            // Switch back to the original listing page
            driver.switchTo().window(tabs.get(0));
        }
        return mostViewedWatches;
    }

    public Integer getViewPerHour(WebDriver driver, By locator) {
        try {
            // Helper method which waits for 3 seconds for the view per hour to be visible on the UI and parses the content and get the Integer value
            addWaitForVisibility(driver, locator, 3);
            String viewPerHourText = driver.findElement(locator).getText();

            if(!viewPerHourText.equals("")) {
                return Integer.parseInt(viewPerHourText.split(" ")[0]);
            }
        } 
        catch (Exception e) {
            // For pages that do not have views per hour would end up in this catch block
            System.out.println("No views per hour found on this page");
        }
        return 0;
    }

    public boolean clickOnButton(WebDriver driver, By locator) {
        try {
            // Gets the desired button or link and clicks on it and returns true indicating that the operation was successful
            WebElement link = driver.findElement(locator);
            link.click();
            return true;
        }
        catch (Exception e) {
            // if the button or link is not found then it returns false indicating that the operation was not successful 
            return false;
        }
    }

    public List<WatchPage> topFiveViewed(List<WatchPage> list, WatchPage watchPage) {
        // Keeps track of the top 5 most viewed watches per hour
        if(list.size() < 5) {
            // If list size is less than 5 then just add it blindly
            list.add(watchPage);
        }
        else {
            // Get the index of the smallest item, for this, a utility class is being used to get the min value
            int smallestIndex = list.indexOf(Collections.min(list));
            // Check whether the watch page has more views than the watch page which has the least views in the list
            if(list.get(smallestIndex).getWatchCount() < watchPage.getWatchCount()) {
                // If true, then replace the watch page with the least value with the one which has a higher view count
                list.set(smallestIndex, watchPage);
            }
        }
        return list;
    }
}