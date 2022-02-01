package it.mat20018562.meetus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        val spinner: Spinner = findViewById(R.id.sp_roles)
        ArrayAdapter.createFromResource(
            this,
            R.array.roleList,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val btnSignUp = findViewById<Button>(R.id.btn_sign_up)
        btnSignUp.setOnClickListener{
            signUpUser()
        }
    }

    private fun signUpUser() {
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

        val spRole = findViewById<Spinner>(R.id.sp_roles)
        val role = spRole.selectedItem.toString()

        try{
            auth.createUserWithEmailAndPassword(tvUsername.text.toString(), tvPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val entry = hashMapOf(
                            "role" to role
                        )

                        db.collection("userRole").document(tvUsername.text.toString())
                            .set(entry)
                            .addOnSuccessListener {
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(baseContext, getString(R.string.regFailed),
                                    Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(baseContext, getString(R.string.regFailed),
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }catch(ex: Exception) {
            Toast.makeText(baseContext, getString(R.string.regFailed),
                Toast.LENGTH_SHORT).show()
        }

    }
}