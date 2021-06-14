package org.openobservatory.ooniprobe.factory;

import org.openobservatory.ooniprobe.model.database.Url;

import io.bloco.faker.Faker;

public class UrlFactory {

    private static final Faker faker = new Faker();

    public static Url build() {
        Url temp = new Url();

        temp.id = faker.number.positive();
        temp.url = faker.internet.url();
        temp.category_code = "FILE";
        temp.country_code = faker.address.countryCode();

        return temp;
    }

    public static Url createAndSave() {
        Url tempUrl = build();
        tempUrl.save();
        return tempUrl;
    }
}
