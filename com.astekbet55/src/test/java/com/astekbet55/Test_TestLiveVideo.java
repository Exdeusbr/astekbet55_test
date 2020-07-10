package com.astekbet55;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.*;

import java.util.List;

public class Test_TestLiveVideo extends Common {
    boolean leftPanelIcon;
    boolean centerPanelIcon;
    boolean gamePageIcon;
    int withVideo = 0;
    int withProblems = 0;
    int withoutVideo = 0;

    int activeScreenSize = 0;

    @Test
    public void TestLiveVideo() {
        String gameId;
        String gameHref;

        driver.get("https://astekbet55.com/");

        // Закрываем окно с предложением подписаться, если вылезло
        try{
            driver.findElement(By.cssSelector(".pf-subs-btn-link")).click();
        }catch (NoSuchElementException ex){}
        // Согласно ТЗ "наличии Live-игр в чемпионате проверяет наличие в нем видео" - переходим в Live
        driver.findElement(By.cssSelector("#live_href")).click();
        // Определяем верхнюю границу видимости для скроллинга левой панели
        WebElement topElement = driver.findElement(By.id("header"));
        activeScreenSize = activeScreenSizeTop(topElement);

        // Перебирать будем "Топ5". Во всяком случае, именно он на скриншоте в ТЗ
        WebElement scrollIndicator = driver.findElement(By.cssSelector("div.left_menuEventCon div.iScrollIndicator"));

        int scrollPx = (driver.findElement(By.cssSelector("div.assideCon_body.top5 li")).getLocation().getY()) - activeScreenSize - 50; //Определяем насколько нужно поднять
        scrollPanel(driver, driver.findElement(By.cssSelector("div.assideCon_body.top5 li")), driver.findElement(By.cssSelector("div.iScrollIndicator")),scrollPx);

        List<WebElement> top5 = driver.findElements(By.cssSelector("div.assideCon_body.top5 li"));
        for (WebElement sport : top5)
        try{
            if(sport.getLocation().getY() > driver.manage().window().getSize().height - 150) {                          // Проверяем положение элемента относительно нижнего края, при необходимости скроллим
                scrollPanel(driver, sport, scrollIndicator ,sport.getLocation().getY() - activeScreenSize);
            }
            sport.click();
            List<WebElement> ligas_menu = sport.findElements(By.cssSelector(".liga_menu li"));
            for(WebElement liga : ligas_menu){
                if(liga.getLocation().getY() > driver.manage().window().getSize().height - 250) {
                    scrollPanel(driver, liga, scrollIndicator ,liga.getLocation().getY() - activeScreenSize);
                }
                if(liga.getLocation().getY() < activeScreenSize) {
                    scrollPanel(driver, liga, scrollIndicator ,-(liga.getLocation().getY() + activeScreenSize));
                }
                sleepSecond(1);
                liga.findElement(By.cssSelector(".strelochka.arr_open")).click();
                // sleepSecond(1);
                List<WebElement> events = liga.findElements(By.cssSelector(".event_menu li"));
                for (WebElement event : events) {
                    if(event.getLocation().getY() > driver.manage().window().getSize().height - 250) {
                        scrollPanel(driver, event, scrollIndicator ,event.getLocation().getY() - activeScreenSize);
                    }
                    if(event.getLocation().getY() < activeScreenSize) {
                        scrollPanel(driver, event, scrollIndicator ,-(event.getLocation().getY() + activeScreenSize));
                    }
                    // Если иконка видеотрансляции обнаружена у элемента на левой панели, проверяем остальные
                    if (isElementPresent(event, By.cssSelector("div.ls-game__action.ls-game__action_video"))) {
                        gameId = event.findElement(By.cssSelector("[data-game]")).getAttribute("data-game"); // Запоминаем ID игры для поиска игры в центральное панели и на странице
                        gameHref = event.findElement(By.cssSelector("[href]")).getAttribute("href");         // Запоминаем ссылку на игру для проверки и вывода в случае, ели найдём не все иконки, чтобы быстро можно было проверить вручную.
                        centerPanelIcon = isElementPresent(driver, By.cssSelector("div.c-events__ico_video[data-gameId='"+gameId+"']"), 10); // Проверяем наличие иконки в центральной панели.
                        event.click();
                        driver.findElement(By.id(gameId)); // Дожидаемся загрузки страницы с игрой
                        gamePageIcon = isElementPresent(driver, By.cssSelector("#hottest_games .link.ico2"), 10); // Проверяем наличие иконки на страницу игры
                        Assert.assertEquals(driver.getCurrentUrl(),gameHref); // Можно проверить адрес текущей страницы.
                        //  sleepSecond(1);
                        if(event.findElement(By.cssSelector(".link.ls-game")).getLocation().getY() > driver.manage().window().getSize().height - 150) {
                            scrollPanel(driver, event.findElement(By.cssSelector(".link.ls-game")), scrollIndicator, event.getLocation().getY() - activeScreenSize);
                        }
                        event.findElement(By.cssSelector(".link.ls-game")).click();
                        leftPanelIcon = true;
                        if(leftPanelIcon && centerPanelIcon && gamePageIcon) {  // Найдены все иконки
                            withVideo++;
                        }
                        else {
                            System.out.println("Не все иконки обнаружены Id: " + gameId + " " + gameHref); // Если нашли не все иконки, выводим Id и ссылку на игру,
                            System.out.println("На левой панели: " + leftPanelIcon + " В центре: " + centerPanelIcon + " На странице игры: " + gamePageIcon);
                            withProblems++;
                        }
                    }
                    else
                        withoutVideo++;
                    }
                try {
                    if(liga.findElement(By.cssSelector(".strelochka.arr_open")).getLocation().getY() < activeScreenSize) {
                        scrollPanel(driver, liga, scrollIndicator ,-(liga.getLocation().getY() + activeScreenSize + 150));
                    }
                    liga.findElement(By.cssSelector(".strelochka.arr_open")).click();
                }
                catch (ElementNotInteractableException ex){
                                   }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
            catch(StaleElementReferenceException ex){   // Если элемен в списке изменился, считаем, что обновился список. Выходим.
                ex.printStackTrace();
                System.out.println("Список игр обновился");
            break;
            }
        // Выводим результат
        System.out.println("Найдено LIVE-игр с трансляциями: " + withVideo);
        System.out.println("из них не все иконки обнаружены у: " + withProblems);
        System.out.println("Найдено LIVE-игр без трансляций: " + withoutVideo);
    }
}
