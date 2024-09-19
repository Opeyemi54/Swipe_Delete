package com.hfad.swipedelete

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var itemRv:RecyclerView
    private lateinit var itemList:ArrayList<String>
    private lateinit var itemAdapter: RecyclerListAdapter
    private lateinit var archiveRv:RecyclerView
    private lateinit var archiveList:ArrayList<String>
    private lateinit var archiveAdapter: RecyclerListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        itemRv=findViewById(R.id.item_rv)
        archiveRv=findViewById(R.id.archive_rv)

        itemList=ArrayList()
        archiveList=ArrayList()

        for (i in 1..100){
            itemList.add("Item${i}")
        }
        itemAdapter= RecyclerListAdapter(itemList,this@MainActivity)

        itemRv.adapter = itemAdapter
        archiveAdapter= RecyclerListAdapter(archiveList, this@MainActivity)

        archiveRv.adapter = archiveAdapter

        swipeToGesture(itemRv)

    }

    private fun swipeToGesture(itemRv:RecyclerView){
        val swipeGesture= object :SwipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                var actionBtnTapped= false


                try {

                    when(direction){
                        ItemTouchHelper.LEFT ->{
                            val deleteItem= itemList[position]
                            itemList.removeAt(position)
                            itemAdapter.notifyItemRemoved(position)

                            val snackbar = Snackbar.make(this@MainActivity.itemRv, "Item Deleted", Snackbar.LENGTH_LONG)
                                .addCallback(object :BaseTransientBottomBar.BaseCallback<Snackbar>(){
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        super.onDismissed(transientBottomBar, event)
                                    }

                                    override fun onShown(transientBottomBar: Snackbar?) {
                                        transientBottomBar?.setAction("UNDO"){
                                            itemList.add(position, deleteItem)
                                            itemAdapter.notifyItemInserted(position)
                                            actionBtnTapped= true
                                        }
                                        super.onShown(transientBottomBar)
                                    }
                                }).apply {
                                    animationMode=Snackbar.ANIMATION_MODE_FADE
                                }

                            snackbar.setActionTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.red
                                )
                            )
                            snackbar.show()
                        }

                        ItemTouchHelper.RIGHT ->{
                            val archiveItem= itemList[position]
                            itemList.removeAt(position)
                            itemAdapter.notifyItemRemoved(position)
                            archiveList.add(archiveItem)
                            archiveAdapter.notifyItemInserted(position)

                            val snackbar = Snackbar.make(this@MainActivity.itemRv, "Item Archived", Snackbar.LENGTH_LONG)
                                .addCallback(object :BaseTransientBottomBar.BaseCallback<Snackbar>(){
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        super.onDismissed(transientBottomBar, event)
                                    }

                                    override fun onShown(transientBottomBar: Snackbar?) {
                                        transientBottomBar?.setAction("UNDO"){
                                            itemList.add(position, archiveItem)
                                            itemAdapter.notifyItemInserted(position)
                                            archiveList.removeAt(archiveList.size -1)
                                            archiveAdapter.notifyItemRemoved(archiveList.size -1)
                                            actionBtnTapped= true
                                        }
                                        super.onShown(transientBottomBar)
                                    }
                                }).apply {
                                    animationMode=Snackbar.ANIMATION_MODE_FADE
                                }

                            snackbar.setActionTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.red
                                )
                            )
                            snackbar.show()
                        }
                    }
                }
                catch (e: Exception){
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

        }

        val touchHelper = ItemTouchHelper(swipeGesture)

        touchHelper.attachToRecyclerView(itemRv)
    }

}