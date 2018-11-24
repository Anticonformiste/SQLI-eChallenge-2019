package ma.sqli.tests.cloudinfrastructure;

public class Machine {
    private String name, os, diskSpace, ram;
    private StateMachine state;

    public Machine() {
    }

    public Machine(String name, String os, String diskSpace, String ram) {
        this.name = name;
        this.os = os;
        this.diskSpace = diskSpace;
        this.ram = ram;
        this.state = StateMachine.INACTIVE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(String diskSpace) {
        this.diskSpace = diskSpace;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public StateMachine getState() {
        return state;
    }

    public void setState(StateMachine state) {
        this.state = state;
    }
}
