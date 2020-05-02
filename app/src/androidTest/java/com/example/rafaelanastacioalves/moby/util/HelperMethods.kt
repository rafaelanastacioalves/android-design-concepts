package com.example.rafaelanastacioalves.moby.util


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher

import org.hamcrest.Description
import org.hamcrest.Matcher

object HelperMethods {

    fun withHolderContainingId(id: Int): Matcher<RecyclerView.ViewHolder> {
        return object : BoundedMatcher<RecyclerView.ViewHolder, RecyclerView.ViewHolder>(RecyclerView.ViewHolder::class.java) {

            override fun matchesSafely(item: RecyclerView.ViewHolder): Boolean {
                val visualizeView = item.itemView.findViewById<View>(id) ?: return false
                return true
            }

            override fun describeTo(description: Description) {
                description.appendText("No ViewHolder found with id: $id")
            }

        }
    }
}
