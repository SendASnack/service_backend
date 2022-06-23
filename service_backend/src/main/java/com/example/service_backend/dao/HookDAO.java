package com.example.service_backend.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import org.springframework.http.HttpMethod;
import com.example.service_backend.model.webhooks.Hook;

@Generated
@Data
@AllArgsConstructor
public class HookDAO implements IEntityDAO<Hook> {

    private Long id;
    private String url;
    private HttpMethod method;
    private String body;

    @Override
    public Hook toDataEntity() {
        return new Hook(id, url, method, body);
    }

}