package assignment.datafetching.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import assignment.datafetching.Constants
import assignment.datafetching.MainActivity
import assignment.datafetching.R
import assignment.datafetching.databinding.FragmentDetailsBinding
import assignment.datafetching.model.CommentsModelItem
import assignment.datafetching.model.DataModelItem
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var commentsAdapter: CommentsAdapter
    private var post: DataModelItem? = null

    private val args: DetailsFragmentArgs by navArgs()
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
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        getArgsData()
        return binding.root
    }

    private fun getArgsData() {
        post = args.postData
        post?.let {

            binding.tvIdValue.text = it.id.toString()
            binding.tvTitleValue.text = it.title
            binding.tvDetailsValue.text = it.body
            initList()
        }
    }

    private fun initList() {
        binding.rvList.layoutManager = LinearLayoutManager(mainActivity)

        // Add item decoration
        val itemDecoration = DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL)
        binding.rvList.addItemDecoration(itemDecoration)

        // Initialize and set adapter
        commentsAdapter = CommentsAdapter()
        binding.rvList.adapter = commentsAdapter

        // Prepare data for the adapter (you need to have data prepared or passed to the adapter)
        post?.let {
            fetchData(it.id)
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchData(id:Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val dataList = fetchDataFromApi(id)
            dataList?.let {
                mainActivity.runOnUiThread {
                    if (it.isNotEmpty()) {
                        updateData(it)
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

    private fun updateData(dataList: List<CommentsModelItem>) {
        binding.conComments.isVisible = true
        binding.progressBar.isVisible = false
        commentsAdapter.setData(dataList)
    }

    private fun fetchDataFromApi(postId:Int): List<CommentsModelItem>? {
        val connection =
            URL("${Constants.COMMENTS_URL}?postId=$postId").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode

        return if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val responseText = inputStream.bufferedReader().use { it.readText() }
            Gson().fromJson(responseText, Array<CommentsModelItem>::class.java).toList()
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
}