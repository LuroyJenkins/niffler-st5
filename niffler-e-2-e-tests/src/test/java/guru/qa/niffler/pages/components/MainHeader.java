package guru.qa.niffler.pages.components;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class MainHeader {
    private final SelenideElement friendsBtn = $("[data-tooltip-id=friends]");
    private final SelenideElement allPeopleBtn = $("[data-tooltip-id=people]");
    private final SelenideElement notificationDot = friendsBtn.$(".header__sign");

    public void clickFriendsBtn() {
        friendsBtn.click();
    }

    public void clickAllPeopleBtn() {
        allPeopleBtn.click();
    }

    public void redDotAppearCheck(){
        notificationDot.shouldBe(visible);
    }
}
