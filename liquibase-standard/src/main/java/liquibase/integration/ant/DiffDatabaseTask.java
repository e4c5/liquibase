package liquibase.integration.ant;

import liquibase.GlobalConfiguration;
import liquibase.diff.DiffResult;
import liquibase.diff.output.report.DiffToReport;
import liquibase.exception.DatabaseException;
import lombok.Getter;
import lombok.Setter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

@Setter
public class DiffDatabaseTask extends AbstractDatabaseDiffTask {
    private FileResource outputFile;

    @Getter
    private String outputEncoding = GlobalConfiguration.OUTPUT_FILE_ENCODING.getCurrentValue();

    @Override
    public void executeWithLiquibaseClassloader() throws BuildException {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(outputFile.getOutputStream(), true, getOutputEncoding());
            DiffResult diffResult = getDiffResult();
            DiffToReport diffReport = new DiffToReport(diffResult, printStream);
            log("Writing diff report " + outputFile.toString(), Project.MSG_INFO);
            diffReport.print();
        } catch (DatabaseException e) {
            throw new BuildException("Unable to make diff report: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new BuildException("Unable to make diff report. Encoding [" + outputEncoding + "] is not supported.", e);
        } catch (IOException e) {
            throw new BuildException("Unable to make diff report. Error opening output stream.", e);
        } finally {
            FileUtils.close(printStream);
        }
    }

    @Override
    protected void validateParameters() {
        super.validateParameters();

        if(outputFile == null) {
            throw new BuildException("Unable to make diff report. Output file is required.");
        }
    }

}
