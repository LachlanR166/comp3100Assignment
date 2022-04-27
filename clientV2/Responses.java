

class DATA {
    int nRecs, recLen;

    public DATA(String[] response){
        this.nRecs = Integer.parseInt(response[1]);
        this.recLen = Integer.parseInt(response[2]);
    }
}

class JOBN {
    int submitTime, jobID, estRuntime, core, memory, disk;

    public JOBN(String[] response){
        this.submitTime = Integer.parseInt(response[1]);
        this.jobID = Integer.parseInt(response[2]);
        this.estRuntime = Integer.parseInt(response[3]);
        this.core = Integer.parseInt(response[4]);
        this.memory = Integer.parseInt(response[5]);
        this.disk = Integer.parseInt(response[6]);
    }
}

class JOBP {
    int submitTime, jobID, estRuntime, core, memory, disk;

    public JOBP(String[] response){
        this.submitTime = Integer.parseInt(response[1]);
        this.jobID = Integer.parseInt(response[2]);
        this.estRuntime = Integer.parseInt(response[3]);
        this.core = Integer.parseInt(response[4]);
        this.memory = Integer.parseInt(response[5]);
        this.disk = Integer.parseInt(response[6]);
    }
}

class JCPL {
    int endTime, jobID, serverID;
    String serverType;

    public JCPL(String[] response){
        this.endTime = Integer.parseInt(response[1]);
        this.jobID = Integer.parseInt(response[2]);
        this.serverType = response[3];
        this.serverID = Integer.parseInt(response[4]);
    }
}

class RESF {
    int serverID, timeOfFailure;
    String serverType;

    public RESF(String[] response){
        this.serverType = response[1];
        this.serverID = Integer.parseInt(response[2]);
        this.timeOfFailure = Integer.parseInt(response[3]);
    }
}

class RESR {
    int serverID, timeOfRecovery;
    String serverType;

    public RESR(String[] response){
        this.serverType = response[1];
        this.serverID = Integer.parseInt(response[2]);
        this.timeOfRecovery = Integer.parseInt(response[3]);
    }
}