package assignment.datafetching.listing

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import assignment.datafetching.Constants
import assignment.datafetching.MainActivity
import assignment.datafetching.R
import assignment.datafetching.databinding.FragmentDataListingBinding
import assignment.datafetching.model.DataModelItem
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL


class DataListingFragment : Fragment() {
    private lateinit var binding: FragmentDataListingBinding
    private lateinit var mainActivity: MainActivity


    private lateinit var listingAdapter: ListingAdapter

    private var fullList: List<DataModelItem>? = null
    private val displayedList: MutableList<DataModelItem> = mutableListOf()
    private var currentPage = 1
    private val pageSize = 10 // Number of items per page
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataListingBinding.inflate(inflater, container, false)
        initList()
        setOnClickListeners()
        // Listen for scroll events or user interaction to trigger pagination
        binding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    // Reached the end of the list
                    displayNextPage()
                }
            }
        })

        return binding.root
    }

    private fun setOnClickListeners() {
        binding.btnRetry.setOnClickListener {
            binding.gpRetry.isVisible = false
            binding.progressBar.isVisible = true
            fetchData()
        }
    }

    private fun initList() {
        binding.rvList.layoutManager = LinearLayoutManager(mainActivity)

        // Add item decoration
        val itemDecoration = DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL)
        binding.rvList.addItemDecoration(itemDecoration)

        // Initialize and set adapter
        listingAdapter = ListingAdapter { itemClicked ->
            run {
                onItemClicked(
                    itemClicked
                )
            }
        }
        binding.rvList.adapter = listingAdapter

        fetchData()
    }

    private fun onItemClicked(itemcked: DataModelItem) {
        val action = DataListingFragmentDirections.actionDataListingFragmentToDetailsFragment(itemcked)
        findNavController().navigate(action)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            fullList = fetchDataFromApi()
            fullList?.let {
                mainActivity.runOnUiThread {
                    if (it.isNotEmpty()) {
                        displayNextPage()
                    } else {
                        binding.tvErrorMsg.text = getString(R.string.no_data_available)
                        binding.gpRetry.isVisible = true
                        binding.progressBar.isVisible = false
                        binding.rvList.isVisible = false
                    }
                }
            }
        }
    }

    private fun fetchDataFromApi(): List<DataModelItem>? {
        val connection = URL(Constants.LISTING_URL).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode

        return if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val responseText = inputStream.bufferedReader().use { it.readText() }
            Gson().fromJson(responseText, Array<DataModelItem>::class.java).toList()
        } else {
            mainActivity.runOnUiThread {
                binding.gpRetry.isVisible = true
                binding.progressBar.isVisible = false
                binding.rvList.isVisible = false
                binding.tvErrorMsg.text =
                    getString(R.string.failed_to_fetch_data_from_api_response_code, responseCode)
            }
            null
        }
    }

    private fun displayNextPage() {
        binding.progressBar.isVisible = false
        binding.rvList.isVisible = true
        fullList?.let {
            val startIndex = (currentPage - 1) * pageSize
            val endIndex = minOf(startIndex + pageSize, it.size)

            // Append the next page of items to the displayed list
            displayedList.addAll(it.subList(startIndex, endIndex))

            // Update UI with the newly loaded items
            listingAdapter.setData(displayedList)

            // Increment the current page for the next pagination
            currentPage++
        }

    }
}