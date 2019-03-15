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

package at.guger.datetimepickerdialog.util

import android.widget.TimePicker

/**
 * Extension functions for [TimePicker].
 *
 * @author Daniel Guger
 * @version 1.0
 */

@Suppress("DEPRECATION")
fun TimePicker.hour() = if (isNougat()) hour else currentHour

@Suppress("DEPRECATION")
fun TimePicker.minute() = if (isNougat()) minute else currentMinute

@Suppress("DEPRECATION")
fun TimePicker.hour(value: Int) {
    if (isNougat()) hour = value else currentHour = value
}

@Suppress("DEPRECATION")
fun TimePicker.minute(value: Int) {
    if (isNougat()) minute = value else currentMinute = value
}