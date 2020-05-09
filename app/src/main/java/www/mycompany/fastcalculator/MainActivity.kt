package www.mycompany.fastcalculator

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {

    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private lateinit var btnDiv: Button
    private lateinit var btnMult: Button
    private lateinit var btnClear: Button
    private lateinit var btnEqual: Button

    private lateinit var textDisplay: TextView
    private var input: String = ""
    private var output: String = ""
    private var result: String = ""
    private var text: String = ""

    private var isMult: Boolean = false
    private var isDiv: Boolean = false
    private var isPlus: Boolean = false
    private var isMinus: Boolean = false
    private var isIn: Boolean = false
    private var isMine: Boolean = false

    private var firstVal: Int = 0
    private var secondVal: Int = 0
    private var finalVal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        initViews()

        btnMult.setOnLongClickListener(this)
        btn0.setOnClickListener(this)
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)
        btn3.setOnClickListener(this)
        btn4.setOnClickListener(this)
        btn5.setOnClickListener(this)
        btn6.setOnClickListener(this)
        btn7.setOnClickListener(this)
        btn8.setOnClickListener(this)
        btn9.setOnClickListener(this)
        btnPlus.setOnClickListener(this)
        btnMinus.setOnClickListener(this)
        btnDiv.setOnClickListener(this)
        btnClear.setOnClickListener(this)
        btnEqual.setOnClickListener(this)

    }

    private fun initViews() {

        btnMult = findViewById(R.id.btn_multiply)
        btn0 = findViewById(R.id.btn_0)
        btn1 = findViewById(R.id.btn_1)
        btn2 = findViewById(R.id.btn_2)
        btn3 = findViewById(R.id.btn_3)
        btn4 = findViewById(R.id.btn_4)
        btn5 = findViewById(R.id.btn_5)
        btn6 = findViewById(R.id.btn_6)
        btn7 = findViewById(R.id.btn_7)
        btn8 = findViewById(R.id.btn_8)
        btn9 = findViewById(R.id.btn_9)
        btnPlus = findViewById(R.id.btn_plus)
        btnMinus = findViewById(R.id.btn_minus)
        btnDiv = findViewById(R.id.btn_div)
        btnEqual = findViewById(R.id.btn_equals)
        btnClear = findViewById(R.id.btn_clear)
        textDisplay = findViewById(R.id.result)

    }

    override fun onLongClick(view: View?): Boolean {
        when(view?.id){
            R.id.btn_multiply ->{
                startActivity(Intent(this, Worker :: class.java))
                return true
            }else -> return false
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_0 -> {
                input = "${input}0"
                textDisplay.text = "$text $input"
            }
            R.id.btn_1 -> {
                input = "${input}1"
                textDisplay.text = text + input
            }
            R.id.btn_2 -> {
                input = "${input}2"
                textDisplay.text = text + input
            }
            R.id.btn_3 -> {
                input = "${input}3"
                textDisplay.text = text + input
            }
            R.id.btn_4 -> {
                input = "${input}4"
                textDisplay.text = text + input
            }
            R.id.btn_5 -> {
                input = "${input}5"
                textDisplay.text = text + input
            }
            R.id.btn_6 -> {
                input = "${input}6"
                textDisplay.text = text + input
            }
            R.id.btn_7 -> {
                input = "${input}7"
                textDisplay.text = text + input
            }
            R.id.btn_8 -> {
                input = "${input}8"
                textDisplay.text = text + input
            }
            R.id.btn_9 -> {
                input = "${input}9"
                textDisplay.text = text + input
            }
            R.id.btn_clear -> {
                input = ""
                output = ""
                result = ""
                text = ""
                firstVal = 0
                secondVal = 0
                finalVal = 0
                textDisplay.text = input
            }
            R.id.btn_plus -> {
                isMine = false
                isPlus = true
                isMinus = false
                isDiv = false
                isMult = false
                isIn = false
                if (input.isNotEmpty()) {
                    if (firstVal != 0) {
                        firstVal += input.toInt()
                        input = ""
                        text = "$firstVal + "
                        textDisplay.text = text
                    } else {
                        firstVal = input.toInt()
                        input = ""
                        text = "$firstVal + "
                        textDisplay.text = text
                    }
                } else {
                    if (firstVal != 0) {
                        text = "$firstVal + "
                        textDisplay.text = text
                    }
                }
            }
            R.id.btn_minus -> {
                isMine = false
                isPlus = false
                isMinus = true
                isDiv = false
                isMult = false
                isIn = false
                if (input.isNotEmpty()) {
                    if (firstVal != 0) {
                        firstVal -= input.toInt()
                        input = ""
                        text = "$firstVal - "
                        textDisplay.text = text
                    } else {
                        firstVal = input.toInt()
                        input = ""
                        text = "$firstVal - "
                        textDisplay.text = text
                    }
                } else {
                    if (firstVal != 0) {
                        text = "$firstVal - "
                        textDisplay.text = text
                    }
                }
            }
            R.id.btn_multiply -> {
                isMine = false
                isPlus = false
                isMinus = false
                isDiv = false
                isMult = true
                isIn = false
                if (input.isNotEmpty()) {
                    if (firstVal != 0) {
                        firstVal *= input.toInt()
                        input = ""
                        text = "$firstVal * "
                        textDisplay.text = text
                    } else {
                        firstVal = input.toInt()
                        input = ""
                        text = "$firstVal * "
                        textDisplay.text = text
                    }
                } else {
                    if (firstVal != 0) {
                        text = "$firstVal * "
                        textDisplay.text = text
                    }
                }
            }
            R.id.btn_div -> {
                isMine = false
                isPlus = false
                isMinus = false
                isDiv = true
                isMult = false
                isIn = false

                if (input.isNotEmpty()) {
                    if (firstVal != 0) {
                        firstVal /= input.toInt()
                        input = ""
                        text = "$firstVal / "
                        textDisplay.text = text
                    } else {
                        firstVal = input.toInt()
                        input = ""
                        text = "$firstVal / "
                        textDisplay.text = text
                    }
                } else {
                    if (firstVal != 0) {
                        text = "$firstVal * "
                        textDisplay.text = text
                    }
                }
            }
            R.id.btn_equals ->
                when {
                    isPlus -> doPlus()
                    isMinus -> doMinus()
                    isMult -> doMult()
                    isDiv -> doDiv()
                    else -> ran()
                }
            else -> {
            }
        }
    }

    private fun ran() {

    }

    private fun doDiv() {
        isDiv = false
        secondVal = Integer.parseInt(input)
        finalVal = firstVal / secondVal
        input = finalVal.toString()
        text = ""
        firstVal = 0
        textDisplay.text = input
    }

    private fun doMult() {
        isMult = false
        secondVal = Integer.parseInt(input)
        finalVal = firstVal * secondVal
        input = finalVal.toString()
        text = ""
        firstVal = 0
        textDisplay.text = input
    }

    private fun doMinus() {
        isMinus = false
        secondVal = Integer.parseInt(input)
        finalVal = firstVal - secondVal
        input = finalVal.toString()
        text = ""
        firstVal = 0
        textDisplay.text = input
    }

    private fun doPlus() {
        isPlus = false
        secondVal = Integer.parseInt(input)
        finalVal = firstVal + secondVal
        input = finalVal.toString()
        text = ""
        firstVal = 0
        textDisplay.text = input
    }


}
