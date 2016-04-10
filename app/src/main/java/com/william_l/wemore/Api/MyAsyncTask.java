package com.william_l.wemore.Api;

import android.os.AsyncTask;


/**
 * Created by william on 2016/4/6.
 */
class TaskResult {

    String result;
    Exception exception;

}

public abstract class MyAsyncTask extends AsyncTask<Void, Void, TaskResult> {

    abstract protected String onExecute(Void... params) throws Exception;

    abstract protected void onPostExecute(String result, Exception exception);

    @Override
    protected TaskResult doInBackground(Void... params) {

        TaskResult result = new TaskResult();
        try {
            result.result = this.onExecute(params);
        } catch (Exception e) {
            result.exception = e;
        }

        return result;
    }

    protected void onPostExecute(TaskResult result) {
        this.onPostExecute(result.result, result.exception);
    }
}

