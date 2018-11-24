package ma.sqli.tests.cloudinfrastructure;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

/**
 * L’objectif de cet exercice est de construire un outil léger pour gérer une infrastructure cloud.
 * On se limitera dans cet outil de gérer des espaces de stockage (comme Google Drive), et des
 * machines virtuelles (comme Google Cloud Platform), via quelques opérations simple de gestion.
 *
 * The goal of this exercise is to build a lightweight tool to handle a cloud infrastructure. We
 * will limit ourselves in this tool to manage storages (like Google Drive), and virtual machines
 * (like Google Cloud Platform), using some simple management operations.
 *
 
 * 
 */
public class CloudInfrastructureTest {

    /**
     * This is the main Cloud class. You will need to create it and implement it. The cloud class
     * can also call other classes that you can create if needed.
     */
    private CloudInfrastructure cloud = new CloudInfrastructure();

    /**
     * Create Store in the cloud, identified by its name. Upload documents in that storage.
     * 
     * the method cloud.listStores() display all existing stores with all their content.
     */
    @Test
    public void can_create_a_store_in_cloud() {
        cloud.createStore("myFiles");
        cloud.uploadDocument("myFiles", "book.pdf"); // upload "book.pdf" in the "myFiles" store

        String expected = "myFiles:book.pdf";
        assertEquals(expected, cloud.listStores()); // make sure the cloud.listStores() method
        // return the expected String
    }

    /**
     * We can create multiple stores in the cloud, and upload several documents to each one of them.
     */
    @Test
    public void can_create_multiple_stores_in_cloud() {
        cloud.createStore("myFiles");
        cloud.createStore("myImages");
        cloud.uploadDocument("myImages", "picture.jpeg", "profile.png");

        String expected = "myFiles:empty||myImages:picture.jpeg, profile.png"; // an empty store is display as "empty"
        assertEquals(expected, cloud.listStores());
    }

    /**
     * We can also delete or empty a store. When a store does not contain any documents, "empty" is
     * displayed.
     */
    @Test
    public void can_delete_and_empty_stores_in_cloud() {
        cloud.createStore("myFiles");
        cloud.createStore("myImages");
        cloud.uploadDocument("myImages", "picture.jpeg", "profile.png");

        cloud.deleteStore("myFiles"); // delete a store
        assertEquals("myImages:picture.jpeg, profile.png", cloud.listStores());

        cloud.emptyStore("myImages"); // empty a store
        assertEquals("myImages:empty", cloud.listStores()); // an empty store is display as "empty"
    }

    /**
     * The creation of a store with a name that is already used will throw the CreateStoreException.
     */
    @Test(expected = CreateStoreException.class)
    public void cannot_create_stores_with_same_names() {
        cloud.createStore("myFiles");
        cloud.createStore("myFiles"); // will throw the exception
    }

    /**
     * We move here to the second part of the test, virtual machines (VM). We can create several VMs
     * in the cloud. Each VM can have three possible statuses : Inactive, Running or Stopped. A new
     * VM is always Inactive at its creation. We can then start or stop it.
     */
    @Test
    public void create_machines() {
        // create a new machine takes 4 parameters : name, operating system, disk size, memory.
        cloud.createMachine("machine1", "Linux", "50gb", "8gb");
        cloud.createMachine("machine2", "Windows", "20gb", "4gb");

        // Remember, all machines are inactive by default.
        assertEquals("machine1:inactive||machine2:inactive", cloud.listMachines());


        cloud.startMachine("machine1"); // start the machine "machine1"
        assertEquals("machine1:running||machine2:inactive", cloud.listMachines());

        cloud.startMachine("machine2");
        cloud.stopMachine("machine1"); // stop machine "machine1"
        assertEquals("machine1:stopped||machine2:running", cloud.listMachines());
    }

    /**
     * Trying to start an already running VM will throw a MachineStateException
     */
    @Test(expected = MachineStateException.class)
    public void cannot_launch_already_started_machine() {
        cloud.createMachine("machine1", "Linux", "50gb", "8gb");
        cloud.startMachine("machine1");
        assertEquals("machine1:running", cloud.listMachines());

        cloud.startMachine("machine1"); // will throw the exception
    }

    /**
     * For every VM, we can check the used Disk and memory. The memory is consumed only when a
     * machine is running. The disk size is always used, even if the VM is not running.
     */
    @Test
    public void can_check_used_disk_and_ram_per_machine() {
        cloud.createMachine("machine1", "Linux", "50gb", "8gb");
        assertEquals("machine1:inactive", cloud.listMachines());

        assertEquals(0, cloud.usedMemory("machine1"), PRECISION); // Only running machines consume memory
        assertEquals(50, cloud.usedDisk("machine1"), PRECISION); // the disk is always consumed

        cloud.startMachine("machine1");
        assertEquals(50, cloud.usedDisk("machine1"), PRECISION);
        // as the machine is now running, all its memory is used.
        assertEquals(8, cloud.usedMemory("machine1"), PRECISION);

        cloud.stopMachine("machine1");
        assertEquals(50, cloud.usedDisk("machine1"), PRECISION);
        // The memory will be released as the machine has been stopped
        assertEquals(0, cloud.usedMemory("machine1"), PRECISION);
    }

    /**
     * Same as VMs, we can check the used disk in a storage. A storage does not consume any memory,
     * only disk space.
     * 
     * To simplify the exercise, we will suppose that all documents have one size = 100mb = 0.100gb.
     * The disk used by a store is the sum of the sizes of all documents inside.
     */
    @Test
    public void can_check_used_disk_per_store() {
        cloud.createStore("myImages");
        cloud.uploadDocument("myImages", "picture.jpeg");
        assertEquals("myImages:picture.jpeg", cloud.listStores());

        // One document exists in "myImages", the used disk should be 0.1gb
        Assert.assertEquals(0.100, cloud.usedDisk("myImages"), PRECISION);

        cloud.uploadDocument("myImages", "profile.png");
        assertEquals("myImages:picture.jpeg, profile.png", cloud.listStores());

        // 2 documents, used disk = 200mb
        assertEquals(0.200, cloud.usedDisk("myImages"), PRECISION);
    }

    /**
     * In this test, we can check the used disk and used memory of all machines and stores existing
     * in the cloud.
     * 
     */
    @Test
    public void can_check_aggregated_data_for_all_machines_and_stores() {
        cloud.createMachine("machine1", "Linux", "50gb", "8gb");
        cloud.createMachine("machine2", "Windows", "20gb", "4gb");
        assertEquals("machine1:inactive||machine2:inactive", cloud.listMachines());

        // globalUsedDisk method should return the used disk of all machines and stores existing in the cloud, same for globalUsedMemory
        // for now 2 machines exists, with 50gb and 20gb disk sizes = 70gb
        assertEquals(70, cloud.globalUsedDisk(), PRECISION);
        assertEquals(0, cloud.globalUsedMemory(), PRECISION); // machines are inactive, no memory is used

        cloud.startMachine("machine1");
        assertEquals(70, cloud.globalUsedDisk(), PRECISION);
        assertEquals(8, cloud.globalUsedMemory(), PRECISION);

        cloud.startMachine("machine2");
        assertEquals(70, cloud.globalUsedDisk(), PRECISION);
        assertEquals(12, cloud.globalUsedMemory(), PRECISION);

        cloud.createStore("myImages");
        cloud.uploadDocument("myImages", "picture.jpeg");
        assertEquals("myImages:picture.jpeg", cloud.listStores());

        assertEquals(70.100, cloud.globalUsedDisk(), PRECISION);
        assertEquals(12, cloud.globalUsedMemory(), PRECISION);

        cloud.stopMachine("machine1");
        assertEquals(70.100, cloud.globalUsedDisk(), PRECISION);
        assertEquals(4, cloud.globalUsedMemory(), PRECISION);

        cloud.emptyStore("myImages");
        assertEquals(70, cloud.globalUsedDisk(), PRECISION);
        assertEquals(4, cloud.globalUsedMemory(), PRECISION);
    }

    // Used only to compare double, you can totally ignored it
    private static final double PRECISION = 0.00001;
}
