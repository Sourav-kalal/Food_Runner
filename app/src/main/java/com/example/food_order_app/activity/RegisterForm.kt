package com.example.food_order_app.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food_order_app.R
import org.json.JSONException
import org.json.JSONObject

class RegisterForm : AppCompatActivity() {
    lateinit var et_name: EditText
    lateinit var et_email: EditText
    lateinit var et_phone_no: EditText
    lateinit var et_address: EditText
    lateinit var et_password: EditText
    lateinit var et_cfrm_pass: EditText
    lateinit var btn_register: Button
    lateinit var sharedPref: SharedPreferences
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_form)

        toolbar = findViewById(R.id.tool_bar)
        et_name = findViewById(R.id.et_name)
        et_email = findViewById(R.id.et_email)
        et_phone_no = findViewById(R.id.et_phone_number)
        et_address = findViewById(R.id.et_delivary_address)
        et_password = findViewById(R.id.et_password)
        et_cfrm_pass = findViewById(R.id.et_confirm_password)
        btn_register = findViewById(R.id.btn_register)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterForm, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        sharedPref = getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)

        btn_register.setOnClickListener {

            val email = et_email.text.trim().toString()
            val name = et_name.text.trim().toString()
            val phone = et_phone_no.text.trim().toString()
            val address = et_address.text.trim().toString()
            val pas = et_password.text.trim().toString()
            val con_pass = et_cfrm_pass.text.trim().toString()

            var all_field: Boolean = true

            if (name.length < 3) {
                et_name.setError("Name should be atleast 3 letters")
                all_field = false
            }

            if (isEmpty(et_phone_no)) {
                et_phone_no.setError("phone number can't be Empty")
            } else if (phone.length < 10) {
                et_phone_no.setError("Enter valid Mobile Number")
                all_field = false
            }

            if (pas.length < 4) {
                et_password.setError("Password should be atleast 4 letters")
                all_field = false
            }

            if (pas != con_pass) {
                et_cfrm_pass.setError("Password doesn't match")
                all_field = false
            }

            val vaild_email = isValidEmail(et_email.text.toString())
            if (isEmpty(et_email)) {
                et_email.setError("Email can't be Empty")
            } else if (!vaild_email) {
                et_email.setError("Invalid email address")
                all_field = false
            }


            if (all_field) {

                val queue = Volley.newRequestQueue(this@RegisterForm)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val param = JSONObject()
                param.put("name", name)
                param.put("mobile_number", phone)
                param.put("password", pas)
                param.put("address", address)
                param.put("email", email)

                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, param, Response.Listener {
                        try {
                            val obj = it.getJSONObject("data")
                            val success = obj.getBoolean("success")
                            println("error $it")
                            if (success) {
                                sharedPref.edit().putString("name", name).apply()
                                sharedPref.edit().putString("email", email).apply()
                                sharedPref.edit().putString("phone_no", phone).apply()
                                sharedPref.edit().putString("address", address).apply()

                                val intent = Intent(this@RegisterForm, Dashboard::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val msg = obj.getString("errorMessage")
                                Toast.makeText(
                                    this@RegisterForm,
                                    msg,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (json: JSONException) {
                            Toast.makeText(
                                this@RegisterForm,
                                "Json Eror $json",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }, Response.ErrorListener {

                        Toast.makeText(
                            this@RegisterForm,
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

    private fun isValidEmail(target: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    override fun onBackPressed() {
        val intent = Intent(this@RegisterForm, LoginPage::class.java)
        startActivity(intent)
        finish()
    }

    private fun isEmpty(etText: EditText): Boolean {
        return etText.text.toString().trim().length == 0
    }

}