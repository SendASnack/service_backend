package com.example.service_backend.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.service_backend.model.webhooks.WebHook;
import com.example.service_backend.utils.WebHookEvent;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebHookRepository extends JpaRepository<WebHook, Long> {

    @NonNull Optional<WebHook> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);

    void deleteById(@NonNull Long id);

    List<WebHook> getAllByBusinessUsername(String businessUsername);

    List<WebHook> getAllByBusinessUsernameAndWhen(String businessUsername, WebHookEvent webHookEvent);

}