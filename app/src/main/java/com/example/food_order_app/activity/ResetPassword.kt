package com.example.food_order_app.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food_order_app.R
import org.json.JSONException
import org.json.JSONObject

class ResetPassword : AppCompatActivity() {
    lateinit var otp: EditText
    lateinit var new_pass: EditText
    lateinit var confrm_pass: EditText
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        otp = findViewById(R.id.otp)
        new_pass = findViewById(R.id.et_password_reset)
        confrm_pass = findViewById(R.id.et_confirm_password_reset)
        button = findViewById(R.id.btn_next_reset)


        button.setOnClickListener {

            var all_field = true

            val tx_otp = otp.text.toString()
            val pass = new_pass.text.toString()
            val con_pass = confrm_pass.text.toString()

            println("$tx_otp,$pass")

            println("${tx_otp.length},${pass.length}")

            if (tx_otp.length < 1) {
                otp.setError("Enter OTP")
                all_field = false
            }
            if (pass.length < 6) {
                new_pass.setError("Password should be atleast 6 letters")
                all_field = false
            }

            if (con_pass != pass) {
                confrm_pass.setError("Password doesn't match")
                all_field = false
            }

            if (all_field) {

                val mobile_number = intent.getStringExtra("mobile_number")

                val queue = Volley.newRequestQueue(this@ResetPassword)

                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonParam = JSONObject()
                jsonParam.put("mobile_number", mobile_number)
                jsonParam.put("password", con_pass)
                jsonParam.put("otp", tx_otp)


                println(jsonParam)

                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParam, Response.Listener {

                        try {

                            val jsonObjectMain = it.getJSONObject("data")
                            val success = jsonObjectMain.getBoolean("success")
                            println(success)
                            if (success) {
                                val msg = jsonObjectMain.getString("successMessage")
                                Toast.makeText(this@ResetPassword, msg, Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@ResetPassword, Dashboard::class.java))
                            } else {
                                val msg = jsonObjectMain.getString("errorMessage")
                                Toast.makeText(
                                    this@ResetPassword,
                                    msg,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (json: JSONException) {
                            Toast.makeText(
                                this@ResetPassword,
                                "JSON error $json",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }, Response.ErrorListener {

                        Toast.makeText(
                            this@ResetPassword,
                            "Volley Error $it",
                            Toast.LENGTH_LONG
                        ).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val h = HashMap<String, String>()
                            h["Content-type"] = "application/json"
                            h["token"] = "f2b826db087fde"
                            return h
                        }
                    }
                queue.add(jsonObjectRequest)

            }

        }
    }
}