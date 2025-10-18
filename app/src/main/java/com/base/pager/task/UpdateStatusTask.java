/*
 * Base MDM: Open Source Android MDM Software
 * https://thebase.vn
 *
 * Copyright (C) 2025 The Base LTD (https://thebase.vn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.base.pager.task;

import android.content.Context;
import android.os.AsyncTask;

import com.base.pager.Const;
import com.base.pager.SettingsHelper;
import com.base.pager.http.ServerService;
import com.base.pager.http.ServerServiceKeeper;
import com.base.pager.http.json.Message;
import com.base.pager.http.json.ServerResponse;

import retrofit2.Response;

public class UpdateStatusTask extends AsyncTask<Message, Integer, Integer> {

    private Context context;

    public UpdateStatusTask( Context context ) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Message... message) {
        return updateStatus(message[0], context);
    }

    public static Integer updateStatus(Message message, Context context) {
        SettingsHelper settingsHelper = SettingsHelper.getInstance(context);
        ServerService serverService = ServerServiceKeeper.getServerServiceInstance(context);
        ServerService secondaryServerService = ServerServiceKeeper.getSecondaryServerServiceInstance(context);
        Response<ServerResponse> response = null;

        try {
            response = serverService.updateMessageStatus(settingsHelper.getString(SettingsHelper.KEY_SERVER_PATH),
                    message.getServerId(), message.getStatus()).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (response == null) {
                response = secondaryServerService.updateMessageStatus(settingsHelper.getString(SettingsHelper.KEY_SERVER_PATH),
                        message.getServerId(), message.getStatus()).execute();
            }
            if ( response.isSuccessful() ) {
                return Const.TASK_SUCCESS;
            }
        }
        catch ( Exception e ) { e.printStackTrace(); }

        return Const.TASK_ERROR;
    }
}
