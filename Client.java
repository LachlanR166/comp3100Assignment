import java.io.*;
import java.net.*;

public class Client {
    Socket s;
    DataOutputStream dout;
    BufferedReader din;

    public Client(String host, int port){
        try{
            this.s = new Socket(host, port);
            this.dout = new DataOutputStream(s.getOutputStream());
            this.din = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void sendRequest(String request) throws IOException{
        this.dout.write((request + "\n").getBytes());
    }

    public void receiveResponse() throws IOException{
        String response = this.din.readLine();
        if(response.length() > 0){
            System.out.println(response);
        }
    }

    public void closeConnection() throws IOException{
        this.sendRequest("QUIT");
        this.receiveResponse();
        this.din.close();
        this.dout.close();
        this.s.close();
        System.exit(0);
    }

    public void handshake() throws IOException{
        this.sendRequest("HELO");
        this.receiveResponse();
        this.sendRequest("AUTH " + System.getProperty("user.name"));
        this.receiveResponse();
        this.sendRequest("REDY");
        this.receiveResponse();
    }


    public static void main(String[] args) throws IOException {
        Client myClient = new Client("localhost", 50000);
        myClient.handshake();
        myClient.closeConnection();
    }
}