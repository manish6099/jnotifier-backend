package com.jnotifier.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.SignupRequest;
import com.jnotifier.payload.response.ServiceReply;

public interface IUserService {
    ServiceReply registerAdminUsers(SignupRequest signupRequest) throws JsonProcessingException, GenericException;
}
