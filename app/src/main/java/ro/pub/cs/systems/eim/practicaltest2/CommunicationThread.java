package ro.pub.cs.systems.eim.practicaltest2;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // flux de intrare / iesire
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client(operators/operation)");
            // get info from client in order to make the request to the weather service
            String operator1 = bufferedReader.readLine();
            String operator2 = bufferedReader.readLine();
            String operation = bufferedReader.readLine();
            if (operator1 == null || operator1.isEmpty() || operator2 == null || operator2.isEmpty()
                || operation == null || operation.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client");
                return;
            }


            // execute operation
            String result = null;
            switch(operation) {
                case Constants.ADD:
                    result = String.valueOf(Integer.parseInt(operator1) + Integer.parseInt(operator2));
                    break;
                case Constants.MULTIPLY:
                    result = String.valueOf(Integer.parseInt(operator1) * Integer.parseInt(operator2));
                    Thread.sleep(2000);
                    break;

                default:
                    result = "[COMMUNICATION THREAD] Wrong information";
            }
            // write result to oommunication channel
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}