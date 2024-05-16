package guru.qa.niffler.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.pages.components.MainHeader;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FriendsPage {
    private final ElementsCollection friendsRows = $("table.table tbody").$$("tr");
    private final String submitInvitationBtnLocator = "[data-tooltip-id=submit-invitation]";
    private final String declineInvitationBtnLocator = "[data-tooltip-id=decline-invitation]";
    private final String removeFriendBtnLocator = "[data-tooltip-id=remove-friend]";
    private final String EXIST_FRIENDSHIP_TEXT = "You are friends";

    public final MainHeader header = new MainHeader();

    public void checkReceivedFriendship(List<String> usernames) {
        ElementsCollection friendshipRequestsRows = friendsRows.filterBy(not(have(text(EXIST_FRIENDSHIP_TEXT))));
        assertThat("Количество заявок не совпадает", friendshipRequestsRows.size(), equalTo(usernames.size()));
        for (String username : usernames) {
            SelenideElement invitationRow = friendsRows.findBy(text(username));
            invitationRow.scrollIntoView(false);
            assertThat("Кнопка принять предложение не отображается для - " + username,
                    invitationRow.$(submitInvitationBtnLocator).isDisplayed());
            assertThat("Кнопка отклонить предложение не отображается для - " + username,
                    invitationRow.$(declineInvitationBtnLocator).isDisplayed());
            assertThat("Кнопка удалить из друзей отображается для - " + username,
                    !invitationRow.$(removeFriendBtnLocator).isDisplayed());
            assertThat("Надпись '" + EXIST_FRIENDSHIP_TEXT + "' отображается для - " + username,
                    !invitationRow.$(byText(EXIST_FRIENDSHIP_TEXT)).isDisplayed());
        }
    }

    public void checkExistFriendship(List<String> friends) {
        for (String friend : friends) {
            SelenideElement friendRow = friendsRows.findBy(text(friend));
            friendRow.scrollIntoView(false);
            assertThat("Кнопка принять предложение отображается для - " + friend,
                    !friendRow.$(submitInvitationBtnLocator).isDisplayed());
            assertThat("Кнопка отклонить предложение отображается для - " + friend,
                    !friendRow.$(declineInvitationBtnLocator).isDisplayed());
            assertThat("Кнопка удалить из друзей не отображается для - " + friend,
                    friendRow.$(removeFriendBtnLocator).isDisplayed());
            assertThat("Надпись '" + EXIST_FRIENDSHIP_TEXT + "' не отображается для - " + friend,
                    friendRow.$(byText(EXIST_FRIENDSHIP_TEXT)).isDisplayed());
        }
    }
}
