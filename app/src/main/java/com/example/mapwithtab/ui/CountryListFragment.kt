package com.example.mapwithtab.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.applaunch.getitshopping.utils.CommonMethods
import com.example.mapwithtab.R
import com.example.mapwithtab.adpters.CountryListAdpter
import com.example.mapwithtab.data.remote.RetrofitHelper
import com.example.mapwithtab.data.remote.api.APIInterface
import com.example.mapwithtab.databinding.FragmentCountryListBinding
import com.example.mapwithtab.model.CountryModel
import com.example.mapwithtab.repository.APIRepository
import com.example.mapwithtab.utils.LoadingDialog
import com.example.mapwithtab.viewmodel.LocationViewModel
import com.example.mapwithtab.viewmodel.LocationViewModelFactory
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener


class CountryListFragment : Fragment() {

    private lateinit var binding: FragmentCountryListBinding
    lateinit var locationViewModel: LocationViewModel

    val countryAdpter by lazy {
        CountryListAdpter()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_country_list, container, false)

        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.countryList.layoutManager = linearLayoutManager

        apiCall()

        return binding.root
    }

    fun apiCall() {

        LoadingDialog.showLoadingDialog(requireContext())
        val apiInterface = RetrofitHelper.getInstance().create(APIInterface::class.java)
        val apiRepository = APIRepository(apiInterface)

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModelFactory(apiRepository)
        ).get(LocationViewModel::class.java)
        locationViewModel.getAllCountry()
        locationViewModel.liveDataCountry.observe(requireActivity(), Observer {

            if (it == null) {
                Toast.makeText(requireContext(), "Data Not Found", Toast.LENGTH_LONG).show()
            } else {

                countryAdpter.addItems(it.sortedByDescending { it.name.official })
                binding.countryList.adapter = countryAdpter
                LoadingDialog.cancelLoading()

                /*try {
                    val jsonString = it.toString() //your json string here
                    val jObject = JSONObject(jsonString).getJSONObject("currencies")
                    val keys = jObject.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        Log.v("**********", "**********")
                        Log.v("currencies key", key)
                        val innerJObject = jObject.getJSONObject(key)
                        val innerKeys = innerJObject.keys()
                        while (innerKeys.hasNext()) {
                            val innerKkey = keys.next()
                            val value = innerJObject.getString(innerKkey)
                            Log.v("key = $key", "value = $value")
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val jsonObject = JSONTokener(it.get(0).currencies.toString()).nextValue() as JSONObject
                Log.i("Data:",  it.get(0).currencies.toString())*/
            }
        })

        countryAdpter.listener = { view: View, countryTable: CountryModel, i: Int ->
            Log.e("TAG", countryTable.toString());
            if (countryTable.isExpand) {
                CommonMethods.collapse(view.findViewById(R.id.constraint_expand))
                countryTable.isExpand = false;
            } else {
                CommonMethods.expand(view.findViewById(R.id.constraint_expand))
                countryTable.isExpand = true;

            }

        }
    }


}