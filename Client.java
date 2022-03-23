import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;

public class Client {
    Socket s;
    DataOutputStream dout;
    BufferedReader din;


    String [] serverList;

    public Client(String host, int port){
        try{
            this.s = new Socket(host, port);
            this.dout = new DataOutputStream(this.s.getOutputStream());
            this.din = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void sendRequest(String request) throws IOException{
        this.dout.write((request + "\n").getBytes());
        System.out.println("SENT " + request);
    }

    public String receiveResponse() throws IOException{
        String response = this.din.readLine();

        if(response.length() > 0){
            System.out.println("RCVD " + response);
        }
    
        return response;

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
    }


    public String getServerState(String type) throws IOException{
        System.out.println("\nNext Job: ");

        this.sendRequest("REDY");
        String response = this.receiveResponse();
        if(response.split(" ")[0].equals("JOBN")){

            String [] JOBN = response.split(" ");
            this.sendRequest(String.format("GETS %s %d %d %d", type, Integer.parseInt(JOBN[4]), Integer.parseInt(JOBN[5]), Integer.parseInt(JOBN[6])));

            String [] DATA = this.receiveResponse().split(" ");
            this.serverList = new String[Integer.parseInt(DATA[1])];
            this.sendRequest("OK");

            for(int i = 0; i < serverList.length; i++){
                this.serverList[i] = this.receiveResponse();
            }

            this.sendRequest("OK");
            this.receiveResponse();

            this.sendRequest(String.format("SCHD %s %s %s", JOBN[2], this.serverList[0].split(" ")[0], this.serverList[0].split(" ")[1]));
        }

        else{
            this.sendRequest("OK");
        }

        
        response = this.receiveResponse();
        return response;
            
    }

    public static void main(String[] args) throws IOException {
        Client myClient = new Client("localhost", 50000);
        String response = "";

        myClient.handshake();

        while(!response.equals("NONE")){
        
            response = myClient.getServerState("Capable");
        }
        myClient.closeConnection();
    }
}
