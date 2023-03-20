package Example;

import java.io.*;
import java.net.*;

public class UDPServer {
    public static final int LENGTH_PACKET = 30;
    public static final String HOST = "localhost";
    public static final int PORT = 2345;
    public static final byte[] answer = (" приписка от сервера").getBytes();
    public static void main(String[] args) {
        DatagramSocket servSocket = null;
        DatagramPacket datagram;
        InetAddress clientAddr;
        int clientPort;
        byte[] data;
        try{
            servSocket = new DatagramSocket(PORT);
        }catch(SocketException e){
            System.err.println("Не удаётся открыть сокет : " + e.toString());
        }
        while(true){
            try{
// ------------------------------------------------------------
//очень... долгий цикл
//приём данных от клиента
// ------------------------------------------------------------
                data = new byte[LENGTH_PACKET];
                datagram = new DatagramPacket(data, data.length);
                servSocket.receive(datagram);
                System.out.println("Принято от клиента: " + (new String(datagram.getData())).trim());
// ------------------------------------------------------------
//отправка данных клиенту
//адрес и порт можно вычислить из предыдущей сессии приёма, через
//объект класс DatagramPacket - datagram
// ------------------------------------------------------------
                clientAddr = datagram.getAddress();
                clientPort = datagram.getPort();
// ------------------------------------------------------------
//приписывание к полученному сообщению текста " приписка от сервера" и отправка
//результата обратно клиенту
// ------------------------------------------------------------
                datagram.setData(((new String(datagram.getData())).trim()).getBytes());
                data = getSendData(datagram.getData());
                datagram = new DatagramPacket(data, data.length, clientAddr, clientPort);
                servSocket.send(datagram);
            }catch(IOException e){
                System.err.println("io исключение : " + e.toString());
            }
        }
    }
    // ------------------------------------------------------------
//копирование, работа с массивом типа byte
    protected static byte[] getSendData(byte[] b){
        byte[] result = new byte[b.length + answer.length];
        System.arraycopy(b, 0, result, 0, b.length);
        System.arraycopy(answer, 0, result, b.length, answer.length);
        return result;
    }
}