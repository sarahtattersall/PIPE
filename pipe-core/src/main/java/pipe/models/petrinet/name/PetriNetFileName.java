package pipe.models.petrinet.name;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class PetriNetFileName implements PetriNetName {
    private File file;

    public PetriNetFileName(File file) {
        this.file = file;
    }

    public String getPath() {
        return file.getAbsolutePath();
    }

    @Override
    public String getName() {
        return FilenameUtils.removeExtension(file.getName());
    }

    @Override
    public void visit(NameVisitor visitor) {
        if (visitor instanceof FileNameVisitor) {
            ((FileNameVisitor) visitor).visit(this);
        }
    }

    public File getFile() {
        return file;
    }
}
