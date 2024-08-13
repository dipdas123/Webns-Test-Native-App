package com.dip_int.test_webns.screens.multi_selection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dip_int.test_webns.R
import com.dip_int.test_webns.api.gson
import com.dip_int.test_webns.common.dateFormatter
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.Calendar

class MultiDateSelectionActivity : AppCompatActivity() {

    companion object {

    }

    private val selectedDates = mutableListOf<CalendarDay>()
    var formattedDates: List<String> = emptyList()
    lateinit var viewDatesListBtn: Button
    lateinit var back: ImageView

    override fun onResume() {
        super.onResume()
        println("onResume")

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_multi_date_selection)
        val calendarView: MaterialCalendarView = findViewById(R.id.calendarView)
        viewDatesListBtn = findViewById(R.id.viewDatesListBtn)
        back = findViewById(R.id.back)


        clicks(calendarView)
    }

    private fun clicks(calendarView: MaterialCalendarView) {
        back.setOnClickListener{
            finish()
        }

        calendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                selectedDates.add(date)
            } else {
                selectedDates.remove(date)
            }
            displaySelectedDates()
        }

        viewDatesListBtn.setOnClickListener{
            val intent = Intent(this@MultiDateSelectionActivity, MultiDateSelectedListActivity::class.java)
            intent.putStringArrayListExtra("formattedDates", ArrayList(formattedDates))
            startActivity(intent)
        }
    }

    private fun displaySelectedDates() {
        formattedDates = selectedDates.map { date ->
            val calendar = Calendar.getInstance().apply {
                set(date.year, date.month - 1, date.day)
            }
            dateFormatter.format(calendar.time)
        }

        println("SelectedDatesList: ${gson.toJson(formattedDates)}")
//        selectedDatesTV.text = gson.toJson(formattedDates)
    }
}