# DNSresolver
<img src="https://github.com/kshi219/DNSresolver/blob/master/out2-4.gif">


## Description
This is a command line tool for obtaining an IP address for a web domain name via the DNS protocol. Given a domain name and a the address to a nearby root name server, this tool will query, interpret and re-query based on successive responses from various name servers until an authoritative response is obtained as shown in the gif above. The resulting IP address can be plugged into web browser to contact the desired website.

### make and run with:
`java -jar DNSlookup.jar rootDNS name [-t] `
`-t` is for trace output, turn this on for more interesting view at command line
`name` is domain you are trying to resolve, `rootDNS` is an IP Address of a rootDNS (ex: 192.203.230.10 via NASA ames)
