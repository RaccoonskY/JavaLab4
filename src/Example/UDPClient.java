package Example;

import java.io.*;
import java.net.*;

public class UDPClient {
    public static final int LENGTH_PACKET = 60;
    public static final String HOST = "localhost";
    public static final int PORT = 2345;
    public static void main(String[] args) {
        try{
//-----------------------------------------------------------------
//отправка сообщения на сервер
//-----------------------------------------------------------------
            byte data[] = ("hello!!! % 1234 5-9*6;").getBytes();
            InetAddress addr = InetAddress.getByName(HOST);
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(data, data.length, addr, PORT);
            socket.send(packet);
            System.out.println("Сообщение отправлено...");
//-----------------------------------------------------------------
//приём сообщения с сервера
//-----------------------------------------------------------------
            byte data2[];
            data2 = new byte[LENGTH_PACKET];
            packet = new DatagramPacket(data2, data2.length);
            socket.receive(packet);
            System.out.println((new String(packet.getData())).trim());
//-----------------------------------------------------------------
//закрытие сокета
//-----------------------------------------------------------------
            socket.close();
        }catch(SocketException e){e.printStackTrace();
        }catch(IOException e){e.printStackTrace();}
    }
}