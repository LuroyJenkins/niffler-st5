package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UserQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.FriendsPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.SearchPeoplePage;
import guru.qa.niffler.pages.auth.AuthPage;
import guru.qa.niffler.pages.auth.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeOptions;

import static guru.qa.niffler.model.UserState.*;

@WebTest
@ExtendWith(UserQueueExtension.class)
public class UsersQueueExampleTest {

    private final AuthPage authPage = new AuthPage();
    private final LoginPage loginPage = new LoginPage();
    private final MainPage mainPage = new MainPage();
    private final FriendsPage friendsPage = new FriendsPage();
    private final SearchPeoplePage allPeoplePage = new SearchPeoplePage();

    static {
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--incognito");
        Configuration.browserCapabilities = chromeOptions;
        Configuration.pageLoadStrategy = "eager";
    }

    @BeforeEach
    void openLoginForm(){
        authPage.openAuthPage()
                .loginBtnClick();
    }

    @Test
    void receivedFriendshipCheck(@User(WITH_FRIENDS) UserJson user) {
        loginPage.setUsername(user.username())
                .setPassword(user.testData().password())
                .clickSubmitBtn();
        mainPage.header.clickFriendsBtn();
        friendsPage.header.redDotAppearCheck();
        friendsPage.checkReceivedFriendship(user.testData().inviteReceived());
    }

    @Test
    void sendFriendshipCheck(@User(INVITE_SEND) UserJson user) {
        loginPage.setUsername(user.username())
                .setPassword(user.testData().password())
                .clickSubmitBtn();
        mainPage.header.clickAllPeopleBtn();
        allPeoplePage.checkPendingInvitation(user.testData().inviteSend());
    }

    @Test
    void existFriendshipCheck(@User(WITH_FRIENDS) UserJson user) {
        loginPage.setUsername(user.username())
                .setPassword(user.testData().password())
                .clickSubmitBtn();
        mainPage.header.clickFriendsBtn();
        friendsPage.checkExistFriendship(user.testData().friends());
    }
}
