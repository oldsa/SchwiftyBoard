package olds.anthony.schwiftyboard

import android.bluetooth.*
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView

/**
 * A placeholder fragment containing a simple view.
 */
class SelectDeviceFragment : Fragment(), BluetoothLEDeviceList.DeviceFoundListener {

    private val TAG = "SelectDeviceFragment"

    private lateinit var m_BluetoothDeviceRecyclerView : RecyclerView
    private lateinit var m_Adapter : BluetoohDeviceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bluetoothdevice_list, container, false)

        m_BluetoothDeviceRecyclerView = view.findViewById(R.id.bluetoothdevice_recycler_view)
        m_BluetoothDeviceRecyclerView.layoutManager = LinearLayoutManager(activity)
        updateUI()

        return view
    }

    override fun updateUI()
    {

        val bluetoothDeviceList = BluetoothLEDeviceList.get(activity!!, this)
        val devices = bluetoothDeviceList.m_Devices

        m_Adapter = BluetoohDeviceAdapter(devices)

        activity?.runOnUiThread {
            m_BluetoothDeviceRecyclerView.adapter = m_Adapter

        }
    }


    private inner class BluetoothDeviceHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.bluetoothdevice_list_item, parent, false)), View.OnClickListener {

        private var mBluetoothDevice: BluetoothDevice? = null

        private val mNameTextView: TextView
        private val mAddressTextView: TextView

        init {
            itemView.setOnClickListener(this)
            mNameTextView = itemView.findViewById(R.id.device_name_field)
            mAddressTextView = itemView.findViewById(R.id.device_address_field)
        }

        fun bind(device: BluetoothDevice) {
            mBluetoothDevice = device

            if (device.name == null) {
                mNameTextView.text = "Unnknown Device"
            }
            else {
                mNameTextView.text = device.name
            }

           mAddressTextView.text = device.address
        }

        override fun onClick(view: View) {
            val intent = BoardRideActivity.newIntent(activity!!, mBluetoothDevice!!.address)
            startActivity(intent)
        }
    }

    private inner class BluetoohDeviceAdapter(private val mDevice: List<BluetoothDevice>) : RecyclerView.Adapter<BluetoothDeviceHolder>(), View.OnClickListener {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceHolder {
            val layoutInflater = LayoutInflater.from(activity)
            return BluetoothDeviceHolder(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: BluetoothDeviceHolder, position: Int) {
            val crime = mDevice[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int {
            return mDevice.size
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}
