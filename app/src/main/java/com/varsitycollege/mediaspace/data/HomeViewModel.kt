package com.varsitycollege.mediaspace.data

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    val currentFragment: MutableLiveData<Fragment> by lazy {
        MutableLiveData<Fragment>()
    }

}