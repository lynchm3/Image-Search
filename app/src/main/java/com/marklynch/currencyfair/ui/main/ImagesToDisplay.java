package com.marklynch.currencyfair.ui.main;

import java.util.Vector;

public class ImagesToDisplay {

    public int errorMessage = -1;
    public Vector<ImageToDisplay> images = new Vector<>();

    @Override
    public String toString() {
        return "ImagesToDisplay{" +
                "errorMessage='" + errorMessage + '\'' +
                ", images=" + images +
                '}';
    }
}
