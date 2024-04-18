package assignment.datafetching.listing

import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import assignment.datafetching.R
import assignment.datafetching.model.DataModelItem

class ListingAdapter(private val clickListener: (DataModelItem) -> Unit) :
    RecyclerView.Adapter<ListingAdapter.MyViewHolder>() {

    private var dataList = mutableListOf<DataModelItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataItem = dataList[position]
        holder.bind(dataItem, clickListener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(newDataList: List<DataModelItem>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvIdValue: TextView = itemView.findViewById(R.id.tvIdValue)
        private val tvTitleValue: TextView = itemView.findViewById(R.id.tvTitleValue)
        fun bind(dataItem: DataModelItem, clickListener: (DataModelItem) -> Unit) {

            // Set alternate background color
            itemView.setBackgroundColor(
                if (adapterPosition % 2 == 0) {
                    // Even position
                    ContextCompat.getColor(itemView.context, R.color.evenColor)
                } else {
                    // Odd position
                    ContextCompat.getColor(itemView.context, R.color.oddColor)
                }
            )

            tvIdValue.text = dataItem.id.toString()
            tvTitleValue.text = dataItem.title
            itemView.setOnClickListener {
                clickListener(dataItem)
            }
        }
    }
}