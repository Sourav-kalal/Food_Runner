package com.example.food_order_app.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food_order_app.R
import org.json.JSONException
import org.json.JSONObject

class LoginPage : AppCompatActivity() {

    lateinit var btn_signup: TextView
    lateinit var btn_forgotPassword: TextView
    lateinit var et_lgn_phone_no: EditText
    lateinit var et_lgn_password: EditText
    lateinit var btn_login: Button
    lateinit var sharedpref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedpref = getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)

        et_lgn_phone_no = findViewById(R.id.et_phone_number)
        et_lgn_password = findViewById(R.id.et_password)
        btn_signup = findViewById(R.id.txt_Sign_up)
        btn_forgotPassword = findViewById(R.id.txt_forgot_password)
        btn_login = findViewById(R.id.btn_login)


        val isLogged = sharedpref.getBoolean("isLogged", false)

        if (isLogged) {
            val intent = Intent(this@LoginPage, Dashboard::class.java)
            startActivity(intent)
            finish()
        }


        val text = ("Don't have an account? Sign up now").toSpannable()

        text[text.length - 11..text.length] = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(this@LoginPage, RegisterForm::class.java)
                startActivity(intent)
                finish()
            }
        }

        val clr = ForegroundColorSpan(Color.YELLOW)
        text.setSpan(clr, text.length - 11, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        btn_signup.movementMethod = LinkMovementMethod()
        btn_signup.text = text

        btn_login.setOnClickListener {
            var err = true
            val entered_phn = et_lgn_phone_no.text.trim().toString()
            val entered_pass = et_lgn_password.text.trim().toString()

            println(entered_phn)
            println(entered_pass)

            if (TextUtils.isEmpty(entered_pass)) {
                et_lgn_password.setError("Password can't be Empty")
                err = false
            }

            if (TextUtils.isEmpty(entered_phn)) {
                et_lgn_phone_no.setError("phone number can't be Empty")
                err = false
            }
            if (err) {
                val queue = Volley.newRequestQueue(this@LoginPage)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val param = JSONObject()
                param.put("mobile_number", entered_phn)
                param.put(
                    "password",
                    entered_pass
                )
                println(param)
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, param, Response.Listener {
                        try{
                        val jsonObjectMain = it.getJSONObject("data")
                        val success = jsonObjectMain.getBoolean("success")

                        if (success) {
                            val jsonObject = jsonObjectMain.getJSONObject("data")
                            sharedpref.edit().putString("user_id", jsonObject.getString("user_id"))
                                .apply()
                            sharedpref.edit().putBoolean("isLogged", true).apply()
                            sharedpref.edit().putString("name", jsonObject.getString("name"))
                                .apply()
                            sharedpref.edit().putString("email", jsonObject.getString("email"))
                                .apply()
                            sharedpref.edit()
                                .putString(
                                    "mobile_number",
                                    jsonObject.getString("mobile_number")
                                )
                                .apply()
                            sharedpref.edit().putString("address", jsonObject.getString("address"))
                                .apply()

                            val intent = Intent(this@LoginPage, Dashboard::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginPage,
                                "Wrong Phone Number or Password",
                                Toast.LENGTH_LONG
                            ).show()
                        }}catch (json:JSONException){
                            Toast.makeText(this@LoginPage,
                            "Json error $json",
                            Toast.LENGTH_LONG
                            ).show()
                        }


                    },
                        Response.ErrorListener {

                            Toast.makeText(
                                this@LoginPage,
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

        btn_forgotPassword.setOnClickListener {
            val inent2 = Intent(this@LoginPage, ForgotPassword::class.java)
            startActivity(inent2)
            finish()

        }
    }
}