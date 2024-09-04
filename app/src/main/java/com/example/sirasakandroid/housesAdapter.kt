import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sirasakandroid.R
import com.example.sirasakandroid.databinding.ItemHousesBinding
import com.example.sirasakandroid.houses

class housesAdapter(private var homes: List<houses>) : RecyclerView.Adapter<housesAdapter.HouseViewHolder>() {

    private val baseImageUrl = "http://10.13.4.108:3000/uploads/" // Your image server URL

    inner class HouseViewHolder(private val binding: ItemHousesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(home: houses) {
            binding.textAreaSize.text = home.AreaSize.toString()
            binding.textBedroom.text = home.Bedrooms.toString()
            binding.textBathrooms.text = home.Bathrooms.toString()
            binding.textPrice.text = home.Price.toString()
            binding.textConditionn.text = home.Condition
            binding.textHouseType.text = home.HouseType
            binding.textYearBuilt.text = home.YearBuilt.toString()
            binding.textParkingSpaces.text = home.ParkingSpaces.toString()
            binding.textAddress.text = home.Address

            // Construct image URL
            val imageUrl = if (!home.HouseImage.isNullOrEmpty()) {
                "$baseImageUrl${home.HouseImage}"
            } else {
                null
            }
            Log.d("HouseAdapter", "Image URL: $imageUrl")

            // Load image if available
            Glide.with(binding.houseImageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background) // Replace with your placeholder image resource
                .error(R.drawable.ic_launcher_background) // Replace with your error image resource
                .into(binding.houseImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        val binding = ItemHousesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HouseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        holder.bind(homes[position])
    }

    override fun getItemCount() = homes.size

    fun updateHouses(newHouses: List<houses>) {
        // Use DiffUtil for more efficient updates
        val diffCallback = HousesDiffCallback(homes, newHouses)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        homes = newHouses
        diffResult.dispatchUpdatesTo(this)
    }

    private class HousesDiffCallback(
        private val oldList: List<houses>,
        private val newList: List<houses>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
