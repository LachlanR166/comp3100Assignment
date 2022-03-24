import java.io.*;
import java.net.*;

public class Client {
    Socket s;
    DataOutputStream dout;
    BufferedReader din;

    String [] serverList;
    String [] latestJob;

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
        String [] responseArray = response.split(" ");

        System.out.println("RCVD " + response);

        switch(responseArray[0]){
            case "OK":
                this.handleOK();
                break;

            case "JCPL":
                this.queJobLoop();
                break;

            case "JOBN":
                this.handleJOBN(response);
                break;

            case "DATA":
                this.handleDATA(response);
                break;
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


    public String queJobLoop() throws IOException{
        System.out.println("\nNext Job: ");
        this.sendRequest("REDY");            

        return this.receiveResponse();
            
    }

    //Gets the largest server based on core count and returns a string
    public String getLargestServer(String [] serverList) throws IOException{
        String largestServer = "";
        int largestCoreCount = 0;

        for(int i = 0; i < serverList.length; i ++){
            if(Integer.parseInt(serverList[i].split(" ")[4]) > largestCoreCount){
                largestCoreCount = Integer.parseInt(serverList[i].split(" ")[4]);
                largestServer = serverList[i];
            }
        }

        System.out.println("Largest server: " + largestServer);
        return largestServer;
    }

    public boolean handleOK() throws IOException{
        return true;

    }

    public void handleDATA(String response) throws IOException{
        String [] DATA = response.split(" ");

        this.serverList = new String[Integer.parseInt(DATA[1])];
        this.sendRequest("OK");

        System.out.println("----------------------------------------------------------------");
        for(int i = 0; i < serverList.length; i++){
            this.serverList[i] = this.receiveResponse();
        }
        System.out.println("----------------------------------------------------------------");

        
        String largestServer = this.getLargestServer(this.serverList);

        System.out.println("----------------------------------------------------------------");

        this.sendRequest("OK");
        this.receiveResponse();

        this.sendRequest(String.format("SCHD %s %s %s", this.latestJob[2], largestServer.split(" ")[0], largestServer.split(" ")[1]));
        this.receiveResponse();
    }

    public void handleJOBN(String response) throws IOException{
        this.latestJob = response.split(" ");
        this.sendRequest(String.format("GETS Capable %d %d %d", Integer.parseInt(this.latestJob[4]), Integer.parseInt(this.latestJob[5]), Integer.parseInt(this.latestJob[6])));
        this.receiveResponse();
    }

    public void handleJCPL(String response) throws IOException{
        System.out.println("Response is JCPL");

        while(response.equals("JCPL")){
            this.sendRequest("OK");
            response = this.receiveResponse().split(" ")[0];
        } 
    }

    public static void main(String[] args) throws IOException {
        Client myClient = new Client("localhost", 50000);
        String response = "";

        myClient.handshake();

        while(!response.equals("NONE")){        
            response = myClient.queJobLoop();
        }
        myClient.closeConnection();
    }
}
