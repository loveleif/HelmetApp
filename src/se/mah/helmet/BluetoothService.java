package se.mah.helmet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * This Service class handles the bluetooth interface to the embedded system.
 * 
 * This class is partly influenced by sample code from android.google.com, see
 * http://developer.android.com/resources/samples/BluetoothChat
 * 
 */
public abstract class BluetoothService {
	private static final String TAG = BluetoothService.class.getSimpleName();

	private final BluetoothAdapter adapter;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	// The "well known SPP UUID", kanske inte stämmer
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Buffer size for the bluetooth input
	private static final int BUFFER_SIZE = 1024;

	private int state;

	private static final int STATE_OFF = 1;
	private static final int STATE_ON = 2;
	
	private static final byte END_OF_TRANSMISSION = 4;

	public BluetoothService() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		state = STATE_OFF;
	}

	public synchronized void connect(BluetoothDevice device) {
		disconnect();

		connectThread = new ConnectThread(device);
		connectThread.start();
	}

	/**
	 * This method is run from the ConnectionThread when a connection has been
	 * established.
	 * 
	 * @param socket
	 *            the socket of the connection
	 * @param device
	 *            the connected device
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		// TODO Använda device för att visa namnet på den anslutna enheten i
		// GUI?
		// Cancel any thread currently running a connection
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}

		// Connect
		connectedThread = new ConnectedThread(socket, BUFFER_SIZE);
		state = STATE_ON;
		connectedThread.start();
	}
	
	/**
	 * This method is run when the bluetooth connection fails.
	 */
	public void connectionFailed() {
		// TODO Auto-generated method stub
	}

	/**
	 * This method is run when the bluetooth connection is lost.
	 */
	public void connectionLost(BluetoothDevice lostDevice) {		
		if (state == STATE_OFF)
			return;
		
		try {
			// Sleep before reconnect to save battery
			Thread.sleep(5000);
		} catch (InterruptedException e) { }
		
		// Try reconnecting
		connect(lostDevice);
	}
	
	/**
	 * Disconnect.
	 */
	public void disconnect() {
		Log.i(TAG, "Disconnecting bluetooth.");
		state = STATE_OFF;
		
		// Cancel any ongoing connection attemps
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
		// Cancel any established connections
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
	}
	
	/**
	 * Write data to bluetooth.
	 * @param data data to write
	 */
	public void writeData(byte[] data) {
		connectedThread.write(data);
	}
	
	/**
	 * Override this method to handle recieved data.
	 * 
	 * @param buffer data buffer
	 * @param size size of read data
	 */
	public abstract void recieveData(byte[] buffer, int size);

	/**
	 * Thread for establishing a connection to a BluetoothDevice.
	 * 
	 */
	private class ConnectThread extends Thread {
		private final BluetoothDevice device;
		private final BluetoothSocket socket;

		public ConnectThread(BluetoothDevice device) {
			this.device = device;
			BluetoothSocket tmp = null;
			try {
				tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				Log.e(TAG, "Failed to create BluetoothSocket with device "
						+ device);
			}
			socket = tmp;
		}

		@Override
		public void run() {
			Log.d(TAG, "Connecting to BluetoothDevice...");
			setName("BluetoothConnectThread");

			// Always cancel discovery because it will slow down a connection
			adapter.cancelDiscovery();

			try {
				socket.connect();
			} catch (IOException e) {
				Log.e(TAG, "Failed to establish connection to BluetoothDevice "
						+ device);
				try {
					socket.close();
				} catch (IOException e1) {
				}
				connectionFailed();
			}

			// Remove reference to ConnectThread because we're done.
			synchronized (BluetoothService.this) {
				connectThread = null;
			}

			connected(socket, device);
		}

		public void cancel() {
			// TODO
		}
	}

	/**
	 * Thread for managing input and output from a BluetoothSocket.
	 * 
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket socket;
		private final int bufferSize;
		private final InputStream input;
		private final OutputStream output;

		public ConnectedThread(BluetoothSocket socket, int bufferSize) {
			this.bufferSize = bufferSize;
			this.socket = socket;
			InputStream tmpInput = null;
			OutputStream tmpOutput = null;
			try {
				tmpInput = socket.getInputStream();
				tmpOutput = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "Failed to open Input/Output stream.");
			}
			input = tmpInput;
			output = tmpOutput;
		}

		/*
		 * Listens to incoming data.
		 */
		@Override
		public void run() {
			Log.d(TAG, "Listening on Bluetooth channel.");
			byte[] buffer = new byte[bufferSize];
			int size = 0;
			int offset = 0;
			
			while (true) {
				try {
					// Read from input to buffer until last byte is END_OF_TRANSMISSION
					// (all messages from the embedded system are terminated with END_OF_TRANSMISSION)
					offset = 0;
					do {
						size = input.read(buffer, offset, buffer.length - offset);
						Log.d(TAG, "BT: Read " + size + " bytes.");
						if (size > 0)
							offset += size;
					} while (buffer[offset - 1] != END_OF_TRANSMISSION);
					recieveData(buffer, offset - 1);
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost(socket.getRemoteDevice());
					break;
				}
			}
		}

		/**
		 * Send data to the connected BluetoothDevice.
		 * 
		 * @param data
		 *            data to send
		 */
		public void write(byte[] data) {
			try {
				output.write(data);
			} catch (IOException e) {
				Log.e(TAG,
						"Failed to send data to BluetoothDevice using socket "
								+ socket);
			}
		}
		
		/**
		 * Close connection.
		 */
		public void cancel() {
			try {
				socket.close();
			} catch (IOException e) {
				Log.e(TAG, "Failed to close BluetoothSocket.");
			}
		}
	}
}
