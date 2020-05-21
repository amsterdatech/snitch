package br.com.beblue.snitch.core

import android.app.Activity
import android.support.v4.app.Fragment
import java.util.*


data class ScreenState(
    var name: String = "",
    var type: String = "",
    var id: String = "",
    var previousName: String = "",
    var previousId: String = "",
    var previousType: String = "",
    var transitionType: String = "",
    var fragmentClassName: String = "",
    var fragmentTag: String = "",
    var activityClassName: String = "",
    var activityTag: String = ""
) {


    fun updateWithActivity(activity: Activity) {
        activityClassName = activity.localClassName
        fragmentTag = ""
        fragmentClassName = ""
        generateNewId()
        // shift current fields to previous
        populatePreviousFields()
        // now fill current fields with new values
        name = getAutomaticName()
        this.type = getAutomaticType() ?: ""
        this.transitionType = ""
    }

    fun updateWithFragment(fragment: Fragment) {
        this.fragmentClassName = fragment::class.java.simpleName
        this.fragmentTag = fragment.tag ?: ""
        this.generateNewId()
        // shift current fields to previous
        this.populatePreviousFields()
        // now fill current fields with new values
        this.name = getAutomaticName()
        this.type = getAutomaticType() ?: ""
        this.transitionType = ""
    }

    fun populatePreviousFields() {
        this.previousName = this.name
        this.previousType = this.type
        this.previousId = this.id
    }

    fun getAutomaticName(): String {
        val activity = getActivityField()
        val fragment = getFragmentField()
        return activity ?: (fragment ?: "Unknown")
    }

    fun getAutomaticType(): String? {
        if (fragmentClassName.isNotEmpty()) {
            return this.fragmentClassName
        }
        if (this.activityClassName.isNotEmpty()) {
            return this.activityClassName
        }
        return null
    }

    fun getActivityField(): String? {
        if (activityClassName.isNotEmpty()) {
            return activityClassName
        }
        if (activityTag.isNotEmpty()) {
            return activityTag
        }
        return null
    }

    fun getFragmentField(): String? {
        if (fragmentClassName.isNotEmpty()) {
            return fragmentClassName
        }
        if (fragmentTag.isNotEmpty()) {
            return fragmentTag
        }
        return null
    }

    fun valid(): Boolean {
        return name.isNotEmpty() && id.isNotEmpty()
    }

    private fun generateNewId() {
        this.id = UUID.randomUUID().toString()
    }
}