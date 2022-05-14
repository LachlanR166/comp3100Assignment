/*
Project: Stage 2, S1 2022
Class: Distributed Systems - COMP3100
Author: Lachlan Rigg - 45209715
*/
import java.io.*;
import java.net.*;


public class Clientv2 {
    Socket s;
    DataOutputStream dout;
    BufferedReader din;

    JOBN latestJob;
    Algorithm algorithm = new Algorithm();
    boolean hasServerList = false;

    public static void main(String[] args) throws IOException {
        //Create a new Client on localhost:50000 and get the supplied algorithm type
        Clientv2 myClient = new Clientv2("localhost", 50000, args[0]);

        //Handshake between client and server
        myClient.handshake();

        while(true){
            myClient.sendRequest("REDY");
            myClient.responseController(myClient.receiveResponse());
        }

    }

    //Constructor for Client class
    public Clientv2(String host, int port, String algorithm){
        try{
            this.s = new Socket(host, port);
            this.dout = new DataOutputStream(this.s.getOutputStream());
            this.din = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
            this.algorithm.name = algorithm;
            System.out.println("Using algorithm: " + algorithm);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    //Function that handshakes with the ds-sim server
    public void handshake() throws IOException{
        this.sendRequest("HELO");
        this.receiveResponse();
        this.sendRequest("AUTH " + System.getProperty("user.name"));
        this.receiveResponse();
    }

    //Function to send requests to the server
    public void sendRequest(String request) throws IOException{
        this.dout.write((request + "\n").getBytes());
        //System.out.println("SENT " + request);
    }

    //Function to receive responses and split the result into a string array and return it
    public String[] receiveResponse() throws IOException{
        String response = this.din.readLine();
        //System.out.println("RCVD " + response);

        return response.split(" ");
    }

    //Function to control different tasks based on a response
    public void responseController(String[] response) throws IOException{
  
        switch(response[0]){
            
            //If the response is JOBN, store the latest job in an object and run code based on the selected algorithm
            case "JOBN":
                this.latestJob = new JOBN(response);
                switch(this.algorithm.name){

                    //Largest round robin
                    case "lrr":
                        if(!this.hasServerList){
                            this.getServerData("All");
                        }

                        this.scheduleJob(this.algorithm.lrr.largestServerType, this.algorithm.lrr.nextServerIndex());
                        break;
                    
                    //First capable
                    case "fc":
                        this.getServerData("Capable");
                        this.scheduleJob(this.algorithm.fc.serverType, this.algorithm.fc.serverID);
                        break;

                    //Optimised turnaround time
                    case "ott":
                        this.getServerData("Avail");

                        this.scheduleJob(this.algorithm.ott.serverType, this.algorithm.ott.serverID);
                        break;
                    
                    default:
                        break;
                }
                break;
                    
            case "NONE":
                this.closeConnection();
                break;

            //If the response is DATA then store it in an object and call the code for selected algorithm
            case "DATA":
                DATA data = new DATA(response);
                
                switch(this.algorithm.name){
                    //Largest round robin
                    case "lrr":
                        this.algorithm.lrr = new LRR(this, data);
                        this.hasServerList = true;
                        break;

                    //First capable
                    case "fc":
                        this.algorithm.fc = new FC(this, data);
                        break;
                    
                    //Optimised turnaround time
                    case "ott":
                        this.algorithm.ott = new OTT(this, data, latestJob);
                        break;

                    default:
                        break;
                }
                break;
            
            default:
                break;
            }
    }

    //Function to schedule a job with the server
    public void scheduleJob(String serverType, int serverID) throws IOException{
        this.sendRequest(String.format("SCHD %s %s %s", this.latestJob.jobID, serverType, serverID));
        this.receiveResponse();
    }

    //Function to get server information from ds-server
    public void getServerData(String type) throws IOException{
        if(type.equals("All")){
            this.sendRequest("GETS All");
        }
        else{
            this.sendRequest(String.format("GETS %s %d %d %d", type, this.latestJob.core, this.latestJob.memory, this.latestJob.disk));
        }
        this.responseController(this.receiveResponse());
        this.sendRequest("OK");
        this.receiveResponse();
    }

    //Function to close socket connections and exit the program
    public void closeConnection() throws IOException{
        this.sendRequest("QUIT");
        this.receiveResponse();
        this.din.close();
        this.dout.close();
        this.s.close();
        System.exit(0);
    }
}
