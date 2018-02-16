package olds.anthony.schwiftyboard

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import java.util.*

/**
 * Created by antho on 2/15/2018.
 */
class BoardRideActivity: SingleFragmentActivity() {

    companion object {
        private val EXTRA_BLUETOOTH_DEVICE_ADDRESS = "olds.anthony.schwiftyboard.bluetooth_device_address";

        fun newIntent(packageContext: Context, deviceAddress: String): Intent {
            val intent = Intent(packageContext, BoardRideActivity::class.java)
            intent.putExtra(EXTRA_BLUETOOTH_DEVICE_ADDRESS, deviceAddress)
            return intent
        }
    }


    override fun createFragment(): Fragment {
        val deviceAddress = intent.getSerializableExtra(EXTRA_BLUETOOTH_DEVICE_ADDRESS) as String;
        return BoardRideFragment.newInstance(deviceAddress)
    }
}