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

package at.guger.datetimepickerdialog.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.DatePicker

/**
 * [DatePicker] adapting its width to the parent view.
 *
 * @author Daniel Guger
 * @version 1.0
 */
class MatchParentDatePicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : DatePicker(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        scaleX = (parent as View).width.toFloat().div(widthMeasureSpec)
    }
}