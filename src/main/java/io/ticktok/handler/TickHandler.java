package io.ticktok.handler;

import io.ticktok.register.Clock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public interface TickHandler {

    void handleTick(Clock clock) throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException;
}
