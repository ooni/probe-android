package org.openobservatory.ooniprobe.factory;

import android.content.Context;

import org.apache.commons.io.FileUtils;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import io.bloco.faker.Faker;

public class MeasurementFactory {

    private static final Faker faker = new Faker();

    public static Measurement build(
            AbstractTest testType,
            Result result,
            Url url,
            boolean wasBlocked,
            boolean wasUploaded
    ) {

        Measurement temp = new Measurement();

        temp.id = faker.number.positive();
        temp.result = result;
        temp.is_done = true;
        temp.is_uploaded = wasUploaded;
        temp.is_failed = false;
        temp.is_upload_failed = false;
        temp.is_rerun = false;
        temp.is_anomaly = wasBlocked;
        temp.start_time = faker.date.forward();
        temp.test_name = testType.getName();
        temp.report_id = wasUploaded ? String.valueOf(result.id) : null;
        temp.test_keys = getTestKeyFrom(testType, wasBlocked);
        temp.runtime = faker.number.positive();
        temp.url = url;

        return temp;
    }

    private static String getTestKeyFrom(AbstractTest testType, boolean hasFailed) {
        if (hasFailed) {
            return getBlockedTestKeyFrom(testType);
        }

        return getAccessibleTestKeyFrom(testType);
    }

    private static String getAccessibleTestKeyFrom(AbstractTest testType) {
        String result;
        switch (testType.getName()) {
            // Instant Messaging
            case "whatsapp":
                return "{\"registration_server_status\":\"ok\",\"whatsapp_endpoints_status\":\"ok\",\"whatsapp_web_status\":\"ok\"}";

            case "telegram":
                return "{\"telegram_http_blocking\":\"false\",\"telegram_tcp_blocking\":\"false\",\"telegram_web_status\":\"ok\"}";

            case "facebook_messenger":
                return "{\"facebook_tcp_blocking\":\"false\",\"facebook_dns_blocking\":\"ok\"}";

            case "signal":
                return "{\"signal_backend_status\":\"ok\",\"signal_backend_failure\":\"ok\"}";

            // Circumvention
            case "psiphon":
                return "{\"bootstrap_time\":" + faker.number.positive(5.0, 100.0) + "}";

            case "tor":
                return "{\"dir_port_accessible\":7,\"dir_port_total\":10,\"obfs4_accessible\":14,\"obfs4_total\":15,\"or_port_accessible\":0,\"or_port_dirauth_accessible\":10,\"or_port_dirauth_total\":10,\"or_port_total\":0}";

            case "riseupvpn":
                return "{\"ca_cert_status\":true,\"transport_status\":{\"obfs4\":\"ok\",\"openvpn\":\"ok\"}}";

            // Performance
            case "ndt":
                return "{\"protocol\":7,\"server\":{\"hostname\":\"ndt-mlab2-lis02.mlab-oti.measurement-lab.org\",\"site\":\"lis02\"},\"server_country\":\"PT\",\"server_name\":\"lis02\",\"summary\":{\"avg_rtt\":14.444,\"download\":23186.37200985908,\"max_rtt\":92.873,\"min_rtt\":12.606,\"mss\":1448.0,\"ping\":12.606,\"retransmit_rate\":0.06652086351792866,\"upload\":18466.777738433746}}";

            case "dash":
                return "{\"server\":{\"hostname\":\"neubot-mlab-mlab2-lis02.mlab-oti.measurement-lab.org\",\"site\":\"lis02\"},\"simple\":{\"median_bitrate\":27244.0,\"min_playout_delay\":0.08821899999999872}}";

            case "http_invalid_request_line":
                return "{\"received\":[\"GET cache_object://localhost/ HTTP/1.0\\n\\r\",\"LIHS / HTTP/1.1\\n\\r\",\"GET / HTTP/AGW\\r\\n\",\"ISYIFXVXWFCXCNHMWXONZWOHJYNOVUJXGQMEOGUDUKKVRLRXOEJXZPVPENRELTTWFCMNTBKXXFKSDKIYLXIZZSBUFUHIQBAPFYRFVRAGMUIETRUMPLGRBTGUINIVFDTPVNRHBDALGZWQKCMEPJCOHKTIGVOLDJBBQKCLBKGUDYZKFSZOJLTJDEINCGRXTZHHTZNZHBTOLPREOLOZDTDPHRYISMIXAPTEKSIXIIHDICFMAEVRCDZXAGXHGCKAOWKHVQKKSDVXTYUJJGDECXOXMLXVLCRRPBJEMICYPTUTXJLTGZXSWZZHEFBLXAUIVQQLYMUWQKPZVMOCVBNXMIYNBFHUJKEJGEKXKHTRTLRZWMGTMLFRBXZPBYJUJFZKNOSHPQHHTNDVFWBJKRXFHMRAXHEORCKXGTTTLCZGCKTOVOHAGXLNHEXGAGEYOKMRKYHPZQGRYHKOTGSQJZMULWMEWVZGRFEQBVDIVLEEBOYOWTMLGZUALZXXXGTAXQPSFVJHYPKOUDYIKSTANPDFDFVJULCRTFAHGKSGWWJSRWFFYLNRNKLQHIAVGJNQPURZMDBYMYJJATUAMXBBBTQHCZWLPZVRBLZRUBAYQMQDKYCDBOIIWNSIXBXVPKUCZJBEPWIOIPDPYYCEFWDQWHMIOAPGYQEHKYDNDDNFJVBNBHHCTDUOHRUHSVBYYEGZIEZACIMTOBLSZDMTTWQILGKXAHJJNGDUDCBYVFOJJFGNKYJFHNYRVPCVUACOOVYELLGSWJAMKQMBJDBYYZDUNEKSUTBNSFRAPEROAWXZESGDNQZPGNJRTUKWAALTJRGZRGLOJGRYJQHLDQOWSXHSDKWEENXKRXCCCXYQXODNAOLQRLWRQXVLAXHFWEJDRJVQHSHZJIUERZANPKESMLFHDNOFNJZKVCKBEBGAIMSMXKYGSOSAUYAZOTBCNKJCSLDRPZMCUTPGPOMXKSDDLRXMLJCQQNRPTJYWBUJEZFPBOHOCZQEITFLMPTMYWBFDVQNCJSKFMRIN / HTTP/1.1\\r\\n\",\"VGZVC VSLCA SQXFB JKIFN\\r\\n\"],\"sent\":[\"GET cache_object://localhost/ HTTP/1.0\\n\\r\",\"LIHS / HTTP/1.1\\n\\r\",\"GET / HTTP/AGW\\r\\n\",\"ISYIFXVXWFCXCNHMWXONZWOHJYNOVUJXGQMEOGUDUKKVRLRXOEJXZPVPENRELTTWFCMNTBKXXFKSDKIYLXIZZSBUFUHIQBAPFYRFVRAGMUIETRUMPLGRBTGUINIVFDTPVNRHBDALGZWQKCMEPJCOHKTIGVOLDJBBQKCLBKGUDYZKFSZOJLTJDEINCGRXTZHHTZNZHBTOLPREOLOZDTDPHRYISMIXAPTEKSIXIIHDICFMAEVRCDZXAGXHGCKAOWKHVQKKSDVXTYUJJGDECXOXMLXVLCRRPBJEMICYPTUTXJLTGZXSWZZHEFBLXAUIVQQLYMUWQKPZVMOCVBNXMIYNBFHUJKEJGEKXKHTRTLRZWMGTMLFRBXZPBYJUJFZKNOSHPQHHTNDVFWBJKRXFHMRAXHEORCKXGTTTLCZGCKTOVOHAGXLNHEXGAGEYOKMRKYHPZQGRYHKOTGSQJZMULWMEWVZGRFEQBVDIVLEEBOYOWTMLGZUALZXXXGTAXQPSFVJHYPKOUDYIKSTANPDFDFVJULCRTFAHGKSGWWJSRWFFYLNRNKLQHIAVGJNQPURZMDBYMYJJATUAMXBBBTQHCZWLPZVRBLZRUBAYQMQDKYCDBOIIWNSIXBXVPKUCZJBEPWIOIPDPYYCEFWDQWHMIOAPGYQEHKYDNDDNFJVBNBHHCTDUOHRUHSVBYYEGZIEZACIMTOBLSZDMTTWQILGKXAHJJNGDUDCBYVFOJJFGNKYJFHNYRVPCVUACOOVYELLGSWJAMKQMBJDBYYZDUNEKSUTBNSFRAPEROAWXZESGDNQZPGNJRTUKWAALTJRGZRGLOJGRYJQHLDQOWSXHSDKWEENXKRXCCCXYQXODNAOLQRLWRQXVLAXHFWEJDRJVQHSHZJIUERZANPKESMLFHDNOFNJZKVCKBEBGAIMSMXKYGSOSAUYAZOTBCNKJCSLDRPZMCUTPGPOMXKSDDLRXMLJCQQNRPTJYWBUJEZFPBOHOCZQEITFLMPTMYWBFDVQNCJSKFMRIN / HTTP/1.1\\r\\n\",\"VGZVC VSLCA SQXFB JKIFN\\r\\n\"],\"tampering\":{\"value\":false}}";

            case "http_header_field_manipulation":
                return "{\"tampering\":{\"header_field_name\":\"x-content-type-options\"}}";

            default:
                result = "{}";
        }

        return result;
    }

    private static String getBlockedTestKeyFrom(AbstractTest testType) {
        String result;
        switch (testType.getName()) {
            // Instant Messaging
            case "whatsapp":
                return "{\"registration_server_status\":\"blocked\",\"whatsapp_endpoints_status\":\"blocked\",\"whatsapp_web_status\":\"blocked\"}";

            case "telegram":
                return "{\"telegram_http_blocking\":\"true\",\"telegram_tcp_blocking\":\"true\",\"telegram_web_status\":\"blocked\"}";

            case "facebook_messenger":
                return "{\"facebook_tcp_blocking\":\"true\",\"facebook_dns_blocking\":\"true\"}";

            case "signal":
                return "{\"signal_backend_status\":\"blocked\",\"signal_backend_failure\":\"blocked\"}";

            // Circumvention
            case "riseupvpn":
                return "{\"ca_cert_status\":false,\"failing_gateways\":[{\"ip\":\"10.0.0.0\",\"port\":\"8000\",\"transport_type\":\"openvpn\"}, {\"ip\":\"10.0.0.0\",\"port\":\"8000\",\"transport_type\":\"obfs4\"}]}";

            // Performance
            case "ndt":
                return "";

            case "dash":
                return "";

            case "http_invalid_request_line":
                return "";

            case "http_header_field_manipulation":
                return "";

            case "tor":
            default:
                result = "{}";
        }

        return result;
    }

    public static void addEntryFiles(Context context, List<Measurement> measurements, Boolean markUploaded) {
        measurements.forEach(measurement -> {
            String entryName = String.format("%d_%d", faker.number.positive(), faker.number.positive());
            addEntryFile(context, entryName, measurement, markUploaded);
        });
    }

    public static boolean addEntryFile(Context context, String reportId, Measurement measurement, Boolean markUploaded) {
        try {
            //Simulating measurement done and uploaded
            measurement.report_id = reportId;
            measurement.is_done = true;
            measurement.is_uploaded = markUploaded;
            measurement.save();
            File entryFile = Measurement.getEntryFile(context, measurement.id, measurement.test_name);
            entryFile.getParentFile().mkdirs();
            FileUtils.writeStringToFile(
                    entryFile,
                    "",
                    Charset.forName("UTF-8")
            );
        } catch (IOException e) {
            return false;
        }

        return true;
    }

}
