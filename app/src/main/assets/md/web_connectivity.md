# Web connectivity

This test examines whether websites are reachable and if they are not, it
attempts to determine whether access to them is blocked through DNS tampering,
TCP connection RST/IP blocking or by a transparent HTTP proxy.

Specifically, this test is designed to perform the following:

* Resolver identification

* DNS lookup

* TCP connect

* HTTP GET request

By default, this test performs the above (excluding the first step, which is
performed only over the network of the user) both over a control server and over
the network of the user. If the results from both networks match, then there is
no clear sign of network interference; but if the results are different, then
the websites that the user is testing are likely censored.

Below we provide information about how each step performed under the web
connectivity test works.

## 1. Resolver identification

The domain name system (DNS) is what is responsible for transforming a host name
(e.g. torproject.org) into an IP address (e.g. 38.229.72.16). Internet Service
Providers, amongst others, run DNS resolvers which map IP addresses to host
names. In some circumstances though, ISPs map the requested host names to the
wrong IP addresses, which is a form of tampering.

As a first step, the web connectivity test attempts to identify which DNS
resolver is being used by the user. It does so by performing a DNS query to
special domains (such as whoami.akamai.com) which will disclose the IP address
of the resolver.

## 2. DNS lookup

Once the web connectivity test has identified the DNS resolver of the user, it
then attempts to identify which addresses and are mapped to the tested host
names by the resolver. It does so by performing a DNS lookup, which asks the
resolver to disclose which IP addresses are mapped to the tested host names, as
well as which other host names are linked to the tested host names under DNS
queries.

## 3. TCP connect

The web connectivity test will then try to connect to the tested websites by
attempting to establish a TCP session on port 80 (or port 443 for URLs that
begin with HTTPS) for the list of IP addresses that were identified in the
previous step (DNS lookup).

## 4. HTTP GET request

As the web connectivity test connects to tested websites (through the previous
step), it sends requests through the HTTP protocol to the servers which are
hosting those websites. A server normally responds to an HTTP GET request with
the content of the webpage that is requested.

## Comparison of results: Identifying censorship

Once the above steps of the web connectivity test are performed *both* over a
control server and over the network of the user, the collected results are then
compared with the aim of identifying whether and how tested websites are
tampered with. If the compared results do *not* match, then there is a sign of
network interference.

Below are the conditions under which the following types of blocking are
identified:

* **DNS blocking:** If the DNS responses (such as the IP addresses mapped to
    host names) do *not* match

* **TCP/IP blocking:** If a TCP session to connect to websites was *not*
    established over the network of the user

* **HTTP blocking:** If the HTTP request over the user's network failed, or the
* **HTTP status codes don't match, or all of the following apply:

    * The body length of compared websites (over the control server and the
      network of the user) differs by some percentage

    * The HTTP headers names do not match

    * The HTML title tags do not match

The examples below (testing piratebay.se and google.com for censorship in Italy) show
what the output of the web connectivity test could look like:

```
Starting test for http://thepiratebay.se/

* doing DNS query for thepiratebay.se

* connecting to 216.58.198.46:443

* doing HTTP(s) request http://thepiratebay.se/

* performing control request with backend

Result for http://thepiratebay.se/
----------------------------------
* BLOCKING DETECTED due to dns
* Is NOT accessible

Starting test for https://google.com/

* doing DNS query for google.com

* connecting to 83.224.65.41:80

* doing HTTP(s) request https://google.com/

* performing control request with backend

Result for https://google.com/
------------------------------
* No blocking detected
* Is accessible

Summary for web_connectivity
----------------------------

Accessible URLS
---------------
* https://google.com/

Not accessible URLS
-------------------
* http://thepiratebay.se/

URLS possibly blocked due to dns
--------------------------------
* http://thepiratebay.se/
```

**Note:** DNS resolvers, such as Google or your local ISP, often provide users
with IP addresses that are closest to them geographically. Often this is not
done with the intent of network tampering, but merely for the purpose of
providing users faster access to websites. As a result, some false positives
might arise in OONI measurements. Other false positives might occur when tested
websites serve different content depending on the country that the user is
connecting from, or in the cases when websites return failures even though they
are not tampered with.
