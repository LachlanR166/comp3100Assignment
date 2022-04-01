/*
Project: Stage 1
Class: Distributed Systems - COMP3100
Author: Lachlan Rigg - 45209715
*/

import java.io.*;
import java.net.*;

public class Client {
    Socket s;
    DataOutputStream dout;
    BufferedReader din;

    String[] latestJob;
    String largestServerType = "";
    int largestCoreCount = 0;
    int noOfServers = 0;
    int currentServerIndex = 0;

    boolean hasFoundLargestServerType = false;

    //Constructor for Client class
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

    //Function to send requests to the server
    public void sendRequest(String request) throws IOException{
        this.dout.write((request + "\n").getBytes());
        //System.out.println("SENT " + request);
    }

    //Function to receive all responses from the server
    public String receiveResponse() throws IOException{
        String response = this.din.readLine();
        String [] responseArray = response.split(" ");

        //System.out.println("RCVD " + response);

        //Switch case to determine what the correct response should be
        switch(responseArray[0]){

            //If its JCPL then send REDY and start a new job loop
            case "JCPL":
                this.queJobLoop();
                break;

            //Else if the first element is JOBN, save the entire string array as latest job and schedule a job
            case "JOBN":
                this.latestJob = responseArray;
                this.scheduler();
                break;

            //Else if the response is DATA, find the largest server type (LRR)
            case "DATA":
                this.findLargestServerType(response);
                break;

            //Else if NONE, close the connection to the server
            case "NONE":
                this.closeConnection();
                break;
                
            default:
                break;
        }
    
        return response;

    }

    //Function that handshakes with the ds-sim server
    public void handshake() throws IOException{
        this.sendRequest("HELO");
        this.receiveResponse();
        this.sendRequest("AUTH " + System.getProperty("user.name"));
        this.receiveResponse();
    }

    //Function that starts the next job que
    public String queJobLoop() throws IOException{
        this.sendRequest("REDY");            

        return this.receiveResponse();
            
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

    //Function that runs once and finds the largest server type and number of servers belonging to that type
    public void findLargestServerType(String response) throws IOException{
        //Split up the response string into different elements
        String [] DATA = response.split(" ");

        this.sendRequest("OK");
        //Iterate through all responses from the server
        for(int i = 0; i < Integer.parseInt(DATA[1]); i++){
            //Receive a response
            response = this.receiveResponse();

            //Split into an array to access individual elements
            String[] responseArray = response.split(" ");

            //If the core count of the current server is larger than the previous and the server type differs; then update it
            if(Integer.parseInt(responseArray[4]) > this.largestCoreCount){
                this.largestServerType = responseArray[0];
                this.largestCoreCount = Integer.parseInt(responseArray[4]);
                this.noOfServers = 0;
            }

            //Else if the core counts are the same and the server types are the same then increase number of server by one
            else if((Integer.parseInt(responseArray[4]) == this.largestCoreCount) && (responseArray[0].equals(this.largestServerType))){
                this.noOfServers++;
            }
        }
        //Largest server type has been found so is set to true so it wont run again
        this.hasFoundLargestServerType = true;
    }

    //Function to handle scheduler
    public void scheduler() throws IOException{
        
        //If the largest server type has not yet been found
        if(!hasFoundLargestServerType){
            //Send GETS All to the server
            this.sendRequest("GETS All");
            //Server will send DATA in return and trigger findLargestServerType()
            this.receiveResponse();
            this.sendRequest("OK");
            this.receiveResponse();
        }  

        //Schedule the job with the largest server type with rotating server id [i.e "0,1,2,3,0,1,2,3"]
        this.sendRequest(String.format("SCHD %s %s %s", this.latestJob[2], this.largestServerType, this.currentServerIndex));

        //If the current server index is greater than or equal to the number of servers then reset to zero
        if(this.currentServerIndex >= this.noOfServers){
            this.currentServerIndex = 0;
        }

        //Otherwise increase the current index by one
        else{
            this.currentServerIndex++;
        }

        this.receiveResponse();
    }


    public static void main(String[] args) throws IOException {
        //Create a new Client on localhost:50000
        Client myClient = new Client("localhost", 50000);
        String response = "";

        //Handshake between client and server
        myClient.handshake();

        //While the response does not equal "NONE"
        while(!response.equals("NONE")){
            //Que job loop, eventually returning a response
            response = myClient.queJobLoop();
        }

    }
}
