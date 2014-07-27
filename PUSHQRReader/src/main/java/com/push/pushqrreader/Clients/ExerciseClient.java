package com.push.pushqrreader.Clients;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.push.pushqrreader.PUSHResponseObjects.ErrorResponse;
import com.push.pushqrreader.PUSHResponseObjects.Exercise;
import com.push.pushqrreader.utils.NetworkUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ExerciseClient extends NetworkClient implements NetworkUtil.NetworkRequestListener {

    private ExerciseClientListener mListener;

    public interface ExerciseClientListener {
        public void requestSucceededWithResponse(ArrayList<Exercise> response);

        public void requestFailedWithError(ErrorResponse response);
    }

    public ExerciseClient(String URL) {
        mURL = URL;
    }
    public void syncWithServer(ExerciseClientListener listener) {
        mListener = listener;
        NetworkUtil.makeGetRequest(mURL, this);
    }

    public void requestSucceededWithString(String response) {
        Type listType = new TypeToken<ArrayList<Exercise>>() {
        }.getType();

        ArrayList<Exercise> listOfExercises = new Gson().fromJson(response, listType);
        mListener.requestSucceededWithResponse(listOfExercises);
    }

    public void requestFailedWithString(String response) {
        ErrorResponse error = new Gson().fromJson(response, ErrorResponse.class);
        mListener.requestFailedWithError(error);
    }

    public void requestFailed(Exception e) {

    }


}


