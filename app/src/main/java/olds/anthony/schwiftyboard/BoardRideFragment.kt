package olds.anthony.schwiftyboard

import android.bluetooth.*
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_board_ride.*
import java.util.*

/**
 * Created by antho on 2/15/2018.
 */
class BoardRideFragment: Fragment(), SensorEventListener {

    private val TAG = "BoardRideFragment"
    //This is the characteristic used to set the color of the light.
    private val CHANGE_COLOR_INSTANCE_ID = 46
    private val CHANGE_COLOR_VALUE_PREFIX = "56"
    private val CHANGE_COLOR_VALUE_SUFFIX = "00f0aa"

    var m_DeviceAddress : String? = null

    var m_LEDevice : BluetoothDevice? = null
    private var m_BluetoothGatt : BluetoothGatt? = null
    private val m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var m_ChangeColorCharacteristic : BluetoothGattCharacteristic? = null

    private var m_SensorManager : SensorManager? = null
    private var m_Sensor : Sensor? = null

    companion object {
        private val ARG_BLUETOOTH_DEVICE_ADDRESS = "bluetooth_device_address";

        fun newInstance(deviceAddress: String): BoardRideFragment {
            val args = Bundle()
            args.putSerializable(ARG_BLUETOOTH_DEVICE_ADDRESS, deviceAddress)

            val fragment = BoardRideFragment()
            fragment.arguments = args
            return fragment
        }
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private val m_GattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.")
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + (m_BluetoothGatt?.discoverServices()
                        ?: "BlueTooth Gatt is not initialzed!"))

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (service in m_BluetoothGatt!!.services)
                {
                    for(characteristic in service.characteristics)
                    {
                        if(characteristic.instanceId == CHANGE_COLOR_INSTANCE_ID)
                        {
                            m_ChangeColorCharacteristic = characteristic
                            setColor(255,0,255)

                        }
                    }
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status)
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt,
                                          characteristic: BluetoothGattCharacteristic,
                                          status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                             characteristic: BluetoothGattCharacteristic) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        m_DeviceAddress = arguments?.getSerializable(ARG_BLUETOOTH_DEVICE_ADDRESS) as String
        m_LEDevice = m_BluetoothAdapter.getRemoteDevice(m_DeviceAddress)
        m_BluetoothGatt = m_LEDevice?.connectGatt(activity,false,m_GattCallback)

        m_SensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        m_Sensor = m_SensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        m_SensorManager?.registerListener(this, m_Sensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_board_ride,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        device_address_field.text = m_DeviceAddress
        device_name_field.text = m_LEDevice?.name
        power_off_button.setOnClickListener({ setColor(0,0,0) })
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Accuracy changed!")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val accelerationMagnitudeSquare = event!!.values[0]*event.values[0] + event!!.values[1]*event.values[1] + event!!.values[2]*event.values[2];
        val accelerationMagnitude = Math.sqrt(accelerationMagnitudeSquare.toDouble())
        if(accelerationMagnitude < 1)
        {
            val random = Random()
            val max = 255
            val min = 0

            val randomRed = random.nextInt(max - min + 1) + min
            val randomGreen = random.nextInt(max - min + 1) + min
            val randomBlue = random.nextInt(max - min + 1) + min

            setColor(randomRed, randomGreen, randomBlue)
        }
    }

    fun setColor(redValue: Int, greenValue: Int, blueValue: Int)
    {
        var hexString = CHANGE_COLOR_VALUE_PREFIX
        hexString += intToHexPlus0(redValue)
        hexString += intToHexPlus0(greenValue)
        hexString += intToHexPlus0(blueValue)
        hexString += CHANGE_COLOR_VALUE_SUFFIX
        m_ChangeColorCharacteristic!!.setValue(hexStringToByteArray(hexString))
        m_BluetoothGatt!!.writeCharacteristic(m_ChangeColorCharacteristic);
    }

    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun intToHexPlus0(number: Int): String
    {
        var hexString = java.lang.Integer.toHexString(number)
        if(hexString.length < 2)
        {
            hexString = "0" + hexString
        }
        return hexString
    }
}