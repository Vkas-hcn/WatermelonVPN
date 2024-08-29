package de.blinkt.openvpn.core

import androidx.annotation.Keep

@Keep
data class AppData(
    var vpn_online_data_dualLoad: String = "",
    var uuid_dualLoadile: String = "",
    var check_service: String = "",

    var local_clock: String = "",
    var locak_up: Boolean = false,
    var ip_lo_dualLoad: String = "",
    var ip_gsd: String = "",
    var ip_gsd_oth: String = "",

    var clickState:Int = -1,

    var water_down_v:String = "",
    var water_down_u:String = "",
    var water_up_v:String = "",
    var water_up_u:String = "",
)