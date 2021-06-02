package org.openobservatory.ooniprobe.factory;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import io.bloco.faker.Faker;

public class JsonResultFactory {

    static Faker faker = new Faker();
    static Gson gson = new Gson();

    public static JsonResult build(AbstractTest testType, boolean successTestKeys) {
        JsonResult temp = new JsonResult();

        temp.test_start_time = faker.date.backward();
        temp.measurement_start_time = faker.date.backward();
        temp.test_runtime = (double) faker.number.positive();

        TestKeys testKeys = null;

        if (successTestKeys) {
            testKeys = gson.fromJson(TestKeyFactory.getAccessibleStringFrom(testType), TestKeys.class);
        } else {
            testKeys = gson.fromJson(TestKeyFactory.getBlockedStringFrom(testType), TestKeys.class);
        }

        temp.test_keys = testKeys;

        return temp;
    }
}
