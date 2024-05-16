package guru.qa.niffler.helpers;

import java.util.Date;

import static io.qameta.allure.Allure.*;

public class AllureUtils {

    public static void startTestCase(){
        getLifecycle().updateTestCase(testCase -> testCase.setStart(new Date().getTime()));
    }
}
