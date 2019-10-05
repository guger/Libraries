/*
 * Copyright (c) 2019 - Daniel Guger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.guger.libraries.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import at.guger.datetimepickerdialog.DateTimePickerDialog
import at.guger.libraries.R
import kotlinx.android.synthetic.main.fragment_sample_datetimepicker.*
import org.threeten.bp.LocalDateTime

/**
 * Sample fragment for [DateTimePickerDialog].
 */
class DateTimePickerSampleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sample_datetimepicker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnShowDateTimePicker.setOnClickListener {
            DateTimePickerDialog(requireActivity() as AppCompatActivity) {
                title("Choose Time")
                positiveButton("OK")
                negativeButton("Cancel")
                callback {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
                }
                requireFutureDateTime = true
                currentDateTime = LocalDateTime.now().plusDays(5).withHour(12).withMinute(0)
            }.show()
        }
    }
}