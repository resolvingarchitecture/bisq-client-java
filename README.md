# Bisq Client (Java)
The Bisq Client Service acts as a routable service to/from Bisq.

* Will support local, embedded, and remote instances as modes.
* Initial support expects a local instance, which can be installed from https://bisq.network
* A future version will support an embedded version and a remote version using its Tor address.
* Once all three modes are supported, the service on startup will see if a preferred mode is set
  and if so, will start in that mode. If that mode fails, it try the other two modes and use either if
  successful. If no preferred mode is set, if a local instance is installed, it will use that; if
  not and a remote Tor address is provided, it will use the remote instance, otherwise it will start
  up the embedded instance.

## Local
The local version is best for traders who are familiar with Bisq so they're able to confirm the
underlying service and able to act on any issues that may arise.

* If local Bisq instance unable to reach the Bisq network via Tor, a notification will be published.
* Communicates with Bisq node via http on localhost (requires HTTP Service).
* Bisq uses Tor to connect to Bitcoin network if local Bitcoin node is not present.

## Remote
A remote version is best for light-weight clients such as mobile phones, RaspberryPis, etc.

## Embedded
An embedded version will be available once the APIs are reliably flushed out. It will be
best for non-traders who are less price-sensitive and perhaps less technical.

