package com.fireblaze.evento;

import org.json.JSONArray;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

    }
    @Test
    public void test_notify() throws Exception{
        SendNotificationActivity activity = new SendNotificationActivity();
        JSONArray array = new JSONArray();
        array.put("dHMyIXt5PSg:APA91bEqrxTtaN5lqs76j2LP764F659yFJx0c_Qivx1idSgIs3Ddvrss2YwzroeL5NOLdjgeyzBoM_8bshsCm1NC_9cAx7MAsFs54yb7qanypc_024t0eTT4FDhd1hLa7eIp2ZFFzxKh");
        activity.pushFCMNotification(array);
    }

}