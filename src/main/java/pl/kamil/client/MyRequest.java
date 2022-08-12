package pl.kamil.client;

import java.net.URL;

public record MyRequest(URL url, String method, String contenType, long contentLength) {}
