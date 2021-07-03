package org.openobservatory.ooniprobe.factory;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Psiphon;
import org.openobservatory.ooniprobe.test.test.RiseupVPN;
import org.openobservatory.ooniprobe.test.test.Signal;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.Tor;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import io.bloco.faker.Faker;

public class TestKeyFactory {

    static Faker faker = new Faker();
    static Gson gson = new Gson();

    public static TestKeys getAccessible(String testTypeName) {
        return gson.fromJson(getAccessibleString(testTypeName), TestKeys.class);
    }

    public static String getAccessibleStringFrom(AbstractTest testType) {
        return getAccessibleString(testType.getName());
    }

    public static String getAccessibleString(String testTypeName) {
        String result;
        switch (testTypeName) {
            // Web Connectivity
            case WebConnectivity.NAME:
                return "{\"blocking\":\"false\"}";

            // Instant Messaging
            case Whatsapp.NAME:
                return "{\"registration_server_status\":\"ok\",\"whatsapp_endpoints_status\":\"ok\",\"whatsapp_web_status\":\"ok\"}";

            case Telegram.NAME:
                return "{\"telegram_http_blocking\":\"false\",\"telegram_tcp_blocking\":\"false\",\"telegram_web_status\":\"ok\"}";

            case FacebookMessenger.NAME:
                return "{\"facebook_tcp_blocking\":\"false\",\"facebook_dns_blocking\":\"ok\"}";

            case Signal.NAME:
                return "{\"signal_backend_status\":\"ok\",\"signal_backend_failure\":\"ok\"}";

            // Circumvention
            case Psiphon.NAME:
                return "{\"bootstrap_time\":" + faker.number.positive(5.0, 100.0) + "}";

            case Tor.NAME:
                return "{\"dir_port_accessible\":7,\"dir_port_total\":10,\"obfs4_accessible\":14,\"obfs4_total\":15,\"or_port_accessible\":0,\"or_port_dirauth_accessible\":10,\"or_port_dirauth_total\":10,\"or_port_total\":0}";

            case RiseupVPN.NAME:
                return "{\"ca_cert_status\":true,\"transport_status\":{\"obfs4\":\"ok\",\"openvpn\":\"ok\"}}";

            // Performance
            case Ndt.NAME:
                return "{\"protocol\":7,\"server\":{\"hostname\":\"ndt-mlab2-lis02.mlab-oti.measurement-lab.org\",\"site\":\"lis02\"},\"server_country\":\"PT\",\"server_name\":\"lis02\",\"summary\":{\"avg_rtt\":14.444,\"download\":23186.37200985908,\"max_rtt\":92.873,\"min_rtt\":12.606,\"mss\":1448.0,\"ping\":12.606,\"retransmit_rate\":0.06652086351792866,\"upload\":18466.777738433746}}";

            case Dash.NAME:
                return "{\"server\":{\"hostname\":\"neubot-mlab-mlab2-lis02.mlab-oti.measurement-lab.org\",\"site\":\"lis02\"},\"simple\":{\"median_bitrate\":27244.0,\"min_playout_delay\":0.08821899999999872}}";

            case HttpInvalidRequestLine.NAME:
                return "{\"received\":[\"GET cache_object://localhost/ HTTP/1.0\\n\\r\",\"LIHS / HTTP/1.1\\n\\r\",\"GET / HTTP/AGW\\r\\n\",\"ISYIFXVXWFCXCNHMWXONZWOHJYNOVUJXGQMEOGUDUKKVRLRXOEJXZPVPENRELTTWFCMNTBKXXFKSDKIYLXIZZSBUFUHIQBAPFYRFVRAGMUIETRUMPLGRBTGUINIVFDTPVNRHBDALGZWQKCMEPJCOHKTIGVOLDJBBQKCLBKGUDYZKFSZOJLTJDEINCGRXTZHHTZNZHBTOLPREOLOZDTDPHRYISMIXAPTEKSIXIIHDICFMAEVRCDZXAGXHGCKAOWKHVQKKSDVXTYUJJGDECXOXMLXVLCRRPBJEMICYPTUTXJLTGZXSWZZHEFBLXAUIVQQLYMUWQKPZVMOCVBNXMIYNBFHUJKEJGEKXKHTRTLRZWMGTMLFRBXZPBYJUJFZKNOSHPQHHTNDVFWBJKRXFHMRAXHEORCKXGTTTLCZGCKTOVOHAGXLNHEXGAGEYOKMRKYHPZQGRYHKOTGSQJZMULWMEWVZGRFEQBVDIVLEEBOYOWTMLGZUALZXXXGTAXQPSFVJHYPKOUDYIKSTANPDFDFVJULCRTFAHGKSGWWJSRWFFYLNRNKLQHIAVGJNQPURZMDBYMYJJATUAMXBBBTQHCZWLPZVRBLZRUBAYQMQDKYCDBOIIWNSIXBXVPKUCZJBEPWIOIPDPYYCEFWDQWHMIOAPGYQEHKYDNDDNFJVBNBHHCTDUOHRUHSVBYYEGZIEZACIMTOBLSZDMTTWQILGKXAHJJNGDUDCBYVFOJJFGNKYJFHNYRVPCVUACOOVYELLGSWJAMKQMBJDBYYZDUNEKSUTBNSFRAPEROAWXZESGDNQZPGNJRTUKWAALTJRGZRGLOJGRYJQHLDQOWSXHSDKWEENXKRXCCCXYQXODNAOLQRLWRQXVLAXHFWEJDRJVQHSHZJIUERZANPKESMLFHDNOFNJZKVCKBEBGAIMSMXKYGSOSAUYAZOTBCNKJCSLDRPZMCUTPGPOMXKSDDLRXMLJCQQNRPTJYWBUJEZFPBOHOCZQEITFLMPTMYWBFDVQNCJSKFMRIN / HTTP/1.1\\r\\n\",\"VGZVC VSLCA SQXFB JKIFN\\r\\n\"],\"sent\":[\"GET cache_object://localhost/ HTTP/1.0\\n\\r\",\"LIHS / HTTP/1.1\\n\\r\",\"GET / HTTP/AGW\\r\\n\",\"ISYIFXVXWFCXCNHMWXONZWOHJYNOVUJXGQMEOGUDUKKVRLRXOEJXZPVPENRELTTWFCMNTBKXXFKSDKIYLXIZZSBUFUHIQBAPFYRFVRAGMUIETRUMPLGRBTGUINIVFDTPVNRHBDALGZWQKCMEPJCOHKTIGVOLDJBBQKCLBKGUDYZKFSZOJLTJDEINCGRXTZHHTZNZHBTOLPREOLOZDTDPHRYISMIXAPTEKSIXIIHDICFMAEVRCDZXAGXHGCKAOWKHVQKKSDVXTYUJJGDECXOXMLXVLCRRPBJEMICYPTUTXJLTGZXSWZZHEFBLXAUIVQQLYMUWQKPZVMOCVBNXMIYNBFHUJKEJGEKXKHTRTLRZWMGTMLFRBXZPBYJUJFZKNOSHPQHHTNDVFWBJKRXFHMRAXHEORCKXGTTTLCZGCKTOVOHAGXLNHEXGAGEYOKMRKYHPZQGRYHKOTGSQJZMULWMEWVZGRFEQBVDIVLEEBOYOWTMLGZUALZXXXGTAXQPSFVJHYPKOUDYIKSTANPDFDFVJULCRTFAHGKSGWWJSRWFFYLNRNKLQHIAVGJNQPURZMDBYMYJJATUAMXBBBTQHCZWLPZVRBLZRUBAYQMQDKYCDBOIIWNSIXBXVPKUCZJBEPWIOIPDPYYCEFWDQWHMIOAPGYQEHKYDNDDNFJVBNBHHCTDUOHRUHSVBYYEGZIEZACIMTOBLSZDMTTWQILGKXAHJJNGDUDCBYVFOJJFGNKYJFHNYRVPCVUACOOVYELLGSWJAMKQMBJDBYYZDUNEKSUTBNSFRAPEROAWXZESGDNQZPGNJRTUKWAALTJRGZRGLOJGRYJQHLDQOWSXHSDKWEENXKRXCCCXYQXODNAOLQRLWRQXVLAXHFWEJDRJVQHSHZJIUERZANPKESMLFHDNOFNJZKVCKBEBGAIMSMXKYGSOSAUYAZOTBCNKJCSLDRPZMCUTPGPOMXKSDDLRXMLJCQQNRPTJYWBUJEZFPBOHOCZQEITFLMPTMYWBFDVQNCJSKFMRIN / HTTP/1.1\\r\\n\",\"VGZVC VSLCA SQXFB JKIFN\\r\\n\"],\"tampering\":{\"value\":false}}";

            case HttpHeaderFieldManipulation.NAME:
                return "{\"tampering\":{\"value\":\"false\"}}";

            default:
                result = "{}";
        }

        return result;
    }

    public static TestKeys getBlocked(String testTypeName) {
        return gson.fromJson(getBlockedString(testTypeName), TestKeys.class);
    }

    public static String getBlockedStringFrom(AbstractTest testType) {
        return getBlockedString(testType.getName());
    }

    public static String getBlockedString(String testTypeName) {
        String result;
        switch (testTypeName) {
            // Web Connectivity
            case WebConnectivity.NAME:
                return "{\"blocking\":\"true\"}";

            // Instant Messaging
            case Whatsapp.NAME:
                return "{\"registration_server_status\":\"blocked\",\"whatsapp_endpoints_status\":\"blocked\",\"whatsapp_web_status\":\"blocked\"}";

            case Telegram.NAME:
                return "{\"telegram_http_blocking\":\"true\",\"telegram_tcp_blocking\":\"true\",\"telegram_web_status\":\"blocked\"}";

            case FacebookMessenger.NAME:
                return "{\"facebook_tcp_blocking\":\"true\",\"facebook_dns_blocking\":\"true\"}";

            case Signal.NAME:
                return "{\"signal_backend_status\":\"blocking\",\"signal_backend_failure\":\"blocked\"}";

            // Circumvention
            case RiseupVPN.NAME:
                return "{\"transport_status\":{\"obfs4\":\"blocked\",\"openvpn\":\"blocked\"},\"ca_cert_status\":false,\"failing_gateways\":[{\"ip\":\"10.0.0.0\",\"port\":\"8000\",\"transport_type\":\"openvpn\"}, {\"ip\":\"10.0.0.0\",\"port\":\"8000\",\"transport_type\":\"obfs4\"}]}";

            case Tor.NAME:
                return "{\"dir_port_accessible\":0,\"dir_port_total\":14,\"obfs4_accessible\":14,\"obfs4_total\":15,\"or_port_accessible\":0,\"or_port_dirauth_accessible\":10,\"or_port_dirauth_total\":10,\"or_port_total\":0}";

            // Performance
            case HttpHeaderFieldManipulation.NAME:
            case HttpInvalidRequestLine.NAME:
                return "{\"tampering\":{\"value\":\"true\"}}";

            case Psiphon.NAME:
            case Dash.NAME:
                return "{\"failure\":\"ok\"}";


            case Ndt.NAME:
            default:
                result = "{}";
        }

        return result;
    }
}
