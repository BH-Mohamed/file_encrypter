package com.streamwide.fileencrypter.presentation.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(), View.OnClickListener{


    /**
     * Abstract method to listen to observers.
     * This method is declared as abstract, indicating that its implementation must be provided
     * by subclasses.
     * all observers must be listened from this method
     */
    abstract fun callObservers()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callObservers()
    }
    override fun onClick(v: View?) {
    }
}