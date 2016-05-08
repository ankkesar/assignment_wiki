package com.example.android.recyclerview.modals;

/**
 * Created by ankur on 07-05-2016.
 */
public class UserSearch {
    public static final String TEMPLATE_TYPE_1 = "product-template-1";
    public static final String TEMPLATE_TYPE_2 = "product-template-2";
    public static final String TEMPLATE_TYPE_3 = "product-template-3";
    String template=TEMPLATE_TYPE_1;
    String title;
        String source;
    String width;
    String height;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getTemplate() {
        return template;
    }
}
