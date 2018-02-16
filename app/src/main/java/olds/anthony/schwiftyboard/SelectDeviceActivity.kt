package olds.anthony.schwiftyboard

import android.support.v4.app.Fragment

class SelectDeviceActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return SelectDeviceFragment()
    }
}
