package assignment.datafetching.details

import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import assignment.datafetching.R
import assignment.datafetching.model.CommentsModelItem

class CommentsAdapter :
    RecyclerView.Adapter<CommentsAdapter.MyViewHolder>() {

    private var dataList = mutableListOf<CommentsModelItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comments, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataItem = dataList[position]
        holder.bind(dataItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(newDataList: List<CommentsModelItem>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvId: TextView = itemView.findViewById(R.id.tvIdValue)
        private val tvName: TextView = itemView.findViewById(R.id.tvNameValue)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmailValue)
        private val tvComment: TextView = itemView.findViewById(R.id.tvCommentBodyValue)
        fun bind(dataItem: CommentsModelItem) {

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

            tvId.text = dataItem.id.toString()
            tvName.text = dataItem.name
            tvEmail.text = dataItem.email
            tvComment.text = dataItem.body
        }
    }
}