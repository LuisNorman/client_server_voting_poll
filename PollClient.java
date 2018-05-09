package com.norman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PollClient extends Thread{
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private byte[] buffer = new byte[4000];
    private boolean running;

    public PollClient () {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
            port = 4000;
            buffer = ("I want to vote").getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4000);
            socket.send(packet);
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void run() {
        running = true;
        buffer = new byte[4000];
        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            try {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);

                if (received.equals("Vote (1) yes, (2) no, (3) don't care")) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    String out = in.readLine();
                    buffer = out.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, address, port);
                    buffer = new byte[4000];
                    socket.send(packet);
                }

                String clkBtn = "\nEnter the word 'view yes' to view all yes results\n" +
                        "Enter the word 'view no' to view all no results\n" +
                        "Enter the word 'view dc' to view all don't care results\n" +
                        "Enter the word 'end' to exit the polls\n";

                if (received.equals(clkBtn)) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    String out = in.readLine();
                    if (out.equals("end")) {
                        running = false;
                        continue;
                    }
                    buffer = out.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, address, port);
                    buffer = new byte[4000];
                    socket.send(packet);
                }



            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}
