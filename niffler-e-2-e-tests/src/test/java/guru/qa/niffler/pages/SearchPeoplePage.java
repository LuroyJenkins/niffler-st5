package guru.qa.niffler.pages;

import com.codeborne.selenide.*;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.hamcrest.MatcherAssert.assertThat;

public class SearchPeoplePage {
    private final ElementsCollection peopleRows = $("table.table tbody").$$("tr");
    private final String addFriendBtnLocator = "[data-tooltip-id=add-friend]";
    private final String removeFriendBtnLocator = "[data-tooltip-id=remove-friend]";
    private final String PENDING_INVITATION_TEXT = "Pending invitation";

    public void checkPendingInvitation(List<String> inviteSend) {
        ElementsCollection sendRequestRow = peopleRows.filterBy(text(PENDING_INVITATION_TEXT));
        for (String invitation : inviteSend) {
            SelenideElement invitationRow = sendRequestRow.findBy(text(invitation));
            invitationRow.scrollIntoView(false);
            assertThat("Кнопка добавить друга отображается для - " + invitation,
                    !invitationRow.$(addFriendBtnLocator).isDisplayed());
            assertThat("Кнопка удалить друга отображается для - " + invitation,
                    !invitationRow.$(removeFriendBtnLocator).isDisplayed());
            assertThat("Ожидание подтверждения не отображается для - " + invitation,
                   invitationRow.$(byText(PENDING_INVITATION_TEXT)).isDisplayed());
        }
    }

}
