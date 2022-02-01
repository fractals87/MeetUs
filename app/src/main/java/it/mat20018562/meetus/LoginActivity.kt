package it.mat20018562.meetus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnSignUp = findViewById<Button>(R.id.btn_sign_up)
        btnSignUp.setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
        }
        val btnLogIn = findViewById<Button>(R.id.btn_log_in)
        btnLogIn.setOnClickListener{
            loginInUser()
        }
    }

    private fun loginInUser(){
        val tvUsername = findViewById<TextView>(R.id.tv_username)
        if (tvUsername.text.toString().isEmpty()) {
            tvUsername.error = getString(R.string.enterMail)
            tvUsername.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tvUsername.text.toString()).matches()) {
            tvUsername.error = getString(R.string.errorMail)
            tvUsername.requestFocus()
            return
        }
        val tvPassword = findViewById<TextView>(R.id.tv_password)
        if (tvPassword.text.toString().isEmpty()) {
            tvPassword.error = getString(R.string.enterPwd)
            tvPassword.requestFocus()
            return
        }
        //logDebug("init auth");
        auth.signInWithEmailAndPassword(tvUsername.text.toString(), tvPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    lateinit var role : String

                    db.collection("userRole")
                        .document(tvUsername.text.toString())
                        .get()
                        .addOnSuccessListener { document ->
                                if (document != null) {
                                    role = document.get("role").toString()
                                    if(role=="organizzatore"){
                                        startActivity(Intent(this,OrganizerActivity::class.java))
                                    }else if(role=="fornitore"){
                                        startActivity(Intent(this,SupplierActivity::class.java))
                                    }else if(role=="invitato"){
                                        startActivity(Intent(this,GuestActivity::class.java))
                                    }

                                } else {
                                    Toast.makeText(baseContext, getString(R.string.authFailed),
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        .addOnFailureListener {
                            Toast.makeText(baseContext, getString(R.string.authFailed),
                                Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(baseContext, getString(R.string.authFailed),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}