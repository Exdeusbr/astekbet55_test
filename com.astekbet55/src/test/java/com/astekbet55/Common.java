package com.astekbet55;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;


import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class Common {
    public WebDriver driver;

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    public void sleepSecond(int x) {
        try {
            sleep(x*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

// Проверка наличия элемента
    boolean isElementPresent(WebDriver driver, By locator){
        driver.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS); // Для ускоренного поиска отключаем ожидание
        int size = driver.findElements(locator).size();
        driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
        return size>0;
    }
// Перегружаем метод для случая, когда нужно подождать
    boolean isElementPresent(WebDriver driver, By locator, int wait){
        driver.manage().timeouts().implicitlyWait(wait,TimeUnit.SECONDS);
        int size = driver.findElements(locator).size();
        driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
        return size>0;
    }
// Перегружаем метод для случая, когда будем искать внутри родительского элемента
    boolean isElementPresent(WebElement parent, By locator){
            driver.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS);
            int size = parent.findElements(locator).size();
            driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
            return size>0;
    }
// Для поиска в родителе с ожиданием
    boolean isElementPresent(WebElement parent, By locator, int wait){
        driver.manage().timeouts().implicitlyWait(wait,TimeUnit.SECONDS);
        int size = parent.findElements(locator).size();
        driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
        return size>0;
    }
// Определяем вержнюю границу видимого экрана
    public int activeScreenSizeTop(WebElement topElement){
        return  topElement.getLocation().getY() + topElement.getSize().height;

    }
// Прокручиваем панель через Actions, двигая ползунок скролла
    public void scrollPanel(WebDriver driver, WebElement element, WebElement scrollIndicator, int scrollPx)
    {
        int scrollPoints = 0;
        int scrollCoeficient = getScrollCoefficient(driver, scrollIndicator, element);
        if(scrollPx >= 0) {
            scrollPoints = ((scrollPx - element.getSize().height) / scrollCoeficient);
        }
        else{
            scrollPoints = ((scrollPx) / scrollCoeficient);
        }
            try {
                Actions dragger = new Actions(driver);
                dragger.moveToElement(scrollIndicator).clickAndHold().moveByOffset(0, scrollPoints).release(scrollIndicator).build().perform();
                sleepSecond(1);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }
// Экспериментально вычисляем коеффициент смещения элемента при перемещении ползунка
    public int getScrollCoefficient(WebDriver driver, WebElement scrollIndicator, WebElement testelement)
    {
        int y = testelement.getLocation().getY();
        int y1;
        int coef;
        try
        {
            Actions dragger = new Actions(driver);
            dragger.moveToElement(scrollIndicator).clickAndHold().moveByOffset(0, 100).release(scrollIndicator).build().perform();
            y1 = testelement.getLocation().getY();
            dragger.moveToElement(scrollIndicator).clickAndHold().moveByOffset(0, -100).release(scrollIndicator).build().perform();
            coef = (int)Math.round(((double)(y)-y1)/100);
            return coef==0?5:coef; // такое среднее значение было на практике
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 5; // такое среднее значение было на практике
        }
    }

    @After
    public void close(){
        driver.quit();
        driver = null;
    }
}
