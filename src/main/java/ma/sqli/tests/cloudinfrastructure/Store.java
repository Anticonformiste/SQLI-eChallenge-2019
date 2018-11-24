package ma.sqli.tests.cloudinfrastructure;

import java.util.ArrayList;
import java.util.Collection;

public class Store {
    private String name;
    private Collection<String> fileNames;

    public Store(String name) {
        this.name = name;
        fileNames = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(Collection<String> fileNames) {
        this.fileNames = fileNames;
    }
}
