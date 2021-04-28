# Bisq Client (Java)
The Bisq Client Service acts as a routable service to/from Bisq.

## Summary
Bisq is a non-custodial decentralized Bitcoin exchange for both national and crypto. It is primarily
designed for traders in that each person making a trade sets an offer price or selects an offer price.
For people who just wish to exchange a currency for another and not greatly price sensitive,
this is too complicated. A simpler method for interacting with Bisq is needed.

This service aims to provide that simpler method. Quickness of exchange is preferred over making a
profit from the exchange by taking the lowest offers available up to thresholds set by the end user.
When no available offers exist within a selected threshold (default is <=20% above/below market),
an offer is created. Threshold can be changed by the end user.

## Roadmap
* Initial support will embed Bisq as today the HTTP JSON API is not workable.
* A future version will support a local instance and a remote instance using 1M5 (for lightweight clients).

## Modes
Will support local, embedded, and remote instances as modes.
Once all three modes are supported, the service on startup will see if a preferred mode is set
and if so, will start in that mode. If that mode fails, it will try the other two modes and use either if
successful. If no preferred mode is set, if a local instance is installed, it will use that; if
not, and a remote Tor address is provided, it will use the remote instance, otherwise it will start
up the embedded instance.

### Embedded
The embedded version runs Bisq core as a headless daemon within the 1M5 OS process.

* Embedded Bisq uses Tor to connect to Bisq and Bitcoin networks.
* Future versions will support routing around Tor blocks using 1M5 protocols.
* Most performant method of accessing Bisq as no wire protocol is required.
* May soak up much memory, e.g. we have seen 6Gb of RAM usage with Bisq over time.

### Remote
A remote version is best for light-weight clients such as mobile phones, RaspberryPis, etc.
This will allow access to a remote Bisq instance through 1M5 or directly through Tor.

### Local
The local version is best for traders who are familiar with Bisq so they're able to confirm the
underlying service and able to act on any issues that may arise.

* If local Bisq instance unable to reach the Bisq network via Tor, a notification will be published.
* Communicates with Bisq node via http on localhost (requires HTTP Service).
* Bisq uses Tor to connect to Bitcoin network if local Bitcoin node is not present.
