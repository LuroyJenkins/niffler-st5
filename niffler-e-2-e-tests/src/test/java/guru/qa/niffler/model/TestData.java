package guru.qa.niffler.model;

import java.util.List;

public record TestData(
        String password,
        List<String> friends,
        List<String> inviteSend,
        List<String> inviteReceived) {
}
