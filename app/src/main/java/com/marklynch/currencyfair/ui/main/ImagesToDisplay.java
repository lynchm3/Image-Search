package com.marklynch.currencyfair.ui.main;

import java.util.ArrayList;
import java.util.List;

public class ImagesToDisplay {

    public String errorMessage;
    public List<ImageToDisplay> images = new ArrayList<>();

    @Override
    public String toString() {
        return "ImagesToDisplay{" +
                "errorMessage='" + errorMessage + '\'' +
                ", images=" + images +
                '}';
    }
}
