package org.openobservatory.ooniprobe.factory;

import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;

import io.bloco.faker.Faker;

public class NetworkFactory {

    private static final Faker faker = new Faker();

    public static Network build() {
        Network temp = new Network();

        temp.id = faker.number.positive();
        temp.asn = faker.internet.domainWord();
        temp.ip = faker.internet.ipV4Address();
        temp.network_name = faker.internet.domainName();
        temp.network_type = faker.internet.domainSuffix();
        temp.country_code =  faker.address.countryCode();

        return temp;
    }

    public static Network createAndSave(Result result) {
        Network tempNetwork = build();
        result.network = tempNetwork;
        tempNetwork.save();
        return  tempNetwork;
    }
}
