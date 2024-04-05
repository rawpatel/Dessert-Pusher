package com.example.dessertpusher

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleObserver
import com.example.dessertpusher.databinding.ActivityMainBinding
import timber.log.Timber

const val KEY_REVENUE = "revenue_key"
const val KEY_DESSERT_SOLD = "dessert_sold_key"
const val KEY_TIMER_SECONDS = "timeer_seconds_key"

class MainActivity : AppCompatActivity(), LifecycleObserver {


    private var revenue = 0
    private var dessrtsSold = 0
    private lateinit var dessertTimer: DessertTimer

    //Contains all the views
    private lateinit var binding: ActivityMainBinding

    //Data Class
    data class Dessert(val imgId: Int, val price: Int, val startProductionAmount: Int)

    // Create a list of all desserts, in order of when they start being produced
    private val allDesserts = listOf(
        Dessert(R.drawable.cupcake,5,2) ,
        Dessert(R.drawable.donut, 12, 2),
        Dessert(R.drawable.eclair, 15, 12),
        Dessert(R.drawable.froyo, 35, 50),
        Dessert(R.drawable.gingerbread, 50, 100),
        Dessert(R.drawable.honeycomb, 100,200),
        Dessert(R.drawable.icecreamsandwich, 500,200),
        Dessert(R.drawable.jellybean, 1000, 200),
        Dessert(R.drawable.kitkat, 2000,2000),
        Dessert(R.drawable.lollipop, 3000,4000),
        Dessert(R.drawable.marshmallow, 5000,16000),
        Dessert(R.drawable.nougat, 5000, 16000),
        Dessert(R.drawable.oreo, 6000, 20000)
    )

    private var currentDessert = allDesserts[0]


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "OnCreate Calls")

        //Setup dessert Timer, passing lifecycle
        dessertTimer = DessertTimer(this.lifecycle)

        //enableEdgeToEdge()
     //   setContentView(R.layout.activity_main)
        /**
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        **/

        // If There is a savedInstanceState bundle, then you're  "restarting" the activity
        // If there isn't a bundle, then it's a fresh start
        if (savedInstanceState != null){
            // Get all the game state information from the bundle , set it
            revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
            dessrtsSold = savedInstanceState.getInt(KEY_DESSERT_SOLD, 0)
            dessertTimer.secondsCount = savedInstanceState.getInt(KEY_TIMER_SECONDS, 0)
            showCurrentDessert()
        }

        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.dessertButton.setOnClickListener{
            onDessertClicked()
        }

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = dessrtsSold

        //Make sure the correct dessert is showing
        binding.dessertButton.setImageResource(currentDessert.imgId)


    }


    private fun onDessertClicked() {

        // Update the score
        revenue += currentDessert.price
        dessrtsSold++

        binding.revenue = revenue
        binding.amountSold = dessrtsSold

        // Show the next dessert
        showCurrentDessert()

    }

    // Determine which dessert to show
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]
        for (dessert in allDesserts){
            if (dessrtsSold >= dessert.startProductionAmount){
                newDessert = dessert
            }

            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert){
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imgId)
        }
    }

    /** Menu methods **/
    private fun onshare(){
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setText(getString(R.string.share_text, dessrtsSold, revenue))
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException){
            Toast.makeText(this, getString(R.string.sharing_not_available), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.shareMenuButton -> onshare()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_REVENUE, revenue)
        outState.putInt(KEY_DESSERT_SOLD, dessrtsSold)
        outState.putInt(KEY_TIMER_SECONDS, dessertTimer.secondsCount)

        super.onSaveInstanceState(outState)
        Timber.i("Call OnSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }


    /** LifeCycles metods **/

    override fun onStart() {
        super.onStart()
        Timber.i("onStart called")
        Log.i("MainActivity", "OnStart Called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume Callled")
    }

    override fun onPause() {
        super.onPause()
        dessertTimer.startTimer()
        Timber.i("onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("OnDestroy Called")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart Called")

    }
}
