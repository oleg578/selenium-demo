package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.logging.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String parameter = args[0];
        WebDriver driver = null;
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.SEVERE);

        Handler handler = new FileHandler("./log/parser.log");
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
        try {
            driver = new FirefoxDriver();
            driver.get(parameter);

            // try to load and build the DOM in a period of 10 seconds
            // these code is useless in current situation really
            // just it is a warranty that DOM is built - for Selenium, of course
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(d -> ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));

            // We are ready to get all links from page and write them into file
            // without this magic we will get exception with a stale links element
            // one of the next magic resolve this obstacle :)

            // maximize window
            //driver.manage().window().maximize();

            // or set window size 1 pixel
            //driver.manage().window().setSize(new Dimension(1, 1));

            List<WebElement> links = driver.findElements(By.tagName("a"));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./log/links.txt"))) {
                links.forEach(link -> {
                    String linkText = link.getText().isEmpty()
                            ? "no value ..."
                            : link.getText().replaceAll("\\n"," ").trim();
                    if (link.getText().trim().length() > 29) {
                        linkText = link.getText().substring(0, 29) + "...";
                    }
                    String logRow = String.format("%-32s: %s\n", linkText, link.getAttribute("href"));
                    try {
                        writer.write(logRow);
                    } catch (IOException e) {
                        logger.severe(e.getMessage());
                    }
                });
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            assert driver != null;
            driver.quit();
        }
    }
}