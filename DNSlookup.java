
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;
import java.io.*;
import java.net.SocketTimeoutException;

/**
 *
 */

/**
 * @author Donald Acton
 * This example is adapted from Kurose & Ross
 *
 */
public class DNSlookup {


	static final int MIN_PERMITTED_ARGUMENT_COUNT = 2;
	static boolean tracingOn = false;
	static InetAddress serverAddress;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int argCount = args.length;

		if (argCount < 2 || argCount > 3)
		{
			usage();
			return;
		}

 		String fqdn = args[1];
		String server = args[0];

		if (argCount == 3 && args[2].equals("-t"))
		{
				tracingOn = true;
		}

		String[] answer = {};
		try
		{
			answer = resolveQuery(server, server, fqdn, tracingOn, 30, Integer.MAX_VALUE);
		}
		catch (SocketTimeoutException e)
		{
			String[] temp = {"-2", "0.0.0.0"};
			answer = temp;
		}
		catch (Exception e)
		{
			String[] temp = {"-4", "0.0.0.0"};
			answer = temp;
		}
    System.out.format("%s %s %s\n", fqdn,answer[0],answer[1]);
		return;
	}

	private static void usage() {
		System.out.println("Usage: java -jar DNSlookup.jar rootDNS name [-t]");
		System.out.println("   where");
		System.out.println("       rootDNS - the IP address (in dotted form) of the root");
		System.out.println("                 DNS server you are to start your search at");
		System.out.println("       name    - fully qualified domain name to lookup");
		System.out.println("       -t      -trace the queries made and responses received");
	}


	private static String[] resolveQuery(String rootserver, String server, String fqdn, boolean tracingOn, int count, int ttl) throws Exception
	{
		if (count == 0)
		{
			String[] answer = {"-3","0.0.0.0"};
			return answer;
		}
		count--;
		DNSResponse response = null;
		response=query(server, fqdn, "A");
		if(response.getReplyCode() == 3)
		{
			String[] answer = {"-1","0.0.0.0"};
			return answer;
		}
		if (tracingOn && response != null)
		{
			response.dumpResponse();
		}
		//check if we have authoritative answer, if so print output and return
		InetAddress ip;
		if((ip = response.getIPaddr()) != null)
		{
			if(ttl < response.getIPttl())
			{
				String[] answer = {String.valueOf(ttl), ip.getHostAddress()};
				return answer;
			}
			else
			{
				String[] answer = {String.valueOf(response.getIPttl()), ip.getHostAddress()};
				return answer;
			}
		}
		////check if we have a CNAME, if so recursive search with new cname
		String cname;
		if ((cname = response.getCNAME()) != null)
		{
			if (response.getCNAMEttl() < ttl){ttl = response.getCNAMEttl();}
			return resolveQuery(rootserver,rootserver, cname, tracingOn, count, ttl);
		}
		/// check if there is nameserver, if so search using nameserver as server
		if (response.getAuthoritativeDNSname() != null)
		{
			String nsname = response.getAuthoritativeDNSname();
			InetAddress nserver = response.reQueryTo();
			if (nserver != null)
			{
				return resolveQuery(rootserver,nserver.getHostAddress(), fqdn, tracingOn, count, ttl);
			}
			else
			{
				String nsIP = (resolveQuery(rootserver,rootserver,nsname,tracingOn, count, ttl))[1];
				return resolveQuery(rootserver, nsIP, fqdn, tracingOn, count, ttl);
			}
		}


		String[] answer = {"-4","0.0.0.0"};
		return answer;
	}
	private static DNSResponse query(String server, String fqdn, String qtype) throws Exception
	{
		InetAddress serverAddress = InetAddress.getByName(server);
		return query(serverAddress, fqdn, qtype);
	}
	private static DNSResponse query(InetAddress server, String fqdn, String qtype) throws Exception
	{
		InetAddress serverAddress = server;
		int port = 53; // default port for DNS
		byte[] outbuf = new byte[512];
		//////////////////SEND PACKET///////////////////////////
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(5000);
		Random rand = new Random();
		//generate random ID for query
		int id = rand.nextInt(5000)+1; //49031337 is just a random large prime
		// generate query
		DNSQuery query = new DNSQuery(serverAddress,fqdn, id);
		outbuf = query.toByte();
		DatagramPacket packet = new DatagramPacket(outbuf, outbuf.length, serverAddress, port);
		socket.send(packet);
		//////////////////////////////////////////////////////////



		/////////////CATCH RESPONSE PACKET /////////////////////
		byte[] buf = new byte[512];
		boolean resend = false;
		DatagramPacket inpacket = new DatagramPacket(buf, buf.length);
		try
		{
      socket.receive(inpacket);
    }
		catch (SocketTimeoutException e)
		{
			resend = true;
			socket.send(packet);
    }
		if (resend)
		{
			socket.receive(inpacket);
		}
		DNSResponse response = new DNSResponse(inpacket.getData(), inpacket.getLength(), query);
		while (response.queryID() != id)
		{
			socket.receive(inpacket);
			response = new DNSResponse(inpacket.getData(), inpacket.getLength(), query);
		}

		/////////////////////////////////////////////////////////

		return response;
	}
}
