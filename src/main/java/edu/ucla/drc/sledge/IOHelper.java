package edu.ucla.drc.sledge;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucla.drc.sledge.project.ProjectModel;
import edu.ucla.drc.sledge.project.ProjectModelBuilderFromJson;
import edu.ucla.drc.sledge.project.ProjectModelBuilderToJson;

import java.io.File;
import java.io.IOException;

public class IOHelper {

    public void saveModelToFile (ProjectModel model, File outFile) throws IOException {
        ProjectModel.Exporter exporter = new ProjectModelBuilderToJson();
        model.exportTo(exporter);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, exporter);
    }

    public ProjectModel loadModelFromFile (File inFile) throws InvalidFileFormatException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ProjectModelBuilderFromJson builder = mapper.readValue(inFile, ProjectModelBuilderFromJson.class);
            return builder.toModel();
        } catch (JsonParseException ex) {
            JsonLocation location = ex.getLocation();
            throw new InvalidFileFormatException(inFile, location.getLineNr(), location.getCharOffset());
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class InvalidFileFormatException extends Exception {
        private final File inFile;
        private final int lineNo;
        private final long charOffset;

        public InvalidFileFormatException (File inFile, int lineNo, long charOffset) {
            this.inFile = inFile;
            this.lineNo = lineNo;
            this.charOffset = charOffset;
        }

        public File getInFile() {
            return inFile;
        }

        public int getLineNo() {
            return lineNo;
        }

        public long getCharOffset() {
            return charOffset;
        }
    }
}
