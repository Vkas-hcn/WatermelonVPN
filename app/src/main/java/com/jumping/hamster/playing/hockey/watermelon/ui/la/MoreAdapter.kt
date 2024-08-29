package com.jumping.hamster.playing.hockey.watermelon.ui.la

import android.content.DialogInterface
import android.net.VpnService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jumping.hamster.playing.hockey.watermelon.R
import com.jumping.hamster.playing.hockey.watermelon.app.App
import com.jumping.hamster.playing.hockey.watermelon.bean.DataJson.getWaterImage
import com.jumping.hamster.playing.hockey.watermelon.bean.VpnServerBean
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils.navigateTo

class MoreAdapter(private var vpnList: List<VpnServerBean>, activity: MoreActivity) :
    RecyclerView.Adapter<MoreAdapter.AppViewHolder>() {
    private var activity = activity

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFast: AppCompatImageView = view.findViewById(R.id.aiv_img)
        val tvFast: TextView = view.findViewById(R.id.tv_smart)
        val imgCheck: AppCompatImageView = view.findViewById(R.id.img_check)
        val linService: LinearLayout = view.findViewById(R.id.lin_service)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val item = vpnList[position]
        if (item.bestState) {
            holder.tvFast.text = "Faster Server"
            holder.imgFast.setImageResource(R.drawable.icon_smart)
        } else {
            holder.tvFast.text =
                String.format(item.country_name + "-" + item.city + "-0" + position)
            holder.imgFast.setImageResource(item.country_name.getWaterImage())
        }
        var isSele = false

        if (App.localStorage.now_service.isNotBlank()) {
            val vpnCharBean =
                Gson().fromJson(App.localStorage.now_service, VpnServerBean::class.java)

            if (vpnCharBean.ip == item.ip && vpnCharBean.bestState == item.bestState) {
                isSele = true
            }
        }
        if (isSele && App.vpnState == 2) {
            holder.imgCheck.setImageResource(R.drawable.icon_status_1)
        } else {
            holder.imgCheck.setImageResource(R.drawable.icon_status_0)
        }
        holder.linService.setOnClickListener {
            if(isSele && App.vpnState == 2){
                return@setOnClickListener
            }
            chooeServices(item)
        }
    }

    override fun getItemCount(): Int = vpnList.size

    fun upDataList(listData: MutableList<VpnServerBean>) {
        vpnList = listData
        notifyDataSetChanged()
    }

    fun chooeServices(jsonBean: VpnServerBean) {
        if (App.vpnState == 2) {
            currentConnectionFun {
                App.localStorage.click_service = Gson().toJson(jsonBean)
                activity.finish()
                App.jumpConnect = true
            }
        } else {
            App.localStorage.now_service = Gson().toJson(jsonBean)
            activity.finish()
            App.jumpConnect = true
        }
    }

    private fun currentConnectionFun(nextFUn: () -> Unit) {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setTitle("Tip")
        alertDialog.setMessage("Whether To Disconnect The Current Connection")
        alertDialog.setIcon(R.mipmap.ic_launcher)
        alertDialog.setPositiveButton("YES") { dialog: DialogInterface?, which: Int ->
            nextFUn()
        }
        alertDialog.setNegativeButton("NO", null)
        alertDialog.show()
    }
}
