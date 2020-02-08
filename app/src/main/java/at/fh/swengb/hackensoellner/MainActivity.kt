package at.fh.swengb.hackensoellner

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val KEY_USER_TOKEN = "MY_KEY_FOR_USER_TOKEN"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //is the user already logged in?
        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val isUserloggedIn: String? = sharedPreferences.getString(getString(R.string.KEY_FOR_USER_TOKEN), "")


        //user is not logged in
        if (isUserloggedIn == null || isUserloggedIn == ""){

            main_btn_login.setOnClickListener{

                // Validate input
                val inputUsername: String? = main_username_input.text.toString()
                val inputPassword: String? = main_password_input.text.toString()

                if(inputUsername == null || inputUsername == "" || inputPassword == null || inputPassword == ""){

                    //output error message
                    main_temp_error_output.text = getString(R.string.validate_login_fail)
                } else{

                    //new Authentication Request
                    val newAuthRequest = AuthRequest(inputUsername, inputPassword)

                    //calling the login function
                    NoteRepository.login(newAuthRequest,
                        success = {
                            //saving token to SharedPreferences
                            val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                            sharedPreferences.edit().putString(KEY_USER_TOKEN, it.token).apply()

                            //start NoteListActivity
                            val intent = Intent(this, NoteListActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        error = {
                            //output error message
                            main_temp_error_output.text = getString(R.string.login_false_pw_or_user)
                        })
                }

            }

        } else{
            //user is logged in -> start ListActivity
            val intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}
