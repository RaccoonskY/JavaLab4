package MyTCP;

import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

public class MyClient implements Runnable {
    public static  int PORT = 2500;
    public static  String HOST = "localhost";
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private MyLogger logger;

    public MyClient( String _HOST, int _PORT, String path_to_file){

        PORT = _PORT;
        HOST = _HOST;

        try {
            logger = new MyLogger(path_to_file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void run(){
        try {
            Scanner cnl_scanner = new Scanner(System.in);
            clientSocket = new Socket(HOST, PORT);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while(true) {
                System.out.println("Enter the new request:");
                var client_req = cnl_scanner.nextLine();
                out.println(client_req);
                if(".".equals(client_req)){
                    logger.CloseFile();
                    break;
                }
                var response = in.readLine();
                while (!"$".equals(response)){

                    logger.LogString(response);
                    response = in.readLine();
                }
                Thread.yield();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        MyClient ja = new MyClient( args[0], Integer.parseInt(args[1]), args[2]);
        Thread th = new Thread(ja);
        th.start();
    }
}