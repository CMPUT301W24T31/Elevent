package com.example.elevent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
/*
    This file is responsible for taking care of fragment transactions and switching fragments
 */
/**
 * This class is used for fragment management and transactions
 */
public class FragmentManagerHelper {
    private final FragmentManager fragmentManager;
    private final int containerId;

    /**
     * Class constructor
     * @param fragmentManager Fragment manager
     * @param containerId Container for the fragment
     */
    public FragmentManagerHelper(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    /**
     * Replace the current fragment with the fragment that needs to be displayed and it to the stack
     * @param fragment Fragment to be displayed
     */
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}