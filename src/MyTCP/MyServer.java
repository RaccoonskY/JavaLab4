package MyTCP;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MyServer {
    public static  int PORT = 2500;
    private static final int SIZE = 10;
    private ServerSocket servSocket;


    static Float[][] float_arr;
    static Integer[][] int_arr;
    static String[][] str_arr;



    public static Map<String, ArrayList<String>> prohibited_dict;
    public MyLogger logger;

    public static  <T> void PrintArray(T[] @NotNull [] arrToPrint){
        StringBuilder strViewArr = new StringBuilder();
        for (var i:arrToPrint) {
            for (var j:i) {
                if (j != null){
                    strViewArr.append(j.toString());
                    strViewArr.append(" ");
                }
            }
            strViewArr.append("\n");
        }
        System.out.println(strViewArr);
    }


    public static  <T> String GetStringArr(T[] @NotNull [] arrToPrint){
        StringBuilder strViewArr = new StringBuilder();
        for (var i:arrToPrint) {
            for (var j:i) {
                if (j != null){
                    strViewArr.append(j.toString());
                    strViewArr.append(" ");
                }
            }
            strViewArr.append("\n");
        }
        return strViewArr.toString();
    }

    public static void main(String[] args) {
        float_arr = new Float[2][SIZE];
        int_arr = new Integer[2][SIZE];
        str_arr = new String[2][SIZE];

        prohibited_dict = new HashMap<String,ArrayList<String>>();

        PrintArray(float_arr);
        int _PORT;
        try {
            FileReader setting_fr = new FileReader("C:\\Users\\Victor\\Desktop\\3course\\6сем\\Java\\лабы\\lab4\\JavaLab4\\src\\MyTCP\\server_setting.txt");
            Scanner settig_scn = new Scanner(setting_fr);
             _PORT = settig_scn.nextInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Scanner cnl_scanner = new Scanner(System.in);
        System.out.println("Enter the absolute path of the log file:");
        var path_to_log = cnl_scanner.nextLine();

        System.out.println("Enter 'STRING a b' values to prohibit changing (. - to stop):");
        String prohb_str = cnl_scanner.nextLine();
        while ( !prohb_str.equals(".")){
            var prohb_splitted = prohb_str.split(" ");
            String values =prohb_splitted[1]+" "+ prohb_splitted[2];
            if (!prohibited_dict.containsKey(prohb_splitted[0]))
            {
                prohibited_dict.put(prohb_splitted[0], new ArrayList<>());
            }
            prohibited_dict.get(prohb_splitted[0]).add(values);
            prohb_str = cnl_scanner.nextLine();
        }

        MyServer tcpServer = new MyServer(_PORT, path_to_log);
        tcpServer.go();

    }

    public MyServer(Integer _PORT, String path_to_log) {

        try {
            logger = new MyLogger(path_to_log);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PORT = _PORT;
        try {
            servSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Unable to open socket for the server: " + e.toString());
        }
    }



    public void go() {
        class Listener implements Runnable {
            private Socket clientSocket;
            private PrintWriter out;
            private BufferedReader in;

            public Listener(Socket aSocket) {
                clientSocket = aSocket;
            }

            public void run() {
                try {
                    System.out.println("User connected");
                    logger.LogString("User connected");

                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("New request: "+inputLine);
                        logger.LogString("New request: "+inputLine);
                        if (".".equals(inputLine)) {
                            logger.CloseFile();
                            out.println("good bye");
                            break;
                        }
                        var splitRequest = inputLine.split(" ");
                        if ("GET".equals(splitRequest[0])) {
                            if ("ALL".equals(splitRequest[1]))
                            {
                                out.println("All arrays are:");
                                out.println(GetStringArr(float_arr) + GetStringArr(str_arr) + GetStringArr(int_arr));
                                out.println("$");


                            }
                            else {
                                out.println("SEND i and j to get elem from array as 'i j': ");
                                out.println("$");
                                inputLine = in.readLine();
                                var splitedInput = inputLine.split(" ");
                                var i = Integer.parseInt(splitedInput[0]);
                                var j = Integer.parseInt(splitedInput[0]);

                                String response = null;
                                switch (splitRequest[1]) {
                                    case ("FLOAT") -> {
                                        if (float_arr[i][j] != null){
                                            response = float_arr[i][j].toString();
                                        }
                                    }
                                    case ("STRING") -> {
                                        if (str_arr[i][j] != null){
                                            response = str_arr[i][j];
                                        }

                                    }
                                    case ("INT") -> {
                                        if (int_arr[i][j] != null){
                                            response = int_arr[i][j].toString();
                                        }

                                    }
                                    default -> out.println("Your GET command cannot be handled");
                                }
                                if (response!=null){
                                    out.println("The result of search is: "+response);
                                }
                                out.println("$");
                            }
                        }
                        else if ("POST".equals(splitRequest[0])) {
                            out.println("SEND i and j to post element to array as 'i j': ");
                            out.println("$");
                            inputLine = in.readLine();
                            var splitedInput = inputLine.split(" ");
                            var i = Integer.parseInt(splitedInput[0]);
                            var j = Integer.parseInt(splitedInput[1]);
                            String index_values = splitedInput[0]+" "+splitedInput[1];

                            if (prohibited_dict.containsKey(splitRequest[1])) {
                                if(prohibited_dict.get(splitRequest[1]).contains(index_values)){
                                    out.println("THIS ELEMENT IS PROHIBITED!\nACCESS DENIED");
                                    out.println("$");
                                }
                                else {
                                    out.println("Send new elem: ");
                                    out.println("$");
                                    var new_elem = in.readLine();
                                    switch (splitRequest[1]) {
                                        case ("FLOAT") -> {
                                            float_arr[i][j] = Float.parseFloat(new_elem);
                                            out.println("New array is:");
                                            out.println(GetStringArr(float_arr));
                                            System.out.println(GetStringArr(float_arr));
                                            logger.LogString(GetStringArr(float_arr));
                                        }
                                        case ("STRING") -> {
                                            str_arr[i][j] = new_elem;
                                            out.println("New array is:");
                                            out.println(GetStringArr(str_arr));
                                            System.out.println(GetStringArr(str_arr));
                                            logger.LogString(GetStringArr(str_arr));
                                        }
                                        case ("INT") -> {
                                            int_arr[i][j] = Integer.parseInt(new_elem);
                                            out.println("New array is:");
                                            out.println(GetStringArr(int_arr));
                                            System.out.println(GetStringArr(int_arr));
                                            logger.LogString(GetStringArr(int_arr));
                                        }
                                        default -> out.println("Your POST command cannot be handled");
                                    }
                                    out.println("$");
                                }
                            }

                        }
                    }

                    try {
                        logger.CloseFile();
                        in.close();
                        out.close();
                        clientSocket.close();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
                    System.err.println("Exception: " + e.toString());
                }
            }
        }

        System.out.println("Server launched...");
        logger.LogString("Server launched...");
        while (true) {
            try {
                Socket socket = servSocket.accept();
                Listener listener = new Listener(socket);
                Thread thread = new Thread(listener);
                thread.start();
            } catch (IOException e) {
                System.err.println("Exception: " + e.toString());
            }
        }
    }
}


/*C:\Users\Victor\Desktop\3course\6сем\Java\лабы\lab4\JavaLab4\src\MyTCP\MyClient.java*/