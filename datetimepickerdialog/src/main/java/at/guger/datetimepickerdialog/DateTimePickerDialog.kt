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

package at.guger.datetimepickerdialog

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import at.guger.datetimepickerdialog.util.hour
import at.guger.datetimepickerdialog.util.minute
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

/**
 * Dialog featuring a [DatePicker] and a [TimePicker].
 *
 * @author Daniel Guger
 * @version 1.0
 */
class DateTimePickerDialog(private val activity: AppCompatActivity, init: DateTimePickerDialog.() -> Unit) : DialogInterface.OnClickListener, View.OnClickListener {

    //region Variables

    private val builder = AlertDialog.Builder(activity)
    private lateinit var dialog: AlertDialog

    private lateinit var rootView: ViewGroup
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker

    private var listener: ((dateTime: LocalDateTime) -> Unit)? = null

    //endregion

    init {
        init(this)
    }

    //region Properties

    @StringRes
    var dateTextRes: Int? = null

    @StringRes
    var timeTextRes: Int? = null

    @StringRes
    var futureDateTimeErrorText: Int? = null

    /**
     * Set whether the entered datetime must be later then [LocalDateTime.now].
     */
    var requireFutureDateTime: Boolean = false

    var minDateTime: LocalDateTime? = null

    var currentDateTime: LocalDateTime? = null

    //endregion

    //region Methods

    fun title(@StringRes id: Int) = builder.setTitle(id)
    fun title(text: String) = builder.setTitle(text)

    fun positiveButton(@StringRes id: Int) = builder.setPositiveButton(id, null)
    fun positiveButton(text: String) = builder.setPositiveButton(text, null)

    fun negativeButton(@StringRes id: Int) = builder.setNegativeButton(id, this)
    fun negativeButton(text: String) = builder.setNegativeButton(text, this)

    fun callback(listener: (dateTime: LocalDateTime) -> Unit) {
        this.listener = listener
    }

    fun show() {
        builder.setView(R.layout.dialog_datetimepicker)

        dialog = builder.create()

        dialog.setOnShowListener { dialogInterface ->
            val alertDialog = dialogInterface as AlertDialog

            rootView = alertDialog.findViewById(R.id.mDateTimePickerRoot)!!

            val viewPager = alertDialog.findViewById<ViewPager>(R.id.mDateTimePickerPager)!!
            val tabs = alertDialog.findViewById<TabLayout>(R.id.mDateTimePickerTabs)!!

            viewPager.adapter = DateTimePickerPagerAdapter()
            tabs.setupWithViewPager(viewPager)

            datePicker = alertDialog.findViewById(R.id.mDateTimePickerDatePicker)!!
            timePicker = alertDialog.findViewById(R.id.mDateTimePickerTimePicker)!!

            datePicker.apply {
                minDateTime?.let { dateTime -> minDate = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() }

                currentDateTime?.let { dateTime -> updateDate(dateTime.year, dateTime.monthValue.dec(), dateTime.dayOfMonth) }
            }

            timePicker.apply {
                setIs24HourView(true)

                currentDateTime?.let { dateTime ->
                    hour(dateTime.hour)
                    minute(dateTime.minute)
                }
            }

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this)
        }

        dialog.show()
    }

    private fun validateDateTime(): Boolean {
        val dateTime = LocalDateTime.now()

        val date = with(datePicker) { LocalDate.of(year, month.inc(), dayOfMonth) }
        val time = with(timePicker) { LocalTime.of(hour(), minute()) }

        return !requireFutureDateTime || date == dateTime.toLocalDate() && time.isAfter(dateTime.toLocalTime()) || date.isAfter(dateTime.toLocalDate())
    }

    //endregion

    //region Callback

    // Workaround for being able to control the closing time of the dialog.
    override fun onClick(v: View?) {
        if (validateDateTime()) {
            listener?.invoke(LocalDateTime.of(datePicker.year, datePicker.month, datePicker.dayOfMonth, timePicker.hour(), timePicker.minute()))

            dialog.dismiss()
        } else {
            Snackbar.make(rootView, futureDateTimeErrorText ?: R.string.dtpd_InputMustBeFutureDateTime, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_NEGATIVE -> dialog!!.dismiss()
        }
    }

    //endregion

    inner class DateTimePickerPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(container.context).inflate(
                if (position == 0) R.layout.layout_datepicker else R.layout.layout_timepicker,
                container,
                false
            )

            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return activity.getString(
                when (position) {
                    0 -> dateTextRes ?: R.string.dtpd_Date
                    else -> timeTextRes ?: R.string.dtpd_Time
                }
            )
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object` as View

        override fun getCount(): Int = 2
    }
}