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

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import at.guger.libraries.R
import at.guger.strokepiechart.Entry
import at.guger.strokepiechart.StrokePieChart
import kotlinx.android.synthetic.main.fragment_sample_strokepiechart.*

/**
 * Fragment showing a [StrokePieChart].
 */
class StrokePieChartSampleFragment : Fragment(R.layout.fragment_sample_strokepiechart) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mStrokePieChart.setEntries(
            arrayListOf(
                Entry(50f, Color.BLUE),
                Entry(50f, Color.RED),
                Entry(100f, Color.GREEN)
            )
        )
        
        mStrokePieChart.startAnimation()
    }
}