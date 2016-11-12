# HTTP invalid request line

This test tries to detect the presence of network components (“middle box”)
which could be responsible for censorship and/or traffic manipulation.

Instead of sending a normal HTTP request, this test sends an invalid HTTP
request line - containing an invalid HTTP version number, an invalid field count
and a huge request method – to an echo service listening on the standard HTTP
port. An echo service is a very useful debugging and measurement tool, which
simply sends back to the originating source any data it receives. If a middle
box is not present in the network between the user and an echo service, then the
echo service will send the invalid HTTP request line back to the user, exactly
as it received it. In such cases, we assume that there is no visible traffic
manipulation in the tested network.

If, however, a middle box is present in the tested network, the invalid HTTP
request line will be intercepted by the middle box and this may trigger an error
and that will subsequently be sent back to OONI. Such errors indicate that
software for traffic manipulation is likely placed in the tested network, though
it's not always clear what that software is. In some cases though, we are able
to identify censorship and/or surveillance vendors through the error messages in
the received HTTP response.

So far, based on this technique we have
[detected](https://explorer.ooni.torproject.org/highlights/) the use of
**BlueCoat**, **Squid** and **Privoxy** in networks across 11 countries around
the world.

**Note:** A false negative could potentially occur in the hypothetical instance
that ISPs are using highly sophisticated censorship and/or surveillance software
that is specifically designed to not trigger errors when receiving invalid HTTP
request lines like the ones of this test. Furthermore, the presence of a middle
box is not necessarily indicative of traffic manipulation, as they are often
used in networks for caching purposes.

