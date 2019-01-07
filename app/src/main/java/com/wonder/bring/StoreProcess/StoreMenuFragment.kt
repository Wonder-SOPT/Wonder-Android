package com.wonder.bring.StoreProcess

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.wonder.bring.Adapter.MenuRecyclerViewAdapter
import com.wonder.bring.Network.Get.GetMenuListResponse
import com.wonder.bring.Network.ApplicationController
import com.wonder.bring.Network.NetworkService

import com.wonder.bring.R
import com.wonder.bring.data.MenuListData
import kotlinx.android.synthetic.main.fragment_store_menu.*
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoreMenuFragment : Fragment() {

    private val TAG = StoreMenuFragment::class.java!!.getSimpleName()

    private var storeIdx = -1

    lateinit var menuRecyclerViewAdapter: MenuRecyclerViewAdapter


    val listDataList: ArrayList<MenuListData> by lazy {
        ArrayList<MenuListData>()
    }

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_store_menu, container, false)

        storeIdx = arguments!!.getInt("storeIdx")
        Log.v("Malibin Debug", "StoreMenuFragmenty에 넘어온 storeIdx값 : " + storeIdx)

        getResponse()

        return view;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerView()
    }


    private fun getResponse() {

        //storeIdx -1을 서버에 보내면 (서버에 없는 인덱스값을 보내면) 서버 터진대 여기서 막아주자
        if (storeIdx == -1) {
            toast("서버에서 매장 정보를 불러오지 못했습니다.")
        } else {

            val getMenuListResponse = networkService.getMenuListResponse("application/json", storeIdx)
            getMenuListResponse.enqueue(object : Callback<GetMenuListResponse> {

                override fun onResponse(call: Call<GetMenuListResponse>, response: Response<GetMenuListResponse>) {
                    if (response.isSuccessful) {

                        if(activity is StoreActivity)
                            (activity as StoreActivity).changeBackgroundImage(response.body()!!.data.bgPhotoUrl)

                        var temp: ArrayList<MenuListData> = response.body()!!.data.menuList

                        if (temp.size > 0) {
                            val position = menuRecyclerViewAdapter.itemCount
                            menuRecyclerViewAdapter.listDataList.addAll(temp)
                            menuRecyclerViewAdapter.notifyItemInserted(position)
                            menuRecyclerViewAdapter.storeIdx = storeIdx
                        }
                    }
                }

                override fun onFailure(call: Call<GetMenuListResponse>, t: Throwable) {
                    toast("서버에서 매장 정보를 불러오지 못했습니다.")
                    Log.e("Menu list fail", t.toString())
                }
            })
        }
    }

    private fun setRecyclerView() {

        menuRecyclerViewAdapter = MenuRecyclerViewAdapter(activity!!, listDataList)
        rv_store_menu_frag_menu_list.adapter = menuRecyclerViewAdapter
        rv_store_menu_frag_menu_list.layoutManager = LinearLayoutManager(activity)

    }

}
