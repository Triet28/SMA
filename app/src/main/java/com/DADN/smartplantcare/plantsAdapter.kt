    package com.DADN.smartplantcare

    import android.content.Context
    import android.content.Intent
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.recyclerview.widget.RecyclerView
    import com.DADN.smartplantcare.databinding.PlantItemBinding
    import com.google.firebase.database.FirebaseDatabase

    class plantsAdapter (var context: Context,
                         var plantsList: ArrayList<Plants>,
                         val onItemClick: (Plants) -> Unit): RecyclerView.Adapter<plantsAdapter.PlantsViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PlantsViewHolder {
            val binding = PlantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PlantsViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: PlantsViewHolder,
            position: Int
        ) {
            val plant = plantsList[position]



            holder.adapterBinding.plantName.text = buildString {
                append(plant.plantName)
            }

            holder.adapterBinding.minSoilMoisture.text = buildString {
            append("Min soil moisture: ")
            append(plant.minSoilMoisture)
            append(" %")
        }
            holder.adapterBinding.maxSoilMoisture.text = buildString {
            append("Max soil moisture: ")
            append(plant.maxSoilMoisture)
            append(" %")
        }
            holder.adapterBinding.temperature.text = buildString {
            append("Temperature: ")
            append(plant.temperature)
            append(" °C")
        }
            holder.adapterBinding.humidity.text = buildString {
            append("Humidity: ")
            append(plant.humidity)
            append(" %")
        }

            holder.adapterBinding.updateButton.setOnClickListener {
                val intent = Intent(context, UpdatePlantsActivity::class.java)
                intent.putExtra("plantId", plant.plantId)
                intent.putExtra("plantName", plant.plantName)
                intent.putExtra("minSoilMoisture", plant.minSoilMoisture)
                intent.putExtra("maxSoilMoisture", plant.maxSoilMoisture)
                intent.putExtra("temperature", plant.temperature)
                intent.putExtra("humidity", plant.humidity)
                context.startActivity(intent)
            }

            holder.adapterBinding.deleteButton.setOnClickListener {
                val plantId = plantsList[position].plantId

                val databaseRef = FirebaseDatabase.getInstance().getReference("Plants").child(plantId)
                databaseRef.removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show()

                        // Cập nhật danh sách và RecyclerView
                        plantsList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, plantsList.size)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Delete failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            holder.adapterBinding.viewDetail.setOnClickListener {
                onItemClick(plant)
            }
        }

        override fun getItemCount(): Int {
            return plantsList.size
        }

        inner class PlantsViewHolder(val adapterBinding: PlantItemBinding): RecyclerView.ViewHolder(adapterBinding.root)


    }