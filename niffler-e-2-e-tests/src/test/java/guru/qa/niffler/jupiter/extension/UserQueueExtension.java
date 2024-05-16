package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.helpers.AllureUtils;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserState;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import static guru.qa.niffler.model.UserJson.simpleUser;

public class UserQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UserQueueExtension.class);

    private static final Queue<UserJson> COMMON_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<UserJson> INVITE_SEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<UserJson> INVITE_RECEIVED_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<UserJson> USERS_WITH_FRIENDS = new ConcurrentLinkedQueue<>();

    static {
        COMMON_USERS.add(simpleUser("nikita", "12345", null, null, null));
        COMMON_USERS.add(simpleUser("duck", "12345", null, null, null));
        COMMON_USERS.add(simpleUser("barsik", "12345", null, null, null));

        INVITE_SEND_USERS.add(simpleUser("john", "12345",
                null, List.of("miron", "michael", "noah"), null));
        INVITE_SEND_USERS.add(simpleUser("mike", "12345",
                null, List.of("michael"), null));
        INVITE_SEND_USERS.add(simpleUser("nicola", "12345",
                null, List.of("michael", "noah"), null));

        INVITE_RECEIVED_USERS.add(simpleUser("miron", "12345",
                null, null, List.of("john")));
        INVITE_RECEIVED_USERS.add(simpleUser("michael", "12345",
                null, null, List.of("john", "mike", "nicola")));
        INVITE_RECEIVED_USERS.add(simpleUser("noah", "12345",
                null, null, List.of("john", "nicola")));

        USERS_WITH_FRIENDS.add(simpleUser("troy", "12345", List.of("bob", "pol"),
                null, null));
        USERS_WITH_FRIENDS.add(simpleUser("bob", "12345", List.of("troy", "pol"),
                null, null));
        USERS_WITH_FRIENDS.add(simpleUser("pol", "12345", List.of("troy"),
                null, null));
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        List<Method> beforeEachMethods = Arrays.stream(extensionContext.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .toList();

        List<UserState> userStatesFromBeforeEach = beforeEachMethods.stream()
                .flatMap(method -> Arrays.stream(method.getParameters())
                        .filter(parameter -> parameter.isAnnotationPresent(User.class))
                        .map(parameter -> parameter.getAnnotation(User.class).value()))
                .toList();

        List<UserState> userStates = new ArrayList<>(Arrays.stream(extensionContext.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, User.class))
                .map(p -> p.getAnnotation(User.class).value()).toList());

        userStates.addAll(userStatesFromBeforeEach);

        Map<UserState, List<UserJson>> usersByState = userStates.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.mapping(this::getCustomUser, Collectors.toList())));

        AllureUtils.startTestCase();
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), usersByState);
    }

    private UserJson getCustomUser(UserState state) {
        return switch (state) {
            case COMMON -> waitUserFromQueue(COMMON_USERS);
            case INVITE_SEND -> waitUserFromQueue(INVITE_SEND_USERS);
            case INVITE_RECEIVED -> waitUserFromQueue(INVITE_RECEIVED_USERS);
            case WITH_FRIENDS -> waitUserFromQueue(USERS_WITH_FRIENDS);
        };
    }

    private synchronized UserJson waitUserFromQueue(Queue<UserJson> usersQueue) {
        while (Optional.ofNullable(usersQueue.peek()).isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return usersQueue.poll();
    }

    private synchronized void putCustomUserIntoQueue(UserState state, UserJson user) {
        switch (state) {
            case COMMON -> COMMON_USERS.add(user);
            case INVITE_SEND -> INVITE_SEND_USERS.add(user);
            case INVITE_RECEIVED -> INVITE_RECEIVED_USERS.add(user);
            case WITH_FRIENDS -> USERS_WITH_FRIENDS.add(user);
        }
        notify();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Map<UserState, List<UserJson>> usersMap = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        for (Map.Entry<UserState, List<UserJson>> entry : usersMap.entrySet()) {
            UserState state = entry.getKey();
            List<UserJson> usersList = entry.getValue();

            for (UserJson user : usersList) {
                putCustomUserIntoQueue(state, user);
            }
        }
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext
                .getParameter()
                .getType()
                .isAssignableFrom(UserJson.class)
                && parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Optional<User> annotation = AnnotationSupport.findAnnotation(parameterContext.getParameter(), User.class);
        UserState state = annotation.orElseThrow().value();

        Map<UserState, List<UserJson>> users = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class);

        return users.get(state).getFirst();
    }
}
