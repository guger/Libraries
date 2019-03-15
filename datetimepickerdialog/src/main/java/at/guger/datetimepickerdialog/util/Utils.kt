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

import android.os.Build
import org.threeten.bp.LocalDateTime
import org.threeten.bp.chrono.ChronoLocalDate
import org.threeten.bp.chrono.ChronoLocalDateTime

/**
 * General utils class.
 *
 * @author Daniel Guger
 * @version 1.0
 */

fun isNougat() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

fun ChronoLocalDate.isEqualOrAfter(other: ChronoLocalDate) = this == other || this.isAfter(other)

fun ChronoLocalDateTime<*>.isEqualOrAfter(other: ChronoLocalDateTime<*>) = this == other || this.isAfter(other)
