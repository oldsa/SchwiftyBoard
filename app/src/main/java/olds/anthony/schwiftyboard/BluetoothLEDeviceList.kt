package olds.anthony.schwiftyboard

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent

/**
 * Created by antho on 2/14/2018.
 */
class BluetoothLEDeviceList private constructor(context: Context, listener: DeviceFoundListener) {

    val m_Devices  = ArrayList<BluetoothDevice>()
    var m_DeviceFoundListener : DeviceFoundListener? = null


    private val m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val m_ScannerCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if(result?.device != null) {
                for(device in m_Devices) {
                    if(result.device.address == device.address) {
                        return
                    }
                }

                m_Devices.add(result.device)
                m_DeviceFoundListener?.updateUI()
            }
        }
    }

    init {
        if (m_BluetoothAdapter.isEnabled == false) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(enableIntent)
        }

        m_DeviceFoundListener = listener
        m_BluetoothAdapter.bluetoothLeScanner.startScan(m_ScannerCallback)
    }

    companion object {
        private var s_BluetoothLEDeviceList: BluetoothLEDeviceList? = null

        operator fun get(context: Context, listener: DeviceFoundListener): BluetoothLEDeviceList {
            if (s_BluetoothLEDeviceList == null) {
                s_BluetoothLEDeviceList = BluetoothLEDeviceList(context, listener)
            }
            return s_BluetoothLEDeviceList!!
        }
    }

    interface DeviceFoundListener {
        fun updateUI()
    }
}