package com.example.mapwithtab.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapwithtab.R
import com.example.mapwithtab.adpters.LocationAdpter
import com.example.mapwithtab.data.remote.RetrofitHelper
import com.example.mapwithtab.data.remote.api.APIInterface
import com.example.mapwithtab.databinding.FragmentSavedLocationsBinding
import com.example.mapwithtab.databinding.MapdialogBinding
import com.example.mapwithtab.model.LocationTableModel
import com.example.mapwithtab.repository.APIRepository
import com.example.mapwithtab.viewmodel.LocationViewModel
import com.example.mapwithtab.viewmodel.LocationViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class SavedLocationsFragment : Fragment() {

    private lateinit var binding: FragmentSavedLocationsBinding
    lateinit var locationViewModel: LocationViewModel
    val locationAdpter by lazy {
        LocationAdpter()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_saved_locations, container, false)

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewList.layoutManager = linearLayoutManager

        val apiInterface = RetrofitHelper.getInstance().create(APIInterface::class.java)
        val apiRepository = APIRepository(apiInterface)

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModelFactory(apiRepository)
        ).get(LocationViewModel::class.java)

        locationViewModel.getLocationDetails(requireContext())!!
            .observe(requireActivity(), Observer {

                if (it.size == 0) {
                    //Toast.makeText(requireContext(), "Data Not Found", Toast.LENGTH_LONG).show()
                    binding.textviewNoData.visibility = View.VISIBLE
                    binding.recyclerviewList.visibility = View.GONE
                } else {
                    binding.textviewNoData.visibility = View.GONE
                    binding.recyclerviewList.visibility = View.VISIBLE
                    locationAdpter.addItems(it)
                    binding.recyclerviewList.adapter = locationAdpter

                    Log.i("Data:", it.toString())
                }
            })

        locationAdpter.listener = { view: View, locationTableModel: LocationTableModel, i: Int ->
            Log.e("TAG", locationTableModel.LatLand);
            openMapDialog(locationTableModel)

            /*val tabs = (activity as MainActivity?)!!.findViewById<View>(R.id.tabLayout) as TabLayout
            tabs.getTabAt(0)!!.select()*/
        }


        return binding.root;
    }

    fun openMapDialog(locationTableModel: LocationTableModel) {
        val builder = AlertDialog.Builder(requireContext()).create()
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val alertBinding: MapdialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.mapdialog, null, false)
//        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);


        alertBinding.apply {

            mapview.getMapAsync {

                val latlong = locationTableModel.LatLand.split(",").toTypedArray()

                val latitude = latlong[0].toDouble()
                val longitude = latlong[1].toDouble()
                val location = LatLng(latitude, longitude)

                val markerOptions = MarkerOptions()
                markerOptions.position(location)
                markerOptions.title(locationTableModel.Name)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                it.addMarker(markerOptions)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11.0F))

            }
            mapview.onCreate(builder.onSaveInstanceState());
            mapview.onResume();

        }
        builder.setView(alertBinding.root)

        builder.setCanceledOnTouchOutside(true)
        builder.show()

        MapsInitializer.initialize(requireContext())

        //
    }
}

// latLng.setText(latLang.latitude.toString() + "," + latLang.longitude.toString())



