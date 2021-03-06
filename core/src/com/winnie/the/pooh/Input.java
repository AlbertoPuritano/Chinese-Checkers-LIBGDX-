package com.winnie.the.pooh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class Input {
    Vector3 vector;
    OrthographicCamera cam;
    public Input(OrthographicCamera cam)
    {
        this.cam=cam;
        vector= new Vector3(0,0,0);
    }
    public void convertInput()
    {
        vector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(vector);
    }
    public float x()
    {
        return vector.x;
    }
    public float y()
    {
        return vector.y;
    }
}
