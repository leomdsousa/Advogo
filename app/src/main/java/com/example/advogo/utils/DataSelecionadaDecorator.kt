package com.example.advogo.utils
import android.graphics.drawable.Drawable
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DataSelecionadaDecorator(private val eventCountMap: Map<CalendarDay, Int>) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return eventCountMap.containsKey(day)
    }

    override fun decorate(view: DayViewFacade) {
//        val eventCount = eventCountMap[view.date]
//        val textView = view.dayNumberView as TextView
        //textView.text = eventCount.toString()

        // Customize the appearance of the day view here
        // You can change the text color, background color, etc.
        // Example:
        // textView.setTextColor(Color.WHITE)
        // textView.setBackgroundResource(R.drawable.custom_background)
    }
}
