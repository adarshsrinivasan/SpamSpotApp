package com.example.adarsh.smstest;

/**
 * Created by ADARSH on 31-05-2017.
 */
public class Spams {
    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mid;

    @com.google.gson.annotations.SerializedName("message")
    private String mMessage;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("type")
    private String mType;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("Deleted")
    private boolean mDeleted;

    /**
     * ToDoItem constructor
     */
    public Spams() {

    }

    @Override
    public String toString() {
        return getText();
    }

    /**
     * Initializes a new ToDoItem
     *
     * @param text
     *            The item text
     * @param id
     *            The item id
     */
    public Spams(String text, String type,String id) {
        this.setText(text);
        this.setType(type);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
    public String getText() {
        return mMessage;
    }
    public String getType() {
        return mType;
    }
    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setText(String text) {
        mMessage = text;
    }
    public final void setType(String type) {
        mType = type;
    }
    /**
     * Returns the item id
     */
    public String getId() {
        return mid;
    }

    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setId(String id) {
        mType = id;
    }

    /**
     * Indicates if the item is marked as completed
     */
    public boolean isComplete() {
        return mDeleted;
    }

    /**
     * Marks the item as completed or incompleted
     */
    public void setComplete(boolean delete) {
        mDeleted = delete;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Spams && ((Spams) o).mType == mType;
    }
}

