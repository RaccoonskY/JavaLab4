package MyTCP;
import java.io.FileWriter;
import java.io.IOException;

public class MyLogger {

    FileWriter fw;
    public MyLogger(String path_to_file) throws IOException {
        fw = new FileWriter(path_to_file);
    }

    public void LogString(String logged_string)  {
        try {
            fw.write(logged_string+"\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void CloseFile() throws IOException {
        fw.close();
    }
}