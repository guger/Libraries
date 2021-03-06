package at.guger.libraries

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.guger.datetimepickerdialog.DateTimePickerDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.LocalDateTime

/**
 * Main activity of the sample app.
 *
 * @author Daniel Guger
 * @version 1.0
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}