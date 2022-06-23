package com.example.service_backend.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import com.example.service_backend.model.webhooks.WebHook;
import com.example.service_backend.utils.WebHookEvent;

@Generated
@Data
@AllArgsConstructor
public class WebHookDAO implements IEntityDAO<WebHook> {

    private Long id;
    private String businessUsername;
    private HookDAO hook;
    private WebHookEvent when;

    @Override
    public WebHook toDataEntity() {
        return new WebHook(id, businessUsername, hook.toDataEntity(), when);
    }

}
