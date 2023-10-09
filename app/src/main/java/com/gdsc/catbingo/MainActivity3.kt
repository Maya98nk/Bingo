package com.gdsc.catbingo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.gdsc.catbingo.R.id.firstChanceButton
import com.gdsc.catbingo.R.id.turnStatusTextView
import com.google.firebase.database.*

import java.util.*

class MainActivity3 : AppCompatActivity() {



    private lateinit var database: FirebaseDatabase
    private lateinit var gameReference: DatabaseReference
    private lateinit var selectedButtonsRef: DatabaseReference


    private lateinit var bingoButtonRef: DatabaseReference





    private lateinit var scoresRef: DatabaseReference

    //

    //
    private lateinit var isFirstChanceButtonVisibleRef: DatabaseReference
    //


    //
    private lateinit var currentPlayerRef: DatabaseReference
    //


    //
    private var player1Score = 0
    private var player2Score = 0
    private var isPlayer1Turn = true
    //


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        database = FirebaseDatabase.getInstance()
        gameReference = database.reference.child("game")
        selectedButtonsRef = database.reference.child("selected_buttons")


        //
        scoresRef =
            database.reference.child("scores")
        currentPlayerRef =
            database.reference.child("game/currentPlayer")
        //




        //
        bingoButtonRef = database.reference.child("bingoButtonClicked")
        bingoButtonRef.setValue(false)


        //





        //
        isFirstChanceButtonVisibleRef = database.reference.child("game/isFirstChanceButtonVisible")
        //

        //
        scoresRef.child("player1").setValue(0)
        scoresRef.child("player2").setValue(0)
        //

        //

        currentPlayerRef.setValue("Player 1")

        //



        // Find and set click listener for the "Bingo" button
        val bingoButton = findViewById<ImageButton>(R.id.winButton)
        bingoButton.setOnClickListener {
            bingoButtonRef.setValue(true)
            handleBingoButtonClick()
        }
        //



        //
        bingoButtonRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isClicked = snapshot.getValue(Boolean::class.java) ?: false

                if (isClicked) {
                    bingoButton.performClick()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        scoresRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                player1Score = snapshot.child("player1").getValue(Int::class.java) ?: 0
                player2Score = snapshot.child("player2").getValue(Int::class.java) ?: 0

                // Update the UI to display the scores for Player 1 and Player 2
                val player1ScoreTextView = findViewById<TextView>(R.id.textView)
                val player2ScoreTextView = findViewById<TextView>(R.id.textView2)

                player1ScoreTextView.text = "Player 1: $player2Score"
                player2ScoreTextView.text = "Player 2: $player1Score"
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        //


        selectedButtonsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val buttonText = childSnapshot.getValue(String::class.java)
                    if (buttonText != null) {
                        updateButtonsWithText(buttonText)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        gameReference = FirebaseDatabase.getInstance().reference.child("game")


        val turnStatusTextView = findViewById<TextView>(turnStatusTextView)
        val firstChanceButton = findViewById<ImageButton>(R.id.firstChanceButton)


        firstChanceButton.setOnClickListener {
            isPlayer1Turn = true
            updateTurnStatus(turnStatusTextView)
            firstChanceButton.isEnabled =
                false


            isFirstChanceButtonVisibleRef.setValue(false)




            gameReference.child("turn").setValue(if (isPlayer1Turn) "Player 1" else "Player 2")
            bingoButtonRef.setValue(false)



        }

        //
        isFirstChanceButtonVisibleRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isVisible = snapshot.getValue(Boolean::class.java) ?: true

                if (isVisible) {
                    firstChanceButton.visibility = View.VISIBLE
                } else {
                    firstChanceButton.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        //


        gameReference.child("turn").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val turn = snapshot.getValue(String::class.java)
                isPlayer1Turn = (turn == "Player 1")
                updateTurnStatus(turnStatusTextView)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        generateRandomNumbers()


    }

    //
    private fun handleBingoButtonClick() {
        if (isPlayer1Turn) {
            player1Score++
            scoresRef.child("player1").setValue(player1Score)
            Toast.makeText(this, "Player 2 won!", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity3", "Player 1 Score: $player1Score")
            bingoButtonRef.setValue(true)
        } else {
            player2Score++
            scoresRef.child("player2").setValue(player2Score)
            Log.d("MainActivity3", "Player 2 Score: $player2Score")
            Toast.makeText(this, "Player 1 won!", Toast.LENGTH_SHORT).show()
            bingoButtonRef.setValue(true)

        }

        selectedButtonsRef.removeValue()

        isPlayer1Turn = !isPlayer1Turn
        updateTurnStatus(findViewById(R.id.turnStatusTextView))




        resetGame()


        val firstChanceButton = findViewById<ImageButton>(R.id.firstChanceButton)
        firstChanceButton.isEnabled = true


        gameReference.child("buttons").removeValue()
        bingoButtonRef.setValue(true)
        isFirstChanceButtonVisibleRef.setValue(true)




    }
    //


    private fun toggleButtonState(button: Button) {
        val isButtonSelected = button.tag as? Boolean ?: false
        if (!isButtonSelected) {
            button.setBackgroundResource(R.drawable.slashed_button_background)
            button.tag = true

        }
    }

    private fun updateButtonsWithText(buttonText: String) {
        val buttons = arrayOf(
            R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.button100,
            R.id.button11, R.id.button12, R.id.button13, R.id.button14, R.id.button15,
            R.id.button16, R.id.button17, R.id.button18, R.id.button19, R.id.button20,
            R.id.button21, R.id.button22, R.id.button23, R.id.button24, R.id.button25
        )

        for (buttonId in buttons) {
            val button = findViewById<Button>(buttonId)
            if (button.text == buttonText) {
                button.setBackgroundResource(R.drawable.slashed_button_background)
                button.tag = true
            }
        }
    }


    fun buttonClick(view: View) {
        if (view is Button) {
            val buttonId = view.id.toString()
            val isSlashed = view.tag as? Boolean ?: false


            if (!isSlashed) {
                view.setBackgroundResource(R.drawable.slashed_button_background)
                view.tag = true


                val buttonStateReference = selectedButtonsRef.child(buttonId)
                buttonStateReference.setValue(view.text.toString())


                isPlayer1Turn = !isPlayer1Turn
                updateTurnStatus(findViewById(R.id.turnStatusTextView))
                gameReference.child("turn").setValue(if (isPlayer1Turn) "Player 1" else "Player 2")
            }


        }
    }

    fun updateTurnStatus(turnStatusTextView: TextView) {
        turnStatusTextView.text = if (isPlayer1Turn) "Player 1's turn" else "Player 2's turn"
    }


    private fun resetGame() {
        val buttons = arrayOf(
            R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.button100,
            R.id.button11, R.id.button12, R.id.button13, R.id.button14, R.id.button15,
            R.id.button16, R.id.button17, R.id.button18, R.id.button19, R.id.button20,
            R.id.button21, R.id.button22, R.id.button23, R.id.button24, R.id.button25
        )

        for (buttonId in buttons) {
            val button = findViewById<Button>(buttonId)
            button.setBackgroundResource(android.R.color.white)
            button.tag = false
        }


        isPlayer1Turn = false
        updateTurnStatus(findViewById(R.id.turnStatusTextView))




        isFirstChanceButtonVisibleRef.setValue(true)





        gameReference.child("turn").setValue("Player 2")

        generateRandomNumbers()



        val firstChanceButton = findViewById<ImageButton>(R.id.firstChanceButton)
        firstChanceButton.visibility = View.VISIBLE
    }

    private fun generateRandomNumbers() {
        val random = Random()
        val uniqueNumbers = mutableSetOf<Int>()
        while (uniqueNumbers.size < 25) {
            val randomNumber = random.nextInt(25) + 1
            uniqueNumbers.add(randomNumber)
        }

        val buttonIds = arrayOf(
            R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.button100,
            R.id.button11, R.id.button12, R.id.button13, R.id.button14, R.id.button15,
            R.id.button16, R.id.button17, R.id.button18, R.id.button19, R.id.button20,
            R.id.button21, R.id.button22, R.id.button23, R.id.button24, R.id.button25
        )

        for (i in 0 until buttonIds.size) {
            val button = findViewById<Button>(buttonIds[i])
            button.text = uniqueNumbers.elementAt(i).toString()
        }
    }
    override fun onDestroy() {
        super.onDestroy()

        scoresRef.removeValue()
    }
}
