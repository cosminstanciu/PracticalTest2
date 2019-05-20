package ro.pub.cs.systems.eim.practicaltest2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest2MainActivity extends AppCompatActivity {
    //server
    private EditText serverPortEditText = null;
    private Button startServerButton = null;

    //client
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText operator1EditText = null;
    private EditText operator2EditText = null;
    private Spinner operationTypeSpinner = null;
    private Button submitButton = null;
    private TextView resultTextView = null;

    // client and server threads
    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    // button listeners
    private StartServerButtonClickListener startServerButtonClickListener = new StartServerButtonClickListener();
    private class StartServerButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Start Server
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private SubmitButtonClickListener submitButtonClickListener = new SubmitButtonClickListener();
    private class SubmitButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            // get value of fields entered by user
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String operator1 = operator1EditText.getText().toString();
            String operator2 = operator2EditText.getText().toString();
            String operationType = operationTypeSpinner.getSelectedItem().toString();
            if (operator1 == null || operator1.isEmpty()
                    || operator2 == null || operator2.isEmpty()
                    || operationType == null || operationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (operator1 / operator2 / operationType) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            resultTextView.setText("");

            // start client
            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), operator1, operator2, operationType, resultTextView);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test2_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        startServerButton = findViewById(R.id.start_server_button);
        startServerButton.setOnClickListener(startServerButtonClickListener);

        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);

        operator1EditText = findViewById(R.id.operator1_edit_text);
        operator2EditText = findViewById(R.id.operator2_edit_text);
        operationTypeSpinner = findViewById(R.id.operation_type_spinner);
        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(submitButtonClickListener);

        resultTextView = findViewById(R.id.result_text_view);

    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
