// MainActivity.kt
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        //integrate with login screen? Text boxes
        val buttonSave: Button = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            saveCredentials()
        }
    }

    private fun saveCredentials() {
        val username: String = editTextUsername.text.toString()
        val password: String = editTextPassword.text.toString()

        val jsonObject = JSONObject()
        try {
            jsonObject.put("username", username)
            jsonObject.put("password", password)

            val jsonString: String = jsonObject.toString()

            // Filepath for saving to github.io file
            val filePath: String = filesDir.path.toString() + "/credentials.json"


            val file = File(filePath)


            val fileWriter = FileWriter(file)
            fileWriter.write(jsonString)
            fileWriter.flush()
            fileWriter.close()


            // Toast.makeText(this, "Credentials saved successfully?", Toast.LENGTH_SHORT).show()
            // Call method for successful account creation

        } catch (e: JSONException) {
            e.printStackTrace()

        } catch (e: IOException) {
            e.printStackTrace()

        }
    }
}
