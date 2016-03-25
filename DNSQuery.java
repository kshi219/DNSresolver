import java.net.InetAddress;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class DNSQuery {


 // fields are relevant parts for a query following format of DNS message outlined in RFC
  private int queryID;         /* this is for the response it must match the one in the request */
  private byte[] header;
  // tail = Qtype|Qclass
  // by default Qtype is A, but can also be CNAME or NS, QClass is always the same
  private byte[] tail;
  private String Qtype = "A";
  //used to produce the byte array
  private ByteArrayOutputStream buffer;
  //iterator useful in encode/decoding
  private int byteNo = 0;
  ///opCode should be 0 for default queries
  private int queryCount = 1;
  // answer, name server , additional counts all set to zero for us
  private String name;
  private boolean isRecursive = false;
  private InetAddress serverAddress;


public DNSQuery(InetAddress server, String queryName, int qID)
{
  this(server, queryName, qID, false, 1, "A");
}

public DNSQuery(InetAddress server, String queryName, int qID, String qtype)
{
  this(server, queryName, qID, false, 1, qtype);
}

public DNSQuery(InetAddress server, String queryName, int qID, boolean recursive, int qCount,String type)
{
  serverAddress = server;
  queryID = qID;
  queryCount = qCount;
  name = queryName;
  Qtype = type;
  isRecursive = recursive;
  header = new byte[12];
  tail = new byte[4];
  Arrays.fill(header, (byte) 0);
  header[0] = (byte) ((queryID >> 8) & 0x00ff );
  header[1] = (byte) (queryID & 0x00ff);
  if (isRecursive)
  {
    header[2] = 0x01;
  }
  header[4] = (byte) ((queryCount >> 8) & 0x00ff );
  header[5] = (byte) (queryCount & 0x00ff);
  tail[0] = 0x00;
  if (Qtype == "A")
  {
    tail[1] = 0x01;
  }
  if (Qtype == "CNAME")
  {
    tail[1] = 0x05;
  }
  if (Qtype == "NS")
  {
    tail[1] = 0x02;
  }
  tail[2] = 0x00;
  tail[3] = 0x01; //Qclass for us is aways IN for internet
}

public byte[] toByte() throws IOException{
  if (buffer == null)
  {
    buffer = new ByteArrayOutputStream();
    buffer.write(header);
    String[] labels= name.split("\\.");
    for(String s : labels)
    {
      byte length = (byte) (s.length() & 0x00ff);
      buffer.write(length);
      buffer.write(s.getBytes(StandardCharsets.UTF_8));
    }
    buffer.write(0x00);
    buffer.write(tail);
  }

  return buffer.toByteArray();
}

public InetAddress getServerAddress()
{
  return serverAddress;
}

public String getQueryAsString()
{
  return name;
}

public int queryID() {
  return queryID;
}











}
