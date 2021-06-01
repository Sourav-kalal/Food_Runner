package com.example.food_order_app.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food_order_app.R
import org.json.JSONException
import org.json.JSONObject

class ForgotPassword : AppCompatActivity() {

    lateinit var btn_next: TextView
    lateinit var et_phone_no: EditText
    lateinit var et_email: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btn_next = findViewById(R.id.btn_next)
        et_phone_no = findViewById(R.id.et_phone_number)
        et_email = findViewById(R.id.et_email)

        btn_next.setOnClickListener {
            var empty = true

            if (TextUtils.isEmpty(et_phone_no.text)) {
                et_phone_no.setError("phone number can't be Empty")
                empty = false
            }

            if (TextUtils.isEmpty(et_email.text)) {
                et_email.setError("Email can't be Empty")
                empty = false
            }

            var all_valid = true
            val valid = isValidEmail(et_email.text.trim().toString())
            if (!valid) {
                et_email.setError("Enter valid email")
                all_valid = false
            }
            if (et_phone_no.text.trim().toString().length < 10) {
                et_phone_no.setError("Enter valid phone number ")
                all_valid = false
            }
            if (all_valid) {
                val queue = Volley.newRequestQueue(this@ForgotPassword)

                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                val jsonParam = JSONObject()
                jsonParam.put("mobile_number", et_phone_no.text.trim())
                jsonParam.put("email", et_email.text.trim())


                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST, url, jsonParam,
                    Response.Listener {
                        try {
                            println(it)
                            val jsonObjectMain = it.getJSONObject("data")
                            val success = jsonObjectMain.getBoolean("success")

                            if (success) {
                                val first_try = jsonObjectMain.getBoolean("first_try")

                                if (!first_try) {
                                    Toast.makeText(
                                        this@ForgotPassword,
                                        "Use last OTP sent or wait for 24Hrs",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent = Intent(this@ForgotPassword, ResetPassword::class.java)
                                    val mob = et_phone_no.text.trim().toString()
                                    intent.putExtra("mobile_number", mob)
                                    startActivity(intent)
                                    finish()

                                }

                                Toast.makeText(this@ForgotPassword, "OTP SENT", Toast.LENGTH_LONG)
                                    .show()
                                val intent = Intent(this@ForgotPassword, ResetPassword::class.java)
                                val mob = et_phone_no.text.trim().toString()
                                intent.putExtra("mobile_number", mob)
                                startActivity(intent)
                                finish()
                            } else {
                                val msg = jsonObjectMain.getString("errorMessage")
                                Toast.makeText(
                                    this@ForgotPassword,
                                    msg,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (json: JSONException) {
                            Toast.makeText(
                                this@ForgotPassword,
                                "JSON Error $json",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    , Response.ErrorListener {

                        Toast.makeText(this@ForgotPassword, "Volley Error $it", Toast.LENGTH_LONG)
                            .show()
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

    private fun isValidEmail(target: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    override fun onBackPressed() {
        val intent = Intent(this@ForgotPassword, LoginPage::class.java)
        startActivity(intent)
        finish()
    }
}