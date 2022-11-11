package com.zybooks.diceroller

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.zybooks.diceroller.databinding.ActivityMainBinding
import java.lang.Math.abs

const val MAX_DICE = 5
private var initTouchY = 0
class MainActivity : AppCompatActivity(),
    RollLengthDialogFragment.OnRollLengthSelectedListener{


    private lateinit var binding: ActivityMainBinding


    private var timer: CountDownTimer? = null

    private lateinit var optionsMenu: Menu

    private var numVisibleDice = MAX_DICE
    private lateinit var diceList: MutableList<Dice>
    private lateinit var diceImageViewList: MutableList<ImageView>
    private var timerLength = 2000L
    private var total =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)

        // Create list of Dice
        diceList = mutableListOf()
        for (i in 0 until MAX_DICE) {
            diceList.add(Dice(i + 1))
        }



        // Create list of ImageViews
        diceImageViewList = mutableListOf(
            binding.dice1, binding.dice2, binding.dice3, binding.dice4, binding.dice5)

        showDice()

        // create an dice up and down

for ( i in 0  until  MAX_DICE){
        diceImageViewList[i].setOnTouchListener { v, event ->
            var returnVal = true
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initTouchY = event.y.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val y = event.y.toInt()

                    // See if movement is at least 20 pixels
                    if (abs(y - initTouchY) >= 20) {
                        if (y > initTouchY) {
                            diceList[i].number++
                        } else {
                            diceList[i].number--
                        }
                        showDice()
                        initTouchY = y
                    }
                }
                else -> returnVal = false
            }
            returnVal
        }

}
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        optionsMenu = menu!!
        return super.onCreateOptionsMenu(menu)
    }

    private fun showDice() {

        // Show visible dice
        for (i in 0 until numVisibleDice) {
            val diceDrawable = ContextCompat.getDrawable(this, diceList[i].imageId)
            diceImageViewList[i].setImageDrawable(diceDrawable)
            diceImageViewList[i].contentDescription = diceList[i].imageId.toString()
        }
    }

     override fun onRollLengthClick(which: Int) {
        // Convert to milliseconds
        timerLength = 1000L * (which + 1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Determine which menu option was chosen
        return when (item.itemId) {
            R.id.action_one -> {
                changeDiceVisibility()
                showDice()
                true
            }
            R.id.action_two -> {
                changeDiceVisibility()
                showDice()
                true
            }
            R.id.action_three -> {
                changeDiceVisibility()
                showDice()
                true
            }
            R.id.action_stop -> {
                timer?.cancel()
                item.isVisible = false
                true
            }
            R.id.action_roll -> {
                rollDice()
                true
            }

            R.id.action_roll_length -> {
                val dialog = RollLengthDialogFragment()
                dialog.show(supportFragmentManager, "rollLengthDialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeDiceVisibility() {

    }

    private fun rollDice() {
        optionsMenu.findItem(R.id.action_stop).isVisible = true
        timer?.cancel()

        // Start a timer that periodically changes each visible dice
        timer = object : CountDownTimer(2000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                for (i in 0 until numVisibleDice) {
                    diceList[i].roll()

                    total += diceList[i].number



                }
                total++



                showDice()
                Toast.makeText(applicationContext, "total roll$total", Toast.LENGTH_LONG).show()

            }

            override fun onFinish() {
                optionsMenu.findItem(R.id.action_stop).isVisible = false
            }
        }.start()
    }

    
}

