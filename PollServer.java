package com.norman;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class PollServer extends Thread {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private boolean running;
    private byte[] buffer = new byte[4000];
    private int yes = 0;
    private int no = 0;
    private int dc = 0;

    public PollServer() throws SocketException {
        socket = new DatagramSocket(4000);
    }

    public void run() {
        running = true;
        packet = new DatagramPacket(buffer, buffer.length);


        while (running) {
            buffer = new byte[4000];
            packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String received = new String(packet.getData(), 0, packet.getLength());

                if (received.equals("I want to vote")) {
                    System.out.println(received);
                    buffer = ("Vote (1) yes, (2) no, (3) don't care").getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                    socket.send(packet);
                }

                if (received.equals("1")) {
                    yes++;
                    sendConfirmation();
                    sendViewOptions();
                }

                if (received.equals("2")) {
                    no++;
                    sendConfirmation();
                    sendViewOptions();
                }

                if (received.equals("3")) {
                    dc++;
                    sendConfirmation();
                    sendViewOptions();
                }

                if (received.equals("view yes")) {
                    sendResults(yes, "Yes");
                }

                if (received.equals("view no")) {
                    sendResults(no, "No");
                }

                if (received.equals("view dc")) {
                    sendResults(dc, "Don't Care");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendConfirmation() throws IOException {
        buffer = ("\nCongratulations you have voted!").getBytes();
        packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
        socket.send(packet);

    }

    public void sendViewOptions() throws IOException {
        buffer = ("\nEnter the word 'view yes' to view all yes results\n" +
                "Enter the word 'view no' to view all no results\n" +
                "Enter the word 'view dc' to view all don't care results\n" +
                "Enter the word 'end' to exit the polls\n").getBytes();
        packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
        socket.send(packet);
    }

    public void sendResults(int result, String title) throws IOException {
        buffer = ("\n"+title+" votes: " ).getBytes();
        packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
        socket.send(packet);

        buffer = new byte[4000];
        buffer = Integer.toString(result).getBytes();
        packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
        socket.send(packet);
        sendViewOptions();
    }
}
