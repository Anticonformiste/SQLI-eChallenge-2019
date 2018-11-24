package ma.sqli.tests.cloudinfrastructure;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudInfrastructure{

    // The list of stores available in the Cloud
    private Collection<Store> stores;
    // The list of VMs in the Cloud
    private Collection<Machine> machines;

    public CloudInfrastructure() {
        stores = new ArrayList<>();
        machines = new ArrayList<>();
    }

    /*
    * + + + + + + + + + + +
    * START: Managing Stores
    * + + + + + + + + + + +
    * */

    //Creating new stores
    public void createStore(String storeName) throws CreateStoreException {
        //Check if there is any store with the same name
        if(stores.contains(getStoreByName(storeName)))
            throw new CreateStoreException();
        stores.add(new Store(storeName));
    }

    //Uploading files to store
    public void uploadDocument(String storeName, String ...fileNames){
        for (String fName:fileNames) {
            getStoreByName(storeName).getFileNames().add(fName);
        }
    }

    //Delete a store via its name
    public void deleteStore(String storeName){
        Store toRemove = getStoreByName(storeName);
        stores.remove(toRemove);
    }

    //Empty the files of a store
    public void emptyStore(String storeName){
        Store toEmpty = getStoreByName(storeName);
        toEmpty.getFileNames().clear();
    }

    //List the cloud's stores with their content
    public String listStores(){
        StringBuilder sb = new StringBuilder();
        Iterator<Store> storesIterator = stores.iterator();

        while (storesIterator.hasNext()){
            Store store = storesIterator.next();
            sb.append(store.getName()).append(":");
            if(store.getFileNames().isEmpty())//An empty store
                sb.append("empty");
            else{//List the store elements
                String filesCsv = String.join(", ", store.getFileNames());
                sb.append(filesCsv);
            }
            //Put the " || " stores separator
            if(storesIterator.hasNext())
                sb.append("||");
        }
        return sb.toString();
    }

    /*
     * + + + + + + + + + + +
     * END: Managing Stores
     * + + + + + + + + + + +
     * */

    // = = = = = = = = = = = = = = = = =

    /*
     * + + + + + + + + + + + + + + + +
     * START: Managing VirtualMachines
     * + + + + + + + + + + + + + + + +
     * */

    //A helper function
    private Machine getVmByName(String vmName){
        return machines.stream().filter(m -> m.getName().equals(vmName)).findAny().orElse(null);
    }

    private Store getStoreByName(String storeName){
        return stores.stream().filter(s -> s.getName().equals(storeName)).findAny().orElse(null);
    }

    //Extract first number occurrence from a string(To get RAM and HDD size)
    private int extractNumberValue(String input){
        int size = -1;
        //1st METHOD: Via String Manipulation
        StringBuilder sb = new StringBuilder(input);
        sb.reverse().delete(0,2).reverse();
        size = Integer.parseInt(sb.toString());

        //2nd METHOD:  Via Regex
        /*Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(input);
        while (m.find()) {
            size = Integer.parseInt(m.group());
            break;//To not continue after some "Alpha chars" (in the case of some malicious inputs[50gb75])
        }*/
        return size;
    }

    //Create a new Machine
    public void createMachine(String vmName, String os, String hdd, String ram){
        machines.add(new Machine(vmName,os,hdd,ram));
    }

    //Start a not-running machine
    public void startMachine(String vmName) throws MachineStateException{
        Machine vm = getVmByName(vmName);
        if(vm.getState()==StateMachine.RUNNING)
            throw new MachineStateException();
        if(vm != null) //If there is no VM ith the given name
            vm.setState(StateMachine.RUNNING);
    }

    //Stop a running machine
    public void stopMachine(String vmName){
        Machine vm = getVmByName(vmName);
        if(vm != null) //If there is no VM ith the given name
            vm.setState(StateMachine.STOPPED);
    }

    //List all the Cloud's machines
    public String listMachines(){
        StringBuilder sb = new StringBuilder();
        Iterator<Machine> vmsIterator = machines.iterator();

        while (vmsIterator.hasNext()){
            Machine vm = vmsIterator.next();
            sb.append(vm.getName()).append(":").append(vm.getState().toString().toLowerCase());
            //Put the " || " VMs separator
            if(vmsIterator.hasNext())
                sb.append("||");
        }
        return sb.toString();
    }

    //Get the used memory
    public int usedMemory(String vmName){
        Machine vm = getVmByName(vmName);
        //TODO:extract the number from the string
        return (vm.getState() == StateMachine.RUNNING)?extractNumberValue(vm.getRam()):0;
    }

    //Get the used disk for a VM && Get the used Storage for a store
    public double usedDisk(String name){
        Machine vm = getVmByName(name);
        Store store = getStoreByName(name);
        double usedDisk = 0;

        if(vm != null)// A VM
            //TODO:extract the number from the string
            usedDisk = extractNumberValue(vm.getDiskSpace());
        else{//A Store
            double storeSize = store.getFileNames().size() * 0.100;
            usedDisk = storeSize;
        }

        return usedDisk;
    }

    //Get the global used disk
    public double globalUsedDisk(){
        double gUsedDisk = 0;
        //The usedDisk by stores
        for (Store store:stores) {
            gUsedDisk += usedDisk(store.getName());
        }
        //The usedDisk by VMs
        if(!machines.isEmpty())
            for (Machine vm:machines) {
                gUsedDisk += usedDisk(vm.getName());
            }
        return gUsedDisk;
    }

    //Get the global used memory by VMs
    public double globalUsedMemory(){
        double gUsedMemory = 0;
        if(!machines.isEmpty())
            for (Machine vm:machines) {
                gUsedMemory += usedMemory(vm.getName());
            }
        return gUsedMemory;
    }
}