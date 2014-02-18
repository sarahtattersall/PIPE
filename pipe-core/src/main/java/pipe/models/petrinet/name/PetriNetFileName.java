package pipe.models.petrinet.name;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class PetriNetFileName implements PetriNetName {
    private File file;

    public PetriNetFileName(File file) {
        this.file = file;
    }


    @Override
    public String getName() {
        return FilenameUtils.removeExtension(file.getName());
    }
}
