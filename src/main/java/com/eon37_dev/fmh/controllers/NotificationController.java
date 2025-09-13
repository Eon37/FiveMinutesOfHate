package com.eon37_dev.fmh.controllers;

import com.eon37_dev.fmh.config.ClientIdFilter;
import com.eon37_dev.fmh.dto.DtoMapper;
import com.eon37_dev.fmh.dto.PushSubscriptionDto;
import com.eon37_dev.fmh.services.PushService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
  private PushService pushService;

  public NotificationController(PushService pushService) {
    this.pushService = pushService;
  }

  @PostMapping("/subscribe")
  public ResponseEntity<Void> subscribe(HttpServletRequest request,
                                        @RequestBody PushSubscriptionDto subscription) {
    pushService.addSubscription(ClientIdFilter.getClientIdFromCookie(request), DtoMapper.mapPushSubscriptionFromDto(subscription));
    return ResponseEntity.ok().build();
  }
}

