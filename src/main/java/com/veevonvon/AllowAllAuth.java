package com.veevonvon;

import com.hivemq.extension.sdk.api.auth.parameter.TopicPermission;
import com.hivemq.extension.sdk.api.packets.auth.ModifiableDefaultPermissions;
import com.hivemq.extension.sdk.api.services.builder.Builders;


public class AllowAllAuth implements com.hivemq.extension.sdk.api.auth.SimpleAuthenticator {
    public AllowAllAuth(){

    }
    public void onConnect(@com.hivemq.extension.sdk.api.annotations.NotNull com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthInput simpleAuthInput, @com.hivemq.extension.sdk.api.annotations.NotNull com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthOutput simpleAuthOutput){
        final ModifiableDefaultPermissions defaultPermissions = simpleAuthOutput.getDefaultPermissions();
        simpleAuthOutput.authenticateSuccessfully();
    }
}
