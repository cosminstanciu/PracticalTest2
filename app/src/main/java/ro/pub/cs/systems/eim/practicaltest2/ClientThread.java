package ro.pub.cs.systems.eim.practicaltest2;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String operator1;
    private String operator2;
    private String operation;
    private TextView resultTextView;

    private Socket socket;

    public ClientThread(String address, int port, String operator1, String operator2, String operation, TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.operation = operation;
        this.resultTextView = resultTextView;
    }

    @Override
    public void run() {
        try{
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            // put info from activity to communication channel
            printWriter.println(operator1);
            printWriter.flush();
            printWriter.println(operator2);
            printWriter.flush();
            printWriter.println(operation);
            printWriter.flush();
            final String result;

            // get result and put it to screen
            result = bufferedReader.readLine();
            resultTextView.post(new Runnable() {
                @Override
                public void run() {
                    resultTextView.setText(result);
                }
            });

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
