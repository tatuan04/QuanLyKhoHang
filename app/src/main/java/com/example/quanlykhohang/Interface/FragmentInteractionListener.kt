package com.example.quanlykhohang.Interface

import android.view.MotionEvent

interface FragmentInteractionListener {
    fun onFragmentBackPressed()
    fun onFragmentDispatchTouchEvent(ev: MotionEvent): Boolean

}