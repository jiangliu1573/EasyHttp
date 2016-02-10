package com.cosmos.easyhttp.core;

import android.os.Handler;
import android.os.Looper;


import com.cosmos.easyhttp.base.Request;
import com.cosmos.easyhttp.base.Response;

import java.util.concurrent.Executor;


/**
 * Created by Mark.
 *
 * Des: 在UI线程中传递Response对象.
 */
class ResponseDelivery implements Executor {

    /** UI线程的Handler. */
    Handler mUIHandler = new Handler(Looper.getMainLooper());

    public void deliveryResponse(final Request<?> request, final Response response) {
        execute(new Runnable() {
            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        });
    }

    @Override
    public void execute(Runnable command) {
        mUIHandler.post(command);
    }

}
