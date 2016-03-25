
This directory contains a collection of binary files (ending in .bin)
that represent the exact contents of a message as seen at the
application level. That is  each .bin file contains the exact contents of
exactly one sent or received message, so in theory if you read the
file into an array of bytes you would have exactly what you would get
if you had read the packet from the datagram socket. 

There are also a number of .pcapng files. These files are wireshark
capture files for some traces of DNS lookups of varying complexity.

** InitialQuery.bin - Sample of what a query looks like

** WiresharkQueryblueberry_ugrad_cs_ubc_ca.pcapng - a lookup of a
   non-existant name in the cs.ubc.ca domain

** NoSuchNameResponse.bin - the binary version of the last message

** WiresharkQuerywww_ugrad_cs_ubc_ca.pcapng - a trace for the lookup
   of www.ugrad.cs.ubc.ca. This is a fairly straightforward lookup
   that doesn't involve and CNAMES nor does it require the client to
   have to perform the lookup of an IP address for a name server

The files below are the individual responses in the above trace.
Response1_2b2b.bin
Response2_0488.bin
Response3_fdc3.bin
Response4_5f9f.bin

** WiresharkQuerywww_stanford_edu.pcapng - this is a trace where
   partway through, the response provides a name server to do that
   next lookup at but an IP address isn't provided so you have to do
   an IP address lookup of the name server before proceeding with the
   remainder of the search.

** WiresharkQueryprep_ai_mit_edu.pcapng - this is query where the
   initial answer is that prep.ai.mit is an alias for ftp.gnu.org so
   once that is determined a new query is started to lookup ftp.gnu.org


** WiresharkQueryfinance_google_ca.pcapng - One of the more
   complicated traces with lots of redirection. Note that the client
   doing this trace didn't make particularly good use of the
   information in the alternate sections that could have reduced the
   total number of queries. 

Note the on the department linux machines and most macs you should be
able to do a hexdump -C to print the .bin files out in a way that will
help you figure out how to encode and decode a DNS query or response. 

